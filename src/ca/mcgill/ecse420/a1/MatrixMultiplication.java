package ca.mcgill.ecse420.a1;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplication {

  private static final int NUMBER_THREADS = Runtime.getRuntime().availableProcessors();
  private static final int MATRIX_SIZE = 1000;
  private static final int NUM_ITERATIONS = 1;

  public static void main(String[] args) {

    boolean success = true;
    for (int i = 0; i < NUM_ITERATIONS; i++){
      System.out.println("\n==== ITERATION " + (i+1) + "/" + NUM_ITERATIONS + " ====\n");

      // Generate two random matrices, same size
      double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
      double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

      long start = System.currentTimeMillis();
      double[][] seqResult = sequentialMultiplyMatrix(a, b);
      long stop = System.currentTimeMillis();
      System.out.println("###### Sequential Multiplication ######");
      //		System.out.println("Sequential result:");
      //		print2DMatrix(seqResult);
      System.out.println("Sequential exec time(ms): " + (stop - start));

      start = System.currentTimeMillis();
      double[][] paraResult = parallelMultiplyMatrix(a, b);
      stop = System.currentTimeMillis();
      System.out.println("###### Parallel Multiplication ######");
      //    System.out.println("Parallel result:");
      //    print2DMatrix(paraResult);
      System.out.println("Parallel exec time(ms): " + (stop - start));

      if (!Arrays.deepEquals(seqResult, paraResult)) {
        success = false;
        break;
      }
    }
    if (!success) {
      System.out.println("Sequential and parallel results did not match");
    }

  }

  /**
   * Returns the result of a sequential matrix multiplication
   * The two matrices are randomly generated
   * @param a is the first matrix
   * @param b is the second matrix
   * @return the result of the multiplication
   * */
  public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
    int aRows = a.length;
    int bRows = b.length;
    int bColumns = b[0].length;
    int aColumns = a[0].length;
    double[][] c = new double[aRows][bColumns];

    if (aColumns != bRows) {
      throw new ArithmeticException("Invalid matrix dimensions");
    }

    for (int i = 0; i < aRows; i++) {
      for (int j = 0; j < bColumns; j++) {
        for (int k = 0; k < aColumns; k++) {
          c[i][j] += a[i][k] * b[k][j];
        }
      }
    }
    return c;
  }

  /**
   * Returns the result of a concurrent matrix multiplication
   * The two matrices are randomly generated
   * @param a is the first matrix
   * @param b is the second matrix
   * @return the result of the multiplication
   * */
  public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
    int aRows = a.length;
    int bRows = b.length;
    int bColumns = b[0].length;
    int aColumns = a[0].length;
    double[][] c = new double[aRows][bColumns];

    if (aColumns != bRows) {
      throw new ArithmeticException("Invalid matrix dimensions");
    }

    try {
      ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

      for (int i = 0; i < aRows; i++) {
        for (int j = 0; j < bColumns; j++) {
          executor.execute(new ParallelMultiply(i, j, a, b, c));
        }
      }

      executor.shutdown();
      executor.awaitTermination(MATRIX_SIZE, TimeUnit.SECONDS);
      System.out.println("Terminated successfully: " + executor.isTerminated());

    } catch (Exception e) {
      e.printStackTrace();
    }

    return c;
  }

  static class ParallelMultiply implements Runnable {
    private int row;
    private int column;
    private double[][] a;
    private double[][] b;
    private double[][] c;

    ParallelMultiply(final int row, final int column, final double[][] a, final double[][] b, final double[][] c) {
      this.row = row;
      this.column = column;
      this.a = a;
      this.b = b;
      this.c = c;
    }

    public void run() {
      for (int k = 0; k < a.length; k++) {
        c[row][column] += a[row][k] * b[k][column];
      }
    }
  }
        
  /**
   * Populates a matrix of given size with randomly generated integers between 0-10.
   * @param numRows number of rows
   * @param numCols number of cols
   * @return matrix
   */
  private static double[][] generateRandomMatrix (int numRows, int numCols) {
    double matrix[][] = new double[numRows][numCols];
    for (int row = 0 ; row < numRows ; row++ ) {
      for (int col = 0 ; col < numCols ; col++ ) {
        matrix[row][col] = (double) ((int) (Math.random() * 10.0));
      }
    }
    return matrix;
  }

  /**
   * Prints a formatted 2D matrix.
   * @param c matrix to print
   */
  private static void print2DMatrix(double[][] c) {
    for (int i = 0; i < c.length; i++) {
      for (int j = 0; j < c[i].length; j++) {
        System.out.print(c[i][j] + " ");
      }
      System.out.println();
    }
  }

}
