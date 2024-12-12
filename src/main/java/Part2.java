package main.java;

import main.java.entity.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Part2 {
	public static void main(String[] args) {
		final String fileName = "src\\main\\resources\\data.ser";

		List<Entity> initialList = new ArrayList<>(List.of(
				new Customer(1,"Andrii", "Sumskaya Street"),
				new Supplier(1, "Roman", "Sladkovicova 1248"),
				new Product(1,"Viktoria",100.1,100)
		));

		try(ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(fileName))){
			for(Entity ent : initialList)
				oos.writeObject(ent);
		} catch(IOException ex){
			throw new RuntimeException("IOException raised when writing to " + fileName, ex);
		}

		List<Entity> readList = new ArrayList<>();
		try(ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(fileName))){
			while(true) {
				try {
					readList.add((Entity) ois.readObject());
				} catch (EOFException ex) {
					break;
				} catch(ClassNotFoundException ex){
					throw new RuntimeException("Class was not found when reading " + fileName);
				}
			}
		} catch(IOException ex){
			throw new RuntimeException("IOException raised when reading from " + fileName, ex);
		}

		System.out.println("Showing the original list...");
		initialList.forEach(System.out::println);

		System.out.println("\nShowing the list after reading serialized objects from file...");
		readList.forEach(System.out::println);
	}
}
