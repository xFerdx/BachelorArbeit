import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MyPanel extends JPanel{

    double[][] points = new double[0][2];
    ArrayList<Integer> solution = new ArrayList<>();
    final int pointSize = 6;
    final int[] range = {300,300};


    MyPanel(){
        this.setPreferredSize(new Dimension(500,500));
        setFocusable(true);
        requestFocus();

        JLabel label = new JLabel();

        JTextField textField = new JTextField(5);
        JButton button = new JButton("create Points");
        button.addActionListener(e -> generateNewPoints(Integer.parseInt(textField.getText())));


        JButton button2 = new JButton("BF");
        button2.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.bruteForce();
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button3 = new JButton("HK");
        button3.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.tspHeldKarp();
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button4 = new JButton("NN");
        button4.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.nn();
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button5 = new JButton("Rand Ins");
        button5.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.randomInsert();
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button6 = new JButton("cheap Ins");
        button6.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.cheapestInsertion();
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });


        JButton button7 = new JButton("rand");
        button7.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.randomRoute();
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button8 = new JButton("Ant");
        button8.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            ACOAlgorithm ac = new ACOAlgorithm(getLengths(),0.8,1.0,2.0,0.5,1.0);//AntColonyOptimization(getLengths());
            solution = ac.solve(100);
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });


        JTextField textField2 = new JTextField(5);
        JButton button10 = new JButton("Swap");
        button10.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            double oldLen = tsp.calcLength(solution);
            double newLen;
            while(true){
                solution = tsp.swap(solution, Integer.parseInt(textField2.getText()));
                newLen = tsp.calcLength(solution);
                if(oldLen==newLen)break;
                oldLen=newLen;
            }

            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button11 = new JButton("LB");
        button11.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            double lowerBound = tsp.lowerBound();
            label.setText(String.valueOf(lowerBound));
        });

        JButton button12 = new JButton("GA");
        button12.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            gen ga = new gen(getLengths());
            solution = (ArrayList<Integer>) ga.optimize();

            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button13 = new JButton("Chris");
        button13.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.christofides();

            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button14 = new JButton("2Opt");
        button14.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.Opt2(solution);
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button15 = new JButton("3Opt");
        button15.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.Opt3(solution);
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button16 = new JButton("far inser");
        button16.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.farthestInsertTSP();
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button17 = new JButton("Or-tools");
        button17.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = OrTools.solve(getLengths(),1,0);
            System.out.println(solution);
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button18 = new JButton("LK");
        button18.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            LinKernighan lk = new LinKernighan(getLengths());
            solution = lk.runAlgorithm();
            System.out.println(solution);
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        add(label);
        add(textField);
        add(button);
        add(button2);
        add(button3);
        add(button4);
        add(button5);
        add(button6);
        add(button7);
        add(button8);
        add(textField2);
        add(button10);
        add(button11);
        add(button12);
        add(button13);
        add(button14);
        add(button15);
        add(button16);
        add(button17);
        add(button18);


    }

    public void generateNewPoints(int number){
        solution = new ArrayList<>();
        points = new double[number][2];
        for (int i = 0; i < number; i++) {
            points[i][0] = Math.random();
            points[i][1] = Math.random();
        }
        getLengths();
        repaint();
    }

    public double[][] getLengths(){
        double[][] lengths = new double[points.length][points.length];
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                lengths[i][j] = (float) Math.sqrt(Math.pow(points[i][0]-points[j][0],2)+Math.pow(points[i][1]-points[j][1],2));
            }
        }
        return lengths;
    }

    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawRect(100,100,300+pointSize,300+pointSize);
        for (int i = 0; i < points.length; i++) {
            double[] point = points[i];
            g2d.setColor(i==0?Color.GREEN:Color.BLUE);
            g2d.fillOval(100 + (int)(point[0] * range[0]), 100 + (int)(point[1] * range[1]), pointSize, pointSize);
            if(points.length <= 50)g2d.drawString(String.valueOf(i),100 + (int)(point[0] * range[0]),100 + (int)(point[1] * range[1]));
        }

        for (int i = 0; i < solution.size()-1; i++) {
            g2d.setColor(Color.BLACK);
            g2d.drawLine(100 + (int) (points[solution.get(i)][0] * range[0]) + pointSize/2,100 + (int) (points[solution.get(i)][1] * range[1]) + pointSize/2,100 + (int) (points[solution.get(i+1)][0] * range[0]) + pointSize/2,100 + (int) (points[solution.get(i+1)][1] * range[1]) + pointSize/2);
        }
    }




}
