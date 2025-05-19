package TheRayTracer.lights;

import TheRayTracer.objects.Object3D;
import TheRayTracer.Vector3D;
import TheRayTracer.Intersection;
import java.util.List;

import java.awt.*;

public abstract class Light extends Object3D {
    private double intensity;

    public Light(Vector3D position, Color color, double intensity) {
        super(position, color);
        setIntensity(intensity);
    }

    public abstract boolean isInShadow(Vector3D point, Vector3D normal, List<Object3D> objects);

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public abstract double getNDotL(Intersection intersection);
}
