package database;

/**
 * Class used for write operations to .csv (comma-separated values) files
 */
public class CSVWriter {
    public static CSVWriter writerInstance = null;

    private CSVWriter() {}

    public static CSVWriter getInstance() {
        if (writerInstance == null)
            writerInstance = new CSVWriter();
        return writerInstance;
    }

    public void write() {

    }
}
