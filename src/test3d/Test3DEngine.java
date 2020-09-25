package test3d;

import engine.AbstractGame;
import engine.GameContainer;
import engine.gfx.Renderer;
import engine.gfx.images.Image;
import engine.gfx.images.ImageTile;
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
public class Test3DEngine extends AbstractGame {

    private PipeLine pipeLine;

    private ImageTile imageTile;

    private Image texture;

    private Mesh mesh = new Mesh();

    private Vec4df cubeTranslation = new Vec4df();

    private Vec4df cubeRotation = new Vec4df(0.0f, 0.0f, 0.0f);

    private boolean isPerspectiveProjection = true;

    private Test3DEngine(String title) {
        super(title);
    }

    @Override
    public void initialize(GameContainer gc) {
        pipeLine = new PipeLine(gc);

        /*if ( !mesh.loadFromObjectFile("resources/mountains_texture.obj", true) ) {
            mesh = pipeLine.getUnitCube();
        }*/
        mesh = pipeLine.getUnitCube();

        imageTile = new ImageTile("/dg_dungeon32.gif", 32, 32);
        texture = new Image("/Super_Paper_Mario2.png");
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
            cubeRotation.addToX(0.2f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD2) ) {
            cubeRotation.addToX(-0.2f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD4) ) {
            cubeRotation.addToY(0.2f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD6) ) {
            cubeRotation.addToY(-0.2f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD7) ) {
            cubeRotation.addToZ(0.2f * dt);
        }
        if ( gc.getInput().isKeyHeld(KeyEvent.VK_NUMPAD3) ) {
            cubeRotation.addToZ(-0.2f * dt);
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

    /**
     * This method manage the input user for the projection flag and
     * updates when the flag changes the projection matrix.
     * @param gc the object GameContainer with the Input Object
     */
    private void updateProjectionMatrix(GameContainer gc) {
        if ( gc.getInput().isKeyDown(KeyEvent.VK_NUMPAD5) ) {
            isPerspectiveProjection = !isPerspectiveProjection;
            if ( isPerspectiveProjection ) {
                pipeLine.setPerspective(Perspective.NORMAL);
            } else {
                pipeLine.setPerspective(Perspective.ORTHOGONAL);
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
        updateRenderFlags(gc);
        updateCamera(gc, dt);
        updateCube(gc, dt);
        updateProjectionMatrix(gc);
        transformCube();
        updateTexture(gc);
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        pipeLine.renderMesh(mesh, texture);
    }

    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new Test3DEngine("Test 3D Engine"));
        gc.start();
    }

}
