package com.rebel.opengl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.rebel.opengl.model.Model;
import com.rebel.opengl.model.TrochoidCylindroid;
import com.rebel.opengl.model.Vector;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

public class Canvas extends GLCanvas implements GLEventListener {
    private static double oldAngleX;
    private static double oldAngleY;
    private static double oldXCord;
    private static double oldYCord;
    private static double xPosition;
    private static double yPosition;
    private static float zoom = -10F;
    private static int c = 2;
    private static int k = 2;
    private static int h = 1;
    private static double alpha = 0.05d * Math.PI;
    private static double fi = 0.3d * Math.PI;
    private static double teta0 = 0;
    private static double p = k * Math.PI;
    private static float deltaZoom = 1f;
    private final Model model;
    private GLU glu;
    private double[] matrix = new double[16];
    private List<double[][]> objectArray;

    private static boolean moveFingerDown1 = false;
    private static boolean moveFingerUp1 = false;
    private static boolean moveFingerDown2 = false;
    private static boolean moveFingerUp2 = false;
    private static boolean moveFingerDown3 = false;
    private static boolean moveFingerUp3 = false;
    private static boolean moveFingerDown4 = false;
    private static boolean moveFingerUp4 = false;
    private static boolean moveFingerDown5 = false;
    private static boolean moveFingerUp5 = false;

