/*
 * KruskalAlgorithmTutorial - macOS Port
 *
 * MODIFIED FOR macOS:
 * - Replaced <gl/glut.h> with macOS-appropriate headers (#ifdef __APPLE__)
 * - Replaced SOIL texture loading with stb_image
 * - Updated texture file paths to use Textures/ directory
 * - Added GL_SILENCE_DEPRECATION to suppress macOS OpenGL deprecation warnings
 *
 * Original: Windows/Code::Blocks with FreeGLUT + SOIL
 * Port target: macOS (Apple Silicon) with system GLUT framework
 *
 * Controls:
 *   Right Arrow - advance tutorial step
 *   Left Arrow  - go back one step
 *   Mouse click - interact with quiz (steps 6-8)
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

/* MODIFIED FOR macOS: Platform-specific OpenGL/GLUT headers */
#ifdef __APPLE__
#define GL_SILENCE_DEPRECATION
#include <OpenGL/gl.h>
#include <OpenGL/glu.h>
#include <GLUT/glut.h>
#else
#include <GL/gl.h>
#include <GL/glu.h>
#include <GL/glut.h>
#endif

/* MODIFIED FOR macOS: Replace SOIL with stb_image */
#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

#define PI 3.1415926535897932

/* MODIFIED FOR macOS: Absolute path to Textures directory */
static const char *TEXTURE_DIR = "/Users/nikhilraj/Workground/KruskalAlgorithmGraphicsProject/KruskalAlgorithmTutorial/Textures/";

int step = -3;
int x11 = 250, y11 = 350;
int e = 0, c = 0, accept = 1;
double start_pos[10][10];  /* MODIFIED FOR macOS: renamed from 'start' to avoid shadowing */
double end_pos[10];         /* MODIFIED FOR macOS: renamed from 'end' to avoid shadowing */
double pl[10][10];
int a[10] = {0};

void initial_mode()
{
	glClearColor(1.0, 1.0, 1.0, 1.0);
	glPointSize(5.0);

	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	gluOrtho2D(0.0, 1000.0, 0.0, 1000.0);
}

void assignment()
{
	int d = 250; int f = 100; int i;
	/* vertex assign */
	start_pos[0][0] = 254; start_pos[0][1] = 820;
	start_pos[1][0] = start_pos[0][0] + (d / 2); start_pos[1][1] = start_pos[0][1] + f;
	start_pos[2][0] = start_pos[0][0] + (d + (d / 2)); start_pos[2][1] = start_pos[0][1] + f;
	start_pos[3][0] = start_pos[0][0] + (2*d); start_pos[3][1] = start_pos[0][1];
	start_pos[4][0] = start_pos[0][0] + d; start_pos[4][1] = start_pos[0][1] - f;
	start_pos[5][0] = start_pos[0][0] + d; start_pos[5][1] = start_pos[0][1];

	/* end_pos[] assign */
	for(i = 0; i < 10; i++) end_pos[i] = start_pos[i][1];

	/* pl[][] assign */
	pl[0][0] = 286; pl[0][1] = 868;
	pl[4][0] = pl[0][0] + (d / 2) - 2; pl[4][1] = pl[0][1];
	pl[5][0] = pl[0][0] + d + 2; pl[5][1] = pl[0][1];
	pl[6][0] = pl[0][0] + (d + (d / 2)); pl[6][1] = pl[0][1];
	pl[3][0] = pl[0][0] + d - 45; pl[3][1] = pl[0][1] + (f / 2) + 10;
	pl[1][0] = start_pos[0][0] + (d / 2); pl[1][1] = start_pos[0][1] + 10;
	pl[7][0] = pl[1][0] + (1 * d); pl[7][1] = pl[1][1];
	pl[2][0] = pl[1][0]; pl[2][1] = pl[0][1] - (d / 2);
	pl[8][0] = pl[2][0] + d; pl[8][1] = pl[2][1];
	pl[9][0] = pl[2][0] + (d / 2) - 20; pl[9][1] = pl[2][1] + (d / 8);
}

void WriteText(char *string, int x, int y)
{
	assignment();
	glRasterPos2f(x, y);
	while(*string) glutBitmapCharacter(GLUT_BITMAP_TIMES_ROMAN_24, *string++);
}

