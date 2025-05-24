package TheRayTracer.objects;

import TheRayTracer.Vector3D;

import java.awt.*;

public abstract class Object3D implements IIntersectable{
    private Color color;
    private Vector3D position;
    private double shininess;
    private double specularCoefficient;


    public Object3D(Vector3D position, Color color, double shininess, double specularCoefficient) {
        setPosition(position);
        setColor(color);
        setShininess(shininess);
        setSpecularCoefficient(specularCoefficient);
    }

    public double getShininess() {
        return shininess;
    }

    public void setShininess(double shininess) {
        this.shininess = shininess;
    }

    public double getSpecularCoefficient() {
        return specularCoefficient;
    }

    public void setSpecularCoefficient(double specularCoefficient) {
        this.specularCoefficient = specularCoefficient;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }
}