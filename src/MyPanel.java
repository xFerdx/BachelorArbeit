import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MyPanel extends JPanel{

    double[][] points = new double[0][2];
    ArrayList<Integer> solution = new ArrayList<>();
    final int pointSize = 6;
    final int[] range = {300,300};


    MyPanel(){
        this.setPreferredSize(new Dimension(600,500));
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
            double t1 = System.currentTimeMillis();
            solution = tsp.randomInsert();
            System.out.println("zakakak"+(System.currentTimeMillis()-t1));
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
            solution = tsp.aco(0.2,1.0,3.0,0.5,100);
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
            solution = ga.optimize();

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
            tsp.Opt2(solution);
            System.out.println(solution.toString());
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button15 = new JButton("3Opt");
        button15.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            tsp.Opt3(solution);
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
            solution = tsp.orTools();
            System.out.println(solution);
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button18 = new JButton("LK");
        button18.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.linK();
            System.out.println(solution);
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button19 = new JButton("Read file");
        button19.addActionListener(e -> {
            readTSPFile("TSPFiles/pcb1173.tsp");
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
        add(button19);


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

    public void readTSPFile(String filePath) {
        ArrayList<double[]> nodeList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean startReadingNodes = false;

            while ((line = br.readLine()) != null) {
                if (line.trim().equals("NODE_COORD_SECTION")) {
                    startReadingNodes = true;
                    continue;
                }

                if (line.trim().equals("EOF")) {
                    break;
                }

                if (startReadingNodes) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length == 3) {
                        int nodeId = Integer.parseInt(parts[0]);
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        nodeList.add(new double[]{x, y});
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[][] nodesArray = new double[nodeList.size()][2];
        for (int i = 0; i < nodeList.size(); i++) {
                nodesArray[i] = nodeList.get(i);
        }

        points = nodesArray;
    }

    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawRect(100,100,300+pointSize,300+pointSize);
        double maxX = 0;
        double maxY = 0;
        for (int i = 0; i < points.length; i++) {
            maxX = Math.max(maxX,points[i][0]);
            maxY = Math.max(maxY,points[i][1]);
        }
        for (int i = 0; i < points.length; i++) {
            double[] point = points[i];
            g2d.setColor(i==0?Color.GREEN:Color.BLUE);
            g2d.fillOval(100 + (int)(point[0] * range[0]/maxX), 100 + (int)(point[1] * range[1]/maxY), pointSize, pointSize);
            if(points.length <= 50)g2d.drawString(String.valueOf(i),100 + (int)(point[0] * range[0]/maxX),100 + (int)(point[1] * range[1]/maxY));
        }

        for (int i = 0; i < solution.size()-1; i++) {
            g2d.setColor(Color.BLACK);
            g2d.drawLine(100 + (int) (points[solution.get(i)][0] * range[0]/maxX) + pointSize/2,
                    100 + (int) (points[solution.get(i)][1] * range[1]/maxY) + pointSize/2,
                    100 + (int) (points[solution.get(i+1)][0] * range[0]/maxX) + pointSize/2,
                    100 + (int) (points[solution.get(i+1)][1] * range[1]/maxY) + pointSize/2);
        }
    }




}