/* MODIFIED FOR macOS: Replaced SOIL_load_OGL_texture with stb_image-based loader */
void ImageLoader(const char *file, int a, int b, int c, int d)
{
	/* Build full path: Textures/<filename> */
	char fullpath[256];
	snprintf(fullpath, sizeof(fullpath), "%s%s", TEXTURE_DIR, file);

	/* Flip vertically on load to match OpenGL's bottom-to-top expectation
	   (equivalent to SOIL_FLAG_INVERT_Y in the original) */
	stbi_set_flip_vertically_on_load(1);

	int width, height, channels;
	unsigned char *data = stbi_load(fullpath, &width, &height, &channels, 0);
	if (!data) {
		fprintf(stderr, "Warning: Could not load texture '%s': %s\n", fullpath, stbi_failure_reason());
		return;
	}

	GLenum format = (channels == 4) ? GL_RGBA : GL_RGB;

	GLuint tex;
	glGenTextures(1, &tex);

	glEnable(GL_TEXTURE_2D);

	/* GL_REPLACE: texture color fully replaces fragment color for both RGB and RGBA.
	   (Original used GL_DECAL which caused color distortion on PNG files with alpha) */
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

	glBindTexture(GL_TEXTURE_2D, tex);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

	/* Fix RGB stripe artifacts: stb_image rows may not be 4-byte aligned */
	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

	glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data);
	glGenerateMipmap(GL_TEXTURE_2D);

	stbi_image_free(data);

	glBegin(GL_QUADS);
		glTexCoord2f(0.0, 1.0); glVertex2f(a, b);
		glTexCoord2f(1.0, 1.0); glVertex2f(c, b);
		glTexCoord2f(1.0, 0.0); glVertex2f(c, d);
		glTexCoord2f(0.0, 0.0); glVertex2f(a, d);
	glEnd();

	glFlush();
	glDisable(GL_TEXTURE_2D);

	glDeleteTextures(1, &tex);
}

void drawsquare(int x, int y)
{
	glPointSize(5.0);
	glColor3f(0.309804, 0.184314, 0.309804);

	glBegin(GL_QUADS);
		glVertex2i(x - 90, y + 30);
		glVertex2i(x + 90, y + 30);
		glVertex2i(x + 90, y - 30);
		glVertex2i(x - 90, y - 30);
	glEnd();

	glFlush();
}

void check_answer()
{
	if(e != 4) accept = 0;
	else
	{
		if(c == e) accept = 1;
		else accept = 0;
	}
}

void draw_circle(int h, int k, int r)
{
	double THREE_SIXTY = 2 * PI;
	double ONE_DEGREE = (PI / 180);
	double x, y, i;

	glBegin(GL_POLYGON);

	for(i = ONE_DEGREE; i <= THREE_SIXTY; i += ONE_DEGREE)
	{
		x = h + cos(i) * r;
		y = k + sin(i) * r;
		glVertex2d(x, y);
	}

	glEnd();
}

void draw_edges(int x1, int y1, int x2, int y2)
{
	glLineWidth(8.4f);
	glColor3f(1.0f, 0.0f, 0.0f);
	glBegin(GL_LINES);
		glVertex2d(x1, y1);
		glVertex2d(x2, y2);
	glEnd();
}

