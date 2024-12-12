package main.java.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ObjectCsvReader implements AutoCloseable {
    BufferedReader in;

    ObjectCsvReader(BufferedReader in) {
        this.in = in;
    }

    /**
     * Reading {@link CsvSerializable} from {@code csv} file is only possible
     * if all the specified objects have parameterless constructor as to call the
     * {@link CsvSerializable#fromFields(String[])} the object is first created
     * without parameters and then is used to call the method
     *
     * @return {@link List} of read {@link CsvSerializable} objects
     * @throws IOException      to know when this is thrown see {@link IOException}
     * @throws IllegalCsvFormat when parsing error was encountered which means that
     *                          there is an error in csv rows which are used for objects
     */
    List<CsvSerializable> read() throws IOException, IllegalCsvFormat {
        List<CsvSerializable> ls = new ArrayList<>();

        String line;
        String[] vals;
        while ((line = in.readLine()) != null) {
            try {
                vals = line.split(",");

                //Check if the class is in default or named package
                String clazzFullName = vals[0].isEmpty() ?
                        vals[1] //If first value is empty then it's in the default package
                        : (vals[0] + '.' + vals[1]); // Otherwise class has a package

                CsvSerializable obj = (CsvSerializable)
                        (Class.forName(clazzFullName)
                                .getDeclaredConstructor()
                                .newInstance());
                ls.add(obj.fromFields(Arrays.copyOfRange(vals, 2, vals.length)));
            } catch (IndexOutOfBoundsException ex) {
                throw new IllegalCsvFormat(
                        "The opened file lacks package path with invalid class name", ex);
            } catch (NoSuchMethodException | IllegalAccessException ex) {
                throw new NoParameterlessConstructorException(
                        "Define the parameterless constructor in the class provided in the line: " + line, ex);
            } catch (ReflectiveOperationException ex) {
                throw new IllegalArgumentException(ex.toString(), ex);
            }
        }
        return ls;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
