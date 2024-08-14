import java.awt.*;
import javax.imageio.plugins.tiff.ExifTIFFTagSet;
import javax.swing.JComponent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


public class Window extends JComponent {
    private Color Brown = new Color(70, 56, 0); 
    private Color BoardC = new Color(100, 100, 70);
    private Color Black = new Color(0,0,0);
    private Color Highli = new Color(0,0,0,100);
    private Square[][] board = new Square[8][8];
    private int[][] coords= new int[2][0];
    private boolean highlights = false;
    public Window() {
    
    }
    public void transfer(Square[][] s) {
        board = s;
    }
    public void highlight (int[][] c) {
        coords = c;
    }
    public void sH(boolean b) {
        highlights = b;
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 8; i > 0; i--) {
            for (int j = 0; j < 4; j++) {
                g.setColor(BoardC);
                g.fillRect(((i%2)*80) + j * 160, (i-1)*80, 80, 80);
                g.setColor(Black);
                g.drawRect(0,0,640,640);
            }
        }
        if (highlights) {
            g.setColor(Highli);
            for (int i = 0; i < coords[0].length; i++) {
                g.fillRect(coords[0][i]*80, coords[1][i]*80, 80, 80);
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    g.drawImage(board[i][j].getImg(),j*80,i*80,null);
                } catch (Exception e) {
                }
            }
        }
    }
}
