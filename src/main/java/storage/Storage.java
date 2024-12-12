package main.java.storage;

import java.io.*;
import java.util.*;

public class Storage implements TextStorage<CsvSerializable> {
    static class ObjectCsvWriter implements AutoCloseable{
        BufferedWriter out;

        ObjectCsvWriter(BufferedWriter out){
            this.out = Objects.requireNonNull(out);
        }

        void write(CsvSerializable ent) throws IOException {
            String clName = Objects.requireNonNull(ent).getClass().getName();

            int packageIndex = clName.lastIndexOf('.');

            //If class is placed in default package then packageIndex
            // returns '-1' so we handle this manually
            String packg = (packageIndex != -1) ? clName.substring(0, packageIndex) : "";

            //If class has package then we should take thew substring
            // from the last dot which describes the end of the package name
            // and include all the possible outer classes if one is inner one
            String name = (packageIndex != -1) ? clName.substring(packageIndex + 1) : clName;

            out.write(packg + ',' + name + ',' + ent.getCsvString() + "\n");
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }

    static class ObjectCsvReader implements AutoCloseable{
        BufferedReader in;

        ObjectCsvReader(BufferedReader in){
            this.in = in;
        }

        /**
         * Reading {@link CsvSerializable} from {@code csv} file is only possible
         * if all the specified objects have parameterless constructor as to call the
         * {@link CsvSerializable#fromFields(String[])} the object is first created
         * without parameters and then is used to call the method
         * @return {@link List} of read {@link CsvSerializable} objects
         * @throws IOException to know when this is thrown see {@link IOException}
         * @throws IllegalCsvFormat when parsing error was encountered which means that
         * there is an error in csv rows which are used for objects
         */
        List<CsvSerializable> read()
                throws IOException, IllegalCsvFormat {
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
                            Class.forName(clazzFullName)
                                    .getDeclaredConstructor()
                                    .newInstance();
                    ls.add(obj.fromFields(Arrays.copyOfRange(vals, 2, vals.length)));
                } catch (IndexOutOfBoundsException ex) {
                    throw new IllegalCsvFormat(
                            "The opened file lacks package path with invalid class name", ex);
                } catch (NoSuchMethodException | IllegalAccessException ex) {
                    throw new NoParameterlessConstructorException(
                            "Define the parameterless constructor in the class provided in the line: " + line, ex);
                } catch (ReflectiveOperationException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
            return ls;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }

    @Override
    public void save(List<CsvSerializable> collection, String fileName)
            throws IOException {
        Objects.requireNonNull(collection);
        if(Objects.requireNonNull(fileName).isEmpty())
            throw new IllegalArgumentException("The fileName parameter cannot be empty");

        try(ObjectCsvWriter ew = new ObjectCsvWriter(
                new BufferedWriter
                        (new FileWriter(fileName)))) {
            for (CsvSerializable obj : collection)
                if (obj != null)
                    ew.write(obj);
        }
    }

    @Override
    public List<CsvSerializable> load(String fileName)
            throws IOException {
        try(ObjectCsvReader er = new ObjectCsvReader(
                new BufferedReader(
                        new FileReader(fileName)))) {
            return er.read();
        }
    }
}
