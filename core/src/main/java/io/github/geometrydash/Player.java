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
    Point[] corners;
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

        corners = new Point[]{bottomLeft, topLeft, topRight, bottomRight};

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

    private void rotate(float deg) {
        rotation += deg;

        for (Triangle triangle : triangles) {
            triangle.rotateBy(deg,  position);
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
            rotate(3.1f);
        }

        boolean jumpKeyPressed = Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE);

        force.update();

        float[] distances = new float[5];
        distances[4] = stage.raycast(position);

        for (int i = 0; i < 4; ++i) {
            distances[i] = stage.raycast(corners[i]);
        }

        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;

        for (int i = 0; i < 4; ++i) {
            Point point = corners[i];
            if (point.x < corners[left].x) left = i;
            if (point.x > corners[right].x) right = i;
            if (point.y < corners[bottom].y) bottom = i;
            if (point.y > corners[top].y) top = i;
        }


        move(0, force.dy);
        Behaviour groundCollisions = checkCollisions(stage);
        if (groundCollisions == Behaviour.Kill) {
            behaviour = Behaviour.Dying;
            return;
        } else {
            if (distances[bottom] < -0.1f) {
                move(0, -distances[bottom]);
//                if (left.y != right.y)
            }
        }
        boolean isTouchingGround = groundCollisions != Behaviour.None;

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
