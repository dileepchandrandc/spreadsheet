package org.example;

import org.example.spreadsheet.SpreadSheet;
import org.example.spreadsheet.customexception.InvalidDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    SpreadSheet spreadSheet;
    @BeforeEach
    public void init() {
        spreadSheet = new SpreadSheet();
    }
    @Test
    public void testWithoutExpression() {
        // NOTE : Testing simple cells
        spreadSheet.setCellValue("A1", 10);
        spreadSheet.setCellValue("A2", 15);
        spreadSheet.setCellValue("A3", 12);
        spreadSheet.setCellValue("A4", 15);
        assertEquals(spreadSheet.getCellValue("A1"), 10);
        assertEquals(spreadSheet.getCellValue("A2"), 15);
        assertEquals(spreadSheet.getCellValue("A3"), 12);
        assertEquals(spreadSheet.getCellValue("A4"), 15);
    }

    @Test
    public void testWithExpression() {
        // NOTE : Testing cell with valid expression
        spreadSheet.setCellValue("A1", 10);
        spreadSheet.setCellValue("A2", 15);
        spreadSheet.setCellValue("A3", 12);
        spreadSheet.setCellValue("A4", "=A1+A2+A3");
        assertEquals(spreadSheet.getCellValue("A1"), 10);
        assertEquals(spreadSheet.getCellValue("A2"), 15);
        assertEquals(spreadSheet.getCellValue("A3"), 12);
        assertEquals(spreadSheet.getCellValue("A4"), 37);
    }

    @Test
    public void testWithComplexExpression() {
        // NOTE : Testing cell with valid complex expression
        spreadSheet.setCellValue("A1", 10);
        spreadSheet.setCellValue("A2", 15);
        spreadSheet.setCellValue("A3", 12);
        spreadSheet.setCellValue("A4", "=A1*(A2+A3)");
        assertEquals(spreadSheet.getCellValue("A1"), 10);
        assertEquals(spreadSheet.getCellValue("A2"), 15);
        assertEquals(spreadSheet.getCellValue("A3"), 12);
        assertEquals(spreadSheet.getCellValue("A4"), 270);
    }

    @Test
    public void testWithInvalidCell() {
        // NOTE : Testing cell with invalid cell specified in the expression
        spreadSheet.setCellValue("A1", 10);
        spreadSheet.setCellValue("A2", 15);
        spreadSheet.setCellValue("A3", 12);
        spreadSheet.setCellValue("A4", "=A1+A2+A5");
        spreadSheet.setCellValue("A5", 20);
        assertEquals(spreadSheet.getCellValue("A1"), 10);
        assertEquals(spreadSheet.getCellValue("A2"), 15);
        assertEquals(spreadSheet.getCellValue("A3"), 12);
        assertThrows(InvalidDataException.class, () -> spreadSheet.getCellValue("A4"));
        assertEquals(spreadSheet.getCellValue("A5"), 20);
    }

    @Test
    public void testWithInvalidExpression() {
        // NOTE : Testing cell with invalid expression
        spreadSheet.setCellValue("A1", 10);
        spreadSheet.setCellValue("A2", 15);
        spreadSheet.setCellValue("A3", 12);
        spreadSheet.setCellValue("A4", "=A1+A2+A3+");
        assertEquals(spreadSheet.getCellValue("A1"), 10);
        assertEquals(spreadSheet.getCellValue("A2"), 15);
        assertEquals(spreadSheet.getCellValue("A3"), 12);
        assertThrows(InvalidDataException.class, () -> spreadSheet.getCellValue("A4"));
    }

    @Test
    public void testComplexExpressionWithInvalidParenthesis() {
        // NOTE : Testing cell with complex expression having invalid parenthesis combination
        spreadSheet.setCellValue("A1", 10);
        spreadSheet.setCellValue("A2", 15);
        spreadSheet.setCellValue("A3", 12);
        spreadSheet.setCellValue("A4", "=A1*((A2+A3)");
        assertEquals(spreadSheet.getCellValue("A1"), 10);
        assertEquals(spreadSheet.getCellValue("A2"), 15);
        assertEquals(spreadSheet.getCellValue("A3"), 12);
        assertThrows(InvalidDataException.class, () -> spreadSheet.getCellValue("A4"));
    }
}
