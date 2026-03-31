# Optimisation Guide: Better Code for the Same Result

> How to restructure this project for maintainability, responsiveness, extensibility, and modern best practices — while keeping the same visual tutorial experience.

---

## Table of Contents

1. [Current Pain Points](#current-pain-points)
2. [Optimisation 1: Data-Driven Steps (Eliminate the if-else Cascade)](#optimisation-1-data-driven-steps)
3. [Optimisation 2: Responsive / Resolution-Independent Graphics](#optimisation-2-responsive-graphics)
4. [Optimisation 3: Proper Graph Data Structure](#optimisation-3-proper-graph-data-structure)
5. [Optimisation 4: Texture Management](#optimisation-4-texture-management)
6. [Optimisation 5: Actual Algorithm Implementation](#optimisation-5-actual-algorithm-implementation)
7. [Optimisation 6: Smooth Animation](#optimisation-6-smooth-animation)
8. [Optimisation 7: State Machine Pattern](#optimisation-7-state-machine-pattern)
9. [Optimisation 8: Configurable Graph Input](#optimisation-8-configurable-graph-input)
10. [Optimisation 9: Modern Alternatives to Legacy OpenGL](#optimisation-9-modern-alternatives)
11. [Optimisation 10: Code Organisation](#optimisation-10-code-organisation)
12. [Putting It All Together: Refactored Architecture](#putting-it-all-together)

---

## Current Pain Points

Before diving into solutions, here's what makes the current code hard to maintain:

| Issue | Where | Why It's a Problem |
|-------|-------|--------------------|
| Giant if-else cascade in `renderer()` | `step == -3`, `step == -2`, ... `step == 8` — 11 branches | Adding/removing/reordering steps requires editing multiple places |
| Hardcoded pixel coordinates | `254, 820`, `379, 920`, etc. everywhere | Can't resize the window; can't adapt to different screen sizes |
| Magic numbers | `650`, `19`, `8.4f`, `0.419608`, `0.137255` | Meaning is unclear without reading surrounding context |
| Duplicate graph drawing code | `Graph()` has `if(step <= 5)` and `else` branches with near-identical code | Bug fixes need to be applied in two places |
| No real graph data structure | Vertices are parallel arrays (`start_pos[][]`), edges are implicit in `draw_edges()` call ordering | Hard to reason about which edge is which |
| Texture re-loaded every frame (original C) | `ImageLoader()` loads from disk, creates GL texture, draws, and deletes — every single frame | Massive performance waste |
| Mixed rendering layers (Java) | OpenGL for shapes, AWT for text/images — fighting for the same pixels | Rendering order bugs, platform-specific flickering |
| No separation of concerns | Rendering, input handling, game state, and data all in one file | Everything is entangled |

---

## Optimisation 1: Data-Driven Steps

### The Problem

The current `renderer()` function is a wall of if-else:

```c
if(step == -3) { /* 30 lines */ }
if(step == -2) { /* 30 lines */ }
if(step == -1) { /* 5 lines */ }
if(step == 0)  { /* 8 lines */ }
// ... 8 more blocks
```

Adding a new step means: add a new if-block, update all step number references, update the keyboard handler, etc.

### The Solution: Step Descriptors

Define each step as a data structure:

```c
typedef struct {
    const char *background;       // texture filename (NULL for grey bg)
    const char *overlay_image;    // additional image (NULL if none)
    int overlay_x, overlay_y, overlay_w, overlay_h;
    const char *text_lines[4];    // up to 4 lines of text
    int text_x, text_y;           // text starting position
    float text_r, text_g, text_b; // text colour
    int show_graph;               // whether to call Graph()
    int edge_to_move;             // which a[i] to set to 650 on advance (-1 for none)
    int edge_to_undo;             // which a[i] to set to 0 on go-back (-1 for none)
} StepDescriptor;

static const StepDescriptor steps[] = {
    // Step -3 → index 0
    {
        .background = NULL,  // grey bg
        .overlay_image = "logo.png",
        .overlay_x = 50, .overlay_y = 950, .overlay_w = 950, .overlay_h = 50,
        .text_lines = {
            "Devised by some guy named Kruskal, it's one of the most efficient",
            "methods to find out the minimum spanning tree of a given graph.",
            "Don't worry if nothing of this gets in your head right now",
            NULL
        },
        .text_x = 57, .text_y = 320,
        .text_r = 0, .text_g = 0, .text_b = 0,
        .show_graph = 0,
        .edge_to_move = -1,
        .edge_to_undo = -1,
    },
    // Step -2 → index 1
    { /* ... */ },
    // Step -1 → index 2
    {
        .background = "nature.jpg",
        .text_lines = { "Let's consider the problem as graph...", NULL },
        .show_graph = 1,
        .edge_to_move = -1,
    },
    // Step 0 → index 3
    {
        .background = "nature.jpg",
        .overlay_image = "sales1.jpg",
        .text_lines = { "According to the algorithm...", NULL },
        .show_graph = 1,
        .edge_to_move = 3,   // a[3] = 650 → BC moves down
        .edge_to_undo = 3,
    },
    // ... etc
};

#define NUM_STEPS (sizeof(steps) / sizeof(steps[0]))
```

Now the renderer becomes **generic**:

```c
void renderer() {
    int idx = step + 3;  // map step -3..8 to index 0..11
    if (idx < 0 || idx >= NUM_STEPS) return;

    const StepDescriptor *s = &steps[idx];
    glClear(GL_COLOR_BUFFER_BIT);

    if (s->background)
        draw_cached_texture(s->background, 0, 0, 1000, 1000);
    else
        glClearColor(0.4, 0.4, 0.4, 1.0);

    if (s->overlay_image)
        draw_cached_texture(s->overlay_image, s->overlay_x, s->overlay_y, s->overlay_w, s->overlay_h);

    glColor3f(s->text_r, s->text_g, s->text_b);
    for (int i = 0; s->text_lines[i]; i++)
        WriteText(s->text_lines[i], s->text_x, s->text_y - i * 30);

    if (s->show_graph)
        Graph();

    glFlush();
}

void typeEvent(int key, int x, int y) {
    int idx = step + 3;
    if (key == GLUT_KEY_RIGHT && idx + 1 < NUM_STEPS) {
        if (steps[idx].edge_to_move >= 0)
            a[steps[idx].edge_to_move] = 650;
        step++;
        glutPostRedisplay();
    }
    if (key == GLUT_KEY_LEFT && idx > 0) {
        if (steps[idx].edge_to_undo >= 0)
            a[steps[idx].edge_to_undo] = 0;
        step--;
        glutPostRedisplay();
    }
}
```

**Benefits:**
- Adding a step = adding one struct to the array
- Reordering steps = moving array elements
- No rendering code changes needed for content changes
- Step data could even be loaded from a JSON/config file

---

## Optimisation 2: Responsive Graphics

### The Problem

Everything is hardcoded to a 1000×1000 pixel window:
```c
gluOrtho2D(0.0, 1000.0, 0.0, 1000.0);
glutInitWindowSize(1000, 1000);
start_pos[0][0] = 254; start_pos[0][1] = 820;  // absolute pixels
```

Resizing the window clips or stretches the content.

### The Solution: Normalised Coordinates + Viewport Scaling

**Use a 0.0–1.0 normalised coordinate system** and convert to actual pixels at render time:

```c
// Define positions as fractions of window size (0.0 to 1.0)
#define VERT_A_X  0.254f
#define VERT_A_Y  0.820f
#define VERT_B_X  0.379f
#define VERT_B_Y  0.920f

// In the reshape callback, maintain aspect ratio
void reshape(int w, int h) {
    glViewport(0, 0, w, h);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();

    float aspect = (float)w / (float)h;
    if (aspect >= 1.0f) {
        // Window wider than tall — add horizontal padding
        gluOrtho2D(-0.5 * (aspect - 1.0), 1.0 + 0.5 * (aspect - 1.0), 0.0, 1.0);
    } else {
        // Window taller than wide — add vertical padding
        float inv = 1.0f / aspect;
        gluOrtho2D(0.0, 1.0, -0.5 * (inv - 1.0), 1.0 + 0.5 * (inv - 1.0));
    }
}
```

Now all vertex positions are in `[0, 1]` range and scale with window size.

**For vertex circles**, scale the radius proportionally:

```c
void draw_circle(float cx, float cy, float radius_frac) {
    // radius_frac is something like 0.019 (19/1000)
    for (float angle = 0; angle < 2*PI; angle += PI/180) {
        float x = cx + cos(angle) * radius_frac;
        float y = cy + sin(angle) * radius_frac;
        glVertex2f(x, y);
    }
}
```

**For text**, GLUT bitmap fonts don't scale. Options:
1. Use `glutStrokeCharacter()` with `GLUT_STROKE_ROMAN` (vector font, scales with transforms)
2. Use a text rendering library
3. Accept fixed-size text (it still looks fine on resize since it's screen-pixel based)

---

## Optimisation 3: Proper Graph Data Structure

### The Problem

There's no explicit graph representation. Vertices are in `start_pos[][]`, edges are implicit in the order of `draw_edges()` calls, and weights are hardcoded strings in `WriteText()` calls.

### The Solution

```c
typedef struct {
    float x, y;
    char label;
} Vertex;

typedef struct {
    int from, to;    // vertex indices
    int weight;
    int in_mst;      // is this edge part of the MST?
    float offset_y;  // current Y-offset for animation (0.0 or TARGET_OFFSET)
} Edge;

typedef struct {
    Vertex vertices[MAX_VERTICES];
    int num_vertices;
    Edge edges[MAX_EDGES];
    int num_edges;
} Graph;

// The tutorial graph
Graph tutorial_graph = {
    .vertices = {
        {0.254, 0.820, 'A'},
        {0.379, 0.920, 'B'},
        {0.629, 0.920, 'C'},
        {0.754, 0.820, 'D'},
        {0.504, 0.720, 'E'},
        {0.504, 0.820, 'F'},
    },
    .num_vertices = 6,
    .edges = {
        {0, 1, 3, 1, 0},  // A-B, weight 3, IS in MST
        {0, 5, 5, 0, 0},  // A-F, weight 5, NOT in MST
        {0, 4, 6, 0, 0},  // A-E, weight 6
        {1, 2, 1, 1, 0},  // B-C, weight 1, IS in MST
        {1, 5, 4, 1, 0},  // B-F, weight 4, IS in MST
        {2, 5, 4, 0, 0},  // C-F, weight 4
        {2, 3, 6, 0, 0},  // C-D, weight 6
        {3, 5, 5, 1, 0},  // D-F, weight 5, IS in MST
        {4, 3, 8, 0, 0},  // E-D, weight 8
        {4, 5, 2, 1, 0},  // E-F, weight 2, IS in MST
    },
    .num_edges = 10,
};
```

Now rendering becomes a loop:

```c
void render_graph(Graph *g, float lower_offset) {
    // Draw all edges
    for (int i = 0; i < g->num_edges; i++) {
        Edge *e = &g->edges[i];
        Vertex *v1 = &g->vertices[e->from];
        Vertex *v2 = &g->vertices[e->to];
        draw_edge_line(v1->x, v1->y - e->offset_y,
                       v2->x, v2->y - e->offset_y);
        // Draw weight label at midpoint
        float mx = (v1->x + v2->x) / 2;
        float my = (v1->y + v2->y) / 2 - e->offset_y;
        draw_number(e->weight, mx, my);
    }

    // Draw upper vertices
    for (int i = 0; i < g->num_vertices; i++)
        draw_circle(g->vertices[i].x, g->vertices[i].y, VERTEX_RADIUS);

    // Draw lower (MST) vertices
    for (int i = 0; i < g->num_vertices; i++)
        draw_circle(g->vertices[i].x, g->vertices[i].y - lower_offset, VERTEX_RADIUS);
}
```

**Benefits:**
- Edge weight labels are computed from data, not copy-pasted
- Midpoint label positions are calculated, not manually placed
- Adding/removing vertices/edges only changes the data arrays
- The quiz graph can be a separate `Graph` struct
- Graph data could be loaded from a file

---

## Optimisation 4: Texture Management

### The Problem (C Version, original)

`ImageLoader()` does this **every single frame**:
1. Load image from disk → `stbi_load()`
2. Create GPU texture → `glGenTextures()`, `glTexImage2D()`
3. Draw one quad
4. Delete GPU texture → `glDeleteTextures()`

This is like buying a picture frame, hanging a photo, looking at it, and throwing the frame away — 60 times per second.

### The Solution: Texture Cache

```c
#define MAX_TEXTURES 16

typedef struct {
    char filename[64];
    GLuint gl_id;
    int width, height;
    int loaded;
} CachedTexture;

static CachedTexture texture_cache[MAX_TEXTURES];
static int num_cached = 0;

GLuint get_texture(const char *filename) {
    // Check cache first
    for (int i = 0; i < num_cached; i++) {
        if (strcmp(texture_cache[i].filename, filename) == 0)
            return texture_cache[i].gl_id;
    }

    // Not cached — load it
    char fullpath[256];
    snprintf(fullpath, sizeof(fullpath), "%s%s", TEXTURE_DIR, filename);

    int w, h, channels;
    unsigned char *data = stbi_load(fullpath, &w, &h, &channels, 0);
    if (!data) return 0;

    GLenum fmt = (channels == 4) ? GL_RGBA : GL_RGB;
    GLuint tex;
    glGenTextures(1, &tex);
    glBindTexture(GL_TEXTURE_2D, tex);
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    glTexImage2D(GL_TEXTURE_2D, 0, fmt, w, h, 0, fmt, GL_UNSIGNED_BYTE, data);
    glGenerateMipmap(GL_TEXTURE_2D);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    stbi_image_free(data);

    // Store in cache
    CachedTexture *c = &texture_cache[num_cached++];
    strncpy(c->filename, filename, 63);
    c->gl_id = tex;
    c->width = w;
    c->height = h;
    c->loaded = 1;

    return tex;
}

void draw_textured_quad(const char *filename, float x1, float y1, float x2, float y2) {
    GLuint tex = get_texture(filename);
    if (!tex) return;

    glEnable(GL_TEXTURE_2D);
    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
    glBindTexture(GL_TEXTURE_2D, tex);

    glBegin(GL_QUADS);
        glTexCoord2f(0, 1); glVertex2f(x1, y1);
        glTexCoord2f(1, 1); glVertex2f(x2, y1);
        glTexCoord2f(1, 0); glVertex2f(x2, y2);
        glTexCoord2f(0, 0); glVertex2f(x1, y2);
    glEnd();

    glDisable(GL_TEXTURE_2D);
}
```

**Result:** Textures are loaded from disk **once** and reused forever. The macOS port already partially does this for the Java version (`bgTexture` is cached), but the C version still reloads every frame.

---

## Optimisation 5: Actual Algorithm Implementation

### The Problem

The demo doesn't actually run Kruskal's algorithm. The MST edges are hardcoded — the code "knows" in advance which edges to select. This means the demo can only show one specific graph.

### The Solution: Implement Union-Find + Kruskal's

```c
// Union-Find (Disjoint Set Union) for cycle detection
int parent[MAX_VERTICES];
int rank_arr[MAX_VERTICES];

void uf_init(int n) {
    for (int i = 0; i < n; i++) { parent[i] = i; rank_arr[i] = 0; }
}

int uf_find(int x) {
    if (parent[x] != x) parent[x] = uf_find(parent[x]);  // path compression
    return parent[x];
}

int uf_union(int x, int y) {
    int rx = uf_find(x), ry = uf_find(y);
    if (rx == ry) return 0;  // already connected → would create cycle
    if (rank_arr[rx] < rank_arr[ry]) { int t = rx; rx = ry; ry = t; }
    parent[ry] = rx;
    if (rank_arr[rx] == rank_arr[ry]) rank_arr[rx]++;
    return 1;  // successfully merged
}

// Kruskal's algorithm — generates the step sequence
typedef struct { int edge_index; } MSTStep;
MSTStep mst_steps[MAX_EDGES];
int num_mst_steps = 0;

void compute_kruskal(Graph *g) {
    // Sort edges by weight
    int sorted[MAX_EDGES];
    for (int i = 0; i < g->num_edges; i++) sorted[i] = i;
    // (simple insertion sort for small n)
    for (int i = 1; i < g->num_edges; i++) {
        int key = sorted[i];
        int j = i - 1;
        while (j >= 0 && g->edges[sorted[j]].weight > g->edges[key].weight) {
            sorted[j+1] = sorted[j];
            j--;
        }
        sorted[j+1] = key;
    }

    uf_init(g->num_vertices);
    num_mst_steps = 0;

    for (int i = 0; i < g->num_edges; i++) {
        Edge *e = &g->edges[sorted[i]];
        if (uf_union(e->from, e->to)) {
            e->in_mst = 1;
            mst_steps[num_mst_steps++].edge_index = sorted[i];
        }
    }
}
```

Now the tutorial steps are **generated** from the algorithm, not hardcoded. You could feed in **any** graph and the demo would automatically produce the correct step sequence.

---

## Optimisation 6: Smooth Animation

### The Problem

Currently, edges "teleport" from the upper graph to the lower graph — `a[i]` jumps from 0 to 650 instantly.

### The Solution: Lerp (Linear Interpolation)

```c
typedef struct {
    float current;
    float target;
    float speed;  // units per second
} AnimatedValue;

void update_animated(AnimatedValue *v, float dt) {
    if (v->current < v->target) {
        v->current += v->speed * dt;
        if (v->current > v->target) v->current = v->target;
    } else if (v->current > v->target) {
        v->current -= v->speed * dt;
        if (v->current < v->target) v->current = v->target;
    }
}

// Replace int a[10] with:
AnimatedValue edge_offset[10];

// In the step advance:
edge_offset[3].target = 650.0f;  // BC starts sliding down
edge_offset[3].speed = 1300.0f;  // pixels per second (half-second animation)

// In the render loop (use glutIdleFunc or glutTimerFunc for timing):
void idle() {
    float dt = get_delta_time();  // time since last frame
    for (int i = 0; i < 10; i++)
        update_animated(&edge_offset[i], dt);
    glutPostRedisplay();
}
```

For GLUT timing:

```c
static int last_time = 0;

float get_delta_time() {
    int now = glutGet(GLUT_ELAPSED_TIME);
    float dt = (now - last_time) / 1000.0f;
    last_time = now;
    return dt;
}
```

**Switch to double-buffered** for smooth animation:

```c
glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB);  // was GLUT_SINGLE
// End of renderer():
glutSwapBuffers();  // instead of glFlush()
```

---

## Optimisation 7: State Machine Pattern

### The Problem

The step-based approach mixes rendering states with application logic. The intro screens, the tutorial steps, and the quiz are all handled by the same `renderer()` function checking `step`.

### The Solution: Explicit State Machine

```c
typedef enum {
    STATE_INTRO,
    STATE_TUTORIAL,
    STATE_QUIZ_PROMPT,
    STATE_QUIZ_ACTIVE,
    STATE_QUIZ_RESULT,
} AppState;

typedef struct {
    AppState state;
    int intro_page;        // 0, 1, 2 for the intro screens
    int tutorial_step;     // 0..4 for the 5 MST edges
    Graph *current_graph;
    int quiz_selections[MAX_EDGES];
    int quiz_correct;
} AppContext;

void render(AppContext *ctx) {
    switch (ctx->state) {
        case STATE_INTRO:    render_intro(ctx);        break;
        case STATE_TUTORIAL: render_tutorial(ctx);      break;
        case STATE_QUIZ_PROMPT: render_quiz_prompt(ctx); break;
        case STATE_QUIZ_ACTIVE: render_quiz(ctx);       break;
        case STATE_QUIZ_RESULT: render_result(ctx);     break;
    }
}

void handle_key(AppContext *ctx, int key) {
    switch (ctx->state) {
        case STATE_INTRO:
            if (key == GLUT_KEY_RIGHT) {
                ctx->intro_page++;
                if (ctx->intro_page > 2) {
                    ctx->state = STATE_TUTORIAL;
                    ctx->tutorial_step = 0;
                }
            }
            break;
        case STATE_TUTORIAL:
            if (key == GLUT_KEY_RIGHT) {
                advance_tutorial(ctx);
                if (ctx->tutorial_step >= NUM_MST_EDGES) {
                    ctx->state = STATE_QUIZ_PROMPT;
                }
            }
            break;
        // ...
    }
}
```

**Benefits:**
- Each state has its own render/input handler — no giant if-else
- State transitions are explicit and documented
- Easy to add new states (e.g., a "help" screen, a "choose graph" screen)
- Input handling is state-aware (quiz clicks don't fire during tutorial)

---

## Optimisation 8: Configurable Graph Input

### The Problem

The graph is hardcoded in C arrays. To change the graph (different vertices, edges, weights), you must edit the source code and recompile.

### The Solution: Load from a Simple Text File

**Format (`graph.txt`):**
```
# Vertices: label x y
V A 0.254 0.820
V B 0.379 0.920
V C 0.629 0.920
V D 0.754 0.820
V E 0.504 0.720
V F 0.504 0.820

# Edges: from to weight
E A B 3
E A F 5
E A E 6
E B C 1
E B F 4
E C F 4
E C D 6
E D F 5
E D E 8
E F E 2
```

**Loader:**
```c
Graph* load_graph(const char *filename) {
    Graph *g = calloc(1, sizeof(Graph));
    FILE *f = fopen(filename, "r");
    char line[256];

    while (fgets(line, sizeof(line), f)) {
        if (line[0] == '#' || line[0] == '\n') continue;

        char type;
        if (sscanf(line, "%c", &type) != 1) continue;

        if (type == 'V') {
            char label;
            float x, y;
            sscanf(line, "V %c %f %f", &label, &x, &y);
            g->vertices[g->num_vertices++] = (Vertex){x, y, label};
        }
        else if (type == 'E') {
            char from, to;
            int weight;
            sscanf(line, "E %c %c %d", &from, &to, &weight);
            int fi = find_vertex(g, from);
            int ti = find_vertex(g, to);
            g->edges[g->num_edges++] = (Edge){fi, ti, weight, 0, 0};
        }
    }
    fclose(f);
    return g;
}
```

Now you can demo Kruskal's on **any** graph by swapping the config file — no recompilation needed.

---

## Optimisation 9: Modern Alternatives to Legacy OpenGL

### The Problem

Both implementations use **legacy OpenGL** (fixed-function pipeline with `glBegin/glEnd`, `glVertex`, `glColor`). OpenGL is deprecated on macOS (since 10.14 Mojave) and the fixed-function pipeline is considered obsolete everywhere.

### Options (in order of recommendation for a project like this)

#### Option A: SDL2 + SDL_Renderer (Easiest Migration)

```c
#include <SDL2/SDL.h>
#include <SDL2/SDL_image.h>
#include <SDL2/SDL_ttf.h>

// Circles: SDL_RenderDrawPoint in a loop
// Lines: SDL_RenderDrawLine
// Images: SDL_Texture loaded once, SDL_RenderCopy to draw
// Text: TTF_RenderText_Blended → SDL_CreateTextureFromSurface

// Benefits:
// - Works on macOS, Windows, Linux without deprecation warnings
// - 2D-focused API (no 3D overhead)
// - SDL_Image handles PNG/JPG natively
// - SDL_TTF gives scalable TrueType fonts
// - brew install sdl2 sdl2_image sdl2_ttf
```

#### Option B: Raylib (Simplest API)

```c
#include "raylib.h"

int main() {
    InitWindow(1000, 1000, "Kruskal Tutorial");
    Texture2D bg = LoadTexture("Textures/nature.jpg");

    while (!WindowShouldClose()) {
        BeginDrawing();
        DrawTexture(bg, 0, 0, WHITE);
        DrawCircle(254, 180, 19, PURPLE);
        DrawLine(254, 180, 379, 80, RED);
        DrawText("A", 249, 172, 24, YELLOW);
        EndDrawing();
    }

    UnloadTexture(bg);
    CloseWindow();
}

// Benefits:
// - Single header, trivial setup
// - Works on all platforms including web (via Emscripten!)
// - Built-in texture caching, font rendering, input handling
// - Perfect for educational graphics projects
```

#### Option C: Dear ImGui + OpenGL 3.3 (Most Modern)

For a more "application-like" feel with UI widgets for controls instead of keyboard input.

#### Option D: Web-Based (HTML5 Canvas / p5.js)

```javascript
// p5.js version — runs in any browser, no compilation needed
function setup() {
    createCanvas(1000, 1000);
    bg = loadImage('Textures/nature.jpg');
}

function draw() {
    image(bg, 0, 0, 1000, 1000);
    fill(100, 0, 150);
    circle(254, 180, 38);
    stroke(255, 0, 0);
    strokeWeight(8);
    line(254, 180, 379, 80);
    fill(255, 255, 0);
    text('A', 249, 185);
}
```

**This is arguably the best option for a tutorial demo** — zero installation, runs everywhere, easy to share as a URL.

---

## Optimisation 10: Code Organisation

### The Problem

Everything is in one 640-line file. Rendering, input, data, and logic are all interleaved.

### The Solution: Modular File Structure

```
src/
├── main.c              # Entry point, GLUT setup, main loop
├── graph.h / graph.c   # Graph data structure, loading, Kruskal's algorithm
├── renderer.h / .c     # All OpenGL drawing functions
├── texture.h / .c      # Texture cache management
├── steps.h / .c        # Step descriptors, step logic
├── quiz.h / .c         # Quiz state, validation
├── input.h / .c        # Keyboard and mouse handlers
├── animation.h / .c    # Lerp/animation utilities
└── constants.h         # Named constants (colours, sizes, offsets)
```

**`constants.h`** — replace all magic numbers:

```c
#ifndef CONSTANTS_H
#define CONSTANTS_H

// Window
#define WINDOW_W        1000
#define WINDOW_H        1000

// Graph layout
#define GRAPH_UPPER_Y   0.82f
#define GRAPH_LOWER_Y   0.17f
#define GRAPH_OFFSET    0.65f   // was the magic number 650
#define VERTEX_RADIUS   0.019f

// Colours (named after their visual purpose, not RGB values)
#define COLOR_VERTEX_UPPER   0.419608f, 0.137255f, 0.556863f  // dark slate blue
#define COLOR_VERTEX_LOWER   0.158824f, 1.0f, 0.0f            // lime green
#define COLOR_EDGE           1.0f, 0.0f, 0.0f                 // red
#define COLOR_LABEL_UPPER    1.0f, 1.0f, 0.0f                 // yellow
#define COLOR_LABEL_LOWER    1.0f, 0.0f, 0.0f                 // red
#define COLOR_WEIGHT         0.556863f, 0.137255f, 0.419608f  // purple-pink
#define COLOR_BUTTON_BG      0.309804f, 0.184314f, 0.309804f  // dark plum
#define COLOR_BUTTON_TEXT     1.0f, 1.0f, 0.0f                // yellow

// Line widths
#define EDGE_LINE_WIDTH  8.4f

#endif
```

---

## Putting It All Together

### Refactored Architecture Diagram

```
┌─────────────────────────────────────────────┐
│                 main.c                       │
│  - glutInit, window creation                │
│  - Register callbacks                       │
│  - glutMainLoop                             │
└────────┬──────────┬────────────┬────────────┘
         │          │            │
    ┌────▼───┐ ┌────▼────┐ ┌────▼─────┐
    │ input  │ │renderer │ │animation │
    │handler │ │  loop   │ │  update  │
    └────┬───┘ └────┬────┘ └────┬─────┘
         │          │            │
    ┌────▼──────────▼────────────▼────┐
    │         AppContext (state)       │
    │  - current state (intro/tutorial │
    │    /quiz/result)                 │
    │  - graph data                   │
    │  - animation values             │
    │  - quiz selections              │
    └────────────┬────────────────────┘
                 │
    ┌────────────▼────────────────────┐
    │        graph.c + steps.c        │
    │  - Graph struct (vertices,      │
    │    edges, weights)              │
    │  - StepDescriptor array         │
    │  - compute_kruskal()            │
    │  - load_graph() from file       │
    └────────────┬────────────────────┘
                 │
    ┌────────────▼────────────────────┐
    │          texture.c              │
    │  - Texture cache (load once)    │
    │  - draw_textured_quad()         │
    └─────────────────────────────────┘
```

### Priority Order for Refactoring

If you want to incrementally improve the codebase, here's the recommended order (highest impact first):

| Priority | Optimisation | Effort | Impact |
|----------|-------------|--------|--------|
| 1 | **Texture caching** (Opt 4) | Low | High — eliminates per-frame disk I/O |
| 2 | **Named constants** (Opt 10 partial) | Low | Medium — makes code readable |
| 3 | **Graph data structure** (Opt 3) | Medium | High — enables loops instead of copy-paste |
| 4 | **Data-driven steps** (Opt 1) | Medium | High — eliminates if-else cascade |
| 5 | **Responsive coords** (Opt 2) | Medium | Medium — enables window resize |
| 6 | **State machine** (Opt 7) | Medium | Medium — cleaner architecture |
| 7 | **Smooth animation** (Opt 6) | Low | Medium — visual polish |
| 8 | **Actual algorithm** (Opt 5) | Medium | High — makes it work for any graph |
| 9 | **File-based graph** (Opt 8) | Low | Medium — runtime configuration |
| 10 | **Modern library** (Opt 9) | High | High — future-proofs the project |

### The Dream: Web Version

The ultimate optimisation for a **tutorial/educational demo** is to port it to the web. A p5.js or HTML5 Canvas version would:
- Run in any browser, no installation
- Be shareable as a single URL
- Work on phones and tablets
- Support touch input for the quiz
- Be deployable to GitHub Pages for free

The core logic (graph, steps, quiz validation) would translate almost 1:1 from C to JavaScript, and the rendering would be simpler (Canvas 2D API is higher-level than OpenGL).

---

## Summary

The current codebase works, and that's an achievement. But it's a "write once, don't touch" kind of project. With these optimisations, it could become:

- **Maintainable**: Data-driven steps, named constants, modular files
- **Flexible**: Any graph, loaded from file, algorithm computed at runtime
- **Responsive**: Works at any window size
- **Smooth**: Animated edge transitions instead of teleportation
- **Performant**: Textures loaded once, not every frame
- **Portable**: Web version runs anywhere
- **Educational**: The code itself becomes a teaching tool, not just the demo
