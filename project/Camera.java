public class Camera
{
    private float[] cameraPosition = { 0, 0, 0 };
    private float[] cameraScale = { 1, 1, 1 };

    // 3x3 orientation matrix: rows are the camera's local right/up/forward axes in world space
    private float[] cameraOrientation = {
        1, 0, 0,
        0, 1, 0,
        0, 0, 1
    };
    
    public float[] getCameraPosition () {
        return cameraPosition;
    }
    
    public float[] getCameraScale () {
        return cameraScale;
    }
    
    public float[] getCameraOrientation () {
        return cameraOrientation;
    }
    
    public void setCameraPosition (float[] array) {
        cameraPosition[0] = array[0];
        cameraPosition[1] = array[1];
        cameraPosition[2] = array[2];
    }
    
    public void setCameraScale (float[] array) {
        cameraScale[0] = array[0];
        cameraScale[1] = array[1];
        cameraScale[2] = array[2];
    }
    
    public void setCameraOrientation (float[] array) {
        cameraOrientation[0] = array[0];
        cameraOrientation[1] = array[1];
        cameraOrientation[2] = array[2];
        
        cameraOrientation[3] = array[3];
        cameraOrientation[4] = array[4];
        cameraOrientation[5] = array[5];
        
        cameraOrientation[6] = array[6];
        cameraOrientation[7] = array[7];
        cameraOrientation[8] = array[8];
    }
}