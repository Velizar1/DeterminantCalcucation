package com.company;


public class CalculateElementQuantitie implements Runnable{


    int value;
    private int[][] matrix;
    private int size;
    private int row;
    private int start;
    private boolean quiet;

    CalculateElementQuantitie(int size,boolean q,int row,int[][]matrix) {
        this.quiet=q;
        this.size=size;
        this.row=row;
        this.start=0;
        this.matrix = matrix;

    }
    private static  void getCofactor(int mat[][],int temp[][], int p, int q, int n)
    {
        int i = 0, j = 0;

        // Looping for each element of
        // the matrix
        for (int row = 0; row < n; row++)
        {
            for (int col = 0; col < n; col++)
            {
                if (row != p && col != q)
                {
                    temp[i][j++] = mat[row][col];

                    if (j == n - 1)
                    {
                        j = 0;
                        i++;
                    }
                }
            }
        }

    }


    private int  determinantOfMatrix(int mat[][], int n)
    {
        int D = 0; // Initialize result

        if (n == 1)
            return mat[0][0];

        // To store cofactors
        int temp[][] = new int[size][size];


        int sign = 1;

        for (int f =0 ; f < n; f++) {
            // Getting Cofactor of mat[0][f]
            getCofactor(mat, temp, start, f, n);
            D += sign * mat[0][f]
                    * determinantOfMatrix(temp, n - 1);

            sign = -sign;
        }
        return D;
    }


    public int getValue(){


        return value;
    }
    @Override
    public void run() {

            long startTime = System.currentTimeMillis();

            int help[][] = new int[size][size];
            getCofactor(matrix, help, start, row, size);
            value = determinantOfMatrix(help, size - 1);
            value=value*matrix[0][row];
            if (!quiet) {
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.printf("Thread-<%d> started.\n", row + 1);
                System.out.printf("Thread-<%d> stopped. Value calculated : %d\n", row + 1, value);
                System.out.printf("Thread-<%d> execution time was (millis): %d\n", row + 1, elapsedTime);

            }

    }

}
