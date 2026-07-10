package services;

import daos.AdminRentalDAO;
import dto.AdminRentalRow;
import enums.RentalStatus;
import enums.VehicleStatus;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import models.Payment;
import models.Rental;
import models.RentalStatusHistory;
import models.Station;
import models.Vehicle;
import realtime.RealtimeEventPublisher;
import utils.JPAUtil;

/** Administrative read and cancellation operations for rental transactions. */
public class AdminRentalService {
    private final AdminRentalDAO rentalDAO = new AdminRentalDAO();

    public List<AdminRentalRow> search(String keyword, String stationId, String status, String startDate, String endDate) {
        Date start = parseOptionalDate(startDate, "Start date");
        Date end = parseOptionalDate(endDate, "End date");
        if (start != null && end != null && start.after(end)) throw new IllegalArgumentException("Start date must not be after end date.");
        return JPAUtil.execute(em -> rentalDAO.search(em, keyword, stationId, parseStatus(status), start, end));
    }
    public AdminRentalRow findDetail(String rentalId) { return JPAUtil.execute(em -> rentalDAO.findDetail(em, rentalId)); }
    public List<Station> findAllStations() { return JPAUtil.execute(rentalDAO::findAllStations); }
    public List<Payment> findPayments(String rentalId) { return JPAUtil.execute(em -> rentalDAO.findPayments(em, rentalId)); }
    public List<RentalStatusHistory> findHistory(String rentalId) { return JPAUtil.execute(em -> rentalDAO.findHistory(em, rentalId)); }

    public void cancelBookedRental(String rentalId) {
        required(rentalId, "Rental ID");
        JPAUtil.executeInTransaction(em -> {
            Rental rental = rentalDAO.findForUpdate(em, rentalId);
            if (rental == null) throw new IllegalArgumentException("Rental not found.");
            if (rental.getStatus() != RentalStatus.BOOKED) throw new IllegalStateException("Only a BOOKED rental can be cancelled.");
            Vehicle vehicle = rentalDAO.findVehicleForUpdate(em, rental.getVehicleId());
            rental.setStatus(RentalStatus.CANCELLED);
            if (vehicle != null && vehicle.getStatus() == VehicleStatus.RENTED) vehicle.setStatus(VehicleStatus.AVAILABLE);
            em.persist(new RentalStatusHistory(UUID.randomUUID().toString(), rental.getRentalId(),
                    RentalStatus.CANCELLED, new Timestamp(System.currentTimeMillis())));
            return null;
        });
        RealtimeEventPublisher.admin("RENTAL_CANCELLED", "Rental cancelled", "A booked rental was cancelled by an administrator.");
    }

    private RentalStatus parseStatus(String value) {
        if (value == null || value.trim().isEmpty() || "ALL".equalsIgnoreCase(value.trim())) return null;
        try { return RentalStatus.valueOf(value.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new IllegalArgumentException("Rental status is invalid."); }
    }
    private Date parseOptionalDate(String value, String label) {
        if (value == null || value.trim().isEmpty()) return null;
        try { return Date.valueOf(value.trim()); }
        catch (IllegalArgumentException ex) { throw new IllegalArgumentException(label + " is invalid."); }
    }
    private void required(String value, String label) { if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(label + " is required."); }
}
