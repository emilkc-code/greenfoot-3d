import greenfoot.*;

public class Cube extends Meshes
{
    public Cube() {
        meshPosition = new float[] { 0, 0, 0 };
        meshRotation = new float[] { 180, 0, 0 };
        meshScale    = new float[] { 900, 900, 900 };

        ObjLoader.load("cube.obj");
        meshVertices = ObjLoader.loadedVertices;
        meshTriangleIndices = ObjLoader.loadedTriangleIndices;
        meshTriangleUVs = ObjLoader.loadedTriangleUVs;
    }
}