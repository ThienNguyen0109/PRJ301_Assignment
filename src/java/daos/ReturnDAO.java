package daos;

import dto.ReturnRentalDTO;
import enums.RentalStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import models.Account;
import models.Rental;
import models.Station;
import models.Vehicle;
import models.VehicleModel;
import utils.JPAUtil;

public class ReturnDAO implements IReturnDAO {
    @Override
    public List<ReturnRentalDTO> searchRentedRentals(String keyword, Date endDate) {
        return JPAUtil.execute(em -> {
            String normalized = normalize(keyword);
            boolean rentalIdSearch = isUuid(normalized);
            StringBuilder jpql = new StringBuilder(
                    "SELECT r, c, v, m, s FROM Rental r JOIN r.customer c JOIN r.vehicle v "
                    + "JOIN v.model m JOIN r.pickupStation s WHERE r.status = :status ");
            if (!normalized.isEmpty()) {
                jpql.append("AND (");
                if (rentalIdSearch) jpql.append("r.rentalId = :rentalId OR ");
                jpql.append("LOWER(c.fullName) LIKE :keyword OR LOWER(c.email) LIKE :keyword "
                        + "OR c.phone LIKE :phone OR LOWER(v.licensePlate) LIKE :keyword) ");
            }
            if (endDate != null) jpql.append("AND r.endDate = :endDate ");
            jpql.append("ORDER BY r.endDate ASC, r.createdAt ASC");

            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                    .setParameter("status", RentalStatus.RENTED);
            if (!normalized.isEmpty()) {
                query.setParameter("keyword", "%" + normalized + "%");
                query.setParameter("phone", "%" + normalized + "%");
                if (rentalIdSearch) query.setParameter("rentalId", normalized);
            }
            if (endDate != null) query.setParameter("endDate", endDate);
            return query.getResultList().stream().map(this::map).collect(Collectors.toList());
        });
    }

    @Override
    public ReturnRentalDTO findRentalDetail(String rentalId) {
        if (rentalId == null || rentalId.trim().isEmpty()) return null;
        return JPAUtil.execute(em -> {
            List<Object[]> rows = em.createQuery(
                    "SELECT r, c, v, m, s FROM Rental r JOIN r.customer c JOIN r.vehicle v "
                    + "JOIN v.model m JOIN r.pickupStation s WHERE r.rentalId = :rentalId", Object[].class)
                    .setParameter("rentalId", rentalId.trim()).setMaxResults(1).getResultList();
            return rows.isEmpty() ? null : map(rows.get(0));
        });
    }

    @Override
    public Rental findRentalForUpdate(EntityManager em, String rentalId) {
        return em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public Vehicle findVehicleForUpdate(EntityManager em, String vehicleId) {
        return em.find(Vehicle.class, vehicleId, LockModeType.PESSIMISTIC_WRITE);
    }

    private ReturnRentalDTO map(Object[] row) {
        Rental rental = (Rental) row[0]; Account customer = (Account) row[1];
        Vehicle vehicle = (Vehicle) row[2]; VehicleModel model = (VehicleModel) row[3]; Station station = (Station) row[4];
        BigDecimal pricePerDay = toMoney(model.getPricePerDay());
        int lateDays = calculateLateDays(rental.getEndDate());
        BigDecimal lateFee = pricePerDay.multiply(BigDecimal.valueOf(lateDays));
        return new ReturnRentalDTO(rental.getRentalId(), customer.getFullName(), customer.getEmail(), customer.getPhone(),
                model.getName(), vehicle.getLicensePlate(), vehicle.getBatteryLevel(), rental.getStartDate(), rental.getEndDate(),
                rental.getTotalAmount(), pricePerDay, lateDays, lateFee, station.getName(), rental.getStatus(), vehicle.getStatus());
    }

    private String normalize(String value) { return value == null ? "" : value.trim().toLowerCase(Locale.ROOT); }
    private boolean isUuid(String value) {
        try { java.util.UUID.fromString(value); return true; }
        catch (IllegalArgumentException ex) { return false; }
    }

    private int calculateLateDays(Date endDate) {
        if (endDate == null) return 0;
        long days = ChronoUnit.DAYS.between(endDate.toLocalDate(), LocalDate.now());
        return days > 0 ? (int) days : 0;
    }

    private BigDecimal toMoney(Double value) {
        if (value == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
