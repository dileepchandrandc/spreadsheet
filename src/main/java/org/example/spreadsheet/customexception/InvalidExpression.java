package org.example.spreadsheet.customexception;

public class InvalidExpression extends RuntimeException{

    public InvalidExpression(String message) {
        super(message);
    }
}
