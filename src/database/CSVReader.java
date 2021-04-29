package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

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

    public List<String[]> readFile(String path) throws Exception {
        File fpath = new File(path);
        if (!fpath.isFile()) throw new Exception("Bad path to the file");
        try {
            FileReader csvFile = new FileReader(path);
            BufferedReader buffReader = new BufferedReader(csvFile);
            List<String[]> fileContent = new ArrayList<>();
            String line;
            while((line = buffReader.readLine()) != null) {
                String[] data = line.split(",");
                fileContent.add(data);
            }
            buffReader.close();
            csvFile.close();
            return fileContent;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
