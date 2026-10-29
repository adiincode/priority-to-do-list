import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Base64;

public class TaskManager {
    private final TaskArray tasks = new TaskArray();
    private final UndoStack undoStack = new UndoStack();
    private int nextId = 1;
    private final Path dataFile;

    public TaskManager() {
        this(Paths.get("data", "tasks.txt"));
    }

    public TaskManager(Path dataFile) {
        this.dataFile = dataFile;
        load();
    }

    public Task[] getTasks() { return tasks.toArray(); }

    public Task addTask(String title, LocalDate deadline, String priority) {
        Task task = new Task(nextId++, title.trim(), deadline, priority, false);
        tasks.add(task);
        save();
        return task;
    }

    public Task findById(int id) { return tasks.findById(id); }

    public boolean markCompleted(int id) {
        Task task = tasks.findById(id);
        if (task == null || task.isCompleted()) return false;
        undoStack.push(new UndoStack.UndoAction(id, task.isCompleted()));
        task.setCompleted(true);
        save();
        return true;
    }

    public boolean undoLastCompletion() {
        UndoStack.UndoAction action = undoStack.pop();
        if (action == null) return false;
        Task task = tasks.findById(action.getTaskId());
        if (task == null) return false;
        task.setCompleted(action.getPreviousCompleted());
        save();
        return true;
    }

    public boolean deleteTask(int id) {
        Task removed = tasks.removeById(id);
        if (removed == null) return false;
        save();
        return true;
    }

    public boolean isUndoAvailable() { return !undoStack.isEmpty(); }

    public void save() {
        try {
            Path parent = dataFile.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (BufferedWriter writer = Files.newBufferedWriter(dataFile, StandardCharsets.UTF_8)) {
                for (Task task : tasks.toArray()) {
                    String encodedTitle = Base64.getEncoder().encodeToString(
                        task.getTitle().getBytes(StandardCharsets.UTF_8));
                    writer.write(task.getId() + "|" + encodedTitle + "|" +
                        task.getDeadline() + "|" + task.getPriority() + "|" + task.isCompleted());
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not save tasks: " + ex.getMessage(), ex);
        }
    }

    private void load() {
        if (!Files.exists(dataFile)) return;
        try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] p = line.split("\\|", -1);
                    if (p.length != 5) continue;
                    int id = Integer.parseInt(p[0]);
                    String title = new String(Base64.getDecoder().decode(p[1]), StandardCharsets.UTF_8);
                    LocalDate deadline = LocalDate.parse(p[2]);
                    String priority = p[3];
                    boolean completed = Boolean.parseBoolean(p[4]);
                    tasks.add(new Task(id, title, deadline, priority, completed));
                    nextId = Math.max(nextId, id + 1);
                } catch (RuntimeException ignored) {
                    // Skip malformed records rather than preventing the app from opening.
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load tasks: " + ex.getMessage(), ex);
        }
    }
}
