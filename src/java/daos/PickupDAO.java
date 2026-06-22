package daos;

import dto.PickupRentalDTO;
import enums.RentalStatus;
import java.sql.Date;
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

public class PickupDAO implements IPickupDAO {

    @Override
    public List<PickupRentalDTO> searchBookedRentals(String keyword, Date pickupDate) {
        return JPAUtil.execute(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT r, c, v, m, s FROM Rental r "
                    + "JOIN r.customer c JOIN r.vehicle v JOIN v.model m JOIN r.pickupStation s "
                    + "WHERE r.status = :status ");
            String normalized = normalize(keyword);
            boolean rentalIdSearch = isUuid(normalized);
            if (!normalized.isEmpty()) {
                jpql.append("AND (");
                if (rentalIdSearch) {
                    jpql.append("r.rentalId = :rentalId OR ");
                }
                jpql.append("LOWER(c.email) LIKE :keyword OR LOWER(c.fullName) LIKE :keyword OR c.phone LIKE :phone) ");
            }
            if (pickupDate != null) {
                jpql.append("AND r.startDate = :pickupDate ");
            }
            jpql.append("ORDER BY r.startDate ASC, r.createdAt ASC");

            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                    .setParameter("status", RentalStatus.BOOKED);
            if (!normalized.isEmpty()) {
                query.setParameter("keyword", "%" + normalized + "%");
                query.setParameter("phone", "%" + normalized + "%");
                if (rentalIdSearch) {
                    query.setParameter("rentalId", normalized);
                }
            }
            if (pickupDate != null) {
                query.setParameter("pickupDate", pickupDate);
            }
            return query.getResultList().stream().map(this::map).collect(Collectors.toList());
        });
    }

    @Override
    public PickupRentalDTO findRentalDetail(String rentalId) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            return null;
        }
        return JPAUtil.execute(em -> {
            List<Object[]> rows = em.createQuery(
                    "SELECT r, c, v, m, s FROM Rental r "
                    + "JOIN r.customer c JOIN r.vehicle v JOIN v.model m JOIN r.pickupStation s "
                    + "WHERE r.rentalId = :rentalId", Object[].class)
                    .setParameter("rentalId", rentalId.trim())
                    .setMaxResults(1)
                    .getResultList();
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

    private PickupRentalDTO map(Object[] row) {
        Rental rental = (Rental) row[0];
        Account customer = (Account) row[1];
        Vehicle vehicle = (Vehicle) row[2];
        VehicleModel model = (VehicleModel) row[3];
        Station station = (Station) row[4];
        return new PickupRentalDTO(rental.getRentalId(), customer.getFullName(), customer.getEmail(),
                customer.getPhone(), model.getName(), vehicle.getLicensePlate(), vehicle.getBatteryLevel(),
                rental.getStartDate(), rental.getEndDate(), station.getName(), rental.getStatus(), vehicle.getStatus());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isUuid(String value) {
        try {
            java.util.UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
