package TheRayTracer.lights;

import TheRayTracer.BVH.BVHNode;
import TheRayTracer.BVH.BoundingBox;
import TheRayTracer.Intersection;
import TheRayTracer.Ray;
import TheRayTracer.Vector3D;
import TheRayTracer.objects.Object3D;
import TheRayTracer.objects.Triangle;

import java.awt.*;
import java.util.List;

public class DirectionalLight extends Light{
    public Vector3D direction;

    public DirectionalLight(Vector3D direction, Color color, double intensity) {
        super(Vector3D.ZERO(), color, intensity);
        setDirection(direction);
    }

    @Override
    public double getNDotL(Intersection intersection) {
            return Math.max(Vector3D.dotProduct(intersection.getNormal(), Vector3D.scalarMultiplication(getDirection(), -1.0)), 0.0);
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        return null;
    }

    public Vector3D getDirection() {
        return direction;
    }

    public void setDirection(Vector3D direction) {
        this.direction = Vector3D.normalize(direction);
    }

    @Override
    public boolean isInShadow(Vector3D point, Vector3D normal, Object3D bvhRoot) {
        return false;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }
}
