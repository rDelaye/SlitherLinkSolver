package main;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;

public class main
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SlitherLink problem = new SlitherLink(7, 6, 4);
        Model model = new Model("SlitherLink feasbility problem");
        BoolVar[] vars = new BoolVar[problem.getX()*(problem.getY()+1) + problem.getY()*(problem.getX()+1)];
        
        System.out.println(problem);
    }
}
