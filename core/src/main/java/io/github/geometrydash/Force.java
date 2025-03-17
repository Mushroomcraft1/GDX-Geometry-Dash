package io.github.geometrydash;

public class Force {
    public float dx;
    public float dy;
    public float ax;
    public float ay;
    static float gravity = -0.17f;
    static float jump = 5;

    public Force(float accelX, float accelY) {
        ax = accelX;
        ay = accelY;
    }

    public void update() {
        dx += ax;
        dy += ay;
    }
}
