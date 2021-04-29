package database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used for write operations to .csv (comma-separated values) files
 */
public class CSVWriter {
    private static CSVWriter writerInstance = null;

    private CSVWriter() {}

    public static CSVWriter getInstance() {
        if (writerInstance == null)
            writerInstance = new CSVWriter();
        return writerInstance;
    }

    public <T> void writeData(String path, List<T> content, IDatabaseOperations<T> service) {
        List<String[]> data = new ArrayList<>();
        for (T row: content) {
            data.add(service.toDBString(row));
        }
        try {
            writeFile(path, data, false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeFile(String path, List<String[]> data, Boolean appendMode) throws Exception {
        File fpath = new File(path);
        if (!fpath.isFile()) throw new Exception("Bad path to the file");
        try {
            FileWriter csvFile = new FileWriter(path, appendMode);
            BufferedWriter buffWriter = new BufferedWriter(csvFile);
            for (String[] line: data) {
                buffWriter.write(String.join(",", line));
                buffWriter.write("\n");
            }
            buffWriter.close();
            csvFile.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
