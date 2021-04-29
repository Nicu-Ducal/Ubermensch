package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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

    public void readFile(String path) throws Exception {
        File fpath = new File(path);
        if (!fpath.isFile()) throw new Exception("Bad path to the file");
        try {
            FileReader csvFile = new FileReader(path);
            BufferedReader buffReader = new BufferedReader(csvFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
