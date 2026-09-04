public class MatrixMath
{

// Bunch of complicated matrix math that we put in its own class

public void matrixRotation(float[] matrix, float rotX, float rotY, float rotZ) {
  float x = matrix[0], y = matrix[1], z = matrix[2];

  float radY = (float) Math.toRadians(rotY);
  float cosY = (float) Math.cos(radY), sinY = (float) Math.sin(radY);
  float x1 = x * cosY + z * sinY;
  float y1 = y;
  float z1 = -x * sinY + z * cosY;

  float radX = (float) Math.toRadians(rotX);
  float cosX = (float) Math.cos(radX), sinX = (float) Math.sin(radX);
  float x2 = x1;
  float y2 = y1 * cosX - z1 * sinX;
  float z2 = y1 * sinX + z1 * cosX;

  float radZ = (float) Math.toRadians(rotZ);
  float cosZ = (float) Math.cos(radZ), sinZ = (float) Math.sin(radZ);
  float x3 = x2 * cosZ - y2 * sinZ;
  float y3 = x2 * sinZ + y2 * cosZ;

  matrix[0] = x3;
  matrix[1] = y3;
  matrix[2] = z2;
}

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

public float[] rotationX3x3(float degrees) {
  float rad = (float) Math.toRadians(degrees);
  float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
  return new float[] { 1,0,0,  0,c,-s,  0,s,c };
}

public float[] rotationY3x3(float degrees) {
  float rad = (float) Math.toRadians(degrees);
  float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
  return new float[] { c,0,s,  0,1,0,  -s,0,c };
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
