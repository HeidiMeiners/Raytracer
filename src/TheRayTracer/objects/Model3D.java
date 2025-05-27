package TheRayTracer.objects;

import TheRayTracer.BVH.BoundingBox;
import TheRayTracer.Intersection;
import TheRayTracer.Ray;
import TheRayTracer.Vector3D;

import java.awt.*;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model3D extends Object3D{
    private List<Triangle> triangles;

    public Model3D(Vector3D position, Triangle[] triangles, Color color, double shininess, double spectacularCoefficient, double refractionCoefficient, double transparency) {
        super(position, color, shininess, spectacularCoefficient, refractionCoefficient, transparency);
        setTriangles(triangles);
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public void setTriangles(Triangle[] triangles) {
        Vector3D position = getPosition();
        Set<Vector3D> uniqueVertices = new HashSet<>();
        for(Triangle triangle : triangles){
            uniqueVertices.addAll(Arrays.asList(triangle.getVertices()));
        }

        for(Vector3D vertex : uniqueVertices){
            vertex.setX(vertex.getX() + position.getX());
            vertex.setY(vertex.getY() + position.getY());
            vertex.setZ(vertex.getZ() + position.getZ());
        }
        this.triangles = Arrays.asList(triangles);
    }


    @Override
    public Intersection getIntersection(Ray ray) {
        double distance = -1;
        Vector3D position = getPosition();
        Vector3D normal = Vector3D.ZERO();

        for(Triangle triangle : getTriangles()){
            Intersection intersection = triangle.getIntersection(ray);
            double intersectionDistance = intersection.getDistance();
            if(intersectionDistance > 0 &&
                    (intersectionDistance < distance || distance < 0)){
                distance = intersectionDistance;
                position = Vector3D.add(ray.getOrigin(), Vector3D.scalarMultiplication(ray.getDirection(), distance));
                normal = triangle.getNormal(position);
            }
        }

        if(distance == -1){
            return null;
        }

        return new Intersection(position, distance, normal, this);
    }

    @Override
    public BoundingBox getBoundingBox() {
        Triangle[] trianglesArray = triangles.toArray(new Triangle[0]);
        if (trianglesArray.length == 0) return null;
        BoundingBox boundingBox = trianglesArray[0].getBoundingBox();
        for (int i = 1; i < trianglesArray.length; i++) {
            boundingBox = BoundingBox.combine(boundingBox, trianglesArray[i].getBoundingBox());
        }
        return boundingBox;
    }

}
