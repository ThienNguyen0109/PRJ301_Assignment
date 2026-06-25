package daos;

import dto.CustomerRentalHistoryDTO;
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
            "SELECT r, v, m, s FROM Rental r "
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
                rental.getStatus(),
                rental.getCreatedAt());
    }
}
