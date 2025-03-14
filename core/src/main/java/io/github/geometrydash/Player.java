package io.github.geometrydash;

import io.github.geometrydash.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player {
    public Point position;
    public Force force;

    Triangle[] triangles;
    Point[] points;
    Behaviour behaviour = Behaviour.Player;
    Point translation;
    int deathAnimationTimer = 0;
    float scale = 15;
    int lastJump = 0;
    int lastTouchingGround = 0;
    boolean reset = false;

    public Player() {
        float halfSize = scale / 2;
        position = new Point(halfSize, halfSize);
        force = new Force(0, Force.gravity);
        force.dx = 1.3f;
        translation = new Point(0, 0);

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

        if (behaviour == Behaviour.Dying) {
            for (Triangle triangle : triangles) {
                triangle.ColorOverride = new Color(1, 1, 1, 1 - (float)deathAnimationTimer / 100);
            }
        }

        for (Triangle triangle : triangles) {
            triangle.draw(translate, renderer, props);
        }

        if (deathAnimationTimer > 150) {
            reset = true;
        }
    }

    private void move(float x, float y) {
        position.x += x;
        position.y += y;

        for (Point point : points) {
            point.move(x, y);
            point.setTranslation(translation);
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
        if (behaviour == Behaviour.Dying) {
            translation.x -= force.dx;
            move(force.dx, 0);
            ++deathAnimationTimer;
            return;
        }

        boolean jumpKeyPressed = Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE);

        force.update();
        move(0, force.dy);
        boolean isTouchingGround = checkCollisions(stage) != Behaviour.None;

        ++lastJump;
        if (isTouchingGround) {
            move(0, -force.dy);
            force.dy = 0;
            lastTouchingGround = 0;
        } else {
            ++lastTouchingGround;
        }

        boolean canJump = lastJump >= 10 && lastTouchingGround <= 5;
        if (canJump && jumpKeyPressed) {
            force.dy = Force.jump;
            lastJump = 0;
        }

        move(force.dx, 0);
        Behaviour collisions = checkCollisions(stage);

        if (collisions != Behaviour.None) {
            behaviour = Behaviour.Dying;
        }
    }
}
