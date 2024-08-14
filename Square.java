import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Square {
    private char piece;
    private int security;
    private boolean white;
    private BufferedImage icon;
    private double value;
    private int bval;
    public Square(char p, boolean w) {
        piece = p;
        white = w;
        try {
            setImg();
        } catch (IOException e) {
            System.out.println("Error: Constructor 1");
        }
        value = bval;
    }
    public Square(char p, boolean w, int s) {
        piece = p;
        white = w;
        security = s;
        try {
            setImg();
        } catch (IOException e) {
            System.out.println("Error: Constructor 2");
        }
        value = bval;
    }
    public void changeType(char p, boolean w)  {
        piece = p;
        white = w;
        try {
            setImg();
        } catch (IOException e) {
            System.out.println("Error: ChangeType 1");
        }
    }
    public char getPiece() {
        return piece;
    }
    public double getVal() {
        return value;
    }
    public boolean getWhite() {
        return white;
    }
    public void setSec(int s) {
        security = s;
    }
    public void addSec(int s) {
        security += s;
    }
    public void addVal(double v) {
        value += v;
    }
    public void setVal(double v) {
        value = v;
    }
    public int getSec() {
        return security;
    }
    public BufferedImage getImg() {
        return icon;
    }
    public int getBval() {
        return bval;
    }
    private void setImg() throws IOException {
        File file;
        if (white) {
            switch(piece) {
                case 'P':
                    file = new File("Pawn_White.png");
                    icon = ImageIO.read(file);
                    bval = 1;
                    break;
                case 'N':
                    file = new File("Knight_White.png");
                    icon = ImageIO.read(file);
                    bval = 3;
                    break;
                case 'B':
                    file = new File("Bishop_White.png");
                    icon = ImageIO.read(file);
                    bval = 3;
                    break;
                case 'R':
                    file = new File("Rook_White.png");
                    icon = ImageIO.read(file);
                    bval = 5;
                    break;
                case 'K':
                    file = new File("King_White.png");
                    icon = ImageIO.read(file);
                    bval = 100;
                    break;
                case 'Q':
                    file = new File("Queen_White.png");
                    icon = ImageIO.read(file);
                    bval = 9;
                    break;
                case 'O':
                    file = new File("Blank.png");
                    icon = ImageIO.read(file); 
                    bval = 0;
            }
        } else {
            switch(piece) {
                case 'P':
                   file = new File("Pawn_Black.png");
                    icon = ImageIO.read(file);
                    bval = 1;
                    break;
                case 'N':
                    file = new File("Knight_Black.png");
                    icon = ImageIO.read(file);
                    bval = 3;
                    break;
                case 'B':
                    file = new File("Bishop_Black.png");
                    icon = ImageIO.read(file);
                    bval = 3;
                    break;
                case 'R':
                    file = new File("Rook_Black.png");
                    icon = ImageIO.read(file);
                    bval = 5;
                    break;
                case 'K':
                    file = new File("King_Black.png");
                    icon = ImageIO.read(file);
                    bval = 100;
                    break;
                case 'Q':
                    file = new File("Queen_Black.png");
                    icon = ImageIO.read(file);
                    bval = 9;
                    break;
                case 'O':
                    file = new File("Blank.png");
                    icon = ImageIO.read(file);
                    bval = 0;
            } 
        }
    }
}
