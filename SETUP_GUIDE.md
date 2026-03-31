# Setup Guide: Kruskal Algorithm Graphics Tutorial

> A comprehensive, beginner-friendly guide to compiling, running, and distributing this project on **Windows** and **macOS**.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Repository Structure](#repository-structure)
3. [Windows Setup (C Version)](#windows-setup-c-version)
4. [Windows Setup (Java Version)](#windows-setup-java-version)
5. [macOS Setup (C Version)](#macos-setup-c-version)
6. [macOS Setup (Java Version)](#macos-setup-java-version)
7. [Making It Portable / Distributable](#making-it-portable--distributable)

---

## Project Overview

This project demonstrates **Kruskal's Minimum Spanning Tree Algorithm** through an interactive OpenGL-based tutorial. There are two independent implementations:

| Version | Language | Graphics Stack | Image Library |
|---------|----------|---------------|---------------|
| C | C (C99) | OpenGL + GLUT | SOIL (Windows) / stb_image (macOS) |
| Java | Java | JOGL + AWT/Swing | `TextureIO` (JOGL) + `ImageIcon` (Swing) |

Both versions produce an identical tutorial experience: intro screens, step-by-step MST construction, and an interactive quiz.

---

## Repository Structure

```
KruskalAlgorithmTutorial/
├── C/
│   ├── KruskalAlgorithmTutorial.c          # Original Windows source
│   ├── KruskalAlgorithmTutorial_macOS.c    # macOS-ported source
│   ├── stb_image.h                         # Single-header image loader (macOS)
│   ├── kruskal_tutorial                    # Compiled macOS binary
│   └── How_to_setup.md                     # Original Windows setup notes
├── Java/
│   ├── nbproject/Source/
│   │   └── KruskalAlgorithmTutorial.java   # Original Java source (JOGL 1.x)
│   ├── KruskalAlgorithmTutorial_macOS.java # macOS-ported source (JOGL 2.6.0)
│   ├── lib/
│   │   ├── jogl-all-2.6.0.jar
│   │   ├── gluegen-rt-2.6.0.jar
│   │   ├── jogl-all-2.6.0-natives-macosx-universal.jar
│   │   └── gluegen-rt-2.6.0-natives-macosx-universal.jar
│   └── build.xml                           # Original NetBeans build file
├── Textures/
│   ├── logo.png, nature.jpg, sales1.jpg, sales2.jpg
│   ├── test.jpg, mouseup2.jpg
│   ├── right.png, wrong.png
│   └── XI.png
└── README.md
```

---

## Windows Setup (C Version)

### Prerequisites

You need three things: a C compiler, an OpenGL/GLUT library, and the SOIL image library.

### Step 1: Install MinGW (C Compiler)

1. Download MinGW from [https://osdn.net/projects/mingw/](https://osdn.net/projects/mingw/) or use [MSYS2](https://www.msys2.org/) (recommended for modern systems).
2. During installation, select at minimum:
   - `mingw32-gcc-g++` (C/C++ compiler)
   - `mingw32-base` (base tools including `make`)
3. The default install location is `C:\MinGW`.
4. Add `C:\MinGW\bin` to your system `PATH` environment variable:
   - Right-click "This PC" → Properties → Advanced system settings → Environment Variables
   - Under "System variables", find `Path`, click Edit, and add `C:\MinGW\bin`
5. Verify: Open Command Prompt and type:
   ```cmd
   gcc --version
   ```
   You should see the GCC version info.

### Step 2: Install FreeGLUT

1. Download FreeGLUT for MinGW from [https://www.transmissionzero.co.uk/software/freeglut-devel/](https://www.transmissionzero.co.uk/software/freeglut-devel/) (get the MinGW package).
2. Unzip the archive. Inside you'll find `bin/`, `lib/`, and `include/` folders.
3. Copy the files to your MinGW installation:
   - Copy `bin/freeglut.dll` → `C:\Windows\System32\` (and `C:\Windows\SysWOW64\` on 64-bit)
   - Copy `lib/libfreeglut.a` and `lib/libfreeglut_static.a` → `C:\MinGW\lib\`
   - Copy `include/GL/*.h` (all header files) → `C:\MinGW\include\GL\`

### Step 3: Install SOIL (Simple OpenGL Image Library)

1. Download SOIL from [http://www.lonesock.net/soil.html](http://www.lonesock.net/soil.html).
2. Unzip. Copy `src/*` (all headers and the `original` folder) → `C:\MinGW\include\`
3. Copy `lib/libSOIL.a` → `C:\MinGW\lib\`

### Step 4: Compile and Run

Open Command Prompt, navigate to the project directory, and compile:

```cmd
cd KruskalAlgorithmTutorial
gcc -o C\kruskal_tutorial.exe C\KruskalAlgorithmTutorial.c -lfreeglut -lopengl32 -lglu32 -lSOIL
```

> **Important**: The original code expects texture files in a `Textures/` directory relative to where you run the binary. Always run from the repo root:

```cmd
C\kruskal_tutorial.exe
```

### Alternative: Code::Blocks IDE

If you prefer an IDE:

1. Install [Code::Blocks](https://www.codeblocks.org/downloads/) (the version **with MinGW bundled**).
2. Follow the linker setup described in `C/How_to_setup.md` — you add the `.a` files to linker settings and set the linker flags: `-lglu32 -lfreeglut -lfreeglut_static -lSOIL -lopengl32`
3. Create a GLUT project, add `KruskalAlgorithmTutorial.c`, and build.

---

## Windows Setup (Java Version)

### Prerequisites

- **JDK 8 or later** (JDK 8–17 recommended for simplicity; JDK 18+ requires extra module flags)
- **JOGL 1.x libraries** (for the original code) or **JOGL 2.x** (for modernised code)

### Step 1: Install JDK

1. Download from [https://adoptium.net/](https://adoptium.net/) (Temurin/Eclipse Adoptium) or Oracle.
2. Install, and ensure `java` and `javac` are on your `PATH`:
   ```cmd
   java -version
   javac -version
   ```

### Step 2: Get JOGL Libraries

The original code uses JOGL 1.x (`javax.media.opengl`). For Windows:

1. Download JOGL 1.1.1a from [https://jogamp.org/deployment/archive/](https://jogamp.org/deployment/archive/) or find the legacy jars.
2. You need: `jogl.jar`, `gluegen-rt.jar`, and the native DLLs for Windows (e.g., `jogl_awt.dll`, `jogl.dll`, `gluegen-rt.dll`).
3. Place the JARs in `Java/lib/` and the DLLs either:
   - In the same directory as your `.class` file, or
   - On your `java.library.path`

### Step 3: Compile and Run

```cmd
cd KruskalAlgorithmTutorial

rem Compile
javac -cp "Java\lib\jogl.jar;Java\lib\gluegen-rt.jar" Java\nbproject\Source\KruskalAlgorithmTutorial.java

rem Run (from repo root so Textures/ is found)
java -cp "Java\nbproject;Java\lib\jogl.jar;Java\lib\gluegen-rt.jar" -Djava.library.path="Java\lib" Source.File.KruskalAlgorithmTutorial
```

> **Note**: The original Java code has hardcoded Windows paths (`G:\Nikhil Raj\...`). You will need to update these paths in `KruskalAlgorithmTutorial.java` to point to your local `Textures/` directory.

---

## macOS Setup (C Version)

### Prerequisites

- **Xcode Command Line Tools** (provides `clang`, the C compiler)
- macOS comes with OpenGL and GLUT frameworks pre-installed (deprecated but functional)
- No external libraries needed — we use `stb_image.h` (already included in the repo) instead of SOIL

### Step 1: Install Xcode Command Line Tools

Open Terminal and run:

```bash
xcode-select --install
```

Click "Install" in the dialog that appears. Verify:

```bash
clang --version
```

### Step 2: Download stb_image.h (if not present)

The repo already includes `C/stb_image.h`. If for some reason it's missing:

```bash
curl -o C/stb_image.h https://raw.githubusercontent.com/nothings/stb/master/stb_image.h
```

### Step 3: Update the Texture Path

The macOS port uses an absolute path in `KruskalAlgorithmTutorial_macOS.c`. Open the file and update this line to match your local clone location:

```c
static const char *TEXTURE_DIR = "/your/path/to/KruskalAlgorithmTutorial/Textures/";
```

### Step 4: Compile

```bash
cd KruskalAlgorithmTutorial

clang -o C/kruskal_tutorial \
      C/KruskalAlgorithmTutorial_macOS.c \
      -framework OpenGL \
      -framework GLUT \
      -lm \
      -Wno-deprecated-declarations
```

The `-Wno-deprecated-declarations` flag silences macOS's OpenGL deprecation warnings (OpenGL is deprecated on macOS since 10.14 but still fully functional).

### Step 5: Run

```bash
./C/kruskal_tutorial
```

**Controls:**
- **Right Arrow** → Advance to next step
- **Left Arrow** → Go back one step
- **Mouse click** → Interact with quiz (steps 6–8)

---

## macOS Setup (Java Version)

### Prerequisites

- **JDK 17 or later** (JDK 24 tested and working)
- **JOGL 2.6.0** with Apple Silicon (arm64) native libraries (already included in `Java/lib/`)

### Step 1: Install JDK

Using Homebrew (recommended):

```bash
brew install openjdk
```

Or download from [https://adoptium.net/](https://adoptium.net/). Verify:

```bash
java -version
javac -version
```

### Step 2: Compile

```bash
cd KruskalAlgorithmTutorial

javac -cp "Java/lib/jogl-all-2.6.0.jar:Java/lib/gluegen-rt-2.6.0.jar" \
      Java/KruskalAlgorithmTutorial_macOS.java
```

### Step 3: Run

```bash
java --enable-native-access=ALL-UNNAMED \
     --add-opens java.desktop/sun.awt=ALL-UNNAMED \
     -cp "Java:Java/lib/jogl-all-2.6.0.jar:Java/lib/gluegen-rt-2.6.0.jar:Java/lib/jogl-all-2.6.0-natives-macosx-universal.jar:Java/lib/gluegen-rt-2.6.0-natives-macosx-universal.jar" \
     KruskalAlgorithmTutorial_macOS
```

> **JDK Flags Explained:**
> - `--enable-native-access=ALL-UNNAMED` — JOGL loads native `.dylib` files; JDK 22+ requires this flag
> - `--add-opens java.desktop/sun.awt=ALL-UNNAMED` — JOGL accesses internal Sun AWT classes for the GLCanvas

> **Do NOT use** `-XstartOnFirstThread` — that flag is only for JOGL's NEWT windowing toolkit. With AWT GLCanvas (which this project uses), it causes a deadlock.

**Controls:**
- **Right Arrow** → Advance to next step
- **Mouse drag** → Drag edges in quiz mode (step 7)
- **Click "Check"** → Verify your answer
- **Click "Reset"** → Start quiz over

---

## Making It Portable / Distributable

### C Version — Windows (.exe)

**Standalone .exe distribution:**

1. Compile as a static binary to avoid DLL dependencies:
   ```cmd
   gcc -static -o kruskal_tutorial.exe KruskalAlgorithmTutorial.c -lfreeglut_static -lopengl32 -lglu32 -lSOIL -lwinmm -lgdi32
   ```
2. Create a distribution folder:
   ```
   KruskalTutorial/
   ├── kruskal_tutorial.exe
   ├── freeglut.dll          (if not statically linked)
   └── Textures/
       ├── logo.png
       ├── nature.jpg
       ├── sales1.jpg
       ├── sales2.jpg
       ├── test.jpg
       ├── mouseup2.jpg
       ├── right.png
       ├── wrong.png
       └── XI.png
   ```
3. **Important**: Change the texture path in code to use relative paths (`"Textures/"`) and ensure the user runs the `.exe` from the folder containing `Textures/`.
4. Zip the folder and share — recipient just unzips and double-clicks the `.exe`.

**Creating an installer (optional):**
- Use [Inno Setup](https://jrsoftware.org/isinfo.php) or [NSIS](https://nsis.sourceforge.io/) to create a proper Windows installer that places the exe + textures in Program Files and creates a Start Menu shortcut.

### C Version — macOS (.app / .dmg)

**Creating a .app bundle:**

macOS applications are distributed as `.app` bundles, which are just specially structured folders:

```bash
# Create the .app structure
mkdir -p KruskalTutorial.app/Contents/MacOS
mkdir -p KruskalTutorial.app/Contents/Resources/Textures

# Copy the binary
cp C/kruskal_tutorial KruskalTutorial.app/Contents/MacOS/

# Copy textures
cp Textures/* KruskalTutorial.app/Contents/Resources/Textures/

# Create Info.plist
cat > KruskalTutorial.app/Contents/Info.plist << 'PLIST'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>kruskal_tutorial</string>
    <key>CFBundleIdentifier</key>
    <string>com.kruskal.tutorial</string>
    <key>CFBundleName</key>
    <string>Kruskal Algorithm Tutorial</string>
    <key>CFBundleVersion</key>
    <string>1.0</string>
    <key>CFBundlePackageType</key>
    <string>APPL</string>
</dict>
</plist>
PLIST
```

> **Caveat**: To use a `.app` bundle, you must change the texture path in the C code to resolve relative to the bundle's `Resources/` directory using `CFBundleCopyResourcesDirectoryURL()` from CoreFoundation, or switch to a relative path and set the working directory appropriately.

**Creating a .dmg:**

```bash
# Create a DMG from the .app
hdiutil create -volname "Kruskal Tutorial" \
               -srcfolder KruskalTutorial.app \
               -ov -format UDZO \
               KruskalTutorial.dmg
```

Recipients can open the `.dmg`, drag the app to Applications, and run it.

> **Gatekeeper Note**: Unless you sign the binary with an Apple Developer certificate (`codesign`) and notarize it (`xcrun notarytool`), recipients will see a "Cannot be opened because it is from an unidentified developer" warning. They can bypass it via: **Right-click → Open** (first time only) or via **System Settings → Privacy & Security → Open Anyway**.

### Java Version — Cross-Platform JAR

Java is inherently portable. You can create a single runnable JAR:

1. **Create a manifest file** (`MANIFEST.MF`):
   ```
   Main-Class: KruskalAlgorithmTutorial_macOS
   Class-Path: lib/jogl-all-2.6.0.jar lib/gluegen-rt-2.6.0.jar
   ```

2. **Package into a JAR**:
   ```bash
   jar cfm KruskalTutorial.jar MANIFEST.MF \
       -C Java/ KruskalAlgorithmTutorial_macOS.class
   ```

3. **Distribution folder**:
   ```
   KruskalTutorial/
   ├── KruskalTutorial.jar
   ├── lib/
   │   ├── jogl-all-2.6.0.jar
   │   ├── gluegen-rt-2.6.0.jar
   │   ├── jogl-all-2.6.0-natives-macosx-universal.jar (macOS)
   │   ├── jogl-all-2.6.0-natives-windows-amd64.jar   (Windows)
   │   ├── gluegen-rt-2.6.0-natives-macosx-universal.jar
   │   └── gluegen-rt-2.6.0-natives-windows-amd64.jar
   └── Textures/
       └── (all texture files)
   ```

4. **Run** (with required JVM flags for JDK 17+):
   ```bash
   java --enable-native-access=ALL-UNNAMED \
        --add-opens java.desktop/sun.awt=ALL-UNNAMED \
        -jar KruskalTutorial.jar
   ```

> **Cross-platform tip**: JOGL provides native JARs for every platform. Include all of them (`natives-windows-amd64`, `natives-linux-amd64`, `natives-macosx-universal`) and JOGL will automatically extract the right one at runtime.

**Using jpackage (JDK 14+) for native installers:**

```bash
jpackage --input dist/ \
         --main-jar KruskalTutorial.jar \
         --main-class KruskalAlgorithmTutorial_macOS \
         --name "Kruskal Tutorial" \
         --type dmg \
         --java-options "--enable-native-access=ALL-UNNAMED" \
         --java-options "--add-opens java.desktop/sun.awt=ALL-UNNAMED"
```

This creates a native `.dmg` (macOS) or `.exe`/`.msi` (Windows) installer with a bundled JRE — the recipient doesn't even need Java installed.

---

## Quick Reference: Compile & Run Commands

| Platform | Version | Compile | Run |
|----------|---------|---------|-----|
| **macOS** | C | `clang -o C/kruskal_tutorial C/KruskalAlgorithmTutorial_macOS.c -framework OpenGL -framework GLUT -lm -Wno-deprecated-declarations` | `./C/kruskal_tutorial` |
| **macOS** | Java | `javac -cp "Java/lib/jogl-all-2.6.0.jar:Java/lib/gluegen-rt-2.6.0.jar" Java/KruskalAlgorithmTutorial_macOS.java` | `java --enable-native-access=ALL-UNNAMED --add-opens java.desktop/sun.awt=ALL-UNNAMED -cp "Java:Java/lib/*.jar" KruskalAlgorithmTutorial_macOS` |
| **Windows** | C | `gcc -o kruskal_tutorial.exe C/KruskalAlgorithmTutorial.c -lfreeglut -lopengl32 -lglu32 -lSOIL` | `kruskal_tutorial.exe` |
| **Windows** | Java | `javac -cp "Java\lib\jogl.jar;Java\lib\gluegen-rt.jar" Java\nbproject\Source\KruskalAlgorithmTutorial.java` | `java -cp "Java\nbproject;Java\lib\*" -Djava.library.path="Java\lib" Source.File.KruskalAlgorithmTutorial` |

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `can't fopen` errors for textures (C) | You're running from the wrong directory. Run from the repo root, or update `TEXTURE_DIR` in the source |
| `GL_SILENCE_DEPRECATION` warnings (macOS C) | Already suppressed in the macOS port. If you see them, ensure you're compiling `_macOS.c` with `-Wno-deprecated-declarations` |
| `UnsatisfiedLinkError` / missing native library (Java) | Ensure JOGL native JARs are on the classpath. On macOS, use JOGL 2.6.0 (arm64 support) |
| `-XstartOnFirstThread` deadlock (Java macOS) | Do NOT use this flag with AWT GLCanvas. It's only for JOGL NEWT |
| `module java.desktop does not export sun.awt` (Java JDK 17+) | Add `--add-opens java.desktop/sun.awt=ALL-UNNAMED` to the `java` command |
| Black/grey window with no content (Java macOS) | Ensure `requestFocusInWindow()` is called after `setVisible(true)` |
| RGB stripe artifacts on textures (C macOS) | Already fixed with `glPixelStorei(GL_UNPACK_ALIGNMENT, 1)` in the macOS port |
