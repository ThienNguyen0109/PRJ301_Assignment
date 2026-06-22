package services;

import daos.IReturnDAO;
import daos.ReturnDAO;
import dto.ReturnRentalDTO;
import enums.IncidentSeverity;
import enums.MaintenanceStatus;
import enums.RentalStatus;
import enums.VehicleCondition;
import enums.VehicleStatus;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import models.IncidentReport;
import models.Rental;
import models.RentalStatusHistory;
import models.Vehicle;
import models.VehicleMaintenance;
import utils.JPAUtil;

public class ReturnService {
    private final IReturnDAO returnDAO;

    public ReturnService() { this(new ReturnDAO()); }
    public ReturnService(IReturnDAO returnDAO) { this.returnDAO = returnDAO; }

    public List<ReturnRentalDTO> searchRentedRentals(String keyword, Date endDate) {
        return returnDAO.searchRentedRentals(keyword, endDate);
    }

    public ReturnRentalDTO findRentalDetail(String rentalId) {
        return returnDAO.findRentalDetail(rentalId);
    }

    public boolean confirmReturn(String rentalId, int batteryLevel, VehicleCondition condition,
            String notes, String damageDescription, IncidentSeverity severity) {
        validateInput(batteryLevel, condition, damageDescription, severity);
        return JPAUtil.executeInTransaction(em -> {
            Rental rental = requireRentedRental(em, rentalId);
            Vehicle vehicle = returnDAO.findVehicleForUpdate(em, rental.getVehicleId());
            if (vehicle == null) {
                throw new IllegalStateException("Không tìm thấy xe của đơn thuê.");
            }
            if (vehicle.getStatus() != VehicleStatus.RENTED) {
                throw new IllegalStateException("Chỉ xe ở trạng thái RENTED mới được xác nhận trả.");
            }

            boolean damaged = condition == VehicleCondition.DAMAGED;
            vehicle.setBatteryLevel(batteryLevel);
            rental.setStatus(RentalStatus.COMPLETED);
            if (damaged) {
                String description = combineDescription(damageDescription, notes);
                em.persist(new IncidentReport(UUID.randomUUID().toString(), rental.getRentalId(),
                        vehicle.getVehicleId(), description, severity, now()));
                em.persist(new VehicleMaintenance(UUID.randomUUID().toString(), vehicle.getVehicleId(),
                        description, now(), MaintenanceStatus.PENDING));
                vehicle.setStatus(VehicleStatus.MAINTENANCE);
            } else {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
            }
            em.persist(new RentalStatusHistory(UUID.randomUUID().toString(), rental.getRentalId(),
                    RentalStatus.COMPLETED, now()));
            return damaged;
        });
    }

    private Rental requireRentedRental(EntityManager em, String rentalId) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            throw new IllegalArgumentException("Rental ID không được để trống.");
        }
        Rental rental = returnDAO.findRentalForUpdate(em, rentalId.trim());
        if (rental == null) {
            throw new IllegalStateException("Không tìm thấy đơn thuê.");
        }
        if (rental.getStatus() != RentalStatus.RENTED) {
            throw new IllegalStateException("Chỉ đơn thuê ở trạng thái RENTED mới được trả xe.");
        }
        return rental;
    }

    private void validateInput(int batteryLevel, VehicleCondition condition, String damage,
            IncidentSeverity severity) {
        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("Mức pin phải từ 0 đến 100.");
        }
        if (condition == null) {
            throw new IllegalArgumentException("Vui lòng chọn tình trạng xe.");
        }
        if (condition == VehicleCondition.DAMAGED) {
            if (damage == null || damage.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập mô tả hư hỏng.");
            }
            if (severity == null) {
                throw new IllegalArgumentException("Vui lòng chọn mức độ hư hỏng.");
            }
        }
    }

    private String combineDescription(String damage, String notes) {
        String result = damage == null ? "" : damage.trim();
        if (notes != null && !notes.trim().isEmpty()) {
            result += " | Ghi chú: " + notes.trim();
        }
        return result;
    }

    private Timestamp now() { return new Timestamp(System.currentTimeMillis()); }
}
