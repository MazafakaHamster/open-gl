package com.rebel.opengl;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

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
    private static Webcam webcam;
    private static Long start;
    private static Long count = 0L;
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
                if (notches > 0) {
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
        Dimension size = WebcamResolution.VGA.getSize();

        webcam = Webcam.getDefault();
        webcam.setViewSize(size);
        webcam.open(true);
        start = System.currentTimeMillis();
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
        gl.glEnable(GL2.GL_TEXTURE_2D);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        drawBackground(gl);

        gl.glTranslatef((float) xPosition, (float) yPosition, zoom);
        gl.glRotatef((float) oldAngleX, 0f, 1f, 0f);
        gl.glRotatef((float) oldAngleY, 1f, 0f, 0f);

        Pair<double[][], double[][]> pair = calculateModel();

        double[][] points = pair.getKey();
        double[][] color = pair.getValue();



        drawFrustums(gl, points, color);
    }

    private void drawBackground(GL2 gl) {
        BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
        Texture texture = AWTTextureIO.newTexture(getGLProfile(), image, false);
        int t = texture.getTextureObject(gl);

        gl.glBindTexture(GL2.GL_TEXTURE_2D, t);
        gl.glBegin(GL2.GL_TRIANGLES);

        gl.glColor3d(1, 1, 1);

        gl.glTexCoord2f(0, 1);
        gl.glVertex3f(-4, -4, -6); //a
        gl.glTexCoord2f(1, 1);
        gl.glVertex3f(4, -4, -6); //b
        gl.glTexCoord2f(0, 0);
        gl.glVertex3f(-4, 4, -6); //c

        gl.glTexCoord2f(1, 1);
        gl.glVertex3f(4, -4, -6); //d
        gl.glTexCoord2f(0, 0);
        gl.glVertex3f(-4, 4, -6); //e
        gl.glTexCoord2f(1, 0);
        gl.glVertex3f(4, 4, -6); //f

        gl.glEnd();
        gl.glPopMatrix();
    }

    private void drawModel(GL2 gl, double[][] points, double[][] color) {
        gl.glPushMatrix();
        gl.glBegin(GL2.GL_POLYGON);

        for (int i = 0; i < points.length; i++) {
            gl.glColor3d(color[i][0], color[i][1], color[i][2]);
            gl.glVertex3d(points[i][0], points[i][1], points[i][2]);
        }
        gl.glEnd();
        gl.glPopMatrix();
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

    private void drawFrustums(GL2 gl, double[][] points, double[][] color) {
        double distance = 0.008;
        double convergence = 0.5;
        double angle = 45;
        double aspectRatio = getWidth() / getHeight();
        double near = 0.5;
        double far = 50;

        double halfDistance = distance / 2;
        double halfAngle = angle / 2;

        double a = aspectRatio * Math.tan(halfAngle) * convergence;
        double b = a - halfDistance;
        double c = a + halfDistance;
        double top = near * Math.tan(halfAngle);
        double bottom = -top;

        double leftL = -b * near / convergence;
        double rightL = c * near / convergence;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(leftL, rightL, bottom, top, near, far);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslated(halfDistance, 0, 0);

        gl.glTranslated(xPosition, yPosition, zoom);
        gl.glRotatef((float) oldAngleX, 0f, 1f, 0f);
        gl.glRotatef((float) oldAngleY, 1f, 0f, 0f);

        gl.glColorMask(true, false, false, false);
        drawModel(gl, points, color);

        gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);

        double leftR = -c * near / convergence;
        double rightR = b * near / convergence;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(leftR, rightR, bottom, top, near, far);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslated(-halfDistance, 0, 0);

        gl.glTranslated(xPosition, yPosition, zoom);
        gl.glRotatef((float) oldAngleX, 0f, 1f, 0f);
        gl.glRotatef((float) oldAngleY, 1f, 0f, 0f);

        gl.glColorMask(false, true, true, false);
        drawModel(gl, points, color);

        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glColorMask(true, true, true, true);
    }

    private Vector initPos(double[] matrix) {
        return new Vector(-(matrix[0] * matrix[12] + matrix[1] * matrix[13] + matrix[2] * matrix[14]),
                          -(matrix[4] * matrix[12] + matrix[5] * matrix[13] + matrix[6] * matrix[14]),
                          -(matrix[8] * matrix[12] + matrix[9] * matrix[13] + matrix[10] * matrix[14]));
    }
}