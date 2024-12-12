package main.java.storage;

public class IllegalCsvFormat extends RuntimeException{
    public IllegalCsvFormat(String message){
        super(message);
    }

    public IllegalCsvFormat(String message, Exception ex){
        super(message, ex);
    }

    public IllegalCsvFormat(Exception ex){
        super(ex);
    }
}
