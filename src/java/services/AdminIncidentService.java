package services;
import daos.AdminIncidentDAO; import dto.AdminIncidentRow; import enums.IncidentSeverity; import java.util.List; import models.IncidentReport; import utils.JPAUtil;
public class AdminIncidentService {
    private final AdminIncidentDAO incidentDAO=new AdminIncidentDAO();
    public List<AdminIncidentRow> search(String keyword,String severity){return JPAUtil.execute(em->incidentDAO.search(em,keyword,parseSeverity(severity)));}
    public AdminIncidentRow findDetail(String id){return JPAUtil.execute(em->incidentDAO.findDetail(em,id));}
    public void update(String id,String description,String severity){required(id,"Incident ID");required(description,"Incident description");IncidentSeverity value=parseSeverityRequired(severity);JPAUtil.executeInTransaction(em->{IncidentReport incident=incidentDAO.findForUpdate(em,id);if(incident==null)throw new IllegalArgumentException("Incident not found.");incident.setDescription(description.trim());incident.setSeverity(value);return null;});}
    private IncidentSeverity parseSeverity(String v){if(v==null||v.trim().isEmpty()||"ALL".equalsIgnoreCase(v.trim()))return null;return parseSeverityRequired(v);} private IncidentSeverity parseSeverityRequired(String v){try{return IncidentSeverity.valueOf(v.trim().toUpperCase());}catch(Exception e){throw new IllegalArgumentException("Incident severity is invalid.");}} private void required(String v,String label){if(v==null||v.trim().isEmpty())throw new IllegalArgumentException(label+" is required.");}
}
