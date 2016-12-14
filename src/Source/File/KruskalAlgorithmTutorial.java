package Source.File;

import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/*

    Honorable
*/
public class KruskalAlgorithmTutorial extends GLCanvas implements GLEventListener,KeyListener,MouseMotionListener,MouseListener
{
    double [][] start;double [][] end;
    double [][] newstart;double [][] newend;
    double [][] tempstart;double [][] tempend;
    int [][] edges; int [][] nedges;
    int step,textureID,container,word,mx,my,e,c;
    private TextRenderer t;
    boolean mouseDragged,mouseMoved,mouseClicked,mouseReleased,accept,reset;
    
    public KruskalAlgorithmTutorial()
    {
        this.addGLEventListener(this);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        setFocusable(true);
        
        start = new double [10][10];end = new double [10][10];   
        newstart = new double [10][10];newend = new double [10][10];
        tempend = new double [10][10];tempstart = new double [10][10];
        edges = new int [10][10];nedges = new int [10][10];
        assignment();
        textureID = e = c = 0;step  = -3;
        container = -1;
        visited = new boolean[5];
        reset = true;
    }
    
    
    public void paint(Graphics g)
    {
        super.paint(g);        
        ImageIcon sales1 = new ImageIcon("G:\\Nikhil Raj\\college\\Dev C++\\codes\\Graphics\\CG_PROGRAM_PROJECT\\MyProject\\sales1.jpg");
        ImageIcon sales2 = new ImageIcon("G:\\Nikhil Raj\\college\\Dev C++\\codes\\Graphics\\CG_PROGRAM_PROJECT\\MyProject\\sales2.jpg");
        ImageIcon logo = new ImageIcon("G:\\Nikhil Raj\\college\\Dev C++\\codes\\Graphics\\CG_PROGRAM_PROJECT\\MyProject\\logo.png");
        
        Font Font1 = new Font("Impact",Font.BOLD,19);
        Font Font2 = new Font("Serif",Font.BOLD,21);
        Font Font3 = new Font("DialogInput",Font.BOLD,24);
        g.setFont(Font1);
        
        int factor = 66;
        
        if(step == -3)
        {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, 1000, 1000);
            logo.paintIcon(this,g,23,100);
            g.setFont(Font3);
            g.setColor(Color.BLACK);
            g.drawString("Devised by some guy named Kruskal it's one of the most efficient", 52, 703);
            g.drawString("methods to find out the minimum spanning tree of a given graph", 52,733);
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
            logo.paintIcon(this,g,23,100);
            g.setFont(Font3);
            g.setColor(Color.BLACK);
            g.drawString("Let's say there's a salesman who has to traverse a given number", 35, 703);
            g.drawString("of cities and the cost of traversing from a city to other is given", 35,733);
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
            //labeling the nodes
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
            //------------------

            //now writing for edges
            g.setFont(Font2);
            g.setColor(Color.ORANGE);
            g.drawString("3",edges[3][0],edges[3][1]);
            g.drawString("1",edges[0][0],edges[0][1]);
            g.drawString("2",edges[4][0],edges[4][1]);
            g.drawString("4",edges[6][0],edges[6][1]);
            g.drawString("5",edges[2][0],edges[2][1]);
            g.drawString("5",edges[1][0],edges[1][1]);
            g.drawString("6",edges[5][0],edges[5][1]);
            g.drawString("4",edges[7][0],edges[7][1]);
            g.drawString("6",edges[8][0],edges[8][1]);
            g.drawString("8",edges[9][0],edges[9][1]);
            //---------------------
        }
        
        if(step >= 0 && step < 5) sales1.paintIcon(this,g,40,340);
        if(step == 5) sales2.paintIcon(this,g,652,328);
        
