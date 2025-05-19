package TheRayTracer.objects;

import TheRayTracer.Intersection;
import TheRayTracer.Ray;
import TheRayTracer.Vector3D;

public interface IIntersectable {
    public abstract Intersection getIntersection(Ray ray);
}
