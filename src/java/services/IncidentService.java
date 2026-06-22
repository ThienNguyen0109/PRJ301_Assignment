package services;

import daos.IIncidentDAO;
import daos.IncidentDAO;
import dto.IncidentReportDTO;
import enums.IncidentSeverity;
import java.util.List;

public class IncidentService {
    private final IIncidentDAO incidentDAO;

    public IncidentService() { this(new IncidentDAO()); }
    public IncidentService(IIncidentDAO incidentDAO) { this.incidentDAO = incidentDAO; }

    public List<IncidentReportDTO> findIncidents(IncidentSeverity severity) {
        return incidentDAO.findIncidents(severity);
    }

    public IncidentReportDTO findById(String incidentId) { return incidentDAO.findById(incidentId); }
}