void Graph()
{
	int r = 19;
	if(step <= 5)
	{
		glColor3f(1.0, 0.0, 0.0);
		draw_edges(start_pos[0][0], end_pos[0] - a[0], start_pos[1][0], end_pos[1] - a[0]);    /* AB */
		draw_edges(start_pos[0][0], end_pos[0] - a[1], start_pos[5][0], end_pos[5] - a[1]);    /* AF */
		draw_edges(start_pos[0][0], end_pos[0] - a[2], start_pos[4][0], end_pos[4] - a[2]);    /* AE */
		draw_edges(start_pos[1][0], end_pos[1] - a[3], start_pos[2][0], end_pos[2] - a[3]);    /* BC */
		draw_edges(start_pos[1][0], end_pos[1] - a[4], start_pos[5][0], end_pos[5] - a[4]);    /* BF */
		draw_edges(start_pos[2][0], end_pos[2] - a[5], start_pos[5][0], end_pos[5] - a[5]);    /* CF */
		draw_edges(start_pos[2][0], end_pos[2] - a[6], start_pos[3][0], end_pos[3] - a[6]);    /* CD */
		draw_edges(start_pos[3][0], end_pos[3] - a[7], start_pos[5][0], end_pos[5] - a[7]);    /* DF */
		draw_edges(start_pos[4][0], end_pos[4] - a[8], start_pos[3][0], end_pos[3] - a[8]);    /* ED */
		draw_edges(start_pos[4][0], end_pos[4] - a[9], start_pos[5][0], end_pos[5] - a[9]);    /* EF */

		glColor3f(0.419608, 0.137255, 0.556863);  /* dark slate blue */
		draw_circle(start_pos[0][0], start_pos[0][1], r);
		draw_circle(start_pos[1][0], start_pos[1][1], r);
		draw_circle(start_pos[2][0], start_pos[2][1], r);
		draw_circle(start_pos[3][0], start_pos[3][1], r);
		draw_circle(start_pos[4][0], start_pos[4][1], r);
		draw_circle(start_pos[5][0], start_pos[5][1], r);

		int s = 8, w = 650; int fact = 5;

		glColor3f(0.158824, 1.0, 0.0);  /* lime green */
		draw_circle(start_pos[0][0], start_pos[0][1] - w, r);
		draw_circle(start_pos[1][0], start_pos[1][1] - w, r);
		draw_circle(start_pos[2][0], start_pos[2][1] - w, r);
		draw_circle(start_pos[3][0], start_pos[3][1] - w, r);
		draw_circle(start_pos[4][0], start_pos[4][1] - w, r);
		draw_circle(start_pos[5][0], start_pos[5][1] - w, r);

		glColor3f(1.0, 1.0, 0.0);
		WriteText("A", start_pos[0][0] - fact, start_pos[0][1] - s);
		WriteText("B", start_pos[1][0] - fact, start_pos[1][1] - s);
		WriteText("C", start_pos[2][0] - fact, start_pos[2][1] - s);
		WriteText("D", start_pos[3][0] - fact, start_pos[3][1] - s);
		WriteText("E", start_pos[4][0] - fact, start_pos[4][1] - s);
		WriteText("F", start_pos[5][0] - fact, start_pos[5][1] - s);

		glColor3f(1.0, 0.0, 0.0);
		WriteText("A", start_pos[0][0] - fact, start_pos[0][1] - w - s);
		WriteText("B", start_pos[1][0] - fact, start_pos[1][1] - w - s);
		WriteText("C", start_pos[2][0] - fact, start_pos[2][1] - w - s);
		WriteText("D", start_pos[3][0] - fact, start_pos[3][1] - w - s);
		WriteText("E", start_pos[4][0] - fact, start_pos[4][1] - w - s);
		WriteText("F", start_pos[5][0] - fact, start_pos[5][1] - w - s);

		glColor3f(0.556863, 0.137255, 0.419608);
		WriteText("3", pl[0][0], pl[0][1] - a[0]);
		WriteText("5", pl[1][0], pl[1][1] - a[1]);
		WriteText("6", pl[2][0], pl[2][1] - a[2]);
		WriteText("1", pl[3][0], pl[3][1] - a[3]);
		WriteText("4", pl[4][0], pl[4][1] - a[4]);
		WriteText("4", pl[5][0], pl[5][1] - a[5]);
		WriteText("6", pl[6][0], pl[6][1] - a[6]);
		WriteText("5", pl[7][0], pl[7][1] - a[7]);
		WriteText("8", pl[8][0], pl[8][1] - a[8]);
		WriteText("2", pl[9][0], pl[9][1] - a[9]);
	}
	else
	{
		glColor3f(1.0, 0.0, 0.0);
		draw_edges(start_pos[0][0], end_pos[0] - a[0], start_pos[1][0], end_pos[1] - a[0]);    /* AB */
		draw_edges(start_pos[0][0], end_pos[0] - a[1], start_pos[5][0], end_pos[5] - a[1]);    /* AF */
		draw_edges(start_pos[1][0], end_pos[1] - a[3], start_pos[2][0], end_pos[2] - a[3]);    /* BC */
		draw_edges(start_pos[1][0], end_pos[1] - a[4], start_pos[5][0], end_pos[5] - a[4]);    /* BF */
		draw_edges(start_pos[2][0], end_pos[2] - a[5], start_pos[5][0], end_pos[5] - a[5]);    /* CF */
		draw_edges(start_pos[2][0], end_pos[2] - a[6], start_pos[3][0], end_pos[3] - a[6]);    /* CD */
		draw_edges(start_pos[3][0], end_pos[3] - a[7], start_pos[5][0], end_pos[5] - a[7]);    /* DF */

		glColor3f(0.419608, 0.137255, 0.556863);  /* dark slate blue */
		draw_circle(start_pos[0][0], start_pos[0][1], r);
		draw_circle(start_pos[1][0], start_pos[1][1], r);
		draw_circle(start_pos[2][0], start_pos[2][1], r);
		draw_circle(start_pos[3][0], start_pos[3][1], r);
		draw_circle(start_pos[5][0], start_pos[5][1], r);

		int s = 8, w = 650; int fact = 5;

		glColor3f(0.158824, 1.0, 0.0);  /* lime green */
		draw_circle(start_pos[0][0], start_pos[0][1] - w, r);
		draw_circle(start_pos[1][0], start_pos[1][1] - w, r);
		draw_circle(start_pos[2][0], start_pos[2][1] - w, r);
		draw_circle(start_pos[3][0], start_pos[3][1] - w, r);
		draw_circle(start_pos[5][0], start_pos[5][1] - w, r);

		glColor3f(1.0, 1.0, 0.0);
		WriteText("A", start_pos[0][0] - fact, start_pos[0][1] - s);
		WriteText("B", start_pos[1][0] - fact, start_pos[1][1] - s);
		WriteText("C", start_pos[2][0] - fact, start_pos[2][1] - s);
		WriteText("D", start_pos[3][0] - fact, start_pos[3][1] - s);
		WriteText("F", start_pos[5][0] - fact, start_pos[5][1] - s);

		glColor3f(1.0, 0.0, 0.0);
		WriteText("A", start_pos[0][0] - fact, start_pos[0][1] - w - s);
		WriteText("B", start_pos[1][0] - fact, start_pos[1][1] - w - s);
		WriteText("C", start_pos[2][0] - fact, start_pos[2][1] - w - s);
		WriteText("D", start_pos[3][0] - fact, start_pos[3][1] - w - s);
		WriteText("F", start_pos[5][0] - fact, start_pos[5][1] - w - s);

		glColor3f(0.556863, 0.137255, 0.419608);
		WriteText("5", pl[0][0], pl[0][1] - a[0]);
		WriteText("6", pl[1][0], pl[1][1] - a[1]);
		WriteText("1", pl[3][0], pl[3][1] - a[3]);
		WriteText("3", pl[4][0], pl[4][1] - a[4]);
		WriteText("4", pl[5][0], pl[5][1] - a[5]);
		WriteText("6", pl[6][0], pl[6][1] - a[6]);
		WriteText("2", pl[7][0], pl[7][1] - a[7]);
	}
}

