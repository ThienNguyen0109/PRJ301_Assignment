package daos;

import dto.IncidentReportDTO;
import enums.IncidentSeverity;
import java.util.List;
import java.util.stream.Collectors;
import models.IncidentReport;
import models.Vehicle;
import models.VehicleModel;
import utils.JPAUtil;

public class IncidentDAO implements IIncidentDAO {
    @Override
    public List<IncidentReportDTO> findIncidents(IncidentSeverity severity) {
        return JPAUtil.execute(em -> {
            String jpql = "SELECT i, v, m FROM IncidentReport i JOIN i.vehicle v JOIN v.model m "
                    + (severity == null ? "" : "WHERE i.severity = :severity ")
                    + "ORDER BY i.createdAt DESC";
            javax.persistence.TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            if (severity != null) query.setParameter("severity", severity);
            return query.getResultList().stream().map(this::map).collect(Collectors.toList());
        });
    }

    @Override
    public IncidentReportDTO findById(String incidentId) {
        if (incidentId == null || incidentId.trim().isEmpty()) return null;
        return JPAUtil.execute(em -> {
            List<Object[]> rows = em.createQuery(
                    "SELECT i, v, m FROM IncidentReport i JOIN i.vehicle v JOIN v.model m WHERE i.incidentId = :id",
                    Object[].class).setParameter("id", incidentId.trim()).setMaxResults(1).getResultList();
            return rows.isEmpty() ? null : map(rows.get(0));
        });
    }

    private IncidentReportDTO map(Object[] row) {
        IncidentReport incident = (IncidentReport) row[0];
        Vehicle vehicle = (Vehicle) row[1];
        VehicleModel model = (VehicleModel) row[2];
        return new IncidentReportDTO(incident.getIncidentId(), incident.getRentalId(), model.getName(),
                vehicle.getLicensePlate(), incident.getDescription(), incident.getSeverity(), incident.getCreatedAt());
    }
}
