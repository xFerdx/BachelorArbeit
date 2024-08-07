import lkh.LK;

import java.util.*;
import java.util.stream.Collectors;

public class TSPSolver {

    private double[][] lengths;
    ArrayList<Integer> solution = new ArrayList<>();

    public TSPSolver(double[][] lengths) {
        this.lengths = lengths;
    }

    public TSPSolver(){
    }

    public void setLengths(double[][] lengths) {
        this.lengths = lengths;
    }

    public double lowerBound(){
        double max1 = 0;
        for (int i = 1; i < lengths.length; i++) {
            if(i != 1)  continue;
            ArrayList<Integer>[] a = MST(i,false);
            int[] b = find2Mins(lengths[i]);
            double len = calcMSTLen(a,b,i);
            max1 = Math.max(max1, len);
        }
        //double max2 = Math.sqrt(0.5)*(Math.sqrt(lengths.length)-1/(Math.sqrt(lengths.length)));
        //double max2 = Math.sqrt(lengths.length)*0.7078+0.551;
        double max2 = 0;

        return Math.max(max1,max2);
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
        for (int i = 0; i < lengths.length; i++) {
            notVisited.add(i);
        }
        double[] minVal = {calcLength(randomInsert())};
        bfRec(visited, notVisited, 0, minVal);
        return solution;
    }

