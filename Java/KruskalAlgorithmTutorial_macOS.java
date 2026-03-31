/*
 * KruskalAlgorithmTutorial - macOS Port (JOGL 2.6.0)
 *
 * MODIFIED FOR macOS (v2 — full bug fix):
 * - Ported from JOGL 1.x (javax.media.opengl) to JOGL 2.6.0 (com.jogamp.opengl)
 * - Replaced all hardcoded Windows paths (G:\Nikhil Raj\...) with relative paths
 * - Added missing 'visited' field declaration (was a bug in original)
 * - Changed GL → GL2 for desktop OpenGL compatibility
 * - Fixed: JOGL 2.3.2 → 2.6.0 for Apple Silicon arm64 native support
 * - Fixed: Removed -XstartOnFirstThread (only needed for NEWT, not AWT GLCanvas)
 * - Fixed: Texture loaded from disk every frame → load once in init(), cache it
 * - Fixed: TextRenderer created every frame → create once, reuse
 * - Fixed: ImageIcon created every paint() → cache in fields
 * - Fixed: init() was using GL_MODELVIEW for ortho → use GL_PROJECTION
 * - Fixed: main() runs directly (no SwingUtilities.invokeLater deadlock with macOS)
 *
 * Controls:
 *   Right Arrow - advance tutorial step
 *   Left Arrow  - go back (intro screens only)
 *   Mouse click - interact with quiz (steps 6-8)
 *   Mouse drag  - drag edges in quiz mode (step 7)
 *
 * Run command (from repo root):
 *   javac -cp "Java/lib/jogl-all-2.6.0.jar:Java/lib/gluegen-rt-2.6.0.jar" Java/KruskalAlgorithmTutorial_macOS.java
 *   java --enable-native-access=ALL-UNNAMED \
 *        --add-opens java.desktop/sun.awt=ALL-UNNAMED \
 *        -cp "Java:Java/lib/jogl-all-2.6.0.jar:Java/lib/gluegen-rt-2.6.0.jar:Java/lib/jogl-all-2.6.0-natives-macosx-universal.jar:Java/lib/gluegen-rt-2.6.0-natives-macosx-universal.jar" \
 *        KruskalAlgorithmTutorial_macOS
 */

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class KruskalAlgorithmTutorial_macOS extends GLCanvas implements GLEventListener, KeyListener, MouseMotionListener, MouseListener
{
    double [][] start; double [][] end;
    double [][] newstart; double [][] newend;
    double [][] tempstart; double [][] tempend;
    int [][] edges; int [][] nedges;
    int step, textureID, container, word, mx, my, e, c;
    boolean mouseDragged, mouseMoved, mouseClicked, mouseReleased, accept, reset;
    boolean [] visited;

    /* MODIFIED FOR macOS: Texture directory path (relative to working directory) */
    private static final String TEXTURE_DIR = "Textures" + File.separator;

    /* MODIFIED FOR macOS: Cache TextRenderers — don't recreate every frame */
    private TextRenderer textRendererMono;
    private TextRenderer textRendererDialog;

    /* MODIFIED FOR macOS: Cache ImageIcons — don't reload from disk every paint */
    private ImageIcon imgSales1, imgSales2, imgLogo, imgMouseUp, imgWrong, imgCorrect;

    /* MODIFIED FOR macOS: Cache the background texture — don't reload every frame */
    private Texture bgTexture;
    private boolean bgTextureLoaded = false;

    public KruskalAlgorithmTutorial_macOS()
    {
        /* MODIFIED FOR macOS: Explicitly request GL2 profile */
        super(new GLCapabilities(GLProfile.get(GLProfile.GL2)));

        this.addGLEventListener(this);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        setFocusable(true);

        start = new double [10][10]; end = new double [10][10];
        newstart = new double [10][10]; newend = new double [10][10];
        tempend = new double [10][10]; tempstart = new double [10][10];
        edges = new int [10][10]; nedges = new int [10][10];
        assignment();
        textureID = e = c = 0; step  = -3;
        container = -1;
        visited = new boolean[5];
        reset = true;

        /* MODIFIED FOR macOS: Pre-load images once */
        imgSales1 = new ImageIcon(TEXTURE_DIR + "sales1.jpg");
        imgSales2 = new ImageIcon(TEXTURE_DIR + "sales2.jpg");
        imgLogo   = new ImageIcon(TEXTURE_DIR + "logo.png");
        imgMouseUp = new ImageIcon(TEXTURE_DIR + "mouseup2.jpg");
        imgWrong   = new ImageIcon(TEXTURE_DIR + "wrong.png");
        imgCorrect = new ImageIcon(TEXTURE_DIR + "right.png");
    }


    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        Font Font1 = new Font("Impact", Font.BOLD, 19);
        Font Font2 = new Font("Serif", Font.BOLD, 21);
        Font Font3 = new Font("DialogInput", Font.BOLD, 24);
        g.setFont(Font1);

        int factor = 66;

        if(step == -3)
        {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, 1000, 1000);
            imgLogo.paintIcon(this, g, 23, 100);
            g.setFont(Font3);
            g.setColor(Color.BLACK);
            g.drawString("Devised by some guy named Kruskal it's one of the most efficient", 52, 703);
            g.drawString("methods to find out the minimum spanning tree of a given graph", 52, 733);
            g.drawString("Don't worry if nothing of this gets in your head right now", 52, 763);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 600, 500, 4);
            g.fillRect(500, 870, 500, 4);
            g.setColor(Color.ORANGE);
            g.fillRect(500, 600, 10, 4);
            g.fillRect(496, 870, 10, 4);
        }
        if(step == -2)
        {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, 1000, 1000);
            imgLogo.paintIcon(this, g, 23, 100);
            g.setFont(Font3);
            g.setColor(Color.BLACK);
            g.drawString("Let's say there's a salesman who has to traverse a given number", 35, 703);
            g.drawString("of cities and the cost of traversing from a city to other is given", 35, 733);
            g.drawString("So this algorithm helps him to find the cheapest way to do that", 35, 763);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 600, 951, 4);
            g.fillRect(33, 870, 970, 4);
            g.setColor(Color.ORANGE);
            g.fillRect(951, 600, 10, 4);
            g.fillRect(23, 870, 10, 4);
        }

        if(step >= -1 && step <= 5)
        {
            g.setColor(Color.YELLOW);
            g.drawString("A", 271, 117);
            g.drawString("D", 703, 117);
            g.drawString("F", 487, 117);
            g.drawString("B", 364, 17);
            g.drawString("C", 610, 17);
            g.drawString("E", 487, 220);

            g.setColor(Color.red);
            g.drawString("A", 271, 817);
            g.drawString("D", 703, 817);
            g.drawString("F", 487, 817);
            g.drawString("B", 364, 717);
            g.drawString("C", 610, 717);
            g.drawString("E", 487, 920);

            g.setFont(Font2);
            g.setColor(Color.ORANGE);
            g.drawString("3", edges[3][0], edges[3][1]);
            g.drawString("1", edges[0][0], edges[0][1]);
            g.drawString("2", edges[4][0], edges[4][1]);
            g.drawString("4", edges[6][0], edges[6][1]);
            g.drawString("5", edges[2][0], edges[2][1]);
            g.drawString("5", edges[1][0], edges[1][1]);
            g.drawString("6", edges[5][0], edges[5][1]);
            g.drawString("4", edges[7][0], edges[7][1]);
            g.drawString("6", edges[8][0], edges[8][1]);
            g.drawString("8", edges[9][0], edges[9][1]);
        }

        if(step >= 0 && step < 5) imgSales1.paintIcon(this, g, 40, 340);
        if(step == 5) imgSales2.paintIcon(this, g, 652, 328);

        if(step == 6)
        {
            g.setColor(Color.cyan);
            g.fill3DRect(500, 500, 200, 60, true);

            g.setColor(Color.DARK_GRAY);
            g.setFont(Font3);
            g.drawString("Yeah sure", 540, 535);

            if(!mouseDragged)
            {
                g.setColor(Color.cyan);
                g.fill3DRect(mx - 100, my - 100, 200, 60, true);
                g.setColor(Color.DARK_GRAY);
                g.setFont(Font3);
                g.drawString("No Thanks", mx - 60, my - 65);
                repaint();
            }
        }

        if(step >= 7)
        {
            if(!mouseDragged && !mouseMoved && !mouseReleased) assignment();

            g.setColor(Color.YELLOW);
            g.drawString("A", 271, 117 + factor - 5);
            g.drawString("D", 703, 117 + factor - 5);
            g.drawString("E", 487, 117 + factor - 5);
            g.drawString("B", 364, 17 + factor);
            g.drawString("C", 610, 17 + factor);

            imgMouseUp.paintIcon(this, g, 620, 280);

            g.setColor(Color.red);
            g.drawString("A", 271, 817);
            g.drawString("D", 703, 817);
            g.drawString("E", 487, 817);
            g.drawString("B", 364, 717);
            g.drawString("C", 610, 717);

            g.setFont(Font2);
            g.setColor(Color.ORANGE);
            g.drawString("5", edges[3][0], edges[3][1] + factor - 6);
            g.drawString("1", edges[0][0], edges[0][1] + factor - 10);
            g.drawString("3", edges[6][0], edges[6][1] + factor - 6);
            g.drawString("2", edges[2][0] + 30, edges[2][1] + factor - 6);
            g.drawString("6", edges[1][0], edges[1][1] + factor - 6);
            g.drawString("6", edges[5][0], edges[5][1] + factor);
            g.drawString("4", edges[7][0], edges[7][1] + factor - 6);

            g.setColor(Color.cyan);
            g.fill3DRect(100, 500, 200, 60, true);
            g.fill3DRect(700, 500, 200, 60, true);
            g.setColor(Color.DARK_GRAY);
            g.setFont(Font3);
            g.drawString("Check", 160, 535);
            g.drawString("Reset", 760, 535);

            if(step != 8) reset = true;

            if(!reset)
            {
                if(accept) imgCorrect.paintIcon(this, g, 400, 450);
                else imgWrong.paintIcon(this, g, 400, 450);
            }
        }
    }

    public static void main(String [] args)
    {
        /*
         * MODIFIED FOR macOS:
         * - Do NOT use SwingUtilities.invokeLater with GLCanvas on macOS.
         *   It can deadlock with the macOS native event loop.
         * - Do NOT use -XstartOnFirstThread with AWT GLCanvas (that's for NEWT only).
         * - Construct the JFrame directly on the main thread like the original code did.
         */
        JFrame jf = new JFrame("Kruskal Algorithm");
        KruskalAlgorithmTutorial_macOS canvas = new KruskalAlgorithmTutorial_macOS();
        jf.add(canvas);
        jf.setSize(1000, 1000);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        canvas.requestFocusInWindow(); /* MODIFIED FOR macOS: Ensure keyboard events work */
    }

    @Override
    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        gl.glPointSize(15.0f);

        /* MODIFIED FOR macOS: Use GL_PROJECTION for ortho (was incorrectly GL_MODELVIEW) */
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(-800.0, 800.0, -800.0, 800.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        /* MODIFIED FOR macOS: Create TextRenderers once, not every frame */
        textRendererMono = new TextRenderer(new Font("Monospaced", Font.BOLD, 24));
        textRendererDialog = new TextRenderer(new Font("Dialog", Font.BOLD, 44));

        /* MODIFIED FOR macOS: Load background texture once */
        try
        {
            File f = new File(TEXTURE_DIR + "nature.jpg");
            if (f.exists())
            {
                bgTexture = TextureIO.newTexture(f, true);
                bgTextureLoaded = true;
                System.out.println("Background texture loaded: " + f.getAbsolutePath());
            }
            else
            {
                System.err.println("Warning: Background texture not found: " + f.getAbsolutePath());
            }
        }
        catch (Exception ex)
        {
            System.err.println("Warning: Could not load background texture: " + ex.getMessage());
        }
    }

    @Override
    public void display(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        background(drawable);
        Graph(drawable);
        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        GL2 gl = drawable.getGL().getGL2();
        if(height <= 0) height = 1;
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable)
    {
        /* MODIFIED FOR macOS: Clean up GPU resources */
        if (textRendererMono != null) textRendererMono.dispose();
        if (textRendererDialog != null) textRendererDialog.dispose();
        if (bgTexture != null) bgTexture.destroy(drawable.getGL());
    }

    public void assignment()
    {
        start[0][0] = tempstart[0][0] = -171; start[0][1]  = tempstart[0][1] = 773; end[0][0] = tempend[0][0] = 171; end[0][1] = tempend[0][1] = 773;
        start[1][0] = tempstart[1][0] = -321; start[1][1] = tempstart[1][1] = 620; end[1][0] = tempend[1][0] = -28; end[1][1] = tempend[1][1] = 620;
        start[2][0] = tempstart[2][0] = 321; start[2][1] = tempstart[2][1] = 620; end[2][0] = tempend[2][0] = 27; end[2][1] = tempend[2][1] = 620;
        start[3][0] = tempstart[3][0] = 0; start[3][1] = tempstart[3][1] = 591; end[3][0] = tempend[3][0] = 0; end[3][1] = tempend[3][1] = 479;
        start[4][0] = tempstart[4][0] = -329.7; start[4][1] = tempstart[4][1] = 640.7; end[4][0] = tempend[4][0] = -220.3; end[4][1] = tempend[4][1] = 752.3;
        start[5][0] = 220.3; start[5][1] = 752.3; end[5][0] = 329.3; end[5][1] = 640.7;
        start[6][0] = tempstart[6][0] = -176.9669; start[6][1] = tempstart[6][1] = 757.1151005; end[6][0] = tempend[6][0] = -23.0331; end[6][1] =  tempend[6][1] = 637.6203321;
        start[7][0] = 176.9668956; start[7][1] = 755.3796679; end[7][0] = 17.6203321; end[7][1] = 643.033104;
        start[8][0] = -323.9128244; start[8][1] = 607.3327481; end[8][0] = -26.08717559; end[8][1] = 462.6672519;
        start[9][0] = 323.9128244; start[9][1] = 607.3327481; end[9][0] = 26.08717559; end[9][1] = 462.6672519;

        newstart[0][0] = -171; newstart[0][1] = -380; newend[0][0] = 171; newend[0][1] = -380;
        newstart[3][0] = 0; newstart[3][1] = -579; newend[3][0] = 0; newend[3][1] = -691;
        newstart[4][0] = -330.8129595; newstart[4][1] = -528.2547137; newend[4][0] = -219.1870405; newend[4][1] = -401.7452863;
        newstart[6][0] = -177.9037598; newstart[6][1] = -398.7818042; newend[6][0] = -22.09624022; newend[6][1] = -531.2181958;
        newstart[2][0] = 29; newstart[2][1] = -550; newend[2][0] = 321; newend[2][1] = -550;

        edges[3][0] = 305; edges[3][1] = 65;
        edges[0][0] = 475; edges[0][1] = 12;
        edges[4][0] = 499; edges[4][1] = 165;
        edges[6][0] = 436; edges[6][1] = 58;
        edges[2][0] = 581; edges[2][1] = 99;
        edges[1][0] = 371; edges[1][1] = 99;
        edges[5][0] = 669; edges[5][1] = 58;
        edges[7][0] = 537; edges[7][1] = 58;
        edges[8][0] = 357; edges[8][1] = 176;
        edges[9][0] = 605; edges[9][1] = 176;

        nedges[3][0] = 305; nedges[3][1] = 755;
        nedges[0][0] = 481; nedges[0][1] = 692;
        nedges[4][0] = 505; nedges[4][1] = 865;
        nedges[6][0] = 436; nedges[6][1] = 755;
        nedges[2][0] = 591; nedges[2][1] = 795;
    }

    private void circle(int h, int k, int r, GLAutoDrawable drawable)
    {
        final double THREE_SIXTY = 2 * Math.PI;
        final double ONE_DEGREE = (Math.PI / 180);
        double x, y;

        GL2 gl = drawable.getGL().getGL2();

        gl.glBegin(GL2.GL_POLYGON);

        for(double i = ONE_DEGREE; i <= THREE_SIXTY; i += ONE_DEGREE)
        {
            x = h + Math.cos(i) * r;
            y = k + Math.sin(i) * r;
            gl.glVertex2d(x, y);
        }

        gl.glEnd();
    }

    private void drawEdges(double x1, double y1, double x2, double y2, GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();

        gl.glLineWidth(8.4f);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glBegin(GL.GL_LINES);
            gl.glVertex2d(x1, y1);
            gl.glVertex2d(x2, y2);
        gl.glEnd();
        gl.glColor3f(1.0f, 0.0f, 0.0f);
    }

    public void Graph(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        int r = 35;

        if(step <= 5)
        {
            drawEdges(start[0][0], start[0][1], end[0][0], end[0][1], drawable);
            drawEdges(start[1][0], start[1][1], end[1][0], end[1][1], drawable);
            drawEdges(start[2][0], start[2][1], end[2][0], end[2][1], drawable);
            drawEdges(start[3][0], start[3][1], end[3][0], end[3][1], drawable);
            drawEdges(start[4][0], start[4][1], end[4][0], end[4][1], drawable);
            drawEdges(start[5][0], start[5][1], end[5][0], end[5][1], drawable);
            drawEdges(start[6][0], start[6][1], end[6][0], end[6][1], drawable);
            drawEdges(start[7][0], start[7][1], end[7][0], end[7][1], drawable);
            drawEdges(start[8][0], start[8][1], end[8][0], end[8][1], drawable);
            drawEdges(start[9][0], start[9][1], end[9][0], end[9][1], drawable);

            gl.glColor3f(0.658824f, 0.0f, 0.658824f);
            circle(-200, 773, r, drawable);
            circle(200, 773, r, drawable);
            circle(-350, 620, r, drawable);
            circle(350, 620, r, drawable);
            circle(0, 620, r, drawable);
            circle(0, 450, r, drawable);

            gl.glColor3f(0.158824f, 1.0f, 0.0f);
            circle(-200, -380, r, drawable);
            circle(200, -380, r, drawable);
            circle(-350, -550, r, drawable);
            circle(350, -550, r, drawable);
            circle(0, -550, r, drawable);
            circle(0, -720, r, drawable);
        }

        if(step >= 7)
        {
            if(!mouseDragged && !mouseMoved && !mouseReleased)
            {
                assignment();
            }

            drawEdges(start[0][0], start[0][1] - 100, end[0][0], end[0][1] - 100, drawable);
            drawEdges(start[1][0], start[1][1] - 100, end[1][0], end[1][1] - 100, drawable);
            drawEdges(start[2][0], start[2][1] - 100, end[2][0], end[2][1] - 100, drawable);
            drawEdges(start[4][0], start[4][1] - 100, end[4][0], end[4][1] - 100, drawable);
            drawEdges(start[5][0], start[5][1] - 100, end[5][0], end[5][1] - 100, drawable);
            drawEdges(start[6][0], start[6][1] - 100, end[6][0], end[6][1] - 100, drawable);
            drawEdges(start[7][0], start[7][1] - 100, end[7][0], end[7][1] - 100, drawable);

            gl.glColor3f(0.658824f, 0.0f, 0.658824f);
            circle(-200, 773 - 100, r, drawable);
            circle(200, 773 - 100, r, drawable);
            circle(-350, 620 - 100, r, drawable);
            circle(350, 620 - 100, r, drawable);
            circle(0, 620 - 100, r, drawable);

            gl.glColor3f(0.158824f, 1.0f, 0.0f);
            circle(-200, -380, r, drawable);
            circle(200, -380, r, drawable);
            circle(-350, -550, r, drawable);
            circle(350, -550, r, drawable);
            circle(0, -550, r, drawable);
        }

        /* MODIFIED FOR macOS: Reuse cached TextRenderers instead of creating new ones */
        textRendererMono.setColor(0.9f, 0.9f, 0.98f, 1);
        textRendererMono.beginRendering(getWidth(), getHeight());

        if(step == -1)
        {
            textRendererMono.draw("Lets' Consider the problem as graph", 250, 365);
            textRendererMono.draw("Labeling the nodes, weights of the edges and", 280, 340);
            textRendererMono.draw("the solution graph", 310, 315);
        }
        if(step == 0)
        {
            textRendererMono.draw("According to the algorithm we have to choose", 250, 365);
            textRendererMono.draw("the minimum weighted edge every time and check ", 280, 340);
            textRendererMono.draw("for edges NOT making cycles i.e., edge BC", 310, 315);
        }
        if(step == 1)
        {
            textRendererMono.draw("Now the next least weighted edge is FE", 250, 365);
            textRendererMono.draw("doesn't make a cycle either", 280, 340);
        }
        if(step == 2)
        {
            textRendererMono.draw("Now the next least weighted edge is AB", 250, 365);
            textRendererMono.draw("doesn't make a cycle", 280, 340);
        }
        if(step == 3)
        {
            textRendererMono.draw("Now the next least weighted edge is BF", 250, 365);
            textRendererMono.draw("doesn't make a cycle", 280, 340);
        }
        if(step == 4)
        {
            textRendererMono.draw("Now the next least weighted edge is FD", 250, 365);
            textRendererMono.draw("doesn't make a cycle", 280, 340);
            textRendererMono.draw("could've chosen CF but that makes a cycle viz.,", 310, 315);
            textRendererMono.draw("B -> C -> F", 280, 295);
        }
        if(step == 5)
        {
            textRendererMono.draw("Now the graph below is the one of the optimal ", 250, 365);
            textRendererMono.draw("minimum spanning tree for the given graph", 280, 340);
            textRendererMono.draw("saving money for the salesmen", 310, 315);
        }
        textRendererMono.endRendering();

        if(step == 6)
        {
            textRendererDialog.setColor(0.196078f, 0.8f, 0.196078f, 1f);
            textRendererDialog.beginRendering(getWidth(), getHeight());
            textRendererDialog.draw("How about a Simple Question  ", 200, 665);
            textRendererDialog.draw("         to test your understanding ?", 200, 620);
            textRendererDialog.endRendering();
        }

        textRendererMono.setColor(0.9f, 0.9f, 0.98f, 1);
        textRendererMono.beginRendering(getWidth(), getHeight());

        if(step == 7)
        {
            textRendererMono.draw("Drag the edges to complete the tree given below  ", 200, 365);
            textRendererMono.draw(" using the mouse make a minimum spanning tree", 230, 340);
        }
        if(step == 8)
        {
            if(e == 5) accept = false;
            else if(e < 4)
            {
                if(e == 0) textRendererMono.draw("           The tree isn't complete", 210, 355);
                if(e == 1) textRendererMono.draw("This isn't the optimal solution", 310, 365);
                accept = false;
            }
            else if(e == 4)
            {
                if(c != e)
                {
                    accept = false;
                    if(c - e == 1) textRendererMono.draw("There is a cycle", 210, 365);
                    else textRendererMono.draw("There are cycles", r, r);
                }
                else
                {
                    textRendererMono.draw("Correct !!", 390, 365);
                    accept = true;
                }
            }
        }
        textRendererMono.endRendering();
    }

    public void background(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();

        /* MODIFIED FOR macOS: Use cached texture instead of loading every frame */
        if (bgTextureLoaded && bgTexture != null)
        {
            gl.glEnable(GL.GL_TEXTURE_2D);
            bgTexture.bind(gl);
            gl.glDepthMask(false);
            gl.glColor3f(1.0f, 1.0f, 1.0f);

            gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2f(0f, 0f); gl.glVertex2f(800f, 800f);
                gl.glTexCoord2f(1f, 0f); gl.glVertex2f(-800f, 800f);
                gl.glTexCoord2f(1f, 1f); gl.glVertex2f(-800f, -800f);
                gl.glTexCoord2f(0f, 1f); gl.glVertex2f(800f, -800f);
            gl.glEnd();

            gl.glDepthMask(true);
            gl.glDisable(GL.GL_TEXTURE_2D);
        }
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e)
    {
        int f = e.getKeyCode();

        if(f == KeyEvent.VK_LEFT)
        {
            repaint();
        }

        if(f == KeyEvent.VK_RIGHT)
        {
            if(step == -3)
            {
                step++; repaint(); return;
            }
            if(step == -2)
            {
                step++; repaint(); return;
            }
            if(step == -1)
            {
                step++; repaint(); return;
            }
            if(step == 0)
            {
                start[0][0] = newstart[0][0]; start[0][1] = newstart[0][1];
                end[0][0] = newend[0][0]; end[0][1] = newend[0][1];
                edges[0][0] = nedges[0][0]; edges[0][1] = nedges[0][1];
                repaint(); step++;
                return;
            }
            if(step == 1)
            {
                start[3][0] = newstart[3][0]; start[3][1] = newstart[3][1];
                end[3][0] = newend[3][0]; end[3][1] = newend[3][1];
                edges[4][0] = nedges[4][0]; edges[4][1] = nedges[4][1];
                repaint(); step++;
                return;
            }
            if(step == 2)
            {
                start[4][0] = newstart[4][0]; start[4][1] = newstart[4][1];
                end[4][0] = newend[4][0]; end[4][1] = newend[4][1];
                edges[3][0] = nedges[3][0]; edges[3][1] = nedges[3][1];
                repaint(); step++;
                return;
            }
            if(step == 3)
            {
                start[6][0] = newstart[6][0]; start[6][1] = newstart[6][1];
                end[6][0] = newend[6][0]; end[6][1] = newend[6][1];
                edges[6][0] = nedges[6][0]; edges[6][1] = nedges[6][1];
                repaint(); step++;
                return;
            }
            if(step == 4)
            {
                start[2][0] = newstart[2][0]; start[2][1] = newstart[2][1];
                end[2][0] = newend[2][0]; end[2][1] = newend[2][1];
                edges[2][0] = nedges[2][0]; edges[2][1] = nedges[2][1];
                repaint(); step++;
                return;
            }
            if(step == 5)
            {
                step++; repaint(); return;
            }
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void mouseDragged(MouseEvent ex)
    {
        mx = ex.getX();
        my = ex.getY();

        mouseDragged = true;
        mouseMoved = false;
        mouseClicked = false;
        mouseReleased = false;
        double diff = 0;
        if(step == 7)
        {
            if( (mx >= 300 && mx <= 330) && (my >= 100 && my <= 140) ){ container = 4; word = 3; e++; c++; }
            if( (mx >= 400 && mx <= 430) && (my >= 100 && my <= 140) ){ container = 6; word = 6; e++; c++; }
            if( (mx >= 450 && mx <= 500) && (my >= 50 && my <= 90) ){ container = 0; word = 0; e++; c++; }
            if( (mx >= 590 && mx <= 620) && (my >= 140 && my <= 170) ){ container = 2; word = 2; e++; c++; }
            if( (mx >= 350 && mx <= 400) && (my >= 140 && my <= 170) ){ container = 1; word = 1; c++; }
            if( (mx >= 540 && mx <= 570) && (my >= 100 && my <= 140) ){ container = 7; word = 7; c++; }
            if( (mx >= 640 && mx <= 680) && (my >= 100 && my <= 140) ){ container = 5; word = 5; c++; }
        }
        if(container != -1)
        {
            diff = end[container][1] - start[container][1];
            start[container][1] = ((8 * (1000 - my)) / 5) - 800;
            end[container][1] = diff + start[container][1];
            edges[word][0] = mx; edges[word][1] = my;
        }
        repaint();
    }

    public void mouseMoved(MouseEvent e)
    {
        mx = e.getX();
        my = e.getY();

        mouseDragged = false;
        mouseMoved = true;
        mouseClicked = false;
        mouseReleased = true;
    }

    public void mouseClicked(MouseEvent ex)
    {
        mx = ex.getX();
        my = ex.getY();

        mouseDragged = false;
        mouseMoved = false;
        mouseClicked = true;
        mouseReleased = false;

        if(step == 6)
        {
            if( (mx >= 450 && mx <= 650) && (my >= 450 && my <= 550) )
            {
                step++;
                repaint();
            }
        }
        if(step == 7)
        {
            if( (mx >= 180 && mx <= 250) && (my >= 510 && my <= 590) )
            {
                step++; reset = false;
                repaint();
            }
            if( (mx >= 750 && mx <= 1000) && (my >= 510 && my <= 590) )
            {
                step = 7; e = c = 0; accept = true;
                repaint();
            }
        }
        if(step == 8)
            if( (mx >= 750 && mx <= 1000) && (my >= 510 && my <= 590) )
            {
                step = 7; e = c = 0; accept = true;
                repaint();
            }
    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e)
    {
        mouseDragged = false;
        mouseClicked = false;
        mouseMoved = false;
        mouseReleased = true;

        container = -1;
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}
