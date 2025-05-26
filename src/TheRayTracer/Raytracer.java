package TheRayTracer;

import TheRayTracer.lights.Light;
import TheRayTracer.lights.PointLight;
import TheRayTracer.objects.*;
import TheRayTracer.tools.OBJReader;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class Raytracer {
    public static void main(String[] args) {
        System.out.println(new Date());
        Scene scene01 = new Scene();
        Scene scene02 = new Scene();

        scene01.setCamera(new Camera(new Vector3D(0, 0, -4), 60, 55, 300, 300, .6, 50));
        scene02.setCamera(new Camera(new Vector3D(0, 0, -4), 60, 55, 500, 300, .6, 50));

        scene01.addLight(new PointLight(new Vector3D(0, 3, 4), Color.WHITE, 3));
        scene01.addLight(new PointLight(new Vector3D(2, 1, -3), Color.WHITE, 3));
        scene01.addLight(new PointLight(new Vector3D(-2, 1, -3), Color.WHITE, 3));

        scene02.addLight(new PointLight(new Vector3D(0, 3, -3), Color.WHITE, 3));
        scene02.addLight(new PointLight(new Vector3D(0, 3, 2), Color.orange, 3));

        scene01.addObject(new Model3D(new Vector3D(0, -2, 0), new Triangle[]{
                new Triangle(new Vector3D(-20, 0, -20), new Vector3D(20, 0, -20), new Vector3D(20, 0, 20)),
                new Triangle(new Vector3D(-20, 0, -20), new Vector3D(20, 0, 20), new Vector3D(-12, 0, 20))},
                Color.LIGHT_GRAY,10,.1,0,0));
        scene01.addObject(new Model3D(new Vector3D(0, 0, 10), new Triangle[]{
                new Triangle(new Vector3D(-20, -20, 0), new Vector3D(20, -20, 0), new Vector3D(20,20, 0)),
                new Triangle(new Vector3D(-20, -20, 0), new Vector3D(20, 20, 0), new Vector3D(-20, 20, 0))},
                Color.lightGray,100,1,0,0));
        scene01.addObject(new Sphere(new Vector3D( 1.0, -1.5,  0), .5, Color.white,100,1,1.5,.9));
        scene01.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\Raytracer\\src\\TheRayTracer\\Scene01Objects\\glass.obj",new Vector3D(0.0, -2,  2),Color.blue,1,0,10,.1,1.5,.9));
        scene01.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\Raytracer\\src\\TheRayTracer\\Scene01Objects\\CanOBJ.obj",new Vector3D(2.5, -2,  2),Color.darkGray,1,0,80,.8,0,0));
        scene01.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\Raytracer\\src\\TheRayTracer\\Scene01Objects\\Cup.obj",new Vector3D(-2.5, -2,  2.5),new Color(210,200,180),.5,0,1,0,0,0));
        scene01.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\Raytracer\\src\\TheRayTracer\\Scene01Objects\\Wrench_OBJ.obj",new Vector3D(-1, -1.9,  0),Color.gray,1,30,80,.8,0,0));

        scene02.addObject(new Model3D(new Vector3D(0, -2, 0), new Triangle[]{
                new Triangle(new Vector3D(-20, 0, -20), new Vector3D(20, 0, -20), new Vector3D(20, 0, 20)),
                new Triangle(new Vector3D(-20, 0, -20), new Vector3D(20, 0, 20), new Vector3D(-12, 0, 20))},
                Color.LIGHT_GRAY,10,.1,0,0));
        scene02.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\Raytracer\\src\\TheRayTracer\\Scene02Objects\\OBJ.obj",new Vector3D(-1, -1.9,  3),new Color(255, 215, 0),.3,90,80,.8,0,0));
        scene02.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\Raytracer\\src\\TheRayTracer\\Scene02Objects\\Cube.obj",new Vector3D(1, -1.9,  0),Color.white,1,0,10,.1,1.5,.9));
        scene02.addObject(OBJReader.getModel3D("C:\\ISGC\\4_Semestre_ISGC\\Graficas_computacionales\\3Parcial\\Raytracer\\src\\TheRayTracer\\Scene02Objects\\Cube.obj",new Vector3D(-1, 1,  0),Color.white,1,0,10,.1,1.5,.9));




        BufferedImage image = raytrace(scene02);
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
        BufferedImage image = new BufferedImage(
                mainCamera.getResolutionWidth(),
                mainCamera.getResolutionHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        List<Object3D> objects = scene.getObjects();
        List<Light> lights = scene.getLights();
        Vector3D[][] posRaytrace = mainCamera.calculatePositionsToRay();
        Vector3D pos = mainCamera.getPosition();
        double cameraZ = pos.getZ();
        ReflectionRefraction reflecRefrac = new ReflectionRefraction();

        int width   = posRaytrace.length;
        int height  = posRaytrace[0].length;
        int block   = 100;  // puedes ajustar (p.ej. 32, 64, 100)
        int nBlocks = (width + block - 1)/block;

        IntStream.range(0, nBlocks).parallel().forEach(t -> {
            int startX = t*block;
            int endX   = Math.min(width, startX + block);
            for (int i = startX; i < endX; i++) {
                for (int j = 0; j < height; j++) {
                    double x = posRaytrace[i][j].getX() + pos.getX();
                    double y = posRaytrace[i][j].getY() + pos.getY();
                    double z = posRaytrace[i][j].getZ() + pos.getZ();

                    Ray ray = new Ray(mainCamera.getPosition(), new Vector3D(x, y, z));
                    Intersection closestIntersection = raycast(ray, objects, null, new double[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

                    Color pixelColor = Color.black;
                    if (closestIntersection != null) {
                        Color reflected = Color.BLACK;
                        Color refracted = Color.BLACK;
                        Color objColor = closestIntersection.getObject().getColor();
                        Color ogColor = objectColor(closestIntersection, pixelColor, objects, lights, mainCamera);

                        // reflexión
                        if (closestIntersection.getObject().getShininess() != 0 || closestIntersection.getObject().getSpecularCoefficient() != 0) {
                            reflected = reflecRefrac.reflectionColor(closestIntersection, ray.getDirection(), 3, objects, mainCamera, objColor, lights);
                        }
                        // refraction
                        if (closestIntersection.getObject().getTransparency() != 0 || closestIntersection.getObject().getRefractionCoefficient() != 0) {
                            refracted = reflecRefrac.refraction(closestIntersection, ray.getDirection(), objects, mainCamera, objColor, 3, lights);
                        }

                        // mezcla final
                        double kr = closestIntersection.getObject().getSpecularCoefficient();
                        double kt = closestIntersection.getObject().getTransparency();
                        double kd = 1.0 - kr - kt;

                        int r = (int) (kd * ogColor.getRed() + kr * reflected.getRed() + kt * refracted.getRed());
                        int g = (int) (kd * ogColor.getGreen() + kr * reflected.getGreen() + kt * refracted.getGreen());
                        int b = (int) (kd * ogColor.getBlue() + kr * reflected.getBlue() + kt * refracted.getBlue());

                        r = Math.min(255, Math.max(0, r));
                        g = Math.min(255, Math.max(0, g));
                        b = Math.min(255, Math.max(0, b));

                        pixelColor = new Color(r, g, b);
                    }
                    image.setRGB(i, j, pixelColor.getRGB());
                }
            }
        });
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

    public static Color objectColor(Intersection closestIntersection, Color pixelColor, List<Object3D> objects, List<Light> lights, Camera camera) {
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

            Vector3D viewDir = Vector3D.normalize(Vector3D.substract(camera.getPosition(), closestIntersection.getPosition()));
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
        return pixelColor;
    }
}