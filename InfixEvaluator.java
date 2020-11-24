
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;


public class InfixEvaluator {

    private final ArrayList<String> tokens = new ArrayList();
    private final Stack<String> opStack = new Stack();
    private final Stack<Double> valStack = new Stack();
    private final ArrayList<String> postfix = new ArrayList();
    private int index = 0;
    private boolean parseCorrect = true;
    private boolean isop = false;
    private final DecimalFormat df = new DecimalFormat("0");
    
    

    public Stack<Double> getValStack() {
		return valStack;
	}


	public InfixEvaluator(int n) {
        df.setMaximumFractionDigits(n);
    }


    private boolean nextToken() {
        index++;
        return !(index >= tokens.size());
    }

   
    private String currentToken() {
        return tokens.get(index);
    }

    
    private boolean parse() {
        parseCorrect = true;
        index = 0;
        expr();
        if (nextToken()) {
            return false;
        }
        return parseCorrect;
    }

   
    private boolean expect(String s) {
        if (index >= tokens.size()) {
            return false;
        } else {
            return currentToken().equals(s);
        }
    }
    
    
    private void expr() {
        isop = false;
        term();
        while (expect("+") || expect("-")) {
            isop = true;
            nextToken();
            term();
        }
    }

    private void term() {
        isop = false;
        factor();
        while (expect("*") || expect("/") ) {
            isop = true;
            nextToken();
            factor();
        }
    }

    
    private void factor() {
       
        if (value()) {
            nextToken();
        } else if (expect("(")) {
            nextToken();
            expr();
            if (expect(")")) {
                nextToken();
            } else {
                parseCorrect = false;
            }
        } else {
            parseCorrect = false;
        }
    }

   
    private boolean value() {
        if (index >= tokens.size()) {
            return false;
        }
        if (isDouble(tokens.get(index))) {
            return true;
        }
        if (expect("-")) {
            nextToken();
            if (index >= tokens.size()) {
                return false;
            }
           
            if (isDouble(tokens.get(index))) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

   
    private double doMath() {
        double x, y;
        if (postfix.isEmpty()) {
            return valStack.pop();
        }
        String s = postfix.remove(0);
        switch (s) {
            case "+":
                x = valStack.pop();
                y = valStack.pop();
                valStack.push(y + x);
                return doMath();
            case "-":
                x = valStack.pop();
                y = valStack.pop();
                valStack.push(y - x);
                return doMath();
            case "*":
                x = valStack.pop();
                y = valStack.pop();
                valStack.push(y * x);
                return doMath();
            case "/":
                x = valStack.pop();
                y = valStack.pop();
                valStack.push(y / x);
                return doMath();
            case "%":
            	x= valStack.pop();
            	y = valStack.pop();
            	valStack.push(x%y);
            	return doMath();
            
            case "_":
                valStack.push(-1 * valStack.pop());
                return doMath();
            default:
                valStack.push(Double.parseDouble(s));
                return doMath();
        }
    }

   
    private String toPostFix() {
        postfix.clear();
        opStack.clear();
        boolean operand = true;
        String ps = "";
        for (String s : tokens) {
            if (isDouble(s)) {
                postfix.add(s);
                operand = false;
                continue;
            }
            if (isLeftParen(s)) {
                opStack.push(s);
                operand = true;
                continue;
            }
            if (s.equalsIgnoreCase("-") && operand) {
                opStack.push("_");
                continue;
            }
            if (isOperator(s)) {
                operand = true;
                while (!opStack.isEmpty() && !isLeftParen(opStack.peek())) {
                    if (precedence(s) <= precedence(opStack.peek())) {
                        postfix.add(opStack.pop());
                    } else {
                        break;
                    }
                }
                opStack.push(s);
                continue;
            }
            if (isRightParen(s)) {
                operand = false;
                while (!opStack.isEmpty() && !isLeftParen(opStack.peek())) {
                    postfix.add(opStack.pop());
                }

                opStack.pop();
                continue;
            }
        }
        while (!opStack.isEmpty()) {
            postfix.add(opStack.pop());
        }
        for (String str : postfix) {
            ps += str;
            ps += " ";
        }

        return ps;

    }

    
    private boolean isOperator(String s) {
        switch (s) {
            case "+":
            case "-":
            case "*":
            case "/":
            case "_":
            case "%":
                return true;
            default:
                return false;
        }
    }

 
    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

 
    private boolean isLeftParen(String s) {
        return s.equals("(");
    }

    
    private boolean isRightParen(String s) {
        return s.equals(")");
    }

    
    public String evaluate(String s) {
        valStack.clear();
        buildTokens(s);
        String output = "";
        if (!parse()) {
            return "Lexical error";
        }
        toPostFix();
        try {
            output = df.format(doMath());
        } catch (Exception e) {
            return "Computational error";
        }
        if (output.equals("�")) {
            return "Computational error";
        }
        return output;
    }


    private int precedence(String s) {
        switch (s) {
            case "+":
                return 10;
            case "-":
                return 10;
            case "*":
                return 20;
            case "/":
                return 20;
            case "%":
            	return 20;
       
            case "_":
                return 10;
         
            default:
                return -1;
        }
    }


    private void buildTokens(String s) {
        tokens.clear();
        StringBuilder in = new StringBuilder();
        String operator = "";
        for (char c : s.toCharArray()) {
            if (c == 46 || (c > 47 && c < 58)) {
                in.append(c);
                continue;
            }
            switch (c) {
                case '+':
                    operator = "+";
                    break;
                case '-':
                    operator = "-";
                    break;
                case '*':
                    operator = "*";
                    break;
                case '/':
                    operator = "/";
                    break;
                case '%':
                	operator ="%";
                	break;
               
                case '(':
                    operator = "(";
                    break;
                case ')':
                    operator = ")";
                    break;
                
            }
            if (in.length() > 0) {
                tokens.add(in.toString());
                in.delete(0, in.length());
            }
            tokens.add(operator);
        }
        if (in.length() > 0) {
            tokens.add(in.toString());
            in.delete(0, in.length());
        }
    }
}