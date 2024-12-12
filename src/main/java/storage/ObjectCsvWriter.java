package main.java.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

class ObjectCsvWriter implements AutoCloseable{
    BufferedWriter out;

    ObjectCsvWriter(BufferedWriter out){
        this.out = Objects.requireNonNull(out);
    }

    void write(CsvSerializable ent) throws IOException {
        String clazzName = Objects.requireNonNull(ent).getClass().getName();

        int packageIndex = clazzName.lastIndexOf('.');

        //If class is placed in default package then packageIndex
        //returns '-1' so we handle this manually
        String packg = (packageIndex != -1) ? clazzName.substring(0, packageIndex) : "";

        //If class has package then we should take the substring
        //from the last dot which describes the end of the package name
        //and include all the possible outer classes if one is inner one
        String name = (packageIndex != -1) ? clazzName.substring(packageIndex + 1) : clazzName;

        out.write(packg + ',' + name + ',' + ent.getCsvString() + "\n");
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}

