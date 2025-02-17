import java.time.LocalDate;

public class Reminder {
    private Task task;
    private ReminderType reminderType;  // Χρησιμοποιούμε το σωστό όνομα
    private LocalDate reminderDate;

    // Enum για τον τύπο υπενθύμισης
    public enum ReminderType {
        ONE_DAY_BEFORE, ONE_WEEK_BEFORE, ONE_MONTH_BEFORE, SPECIFIC_DATE
    }

    // Constructor
    public Reminder(Task task, ReminderType reminderType, LocalDate reminderDate) {
        this.task = task;
        this.reminderType = reminderType;
        this.reminderDate = reminderDate;
    }

    // Getters και Setters
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public ReminderType getReminderType() {  // Εδώ επιστρέφουμε το reminderType
        return reminderType;
    }

    public void setReminderType(ReminderType reminderType) {  // Ενημερώνουμε το reminderType
        this.reminderType = reminderType;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderInfo() {
        return "Task: " + task.getTitle() + " | Reminder Type: " + reminderType + " | Date: " + reminderDate;
    }

    // Μέθοδος που ελέγχει αν η υπενθύμιση είναι έγκυρη
    public boolean isReminderValid() {
        LocalDate taskDeadline = task.getDeadline();

        switch (reminderType) {  // Χρησιμοποιούμε το σωστό όνομα "reminderType"
            case ONE_DAY_BEFORE:
                return !taskDeadline.minusDays(1).isBefore(LocalDate.now());
            case ONE_WEEK_BEFORE:
                return !taskDeadline.minusWeeks(1).isBefore(LocalDate.now());
            case ONE_MONTH_BEFORE:
                return !taskDeadline.minusMonths(1).isBefore(LocalDate.now());
            case SPECIFIC_DATE:
                return !reminderDate.isAfter(taskDeadline);
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "task=" + task +
                ", reminderType=" + reminderType +  // Χρησιμοποιούμε το σωστό όνομα "reminderType"
                ", reminderDate=" + reminderDate +
                '}';
    }
}
