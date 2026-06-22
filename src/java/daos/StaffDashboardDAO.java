package daos;

import dto.StaffActivityDTO;
import dto.StaffDashboardDTO;
import enums.RentalStatus;
import enums.VehicleStatus;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import models.Account;
import models.Rental;
import models.RentalStatusHistory;
import models.Vehicle;
import models.VehicleModel;
import utils.JPAUtil;

public class StaffDashboardDAO {

    public StaffDashboardDTO loadDashboard() {
        return JPAUtil.execute(em -> {
            long waitingPickup = countRental(em, RentalStatus.BOOKED);
            long currentlyRented = countRental(em, RentalStatus.RENTED);
            long waitingReturn = em.createQuery(
                    "SELECT COUNT(r) FROM Rental r WHERE r.status = :status AND r.endDate <= :today", Long.class)
                    .setParameter("status", RentalStatus.RENTED)
                    .setParameter("today", new Date(System.currentTimeMillis()))
                    .getSingleResult();
            long maintenance = em.createQuery(
                    "SELECT COUNT(v) FROM Vehicle v WHERE v.status = :status", Long.class)
                    .setParameter("status", VehicleStatus.MAINTENANCE)
                    .getSingleResult();

            List<StaffActivityDTO> activities = em.createQuery(
                    "SELECT h, r, c, v, m FROM RentalStatusHistory h "
                    + "JOIN h.rental r JOIN r.customer c JOIN r.vehicle v JOIN v.model m "
                    + "ORDER BY h.changedAt DESC", Object[].class)
                    .setMaxResults(8)
                    .getResultList().stream().map(this::mapActivity).collect(Collectors.toList());

            return new StaffDashboardDTO(waitingPickup, currentlyRented, waitingReturn, maintenance, activities);
        });
    }

    private long countRental(javax.persistence.EntityManager em, RentalStatus status) {
        return em.createQuery("SELECT COUNT(r) FROM Rental r WHERE r.status = :status", Long.class)
                .setParameter("status", status).getSingleResult();
    }

    private StaffActivityDTO mapActivity(Object[] row) {
        RentalStatusHistory history = (RentalStatusHistory) row[0];
        Rental rental = (Rental) row[1];
        Account customer = (Account) row[2];
        Vehicle vehicle = (Vehicle) row[3];
        VehicleModel model = (VehicleModel) row[4];
        return new StaffActivityDTO(rental.getRentalId(), customer.getFullName(),
                model.getName() + " · " + vehicle.getLicensePlate(), action(history), history.getChangedAt());
    }

    private String action(RentalStatusHistory history) {
        switch (history.getStatus()) {
            case RENTED: return "Vehicle Picked Up";
            case COMPLETED: return "Vehicle Returned";
            case NO_SHOW: return "Marked No Show";
            case CANCELLED: return "Rental Cancelled";
            default: return "Booking Created";
        }
    }
}
