import java.util.*;

public class gen {
    private final int generationSize;
    private final int genomeSize;
    private final int numberOfCities;
    private final int reproductionSize;
    private final int maxIterations;
    private final float mutationRate;
    private final int tournamentSize;
    private final SelectionType selectionType;
    private final double[][] travelPrices;
    private final int startingCity;
    private final int targetFitness;

    public gen(double[][] travelPrices){
        this.numberOfCities = travelPrices.length;
        this.genomeSize = numberOfCities-1;
        this.selectionType = SelectionType.TOURNAMENT;
        this.travelPrices = travelPrices;
        this.startingCity = 0;
        this.targetFitness = 0;

        generationSize = 500;
        reproductionSize = 200;
        maxIterations = 10000;
        mutationRate = 0.1f;
        tournamentSize = 40;
    }

    public List<SalesmanGenome> initialPopulation(){
        List<SalesmanGenome> population = new ArrayList<>();
        for(int i=0; i<generationSize; i++){
            population.add(new SalesmanGenome(numberOfCities, travelPrices, startingCity));
        }
        return population;
    }

    public List<SalesmanGenome> selection(List<SalesmanGenome> population){
        List<SalesmanGenome> selected = new ArrayList<>();
        for(int i=0; i<reproductionSize; i++){
            if(selectionType == SelectionType.ROULETTE){
                selected.add(rouletteSelection(population));
            }
            else if(selectionType == SelectionType.TOURNAMENT){
                selected.add(tournamentSelection(population));
            }
        }

        return selected;
    }

    public SalesmanGenome rouletteSelection(List<SalesmanGenome> population){
        double totalFitness = population.stream().map(SalesmanGenome::getFitness).mapToDouble(Double::doubleValue).sum();
        Random random = new Random();
        int selectedValue = random.nextInt((int) totalFitness);
        float recValue = (float) 1/selectedValue;
        float currentSum = 0;
        for(SalesmanGenome genome : population){
            currentSum += (float) (1/genome.getFitness());
            if(currentSum>=recValue){
                return genome;
            }
        }
        int selectRandom = random.nextInt(generationSize);
        return population.get(selectRandom);
    }

    public static <E> List<E> pickNRandomElements(List<E> list, int n) {
        Random r = new Random();
        int length = list.size();
        if (length < n) return null;
        for (int i = length - 1; i >= length - n; --i) {
            Collections.swap(list, i , r.nextInt(i + 1));
        }
        return list.subList(length - n, length);
    }

    public SalesmanGenome tournamentSelection(List<SalesmanGenome> population){
        return Collections.min(pickNRandomElements(population,tournamentSize));
    }

    public SalesmanGenome mutate(SalesmanGenome salesman){
        Random random = new Random();
        float mutate = random.nextFloat();
        if(mutate<mutationRate) {
            List<Integer> genome = salesman.getGenome();
            Collections.swap(genome, random.nextInt(genomeSize), random.nextInt(genomeSize));
            return new SalesmanGenome(genome, numberOfCities, travelPrices, startingCity);
        }
        return salesman;
    }

    public List<SalesmanGenome> createGeneration(List<SalesmanGenome> population){
        List<SalesmanGenome> generation = new ArrayList<>();
        int currentGenerationSize = 0;
        while(currentGenerationSize < generationSize){
            List<SalesmanGenome> parents = pickNRandomElements(population,2);
            List<SalesmanGenome> children = crossover(parents);
            children.set(0, mutate(children.get(0)));
            children.set(1, mutate(children.get(1)));
            generation.addAll(children);
            currentGenerationSize+=2;
        }
        return generation;
    }

    public List<SalesmanGenome> crossover(List<SalesmanGenome> parents){
        Random random = new Random();
        int breakpoint = random.nextInt(genomeSize);
        List<SalesmanGenome> children = new ArrayList<>();
        List<Integer> parent1Genome = new ArrayList<>(parents.get(0).getGenome());
        List<Integer> parent2Genome = new ArrayList<>(parents.get(1).getGenome());

        for(int i = 0; i<breakpoint; i++){
            int newVal;
            newVal = parent2Genome.get(i);
            Collections.swap(parent1Genome,parent1Genome.indexOf(newVal),i);
        }
        children.add(new SalesmanGenome(parent1Genome,numberOfCities,travelPrices,startingCity));
        parent1Genome = parents.get(0).getGenome(); // reseting the edited parent

        for(int i = breakpoint; i<genomeSize; i++){
            int newVal = parent1Genome.get(i);
            Collections.swap(parent2Genome,parent2Genome.indexOf(newVal),i);
        }
        children.add(new SalesmanGenome(parent2Genome,numberOfCities,travelPrices,startingCity));

        return children;
    }

    public List<Integer> optimize(){
        List<SalesmanGenome> population = initialPopulation();
        SalesmanGenome globalBestGenome = population.get(0);
        for(int i=0; i<maxIterations; i++){
            List<SalesmanGenome> selected = selection(population);
            population = createGeneration(selected);
            globalBestGenome = Collections.min(population);
            if(globalBestGenome.getFitness() < targetFitness)
                break;
        }
        ArrayList<Integer> ret = (ArrayList<Integer>) globalBestGenome.genome;
        ret.add(startingCity);
        ret.add(0,startingCity);

        return ret;
    }

}

enum SelectionType {
    TOURNAMENT,
    ROULETTE
}

class SalesmanGenome implements Comparable {
    List<Integer> genome;
    double[][] travelPrices;
    int startingCity;
    int numberOfCities;
    double fitness;

    public SalesmanGenome(int numberOfCities, double[][] travelPrices, int startingCity){
        this.travelPrices = travelPrices;
        this.startingCity = startingCity;
        this.numberOfCities = numberOfCities;
        genome = randomSalesman();
        fitness = this.calculateFitness();
    }

    public SalesmanGenome(List<Integer> permutationOfCities, int numberOfCities, double[][] travelPrices, int startingCity){
        genome = permutationOfCities;
        this.travelPrices = travelPrices;
        this.startingCity = startingCity;
        this.numberOfCities = numberOfCities;
        fitness = this.calculateFitness();
    }

    public double calculateFitness(){
        double fitness = 0;
        int currentCity = startingCity;
        for ( int gene : genome) {
            fitness += travelPrices[currentCity][gene];
            currentCity = gene;
        }
        fitness += travelPrices[genome.get(numberOfCities-2)][startingCity];
        return fitness;
    }

    private List<Integer> randomSalesman(){
        List<Integer> result = new ArrayList<>();
        for(int i=0; i<numberOfCities; i++) {
            if(i!=startingCity)
                result.add(i);
        }
        Collections.shuffle(result);
        return result;
    }

    public List<Integer> getGenome() {
        return genome;
    }


    public double getFitness() {
        return fitness;
    }

    @Override
    public int compareTo(Object o) {
        SalesmanGenome genome = (SalesmanGenome) o;
        if(this.fitness > genome.getFitness())
            return 1;
        else if(this.fitness < genome.getFitness())
            return -1;
        else
            return 0;
    }
}