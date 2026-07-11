package services;

import daos.AdminRentalDiscountDAO;
import dto.AdminRentalDiscountRow;
import java.util.List;
import utils.JPAUtil;

public class AdminRentalDiscountService {
    private final AdminRentalDiscountDAO rentalDiscountDAO = new AdminRentalDiscountDAO();

    public List<AdminRentalDiscountRow> search(String keyword) {
        return JPAUtil.execute(em -> rentalDiscountDAO.search(em, keyword));
    }

    public AdminRentalDiscountRow findDetail(String id) {
        return JPAUtil.execute(em -> rentalDiscountDAO.findDetail(em, id));
    }
}
