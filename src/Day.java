import java.util.ArrayList;

public class Day {
    private ArrayList<TaskModel> tasks;
    private final int dayNumber;
    private final int dayOfTheWeek;
    private final int weekNumb;

    public Day(int dayNumber) {
        this.dayNumber = dayNumber;
        dayOfTheWeek = dayNumber%7;
        weekNumb = (dayNumber-1)/7;
        tasks = new ArrayList<>();
    }

    public Day(ArrayList<TaskModel> tasks, int dayNumber) {
        this.tasks = tasks;
        this.dayNumber = dayNumber;
        dayOfTheWeek = dayNumber%7;
        weekNumb = (dayNumber-1)/7;
    }

    public ArrayList<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskModel> tasks) {
        this.tasks = tasks;
    }

    public void addTask(TaskModel task) {
        tasks.add(task);
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public int getWeekNumb() {
        return weekNumb;
    }
}
