package daos;
import dto.AdminReviewRow; import java.util.*; import javax.persistence.*;
public class AdminReviewDAO {
    private static final String SELECT="SELECT r, c, m FROM Review r JOIN r.customer c JOIN r.model m ";
    public List<AdminReviewRow> search(EntityManager em,String keyword,Integer rating){String key=trim(keyword).toLowerCase();String jpql=SELECT+"WHERE (:key='' OR LOWER(c.fullName) LIKE :pattern OR LOWER(c.email) LIKE :pattern OR LOWER(m.name) LIKE :pattern OR LOWER(COALESCE(r.comment,'')) LIKE :pattern)"+(rating==null?"":" AND r.rating=:rating")+" ORDER BY r.createdAt DESC";TypedQuery<Object[]> q=em.createQuery(jpql,Object[].class).setParameter("key",key).setParameter("pattern","%"+key+"%");if(rating!=null)q.setParameter("rating",rating);return map(q.getResultList());}
    public AdminReviewRow findDetail(EntityManager em,String id){List<Object[]> rows=em.createQuery(SELECT+"WHERE r.reviewId=:id",Object[].class).setParameter("id",trim(id)).setMaxResults(1).getResultList();return rows.isEmpty()?null:map(rows).get(0);}
    private List<AdminReviewRow> map(List<Object[]> rows){List<AdminReviewRow> out=new ArrayList<>();for(Object[] row:rows){models.Review r=(models.Review)row[0];models.Account c=(models.Account)row[1];models.VehicleModel m=(models.VehicleModel)row[2];out.add(new AdminReviewRow(r.getReviewId(),r.getRentalId(),c.getFullName(),c.getEmail(),m.getName(),r.getRating(),r.getComment(),r.getCreatedAt()));}return out;} private String trim(String v){return v==null?"":v.trim();}
}
