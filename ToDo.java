import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;

class BaseTask {
    protected String description;
    protected LocalDateTime createdDate;
    protected int priority;

    public BaseTask(String description, int priority) {
        this.description = description;
        this.createdDate = LocalDateTime.now();
        this.priority = priority;
    }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public int getPriority() { return priority; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(int priority) { this.priority = priority; }
    public String toString() { return description; }

}

class Task extends BaseTask {
    private String deadline;
    private ArrayList<String> subtasks;

    public Task(String description, int priority) {
        super(description, priority);
        this.deadline = "";
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<String> getSubtasks() { return subtasks; }

    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void addSubtask(String subtask) {
        if (subtask != null && !subtask.trim().isEmpty()) {
            subtasks.add(subtask);
        }
    }
    public void removeSubtask(int index) {
        if (index >= 0 && index < subtasks.size()) {
            subtasks.remove(index);
        }
    }

    public String toString() {
        String result = description;
        if (!deadline.isEmpty()) {
            result += " ----------------- until:" + deadline;
        }
        return result;
    }

}

class TaskSorter {
    //сортировки с помощью сравнений через лямбда выражения
    public static void sortByPriority(ArrayList<BaseTask> tasks) {
        tasks.sort((t1, t2) -> Integer.compare(t1.getPriority(), t2.getPriority()));
    }

    public static void sortByDate(ArrayList<BaseTask> tasks) {
        tasks.sort((t1, t2) -> t1.getCreatedDate().compareTo(t2.getCreatedDate()));
    }
}

public class ToDo {
    private ArrayList<BaseTask> tasks = new ArrayList<>();
    private ArrayList<BaseTask> deletedTasks = new ArrayList<>();
    private Scanner scan = new Scanner(System.in);

    private static final int DEFAULT_FIRST_PRIORITY = 1;
    private static final int MAX_TASKS = 200;

    private class SubtaskArchive {
        int taskIndex;
        String subtask;

        SubtaskArchive(int taskIndex, String subtask) {
            this.taskIndex = taskIndex;
            this.subtask = subtask;
        }
    }

    private ArrayList<SubtaskArchive> deletedSubtasks = new ArrayList<>();

    private boolean isListEmpty() {
        if (tasks.isEmpty()) {
            System.out.println("first of all, your list is empty...");
            return true;
        }
        return false;
    }

    private boolean isValidIndex(int number, int max) {
        if (number < 1 || number > max) {
            System.out.println("enter a CORRECT number please (1 - " + max + ")");
            return false;
        }
        return true;
    }

    private boolean isValidIndex(int number) { return isValidIndex(number, tasks.size()); }

    private boolean isValidSubtaskIndex(int taskIndex, int subtaskNumber) {
        BaseTask baseTask = tasks.get(taskIndex);
        if (!(baseTask instanceof Task)) return false; //проверка можно ли у задачи брать подзадачи

        Task task = (Task) baseTask;
        return isValidIndex(subtaskNumber, task.getSubtasks().size());
    }

    private int readTaskNumber() {
        try {
            int number = scan.nextInt();
            scan.nextLine();
            return number;
        } catch (java.util.InputMismatchException e) {
            System.out.println("enter a NUMBER please");
            scan.nextLine();
            return -1;
        }
    }

    private String readInputWithValidation(String prompt, int maxLength) {
        System.out.println(prompt);
        String input = scan.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("cannot be empty");
            return null;
        }

        if (input.length() > maxLength) {
            System.out.println("too long! maximum " + maxLength + " characters");
            return null;
        }

        return input;
    }

    private Task getTaskAsTask(int index) {
        BaseTask baseTask = tasks.get(index);
        return (baseTask instanceof Task) ? (Task) baseTask : null; //преобразуем BaseTask в Task чтобы получить доступ к специальным методам
    }

    private void displayTasks(ArrayList<BaseTask> tasks, boolean showDates) {
        for (int i = 0; i < tasks.size(); i++) {
            BaseTask task = tasks.get(i);
            String taskInfo = (i + 1) + "\\\\ " + task + " [priority: " + task.getPriority();
            if (showDates) {
                taskInfo += ", date: " + task.getCreatedDate().toLocalDate();
            }
            taskInfo += "]";
            System.out.println(taskInfo);

            if (task instanceof Task) {
                Task concreteTask = (Task) task;
                ArrayList<String> subtasks = concreteTask.getSubtasks();
                if (!subtasks.isEmpty()) {
                    for (int j = 0; j < subtasks.size(); j++) {
                        System.out.println("   " + (i + 1) + "." + (j + 1) + " " + subtasks.get(j));
                    }
                }
            }
        }
    }

