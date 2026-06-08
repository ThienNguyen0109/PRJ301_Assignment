package daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Vehicle;
import dto.VehicleSearchResult;
import enums.VehicleStatus;
import utils.DBUtils;

/**
 * Data Access Object for vehicle availability search
 */
public class VehicleSearchDAO implements IVehicleSearchDAO {
    private static final Logger LOGGER = Logger.getLogger(VehicleSearchDAO.class.getName());

    @Override
    public List<VehicleSearchResult> getFeaturedAvailableVehicleModels(int limit) {
        List<VehicleSearchResult> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql =
                "SELECT TOP (?) vm.model_id, vm.name AS model_name, vm.brand, vm.description, vm.price_per_day, vm.seat_count, " +
                "       s.station_id, s.name AS station_name, s.address AS station_address, " +
                "       COUNT(v.vehicle_id) AS remaining, " +
                "       img.image_url AS thumbnail_image " +
                "FROM Vehicle v " +
                "INNER JOIN Vehicle_Model vm ON v.model_id = vm.model_id " +
                "INNER JOIN Station s ON v.station_id = s.station_id " +
                "OUTER APPLY ( " +
                "    SELECT TOP 1 image_url " +
                "    FROM Vehicle_Model_Image " +
                "    WHERE model_id = vm.model_id " +
                "    ORDER BY CASE image_type WHEN 'FRONT' THEN 0 WHEN 'BACK' THEN 1 ELSE 2 END " +
                ") img " +
                "WHERE v.status = 'AVAILABLE' " +
                "GROUP BY vm.model_id, vm.name, vm.brand, vm.description, vm.price_per_day, vm.seat_count, " +
                "         s.station_id, s.name, s.address, img.image_url " +
                "ORDER BY remaining DESC, vm.name, s.name";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Math.max(1, limit));
            rs = stmt.executeQuery();

            while (rs.next()) {
                VehicleSearchResult result = new VehicleSearchResult();
                result.setModelId(rs.getString("model_id"));
                result.setModelName(rs.getString("model_name"));
                result.setBrand(rs.getString("brand"));
                result.setDescription(rs.getString("description"));
                result.setPricePerDay(rs.getDouble("price_per_day"));
                result.setSeatCount(rs.getInt("seat_count"));
                result.setRemaining(rs.getInt("remaining"));
                result.setThumbnailImage(rs.getString("thumbnail_image"));
                result.setStationId(rs.getString("station_id"));
                result.setStationName(rs.getString("station_name"));
                result.setStationAddress(rs.getString("station_address"));
                results.add(result);
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return results;
    }

    @Override
    public VehicleSearchResult getAvailableVehicleModelAtStation(String modelId, String stationId) {
        VehicleSearchResult result = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql =
                "SELECT vm.model_id, vm.name AS model_name, vm.brand, vm.description, vm.price_per_day, vm.seat_count, " +
                "       s.station_id, s.name AS station_name, s.address AS station_address, " +
                "       COUNT(v.vehicle_id) AS remaining, " +
                "       img.image_url AS thumbnail_image " +
                "FROM Vehicle v " +
                "INNER JOIN Vehicle_Model vm ON v.model_id = vm.model_id " +
                "INNER JOIN Station s ON v.station_id = s.station_id " +
                "OUTER APPLY ( " +
                "    SELECT TOP 1 image_url " +
                "    FROM Vehicle_Model_Image " +
                "    WHERE model_id = vm.model_id " +
                "    ORDER BY CASE image_type WHEN 'FRONT' THEN 0 WHEN 'BACK' THEN 1 ELSE 2 END " +
                ") img " +
                "WHERE v.status = 'AVAILABLE' " +
                "  AND vm.model_id = ? " +
                "  AND s.station_id = ? " +
                "GROUP BY vm.model_id, vm.name, vm.brand, vm.description, vm.price_per_day, vm.seat_count, " +
                "         s.station_id, s.name, s.address, img.image_url";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, modelId);
            stmt.setString(2, stationId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                result = new VehicleSearchResult();
                result.setModelId(rs.getString("model_id"));
                result.setModelName(rs.getString("model_name"));
                result.setBrand(rs.getString("brand"));
                result.setDescription(rs.getString("description"));
                result.setPricePerDay(rs.getDouble("price_per_day"));
                result.setSeatCount(rs.getInt("seat_count"));
                result.setRemaining(rs.getInt("remaining"));
                result.setThumbnailImage(rs.getString("thumbnail_image"));
                result.setStationId(rs.getString("station_id"));
                result.setStationName(rs.getString("station_name"));
                result.setStationAddress(rs.getString("station_address"));
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return result;
    }

    @Override
    public List<VehicleSearchResult> searchAvailableVehicleModels(String stationId, String categoryId) {
        List<VehicleSearchResult> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT vm.model_id, vm.name AS model_name, vm.brand, vm.description, vm.price_per_day, vm.seat_count, ");
            sql.append("       s.station_id, s.name AS station_name, s.address AS station_address, ");
            sql.append("       COUNT(v.vehicle_id) AS remaining, ");
            sql.append("       img.image_url AS thumbnail_image ");
            sql.append("FROM Vehicle v ");
            sql.append("INNER JOIN Vehicle_Model vm ON v.model_id = vm.model_id ");
            sql.append("INNER JOIN Station s ON v.station_id = s.station_id ");
            sql.append("OUTER APPLY ( ");
            sql.append("    SELECT TOP 1 image_url ");
            sql.append("    FROM Vehicle_Model_Image ");
            sql.append("    WHERE model_id = vm.model_id ");
            sql.append("    ORDER BY CASE image_type WHEN 'FRONT' THEN 0 WHEN 'BACK' THEN 1 ELSE 2 END ");
            sql.append(") img ");
            sql.append("WHERE v.status = 'AVAILABLE' ");

            if (stationId != null && !stationId.trim().isEmpty()) {
                sql.append("AND v.station_id = ? ");
            }
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                sql.append("AND vm.category_id = ? ");
            }

