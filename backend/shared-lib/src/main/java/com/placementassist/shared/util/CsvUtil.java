package com.placementassist.shared.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.placementassist.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for OpenCSV reading and writing operations.
 */
public final class CsvUtil {

    private CsvUtil() {
    }

    public static byte[] writeCsv(String[] headers, List<String[]> rows) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {

            writer.writeNext(headers);
            for (String[] row : rows) {
                writer.writeNext(row);
            }
            writer.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "CSV_EXPORT_ERROR", "Failed to generate CSV: " + e.getMessage());
        }
    }

    public static List<String[]> readCsv(InputStream inputStream) {
        try (InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVReader reader = new CSVReader(isr)) {
            return reader.readAll();
        } catch (IOException | CsvException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CSV_PARSE_ERROR", "Failed to parse CSV file: " + e.getMessage());
        }
    }
}
