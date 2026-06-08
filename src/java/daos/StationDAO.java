package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Station;
import utils.DBUtils;

/**
 * Data Access Object for Station
 */
public class StationDAO implements IStationDAO {
    private static final Logger LOGGER = Logger.getLogger(StationDAO.class.getName());

    @Override
    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT station_id, name, address, contact_number FROM Station ORDER BY name";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Station station = new Station();
                station.setStationId(rs.getString("station_id"));
                station.setName(rs.getString("name"));
                station.setAddress(rs.getString("address"));
                station.setContactNumber(rs.getString("contact_number"));
                stations.add(station);
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return stations;
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

