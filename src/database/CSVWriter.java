package database;

import java.io.*;

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

    public void writeLineToFile(String path, String content, Boolean appendMode) throws Exception {
        File fpath = new File(path);
        if (!fpath.isFile()) throw new Exception("Bad path to the file");
        try {
            FileWriter csvFile = new FileWriter(path, appendMode);
            BufferedWriter buffWriter = new BufferedWriter(csvFile);
            buffWriter.write(content + "\n");
            buffWriter.close();
            csvFile.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void clearFile(String path) throws Exception {
        File fpath = new File(path);
        if (!fpath.isFile()) throw new Exception("Bad path to the file");
        try {
            FileWriter csvFile = new FileWriter(path, false);
            BufferedWriter buffWriter = new BufferedWriter(csvFile);
            buffWriter.write("");
            buffWriter.close();
            csvFile.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
