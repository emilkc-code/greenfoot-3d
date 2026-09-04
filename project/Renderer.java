import greenfoot.*;

// I'll try and explain this later

public class Renderer extends Actor
{
public Renderer(int width, int height) {
  setImage(new GreenfootImage(width, height));
  drawScene();
}

private int fov = 90;
private java.util.List<float[]> renderQueue = new java.util.ArrayList<>();
private MatrixMath matrixMath = new MatrixMath();

private Camera camera;
public void setCamera(Camera camera) {
  this.camera = camera;
}

public void clearQueue () {
  renderQueue.clear();
}

public java.util.List<float[]> getRenderQueue () {
  return renderQueue;
}

public void queueMesh(float[] meshPosition, float[] meshRotation, float[] meshScale, float[] meshVertices, int[] triIndices, float[] triangleUVs, GreenfootImage texture) {
  if (camera == null) { return; }

  float[] worldVerts = meshVertices.clone();

  // 1. Local scale
  for (int i = 0; i < worldVerts.length; i += 4) {
    worldVerts[i]     *= meshScale[0];
    worldVerts[i + 1] *= meshScale[1];
    worldVerts[i + 2] *= meshScale[2];
  }

  // 2. Mesh rotation
  for (int i = 0; i < worldVerts.length; i += 4) {
    float[] v = java.util.Arrays.copyOfRange(worldVerts, i, i + 4);
    matrixMath.matrixRotation(v, meshRotation[0], meshRotation[1], meshRotation[2]);
    System.arraycopy(v, 0, worldVerts, i, 4);
  }

  // 3. Translate into world space
  for (int i = 0; i < worldVerts.length; i += 4) {
    worldVerts[i]     += meshPosition[0];
    worldVerts[i + 1] += meshPosition[1];
    worldVerts[i + 2] += meshPosition[2];
  }

  // 4. World -> camera space
  float[] viewMatrix = matrixMath.transpose3x3(camera.getCameraOrientation());
  for (int i = 0; i < worldVerts.length; i += 4) {
    float vx = worldVerts[i]     - camera.getCameraPosition()[0];
    float vy = worldVerts[i + 1] - camera.getCameraPosition()[1];
    float vz = worldVerts[i + 2] - camera.getCameraPosition()[2];

    float[] camSpace = matrixMath.multiply3x1(viewMatrix, new float[]{ vx, vy, vz });

    worldVerts[i]     = camSpace[0] / camera.getCameraScale()[0];
    worldVerts[i + 1] = camSpace[1] / camera.getCameraScale()[1];
    worldVerts[i + 2] = camSpace[2] / camera.getCameraScale()[2];
  }

  float[] camSpaceVerts = worldVerts.clone(); // needed for depth + hypotenuse shading

  // 5. Perspective projection
  for (int i = 0; i < worldVerts.length; i += 4) {
    float[] v = java.util.Arrays.copyOfRange(worldVerts, i, i + 4);
    matrixMath.matrixPerspectiveProjection(v, fov);
    System.arraycopy(v, 0, worldVerts, i, 4);
  }

  int screenWidth = getImage().getWidth();
  int screenHeight = getImage().getHeight();

  // 6. Build triangles and push into the shared queue instead of drawing
  for (int i = 0; i < triIndices.length; i += 3) {
    int v1 = triIndices[i] * 4;
    int v2 = triIndices[i + 1] * 4;
    int v3 = triIndices[i + 2] * 4;

    float w1 = worldVerts[v1 + 3];
    float w2 = worldVerts[v2 + 3];
    float w3 = worldVerts[v3 + 3];
    if (w1 <= 0.0001f || w2 <= 0.0001f || w3 <= 0.0001f) { continue; }

    // Depth for SORTING: centroid z across all 3 vertices (camera space)
    float nearestZ = Math.max(camSpaceVerts[v1 + 2],
            Math.max(camSpaceVerts[v2 + 2], camSpaceVerts[v3 + 2]));
    float sortDepth = -nearestZ;

    // Depth for SHADING: hypotenuse midpoint, as established earlier
    float d12 = cameraSpaceDistance(camSpaceVerts, v1, v2);
    float d23 = cameraSpaceDistance(camSpaceVerts, v2, v3);
    float d31 = cameraSpaceDistance(camSpaceVerts, v3, v1);

    float hypMidZ;
    if (d12 >= d23 && d12 >= d31) {
      hypMidZ = (camSpaceVerts[v1 + 2] + camSpaceVerts[v2 + 2]) / 2.0f;
    } else if (d23 >= d12 && d23 >= d31) {
      hypMidZ = (camSpaceVerts[v2 + 2] + camSpaceVerts[v3 + 2]) / 2.0f;
    } else {
      hypMidZ = (camSpaceVerts[v3 + 2] + camSpaceVerts[v1 + 2]) / 2.0f;
    }

    float shadeDistance = -hypMidZ;
    float nearDist = 200f, farDist = 5000f;
    float brightness = 1.0f - ((shadeDistance - nearDist) / (farDist - nearDist));
    brightness = Math.max(0.0f, Math.min(1.0f, brightness));

    // --- Texture sampling instead of a fixed color[] ---
    int triIdx = i / 3;
    float u = triangleUVs[triIdx * 2];
    float v = triangleUVs[triIdx * 2 + 1];
    Color texel = sampleTexture(texture, u, v);

    int r = Math.min(255, (int) (texel.getRed()   * brightness));
    int g = Math.min(255, (int) (texel.getGreen() * brightness));
    int b = Math.min(255, (int) (texel.getBlue()  * brightness));

    float x1 = (worldVerts[v1]     * 400) + (screenWidth / 2.0f);
    float y1 = (worldVerts[v1 + 1] * 400) + (screenHeight / 2.0f);
    float x2 = (worldVerts[v2]     * 400) + (screenWidth / 2.0f);
    float y2 = (worldVerts[v2 + 1] * 400) + (screenHeight / 2.0f);
    float x3 = (worldVerts[v3]     * 400) + (screenWidth / 2.0f);
    float y3 = (worldVerts[v3 + 1] * 400) + (screenHeight / 2.0f);

    float signedArea = (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
    if (signedArea <= 0) { continue; }

    renderQueue.add(new float[]{ x1, y1, x2, y2, x3, y3, sortDepth, r, g, b });
  }
}

private Color sampleTexture(GreenfootImage texture, float u, float v) {
  // OBJ UVs are typically 0..1 with (0,0) at bottom-left; images index (0,0) at top-left, so flip v
  int texWidth = texture.getWidth();
  int texHeight = texture.getHeight();

  int x = (int) (u * (texWidth - 1));
  int y = (int) ((1.0f - v) * (texHeight - 1));

  x = Math.max(0, Math.min(texWidth - 1, x));
  y = Math.max(0, Math.min(texHeight - 1, y));

  return texture.getColorAt(x, y);
}

public void drawScene() {
  GreenfootImage canvas = getImage();
  canvas.clear();
  canvas.setColor(Color.BLACK);
  canvas.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

  // Farthest first, nearest last, so near triangles paint over far ones
  renderQueue.sort((a, b) -> Float.compare(b[6], a[6]));

  for (float[] tri : renderQueue) {
    int[] xPoints = { (int) tri[0], (int) tri[2], (int) tri[4] };
    int[] yPoints = { (int) tri[1], (int) tri[3], (int) tri[5] };

    canvas.setColor(new Color((int) tri[7], (int) tri[8], (int) tri[9]));
    canvas.fillPolygon(xPoints, yPoints, 3);
  }
}

private float cameraSpaceDistance(float[] verts, int i1, int i2) {
  float dx = verts[i1]     - verts[i2];
  float dy = verts[i1 + 1] - verts[i2 + 1];
  float dz = verts[i1 + 2] - verts[i2 + 2];
  return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
}
}