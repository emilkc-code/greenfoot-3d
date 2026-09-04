public class Compositor
{
// Here we define our meshes
// We could add them to an array and render everything in the array,
// but we may want to change properties of the mesh before adding it to our scene
private final Cube cube = new Cube();
private final Teapot teapot = new Teapot();
private final StanfordBunny stanfordBunny = new StanfordBunny();
private final LostEmpire lostEmpire = new LostEmpire();
private final XYZRGBDragon xYZRGBDragon = new XYZRGBDragon();
private Renderer renderer;

public void setRenderer(Renderer renderer) {
  this.renderer = renderer;
}

private void queueMesh(Meshes mesh) {
  renderer.queueMesh(
    mesh.getPosition(),
    mesh.getRotation(),
    mesh.getScale(),
    mesh.getVertices(),
    mesh.getTriangleIndices(),
    mesh.getTriangleUVs(),
    mesh.getTexture()
  );
}

// This gets called every cycle
// It clears all meshes from the scene and re-adds them
public void composit() {
  if (renderer == null) { return; }

  renderer.clearQueue();

  // Here we set the position before placing our mesh
  // We could also use .setRotation or .setScale
  cube.setPosition(new float[] { 0, 0, -2000 });
  //queueMesh(cube);                              <-- Uncomment to place the cube

  teapot.setPosition(new float[] { 2000, 500, -2000 });
  queueMesh(teapot);

  stanfordBunny.setPosition(new float[] { 4000, 500, -2000 });
  //queueMesh(stanfordBunny);

  lostEmpire.setPosition(new float[] { 0, 600, -2000 });
  //queueMesh(lostEmpire);

  xYZRGBDragon.setPosition(new float[] { 0, 600, -2000 });
  //queueMesh(xYZRGBDragon);

  // Here we render the scene after placing our meshes
  renderer.drawScene();
}
}