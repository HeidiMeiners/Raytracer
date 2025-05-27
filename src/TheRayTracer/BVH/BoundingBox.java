package TheRayTracer.BVH;

import TheRayTracer.Vector3D;
import TheRayTracer.Ray;

public class BoundingBox {
    public Vector3D min;
    public Vector3D max;

    public BoundingBox(Vector3D min, Vector3D max) {
        this.min = min;
        this.max = max;
    }

    public static BoundingBox combine(BoundingBox a, BoundingBox b){
        Vector3D newMin = new Vector3D(Math.min(a.min.getX(), b.min.getX()), Math.min(a.min.getY(), b.min.getY()), Math.min(a.min.getZ(), b.min.getZ()));
        Vector3D newMax = new Vector3D(Math.max(a.max.getX(), b.max.getX()), Math.max(a.max.getY(), b.max.getY()), Math.max(a.max.getZ(), b.max.getZ()));

        return new BoundingBox(newMin, newMax);
    }

    public boolean intersects(Ray ray) {
        Vector3D origin = ray.getOrigin();
        Vector3D dir = ray.getDirection();

        double tmin = Double.NEGATIVE_INFINITY;
        double tmax = Double.POSITIVE_INFINITY;

        // Eje X
        if (Math.abs(dir.getX()) > 1e-8) {
            double tx1 = (min.getX() - origin.getX()) / dir.getX();
            double tx2 = (max.getX() - origin.getX()) / dir.getX();
            tmin = Math.max(tmin, Math.min(tx1, tx2));
            tmax = Math.min(tmax, Math.max(tx1, tx2));
        } else if (origin.getX() < min.getX() || origin.getX() > max.getX()) {
            return false; // Rayo paralelo al plano X y fuera del intervalo
        }

        // Eje Y
        if (Math.abs(dir.getY()) > 1e-8) {
            double ty1 = (min.getY() - origin.getY()) / dir.getY();
            double ty2 = (max.getY() - origin.getY()) / dir.getY();
            tmin = Math.max(tmin, Math.min(ty1, ty2));
            tmax = Math.min(tmax, Math.max(ty1, ty2));
        } else if (origin.getY() < min.getY() || origin.getY() > max.getY()) {
            return false;
        }

        // Eje Z
        if (Math.abs(dir.getZ()) > 1e-8) {
            double tz1 = (min.getZ() - origin.getZ()) / dir.getZ();
            double tz2 = (max.getZ() - origin.getZ()) / dir.getZ();
            tmin = Math.max(tmin, Math.min(tz1, tz2));
            tmax = Math.min(tmax, Math.max(tz1, tz2));
        } else if (origin.getZ() < min.getZ() || origin.getZ() > max.getZ()) {
            return false;
        }

        return tmax >= Math.max(tmin, 0.0);
    }


    public Vector3D getCenter() {
        return new Vector3D(
                (min.getX() + max.getX()) / 2.0,
                (min.getY() + max.getY()) / 2.0,
                (min.getZ() + max.getZ()) / 2.0
        );
    }

}
