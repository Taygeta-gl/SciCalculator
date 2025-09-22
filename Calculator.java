import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
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
                if("+-*/^()".indexOf(c)!=-1) {
                    tokens.add(Character.toString(c)); // else if. it is operation, then you convert it to string and add to tokens
                }
                else if(Character.isLetter(c)) {
                    String func ="";
                    while(i < expr.length() && Character.isLetter(expr.charAt(i))) {
                        func += expr.charAt(i);
                        i++;
                    }
                    i--;
                    tokens.add(func);
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
        precedence.put("%",2);
        precedence.put("^", 3);

      

        for (String token : tokens) {
            if (token.matches("\\d+(\\.\\d+)?")) { // number
                output.add(token);
            } else if (precedence.containsKey(token)) { // operator
                while (!stack.isEmpty() && precedence.containsKey(stack.peek())) {
                    String top = stack.peek();
                    if (token.equals("^")) {
        // right-associative: stop if top has same precedence
            if (precedence.get(top) > precedence.get(token)) {
                output.add(stack.pop());
            } else {
            break;
            }
        } else {
        // left-associative: pop while top >= token
                if (precedence.get(top) >= precedence.get(token)) {
                output.add(stack.pop());
            } else {
            break;
            }
    }
}


                stack.push(token);
            } else if (token.matches("log|ln|n!|sqrt|square")) { 
                stack.push(token);
    }

            else if (token.equals("(")) {
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
             else if (token.matches("log|ln|sqrt|square|n!")) { 
            Double val = stack.pop();
            
            switch(token) {
                case "log": stack.push(Math.log10(val)); break;
                case "ln": stack.push(Math.log(val)); break;
                case "sqrt": stack.push(Math.sqrt(val)); break;
                case "square": stack.push(val * val); break;
                case "n!": stack.push(factorial(val.intValue())); break;
            }
        }
        else {
            double b = stack.pop();
            double a = stack.pop();
            switch(token) {
                case "+": stack.push(a+b); break;
                case "-": stack.push(a-b); break;
                case "*": stack.push(a*b); break;
                case "/": 
                if(b==0) {
                    throw new ArithmeticException ("Division by 0");
                }
                stack.push(a/b);
                break;
                 case "%": stack.push(a % b); break;
                case "^": stack.push(Math.pow(a, b)); break;

            }
        }
    }
    return stack.pop();
}

private static double factorial(int n) {
    if (n < 0) throw new ArithmeticException("Negative factorial not defined");
    double result = 1;
    for (int i = 2; i <= n; i++) result *= i;
    return result;
}    

    public static void main(String[] args) {

        JFrame frame = new JFrame("Calculator");

        frame.setSize(350, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextField display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setBackground(new Color(0X43537E));
        display.setForeground(Color.WHITE); // text color
        display.setCaretColor(Color.WHITE);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setPreferredSize(new Dimension(0, 80));
        display.setBorder(new RoundedBorder(20)); // 20 = corner radius
        

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x181F32));
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // padding
        topPanel.add(display, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 5, 15, 5));
        panel.setBackground(new Color(0x181F32));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
       
         String[] buttons = {
            "C", "(", ")", "/", "sqrt",
            "7","8","9","*", "^",
            "4","5","6","-", "%",
            "1","2","3","+", "n!",
            "0",".","=", "←", "log",
            "ln", "square"
               
        };

        for(String text : buttons) {
            RoundedButton button;
            if ("0123456789".contains(text)) {
                button = new RoundedButton(text, 20);
                button.setBackground(new Color(0x43537E)); // number buttons
            } else {
                button = new RoundedButton(text, 30);
                button.setBackground(new Color(0xA4C3E8)); // operator buttons
            }
                button.setFont(new Font("Arial", Font.BOLD, 20));



                button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String cmd = e.getActionCommand();
                    if(cmd.equals("C")) {
                        display.setText("");
                    }
                    else if(cmd.equals("=")) {
                        try {
                            String expr = display.getText();
                            ArrayList<String> tokens = tokenize(expr);
                            ArrayList<String> postfix = infixToPostfix(tokens);
                            double result = evaluatePostfix(postfix);
                            display.setText(Double.toString(result));
                        } catch (Exception er) {
                            display.setText("Error");
                        }
                    }
                    else if(cmd.equals("←")) {
                        String text = display.getText();
                        if(!text.isEmpty()) {
                            display.setText(text.substring(0, text.length()-1));
                        }
                    }
                    else {
                        display.setText(display.getText() + cmd);
                    }
                }
            });
             panel.add(button);
        }

      /**   For consloe logging
       * System.out.println("Calculator");
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter expression");
        String expr = sc.nextLine();
        
        
        ArrayList<String> tokens = tokenize(expr);
        ArrayList<String> postfix = infixToPostfix(tokens); //infix is how people write math and postfix is operands go after the numbers
        double result = evaluatePostfix(postfix);

        System.out.println("Result: " + result);
        
*/
        frame.setLayout(new BorderLayout());
        
        frame.add(panel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.setVisible(true);

    }

}

