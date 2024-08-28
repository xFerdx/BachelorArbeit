import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Main {
    public static void main(String[] args) throws Exception {

        new MyFrame();

    }

    private static void HGAMR() throws IOException {
        double[][] values = new double[1001][3];

        for (int j = 0; j < 5; j++) {
            double[][] points = getPoints(100);
            double[][] lengths = getLen(points);
            TSPSolver tsp = new TSPSolver(lengths);
            double optimal = TSPSolver.calcLength(Concorde.solve(points), lengths);
            for (int i = 0; i <= 1000; i++) {
                double currentLen = tsp.calcLength(tsp.ga(100, 100, 5, i / 1000f, 0.05f, "swap", true));
                values[i][0] += (currentLen / optimal)/5;
                currentLen = tsp.calcLength(tsp.ga(100, 100, 5, i / 1000f, 0.05f, "disp", true));
                values[i][1] += (currentLen / optimal)/5;
                currentLen = tsp.calcLength(tsp.ga(100, 100, 5, i / 1000f, 0.05f, "scramble", true));
                values[i][2] += (currentLen / optimal)/5;
                System.out.println(j+" "+i);
            }
        }


        writeToExcel(values);
    }

    private static void HGATS() throws IOException {

        double[] values = new double[1001];

        for (int j = 0; j < 20; j++) {
            double[][] points = getPoints(100);
            double[][] lengths = getLen(points);
            TSPSolver tsp = new TSPSolver(lengths);
            double optimal = TSPSolver.calcLength(Concorde.solve(points), lengths);
            for (int i = 1; i <= 100; i++) {
                double currentLen = tsp.calcLength(tsp.ga(20, 200, i, 0.05f, 0.05f, "swap", true));
                values[i] += (currentLen / optimal)/5;
                System.out.println(j+" "+i);
            }
        }


        writeToExcel(values);
    }

    private static void HGAER() throws IOException {
        double[] values = new double[1001];

        for (int j = 0; j < 5; j++) {
            double[][] points = getPoints(100);
            double[][] lengths = getLen(points);
            TSPSolver tsp = new TSPSolver(lengths);
            double optimal = TSPSolver.calcLength(Concorde.solve(points), lengths);
            for (int i = 0; i <= 1000; i++) {
                double currentLen = tsp.calcLength(tsp.ga(20, 200, 4, 0.05f, i/1000f, "swap", true));
                values[i] += (currentLen / optimal)/5;
                System.out.println(j+" "+i);
            }
        }

        writeToExcel(values);
    }

    private static void HGAPS() throws IOException {
        double[] values = new double[401];

        for (int j = 0; j < 5; j++) {
            double[][] points = getPoints(100);
            double[][] lengths = getLen(points);
            TSPSolver tsp = new TSPSolver(lengths);
            double optimal = TSPSolver.calcLength(Concorde.solve(points), lengths);
            for (int i = 1; i <= 400; i++) {
                double currentLen = tsp.calcLength(tsp.ga(20, i, 5, 0.05f, 0.05f, "swap", true));
                values[i] += (currentLen / optimal)/5;
                System.out.println(j+" "+i);
            }
        }

        writeToExcel(values);
    }

    private static void HGAGen() throws IOException {
        double[] values = new double[401];

        for (int j = 0; j < 5; j++) {
            double[][] points = getPoints(200);
            double[][] lengths = getLen(points);
            TSPSolver tsp = new TSPSolver(lengths);
            double optimal = TSPSolver.calcLength(Concorde.solve(points), lengths);
            for (int i = 1; i <= 400; i++) {
                double currentLen = tsp.calcLength(tsp.ga(i, 100, 5, 0.05f, 0.05f, "swap", true));
                values[i] += (currentLen / optimal)/5;
                System.out.println(j+" "+i);
            }
        }

        writeToExcel(values);
    }

    private static void HGAvsGA() throws IOException {
        double[][] values = new double[5][2];

        int[] p = {10,50,100,200};

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < p.length; i++) {
                double[][] points = getPoints(p[i]);
                double[][] lengths = getLen(points);
                TSPSolver tsp = new TSPSolver(lengths);
                double optimal = TSPSolver.calcLength(Concorde.solve(points), lengths);
                double currentLen = tsp.calcLength(tsp.ga(100, 100, 5, 0.2f, 0.05f, "swap",true));
                values[i][0] += (currentLen / optimal)/5;
                currentLen = tsp.calcLength(tsp.ga(100, 100, 5, 0.2f, 0.05f, "swap",false));
                values[i][1] += (currentLen / optimal)/5;
            }

        }

        writeToExcel(values);
    }

    private static void reduceExcelSize(String excelFilePath) {
        int maxOverride = 100 * 1024 * 1024;
        org.apache.poi.util.IOUtils.setByteArrayMaxOverride(maxOverride);
        try (FileInputStream inputStream = new FileInputStream(excelFilePath);
             Workbook inputWorkbook = new XSSFWorkbook(inputStream);
             FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
            Sheet inputSheet = inputWorkbook.getSheetAt(0);
            Sheet outputSheet = inputWorkbook.createSheet("Reduced");

            int rowCounter = 0;
            for (Row row : inputSheet) {
                if (rowCounter % 100 == 0) {
                    outputSheet.createRow(rowCounter / 100).createCell(0).setCellValue(row.getCell(0).getNumericCellValue());
                }
                rowCounter++;
            }

            inputWorkbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private static void genTournamentSize() throws IOException {
        double[][] points = getPoints(100);
        double[][] lengths = getLen(points);
        TSPSolver tsp = new TSPSolver(lengths);
        double optimal = TSPSolver.calcLength(Concorde.solve(points), lengths);

        double[] values = new double[100];

        for (int i = 1; i < 100; i++) {
            double currentLen = tsp.calcLength(tsp.ga(1000, 1000, i, 0.5f, 0.05f));
            values[i] = currentLen / optimal;
            System.out.println(i);
        }

        writeToExcel(values);
    }

    private static void genMR() throws IOException {

        double[][] points = getPoints(100);
        double[][] lengths = getLen(points);
        TSPSolver tsp = new TSPSolver(lengths);
        double optimal = TSPSolver.calcLength(Concorde.solve(points), lengths);

        double[] values = new double[1001];

        for (int i = 0; i <= 1000; i++) {
            double currentLen = tsp.calcLength(tsp.ga(1000, 1000, 5, i / 1000f, 0.05f));
            values[i] = currentLen / optimal;
            System.out.println(i);
        }

        writeToExcel(values);
    }

    private static void acoAlphaBeta() throws IOException {
        int l = 100;

        double minA = 0;
        double minB = 0;
        double maxA = 10;
        double maxB = 20;
        int aSteps = 50;
        int bSteps = 50;
        int iForProb = 5;
        int probs = 5;

        double[][] values = new double[aSteps][bSteps];


        for (int p1 = 0; p1 < probs; p1++) {
            double[][] p = getPoints(l);
            double[][] len = getLen(p);
            TSPSolver tsp = new TSPSolver(len);
            double optimal = tsp.calcLength(Concorde.solve(p));
            for (int p2 = 0; p2 < iForProb; p2++) {
                for (int i = 0; i < aSteps; i++) {
                    double a = ((double) i / aSteps) * (maxA - minA) + minA;
                    for (int j = 0; j < bSteps; j++) {
                        double b = ((double) j / bSteps) * (maxB - minB) + minB;
                        double currentLen = tsp.calcLength(tsp.aco(0.1, a, b, 0.1, 50));
                        values[i][j] += (currentLen / optimal) / (probs * iForProb);
                    }
                    System.out.println(p1 + " " + p2 + " " + i);
                }
            }
        }

        writeToExcel(values);
    }


    private static void acoAnts() throws IOException {
        int iForProb = 5;
        int probs = 5;

        double[][] values = new double[100][100];

        for (int p1 = 0; p1 < probs; p1++) {
            for (int i = 4; i < 100; i += 2) {
                double[][] p = getPoints(i);
                double[][] len = getLen(p);
                TSPSolver tsp = new TSPSolver(len);
                double optimal;
                if (i < 22)
                    optimal = tsp.calcLength(tsp.tspHeldKarp());
                else
                    optimal = tsp.calcLength(Concorde.solve(p));

                for (int p2 = 0; p2 < iForProb; p2++) {
                    for (int j = 1; j < 100; j += 2) {
                        double currentLen = tsp.calcLength(tsp.aco(j, 1.5, 10, 0.1, 50));
                        values[i / 2][j / 2] += (currentLen / optimal) / (probs * iForProb);
                    }
                    System.out.println(p1 + " " + i + " " + p2);
                }

            }
        }

        writeToExcel(values);
    }


    private static void acoIterations() throws IOException {

        double[] values = new double[400];

        for (int i = 0; i < 5; i++) {
            double[][] p = getPoints(100);
            double[][] len = getLen(p);

            TSPSolver tsp = new TSPSolver(len);
            double optimal = tsp.calcLength(Concorde.solve(p));
            for (int j = 1; j < 400; j++) {
                double currentLen = tsp.calcLength(tsp.aco(0.25, 1.5, 10, 0.1, j));
                values[j] += (currentLen / optimal) / (5);
                System.out.println(i + " " + j);
            }

        }
        writeToExcel(values);

    }


    private static void acoER() throws IOException {

        double[] values = new double[1001];

        for (int i = 0; i < 5; i++) {
            double[][] p = getPoints(100);
            double[][] len = getLen(p);

            TSPSolver tsp = new TSPSolver(len);
            double optimal = tsp.calcLength(Concorde.solve(p));
            for (int j = 0; j <= 1000; j++) {
                double currentLen = tsp.calcLength(tsp.aco(0.25, 1.5, 10, j / 1000d, 100));
                values[j] += (currentLen / optimal) / (5);
                System.out.println(i + " " + j);
            }
        }

        writeToExcel(values);

    }

    private static void acoERMoreIts() throws IOException {

        double[] values = new double[1001];

        for (int i = 0; i < 5; i++) {
            double[][] p = getPoints(100);
            double[][] len = getLen(p);

            TSPSolver tsp = new TSPSolver(len);
            double optimal = tsp.calcLength(Concorde.solve(p));
            for (int j = 0; j <= 1000; j++) {
                double currentLen = tsp.calcLength(tsp.aco(0.25, 1.5, 10, j / 1000d, 1000));
                values[j] += (currentLen / optimal) / (5);
                System.out.println(i + " " + j);
            }
        }

        writeToExcel(values);

    }

    private static void multiple100() throws Exception {
        TSPSolver tsp = new TSPSolver();
        int iter = 100;

        ArrayList<Callable<ArrayList<Integer>>> algs = new ArrayList<>();

        algs.add(tsp::nn);
        algs.add(tsp::cheapestInsertion);
        algs.add(tsp::farthestInsertTSP);
        algs.add(tsp::randomInsert);
        algs.add(tsp::christofides);
        algs.add(() -> tsp.aco(0.25f, 1.5f, 10f, 0.03f, 100));
        algs.add(() -> tsp.ga(1000, 1000, 5, 0.2f, 0.05f));
        algs.add(tsp::linK);


        double[][] times = new double[iter][algs.size()];
        for (int i = 0; i < iter; i++) {
            System.out.println(i);
            double[][] points = getPoints(100);
            tsp.setLengths(getLen(points));
            double optimal = tsp.calcLength(Concorde.solve(points));
            System.out.println(i);
            for (int j = 0; j < algs.size(); j++) {
                double currentSolution = tsp.calcLength(algs.get(j).call());
                times[i][j] = currentSolution / optimal;
            }
        }

        writeToExcel(times);
    }

    private static void multiple100PlusOpt() throws Exception {
        TSPSolver tsp = new TSPSolver();
        int iter = 100;

        ArrayList<Callable<ArrayList<Integer>>> algs = new ArrayList<>();

//        algs.add(tsp::randomRoute);
//        algs.add(tsp::nn);
//        algs.add(tsp::cheapestInsertion);
//        algs.add(tsp::farthestInsertTSP);
//        algs.add(tsp::randomInsert);
//        algs.add(tsp::christofides);
//        algs.add(() -> tsp.aco(0.25f, 1.5f, 10f, 0.03f, 100));
        algs.add(() -> tsp.ga(20, 200, 4, 0.05f, 0f,"swap",true));
//        algs.add(tsp::linK);


        double[][] times = new double[iter][algs.size() * 3];
        for (int i = 0; i < iter; i++) {
            System.out.println(i);
            double[][] points = getPoints(100);
            tsp.setLengths(getLen(points));
            double optimal = tsp.calcLength(Concorde.solve(points));
            System.out.println(i);
            for (int j = 0; j < algs.size(); j++) {
                ArrayList<Integer> currentRoute = algs.get(j).call();
                double currentSolution = tsp.calcLength(currentRoute);
                tsp.Opt2(currentRoute);
                double opt2 = tsp.calcLength(currentRoute);
                tsp.Opt3(currentRoute);
                double opt3 = tsp.calcLength(currentRoute);
                times[i][j * 3] = currentSolution / optimal;
                times[i][j * 3 + 1] = opt2 / optimal;
                times[i][j * 3 + 2] = opt3 / optimal;
            }
        }

        writeToExcel(times);
    }


    private static void qualityForSize() throws Exception {
        TSPSolver tsp = new TSPSolver();
        int iter = 200;

        ArrayList<Callable<ArrayList<Integer>>> algs = new ArrayList<>();
//        algs.add(tsp::nn);
//        algs.add(tsp::cheapestInsertion);
//        algs.add(tsp::farthestInsertTSP);
//        algs.add(tsp::randomInsert);
//        algs.add(tsp::christofides);
//        algs.add(() -> tsp.aco(0.25f, 1.5f, 10f, 0.03f, 100));
        algs.add(() -> tsp.ga(20, 200, 4, 0.05f, 0f,"swap",true));
//        algs.add(tsp::linK);


        double[][] times = new double[iter][algs.size()];
//        double[][] times = new double[iter][2];
        for (int i = 3; i < iter; i++) {
            int its = 10;
            for (int j = 0; j < its; j++) {
                System.out.println(i);
                double[][] points = getPoints(i);
                tsp.setLengths(getLen(points));
                double optimal;
                if (i < 22)
                    optimal = tsp.calcLength(tsp.tspHeldKarp());
                else
                    optimal = tsp.calcLength(Concorde.solve(points));
                for (int k = 0; k < algs.size(); k++) {
                    double currentLen = tsp.calcLength(algs.get(k).call());
                    times[i][k] += (currentLen / optimal) / its;
                }
//                ArrayList<Integer> Route = tsp.randomRoute();
//                tsp.Opt2(Route);
//                double currentLen = tsp.calcLength(Route);
//                times[i][0] += (currentLen / optimal) / its;
//                Route = tsp.randomRoute();
//                tsp.Opt3(Route);
//                currentLen = tsp.calcLength(Route);
//                times[i][1] += (currentLen / optimal) / its;
            }
        }

        writeToExcel(times);
    }


    private static void perfectsForSize() throws Exception {
        TSPSolver tsp = new TSPSolver();
        int iter = 100;

        ArrayList<Callable<ArrayList<Integer>>> algs = new ArrayList<>();
//        algs.add(tsp::randomRoute);
//        algs.add(tsp::randomRoute);
//        algs.add(tsp::nn);
//        algs.add(tsp::cheapestInsertion);
//        algs.add(tsp::farthestInsertTSP);
//        algs.add(tsp::randomInsert);
//        algs.add(tsp::christofides);
//        algs.add(() -> tsp.aco(0.25f, 1.5f, 10f, 0.03f, 100));
        algs.add(() -> tsp.ga(20, 200, 4, 0.05f, 0f,"swap",true));
//        algs.add(tsp::linK);

        double[][] times = new double[iter][algs.size()];
        for (int i = 3; i < iter; i++) {
            int its = 10;
            for (int j = 0; j < its; j++) {
                System.out.println("lasd"+ i);
                double[][] points = getPoints(i);
                tsp.setLengths(getLen(points));
                double optimal;
                if (i < 22)
                    optimal = tsp.calcLength(tsp.tspHeldKarp());
                else
                    optimal = tsp.calcLength(Concorde.solve(points));
                for (int k = 0; k < algs.size(); k++) {
                    ArrayList<Integer> c = algs.get(k).call();
                    if(k == 0)tsp.Opt2(c);
                    else if(k == 1)tsp.Opt3(c);
                    double currentLen = tsp.calcLength(c);
                    times[i][k] += (currentLen / optimal) == 1 ? 1:0;
                }
            }
        }

        writeToExcel(times);
    }


    private static void memory() throws IOException {

        TSPSolver tsp = new TSPSolver();
        int iter = 200;

        ArrayList<Runnable> algs = new ArrayList<>();
        algs.add(tsp::bruteForce);
        algs.add(tsp::tspHeldKarp);
        algs.add(tsp::nn);
        algs.add(tsp::cheapestInsertion);
        algs.add(tsp::farthestInsertTSP);
        algs.add(tsp::randomInsert);
        algs.add(tsp::christofides);
        algs.add(() -> tsp.Opt2(tsp.randomRoute()));
        algs.add(() -> tsp.Opt3(tsp.randomRoute()));
        algs.add(() -> tsp.aco(0.25f, 1.5f, 10f, 0.03f, 100));
        algs.add(() -> tsp.ga(1000, 1000, 5, 0.2f, 0.05f));
        algs.add(tsp::linK);

        boolean[] out = new boolean[algs.size()];
        double[][] times = new double[iter][algs.size()];
        for (int i = 3; i < iter; i++) {
            System.out.println(i);
            tsp.setLengths(getLen(getPoints(i)));
            for (int j = 0; j < algs.size(); j++) {
                if (out[j]) continue;

                double t = measureTime(algs.get(j));
                long m = measureMemory(algs.get(j));

                times[i][j] = m;
                if (t > 1000) out[j] = true;
            }
        }

        writeToExcel(times);
    }


    private static void differentPointDistribution() throws Exception {
        ArrayList<Callable<ArrayList<Integer>>> algs = new ArrayList<>();
        TSPSolver tsp = new TSPSolver();
//        algs.add(tsp::randomRoute);
//        algs.add(tsp::randomRoute);
//        algs.add(tsp::nn);
//        algs.add(tsp::cheapestInsertion);
//        algs.add(tsp::farthestInsertTSP);
//        algs.add(tsp::randomInsert);
//        algs.add(tsp::christofides);
//        algs.add(() -> tsp.aco(0.25f, 1.5f, 10f, 0.03f, 100));
        algs.add(() -> tsp.ga(20, 200, 4, 0.05f, 0f,"swap",true));
//        algs.add(tsp::linK);

        int iter = 100;

        double[][] values = new double[(iter+1)*4][algs.size()];



        for (int i = 0; i < iter; i++) {
            for (int j = 0; j < 4; j++) {
                double[][] p;
                switch (j){
                    case 0: p = MyPanel.getRandomPoints(100);break;
                    case 1: p = MyPanel.getNormalDistributedPoints(100);break;
                    case 2: p = MyPanel.getGridPoints(100);break;
                    case 3: p = MyPanel.getClusteredPoints(100);break;
                    default: throw new IllegalStateException("Unexpected value: " + j);
                }
                tsp.setLengths(getLen(p));
                double optimal = tsp.calcLength(Concorde.solve(p));
                for (int k = 0; k < algs.size(); k++) {
                    ArrayList<Integer> currentRoute = algs.get(k).call();
//                    if(k == 0)tsp.Opt2(currentRoute);
//                    else if (k == 1)tsp.Opt3(currentRoute);

                    values[j*(iter+1)+i][k] = tsp.calcLength(currentRoute)/optimal;

                }
            }
            System.out.println(i);
        }

        writeToExcel(values);
    }

    private static void times() throws IOException {
        TSPSolver tsp = new TSPSolver();
        int iter = 5000;

        ArrayList<Runnable> algs = new ArrayList<>();
//        algs.add(tsp::bruteForce);
//        algs.add(tsp::tspHeldKarp);
//        algs.add(tsp::nn);
//        algs.add(tsp::cheapestInsertion);
//        algs.add(tsp::farthestInsertTSP);
//        algs.add(tsp::randomInsert);
//        algs.add(tsp::christofides);
//        algs.add(() -> tsp.aco(0.25f, 1.5f, 10f, 0.03f, 100));
        algs.add(() -> tsp.ga(20, 200, 4, 0.05f, 0f,"swap",true));
//        algs.add(tsp::linK);
//        algs.add(() -> tsp.Opt2(tsp.nn()));
//        algs.add(() -> tsp.Opt3(tsp.nn()));



        boolean[] out = new boolean[algs.size()];
        double[][] times = new double[iter][algs.size()];
        for (int i = 3; i < iter; i++) {
            System.out.println(i);
            tsp.setLengths(getLen(getPoints(i)));
            for (int j = 0; j < algs.size(); j++) {
                if (out[j]) continue;
                double t = measureTime(algs.get(j));
                times[i][j] = t;
                if (t > 5000) out[j] = true;
            }
        }

        writeToExcel(times);
    }

    private static double measureTime(Runnable algorithm) {
        long startTime = System.nanoTime();
        algorithm.run();
        return (System.nanoTime() - startTime) / 1_000_000.0;
    }

    public static long measureMemory(Runnable task) {
        task.run();
        System.gc();
        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        task.run();
        System.gc();
        long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return afterUsedMem - beforeUsedMem;
    }

    public static double[][] getPoints(int n) {
        double[][] points = new double[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = Math.random();
            points[i][1] = Math.random();
        }
        return points;
    }

    public static double[][] getLen(double[][] points) {
        int n = points.length;
        double[][] lengths = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                lengths[i][j] = Math.sqrt(Math.pow(points[i][0] - points[j][0], 2) + Math.pow(points[i][1] - points[j][1], 2));
            }
        }
        return lengths;
    }

    public static void writeToExcel(double[][] times) throws IOException {
        String filePath = "example.xlsx";
        Workbook workbook = new XSSFWorkbook(Files.newInputStream(Paths.get(filePath)));

        Sheet sheet = workbook.getSheetAt(0);

        clearSheet(sheet);

        for (int rowIndex = 0; rowIndex < times.length; rowIndex++) {
            Row row = sheet.createRow(rowIndex);
            for (int colIndex = 0; colIndex < times[0].length; colIndex++) {
                if (times[rowIndex][colIndex] == 0) continue;
                Cell cell = row.createCell(colIndex);
                cell.setCellValue(times[rowIndex][colIndex]);
            }
        }

        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        workbook.close();
    }

    public static void writeToExcel(double[] times) throws IOException {
        String filePath = "example.xlsx";
        Workbook workbook = new XSSFWorkbook(Files.newInputStream(Paths.get(filePath)));

        Sheet sheet = workbook.getSheetAt(0);

        clearSheet(sheet);

        for (int rowIndex = 0; rowIndex < times.length; rowIndex++) {
            Row row = sheet.createRow(rowIndex);
            if (times[rowIndex] == 0) continue;
            Cell cell = row.createCell(0);
            cell.setCellValue(times[rowIndex]);
        }

        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        workbook.close();
    }


    public static void clearSheet(Sheet sheet) {
        for (int i = sheet.getLastRowNum(); i >= 0; i--) {
            Row row = sheet.getRow(i);
            if (row != null) {
                sheet.removeRow(row);
            }
        }
    }
}