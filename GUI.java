import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.*;
import java.lang.InterruptedException;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GUI implements MouseListener {

    private static double curBVAL = 0;
    private static double curWVAL = 0;
    private static JFrame f = new JFrame(); 
    private static Window w = new Window();
    private static Square[][] board = new Square[8][8];
    private static boolean selected = false;
    private static int selecX, selecY;
    private static boolean wturn = true;
    private static final boolean AIwhite = false;
    //PosAIMove[n][0] would be y1, [n][1] would be x1, [n][2] would be y2, [n][3] would be x2. One row would be one possible move.
    private static ArrayList<int[]> PosAIMoves = new ArrayList<int[]>();
    private static ArrayList<int[]> ChosenMoves = new ArrayList<int[]>();
    private static final int depth = 3;

    public GUI() {
        f.add(w);
        f.addMouseListener(this);
        f.setFocusable(true);
        f.requestFocusInWindow();
    }
    public static void main(String[] args) {
        GUI gui = new GUI();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(700,670);
        w.setVisible(true);
        setUp(board);
        w.transfer(board);
        w.repaint();
        /*
        Moves(board,PosAIMoves,AIwhite);
        for (int i = 0; i < PosAIMoves.size(); i++) {
            //System.out.println(PosAIMoves.get(i)[1] + " " + PosAIMoves.get(i)[0] + " " + PosAIMoves.get(i)[3] + " " + PosAIMoves.get(i)[2]);
        }
        */
        if (AIwhite) {
            Run();
        }
    }
    private static void resetVal(Square[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j].setVal(board[i][j].getBval());
            }
        }
    }
    private static double rmInvalid(ArrayList<int[]> input) {
        try {
            for (int k = 0; k < input.size(); k++) {
                int[] arr = input.get(k);
                if (!isValid(arr[1],arr[0],arr[3],arr[2],board)) {
                    input.remove(k);
                    k--;
                }
            }
        } catch (Exception e) {
            //nothing!
        }
        return input.size()/10.0;
    }
    public static void Run() {
        ////System.out.println("Run");

        w.repaint();
        resetVal(board);
        PosAIMoves.clear();
        ////System.out.println("Hello1");
        Moves(board,PosAIMoves,AIwhite);
        ////System.out.println("Hello2");
        for (int n = 0; n < PosAIMoves.size(); n++) {
            for (int sn = 0; sn < 4; sn++) {
                System.out.print(PosAIMoves.get(n)[sn] + " ");
            }
            System.out.println();
        }
        double x = rmInvalid(PosAIMoves);
        curBVAL = calcB(board);
        curWVAL = calcW(board);
        System.out.println("[" + curWVAL + ", " + curBVAL + "]");
        Square[][] step1 = new Square[8][8]; 
        int[][] moves = new int[3][4];
        ////System.out.println("Hello");
        try { //Manual Recursion. To be updated in draft 2. 
              //Minimax Search
              ////System.out.println("STep1");
            int k1 = 0;
            double max1 = -1000.0;
            for (int s1 = 0; s1 < PosAIMoves.size(); s1++) { //STEP1 move is a MIN. STEP2 move to step 3 is a MAX. Step0 to STEP1 is a MAX.
                           //MAX
                System.out.println(k1);
                step1 = copy(board);
                int[] arr1 = PosAIMoves.get(s1);
                System.out.println(PosAIMoves.size());
                System.out.println(arr1[0] + " " + arr1[1] + " " + arr1[2] + " " + arr1[3]);
                //Here
                step1[arr1[2]][arr1[3]] = step1[arr1[0]][arr1[1]];
                //Here
                step1[arr1[0]][arr1[1]] = new Square('O',false);
                ArrayList<int[]> step1moves = new ArrayList<int[]>();
                boolean step1white = !AIwhite;
                Moves(step1,step1moves,step1white);
                double min2 = 1000.0;
                x = rmInvalid(step1moves);
                double step1WVAL = calcW(step1);
                double step1BVAL = calcB(step1);
                try {
                    Square[][] step2 = copy(step1);
                    int k2 = 0;
                    for (int s2 = 0; s2 < step1moves.size(); s2++) { //MIN
                        step2 = copy(step1);
                        int[] arr2 = step1moves.get(s2);
                        step2[arr2[2]][arr2[3]] = step2[arr2[0]][arr2[1]];
                        step2[arr2[0]][arr2[1]] = new Square('O',false);
                        ArrayList<int[]> step2moves = new ArrayList<int[]>();
                        boolean step2white = !step1white;
                        Moves(step2,step2moves,step2white);
                        double max3 = -1000.0;
                        x = rmInvalid(step2moves);
                        double step2WVAL = calcW(step2);
                        double step2BVAL = calcB(step2);
                        try {
                            ////System.out.println("Checked");
                            Square[][] step3 = copy(step2);
                            int[] move3 = new int[4];
                            int k3 = 0;
                            for (int s3 = 0; s3 < step2moves.size(); s3++) { //MAX
                                step3 = copy(step2);
                                int[] arr3 = step2moves.get(s3);
                                step3[arr3[2]][arr3[3]] = step3[arr3[0]][arr3[1]];
                                step3[arr3[0]][arr3[1]] = new Square('O',false);
                                ArrayList<int[]> step3moves = new ArrayList<int[]>();
                                boolean step3white = !step2white;
                                Moves(step3,step3moves,step3white);
                                //x = rmInvalid(step3moves);
                                double step3WVAL = calcW(step3);
                                double step3BVAL = calcB(step3);
                                int multi3 = step2white ? 1 : -1;
                                if ((step3WVAL - step3BVAL)*multi3 > max3) {
                                    max3 = (step3WVAL - step3BVAL) * multi3;
                                    moves[2] = arr3;
                                }

                                k3++;
                            }
                        } catch (Exception e) { System.out.println("Step3 Caught"); }
                        if (max3 < min2) {
                            min2 = max3;
                            moves[1] = arr2;
                        }
                        k2++;
                    }

                } catch (Exception e) { System.out.println("Step2 Caught"); }
                if (min2 > max1) {
                    max1 = min2;
                    moves[0] = arr1;
                }
                k1++;
            }
        } catch (Exception e) { System.out.println("Step1 Caught"); }
        System.out.println("AI Moved");
        board[moves[0][2]][moves[0][3]] = board[moves[0][0]][moves[0][1]];
        board[moves[0][0]][moves[0][1]] = new Square('O',false);
        System.out.println("(" + moves[0][0] + ", " + moves[0][1] + ")" + "(" + moves[0][2] + ", " + moves[0][3] + ")");
        wturn = !wturn;
    }
    public static void Run1() {
        ////System.out.println("Run");
        f.repaint();
        resetVal(board);
        PosAIMoves.clear();
        Moves(board,PosAIMoves,AIwhite);
        //double x = rmInvalid(PosAIMoves);
        curBVAL = calcB(board);
        curWVAL = calcW(board);
        System.out.println("[" + curWVAL + ", " + curBVAL + "]");
        Square[][] step1 = new Square[8][8]; 
        int[] move = new int[4];
        ////System.out.println("Hello");
        try { //Manual Recursion. To be updated in draft 2. 
              //Minimax Search
              ////System.out.println("STep1");
            int k1 = 0;
            double max1 = -1000.0;
            while (true) { //STEP1 move is a MIN. STEP2 move to step 3 is a MAX. Step0 to STEP1 is a MAX.
                           //MAX
                step1 = copy(board);
                int[] arr1 = PosAIMoves.get(k1);
                step1[arr1[2]][arr1[3]] = step1[arr1[0]][arr1[1]];
                step1[arr1[0]][arr1[1]] = new Square('O',false);
                ArrayList<int[]> step1moves = new ArrayList<int[]>();
                boolean step1white = !AIwhite;
                Moves(step1,step1moves,step1white);
                //rmInvalid(step1moves);
                double step1WVAL = calcW(step1);
                double step1BVAL = calcB(step1);
                double min2 = 1000.0;
                int multi = AIwhite ? -1 : 1;

                if ((step1WVAL - step1BVAL)*multi > max1) {
                    max1 = (step1WVAL - step1BVAL)*multi; 
                    move = arr1;
                }
                k1++;
            }
        } catch (Exception e) { /*nothing*/ }
        System.out.println("AI Moved");
        board[move[2]][move[3]] = board[move[0]][move[1]];
        board[move[0]][move[1]] = new Square('O',false);
        System.out.println("(" + move[0] + ", " + move[1] + ")" + "(" + move[2] + ", " + move[3] + ")");
        wturn = !wturn;
    }
    public static double calcW(Square[][] board) {
        double ret = 0.0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getWhite() == true && board[i][j].getSec() > -1) {
                    ret += board[i][j].getVal();
                }
            }
        }
        return ret;
    }
    public static double calcB(Square[][] board) {
        double ret = 0.0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getWhite() == false) {
                    ret += board[i][j].getVal();
                }
            }
        }
        return ret;
    }
    public static void setUp(Square[][] board) {
        board[0][0] = new Square('R',false);
        board[0][7] = new Square('R',false);
        board[0][6] = new Square('N',false);
        board[0][1] = new Square('N',false);
        board[0][2] = new Square('B',false);
        board[0][5] = new Square('B',false);
        board[0][4] = new Square('K',false);
        board[0][3] = new Square('Q',false);
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Square('P', false);
        }

        board[7][0] = new Square('R',true);
        board[7][7] = new Square('R',true);
        board[7][6] = new Square('N',true);
        board[7][1] = new Square('N',true);
        board[7][2] = new Square('B',true);
        board[7][5] = new Square('B',true);
        board[7][4] = new Square('K',true);
        board[7][3] = new Square('Q',true);
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Square('P',true);
        }

        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Square('O',false);
            }
        }
    }
    public static void Moves(Square[][] board,ArrayList<int[]> PosAIMoves, boolean AIwhite) {
        //Redesign of SetSecurity method
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boolean border = false;
                boolean ijwhite = board[i][j].getWhite();
                int k = 0;
                //System.out.println(i + " " + j);
                //System.out.println("Pre-Diagonals");
                //------------DIAGONALS----------------
                if (board[i][j].getPiece() == 'B' || board[i][j].getPiece() == 'Q') {
                    k = 1;
                    border = i+k < 8 && j+k < 8;

                    while (border && board[i+k][j+k].getPiece() == 'O') {
                        if (ijwhite) {
                            int[] tarr = {i,j,i+k,j+k}; PosAIMoves.add(tarr);
                        }
                        board[i][j].addVal(0.1);

                        k++;
                        border = i+k < 8 && j+k < 8;
                    }
                    if (border) {
                        if (board[i][j].getWhite() == board[i+k][j+k].getWhite()) {
                            board[i+k][j+k].addSec(1);
                        } else {
                            board[i+k][j+k].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i+k,j+k}; PosAIMoves.add(tarr);
                        }
                    }
                    //System.out.println("CheckPoint1");

                    k = 1;
                    border = i-k > -1 && j+k < 8;
                    //System.out.println("CheckPoint1.25");
                    while (border && board[i-k][j+k].getPiece() == 'O') {
                        //    //System.out.println("LoopChecker");
                        if (ijwhite) {
                            int[] tarr = {i,j,i-k,j+k}; PosAIMoves.add(tarr);
                        }
                        board[i][j].addVal(0.1);

                        k++; 
                        border = i-k > -1 && j+k < 8;
                    }
                    //System.out.println("Checkpoint1.5");
                    if (border) {
                        if (board[i][j].getWhite() == board[i-k][j+k].getWhite()) {
                            board[i-k][j+k].addSec(1);
                        } else {
                            board[i-k][j+k].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i-k,j+k}; PosAIMoves.add(tarr);
                        }
                    }
                    //System.out.println("Checkpoint2");

                    k = 1;
                    border = i+k < 8 && j-k > -1;

                    while (border && board[i+k][j-k].getPiece() == 'O') {
                        if (ijwhite) {
                            int[] tarr = {i,j,i+k,j-k}; PosAIMoves.add(tarr);
                        }
                        board[i][j].addVal(0.1);

                        k++;
                        border = i+k < 8 && j-k > -1;
                    }
                    if (border) {
                        if (board[i][j].getWhite() == board[i+k][j-k].getWhite()) {
                            board[i+k][j-k].addSec(1);
                        } else {
                            board[i+k][j-k].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i+k,j-k}; PosAIMoves.add(tarr);
                        }
                    }
                    //System.out.println("CheckPoint3");

                    k = 1;
                    border = i-k > -1 && j-k > -1;

                    while (border && board[i-k][j-k].getPiece() == 'O') {
                        if (ijwhite) {
                            int[] tarr = {i,j,i-k,j-k}; PosAIMoves.add(tarr);
                        }
                        board[i][j].addVal(0.1);

                        k++;
                        border = i-k > -1 && j-k > -1;
                    }
                    if (border) {
                        if (board[i][j].getWhite() == board[i-k][j-k].getWhite()) {
                            board[i-k][j-k].addSec(1);
                        } else {
                            board[i-k][j-k].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i-k,j-k}; PosAIMoves.add(tarr);
                        }
                    }
                    //System.out.println("CheckPoint4");
                }
                //System.out.println("Post-Diagonals");
                //---------------Files And Ranks---------------
                //System.out.println("Pre-Rooks");
                if (board[i][j].getPiece() == 'R' || board[i][j].getPiece() == 'Q') {
                    k = 1;
                    border = i+k < 8;
                    //System.out.println("Pre-Loop");
                    while (border && board[i+k][j].getPiece() == 'O') {
                        if (ijwhite) {
                            int[] tarr = {i,j,i+k,j}; PosAIMoves.add(tarr);
                        }
                        board[i][j].addVal(0.1);

                        k++;
                        border = i+k < 8;
                    }
                    //System.out.println("Post-loops");
                    if (border) {
                        if (ijwhite == board[i+k][j].getWhite()) {
                            board[i+k][j].addSec(1);
                        } else {
                            board[i+k][j].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i+k,j}; PosAIMoves.add(tarr);
                        }
                    }
                    //System.out.println("Checkpoint 1");
                    k = 1;
                    border = i-k > -1;
                    while (border && board[i-k][j].getPiece() == 'O') {
                        if (ijwhite) {
                            int[] tarr = {i,j,i-k,j}; PosAIMoves.add(tarr);
                        }
                        board[i][j].addVal(0.1);

                        k++;
                        border = i-k > -1;
                    }
                    if (border) {
                        if (ijwhite == board[i-k][j].getWhite()) {
                            board[i-k][j].addSec(1);
                        } else {
                            board[i-k][j].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i-k,j}; PosAIMoves.add(tarr);
                        }
                    }

                    //System.out.println("CheckPoint2");
                    k = 1;
                    border = j+k < 8;
                    while (border && board[i][j+k].getPiece() == 'O') {
                        if (ijwhite) {
                            int[] tarr = {i,j,i,j+k}; PosAIMoves.add(tarr);
                        }
                        board[i][j].addVal(0.1);

                        k++;
                        border = j+k < 8;
                    }
                    if (border) {
                        if (ijwhite == board[i][j+k].getWhite()) {
                            board[i][j+k].addSec(1);
                        } else {
                            board[i][j+k].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i,j+k}; PosAIMoves.add(tarr);
                        }
                    }

                    k = 1;
                    border = j-k > -1;
                    while (border && board[i][j-k].getPiece() == 'O') {
                        if (ijwhite) {
                            int[] tarr = {i,j,i,j-k}; PosAIMoves.add(tarr);
                        }
                        board[i][j].addVal(0.1);

                        k++;
                        border = j-k > -1;
                    }
                    if (border) {
                        if (ijwhite == board[i][j-k].getWhite()) {
                            board[i][j-k].addSec(1);
                        } else {
                            board[i][j-k].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i,j-k}; PosAIMoves.add(tarr);
                        }
                    }
                    //System.out.println("Post-Rooks");
                } else 
