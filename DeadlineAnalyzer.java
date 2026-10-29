import java.util.Arrays;
import java.util.Comparator;

public class DeadlineAnalyzer {
    public static Task[] sortByDeadline(Task[] tasks) {
        Task[] copy = Arrays.copyOf(tasks, tasks.length);
        Arrays.sort(copy, Comparator
            .comparing(Task::isCompleted)
            .thenComparing(Task::getDeadline)
            .thenComparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER));
        return copy;
    }

    public static Task[] sortByPriority(Task[] tasks) {
        Task[] copy = Arrays.copyOf(tasks, tasks.length);
        Arrays.sort(copy, (a, b) -> {
            if (a.isCompleted() != b.isCompleted()) return a.isCompleted() ? 1 : -1;
            int priorityCompare = Integer.compare(b.priorityWeight(), a.priorityWeight());
            if (priorityCompare != 0) return priorityCompare;
            return a.getDeadline().compareTo(b.getDeadline());
        });
        return copy;
    }

    public static Task[] sortByUrgency(Task[] tasks) {
        Task[] copy = Arrays.copyOf(tasks, tasks.length);
        Arrays.sort(copy, (a, b) -> {
            if (a.isCompleted() != b.isCompleted()) return a.isCompleted() ? 1 : -1;
            return Double.compare(b.urgencyScore(), a.urgencyScore());
        });
        return copy;
    }
}
