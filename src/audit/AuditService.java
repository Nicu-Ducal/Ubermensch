package audit;

import database.CSVWriter;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    public static AuditService auditInstance = null;
    private DateTimeFormatter ft = null;
    private final String LOGS_FILE = Paths.get(System.getProperty("user.dir"), "src", "audit", "audit_logs.csv").toString();
    // Non OS Friendly Version: System.getProperty("user.dir") + "/src/audit/audit_logs.csv";

    private AuditService() {
        ft = DateTimeFormatter.ofPattern("dd.MM.yyyy, kk:mm");
    }

    public static AuditService getInstance() {
        if (auditInstance == null)
            auditInstance = new AuditService();
        return auditInstance;
    }

    public void LogAction(String action) {
        CSVWriter writer = CSVWriter.getInstance();
        LocalDateTime dateTime = LocalDateTime.now();
        try {
            writer.writeLineToFile(LOGS_FILE, action + ",Time: " + dateTime.format(ft), true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void ClearLogs() {
        CSVWriter writer = CSVWriter.getInstance();
        try {
            writer.clearFile(LOGS_FILE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
