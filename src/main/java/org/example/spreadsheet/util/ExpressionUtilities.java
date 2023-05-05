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
        List<String> postfixExpression = infixToPostfix(expression.substring(1));
        System.out.println(postfixExpression);
        return evaluatePostfixExpression(postfixExpression, sheet);
    }

    private static Integer evaluatePostfixExpression(List<String> postfixExpression, Map<String, Cell<?>> sheet) throws InvalidExpression{
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < postfixExpression.size(); i++) {
            String currentItem = postfixExpression.get(i);
            if (currentItem.length() == 1 && isOperator(currentItem.charAt(0))) {
                Integer second = stack.pop();
                if (stack.isEmpty() && (currentItem.charAt(0) == '+' || currentItem.charAt(0) == '-')) {
                    stack.push(computeValue(0, second, currentItem.charAt(0)));
                } else if(!stack.isEmpty()) {
                    Integer first = stack.pop();
                    stack.push(computeValue(first, second, currentItem.charAt(0)));
                } else {
                    throw new InvalidExpression("Invalid expression");
                }
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
                    throw new InvalidExpression("Cell value is null : " + currentItem);
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
    private static List<String> infixToPostfix(String expression) throws InvalidExpression{
        Stack<Character> stack = new Stack<>();
        int length = expression.length();
        String operand = "";
        List<String> outExpression = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            char currentChar = expression.charAt(i);
            if (i == length - 1 && operand != "") {
                outExpression.add(operand);
                operand = "";
            }
            if (currentChar == '(') {
                stack.push(currentChar);
            } else if (currentChar == ')'){
                if (operand != "") {
                    outExpression.add(operand);
                    operand = "";
                }
                if (!stack.isEmpty()) {
                    char top = stack.pop();
                    outExpression.add(String.valueOf(top));
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        outExpression.add(String.valueOf(top));
                        top = stack.pop();
                    }
                    stack.pop();
                }
            } else if (isOperator(currentChar)) {
                if (operand != "") {
                    outExpression.add(operand);
                    operand = "";
                }
                if (!stack.isEmpty()) {
                    if (stack.peek() != '(') {
                        int currentOpPrecedence = getPrecedence(currentChar);
                        int currentStackTopPrecedence = getPrecedence(stack.peek());
                        while (currentOpPrecedence > currentStackTopPrecedence) {
                            char top = stack.pop();
                            outExpression.add(String.valueOf(top));
                            if (!stack.isEmpty() && stack.peek() != '(') {
                                currentOpPrecedence = getPrecedence(currentChar);
                                currentStackTopPrecedence = getPrecedence(stack.peek());
                            } else {
                                break;
                            }
                        }
                    }
                }
                stack.push(currentChar);
            } else {
                operand += currentChar;
            }
        }
        while(!stack.isEmpty()) {
            char top = stack.pop();
            outExpression.add(String.valueOf(top));
        }
        return outExpression;
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
        String basePattern = "[A-Z]{1,3}\\d{1,7}";
        Pattern pattern = Pattern.compile("^=[-+]?(\\()*([-+]?" + basePattern + ")+((\\))*[-+/*](\\()*[-+]?" + basePattern + ")*(\\))*$");
        Matcher matcher = pattern.matcher(expression);
        return matcher.find();
    }
}
