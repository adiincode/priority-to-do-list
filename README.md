# Deadline Rescue System

A Java Swing mini-project that demonstrates core Data Structures and Algorithms through a practical task manager.

## Web frontend
Open `index.html` in any browser. It runs the same logic (custom array, custom stack, merge sort) in JavaScript and includes a live data-structure lab. A presentation is included: `Deadline_Rescue_Presentation.pptx`.

## Features
- Add tasks with a title, deadline, and priority.
- Display pending, completed, overdue, and due-today tasks.
- Sort by urgency, deadline, or priority.
- Mark tasks completed and undo the latest completion using a custom stack.
- Search tasks by ID in the task manager implementation.
- Persist tasks between launches in `data/tasks.txt`.
- Custom resizable array and custom stack (not Java's built-in Stack).

## Requirements
- JDK 8 or later (JDK 11+ recommended)
- No external libraries required

## Run on Windows
1. Extract the ZIP.
2. Open Command Prompt in the `DeadlineRescue` folder.
3. Compile:

   ```bat
   javac -encoding UTF-8 -d out src\*.java
   ```

4. Run:

   ```bat
   java -cp out Main
   ```

## Run on macOS/Linux
From the `DeadlineRescue` folder:

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

## DSA concepts demonstrated
- **Custom dynamic array:** insertion, traversal, linear search, deletion by shifting, capacity growth.
- **Custom stack:** push, pop, peek, dynamic capacity growth.
- **Sorting:** deadline order, priority order, and urgency-score order.
- **Date analysis:** overdue detection and days remaining.
- **Complexity discussion:**
  - Array lookup by index: `O(1)`
  - Linear search by ID: `O(n)`
  - Add to array when capacity is available: `O(1)` amortized
  - Remove by ID: `O(n)` due to shifting
  - Sorting tasks: `O(n log n)` using Java's sorting implementation in `DeadlineAnalyzer`
  - Stack push/pop: `O(1)` amortized

## How the urgency score works
- High priority has weight 3, Medium 2, Low 1.
- Pending overdue tasks receive a high score so they rise to the top.
- Other pending tasks use `10 * priorityWeight / (daysLeft + 1)`.
- Completed tasks are always sorted after pending tasks.

This is a simple heuristic for demonstration, not a universal task-planning formula.

## Project structure

```text
DeadlineRescue/
├── src/
│   ├── Main.java
│   ├── Dashboard.java
│   ├── Task.java
│   ├── TaskArray.java
│   ├── UndoStack.java
│   ├── DeadlineAnalyzer.java
│   └── TaskManager.java
├── data/               # Created automatically when tasks are saved
└── README.md
```

## Notes
- Date input must use `YYYY-MM-DD`, e.g. `2026-11-05`.
- The undo stack is kept in memory for the current session. Tasks themselves are saved to disk.
- Deleting a task is intentionally not undoable in this starter version.
