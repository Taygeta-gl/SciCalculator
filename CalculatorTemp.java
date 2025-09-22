import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class CalculatorTemp {

    // STEP 1: Tokenizer
    public static ArrayList<String> tokenize(String expr) {
        ArrayList<String> tokens = new ArrayList<>();
        String number = "";
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                number += c;
            } else {
                if (!number.isEmpty()) {
                    tokens.add(number);
                    number = "";
                }
                if ("+-*/()".indexOf(c) != -1) {
                    tokens.add(Character.toString(c));
                }
            }
        }
        if (!number.isEmpty()) {
            tokens.add(number);
        }
        return tokens;
    }

    // STEP 2: Infix → Postfix
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

    // STEP 3: Evaluate Postfix
    public static double evaluatePostfix(ArrayList<String> postfix) {
        Stack<Double> stack = new Stack<>();
        for (String token : postfix) {
            if (token.matches("\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                }
            }
        }
        return stack.pop();
    }

    // GUI
    public static void main(String[] args) {
        JFrame frame = new JFrame("Calculator");
        frame.setSize(300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField display = new JTextField();
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 8, 5));

        String[] buttons = {
            "7","8","9","/",
            "4","5","6","*",
            "1","2","3","-",
            "0",".","=","+",
            "C"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String cmd = e.getActionCommand();
                    if (cmd.equals("C")) {
                        display.setText("");
                    } else if (cmd.equals("=")) {
                        try {
                            String expr = display.getText();
                            ArrayList<String> tokens = tokenize(expr);
                            ArrayList<String> postfix = infixToPostfix(tokens);
                            double result = evaluatePostfix(postfix);
                            display.setText(Double.toString(result));
                        } catch (Exception ex) {
                            display.setText("Error");
                        }
                    } else {
                        display.setText(display.getText() + cmd);
                    }
                }
            });
            panel.add(button);
        }

        frame.setLayout(new BorderLayout());
        frame.add(display, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
