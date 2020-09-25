package engine3d;

import engine.gfx.images.Image;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The mesh class
 * Contains the triangles and the texture image
 *
 * @class Mesh
 * @author Sergio Martí Torregrosa
 * @date 18/08/2020
 */
public class Mesh {

    /**
     * The triangles which forms the mesh
     */
    private ArrayList<Triangle> tris;

    /**
     * The image texture
     */
    private Image texture = null;

    /**
     * The constructor
     */
    public Mesh() {
        tris = new ArrayList<>();
    }

    /**
     * Constructor
     * @param tris the triangles what conform the mesh
     */
    public Mesh(ArrayList<Triangle> tris) {
        this.tris = tris;
    }

    /**
     * The getter for the triangles
     * @return the triangles
     */
    public ArrayList<Triangle> getTris() {
        return tris;
    }

    /**
     * the getter for the texture
     * @return the texture image
     */
    public Image getTexture() {
        return texture;
    }

    /**
     * The setter for the triangles
     * @param tris the new triangles
     */
    public void setTris(ArrayList<Triangle> tris) {
        this.tris = tris;
    }

    /**
     * The setter for the texture
     * @param texture the new image for the texture
     */
    public void setTexture(Image texture) {
        this.texture = texture;
    }

    /**
     * This method of the Mesh class loads a ".obj" file to understandable data for
     * the classes of the 3D engine. This allows to work with outer 3D models.
     * @param path the path where is the ".obj" file
     * @return return true if the load was fine, or false if it can't load the file
     */
    public boolean loadFromObjectFile(String path, boolean hasTexture) {
        ArrayList<Vec4df> vertex = new ArrayList<>();
        ArrayList<Vec3df> texturePoints = new ArrayList<>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(path));
            String line = "";

            while ( line != null ) {
                String[] splitLine = line.split(" ");
                if ( splitLine[0].equalsIgnoreCase("v") ) {
                    float x = Float.parseFloat(splitLine[1]);
                    float y = Float.parseFloat(splitLine[2]);
                    float z = Float.parseFloat(splitLine[3]);
                    vertex.add(new Vec4df(x, y, z));
                }

                if ( splitLine[0].equalsIgnoreCase("vt") ) {
                    float x = Float.parseFloat(splitLine[1]);
                    float y = Float.parseFloat(splitLine[2]);
                    texturePoints.add(new Vec3df(x, y));
                }

                if ( splitLine[0].equalsIgnoreCase("f") ) {
                    if ( !hasTexture ) {
                        int[] face = new int[3];
                        for ( int i = 1; i < splitLine.length; i++ ) {
                            String[] numVertex = splitLine[i].split("//");
                            face[i - 1] = Integer.parseInt(numVertex[0]);
                        }
                        tris.add(
                                new Triangle(
                                        new Vec4df[] {
                                                vertex.get(face[0] - 1),
                                                vertex.get(face[1] - 1),
                                                vertex.get(face[2] - 1)
                                        })
                        );
                    } else {
                        int[] facePoint = new int[3];
                        int[] faceTexture = new int[3];
                        for ( int i = 1; i < splitLine.length; i++ ) {
                            String[] splitNumber = splitLine[i].split("/");
                            facePoint[i - 1] = Integer.parseInt(splitNumber[0]);
                            faceTexture[i - 1] = Integer.parseInt(splitNumber[2]);
                        }
                        tris.add(
                                new Triangle(
                                        new Vec4df[] {
                                                vertex.get(facePoint[0] - 1),
                                                vertex.get(facePoint[1] - 1),
                                                vertex.get(facePoint[2] - 1)
                                        },
                                        new Vec3df[] {
                                                texturePoints.get(faceTexture[0]),
                                                texturePoints.get(faceTexture[1]),
                                                texturePoints.get(faceTexture[2]),
                                        }
                                )
                        );
                    }
                }

                line = bf.readLine();
            }
            bf.close();
            return true;
        } catch ( FileNotFoundException e ) {
            System.out.println("No se encuentra el fichero.");
            return false;
        } catch ( IOException e ) {
            System.out.println("No se puede leer el fichero");
            return false;
        }
    }

    public void showInformation() {
        for ( int i = 0; i < tris.size() - 2; i++ ) {
            System.out.println(String.format("Triangulo %d", i));
            for ( int j = 0; j < tris.get(i).getP().length; j++ ) {
                System.out.println(
                        String.format("X: %f Y: %f Z: %f",
                                tris.get(i).getP()[j].getX(),
                                tris.get(i).getP()[j].getY(),
                                tris.get(i).getP()[j].getZ())
                );
            }
        }
    }

}
