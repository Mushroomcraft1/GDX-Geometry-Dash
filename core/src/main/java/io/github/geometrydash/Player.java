package io.github.geometrydash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player {
    public Point position;
    public Force force;

    Triangle[] triangles;
    Point[] points;
    float scale = 15;

    public Player() {
        float halfSize = scale / 2;
        position = new Point(halfSize, halfSize);
        force = new Force(0, 0);
        force.dx = 1;

        points = new Point[]{
            position.shiftXY(-halfSize, -halfSize),
            position.shiftXY(halfSize, -halfSize),
            position.shiftXY(-halfSize, halfSize),
            position.shiftXY(halfSize, halfSize)
        };

        Point[] tri1points = {points[0], points[1], points[2]};
        Point[] tri2points = {points[1], points[2], points[3]};

        triangles = new Triangle[]{
            new Triangle(Behaviour.Player, tri1points),
            new Triangle(Behaviour.Player, tri2points)
        };
    }

    public void draw(Stage stage, ShapeRenderer renderer, ScreenProperties props) {
        Point translate = new Point(-stage.scroll.x, 0);
        for (Triangle triangle : triangles) {
            triangle.draw(translate, renderer, props);
        }
    }

    private void move(float x, float y) {
        position.x += x;
        position.y += y;

        for (Point point : points) {
            point.move(x, y);
        }
    }

    private Behaviour checkCollisions(Stage stage) {
        for (Triangle triangle : triangles) {
            for (Point point : triangle.points) {
                if (point.isOutOfBoundsY()) return Behaviour.Platform;
            }

            Triangle colliding = stage.colliding(triangle);
            if (colliding != null) {
                return colliding.behaviour;
            }
        }

        return Behaviour.None;
    }

    public void physics(Stage stage) {
        boolean jumpKeyPressed = Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE);

        force.update();
        move(0, force.dy);
        boolean isTouchingGround = checkCollisions(stage) == Behaviour.Platform;

        if (isTouchingGround) {
            move(0, -force.dy);
            force.ay = 0;
            force.dy = jumpKeyPressed ? Force.jump : 0;
        } else {
            force.ay = Force.gravity;
        }

        move(force.dx, 0);
        Behaviour collisions = checkCollisions(stage);
        for (Triangle tri :
            triangles) {
            tri.behaviour = Behaviour.Player;
        }
        if (collisions != Behaviour.None) {
            for (Triangle tri :
                triangles) {
                tri.behaviour = Behaviour.Colliding;
            }
            move(-force.dx, 0);
        }
    }
}
