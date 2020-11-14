import engine.GameContainer;

/**
 * This class contains the main method and exit of the program
 *
 * This code is simply to try different 3d models rendered
 * with the engine.
 *
 * @class TestEngine3D
 * @author Sergio Martí Torregrosa
 * @date 13/10/2020
 */
public class TestEngine3D {

    public static void main(String[] args) {
        Engine3D engine3D = new Engine3D(
                "Test 3D Spyro Glimmer level",
                "C:\\Users\\Sergio\\IdeaProjects\\ENGINE-PROJECTS\\engine-gfx3d\\testresources\\models3d\\mountains_texture.obj",
                "C:\\Users\\Sergio\\IdeaProjects\\ENGINE-PROJECTS\\engine-gfx3d\\testresources\\textures\\texture.png");
        GameContainer gc = new GameContainer(engine3D);
        gc.start();
    }

}
