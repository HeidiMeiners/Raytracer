package TheRayTracer.lights;

import TheRayTracer.Intersection;
import TheRayTracer.Ray;
import TheRayTracer.Vector3D;

import java.awt.*;

public class PointLight extends Light{
    public PointLight(Vector3D position, Color color, double intensity) {
        super(position, color, intensity);

    }

    @Override
    public double getNDotL(Intersection intersection) {
        return Math.max(Vector3D.dotProduct(intersection.getNormal(), Vector3D.scalarMultiplication(Vector3D.normalize(Vector3D.substract(intersection.getPosition(),getPosition())), -1.0)), 0.0);
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        return null;
    }
}