            sql.append("GROUP BY vm.model_id, vm.name, vm.brand, vm.description, vm.price_per_day, vm.seat_count, ");
            sql.append("         s.station_id, s.name, s.address, img.image_url ");
            sql.append("ORDER BY s.name, vm.name");

            stmt = conn.prepareStatement(sql.toString());
            int index = 1;
            if (stationId != null && !stationId.trim().isEmpty()) {
                stmt.setString(index++, stationId);
            }
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                stmt.setString(index++, categoryId);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                VehicleSearchResult result = new VehicleSearchResult();
                result.setModelId(rs.getString("model_id"));
                result.setModelName(rs.getString("model_name"));
                result.setBrand(rs.getString("brand"));
                result.setDescription(rs.getString("description"));
                result.setPricePerDay(rs.getDouble("price_per_day"));
                result.setSeatCount(rs.getInt("seat_count"));
                result.setRemaining(rs.getInt("remaining"));
                result.setThumbnailImage(rs.getString("thumbnail_image"));
                result.setStationId(rs.getString("station_id"));
                result.setStationName(rs.getString("station_name"));
                result.setStationAddress(rs.getString("station_address"));
                results.add(result);
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return results;
    }

    @Override
    public List<VehicleSearchResult> searchAvailableVehicleModels(String stationId, String categoryId, Date startDate, Date endDate) {
        List<VehicleSearchResult> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql =
                "SELECT vm.model_id, vm.name AS model_name, vm.brand, vm.description, vm.price_per_day, vm.seat_count, " +
                "       COUNT(v.vehicle_id) AS remaining, " +
                "       img.image_url AS thumbnail_image " +
                "FROM Vehicle v " +
                "INNER JOIN Vehicle_Model vm ON v.model_id = vm.model_id " +
                "OUTER APPLY ( " +
                "    SELECT TOP 1 image_url " +
                "    FROM Vehicle_Model_Image " +
                "    WHERE model_id = vm.model_id " +
                "    ORDER BY CASE image_type WHEN 'FRONT' THEN 0 WHEN 'BACK' THEN 1 ELSE 2 END " +
                ") img " +
                "WHERE v.station_id = ? " +
                "  AND v.status = 'AVAILABLE' " +
                "  AND vm.category_id = ? " +
                "  AND NOT EXISTS ( " +
                "      SELECT 1 FROM Rental r " +
                "      WHERE r.vehicle_id = v.vehicle_id " +
                "        AND r.status IN ('BOOKED', 'RENTED') " +
                "        AND r.start_date < ? " +
                "        AND r.end_date > ? " +
                "  ) " +
                "GROUP BY vm.model_id, vm.name, vm.brand, vm.description, vm.price_per_day, vm.seat_count, img.image_url " +
                "ORDER BY vm.name";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, stationId);
            stmt.setString(2, categoryId);
            stmt.setDate(3, endDate);
            stmt.setDate(4, startDate);
            rs = stmt.executeQuery();

            while (rs.next()) {
                VehicleSearchResult result = new VehicleSearchResult();
                result.setModelId(rs.getString("model_id"));
                result.setModelName(rs.getString("model_name"));
                result.setBrand(rs.getString("brand"));
                result.setDescription(rs.getString("description"));
                result.setPricePerDay(rs.getDouble("price_per_day"));
                result.setSeatCount(rs.getInt("seat_count"));
                result.setRemaining(rs.getInt("remaining"));
                result.setThumbnailImage(rs.getString("thumbnail_image"));
                results.add(result);
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return results;
    }

    @Override
    public List<Vehicle> getAvailableVehiclesByModel(String stationId, String modelId, Date startDate, Date endDate) {
        List<Vehicle> vehicles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql =
                "SELECT vehicle_id, model_id, station_id, license_plate, color, battery_level, status, created_at " +
                "FROM Vehicle v " +
                "WHERE v.station_id = ? " +
                "  AND v.model_id = ? " +
                "  AND v.status = 'AVAILABLE' " +
                "  AND NOT EXISTS ( " +
                "      SELECT 1 FROM Rental r " +
                "      WHERE r.vehicle_id = v.vehicle_id " +
                "        AND r.status IN ('BOOKED', 'RENTED') " +
                "        AND r.start_date < ? " +
                "        AND r.end_date > ? " +
                "  ) " +
                "ORDER BY license_plate";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, stationId);
            stmt.setString(2, modelId);
            stmt.setDate(3, endDate);
            stmt.setDate(4, startDate);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(rs.getString("vehicle_id"));
                vehicle.setModelId(rs.getString("model_id"));
                vehicle.setStationId(rs.getString("station_id"));
                vehicle.setLicensePlate(rs.getString("license_plate"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setBatteryLevel(rs.getInt("battery_level"));
                vehicle.setStatus(VehicleStatus.fromValue(rs.getString("status")));
                vehicle.setCreatedAt(rs.getTimestamp("created_at"));
                vehicles.add(vehicle);
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return vehicles;
    }

    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error closing resources", ex);
        }
    }
}

