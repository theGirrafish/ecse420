package ca.mcgill.ecse420.a3;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixVectorMultiplication {
    private static final int NUM_THREADS = 4;
    private static final int MATRIX_SIZE = 2000;
    private static final int NUM_ITER = 10;
    private static final Random RAND = new Random();

    public static void main(String[] args) {
        double speed_ave = 0;
        for (int i = 0; i < NUM_ITER; i++) {
            long start;
            double[][] A = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
            double[] b = generateRandomVector(MATRIX_SIZE);

            start = System.nanoTime();
            double[] res1 = sequentialMultiply(A, b);
            double end1 = (double) (System.nanoTime() - start);
            System.out.println("Elapsed time: " + end1);

            start = System.nanoTime();
            double[] res2 = parallelMultiply(A, b);
            double end2 = (double) (System.nanoTime() - start);
            System.out.println("Elapsed time: " + end2);

            double speed = Math.round(end1 / end2 * 100.0) / 100.0;
            speed_ave += speed;

            System.out.println(Arrays.toString(res1));
            System.out.println(Arrays.toString(res2));
            System.out.println("Same result: " + Arrays.equals(res1, res2));
            System.out.println("Parallel was " + speed + "x faster than sequential!");
        }
        speed_ave /= NUM_ITER;
        System.out.println("\nAverage speedup was: " + (Math.round(speed_ave * 100.0) / 100.0) + "x for " + NUM_ITER + " iterations");
    }

    public static double[] parallelMultiply(double[][] matrix, double[] vector) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        double[] res = new double[MATRIX_SIZE];

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.execute(new ParallelMultiply(matrix, vector, res, i));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static class ParallelMultiply implements Runnable {
        double[][] matrix;
        double[] vector;
        double[] res;
        int rowNum;
        int rows;
        int cols;

        private ParallelMultiply(double[][] matrix, double[] vector, double[] res, int rowNum) {
            this.matrix = matrix;
            this.vector = vector;
            this.res = res;
            this.rowNum = rowNum;
            rows = matrix.length;
            cols = matrix[0].length;
        }

        @Override
        public void run() {

            // Starting at rowNum, skip num threads every iteration
            for (int i = rowNum; i < rows; i += NUM_THREADS) {
                // Same algorithm as sequential multiply
                res[i] = 0;
                for (int j = 0; j < cols; j++) {
                    res[i] += matrix[i][j] * vector[j];
                }
            }
        }
    }

    public static double[] sequentialMultiply(double[][] matrix, double[] vector) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        double[] res = new double[rows];

        for (int i = 0; i < rows; i++) {
            res[i] = 0;
            for (int j = 0; j < cols; j++) {
                res[i] += matrix[i][j] * vector[j];
            }
        }
        return res;
    }

    private static double[][] generateRandomMatrix(int numRows, int numCols) {
        double[][] matrix = new double[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                matrix[row][col] = (double) ((int) (RAND.nextInt(10) + 1));
            }
        }
        return matrix;
    }

    private static double[] generateRandomVector(int numElems) {
        double[] vector = new double[numElems];
        for (int i = 0; i < numElems; i++) {
            vector[i] = (double) ((int) (RAND.nextInt(10) + 1));
        }
        return vector;
    }
}
