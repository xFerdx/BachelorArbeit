import java.util.*;
import java.util.stream.Collectors;

public class TSPSolver {

    private final double[][] lengths;
    ArrayList<Integer> solution = new ArrayList<>();

    public TSPSolver(double[][] lengths) {
        this.lengths = lengths;
    }

    public double lowerBound(){
        double max = 0;
        for (int i = 1; i < lengths.length; i++) {
            ArrayList<Integer>[] a = MST(i);
            int[] b = find2Mins(lengths[i]);
            double len = calcMSTLen(a,b,i);
            max = Math.max(max, len);
        }

        return max;
    }


    public static int[] find2Mins(double[] arr) {
        int min1Idx = -1;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] <= 0)continue;
            if(min1Idx == -1 || arr[i] < arr[min1Idx]){
                min1Idx = i;
            }
        }

        int min2Idx = -1;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] <= 0 || i == min1Idx)continue;
            if(min2Idx == -1 || arr[i] < arr[min2Idx]){
                min2Idx = i;
            }
        }


        return new int[]{min1Idx, min2Idx};
    }

    private double calcMSTLen(ArrayList<Integer>[] a, int[] b, int z){
        double ret = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].size(); j++) {
                ret += lengths[i][a[i].get(j)];
            }
        }
        ret += lengths[b[0]][z]+lengths[b[1]][z];
        return ret;
    }


    public ArrayList<Integer> bruteForce(){
        ArrayList<Integer> visited = new ArrayList<>();
        visited.add(0);
        ArrayList<Integer> notVisited = new ArrayList<>();
        for (int i = 1; i < lengths.length; i++) {
            notVisited.add(i);
        }
        double[] minVal = {calcLength(randomInsert())};
        bfRec(visited, notVisited, 0, minVal);
        return solution;
    }

    private void bfRec(ArrayList<Integer> visited, ArrayList<Integer> notVisited, double thisLen, double[] currentMin){
        if(thisLen >= currentMin[0])return;
        if(visited.size() == lengths.length){
            visited.add(0);
            if(calcLength(visited)<currentMin[0]){
                currentMin[0] = calcLength(visited);
                solution = visited;
                return;
            }
        }
        for (int i = 0; i < notVisited.size(); i++) {
            ArrayList<Integer> newVisited = new ArrayList<>(visited);
            newVisited.add(notVisited.get(i));
            ArrayList<Integer> newNotVisited = new ArrayList<>(notVisited);
            newNotVisited.remove(i);
            bfRec(newVisited, newNotVisited, thisLen+lengths[notVisited.get(i)][visited.get(visited.size()-1)],currentMin);
        }
    }



    public ArrayList<Integer> tspHeldKarp() {
        int n = lengths.length;
        double[][] dp = new double[n][1 << n];
        int[][] parent = new int[n][1 << n];
        for (double[] row : dp) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
        dp[0][1] = 0;

        for (int mask = 1; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) != 0) {
                    for (int v = 0; v < n; v++) {
                        if ((mask & (1 << v)) != 0 && u != v) {
                            double newDistance = dp[u][mask ^ (1 << v)] + lengths[u][v];
                            if (newDistance < dp[v][mask]) {
                                dp[v][mask] = newDistance;
                                parent[v][mask] = u;
                            }
                        }
                    }
                }
            }
        }

        double minDistance = Double.POSITIVE_INFINITY;
        int minIdx = -1;
        for (int v = 1; v < n; v++) {
            double distance = dp[v][(1 << n) - 1] + lengths[v][0]; // Complete tour
            if (distance < minDistance) {
                minDistance = distance;
                minIdx = v;
            }
        }

        int[] tourIndices = new int[n+1];
        int mask = (1 << n) - 1;
        int idx = minIdx;
        for (int i = n - 1; i >= 0; i--) {
            tourIndices[i] = idx;
            idx = parent[idx][mask];
            mask ^= (1 << tourIndices[i]);
        }

        return Arrays.stream(tourIndices).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Integer> nn(){
        ArrayList<Integer> ret = new ArrayList<>(lengths.length);
        ret.add(0);
        while(ret.size()<lengths.length){
            int lastIdx = ret.get(ret.size()-1);
            int minIdx = -1;
            for (int i = 0; i < lengths.length; i++) {
                if(ret.contains(i))continue;
                if(minIdx == -1 || lengths[lastIdx][i]<lengths[lastIdx][minIdx]){
                    minIdx = i;
                }
            }
            ret.add(minIdx);
        }

        ret.add(0);
        return ret;
    }

    public ArrayList<Integer> randomInsert(){
        int l = lengths.length;
        ArrayList<Integer> ret = new ArrayList<>(l+1);
        ret.add(0);
        ret.add(0);
        for (int i = 1; i < l; i++) {
            double minVal = Float.MAX_VALUE;
            int minIdx = -1;
            for (int j = 1; j <= i; j++) {
                ret.add(j,i);
                double val = lengths[ret.get(j-1)][ret.get(j)]+lengths[ret.get(j)][ret.get(j+1)]-lengths[ret.get(j-1)][ret.get(j+1)];//calcLength(ret);
                ret.remove(j);
                if(val<minVal){
                    minVal = val;
                    minIdx = j;
                }
            }
            ret.add(minIdx,i);
        }

        return ret;
    }

    public ArrayList<Integer> cheapestInsertion() {
        int n = lengths.length;
        ArrayList<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[n];

        int currentCity = 0;
        tour.add(currentCity);
        visited[currentCity] = true;

        while (tour.size() < n) {
            double minIncrease = Double.MAX_VALUE;
            int nextCity = -1;
            int insertPosition = -1;

            // Find the cheapest insertion
            for (int city = 0; city < n; city++) {
                if (!visited[city]) {
                    for (int i = 0; i < tour.size(); i++) {
                        int current = tour.get(i);
                        int next = (i + 1) % tour.size();
                        double increase = lengths[current][city] + lengths[city][tour.get(next)] - lengths[current][tour.get(next)];
                        if (increase < minIncrease) {
                            minIncrease = increase;
                            nextCity = city;
                            insertPosition = next;
                        }
                    }
                }
            }
            tour.add(insertPosition, nextCity);
            visited[nextCity] = true;
        }


        tour.add(0,0);

        return tour;
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


    
    public ArrayList<Integer> opt(ArrayList<Integer> list, int z){
        ArrayDeque<ArrayList<Integer>> permQ = new ArrayDeque<>((int) Math.pow(list.size(),z));
        int l = list.size();

        for (int i = 1; i < l-1; i++) {
            ArrayList<Integer> a = new ArrayList<>();
            a.add(i);
            permQ.add(a);
        }

        while(permQ.peek() != null && permQ.peek().size() < z){
            ArrayList<Integer> a = permQ.pop();
            for (int i = 1; i < l-1; i++) {
                ArrayList<Integer> b = new ArrayList<>(a);
                b.add(i);
                permQ.add(b);
            }
        }

        ArrayList<Integer> minRoute = new ArrayList<>(list);
        double minLen = calcLength(minRoute);

        while(!permQ.isEmpty()){
            ArrayList<Integer> a = permQ.pop();
            ArrayList<Integer> thisRoute = new ArrayList<>(list);

            int temp = thisRoute.get(a.get(0));
            for (int i = 0; i < a.size()-1; i++) {
                thisRoute.set(a.get(i),thisRoute.get(a.get(i+1)));
            }
            thisRoute.set(a.get(a.size()-1),temp);

            double thisLen = calcLength(thisRoute);

            if(thisLen<minLen){
                minRoute = thisRoute;
                minLen = thisLen;
            }



        }

        return minRoute;
    }


    public static List<Integer> greedyTSP(double[][] distances) {
        int n = distances.length;
        boolean[] visited = new boolean[n];
        List<Integer> tour = new ArrayList<>();

        int currentCity = 0;
        tour.add(currentCity);
        visited[currentCity] = true;

        while (tour.size() < n) {
            double minDistance = Double.MAX_VALUE;
            int nextCity = -1;
            for (int i = 0; i < n; i++) {
                if (!visited[i] && i != currentCity) {
                    if (distances[currentCity][i] < minDistance) {
                        minDistance = distances[currentCity][i];
                        nextCity = i;
                    }
                }
            }
            tour.add(nextCity);
            visited[nextCity] = true;
            currentCity = nextCity;
        }

        tour.add(0);

        return tour;
    }


    public ArrayList<Integer>[] MST(Integer without){
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
            graph[nVisMin].add(visMin);
            notVisited.remove((Integer) nVisMin);
            visited.add(nVisMin);

        }

        return graph;
    }


    public ArrayList<Integer> getOddPoints(ArrayList<Integer>[] graph){
        ArrayList<Integer> ret = new ArrayList<>();
        if(graph[0].size() % 2 == 1)ret.add(0);
        for (int i = 1; i < graph.length; i++) {
            if(graph[i].size() % 2 == 1)ret.add(i);
        }

        return ret;
    }


    public ArrayList<int[]> getMinWeightPerfectMatching(ArrayList<Integer> vertices) {
        ArrayList<int[]> minMatching = new ArrayList<>();

        if(vertices.size() % 2 == 1)throw new RuntimeException("not even number");

        while(!vertices.isEmpty()) {
            double minWeight = Double.MAX_VALUE;
            int[] bestPair = new int[2];
            for (int i = 0; i < vertices.size(); i++) {
                for (int j = i + 1; j < vertices.size(); j++) {
                    double weight = lengths[vertices.get(i)][vertices.get(j)];
                    if (weight < minWeight) {
                        minWeight = weight;
                        bestPair[0] = vertices.get(i);
                        bestPair[1] = vertices.get(j);
                    }
                }
            }
            minMatching.add(bestPair);
            vertices.remove((Integer) bestPair[0]);
            vertices.remove((Integer) bestPair[1]);
        }




        return minMatching;
    }


    private ArrayList<Integer> eulerCircuit(ArrayList<Integer>[] graph, ArrayList<Integer> currentRoute, ArrayList<Integer> missing){
        if(missing.isEmpty())
           return currentRoute;

        int currentPoint = currentRoute.get(currentRoute.size()-1);

        if(graph[currentPoint].isEmpty())return null;

        ArrayList<Integer>[][] graphs = new ArrayList[graph[currentPoint].size()][graph.length];
        ArrayList<Integer>[] missings = new ArrayList[graph[currentPoint].size()];
        ArrayList<Integer>[] currentRoutes = new ArrayList[graph[currentPoint].size()];

        for (int i = 0; i < graph[currentPoint].size(); i++) {
            if(i == 0){
                graphs[i] = graph;
                missings[i] = missing;
                currentRoutes[i] = currentRoute;
            }else{
                ArrayList<Integer>[] temp = new ArrayList[graph.length];
                for (int j = 0; j < graph.length; j++) {
                    temp[j] = new ArrayList<>(graph[j]);
                }
                graphs[i] = temp;
                missings[i] = new ArrayList<>(missing);
                currentRoutes[i] = new ArrayList<>(currentRoute);
            }
        }

        for (int i = 0; i < graph[currentPoint].size(); i++) {
            ArrayList<Integer>[] g = graphs[i];
            int nextPoint = graph[currentPoint].get(i);
            currentRoutes[i].add(nextPoint);
            missings[i].remove((Integer) nextPoint);
            g[currentPoint].remove((Integer) nextPoint);
            g[nextPoint].remove((Integer) currentPoint);
            ArrayList<Integer> ret = eulerCircuit(g, currentRoutes[i], missings[i]);
            if(ret != null) return ret;
        }

        return null;
    }

    private void hamiltonCircuit(ArrayList<Integer> eulerCir){
        HashSet<Integer> containing = new HashSet<>();
        for (int i = 0; i < eulerCir.size(); i++) {
            if(!containing.add(eulerCir.get(i))){
                eulerCir.remove(i);
                i--;
            }
        }
        eulerCir.add(0);
    }


    public ArrayList<Integer> christofides() {
        long startTime, endTime;

        // Section 1: Minimum Spanning Tree (MST)
        startTime = System.nanoTime();
        ArrayList<Integer>[] graph = MST(null);
        endTime = System.nanoTime();
        System.out.println("Time for MST: " + (endTime - startTime) + " ns");

        // Section 2: Find odd degree vertices
        startTime = System.nanoTime();
        ArrayList<Integer> oddPoints = getOddPoints(graph);
        endTime = System.nanoTime();
        System.out.println("Time for finding odd degree vertices: " + (endTime - startTime) + " ns");

        // Section 3: Minimum Weight Perfect Matching
        startTime = System.nanoTime();
        ArrayList<int[]> s = getMinWeightPerfectMatching(oddPoints);
        endTime = System.nanoTime();
        System.out.println("Time for Minimum Weight Perfect Matching: " + (endTime - startTime) + " ns");

        // Add the matching edges to the graph
        startTime = System.nanoTime();
        for (int[] e : s) {
            graph[e[0]].add(e[1]);
            graph[e[1]].add(e[0]);
        }
        endTime = System.nanoTime();
        System.out.println("Time for adding matching edges to the graph: " + (endTime - startTime) + " ns");

        // Section 4: Euler Circuit
        startTime = System.nanoTime();
        ArrayList<Integer> startRoute = new ArrayList<>();
        startRoute.add(0);
        ArrayList<Integer> missing = new ArrayList<>();
        for (int i = 0; i < lengths.length; i++) {
            missing.add(i);
        }
        ArrayList<Integer> ec = eulerCircuit(graph, startRoute, missing);
        endTime = System.nanoTime();
        System.out.println("Time for finding Euler Circuit: " + (endTime - startTime) + " ns");

        // Section 5: Hamiltonian Circuit
        startTime = System.nanoTime();
        hamiltonCircuit(ec);
        endTime = System.nanoTime();
        System.out.println("Time for converting to Hamiltonian Circuit: " + (endTime - startTime) + " ns");

        return ec;
    }













}

