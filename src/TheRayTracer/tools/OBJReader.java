package TheRayTracer.tools;

import TheRayTracer.Vector3D;
import TheRayTracer.objects.Model3D;
import TheRayTracer.objects.Triangle;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class OBJReader {

    public static Model3D getModel3D(String path, Vector3D origin, Color color,double scale,double angle, double shininess, double specularCoefficient, double refractionCoefficient, double transparency) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            List<Triangle> triangles = new ArrayList<>();
            List<Vector3D> vertices = new ArrayList<>();
            List<Vector3D> normals = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("v ") || line.startsWith("vn ")) {
                    String[] components = line.split("\\s+");
                    if (components.length >= 4) {
                        double x = Double.parseDouble(components[1])*scale;
                        double y = Double.parseDouble(components[2])*scale;
                        double z = Double.parseDouble(components[3])*scale;
                        Vector3D vec = new Vector3D(x, y, z);
                        if (line.startsWith("v ")) {
                            vertices.add(vec);
                        } else {
                            normals.add(vec);
                        }
                    }
                } else if (line.startsWith("f ")) {
                    String[] faceComponents = line.split("\\s+");
                    List<Vector3D> triangleVertices = new ArrayList<>();
                    List<Vector3D> triangleNormals = new ArrayList<>();

                    for (int i = 1; i < faceComponents.length; i++) {
                        String[] parts = faceComponents[i].split("/");
                        if (parts.length >= 3) {
                            int vertexIdx = Integer.parseInt(parts[0]) - 1;
                            int normalIdx = Integer.parseInt(parts[2]) - 1;
                            triangleVertices.add(vertices.get(vertexIdx));
                            triangleNormals.add(normals.get(normalIdx));
                        }
                    }

                    // Trianlge 1
                    if (triangleVertices.size() >= 3) {
                        Triangle tri1 = new Triangle(
                                triangleVertices.get(0),
                                triangleVertices.get(1),
                                triangleVertices.get(2)
                        );
                        tri1.setNormals(new Vector3D[]{
                                triangleNormals.get(0),
                                triangleNormals.get(1),
                                triangleNormals.get(2)
                        });
                        triangles.add(tri1);

                        // Triangle 2 (only if quad)
                        if (triangleVertices.size() == 4) {
                            Triangle tri2 = new Triangle(
                                    triangleVertices.get(0),
                                    triangleVertices.get(2),
                                    triangleVertices.get(3)
                            );
                            tri2.setNormals(new Vector3D[]{
                                    triangleNormals.get(0),
                                    triangleNormals.get(2),
                                    triangleNormals.get(3)
                            });
                            triangles.add(tri2);
                        }
                    }
                }
            }
            reader.close();
            rotateObjectY(triangles.toArray(new Triangle[triangles.size()]),angle);
            return new Model3D(origin, triangles.toArray(new Triangle[triangles.size()]), color, shininess, specularCoefficient, refractionCoefficient, transparency);
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return null;
    }

    private static void rotateObjectY(Triangle[] triangles, double angleDegrees) {
        double angle = Math.toRadians(angleDegrees);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        for (Triangle triangle : triangles) {
            Vector3D[] vertices = triangle.getVertices();
            Vector3D[] normals = triangle.getNormals();

            for (int i = 0; i < 3; i++) {
                double x = vertices[i].getX();
                double z = vertices[i].getZ();
                vertices[i] = new Vector3D(
                        x * cos + z * sin,
                        vertices[i].getY(),
                        -x * sin + z * cos
                );

                double nx = normals[i].getX();
                double nz = normals[i].getZ();
                normals[i] = Vector3D.normalize(new Vector3D(
                        nx * cos + nz * sin,
                        normals[i].getY(),
                        -nx * sin + nz * cos
                ));
            }

            triangle.setVertices(vertices);
            triangle.setNormals(normals);
        }
    }

}