import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class MyPanel extends JPanel{

    double[][] points = new double[0][2];
    ArrayList<Integer> solution = new ArrayList<>();
    final int pointSize = 6;
    final int[] rangeMin = {100, 100};
    final int[] rangeMax = {600, 600};

    MyPanel(){
        this.setPreferredSize(new Dimension(800,700));
        setFocusable(true);
        requestFocus();
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        JPanel heuristicsPanel = new JPanel();
        JPanel optimizationPanel = new JPanel();
        JPanel optimalPanel = new JPanel();
        JPanel resultPanel = new JPanel();

        JLabel labelLen = new JLabel();

        JComboBox<String> comboBoxDistro = new JComboBox<>(new String[]{"random", "grid", "normal distro", "cluster"});

        JTextField inputNumberPoints = new JTextField(5);
        JButton buttonCreateP = new JButton("create Points");
        buttonCreateP.addActionListener(e -> {
            generateNewPoints(Integer.parseInt(inputNumberPoints.getText()), (String) Objects.requireNonNull(comboBoxDistro.getSelectedItem()));
            labelLen.setText("");
        });


        JButton buttonBF = new JButton("BF");
        buttonBF.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.bruteForce();
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonHK = new JButton("HK");
        buttonHK.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.tspHeldKarp();
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonNN = new JButton("NN");
        buttonNN.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.nn();
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonRI = new JButton("rand Ins");
        buttonRI.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.randomInsert();
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonCI = new JButton("cheap Ins");
        buttonCI.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.cheapestInsertion();
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonFI = new JButton("far Ins");
        buttonFI.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.farthestInsertTSP();
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonRAND = new JButton("random");
        buttonRAND.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.randomRoute();
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonACO = new JButton("ACO");
        buttonACO.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.aco(0.25,1.5,10.0,0.03,100);
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonLB = new JButton("Lower Bound");
        buttonLB.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            double lowerBound = tsp.lowerBound();
            labelLen.setText(String.valueOf(lowerBound));
        });

        JButton buttonGA = new JButton("GA");
        buttonGA.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.ga(20,200,4,0.05f,0,"swap",true);
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonCHR = new JButton("Chris");
        buttonCHR.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.christofides();

            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button2OPT = new JButton("2Opt");
        button2OPT.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            tsp.Opt2(solution);
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton button3OPT = new JButton("3Opt");
        button3OPT.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            tsp.Opt3(solution);
            System.out.println(solution.toString());
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonLK = new JButton("LK");
        buttonLK.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = tsp.linK();
            System.out.println(solution);
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });

        JButton buttonREAD = new JButton("Read file");
        buttonREAD.addActionListener(e -> {
            System.out.println("Enter a filename:");
            Scanner scanner = new Scanner(System.in);
            readTSPFile(scanner.next());
            repaint();
        });

        JButton buttonCON = new JButton("optimal Solution");
        buttonCON.addActionListener(e -> {
            TSPSolver tsp = new TSPSolver(getLengths());
            solution = Concorde.solve(points);
            labelLen.setText(String.valueOf(tsp.calcLength(solution)));
            repaint();
        });


        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(new JLabel("number of Points:"));
        controlPanel.add(inputNumberPoints);
        controlPanel.add(new JLabel("Distribution:"));
        controlPanel.add(comboBoxDistro);
        controlPanel.add(buttonCreateP);
        controlPanel.add(buttonREAD);

        heuristicsPanel.setLayout(new GridLayout(0, 1));
        heuristicsPanel.setBorder(BorderFactory.createTitledBorder("Heuristics"));
        heuristicsPanel.add(buttonRAND);
        heuristicsPanel.add(buttonBF);
        heuristicsPanel.add(buttonHK);
        heuristicsPanel.add(buttonNN);
        heuristicsPanel.add(buttonRI);
        heuristicsPanel.add(buttonFI);
        heuristicsPanel.add(buttonCI);
        heuristicsPanel.add(buttonCHR);
        heuristicsPanel.add(buttonACO);
        heuristicsPanel.add(buttonGA);
        heuristicsPanel.add(buttonLK);

        optimizationPanel.setLayout(new GridLayout(0, 1));
        optimizationPanel.setBorder(BorderFactory.createTitledBorder("Optimizations"));
        optimizationPanel.add(button2OPT);
        optimizationPanel.add(button3OPT);

        optimalPanel.setLayout(new GridLayout(0, 1));
        optimalPanel.setBorder(BorderFactory.createTitledBorder("Optimal solution"));
        optimalPanel.add(buttonCON);

        resultPanel.setLayout(new FlowLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Result"));
        resultPanel.add(labelLen);

        JPanel solverPanel = new JPanel();
        solverPanel.setLayout(new BoxLayout(solverPanel, BoxLayout.Y_AXIS));
        solverPanel.add(heuristicsPanel);
        solverPanel.add(optimizationPanel);
        solverPanel.add(optimalPanel);

        add(controlPanel, BorderLayout.NORTH);
        add(solverPanel, BorderLayout.EAST);
        add(resultPanel, BorderLayout.SOUTH);
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
        solution = new ArrayList<>();
        ArrayList<double[]> nodeList = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader("tspProblems/"+filePath))) {
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

                if (line.startsWith("EDGE_WEIGHT_TYPE") && !line.contains("EUC_2D")) {
                    throw new IllegalArgumentException("Unsupported "+line);
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
        g2d.drawRect(rangeMin[0],rangeMin[1],rangeMax[0]-rangeMin[0]+pointSize,rangeMax[1]-rangeMin[1]+pointSize);

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

        if(points.length <= 1){
            maxX = 1;
            maxY = 1;
            minX = 0;
            minY = 0;
        }

        g2d.drawString("X", (rangeMax[0]+rangeMin[0])/2, rangeMin[1]-2);
        g2d.drawString("Y", rangeMin[0]-10, (rangeMax[1]+rangeMin[1])/2);

        g2d.drawString(String.format("%.2f", minX), rangeMin[0]+10, rangeMin[1]-3);
        g2d.drawString(String.format("%.2f", maxX), rangeMax[0]-10, rangeMin[1]-3);
        FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
        String s1 = String.format("%.2f", minY);
        String s2 = String.format("%.2f", maxY);
        g2d.drawString(s1, rangeMin[0]-fm.stringWidth(s1), rangeMin[1]+15);
        g2d.drawString(s2, rangeMin[0]-fm.stringWidth(s2), rangeMax[1]-5);

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
