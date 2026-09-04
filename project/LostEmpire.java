import greenfoot.*;

public class LostEmpire extends Meshes
{
public LostEmpire() {
  meshPosition = new float[] { 0, 0, 0 };
  meshRotation = new float[] { 180, 180, 0 };
  meshScale    = new float[] { 40, 40, 40 };

  ObjLoader.load("lost_empire.obj");
  meshVertices = ObjLoader.loadedVertices;
  meshTriangleIndices = ObjLoader.loadedTriangleIndices;
  meshTriangleUVs = ObjLoader.loadedTriangleUVs;

  texture = new GreenfootImage("lost_empire-RGB.png"); // put this in your project's /images folder
}
}