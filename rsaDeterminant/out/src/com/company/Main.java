package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {


    private  static int[][] readFile(String file)throws FileNotFoundException{
        Scanner input;
        File opened=new File(file);
        input= new Scanner(opened);

        input.nextLine();
        int size = Integer.parseInt(input.nextLine());
        int[][] fileMatrix = new int[size][size];

        for(int i = 0; i < size; i++){
            for(int k = 0; k < size; k++){
                fileMatrix[i][k] =  input.nextInt();
            }
        }
        System.out.printf("Matrix from file <%s> generated successfully!\n",file);
        input.close();
        return fileMatrix;
    }

    private static void display(int[][] mat, int row)
    {
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < row; j++)
                System.out.print(mat[i][j]+" ");

            System.out.print("\n");
        }
        System.out.print("\n");
    }
    private static int[][] createMatrix(int n) {
        int[][] generatedMatrix = new int[n][n];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                generatedMatrix[i][j] = ThreadLocalRandom.current().nextInt(0,  20);
            }
        }
        return generatedMatrix;

    }

    private static  void saveToFile(String filename,int val){
         Formatter output=null;// object used to output text to file

        try
         {
             output = new Formatter( filename);
             output.format("Value of last calculation: "+val+"\n");
         }
         catch ( SecurityException securityException )
         {
             System.err.println("You do not have write access to this file." );
             System.exit( 1 );
         }
          catch ( FileNotFoundException filesNotFoundException )
         {
             System.err.println( "Error creating file." );
             System.exit( 1 );
         }

        System.out.println("Successfully saved to "+filename+"\n");
        output.close();


    }
    private static int calculateDeterminant(int size,boolean quiet,int mat[][],ExecutorService application,int sum){
        CalculateElementQuantitie[] calculate=new CalculateElementQuantitie[size];

        display(mat,size);

        for (int i = 0; i <size ; i++) {


            calculate[i]=new CalculateElementQuantitie(size,quiet,i,mat);
            application.execute(calculate[i]);

        }
        application.shutdown();

        //waiting all threads to finish
        try {
            if (!application.awaitTermination(120, TimeUnit.SECONDS)) {
                System.out.println("Timed out while waiting for tasks to finish.\n" );
            }
        } catch (InterruptedException ex) {
            System.out.println("Interrupted while wait for tasks to finish.\n" );
        }



        for (int i = 0; i < size; i++) {

            sum+=Math.pow(-1,i+2)*calculate[i].getValue();
        }
        return sum;
    }


    public static void main(String[] args) {


        if(args.length<4){
            System.err.println("Wrong or short command line!\n");
            System.exit(1);
        }

        List<String> arguments = Arrays.asList(args);
        int indexOfThread=arguments.indexOf("-t");
        if(indexOfThread<0){
            System.err.println("Wrong or short command line... '-t <number>' expected!\n");
            System.exit(1);
        }
        int threads= Integer.parseInt(arguments.get(indexOfThread+1));

        long startTime = System.currentTimeMillis();
        int sum=0;
        int[][] mat;
        boolean quiet=false;


        if(arguments.contains("-n")){


            int index=arguments.indexOf("-n");
            int size= Integer.parseInt(arguments.get(index+1));
            threads=threads<=size?threads:size;
            ExecutorService application = Executors.newFixedThreadPool(threads);
            mat=createMatrix(size);

            if(arguments.contains("-q")){
                quiet=true;
            }
            sum=calculateDeterminant(size,quiet,mat,application,sum);

        }
        else if(arguments.contains("-i")) {


            int index = arguments.indexOf("-i");
            String fileName = arguments.get(index + 1);

            try {
                mat = readFile(fileName);
                int size = mat.length;
                threads=threads<=size?threads:size;
                ExecutorService application = Executors.newFixedThreadPool(threads);
                if (arguments.contains("-q")) {
                    quiet = true;
                }
                sum=calculateDeterminant(size, quiet, mat, application, sum);

            } catch (FileNotFoundException e) {
                System.err.println("Error opening file.\n");
            }
        }

        if(arguments.contains("-o")&& arguments.size()>3){
            int index = arguments.indexOf("-o");
            String toFile = arguments.get(index + 1);
            saveToFile(toFile,sum);
        }


        long time =System.currentTimeMillis()-startTime ;
        System.out.printf("Calculated for: %d\n",time);
        System.out.println("Determinant Value : "+sum+"\n");

    }
}
