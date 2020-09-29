import engine.AbstractGame;
import engine.GameContainer;
import engine.gfx.Renderer;
import engine.gfx.images.Image;
import engine.gfx.images.ImageTile;
import engine3d.*;
import engine3d.matrix.Mat4x4;
import engine3d.matrix.MatrixMath;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Esta clase es un programa para testear y depurar el funcionamiento de las clases
 * del motor 3D.
 * Objetivo: Conseguir renderizar texturas.
 *
 * @class Test3DEngine.
 * @autor Sergio Martí Torregrosa. sMartiTo
 * @version 0.0.01 pre-alpha.
 * @date 2020-07-28
 */
public class Test3DEngine extends AbstractGame {

    private final double SECONDS = 1000000000.0;

    /**
     * We need this for the offsetScaleTriangle method.
     * How is a susceptible point to increase the computational
     * cost, we define this constant here
     */
    private final Vec4df OFFSET_VIEW = new Vec4df(1.0f, 1.0f, 0.0f, 0.0f);

    /**
     * We need this for the triangleClipAgainstPlane
     */
    private final Vec4df PLANE_POINT = new Vec4df(0.0f, 0.0f, 0.1f);

    /**
     * Same as the planePoint
     */
    private final Vec4df PLANE_NORMAL = new Vec4df(0.0f, 0.0f, 1.0f);

    /**
     * the texture. Would it be better if this will be inside the Mesh object?
     */
    private Image texture;

    private ImageTile imageTile;

    private Mat4x4 matProjection = new Mat4x4();

    private Mat4x4 matView;

    private Camera cameraObj;

    private Mesh mesh = new Mesh();

    private Vec4df lightDirection;

    private Vec4df cubeTranslation = new Vec4df();

    private Vec4df cubeRotation = new Vec4df(0.0f, 0.0f, 0.0f);

    private RenderFlags renderFlag = RenderFlags.RENDER_TEXTURED;

    private boolean isShowingInformation = true;

    private boolean isPerspectiveProjection = true;

    private boolean isShowingElapsedTime = false;

    private boolean isShowingCameraInformation = true;

    private boolean isShowingTriangleInformation = false;

    private int numTrianglesDrawn = 0;

    private double elapsedTimeProjection = 0;

    private double elapsedTimeSort = 0;

    private double elapsedTimeRasterizeTrianglesAgainstCamera = 0;

    private double elapsedTimeRasterizeOneTriangle = 0;

    private double elapsedTimeClippingAlgorithm = 0;

    private double elapsedTimeIsTriangleInside = 0;

    private double elapsedTimeRasterize = 0;

    private double elapsedTimeRenderOneTriangle = 0;

    private double elapsedTimeRenderAllTriangles = 0;

    private int count = 0;

    private float[] depthBuffer;

    /**
     * This ArrayList contains all the texts which are going to be
     * drawn on the screen
     */
    private ArrayList<String> texts = new ArrayList<>();

    /**
     * Constructor de la clase.
     * @param title el título de la aplicación.
     */
    private Test3DEngine(String title) {
        super(title);
    }


