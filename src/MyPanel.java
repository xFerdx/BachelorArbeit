import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MyPanel extends JPanel{

    int[][] points = new int[0][2];
    ArrayList<Integer> solution = new ArrayList<>();
    final int pointSize = 6;

    MyPanel(){
        this.setPreferredSize(new Dimension(500,500));
        setFocusable(true);
        requestFocus();

        JLabel label = new JLabel();

        JTextField textField = new JTextField(5);
        JButton button = new JButton("create Points");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateNewPoints(Integer.parseInt(textField.getText()));
            }
        });


        JButton button2 = new JButton("BF");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                solution = tsp.bruteForce();
                System.out.println(solution.toString());
                label.setText(String.valueOf(tsp.calcLength(solution)));
                repaint();
            }
        });

        JButton button3 = new JButton("HK");
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                solution = tsp.tspHeldKarp();
                System.out.println(solution.toString());
                label.setText(String.valueOf(tsp.calcLength(solution)));
                repaint();
            }
        });

        JButton button4 = new JButton("NN");
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                solution = tsp.nn();
                System.out.println(solution.toString());
                label.setText(String.valueOf(tsp.calcLength(solution)));
                repaint();
            }
        });

        JButton button5 = new JButton("Rand Ins");
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                solution = tsp.randomInsert();
                System.out.println(solution.toString());
                label.setText(String.valueOf(tsp.calcLength(solution)));
                repaint();
            }
        });

        JButton button6 = new JButton("cheap Ins");
        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                solution = tsp.cheapestInsertion();
                System.out.println(solution.toString());
                label.setText(String.valueOf(tsp.calcLength(solution)));
                repaint();
            }
        });


        JButton button7 = new JButton("rand");
        button7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                solution = tsp.randomRoute();
                System.out.println(solution.toString());
                label.setText(String.valueOf(tsp.calcLength(solution)));
                repaint();
            }
        });

        JButton button8 = new JButton("Ant");
        button8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                ACOAlgorithm ac = new ACOAlgorithm(getLengths(),0.8,1.0,2.0,0.5,1.0);//AntColonyOptimization(getLengths());
                solution = ac.solve(100);
                System.out.println(solution.toString());
                label.setText(String.valueOf(tsp.calcLength(solution)));
                repaint();
            }
        });

        JButton button9 = new JButton("CalcAll");
        button9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 2; i < 20; i++) {
                    float sum = 0;
                    for (int j = 0; j < 100; j++) {
                        generateNewPoints(i);
                        TSPSolver tsp = new TSPSolver(getLengths());
                        solution = tsp.tspHeldKarp();
                        sum += tsp.calcLength(solution);
                    }
                    sum /= 100;
                    System.out.println(i+": "+sum);
                }
            }
        });


        JTextField textField2 = new JTextField(5);
        JButton button10 = new JButton("Opt");
        button10.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                double oldLen = tsp.calcLength(solution);
                double newLen;
                while(true){
                    solution = tsp.opt(solution, Integer.parseInt(textField2.getText()));
                    newLen = tsp.calcLength(solution);
                    if(oldLen==newLen)break;
                    oldLen=newLen;
                }

                System.out.println(solution.toString());


                label.setText(String.valueOf(tsp.calcLength(solution)));
                repaint();
            }
        });

        JButton button11 = new JButton("LB");
        button11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                double lowerBound = tsp.lowerBound();
                label.setText(String.valueOf(Math.round(lowerBound)));
            }
        });

        JButton button12 = new JButton("GA");
        button12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                gen ga = new gen(getLengths());
                solution = (ArrayList<Integer>) ga.optimize();

                System.out.println(solution.toString());
                label.setText(String.valueOf(tsp.calcLength(solution)));
                repaint();
            }
        });

        JButton button13 = new JButton("Chris");
        button13.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TSPSolver tsp = new TSPSolver(getLengths());
                solution = tsp.christofides();

                System.out.println(solution.toString());
                label.setText(String.valueOf(tsp.calcLength(solution)));
                repaint();
            }
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
        add(button9);
        add(textField2);
        add(button10);
        add(button11);
        add(button12);
        add(button13);


    }

    public void generateNewPoints(int number){
        solution = new ArrayList<>();
        points = new int[number][2];
        for (int i = 0; i < number; i++) {
            points[i][0] = (int) (Math.random()*300)+100;
            points[i][1] = (int) (Math.random()*300)+100;
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
            int[] point = points[i];
            g2d.setColor(i==0?Color.GREEN:Color.BLUE);
            g2d.fillOval(point[0], point[1], pointSize, pointSize);
            if(points.length <= 50)g2d.drawString(String.valueOf(i),point[0],point[1]);
        }

        for (int i = 0; i < solution.size()-1; i++) {
            g2d.setColor(Color.BLACK);
            g2d.drawLine(points[solution.get(i)][0]+pointSize/2,points[solution.get(i)][1]+pointSize/2,points[solution.get(i+1)][0]+pointSize/2,points[solution.get(i+1)][1]+pointSize/2);
        }
    }




}
