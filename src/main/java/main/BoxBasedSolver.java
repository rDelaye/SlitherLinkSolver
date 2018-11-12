/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.List;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.explanations.ExplanationEngine;
import org.chocosolver.solver.search.loop.monitors.IMonitorOpenNode;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

/**
 *
 * @author Robin
 */
public class BoxBasedSolver
{    
    private  int[] premier = new int[]{2,3,5,7};
    private  List<int[]> domains = new ArrayList<>();
    private Solution sol;
    private SlitherLink sl;
    public BoxBasedSolver(SlitherLink sl)
    {
        //Create possible domains
        domains.add(new int[]{-1}); // 0 line can be drawn
        domains.add(new int[]{2,3,5,7}); // 1 line can be drawn
        domains.add(new int[]{6,10,14,15,21,35});// 2 line can be drawn
        domains.add(new int[]{30, 42, 70, 105});// 3 line can be drawn
        domains.add(new int[]{-1,2,3,5,7,6,10,14,15,21,35,30,42,70,105}); // 0 or 1 or 2 or 3 line can be drawn
        this.sl = sl;
        
        
    }
    
    
    
    
    public Solution solve()
    {
        int[][] grid = sl.getCells();
        Model model = new Model("Box-Based SlitherLink Solver");
        //Add all var with their domain. Their domain correspond to an array of value depending of thenumber inscribed at the same position on the grid to solve.
        IntVar[][] vars = new IntVar[sl.getX()][sl.getY()];        
        for(int i = 0; i < sl.getX(); i++)
        {
            for(int j = 0; j < sl.getY(); j++)
            {
                if(grid[i][j] == Integer.MIN_VALUE)
                    vars[i][j] = model.intVar("B"+i+","+j, domains.get(4));
                else
                    vars[i][j] = model.intVar("B"+i+","+j, domains.get(grid[i][j]));
            }
        }
        
        //Add continuity constraints : for every "line" drawn, there has to be 2 neightbour "line" (on the same var or neightbour var)
        IntVar v0 = model.intVar(0);
        IntVar v2 = model.intVar(2);
        IntVar v3 = model.intVar(3);
        IntVar v5 = model.intVar(5);
        IntVar v7 = model.intVar(7);
        //Creation "loop" constraints
        //Créer des contraintes pour chaque cases
        
        for(int i = 0; i < sl.getX(); i++)
        {
            for(int j = 0; j < sl.getY(); j++)
            {
                //LIGNE DU HAUT
                //SI ligne en haut de la case, alors "Y a-t-il une et une seule ligne adjacente a gauche" ET "Y a-t-il une et une seule ligne adjacente a droite" (Représenté par SUM(BoolVar des constraints)
                if(i == 0 && j == 0)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.mod(vars[i][j], v7, v0), model.sum(new BoolVar[]{model.mod(vars[i][j + 1], v2, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i][j + 1], v2, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i + 1][j], v3, v0).reify(), model.mod(vars[i][j + 1], v5, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i + 1][j], v7, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j + 1], v5, v0).reify(), model.mod(vars[i+1][j], v3, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.mod(vars[i][j], v2, v0), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i +1 ][j], v7, v0).reify()}, "=", 1)));     
                }
                else if(i == sl.getX() - 1 && j == 0)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i-1][j], v7, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i-1][j], v3, v0).reify(), model.mod(vars[i][j+1], v2, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i][j+1], v5, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i][j+1], v2, v0).reify(), model.mod(vars[i-1][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.mod(vars[i][j], v7, v0), model.sum(new BoolVar[]{model.mod(vars[i][j+1], v5, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.mod(vars[i][j], v5, v0), model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i-1][j], v7, v0).reify()}, "=", 1)));
                }
                else if(i == 0 && j == sl.getY() - 1)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.mod(vars[i][j], v3, v0), model.sum(new BoolVar[]{model.mod(vars[i][j-1], v2, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.mod(vars[i][j], v2, v0), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i+1][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j-1], v5, v0).reify(), model.mod(vars[i][j], v7, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i+1][j], v3, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i][j-1], v2, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i][j-1], v5, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify()}, "=", 1)));                                                             
                }
                else if(i == sl.getX() - 1&& j == sl.getY() - 1)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j-1], v2, v0).reify(), model.mod(vars[i-1][j], v7, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i-1][j], v3, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i-1][j], v3, v0).reify()}, "=", 1), model.mod(vars[i][j], v5, v0)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j-1], v5, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1), model.mod(vars[i][j], v3, v0)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i][j-1], v2, v0).reify(), model.mod(vars[i-1][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i][j-1], v5, v0).reify()}, "=", 1)));                                                                                 
                }
                else if(i == 0)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v7, v0).reify(), model.mod(vars[i][j-1], v2, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j+1], v2, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i][j+1], v2, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i][j+1], v5, v0).reify(), model.mod(vars[i+1][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j+1], v5, v0).reify(), model.mod(vars[i][j], v3, v0).reify(), model.mod(vars[i+1][j], v3, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i+1][j], v7, v0).reify(), model.mod(vars[i][j], v7, v0).reify(), model.mod(vars[i][j-1], v5, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i][j-1], v2, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify(), model.mod(vars[i][j-1], v5, v0).reify()}, "=", 1)));                                                             
                } 
                else if(i == sl.getX() - 1)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i-1][j], v7, v0).reify(), model.mod(vars[i][j-1], v2, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i-1][j], v3, v0).reify(), model.mod(vars[i][j + 1], v2, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i][j+1], v5, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i][j+1], v2, v0).reify(), model.mod(vars[i-1][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j-1], v5, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v3, v0).reify(), model.mod(vars[i][j+1], v5, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i][j-1], v2, v0).reify(), model.mod(vars[i-1][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i][j-1], v5, v0).reify()}, "=", 1)));                                                                                 
                }
                else if(j == 0)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i-1][j], v7, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j+1], v2, v0).reify(), model.mod(vars[i][j], v3, v0).reify(), model.mod(vars[i-1][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i-1][j], v3, v0).reify(), model.mod(vars[i][j+1], v2, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i][j+1], v5, v0).reify(), model.mod(vars[i+1][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v7, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j+1], v5, v0).reify(), model.mod(vars[i+1][j], v3, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i-1][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify()}, "=", 1)));                                                                                 
                }
                else if(j == sl.getY() - 1)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j-1], v2, v0).reify(), model.mod(vars[i-1][j], v7, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i-1][j], v3, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i-1][j], v3, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i+1][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i+1][j], v3, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j-1], v5, v0).reify(), model.mod(vars[i][j], v7, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i-1][j], v7, v0).reify(), model.mod(vars[i][j-1], v2, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i][j-1], v5, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify()}, "=", 1)));
                }
                else
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j-1], v2, v0).reify(), model.mod(vars[i-1][j], v7, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j+1], v2, v0).reify(), model.mod(vars[i-1][j], v3, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i-1][j], v3, v0).reify(), model.mod(vars[i][j+1], v2, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i+1][j], v3, v0).reify(), model.mod(vars[i][j+1], v5, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j-1], v5, v0).reify(), model.mod(vars[i][j], v7, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j+1], v5, v0).reify(), model.mod(vars[i+1][j], v3, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i-1][j], v7, v0).reify(), model.mod(vars[i][j-1], v2, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify(), model.mod(vars[i][j-1], v5, v0).reify()}, "=", 1)));
                }
            }            
        }   
        
        for(int i = 0; i < sl.getX(); i++)
        {
            for(int j = 0; j < sl.getY(); j++)
            {
                if(i != 0)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.mod(vars[i - 1][j], v5, v0));
                }
                if(i != sl.getX() - 1)
                {
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.mod(vars[i + 1][j], v2, v0));
                }
                if(j != 0)
                {
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.mod(vars[i][j - 1], v3, v0));
                }
                if(j != sl.getY() - 1)
                {
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.mod(vars[i][j + 1], v7, v0));
                }
            }
        }
        
        
        
        sol = model.getSolver().findSolution();
        /*if(sol == null)
            System.out.println("NO SOL");
        else
            for(int i = 0; i < sl.getX(); i++)
            {
                for(int j = 0; j < sl.getY(); j++)
                {
                    //System.out.println("B" + i + ", " + j + ": " + sol.getIntVal(vars[i][j]));
                }
            }
        */
        
        return sol;
    }
}