    /**
     * Este método crea un cubo unitario. Siempre útil para testear motores 3D.
     * @return un cubo 3D unitario.
     */
    private Mesh initializationCube() {
        Mesh cube = new Mesh();

        ArrayList<Triangle> tris = new ArrayList<>();
        // SOUTH
        tris.add(new Triangle(new Vec4df[]{new Vec4df(0.0f, 0.0f, 0.0f), new Vec4df(0.0f, 1.0f, 0.0f), new Vec4df(1.0f, 1.0f, 0.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(0.0f, 0.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f)}));
        tris.add(new Triangle(new Vec4df[]{new Vec4df(0.0f, 0.0f, 0.0f), new Vec4df(1.0f, 1.0f, 0.0f), new Vec4df(1.0f, 0.0f, 0.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f), new Vec3df(1.0f, 1.0f, 1.0f)}));
        // EAST
        tris.add(new Triangle(new Vec4df[]{new Vec4df(1.0f, 0.0f, 0.0f), new Vec4df(1.0f, 1.0f, 0.0f), new Vec4df(1.0f, 1.0f, 1.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(0.0f, 0.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f)}));
        tris.add(new Triangle(new Vec4df[]{new Vec4df(1.0f, 0.0f, 0.0f), new Vec4df(1.0f, 1.0f, 1.0f), new Vec4df(1.0f, 0.0f, 1.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f), new Vec3df(1.0f, 1.0f, 1.0f)}));
        // NORTH
        tris.add(new Triangle(new Vec4df[]{new Vec4df(1.0f, 0.0f, 1.0f), new Vec4df(1.0f, 1.0f, 1.0f), new Vec4df(0.0f, 1.0f, 1.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(0.0f, 0.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f)}));
        tris.add(new Triangle(new Vec4df[]{new Vec4df(1.0f, 0.0f, 1.0f), new Vec4df(0.0f, 1.0f, 1.0f), new Vec4df(0.0f, 0.0f, 1.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f), new Vec3df(1.0f, 1.0f, 1.0f)}));
        // WEST
        tris.add(new Triangle(new Vec4df[]{new Vec4df(0.0f, 0.0f, 1.0f), new Vec4df(0.0f, 1.0f, 1.0f), new Vec4df(0.0f, 1.0f, 0.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(0.0f, 0.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f)}));
        tris.add(new Triangle(new Vec4df[]{new Vec4df(0.0f, 0.0f, 1.0f), new Vec4df(0.0f, 1.0f, 0.0f), new Vec4df(0.0f, 0.0f, 0.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f), new Vec3df(1.0f, 1.0f, 1.0f)}));
        // TOP
        tris.add(new Triangle(new Vec4df[]{new Vec4df(0.0f, 1.0f, 0.0f), new Vec4df(0.0f, 1.0f, 1.0f), new Vec4df(1.0f, 1.0f, 1.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(0.0f, 0.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f)}));
        tris.add(new Triangle(new Vec4df[]{new Vec4df(0.0f, 1.0f, 0.0f), new Vec4df(1.0f, 1.0f, 1.0f), new Vec4df(1.0f, 1.0f, 0.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f), new Vec3df(1.0f, 1.0f, 1.0f)}));
        // BOTTOM
        tris.add(new Triangle(new Vec4df[]{new Vec4df(1.0f, 0.0f, 1.0f), new Vec4df(0.0f, 0.0f, 1.0f), new Vec4df(0.0f, 0.0f, 0.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(0.0f, 0.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f)}));
        tris.add(new Triangle(new Vec4df[]{new Vec4df(1.0f, 0.0f, 1.0f), new Vec4df(0.0f, 0.0f, 0.0f), new Vec4df(1.0f, 0.0f, 0.0f)}, new Vec3df[]{new Vec3df(0.0f, 1.0f, 1.0f), new Vec3df(1.0f, 0.0f, 1.0f), new Vec3df(1.0f, 1.0f, 1.0f)}));

        cube.setTris(tris);

        return cube;
    }

    /**
     * Este método construye la matriz de proyección.
     * @param gc el objeto GameContainer que contiene información necesaria como el tamaño de la
     *           pantalla, necesario para calcular el aspectRatio.
     */
    private Mat4x4 buildProjectionMatrix(GameContainer gc) {
        float near = 0.1f;
        float far = 1000.0f;
        float fovDegrees = 90.0f;
        float aspectRatio = (float)gc.getHeight() / (float)gc.getWidth();
        return MatrixMath.matrixMakeProjection(fovDegrees, aspectRatio, near, far);
    }

    /**
     * Este método construye y asigna las matrices correspondientes a la cámara y
     * a la matriz de visión.
     */
    private void buildCameraMatrices() {
        cameraObj = new Camera();
        cameraObj.setOrigin(new Vec4df(0.533f, 0.800f, -0.684f));
        matView = cameraObj.getMatView();
    }

    @Override
    public void initialize(GameContainer gc) {
        depthBuffer = new float[gc.getWidth() * gc.getHeight()];

        if ( !mesh.loadFromObjectFile("resources/mountains_texture.obj", true) ) {
            mesh = initializationCube();
        }

        imageTile = new ImageTile("/dungeon_tiles/dg_dungeon32.gif", 32, 32);
        texture = new Image("/test3.png");

        matProjection = buildProjectionMatrix(gc);
        buildCameraMatrices();

        lightDirection = new Vec4df(0.0f, 0.0f, -1.0f);
        lightDirection = MatrixMath.vectorNormalise(lightDirection);
    }


    /**
     * This method manages the user input for camera translation, or panning.
     * @param gc the GameContainer object with te Input object needed to manage user inputs
     * @param dt the elapsed time between frames
     */
    private void updateCameraPanning(GameContainer gc, float dt) {
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_RIGHT) ) {
            cameraObj.getOrigin().addToX(8.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_LEFT) ) {
            cameraObj.getOrigin().addToX(-8.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_UP) ) {
            cameraObj.getOrigin().addToY(8.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_DOWN) ) {
            cameraObj.getOrigin().addToY(-8.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_Z) ) {
            cameraObj.getOrigin().addToZ(8.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_X) ) {
            cameraObj.getOrigin().addToZ(-8.0f * dt);
        }
    }

    /**
     * This method manages the user input for camera rotation
     * @param gc the GameContainer object with te Input object needed to manage user inputs
     * @param dt the elapsed time between frames
     */
    private void updateCameraRotation(GameContainer gc, float dt) {
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_W) ) {
            cameraObj.rotX(-2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_S) ) {
            cameraObj.rotX(2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_A) ) {
            cameraObj.rotY(-2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_D) ) {
            cameraObj.rotY(2.0f * dt);
        }
    }

    /**
     * En este método se gestiona las entradas de usuario para manejar la posición y
     * rotación de la cámara.
     * @param gc el objeto GameContainer para acceder al objeto Input.
     * @param dt el tiempo transcurrido entre actualización y aztualización.
     */
    private void updateCamera(GameContainer gc, float dt) {
        // Camera panning
        updateCameraPanning(gc, dt);

        // Camera Rotation
        updateCameraRotation(gc, dt);

        // Camera Zooming
        Vec4df forward = MatrixMath.vectorMul(cameraObj.getLookDirection(), - gc.getInput().getScroll() * 0.5f * dt);
        cameraObj.setOrigin(MatrixMath.vectorAdd(cameraObj.getOrigin(), forward));

        matView = cameraObj.getMatView();
    }

    /**
     * En este método se gestiona las entradas de usuario para manejar la rotación
     * del cubo.
     * @param gc el objeto GameContainer para acceder al objeto Input.
     */
    private void updateCube(GameContainer gc) {
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD8) ) {
            cubeRotation.setX(0.02f);
        } else {
            cubeRotation.setX(0.0f);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD2) ) {
            cubeRotation.setX(-0.02f);
        } else {
            cubeRotation.setX(0.0f);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD4) ) {
            cubeRotation.setY(0.02f);
        } else {
            cubeRotation.setY(0.0f);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD6) ) {
            cubeRotation.setY(-0.02f);
        } else {
            cubeRotation.setY(0.0f);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD7) ) {
            cubeRotation.setZ(0.02f);
        } else {
            cubeRotation.setZ(0.0f);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD3) ) {
            cubeRotation.setZ(-0.02f);
        } else {
            cubeRotation.setZ(0.0f);
        }
    }

    /**
     * Este método sirve para transformar los triangulos que forman el la malla (el cubo).
     */
    private void transformCube() {
        Mat4x4 matIdentity = MatrixMath.matrixMakeIdentity();
        Mat4x4 matRotX = MatrixMath.matrixMakeRotationX(cubeRotation.getX());
        matRotX = MatrixMath.matrixMultiplyMatrix(matIdentity, matRotX);
        Mat4x4 matRotY = MatrixMath.matrixMakeRotationY(cubeRotation.getY());
        Mat4x4 matRotXY = MatrixMath.matrixMultiplyMatrix(matRotY, matRotX);
        Mat4x4 matRotZ = MatrixMath.matrixMakeRotationZ(cubeRotation.getZ());
        Mat4x4 matRot = MatrixMath.matrixMultiplyMatrix(matRotXY, matRotZ);
        Mat4x4 matTranslation = MatrixMath.matrixMakeTranslation(cubeTranslation.getX(), cubeTranslation.getY(), cubeTranslation.getZ());
        Mat4x4 matRotTrans = MatrixMath.matrixMultiplyMatrix(matRot, matTranslation);
        for ( Triangle triangle : mesh.getTris() ) {
            triangle.setP(MatrixMath.matrixMultiplyVectors(matRotTrans, triangle.getP()));
        }
    }

    /**
     * This method manage the user inputs for showing information flags
     * @param gc the object GameContainer with the Input Object
     */
    private void updateInformationFlags(GameContainer gc) {
        if ( gc.getInput().isKeyDown(KeyEvent.VK_SPACE) ) {
            isShowingInformation = !isShowingInformation;
        }

        if ( gc.getInput().isKeyDown(KeyEvent.VK_E) ) {
            isShowingElapsedTime = !isShowingElapsedTime;
        }

        if ( gc.getInput().isKeyDown(KeyEvent.VK_C) ) {
            isShowingCameraInformation = !isShowingCameraInformation;
        }

        if ( gc.getInput().isKeyDown(KeyEvent.VK_V) ) {
            isShowingTriangleInformation = !isShowingTriangleInformation;
        }
    }

    /**
     * This method manage the user inputs for render flags
     * @param gc the object GameContainer with the Input Object
     */
    private void updateRenderFlags(GameContainer gc) {
        if ( gc.getInput().isKeyDown(KeyEvent.VK_H) ) {
            renderFlag = RenderFlags.RENDER_WIRE;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_J) ) {
            renderFlag = RenderFlags.RENDER_FLAT;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_K) ) {
            renderFlag = RenderFlags.RENDER_SMOOTH_FLAT;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_T) ) {
            renderFlag = RenderFlags.RENDER_TEXTURED;
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_Y) ) {
            renderFlag = RenderFlags.RENDER_FULL_TEXTURED;
        }
    }

    /**
     * This method updates the ArrayList texts with the
     * new information for the current frame
     */
    private void updateInformation(GameContainer gc) {
        texts.clear();

        texts.add("Mouse wheel: " + gc.getInput().getScroll());
        texts.add("Rendering as: " + renderFlag + " press H, J, K, T to change");
        texts.add("Mesh triangles: " + mesh.getTris().size());
        texts.add("Triangles drawn: " + numTrianglesDrawn);
        if ( isShowingElapsedTime ) {
            texts.add(String.format("Elapsed time to project triangles: %.6f", elapsedTimeProjection));
            texts.add(String.format("Elapsed time to rasterize one triangle against the camera: %.6f", elapsedTimeRasterizeTrianglesAgainstCamera));
            texts.add(String.format("Elapsed time to sort triangles: %.6f", elapsedTimeSort));
            texts.add(String.format("Elapsed time to know if one triangle is inside the screen: %.6f", elapsedTimeIsTriangleInside));
            texts.add(String.format("Elapsed time for clipping algorithm: %.6f", elapsedTimeClippingAlgorithm));
            texts.add(String.format("Elapsed time to rasterize one triangles: %.6f", elapsedTimeRasterizeOneTriangle));
            texts.add(String.format("Elapsed time to rasterize all triangles: %.6f", elapsedTimeRasterize));
            texts.add(String.format("%3.2f times rasterize all triangles", elapsedTimeRasterize / elapsedTimeRasterizeOneTriangle));
            texts.add(String.format("Elapsed time to render one triangle: %.6f", elapsedTimeRenderOneTriangle));
            texts.add(String.format("Elapsed time to render all triangles: %.6f", elapsedTimeRenderAllTriangles));
            texts.add("Counter: " + count);
            count = 0;
        }
    }

    /**
     * This method add to the texts what are going to be drawn in the screen,
     * the texture information of the triangles passed as a parameter.
     * @param triangles the triangles which information will be shown.
     */
    private void updateTrianglesInformation(ArrayList<Triangle> triangles) {
        int i = 0;
        for (Triangle triangle : triangles ) {
            String[] strings = {
                    "Triangle: " + i,
                    "w" + i + " value 0: " + triangle.getP()[0].getW() +
                    " value 1: " + triangle.getP()[1].getW() +
                    " value 2: " + triangle.getP()[2].getW(),
                    "t" + i + " " + triangle.getT()[0].toString(),
                    "t" + i + " " + triangle.getT()[1].toString(),
                    "t" + i + " " + triangle.getT()[2].toString(),
            };
            for ( String string : strings ) {
                if ( !texts.contains(string) ) {
                    texts.add(string);
                }
            }
            i++;
        }
    }

    /**
     * This method manage the input user for the projection flag and
     * updates when the flag changes the projection matrix.
     * @param gc the object GameContainer with the Input Object
     */
    private void updateProjectionMatrix(GameContainer gc) {
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD5) ) {
            isPerspectiveProjection = !isPerspectiveProjection;
            if ( isPerspectiveProjection ) {
                matProjection = buildProjectionMatrix(gc);
            } else {
                // todo cambiar el origen que alomejor no cambia.
                matProjection = MatrixMath.matrixMakeOrthogonalProjection(cameraObj.getOrigin().getZ());
            }
        }
    }

    /**
     * This method change the texture to render. The new texture is extracted from
     * the image tile
     * @param gc the GameContainer object.
     */
    private void updateTexture(GameContainer gc) {
        if ( gc.getInput().isKeyDown(KeyEvent.VK_1) ) {
            texture = imageTile.getTileImage(0, 0);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_2) ) {
            texture = imageTile.getTileImage(1, 0);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_3) ) {
            texture = imageTile.getTileImage(2, 0);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_4) ) {
            texture = imageTile.getTileImage(0, 3);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_5) ) {
            texture = imageTile.getTileImage(1, 3);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_6) ) {
            texture = imageTile.getTileImage(2, 3);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_7) ) {
            texture = imageTile.getTileImage(6, 5);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_8) ) {
            texture = imageTile.getTileImage(7, 5);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_9) ) {
            texture = imageTile.getTileImage(8, 5);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_0) ) {
            texture = imageTile.getTileImage(0, 6);
        }
    }

    @Override
    public void update(GameContainer gc, float dt) {
        updateInformationFlags(gc);

        updateRenderFlags(gc);

        updateCamera(gc, dt);

        updateCube(gc);

        updateProjectionMatrix(gc);

        transformCube();

        updateInformation(gc);

        updateTexture(gc);
    }


    /**
     * Este método es el responsable de calcular un color en base al parámetro "light".
     * @param light es la iluminación. Entre 0 para nada y 1 para máxima iluminación.
     * @return devuleve un color en base a la iluminación.
     */
    private int calculateColor(float light) {
        light = Math.max(0.1f, light);
        light *= 256;
        return (0xff << 24 | (int)light << 16 | (int)light << 8 | (int)light);
    }

    private int calculateColor(int inputColor, float light) {
        int a = ((inputColor >> 24) & 0xff);
        int r = ((inputColor >> 16) & 0xff);
        int g = ((inputColor >>  8) & 0xff);
        int b = ((inputColor      ) & 0xff);
        light = Math.max(0.1f, light);
        int newR = (int)(light * r);
        int newG = (int)(light * g);
        int newB = (int)(light * b);
        return (a << 24 | newR << 16 | newG << 8 | newB);
    }

    /**
     * El vector normal de un plano es un vector que es perpendicular a las dos direcciones
     * que forman el plano. Es decir, hace 90 grados con dos rectas.
     * @param triangle son los puntos que pertenecen a un plano. En nuestro caso, el plano es el triangulo.
     * @return devuleve la normal al plano formado por los puntos.
     */
    private Vec4df calculateNormalToPlane(Triangle triangle) {
        Vec4df normal = new Vec4df();

        Vec4df line1 = MatrixMath.vectorSub(triangle.getP()[1], triangle.getP()[0]);

        Vec4df line2 = MatrixMath.vectorSub(triangle.getP()[2], triangle.getP()[0]);

        normal.setX( line1.getY() * line2.getZ() - line1.getZ() * line2.getY() );
        normal.setY( line1.getZ() * line2.getX() - line1.getX() * line2.getZ() );
        normal.setZ( line1.getX() * line2.getY() - line1.getY() * line2.getX() );

        return MatrixMath.vectorNormalise(normal);
    }

    /**
     * This method corrects the texture
     * @param triangle the triangle to correct
     */
    private void textureCorrection(Triangle triangle) {
        for ( int i = 0; i < triangle.getT().length; i++ ) {
            triangle.getT()[i].setX(triangle.getT()[i].getX() / triangle.getP()[i].getW());
            triangle.getT()[i].setY(triangle.getT()[i].getY() / triangle.getP()[i].getW());
            triangle.getT()[i].setZ(1.0f / triangle.getP()[i].getW());
        }
    }

    /**
     * Esta función sirve para escalar y transladr los puntos proyectados a algo visible por pantalla.
     * @param triangle el triangulo proyectado en 2D.
     * @param width el ancho de la pantalla.
     * @param height el alto de la pantalla.
     */
    private void offSetProjectedTriangle(Triangle triangle, int width, int height) {
        for ( int i = 0; i < triangle.getP().length; i++ ) {
            triangle.getP()[i].set(MatrixMath.vectorDiv(triangle.getP()[i], triangle.getP()[i].getW()));
            triangle.getP()[i].multiplyXBy(-1.0f);
            triangle.getP()[i].multiplyYBy(-1.0f);
            triangle.getP()[i].set(MatrixMath.vectorAdd(triangle.getP()[i], OFFSET_VIEW));
            triangle.getP()[i].multiplyXBy(0.5f * width);
            triangle.getP()[i].multiplyYBy(0.5f * height);
        }
    }

    /**
     * Este método sirve para calcular los triangulos proyectados de un espacio 3D a 2D.
     * Se requiere del ancho y el alto de la pantalla para escalar y transladar el resultado en algo visible
     * por pantalla.
     * @param triangles los triangulos 3D a proyectar a 2D.
     * @param width el ancho de la pantalla.
     * @param height el alto de la pantalla.
     * @return un @ArrayList de triangulos proyectados en 2D.
     */
    private ArrayList<Triangle> projectTriangles(ArrayList<Triangle> triangles, int width, int height) {
        ArrayList<Triangle> trianglesProjected = new ArrayList<>();
        Triangle triangleViewed, triangleProjected;
        Vec4df normal, diffTrianglePointCameraOrigin;
        int color;
        for ( Triangle triangle : triangles ) {

            normal = calculateNormalToPlane(triangle);

            diffTrianglePointCameraOrigin = MatrixMath.vectorSub(triangle.getP()[0], cameraObj.getOrigin());

            if ( MatrixMath.vectorDotProduct(normal, diffTrianglePointCameraOrigin) < 0.0f ) {
                color = calculateColor(MatrixMath.vectorDotProduct(normal, lightDirection));

                triangleViewed = new Triangle(
                        MatrixMath.matrixMultiplyVectors(matView, triangle.getP()),
                        triangle.getT(),
                        color
                );

                double lastTime = System.nanoTime() / SECONDS;
                ArrayList<Triangle> clippedTriangles = MatrixMath.triangleClipAgainstPlane(
                        PLANE_POINT,
                        PLANE_NORMAL,
                        triangleViewed
                );
                elapsedTimeRasterizeTrianglesAgainstCamera = (System.nanoTime() / SECONDS) - lastTime;

                for ( Triangle triangleClipped : clippedTriangles ) {
                    triangleProjected = new Triangle(
                            MatrixMath.matrixMultiplyVectors(matProjection, triangleClipped.getP()),
                            triangleClipped.getT(),
                            triangleClipped.getColor(),
                            triangleClipped.getBrightness()
                    );

                    textureCorrection(triangleClipped);

                    offSetProjectedTriangle(triangleProjected, width, height);

                    trianglesProjected.add(triangleProjected);
                }
            }
        }

        numTrianglesDrawn = trianglesProjected.size();
        return trianglesProjected;
    }

    /**
     * This method calculates if one triangle is inside the screen or it isn't.
     * To do it, it goes over all the points which conform the triangle and if one
     * of them is outside the screen, returns false. Else, it returns true.
     * @param triangle the triangle to know if is inside the screen
     * @param width width screen
     * @param height height screen
     * @return return true if the triangle is inside the screen or false if one of its points is outside
     */
    private boolean isInsideScreen(Triangle triangle, int width, int height) {
        double lastTime = System.nanoTime() / SECONDS;
        for ( Vec4df point : triangle.getP() ) {
            if ( point.getX() > width || point.getX() < 0 || point.getY() > height || point.getY() < 0 ) {
                elapsedTimeIsTriangleInside = (System.nanoTime() / SECONDS) - lastTime;
                return false;
            }
        }
        elapsedTimeIsTriangleInside = (System.nanoTime() / SECONDS) - lastTime;
        return true;
    }

    /**
     * This method clips one triangle against the screen border specified as a parameter
     * @param triangle the triangle to clipp
     * @param border the border: 0 to top, 1 to bottom, 2 to left and 3 for right
     * @param width the width screen
     * @param height the height screen
     * @return return an ArrayList with the triangles clipped from the first triangle pass as a parameter
     */
    private ArrayList<Triangle> clipTriangleAgainstBorder(Triangle triangle, int border, int width, int height) {
        switch ( border ) {
            default:
            case 0:
                return MatrixMath.triangleClipAgainstPlane(
                        new Vec4df(0.0f, 0.0f, 0.0f),
                        new Vec4df(0.0f, 1.0f, 0.0f),
                        triangle
                );
            case 1:
                return MatrixMath.triangleClipAgainstPlane(
                        new Vec4df(0.0f, (float) height - 1, 0.0f),
                        new Vec4df(0.0f, -1.0f, 0.0f),
                        triangle
                );
            case 2:
                return MatrixMath.triangleClipAgainstPlane(
                        new Vec4df(0.0f, 0.0f, 0.0f),
                        new Vec4df(1.0f, 0.0f, 0.0f),
                        triangle
                );
            case 3:
                return MatrixMath.triangleClipAgainstPlane(
                        new Vec4df((float) width - 1, 0.0f, 0.0f),
                        new Vec4df(-1.0f, 0.0f, 0.0f),
                        triangle
                );
        }
    }

    /**
     * This method clips all the projected and sorted triangles to fit inside the screen.
     * Really important, reduce the cost of drawing giant triangles, absolutely necessary.
     * @param width width screen
     * @param height height screen
     * @param triangles the triangles that are going to be chopped
     * @return return a queue with all triangles clipped
     */
    private ArrayList<Triangle> rasterizeTriangles(ArrayList<Triangle> triangles, int width, int height) {
        ArrayList<Triangle> finalTriangles = new ArrayList<>();
        LinkedList<Triangle> trianglesOutsideScreenQueue = new LinkedList<>();
        Triangle triangleToTest;

        for ( Triangle triangle : triangles ) {
            double lastTime = System.nanoTime() / SECONDS;

            if ( isInsideScreen(triangle, width, height) ) {
                finalTriangles.add(triangle);
            } else {
                // todo aún así sigue siendo muy lento. habría que optimizarlo, pero entonces se tendría que cambiar el algoritmo entero
                trianglesOutsideScreenQueue.add(triangle);
                int numNewTriangles = trianglesOutsideScreenQueue.size();

                for ( int i = 0; i < 4; i++ ) {
                    while ( numNewTriangles > 0 ) {
                        triangleToTest = trianglesOutsideScreenQueue.remove();
                        numNewTriangles--;

                        if ( isInsideScreen(triangleToTest, width, height) ) {
                            finalTriangles.add(triangleToTest);
                        } else {
                            double lastTime1 = System.nanoTime() / SECONDS;
                            trianglesOutsideScreenQueue.addAll(clipTriangleAgainstBorder(triangleToTest, i, width, height));
                            elapsedTimeClippingAlgorithm = (System.nanoTime() / SECONDS) - lastTime1;
                            count++;
                        }

                    }
                    numNewTriangles = trianglesOutsideScreenQueue.size();
                }

                finalTriangles.addAll(trianglesOutsideScreenQueue);
                trianglesOutsideScreenQueue.clear();

            }

            elapsedTimeRasterizeOneTriangle = (System.nanoTime() / SECONDS) - lastTime;
        }

        numTrianglesDrawn = finalTriangles.size();

        return finalTriangles;
    }

    /**
     * This method clears all the depth buffer, and sets all the values to 0.
     * The size to fill is needed specified.
     * @param size the size of the depth buffer. In this case, always be the size of the screen.
     */
    private void clearDepthBuffer(int size) {
        for ( int i = 0; i < size; i++ ) {
            depthBuffer[i] = 0.0f;
        }
    }


    /**
     * Muestra información por pantalla, como la rueda del ratón, y la información de la cámara.
     * @param r el objeto Render con todas las funciones necesarias para dibujar.
     */
    private void showInformation(Renderer r) {
        int x = 10;
        int y = 10;

        for ( String text : texts ) {
            r.drawText(text, x, y, 0xffffffff);
            y += 30;
        }

        /*r.drawText("Texture sample: ", x, y, 0xffffffff);
        y += 30;
        r.drawImage(texture, x, y);
        y += (texture.getH());*/

        if ( isShowingCameraInformation ) {
            cameraObj.showInformation(r, x, y, 0xffffffff);
        }
    }

    /**
     * Este método dibuja en pantalla un triangulo relleno.
     * @param r el objeto render con todas las funciones de dibujado.
     * @param triangle el objeto triangulo a dibujar.
     */
    private void drawFlatTriangle(Renderer r, Triangle triangle) {
        r.drawFillTriangle(
                (int)triangle.getP()[0].getX(), (int)triangle.getP()[0].getY(),
                (int)triangle.getP()[1].getX(), (int)triangle.getP()[1].getY(),
                (int)triangle.getP()[2].getX(), (int)triangle.getP()[2].getY(),
                triangle.getColor()
        );
        r.drawTriangle(
                (int)triangle.getP()[0].getX(), (int)triangle.getP()[0].getY(),
                (int)triangle.getP()[1].getX(), (int)triangle.getP()[1].getY(),
                (int)triangle.getP()[2].getX(), (int)triangle.getP()[2].getY(),
                0xff000000
        );
    }

    /**
     * Este método dibuja un triangulo plano relleno, pero sin un borde negro.
     * @param r el objeto render con todas las funciones de dibujado.
     * @param triangle el objeto triangulo a dibujar.
     */
    private void drawFlatSmoothTriangle(Renderer r, Triangle triangle) {
        r.drawFillTriangle(
                (int)triangle.getP()[0].getX(), (int)triangle.getP()[0].getY(),
                (int)triangle.getP()[1].getX(), (int)triangle.getP()[1].getY(),
                (int)triangle.getP()[2].getX(), (int)triangle.getP()[2].getY(),
                triangle.getColor()
        );
    }

    /**
     * Este método dibuja en pantalla un triangulo, pero simplemente
     * las lineas que lo delimitan.
     * @param r el objeto render con todas las funciones de dibujado.
     * @param triangle el objeto triangulo a dibujar.
     */
    private void drawWireTriangle(Renderer r, Triangle triangle) {
        r.drawTriangle(
                (int)triangle.getP()[0].getX(), (int)triangle.getP()[0].getY(),
                (int)triangle.getP()[1].getX(), (int)triangle.getP()[1].getY(),
                (int)triangle.getP()[2].getX(), (int)triangle.getP()[2].getY(),
                triangle.getColor()
        );
    }

    /**
     * This method sets the correspondent pixel of the triangle to
     * the pixel from the texture bitmap.
     * @param gc GameContainer object, needed for write the depthBuffer
     * @param r the Renderer object
     * @param y the y value of the pixel
     * @param x the x value of the pixel
     * @param tex_u value x fot the texture
     * @param tex_v value y for the texture
     * @param tex_w value z for the texture, needed for perspective projection
     * @param texture the texture, which is a bitmap image
     */
    private void setPixelForTriangle(GameContainer gc, Renderer r,
                                     int y, int x,
                                     float tex_u, float tex_v, float tex_w,
                                     float brightness,
                                     Image texture) {
        int color;
        int sampleX = (int)((tex_u / tex_w) * texture.getW());
        int sampleY = (int)((tex_v / tex_w) * texture.getH());

        if ( sampleX >= texture.getW() ) {
            sampleX = texture.getW() -1;
        }

        if ( sampleY >= texture.getH() ) {
            sampleY = texture.getH() -1;
        }

        try {
            color = texture.getPixel(sampleX, sampleY);
            color = calculateColor(color, brightness);
        } catch ( ArrayIndexOutOfBoundsException e ) {
            color = 0x00000000;
            String errorMessage = "X: " + sampleX + " Y: " + sampleY + " outside of " + texture.getW() + "x" + texture.getH();
            System.out.println("Get sample Error: " + errorMessage + e.getMessage());
        }

        try {
            if (tex_w > depthBuffer[y * gc.getWidth() + x]) {
                r.setPixel(x, y, color);
                depthBuffer[y * gc.getWidth() + x] = tex_w;
            }
        } catch ( ArrayIndexOutOfBoundsException e ) {
            String errorMessage = "X: " + x + " Y: " + y + " outside of " + gc.getWidth() + "x" + gc.getHeight();
            System.out.println("Set pixel Error: " + errorMessage + e.getMessage());
        }

    }

    /**
     * This method draws a textured triangle. Code copied from Javidx9 video
     * Code-It-Yourself! 3D Graphics Engine Part #4 - Texturing & depth buffers.
     * https://www.youtube.com/watch?v=nBzCS-Y0FcY&t=1722s
     * @param r the render object with all drawing methods
     * @param x1 x value for point one of triangle
     * @param y1 y value for point one of triangle
     * @param u1 x value for texture point one of triangle
     * @param v1 y value for texture point one of triangle
     * @param x2 x value for point two of triangle
     * @param y2 y value for point two of triangle
     * @param u2 x value for texture point two of triangle
     * @param v2 y value for texture point two of triangle
     * @param x3 x value for point three of triangle
     * @param y3 y value for point three of triangle
     * @param u3 x value for texture point three triangle
     * @param v3 y value for texture point three triangle
     * @param texture the image texture
     */
    private void drawTexturedTriangle(GameContainer gc, Renderer r,
                                      int x1, int y1, float u1, float v1, float w1,
                                      int x2, int y2, float u2, float v2, float w2,
                                      int x3, int y3, float u3, float v3, float w3,
                                      float brightness,
                                      Image texture) {
        if (y2 < y1) {
            int tempInteger = y1;
            y1 = y2;
            y2 = tempInteger;

            tempInteger = x1;
            x1 = x2;
            x2 = tempInteger;

            float tempFloat = u1;
            u1 = u2;
            u2 = tempFloat;

            tempFloat = v1;
            v1 = v2;
            v2 = tempFloat;

            tempFloat = w1;
            w1 = w2;
            w2 = tempFloat;
        }

        if (y3 < y1) {
            int tempInteger = y1;
            y1 = y3;
            y3 = tempInteger;

            tempInteger = x1;
            x1 = x3;
            x3 = tempInteger;

            float tempFloat = u1;
            u1 = u3;
            u3 = tempFloat;

            tempFloat = v1;
            v1 = v3;
            v3 = tempFloat;

            tempFloat = w1;
            w1 = w3;
            w3 = tempFloat;
        }

        if (y3 < y2) {
            int tempInteger = y2;
            y2 = y3;
            y3 = tempInteger;

            tempInteger = x2;
            x2 = x3;
            x3 = tempInteger;

            float tempFloat = u2;
            u2 = u3;
            u3 = tempFloat;

            tempFloat = v2;
            v2 = v3;
            v3 = tempFloat;

            tempFloat = w2;
            w2 = w3;
            w3 = tempFloat;
        }

        int dy1 = y2 - y1;
        int dx1 = x2 - x1;
        float dv1 = v2 - v1;
        float du1 = u2 - u1;
        float dw1 = w2 - w1;

        int dy2 = y3 - y1;
        int dx2 = x3 - x1;
        float dv2 = v3 - v1;
        float du2 = u3 - u1;
        float dw2 = w3 - w1;

        float tex_u, tex_v, tex_w;

        float dax_step = 0, dbx_step = 0, du1_step = 0, dv1_step = 0, du2_step = 0, dv2_step = 0, dw1_step = 0, dw2_step = 0;

        if ( dy1 != 0 ) {
            dax_step = dx1 / (float)Math.abs(dy1);
        }
        if ( dy2 != 0 ) {
            dbx_step = dx2 / (float)Math.abs(dy2);
        }

        if ( dy1 != 0 ) {
            du1_step = du1 / (float)Math.abs(dy1);
        }
        if ( dy1 != 0 ) {
            dv1_step = dv1 / (float)Math.abs(dy1);
        }
        if ( dy1 != 0 ) {
            dw1_step = dw1 / (float)Math.abs(dy1);
        }

        if ( dy2 != 0 ) {
            du2_step = du2 / (float)Math.abs(dy2);
        }
        if ( dy2 != 0 ) {
            dv2_step = dv2 / (float)Math.abs(dy2);
        }
        if ( dy2 != 0 ) {
            dw2_step = dw2 / (float)Math.abs(dy2);
        }

        if ( dy1 != 0 ) {
            for ( int i = y1; i <= y2; i++ ) {
                int ax = (int)(x1 + (float)(i - y1) * dax_step);
                int bx = (int)(x1 + (float)(i - y1) * dbx_step);

                float tex_su = u1 + (float)(i - y1) * du1_step;
                float tex_sv = v1 + (float)(i - y1) * dv1_step;
                float tex_sw = w1 + (float)(i - y1) * dw1_step;

                float tex_eu = u1 + (float)(i - y1) * du2_step;
                float tex_ev = v1 + (float)(i - y1) * dv2_step;
                float tex_ew = w1 + (float)(i - y1) * dw2_step;

                if ( ax > bx ) {
                    int tempInteger = ax;
                    ax = bx;
                    bx = tempInteger;

                    float tempFloat = tex_su;
                    tex_su = tex_eu;
                    tex_eu = tempFloat;

                    tempFloat = tex_sv;
                    tex_sv = tex_ev;
                    tex_ev = tempFloat;

                    tempFloat = tex_sw;
                    tex_sw = tex_ew;
                    tex_ew = tempFloat;
                }

                //tex_u = tex_su;
                //tex_v = tex_sv;
                //tex_w = tex_sw;

                float tstep = 1.0f / ((float)(bx - ax));
                float t = 0.0f;

                for (int j = ax; j < bx; j++) {
                    tex_u = (1.0f - t) * tex_su + t * tex_eu;
                    tex_v = (1.0f - t) * tex_sv + t * tex_ev;
                    tex_w = (1.0f - t) * tex_sw + t * tex_ew;

                    setPixelForTriangle(gc, r, i, j, tex_u, tex_v, tex_w, brightness, texture);

                    t += tstep;
                }
            }
        }

        dy1 = y3 - y2;
        dx1 = x3 - x2;
        dv1 = v3 - v2;
        du1 = u3 - u2;
        dw1 = w3 - w2;

        if ( dy1 != 0 ) {
            dax_step = dx1 / (float)Math.abs(dy1);
        }
        if ( dy2 != 0 ) {
            dbx_step = dx2 / (float)Math.abs(dy2);
        }

        du1_step = 0;
        dv1_step = 0;
        if ( dy1 != 0 ) {
            du1_step = du1 / (float)Math.abs(dy1);
        }
        if ( dy1 != 0 ) {
            dv1_step = dv1 / (float)Math.abs(dy1);
        }
        if ( dy1 != 0 ) {
            dw1_step = dw1 / (float)Math.abs(dy1);
        }

        if ( dy1 != 0 ) {
            for (int i = y2; i <= y3; i++) {
                int ax = (int)(x2 + (float)(i - y2) * dax_step);
                int bx = (int)(x1 + (float)(i - y1) * dbx_step);

                float tex_su = u2 + (float)(i - y2) * du1_step;
                float tex_sv = v2 + (float)(i - y2) * dv1_step;
                float tex_sw = w2 + (float)(i - y2) * dw1_step;

                float tex_eu = u1 + (float)(i - y1) * du2_step;
                float tex_ev = v1 + (float)(i - y1) * dv2_step;
                float tex_ew = w1 + (float)(i - y1) * dw2_step;

                if (ax > bx) {
                    int tempInteger = ax;
                    ax = bx;
                    bx = tempInteger;

                    float tempFloat = tex_su;
                    tex_su = tex_eu;
                    tex_eu = tempFloat;

                    tempFloat = tex_sv;
                    tex_sv = tex_ev;
                    tex_ev = tempFloat;

                    tempFloat = tex_sw;
                    tex_sw = tex_ew;
                    tex_ew = tempFloat;
                }

                //tex_u = tex_su;
                //tex_v = tex_sv;
                //tex_w = tex_sw;

                float tstep = 1.0f / ((float)(bx - ax));
                float t = 0.0f;

                for (int j = ax; j < bx; j++) {
                    tex_u = (1.0f - t) * tex_su + t * tex_eu;
                    tex_v = (1.0f - t) * tex_sv + t * tex_ev;
                    tex_w = (1.0f - t) * tex_sw + t * tex_ew;

                    setPixelForTriangle(gc, r, i, j, tex_u, tex_v, tex_w, brightness, texture);

                    t += tstep;
                }
            }
        }
    }

    /**
     * This method is an overload of the drawTexturedTriangle, for this reason, it passes as a
     * parameter a triangle, to clarify the code.
     * @param r the render object with all drawing methods
     * @param triangle the triangle to draw
     * @param texture the image texture
     */
    private void drawTexturedTriangle(GameContainer gc, Renderer r, Triangle triangle, Image texture) {
        drawTexturedTriangle(gc, r,
                (int) triangle.getP()[0].getX(),
                (int) triangle.getP()[0].getY(),
                (int) triangle.getT()[0].getX(),
                (int) triangle.getT()[0].getY(),
                (int) triangle.getT()[0].getZ(),
                (int) triangle.getP()[1].getX(),
                (int) triangle.getP()[1].getY(),
                (int) triangle.getT()[1].getX(),
                (int) triangle.getT()[1].getY(),
                (int) triangle.getT()[1].getZ(),
                (int) triangle.getP()[2].getX(),
                (int) triangle.getP()[2].getY(),
                (int) triangle.getT()[2].getX(),
                (int) triangle.getT()[2].getY(),
                (int) triangle.getT()[2].getZ(),
                triangle.getBrightness(),
                texture);
    }

    /**
     * This method draws a triangle in any form what is would to draw
     * @param r renderer object which contains all drawing methods
     * @param triangle triangle to render.
     */
    private void renderTriangle(GameContainer gc, Renderer r, Triangle triangle) {
        double lastTime = System.nanoTime() / SECONDS;
        switch ( renderFlag ) {
            case RENDER_FLAT:
                drawFlatTriangle(r, triangle);
                break;
            case RENDER_SMOOTH_FLAT:
                drawFlatSmoothTriangle(r, triangle);
                break;
            case RENDER_WIRE:
                drawWireTriangle(r, triangle);
                break;
            case RENDER_TEXTURED:
                drawTexturedTriangle(gc, r, triangle, texture);
                drawWireTriangle(r, triangle);
                break;
            case RENDER_FULL_TEXTURED:
                drawTexturedTriangle(gc, r, triangle, texture);
                break;
        }
        elapsedTimeRenderOneTriangle = (System.nanoTime() / SECONDS) - lastTime;
    }

    /**
     * This method goes over an ArrayList filled with triangles and draws them depending on the render
     * flag which is active.
     * @param r renderer object with all the drawing methods.
     * @param triangles triangles to drawn. In this case, the projected and rasterized triangles.
     */
    private void renderTriangles(GameContainer gc, Renderer r, ArrayList<Triangle> triangles) {
        double lastTime = System.nanoTime() / SECONDS;

        for ( Triangle triangle : triangles ) {
            renderTriangle(gc, r, triangle);
        }
        elapsedTimeRenderAllTriangles = (System.nanoTime() / SECONDS) - lastTime;
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        double lastTime;

        lastTime = System.nanoTime() / SECONDS;
        ArrayList<Triangle> projectedTriangles = projectTriangles(mesh.getTris(), gc.getWidth(), gc.getHeight());
        elapsedTimeProjection = (System.nanoTime() / SECONDS) - lastTime;

        lastTime = System.nanoTime() / SECONDS;
        projectedTriangles.sort(
                (o1, o2) -> {
                    float medZ1 = (o1.getP()[0].getZ() + o1.getP()[1].getZ() + o1.getP()[2].getZ()) / 3.0f;
                    float medZ2 = (o2.getP()[0].getZ() + o2.getP()[1].getZ() + o2.getP()[2].getZ()) / 3.0f;
                    return Float.compare(medZ2, medZ1);
                }
        );
        elapsedTimeSort = (System.nanoTime() / SECONDS) - lastTime;

        lastTime = System.nanoTime() / SECONDS;
        ArrayList<Triangle> rasterizeTriangles = rasterizeTriangles(projectedTriangles, gc.getWidth(), gc.getHeight());
        elapsedTimeRasterize = (System.nanoTime() / SECONDS) - lastTime;

        clearDepthBuffer(gc.getWidth() * gc.getHeight());

        renderTriangles(gc, r, rasterizeTriangles);

        if ( isShowingInformation ) {

            if ( isShowingTriangleInformation )  {
                updateTrianglesInformation(projectedTriangles);
            }

            showInformation(r);
        }
    }

    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new Test3DEngine("Test 3D Engine"));
        gc.start();
    }

}
