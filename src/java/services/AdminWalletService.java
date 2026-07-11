package services;

import daos.AdminWalletDAO;
import dto.AdminWalletRow;
import java.util.List;
import models.WalletTransaction;
import utils.JPAUtil;

/** Read-only administrator access to customer wallets and their audit trail. */
public class AdminWalletService {
    private final AdminWalletDAO walletDAO = new AdminWalletDAO();
    public List<AdminWalletRow> search(String keyword) { return JPAUtil.execute(em -> walletDAO.search(em, keyword)); }
    public AdminWalletRow findDetail(String walletId) { return JPAUtil.execute(em -> walletDAO.findDetail(em, walletId)); }
    public List<WalletTransaction> findTransactions(String walletId) { return JPAUtil.execute(em -> walletDAO.findTransactions(em, walletId)); }
}
