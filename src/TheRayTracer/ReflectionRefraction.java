package TheRayTracer;

import java.awt.*;

public class ReflectionRefraction {
    public Ray reflectionRay(Intersection intersection,Vector3D direction) {
        Vector3D newRayDirection=Vector3D.substract(Vector3D.normalize(direction),Vector3D.scalarMultiplication(Vector3D.scalarMultiplication(intersection.getNormal(),2),Vector3D.dotProduct(Vector3D.normalize(direction),intersection.getNormal())));
        Vector3D origin = Vector3D.add(intersection.getPosition(), Vector3D.scalarMultiplication(intersection.getNormal(), 0.0001));
        return new Ray(origin,newRayDirection);
    }
}
