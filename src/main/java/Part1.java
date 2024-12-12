package main.java;


import main.java.entity.Entities;
import main.java.entity.Entity;
import main.java.storage.CsvSerializable;
import main.java.storage.Storage;
import main.java.storage.TextStorage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Part1 {
	private static void writeDataIntoFile(String fileName) throws IOException{
		try (FileOutputStream fos = new FileOutputStream(fileName)) {
			String entPackage = "main.java.entity";
			fos.write((
					entPackage + ",Supplier,1,Roman,Sladkovicova 12378\n" +
							entPackage + ",Supplier,2,Andrii,Sumskaya Street\n" +
							entPackage + ",Customer,1,Anastasia,Plekhanivska Street \n" +
							entPackage + ",Product,1,Viktoria,100.1,100"
			).getBytes());
		}
	}

	private static void outputFileData(String fileName) throws IOException{
		try (BufferedReader reader = new BufferedReader(
				new FileReader(fileName))) {
			reader.lines().forEach(System.out::println);
		}
    }

	public static void main(String[] args) throws IOException {
		final String inputFile = "src\\main\\resources\\data.csv";
		final String outputFile = "src\\main\\resources\\data.out.csv";

		//Create the file with a test data
		writeDataIntoFile(inputFile);

		//Get a storage instance
		TextStorage<CsvSerializable> storage = new Storage();

		// Get a list of the Domain objects from the external storage
		List<CsvSerializable> ls = storage.load(inputFile);

		//Show the obtained data
		System.out.println("The obtained data:");
		ls.forEach(System.out::println);

		//Sort the data
		List<Entity> sortedList = new ArrayList<>(ls.stream().map(s -> ((Entity) s)).toList());
		sortedList.sort(new Entities.NameAndIdComparator());

		//Show the sorted data
		System.out.println("\nSorted data:");
		sortedList.forEach(System.out::println);

		//Save sorted data into external storage
		storage.save(sortedList.stream().map(s -> (CsvSerializable) s).toList(), outputFile);

		//Show the raw text from the source and destination storages
		System.out.println("\nThe initial data:");
		outputFileData(inputFile);

		System.out.println("\nThe sorted written data into a file:");
		outputFileData(outputFile);
	}
}
