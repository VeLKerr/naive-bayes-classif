package com.naivebayesclassifier;

import java.util.List;
import static com.naivebayesclassifier.Main.PART_NUMBER;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class DataSetPartition {
    private final List<Integer> learning;
    private final List<Integer> testing;
    private int testingSize;

    public DataSetPartition(int testingSize) {
        this.testingSize = testingSize;
        learning = new ArrayList<>();
        testing = new ArrayList<>();
        setInitial();
    }

    public DataSetPartition() {
        learning = new ArrayList<>();
        testing = new ArrayList<>();
    }
    
    private void setInitial(){
        for(int i=0; i<testingSize; i++){
            testing.add(PART_NUMBER - i);
        }
        for(int j=1; j<=PART_NUMBER - testingSize; j++){
            learning.add(j);
        }
    }

    public void setTestingSize(int testingSize) {
        this.testingSize = testingSize;
        setInitial();
    }
    
    public void nextExperiment(){
        int max = Collections.max(testing);
        int min = Collections.min(testing);
        if(min > 1){
            testing.remove(max);
            learning.add(max);
            testing.add(min - 1);
            learning.remove(min - 1);
        }
        else
            if(min == 1 && max != PART_NUMBER){
                testing.remove(max);
                learning.add(max);
                testing.add(PART_NUMBER);
                learning.remove(PART_NUMBER);
            }
            else{
                int learnMax = Collections.max(learning);
                int learnMin = Collections.min(learning);
                testing.add(learnMax);
                learning.remove(learnMax);
                testing.remove(learnMin - 1);
                learning.add(learnMin - 1);
            }
    }

    public List<Integer> getLearning() {
        return learning;
    }

    public List<Integer> getTesting() {
        return testing;
    }
    
}
