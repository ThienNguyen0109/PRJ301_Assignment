package models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Entity class representing a Station
 */
@Entity
@Table(name = "Station")
public class Station implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "station_id", columnDefinition = "uniqueidentifier")
    private String stationId;
    @Column(name = "name", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String name;
    @Lob
    @Column(name = "address", columnDefinition = "NVARCHAR(MAX)")
    private String address;
    @Column(name = "contact_number", length = 20)
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

