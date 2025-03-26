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

        renderer.setColor(Color.GREEN);

        stage.raycast(position.shiftY(-1000), position.shiftY(1000), renderer, props);

        for (int i = 0; i < 4; ++i) {
            stage.raycast(corners[i].shiftY(-1000), corners[i].shiftY(1000), renderer, props);
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
        rotation %= 90;

        for (Triangle triangle : triangles) {
            triangle.rotateBy(deg, position);
        }
    }

    private void setRotation(float deg) {
        for (Triangle triangle : triangles) {
            triangle.rotateBy(deg - rotation, position);
        }

        rotation = deg % 90;
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
        move(0, force.dy);

        float[] distances = new float[4];

        for (int i = 0; i < 4; ++i) {
            Point pointA = new Point(corners[i].x, 0);
            Point pointB = pointA.shiftY(10_000);

            distances[i] = corners[i].y - stage.raycast(pointA, pointB, null, null);
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

        float offset = 0;
        boolean isTouchingGround = false;

        for (float distance : distances) {
            if (distance < 0 && distance < offset) {
                offset = distance;
                isTouchingGround = true;
            }
        }

        ++lastJump;
        if (isTouchingGround) {
            move(0, -force.dy);

            if (rotation < 15 || rotation > 25) {
                setRotation(0);
                Point pointA = new Point(position.x, 0);
                Point pointB = pointA.shiftY(10_000);

                float distToFloor = stage.raycast(pointA, pointB, null, null) - position.y + scale / 2;


                if (distToFloor > 0) {
                    if (distToFloor > 10) {
                        behaviour = Behaviour.Dying;
                        return;
                    }

                    move(0, distToFloor);
                }
            } else {
                rotate(10f);
            }

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

        move(force.dx, 0.5f);
        Behaviour collisions = checkCollisions(stage);

        if (collisions != Behaviour.None) {
            behaviour = Behaviour.Dying;
        }

//        System.out.println(distances[0] + " " + distances[1] + " " + distances[2] + " " + distances[3] + " " + distances[4]);

        move(0, -0.5f);
    }
}
