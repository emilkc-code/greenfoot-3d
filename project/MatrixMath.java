public class MatrixMath
{

/* Matrix math that we put in its own class */

/**
 * Performs rotation in 3D space
 * @param matrix
 * @param rotX
 * @param rotY
 * @param rotZ
 */
public void matrixRotation(float[] matrix, float rotX, float rotY, float rotZ) {
  float[] mathMatrix = multiply3x1(rotationX3x3(rotX), matrix);
  mathMatrix = multiply3x1(rotationY3x3(rotY), mathMatrix);
  mathMatrix = multiply3x1(rotationZ3x3(rotZ), mathMatrix);

  matrix[0] = mathMatrix[0];
  matrix[1] = mathMatrix[1];
  matrix[2] = mathMatrix[2];
}

/**
 * Applies perspective based on an fov
 * @param matrix
 * @param fov
 */
public void matrixPerspectiveProjection(float[] matrix, int fov) {
  float x = matrix[0], y = matrix[1], z = matrix[2], w = matrix[3];
  float fovParam = (float) Math.tan(Math.toRadians(fov) / 2.0f);
  float zFar = 2000f, zNear = 1f;

  float m00 = 1.0f / fovParam;
  float m11 = 1.0f / fovParam;
  float m22 = -(zFar + zNear) / (zFar - zNear);
  float m23 = -(2.0f * zNear * zFar) / (zFar - zNear);

  float projX = m00 * x;
  float projY = m11 * y;
  float projZ = m22 * z + m23 * w;
  float projW = -z;

  if (projW != 0) {
    projX /= projW;
    projY /= projW;
  }

  matrix[0] = projX;
  matrix[1] = projY;
  matrix[2] = projZ;
  matrix[3] = projW;
}

/**
 * Returns a matrix to multiply with for rotation along the x-axis in 3D space
 * @param degrees
 * @return
 */
public float[] rotationX3x3(float degrees) {
  float rad = (float) Math.toRadians(degrees);
  float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
  return new float[] { 1,0,0,  0,c,-s,  0,s,c };
}

/**
 * Returns a matrix to multiply with for rotation along the y-axis in 3D space
 * @param degrees
 * @return
 */
public float[] rotationY3x3(float degrees) {
  float rad = (float) Math.toRadians(degrees);
  float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
  return new float[] { c,0,s,  0,1,0,  -s,0,c };
}

/**
 * Returns a matrix to multiply with for rotation along the z-axis in 3D space
 * @param degrees
 * @return
 */
public float[] rotationZ3x3(float degrees) {
  float rad = (float) Math.toRadians(degrees);
  float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
  return new float[] { c,-s,0,  s,c,0,  0,0,1 };
}

public float[] transpose3x3(float[] m) {
  return new float[] { m[0],m[3],m[6],  m[1],m[4],m[7],  m[2],m[5],m[8] };
}

public float[] multiply3x3(float[] a, float[] b) {
  float[] r = new float[9];
  for (int row = 0; row < 3; row++)
    for (int col = 0; col < 3; col++)
      r[row*3+col] = a[row*3]*b[col] + a[row*3+1]*b[3+col] + a[row*3+2]*b[6+col];
  return r;
}

public float[] multiply3x1(float[] m, float[] v) {
  return new float[] {
          m[0]*v[0] + m[1]*v[1] + m[2]*v[2],
          m[3]*v[0] + m[4]*v[1] + m[5]*v[2],
          m[6]*v[0] + m[7]*v[1] + m[8]*v[2]
  };
}
}
