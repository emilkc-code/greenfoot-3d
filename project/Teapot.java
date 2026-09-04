import greenfoot.*;

public class Teapot extends Meshes
{
public Teapot() {
  meshPosition = new float[] { 0, 0, 0 };
  meshRotation = new float[] { 180, 0, 0 };
  meshScale    = new float[] { 10, 10, 10 };

  ObjLoader.load("teapot.obj"); // filename relative to the project's root folder
  meshVertices = ObjLoader.loadedVertices;
  meshTriangleIndices = ObjLoader.loadedTriangleIndices;
  meshTriangleUVs = ObjLoader.loadedTriangleUVs;

  texture = new GreenfootImage("teapot.png"); // put this in your project's /images folder
}
}