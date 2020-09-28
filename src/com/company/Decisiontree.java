package com.company;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Decisiontree {
  String[][] table, testTable, allData;
  int dataRows=0, nTrainSet, dataColumns;

  public void readData(String file) throws IOException {

      BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
      String[] arr= new String[30];
      String frow; int r=0;
      while ( bufferedReader.readLine()!= null) dataRows++;

      nTrainSet= (int) Math.round((dataRows-1)*.9);

      bufferedReader = new BufferedReader(new FileReader(file));

      while ((frow = bufferedReader.readLine())!= null){

          arr = frow.split(",");

          if(r==0) {

              allData = new String[dataRows][arr.length];
              table = new String[nTrainSet+1][arr.length];
              testTable = new String[(dataRows) - nTrainSet][arr.length];

              for (int i=0;i<arr.length;i++) {
                  testTable[r][i] = arr[i]; table[r][i]=arr[i];
              }
          }

          for (int i=0;i<arr.length;i++)
              allData[r][i]=arr[i];
          r+=1;
      }

      dataColumns=arr.length;

      bufferedReader.close();

      splitData();
  }

  public void splitData(){
      boolean[] flag= new boolean[dataRows];
      int rIndex, tindex=1; Random random= new Random();

      for(int i=0;i<nTrainSet;i++){
        rIndex= random.nextInt(dataRows);

        if(rIndex==0 || flag[rIndex]){ i--;continue;}

        else{
            for (int j=0;j<dataColumns;j++)
              table[tindex][j] = allData[rIndex][j]; tindex++;
              flag[rIndex]=true;
        }
      }
      tindex=1;

      for(int i=1;i<flag.length;i++){
          if(!flag[i]){

              for (int j=0;j<dataColumns;j++){

                  testTable[tindex][j] = allData[i][j];
              }
              tindex++;
          }
      }
     // System.out.println(dataRows+" . "+allData.length+" . "+table.length+" . "+testTable.length+" . "+dataColumns);
  }
  public  double calculateTotalEntropy(String[][] table){
      double wholeEntropy,dec1=0,dec2=0;
      int dataCount= table.length-1;

      for (int i = 1; i <= dataCount; i++) {
          if (table[1][dataColumns-1].equals(table[i][dataColumns-1])) dec1++;
          else dec2++;
      }
      if(dec1==0 || dec2 == 0) return 0;
      wholeEntropy = -(dec1 / dataCount) * (Math.log(dec1 / dataCount) / Math.log(2)) - (dec2 / dataCount) * (Math.log(dec2 / dataCount) / Math.log(2));

      return wholeEntropy;
  }

  public double calculateBranchEntropy(double dec1, double dec2, double branchFreq, int rows){

      return (-((dec1/branchFreq)*(Math.log(dec1/branchFreq)/Math.log(2)))-((dec2/branchFreq)*(Math.log(dec2/branchFreq)/Math.log(2))))*(branchFreq/(rows-1));

  }

  public String[][] makeChildTable(String[][] parentTable, String branchName, int column){

      int rTRow=1,temp=1,trow = parentTable.length;

      for(int i=1; i<trow;i++){

          if(parentTable[i][column].equals(branchName)) rTRow++;
      }
      String[][] newTable= new String[rTRow][dataColumns];

      for(int i=0; i<dataColumns; i++) newTable[0][i] = parentTable[0][i];

      for(int i=1;i<trow;i++){
          if(parentTable[i][column].equals(branchName)){
              for (int j=0;j<dataColumns;j++)
                  newTable[temp][j]= parentTable[i][j];
              temp++;
          }
      }
      return newTable;
  }

  public Node expandBranches(String branchName, String[][] table){

      Node n = new Node();
      int rows=table.length;
      Set<String> temp = new HashSet<>();
      Set<String>branches = new HashSet<>();
      int currentColumnNumber=0, newTColumn=1;
      double maxGain = 0.0;
      
      double totalEntropy= calculateTotalEntropy(table);

      if(totalEntropy==0){

          n.leaf=true;
          n.decision = table[1][dataColumns-1];
          return n;
      }

      while (currentColumnNumber< dataColumns-1){
          double featureEntropy=0;

          for(int i=1;i<rows;i++)
              branches.add(table[i][currentColumnNumber]);

          for(String branch: branches){

              double branchFrequency=0; double dec1=0, dec2=0;

              for (int j=1;j<rows;j++){

                  if(branch.equals(table[j][currentColumnNumber])){
                      branchFrequency++;
                      if(table[1][dataColumns-1].equals(table[j][dataColumns-1])) dec1++; else dec2++;
                  }
              }
              if(dec1==0 || dec2==0) 
                  featureEntropy+=0;
              else
                  featureEntropy += calculateBranchEntropy(dec1,dec2,branchFrequency,rows);
              
          }
          double d = totalEntropy-featureEntropy;
          //System.out.println("Info Gain for "+ table[0][currentColumnNumber]+": "+  d);

          if( (totalEntropy-featureEntropy) > maxGain){
              temp.clear();
              maxGain = totalEntropy-featureEntropy;
              newTColumn = currentColumnNumber;
              n.attribute = table[0][currentColumnNumber];
              n.leaf=false;

              for(String s: branches)
                  temp.add(s);
          }
          currentColumnNumber++;
          branches.clear();
      }
     // System.out.println("\nSelected Feature: "+n.attribute+"\n");
      for(String s: temp){
          String[][] reducedTable= makeChildTable(table, s, newTColumn);
          n.nodes.put(s,expandBranches(s,reducedTable));
      }

      for (Map.Entry<String, Node> entry : n.nodes.entrySet()) {
          String key = entry.getKey();
          Node nod = entry.getValue();
      }
      return n;
  }

  public void TurnTheLights_On(Node node, int depth){

      if(node.leaf){
          System.out.print(node.decision);
      }
      else {
          depth++;
          System.out.print(node.attribute);

          for(Map.Entry<String,Node> entry : node.nodes.entrySet()){
              System.out.println("");

              for (int i=0;i<depth;i++) System.out.print("\t");

              System.out.print("--"+entry.getKey()+"--> ");

              TurnTheLights_On(entry.getValue(),depth+2);
          }
      }
  }
    public void init(String file) throws IOException {

      readData(file);

       Node n = expandBranches("",table);

        System.out.println("---The Tree--\n");

       TurnTheLights_On(n,0);

       CrossValidation xval= new CrossValidation(testTable,dataColumns);

       xval.accuracyTest(n);

        System.out.println("*Total data= "+(allData.length-1)+", Train Data= "+(table.length-1)+", Test Data= "+(testTable.length-1));
    }
}
