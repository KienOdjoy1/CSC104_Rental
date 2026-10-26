import java.time.LocalDate;

public class MaintenanceRecord {
    private String recordId;
    private Van van;
    private Employee mechanic;
    private String note;
    private LocalDate startDate;
    private LocalDate endDate;

    public MaintenanceRecord(String recordId, Van van, Employee mechanic, String note, LocalDate startDate) {
        this.recordId = recordId;
        this.van = van;
        this.mechanic = mechanic;
        this.note = note;
        this.startDate = startDate;
    }

    public String getRecordId() {
        return recordId;
    }

    public Van getVan() {
        return van;
    }

    public Employee getMechanic() {
        return mechanic;
    }

    public String getNote() {
        return note;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isOpen() {
        return endDate == null;
    }

    public void finish(LocalDate date) {
        this.endDate = date;
    }

    public void restoreEndDate(LocalDate date) {
        this.endDate = date;
    }

    public String toFileString() {
        return recordId + "," + van.getPlateNumber() + "," + mechanic.getEmployeeId() + "," + note
                + "," + startDate + "," + (endDate == null ? "none" : endDate.toString());
    }

    @Override
    public String toString() {
        String status = isOpen() ? "In maintenance" : "Done " + endDate;
        return recordId + " | " + van.getPlateNumber() + " | " + note + " | by " + mechanic.getName()
                + " | from " + startDate + " | " + status;
    }

}
