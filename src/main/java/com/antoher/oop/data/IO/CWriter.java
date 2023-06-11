package com.antoher.oop.data.IO;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CWriter {
    private final CSVFormat csvFormat;

    public CWriter() {
        this.csvFormat = CSVFormat.Builder.create().build();
    }

    public void writeFromMap(File file, List<Map<String, Object>> data) {
        try (FileWriter fileWriter = new FileWriter(file);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, csvFormat)) {
            Map<String, Object> firstRow = data.get(0);
            List<String> headers = new ArrayList<>(firstRow.keySet());

            csvPrinter.printRecord(headers);
            for (Map<String, Object> row : data) {
                csvPrinter.printRecord(row.values());
            }

            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
