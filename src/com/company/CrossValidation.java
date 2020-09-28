package com.company;

import java.util.Map;

public class CrossValidation {

    private String[][] testTable; int t=0;
    boolean leaf=false;
    String[] singleSample;
    private int dataColumn;

    public CrossValidation(String[][] testTable, int dataColumn) {
        this.testTable = testTable;
        this.dataColumn = dataColumn;
        singleSample=new String[dataColumn];
    }

    public void matchMaking(Node node){
        if(node.leaf){
            if(node.decision.equals(singleSample[dataColumn-1])) t++; leaf=true; return;
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
    public void accuracyTest(Node node){

        for(int i=1;i<testTable.length;i++){

            for(int k=0;k<dataColumn;k++){
                singleSample[k]=testTable[i][k];
            }
            matchMaking(node);
        }

        System.out.println("\nAccuracy: "+ t/(testTable.length-1)*100 +"%");
    }
}
