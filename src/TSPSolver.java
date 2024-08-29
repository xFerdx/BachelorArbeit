import lk.LK;
import java.util.*;

public class TSPSolver {

    private double[][] lengths;

    public TSPSolver(double[][] lengths) {
        this.lengths = lengths;
    }

    public TSPSolver(){
    }

    public void setLengths(double[][] lengths) {
        this.lengths = lengths;
    }

    public double lowerBound(){
        LB lb = new LB(lengths);
        return lb.lowerBound();
    }

    public ArrayList<Integer> bruteForce(){
        Exact exact = new Exact(lengths);
        return exact.bruteForce();
    }

    public ArrayList<Integer> heldKarp(){
        Exact exact = new Exact(lengths);
        return exact.heldKarp();
    }

    public ArrayList<Integer> nn(){
        SimpleHeuristics sh = new SimpleHeuristics(lengths);
        return sh.nn();
    }

    public ArrayList<Integer> randomInsertion(){
        SimpleHeuristics sh = new SimpleHeuristics(lengths);
        return sh.randomInsert();
    }

    public ArrayList<Integer> cheapestInsertion(){
        SimpleHeuristics sh = new SimpleHeuristics(lengths);
        return sh.cheapestInsertion();
    }

    public ArrayList<Integer> farthestInsertion(){
        SimpleHeuristics sh = new SimpleHeuristics(lengths);
        return sh.farthestInsertTSP();
    }

    public ArrayList<Integer> randomRoute(){
        int l = lengths.length;
        ArrayList<Integer> route = new ArrayList<>(l+1);
        route.add(0);
        for (int i = 0; i < l-1; i++) {
            ArrayList<Integer> candidates = new ArrayList<>(l);
            for (int j = 0; j < l; j++) {
               if(!route.contains(j))candidates.add(j);
            }
            route.add(candidates.get((int) (Math.random()*candidates.size())));
        }
        route.add(0);
        return route;
    }

    public double calcLength(ArrayList<Integer> l){
        double ret = 0;
        for (int i = 0; i < l.size()-1; i++) {
            ret += lengths[l.get(i)][l.get(i+1)];
        }
        return ret;
    }

    public static double calcLength(ArrayList<Integer>l, double[][] lengths){
        double ret = 0;
        for (int i = 0; i < l.size()-1; i++) {
            ret += lengths[l.get(i)][l.get(i+1)];
        }
        return ret;
    }

    public ArrayList<Integer> christofides(){
        Christofides chr = new Christofides(lengths);
        return chr.solve();
    }

    public ArrayList<Integer> linK(){
        return LK.solve(lengths,1);
    }

    public ArrayList<Integer> aco(double antRate, double alpha, double beta, double evaporationRate, int iterations){
        ACO ac = new ACO(lengths,antRate,alpha,beta,evaporationRate);
        return ac.solve(iterations);
    }

    public ArrayList<Integer> ga(int generations, int populationSize, int tournamentSize, float mutationRate, float elitismRate, String mutation, boolean opt){
        GA g = new GA(lengths, generations, populationSize, tournamentSize, mutationRate, elitismRate, mutation, opt);
        return g.solve();
    }

    public ArrayList<Integer> ga(int generations, int populationSize, int tournamentSize, float mutationRate, float elitismRate){
        GA g = new GA(lengths, generations, populationSize, tournamentSize, mutationRate, elitismRate, "swap", false);
        return g.solve();
    }

    public ArrayList<Integer> ga(){
        GA g = new GA(lengths);
        return g.solve();
    }


    public void Opt2(ArrayList<Integer> tour) {
        Opt opt = new Opt(lengths);
        opt.Opt2(tour);
    }

    public void Opt3(ArrayList<Integer> tour) {
        Opt opt = new Opt(lengths);
        opt.Opt3(tour);
    }

    public ArrayList<Integer>[] MST(Integer without, boolean doubled){
        ArrayList<Integer> visited = new ArrayList<>();
        ArrayList<Integer> notVisited = new ArrayList<>();
        visited.add(0);
        for (int i = 1; i < lengths.length; i++) {
            if(without == null || without != i)notVisited.add(i);
        }

        ArrayList<Integer>[] graph = new ArrayList[lengths.length];
        for (int i = 0; i < graph.length; i++) {
            graph[i] = new ArrayList<>();
        }

        while(!notVisited.isEmpty()){
            int visMin = -1;
            int nVisMin = -1;
            double minDist = Double.MAX_VALUE;
            for (int vis: visited) {
                for (int nVis: notVisited) {
                    double dist = lengths[vis][nVis];
                    if(visMin == -1 || dist < minDist){
                        minDist = dist;
                        visMin = vis;
                        nVisMin = nVis;
                    }
                }
            }
            graph[visMin].add(nVisMin);
            if(doubled)graph[nVisMin].add(visMin);
            notVisited.remove((Integer) nVisMin);
            visited.add(nVisMin);

        }

        return graph;
    }

}

