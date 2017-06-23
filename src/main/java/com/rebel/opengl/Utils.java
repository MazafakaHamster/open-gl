package com.rebel.opengl;

public class Utils {
    public static double[][] mirrorMatrix(double p[], double v[]) {
        double dot = p[0] * v[0] + p[1] * v[1] + p[2] * v[2];

        double[][] result = new double[4][4];

        result[0][0] = 1 - 2 * v[0] * v[0];
        result[1][0] = -2 * v[0] * v[1];
        result[2][0] = -2 * v[0] * v[2];
        result[3][0] = 2 * dot * v[0];

        result[0][1] = -2 * v[1] * v[0];
        result[1][1] = 1 - 2 * v[1] * v[1];
        result[2][1] = -2 * v[1] * v[2];
        result[3][1] = 2 * dot * v[1];

        result[0][2] = -2 * v[2] * v[0];
        result[1][2] = -2 * v[2] * v[1];
        result[2][2] = 1 - 2 * v[2] * v[2];
        result[3][2] = 2 * dot * v[2];

        result[0][3] = 0;
        result[1][3] = 0;
        result[2][3] = 0;
        result[3][3] = 1;

        return result;
    }

    public static double[] vectMatrixMult(double[][] mat, double[] v) {
        int n = mat.length;
        int m = mat[0].length;

        if (v.length != m) {
            throw new IllegalArgumentException("Wrong vector size");
        }

        double[] result = new double[m];

        for (int i = 0; i < n; i++) {
            double rowSum = 0;
            for (int j = 0; j < m; j++) {
                rowSum += mat[i][j] * v[j];
            }
            result[i] = rowSum;
        }
        return result;
    }
}
