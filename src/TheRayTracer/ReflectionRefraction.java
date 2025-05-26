package TheRayTracer;

import TheRayTracer.lights.Light;
import TheRayTracer.objects.Camera;
import TheRayTracer.objects.Object3D;

import java.awt.*;
import java.util.List;

import static TheRayTracer.Raytracer.objectColor;
import static TheRayTracer.Raytracer.raycast;

public class ReflectionRefraction {

    private Ray reflectionRay(Intersection intersection,Vector3D direction) { //Here we get the new ray from the object that is going to reflect
        Vector3D newRayDirection=Vector3D.substract(Vector3D.normalize(direction),Vector3D.scalarMultiplication(Vector3D.scalarMultiplication(intersection.getNormal(),2),Vector3D.dotProduct(Vector3D.normalize(direction),intersection.getNormal())));
        Vector3D origin = Vector3D.add(intersection.getPosition(), Vector3D.scalarMultiplication(intersection.getNormal(), 0.0001));
        return new Ray(origin,newRayDirection);
    }

    //here we get the final color from the pixel, and it is  a recursive methode, that makes us capable of do multiple reflections
    public Color reflectionColor(Intersection intersection, Vector3D direction, int depth, List<Object3D> objects, Camera camera, Color pixelColor,List<Light> lights) {
        if (depth==0){ //the end of the recursive method
            return pixelColor;
        }

        double[] nearFarPlanes = camera.getNearFarPlanes();
        Ray newRay = reflectionRay(intersection, direction);//the new ray

        Intersection reflectedObject = raycast(newRay, objects, null,
                new double[]{camera.getPosition().getZ() + nearFarPlanes[0], camera.getPosition().getZ() + nearFarPlanes[1]});

        if (reflectedObject==null){
            return pixelColor;
        }

        Color reflectedColor=objectColor(reflectedObject,Color.black,objects,lights,camera); //the reflected color
        reflectedColor=reflectionColor(reflectedObject,newRay.getDirection(),depth-1,objects,camera,reflectedColor,lights); //here we recall the method
        //and here we add to the original color the reflection color, that gives us the final color
        int finalColorRed=(int)(((1-intersection.getObject().getSpecularCoefficient())*pixelColor.getRed())+(intersection.getObject().getSpecularCoefficient()*reflectedColor.getRed()));
        int finalColorGreen=(int)(((1-intersection.getObject().getSpecularCoefficient())*pixelColor.getGreen())+(intersection.getObject().getSpecularCoefficient()*reflectedColor.getGreen()));
        int finalColorBlue=(int)(((1-intersection.getObject().getSpecularCoefficient())*pixelColor.getBlue())+(intersection.getObject().getSpecularCoefficient()*reflectedColor.getBlue()));
        pixelColor = new Color(Math.min(255, Math.max(0, finalColorRed)),Math.min(255, Math.max(0, finalColorGreen)),Math.min(255, Math.max(0, finalColorBlue)));


        return pixelColor;
    }

    public Color refraction(Intersection intersection, Vector3D incidentDir, List<Object3D> objects, Camera camera, Color pixelColor, int depth, List<Light> lights) {
        if (depth == 0) { //end of the recursion
            return pixelColor;
        }

        Vector3D N = intersection.getNormal();
        Vector3D I = Vector3D.normalize(incidentDir);

        double n1 = 1.0; // air
        double n2 = intersection.getObject().getRefractionCoefficient(); //object

        double cosi = Vector3D.dotProduct(I, N);
        if (cosi < 0) {
            cosi = -cosi;
        } else {
            N = Vector3D.scalarMultiplication(N, -1);
            double tmp = n1;
            n1 = n2;
            n2 = tmp;
        }

        // factor k
        double eta = n1 / n2;
        double k = 1 - eta * eta * (1 - cosi * cosi);

        if (k < 0) {
            return pixelColor;
        }

        // new direction
        Vector3D refractedDirection = Vector3D.add(Vector3D.scalarMultiplication(I, eta), Vector3D.scalarMultiplication(N, eta * cosi - Math.sqrt(k)));
        refractedDirection = Vector3D.normalize(refractedDirection);

        // this make the origin stay out of the original intersection
        Vector3D origin = Vector3D.add(intersection.getPosition(), Vector3D.scalarMultiplication(refractedDirection, 1e-4));
        Ray refractedRay = new Ray(origin, refractedDirection);

        // Raycast of the new ray
        double[] nearFarPlanes = camera.getNearFarPlanes();
        Intersection nextHit = Raytracer.raycast(refractedRay, objects, null, new double[]{camera.getPosition().getZ() + nearFarPlanes[0], camera.getPosition().getZ() + nearFarPlanes[1]});

        Color refractedColor;
        if (nextHit == null) {//if it does not hit anything we take the background color
            refractedColor = Color.BLACK;
        } else {
            Color objColor;
            if (nextHit.getObject().getShininess()!=0){
                objColor = reflectionColor(nextHit,refractedDirection,2,objects,camera,objectColor(nextHit,Color.BLACK,objects,lights,camera),lights);
            }
            else {
                objColor = objectColor(nextHit,Color.BLACK,objects,lights,camera);
            }
            refractedColor = refraction(nextHit, refractedRay.getDirection(), objects, camera, objColor, depth - 1,lights);
        }

        // mix the original color and the refracted color
        double t = intersection.getObject().getTransparency();
        int r = (int)((1 - t) * pixelColor.getRed()   + t * refractedColor.getRed());
        int g = (int)((1 - t) * pixelColor.getGreen() + t * refractedColor.getGreen());
        int b = (int)((1 - t) * pixelColor.getBlue()  + t * refractedColor.getBlue());

        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));

        return new Color(r, g, b);
    }
}
