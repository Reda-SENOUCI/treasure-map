package org.example.port.adapter.cli;

public class InvalidDataException extends Exception {
    public InvalidDataException(String message) {
        super("Invalid data " + message);
    }
}
