import engine.AbstractGame;
import engine.GameContainer;
import engine.gfx.Renderer;
import engine.gfx.images.Image;
import engine3d.*;
import engine3d.matrix.Mat4x4;
import engine3d.matrix.MatrixMath;

import java.awt.event.KeyEvent;

/**
 * This class is a program to test and debug the good behaviour of the classes from
 * the 3D engine.
 *
 * @class: Test3DEngine2.
 * @autor: Sergio Martí Torregrosa. sMartiTo
 * @date: 2020-07-28
 */
public class Engine3D extends AbstractGame {

    /**
     * The pipeline for render 3D objects
     */
    private PipeLine pipeLine;

    /**
     * The texture of the 3D object
     */
    private Image texture;

    /**
     * The mesh which conforms the object
     */
    private Mesh mesh = new Mesh();

    /**
     * The translation of the mesh
     */
    private Vec4df meshTranslation = new Vec4df();

    /**
     * The rotation of the mesh
     */
    private Vec4df meshRotation = new Vec4df(0.0f, 0.0f, 0.0f);

    /**
     * The path for the mesh
     */
    private String meshPath;

    /**
     * The path for the texture
     */
    private String texturePath;

    /**
     * Constructor
     * @param title the title of the program
     */
    Engine3D(String title, String meshPath, String texturePath) {
        super(title);
        this.meshPath = meshPath;
        this.texturePath = texturePath;
    }

    @Override
    public void initialize(GameContainer gc) {
        pipeLine = new PipeLine(gc);

        if ( !mesh.loadFromObjectFile(meshPath, true) ) {
            mesh = pipeLine.getUnitCube();
        }

        texture = new Image(texturePath);
    }

    /**
     * This method manages the user input for camera translation, or panning.
     * @param gc the GameContainer object with te Input object needed to manage user inputs
     * @param dt the elapsed time between frames
     */
    private void updateCameraPanning(GameContainer gc, float dt) {
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_RIGHT) ) {
            pipeLine.getCameraObj().getOrigin().addToX(2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_LEFT) ) {
            pipeLine.getCameraObj().getOrigin().addToX(-2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_UP) ) {
            pipeLine.getCameraObj().getOrigin().addToY(2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_DOWN) ) {
            pipeLine.getCameraObj().getOrigin().addToY(-2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_Z) ) {
            pipeLine.getCameraObj().getOrigin().addToZ(2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_X) ) {
            pipeLine.getCameraObj().getOrigin().addToZ(-2.0f * dt);
        }
    }

    /**
     * This method manages the user input for camera rotation
     * @param gc the GameContainer object with te Input object needed to manage user inputs
     * @param dt the elapsed time between frames
     */
    private void updateCameraRotation(GameContainer gc, float dt) {
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_W) ) {
            pipeLine.getCameraObj().rotX(-2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_S) ) {
            pipeLine.getCameraObj().rotX(2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_A) ) {
            pipeLine.getCameraObj().rotY(-2.0f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_D) ) {
            pipeLine.getCameraObj().rotY(2.0f * dt);
        }
    }

    /**
     * This method manages the user input for camera zooming
     * @param gc the GameContainer object with te Input object needed to manage user inputs
     * @param dt the elapsed time between frames
     */
    private void updateCameraZoom(GameContainer gc, float dt) {
        Vec4df forward = MatrixMath.vectorMul(pipeLine.getCameraObj().getLookDirection(), - gc.getInput().getScroll() * 0.5f * dt);
        pipeLine.getCameraObj().setOrigin(MatrixMath.vectorAdd(pipeLine.getCameraObj().getOrigin(), forward));
    }

    /**
     * En este método se gestiona las entradas de usuario para manejar la posición y
     * rotación de la cámara.
     * @param gc el objeto GameContainer para acceder al objeto Input.
     * @param dt el tiempo transcurrido entre actualización y aztualización.
     */
    private void updateCamera(GameContainer gc, float dt) {
        updateCameraPanning(gc, dt);
        updateCameraRotation(gc, dt);
        updateCameraZoom(gc, dt);
        pipeLine.setMatView(pipeLine.getCameraObj().getMatView());
    }

    /**
     * En este método se gestiona las entradas de usuario para manejar la rotación
     * del cubo.
     * @param gc el objeto GameContainer para acceder al objeto Input.
     */
    private void updateCube(GameContainer gc, float dt) {
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD8) ) {
            meshRotation.addToX(0.2f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD2) ) {
            meshRotation.addToX(-0.2f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD4) ) {
            meshRotation.addToY(0.2f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD6) ) {
            meshRotation.addToY(-0.2f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD7) ) {
            meshRotation.addToZ(0.2f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD3) ) {
            meshRotation.addToZ(-0.2f * dt);
        }
    }

    /**
     * Este método sirve para transformar los triangulos que forman el la malla (el cubo).
     */
    private void transformCube() {
        Mat4x4 matIdentity = MatrixMath.matrixMakeIdentity();
        Mat4x4 matRotX = MatrixMath.matrixMakeRotationX(meshRotation.getX());
        matRotX = MatrixMath.matrixMultiplyMatrix(matIdentity, matRotX);
        Mat4x4 matRotY = MatrixMath.matrixMakeRotationY(meshRotation.getY());
        Mat4x4 matRotXY = MatrixMath.matrixMultiplyMatrix(matRotY, matRotX);
        Mat4x4 matRotZ = MatrixMath.matrixMakeRotationZ(meshRotation.getZ());
        Mat4x4 matRot = MatrixMath.matrixMultiplyMatrix(matRotXY, matRotZ);
        Mat4x4 matTranslation = MatrixMath.matrixMakeTranslation(meshTranslation.getX(), meshTranslation.getY(), meshTranslation.getZ());
        Mat4x4 matRotTrans = MatrixMath.matrixMultiplyMatrix(matRot, matTranslation);
        pipeLine.setTransform(matRotTrans);
    }

    /**
     * This method manage the user inputs for render flags
     * @param gc the object GameContainer with the Input Object
     */
    private void updateRenderFlags(GameContainer gc) {
        if ( gc.getInput().isKeyDown(KeyEvent.VK_H) ) {
            pipeLine.getRenderer3D().setRenderFlag(RenderFlags.RENDER_WIRE);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_J) ) {
            pipeLine.getRenderer3D().setRenderFlag(RenderFlags.RENDER_FLAT);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_K) ) {
            pipeLine.getRenderer3D().setRenderFlag(RenderFlags.RENDER_SMOOTH_FLAT);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_T) ) {
            pipeLine.getRenderer3D().setRenderFlag(RenderFlags.RENDER_TEXTURED);
        }
        if ( gc.getInput().isKeyDown(KeyEvent.VK_Y) ) {
            pipeLine.getRenderer3D().setRenderFlag(RenderFlags.RENDER_FULL_TEXTURED);
        }
    }

    @Override
    public void update(GameContainer gc, float dt) {
        updateRenderFlags(gc);
        updateCamera(gc, dt);
        updateCube(gc, dt);
        transformCube();
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        pipeLine.renderMesh(mesh, texture);
    }

}