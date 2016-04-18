import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.lang.Thread;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Display extends JFrame{

    //private JFrame mainMap;
    private Polygon poly;
    private Shape highLight;
    private JPanel score1;
    private JPanel score2;
    private Color movesColor = new Color(0, 255, 0, 255); //Color of the possible moves RGBA
    private Color removeColor = new Color(0, 0, 255, 255); //Color of the pieces to be removed RGBA

    public Display() {

        initComponents();

    }

    private void initComponents() {
        setLayout(null);
        setBounds(0,0,1300,1300);
        setUndecorated(true);
        getContentPane().setBackground(new Color(25,25,25));
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(this);
        setVisible(true);

        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        highLight = new Shape(height, (width-height)/2);
        highLight.setBounds(0,0,width, height);
        add(highLight);

        JPanel score1Border = new JPanel();
        score1Border.setBackground(Color.WHITE);
        score1Border.setBounds(2, height-128, 106, 106);
        score1Border.setLayout(null);
        score1 = new JPanel();
        score1.setBounds(3, 3, 100, 100);
        score1.setBackground(new Color(25,25,25));
        JLabel title1 = new JLabel("<html><body>Remaining<br>Pieces</body></html>");
        title1.setForeground(Color.WHITE);
        JLabel number1 = new JLabel();
        number1.setText("12");
        number1.setForeground(Color.WHITE);
        number1.setFont(number1.getFont().deriveFont(36f));

        score1.add(title1);
        score1.add(number1);
        score1.repaint();
        score1Border.add(score1);
        score1Border.repaint();

        class rotatedPanel extends JPanel {
            public rotatedPanel() {
            }

            @Override
            public void paintComponent(Graphics graphics){
                Graphics2D g = (Graphics2D) graphics;
                int x = this.getWidth()/2;
                int y = this.getHeight()/2;
                g.rotate(Math.toRadians(180.0), x, y);
                super.paintComponent(g);
            }

        }

        JPanel score2Border = new JPanel();
        score2Border.setBackground(Color.WHITE);
        score2Border.setBounds(width-108, 22, 106, 106);
        score2Border.setLayout(null);
        score2 = new rotatedPanel();
        score2.setBackground(new Color(25,25,25));
        score2.setBounds(3, 3, 100, 100);
        JLabel title2 = new JLabel("<html><body>Remaining<br>Pieces</body></html>");
        title2.setForeground(Color.WHITE);
        JLabel number2 = new JLabel("12");
        number2.setForeground(Color.WHITE);
        number2.setFont(number2.getFont().deriveFont(36f));

        score2.add(title2);
        score2.add(number2);
        score2.repaint();
        score2Border.add(score2);
        score2Border.repaint();

        add(score1Border);
        add(score2Border);


        JPanel board = new JPanel(null);
        board.setBounds((width-height)/2,0,height,height);
        JLabel picture = new JLabel();

        Image img = getImage("images/board.jpg");
        Image resizedImage = img.getScaledInstance(board.getWidth(), board.getHeight(), 0);
        picture.setIcon(new ImageIcon(resizedImage));
        picture.setBounds(0,0,height,height);
        board.add(picture);

        add(board);
        repaint();
        /*
           double xPolyPer2[] = {0.1, 0.9, 0.9, 0.1};
           double yPolyPer2[] = {0.1, 0.1, 0.9, 0.9};

           int xPoly2[] = transform(xPolyPer2, width);
           int yPoly2[] = transform(yPolyPer2, height);

           Shape test = new Shape(height, 120);
           test.setBounds(0,0,width,height);
           add(test);


           CheckersDrawing t = new CheckersDrawing("Crown", "Crown1", new Color(238, 213, 33, 175));
           t.setXYLoc(1, 7);
           test.addObject(t);

           CheckersDrawing a = new CheckersDrawing("Path", "Path1", new Color(0, 0, 255, 255));
           a.setXYArray(new double[]{3, 5, 3, 1}, new double[]{5,3,1,3});
           test.addObject(a);
         */
    }

    private int[] transform(double[] per, int tot) {
        int[] ret = new int[per.length];
        for(int i=0; i<per.length; i++) {
            ret[i] = (int) (per[i]*tot);
        }
        return ret;
    }

    public void clearHighlights() {
        highLight.clear();
    }

    public void highlightMoves(ArrayList<ArrayList<Integer>> moves) {
        clearHighlights();
        for(int i=0; i<moves.size(); i++) {
            double[] x = new double[moves.get(i).size()/2];
            double[] y = new double[moves.get(i).size()/2];
            for(int j=0; j<x.length; j++) {
                x[j] = moves.get(i).get(j*2);
                y[j] = moves.get(i).get(j*2+1);
            }
            CheckersDrawing temp = new CheckersDrawing("Path", "Path", movesColor);
            temp.setXYArray(x,y);
            highLight.addObject(temp);

        }
    }

    public void highlightJumped(ArrayList<ArrayList<Integer>> jumped) {
        System.out.println(jumped);
        clearHighlights();
        if(jumped.size()!=0) {
            for(int i=0; i<jumped.get(0).size()/2; i++) {
                CheckersDrawing temp = new CheckersDrawing("Remove", "Remove", removeColor);
                temp.setXYLoc(jumped.get(0).get(i*2),jumped.get(0).get(i*2+1));
                highLight.addObject(temp);
            }
        }
    }

    public static Image getImage(final String pathAndFileName) {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(pathAndFileName);
        return Toolkit.getDefaultToolkit().getImage(url);
    }

}

