package TheRayTracer.objects;

import TheRayTracer.BVH.BoundingBox;
import TheRayTracer.Intersection;
import TheRayTracer.Ray;
import TheRayTracer.Vector3D;

import java.awt.*;

public class Sphere extends Object3D{
    private double radius;
    public Sphere(Vector3D position, double radius, Color color,double shininess,double specularCoefficient, double refractionCoefficient, double transparency) {
        super(position, color, shininess, specularCoefficient, refractionCoefficient, transparency);
        setRadius(radius);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        Vector3D L = Vector3D.substract(getPosition(), ray.getOrigin());
        double tca = Vector3D.dotProduct(ray.getDirection(), L);
        double d2 = Vector3D.dotProduct(L, L) - tca * tca;

        double radius2 = getRadius() * getRadius();
        if (d2 > radius2) return null;

        double thc = Math.sqrt(radius2 - d2);
        double t0 = tca - thc;
        double t1 = tca + thc;

        double distance = -1;
        if (t0 > .0001) {
            distance = t0;
        } else if (t1 > .0001) {
            distance = t1;
        } else {
            return null;
        }

        Vector3D position = Vector3D.add(ray.getOrigin(), Vector3D.scalarMultiplication(ray.getDirection(), distance));
        Vector3D normal = Vector3D.normalize(Vector3D.substract(position, getPosition()));
        return new Intersection(position, distance, normal, this);
    }

    @Override
    public BoundingBox getBoundingBox() {
        Vector3D min = new Vector3D(
                getPosition().getX() - radius,
                getPosition().getY() - radius,
                getPosition().getZ() - radius
        );
        Vector3D max = new Vector3D(
                getPosition().getX() + radius,
                getPosition().getY() + radius,
                getPosition().getZ() + radius
        );
        return new BoundingBox(min, max);
    }

}
