package dto;

import java.util.List;

public class StaffDashboardDTO {
    private final long waitingForPickup;
    private final long currentlyRented;
    private final long waitingForReturn;
    private final long underMaintenance;
    private final List<StaffActivityDTO> recentActivities;

    public StaffDashboardDTO(long waitingForPickup, long currentlyRented, long waitingForReturn,
            long underMaintenance, List<StaffActivityDTO> recentActivities) {
        this.waitingForPickup = waitingForPickup;
        this.currentlyRented = currentlyRented;
        this.waitingForReturn = waitingForReturn;
        this.underMaintenance = underMaintenance;
        this.recentActivities = recentActivities;
    }

    public long getWaitingForPickup() { return waitingForPickup; }
    public long getCurrentlyRented() { return currentlyRented; }
    public long getWaitingForReturn() { return waitingForReturn; }
    public long getUnderMaintenance() { return underMaintenance; }
    public List<StaffActivityDTO> getRecentActivities() { return recentActivities; }
}
