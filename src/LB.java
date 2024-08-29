import java.util.ArrayList;

public class LB {

    double[][] lengths;

    public LB(double[][] lengths) {
        this.lengths = lengths;
    }

    public double lowerBound(){
        double max1 = 0;
        for (int i = 1; i < lengths.length; i++) {
            if(i != 1)  continue;
            TSPSolver tsp = new TSPSolver(lengths);
            ArrayList<Integer>[] a = tsp.MST(i,false);
            int[] b = find2Mins(lengths[i]);
            double len = calcMSTLen(a,b,i);
            max1 = Math.max(max1, len);
        }
        //double max2 = Math.sqrt(0.5)*(Math.sqrt(lengths.length)-1/(Math.sqrt(lengths.length)));
        double max2 = Math.sqrt(lengths.length)*0.7078+0.551;

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

}
