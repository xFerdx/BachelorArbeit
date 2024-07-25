import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class concorde {

    public ArrayList<Integer> solve(double[][] points) {
        writeToFile(points);
        System.out.println("write");
        executeScript();
        System.out.println("exe");
        ArrayList<Integer> a =  readFromFile();
        System.out.println("read");
        return a;
    }

    public void writeToFile(double[][] points) {
        String filePath = "src/concord/in.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < points.length; i++) {
                int factor = points[0][0] < 1?1000000:1;
                writer.write((int) (points[i][0] * factor) + ":" + (int) (points[i][1] * factor));
                if (i != points.length - 1) writer.newLine();
            }
        } catch (IOException ignored) {
        }
    }

    public void executeScript() {

        ProcessBuilder processBuilder = new ProcessBuilder("src/concord/test.bat");
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            System.out.println("started");

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }


            int exitCode = process.waitFor();
            System.out.println("ended");
            if (exitCode == 0) {
                System.out.println("Bat file executed successfully.");
            } else {
                System.out.println("Bat file execution failed with code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Integer> readFromFile() {
        ArrayList<Integer> numbers = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File("src/concord/out.txt"))) {
            scanner.useDelimiter("\\s+|\\[|\\]");

            while (scanner.hasNext()) {
                if (scanner.hasNextInt()) {
                    numbers.add(scanner.nextInt());
                } else {
                    scanner.next();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        numbers.add(0);

        return numbers;
    }
}