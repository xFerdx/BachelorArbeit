import java.util.*;
import java.util.stream.Collectors;

public class TSPSolver {

    private final double[][] lengths;
    ArrayList<Integer> solution = new ArrayList<>();

    public TSPSolver(double[][] lengths) {
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
        double max2 = Math.sqrt(0.5)*(Math.sqrt(lengths.length)-1/(Math.sqrt(lengths.length)));
        System.out.println(max1);
        System.out.println(max2);

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

    public ArrayList<Integer> farthestInsertTSP() {
        int n = lengths.length;
        ArrayList<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[n];

        tour.add(0);
        visited[0] = true;

        while (tour.size() < n) {
            System.out.println(tour.toString());
            int farthestCity = -1;
            int insertPosition = -1;
            double maxDistance = -1.0;

            for (int i = 0; i < n; i++) {
                if (!visited[i]) {
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

    public ArrayList<Integer> Opt2(ArrayList<Integer> l) {
        ArrayList<Integer> bestTour = new ArrayList<>(l);
        double bestLen = calcLength(l);
        boolean improved;

        do {
            improved = false;
            for (int i = 1; i < bestTour.size() - 2; i++) {
                for (int j = i + 1; j < bestTour.size() - 1; j++) {
                    ArrayList<Integer> temp = new ArrayList<>(bestTour);
                    reverseSegment(temp, i, j);
                    double newLen = calcLength(temp);
                    if (newLen < bestLen) {
                        bestTour = temp;
                        bestLen = newLen;
                        improved = true;
                        break;
                    }
                }
                if (improved) {
                    break;
                }
            }
        } while (improved);

        return bestTour;
    }




    public ArrayList<Integer> Opt3(ArrayList<Integer> l) {
        ArrayList<Integer> bestTour = new ArrayList<>(l);
        double bestLen = calcLength(l);
        boolean improved;

        do {
            improved = false;
            outerLoop:
            for (int i = 1; i < bestTour.size() - 1; i++) {
                for (int j = 1; j < bestTour.size() - 1; j++) {
                    for (int k = 1; k < bestTour.size() - 1; k++) {
                        ArrayList<Integer> temp = new ArrayList<>(bestTour);
                        reverseSegment(temp, i + 1, j);
                        reverseSegment(temp, j + 1, k);
                        double newLen = calcLength(temp);
                        if (newLen < bestLen) {
                            bestTour = temp;
                            bestLen = newLen;
                            improved = true;
                            break outerLoop;
                        }
                    }
                }
            }
        } while (improved);

        return bestTour;
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

    ArrayList<int[]> so;

    public void getMinWeightPerfectMatchingBF(ArrayList<Integer> vertices, ArrayList<int[]> matches, double currentLen, double[] minLen) {
        if(currentLen >= minLen[0])return;
        if(vertices.isEmpty()){
            System.out.println("zaka");
            so = new ArrayList<>(matches);
            return;
        }

        for (int i = 1; i < vertices.size(); i++) {
            ArrayList<Integer> newVert = new ArrayList<>(vertices);
            newVert.remove(0);
            newVert.remove(i-1);
            ArrayList<int[]> newMatches = new ArrayList<>();
            for (int j = 0; j < matches.size(); j++) {
                newMatches.add(new int[]{matches.get(j)[0], matches.get(j)[1]});
            }
            newMatches.add(new int[]{vertices.get(0), vertices.get(i)});
            getMinWeightPerfectMatchingBF(newVert, newMatches, currentLen + lengths[vertices.get(0)][vertices.get(i)], minLen);
        }
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
        for (int i = 0; i < graph.length; i++) {
            System.out.println(i+": "+graph[i].toString());
        }

        ArrayList<Integer> oddPoints = getOddPoints(graph);
        System.out.println("oddPoints: "+oddPoints);

        ArrayList<int[]> s = getMinWeightPerfectMatching(oddPoints);

        //etMinWeightPerfectMatchingBF(oddPoints, new ArrayList<>(), 0, new double[]{Double.MAX_VALUE});


        for (int[] e : s) {
            graph[e[0]].add(e[1]);
            graph[e[1]].add(e[0]);
            System.out.println(e[0]+" - "+e[1]);
        }

        for (int i = 0; i < graph.length; i++) {
            System.out.println(i+": "+graph[i].toString());
        }

        ArrayList<Integer> startRoute = new ArrayList<>();
        startRoute.add(0);
        ArrayList<Integer> missing = new ArrayList<>();
        for (int i = 0; i < lengths.length; i++) {
            missing.add(i);
        }

        ArrayList<Integer> ec = eulerCircuit(graph);

        hamiltonCircuit(ec);

        return ec;
    }














}