    public Canvas(Model model) {
        this.model = model;
        this.addGLEventListener(this);
        this.objectArray = new ArrayList<>();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            GLCanvas canvas = new Canvas(new TrochoidCylindroid(c, fi, alpha, h, p, teta0));

            canvas.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case 81:
                            moveFingerDown1 = true;
                            break;
                        case 87:
                            moveFingerDown2 = true;
                            break;
                        case 69:
                            moveFingerDown3 = true;
                            break;
                        case 82:
                            moveFingerDown4 = true;
                            break;
                        case 84:
                            moveFingerDown5 = true;
                            break;
                        case 65:
                            moveFingerUp1 = true;
                            break;
                        case 83:
                            moveFingerUp2 = true;
                            break;
                        case 68:
                            moveFingerUp3 = true;
                            break;
                        case 70:
                            moveFingerUp4 = true;
                            break;
                        case 71:
                            moveFingerUp5 = true;
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case 81:
                            moveFingerDown1 = false;
                            break;
                        case 87:
                            moveFingerDown2 = false;
                            break;
                        case 69:
                            moveFingerDown3 = false;
                            break;
                        case 82:
                            moveFingerDown4 = false;
                            break;
                        case 84:
                            moveFingerDown5 = false;
                            break;
                        case 65:
                            moveFingerUp1 = false;
                            break;
                        case 83:
                            moveFingerUp2 = false;
                            break;
                        case 68:
                            moveFingerUp3 = false;
                            break;
                        case 70:
                            moveFingerUp4 = false;
                            break;
                        case 71:
                            moveFingerUp5 = false;
                    }
                }
            });

            canvas.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
                        oldAngleX += (oldXCord - e.getX()) * 0.4;
                        oldAngleY += (oldYCord - e.getY()) * -0.4;
                        oldXCord = e.getX();
                        oldYCord = e.getY();
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {

                }
            });

            canvas.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0 || (e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
                        oldXCord = e.getXOnScreen();
                        oldYCord = e.getYOnScreen();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            canvas.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
                        int x = e.getXOnScreen();
                        int y = e.getYOnScreen();
                        xPosition += (x - oldXCord) * 0.02;
                        yPosition += (y - oldYCord) * -0.02;
                        oldXCord = x;
                        oldYCord = y;
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {

                }
            });

            canvas.addMouseWheelListener(e -> {
                double notches = e.getWheelRotation();
                if (notches < 0) {
                    zoom -= deltaZoom;
                } else {
                    zoom += deltaZoom;
                }
            });

            canvas.setPreferredSize(new Dimension(800, 600));

            final FPSAnimator animator = new FPSAnimator(canvas, 60, true);

            final JFrame frame = new JFrame();
            frame.getContentPane().add(canvas);
            frame.pack();
            frame.setVisible(true);
            animator.start();
        });
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();

        addNewParalelipiped(new Vector(-4, 0, 0), new Vector(-4, 2, 0), 8, 2.5, 8);

        addNewParalelipiped(new Vector(-4.1, 8, 0), new Vector(-4.2, 9, 0), 1.8, 2, 3);
        addNewParalelipiped(new Vector(-4.2, 10.8, 0), new Vector(-4.3, 12, 0), 1.6, 1.8, 2.25);
        addNewParalelipiped(new Vector(-4.2, 12.6, 0), new Vector(-4.3, 14.3, 0), 1.5, 1.8, 2.5);

        addNewParalelipiped(new Vector(-2.0, 8, 0), new Vector(-2.0, 9, 0), 1.9, 2, 4.5);
        addNewParalelipiped(new Vector(-2.0, 12.5, 0), new Vector(-2.0, 13, 0), 1.9, 2, 3);
        addNewParalelipiped(new Vector(-2.0, 15.5, 0), new Vector(-2.0, 16, 0), 1.8, 2, 2.5);

        addNewParalelipiped(new Vector(0.2, 8, 0), new Vector(0.3, 9, 0), 1.9, 2, 4.8);
        addNewParalelipiped(new Vector(0.3, 12.8, 0), new Vector(0.3, 14, 0), 1.9, 2, 3.25);
        addNewParalelipiped(new Vector(0.4, 16.05, 0), new Vector(0.4, 17, 0), 1.8, 2, 2.5);

        addNewParalelipiped(new Vector(2.4, 8, 0), new Vector(2.8, 10, 0), 1.9, 2, 4.2);
        addNewParalelipiped(new Vector(2.6, 12.2, 0), new Vector(2.7, 13, 0), 1.7, 2, 2.5);
        addNewParalelipiped(new Vector(2.6, 14.7, 0), new Vector(2.7, 16, 0), 1.7, 2, 2.25);

        addNewParalelipiped(new Vector(4, 2.7, 0), new Vector(4, 2.8, 5), 3, 2, 3);
        addNewParalelipiped(new Vector(7, 4.5, 0), new Vector(8.4, 4.9, 0), 2, 2, 2.5);
        addNewParalelipiped(new Vector(9, 6.2, 0), new Vector(10.9, 8, 0), 1.8, 2, 2);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();

        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, matrix, 0);

        gl.glDisable(GL2.GL_LIGHTING);

        gl.glPushMatrix();

        gl.glTranslatef((float) xPosition, (float) yPosition, zoom);
        gl.glRotatef((float) oldAngleX, 0f, 1f, 0f);
        gl.glRotatef((float) oldAngleY, 1f, 0f, 0f);

        drawFingers(gl);

        if (moveFingerDown1 || moveFingerUp1)
            moveFinger(1, -1, 0, 0, moveFingerDown1 ? -0.3 : 0.3);
        if (moveFingerDown2 || moveFingerUp2)
            moveFinger(2, -1, 0, 0, moveFingerDown2 ? -0.3 : 0.3);
        if (moveFingerDown3 || moveFingerUp3)
            moveFinger(3, -1, 0, 0, moveFingerDown3 ? -0.3 : 0.3);
        if (moveFingerDown4 || moveFingerUp4)
            moveFinger(4, -1, 0, 0, moveFingerDown4 ? -0.3 : 0.3);
        if (moveFingerDown5 || moveFingerUp5)
            moveFinger(5, -1, 0, 0, moveFingerDown5 ? -0.3 : 0.3);

        gl.glPopMatrix();
    }

    private void moveFinger(int fingerNum, double x, double y, double z, double a) {
        int block = fingerNum * 3 - 2;
        double[][] fingerMatrixBottom = buildBlockTransformMatrix(objectArray.get(block), x, y, z, a);
        for (int i = block; i < block + 3; i++) {
            objectArray.set(i, Matrix.multiply(objectArray.get(i), fingerMatrixBottom));
        }
        block++;
        double[][] fingerMatrixMiddle = buildBlockTransformMatrix(objectArray.get(block), x, y, z, a);
        for (int i = block; i < block + 2; i++) {
            objectArray.set(i, Matrix.multiply(objectArray.get(i), fingerMatrixMiddle));
        }
        block++;
        double[][] fingerMatrixTop = buildBlockTransformMatrix(objectArray.get(block), x, y, z, a);
        objectArray.set(block, Matrix.multiply(objectArray.get(block), fingerMatrixTop));
    }

    private double[][] buildBlockTransformMatrix(double[][] block, double x, double y, double z, double a) {
        double[][] fingerMatrixTop = Matrix.translate(-block[0][0], -block[0][1], -block[0][2]);
        fingerMatrixTop = Matrix.multiply(fingerMatrixTop, Matrix.rotate(x, y, z, a));
        return Matrix.multiply(fingerMatrixTop, Matrix.translate(block[0][0], block[0][1], block[0][2]));
    }

    private void drawModel(GL2 gl, double[][] points, double[][] color) {
        gl.glBegin(GL2.GL_POLYGON);
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);

        for (int i = 0; i < points.length; i++) {
            gl.glColor3d(color[i][0], color[i][1], color[i][2]);
            gl.glVertex3d(points[i][0], points[i][1], points[i][2]);
        }
        gl.glEnd();
    }

    public void drawFingers(GL2 gl) {
        gl.glBegin(GL2.GL_LINES);
        gl.glColor3d(0.0, 1.0, 1.0);
        objectArray.forEach(object -> {
            gl.glVertex3d(object[0][0], object[0][1], object[0][2]);
            gl.glVertex3d(object[1][0], object[1][1], object[1][2]);
            gl.glVertex3d(object[1][0], object[1][1], object[1][2]);
            gl.glVertex3d(object[2][0], object[2][1], object[2][2]);
            gl.glVertex3d(object[2][0], object[2][1], object[2][2]);
            gl.glVertex3d(object[3][0], object[3][1], object[3][2]);
            gl.glVertex3d(object[3][0], object[3][1], object[3][2]);
            gl.glVertex3d(object[0][0], object[0][1], object[0][2]);

            gl.glVertex3d(object[4][0], object[4][1], object[4][2]);
            gl.glVertex3d(object[5][0], object[5][1], object[5][2]);
            gl.glVertex3d(object[5][0], object[5][1], object[5][2]);
            gl.glVertex3d(object[6][0], object[6][1], object[6][2]);
            gl.glVertex3d(object[6][0], object[6][1], object[6][2]);
            gl.glVertex3d(object[7][0], object[7][1], object[7][2]);
            gl.glVertex3d(object[7][0], object[7][1], object[7][2]);
            gl.glVertex3d(object[4][0], object[4][1], object[4][2]);

            gl.glVertex3d(object[0][0], object[0][1], object[0][2]);
            gl.glVertex3d(object[4][0], object[4][1], object[4][2]);
            gl.glVertex3d(object[1][0], object[1][1], object[1][2]);
            gl.glVertex3d(object[5][0], object[5][1], object[5][2]);
            gl.glVertex3d(object[2][0], object[2][1], object[2][2]);
            gl.glVertex3d(object[6][0], object[6][1], object[6][2]);
            gl.glVertex3d(object[3][0], object[3][1], object[3][2]);
            gl.glVertex3d(object[7][0], object[7][1], object[7][2]);
        });
        gl.glEnd();
    }

    private void addNewParalelipiped(Vector start, Vector end, double width, double height, double depth) {
        Vector initVector = new Vector(0, 1, 0);
        Vector transformedVector = Vector.sub(end, start);
        double[][] transformationMatrix = calculateTransformationMatrix(initVector, start, transformedVector);
        double[][] parallel = {
                new double[]{0, 0, 0, 1},
                new double[]{width, 0, 0, 1},
                new double[]{width, depth, 0, 1},
                new double[]{0, depth, 0, 1},
                new double[]{0, 0, height, 1},
                new double[]{width, 0, height, 1},
                new double[]{width, depth, height, 1},
                new double[]{0, depth, height, 1}
        };
        objectArray.add(Matrix.multiply(parallel, transformationMatrix));
    }

    private double[][] calculateTransformationMatrix(Vector initVector, Vector start, Vector transformedVector) {
        double xRotation = Vector.angle(new Vector(0, initVector.getY(), initVector.getZ()),
                                        new Vector(0, transformedVector.getY(), transformedVector.getZ()));

        double zRotation = Vector.angle(new Vector(initVector.getX(), initVector.getY(), 0),
                                        new Vector(transformedVector.getX(), transformedVector.getY(), 0));

        double[][] rotationAroundX = Matrix.rotate(1, 0, 0, xRotation);

        double[][] rotationAroundZ = Matrix.rotate(0, 0, 1, zRotation);

        double[][] translate = Matrix.translate(start.getX(), start.getY(), start.getZ());

        double[][] result = Matrix.multiply(rotationAroundX, rotationAroundZ);

        result = Matrix.multiply(result, translate);
        return result;
    }

    private Pair<double[][], double[][]> calculateModel() {
        Vector lightPosition = new Vector(10, 0, 0);
        Vector viewerPosition = initPos(matrix);

        double uStart = 0;
        double uEnd = 1;
        double deltaU = 0.02d;
        double deltaV = 0.02d;
        double vStart = 0;
        double vEnd = 4;
        int n = 39004;

        double deltaColor = 0.000000001;

        double[][] color = new double[n][3];
        double[][] points = new double[n][4];

        int i = 0;

        for (double tempU = uStart; tempU < uEnd - deltaU; tempU += deltaU) {
            for (double tempV = vStart; tempV < vEnd - deltaV; tempV += deltaV) {
                double newU = tempU + deltaU;
                double newV = tempV + deltaV;

                double x = model.funcX(newU, newV);
                double y = model.funcY(newU, newV);
                double z = model.funcZ(newU, newV);

                color[i] = getLight(norm(tempU, tempV, deltaColor),
                                    new Vector(x - lightPosition.getX(), y - lightPosition.getY(), z - lightPosition.getZ()),
                                    viewerPosition);
                points[i][0] = model.funcX(tempU, tempV);
                points[i][1] = model.funcY(tempU, tempV);
                points[i][2] = model.funcZ(tempU, tempV);
                points[i][3] = 1;
                i++;

                color[i] = getLight(norm(newU, tempV, deltaColor),
                                    new Vector(x - lightPosition.getX(), y - lightPosition.getY(), z - lightPosition.getZ()),
                                    viewerPosition);
                points[i][0] = model.funcX(newU, tempV);
                points[i][1] = model.funcY(newU, tempV);
                points[i][2] = model.funcZ(newU, tempV);
                points[i][3] = 1;
                i++;

                color[i] = getLight(norm(tempU, newV, deltaColor),
                                    new Vector(x - lightPosition.getX(), y - lightPosition.getY(), z - lightPosition.getZ()),
                                    viewerPosition);
                points[i][0] = model.funcX(tempU, newV);
                points[i][1] = model.funcY(tempU, newV);
                points[i][2] = model.funcZ(tempU, newV);
                points[i][3] = 1;
                i++;

                color[i] = getLight(norm(newU, newV, deltaColor),
                                    new Vector(x - lightPosition.getX(), y - lightPosition.getY(), z - lightPosition.getZ()),
                                    viewerPosition);
                points[i][0] = x;
                points[i][1] = y;
                points[i][2] = z;
                points[i][3] = 1;
                i++;
            }
        }

        return new Pair<>(points, color);
    }

    private void drawFloor(GL2 gl) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3d(0.3, 0.3, 0.35);
        gl.glVertex3d(-4.0, -1.0, 4.0);
        gl.glVertex3d(6.0, -1.0, 4.0);
        gl.glVertex3d(6.0, -1.0, -4.0);
        gl.glVertex3d(-4.0, -1.0, -4.0);
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        if (height == 0) {
            height = 1;
        }
        float aspect = (float) width / height;

        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, aspect, 0.1, 100.0);

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private Vector norm(double u, double v, double delta) {
        Vector vU = new Vector();
        Vector vV = new Vector();
        //(u + delta - u) / delta
        vU.setX((model.funcX(u + delta, v) - model.funcX(u, v)) / delta);
        vU.setY((model.funcY(u + delta, v) - model.funcY(u, v)) / delta);
        vU.setZ((model.funcZ(u + delta, v) - model.funcZ(u, v)) / delta);

        //(v + delta - v) / delta
        vV.setX((model.funcX(u, v + delta) - model.funcX(u, v)) / delta);
        vV.setY((model.funcY(u, v + delta) - model.funcY(u, v)) / delta);
        vV.setZ((model.funcZ(u, v + delta) - model.funcZ(u, v)) / delta);

        return Vector.cross(vU, vV).norm();
    }

    private double[] getLight(Vector normal, Vector lightDirection, Vector viewerPosition) {
        Vector diff = new Vector();
        Vector spec = new Vector();

        Vector planeAmbient = new Vector(0.8d, 0.4d, 0.7d);
        Vector planeDiffuse = new Vector(0.8d, 0.4d, 1d);
        Vector planeSpecular = new Vector(0.6d, 0.9d, 0.2d);

        Vector sourceAmbient = new Vector(0.5f, 0.3d, 0.5d);
        Vector sourceDiffuse = new Vector(0.7f, 0.5d, 0.7d);
        Vector sourceSpecular = new Vector(1d, 0.5d, 0.5d);

        Vector amb = Vector.mult(planeAmbient, sourceAmbient);
        amb = amb.norm();
        amb = Vector.mult(amb, 0.4d);

        diff.setX(Math.max(Vector.scal(lightDirection, normal), 0) * planeDiffuse.getX() * sourceDiffuse.getX());
        diff.setY(Math.max(Vector.scal(lightDirection, normal), 0) * planeDiffuse.getY() * sourceDiffuse.getY());
        diff.setZ(Math.max(Vector.scal(lightDirection, normal), 0) * planeDiffuse.getZ() * sourceDiffuse.getZ());
        diff = Vector.mult(diff, 0.6d);

        Vector reflect = Vector.sub(lightDirection, Vector.mult(normal, 2 * Vector.scal(normal, lightDirection)));
        reflect = reflect.norm();

        spec.setX(Math.max(Vector.scal(reflect, viewerPosition), 0) * planeSpecular.getX() * sourceSpecular.getX());
        spec.setY(Math.max(Vector.scal(reflect, viewerPosition), 0) * planeSpecular.getY() * sourceSpecular.getY());
        spec.setZ(Math.max(Vector.scal(reflect, viewerPosition), 0) * planeSpecular.getZ() * sourceSpecular.getZ());
        spec = Vector.mult(spec, 0.7d);

        Vector light = Vector.add(amb, diff);
        light = Vector.add(light, spec);

        return new double[]{light.getX(), light.getY(), light.getZ()};
    }

    private Vector initPos(double[] matrix) {
        return new Vector(-(matrix[0] * matrix[12] + matrix[1] * matrix[13] + matrix[2] * matrix[14]),
                          -(matrix[4] * matrix[12] + matrix[5] * matrix[13] + matrix[6] * matrix[14]),
                          -(matrix[8] * matrix[12] + matrix[9] * matrix[13] + matrix[10] * matrix[14]));
    }
}