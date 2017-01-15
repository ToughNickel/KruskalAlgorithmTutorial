#include <stdio.h>
#include <math.h>
#include <gl/glut.h>
#include <SOIL.h>
#define PI 3.1415926535897932

int step = -3;
int x11 = 250,y11 = 350;
int e = 0 ,c = 0,accept = 1;
double start[10][10];
double end[10];
double pl[10][10];
int a[10] = {0};

void initial_mode()
{
	glClearColor(1.0,1.0,1.0,1.0);
	glPointSize(5.0);
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	
	gluOrtho2D(0.0,1000.0,0.0,1000.0);
}

void Write(char *string,int x,int y)
{
	assignment();
	glRasterPos2f(x,y);
    while(*string)    glutBitmapCharacter(GLUT_BITMAP_TIMES_ROMAN_24, *string++);
}

void ImageLoader(char *file,int a,int b,int c,int d)
{
	glEnable(GL_TEXTURE_2D);
	
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
	
	glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);    //what if the s,t are > 1.0
	glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);
	
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);    //expanded to fit .., then how'll u adjust the smoothness
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	
	GLuint tex = SOIL_load_OGL_texture
	(
		file,
		SOIL_LOAD_AUTO,
		SOIL_CREATE_NEW_ID,
		SOIL_FLAG_MIPMAPS | SOIL_FLAG_INVERT_Y | SOIL_FLAG_NTSC_SAFE_RGB | SOIL_FLAG_COMPRESS_TO_DXT
	);
	
	glBindTexture(GL_TEXTURE_2D,tex);
    glBegin(GL_QUADS);
    	glTexCoord2f(0.0,1.0);glVertex2f(a,b);
    	glTexCoord2f(1.0,1.0);glVertex2f(c,b);
    	glTexCoord2f(1.0,0.0);glVertex2f(c,d);
    	glTexCoord2f(0.0,0.0);glVertex2f(a,d);
    glEnd();
    
    glFlush();
	
	glDisable(GL_TEXTURE_2D);
}

void drawsquare(int x,int y)
{
	glPointSize(5.0);
	glColor3f(0.309804,0.184314,0.309804);
	
	glBegin(GL_QUADS);
		glVertex2i(x - 90,y + 30);
		glVertex2i(x + 90,y + 30);
		glVertex2i(x + 90,y - 30);
		glVertex2i(x - 90,y - 30);
	glEnd();
	
	glFlush();
}

void check()
{
	if(e != 4) accept = 0;
	else 
	{
		if(c == e) accept = 1;
		else accept = 0;
	}
}

void circle(int h,int k,int r)
{
	double THREE_SIXTY = 2 * PI;
    double ONE_DEGREE = (PI / 180);
    double x,y,i;
        
    glBegin(GL_POLYGON);
    //gl.glColor3f(0.658824f,0.658824f,0.658824f);
        
    for(i = ONE_DEGREE;i <= THREE_SIXTY;i += ONE_DEGREE)
    {
        x = h + cos(i) * r;
        y = k + sin(i) * r;
        glVertex2d(x,y);
    }
        
    glEnd();
}

void edges(int x1,int y1,int x2,int y2)
{
	glLineWidth(8.4f);
    glColor3f(1.0f, 0.0f, 0.0f);
    glBegin(GL_LINES);
        glVertex2d(x1 , y1);
        glVertex2d(x2 , y2);
    glEnd();
}

