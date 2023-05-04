package org.example.spreadsheet;

import org.example.spreadsheet.customexception.InvalidDataException;
import org.example.spreadsheet.customexception.InvalidExpression;
import org.example.spreadsheet.util.ExpressionUtilities;

import java.util.HashMap;
import java.util.Map;

public class SpreadSheet{
    private Map<String, Cell<?>> data;
    public SpreadSheet() {
        data = new HashMap<>();
    }

    private void addData(String cellId, Cell<?> cell) {
        data.put(cellId, cell);
    }

    private void getCell(String cellId) {
        data.get(cellId);
    }

    private void setCellValue(String cellId, String expression) {
        try {
            Integer result = ExpressionUtilities.evaluate(expression, this.data);
            Cell<Integer> cell = new Cell<>();
            cell.setData(result);
            this.addData(cellId, cell);
        } catch (InvalidExpression ex) {
            Cell<String> cell = new Cell<>();
            cell.setData(expression);
            this.addData(cellId, cell);
        }
    }
    public void setCellValue(String cellId, Object data) {
        if (data instanceof Integer) {
            Cell<Integer> cell = new Cell<>();
            cell.setData((Integer)data);
            this.addData(cellId, cell);
        }
        if (data instanceof String) {
            this.setCellValue(cellId, (String) data);
        }
    }

    public int getCellValue(String cellId) {
        Cell<?> cell = data.get(cellId);
        if (cell == null) {
            throw new InvalidDataException("The value of the cell " + cellId + " is null.");
        }
        else {
            if (cell.getData() instanceof Integer)
                return (Integer)cell.getData();
        }
        throw new InvalidDataException("The value of the cell " + cellId + " is " + cell.getData());
    }
}