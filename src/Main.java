import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {

        new MyFrame();
        try {
            return;
        }catch (Exception e){

        }

        GAIter();



    }


    public static void GAIter() throws IOException {
        int l = 100;
        TSPSolver tsp = new TSPSolver(getLen(l));
        int maxIter = 1000;
        int it = 10;
        double[] values = new double[maxIter];

        for (int i = 1; i < maxIter; i++) {
            System.out.println(i);
            for (int j = 0; j < it; j++) {
                values[i] += tsp.calcLength(tsp.gen2(i,500,5,0.1f,0.05f));
            }
            values[i] /= it;
        }

        for (int i = 0; i < maxIter; i++) {
            System.out.println(values[i]);
        }

        writeToExcel(values);
    }

    private static void acoIter() throws IOException {
        int l = 100;
        TSPSolver tsp = new TSPSolver(getLen(l));
        int maxIter = 100;
        int it = 10;
        double[] values = new double[maxIter];

        for (int i = 1; i < maxIter; i++) {
            System.out.println(i);
            for (int j = 0; j < it; j++) {
                values[i] += tsp.calcLength(tsp.aco(0.1,1,5,0.5,i));
            }
            values[i] /= it;
        }

        for (int i = 0; i < maxIter; i++) {
            System.out.println(values[i]);
        }

        writeToExcel(values);
    }

    private static void acoAlphaBeta() throws IOException {
        int l = 100;
        TSPSolver tsp = new TSPSolver(getLen(l));

        double minA = 0;
        double minB = 0;
        double maxA = 10;
        double maxB = 10;
        int aSteps = 10;
        int bSteps = 10;
        int iterations = 10;

        double[][] values = new double[aSteps][bSteps];


        for (int i = 0; i < aSteps; i++) {
            double a = ((double) i /aSteps) * (maxA-minA) + minA;
            for (int j = 0; j < bSteps; j++) {
                double b = ((double) j /bSteps) * (maxB-minB) + minB;



            }
        }
    }

    private static void times() throws IOException {
        TSPSolver tsp = new TSPSolver();
        System.out.println("a");

        int iter = 5000;

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
        algs.add(() -> tsp.linK(tsp.randomRoute()));
        algs.add(() -> tsp.aco(0.1f, 1f, 3f, 0.5f, 100));
        algs.add(() -> tsp.gen2(50, 200, 5, 0.1f, 0.1f));
        algs.add(tsp::orTools);


        tsp.setLengths(getLen(10));
        tsp.orTools();

        boolean[] out = new boolean[algs.size()];
        double[][] times = new double[iter][algs.size()];
        for (int i = 2; i < iter; i++) {
            System.out.println(i);
            tsp.setLengths(getLen(i));
            for (int j = 0; j < algs.size(); j++) {
                if(out[j])continue;
                double t = measureTime(algs.get(j));
                times[i][j] = t;
                if(t > 5000)out[j] = true;
            }
        }

        writeToExcel(times);
    }



    private static double measureTime(Runnable algorithm) {
        long startTime = System.nanoTime();
        algorithm.run();
        return (System.nanoTime() - startTime) / 1_000_000.0;
    }

    public static double[][] getLen(int n){
        double[][] lengths = new double[n][n];
        double[][] points = new double[n][2];
        for (int i = 0; i < n; i++) {
            points[i][0] = Math.random();
            points[i][1] = Math.random();
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                lengths[i][j] = Math.sqrt(Math.pow(points[i][0]-points[j][0],2)+Math.pow(points[i][1]-points[j][1],2));
            }
        }
        return lengths;
    }

    public static void writeToExcel(double[][] times) throws IOException {
        String filePath = "example.xlsx";
        Workbook workbook = new XSSFWorkbook(Files.newInputStream(Paths.get(filePath)));

        Sheet sheet = workbook.getSheetAt(0);

        for (int rowIndex = 0; rowIndex < times.length; rowIndex++) {
            Row row = sheet.createRow(rowIndex);
            for (int colIndex = 0; colIndex < times[0].length; colIndex++) {
                if(times[rowIndex][colIndex] == 0)continue;
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

        for (int rowIndex = 0; rowIndex < times.length; rowIndex++) {
            Row row = sheet.createRow(rowIndex);
                if(times[rowIndex] == 0)continue;
                Cell cell = row.createCell(0);
                cell.setCellValue(times[rowIndex]);
        }

        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        workbook.close();
    }
}