void assignment()
{
	int d = 250;int f = 100;int i;
	//vertex assign
	start[0][0] = 254;start[0][1] = 820;
	start[1][0] = start[0][0] + (d / 2);start[1][1] = start[0][1] + f;
	start[2][0] = start[0][0] + (d + (d / 2));start[2][1] = start[0][1] + f;
	start[3][0] = start[0][0] + (2*d);start[3][1] = start[0][1];
	start[4][0] = start[0][0] + d;start[4][1] = start[0][1] - f;
	start[5][0] = start[0][0] + d;start[5][1] = start[0][1];
	
	//end [][] assign
	for(i = 0;i < 10;i++) end[i] = start[i][1];
	
	//pl[][] assign
	pl[0][0] = 286;pl[0][1] = 868;
	pl[4][0] = pl[0][0] + (d / 2) - 2;pl[4][1] = pl[0][1];
	pl[5][0] = pl[0][0] + d + 2;pl[5][1] = pl[0][1];
	pl[6][0] = pl[0][0] + (d + (d / 2));pl[6][1] = pl[0][1];
	pl[3][0] = pl[0][0] + d - 45;pl[3][1] = pl[0][1] + (f / 2) + 10;
	pl[1][0] = start[0][0] + (d / 2);pl[1][1] = start[0][1] + 10;
	pl[7][0] = pl[1][0] + (1 * d);pl[7][1] = pl[1][1];
	pl[2][0] = pl[1][0];pl[2][1] = pl[0][1] - (d / 2);
	pl[8][0] = pl[2][0] + d;pl[8][1] = pl[2][1];
	pl[9][0] = pl[2][0] + (d / 2) - 20;pl[9][1] = pl[2][1] + (d / 8);
}

