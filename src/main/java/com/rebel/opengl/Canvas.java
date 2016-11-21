package com.rebel.opengl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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

    public Canvas(Model model) {
        this.model = model;
        this.addGLEventListener(this);
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            GLCanvas canvas = new Canvas(new TrochoidCylindroid(c, fi, alpha, h, p, teta0));

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

        gl.glTranslatef((float) xPosition, (float) yPosition, zoom);
        gl.glRotatef((float) oldAngleX, 0f, 1f, 0f);
        gl.glRotatef((float) oldAngleY, 1f, 0f, 0f);

        double[][] points = calculateModel();



        drawFloor(gl);

        drawModel(gl);
    }

    private void drawModel(GL2 gl) {

        Vector lightPosition = new Vector(10, 0, 0);
        Vector viewerPosition = initPos(matrix);

        double uStart = 0;
        double uEnd = 1;
        double deltaU = 0.02d;
        double deltaV = 0.02d;
        double vStart = 0;
        double vEnd = 4;

        double deltaColor = 0.000000001;
        gl.glBegin(GL2.GL_POLYGON);
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);

        for (double tempU = uStart; tempU < uEnd - deltaU; tempU += deltaU) {
            for (double tempV = vStart; tempV < vEnd - deltaV; tempV += deltaV) {
                double newU = tempU + deltaU;
                double newV = tempV + deltaV;

                double x = model.funcX(newU, newV);
                double y = model.funcY(newU, newV);
                double z = model.funcZ(newU, newV);

                double[] color1 = getLight(norm(tempU, tempV, deltaColor),
                                           new Vector(x - lightPosition.getX(), y - lightPosition.getY(), z - lightPosition.getZ()),
                                           viewerPosition);
                gl.glColor3d(color1[0], color1[1], color1[2]);
                gl.glVertex3d(model.funcX(tempU, tempV), model.funcY(tempU, tempV),
                              model.funcZ(tempU, tempV));

                double[] color2 = getLight(norm(newU, tempV, deltaColor),
                                           new Vector(x - lightPosition.getX(), y - lightPosition.getY(), z - lightPosition.getZ()),
                                           viewerPosition);
                gl.glColor3d(color2[0], color2[1], color2[2]);
                gl.glVertex3d(model.funcX(newU, tempV), model.funcY(newU, tempV),
                              model.funcZ(newU, tempV));

                double[] color3 = getLight(norm(tempU, newV, deltaColor),
                                           new Vector(x - lightPosition.getX(), y - lightPosition.getY(), z - lightPosition.getZ()),
                                           viewerPosition);
                gl.glColor3d(color3[0], color3[1], color3[2]);
                gl.glVertex3d(model.funcX(tempU, newV), model.funcY(tempU, newV),
                              model.funcZ(tempU, newV));

                double[] color4 = getLight(norm(newU, newV, deltaColor),
                                           new Vector(x - lightPosition.getX(), y - lightPosition.getY(), z - lightPosition.getZ()),
                                           viewerPosition);
                gl.glColor3d(color4[0], color4[1], color4[2]);
                gl.glVertex3d(x, y, z);
            }
        }
        gl.glEnd();
    }

    private double[][] calculateModel() {

        double uStart = 0;
        double uEnd = 1;
        double deltaU = 0.02d;
        double deltaV = 0.02d;
        double vStart = 0;
        double vEnd = 4;

        int n = Double.valueOf((uEnd - uStart) / deltaU + (vEnd - vStart) / deltaV).intValue();

        double[][] points = new double[n][3];

        int i = 0;

        for (double tempU = uStart; tempU < uEnd - deltaU; tempU += deltaU) {
            for (double tempV = vStart; tempV < vEnd - deltaV; tempV += deltaV) {
                double newU = tempU + deltaU;
                double newV = tempV + deltaV;

                double x = model.funcX(newU, newV);
                double y = model.funcY(newU, newV);
                double z = model.funcZ(newU, newV);

                points[i][0] = model.funcX(tempU, tempV);
                points[i][1] = model.funcY(tempU, tempV);
                points[i][2] = model.funcZ(tempU, tempV);
                i++;

                points[i][0] = model.funcX(newU, tempV);
                points[i][1] = model.funcY(newU, tempV);
                points[i][2] = model.funcZ(newU, tempV);
                i++;

                points[i][0] = model.funcX(tempU, newV);
                points[i][1] = model.funcY(tempU, newV);
                points[i][2] = model.funcZ(tempU, newV);
                i++;

                points[i][0] = x;
                points[i][1] = y;
                points[i][2] = z;
                i++;
            }
        }
        return points;
    }


    private void drawFloor(GL2 gl) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3d(0.7, 0.7, 0.7);
        gl.glVertex3d(-2.0, -1.0, 4.0);
        gl.glVertex3d(8.0, -1.0, 4.0);
        gl.glVertex3d(8.0, -1.0, -4.0);
        gl.glVertex3d(-2.0, -1.0, -4.0);
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

        double[] projMatrix = Utils.mirrorMatrixV2(new double[]{3, -1, 0}, new double[]{0, -1, 0});
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glMultMatrixd(projMatrix, 0);
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