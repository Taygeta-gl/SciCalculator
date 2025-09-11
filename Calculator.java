
import java.util.*;
public class Calculator {
    
    public static ArrayList<String> tokenize(String expr) 
    {
        ArrayList<String> tokens = new ArrayList<>();
        String number="";
        for(int i =0; i<expr.length(); i++) {
            char c = expr.charAt(i);
            if(Character.isDigit(c) || c == '.') {
                number +=c;
            }
            else {
                if(!number.isEmpty()) {
                    tokens.add(number); //like if number is filled already with like digits, then you add it to tokens + emtpy number again
                    number="";
                }
                if("+-*()".indexOf(c)!=-1) {
                    tokens.add(Character.toString(c)); // else if. it is operation, then you convert it to string and add to tokens
                }
            }
        }
        if(!number.isEmpty()) {
            tokens.add(number); //if the numbers is someohow not empty, then add them to tokens
        }
        return tokens;
    }

    public static ArrayList<String> infixToPostfix(ArrayList<String> tokens) {
        ArrayList<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        Map<String, Integer> precedence = new HashMap<>();
        precedence.put("+", 1);
        precedence.put("-", 1);
        precedence.put("*", 2);
        precedence.put("/", 2);

        for (String token : tokens) {
            if (token.matches("\\d+(\\.\\d+)?")) { // number
                output.add(token);
            } else if (precedence.containsKey(token)) { // operator
                while (!stack.isEmpty() && precedence.containsKey(stack.peek())
                        && precedence.get(stack.peek()) >= precedence.get(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.pop();
            }
        }
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        return output;
    }



    public static double evaluatePostfix(ArrayList<String> postfix) {
        Stack<Double> stack = new Stack<>();
        for (String token: postfix) {
            if(token.matches("\\d+(\\.\\d+)?")) {
            stack.push(Double.parseDouble(token));
            }
        else {
            double b = stack.pop();
            double a = stack.pop();
            switch(token) {
                case "+": stack.push(a+b); break;
                case "-": stack.push(a-b); break;
                case "*": stack.push(a*b); break;
                case "/": stack.push(a/b); break;
            }
        }
    }
    return stack.pop();
}

    
    public static void main(String[] args) {
        System.out.println("Calculator");
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter expression");
        String expr = sc.nextLine();
        
        
        ArrayList<String> tokens = tokenize(expr);
        ArrayList<String> postfix = infixToPostfix(tokens); //infix is how people write math and postfix is operands go after the numbers
        double result = evaluatePostfix(postfix);

        System.out.println("Result: " + result);
        

    }

}