void renderer()
{
	int i;
	glClear(GL_COLOR_BUFFER_BIT);
	glPointSize(5.0);

	if(step == -3)
	{
		glClearColor(0.4, 0.4, 0.4, 1.0);
		ImageLoader("logo.png", 50, 950, 950, 50);

		glBegin(GL_QUADS);
			glColor3f(0.4, 0.4, 0.4);
			glVertex2i(50, 370); glVertex2i(452, 370); glVertex2i(452, 375); glVertex2i(50, 375);
		glEnd();

		glBegin(GL_QUADS);
			glColor3f(1.0, 0.5, 0.0);
			glVertex2i(450, 370); glVertex2i(462, 370); glVertex2i(462, 375); glVertex2i(450, 375);
		glEnd();

		glColor3f(0.0, 0.0, 0.0);
		WriteText("Devised by some guy named Kruskal, it's one of the most efficient methods to find out the", 57, 320);
		WriteText("minimum spanning tree of a given graph.Don't worry if nothing of this gets in your head", 57, 290);
		WriteText("right now", 57, 260);

		glBegin(GL_QUADS);
			glColor3f(0.4, 0.4, 0.4);
			glVertex2i(950, 210); glVertex2i(452, 210); glVertex2i(452, 205); glVertex2i(950, 205);
		glEnd();

		glBegin(GL_QUADS);
			glColor3f(1.0, 0.5, 0.0);
			glVertex2i(450, 210); glVertex2i(462, 210); glVertex2i(462, 205); glVertex2i(450, 205);
		glEnd();
	}

	if(step == -2)
	{
		glClearColor(0.4, 0.4, 0.4, 1.0);
		ImageLoader("logo.png", 50, 950, 950, 50);

		glBegin(GL_QUADS);
			glColor3f(0.4, 0.4, 0.4);
			glVertex2i(50, 370); glVertex2i(940, 370); glVertex2i(940, 375); glVertex2i(50, 375);
		glEnd();

		glBegin(GL_QUADS);
			glColor3f(1.0, 0.5, 0.0);
			glVertex2i(940, 370); glVertex2i(950, 370); glVertex2i(950, 375); glVertex2i(940, 375);
		glEnd();

		glColor3f(0.0, 0.0, 0.0);
		WriteText("Let's say there's a salesman who has to traverse a given number of cities and the cost of ", 57, 320);
		WriteText("traversing from a city to other is given. So this algorithm helps him to find the cheapest way", 57, 290);
		WriteText("to do that", 57, 260);

		glBegin(GL_QUADS);
			glColor3f(0.4, 0.4, 0.4);
			glVertex2i(950, 210); glVertex2i(50, 210); glVertex2i(50, 205); glVertex2i(950, 205);
		glEnd();

		glBegin(GL_QUADS);
			glColor3f(1.0, 0.5, 0.0);
			glVertex2i(50, 210); glVertex2i(62, 210); glVertex2i(62, 205); glVertex2i(50, 205);
		glEnd();
	}

	if(step == -1)
	{
		ImageLoader("nature.jpg", 0, 1000, 1000, 0);
		glColor3f(1.0, 1.0, 1.0);
		WriteText("Lets' Consider the problem as graph -- Labeling the nodes, weights of", 217, 390);
		WriteText("the edges and the solution graph", 217, 360);
		Graph();
	}

	if(step == 0)
	{
		ImageLoader("nature.jpg", 0, 1000, 1000, 0);
		ImageLoader("sales1.jpg", 50, 550, 170, 320);
		glColor3f(1.0, 1.0, 1.0);
		WriteText("According to the algorithm we have to choose the minimum weighted edge", 217, 390);
		WriteText("every time and check for edges NOT making cycles i.e., edge BC", 217, 360);
		Graph();
	}

	if(step == 1)
	{
		ImageLoader("nature.jpg", 0, 1000, 1000, 0);
		ImageLoader("sales1.jpg", 50, 550, 170, 320);
		glColor3f(1.0, 1.0, 1.0);
		WriteText("Now the next least weighted edge is FE,doesn't make a cycle either", 217, 390);
		Graph();
	}

	if(step == 2)
	{
		ImageLoader("nature.jpg", 0, 1000, 1000, 0);
		ImageLoader("sales1.jpg", 50, 550, 170, 320);
		glColor3f(1.0, 1.0, 1.0);
		WriteText("Now the next least weighted edge is AB,doesn't make a cycle", 217, 390);
		Graph();
	}

	if(step == 3)
	{
		ImageLoader("nature.jpg", 0, 1000, 1000, 0);
		ImageLoader("sales1.jpg", 50, 550, 170, 320);
		glColor3f(1.0, 1.0, 1.0);
		WriteText("Now the next least weighted edge is BF,doesn't make a cycle", 217, 390);
		Graph();
	}

	if(step == 4)
	{
		ImageLoader("nature.jpg", 0, 1000, 1000, 0);
		ImageLoader("sales1.jpg", 50, 550, 170, 320);
		glColor3f(1.0, 1.0, 1.0);
		WriteText("Now the next least weighted edge is FD,doesn't make a cycle", 217, 390);
		WriteText("could've chosen CF but that makes a cycle viz.,B -> C -> F", 217, 360);
		Graph();
	}

	if(step == 5)
	{
		ImageLoader("nature.jpg", 0, 1000, 1000, 0);
		ImageLoader("sales2.jpg", 830, 620, 950, 420);
		glColor3f(1.0, 1.0, 1.0);
		WriteText("Now the graph below is the one of the optimal minimum spanning tree for", 217, 390);
		WriteText("the given graph,saving money for the salesmen", 217, 360);
		Graph();
	}

	if(step == 6)
	{
		for(i = 0; i < 10; i++) a[i] = 0;
		ImageLoader("nature.jpg", 0, 1000, 1000, 0);
		ImageLoader("test.jpg", 450, 650, 650, 500);
		glColor3f(0.858824, 0.439216, 0.576471);
		WriteText("   How about a problem to test your understanding ?", 237, 790);

		drawsquare(650, 350);
		glColor3f(1.0, 1.0, 0.0);
		WriteText("Yeah Sure", 600, 345);

		drawsquare(x11, y11);
		glColor3f(1.0, 1.0, 0.0);
		WriteText("No Thanks", x11 - 50, y11);
	}

	if(step == 7)
	{
		ImageLoader("nature.jpg", 0, 1000, 1000, 0);
		ImageLoader("mouseup2.jpg", 820, 750, 920, 600);
		glColor3f(1.0, 1.0, 1.0);
		WriteText("Find out the minimum spanning tree of the above graph using Kruskal Algorithm", 117, 390);
		WriteText("by clicking on the middle of the edges you want to select", 117, 360);

		drawsquare(650, 550);
		glColor3f(1.0, 1.0, 0.0);
		WriteText("Reset", 620, 545);

		drawsquare(250, 550);
		glColor3f(1.0, 1.0, 0.0);
		WriteText("Check", 210, 545);
		Graph();
	}
	if(step == 8)
	{
		ImageLoader("nature.jpg", 0, 1000, 1000, 0);
		ImageLoader("mouseup2.jpg", 820, 750, 920, 600);
		glColor3f(1.0, 1.0, 1.0);
		WriteText("Find out the minimum spanning tree of the above graph using Kruskal Algorithm", 117, 390);
		WriteText("by clicking on the middle of the edges you want to select", 117, 360);

		drawsquare(650, 550);
		glColor3f(1.0, 1.0, 0.0);
		WriteText("Reset", 620, 545);

		drawsquare(250, 550);
		glColor3f(1.0, 1.0, 0.0);
		WriteText("Check", 210, 545);
		Graph();

		const char *str;
		if(accept) str = "right.png";
		else str = "wrong.png";
		glColor3f(0.196078, 0.6, 0.8);
		ImageLoader(str, 415, 550, 515, 650);
	}

	glFlush();
}

