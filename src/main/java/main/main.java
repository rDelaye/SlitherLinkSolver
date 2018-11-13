package main;

import java.util.HashMap;
import javafx.util.Pair;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;

public class main
{
    
    private static int NB_ITERACTION = 10;
    
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
        
        HashMap<String,Long> avg_bbs = new HashMap<>();
        HashMap<String,Long> avg_sat = new HashMap<>();
        
        for(int i = 12; i < 17; i++){
            String file = i + ".txt";
            SlitherLink sl = SlitherLink.fromFile(file);
            long zero = 0;
            avg_bbs.put(file,zero);
            avg_sat.put(file,zero);
            
            for(int j = 0; j < 10; j++){
                
                //BoxBased
                    BoxBasedSolver bbs = new BoxBasedSolver(sl);
                    long start = System.currentTimeMillis();
                    Solution res = bbs.solve();
                    long duration = System.currentTimeMillis() - start;
                    avg_bbs.put(file, avg_bbs.get(file) + duration);
                /**/
                    //SAT
                    start = System.currentTimeMillis();
                    SATModel sm = new SATModel(sl);
                    Solver s = sm.model.getSolver();
                    s.solve();
                    duration = System.currentTimeMillis() - start;
                    avg_sat.put(file,avg_sat.get(file) + duration);
                
                }
                
            avg_bbs.put(file,(long)(avg_bbs.get(file)/(NB_ITERACTION)));

            avg_sat.put(file,(long)(avg_sat.get(file)/(NB_ITERACTION)));

        }
        System.out.println("SAT With : ");
        
        for(String p : avg_sat.keySet()){
            System.out.println(p + ";" + avg_sat.get(p));
        }
        
        System.out.println("BBS With : ");
        
        for(String p : avg_bbs.keySet()){
            System.out.println(p + ";" + avg_bbs.get(p));
        }
/**//*
        
        HashMap<Pair<Integer,Integer>,Long> avg_w_fails_sat = new HashMap<>();
        
        HashMap<Pair<Integer,Integer>,Long> avg_w_fails_bbs = new HashMap<>();
        HashMap<Pair<Integer,Integer>,Long> avg_wo_fails_bbs = new HashMap<>();
        for(int i = 6; i < 7 ; i++){
            for(int r = 1; r < 7; r++){
                long zero = 0;
                Pair<Integer,Integer> pair_sl = new Pair<>(i,r);
                avg_w_fails_bbs.put(pair_sl,zero);
                avg_w_fails_sat.put(pair_sl,zero);
                avg_wo_fails_bbs.put(pair_sl,zero);
                int success = 0;
                for(int j = i - 3; j < NB_ITERACTION; j++){
                    SlitherLink sl = new SlitherLink(i,i,r);
                    
                    //BoxBased
                    BoxBasedSolver bbs = new BoxBasedSolver(sl);
                    long start = System.currentTimeMillis();
                    Solution res = bbs.solve();
                    long duration = System.currentTimeMillis() - start;
                    
                    if(res!= null){
                        avg_wo_fails_bbs.put(pair_sl,avg_wo_fails_bbs.get(pair_sl) + duration);
                        success++;
                    }
                    avg_w_fails_bbs.put(pair_sl,avg_w_fails_bbs.get(pair_sl) + duration);
                /**//*
                    //SAT
                    start = System.currentTimeMillis();
                    SATModel sm = new SATModel(sl);
                    Solver s = sm.model.getSolver();
                    s.solve();
                    duration = System.currentTimeMillis() - start;
                    avg_w_fails_sat.put(pair_sl,avg_w_fails_sat.get(pair_sl) + duration);
                
                }
                
                if(success != 0){
                    avg_wo_fails_bbs.put(pair_sl,(long)(avg_wo_fails_bbs.get(pair_sl)/success));
                }
                avg_w_fails_bbs.put(pair_sl,(long)(avg_w_fails_bbs.get(pair_sl)/(NB_ITERACTION - (i-3))));
                
                avg_w_fails_sat.put(pair_sl,(long)(avg_w_fails_sat.get(pair_sl)/(NB_ITERACTION - (i-3))));
            
            }
        }
        
        System.out.println("BBS With : ");
        
        for(Pair<Integer,Integer> p : avg_w_fails_bbs.keySet()){
            System.out.println(p.getKey() + ";" + p.getValue() + ";" + avg_w_fails_bbs.get(p));
        }
        
        System.out.println("BBS With out : ");
        for(Pair<Integer,Integer> p : avg_wo_fails_bbs.keySet()){
            System.out.println(p.getKey() + ";" + p.getValue() + ";" + avg_wo_fails_bbs.get(p));
        }
        
        
        System.out.println("SAT With : ");
        
        for(Pair<Integer,Integer> p : avg_w_fails_sat.keySet()){
            System.out.println(p.getKey() + ";" + p.getValue() + ";" + avg_w_fails_sat.get(p));
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
        /**/
    }
}
