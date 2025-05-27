package TheRayTracer.tools;

import TheRayTracer.Intersection;
import TheRayTracer.Ray;
import TheRayTracer.Vector3D;
import TheRayTracer.lights.Light;
import TheRayTracer.objects.Camera;
import TheRayTracer.objects.Object3D;

import java.awt.*;
import java.util.List;

import static TheRayTracer.Raytracer.objectColor;

public class ReflectionRefraction {

    private Ray reflectionRay(Intersection intersection, Vector3D direction) { //Here we get the new ray from the object that is going to reflect
        Vector3D newRayDirection=Vector3D.substract(Vector3D.normalize(direction),Vector3D.scalarMultiplication(Vector3D.scalarMultiplication(intersection.getNormal(),2),Vector3D.dotProduct(Vector3D.normalize(direction),intersection.getNormal())));
        Vector3D origin = Vector3D.add(intersection.getPosition(), Vector3D.scalarMultiplication(intersection.getNormal(), 0.0001));
        return new Ray(origin,newRayDirection);
    }

    //here we get the final color from the pixel, and it is  a recursive methode, that makes us capable of do multiple reflections
    public Color reflectionColor(Intersection intersection, Vector3D direction, int depth,
                                 Object3D bvhRoot, Camera camera, Color pixelColor, List<Light> lights) {
        if (depth == 0) {
            return pixelColor;
        }

        Ray newRay = reflectionRay(intersection, direction);

        Intersection reflectedObject = rayCaster(newRay, bvhRoot);
        if (reflectedObject == null) return pixelColor;

        Color reflectedColor = objectColor(reflectedObject, Color.BLACK, bvhRoot, lights, camera);
        reflectedColor = reflectionColor(reflectedObject, newRay.getDirection(), depth - 1, bvhRoot, camera, reflectedColor, lights);

        int finalColorRed = (int) (((1 - intersection.getObject().getSpecularCoefficient()) * pixelColor.getRed())
                + (intersection.getObject().getSpecularCoefficient() * reflectedColor.getRed()));
        int finalColorGreen = (int) (((1 - intersection.getObject().getSpecularCoefficient()) * pixelColor.getGreen())
                + (intersection.getObject().getSpecularCoefficient() * reflectedColor.getGreen()));
        int finalColorBlue = (int) (((1 - intersection.getObject().getSpecularCoefficient()) * pixelColor.getBlue())
                + (intersection.getObject().getSpecularCoefficient() * reflectedColor.getBlue()));

        return new Color(
                Math.min(255, Math.max(0, finalColorRed)),
                Math.min(255, Math.max(0, finalColorGreen)),
                Math.min(255, Math.max(0, finalColorBlue))
        );
    }

    public Color refraction(Intersection intersection, Vector3D incidentDir,
                            Object3D bvhRoot, Camera camera, Color pixelColor, int depth, List<Light> lights) {
        if (depth == 0) {
            return pixelColor;
        }

        Vector3D N = intersection.getNormal();
        Vector3D I = Vector3D.normalize(incidentDir);

        double n1 = 1.0;
        double n2 = intersection.getObject().getRefractionCoefficient();

        double cosi = Vector3D.dotProduct(I, N);
        if (cosi < 0) {
            cosi = -cosi;
        } else {
            N = Vector3D.scalarMultiplication(N, -1);
            double tmp = n1;
            n1 = n2;
            n2 = tmp;
        }

        double eta = n1 / n2;
        double k = 1 - eta * eta * (1 - cosi * cosi);
        if (k < 0) {
            return pixelColor;
        }

        Vector3D refractedDirection = Vector3D.add(
                Vector3D.scalarMultiplication(I, eta),
                Vector3D.scalarMultiplication(N, eta * cosi - Math.sqrt(k))
        );
        refractedDirection = Vector3D.normalize(refractedDirection);

        Vector3D origin = Vector3D.add(intersection.getPosition(), Vector3D.scalarMultiplication(refractedDirection, 1e-4));
        Ray refractedRay = new Ray(origin, refractedDirection);

        Intersection nextHit = rayCaster(refractedRay, bvhRoot);
        Color refractedColor;

        if (nextHit == null) {
            refractedColor = Color.BLACK;
        } else {
            Color objColor;
            if (nextHit.getObject().getShininess() != 0) {
                objColor = reflectionColor(nextHit, refractedDirection, 2, bvhRoot, camera, objectColor(nextHit, Color.BLACK, bvhRoot, lights, camera), lights);
            } else {
                objColor = objectColor(nextHit, Color.BLACK, bvhRoot, lights, camera);
            }
            refractedColor = refraction(nextHit, refractedRay.getDirection(), bvhRoot, camera, objColor, depth - 1, lights);
        }

        double t = intersection.getObject().getTransparency();
        int r = (int) ((1 - t) * pixelColor.getRed() + t * refractedColor.getRed());
        int g = (int) ((1 - t) * pixelColor.getGreen() + t * refractedColor.getGreen());
        int b = (int) ((1 - t) * pixelColor.getBlue() + t * refractedColor.getBlue());

        return new Color(
                Math.min(255, Math.max(0, r)),
                Math.min(255, Math.max(0, g)),
                Math.min(255, Math.max(0, b))
        );
    }

    private Intersection rayCaster(Ray ray, Object3D bvhRoot) {
        return bvhRoot.getIntersection(ray);
    }
}
