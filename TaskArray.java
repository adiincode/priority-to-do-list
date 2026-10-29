import java.util.Arrays;

/**
 * A small resizable array implemented manually for DSA demonstration.
 */
public class TaskArray {
    private Task[] data;
    private int size;

    public TaskArray() {
        data = new Task[10];
        size = 0;
    }

    public int size() { return size; }

    public Task get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Invalid task index");
        return data[index];
    }

    public void add(Task task) {
        ensureCapacity();
        data[size++] = task;
    }

    public Task findById(int id) {
        for (int i = 0; i < size; i++) {
            if (data[i].getId() == id) return data[i];
        }
        return null;
    }

    public int indexOfId(int id) {
        for (int i = 0; i < size; i++) {
            if (data[i].getId() == id) return i;
        }
        return -1;
    }

    public Task removeById(int id) {
        int index = indexOfId(id);
        if (index == -1) return null;
        Task removed = data[index];
        for (int i = index; i < size - 1; i++) data[i] = data[i + 1];
        data[--size] = null;
        return removed;
    }

    public Task[] toArray() {
        return Arrays.copyOf(data, size);
    }

    private void ensureCapacity() {
        if (size == data.length) data = Arrays.copyOf(data, data.length * 2);
    }
}
