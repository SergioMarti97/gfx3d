import engine.GameContainer;

public class Main {

    public static void main(String[] args) {
        String title = "Test 3D Engine";
        Engine3D engine3D;

        if ( args.length > 0 ) {
            engine3D = new Engine3D(title, args[0], args[1]);
        } else {
            engine3D = new Engine3D(title, null, null);
        }

        GameContainer gc = new GameContainer(engine3D);
        gc.start();
    }

}
