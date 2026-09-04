import java.io.*;
import java.util.*;

/**
 * Converts a .obj file to raw data
 */
public class ObjLoader
{
public static float[] loadedVertices;
public static int[] loadedTriangleIndices;
public static float[] loadedTriangleUVs; // 2 floats per triangle: averaged (u, v)

public static void load(String filename) {
  List<float[]> vertexList = new ArrayList<>();
  List<float[]> uvList = new ArrayList<>();       // raw "vt u v" entries
  List<Integer> indexList = new ArrayList<>();    // position indices, triangulated
  List<Integer> uvIndexList = new ArrayList<>();  // matching uv indices, triangulated (-1 if none)

  try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();

      if (line.startsWith("v ")) {
        String[] parts = line.split("\\s+");
        vertexList.add(new float[]{
                Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]), 1.0f
        });
      }
      else if (line.startsWith("vt ")) {
        String[] parts = line.split("\\s+");
        uvList.add(new float[]{ Float.parseFloat(parts[1]), Float.parseFloat(parts[2]) });
      }
      else if (line.startsWith("f ")) {
        String[] parts = line.split("\\s+");
        int cornerCount = parts.length - 1;
        int[] posIdx = new int[cornerCount];
        int[] uvIdx = new int[cornerCount];

        for (int i = 1; i < parts.length; i++) {
          String[] tokens = parts[i].split("/");

          posIdx[i - 1] = resolveIndex(Integer.parseInt(tokens[0]), vertexList.size());

          uvIdx[i - 1] = (tokens.length >= 2 && !tokens[1].isEmpty())
                  ? resolveIndex(Integer.parseInt(tokens[1]), uvList.size())
                  : -1;
        }

        for (int i = 1; i < cornerCount - 1; i++) {
          indexList.add(posIdx[0]);   indexList.add(posIdx[i]);   indexList.add(posIdx[i + 1]);
          uvIndexList.add(uvIdx[0]);  uvIndexList.add(uvIdx[i]);  uvIndexList.add(uvIdx[i + 1]);
        }
      }
    }
  } catch (IOException e) {
    System.out.println("Failed to load OBJ file: " + filename);
    e.printStackTrace();
    loadedVertices = new float[0];
    loadedTriangleIndices = new int[0];
    loadedTriangleUVs = new float[0];
    return;
  }

  // Flatten positions
  loadedVertices = new float[vertexList.size() * 4];
  for (int i = 0; i < vertexList.size(); i++) {
    float[] v = vertexList.get(i);
    System.arraycopy(v, 0, loadedVertices, i * 4, 4);
  }

  // Flatten triangle position indices
  loadedTriangleIndices = new int[indexList.size()];
  for (int i = 0; i < indexList.size(); i++) {
    loadedTriangleIndices[i] = indexList.get(i);
  }

  // Compute ONE averaged UV per triangle (Option 2: flat per-triangle sample)
  int triCount = indexList.size() / 3;
  loadedTriangleUVs = new float[triCount * 2];
  for (int t = 0; t < triCount; t++) {
    float sumU = 0, sumV = 0;
    int validCorners = 0;

    for (int c = 0; c < 3; c++) {
      int uvIdx = uvIndexList.get(t * 3 + c);
      if (uvIdx >= 0 && uvIdx < uvList.size()) {
        sumU += uvList.get(uvIdx)[0];
        sumV += uvList.get(uvIdx)[1];
        validCorners++;
      }
    }

    if (validCorners > 0) {
      loadedTriangleUVs[t * 2]     = sumU / validCorners;
      loadedTriangleUVs[t * 2 + 1] = sumV / validCorners;
    } else {
      loadedTriangleUVs[t * 2]     = 0.5f; // no UV data on this face — sample texture center
      loadedTriangleUVs[t * 2 + 1] = 0.5f;
    }
  }
}

// Resolves an OBJ index (1-based positive, or negative/relative) into a
// 0-based index into the list as it stands at this point in the file.
private static int resolveIndex(int rawIndex, int currentListSize) {
  if (rawIndex > 0) {
    return rawIndex - 1;             // absolute, 1-based -> 0-based
  } else {
    return currentListSize + rawIndex; // negative, relative to current count
  }
}
}