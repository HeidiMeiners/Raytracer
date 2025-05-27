package TheRayTracer.lights;

import TheRayTracer.BVH.BoundingBox;
import TheRayTracer.Intersection;
import TheRayTracer.Ray;
import TheRayTracer.Vector3D;
import TheRayTracer.objects.Object3D;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class PointLight extends Light{
    private static final Random random = new Random();

    public PointLight(Vector3D position, Color color, double intensity) {
        super(position, color, intensity);
    }

    @Override
    public boolean isInShadow(Vector3D point, Vector3D normal, Object3D bvhRoot){
        Vector3D toLight = Vector3D.substract(getPosition(), point);
        double distanceToLight = Vector3D.magnitude(toLight);
        Vector3D direction = Vector3D.normalize(toLight);
        Vector3D origin = Vector3D.add(point, Vector3D.scalarMultiplication(normal, 0.0001));

        Ray shadowRay = new Ray(origin, direction);
        Intersection hit = bvhRoot.getIntersection(shadowRay);

        return hit != null && hit.getDistance() < distanceToLight;
    }


    @Override
    public double getNDotL(Intersection intersection) {
        return Math.max(Vector3D.dotProduct(intersection.getNormal(), Vector3D.scalarMultiplication(Vector3D.normalize(Vector3D.substract(intersection.getPosition(),getPosition())), -1)), 0.0);
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        return null;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }
}