        if(step == 6)
        {
            g.setColor(Color.cyan);
            g.fill3DRect(500,500,200,60,true);
            
            g.setColor(Color.cyan);
            //g.fill3DRect(200,500,200,60,true);
            
            g.setColor(Color.DARK_GRAY);
            g.setFont(Font3);
            g.drawString("Yeah sure", 540, 535);
            //g.drawString("No Thanks", 240, 535);
            
            if(!mouseDragged)
            {
                g.setColor(Color.cyan);
                g.fill3DRect(mx - 100, my - 100, 200, 60, true);
                g.setColor(Color.DARK_GRAY);
                g.setFont(Font3);
                g.drawString("No Thanks",mx - 60, my - 65);
                repaint();
            }
        }
        
        if(step >= 7)
        {
            if(!mouseDragged && !mouseMoved && !mouseReleased) assignment();
            ImageIcon mouseup = new ImageIcon("G:\\Nikhil Raj\\college\\Dev C++\\codes\\Graphics\\CG_PROGRAM_PROJECT\\MyProject\\mouseup2.jpg");
            ImageIcon mousedown = new ImageIcon("G:\\Nikhil Raj\\college\\Dev C++\\codes\\Graphics\\CG_PROGRAM_PROJECT\\MyProject\\mousedown2.jpg");
            ImageIcon wrong = new ImageIcon("G:\\Nikhil Raj\\college\\Dev C++\\codes\\Graphics\\CG_PROGRAM_PROJECT\\MyProject\\wrong.png");
            ImageIcon correct = new ImageIcon("G:\\Nikhil Raj\\college\\Dev C++\\codes\\Graphics\\CG_PROGRAM_PROJECT\\MyProject\\right.png");
            
            //labeling the nodes
            g.setColor(Color.YELLOW);
            g.drawString("A", 271, 117 + factor - 5);
            g.drawString("D", 703, 117 + factor - 5);
            g.drawString("E", 487, 117 + factor - 5);
            g.drawString("B", 364, 17 + factor);
            g.drawString("C", 610, 17 + factor);
            //g.drawString("E", 487, 220);

            mouseup.paintIcon(this,g,620,280);

            g.setColor(Color.red);
            g.drawString("A", 271, 817);
            g.drawString("D", 703, 817);
            g.drawString("E", 487, 817);
            g.drawString("B", 364, 717);
            g.drawString("C", 610, 717);
            //g.drawString("E", 487, 920);
            //------------------

            //now writing for edges
            g.setFont(Font2);
            g.setColor(Color.ORANGE);
            g.drawString("5",edges[3][0],edges[3][1] + factor - 6);
            g.drawString("1",edges[0][0],edges[0][1] + factor - 10);
            //g.drawString("2",edges[4][0],edges[4][1]);
            g.drawString("3",edges[6][0],edges[6][1] + factor - 6);
            g.drawString("2",edges[2][0] + 30,edges[2][1] + factor - 6);
            g.drawString("6",edges[1][0],edges[1][1] + factor - 6);
            g.drawString("6",edges[5][0],edges[5][1] + factor);
            g.drawString("4",edges[7][0],edges[7][1] + factor - 6);
            //g.drawString("6",edges[8][0],edges[8][1]);
            //g.drawString("8",edges[9][0],edges[9][1]);
            //---------------------
            g.setColor(Color.cyan);
            g.fill3DRect(100,500,200,60,true);
            g.fill3DRect(700,500,200,60,true);
            g.setColor(Color.DARK_GRAY);
            g.setFont(Font3);
            g.drawString("Check",160,535);
            g.drawString("Reset",760,535);
            
            if(step != 8) reset = true;
            
            if(!reset)
            {
                if(accept) correct.paintIcon(this, g, 400, 450);
                else wrong.paintIcon(this, g, 400, 450);
            }
        }
    }
    
    public static void main(String [] args)
    {
        JFrame jf = new JFrame("Kruskal Algorithm");
        KruskalAlgorithmTutorial canvas = new KruskalAlgorithmTutorial();
        jf.add(canvas);
        jf.setSize(1000,1000);
        jf.setVisible(true);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void init(GLAutoDrawable drawable) 
    {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        gl.glClearColor(1.0f,1.0f,1.0f,0.0f);
        gl.glPointSize(15.0f); 
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluOrtho2D(-800.0,800.0,-800.0,800.0); 
    }

    public void display(GLAutoDrawable drawable) 
    {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        GLUT glut = new GLUT();
        
        int textureID = 0;
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); 
        background(drawable);
        Graph(drawable);
        gl.glFlush();            
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) 
    {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        
        if(height <= 0) height = 1;
        
        final float h = (float) width / (float) height;
        
        gl.glViewport(0,0,width,height);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged){}   
    
    public void assignment()    
    {
        //first the upper one
        start[0][0] = tempstart[0][0] = -171;start[0][1]  = tempstart[0][1] =773;end[0][0] = tempend[0][0] =171;end[0][1] = tempend[0][1] =773;
        start[1][0] = tempstart[1][0] =-321;start[1][1] = tempstart[1][1] =620;end[1][0] = tempend[1][0] =-28;end[1][1] = tempend[1][1] =620;
        start[2][0] = tempstart[2][0] =321;start[2][1] = tempstart[2][1] =620;end[2][0] = tempend[2][0] =27;end[2][1] = tempend[2][1] =620;
        start[3][0] = tempstart[3][0] =0;start[3][1] = tempstart[3][1] =591;end[3][0] = tempend[3][0] =0;end[3][1] = tempend[3][1] =479;
        start[4][0] = tempstart[4][0] =-329.7;start[4][1] = tempstart[4][1] =640.7;end[4][0] = tempend[4][0] =-220.3;end[4][1] = tempend[4][1] =752.3;
        start[5][0] = 220.3;start[5][1] = 752.3;end[5][0] = 329.3;end[5][1] = 640.7;
        start[6][0] = tempstart[6][0] =-176.9669;start[6][1] = tempstart[6][1] =757.1151005;end[6][0] = tempend[6][0] =-23.0331;end[6][1] =  tempend[6][1] =637.6203321;
        start[7][0] = 176.9668956;start[7][1] = 755.3796679;end[7][0] = 17.6203321;end[7][1] = 643.033104;
        start[8][0] = -323.9128244;start[8][1] = 607.3327481;end[8][0] = -26.08717559;end[8][1] = 462.6672519;
        start[9][0] = 323.9128244;start[9][1] = 607.3327481;end[9][0] = 26.08717559;end[9][1] = 462.6672519;
        
        //tempstart = start.clone();tempend = end.clone();
        
        
        //next the lower one      --diff 1153
        newstart[0][0] = -171;newstart[0][1] = -380;newend[0][0] = 171;newend[0][1] = -380;
        newstart[3][0] = 0;newstart[3][1] = -579;newend[3][0] = 0;newend[3][1] = -691;
        newstart[4][0] = -330.8129595;newstart[4][1] = -528.2547137;newend[4][0] = -219.1870405;newend[4][1] = -401.7452863;
        newstart[6][0] = -177.9037598;newstart[6][1] = -398.7818042;newend[6][0] = -22.09624022;newend[6][1] = -531.2181958;
        newstart[2][0] = 29;newstart[2][1] = -550;newend[2][0] = 321;newend[2][1] = -550;  
        
        //now the edges
        edges[3][0] = 305;edges[3][1] = 65;  //AB
        edges[0][0] = 475;edges[0][1] = 12;  //BC
        edges[4][0] = 499;edges[4][1] = 165; //EF
        edges[6][0] = 436;edges[6][1] = 58;  //BF
        edges[2][0] = 581;edges[2][1] = 99;  //DF
        edges[1][0] = 371;edges[1][1] = 99;  //AF
        edges[5][0] = 669;edges[5][1] = 58;  //CD
        edges[7][0] = 537;edges[7][1] = 58;  //CF
        edges[8][0] = 357;edges[8][1] = 176; //AE
        edges[9][0] = 605;edges[9][1] = 176; //DE
        
        //now the lower edges
        nedges[3][0] = 305;nedges[3][1] = 755;  //AB --done
        nedges[0][0] = 481;nedges[0][1] = 692;  //BC --done
        nedges[4][0] = 505;nedges[4][1] = 865;  //EF --done
        nedges[6][0] = 436;nedges[6][1] = 755;  //BF --done
        nedges[2][0] = 591;nedges[2][1] = 795;  //DF --done
    }
    
    private void circle(int h,int k,int r,GLAutoDrawable drawable)
    {        
        final double THREE_SIXTY = 2 * Math.PI;
        final double ONE_DEGREE = (Math.PI / 180);
        double x,y;
        
        GL gl = drawable.getGL();
        
        gl.glBegin(GL.GL_POLYGON);
        //gl.glColor3f(0.658824f,0.658824f,0.658824f);
        
        for(double i = ONE_DEGREE;i <= THREE_SIXTY;i += ONE_DEGREE)
        {
            x = h + Math.cos(i) * r;
            y = k + Math.sin(i) * r;
            gl.glVertex2d(x,y);
        }
        
        gl.glEnd();
    }
    
    private void edges(double x1,double y1,double x2,double y2,GLAutoDrawable drawable)
    {
        GL gl = drawable.getGL();
        
        gl.glLineWidth(8.4f);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glBegin(GL.GL_LINES);
            gl.glVertex2d(x1 ,y1);
            gl.glVertex2d(x2,y2);
        gl.glEnd();
        gl.glColor3f(1.0f, 0.0f, 0.0f);
    }
    
    public void Graph(GLAutoDrawable drawable)
    {
        GL gl = drawable.getGL();
        int r = 35;
        
        if(step <= 5)
        {            
            //graph --edges
            edges(start[0][0],start[0][1],end[0][0],end[0][1],drawable); //0
            edges(start[1][0],start[1][1],end[1][0],end[1][1],drawable); //1
            edges(start[2][0],start[2][1],end[2][0],end[2][1],drawable); //2
            edges(start[3][0],start[3][1],end[3][0],end[3][1],drawable); //3
            //side angled edges --upper left
            edges(start[4][0],start[4][1],end[4][0],end[4][1],drawable); //4        //sine cosine thingy theta = 45.567
            edges(start[5][0],start[5][1],end[5][0],end[5][1],drawable); //5
            //side angled edges --upper left                   //sine cosine thingy theta = 52.584 phi = 37.416 
            edges(start[6][0],start[6][1],end[6][0],end[6][1],drawable); //6
            edges(start[7][0],start[7][1],end[7][0],end[7][1],drawable); //7
            //side angled edges --lower left
            edges(start[8][0],start[8][1],end[8][0],end[8][1],drawable); //8
            //side angled edges --lower right
            edges(start[9][0],start[9][1],end[9][0],end[9][1],drawable); //9

            //graph --nodes
            gl.glColor3f(0.658824f,0.0f,0.658824f);
            circle(-200,773,r,drawable);
            circle(200,773,r,drawable);
            circle(-350,620,r,drawable);
            circle(350,620,r,drawable);
            circle(0,620,r,drawable);
            circle(0,450,r,drawable);

            //graph --sample nodes
            gl.glColor3f(0.158824f,1.0f,0.0f);
            circle(-200,-380,r,drawable);
            circle(200,-380,r,drawable);
            circle(-350,-550,r,drawable);
            circle(350,-550,r,drawable);
            circle(0,-550,r,drawable);
            circle(0,-720,r,drawable);  
        }
        
        
        if(step >= 7)
        {         
            if(!mouseDragged && !mouseMoved && !mouseReleased) 
            {
                assignment();
                System.out.println("entered assigm");
            }
            System.out.println("---------------------------mx = " + mx + " my = " + my);
            //graph --edges
            edges(start[0][0],start[0][1] - 100,end[0][0],end[0][1] - 100,drawable); //0
            edges(start[1][0],start[1][1] - 100,end[1][0],end[1][1] - 100,drawable); //1
            edges(start[2][0],start[2][1] - 100,end[2][0],end[2][1] - 100,drawable); //2
            //--------edges(start[3][0],start[3][1],end[3][0],end[3][1],drawable); //3
            //side angled edges --upper left
            edges(start[4][0],start[4][1] - 100,end[4][0],end[4][1] - 100,drawable); //4        //sine cosine thingy theta = 45.567
            edges(start[5][0],start[5][1] - 100,end[5][0],end[5][1] - 100,drawable); //5
            //side angled edges --upper left                   //sine cosine thingy theta = 52.584 phi = 37.416 
            edges(start[6][0],start[6][1] - 100,end[6][0],end[6][1] - 100,drawable); //6
            edges(start[7][0],start[7][1] - 100,end[7][0],end[7][1] - 100,drawable); //7
            //side angled edges --lower left
            //----edges(start[8][0],start[8][1],end[8][0],end[8][1],drawable); //8
            //side angled edges --lower right
            //----edges(start[9][0],start[9][1],end[9][0],end[9][1],drawable); //9

            //graph --nodes
            gl.glColor3f(0.658824f,0.0f,0.658824f);
            circle(-200,773 - 100,r,drawable);
            circle(200,773 - 100,r,drawable);
            circle(-350,620 - 100,r,drawable);
            circle(350,620 - 100,r,drawable);
            circle(0,620 - 100,r,drawable);
            //circle(0,450,r,drawable);

            //graph --sample nodes
            gl.glColor3f(0.158824f,1.0f,0.0f);
            circle(-200,-380,r,drawable);
            circle(200,-380,r,drawable);
            circle(-350,-550,r,drawable);
            circle(350,-550,r,drawable);
            circle(0,-550,r,drawable);
            //circle(0,-720,r,drawable);  
        }
        
        //step wise guidelines
        t = new TextRenderer(new Font("Monospaced",Font.BOLD,24));
        t.setColor(0.9f,0.9f,0.98f,1);
        t.beginRendering(getWidth(),getHeight());
                
        //step -1 instr
        if(step == -1)
        {
            t.draw("Lets' Consider the problem as graph", 250, 365);
            t.draw("Labeling the nodes, weights of the edges and",280,340);
            t.draw("the solution graph",310,315);
        }
        //step 0 instr
        if(step == 0)
        {
            t.draw("According to the algorithm we have to choose", 250, 365);
            t.draw("the minimum weighted edge every time and check ",280,340);
            t.draw("for edges NOT making cycles i.e., edge BC",310,315);
        }
        //step 1 instr
        if(step == 1)
        {
            t.draw("Now the next least weighted edge is FE", 250, 365);
            t.draw("doesn't make a cycle either",280,340);
        }
        //step 2 instr
        if(step == 2)
        {
            t.draw("Now the next least weighted edge is AB", 250, 365);
            t.draw("doesn't make a cycle",280,340);
        }
        //step 3 instr
        if(step == 3)
        {
            t.draw("Now the next least weighted edge is BF", 250, 365);
            t.draw("doesn't make a cycle",280,340);
        }
        //step 4 instr
        if(step == 4)
        {
            t.draw("Now the next least weighted edge is FD", 250, 365);
            t.draw("doesn't make a cycle",280,340);
            t.draw("could've chosen CF but that makes a cycle viz.,",310,315);
            t.draw("B -> C -> F", 280, 295);
        }
        //step 5 instr
        if(step == 5)
        {
            t.draw("Now the graph below is the one of the optimal ", 250, 365);
            t.draw("minimum spanning tree for the given graph",280,340);
            t.draw("saving money for the salesmen",310,315);
        }
        t.endRendering();
        //step 6 instr
        if(step == 6)
        {            
            t = new TextRenderer(new Font("Dialog",Font.BOLD,44));
            t.setColor(0.196078f,0.8f,0.196078f,1f);
            t.beginRendering(getWidth(), getHeight());
            t.draw("How about a Simple Question  ", 200, 665);
            t.draw("         to test your understanding ?",200,620);
            t.endRendering();
        }
        
        t = new TextRenderer(new Font("Monospaced",Font.BOLD,24));
        t.setColor(0.9f,0.9f,0.98f,1);
        t.beginRendering(getWidth(), getHeight());
        //step 7 instr
        if(step == 7)
        {  
            t.draw("Drag the edges to complete the tree given below  ", 200, 365);
            t.draw(" using the mouse make a minimum spanning tree",230,340);
        }
        //step 8 instr
        if(step == 8)
        {
            if(e == 5) accept = false;
            else if(e < 4)
            {
                if(e == 0) t.draw("           The tree isn't complete", 210, 355);
                if(e == 1) t.draw("This isn't the optimal solution",310,365);
                accept = false;
            }
            else if(e == 4)
            {
                if(c != e)
                {
                    accept = false;
                    if(c - e == 1) t.draw("There is a cycle", 210, 365);
                    else t.draw("There are cycles", r, r);
                }
                else 
                {
                    System.out.println("Correct");
                    t.draw("Correct !!",390,365);
                    accept = true;
                }
            }
        }
        t.endRendering();
    }
    
    public void background(GLAutoDrawable drawable)
    {
        GL gl = drawable.getGL();  
        GLU glu = new GLU();
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D,textureID);
        gl.glDepthMask(false);       
        try 
        {
            File f = new File("G:\\Nikhil Raj\\college\\Dev C++\\codes\\Graphics\\CG_PROGRAM_PROJECT\\MyProject\\nature.jpg");
            Texture t2 = TextureIO.newTexture(f, true);
            textureID = t2.getTextureObject();
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }         
        gl.glColor3f(1.0f,1.0f,1.0f);
        
        gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0f,0f);gl.glVertex2f(800f,800f);
            gl.glTexCoord2f(1f,0f);gl.glVertex2f(-800f,800f);
            gl.glTexCoord2f(1f,1f);gl.glVertex2f(-800f,-800f);
            gl.glTexCoord2f(0f,1f);gl.glVertex2f(800f,-800f);
        gl.glEnd();        
        gl.glDepthMask(true); 
        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) 
    {
        int f = e.getKeyCode();
        
        if(f == KeyEvent.VK_LEFT) 
        {
            System.out.println("Left out");
            repaint();
        }
        
        if(f == KeyEvent.VK_RIGHT)
        {
            if(step == -3)
            {
                step++;System.out.println("Right Key Pressed step = " + step);
                repaint();return;
            }
            if(step == -2)
            {
                step++;System.out.println("Right Key Pressed step = " + step);
                repaint();return;
            }
            if(step == -1)
            {
                step++;System.out.println("Right Key Pressed step = " + step);
                repaint();return;
            }
            if(step == 0)
            {                               
                start[0][0] = newstart[0][0];start[0][1] = newstart[0][1];
                end[0][0] = newend[0][0];end[0][1] = newend[0][1];  
                edges[0][0] = nedges[0][0];edges [0][1] = nedges [0][1];                            
                repaint();step++;
                System.out.println("Right Key Pressed step = " + step);return;
            }
            if(step == 1)
            {            
                start[3][0] = newstart[3][0];start[3][1] = newstart[3][1];
                end[3][0] = newend[3][0];end[3][1] = newend[3][1];
                edges[4][0] = nedges[4][0];edges [4][1] = nedges [4][1];
                repaint();step++;
                System.out.println("Right Key Pressed step = " + step);return;
            }
            if(step == 2)
            {            
                start[4][0] = newstart[4][0];start[4][1] = newstart[4][1];
                end[4][0] = newend[4][0];end[4][1] = newend[4][1];
                edges[3][0] = nedges[3][0];edges [3][1] = nedges [3][1];
                repaint();step++;
                System.out.println("Right Key Pressed step = " + step);return;
            }
            if(step == 3)
            {            
                start[6][0] = newstart[6][0];start[6][1] = newstart[6][1];
                end[6][0] = newend[6][0];end[6][1] = newend[6][1];
                edges[6][0] = nedges[6][0];edges [6][1] = nedges [6][1];
                repaint();step++;
                System.out.println("Right Key Pressed step = " + step);return;
            }
            if(step == 4)
            {            
                start[2][0] = newstart[2][0];start[2][1] = newstart[2][1];
                end[2][0] = newend[2][0];end[2][1] = newend[2][1];
                edges[2][0] = nedges[2][0];edges [2][1] = nedges [2][1];
                repaint();step++;
                System.out.println("Right Key Pressed step = " + step);return;
            }
            if(step == 5)
            {
                step++;System.out.println("Right Key Pressed step = " + step);
                repaint();return;
            }
        }  
    }

    public void keyReleased(KeyEvent e) {}

    public void mouseDragged(MouseEvent ex) 
    {
        mx = ex.getX();
        my = ex.getY();
        
        System.out.println("mouse dragged x = " + mx + " y = " + my); 
        mouseDragged = true;
        mouseMoved = false;
        mouseClicked = false;
        mouseReleased = false;
        double diff = 0;
        if(step == 7)
        { 
            if( (mx >= 300 && mx <= 330) && (my >= 100 && my <= 140) ){ container = 4;word = 3;e++;c++;}
            if( (mx >= 400 && mx <= 430) && (my >= 100 && my <= 140) ){ container = 6;word = 6;e++;c++;}
            if( (mx >= 450 && mx <= 500) && (my >= 50 && my <= 90) ){ container = 0;word = 0;e++;c++;}
            if( (mx >= 590 && mx <= 620) && (my >= 140 && my <= 170) ){ container = 2;word = 2;e++;c++;}
            if( (mx >= 350 && mx <= 400) && (my >= 140 && my <= 170) ){ container = 1;word = 1;c++;}
            if( (mx >= 540 && mx <= 570) && (my >= 100 && my <= 140) ){ container = 7;word = 7;c++;}
            if( (mx >= 640 && mx <= 680) && (my >= 100 && my <= 140) ){ container = 5;word = 5;c++;}
        }
        if(container != -1) 
        {
            diff = end[container][1] - start[container][1];
            start[container][1] = ((8 * (1000 - my)) / 5) - 800;
            end[container][1] = diff + start[container][1];
            edges[word][0] = mx;edges[word][1] = my;
        }
        System.out.println("e = " + e + " c = " + c);
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
        System.out.println("mouse clicked x = " + mx + " y = " + my);
        
        mouseDragged = false;
        mouseMoved = false;
        mouseClicked = true;
        mouseReleased = false;
        
        if(step == 6)
        {
            if( (mx >= 450 && mx <= 650) && (my >= 450 && my <= 550) )
            {
                step++;System.out.println("step 7");
                repaint();
            }
        }
        if(step == 7)
        {
            if( (mx >= 180 && mx <= 250) && (my >= 510 && my <= 590) )
            {
                step++;System.out.println("e = " + e + " c = " + c);reset = false;
                repaint();
            }
            if( (mx >= 750 && mx <= 1000) && (my >= 510 && my <= 590) )
            {
                step = 7;e = c = 0;System.out.println("e = " + e + " c = " + c);accept = true;
                repaint();
            }
        }
        if(step == 8)
            if( (mx >= 750 && mx <= 1000) && (my >= 510 && my <= 590) )
            {
                step = 7;e = c = 0;System.out.println("e = " + e + " c = " + c);accept = true;
                repaint();
            }
        
    }

    public void mousePressed(MouseEvent e){}

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
