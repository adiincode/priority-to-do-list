import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Task {
    private final int id;
    private String title;
    private LocalDate deadline;
    private String priority;
    private boolean completed;

    public Task(int id, String title, LocalDate deadline, String priority, boolean completed) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.priority = priority;
        this.completed = completed;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public String getPriority() { return priority; }
    public boolean isCompleted() { return completed; }

    public void setTitle(String title) { this.title = title; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public long daysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), deadline);
    }

    public boolean isOverdue() {
        return !completed && deadline.isBefore(LocalDate.now());
    }

    public int priorityWeight() {
        if ("High".equalsIgnoreCase(priority)) return 3;
        if ("Medium".equalsIgnoreCase(priority)) return 2;
        return 1;
    }

    public double urgencyScore() {
        if (completed) return 0;
        long days = daysLeft();
        if (days < 0) return 1000 + priorityWeight() * 10 + Math.abs(days);
        return (10.0 * priorityWeight()) / (days + 1);
    }

    @Override
    public String toString() {
        return title + " (ID " + id + ")";
    }
}
