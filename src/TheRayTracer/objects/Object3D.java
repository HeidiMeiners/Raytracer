package TheRayTracer.objects;

import TheRayTracer.Vector3D;

import java.awt.*;

public abstract class Object3D implements IIntersectable{
    private Color color;
    private Vector3D position;
    private double shininess;
    private double specularCoefficient;
    private double refractionCoefficient;
    private double transparency;


    public Object3D(Vector3D position, Color color, double shininess, double specularCoefficient, double refractionCoefficient, double transparency) {
        setPosition(position);
        setColor(color);
        setShininess(shininess);
        setSpecularCoefficient(specularCoefficient);
        setRefractionCoefficient(refractionCoefficient);
        setTransparency(transparency);
    }

    public double getTransparency() {
        return transparency;
    }

    public void setTransparency(double transparency) {
        this.transparency = transparency;
    }

    public double getRefractionCoefficient() {
        return refractionCoefficient;
    }

    public void setRefractionCoefficient(double reflectionCoefficient) {
        this.refractionCoefficient = reflectionCoefficient;
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