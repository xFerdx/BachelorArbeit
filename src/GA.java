import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GA {

    final double[][] lengths;
    final int generations;
    final int populationsSize;
    final int tournamentSize;
    final double mutationRate;
    final double elitismRate;
    final int size;
    double[] fitness;
    int[][] tours;
    int[] bestTour;
    Random rand = new Random();
    String mutation;
    boolean opt;


    public GA(double[][] lengths){
        this.lengths = lengths;
        this.generations = 1000;
        this.populationsSize = 1000;
        this.tournamentSize = 5;
        this.mutationRate = 0.2f;
        this.elitismRate = 0.05f;
        this.size = lengths.length+1;
    }

    public GA(double[][] lengths, int generations, int populationsSize, int tournamentSize, double mutationRate, double elitismRate, String mutation, boolean opt) {
        this.lengths = lengths;
        this.generations = generations;
        this.populationsSize = populationsSize;
        this.tournamentSize = tournamentSize;
        this.mutationRate = mutationRate;
        this.elitismRate = elitismRate;
        this.size = lengths.length+1;
        this.mutation = mutation;
        this.opt = opt;
    }

    public ArrayList<Integer> solve(){
        fitness = new double[populationsSize];
        tours = new int[populationsSize][size];
        for (int i = 0; i < populationsSize; i++) {
            tours[i] = getRandTour();
        }

        for (int i = 0; i < generations; i++) {
            calcFitness();
            int[] parents = getParents();
            int[][] offspring = crossover(parents);
            addElites(offspring, offspring.length-parents.length);

            if(opt) {
                for (int[] o : offspring) {
                    Opt2(o);
                }
            }

            addMutation(offspring);
            if(bestTour == null || calcTourFitness(tours[findBestIdx(fitness)])>calcTourFitness(bestTour))
                bestTour = tours[findBestIdx(fitness)];
            tours = offspring;
        }


        return IntStream.of(bestTour).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    private int[] getRandTour(){
        int[] tour = new int[size];
        for (int i = 1; i < size - 1; i++) {
            tour[i] = i;
        }

        for (int i = 1; i < size - 1; i++) {
            int randomIndex = rand.nextInt(size-2)+1;
            int temp = tour[i];
            tour[i] = tour[randomIndex];
            tour[randomIndex] = temp;
        }
        return tour;
    }

    private void calcFitness(){
        for (int i = 0; i < populationsSize; i++) {
            fitness[i] = calcTourFitness(tours[i]);
        }
    }

    private double calcTourFitness(int[] tour){
        double f = 0;
        for (int j = 0; j < size-1; j++) {
            f += lengths[tour[j]][tour[j+1]];
        }
        return 1/f;
    }

    private int[] getParents(){
        int parentSize = (int) (populationsSize * (1-elitismRate));
        parentSize -= parentSize%2 == 0?0:1;
        int[] parents = new int[parentSize];
        for (int i = 0; i < parentSize; i++) {
            parents[i] = tournamentSelection();
        }
        return parents;
    }

    private int tournamentSelection(){
        int[] candidates = new int[tournamentSize];
        for (int i = 0; i < tournamentSize; i++) {
            candidates[i] = rand.nextInt(populationsSize);
        }
        int bestFitnessIdx = 0;
        for (int i = 1; i < candidates.length; i++) {
            if(fitness[candidates[i]]>fitness[candidates[bestFitnessIdx]])
                bestFitnessIdx = i;
        }
        return candidates[bestFitnessIdx];
    }

    private int[][] crossover(int[] parents){
        int[][] offspring = new int[populationsSize][size];
        for (int i = 0; i < parents.length; i+=2) {
            int[][] offSpringPair = pmx(new int[]{parents[i],parents[i+1]});
            offspring[i] = offSpringPair[0];
            offspring[i+1] = offSpringPair[1];
        }
        return offspring;
    }

    private int[][] pmx(int[] parentPair){
        int[] parent1 = tours[parentPair[0]];
        int[] parent2 = tours[parentPair[1]];

        int[] child1 = new int[size];
        int[] child2 = new int[size];
        int coPoint1 = rand.nextInt(size/2-1)+1;
        int coPoint2 = rand.nextInt(size-1-size/2)+size/2;

        Arrays.fill(child1,-1);
        Arrays.fill(child2,-1);

        for (int i = coPoint1; i <= coPoint2 ; i++) {
            child1[i] = parent2[i];
            child2[i] = parent1[i];
        }

        for (int i = 1; i < size-1; i++) {
            if(i >= coPoint1 && i <= coPoint2)continue;

            if(!contains(child1,parent1[i])){
                child1[i]=parent1[i];
            }else{
                int val = parent1[i];
                while(contains(child1,val)){
                    val = parent1[idxOf(parent2,val)];
                }
                child1[i] = val;
            }

            if(!contains(child2,parent2[i])){
                child2[i]=parent2[i];
            }else{
                int val = parent2[i];
                while(contains(child2,val)){
                    val = parent2[idxOf(parent1,val)];
                }
                child2[i] = val;
            }
        }

        child1[0] = 0;
        child1[child1.length-1] = 0;
        child2[0] = 0;
        child2[child2.length-1] = 0;
        return new int[][]{child1,child2};
    }


    private boolean contains(int[] array, int value) {
        for (int j : array) {
            if (j == value) {
                return true;
            }
        }
        return false;
    }

    private int idxOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }

        return -1;
    }

    private void addElites(int[][] offspring, int number){
        Integer[] fitnessIdx = new Integer[fitness.length];
        for (int i = 0; i < fitnessIdx.length; i++) {
            fitnessIdx[i] = i;
        }
        Arrays.sort(fitnessIdx, Comparator.comparingDouble(index -> -fitness[index]));
        for (int i = populationsSize-number; i < populationsSize; i++) {
            offspring[i] = tours[fitnessIdx[i]];
        }
    }

    private void addMutation(int[][] offSpring){
        for (int[] ints : offSpring) {
            if (Math.random() > mutationRate) continue;
            int t1 = rand.nextInt(size - 2) + 1;
            int t2 = rand.nextInt(size - 2) + 1;
            switch (mutation){
                case "swap":
                    swap(ints, Math.min(t1, t2), Math.max(t1, t2));
                    break;
                case "disp":
                    displacement(ints, Math.min(t1, t2), Math.max(t1, t2));
                    break;
                case "scramble":
                    scramble(ints, Math.min(t1, t2), Math.max(t1, t2));
                    break;
                default:
                    throw new IllegalArgumentException("no such mutation");
            }
        }
    }

    private static void reverseSegment(int[] array, int start, int end) {
        while (start < end) {
            int temp = array[start];
            array[start] = array[end];
            array[end] = temp;
            start++;
            end--;
        }
    }

    private void swap(int[] array, int start, int end) {
        int temp = array[start];
        array[start] = array[end];
        array[end] = temp;
    }

    private void scramble(int[] array, int start, int end) {
        for (int i = end; i > start; i--) {
            int j = rand.nextInt(i - start + 1) + start;
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private void displacement(int[] tour, int start, int end) {
        ArrayList<Integer> t = IntStream.of(tour).boxed().collect(Collectors.toCollection(ArrayList::new));

        t.remove(t.size()-1);

        ArrayList<Integer> t1 = new ArrayList<>(t.subList(0, start));
        ArrayList<Integer> t2 = new ArrayList<>(t.subList(start, end + 1));
        ArrayList<Integer> t3 = new ArrayList<>(t.subList(end + 1, t.size()));

        ArrayList<Integer> ret = new ArrayList<>(t1.size());
        ret.addAll(t1);
        ret.addAll(t3);
        ret.addAll(t2);
        ret.add(t.get(0));

        for (int i = 0; i < tour.length; i++) {
            tour[i] = ret.get(i);
        }

    }

    private int findBestIdx(double[] arr){
        int bestIdx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[bestIdx]) {
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    private void Opt2(int[] tour) {
        boolean improved;
        do {
            improved = false;
            for (int i = 1; i < tour.length - 2; i++) {
                for (int j = i + 1; j < tour.length - 1; j++) {
                    if (lengths[tour[i-1]][tour[j]] + lengths[tour[i]][tour[j+1]] <
                            lengths[tour[i-1]][tour[i]] + lengths[tour[j]][tour[j+1]]) {
                        reverseSegment(tour,i,j);
                        improved = true;
                        break;
                    }
                }
                if (improved) {
                    break;
                }
            }
        } while (improved);
    }



}
