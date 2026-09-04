import greenfoot.*;

public class Renderer extends Actor
{
public Renderer(int width, int height) {
  setImage(new GreenfootImage(width, height));
  drawScene();
}

public static int fov = 90;

private final java.util.List<float[]> renderQueue = new java.util.ArrayList<>();
private final MatrixMath matrixMath = new MatrixMath();

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

/**
 * Adds the mesh to the render-queue
 */
public void queueMesh(Meshes mesh) {
  if (camera == null) { return; }

  float[] worldVerts = mesh.getVertices().clone();

  /* Scale mesh */
  for (int i = 0; i < worldVerts.length; i += 4) {
    worldVerts[i]     *= mesh.getScale()[0];
    worldVerts[i + 1] *= mesh.getScale()[1];
    worldVerts[i + 2] *= mesh.getScale()[2];
  }

  /* Rotate mesh */
  for (int i = 0; i < worldVerts.length; i += 4) {
    float[] v = java.util.Arrays.copyOfRange(worldVerts, i, i + 4);
    matrixMath.matrixRotation(v, mesh.getRotation()[0], mesh.getRotation()[1], mesh.getRotation()[2]);
    System.arraycopy(v, 0, worldVerts, i, 4);
  }

  /* Translate into world space */
  for (int i = 0; i < worldVerts.length; i += 4) {
    worldVerts[i]     += mesh.getPosition()[0];
    worldVerts[i + 1] += mesh.getPosition()[1];
    worldVerts[i + 2] += mesh.getPosition()[2];
  }

  /* World space -> camera space */
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

  /* Perspective projection */
  for (int i = 0; i < worldVerts.length; i += 4) {
    float[] v = java.util.Arrays.copyOfRange(worldVerts, i, i + 4);
    matrixMath.matrixPerspectiveProjection(v, fov);
    System.arraycopy(v, 0, worldVerts, i, 4);
  }

  int screenWidth = getImage().getWidth();
  int screenHeight = getImage().getHeight();

  /* Build triangles and push into the queue */
  for (int i = 0; i < mesh.getTriangleIndices().length; i += 3) {
    int v1 = mesh.getTriangleIndices()[i] * 4;
    int v2 = mesh.getTriangleIndices()[i + 1] * 4;
    int v3 = mesh.getTriangleIndices()[i + 2] * 4;

    float w1 = worldVerts[v1 + 3];
    float w2 = worldVerts[v2 + 3];
    float w3 = worldVerts[v3 + 3];
    if (w1 <= 0.0001f || w2 <= 0.0001f || w3 <= 0.0001f) { continue; }

    /* Depth for SORTING: centroid z across all 3 vertices (camera space) */
    float nearestZ = Math.max(camSpaceVerts[v1 + 2],
            Math.max(camSpaceVerts[v2 + 2], camSpaceVerts[v3 + 2]));
    float sortDepth = -nearestZ;

    /* Depth for SHADING: hypotenuse midpoint (camera space) */
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

    /* Brightness variable (Mesh get darker further away (Basic fog)) */
    float shadeDistance = -hypMidZ;
    float nearDist = 200f, farDist = 5000f;
    float brightness = 1.0f - ((shadeDistance - nearDist) / (farDist - nearDist));
    brightness = Math.clamp(brightness, 0.0f, 1.0f);

    /*
     Texture sampling
     We can only paint the triangle a single color
    */
    int triIdx = i / 3;
    float u = mesh.getTriangleUVs()[triIdx * 2];
    float v = mesh.getTriangleUVs()[triIdx * 2 + 1];
    Color texel = sampleTexture(mesh.getTexture(), u, v);

    /* Apply the fog */
    int r = Math.min(255, (int) (texel.getRed()   * brightness));
    int g = Math.min(255, (int) (texel.getGreen() * brightness));
    int b = Math.min(255, (int) (texel.getBlue()  * brightness));

    /* Fit to screen */
    int screenWidthHalf = (int) (screenWidth / 2.0f);
    int screenHeightHalf = (int) (screenHeight / 2.0f);
    float x1 = worldVerts[v1]     * screenWidthHalf  + screenWidthHalf;
    float y1 = worldVerts[v1 + 1] * screenHeightHalf + screenHeightHalf;
    float x2 = worldVerts[v2]     * screenWidthHalf  + screenWidthHalf;
    float y2 = worldVerts[v2 + 1] * screenHeightHalf + screenHeightHalf;
    float x3 = worldVerts[v3]     * screenWidthHalf  + screenWidthHalf;
    float y3 = worldVerts[v3 + 1] * screenHeightHalf + screenHeightHalf;

    float signedArea = (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
    if (signedArea <= 0) { continue; }

    renderQueue.add(new float[]{ x1, y1, x2, y2, x3, y3, sortDepth, r, g, b });
  }
}

private Color sampleTexture(GreenfootImage texture, float u, float v) {
  /* OBJ UVs are typically 0..1 with (0,0) at bottom-left; images index (0,0) at top-left, so flip v */
  int texWidth = texture.getWidth();
  int texHeight = texture.getHeight();

  int x = (int) (u * (texWidth - 1));
  int y = (int) ((1.0f - v) * (texHeight - 1));

  x = Math.clamp(x, 0, texWidth - 1);
  y = Math.clamp(y, 0, texHeight - 1);

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