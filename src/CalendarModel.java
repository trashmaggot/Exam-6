import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CalendarModel {
    private ArrayList<Day> days;

    public CalendarModel() {
        update();
    }

    public ArrayList<Day> getDays() {
        return days;
    }

    private void update() {
        days = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            days.add(new Day(i+1));
        }

        TaskModel[] taskModels = FileService.readTasksFile();

        Arrays.stream(taskModels).forEach(taskModel -> {
            int dayNumb = taskModel.getDate();
            days.forEach(day -> {
                if(day.getDayNumber() == dayNumb) {
                    day.addTask(taskModel);
                }
            });
        });
    }

    public void removeTask(String taskName) {
        ArrayList<TaskModel> tasks = new ArrayList<>();
        try {
            Collections.addAll(tasks, FileService.readTasksFile());
        } catch (Exception ignore){}

        tasks.removeIf(taskModel -> taskModel.getHeader().equals(taskName));

        FileService.writeTasksFile(tasks.toArray(TaskModel[]::new));
        update();
    }

    public boolean addTask(TaskModel task) {
        if (task.getDate() < 1 || task.getDate() > 30) return false;
        ArrayList<TaskModel> tasks = new ArrayList<>();
        try {
            Collections.addAll(tasks, FileService.readTasksFile());
        } catch (Exception ignore){}

        for (TaskModel t : tasks) {
            if (t.getHeader().equals(task.getHeader())) return false;
        }

        tasks.add(task);
        FileService.writeTasksFile(tasks.toArray(TaskModel[]::new));
        update();
        return true;
    }
}
