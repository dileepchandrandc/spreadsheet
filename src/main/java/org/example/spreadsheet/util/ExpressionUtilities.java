package org.example.spreadsheet.util;

import org.example.spreadsheet.Cell;
import org.example.spreadsheet.customexception.InvalidExpression;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionUtilities {

    public static Integer evaluate(String expression, Map<String, Cell<?>> sheet) throws InvalidExpression {
        if (!validateExpression(expression)) {
            throw new InvalidExpression("Cell value is not a valid expression");
        }
        String postfixExpression = infixToPostfix(expression.substring(1));
        return evaluatePostfixExpression(postfixExpression, sheet);
    }

    private static Integer evaluatePostfixExpression(String postfixExpression, Map<String, Cell<?>> sheet) throws InvalidExpression{
        Stack<Integer> stack = new Stack<>();
        String[] items = postfixExpression.split(",");
        for (int i = 0; i < items.length; i++) {
            String currentItem = items[i];
            if (currentItem.length() == 1 && isOperator(currentItem.charAt(0))) {
                Integer second = stack.pop();
                Integer first = stack.pop();
                stack.push(computeValue(first, second, currentItem.charAt(0)));
            }
            else {
                Cell<?> cell = sheet.get(currentItem);
                if (cell != null) {
                    Object cellData = cell.getData();
                    if (cellData instanceof Integer) {
                        stack.push((Integer)cellData);
                    } else {
                        throw new InvalidExpression("Cell value is not a valid number");
                    }
                } else {
                    throw new InvalidExpression("Cell value is null");
                }
            }
        }
        return stack.pop();
    }

    private static Integer computeValue(Integer first, Integer second, char operator) {
        switch (operator) {
            case '+':
                return first + second;
            case '-':
                return first - second;
            case '*':
                return first * second;
            case '/':
                return first / second;
            default:
                return null;
        }
    }
    private static String infixToPostfix(String expression) throws InvalidExpression{
        Stack<Character> stack = new Stack<>();
        String postfix = "";
        int length = expression.length();
        for (int i = 0; i < length; i++) {
            char currentChar = expression.charAt(i);
            if (currentChar == '(') {
                stack.push(currentChar);
            } else if (currentChar == ')'){
                if (!stack.isEmpty()) {
                    char top = stack.pop();
                    postfix += ",";
                    postfix += top;
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        postfix += ",";
                        postfix += top;
                        top = stack.pop();
                    }
                    stack.pop();
                }
            } else if (isOperator(currentChar)) {
                if (!stack.isEmpty()) {
                    if (stack.peek() != '(') {
                        int currentOpPrecedence = getPrecedence(currentChar);
                        int currentStackTopPrecedence = getPrecedence(stack.peek());
                        while (currentOpPrecedence > currentStackTopPrecedence) {
                            postfix += ",";
                            postfix += stack.pop();
                            if (!stack.isEmpty() && stack.peek() != '(') {
                                currentOpPrecedence = getPrecedence(currentChar);
                                currentStackTopPrecedence = getPrecedence(stack.peek());
                            } else {
                                break;
                            }
                        }
                    }
                }
                postfix += ",";
                stack.push(currentChar);
            } else {
                postfix += currentChar;
            }
        }
        while(!stack.isEmpty()) {
            postfix += ",";
            postfix += stack.pop();
        }
        return postfix;
    }

    private static boolean isOperator(char character) {
        List<Character> validCharacters = Arrays.asList('-', '+', '/', '*');
        return validCharacters.contains(character);
    }

    private static int getPrecedence(char operator) {
        if (operator == '+' || operator == '-') return 1;
        else if (operator == '*' || operator == '/' || operator == '%') return 2;
        throw new InvalidExpression("Invalid operator");
    }

    public static boolean validateExpression(String expression) {
        // TODO : Replace the regular expression with correct one
        Pattern pattern = Pattern.compile(".", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(expression);
        return matcher.find();
    }
}