void Graph()
{
	int r = 19;
	if(step <= 5)
	{
		glColor3f(1.0,0.0,0.0);
		edges(start[0][0],end[0] - a[0],start[1][0],end[1] - a[0]);    //AB
		edges(start[0][0],end[0] - a[1],start[5][0],end[5] - a[1]);    //AF
		edges(start[0][0],end[0] - a[2],start[4][0],end[4] - a[2]);	   //AE
		edges(start[1][0],end[1] - a[3],start[2][0],end[2] - a[3]);	   //BC
		edges(start[1][0],end[1] - a[4],start[5][0],end[5] - a[4]);    //BF
		edges(start[2][0],end[2] - a[5],start[5][0],end[5] - a[5]);	   //CF
		edges(start[2][0],end[2] - a[6],start[3][0],end[3] - a[6]);	   //CD
		edges(start[3][0],end[3] - a[7],start[5][0],end[5] - a[7]);	   //DF
		edges(start[4][0],end[4] - a[8],start[3][0],end[3] - a[8]);	   //ED
		edges(start[4][0],end[4] - a[9],start[5][0],end[5] - a[9]);	   //EF
		
		glColor3f(0.419608,0.137255,0.556863);  //dark slate blue
		circle(start[0][0],start[0][1],r);
		circle(start[1][0],start[1][1],r);
		circle(start[2][0],start[2][1],r);
		circle(start[3][0],start[3][1],r);
		circle(start[4][0],start[4][1],r);
		circle(start[5][0],start[5][1],r);
		
		int s = 8,w = 650;int fact = 5;
		
		glColor3f(0.158824,1.0,0.0);  //lime green
		circle(start[0][0],start[0][1] - w,r);
		circle(start[1][0],start[1][1] - w,r);
		circle(start[2][0],start[2][1] - w,r);
		circle(start[3][0],start[3][1] - w,r);
		circle(start[4][0],start[4][1] - w,r);
		circle(start[5][0],start[5][1] - w,r);
		
		glColor3f(1.0,1.0,0.0);
		Write("A",start[0][0] - fact,start[0][1] - s);
		Write("B",start[1][0] - fact,start[1][1] - s);
		Write("C",start[2][0] - fact,start[2][1] - s);
		Write("D",start[3][0] - fact,start[3][1] - s);
		Write("E",start[4][0] - fact,start[4][1] - s);
		Write("F",start[5][0] - fact,start[5][1] - s);
		
		glColor3f(1.0,0.0,0.0);
		Write("A",start[0][0] - fact,start[0][1] - w  - s);
		Write("B",start[1][0] - fact,start[1][1] - w  - s);
		Write("C",start[2][0] - fact,start[2][1] - w  - s);
		Write("D",start[3][0] - fact,start[3][1] - w  - s);
		Write("E",start[4][0] - fact,start[4][1] - w  - s);
		Write("F",start[5][0] - fact,start[5][1] - w  - s);
		
		glColor3f(0.556863,0.137255,0.419608);
		Write("3",pl[0][0],pl[0][1] - a[0]);
		Write("5",pl[1][0],pl[1][1] - a[1]);
		Write("6",pl[2][0],pl[2][1] - a[2]);
		Write("1",pl[3][0],pl[3][1] - a[3]);
		Write("4",pl[4][0],pl[4][1] - a[4]);
		Write("4",pl[5][0],pl[5][1] - a[5]);
		Write("6",pl[6][0],pl[6][1] - a[6]);
		Write("5",pl[7][0],pl[7][1] - a[7]);
		Write("8",pl[8][0],pl[8][1] - a[8]);
		Write("2",pl[9][0],pl[9][1] - a[9]);
	}
	else
	{
		glColor3f(1.0,0.0,0.0);
		edges(start[0][0],end[0] - a[0],start[1][0],end[1] - a[0]);    //AB
		edges(start[0][0],end[0] - a[1],start[5][0],end[5] - a[1]);    //AF
		//edges(start[0][0],end[0] - a[2],start[4][0],end[4] - a[2]);	   //AE
		edges(start[1][0],end[1] - a[3],start[2][0],end[2] - a[3]);	   //BC
		edges(start[1][0],end[1] - a[4],start[5][0],end[5] - a[4]);    //BF
		edges(start[2][0],end[2] - a[5],start[5][0],end[5] - a[5]);	   //CF
		edges(start[2][0],end[2] - a[6],start[3][0],end[3] - a[6]);	   //CD
		edges(start[3][0],end[3] - a[7],start[5][0],end[5] - a[7]);	   //DF
		//edges(start[4][0],end[4] - a[8],start[3][0],end[3] - a[8]);	   //ED
		//edges(start[4][0],end[4] - a[9],start[5][0],end[5] - a[9]);	   //EF
		
		glColor3f(0.419608,0.137255,0.556863);  //dark slate blue
		circle(start[0][0],start[0][1],r);
		circle(start[1][0],start[1][1],r);
		circle(start[2][0],start[2][1],r);
		circle(start[3][0],start[3][1],r);
		//circle(start[4][0],start[4][1],r);
		circle(start[5][0],start[5][1],r);
		
		int s = 8,w = 650;int fact = 5;
		
		glColor3f(0.158824,1.0,0.0);  //lime green
		circle(start[0][0],start[0][1] - w,r);
		circle(start[1][0],start[1][1] - w,r);
		circle(start[2][0],start[2][1] - w,r);
		circle(start[3][0],start[3][1] - w,r);
		//circle(start[4][0],start[4][1] - w,r);
		circle(start[5][0],start[5][1] - w,r);
		
		glColor3f(1.0,1.0,0.0);
		Write("A",start[0][0] - fact,start[0][1] - s);
		Write("B",start[1][0] - fact,start[1][1] - s);
		Write("C",start[2][0] - fact,start[2][1] - s);
		Write("D",start[3][0] - fact,start[3][1] - s);
		//Write("E",start[4][0] - fact,start[4][1] - s);
		Write("F",start[5][0] - fact,start[5][1] - s);
		
		glColor3f(1.0,0.0,0.0);
		Write("A",start[0][0] - fact,start[0][1] - w  - s);
		Write("B",start[1][0] - fact,start[1][1] - w  - s);
		Write("C",start[2][0] - fact,start[2][1] - w  - s);
		Write("D",start[3][0] - fact,start[3][1] - w  - s);
		//Write("E",start[4][0] - fact,start[4][1] - w  - s);
		Write("F",start[5][0] - fact,start[5][1] - w  - s);
		
		glColor3f(0.556863,0.137255,0.419608);
		Write("5",pl[0][0],pl[0][1] - a[0]);
		Write("6",pl[1][0],pl[1][1] - a[1]);
		//Write("6",pl[2][0],pl[2][1] - a[2]);
		Write("1",pl[3][0],pl[3][1] - a[3]);
		Write("3",pl[4][0],pl[4][1] - a[4]);
		Write("4",pl[5][0],pl[5][1] - a[5]);
		Write("6",pl[6][0],pl[6][1] - a[6]);
		Write("2",pl[7][0],pl[7][1] - a[7]);
		//Write("8",pl[8][0],pl[8][1] - a[8]);
		//Write("2",pl[9][0],pl[9][1] - a[9]);
	}
}



