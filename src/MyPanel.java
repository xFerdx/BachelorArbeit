import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MyPanel extends JPanel{

    double[][] points = new double[0][2];
    ArrayList<Integer> solution = new ArrayList<>();
    final int pointSize = 6;
    final int[] rangeMax = {400,400};
    final int[] rangeMin = {100,100};


    MyPanel(){
        this.setPreferredSize(new Dimension(600,500));
        setFocusable(true);
        requestFocus();

        JLabel label = new JLabel();

        JComboBox<String> comboBox = new JComboBox<>(new String[]{"random", "grid", "normal distro", "cluster"});

        JTextField textField = new JTextField(5);
        JButton button = new JButton("create Points");
        button.addActionListener(e ->
                generateNewPoints(Integer.parseInt(textField.getText()),(String) Objects.requireNonNull(comboBox.getSelectedItem())));


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
            solution = tsp.aco(0.25,1.5,10.0,0.03,100);
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
            solution = tsp.ga(1000,1000,5,0.2f,0.05f);
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
            readTSPFile("C:/Users/Konrad/Documents/Informatik/qa194.tsp");
            repaint();
        });

        JButton button20 = new JButton("con");
        button20.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = Concorde.solve(points);
            label.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        add(label);
        add(comboBox);
        add(textField);
        add(button);
        add(button2);
        add(button3);
        add(button4);
        add(button5);
        add(button6);
        add(button7);
        add(button8);
        add(button11);
        add(button12);
        add(button13);
        add(button14);
        add(button15);
        add(button16);
        add(button17);
        add(button18);
        add(button19);
        add(button20);


    }

    public void generateNewPoints(int number, String type){
        solution = new ArrayList<>();
        switch (type){
            case "random":
                points = getRandomPoints(number);
                break;
            case "grid":
                points = getGridPoints(number);
                break;
            case "normal distro":
                points = getNormalDistributedPoints(number);
                break;
            case "cluster":
                points = getClusteredPoints(number);
                break;
            default:
                System.out.println("no valid type");
        }

        for (double[] point : points) {
            System.out.println(point[0] + " " + point[1]);
        }

        getLengths();
        repaint();
    }

    public static double[][] getRandomPoints(int number){
        double[][] p = new double[number][2];
        for (int i = 0; i < number; i++) {
            p[i][0] = Math.random();
            p[i][1] = Math.random();
        }
        return p;
    }

    public static double[][] getGridPoints(int number) {
        int grids = (int) Math.sqrt(number);
        double[][] p = new double[number][2];

        for (int i = 0; i < number; i++) {
            p[i][0] = (double) ((int) (Math.random() * grids)) /grids;
            p[i][1] = (double) ((int) (Math.random() * grids)) /grids;
        }
        return p;
    }

    public static double[][] getClusteredPoints(int number) {
        Random rand = new Random();
        double[][] points = new double[number][2];
        double clusterRadius = 0.05;
        int numClusters = 10;

        for (int cluster = 0; cluster < numClusters; cluster++) {
            double clusterCenterX = clusterRadius + (1 - 2 * clusterRadius) * rand.nextDouble();
            double clusterCenterY = clusterRadius + (1 - 2 * clusterRadius) * rand.nextDouble();

            int pointsPerCluster = number / numClusters;

            for (int i = 0; i < pointsPerCluster; i++) {
                double angle = rand.nextDouble() * 2 * Math.PI;
                double radius = rand.nextDouble() * clusterRadius;
                double x = clusterCenterX + radius * Math.cos(angle);
                double y = clusterCenterY + radius * Math.sin(angle);

                points[cluster * pointsPerCluster + i][0] = Math.min(Math.max(x, 0), 1);
                points[cluster * pointsPerCluster + i][1] = Math.min(Math.max(y, 0), 1);
            }
        }

        for (int i = number - (number % numClusters); i < number; i++) {
            points[i][0] = rand.nextDouble();
            points[i][1] = rand.nextDouble();
        }

        return points;
    }

    public static double[][] getNormalDistributedPoints(int numPoints) {
        Random rand = new Random();
        double[][] points = new double[numPoints][2];

        for (int i = 0; i < numPoints; i++) {
            points[i][0] = 0.5 + 0.1*rand.nextGaussian();
            points[i][1] = 0.5 + 0.1*rand.nextGaussian();
        }

        return points;
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
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        for (double[] point : points) {
            maxX = Math.max(maxX, point[0]);
            maxY = Math.max(maxY, point[1]);
            minX = Math.min(minX, point[0]);
            minY = Math.min(minY, point[1]);
        }
        for (int i = 0; i < points.length; i++) {
            g2d.setColor(i==0?Color.GREEN:Color.BLUE);
            g2d.fillOval((int) ((points[i][0]-minX)*(rangeMax[0]-rangeMin[0])/(maxX-minX)+rangeMin[0]),
                    (int) ((points[i][1]-minY)*(rangeMax[1]-rangeMin[1])/(maxY-minY)+rangeMin[1]),
                    pointSize, pointSize);
            if(points.length <= 50)g2d.drawString(
                    String.valueOf(i),
                    (int) ((points[i][0]-minX)*(rangeMax[0]-rangeMin[0])/(maxX-minX)+rangeMin[0]),
                    (int) ((points[i][1]-minY)*(rangeMax[1]-rangeMin[1])/(maxY-minY)+rangeMin[1]));
        }

        for (int i = 0; i < solution.size()-1; i++) {
            g2d.setColor(Color.BLACK);
            g2d.drawLine(
                    (int) ((points[solution.get(i)][0]-minX)*(rangeMax[0]-rangeMin[0])/(maxX-minX)+rangeMin[0]+pointSize/2),
                    (int) ((points[solution.get(i)][1]-minY)*(rangeMax[1]-rangeMin[1])/(maxY-minY)+rangeMin[1]+pointSize/2),
                    (int) ((points[solution.get(i+1)][0]-minX)*(rangeMax[0]-rangeMin[0])/(maxX-minX)+rangeMin[0]+pointSize/2),
                    (int) ((points[solution.get(i+1)][1]-minY)*(rangeMax[1]-rangeMin[1])/(maxY-minY)+rangeMin[1]+pointSize/2)
            );
        }
    }




}
