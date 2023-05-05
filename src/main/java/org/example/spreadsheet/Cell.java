package org.example.spreadsheet;

public class Cell<T> {

    private T data;

    public Cell(){}
    public Cell(T data){
        this.setData(data);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