void renderer()
{
	int i;
	glClear(GL_COLOR_BUFFER_BIT);
	glPointSize(5.0);
	
	if(step == -3)
	{
		glClearColor(0.4,0.4,0.4,1.0);
		ImageLoader("logo.png",50,950,950,50);
		
		glBegin(GL_QUADS);
			glPointSize(120.0);
			glColor3f(0.4,0.4,0.4);
			glVertex2i(50,370);
			glVertex2i(452,370);
			glVertex2i(452,375);
			glVertex2i(50,375);
		glEnd();
		
		glBegin(GL_QUADS);
			glPointSize(120.0);
			glColor3f(1.0,0.5,0.0);
			glVertex2i(450,370);
			glVertex2i(462,370);
			glVertex2i(462,375);
			glVertex2i(450,375);
		glEnd();
		
		glColor3f(0.0,0.0,0.0);
		Write("Devised by some guy named Kruskal, it's one of the most efficient methods to find out the",57,320);
		Write("minimum spanning tree of a given graph.Don't worry if nothing of this gets in your head",57,290);
		Write("right now",57,260);
		
		glBegin(GL_QUADS);
			glPointSize(120.0);
			glColor3f(0.4,0.4,0.4);
			glVertex2i(950,210);
			glVertex2i(452,210);
			glVertex2i(452,205);
			glVertex2i(950,205);
		glEnd();
		
		glBegin(GL_QUADS);
			glPointSize(120.0);
			glColor3f(1.0,0.5,0.0);
			glVertex2i(450,210);
			glVertex2i(462,210);
			glVertex2i(462,205);
			glVertex2i(450,205);
		glEnd();
	}
	
	if(step == -2)
	{
		glClearColor(0.4,0.4,0.4,1.0);
		ImageLoader("logo.png",50,950,950,50);
		
		glBegin(GL_QUADS);
			glPointSize(120.0);
			glColor3f(0.4,0.4,0.4);
			glVertex2i(50,370);
			glVertex2i(940,370);
			glVertex2i(940,375);
			glVertex2i(50,375);
		glEnd();
		
		glBegin(GL_QUADS);
			glPointSize(120.0);
			glColor3f(1.0,0.5,0.0);
			glVertex2i(940,370);
			glVertex2i(950,370);
			glVertex2i(950,375);
			glVertex2i(940,375);
		glEnd();
		
		glColor3f(0.0,0.0,0.0);
		Write("Let's say there's a salesman who has to traverse a given number of cities and the cost of ",57,320);
		Write("traversing from a city to other is given. So this algorithm helps him to find the cheapest way",57,290);
		Write("to do that",57,260);
		
		glBegin(GL_QUADS);
			glPointSize(120.0);
			glColor3f(0.4,0.4,0.4);
			glVertex2i(950,210);
			glVertex2i(50,210);
			glVertex2i(50,205);
			glVertex2i(950,205);
		glEnd();
		
		glBegin(GL_QUADS);
			glPointSize(120.0);
			glColor3f(1.0,0.5,0.0);
			glVertex2i(50,210);
			glVertex2i(62,210);
			glVertex2i(62,205);
			glVertex2i(50,205);
		glEnd();
	}
	
	if(step == -1)
	{
		ImageLoader("nature.jpg",0,1000,1000,0);
		glColor3f(1.0,1.0,1.0);
		Write("Lets' Consider the problem as graph -- Labeling the nodes, weights of",217,390);
		Write("the edges and the solution graph",217,360);
		Graph();
	}
	
	if(step == 0)
	{
		ImageLoader("nature.jpg",0,1000,1000,0);
		ImageLoader("sales1.jpg",50,550,170,320);
		glColor3f(1.0,1.0,1.0);
		Write("According to the algorithm we have to choose the minimum weighted edge",217,390);
		Write("every time and check for edges NOT making cycles i.e., edge BC",217,360);
		Graph();
	}
	
	if(step == 1)
	{
		ImageLoader("nature.jpg",0,1000,1000,0);
		ImageLoader("sales1.jpg",50,550,170,320);
		glColor3f(1.0,1.0,1.0);
		Write("Now the next least weighted edge is FE,doesn't make a cycle either",217,390);
		Graph();
	}
	
	if(step == 2)
	{
		ImageLoader("nature.jpg",0,1000,1000,0);
		ImageLoader("sales1.jpg",50,550,170,320);
		glColor3f(1.0,1.0,1.0);
		Write("Now the next least weighted edge is AB,doesn't make a cycle",217,390);
		Graph();
	}
	
	if(step == 3)
	{
		ImageLoader("nature.jpg",0,1000,1000,0);
		ImageLoader("sales1.jpg",50,550,170,320);
		glColor3f(1.0,1.0,1.0);
		Write("Now the next least weighted edge is BF,doesn't make a cycle",217,390);
		Graph();
	}
	
	if(step == 4)
	{
		ImageLoader("nature.jpg",0,1000,1000,0);
		ImageLoader("sales1.jpg",50,550,170,320);
		glColor3f(1.0,1.0,1.0);
		Write("Now the next least weighted edge is FD,doesn't make a cycle",217,390);
		Write("could've chosen CF but that makes a cycle viz.,B -> C -> F",217,360);
		Graph();
	}
	
	if(step == 5)
	{
		ImageLoader("nature.jpg",0,1000,1000,0);
		ImageLoader("sales2.jpg",830,620,950,420);
		glColor3f(1.0,1.0,1.0);
		Write("Now the graph below is the one of the optimal minimum spanning tree for",217,390);
		Write("the given graph,saving money for the salesmen",217,360);
		Graph();
		for(i = 0;i < 10;i++) a[i] = 0;
	}
	
	if(step == 6)
	{
		ImageLoader("nature.jpg",0,1000,1000,0);
		ImageLoader("test.jpg",450,650,650,500);
		glColor3f(0.858824,0.439216,0.576471);
		Write("   How about a problem to test your understanding ?",237,790);
		
		drawsquare(650,350);
		glColor3f(1.0,1.0,0.0);
		Write("Yeah Sure",600,345);
		
		drawsquare(x11,y11);
		glColor3f(1.0,1.0,0.0);
		Write("No Thanks",x11 - 50,y11);
	}
	
	if(step == 7)
	{
		ImageLoader("nature.jpg",0,1000,1000,0);
		ImageLoader("mouseup2.jpg",820,750,920,600);
		glColor3f(1.0,1.0,1.0);
		Write("Find out the minimum spanning tree of the above graph using Kruskal Algorithm",117,390);
		Write("by clicking on the middle of the edges you want to select",117,360);
		
		drawsquare(650,550);
		glColor3f(1.0,1.0,0.0);
		Write("Reset",620,545);
		
		drawsquare(250,550);
		glColor3f(1.0,1.0,0.0);
		Write("Check",210,545);
		Graph();
	}
	if(step == 8)
	{
		ImageLoader("nature.jpg",0,1000,1000,0);
		ImageLoader("mouseup2.jpg",820,750,920,600);
		glColor3f(1.0,1.0,1.0);
		Write("Find out the minimum spanning tree of the above graph using Kruskal Algorithm",117,390);
		Write("by clicking on the middle of the edges you want to select",117,360);
		
		drawsquare(650,550);
		glColor3f(1.0,1.0,0.0);
		Write("Reset",620,545);
		
		drawsquare(250,550);
		glColor3f(1.0,1.0,0.0);
		Write("Check",210,545);
		Graph();
		
		char *str;
		if(accept) str = "right.png";
		else str = "wrong.png";
		glColor3f(0.196078,0.6,0.8);
		ImageLoader(str,415,550,515,650);
	}
	
	glFlush();
}

