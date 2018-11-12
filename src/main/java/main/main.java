package main;

import java.util.HashMap;
import javafx.util.Pair;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.BoolVar;

public class main
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
        SlitherLink problem = new SlitherLink(7, 6, 4);
        Model model = new Model("SlitherLink feasbility problem");
        BoolVar[] vars = new BoolVar[problem.getX()*(problem.getY()+1) + problem.getY()*(problem.getX()+1)];
        
        System.out.println(problem);
    
        BoxBasedSolver bbs = new BoxBasedSolver(problem);
        
        bbs.solve();    
    */
        /*
        HashMap<String,Long> avg = new HashMap<>();
        
        for(int i = 13; i < 14; i++){
            SlitherLink sl = SlitherLink.fromFile(i + ".txt");
            long zero = 0;
            avg.put(i + ".txt",zero);
            for(int j = 0; j < 100; j++){
                System.out.println(i + ".txt");
                BoxBasedSolver bbs = new BoxBasedSolver(sl);
                long start = System.currentTimeMillis();
                if(bbs.solve()!= null){
                    System.out.println("Not Null");
                }
                avg.put(i + ".txt", avg.get(i + ".txt") + System.currentTimeMillis() - start);
            }
        }
        
        System.out.println(avg);
*/
        HashMap<Pair<Integer,Integer>,Long> avg_w_fails_bbs = new HashMap<>();
        HashMap<Pair<Integer,Integer>,Long> avg_wo_fails_bbs = new HashMap<>();
        for(int i = 3; i < 9; i++){
            for(int r = 0; r < 11; r++){
                long zero = 0;
                Pair<Integer,Integer> pair_sl = new Pair<>(i,r);
                avg_w_fails_bbs.put(pair_sl,zero);
                avg_wo_fails_bbs.put(pair_sl,zero);
                int success = 0;
                for(int j = 0; j < 10; j++){
                    SlitherLink sl = new SlitherLink(i,i,r);
                    BoxBasedSolver bbs = new BoxBasedSolver(sl);
                    long start = System.currentTimeMillis();
                    Solution res = bbs.solve();
                    long duration = System.currentTimeMillis() - start;
                    if(res!= null){
                        avg_wo_fails_bbs.put(pair_sl,avg_wo_fails_bbs.get(pair_sl) + duration);
                        success++;
                    }
                    avg_w_fails_bbs.put(pair_sl,avg_w_fails_bbs.get(pair_sl) + duration);
                }
                
                if(success != 0){
                    avg_wo_fails_bbs.put(pair_sl,(long)(avg_wo_fails_bbs.get(pair_sl)/success));
                }
                avg_w_fails_bbs.put(pair_sl,(long)(avg_w_fails_bbs.get(pair_sl)/10));
            
            }
        }
        
        System.out.println("With : ");
        
        for(Pair<Integer,Integer> p : avg_w_fails_bbs.keySet()){
            System.out.println(p.getKey() + ";" + p.getValue() + ";" + avg_w_fails_bbs.get(p));
        }
        
        System.out.println("With out : ");
        for(Pair<Integer,Integer> p : avg_wo_fails_bbs.keySet()){
            System.out.println(p.getKey() + ";" + p.getValue() + ";" + avg_wo_fails_bbs.get(p));
        }
        /*
        for(Pair<Integer,Integer> key : avg_w_fails.keySet()){
            avg_w_fails.put(key, avg_w_fails.get(key)/10);
        }
        
        System.out.println("With : " + avg_w_fails);
        
        /*

        for(int r = 0; r < 10; r++){
            System.out.println(new SlitherLink(4,4,r));
        }
        */
        //System.out.println(avg);
        
    }
}
