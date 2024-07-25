import java.util.*;

public class GA {
    private int generationSize;
    private int genomeSize;
    private int numberOfCities;
    private int reproductionSize;
    private int maxIterations;
    private float mutationRate;
    private int tournamentSize;
    private SelectionType selectionType;
    private int[][] travelPrices;
    private int startingCity;
    private int targetFitness;

    public GA(double[][] travelPrices){
        this.numberOfCities = travelPrices.length;
        this.genomeSize = numberOfCities-1;
        this.selectionType = SelectionType.TOURNAMENT;
        int[][] tp = new int[numberOfCities][numberOfCities];
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                tp[i][j] = (int) (travelPrices[i][j] * 1000000);
            }
        }
        this.travelPrices = tp;
        this.startingCity = 0;
        this.targetFitness = 0;

        generationSize = 1000;
        reproductionSize = 200;
        maxIterations = 500;
        mutationRate = 0.5f;
        tournamentSize = 40;
    }

    public GA(double[][] travelPrices, int generationSize, int reproductionSize, int maxIterations, float mutationRate, int tournamentSize){
        this.numberOfCities = travelPrices.length;
        this.genomeSize = numberOfCities-1;
        this.selectionType = SelectionType.TOURNAMENT;
        int[][] tp = new int[numberOfCities][numberOfCities];
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                tp[i][j] = (int) (travelPrices[i][j] * 1000000);
            }
        }

        this.travelPrices = tp;
        this.startingCity = 0;
        this.targetFitness = 0;

        this.generationSize = generationSize;
        this.reproductionSize = reproductionSize;
        this.maxIterations = maxIterations;
        this.mutationRate = mutationRate;
        this.tournamentSize = tournamentSize;
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

        globalBestGenome.genome.add(0, startingCity);
        globalBestGenome.genome.add(globalBestGenome.genome.size(), startingCity);

        return globalBestGenome.genome;
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
        int totalFitness = population.stream().map(SalesmanGenome::getFitness).mapToInt(Integer::intValue).sum();
        Random random = new Random();
        int selectedValue = random.nextInt(totalFitness);
        float recValue = (float) 1/selectedValue;
        float currentSum = 0;
        for(SalesmanGenome genome : population){
            currentSum += (float) 1/genome.getFitness();
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

        for (int i = length - 1; i >= length - n; --i)
        {
            Collections.swap(list, i , r.nextInt(i + 1));
        }
        return list.subList(length - n, length);
    }

    public SalesmanGenome tournamentSelection(List<SalesmanGenome> population){
        List<SalesmanGenome> selected = pickNRandomElements(population,tournamentSize);
        return Collections.min(selected);
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
        // housekeeping
        Random random = new Random();
        int breakpoint = random.nextInt(genomeSize);
        List<SalesmanGenome> children = new ArrayList<>();

        // copy parental genomes - we copy so we wouldn't modify in case they were
        // chosen to participate in crossover multiple times
        List<Integer> parent1Genome = new ArrayList<>(parents.get(0).getGenome());
        List<Integer> parent2Genome = new ArrayList<>(parents.get(1).getGenome());

        // creating child 1
        for(int i = 0; i<breakpoint; i++){
            int newVal;
            newVal = parent2Genome.get(i);
            Collections.swap(parent1Genome,parent1Genome.indexOf(newVal),i);
        }
        children.add(new SalesmanGenome(parent1Genome,numberOfCities,travelPrices,startingCity));
        parent1Genome = parents.get(0).getGenome(); // reseting the edited parent

        // creating child 2
        for(int i = breakpoint; i<genomeSize; i++){
            int newVal = parent1Genome.get(i);
            Collections.swap(parent2Genome,parent2Genome.indexOf(newVal),i);
        }
        children.add(new SalesmanGenome(parent2Genome,numberOfCities,travelPrices,startingCity));

        return children;
    }

}

enum SelectionType {
    TOURNAMENT,
    ROULETTE
}

class SalesmanGenome implements Comparable {
    List<Integer> genome;
    int[][] travelPrices;
    int startingCity;
    int numberOfCities = 0;
    int fitness;

    public SalesmanGenome(int numberOfCities, int[][] travelPrices, int startingCity){
        this.travelPrices = travelPrices;
        this.startingCity = startingCity;
        this.numberOfCities = numberOfCities;
        genome = randomSalesman();
        fitness = this.calculateFitness();
    }

    public SalesmanGenome(List<Integer> permutationOfCities, int numberOfCities, int[][] travelPrices, int startingCity){
        genome = permutationOfCities;
        this.travelPrices = travelPrices;
        this.startingCity = startingCity;
        this.numberOfCities = numberOfCities;
        fitness = this.calculateFitness();
    }

    public int calculateFitness(){
        int fitness = 0;
        int currentCity = startingCity;
        for ( int gene : genome) {
            fitness += travelPrices[currentCity][gene];
            currentCity = gene;
        }
        fitness += travelPrices[genome.get(numberOfCities-2)][startingCity];
        return fitness;
    }

    private List<Integer> randomSalesman(){
        List<Integer> result = new ArrayList<Integer>();
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

    public int getStartingCity() {
        return startingCity;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path: ");
        sb.append(startingCity);
        for ( int gene: genome ) {
            sb.append(" ");
            sb.append(gene);
        }
        sb.append(" ");
        sb.append(startingCity);
        sb.append("\nLength: ");
        sb.append(this.fitness);
        return sb.toString();
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

