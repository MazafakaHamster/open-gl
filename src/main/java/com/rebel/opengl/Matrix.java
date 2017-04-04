package com.rebel.opengl;

import com.rebel.opengl.model.Vector;
import com.sun.deploy.util.ArrayUtil;

import java.util.Arrays;

public class Matrix {

    public static double[][] transpose(double[][] m) {
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    }

    public static double[][] add(double[][] m1, double[][] m2) {
        if (m1.length != m2.length || m1[0].length != m2[0].length) {
            throw new IllegalArgumentException();
        }
        double[][] temp = m1.clone();
        for (int i = 0; i < m1.length; i++)
            for (int j = 0; j < m1[0].length; j++)
                temp[i][j] += m2[i][j];
        return temp;
    }

    public static double[][] E(int size) {
        double[][] temp = new double[size][size];
        for (int i = 0; i < size; i++) {
            temp[i][i] = 1;
        }
        return temp;
    }

    public static double[][] multiply(double[][] m, double value) {
        double[][] temp = m.clone();
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[i][j] = m[i][j] * value;
        return temp;
    }

    public static double[] multiply(double[][] A, Vector vector) {
        int m = A.length;
        int n = A[0].length;
        if (n != 3) throw new RuntimeException("Illegal matrix dimensions.");
        double[] y = new double[m];
        for (int i = 0; i < m; i++) {
            y[i] += A[i][0] * vector.getX();
            y[i] += A[i][1] * vector.getY();
            y[i] += A[i][2] * vector.getZ();
        }
        return y;
    }

    public static double[] multiply(double[][] A, double[] x) {
        int m = A.length;
        int n = A[0].length;
        if (x.length != n) throw new RuntimeException("Illegal matrix dimensions.");
        double[] y = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                y[i] += A[i][j] * x[j];
        return y;
    }

    public static double[][] multiply(double[][] A, double[][] B) {

        double[][] aCopy = copy(A);
        double[][] bCopy = copy(B);

        int mA = aCopy.length;
        int nA = aCopy[0].length;
        int mB = bCopy.length;
        int nB = bCopy[0].length;
        if (nA != mB) throw new RuntimeException("Illegal matrix dimensions.");
        double[][] C = new double[mA][nB];
        for (int i = 0; i < mA; i++)
            for (int j = 0; j < nB; j++)
                for (int k = 0; k < nA; k++)
                    C[i][j] += aCopy[i][k] * bCopy[k][j];
        return C;
    }

    public static double[][] copy(double[][] m) {
        double[][] copy = new double[m.length][m[0].length];

        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                copy[i][j] = m[i][j];
        return copy;
    }

    public static double[][] translate(double x, double y, double z) {
        return new double[][]{
                new double[]{1, 0, 0, 0},
                new double[]{0, 1, 0, 0},
                new double[]{0, 0, 1, 0},
                new double[]{x, y, z, 1}
        };
    }

    public static double[][] rotate(double x, double y, double z, double a) {
        a = a * Math.PI / 180.0;
        double s = Math.sin(a);
        double c = Math.cos(a);
        double t = 1.0 - c;

        double tx = t * x;
        double ty = t * y;
        double tz = t * z;

        double sz = s * z;
        double sy = s * y;
        double sx = s * x;

        double[][] matrix = new double[][]{
                new double[4],
                new double[4],
                new double[4],
                new double[4]
        };
        matrix[0][0] = tx * x + c;
        matrix[0][1] = tx * y + sz;
        matrix[0][2] = tx * z - sy;
        matrix[0][3] = 0;

        matrix[1][0] = tx * y - sz;
        matrix[1][1] = ty * y + c;
        matrix[1][2] = ty * z + sx;
        matrix[1][3] = 0;

        matrix[2][0] = tx * z + sy;
        matrix[2][1] = ty * z - sx;
        matrix[2][2] = tz * z + c;
        matrix[2][3] = 0;

        matrix[3][0] = 0;
        matrix[3][1] = 0;
        matrix[3][2] = 0;
        matrix[3][3] = 1;
        return matrix;
    }
}
