import greenfoot.*;
public class Control
{
private MatrixMath matrixMath = new MatrixMath();
private Camera camera;

/**
 * Set camera to use for controls
 */
/* We set the camera using our Main class to ensure it's the same as the Renderer class's camera */
public void setCamera(Camera camera) {
  this.camera = camera;
}

/* Here we change the position and rotation of the camera based on keyboard inputs */
public void controlCamera() {
  if (camera == null) { return; }

  float moveSpeed = 10.0f;
  float rotationSpeed = 2.0f;
  float dx = 0, dy = 0, dz = 0;

  if (Greenfoot.isKeyDown("shift")) { moveSpeed = 20.0f; }
  else { moveSpeed = 10.0f; }

  if (Greenfoot.isKeyDown("w")) { dz -= moveSpeed; }
  if (Greenfoot.isKeyDown("s")) { dz += moveSpeed; }
  if (Greenfoot.isKeyDown("a")) { dx -= moveSpeed; }
  if (Greenfoot.isKeyDown("d")) { dx += moveSpeed; }
  if (Greenfoot.isKeyDown("q")) { dy += moveSpeed; }
  if (Greenfoot.isKeyDown("e")) { dy -= moveSpeed; }

  float pitchDelta = 0, yawDelta = 0;
  if (Greenfoot.isKeyDown("up"))    { pitchDelta -= rotationSpeed; }
  if (Greenfoot.isKeyDown("down"))  { pitchDelta += rotationSpeed; }
  if (Greenfoot.isKeyDown("left"))  { yawDelta += rotationSpeed; }
  if (Greenfoot.isKeyDown("right")) { yawDelta -= rotationSpeed; }


  /* Making sure we take into account our current orientation when rotating */
  if (pitchDelta != 0) { camera.setCameraOrientation(matrixMath.multiply3x3(camera.getCameraOrientation(), matrixMath.rotationX3x3(pitchDelta))); }

  /* You can switch multiply3x3 and getCameraOrientation to make yaw local instead of locked to y-axis */
  if (yawDelta   != 0) { camera.setCameraOrientation(matrixMath.multiply3x3(matrixMath.rotationY3x3(yawDelta), camera.getCameraOrientation())); }

  /*
   Same goes here, but for movement
   You can set dx, dy or dz to 0 and add them directly to their
   respective axes in setCameraPosition to lock them to world axes
  */
  float[] localMove = { dx, dy, dz };
  float[] worldMove = matrixMath.multiply3x1(camera.getCameraOrientation(), localMove);

  camera.setCameraPosition(new float[] {
          camera.getCameraPosition()[0] + worldMove[0], // <-- Add directly here
          camera.getCameraPosition()[1] + worldMove[1],
          camera.getCameraPosition()[2] + worldMove[2],
  });
}
}