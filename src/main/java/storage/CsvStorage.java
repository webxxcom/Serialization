package main.java.storage;

import java.io.*;
import java.util.*;

public class CsvStorage implements TextStorage<CsvSerializable> {
    private String inputFile;
    private String outputFile;

    public CsvStorage(String fileName) {
        if(Objects.requireNonNull(fileName).trim().isEmpty())
            throw new IllegalArgumentException("The fileName parameter cannot be empty");

        this.inputFile = fileName;
        this.outputFile = fileName;
    }

    public CsvStorage(String inputFile, String outputFile) {
        if(Objects.requireNonNull(inputFile).trim().isEmpty()
                || Objects.requireNonNull(outputFile).trim().isEmpty())
            throw new IllegalArgumentException("The fileName parameter cannot be empty");

        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public void save(List<CsvSerializable> collection) throws IOException {
        Objects.requireNonNull(collection);
        if(Objects.requireNonNull(outputFile).trim().isEmpty())
            throw new IllegalArgumentException("The fileName parameter cannot be empty");

        try(ObjectCsvWriter ew = new ObjectCsvWriter(
                new BufferedWriter
                        (new FileWriter(outputFile)))) {
            for (CsvSerializable obj : collection)
                if (obj != null)
                    ew.write(obj);
        }
    }

    @Override
    public List<CsvSerializable> load()
            throws IOException {
        try(ObjectCsvReader er = new ObjectCsvReader(
                new BufferedReader(
                        new FileReader(inputFile)))) {
            return er.read();
        }
    }
}
