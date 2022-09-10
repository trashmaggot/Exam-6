import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import server.BasicServer;
import server.ContentType;
import server.ResponseCodes;
import server.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Objects;

public class MyServer extends BasicServer {

    private final static Configuration freemarker = initFreeMarker();
    private CalendarModel calendar;

    protected MyServer(String host, int port) throws IOException {
        super(host, port);
        registerGet("/", this::freemarkerCalendarHandler);
        registerPost("/", this::freemarkerCalendarPostHandler);
        registerGet("/tasks", this::freemarkerDayHandler);
        registerPost("/tasks", this::freemarkerDayPostHandler);
        registerGet("/add", this::freemarkerAddGetHandler);
        registerPost("/add", this::freemarkerAddPostHandler);
    }

    private void freemarkerAddPostHandler(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");

        if (parsed.get("header")!=null && !parsed.get("header").isBlank()) {
            String header = parsed.get("header");
            String status = parsed.get("status");
            String description = parsed.get("description");
            int date = Integer.parseInt(parsed.get("date"));
            TaskModel task = new TaskModel(header, description, date, status);
            if (!calendar.addTask(task)) {
                redirect303(exchange, "/add");
            }
            Day day = calendar.getDays().stream()
                    .filter(d -> d.getDayNumber() == date)
                    .findAny().get();
            String path ="/tasks?dayNumber=" + date;
            redirect303(exchange, path);
        }
    }

    private void freemarkerAddGetHandler(HttpExchange exchange) {
        renderTemplate(exchange, "add.html", null);
    }

    private void freemarkerDayPostHandler(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");

        if (parsed.get("header")!=null && !parsed.get("header").isBlank()) {
            String header = parsed.get("header");
            String status = parsed.get("status");
            String description = parsed.get("description");
            int date = Integer.parseInt(parsed.get("date"));
            TaskModel task = new TaskModel(header, description, date, status);
            calendar.addTask(task);
            Day day = calendar.getDays().stream()
                    .filter(d -> d.getDayNumber() == date)
                    .findAny().get();
            renderTemplate(exchange, "DayList.html", day);
        }

        if (parsed.get("delete")!=null && !parsed.get("delete").isBlank()) {
            int dayNumb = Integer.parseInt(parsed.get("date"));
            calendar.removeTask(parsed.get("delete"));
            String path = "/tasks?dayNumber=" + dayNumb;
            redirect303(exchange, path);
        }

        redirect303(exchange, "/tasks");

    }

    private void freemarkerDayHandler(HttpExchange exchange) {
        String queryParams = getQueryParams(exchange);
        Map<String, String> params = Utils.parseUrlEncoded(queryParams, "&");
        if (params.get("dayNumber") == null) renderTemplate(exchange, "DayList.html", calendar.getDays().get(0));
        else {
            int dayNumber = Integer.parseInt(params.get("dayNumber"));
            Day day = calendar.getDays().stream()
                    .filter(d -> d.getDayNumber() == dayNumber).findAny().get();
            renderTemplate(exchange, "DayList.html", day);
        }
    }

    private void freemarkerCalendarHandler(HttpExchange exchange) {
        calendar = new CalendarModel();
        renderTemplate(exchange, "calendar.html", calendar);
    }

    private void freemarkerCalendarPostHandler(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");

        int dayNumber = Integer.parseInt(parsed.get("go"));

        String path ="/tasks?dayNumber=" + dayNumber;
        redirect303(exchange, path);
    }

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("data"));

            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getQueryParams(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        return Objects.nonNull(query) ? query : "";
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            Template temp = freemarker.getTemplate(templateFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                temp.process(dataModel, writer);
                writer.flush();
                var data = stream.toByteArray();
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}
