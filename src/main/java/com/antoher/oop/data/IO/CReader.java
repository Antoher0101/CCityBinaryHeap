package com.antoher.oop.data.IO;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CReader {
    private final CSVFormat csvFormat;

    public CReader() {
        this.csvFormat = CSVFormat.Builder.create().setHeader().build();
    }

    public List<Map<String, Object>> readToMap(File file) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            Reader reader = new FileReader(file);
            CSVParser csvParser = new CSVParser(reader, csvFormat);

            List<String> headers = csvParser.getHeaderMap().keySet().stream()
                    .toList();

            for (CSVRecord record : csvParser) {
                Map<String, Object> row = new HashMap<>();
                for (String header : headers) {
                    String value = record.get(header);
                    row.put(header, value);
                }
                resultList.add(row);
            }

            csvParser.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return resultList;
    }
}