package com.company;

import java.util.ArrayList;
import java.util.Map;

public class CrossValidation {

    private String[][] testTable; int t=0;
    boolean leaf=false;
    String[] singleSample;
    ArrayList<String> classes;
    int[][] cMat;
    private int dataColumn;

    public CrossValidation(String[][] testTable, int dataColumn, ArrayList classes) {

        this.testTable = testTable;
        this.dataColumn = dataColumn;
        this.classes = classes;
        cMat= new int[classes.size()][classes.size()];
        singleSample=new String[dataColumn];

    }

    public void matchMaking(Node node){
        if(node.leaf){
            if(node.decision.equals(singleSample[dataColumn-1]))
                cMat[classes.indexOf(node.decision)][classes.indexOf(node.decision)]+=1;
            /*t++; leaf=true; return;*/
            else
                cMat[classes.indexOf(node.decision)][classes.indexOf(singleSample[dataColumn-1])]++;

            leaf=true;
            return;
        }

        for(int i=0;i<dataColumn-1;i++){
            if(node.attribute.equals(testTable[0][i])){

                for (Map.Entry<String, Node> entry : node.nodes.entrySet()) {
                    String key = entry.getKey();
                    Node nord = entry.getValue();

                    if(key.equals(singleSample[i])){
                        singleSample[i]="";
                        matchMaking(nord);
                        if(leaf){
                            leaf=false;
                            return;
                        }
                    }
                }

            }
        }
    }

    public double calculateAccuracy(){

        double accuracy=0;
           for(int i=0;i<cMat.length;i++)
               accuracy+=cMat[i][i];

           accuracy/=(testTable.length-1);
           accuracy*=100;

        return accuracy;
    }
    public double calculatePrecision(){

        double precision=0,denom;

        for(int i=0;i<cMat.length;i++){
            denom=0;
            for(int j=0;j<cMat.length;j++){
                denom+=cMat[i][j];
            }
            precision+= cMat[i][i]/denom;
        }
        precision/=classes.size();

        return precision*100;
    }
    public double calculateF1(){

        double f1=0,denomP,denomR,precision,recall;

        for(int i=0;i<cMat.length;i++){
            denomP=denomR=0;

            for(int j=0;j<cMat.length;j++){
                denomP+=cMat[i][j];
                denomR+=cMat[j][i];
            }
            precision = cMat[i][i]/denomP;
            recall = cMat[i][i]/denomR;

            f1+= 2*precision*recall/(precision+recall);
        }

        f1/=classes.size();

        return f1*100;
    }
    public double calculateRecall(){

        double recall=0, denom;

        for(int i=0;i<cMat.length;i++){
            denom=0;
            for(int j=0;j<cMat.length;j++){
                denom+=cMat[j][i];
            }
            recall+= cMat[i][i]/denom;
        }
        recall/=classes.size();

        return recall*100;
    }

    public void buildConfusionMat(Node node){

        for(int i=1;i<testTable.length;i++) {

            for (int k = 0; k < dataColumn; k++) {
                singleSample[k] = testTable[i][k];
            }
            matchMaking(node);
        }
        System.out.println("\nAccuracy: "+ calculateAccuracy() +"% "+"\nPrecision: "+ calculatePrecision() +"% "+
                "\nRecall: "+ calculateRecall() +"%"+"\nF1-score: "+ calculateF1() +"%");
    }
}
