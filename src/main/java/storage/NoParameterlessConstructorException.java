package main.java.storage;

public class NoParameterlessConstructorException extends RuntimeException{
    NoParameterlessConstructorException(String message){
        super(message);
    }

    NoParameterlessConstructorException(String message, Exception ex){
        super(message, ex);
    }

    NoParameterlessConstructorException(Exception ex){
        super(ex);
    }
}