//----------------Knights---------------
                if (board[i][j].getPiece() == 'N') {
                //System.out.println("Pre-Knights");
                    if (i + 1 < 8 && j + 2 < 8) {
                        if (board[i+1][j+2].getPiece() == 'O') {
                            int[] tarr = {i,j,i+1,j+2}; PosAIMoves.add(tarr);
                        } else if (board[i+1][j+2].getWhite() == ijwhite) {
                            board[i+1][j+2].addSec(1);
                        } else {
                            board[i+1][j+2].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i+1,j+2}; PosAIMoves.add(tarr);
                        }
                    }

                    if (i+2 < 8 && j+1 < 8) {
                        if (board[i+2][j+1].getPiece() == 'O') {
                            int[] tarr = {i,j,i+2,j+1}; PosAIMoves.add(tarr);
                        } else if (board[i+2][j+1].getWhite() == ijwhite) {
                            board[i+2][j+1].addSec(1);
                        } else {
                            board[i+2][j+1].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i+2,j+1}; PosAIMoves.add(tarr);
                        }
                    }

                    if (i - 1 > -1 && j + 2 < 8) {
                        if (board[i-1][j+2].getPiece() == 'O') {
                            int[] tarr = {i,j,i-1,j+2}; PosAIMoves.add(tarr);
                        } else if (board[i-1][j+2].getWhite() == ijwhite) {
                            board[i-1][j+2].addSec(1);
                        } else {
                            board[i-1][j+2].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i-1,j+2}; PosAIMoves.add(tarr);
                        }
                    }

                    if (i-2 > -1 && j+1 < 8) {
                        if (board[i-2][j+1].getPiece() == 'O') {
                            int[] tarr = {i,j,i-2,j+1}; PosAIMoves.add(tarr);
                        } else if (board[i-2][j+1].getWhite() == ijwhite) {
                            board[i-2][j+1].addSec(1);
                        } else {
                            board[i-2][j+1].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i-2,j+1}; PosAIMoves.add(tarr);
                        }
                    }

                    if (i-2 > -1 && j-1 > -1) {
                        if (board[i-2][j-1].getPiece() == 'O') {
                            int[] tarr = {i,j,i-2,j-1}; PosAIMoves.add(tarr);
                        } else if (board[i-2][j-1].getWhite() == ijwhite) {
                            board[i-2][j-1].addSec(1);
                        } else {
                            board[i-2][j-1].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i-2,j-1}; PosAIMoves.add(tarr);
                        }
                    }

                    if (i-1 > -1 && j-2 > -1) {
                        if (board[i-1][j-2].getPiece() == 'O') {
                            int[] tarr = {i,j,i-1,j-2}; PosAIMoves.add(tarr);
                        } else if (board[i-1][j-2].getWhite() == ijwhite) {
                            board[i-1][j-2].addSec(1);
                        } else {
                            board[i-1][j-2].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i-1,j-2}; PosAIMoves.add(tarr);
                        }
                    }

                    if (i+2 < 8 && j-1 > -1) {
                        if (board[i+2][j-1].getPiece() == 'O') {
                            int[] tarr = {i,j,i+2,j-1}; PosAIMoves.add(tarr);
                        } else if (board[i+2][j-1].getWhite() == ijwhite) {
                            board[i+2][j-1].addSec(1);
                        } else {
                            board[i+2][j-1].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i+2,j-1}; PosAIMoves.add(tarr);
                        }
                    }

                    if (i+1 < 8 && j-2 > -1) {
                        if (board[i+1][j-2].getPiece() == 'O') {
                            int[] tarr = {i,j,i+1,j-2}; PosAIMoves.add(tarr);
                        } else if (board[i+1][j-2].getWhite() == ijwhite) {
                            board[i+1][j-2].addSec(1);
                        } else {
                            board[i+1][j-2].addSec(-1);
                            board[i][j].addVal(0.1);
                            int[] tarr = {i,j,i+1,j-2}; PosAIMoves.add(tarr);
                        }
                    }
                //System.out.println("Post-Knights");
                } else 