    private void bfRec(ArrayList<Integer> visited, ArrayList<Integer> notVisited, double thisLen, double[] currentMin){
        if(thisLen > currentMin[0])return;
        if(notVisited.isEmpty()){
            solution = visited;
            currentMin[0] = thisLen;
            return;
        }
        for (int n : notVisited) {
            if(n == 0 && !(notVisited.size() == 1))continue;
            ArrayList<Integer> newVisited = new ArrayList<>(visited);
            newVisited.add(n);
            ArrayList<Integer> newNotVisited = new ArrayList<>(notVisited);
            newNotVisited.remove((Integer) n);
            bfRec(newVisited, newNotVisited, thisLen + lengths[n][visited.get(visited.size()-1)],currentMin);
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
                if ((mask & (1 << u)) == 0) continue;
                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0 || u == v) continue;
                    double newDistance = dp[u][mask ^ (1 << v)] + lengths[u][v];
                    if (newDistance < dp[v][mask]) {
                        dp[v][mask] = newDistance;
                        parent[v][mask] = u;
                    }
                }
            }
        }

        double minDistance = Double.POSITIVE_INFINITY;
        int minIdx = -1;
        for (int v = 1; v < n; v++) {
            double distance = dp[v][(1 << n) - 1] + lengths[v][0];
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
        int n = lengths.length;
        ArrayList<Integer> ret = new ArrayList<>(n+1);
        ret.add(0);
        boolean[] used = new boolean[n];
        used[0] = true;
        while(ret.size()<n){
            int lastIdx = ret.get(ret.size()-1);
            int minIdx = -1;
            for (int i = 0; i < lengths.length; i++) {
                if(used[i])continue;
                if(minIdx == -1 || lengths[lastIdx][i]<lengths[lastIdx][minIdx]){
                    minIdx = i;
                }
            }
            ret.add(minIdx);
            used[minIdx] = true;
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
                double val = lengths[ret.get(j-1)][i]+lengths[ret.get(j)][i]-lengths[ret.get(j-1)][ret.get(j)];
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

            for (int city = 0; city < n; city++) {
                if (visited[city]) continue;
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
            tour.add(insertPosition, nextCity);
            visited[nextCity] = true;
        }
        tour.add(0,0);
        return tour;
    }

    public ArrayList<Integer> farthestInsertTSP() {
        int n = lengths.length;
        ArrayList<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[n];

        tour.add(0);
        visited[0] = true;

        while (tour.size() < n) {
            int farthestCity = -1;
            int insertPosition = -1;
            double maxDistance = -1.0;

            for (int i = 0; i < n; i++) {
                if (visited[i]) continue;
                double minDistance = Double.POSITIVE_INFINITY;
                for (int j = 0; j < tour.size(); j++) {
                    int city1 = tour.get(j);
                    double dist = lengths[city1][i];
                    if (dist < minDistance) {
                        minDistance = dist;
                    }
                }

                if (minDistance > maxDistance) {
                    maxDistance = minDistance;
                    farthestCity = i;
                }
            }

            double minIncrease = Double.POSITIVE_INFINITY;
            for (int j = 1; j < tour.size(); j++) {
                int city1 = tour.get(j - 1);
                int city2 = tour.get(j);
                double increase = lengths[city1][farthestCity] + lengths[farthestCity][city2] - lengths[city1][city2];
                if (increase < minIncrease) {
                    minIncrease = increase;
                    insertPosition = j;
                }
            }

            if (insertPosition == -1) {
                tour.add(farthestCity);
            } else {
                tour.add(insertPosition, farthestCity);
            }

            visited[farthestCity] = true;
        }

        tour.add(0);
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

    public static double calcLength(ArrayList<Integer>l, double[][] lengths){
        double ret = 0;
        for (int i = 0; i < l.size()-1; i++) {
            ret += lengths[l.get(i)][l.get(i+1)];
        }
        return ret;
    }


    
    public ArrayList<Integer> swap(ArrayList<Integer> list, int z){
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


    private void reverseSegment(ArrayList<Integer> tour, int i, int j) {
        while (i < j) {
            Collections.swap(tour, i, j);
            i++;
            j--;
        }
    }

    public void Opt2(ArrayList<Integer> tour) {
        boolean improved;
        do {
            improved = false;
            for (int i = 1; i < tour.size() - 2; i++) {
                for (int j = i + 1; j < tour.size() - 1; j++) {
                    if (lengths[tour.get(i-1)][tour.get(j)] + lengths[tour.get(i)][tour.get(j+1)] < lengths[tour.get(i-1)][tour.get(i)] + lengths[tour.get(j)][tour.get(j+1)]) {
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


    public void Opt3(ArrayList<Integer> tour) {
        boolean improved;
        int n = tour.size();
        do {
            improved = false;
            for (int i = 0; i < n - 3; i++) {
                for (int j = i + 1; j < n - 2; j++) {
                    for (int k = j + 1; k < n - 1; k++) {
                        improved = try3OptSwap(tour, i, j, k) || improved;
                    }
                }
            }
        } while (improved);
    }

    private boolean try3OptSwap(ArrayList<Integer> tour, int i, int j, int k) {
        boolean ret = false;
        double originalDistance = calcLength(tour);

        ArrayList<ArrayList<Integer>> newTours = new ArrayList<>();
        for (int l = 1; l < 8; l++) {
            newTours.add(getNewTour(tour, i, j, k, l));
        }

        for (ArrayList<Integer> newTour : newTours) {
            double newDistance = calcLength(newTour);
            if (newDistance < originalDistance) {
                for (int idx = 0; idx < tour.size(); idx++) {
                    tour.set(idx, newTour.get(idx));
                }
                originalDistance = newDistance;
                ret =  true;
            }
        }

        return ret;
    }

    private ArrayList<Integer> getNewTour(ArrayList<Integer> tour, int i, int j, int k, int option) {
        ArrayList<Integer> newTour = new ArrayList<>(tour);
        switch (option) {
            case 1: reverseSegment(newTour, i + 1, j); break;
            case 2: reverseSegment(newTour, j + 1, k); break;
            case 3: reverseSegment(newTour, i + 1, k); break;
            case 4: reverseSegment(newTour, i + 1, j); reverseSegment(newTour, j + 1, k); break;
            case 5: reverseSegment(newTour, i + 1, k); reverseSegment(newTour, j + 1, k); break;
            case 6: reverseSegment(newTour, i + 1, j); reverseSegment(newTour, i + 1, k); break;
            case 7: reverseSegment(newTour, i + 1, j); reverseSegment(newTour, j + 1, k); reverseSegment(newTour, i + 1, k); break;
        }
        return newTour;
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

    public ArrayList<int[]> getMinWeightPerfectMatching2(ArrayList<Integer> vertices) {
        ArrayList<int[]> minMatching = new ArrayList<>();
        if(vertices.size() % 2 == 1)throw new RuntimeException("not even number");

        int max = vertices.stream().mapToInt(Integer::intValue).max().getAsInt();

        while(!vertices.isEmpty()) {
            int farthestPoint = -1;
            double furthestDist = 0;

            int[] minDist = new int[max+1];
            for (int i: vertices) {
                int nearestPoint = -1;
                for (int j: vertices) {
                    if(j == i)continue;
                    if(nearestPoint == -1 || lengths[i][j] < lengths[i][nearestPoint]){
                        nearestPoint = j;
                    }
                }
                minDist[i] = nearestPoint;
            }

            for (int i = 0; i < minDist.length; i++) {
                if(minDist[i] == 0)continue;
                if(farthestPoint == -1 || lengths[i][minDist[i]]>furthestDist) {
                    furthestDist = lengths[i][minDist[i]];
                    farthestPoint = i;
                }
            }

            int bestPoint = -1;

            for (int i: vertices){
                if(i == farthestPoint)continue;
                if(bestPoint == -1 || lengths[farthestPoint][i] < lengths[farthestPoint][bestPoint])
                    bestPoint = i;
            }


            minMatching.add(new int[]{farthestPoint,bestPoint});
            vertices.remove((Integer) farthestPoint);
            vertices.remove((Integer) bestPoint);
        }
        return minMatching;
    }

    public static ArrayList<Integer> eulerCircuit(List<Integer>[] graph) {
        ArrayList<Integer> eulerTour = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();

        stack.push(0);
        while (!stack.isEmpty()) {
            int v = stack.peek();
            if (graph[v].isEmpty()) {
                eulerTour.add(v);
                stack.pop();
            } else {
                int u = graph[v].iterator().next();
                stack.push(u);
                graph[v].remove((Integer)u);
                graph[u].remove((Integer)v);
            }
        }

        eulerTour.add(eulerTour.get(0));
        return eulerTour;
    }

    private void hamiltonCircuit(ArrayList<Integer> eulerCir){
        HashSet<Integer> containing = new HashSet<>();
        for (int i = 0; i < eulerCir.size(); i++) {
            if(!containing.add(eulerCir.get(i))){
                eulerCir.remove(i);
                i--;
            }
        }
        eulerCir.add(eulerCir.get(0));
    }

    public ArrayList<Integer> christofides() {
        ArrayList<Integer>[] graph = MST(null,true);
        ArrayList<Integer> oddPoints = getOddPoints(graph);
        ArrayList<int[]> s = getMinWeightPerfectMatching(oddPoints);
        for (int[] e : s) {
            graph[e[0]].add(e[1]);
            graph[e[1]].add(e[0]);
        }
        ArrayList<Integer> ec = eulerCircuit(graph);
        hamiltonCircuit(ec);
        return ec;
    }


    public ArrayList<Integer> linK(){
        return LK.solve(lengths,1);
    }




    public ArrayList<Integer> orTools(){
        return OrTools.solve(lengths,1,0);
    }

    public ArrayList<Integer> aco(double antRate, double alpha, double beta, double evaporationRate, int iterations){
        ACOAlgorithm ac = new ACOAlgorithm(lengths,antRate,alpha,beta,evaporationRate);
        return ac.solve(iterations);
    }

//    public ArrayList<Integer> ga(int generations, int populationSize, int tournamentSize, float mutationRate, float elitismRate){
//        GA g = new GA(lengths, generations, populationSize, tournamentSize, mutationRate, elitismRate);
//        return g.solve();
//    }

    public ArrayList<Integer> ga(int generations, int populationSize, int tournamentSize, float mutationRate, float elitismRate){
        GA2 g = new GA2(lengths, populationSize, generations,  mutationRate);
        return g.solve();
    }

    public ArrayList<Integer> ga(){
        GA g = new GA(lengths);
        return g.solve();
    }


}

