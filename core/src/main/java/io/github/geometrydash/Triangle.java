package io.github.geometrydash;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Triangle {
    public Behaviour behaviour;

    public Point[] points;
    public Color ColorOverride = null;
    public float rotation = 0;

    public Triangle(Behaviour b, Point[] p) {
        behaviour = b;
        points = p;
    }

    // https://stackoverflow.com/questions/2049582/how-to-determine-if-a-point-is-in-a-2d-triangle
    private float sign(Point p1, Point p2, Point p3) {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }

    private boolean pointInTriangle (Point pt) {
        float d1, d2, d3;
        boolean has_neg, has_pos;

        d1 = sign(pt, points[0], points[1]);
        d2 = sign(pt, points[1], points[2]);
        d3 = sign(pt, points[2], points[0]);

        has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(has_neg && has_pos);
    }

    public boolean isCollidingWith(Triangle triangle2) {
        for (Point point : triangle2.points) {
            if (pointInTriangle(point)) return true;
        }
        return false;
    }

    public void printPos(ScreenProperties props) {
        System.out.println("Triangle:");
        for (Point point : points) {
            point.printPos(props);
        }
    }

    public void move(float x, float y) {
        for (Point point : points) {
            point.x += x;
            point.y += y;
        }
    }

    public void rotateBy(float rot, Point rotateAround) {
        rotation -= rot;

        for (Point point : points) {
            point.rotate(-rot, rotateAround);
        }
    }

    public void draw(Point position, ShapeRenderer renderer, ScreenProperties props) {
        if (ColorOverride != null) {
            renderer.setColor(ColorOverride);
        } else {
            switch (behaviour) {
                case Kill:
                    renderer.setColor(Color.RED);
                    break;
                case Platform:
                    renderer.setColor(Color.BLACK);
                    break;
                case Player:
                    renderer.setColor(Color.WHITE);
                    break;
                case Colliding:
                    renderer.setColor(Color.YELLOW);
                    break;
            }
        }

        Point point1 = points[0];
        Point point2 = points[1];
        Point point3 = points[2];

        point1.setTranslation(position);
        point2.setTranslation(position);
        point3.setTranslation(position);

        renderer.triangle(
            point1.screenX(props), point1.screenY(props),
            point2.screenX(props), point2.screenY(props),
            point3.screenX(props), point3.screenY(props)
        );
    }
}
