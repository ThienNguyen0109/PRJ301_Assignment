package services;
import daos.AdminReviewDAO; import dto.AdminReviewRow; import java.util.List; import utils.JPAUtil;
public class AdminReviewService {
    private final AdminReviewDAO reviewDAO=new AdminReviewDAO();
    public List<AdminReviewRow> search(String keyword,String rating){return JPAUtil.execute(em->reviewDAO.search(em,keyword,parseRating(rating)));}
    public AdminReviewRow findDetail(String id){return JPAUtil.execute(em->reviewDAO.findDetail(em,id));}
    private Integer parseRating(String value){if(value==null||value.trim().isEmpty()||"ALL".equalsIgnoreCase(value.trim()))return null;try{int rating=Integer.parseInt(value.trim());if(rating<1||rating>5)throw new NumberFormatException();return rating;}catch(NumberFormatException e){throw new IllegalArgumentException("Rating must be from 1 to 5.");}}
}
