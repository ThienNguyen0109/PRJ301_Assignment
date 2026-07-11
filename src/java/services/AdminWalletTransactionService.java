package services;

import daos.AdminWalletTransactionDAO;
import dto.AdminWalletTransactionRow;
import enums.TransactionType;
import java.sql.Date;
import java.util.List;
import utils.JPAUtil;

public class AdminWalletTransactionService {
    private final AdminWalletTransactionDAO transactionDAO = new AdminWalletTransactionDAO();

    public List<AdminWalletTransactionRow> search(String keyword, String type, String startDate, String endDate) {
        Date start = parseDate(startDate, "Start date");
        Date end = parseDate(endDate, "End date");
        if (start != null && end != null && start.after(end)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }
        TransactionType transactionType = parseType(type);
        return JPAUtil.execute(em -> transactionDAO.search(em, keyword, transactionType, start, end));
    }

    public AdminWalletTransactionRow findDetail(String id) {
        return JPAUtil.execute(em -> transactionDAO.findDetail(em, id));
    }

    private TransactionType parseType(String value) {
        if (value == null || value.trim().isEmpty() || "ALL".equalsIgnoreCase(value.trim())) {
            return null;
        }
        try {
            return TransactionType.valueOf(value.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Transaction type is invalid.");
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
