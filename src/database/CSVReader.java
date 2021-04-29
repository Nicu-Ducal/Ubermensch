package database;

/**
 * Class used for read operations from .csv (comma-separated values) files
 */
public class CSVReader {
    public static CSVReader readerInstance = null;

    private CSVReader() {}

    public static CSVReader getInstance() {
        if (readerInstance == null)
            readerInstance = new CSVReader();
        return readerInstance;
    }

    public void read() {

    }
}
