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
    private static List<List<Integer>> grid;
    private static int[] premier = new int[]{2,3,5,7};
    private static List<int[]> domains = new ArrayList<>();
    
    public static void createBoxBasedSolver()
    {
        //Create possible domains
        domains.add(new int[]{-1}); // 0 line can be drawn
        domains.add(new int[]{2,3,5,7}); // 1 line can be drawn
        domains.add(new int[]{6,10,14,15,21,35});// 2 line can be drawn
        domains.add(new int[]{30, 42, 70, 105});// 3 line can be drawn
        domains.add(new int[]{-1,2,3,5,7,6,10,14,15,21,35,30,42,70,105}); // 0 or 1 or 2 or 3 line can be drawn
        
        
        //Create a grid for testing
        grid = new ArrayList<>();
        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();
        List<Integer> l3 = new ArrayList<>();
        List<Integer> l4 = new ArrayList<>();
        
        l1.add(-1);
        l1.add(-1);
        l1.add(1);
        l1.add(0);
        l2.add(-1);
        l2.add(-1);
        l2.add(-1);
        l2.add(2);
        l3.add(1);
        l3.add(3);
        l3.add(-1);
        l3.add(-1);
        l4.add(-1);
        l4.add(-1);
        l4.add(-1);
        l4.add(1);
        grid.add(l1);
        grid.add(l2);
        grid.add(l3);
        grid.add(l4);
        
        
    }
    
    
    
    
    public static Solution solve()
    {
        Model model = new Model("Box-Based SlitherLink Solver");
        //Add all var with their domain. Their domain correspond to an array of value depending of thenumber inscribed at the same position on the grid to solve.
        IntVar[][] vars = new IntVar[grid.size()][grid.get(0).size()];        
        for(int i = 0; i < grid.size(); i++)
        {
            for(int j = 0; j < grid.get(i).size(); j++)
            {
                if(grid.get(i).get(j) == -1)
                    vars[i][j] = model.intVar("B"+i+","+j, domains.get(4));
                else
                    vars[i][j] = model.intVar("B"+i+","+j, domains.get(grid.get(i).get(j)));
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
        
        for(int i = 0; i < grid.size(); i++)
        {
            for(int j = 0; j < grid.get(i).size(); j++)
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
                else if(i == grid.size() - 1 && j == 0)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i-1][j], v7, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i-1][j], v3, v0).reify(), model.mod(vars[i][j+1], v2, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i][j+1], v5, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i][j+1], v2, v0).reify(), model.mod(vars[i-1][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.mod(vars[i][j], v7, v0), model.sum(new BoolVar[]{model.mod(vars[i][j+1], v5, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.mod(vars[i][j], v5, v0), model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i-1][j], v7, v0).reify()}, "=", 1)));
                }
                else if(i == 0 && j == grid.get(0).size() - 1)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.and(model.mod(vars[i][j], v3, v0), model.sum(new BoolVar[]{model.mod(vars[i][j-1], v2, v0).reify(), model.mod(vars[i][j], v7, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.and(model.mod(vars[i][j], v2, v0), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i+1][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j-1], v5, v0).reify(), model.mod(vars[i][j], v7, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i+1][j], v3, v0).reify(), model.mod(vars[i][j], v3, v0).reify()}, "=", 1)));     
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.and(model.sum(new BoolVar[]{model.mod(vars[i][j], v2, v0).reify(), model.mod(vars[i][j-1], v2, v0).reify()}, "=", 1), model.sum(new BoolVar[]{model.mod(vars[i][j], v5, v0).reify(), model.mod(vars[i][j-1], v5, v0).reify(), model.mod(vars[i+1][j], v7, v0).reify()}, "=", 1)));                                                             
                }
                else if(i == grid.size() - 1&& j == grid.get(0).size() - 1)
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
                else if(i == grid.size() - 1)
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
                else if(j == grid.get(0).size() - 1)
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
        
        for(int i = 0; i < grid.size(); i++)
        {
            for(int j = 0; j < grid.get(i).size(); j++)
            {
                if(i != 0)
                {
                    model.ifThen(model.mod(vars[i][j], v2, v0), model.mod(vars[i - 1][j], v5, v0));
                }
                if(i != grid.size() - 1)
                {
                    model.ifThen(model.mod(vars[i][j], v5, v0), model.mod(vars[i + 1][j], v2, v0));
                }
                if(j != 0)
                {
                    model.ifThen(model.mod(vars[i][j], v7, v0), model.mod(vars[i][j - 1], v3, v0));
                }
                if(j != grid.get(0).size() - 1)
                {
                    model.ifThen(model.mod(vars[i][j], v3, v0), model.mod(vars[i][j + 1], v7, v0));
                }
            }
        }
        
        
        
        Solution sol = model.getSolver().findSolution();
        if(sol == null)
            System.out.println("NO SOL");
        else
            for(int i = 0; i < grid.size(); i++)
            {
                for(int j = 0; j < grid.get(i).size(); j++)
                {
                    System.out.println("B" + i + ", " + j + ": " + sol.getIntVal(vars[i][j]));
                }
            }
        
        
        return sol;
    }
}
