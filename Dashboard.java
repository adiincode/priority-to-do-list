import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Dashboard extends JFrame {
    private final TaskManager manager = new TaskManager();
    private final DefaultTableModel tableModel = new DefaultTableModel(
        new Object[]{"ID", "Task", "Deadline", "Priority", "Status", "Days Left", "Urgency"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private final JLabel summary = new JLabel();
    private final JComboBox<String> sortBox = new JComboBox<>(
        new String[]{"Recommended urgency", "Deadline (soonest)", "Priority (highest)"});

    public Dashboard() {
        setTitle("Deadline Rescue System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 620);
        setMinimumSize(new Dimension(820, 500));
        setLocationRelativeTo(null);
        buildUi();
        refreshTable();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Deadline Rescue");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        JLabel subtitle = new JLabel("Know what to do next. Rescue your deadlines.");
        subtitle.setForeground(new Color(95, 105, 120));
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        header.add(titlePanel, BorderLayout.WEST);
        summary.setFont(new Font("SansSerif", Font.BOLD, 13));
        summary.setForeground(new Color(38, 90, 140));
        header.add(summary, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        root.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttons.setOpaque(false);

        JButton add = button("＋ Add Task");
        JButton complete = button("✓ Complete");
        JButton undo = button("↶ Undo Completion");
        JButton delete = button("Delete");
        JButton refresh = button("Refresh");

        add.addActionListener(e -> addTaskDialog());
        complete.addActionListener(e -> completeSelected());
        undo.addActionListener(e -> undoCompletion());
        delete.addActionListener(e -> deleteSelected());
        refresh.addActionListener(e -> refreshTable());

        buttons.add(add);
        buttons.add(complete);
        buttons.add(undo);
        buttons.add(delete);
        buttons.add(refresh);

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        sortPanel.setOpaque(false);
        sortPanel.add(new JLabel("Sort by:"));
        sortPanel.add(sortBox);
        sortBox.addActionListener(e -> refreshTable());

        bottom.add(buttons, BorderLayout.WEST);
        bottom.add(sortPanel, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);
    }

    private JButton button(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setMargin(new Insets(9, 12, 9, 12));
        return b;
    }

    private void addTaskDialog() {
        JTextField titleField = new JTextField(22);
        JTextField dateField = new JTextField(LocalDate.now().plusDays(1).toString(), 12);
        JComboBox<String> priorityBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 10));
        panel.add(new JLabel("Task title:"));
        panel.add(titleField);
        panel.add(new JLabel("Deadline (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Priority:"));
        panel.add(priorityBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add a task",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Task title cannot be empty.");
            return;
        }
        try {
            LocalDate deadline = LocalDate.parse(dateField.getText().trim());
            manager.addTask(title, deadline, (String) priorityBox.getSelectedItem());
            refreshTable();
        } catch (DateTimeParseException ex) {
            showError("Enter a valid date in YYYY-MM-DD format, for example 2026-11-05.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private int selectedTaskId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Select a task from the table first.");
            return -1;
        }
        return (Integer) tableModel.getValueAt(table.convertRowIndexToModel(row), 0);
    }

    private void completeSelected() {
        int id = selectedTaskId();
        if (id < 0) return;
        try {
            if (!manager.markCompleted(id)) {
                showError("That task is already completed or no longer exists.");
            }
            refreshTable();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void undoCompletion() {
        try {
            if (!manager.undoLastCompletion()) {
                JOptionPane.showMessageDialog(this, "Nothing to undo yet.", "Undo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            refreshTable();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void deleteSelected() {
        int id = selectedTaskId();
        if (id < 0) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete the selected task? This cannot be undone.", "Confirm deletion",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            manager.deleteTask(id);
            refreshTable();
        } catch (RuntimeException ex) { showError(ex.getMessage()); }
    }

    private void refreshTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        Task[] tasks = manager.getTasks();
        String selected = (String) sortBox.getSelectedItem();
        if ("Deadline (soonest)".equals(selected)) tasks = DeadlineAnalyzer.sortByDeadline(tasks);
        else if ("Priority (highest)".equals(selected)) tasks = DeadlineAnalyzer.sortByPriority(tasks);
        else tasks = DeadlineAnalyzer.sortByUrgency(tasks);

        int pending = 0, overdue = 0, completed = 0;
        for (Task task : tasks) {
            if (task.isCompleted()) completed++;
            else {
                pending++;
                if (task.isOverdue()) overdue++;
            }
            String status = task.isCompleted() ? "Completed" :
                task.isOverdue() ? "OVERDUE" :
                task.getDeadline().equals(LocalDate.now()) ? "Due today" : "Pending";
            String daysLeft = task.isCompleted() ? "—" :
                task.daysLeft() < 0 ? Math.abs(task.daysLeft()) + " day(s) late" :
                task.daysLeft() == 0 ? "Today" : task.daysLeft() + " day(s)";
            tableModel.addRow(new Object[]{task.getId(), task.getTitle(), task.getDeadline(),
                task.getPriority(), status, daysLeft,
                task.isCompleted() ? "—" : String.format("%.1f", task.urgencyScore())});
        }
        summary.setText("Pending: " + pending + "  •  Overdue: " + overdue + "  •  Completed: " + completed);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Deadline Rescue",
            JOptionPane.WARNING_MESSAGE);
    }
}
