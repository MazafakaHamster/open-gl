package com.rebel.opengl;

/**
 * Created by Homia on 24.10.2016.
 */
public class TrochoidCylindroid implements Model {

    private final double c;
    private final double fi;
    private final double alpha;
    private final double h;
    private final double p;
    private final double teta0;

    public TrochoidCylindroid(double c, double fi, double alpha, double h, double p, double teta0) {
        this.c = c;
        this.fi = fi;
        this.alpha = alpha;
        this.h = h;
        this.p = p;
        this.teta0 = teta0;
    }

    @Override
    public double funcX(double u, double v) {
        return c * u + v * (Math.sin(fi) + Math.tan(alpha) * Math.cos(fi) * Math.cos(funcTeta(u)));
    }

    @Override
    public double funcY(double u, double v) {
        return v * Math.tan(alpha) * Math.sin(funcTeta(u));
    }

    @Override
    public double funcZ(double u, double v) {
        return h + v * (Math.tan(alpha) * Math.sin(fi) * Math.cos(funcTeta(u)) - Math.cos(fi));
    }

    private double funcTeta(double u) {
        return p * u + teta0;
    }
}
