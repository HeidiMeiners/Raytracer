package TheRayTracer.objects;

import TheRayTracer.BVH.BoundingBox;
import TheRayTracer.Vector3D;
import TheRayTracer.Intersection;
import TheRayTracer.Ray;

import java.awt.*;

public class Camera extends Object3D {
    //FOV[0] = Horizontal | FOV[1] = Vertical
    private double[] fieldOfView = new double[2];
    private double defaultZ = 15.0;
    private int[] resolution = new int[2];
    private double[] nearFarPlanes = new double[2];

    public Camera(Vector3D position, double fovH, double fovV,
                  int width, int height, double nearPlane, double farPlane) {
        super(position, Color.BLACK,0,0,0,0);
        setFOV(fovH, fovV);
        setResolution(width, height);
        setNearFarPlanes(new double[]{nearPlane, farPlane});
    }

    public double[] getFieldOfView() {
        return fieldOfView;
    }

    private void setFieldOfView(double[] fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    public double getFOVHorizontal() {
        return fieldOfView[0];
    }

    public double getFOVVertical() {
        return fieldOfView[1];
    }

    public void setFOVHorizontal(double fovH) {
        fieldOfView[0] = fovH;
    }

    public void setFOVVertical(double fovV) {
        fieldOfView[1] = fovV;
    }

    public void setFOV(double fovH, double fovV) {
        setFOVHorizontal(fovH);
        setFOVVertical(fovV);
    }

    public double getDefaultZ() {
        return defaultZ;
    }

    public void setDefaultZ(double defaultZ) {
        this.defaultZ = defaultZ;
    }

    public int[] getResolution() {
        return resolution;
    }

    public void setResolutionWidth(int width) {
        resolution[0] = width;
    }

    public void setResolutionHeight(int height) {
        resolution[1] = height;
    }

    public void setResolution(int width, int height) {
        setResolutionWidth(width);
        setResolutionHeight(height);
    }

    public int getResolutionWidth() {
        return resolution[0];
    }

    public int getResolutionHeight() {
        return resolution[1];
    }

    private void setResolution(int[] resolution) {
        this.resolution = resolution;
    }

    public double[] getNearFarPlanes() {
        return nearFarPlanes;
    }

    private void setNearFarPlanes(double[] nearFarPlanes) {
        this.nearFarPlanes = nearFarPlanes;
    }


    public Vector3D[][] calculatePositionsToRay() {
        int width = getResolutionWidth();
        int height = getResolutionHeight();
        double aspectRatio = (double) width / height;

        double fovRadians = Math.toRadians(getFOVVertical());
        double imagePlaneHeight = 2 * Math.tan(fovRadians / 2) * defaultZ;
        double imagePlaneWidth = imagePlaneHeight * aspectRatio;

        double pixelWidth = imagePlaneWidth / width;
        double pixelHeight = imagePlaneHeight / height;

        double minX = -imagePlaneWidth / 2.0;
        double maxY = imagePlaneHeight / 2.0;

        Vector3D[][] positions = new Vector3D[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double posX = minX + pixelWidth * (x + 0.5);
                double posY = maxY - pixelHeight * (y + 0.5);
                positions[x][y] = new Vector3D(posX, posY, defaultZ);
            }
        }

        return positions;
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        return new Intersection(Vector3D.ZERO(), -1, Vector3D.ZERO(), null);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }
}
