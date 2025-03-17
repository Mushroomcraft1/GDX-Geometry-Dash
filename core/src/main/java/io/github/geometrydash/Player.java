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
    Behaviour behaviour = Behaviour.Player;
    Point translation;
    int deathAnimationTimer = 0;
    float scale = 15;
    int lastJump = 0;
    int lastTouchingGround = 0;

    float rotation = 0;
    boolean reset = false;

    public Player() {
        float halfSize = scale / 2;
        position = new Point(halfSize, halfSize);
        force = new Force(0, Force.gravity);
        force.dx = 1.3f;
        translation = new Point(0, 0);

        Point bottomLeft = position.shiftXY(-halfSize, -halfSize);
        Point bottomRight = position.shiftXY(halfSize, -halfSize);
        Point topLeft = position.shiftXY(-halfSize, halfSize);
        Point topRight = position.shiftXY(halfSize, halfSize);

        Point[] tri1points = {bottomLeft, bottomRight, topLeft};
        Point[] tri2points = {bottomRight.copy(), topLeft.copy(), topRight};

        triangles = new Triangle[]{
            new Triangle(Behaviour.Player, tri1points),
            new Triangle(Behaviour.Player, tri2points)
        };
    }

    public void draw(Stage stage, ShapeRenderer renderer, ScreenProperties props) {
        Point translate = new Point(translation.x - stage.scroll.x, translation.y);

        if (behaviour == Behaviour.Dying) {
            for (Triangle triangle : triangles) {
                triangle.ColorOverride = new Color(1, 1, 1, 1 - (float) deathAnimationTimer / 100);
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

        for (Triangle triangle : triangles) {
            triangle.move(x, y);
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

        if (lastTouchingGround > 1) {
            rotation += 1;
            for (Triangle triangle : triangles) {
                triangle.rotateBy(1f, position);
            }
        }

        boolean jumpKeyPressed = Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE);

        force.update();
        move(0, force.dy);

        Behaviour groundCollisions = checkCollisions(stage);
        if (groundCollisions == Behaviour.Kill) {
            behaviour = Behaviour.Dying;
            return;
        }
        boolean isTouchingGround = groundCollisions != Behaviour.None;

        ++lastJump;
        if (isTouchingGround) {
            for (Triangle triangle : triangles) {
                triangle.rotateBy(-triangle.rotation, position);
            }

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
