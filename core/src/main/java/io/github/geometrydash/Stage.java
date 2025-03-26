package io.github.geometrydash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import javax.swing.*;
import java.util.ArrayList;


public class Stage {
    public static int width = 400;
    public static int height = 300;
    public static float ratio = (float) width / (float) height;

    Point scroll;
    private ArrayList<Obstacle> inactive;
    private int inactiveIdx = 0;
    public ArrayList<Obstacle> obstacles;

    public Stage() {
        scroll = new Point(0, 0);
        obstacles = new ArrayList<>();
        inactive = new ArrayList<>();

        inactive.add(new Obstacle(Obstacles.Platform, new Point(200, 0), new Scale(40, 50)));
        inactive.add(new Obstacle(Obstacles.Platform, new Point(300, 0), new Scale(40, 100)));
        inactive.add(new Obstacle(Obstacles.Platform, new Point(400, 0), new Scale(40, 150)));
        inactive.add(new Obstacle(Obstacles.Platform, new Point(520, 100), new Scale(60, 20)));
        inactive.add(new Obstacle(Obstacles.Spike, new Point(440, 0), new Scale(200, 20)));
        inactive.add(new Obstacle(Obstacles.Platform, new Point(700, 0), new Scale(150, 20)));
        inactive.add(new Obstacle(Obstacles.Platform, new Point(750, 20), new Scale(100, 20)));
        inactive.add(new Obstacle(Obstacles.Platform, new Point(800, 40), new Scale(50, 20)));
        inactive.add(new Obstacle(Obstacles.Spike, new Point(850, 0), new Scale(100, 20)));
        inactive.add(new Obstacle(Obstacles.Platform, new Point(950, 0), new Scale(150, 20)));
    }

    public void update(Player player) {
        while (inactiveIdx < inactive.size() &&
            inactive.get(inactiveIdx).position.x < player.position.x + width) {
            obstacles.add(inactive.get(inactiveIdx));
            ++inactiveIdx;
        }

        for (int idx = 0; idx < obstacles.size(); ++idx) {
            Obstacle obstacle = obstacles.get(idx);
            if (obstacle.position.x + obstacle.size.width < player.position.x - (float) width / 2) {
                obstacles.remove(idx);
                --idx;
            }
        }
    }

    public Triangle colliding(Triangle triangle1) {
        for (Obstacle obstacle : obstacles) {
            for (Triangle triangle2 : obstacle.triangles) {
                if (triangle1.isCollidingWith(triangle2) || triangle2.isCollidingWith(triangle1)) {
                    return triangle1;
                }
            }
        }
        return null;
    }


    private Point getIntersection(Point p1, Point p2, Point p3, Point p4) {
        float x1 = p1.x, y1 = p1.y;
        float x2 = p2.x, y2 = p2.y;
        float x3 = p3.x, y3 = p3.y;
        float x4 = p4.x, y4 = p4.y;

        float denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (denominator == 0) {
            return null;
        }

        float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator;
        float u = ((x1 - x3) * (y1 - y2) - (y1 - y3) * (x1 - x2)) / denominator;

        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            float x = x1 + t * (x2 - x1);
            float y = y1 + t * (y2 - y1);
            return new Point(x, y);
        }

        return null;
    }

    public float raycast(Point pointA, Point pointB, ShapeRenderer renderer, ScreenProperties props) {
        float distance = 0;

        for (Obstacle obstacle : obstacles) {
            for (Triangle triangle : obstacle.triangles) {
                float maxX = 0, maxY = 0;
                float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;

                for (Point point : triangle.points) {
                    maxX = Math.max(maxX, point.x);
                    maxY = Math.max(maxY, point.y);
                    minX = Math.min(minX, point.x);
                    minY = Math.min(minY, point.y);
                }

//                if (maxX < pointA.x || minX > pointA.x || minY > pointA.y || maxY < distance) continue;

                Point[][] lines = {
                    {triangle.points[0], triangle.points[1]},
                    {triangle.points[1], triangle.points[2]},
                    {triangle.points[2], triangle.points[0]}
                };

                for (Point[] line : lines) {
                    Point intersection = getIntersection(line[0], line[1], pointA, pointB);

                    if (intersection != null) {
                        distance = Math.max(distance, intersection.y);

                        if (renderer != null) {
                            intersection.x -= scroll.x;
                            renderer.circle(intersection.screenX(props), intersection.screenY(props), 1f);
                        }
                    }
                }
            }
        }

        return distance;
    }

    public void draw(Player player, ShapeRenderer renderer, ScreenProperties props) {
        scroll.x = Math.max(0, player.position.x - (float) width / 4);

        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(props.offsetX, props.offsetY, props.screenWidth, props.screenHeight);

        for (Obstacle obstacle : obstacles) {
            obstacle.draw(scroll, renderer, props);
        }
    }
}
