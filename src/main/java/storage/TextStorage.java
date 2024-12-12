package main.java.storage;

import java.io.IOException;
import java.util.List;

public interface TextStorage<T> {
   void save(List<T> collection) throws IOException;

   List<T> load() throws IOException;
}

