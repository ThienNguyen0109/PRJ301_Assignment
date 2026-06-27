package daos;

import dto.CustomerRentalHistoryDTO;
import enums.ExtraChargeStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import models.Rental;
import models.Station;
import models.Vehicle;
import models.VehicleModel;
import utils.JPAUtil;

public class CustomerRentalHistoryDAO {
    private static final String HISTORY_QUERY =
            "SELECT r, v, m, s, "
            + "(SELECT COALESCE(SUM(c.amount), 0) FROM ExtraCharge c "
            + "WHERE c.rentalId = r.rentalId AND c.status <> :cancelledStatus) "
            + "FROM Rental r "
            + "JOIN r.vehicle v "
            + "JOIN v.model m "
            + "JOIN r.pickupStation s "
            + "WHERE r.customerId = :customerId "
            + "ORDER BY r.createdAt DESC, r.startDate DESC";

    public List<CustomerRentalHistoryDTO> findByCustomerId(String customerId) {
        return findByCustomerId(customerId, 0, Integer.MAX_VALUE);
    }

    public List<CustomerRentalHistoryDTO> findByCustomerId(String customerId, int offset, int limit) {
        return JPAUtil.execute(em -> em.createQuery(
                HISTORY_QUERY, Object[].class)
                .setParameter("customerId", customerId)
                .setParameter("cancelledStatus", ExtraChargeStatus.CANCELLED)
                .setFirstResult(Math.max(0, offset))
                .setMaxResults(Math.max(1, limit))
                .getResultList()
                .stream()
                .map(this::map)
                .collect(Collectors.toList()));
    }

    public long countByCustomerId(String customerId) {
        return JPAUtil.execute(em -> em.createQuery(
                "SELECT COUNT(r) FROM Rental r WHERE r.customerId = :customerId", Long.class)
                .setParameter("customerId", customerId)
                .getSingleResult());
    }

    private CustomerRentalHistoryDTO map(Object[] row) {
        Rental rental = (Rental) row[0];
        Vehicle vehicle = (Vehicle) row[1];
        VehicleModel model = (VehicleModel) row[2];
        Station station = (Station) row[3];
        BigDecimal extraChargeTotal = toBigDecimal(row[4]);
        return new CustomerRentalHistoryDTO(
                rental.getRentalId(),
                model.getName(),
                vehicle.getLicensePlate(),
                station.getName(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getActualReturnDate(),
                rental.getTotalDays(),
                rental.getTotalAmount(),
                rental.getLateFee() == null ? BigDecimal.ZERO : rental.getLateFee(),
                extraChargeTotal,
                rental.getStatus(),
                rental.getCreatedAt());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return BigDecimal.ZERO;
    }
}
