import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileService {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static TaskModel[] readTasksFile(){
        String json = "";
        try{
            json = Files.readString(Paths.get("data/tasks.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return GSON.fromJson(json, TaskModel[].class);
    }

    public static void writeTasksFile(TaskModel[] tracks){
        String json = GSON.toJson(tracks);
        try{
            byte[] arr = json.getBytes();
            Files.write(Paths.get("data/tasks.json"), arr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}