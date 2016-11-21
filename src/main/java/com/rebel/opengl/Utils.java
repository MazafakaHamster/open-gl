package com.rebel.opengl;

public class Utils {
    public static double[] mirrorMatrixV1(double p[], double v[]) {
        double dot = p[0] * v[0] + p[1] * v[1] + p[2] * v[2];

        double[] result = new double[16];

        result[0] = 1 - 2 * v[0] * v[0];
        result[1] = -2 * v[0] * v[1];
        result[2] = -2 * v[0] * v[2];
        result[3] = 2 * dot * v[0];

        result[4] = -2 * v[1] * v[0];
        result[5] = 1 - 2 * v[1] * v[1];
        result[6] = -2 * v[1] * v[2];
        result[7] = 2 * dot * v[1];

        result[8] = -2 * v[2] * v[0];
        result[9] = -2 * v[2] * v[1];
        result[10] = 1 - 2 * v[2] * v[2];
        result[11] = 2 * dot * v[2];

        result[12] = 0;
        result[13] = 0;
        result[14] = 0;
        result[15] = 1;

        return result;
    }

    public static double[] mirrorMatrixV2(double p[], double v[]) {
        double dot = p[0] * v[0] + p[1] * v[1] + p[2] * v[2];

        double[] result = new double[16];

        result[0] = 1 - 2 * v[0] * v[0];
        result[1] = -2 * v[1] * v[0];
        result[2] = -2 * v[2] * v[0];
        result[3] = 0;
        result[4] = -2 * v[0] * v[1];
        result[5] = 1 - 2 * v[1] * v[1];
        result[6] = -2 * v[2] * v[1];
        result[7] = 0;
        result[8] = -2 * v[0] * v[2];
        result[9] = -2 * v[1] * v[2];
        result[10] = 1 - 2 * v[2] * v[2];
        result[11] = 0;
        result[12] = 2 * dot * v[0];
        result[13] = 2 * dot * v[1];
        result[14] = 2 * dot * v[2];
        result[15] = 1;

        return result;
    }
}