//---------------Pawns--------------
                if (board[i][j].getPiece() == 'P') {
                //System.out.println("Pre-Pawns");
                    if (ijwhite) {
                        if (i-1 > -1 && board[i-1][j].getPiece() == 'O') {
                            if (i == 6) {
                                if (board[i-2][j].getPiece() == 'O') {
                                    if (ijwhite == AIwhite) {
                                        int[] tarr1 = {i,j,i-2,j}; PosAIMoves.add(tarr1);
                                    }
                                    board[i][j].addVal(0.1);
                                }
                            }
                            if (ijwhite == AIwhite) {
                                int[] tarr2 = {i,j,i-1,j}; PosAIMoves.add(tarr2);
                            }
                            board[i][j].addVal(0.1);
                        }
                        if (i-1 > -1 && j+1 < 8 && board[i-1][j+1].getPiece() != 'O') {
                            if (board[i-1][j+1].getWhite() != ijwhite) {
                                if (ijwhite == AIwhite) {
                                    int[] tarr3 = {i,j,i-1,j+1}; PosAIMoves.add(tarr3);
                                }
                                board[i][j].addVal(0.1);
                                board[i-1][j+1].addSec(-1);
                            } else {
                                board[i-1][j+1].addSec(1);
                            }
                        }
                        if (i-1 > -1 && j-1 > -1 && board[i-1][j-1].getPiece() != 'O') {
                            if (board[i-1][j-1].getWhite() != ijwhite) {
                                if (ijwhite == AIwhite) {
                                    int[] tarr4 = {i,j,i-1,j-1}; PosAIMoves.add(tarr4);
                                }
                                board[i][j].addVal(0.1);
                                board[i-1][j-1].addSec(-1);
                            } else {
                                board[i-1][j-1].addSec(1);
                            }
                        }
                    } else {
                        if (i+1 < 8 && board[i+1][j].getPiece() == 'O') {
                            if (i == 1) {
                                if (board[i+2][j].getPiece() =='O') {
                                    if (ijwhite == AIwhite) {
                                        int[] tarr1 = {i,j,i+2,j}; PosAIMoves.add(tarr1);
                                    }
                                    board[i][j].addVal(0.1);
                                }
                            }
                            if (ijwhite == AIwhite) {
                                int[] tarr2 = {i,j,i+1,j}; PosAIMoves.add(tarr2);
                            }
                            board[i][j].addVal(0.1);
                        }
                        if (i+1 < 8 && j+1 < 8 && board[i+1][j+1].getPiece() != 'O') {
                            if (board[i+1][j+1].getWhite() != ijwhite) {
                                if (ijwhite == AIwhite) {
                                    int[] tarr3 = {i,j,i+1,j+1}; PosAIMoves.add(tarr3);
                                }
                                board[i][j].addVal(0.1);
                                board[i+1][j+1].addSec(-1);
                            } else {
                                board[i+1][j+1].addSec(1);
                            }
                        }
                        if (i+1 < 8 && j-1 > -1 && board[i+1][j-1].getPiece() != 'O') {
                            if (board[i+1][j-1].getWhite() != ijwhite) {
                                if (ijwhite == AIwhite) {
                                    int[] tarr4 = {i,j,i+1,j-1}; PosAIMoves.add(tarr4);
                                }
                                board[i][j].addVal(0.1);
                                board[i+1][j-1].addSec(-1);
                            } else {
                                board[i+1][j-1].addSec(1);
                            }
                        }
                    }
                } else
