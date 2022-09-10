public class TaskModel {
    private String header;
    private String description;
    private int date;
    private String status;

    public void setHeader(String header) {
        this.header = header;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

    public int getDate() {
        return date;
    }

    public TaskModel() {}

    public TaskModel(String header, String description, int date, String status) {
        this.header = header;
        this.description = description;
        this.date = date;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