void clickEvent(int btn, int state, int x, int y)
{
	int i;
	if(btn == GLUT_LEFT_BUTTON && state == GLUT_DOWN)
	{
		printf("%d %d\n", x, y);
		if(step == 6 && (x <= 340) && (x >= 160) && (y >= 620) && (y <= 680))
		{
			x11 = 350; y11 = 450;
			glutPostRedisplay();
		}
		if(step == 6 && (x <= 440) && (x >= 260) && (y >= 520) && (y <= 580))
		{
			x11 = 450; y11= 550;
			glutPostRedisplay();
		}
		if(step == 6 && (x <= 540) && (x >= 360) && (y >= 420) && (y <= 480))
		{
			x11 = 250; y11 = 350;
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
			check_answer();
			glutPostRedisplay();
		}
		if(step == 7 && (x <= 740) && (x >= 560) && (y >= 420) && (y <= 480))
		{
			assignment(); e = c = 0;
			for(i = 0; i < 10; i++) a[i] = 0;
			glutPostRedisplay();
		}
		if(step == 8 && (x <= 740) && (x >= 560) && (y >= 420) && (y <= 480))
		{
			step--;
			assignment(); e = c = 0;
			for(i = 0; i < 10; i++) a[i] = 0;
			glutPostRedisplay();
		}
		if(step == 7)
		{
			if( (x >= 625 && x <= 650) && (y >= 165 && y <= 185) )
			{
				e++; c++;
				a[7] = 650;
			}
			if( (x >= 487 && x <= 510) && (y >= 75 && y <= 82) )
			{
				e++; c++;
				a[3] = 650;
			}
			if( (x >= 420 && x <= 435) && (y >= 105 && y <= 125) )
			{
				e++; c++;
				a[4] = 650;
			}
			if( (x >= 305 && x <= 315) && (y >= 120 && y <= 145) )
			{
				e++; c++;
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

void typeEvent(int key, int x, int y)
{
	if(key == GLUT_KEY_RIGHT)
	{
		if(step == 0) a[3] = 650;
		if(step == 1) a[9] = 650;
		if(step == 2) a[0] = 650;
		if(step == 3) a[4] = 650;
		if(step == 4) a[7] = 650;
		step++;
		glutPostRedisplay();
	}

	if(key == GLUT_KEY_LEFT)
	{
		if(step == 1) a[3] = 0;
		if(step == 2) a[9] = 0;
		if(step == 3) a[0] = 0;
		if(step == 4) a[4] = 0;
		if(step == 5) a[7] = 0;
		step--;
		glutPostRedisplay();
	}
}

int main(int argc, char **argv)
{
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
	glutInitWindowSize(1000, 1000);
	glutInitWindowPosition(70, 10);
	glutCreateWindow("Kruskal Algorithm Tutorial");
	glutDisplayFunc(renderer);
	glutMouseFunc(clickEvent);
	glutSpecialFunc(typeEvent);
	initial_mode();
	glutMainLoop();
	return 0;
}
