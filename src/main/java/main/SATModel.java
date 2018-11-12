package main;

import static org.chocosolver.solver.constraints.nary.cnf.LogOp.and;
import static org.chocosolver.solver.constraints.nary.cnf.LogOp.nor;
import static org.chocosolver.solver.constraints.nary.cnf.LogOp.or;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;

public class SATModel {
    public SlitherLink problem;
    public Model model;
    
    /**
     * The first array contains horizontal vars
     * The second contains vertical vars
     */
    public BoolVar[][] variables;
    
    public SATModel(SlitherLink p) {
        problem = p;
        // Initiate variables (one for each edge)
        BoolVar[][] variables = new BoolVar[2][];
        variables[0] = new BoolVar[p.getY() * (p.getX()+1)];
        System.out.println(variables[0].length);
        variables[1] = new BoolVar[p.getX() * (p.getY()+1)];
        System.out.println(variables[1].length);
        model = new Model();
        
        // Horizontal edges
        for(int i=0 ; i<p.getX()+1; i++) {
            for(int j=0 ; j<p.getY(); j++) {
                int id = i*p.getY() + j;
                String strId = "Y"+Integer.toString(id);
                variables[0][id] = model.boolVar(strId);
            }
        }
        // Vertical edges
        for(int i=0 ; i<p.getX(); i++) {
            for(int j=0 ; j<p.getY()+1; j++) {
                int id = i*(p.getY()+1) + j;
                String strId = "X"+Integer.toString(id);
                variables[1][id] = model.boolVar(strId);
            }
        }
        
        // Adding general constraints
        for(int i=0 ; i<p.getX(); i++) {
            for(int j=0 ; j<p.getY(); j++) {
                BoolVar x0 = variables[1][(p.getY()+1)*i+j];
                BoolVar x1 = variables[1][(p.getY()+1)*i+j+1];
                BoolVar y0 = variables[0][i*p.getY()+j];
                BoolVar y1 = variables[0][(i+1)*p.getY()+j];
                switch(p.getCells()[i][j]) {
                    case 0:
                        model.sum(new BoolVar[]{x0,x1,y0,y1}, "=", 0).post();
                        //System.out.println("adding 0 for "+i+","+j+": "+x0.getName()+","+x1.getName()+","+y0.getName()+","+y1.getName());
                        break;
                    case 1:
                        model.sum(new BoolVar[]{x0,x1,y0,y1}, "=", 1).post();
                        break;
                    case 2:
                        model.sum(new BoolVar[]{x0,x1,y0,y1}, "=", 2).post();
                        break;
                    case 3:
                        model.sum(new BoolVar[]{x0,x1,y0,y1}, "=", 3).post();
                        break;
                    default:
                        break;
                }
            }
            //model.post();
        }
        
        // Adding constraint to check unique cycle:
        // xor on preceding and folowing edges for every true edge.
        // Begin with the horizontal edges
        for(int i=0; i<variables[0].length; i++) {
            System.out.println("checking var "+variables[0][i].getName());
            // upper edges
            if(i < p.getY()) {
                // left extremity
                if(i%p.getY() == 0) {
                    BoolVar[] left = {variables[1][i]};
                    BoolVar[] right = {variables[0][i+1], variables[1][i+1]};
                    
                    System.out.println("upper left, "+variables[0][i].getName()+": left={"+variables[1][i].getName()+"}, right={"+variables[0][i+1].getName()+", "+variables[1][i+1].getName()+"}");
                    model.ifThen(variables[0][i], model.and(model.sum(left, "=", model.intVar(1)), model.sum(right, "=", model.intVar(1))));
                }
                else if (i%p.getY() == p.getY()-1) { // right extremity
                    BoolVar[] left = {variables[0][i-1], variables[1][i]};
                    BoolVar[] right = {variables[1][i+1]};
                    
                    model.ifThen(variables[0][i], model.and(model.sum(left, "=", model.intVar(1)), model.sum(right, "=", model.intVar(1))));
                } else { // middle
                    BoolVar[] left = {variables[0][i-1], variables[1][i]};
                    BoolVar[] right = {variables[0][i+1], variables[1][i+1]};
                    
                    model.ifThen(variables[0][i], model.and(model.sum(left, "=", model.intVar(1)), model.sum(right, "=", model.intVar(1))));
                }
            }
            else if(i >= p.getX()*p.getY()) { // lower edges
                // left extremity
                if(i%p.getY() == 0) {
                    int tmp = (p.getY()+1) * (p.getX()-1);
                    BoolVar[] left = {variables[1][tmp]};
                    BoolVar[] right = {variables[1][tmp+1], variables[0][i+1]};
                    
                    model.ifThen(variables[0][i], model.and(model.sum(left, "=", model.intVar(1)), model.sum(right, "=", model.intVar(1))));
                }
                else if (i%p.getY() == p.getY()-1) { // right extremity
                    int tmp = (p.getY()+1) * (p.getX()-1) + p.getY();
                    BoolVar[] left = {variables[0][i-1], variables[1][tmp-1]};
                    BoolVar[] right = {variables[1][tmp]};
                    
                    model.ifThen(variables[0][i], model.and(model.sum(left, "=", model.intVar(1)), model.sum(right, "=", model.intVar(1))));
                } else { // middle
                    int col = i%p.getY();
                    int maxLine = (p.getY()+1) * (p.getX()-1);
                    BoolVar[] left = {variables[0][i-1], variables[1][maxLine+col]};
                    BoolVar[] right = {variables[0][i+1], variables[1][maxLine+col+1]};
                    
                    System.out.println("lower middle, "+variables[0][i].getName()+": left={"+variables[0][i-1].getName()+", "+variables[1][maxLine+col].getName()+"}, right={"+variables[0][i+1].getName()+", "+variables[1][maxLine+col+1].getName()+"}");
                    model.ifThen(variables[0][i], model.and(model.sum(left, "=", model.intVar(1)), model.sum(right, "=", model.intVar(1))));
                }
            } else { 
                // left extremity
                if(i%p.getY() == 0) {
                    int col = i%p.getY();
                    int line = (int)i/p.getY();
                    int tmp = (line-1) * (p.getY()+1) + col;
                    BoolVar[] left = {variables[1][tmp], variables[1][tmp+p.getY()+1]};
                    BoolVar[] right = {variables[0][i+1], variables[1][tmp+1], variables[1][tmp+p.getY()+2]};
                    
                    System.out.println("middle left, "+variables[0][i].getName()+": left={"+variables[1][tmp].getName()+", "+variables[1][tmp+p.getY()+1].getName()+"}, right={"+variables[0][i+1].getName()+", "+variables[1][tmp+1].getName()+", "+variables[1][tmp+p.getY()+2].getName()+"}");
                    model.ifThen(variables[0][i], model.and(model.sum(left, "=", model.intVar(1)), model.sum(right, "=", model.intVar(1))));
                }
                else if (i%p.getY() == p.getY()-1) { // right extremity
                    int col = i%p.getY();
                    int line = (int)i/p.getY();
                    int tmp = (line-1) * (p.getY()+1) + col;
                    BoolVar[] left = {variables[0][i-1], variables[1][tmp], variables[1][tmp+p.getY()+1]};
                    BoolVar[] right = {variables[1][tmp+1], variables[1][tmp+p.getY()+2]};
                    
                    model.ifThen(variables[0][i], model.and(model.sum(left, "=", model.intVar(1)), model.sum(right, "=", model.intVar(1))));
                } else { // middle
                    int col = i%p.getY();
                    int line = (int)i/p.getY();
                    int tmp = (line-1) * (p.getY()+1) + col;
                    BoolVar[] left = {variables[0][i-1], variables[1][tmp], variables[1][tmp+p.getY()+1]};
                    BoolVar[] right = {variables[0][i+1], variables[1][tmp+1], variables[1][tmp+p.getY()+2]};
                    
                    model.ifThen(variables[0][i], model.and(model.sum(left, "=", model.intVar(1)), model.sum(right, "=", model.intVar(1))));
                }
            }
        }
        // Then the vertical edges
        for(int i=0; i<variables[1].length; i++) {
            int col = i%(p.getY()+1);
            int line = (int)i/(p.getY()+1);
            
            // left edges
            if(i%(p.getY()+1) == 0) {
                // upper extremity
                if(i == 0) {
                    BoolVar[] up = {variables[0][0]};
                    BoolVar[] down = {variables[0][(line + 1)*p.getY()], variables[1][p.getY()+1]};
                    
                    model.ifThen(variables[1][i], model.and(model.sum(up, "=", model.intVar(1)), model.sum(down, "=", model.intVar(1))));
                }
                else if (i == (p.getY()+1) * (p.getX()-1)) { // lower extremity
                    BoolVar[] up = {variables[1][i-(p.getY()+1)], variables[0][line * p.getX()]};
                    BoolVar[] down = {variables[0][p.getX() * p.getY()]};
                    
                    model.ifThen(variables[1][i], model.and(model.sum(up, "=", model.intVar(1)), model.sum(down, "=", model.intVar(1))));
                } else { // middle
                    BoolVar[] up = {variables[1][i-(p.getY()+1)], variables[0][line * p.getX()]};
                    BoolVar[] down = {variables[1][i+(p.getY()+1)], variables[0][(line+1)*p.getX()]};
                    
                    model.ifThen(variables[1][i], model.and(model.sum(up, "=", model.intVar(1)), model.sum(down, "=", model.intVar(1))));
                }
            }
            else if(i%(p.getY()+1) == p.getY()) { // right edges
                // upper extremity
                if(i == p.getY()) {
                    BoolVar[] up = {variables[0][p.getX()-1]};
                    BoolVar[] down = {variables[1][i+p.getY()+1], variables[0][2*p.getX()-1]};
                    
                    model.ifThen(variables[1][i], model.and(model.sum(up, "=", model.intVar(1)), model.sum(down, "=", model.intVar(1))));
                }
                else if (i == variables[1].length-1) { // lower extremity
                    int tmp = variables[0].length-1;
                    
                    BoolVar[] up = {variables[1][i-(p.getY()+1)], variables[0][tmp-p.getX()]};
                    BoolVar[] down = {variables[0][tmp]};
                    
                    model.ifThen(variables[1][i], model.and(model.sum(up, "=", model.intVar(1)), model.sum(down, "=", model.intVar(1))));
                } else { // middle
                    BoolVar[] up = {variables[1][i-(p.getY()+1)], variables[0][(line + 1) * p.getX() - 1]};
                    BoolVar[] down = {variables[1][i+(p.getY()+1)], variables[0][(line + 2) * p.getX() - 1]};
                    
                    model.ifThen(variables[1][i], model.and(model.sum(up, "=", model.intVar(1)), model.sum(down, "=", model.intVar(1))));
                }
            } else { // middle
                // upper extremity
                if(i < p.getY()) {
                    BoolVar[] up = {variables[0][i-1], variables[0][i]};
                    BoolVar[] down = {variables[0][i+p.getX()], variables[0][i+p.getX()-1], variables[1][i+p.getY()+1]};
                    
                    model.ifThen(variables[1][i], model.and(model.sum(up, "=", model.intVar(1)), model.sum(down, "=", model.intVar(1))));
                }
                else if(i > (p.getY()+1)*(p.getX()-1)) { // lower extremity
                    BoolVar[] up = {variables[0][i - p.getX()], variables[0][i + 1 - p.getX()], variables[1][i - (p.getY()+1)]};
                    BoolVar[] down = {variables[0][i], variables[0][i+1]};
                    
                    model.ifThen(variables[1][i], model.and(model.sum(up, "=", model.intVar(1)), model.sum(down, "=", model.intVar(1))));
                } else { // middle
                    int tmp = line * p.getX() + col;
                    BoolVar[] up = {variables[0][tmp], variables[0][tmp-1], variables[1][i - (p.getY()+1)]};
                    BoolVar[] down = {variables[0][tmp+p.getX()], variables[0][tmp+p.getX()-1], variables[1][i + (p.getY()+1)]};
                    
                    model.ifThen(variables[1][i], model.and(model.sum(up, "=", model.intVar(1)), model.sum(down, "=", model.intVar(1))));
                }
            }
        }
    }
}
