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
                "C:\\Users\\Sergio\\IdeaProjects\\engine-gfx3d\\testresources\\spyro\\spyroGlimmer3.obj",
                "C:\\Users\\Sergio\\IdeaProjects\\engine-gfx3d\\testresources\\spyro\\glimmer.png");
        GameContainer gc = new GameContainer(engine3D);
        gc.start();
    }

}
