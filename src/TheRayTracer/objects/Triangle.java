package TheRayTracer.objects;

import TheRayTracer.Intersection;
import TheRayTracer.Ray;
import TheRayTracer.Vector3D;

public class Triangle implements IIntersectable {
    public static final double EPSILON = 0.0000000000001;
    private Vector3D[] vertices;
    private Vector3D[] normals;

    public Triangle(Vector3D v0, Vector3D v1, Vector3D v2) {
        setVertices(v0, v1, v2);
        setNormals(null);
    }

    public Vector3D[] getVertices() {
        return vertices;
    }

    public void setVertices(Vector3D[] vertices) {
        this.vertices = vertices;
    }

    public void setVertices(Vector3D v0, Vector3D v1, Vector3D v2) {
        setVertices(new Vector3D[]{v0, v1, v2});
    }

    public Vector3D getNormal(Vector3D point) {
        Vector3D normal;
        Vector3D[] normals = getNormals();

        if(normals == null){
            Vector3D[] vertices = getVertices();
            Vector3D v = Vector3D.substract(vertices[1], vertices[0]);
            Vector3D w = Vector3D.substract(vertices[0], vertices[2]);
            normal = Vector3D.normalize(Vector3D.crossProduct(v, w));
        }
        else{
            Vector3D lambda = getLambdas(point);
            Vector3D n0 = Vector3D.scalarMultiplication(normals[0], lambda.getX());
            Vector3D n1 = Vector3D.scalarMultiplication(normals[1], lambda.getY());
            Vector3D n2 = Vector3D.scalarMultiplication(normals[2], lambda.getZ());
            normal = Vector3D.normalize(Vector3D.add(n0, Vector3D.add(n1, n2)));
        }

        return normal;
    }

    public Vector3D[] getNormals() {
        return normals;
    }

    public void setNormals(Vector3D[] normals) {
        this.normals = normals;
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        Intersection intersection = new Intersection(null, -1, null, null);

        Vector3D[] vert = getVertices();
        Vector3D v2v0 = Vector3D.substract(vert[2], vert[0]);
        Vector3D v1v0 = Vector3D.substract(vert[1], vert[0]);
        Vector3D vectorP = Vector3D.crossProduct(ray.getDirection(), v1v0);
        double det = Vector3D.dotProduct(v2v0, vectorP);
        double invDet = 1.0 / det;
        Vector3D vectorT = Vector3D.substract(ray.getOrigin(), vert[0]);
        double u = invDet * Vector3D.dotProduct(vectorT, vectorP);

        if (!(u < 0 || u > 1)) {
            Vector3D vectorQ = Vector3D.crossProduct(vectorT, v2v0);
            double v = invDet * Vector3D.dotProduct(ray.getDirection(), vectorQ);
            if (!(v < 0 || (u + v) > (1.0 + EPSILON))) {
                double t = invDet * Vector3D.dotProduct(vectorQ, v1v0);
                if (t > EPSILON) {
                    Vector3D position = ray.getPointAtDistance(t);
                    Vector3D normal = getNormal(position);
                    intersection.setNormal(normal);
                    intersection.setDistance(t);
                    intersection.setPosition(position);
                }
            }
        }

        return intersection;
    }

    public Vector3D getLambdas(Vector3D p) {
        Vector3D[] vertices = getVertices();

        Vector3D v0= Vector3D.substract(vertices[1], vertices[0]);
        Vector3D v1= Vector3D.substract(vertices[2], vertices[0]);
        Vector3D v2= Vector3D.substract(p,vertices[0]);

        double d00 = Vector3D.dotProduct(v0, v0);
        double d01 = Vector3D.dotProduct(v0, v1);
        double d11 = Vector3D.dotProduct(v1, v1);
        double d20 = Vector3D.dotProduct(v2, v0);
        double d21 = Vector3D.dotProduct(v2, v1);

        double denominator = d00 * d11 - d01 * d01;

        double lambda2 = (d11 * d20 - d01 * d21) / denominator;
        double lambda3 = (d00 * d21 - d01 * d20) / denominator;
        double lambda1 = 1.0 - lambda2 - lambda3;

        return new Vector3D(lambda1, lambda2, lambda3);
    }
}
