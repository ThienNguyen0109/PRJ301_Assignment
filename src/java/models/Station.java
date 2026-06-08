package models;

import java.io.Serializable;

/**
 * Entity class representing a Station
 */
public class Station implements Serializable {
    private static final long serialVersionUID = 1L;

    private String stationId;
    private String name;
    private String address;
    private String contactNumber;

    public Station() {
    }

    public Station(String name, String address, String contactNumber) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public Station(String stationId, String name, String address, String contactNumber) {
        this.stationId = stationId;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return "Station{" +
                "stationId='" + stationId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}

