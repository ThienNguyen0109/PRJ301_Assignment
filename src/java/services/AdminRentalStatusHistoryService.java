package services;

import daos.AdminRentalStatusHistoryDAO;
import dto.AdminRentalStatusHistoryRow;
import enums.RentalStatus;
import java.sql.Date;
import java.util.List;
import utils.JPAUtil;

public class AdminRentalStatusHistoryService {
    private final AdminRentalStatusHistoryDAO historyDAO = new AdminRentalStatusHistoryDAO();

    public List<AdminRentalStatusHistoryRow> search(String keyword, String status, String startDate, String endDate) {
        Date start = parseDate(startDate, "Start date");
        Date end = parseDate(endDate, "End date");
        if (start != null && end != null && start.after(end)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }
        RentalStatus rentalStatus = parseStatus(status);
        return JPAUtil.execute(em -> historyDAO.search(em, keyword, rentalStatus, start, end));
    }

    public AdminRentalStatusHistoryRow findDetail(String id) {
        return JPAUtil.execute(em -> historyDAO.findDetail(em, id));
    }

    private RentalStatus parseStatus(String value) {
        if (value == null || value.trim().isEmpty() || "ALL".equalsIgnoreCase(value.trim())) {
            return null;
        }
        try {
            return RentalStatus.valueOf(value.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Rental status is invalid.");
        }
    }

    private Date parseDate(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Date.valueOf(value.trim());
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(label + " is invalid.");
        }
    }
}
