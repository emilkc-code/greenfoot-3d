import greenfoot.*;
public class Control
{
    private MatrixMath matrixMath = new MatrixMath();
    private Camera camera;
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
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

        if (pitchDelta != 0) { camera.setCameraOrientation(matrixMath.multiply3x3(camera.getCameraOrientation(), matrixMath.rotationX3x3(pitchDelta))); }
        if (yawDelta   != 0) { camera.setCameraOrientation(matrixMath.multiply3x3(matrixMath.rotationY3x3(yawDelta), camera.getCameraOrientation())); }

        float[] localMove = { dx, dy, dz };
        float[] worldMove = matrixMath.multiply3x1(camera.getCameraOrientation(), localMove);

        camera.setCameraPosition(new float[] {
            camera.getCameraPosition()[0] + worldMove[0],
            camera.getCameraPosition()[1] + worldMove[1],
            camera.getCameraPosition()[2] + worldMove[2],
        });
    }
}