import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class MyPanel extends JPanel{

    int[][] points = new int[0][2];

    float[][] lengths = new float[0][0];

    int[] solution = new int[0];

    final int pointSize = 10;

    MyPanel(){
        this.setPreferredSize(new Dimension(500,500));
        setFocusable(true);
        requestFocus();

        JTextField textField = new JTextField(5);

        JButton button = new JButton("create Points");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redrawButtonClicked(Integer.parseInt(textField.getText()));
            }
        });
        JButton button2 = new JButton("BF");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bruteForce();
            }
        });

        add(textField);
        add(button);
        add(button2);


    }

    public void redrawButtonClicked(int number){
        solution = new int[0];
        points = new int[number][2];
        for (int i = 0; i < number; i++) {
            points[i][0] = (int) (Math.random()*300)+100;
            points[i][1] = (int) (Math.random()*300)+100;
        }
        getLengths();
        repaint();
    }

    public void getLengths(){
        lengths = new float[points.length][points.length];
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                lengths[i][j] = (float) Math.sqrt(Math.pow(points[i][0]-points[j][0],2)+Math.pow(points[i][1]-points[j][1],2));
            }
        }
    }

    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawRect(100,100,300+pointSize,300+pointSize);
        for (int i = 0; i < points.length; i++) {
            int[] point = points[i];
            g2d.setColor(i==0?Color.GREEN:Color.BLUE);
            g2d.fillOval(point[0], point[1], pointSize, pointSize);
        }

        for (int i = 0; i < solution.length-1; i++) {
            g2d.setColor(Color.BLACK);
            g2d.drawLine(points[solution[i]][0]+pointSize/2,points[solution[i]][1]+pointSize/2,points[solution[i+1]][0]+pointSize/2,points[solution[i+1]][1]+pointSize/2);
        }
    }

    public void bruteForce(){
        ArrayList<Integer> visited = new ArrayList<>();
        visited.add(0);
        float[] minVal = {Integer.MAX_VALUE};
        bfRec(visited, minVal);
        repaint();
        System.out.println("minVal: "+minVal[0]);
        System.out.println("minPath:"+Arrays.toString(solution));
    }

    private void bfRec(ArrayList<Integer> visited, float[] currentMin){
        if(visited.size()== lengths.length){
            visited.add(0);
            if(calcLength(visited)<currentMin[0]){
                currentMin[0] = calcLength(visited);
                solution = visited.stream().mapToInt(Integer::intValue).toArray();
                return;
            }
        }
        for (int i = 0; i < lengths.length; i++) {
            if(!visited.contains(i)){
                ArrayList<Integer> newVisited = (ArrayList<Integer>) visited.clone();
                newVisited.add(i);
                bfRec(newVisited, currentMin);
            }
        }
    }

    public float calcLength(ArrayList<Integer> l){
        float ret = 0;
        for (int i = 0; i < l.size()-1; i++) {
            ret += lengths[l.get(i)][l.get(i+1)];
        }
        return ret;
    }


}
