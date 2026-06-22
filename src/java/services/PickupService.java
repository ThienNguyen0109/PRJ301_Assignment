package services;

import daos.IPickupDAO;
import daos.PickupDAO;
import dto.PickupRentalDTO;
import enums.RentalStatus;
import enums.VehicleStatus;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import models.Rental;
import models.RentalStatusHistory;
import models.Vehicle;
import utils.JPAUtil;

public class PickupService {
    private final IPickupDAO pickupDAO;

    public PickupService() {
        this(new PickupDAO());
    }

    public PickupService(IPickupDAO pickupDAO) {
        this.pickupDAO = pickupDAO;
    }

    public List<PickupRentalDTO> searchBookedRentals(String keyword, Date pickupDate) {
        return pickupDAO.searchBookedRentals(keyword, pickupDate);
    }

    public PickupRentalDTO findRentalDetail(String rentalId) {
        return pickupDAO.findRentalDetail(rentalId);
    }

    public void confirmPickup(String rentalId) {
        JPAUtil.executeInTransaction(em -> {
            Rental rental = requireBookedRental(em, rentalId);
            Vehicle vehicle = pickupDAO.findVehicleForUpdate(em, rental.getVehicleId());
            if (vehicle == null) {
                throw new IllegalStateException("Không tìm thấy xe của booking.");
            }
            if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
                throw new IllegalStateException("Xe đang bảo trì, không thể giao cho khách.");
            }
            if (vehicle.getStatus() != VehicleStatus.RENTED) {
                throw new IllegalStateException("Xe chưa ở trạng thái RENTED, không thể xác nhận giao xe.");
            }

            rental.setStatus(RentalStatus.RENTED);
            persistHistory(em, rentalId, RentalStatus.RENTED);
            return null;
        });
    }

    public void markNoShow(String rentalId) {
        JPAUtil.executeInTransaction(em -> {
            Rental rental = requireBookedRental(em, rentalId);
            Vehicle vehicle = pickupDAO.findVehicleForUpdate(em, rental.getVehicleId());
            if (vehicle == null) {
                throw new IllegalStateException("Không tìm thấy xe của booking.");
            }

            rental.setStatus(RentalStatus.NO_SHOW);
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            persistHistory(em, rentalId, RentalStatus.NO_SHOW);
            return null;
        });
    }

    private Rental requireBookedRental(javax.persistence.EntityManager em, String rentalId) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID không được để trống.");
        }
        Rental rental = pickupDAO.findRentalForUpdate(em, rentalId.trim());
        if (rental == null) {
            throw new IllegalStateException("Không tìm thấy booking.");
        }
        if (rental.getStatus() != RentalStatus.BOOKED) {
            throw new IllegalStateException("Chỉ booking ở trạng thái BOOKED mới được xử lý nhận xe.");
        }
        return rental;
    }

    private void persistHistory(javax.persistence.EntityManager em, String rentalId, RentalStatus status) {
        em.persist(new RentalStatusHistory(UUID.randomUUID().toString(), rentalId, status,
                new Timestamp(System.currentTimeMillis())));
    }
}