void clickEvent(int btn,int state,int x,int y)
{
	int i;
	if(btn == GLUT_LEFT_BUTTON && state == GLUT_DOWN)
	{
		printf("%d %d\n",x,y);
		if(step == 6 && (x <= 340) && (x >= 160) && (y >= 620) && (y <= 680)) 
		{
			x11 = 350;y11 = 450;
			glutPostRedisplay();	
		}
		if(step == 6 && (x <= 440) && (x >= 260) && (y >= 520) && (y <= 580)) 
		{
			x11 = 450;y11= 550;
			glutPostRedisplay();	
		}
		if(step == 6 && (x <= 540) && (x >= 360) && (y >= 420) && (y <= 480)) 
		{
			x11 = 250;y11 = 350;
			glutPostRedisplay();	
		}
		if(step == 6 && (x <= 740) && (x >= 560) && (y >= 620) && (y <= 780)) 
		{
			step++;
			glutPostRedisplay();	
		}
		if(step == 7 && (x <= 340) && (x >= 160) && (y >= 420) && (y <= 480)) 
		{
			step++;
			check();
			glutPostRedisplay();	
		}
		if(step == 7 && (x <= 740) && (x >= 560) && (y >= 420) && (y <= 480)) 
		{
			assignment();e = c = 0;
			for(i = 0;i < 10;i++) a[i] = 0;
			glutPostRedisplay();	
		}
		if(step == 8 && (x <= 740) && (x >= 560) && (y >= 420) && (y <= 480)) 
		{
			step--;
			assignment();e = c = 0;
			for(i = 0;i < 10;i++) a[i] = 0;
			glutPostRedisplay();	
		}
		if(step == 7)
		{
			if( (x >= 625 && x <= 650) && (y >= 165 && y <= 185) ) 
			{
				e++;c++;
				a[7] = 650;
			} 
			if( (x >= 487 && x <= 510) && (y >= 75 && y <= 82) ) 
			{
				e++;c++;
				a[3] = 650;
			} 
			if( (x >= 420 && x <= 435) && (y >= 105 && y <= 125) ) 
			{
				e++;c++;
				a[4] = 650;
			} 
			if( (x >= 305 && x <= 315) && (y >= 120 && y <= 145) ) 
			{
				e++;c++;
				a[0] = 650;
			} 
			if( (x >= 375 && x <= 395) && (y >= 170 && y <= 185) ) 
			{
				c++;
				a[1] = 650;
			} 
			if( (x >= 560 && x <= 575) && (y >= 120 && y <= 135) ) 
			{
				c++;
				a[5] = 650;
			} 
			if( (x >= 675 && x <= 690) && (y >= 115 && y <= 135) ) 
			{
				c++;
				a[6] = 650;
			} 
			glutPostRedisplay();
		}
	}
}

void typeEvent(int key,int x,int y)
{
	if(key == GLUT_KEY_RIGHT) 
	{
		if(step == 0) 	a[3] = 650;	
		if(step == 1) 	a[9] = 650;	
		if(step == 2)   a[0] = 650;
		if(step == 3)   a[4] = 650;
		if(step == 4)   a[7] = 650;
		step++;
		glutPostRedisplay();
	}
	
	if(key == GLUT_KEY_LEFT) 
	{
		if(step == 1) 	a[3] = 0;	
		if(step == 2) 	a[9] = 0;	
		if(step == 3)   a[0] = 0;
		if(step == 4)   a[4] = 0;
		if(step == 5)   a[7] = 0;
		step--;		
		glutPostRedisplay();
	}
}

int main(int argc,char **argv)
{
	glutInit(&argc,argv);
	glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
	glutInitWindowSize(1000,1000);
	glutInitWindowPosition(70,10);
	glutCreateWindow("Kruskal Algorithm Tutorial");
	glutDisplayFunc(renderer);
	glutMouseFunc(clickEvent);
	glutSpecialFunc(typeEvent);
	initial_mode();
	glutMainLoop();
	return 0;
}
