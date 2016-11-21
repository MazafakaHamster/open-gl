package com.rebel.opengl;

public class Vector {
    private double x;
    private double y;
    private double z;

    public Vector() {
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector add(Vector vectorA, Vector vectorB) {
        return new Vector(vectorA.getX() + vectorB.getX(), vectorA.getY() + vectorB.getY(), vectorA.getZ() + vectorB.getZ());
    }

    public static Vector sub(Vector vectorA, Vector vectorB) {
        return new Vector(vectorA.getX() - vectorB.getX(), vectorA.getY() - vectorB.getY(), vectorA.getZ() - vectorB.getZ());
    }

    public static Vector cross(Vector vectorA, Vector vectorB) {
        return new Vector(vectorA.getY() * vectorB.getZ() - vectorA.getZ() * vectorB.getY(), vectorA.getZ() * vectorB.getX() - vectorA.getX() * vectorB.getZ(), vectorA.getX() * vectorB.getY() - vectorA.getY() * vectorB.getX());
    }

    public static Vector mult(Vector vectorA, Vector vectorB) {
        return new Vector(vectorA.getX() * vectorB.getX(), vectorA.getY() * vectorB.getY(), vectorA.getZ() * vectorB.getZ());
    }

    public static Vector mult(Vector vector, double constants) {
        return new Vector(vector.getX() * constants, vector.getY() * constants, vector.getZ() * constants);
    }

    public static double scal(Vector vectorA, Vector vectorB) {
        return vectorA.getX() * vectorB.getX() + vectorA.getY() * vectorB.getY() + vectorA.getZ() * vectorB.getZ();
    }

    public Vector norm() {
        double length = 1.0 / Math.sqrt(getX() * getX() + getY() * getY() + getZ() * getZ());

        double newX = getX() * length;
        double newY = getY() * length;
        double newZ = getZ() * length;

        return new Vector(newX, newY, newZ);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
