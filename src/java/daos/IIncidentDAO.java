package daos;

import dto.IncidentReportDTO;
import enums.IncidentSeverity;
import java.util.List;

public interface IIncidentDAO {
    List<IncidentReportDTO> findIncidents(IncidentSeverity severity);
    IncidentReportDTO findById(String incidentId);
}
