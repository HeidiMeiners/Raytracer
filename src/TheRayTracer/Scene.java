package TheRayTracer;

import TheRayTracer.BVH.BVHNode;
import TheRayTracer.lights.Light;
import TheRayTracer.objects.Object3D;
import TheRayTracer.objects.Camera;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private Camera camera;
    private List<Object3D> objects;
    private List<Light> lights;
    private BVHNode bvhRoot = null;

    public Scene() {
        setObjects(new ArrayList<>());
        setLights(new ArrayList<>());
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void addObject(Object3D object){
        getObjects().add(object);
    }

    public List<Object3D> getObjects() {
        if(objects == null){
            objects = new ArrayList<>();
        }
        return objects;
    }

    public void setObjects(List<Object3D> objects) {
        this.objects = objects;
    }

    public List<Light> getLights() {
        if(lights == null){
            lights = new ArrayList<>();
        }
        return lights;
    }

    public void setLights(List<Light> lights) {
        this.lights = lights;
    }

    public void addLight(Light light){
        getLights().add(light);
    }

    public void buildBVH() {
        this.bvhRoot = new BVHNode(objects);
    }

    public BVHNode getBVHRoot() {
        return bvhRoot;
    }
}