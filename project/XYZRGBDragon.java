import greenfoot.*;

public class XYZRGBDragon extends Meshes
{
    public XYZRGBDragon() {
        meshPosition = new float[] { 0, 0, 0 };
        meshRotation = new float[] { 180, 0, 0 };
        meshScale    = new float[] { 10, 10, 10 };

        ObjLoader.load("xyzrgb_dragon.obj");
        meshVertices = ObjLoader.loadedVertices;
        meshTriangleIndices = ObjLoader.loadedTriangleIndices;
        meshTriangleUVs = ObjLoader.loadedTriangleUVs;

        texture = new GreenfootImage("teapot.png"); // put this in your project's /images folder
    }
}