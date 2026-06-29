package daos;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class AdminReportDAO {

    public Object[] financialTotals(EntityManager em, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT "
                + "COALESCE(SUM(CASE WHEN status = 'SUCCESS' THEN amount ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN status = 'SUCCESS' AND payment_type = 'BOOKING' THEN amount ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN status = 'SUCCESS' AND payment_type <> 'BOOKING' THEN amount ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END), 0) "
                + "FROM Payment WHERE payment_date >= ?1 AND payment_date < ?2";
        return (Object[]) em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .getSingleResult();
    }

    public List<Object[]> financialRows(EntityManager em, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT payment_method, payment_type, status, COALESCE(SUM(amount), 0) "
                + "FROM Payment WHERE payment_date >= ?1 AND payment_date < ?2 "
                + "GROUP BY payment_method, payment_type, status "
                + "ORDER BY COALESCE(SUM(amount), 0) DESC";
        return em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .getResultList();
    }

    public List<Object[]> monthlyRevenue(EntityManager em, int year) {
        String sql = "SELECT MONTH(payment_date), COALESCE(SUM(amount), 0) "
                + "FROM Payment "
                + "WHERE status = 'SUCCESS' AND YEAR(payment_date) = ?1 "
                + "GROUP BY MONTH(payment_date) "
                + "ORDER BY MONTH(payment_date)";
        return em.createNativeQuery(sql)
                .setParameter(1, year)
                .getResultList();
    }

    public List<Object[]> monthlyRevenue(EntityManager em, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT YEAR(payment_date), MONTH(payment_date), COALESCE(SUM(amount), 0) "
                + "FROM Payment "
                + "WHERE status = 'SUCCESS' AND payment_date >= ?1 AND payment_date < ?2 "
                + "GROUP BY YEAR(payment_date), MONTH(payment_date) "
                + "ORDER BY YEAR(payment_date), MONTH(payment_date)";
        return em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .getResultList();
    }

    public List<Object[]> quarterlyRevenue(EntityManager em, int year) {
        String sql = "SELECT DATEPART(QUARTER, payment_date), COALESCE(SUM(amount), 0) "
                + "FROM Payment "
                + "WHERE status = 'SUCCESS' AND YEAR(payment_date) = ?1 "
                + "GROUP BY DATEPART(QUARTER, payment_date) "
                + "ORDER BY DATEPART(QUARTER, payment_date)";
        return em.createNativeQuery(sql)
                .setParameter(1, year)
                .getResultList();
    }

    public List<Object[]> dailyRevenue(EntityManager em, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT CAST(payment_date AS date), COALESCE(SUM(amount), 0) "
                + "FROM Payment "
                + "WHERE status = 'SUCCESS' AND payment_date >= ?1 AND payment_date < ?2 "
                + "GROUP BY CAST(payment_date AS date) "
                + "ORDER BY CAST(payment_date AS date)";
        return em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .getResultList();
    }

    public List<Object[]> paymentMix(EntityManager em, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT payment_method, COALESCE(SUM(amount), 0) "
                + "FROM Payment "
                + "WHERE status = 'SUCCESS' AND payment_date >= ?1 AND payment_date < ?2 "
                + "GROUP BY payment_method "
                + "ORDER BY COALESCE(SUM(amount), 0) DESC";
        return em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .getResultList();
    }

    public Object[] fleetStatusTotals(EntityManager em) {
        String sql = "SELECT "
                + "COALESCE(SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN status = 'RENTED' THEN 1 ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN status = 'MAINTENANCE' THEN 1 ELSE 0 END), 0), "
                + "COUNT(*) "
                + "FROM Vehicle";
        return (Object[]) em.createNativeQuery(sql).getSingleResult();
    }

    public Object[] topStationRevenue(EntityManager em, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT TOP 1 s.name, COALESCE(SUM(p.amount), 0) "
                + "FROM Station s "
                + "JOIN Rental r ON r.pickup_station_id = s.station_id "
                + "JOIN Payment p ON p.rental_id = r.rental_id "
                + "WHERE p.status = 'SUCCESS' AND p.payment_date >= ?1 AND p.payment_date < ?2 "
                + "GROUP BY s.name "
                + "ORDER BY COALESCE(SUM(p.amount), 0) DESC";
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .getResultList();
        return rows.isEmpty() ? new Object[]{"N/A", 0} : rows.get(0);
    }

    public List<Object[]> stationRows(EntityManager em, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT s.station_id, s.name, "
                + "COALESCE(SUM(CASE WHEN v.status = 'AVAILABLE' THEN 1 ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN v.status = 'RENTED' THEN 1 ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN v.status = 'MAINTENANCE' THEN 1 ELSE 0 END), 0), "
                + "COALESCE((SELECT SUM(p.amount) FROM Rental r "
                + "JOIN Payment p ON p.rental_id = r.rental_id "
                + "WHERE r.pickup_station_id = s.station_id "
                + "AND p.status = 'SUCCESS' AND p.payment_date >= ?1 AND p.payment_date < ?2), 0) "
                + "FROM Station s "
                + "LEFT JOIN Vehicle v ON v.station_id = s.station_id "
                + "GROUP BY s.station_id, s.name "
                + "ORDER BY s.name";
        return em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .getResultList();
    }

    public Object[] modelTotals(EntityManager em, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM Vehicle_Model), "
                + "(SELECT COUNT(*) FROM Incident_Report WHERE created_at >= ?1 AND created_at < ?2), "
                + "(SELECT COUNT(*) FROM Rental WHERE created_at >= ?1 AND created_at < ?2), "
                + "(SELECT COUNT(*) FROM Vehicle WHERE status = 'RENTED'), "
                + "(SELECT COUNT(*) FROM Vehicle) ";
        return (Object[]) em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .getSingleResult();
    }

    public Object[] mostBookedModel(EntityManager em, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT TOP 1 vm.name, COUNT(r.rental_id) "
                + "FROM Vehicle_Model vm "
                + "JOIN Vehicle v ON v.model_id = vm.model_id "
                + "JOIN Rental r ON r.vehicle_id = v.vehicle_id "
                + "WHERE r.created_at >= ?1 AND r.created_at < ?2 "
                + "GROUP BY vm.name "
                + "ORDER BY COUNT(r.rental_id) DESC";
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .getResultList();
        return rows.isEmpty() ? new Object[]{"N/A", 0} : rows.get(0);
    }

    public List<Object[]> modelRows(EntityManager em, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT vm.model_id, vm.name, "
                + "(SELECT COUNT(*) FROM Rental r JOIN Vehicle v2 ON v2.vehicle_id = r.vehicle_id "
                + "WHERE v2.model_id = vm.model_id AND r.created_at >= ?1 AND r.created_at < ?2), "
                + "(SELECT COALESCE(SUM(p.amount), 0) FROM Payment p "
                + "JOIN Rental r2 ON r2.rental_id = p.rental_id "
                + "JOIN Vehicle v3 ON v3.vehicle_id = r2.vehicle_id "
                + "WHERE v3.model_id = vm.model_id AND p.status = 'SUCCESS' "
                + "AND p.payment_date >= ?1 AND p.payment_date < ?2), "
                + "(SELECT COUNT(*) FROM Incident_Report ir JOIN Vehicle v4 ON v4.vehicle_id = ir.vehicle_id "
                + "WHERE v4.model_id = vm.model_id AND ir.created_at >= ?1 AND ir.created_at < ?2) "
                + "FROM Vehicle_Model vm "
                + "ORDER BY vm.name";
        return em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .getResultList();
    }

    public List<Object[]> financialDetailRows(EntityManager em, Timestamp start, Timestamp endExclusive,
            String paymentMethod, String paymentType, String status) {
        String sql = "SELECT p.payment_id, a.full_name, p.payment_method, p.payment_type, p.status, p.amount, p.payment_date "
                + "FROM Payment p "
                + "JOIN Rental r ON r.rental_id = p.rental_id "
                + "JOIN Account a ON a.account_id = r.customer_id "
                + "WHERE p.payment_date >= ?1 AND p.payment_date < ?2 "
                + "AND p.payment_method = ?3 AND p.payment_type = ?4 AND p.status = ?5 "
                + "ORDER BY p.payment_date DESC";
        return em.createNativeQuery(sql)
                .setParameter(1, start)
                .setParameter(2, endExclusive)
                .setParameter(3, paymentMethod)
                .setParameter(4, paymentType)
                .setParameter(5, status)
                .getResultList();
    }

    public List<Object[]> stationDetailRows(EntityManager em, String stationId, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT v.license_plate, vm.name, v.status, v.battery_level, "
                + "COALESCE((SELECT COUNT(*) FROM Rental r WHERE r.vehicle_id = v.vehicle_id "
                + "AND r.created_at >= ?2 AND r.created_at < ?3), 0), "
                + "COALESCE((SELECT SUM(p.amount) FROM Payment p JOIN Rental r2 ON r2.rental_id = p.rental_id "
                + "WHERE r2.vehicle_id = v.vehicle_id AND p.status = 'SUCCESS' "
                + "AND p.payment_date >= ?2 AND p.payment_date < ?3), 0) "
                + "FROM Vehicle v "
                + "JOIN Vehicle_Model vm ON vm.model_id = v.model_id "
                + "WHERE v.station_id = ?1 "
                + "ORDER BY vm.name, v.license_plate";
        return em.createNativeQuery(sql)
                .setParameter(1, stationId)
                .setParameter(2, start)
                .setParameter(3, endExclusive)
                .getResultList();
    }

    public Object stationInfo(EntityManager em, String stationId) {
        List<Object> rows = em.createNativeQuery("SELECT name FROM Station WHERE station_id = ?1")
                .setParameter(1, stationId)
                .getResultList();
        return rows.isEmpty() ? "Station" : rows.get(0);
    }

    public List<Object[]> modelDetailRows(EntityManager em, String modelId, Timestamp start, Timestamp endExclusive) {
        String sql = "SELECT v.license_plate, s.name, v.status, v.battery_level, "
                + "COALESCE((SELECT COUNT(*) FROM Rental r WHERE r.vehicle_id = v.vehicle_id "
                + "AND r.created_at >= ?2 AND r.created_at < ?3), 0), "
                + "COALESCE((SELECT SUM(p.amount) FROM Payment p JOIN Rental r2 ON r2.rental_id = p.rental_id "
                + "WHERE r2.vehicle_id = v.vehicle_id AND p.status = 'SUCCESS' "
                + "AND p.payment_date >= ?2 AND p.payment_date < ?3), 0) "
                + "FROM Vehicle v "
                + "JOIN Station s ON s.station_id = v.station_id "
                + "WHERE v.model_id = ?1 "
                + "ORDER BY s.name, v.license_plate";
        return em.createNativeQuery(sql)
                .setParameter(1, modelId)
                .setParameter(2, start)
                .setParameter(3, endExclusive)
                .getResultList();
    }

    public Object modelInfo(EntityManager em, String modelId) {
        List<Object> rows = em.createNativeQuery("SELECT name FROM Vehicle_Model WHERE model_id = ?1")
                .setParameter(1, modelId)
                .getResultList();
        return rows.isEmpty() ? "Vehicle Model" : rows.get(0);
    }

    public Object[] dashboardTotals(EntityManager em) {
        String sql = "SELECT "
                + "(SELECT COALESCE(SUM(amount), 0) FROM Payment WHERE status = 'SUCCESS'), "
                + "(SELECT COUNT(*) FROM Rental WHERE status IN ('BOOKED', 'RENTED')), "
                + "(SELECT COUNT(*) FROM Vehicle WHERE status = 'AVAILABLE'), "
                + "(SELECT COUNT(*) FROM Extra_Charge WHERE status IN ('PENDING', 'UNPAID'))";
        return (Object[]) em.createNativeQuery(sql).getSingleResult();
    }

    public List<Object[]> recentDashboardRows(EntityManager em) {
        String sql = "SELECT TOP 6 r.rental_id, a.full_name, vm.name, r.status "
                + "FROM Rental r "
                + "JOIN Account a ON a.account_id = r.customer_id "
                + "JOIN Vehicle v ON v.vehicle_id = r.vehicle_id "
                + "JOIN Vehicle_Model vm ON vm.model_id = v.model_id "
                + "ORDER BY r.created_at DESC";
        return em.createNativeQuery(sql).getResultList();
    }
}
