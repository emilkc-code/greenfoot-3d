import greenfoot.*;

public class Meshes
{
    public float[] meshPosition = { 0, 0, 0 };
    public float[] meshRotation = { 0, 0, 0 };
    public float[] meshScale    = { 1, 1, 1 };
    
    public float[] getPosition() {
        return meshPosition;
    }
    
    public float[] getRotation() {
        return meshRotation;
    }
    
    public float[] getScale() {
        return meshScale;
    }
    
    public float[] getVertices() {
        return meshVertices;
    }
    
    public int[] getTriangleIndices() {
        return meshTriangleIndices;
    }
    
    public float[] getTriangleUVs() {
        return meshTriangleUVs;
    }
    
    public GreenfootImage getTexture() {
        return texture;
    }

    public void setPosition(float[] array) {
        meshPosition[0] = array[0];
        meshPosition[1] = array[1];
        meshPosition[2] = array[2];
    }

    public void setRotation(float[] array) {
        meshRotation[0] = array[0];
        meshRotation[1] = array[1];
        meshRotation[2] = array[2];
    }

    public void setScale(float[] array) {
        meshScale[0] = array[0];
        meshScale[1] = array[1];
        meshScale[2] = array[2];
    }
    
    public float[] meshVertices = {};

    public int[] meshTriangleIndices = {};
    
    public float[] meshTriangleUVs = {};
    
    public GreenfootImage texture;
}