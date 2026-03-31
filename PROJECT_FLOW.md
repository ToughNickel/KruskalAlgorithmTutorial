# Project Flow: Kruskal Algorithm Graphics Tutorial

> A detailed walkthrough of the entire demo — the algorithm, the graph, the animation trick, the quiz, and how every piece of code ties together.

---

## Table of Contents

1. [What This Project Is](#what-this-project-is)
2. [The Algorithm: Kruskal's Minimum Spanning Tree](#the-algorithm-kruskals-minimum-spanning-tree)
3. [The Graph Used in the Demo](#the-graph-used-in-the-demo)
4. [Step-by-Step Demo Flow](#step-by-step-demo-flow)
5. [The Animation Trick (C Version)](#the-animation-trick-c-version)
6. [The Edge-Drag Mechanism (Java Version)](#the-edge-drag-mechanism-java-version)
7. [The Quiz System](#the-quiz-system)
8. [Rendering Pipeline](#rendering-pipeline)
9. [Data Structures & Variables](#data-structures--variables)
10. [Differences Between C and Java Implementations](#differences-between-c-and-java-implementations)

---

## What This Project Is

This is **not** a programmatic implementation of Kruskal's algorithm — it doesn't compute an MST from arbitrary input. Instead, it's a **pre-scripted, interactive visual tutorial** that walks the user through applying Kruskal's algorithm on a specific hardcoded graph, step by step. Think of it as an animated slideshow with a quiz at the end.

The demo is framed as a real-world scenario: a salesman needs to find the cheapest route connecting multiple cities. This is the classic Minimum Spanning Tree problem.

---

## The Algorithm: Kruskal's Minimum Spanning Tree

### The Problem

Given a connected, undirected, weighted graph, find a subset of edges that:
1. Connects all vertices (forms a spanning tree)
2. Has the minimum total edge weight

### Kruskal's Algorithm (Greedy Approach)

```
1. Sort all edges by weight (ascending)
2. Initialize an empty edge set for the MST
3. For each edge (in sorted order):
   a. If adding this edge does NOT create a cycle → add it to MST
   b. If adding this edge creates a cycle → skip it
4. Stop when MST has (V - 1) edges, where V = number of vertices
```

### Why It Works

Kruskal's is a greedy algorithm. By always picking the smallest available edge that doesn't create a cycle, it guarantees the optimal (minimum weight) spanning tree. The "no cycle" check is typically done using a **Union-Find** (Disjoint Set Union) data structure, though this demo doesn't implement that — it simply pre-scripts which edges to add.

---

## The Graph Used in the Demo

### Tutorial Graph (Steps -1 through 5)

A graph with **6 vertices** (A, B, C, D, E, F) and **10 edges**:

```
        B ——(1)—— C
       /|╲        |╲
     (3) (5)  (4) (4) (6)
     /   |    ╲   |    ╲
    A   (4)    F  (6)    D
     ╲   |    /        /
     (6) (2) (5)    (8)
       ╲ | /        /
        E ————————
```

| Edge | Weight | Index in `a[]` array |
|------|--------|---------------------|
| A—B  | 3      | `a[0]`              |
| A—F  | 5      | `a[1]`              |
| A—E  | 6      | `a[2]`              |
| B—C  | 1      | `a[3]`              |
| B—F  | 4      | `a[4]`              |
| C—F  | 4      | `a[5]`              |
| C—D  | 6      | `a[6]`              |
| D—F  | 5      | `a[7]`              |
| E—D  | 8      | `a[8]`              |
| E—F  | 2      | `a[9]`              |

### Quiz Graph (Steps 7-8)

A different graph with **5 vertices** (A, B, C, D, E — no F) and **7 edges**:

| Edge | Weight |
|------|--------|
| B—C  | 1      |
| A—F  | 6      |
| D—F  | 2      |
| A—B  | 5      |
| B—F  | 4      |
| C—F  | 4      |
| C—D  | 3      |

---

## Step-by-Step Demo Flow

The entire demo is driven by a `step` variable, starting at `-3`. The user advances with the **Right Arrow** key (C version) or **Right Arrow / Mouse click** (Java version).

### Step -3: Title Screen 1

**What's shown:**
- Grey background with `logo.png` (the project logo/banner)
- Two decorative horizontal lines with orange accent dots
- Text: *"Devised by some guy named Kruskal, it's one of the most efficient methods to find out the minimum spanning tree of a given graph. Don't worry if nothing of this gets in your head right now"*

**Purpose:** Introduce Kruskal's algorithm concept casually.

### Step -2: Title Screen 2

**What's shown:**
- Same grey background + logo
- Decorative lines (now full-width, showing a "progress" effect)
- Text: *"Let's say there's a salesman who has to traverse a given number of cities and the cost of traversing from a city to other is given. So this algorithm helps him to find the cheapest way to do that"*

**Purpose:** Frame the problem as a real-world scenario (travelling salesman cost minimisation).

### Step -1: Graph Introduction

**What's shown:**
- Nature background (`nature.jpg`) fills the entire window
- **Upper graph**: The complete problem graph with all 6 vertices (A–F) drawn as purple circles, all 10 edges as red lines, and edge weights as purple numbers
- **Lower graph**: An empty solution graph — just 6 green circles (same positions, shifted down by 650 pixels) with red vertex labels, waiting to receive MST edges
- Text: *"Let's consider the problem as graph — Labeling the nodes, weights of the edges and the solution graph"*

**Purpose:** Present the full problem graph and the empty MST graph that will be built up.

### Step 0: Pick Edge BC (Weight 1)

**What happens on Right Arrow press:**
- `a[3]` is set to `650` (before rendering step 0)
- This causes edge B—C and its weight label "1" to shift down 650px in the Y axis — effectively moving the edge from the top graph to the bottom (MST) graph

**What's shown:**
- The salesman image (`sales1.jpg`) appears in the lower-left
- Text: *"According to the algorithm we have to choose the minimum weighted edge every time and check for edges NOT making cycles i.e., edge BC"*
- Edge B—C is now visible in the lower MST graph

### Step 1: Pick Edge EF (Weight 2)

- `a[9]` is set to `650` → Edge E—F shifts down
- Text: *"Now the next least weighted edge is FE, doesn't make a cycle either"*

### Step 2: Pick Edge AB (Weight 3)

- `a[0]` is set to `650` → Edge A—B shifts down
- Text: *"Now the next least weighted edge is AB, doesn't make a cycle"*

### Step 3: Pick Edge BF (Weight 4)

- `a[4]` is set to `650` → Edge B—F shifts down
- Text: *"Now the next least weighted edge is BF, doesn't make a cycle"*

### Step 4: Pick Edge DF (Weight 5)

- `a[7]` is set to `650` → Edge D—F shifts down
- Text: *"Now the next least weighted edge is FD, doesn't make a cycle. Could've chosen CF but that makes a cycle viz., B → C → F"*
- This is the crucial teaching moment — explaining why CF (also weight 4) was skipped

### Step 5: MST Complete

**What's shown:**
- The salesman celebration image changes to `sales2.jpg` (upper-right)
- Text: *"Now the graph below is the one of the optimal minimum spanning tree for the given graph, saving money for the salesmen"*
- The lower graph now shows the complete MST: edges BC, EF, AB, BF, DF

**MST Total Weight:** 1 + 2 + 3 + 4 + 5 = **15**

### Step 6: Quiz Prompt

- The `a[]` array is reset to all zeros (clearing the MST)
- A different quiz graph image (`test.jpg`) appears
- Text: *"How about a problem to test your understanding?"*
- Two buttons: **"Yeah Sure"** and **"No Thanks"** (the "No Thanks" button playfully runs away from the cursor in the Java version)

### Step 7: Interactive Quiz

**C Version:** The user clicks on edge midpoints to select edges for the MST. The graph shown is a 5-vertex quiz problem (A, B, C, D, E — no vertex F or its edge). Each click on a valid edge hitbox sets the corresponding `a[i] = 650`, visually moving that edge to the lower graph.

**Java Version:** The user **drags** edges downward from the upper graph to the lower graph using mouse drag events. The edge follows the cursor vertically.

Buttons:
- **"Check"** → Evaluates the answer (advances to step 8)
- **"Reset"** → Clears all selections and resets

### Step 8: Quiz Result

- Shows `right.png` (checkmark) or `wrong.png` (X mark) based on the answer
- **Correct answer**: Exactly 4 edges selected, and all 4 are the correct MST edges
- **Wrong answer conditions**:
  - Fewer than 4 edges → not a spanning tree
  - More edges selected than correct → includes wrong edges / cycles
  - 4 edges but wrong ones → cycles detected
- "Reset" button available to retry

---

## The Animation Trick (C Version)

This is the most clever part of the codebase. There's no actual animation library or timeline system. The "animation" of edges moving from the upper graph to the lower graph is achieved through a single integer array and coordinate arithmetic.

### How It Works

```c
int a[10] = {0};  // one element per edge
```

Each edge is drawn using the `draw_edges()` function with Y-coordinates offset by `a[i]`:

```c
draw_edges(start_pos[0][0], end_pos[0] - a[0], start_pos[1][0], end_pos[1] - a[0]);  // AB
```

- When `a[0] = 0`: Edge AB is drawn at its original Y position (upper graph)
- When `a[0] = 650`: Edge AB is drawn 650 pixels lower (lower graph position)

The value `650` is chosen because that's the vertical distance between the upper graph and the lower MST graph in the 1000×1000 coordinate system.

The same offset applies to the edge weight labels:

```c
WriteText("3", pl[0][0], pl[0][1] - a[0]);  // Weight of AB
```

### Why 650?

The upper graph vertices are centered around Y=820. The lower graph vertices are at Y=170 (820 - 650). So shifting any edge down by 650 pixels moves it from the problem graph to the solution graph while keeping the same relative geometry. The lower graph's vertex positions are hardcoded as `start_pos[i][1] - w` where `w = 650`.

### The State Machine

```
Right Arrow pressed:
  step 0 → a[3] = 650  (B—C moves down)
  step 1 → a[9] = 650  (E—F moves down)
  step 2 → a[0] = 650  (A—B moves down)
  step 3 → a[4] = 650  (B—F moves down)
  step 4 → a[7] = 650  (D—F moves down)
  then step++ (advance to next step)

Left Arrow pressed (going back):
  step 1 → a[3] = 0   (undo B—C)
  step 2 → a[9] = 0   (undo E—F)
  step 3 → a[0] = 0   (undo A—B)
  step 4 → a[4] = 0   (undo B—F)
  step 5 → a[7] = 0   (undo D—F)
  then step-- (go back one step)
```

---

## The Edge-Drag Mechanism (Java Version)

The Java version uses a fundamentally different approach for the quiz (step 7): instead of clicking to select edges, the user **drags** edges from the upper graph to the lower graph.

### How Dragging Works

1. **`mouseDragged()`** fires continuously as the user drags
2. It checks if the drag started within a specific edge's hitbox region
3. If so, it sets `container` to that edge's index and updates the edge's Y-coordinates to follow the mouse:
   ```java
   start[container][1] = ((8 * (1000 - my)) / 5) - 800;
   end[container][1] = diff + start[container][1];
   ```
4. The coordinate transform `((8 * (1000 - my)) / 5) - 800` converts screen coordinates (origin top-left, Y increases downward) to OpenGL coordinates (origin center, Y increases upward, range -800 to +800)
5. On `mouseReleased()`, `container` is reset to `-1`, and the edge stays wherever it was dropped

### Edge and Vertex Data (Java)

The Java version uses a different data structure:
- `start[i][0], start[i][1]` → start point (x, y) of edge i in OpenGL coords
- `end[i][0], end[i][1]` → end point of edge i
- `newstart[i][], newend[i][]` → target positions (lower graph) for MST edges
- `tempstart[i][], tempend[i][]` → backup copies for reset

---

## The Quiz System

### Validation Logic (C Version)

```c
void check_answer()
{
    if(e != 4) accept = 0;        // Must have exactly 4 edges selected
    else
    {
        if(c == e) accept = 1;     // All selected edges are correct MST edges
        else accept = 0;           // Some selected edges are wrong
    }
}
```

Two counters:
- `e` → counts **total** edges selected
- `c` → counts **correct** MST edges selected

In the click handler, correct MST edges increment both `e++` and `c++`. Wrong edges (like AF, CF, CD) only increment `c++` (not `e++`). Wait — actually looking at the code more carefully:

```c
// Correct MST edges: e++ AND c++
if(hit DF) { e++; c++; a[7] = 650; }
if(hit BC) { e++; c++; a[3] = 650; }
if(hit BF) { e++; c++; a[4] = 650; }
if(hit AB) { e++; c++; a[0] = 650; }

// Wrong edges: only c++ (no e++)
if(hit AF) { c++; a[1] = 650; }
if(hit CF) { c++; a[5] = 650; }
if(hit CD) { c++; a[6] = 650; }
```

So `e` counts correct edges, `c` counts all edges. The check is: if `e == 4` and `c == e` (no wrong edges picked), answer is correct.

### Validation Logic (Java Version)

The Java version has more nuanced feedback:
- `e == 0` → *"The tree isn't complete"*
- `e == 1` → *"This isn't the optimal solution"*
- `e == 5` → Too many edges, wrong
- `e == 4, c != e` → *"There is a cycle"* or *"There are cycles"*
- `e == 4, c == e` → *"Correct!!"*

---

## Rendering Pipeline

### C Version — Single-Buffered Immediate Mode OpenGL

```
main()
  ├─ glutInit, glutCreateWindow
  ├─ initial_mode()           → sets projection (gluOrtho2D 0–1000)
  ├─ glutDisplayFunc(renderer)
  ├─ glutSpecialFunc(typeEvent)  → keyboard handler (arrow keys)
  ├─ glutMouseFunc(clickEvent)   → mouse handler (quiz)
  └─ glutMainLoop()

renderer() [called on every redisplay]
  ├─ glClear
  ├─ if(step == -3) → draw title screen 1 (logo + text + decorative lines)
  ├─ if(step == -2) → draw title screen 2
  ├─ if(step == -1) → draw nature background + text + Graph()
  ├─ if(step == 0..4) → draw background + salesman image + text + Graph()
  ├─ if(step == 5) → draw background + celebration image + text + Graph()
  ├─ if(step == 6) → draw background + quiz prompt + buttons
  ├─ if(step == 7) → draw background + quiz graph + Graph() + buttons
  ├─ if(step == 8) → draw background + quiz graph + result image
  └─ glFlush

Graph() [draws the problem and solution graphs]
  ├─ if(step <= 5):
  │   ├─ Draw all 10 edges (Y-offset by a[i] for each)
  │   ├─ Draw 6 purple circles (upper graph vertices)
  │   ├─ Draw 6 green circles (lower graph vertices, Y - 650)
  │   ├─ Draw vertex labels (A–F) in yellow (upper) and red (lower)
  │   └─ Draw edge weight labels (offset by a[i])
  └─ else (step >= 7, quiz mode):
      ├─ Draw 7 edges (different graph, no AE/ED/EF)
      ├─ Draw 5 purple circles (no vertex E)
      ├─ Draw 5 green circles (lower)
      ├─ Draw vertex labels
      └─ Draw edge weight labels
```

### Java Version — Dual Rendering System

The Java version uniquely uses **two rendering systems simultaneously**:

1. **OpenGL (via JOGL GLCanvas)**: Background texture, graph edges (lines), graph vertices (circles)
2. **Java AWT/Swing (`paint()` method)**: Text labels, images (`ImageIcon`), buttons (`fill3DRect`)

```
display(GLAutoDrawable)     ← OpenGL rendering
  ├─ glClear
  ├─ background()           → renders nature.jpg as fullscreen textured quad
  ├─ Graph()                → draws edges and circles via OpenGL
  │   └─ TextRenderer       → draws instruction text via JOGL text utility
  └─ glFlush

paint(Graphics)             ← AWT rendering (overlays on top of OpenGL)
  ├─ super.paint(g)         → triggers OpenGL display()
  ├─ Draw vertex labels (g.drawString)
  ├─ Draw edge weight labels
  ├─ Draw images (ImageIcon.paintIcon)
  └─ Draw buttons (g.fill3DRect + g.drawString)
```

---

## Data Structures & Variables

### C Version

| Variable | Type | Purpose |
|----------|------|---------|
| `step` | `int` | Current tutorial step (-3 to 8) |
| `a[10]` | `int[]` | Y-offset for each edge (0 or 650). This is the animation state |
| `start_pos[10][10]` | `double[][]` | Vertex positions `[vertex][x=0/y=1]` |
| `end_pos[10]` | `double[]` | Y-coordinates of vertices (copied from `start_pos[i][1]`) |
| `pl[10][10]` | `double[][]` | Edge weight label positions `[edge][x=0/y=1]` |
| `e` | `int` | Correct edges selected (quiz) |
| `c` | `int` | Total edges selected (quiz) |
| `accept` | `int` | Quiz result (1 = correct, 0 = wrong) |
| `x11, y11` | `int` | Position of "No Thanks" button (step 6) |
| `TEXTURE_DIR` | `const char*` | Path prefix for texture files |

### Java Version

| Variable | Type | Purpose |
|----------|------|---------|
| `step` | `int` | Current step (-3 to 8) |
| `start[10][2]` | `double[][]` | Edge start points (x, y) in GL coords |
| `end[10][2]` | `double[][]` | Edge end points |
| `newstart/newend` | `double[][]` | Target positions for edges in the lower MST graph |
| `tempstart/tempend` | `double[][]` | Backup copies for reset |
| `edges[10][2]` | `int[][]` | Edge weight label positions in screen coords |
| `nedges[10][2]` | `int[][]` | Lower graph label positions |
| `container` | `int` | Index of edge currently being dragged (-1 = none) |
| `word` | `int` | Index of the weight label for dragged edge |
| `mouseDragged/Moved/Clicked/Released` | `boolean` | Mouse state flags |
| `visited[5]` | `boolean[]` | (Declared but unused in current code) |

---

## Differences Between C and Java Implementations

| Aspect | C Version | Java Version |
|--------|-----------|-------------|
| **Coordinate system** | 0–1000 (bottom-left origin) | -800 to +800 (center origin) |
| **Background rendering** | Texture loaded and drawn per frame via `ImageLoader()` | Texture cached in `init()`, drawn via OpenGL quad |
| **Text rendering** | `glutBitmapCharacter` (bitmap font, OpenGL) | `TextRenderer` (JOGL) + `g.drawString` (AWT) |
| **Image display** | OpenGL textured quads (`ImageLoader()`) | `ImageIcon.paintIcon()` (Swing, painted over GL) |
| **MST animation** | Y-offset trick (`a[i] = 650`) shifts edges down | Edge start/end coordinates are replaced with `newstart/newend` values |
| **Quiz interaction** | Click on edge midpoints | Drag edges with mouse |
| **Quiz graph** | Same 6-vertex graph but different edge subset drawn | 5-vertex graph (vertex E repositioned, vertex F removed) |
| **"No Thanks" button** | Stationary, toggles between 3 positions on click | Follows mouse cursor (runs away from user!) |
| **Window toolkit** | GLUT (handles window, input, main loop) | AWT/Swing JFrame with GLCanvas |
| **Buffering** | Single-buffered (`GLUT_SINGLE`) | Double-buffered (JOGL default) |
| **Texture loading** | stb_image (macOS) / SOIL (Windows) | JOGL `TextureIO` |

---

## The Vertex Layout (Coordinate Deep-Dive)

### C Version (0–1000 coordinate space)

```
Vertex A: (254, 820)        — left side, upper
Vertex B: (379, 920)        — upper-left
Vertex C: (629, 920)        — upper-right
Vertex D: (754, 820)        — right side, upper
Vertex E: (504, 720)        — center, lower
Vertex F: (504, 820)        — center, middle

Lower graph = same X positions, Y - 650:
Vertex A: (254, 170)
Vertex B: (379, 270)
...etc
```

The layout forms a rough hexagonal shape with F at the center, A and D on the sides, B and C on top, and E below.

### Java Version (-800 to +800 coordinate space)

```
Upper graph:
  Nodes at: (-200,773), (200,773), (-350,620), (350,620), (0,620), (0,450)

Lower graph:
  Nodes at: (-200,-380), (200,-380), (-350,-550), (350,-550), (0,-550), (0,-720)
```

The Java version uses OpenGL coordinates centered at (0,0), with the upper graph in the positive Y region and the lower graph in the negative Y region.

---

## Summary

The project is a masterclass in making a complex concept accessible through graphics:

1. **Two intro screens** set the context (Kruskal + salesman framing)
2. **One setup screen** shows the problem graph and empty solution graph
3. **Five algorithm steps** walk through edge selection, with the animation trick making edges visually "move" to the MST
4. **One result screen** shows the complete MST
5. **An interactive quiz** lets the user practice on a different graph
6. **Immediate feedback** with right/wrong icons

The entire thing is driven by a single integer (`step`) and an array of 10 offsets (`a[]`) — elegant in its simplicity.
