package main.java.storage;

import java.io.IOException;
import java.util.List;

public interface TextStorage<T> {
   void save(List<T> collection, String fileName) throws IOException;

   List<T> load(String fileName) throws IOException;
}

