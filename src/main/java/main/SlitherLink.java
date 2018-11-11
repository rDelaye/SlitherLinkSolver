package main;

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
}
