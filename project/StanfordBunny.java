import greenfoot.*;

public class StanfordBunny extends Meshes
{
    public StanfordBunny() {
        meshPosition = new float[] { 0, 0, 0 };
        meshRotation = new float[] { 180, 180, 0 };
        meshScale    = new float[] { 300, 300, 300 };

        ObjLoader.load("stanford-bunny.obj");
        meshVertices = ObjLoader.loadedVertices;
        meshTriangleIndices = ObjLoader.loadedTriangleIndices;
        meshTriangleUVs = ObjLoader.loadedTriangleUVs;

        texture = new GreenfootImage("teapot.png"); // put this in your project's /images folder
    }
}