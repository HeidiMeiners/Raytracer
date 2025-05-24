package TheRayTracer;

import TheRayTracer.lights.DirectionalLight;
import TheRayTracer.lights.Light;
import TheRayTracer.lights.PointLight;
import TheRayTracer.objects.*;
import TheRayTracer.tools.OBJReader;
import TheRayTracer.tools.TransformObject;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Raytracer {
    public static void main(String[] args) {
        System.out.println(new Date());
        Scene scene01 = new Scene();
        TransformObject transform = new TransformObject();

        scene01.setCamera(new Camera(new Vector3D(0, 0, -4), 60, 60,
                500, 500, 0.6, 50.0));

        scene01.addLight(new PointLight(new Vector3D(0, 5, 2), Color.WHITE, 3));
        scene01.addLight(new PointLight(new Vector3D(2, 1, -3), Color.WHITE, 3));
        scene01.addLight(new PointLight(new Vector3D(-2, 1, -3), Color.WHITE, 3));
        //scene01.addLight(new DirectionalLight(new Vector3D(0,0,1), Color.white,.5));

        scene01.addObject(new Model3D(new Vector3D(0, -2, 0), new Triangle[]{
                new Triangle(new Vector3D(-5, 0, -5), new Vector3D(5, 0, -5), new Vector3D(5, 0, 5)),
                new Triangle(new Vector3D(-5, 0, -5), new Vector3D(5, 0, 5), new Vector3D(-5, 0, 5))},
                Color.LIGHT_GRAY,10,.1));
        scene01.addObject(new Sphere(new Vector3D(3, 3, 5), 1, Color.BLUE,80,1));
        scene01.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\Objects\\SmallTeapot.obj", new Vector3D(0, -2, 2), Color.red, 1.5, 0,50,0.5));
        //scene01.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\FirstScene\\Coffe_table.obj",new Vector3D(0, -3, 2),new Color(88,57,39)));
        //scene01.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\FirstScene\\glass.obj",new Vector3D(0,-2,2),Color.blue,1,0));
        //scene01.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\FirstScene\\CanOBJ.obj",new Vector3D(-3,-3,2),Color.gray));
        //scene01.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\FirstScene\\cupa.obj.obj",new Vector3D(3,3,2),Color.white));
        //scene01.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\FirstScene\\bolt.obj",new Vector3D(-3,3,-2),Color.gray));

        BufferedImage image = raytrace(scene01);
        File outputImage = new File("image.png");
        try {
            ImageIO.write(image, "png", outputImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(new Date());
    }

    public static BufferedImage raytrace(Scene scene) {
        Camera mainCamera = scene.getCamera();
        double[] nearFarPlanes = mainCamera.getNearFarPlanes();
        BufferedImage image = new BufferedImage(mainCamera.getResolutionWidth(), mainCamera.getResolutionHeight(), BufferedImage.TYPE_INT_RGB);
        List<Object3D> objects = scene.getObjects();
        List<Light> lights = scene.getLights();
        Vector3D[][] posRaytrace = mainCamera.calculatePositionsToRay();
        Vector3D pos = mainCamera.getPosition();
        double cameraZ = pos.getZ();
        ReflectionRefraction reflecRefrac = new ReflectionRefraction();
        int shadowSamples = 16;    // puedes ajustar
        double shadowRadius = 0.5;

        for (int i = 0; i < posRaytrace.length; i++) {
            for (int j = 0; j < posRaytrace[i].length; j++) {
                double x = posRaytrace[i][j].getX() + pos.getX();
                double y = posRaytrace[i][j].getY() + pos.getY();
                double z = posRaytrace[i][j].getZ() + pos.getZ();

                Ray ray = new Ray(mainCamera.getPosition(), new Vector3D(x, y, z));
                Intersection closestIntersection = raycast(ray, objects, null,
                        new double[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

                Color pixelColor = Color.black;
                if (closestIntersection != null) {
                    Color objColor = closestIntersection.getObject().getColor();
                    //ambient light
                    int redAmbient= (int)(objColor.getRed()*.1);
                    int greenAmbient= (int)(objColor.getGreen()*.1);
                    int blueAmbient= (int)(objColor.getBlue()*.1);
                    Color ambient=new Color(redAmbient,greenAmbient,blueAmbient);
                    pixelColor = addColor(pixelColor, ambient);
                    for (Light light : lights) {
                        if (light.isInShadow(closestIntersection.getPosition(), closestIntersection.getNormal(), objects)) {
                            continue;
                        }

                        double nDotL = light.getNDotL(closestIntersection);
                        Color lightColor = light.getColor();
                        double intensity;

                        if (light instanceof PointLight) {
                            double lightDistance = Vector3D.magnitude(Vector3D.substract(light.getPosition(), closestIntersection.getPosition()));
                            intensity = (light.getIntensity() / lightDistance) * nDotL;
                        } else {
                            intensity = light.getIntensity() * nDotL;
                        }

                        double[] lightColors = new double[]{lightColor.getRed() / 255.0, lightColor.getGreen() / 255.0, lightColor.getBlue() / 255.0};
                        double[] objColors = new double[]{objColor.getRed() / 255.0, objColor.getGreen() / 255.0, objColor.getBlue() / 255.0};
                        for (int colorIndex = 0; colorIndex < lightColors.length; colorIndex++) {
                            objColors[colorIndex] *= intensity * lightColors[colorIndex];
                        }

                        Color diffuse = new Color((float) Math.clamp(objColors[0], 0.0, 1.0),
                                (float) Math.clamp(objColors[1], 0.0, 1.0),
                                (float) Math.clamp(objColors[2], 0.0, 1.0));

                        Vector3D viewDir = Vector3D.normalize(Vector3D.substract(scene.getCamera().getPosition(), closestIntersection.getPosition()));
                        Vector3D lightDir = Vector3D.normalize(Vector3D.substract(light.getPosition(), closestIntersection.getPosition()));
                        Vector3D reflectDir = Vector3D.substract(Vector3D.scalarMultiplication(lightDir,-1), Vector3D.scalarMultiplication(closestIntersection.getNormal(),2 * Vector3D.dotProduct(closestIntersection.getNormal(), Vector3D.scalarMultiplication(lightDir,-1))));
                        double spec = Math.pow(Math.max(0.0, Vector3D.dotProduct(viewDir, reflectDir)), closestIntersection.getObject().getShininess()); // shininess = 100
                        double ks = closestIntersection.getObject().getSpecularCoefficient(); // coeficiente especular

                        Color specular = new Color(
                                (float)Math.clamp(ks * lightColors[0] * spec, 0.0, 1.0),
                                (float)Math.clamp(ks * lightColors[1] * spec, 0.0, 1.0),
                                (float)Math.clamp(ks * lightColors[2] * spec, 0.0, 1.0)
                        );

                        pixelColor = addColor(pixelColor, diffuse);
                        pixelColor = addColor(pixelColor, specular);
                    }
                    if (closestIntersection.getObject().getShininess()!=0 || closestIntersection.getObject().getSpecularCoefficient()!=0) {
                        Intersection reflectedObject =raycast(reflecRefrac.reflectionRay(closestIntersection,new Vector3D(x,y,z)), objects, null,
                                new double[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});
                        if (reflectedObject!=null){
                            Color reflectedColor=reflectedObject.getObject().getColor();
                            int finalColorRed=(int)(((1-closestIntersection.getObject().getSpecularCoefficient())*pixelColor.getRed())+(closestIntersection.getObject().getSpecularCoefficient()*reflectedColor.getRed()));
                            int finalColorGreen=(int)(((1-closestIntersection.getObject().getSpecularCoefficient())*pixelColor.getGreen())+(closestIntersection.getObject().getSpecularCoefficient()*reflectedColor.getGreen()));
                            int finalColorBlue=(int)(((1-closestIntersection.getObject().getSpecularCoefficient())*pixelColor.getBlue())+(closestIntersection.getObject().getSpecularCoefficient()*reflectedColor.getBlue()));
                            pixelColor = new Color(Math.min(255, Math.max(0, finalColorRed)),Math.min(255, Math.max(0, finalColorGreen)),Math.min(255, Math.max(0, finalColorBlue)));

                        }
                    }
                }
                image.setRGB(i, j, pixelColor.getRGB());
            }
        }

        return image;
    }

    public static Intersection raycast(Ray ray, List<Object3D> objects, Object3D caster, double[] clippingPlanes) {
        Intersection closestIntersection = null;

        for (int i = 0; i < objects.size(); i++) {
            Object3D currObj = objects.get(i);
            if (caster == null || !currObj.equals(caster)) {
                Intersection intersection = currObj.getIntersection(ray);
                if (intersection != null) {
                    double distance = intersection.getDistance();
                    double intersectionZ = intersection.getPosition().getZ();

                    if (distance >= 0 &&
                            (closestIntersection == null || distance < closestIntersection.getDistance()) &&
                            (clippingPlanes == null || (intersectionZ >= clippingPlanes[0] && intersectionZ <= clippingPlanes[1]))) {
                        closestIntersection = intersection;
                    }
                }
            }
        }

        return closestIntersection;
    }

    public static Color addColor(Color original, Color otherColor) {
        float red = (float) Math.clamp((original.getRed() / 255.0) + (otherColor.getRed() / 255.0), 0.0, 1.0);
        float green = (float) Math.clamp((original.getGreen() / 255.0) + (otherColor.getGreen() / 255.0), 0.0, 1.0);
        float blue = (float) Math.clamp((original.getBlue() / 255.0) + (otherColor.getBlue() / 255.0), 0.0, 1.0);
        return new Color(red, green, blue);
    }
}