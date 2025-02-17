import java.time.LocalDate;

public class Task {
    private String title;
    private String description;
    private String category;
    private String priority;
    private LocalDate deadline;
    private Status status;

    // Enum για την κατάσταση της εργασίας
    public enum Status {
        OPEN, IN_PROGRESS, POSTPONED, COMPLETED, DELAYED
    }

    // Constructor
    public Task(String title, String description, String category, String priority, LocalDate deadline, Status status) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
        this.status = (status != null) ? status : Status.OPEN; // Default κατάσταση
    }

    // Getters και Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", priority='" + priority + '\'' +
                ", deadline=" + deadline +
                ", status=" + status +
                '}';
    }
}
