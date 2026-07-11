package services;

import daos.AdminStationDAO;
import java.util.List;
import java.util.UUID;
import models.Station;
import utils.JPAUtil;

public class AdminStationService {

    private final AdminStationDAO stationDAO = new AdminStationDAO();

    public List<Station> search(String keyword) {
        return stationDAO.search(keyword);
    }

    public List<Station> getAllStations() {
        return search(null);
    }

    public Station getStationById(String stationId) {
        return stationDAO.findById(stationId);
    }

    public void create(String name, String address, String contactNumber) {
        Station station = new Station(UUID.randomUUID().toString(), trim(name), trim(address), trim(contactNumber));
        validate(station);
        JPAUtil.executeInTransaction(em -> {
            if (stationDAO.nameExists(em, station.getName(), null)) {
                throw new IllegalArgumentException("Station name already exists.");
            }
            em.persist(station);
            return null;
        });
    }

    public void update(String stationId, String name, String address, String contactNumber) {
        validateRequired(stationId, "Station ID");
        Station updated = new Station(stationId.trim(), trim(name), trim(address), trim(contactNumber));
        validate(updated);
        JPAUtil.executeInTransaction(em -> {
            Station current = em.find(Station.class, updated.getStationId());
            if (current == null) {
                throw new IllegalArgumentException("Station not found.");
            }
            if (stationDAO.nameExists(em, updated.getName(), updated.getStationId())) {
                throw new IllegalArgumentException("Station name already exists.");
            }
            current.setName(updated.getName());
            current.setAddress(updated.getAddress());
            current.setContactNumber(updated.getContactNumber());
            return null;
        });
    }

    public void deleteStation(String stationId) {
        validateRequired(stationId, "Station ID");
        stationDAO.delete(stationId.trim());
    }

    private void validate(Station station) {
        validateRequired(station.getName(), "Station name");
        validateRequired(station.getAddress(), "Address");
        if (station.getContactNumber() != null && !station.getContactNumber().trim().isEmpty()
                && !station.getContactNumber().trim().matches("[0-9+\\- ]{8,20}")) {
            throw new IllegalArgumentException("Contact number is invalid.");
        }
    }

    private void validateRequired(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
