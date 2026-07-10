package daos;
import dto.AdminIncidentRow; import enums.IncidentSeverity; import java.util.*; import javax.persistence.*; import models.IncidentReport;
public class AdminIncidentDAO {
    private static final String SELECT="SELECT i, v, m FROM IncidentReport i JOIN i.vehicle v JOIN v.model m ";
    public List<AdminIncidentRow> search(EntityManager em,String keyword,IncidentSeverity severity){String key=trim(keyword).toLowerCase();String jpql=SELECT+"WHERE (:key='' OR LOWER(i.incidentId) LIKE :pattern OR LOWER(i.rentalId) LIKE :pattern OR LOWER(v.licensePlate) LIKE :pattern OR LOWER(m.name) LIKE :pattern)"+(severity==null?"":" AND i.severity=:severity")+" ORDER BY i.createdAt DESC";TypedQuery<Object[]> q=em.createQuery(jpql,Object[].class).setParameter("key",key).setParameter("pattern","%"+key+"%");if(severity!=null)q.setParameter("severity",severity);return map(q.getResultList());}
    public AdminIncidentRow findDetail(EntityManager em,String id){List<Object[]> rows=em.createQuery(SELECT+"WHERE i.incidentId=:id",Object[].class).setParameter("id",trim(id)).setMaxResults(1).getResultList();return rows.isEmpty()?null:map(rows).get(0);}
    public IncidentReport findForUpdate(EntityManager em,String id){return em.find(IncidentReport.class,trim(id),LockModeType.PESSIMISTIC_WRITE);}
    private List<AdminIncidentRow> map(List<Object[]> rows){List<AdminIncidentRow> out=new ArrayList<>();for(Object[] row:rows){IncidentReport i=(IncidentReport)row[0];models.Vehicle v=(models.Vehicle)row[1];models.VehicleModel m=(models.VehicleModel)row[2];out.add(new AdminIncidentRow(i.getIncidentId(),i.getRentalId(),i.getVehicleId(),v.getLicensePlate(),m.getName(),i.getDescription(),i.getSeverity(),i.getCreatedAt()));}return out;} private String trim(String v){return v==null?"":v.trim();}
}
