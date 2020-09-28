package com.company;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Decisiontree dTree= new Decisiontree();
        Scanner scanner=new Scanner(System.in);
        System.out.print("File:\n");
       // String file= scanner.next();
        dTree.init("mushroom_csv.csv");
    }
}