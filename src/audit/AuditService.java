package audit;

public class AuditService {
    public static AuditService auditInstance = null;

    private AuditService() {
    }

    public AuditService getInstance() {
        if (auditInstance == null)
            auditInstance = new AuditService();
        return auditInstance;
    }
}