//---------------KING-----------------
                if (board[i][j].getPiece() == 'K') {
                    for (int row = 0; row < 3; row++) {
                        for (int col = 0; col < 3; col++) {
                            try {
                                if (board[i-1+row][j-1+col].getPiece() == 'O') {
                                    if (ijwhite == AIwhite) {
                                        int[] tarr = {i,j,i-1+row,j-1+col}; PosAIMoves.add(tarr);
                                    }
                                    board[i][j].addVal(0.1);
                                } else {
                                    if (board[i-1+row][j-1+col].getWhite() == ijwhite) {
                                        if (board[i-1+row][j-1+col].getPiece() != 'K') {
                                            board[i-1+row][j-1+col].addSec(1);
                                        } else {
                                            board[i-1+row][j-1+col].addSec(-1);
                                        }
                                    }
                                }
                            } catch (Exception e) { /*nothing?*/ }
                        }
                    }
                    //INSERT KING SAFETY HERE
                    //
                    //
                    //
                    //HERE
                }
            }
        }
    }




    //All Mouse methods
    public void mouseClicked(MouseEvent m) {
        /*
           ////System.out.println("Mouse Clicked"); 
           int x = m.getX()/80;
           int y = m.getY()/80;
           try {
           char p = board[y][x].getPiece();
           if (selected) {
           board[y][x] = board[selecY][selecX]; 
           board[selecY][selecX] = new Square('O',false);
           selected = false;
           w.transfer(board);
           f.repaint();
           } else {
           selecX = x;
           selecY = y;
           selected = true;
           }
           } catch (Exception e) {
        //Out of bounds!
           }
           */
    }
    public void mousePressed(MouseEvent m) {
        //        ////System.out.println("Mouse Clicked"); 
        int x = m.getX()/80;
        int y = m.getY()/80;
        try {
            char p = board[y][x].getPiece();
            if (selected && isValid(selecX,selecY,x,y,board)) {
                System.out.println("Player Moved");
                Square temp = board[selecY][selecX];
                board[selecY][selecX] = new Square('O',false);
                board[y][x] = temp;
                selected = false;
                wturn = !wturn;
                w.transfer(board);
                w.repaint();
                //System.out.println("Ran");
                Run();
            } else {
                if (board[y][x].getPiece() != 'O') {
                    selecX = x;
                    selecY = y;
                    selected = true;
                    w.sH(true);
                    int[][] coords = new int[2][1];
                    coords[0][0] = selecX; coords[1][0] = selecY;
                    w.highlight(coords);
                    w.repaint();
                    w.sH(false);
                }
            }
        } catch (Exception e) {
            //Out of bounds!
        }
    }
    public void mouseReleased(MouseEvent m) {}
    public void mouseEntered(MouseEvent m) {}
    public void mouseExited(MouseEvent m) {}

    public static boolean isValid(int x1, int y1, int x2, int y2, Square[][] grid) {
        if (grid[y1][x1].getPiece() == 'O') {
            return false;
        }
        if (x1 < 0 || x2 < 0 || y1 < 0 || y2 < 0 || x1 > 7 || x2 > 7 || y1 > 7 || y2 > 7) {
            return false;
        }
        if (x1 == x2 && y1 == y2) {
            return false;
        }
        if (grid[y1][x1].getWhite() != wturn){
            return false;
        }
        if (grid[y2][x2].getPiece() != 'O' && grid[y1][x1].getWhite() == grid[y2][x2].getWhite()) { 
            return false;
        }
        if (grid[y1][x1].getPiece() == 'N') {
            return (x2 == x1 + 1 && y2 == y1 + 2) || (x2 == x1 + 1 && y2 == y1 - 2) || (x2 == x1 + 2 && y2 == y1 + 1) || (x2 == x1 + 2 && y2 == y1 - 1) || (x2 == x1 - 1 && y2 == y1 + 2) || (x2 == x1 - 1 && y2 == y1 - 2) || (x2 == x1 - 2 && y2 == y1 + 1) || (x2 == x1 - 2 && y2 == y1 - 1);  
        }
        if (grid[y1][x1].getPiece() == 'B') {
            return diaValid(x1,y1,x2,y2,grid);
        }
        if (grid[y1][x1].getPiece() == 'R') {
            return lineValid(x1,y1,x2,y2,grid);
        }
        if (grid[y1][x1].getPiece() == 'Q') {
            return lineValid(x1,y1,x2,y2,grid) || diaValid(x1,y1,x2,y2,grid);
        }
        if (grid[y1][x1].getPiece() == 'P') {
            int dir = 1;
            if (grid[y1][x1].getWhite()) {
                dir = -1;
            }
            if (x1-x2 != 0) {
                return Math.abs(x1-x2) == 1 && y2-y1 == dir && grid[y1][x1].getWhite() != grid[y2][x2].getWhite();
            }
            if (y2 - y1 == 2*dir) {
                return (y1 == 1 || y1 == 6) && (grid[y1+dir][x1].getPiece() == 'O');
            }
            return y2 - y1 == dir;
        }
        if (grid[y1][x1].getPiece() == 'K') {
            return !(Math.abs(x1 - x2) > 1 || Math.abs(y1 - y2) > 1);
        }
        return true;
    }
    public static boolean diaValid(int x1, int y1, int x2, int y2, Square[][] grid) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        if (dy == dx) {
            int h = (x2 - x1)/dx;
            int k = (y2 - y1)/dy;
            for (int i = 1; i < Math.abs(x1-x2); i++) {
                if (grid[y1+k*i][x1+h*i].getPiece() != 'O') {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }   
    public static boolean lineValid(int x1, int y1, int x2, int y2, Square[][] grid) {  
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        int h = 0;
        int k = 0;
        int dif = 0;
        if (dx == 0 && dy != 0) {
            k = (y2-y1)/dy;
            dif = dy;
        } else if (dy == 0 && dx != 0) {
            h = (x2 -x1)/dx;
            dif = dx;
        } else {
            return false;
        }
        for (int i = 1; i < dif; i++) {
            if (grid[y1+k*i][x1+h*i].getPiece() != 'O') {
                return false;
            }
        }
        return true;
    }
    private static Square[][] copy(Square[][] temp) {
        Square[][] ret = new Square[temp.length][temp[0].length];
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                ret[i][j] = copys(temp[i][j]);
            }
        }
        return ret;
    }
    private static Square copys(Square s) {
        Square ret = new Square(s.getPiece(),s.getWhite());
        ret.setVal(s.getVal());
        ret.setSec(s.getSec());
        return ret;
    }
} 
