/**
 * A manually implemented stack used to undo completion actions.
 */
public class UndoStack {
    public static class UndoAction {
        private final int taskId;
        private final boolean previousCompleted;

        public UndoAction(int taskId, boolean previousCompleted) {
            this.taskId = taskId;
            this.previousCompleted = previousCompleted;
        }

        public int getTaskId() { return taskId; }
        public boolean getPreviousCompleted() { return previousCompleted; }
    }

    private UndoAction[] data = new UndoAction[10];
    private int top = -1;

    public boolean isEmpty() { return top == -1; }

    public void push(UndoAction action) {
        if (top + 1 == data.length) {
            UndoAction[] larger = new UndoAction[data.length * 2];
            System.arraycopy(data, 0, larger, 0, data.length);
            data = larger;
        }
        data[++top] = action;
    }

    public UndoAction pop() {
        if (isEmpty()) return null;
        UndoAction action = data[top];
        data[top--] = null;
        return action;
    }

    public UndoAction peek() {
        return isEmpty() ? null : data[top];
    }
}
