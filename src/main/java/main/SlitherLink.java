package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SlitherLink {

    private int X, Y, ratio = 0;
    private int[][] cells;

    public SlitherLink(int x, int y, int r) {
        X = x;
        Y = y;
        ratio = r;
        cells = new int[X][Y];
        generateRandom();
    }

    private void generateRandom() {
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                // fill approx 1/ratio cells
                if ( (int)(Math.random()*10 % ratio) == 0) {
                    cells[i][j] = (int)(Math.random()*10%4);
                }
                else {
                    cells[i][j] = Integer.MIN_VALUE;
                }
            }
        }
    }

    public int getX() {
        return X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public int getY() {
        return Y;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public int[][] getCells() {
        return cells;
    }

    public void setCells(int[][] cells) {
        this.cells = cells;
    }
    
    @Override
    public String toString() {
        String output = "";
        
        for (int i = 0; i < X; i++) {
            output += "|";
            for (int j = 0; j < Y; j++) {
                output += (cells[i][j] == Integer.MIN_VALUE) ? " |" : (cells[i][j] + "|");
            }
            output += "\n";
        }
        
        return output;
    }
    
    public static SlitherLink fromFile(String file){
        SlitherLink sl = new SlitherLink(0, 0, 0);
        FileReader input = null;
        try {
            input = new FileReader(file);
            BufferedReader bufRead = new BufferedReader(input);
            String myLine = null;
            myLine = bufRead.readLine();
            String[] dims = myLine.split(" ");
            sl.X = Integer.parseInt(dims[1]);
            sl.Y = Integer.parseInt(dims[0]);
            sl.cells = new int[sl.X][sl.Y];
            int j = 0;
            while ( (myLine = bufRead.readLine()) != null)
            {
                String[] array2 = myLine.split(" ");
                for (int i = 0; i < array2.length; i++){
                    System.out.println(array2[i]);
                    if(!array2[i].equals(".")){
                        sl.cells[i][j] = Integer.parseInt(array2[i]);
                    }else{
                        sl.cells[i][j] = Integer.MIN_VALUE;
                    }
                }
                j++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SlitherLink.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SlitherLink.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
                Logger.getLogger(SlitherLink.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