    private void displaySubtasks(Task task, int taskNumber) {
        ArrayList<String> subtasks = task.getSubtasks();
        for (int j = 0; j < subtasks.size(); j++) {
            System.out.println("   " + taskNumber + "." + (j + 1) + " " + subtasks.get(j));
        }
    }

    private void updateTaskPriorities() {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPriority(i + DEFAULT_FIRST_PRIORITY);
        }
    }

    //задача мэйна - запуск приложения
    public static void main(String[] args) {
        ToDo todoApp = new ToDo();
        todoApp.run();
    }

    public void run() {
        while(true) {
            showMenu();

            try {
                int option = scan.nextInt();
                scan.nextLine();

                switch(option) {
                    case 1:
                        createTask();
                        break;
                    case 2:
                        editTask();
                        break;
                    case 3:
                        deleteTask();
                        break;
                    case 4:
                        addDeadline();
                        break;
                    case 5:
                        addSubtask();
                        break;
                    case 6:
                        deleteSubtask();
                        break;
                    case 7:
                        changePriority();
                        break;
                    case 8:
                        sortByDates();
                        break;
                    case 9:
                        sortByPriority();
                        break;
                    case 10:
                        searchTasks();
                        break;
                    case 11:
                        showTodo();
                        break;
                    case 12:
                        showDeletedTasks();
                        break;
                    case 13:
                        restoreTask();
                        break;
                    case 14:
                        restoreSubtask();
                        break;
                    case 15:
                        System.out.println("goodbye!");
                        scan.close();
                        return;
                    default:
                        System.out.println("invalid option! choose 1-15");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("enter a NUMBER from menu!");
                scan.nextLine();
            } catch (Exception e) {
                System.out.println("unexpected error: " + e.getMessage());
                scan.nextLine();
            }
        }
    }

    public void showMenu() {
        System.out.println("\n⋆✴\uFE0E˚｡⋆ to-do list ⋆✴\uFE0E˚｡⋆");
        System.out.println(" ");
        System.out.println("1\\\\ add new task");
        System.out.println("2\\\\ edit any task");
        System.out.println("3\\\\ delete any task");
        System.out.println("4\\\\ add deadline");
        System.out.println("5\\\\ add subtask");
        System.out.println("6\\\\ delete subtask");
        System.out.println("7\\\\ change priority");
        System.out.println("8\\\\ sort tasks by dates");
        System.out.println("9\\\\ sort tasks by priority");
        System.out.println("10\\\\ search for an exact task");
        System.out.println("11\\\\ show all tasks");
        System.out.println("12\\\\ show deleted tasks");
        System.out.println("13\\\\ restore deleted task");
        System.out.println("14\\\\ restore deleted subtask");
        System.out.println("15\\\\ exit");
        System.out.println(" ");
        System.out.println("what do you want to do?");
    }

    public void showTodo() {
        if (isListEmpty()) return;
        displayTasks(tasks, false);
    }

    public void createTask() {
        if (tasks.size() >= MAX_TASKS) {
            System.out.println("cannot add more tasks! maximum limit reached (" + MAX_TASKS + ")");
            return;
        }

        String description = readInputWithValidation("enter the task:", 500);
        if (description == null) return;

        //текущее кол-во задач + 1 == автоматически в конец списка по приоритету
        int newPriority = tasks.size() + DEFAULT_FIRST_PRIORITY;
        Task newTask = new Task(description, newPriority);
        tasks.add(newTask);
        System.out.println("- successfully added! now go making it done");
        showTodo();
    }

    public void editTask() {
        if (isListEmpty()) return;

        System.out.println("enter the number of task you want to edit:");
        int number = readTaskNumber();
        if (number == -1 || !isValidIndex(number)) return; //-1 это если readTaskNumber() говорит об ошибке ввода

        BaseTask task = tasks.get(number - 1);
        System.out.println("current task: " + task);

        String editedDescription = readInputWithValidation("enter edited task:", 500);
        if (editedDescription == null) return;

        task.setDescription(editedDescription);
        System.out.println("- edited!");
        showTodo();
    }

    public void deleteTask() {
        if (isListEmpty()) return;

        System.out.println("enter the number of task you want to delete:");
        int number = readTaskNumber();
        if (number == -1 || !isValidIndex(number)) return;

        BaseTask removedTask = tasks.remove(number - 1);
        deletedTasks.add(removedTask);
        updateTaskPriorities();
        System.out.println("- task removed!");
        showTodo();
    }

    public void restoreTask() {
        if (deletedTasks.isEmpty()) {
            System.out.println("archive is empty, nothing to restore");
            return;
        }

        System.out.println("enter the number of task to restore:");
        int number = readTaskNumber();
        if (number == -1 || !isValidIndex(number, deletedTasks.size())) return;

        if (tasks.size() >= MAX_TASKS) {
            System.out.println("cannot restore task! maximum limit reached (" + MAX_TASKS + ")");
            return;
        }

        BaseTask restoredTask = deletedTasks.remove(number - 1);
        tasks.add(restoredTask);
        restoredTask.setPriority(tasks.size() + DEFAULT_FIRST_PRIORITY);
        System.out.println("- task restored!");
        showTodo();
    }

    public void showDeletedTasks() {
        if (deletedTasks.isEmpty()) {
            System.out.println("archive is empty, nothing was deleted yet");
            return;
        }

        System.out.println("recently deleted tasks:");
        displayTasks(deletedTasks, false);
    }

    public void addSubtask() {
        if (isListEmpty()) return;

        System.out.println("enter the number of task to add subtask:");
        int number = readTaskNumber();
        if (number == -1 || !isValidIndex(number)) return;

        Task task = getTaskAsTask(number - 1);
        if (task == null) return;

        System.out.println("current task: " + task);
        String subtask = readInputWithValidation("enter subtask:", 200);
        if (subtask == null) return;

        task.addSubtask(subtask);
        System.out.println("- subtask added!");
        showTodo();
    }

    public void deleteSubtask() {
        if (isListEmpty()) return;

        System.out.println("enter the number of task:");
        int taskNumber = readTaskNumber();
        if (taskNumber == -1 || !isValidIndex(taskNumber)) return;

        Task task = getTaskAsTask(taskNumber - 1);
        if (task == null) return;

        ArrayList<String> subtasks = task.getSubtasks();
        if (subtasks.isEmpty()) {
            System.out.println("this task has no subtasks");
            return;
        }

        System.out.println("current subtasks:");
        displaySubtasks(task, taskNumber);

        System.out.println("enter the number of subtask to delete (format: task.subtask):");
        String input = scan.nextLine().trim();
        try {
            String[] parts = input.split("\\.");
            if (parts.length != 2) {
                System.out.println("enter correct format (like: 1.2)");
                return;
            }

            int subtaskNumber = Integer.parseInt(parts[1]);
            if (isValidSubtaskIndex(taskNumber - 1, subtaskNumber)) {
                String deletedSubtask = subtasks.get(subtaskNumber - 1);
                deletedSubtasks.add(new SubtaskArchive(taskNumber - 1, deletedSubtask));
                task.removeSubtask(subtaskNumber - 1);
                System.out.println("- subtask deleted!");
                showTodo();
            }
        } catch (NumberFormatException e) {
            System.out.println("enter correct number format (like: 1.2)");
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    public void restoreSubtask() {
        if (deletedSubtasks.isEmpty()) {
            System.out.println("no deleted subtasks to restore");
            return;
        }

        System.out.println("recently deleted subtasks:");
        for (int i = 0; i < deletedSubtasks.size(); i++) {
            SubtaskArchive archive = deletedSubtasks.get(i);
            if (archive.taskIndex >= 0 && archive.taskIndex < tasks.size()) {
                BaseTask baseTask = tasks.get(archive.taskIndex);
                if (baseTask instanceof Task) {
                    Task parentTask = (Task) baseTask;
                    System.out.println((i + 1) + ". " + archive.subtask + " (from task: " + parentTask + ")");
                }
            } else {
                System.out.println((i + 1) + ". " + archive.subtask + " (original task no longer exists)");
            }
        }

        System.out.println("enter the number of subtask to restore:");
        int number = readTaskNumber();
        if (number == -1 || !isValidIndex(number, deletedSubtasks.size())) return;

        SubtaskArchive archive = deletedSubtasks.remove(number - 1);

        if (archive.taskIndex < 0 || archive.taskIndex >= tasks.size()) {
            System.out.println("cannot restore subtask - original task no longer exists");
            return;
        }

        Task parentTask = getTaskAsTask(archive.taskIndex);
        if (parentTask != null) {
            parentTask.addSubtask(archive.subtask);
            System.out.println("- subtask restored to task: " + parentTask);
            showTodo();
        }
    }

    public void sortByDates() {
        if (isListEmpty()) return;

        ArrayList<BaseTask> tasksToSort = new ArrayList<>(tasks);
        TaskSorter.sortByDate(tasksToSort);

        System.out.println("- tasks sorted by dates!");
        displayTasks(tasksToSort, true);
    }

    public void sortByPriority() {
        if (isListEmpty()) return;

        ArrayList<BaseTask> tasksToSort = new ArrayList<>(tasks);
        TaskSorter.sortByPriority(tasksToSort);

        System.out.println("- tasks sorted by priority!");
        displayTasks(tasksToSort, false);
    }

    public void changePriority() {
        if (isListEmpty()) return;

        System.out.println("enter the number of task you want to change priority for:");
        int number = readTaskNumber();
        if (number == -1 || !isValidIndex(number)) return;

        BaseTask task = tasks.get(number - 1);
        System.out.println("current priority: " + task.getPriority());
        System.out.println("enter new priority (1 - " + tasks.size() + ", where 1 is the highest):");

        int newPriority = readTaskNumber();
        if (newPriority == -1) return;

        if (newPriority < DEFAULT_FIRST_PRIORITY || newPriority > tasks.size()) {
            System.out.println("priority must be between " + DEFAULT_FIRST_PRIORITY + " and " + tasks.size());
            return;
        }

        ArrayList<BaseTask> tempTasks = new ArrayList<>(tasks);

        //удаляем задачу с текущей позиции
        BaseTask movedTask = tempTasks.remove(number - 1);
        //и вставляем на новую
        tempTasks.add(newPriority - DEFAULT_FIRST_PRIORITY, movedTask);

        //пересчитываем приоритеты для всех задач
        for (int i = 0; i < tempTasks.size(); i++) {
            tempTasks.get(i).setPriority(i + DEFAULT_FIRST_PRIORITY);
        }

        //обновляем основной список
        tasks = tempTasks;

        System.out.println("- priority changed!");
        showTodo();
    }

    public void addDeadline() {
        if (isListEmpty()) return;

        System.out.println("enter number of a task you need deadline to:");
        int number = readTaskNumber();
        if (number == -1 || !isValidIndex(number)) return;

        Task task = getTaskAsTask(number - 1);
        if (task == null) return;

        String deadlineText = readInputWithValidation("add deadline:", 100);
        if (deadlineText == null) return;

        task.setDeadline(deadlineText);
        System.out.println("- deadline added!");
        showTodo();
    }

    public void searchTasks() {
        if (isListEmpty()) return;

        String searchText = readInputWithValidation("add text for search:", 50);
        if (searchText == null) return;

        System.out.println("found tasks and subtasks:");
        boolean found = false;

        for (int i = 0; i < tasks.size(); i++) {
            BaseTask task = tasks.get(i);
            String taskText = task.toString().toLowerCase();

            //поиск по основной задаче
            if (taskText.contains(searchText)) {
                System.out.println((i + 1) + "\\\\ " + task + " [priority: " + task.getPriority() + "]");
                found = true;
            }

            //поиск по подзадачам
            if (task instanceof Task) {
                Task concreteTask = (Task) task;
                ArrayList<String> subtasks = concreteTask.getSubtasks();
                for (int j = 0; j < subtasks.size(); j++) {
                    if (subtasks.get(j).toLowerCase().contains(searchText)) {
                        System.out.println((i + 1) + "\\\\ " + task + " [priority: " + task.getPriority() + "]");
                        System.out.println("   " + (i + 1) + "." + (j + 1) + " " + subtasks.get(j) + " ← found in subtask");
                        found = true;
                    }
                }
            }
        }

        if (!found) {
            System.out.println("sorry, nothing containing \"" + searchText + "\" is found");
        }
    }
}
