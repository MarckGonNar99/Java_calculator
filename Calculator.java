
import java.text.DecimalFormat;
import java.util.Stack;

public class Calculator {

    public String evaluate(String statement) {
        // Verify null
        if (statement == null) {
            return null;
        }

        // Verify if the statement is empty
        if (statement.isEmpty()) {
            return null;
        }

        // Verify 2 operators together or parentheses 
        if (containsOperatorsTogether(statement) || !areParenthesesBalanced(statement)
                || containsCommas(statement)) {
            return null;
        }

        try {
            double result = evaluateExpression(statement);
            System.out.println(result);
            // Round the result to 4 significant digits
            DecimalFormat df = new DecimalFormat("#.####");
            String roundedResult = df.format(result);
            return roundedResult;
        } catch (ArithmeticException | IllegalArgumentException ex) {
            // If an error occurs while evaluating the expression, return null
            return null;
        }
    }

    private boolean containsOperatorsTogether(String expression) {
        char[] allowedOperators = {'+', '-', '*', '/'};

        for (int i = 0; i < expression.length() - 1; i++) {
            char currentChar = expression.charAt(i);
            char nextChar = expression.charAt(i + 1);

            if (isOperator(currentChar, allowedOperators) && isOperator(nextChar, allowedOperators)) {
                return true; // Operators are together
            }
            // Check if the current character is a minus sign and if the next character is a digit
            if (currentChar == '-' && Character.isDigit(nextChar)) {
                // Skip to the next character, as it's part of a negative number, not an operator
                continue;
            }
        }

        return false; // No operators together
    }

    private boolean areParenthesesBalanced(String expression) {
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    return false; // Unbalanced parentheses
                }
            }
        }

        return stack.isEmpty(); // Should be empty if parentheses are balanced
    }

    private boolean isOperator(char c, char[] allowedOperators) {
        for (char operator : allowedOperators) {
            if (c == operator) {
                return true;
            }
        }
        return false;
    }
    
    //Auxilar method to find ',' which are not allowed
    private boolean containsCommas(String expression) {
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == ',') {
                return true; // Contains commas
            }
        }
        return false; // No commas found
    }

    //Logical Method to Evaluate the Operations
    private double evaluateExpression(String expression) {
        // Use a stack to evaluate operators and operands
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();
         boolean lastWasNumber = false;

        char []expToken=expression.toCharArray();
        for(int i=0;i<expToken.length;i++){
            char ch=expToken[i];
            //Case it´s space
            if(ch==' '){
                continue;
            }else if(Character.isDigit(ch) || ch=='.'){
                StringBuffer buff=new StringBuffer();
                while(i<expToken.length && (Character.isDigit(expToken[i]) || expToken[i]=='.')){
                    buff.append(expToken[i++]);
                }
                i--;
                operands.push(Double.parseDouble(buff.toString()));
                
            }else if(ch=='('){
                operators.push(ch);
            }else if(ch==')'){
                while(operators.peek()!= '('){
                    double output= performOperation(operands.pop(), operands.pop(), operators.pop());
                    operands.push(output);
                }
                            if (!operators.isEmpty() && operators.peek() == '(') {
                    operators.pop(); // Remove the opening parenthesis
                } else {
                    throw new IllegalArgumentException("Unbalanced parentheses");
                }
                lastWasNumber = true; // Indicate that the last token was a number
            }else if(isOperator(ch)){
                while(!operators.isEmpty() && precedence(ch)<= precedence(operators.peek())){
                    double output=performOperation(operands.pop(), operands.pop(),operators.pop());
                    operands.push(output);
                }
                operators.push(ch);
            }
        }
        while(!operators.isEmpty()){
            double output= performOperation(operands.pop(), operands.pop(),operators.pop());
            operands.push(output);
        }
        return operands.peek();
    }
    

    //Auxiliar method to see the symbols allowed
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int precedence(char op){
        if(op=='*' || op=='/'){
            return 2;
        }else if(op=='+' || op=='-'){
            return 1;
        }
        return 0;
    }

    //Auxiliar method to have an individual operation
    private void resolveOperation(Stack<Double> operands, Stack<Character> operators) {
        char operator = operators.pop();
        double num2 = operands.pop();
        double num1 = operands.pop();
        double result = performOperation(num1, num2, operator);
        operands.push(result);
    }

    //Do the operation
    private double performOperation(double num1, double num2, char operator) {
        switch (operator) {
            case '+':
                return num1 + num2;
            case '-':
                return num2 - num1;
            case '*':
                return num1 * num2;
            case '/':
                if (num2 == 0) {
                    throw new ArithmeticException("Division by zero not allowed");
                }
                return num2 / num1;
            default:
                throw new IllegalArgumentException("Invalid operator");
        }
    }
}
