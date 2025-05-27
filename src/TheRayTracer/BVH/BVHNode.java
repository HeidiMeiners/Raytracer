package TheRayTracer.BVH;

import TheRayTracer.Intersection;
import TheRayTracer.Ray;
import TheRayTracer.Vector3D;
import TheRayTracer.objects.Object3D;

import java.awt.*;
import java.util.List;

public class BVHNode extends Object3D {
    private BoundingBox box;
    private Object3D left;
    private Object3D right;

    public BVHNode(List<Object3D> objects) {
        super(Vector3D.ZERO(), Color.BLACK,0,0,0,0);
        if (objects.size() == 1) {
            this.left = this.right = objects.getFirst();
            this.box = objects.getFirst().getBoundingBox();
        } else if (objects.size() == 2) {
            this.left = objects.get(0);
            this.right = objects.get(1);
            this.box = BoundingBox.combine(left.getBoundingBox(), right.getBoundingBox());
        } else {
            // Escoge un eje aleatorio para dividir (X, Y, Z)
            int axis = (int)(Math.random() * 3);
            objects.sort((a, b) -> {
                double centerA = a.getBoundingBox().getCenter().get(axis);
                double centerB = b.getBoundingBox().getCenter().get(axis);
                return Double.compare(centerA, centerB);
            });

            int mid = objects.size() / 2;
            List<Object3D> leftList = objects.subList(0, mid);
            List<Object3D> rightList = objects.subList(mid, objects.size());

            this.left = new BVHNode(leftList);
            this.right = new BVHNode(rightList);

            this.box = BoundingBox.combine(left.getBoundingBox(), right.getBoundingBox());
        }
    }


    @Override
    public BoundingBox getBoundingBox() {
        return box;
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        if (!box.intersects(ray)) {
            return null;
        }

        Intersection leftHit = left.getIntersection(ray);
        Intersection rightHit = right.getIntersection(ray);

        if (leftHit == null) return rightHit;
        if (rightHit == null) return leftHit;

        return (leftHit.getDistance() < rightHit.getDistance()) ? leftHit : rightHit;
    }

}
