package io.github.geometrydash;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;


public class Stage {
    public static int width = 400;
    public static int height = 300;
    public static float ratio = (float)width / (float)height;

    Point scroll;
    public ArrayList<Obstacle> obstacles;

    public Stage() {
        scroll = new Point(0, 0);
        obstacles = new ArrayList<>();

        obstacles.add(new Obstacle(Obstacles.Platform,  new Point(200, 0), new Scale(40,50)));
        obstacles.add(new Obstacle(Obstacles.Platform,  new Point(300, 0), new Scale(40,100)));
        obstacles.add(new Obstacle(Obstacles.Platform,  new Point(400, 0), new Scale(40,150)));
        obstacles.add(new Obstacle(Obstacles.Platform,  new Point(520, 100), new Scale(60,20)));
        obstacles.add(new Obstacle(Obstacles.Spike,  new Point(440, 0), new Scale(200,20)));
    }

    public Triangle colliding(Triangle triangle1)  {
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

    public float raycast(Point pointA) {
        float distance = pointA.y;
        Point pointB = new Point(pointA.x, 0);

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
                    { triangle.points[0], triangle.points[1] },
                    { triangle.points[1], triangle.points[2] },
                    { triangle.points[2], triangle.points[0] }
                };

                for (Point[] line : lines) {
                    Point intersection = getIntersection(line[0], line[1], pointA, pointB);

                    if (intersection != null) {
                        distance = Math.min(distance, pointA.y - intersection.y);
                    }
                }
            }
        }

        return distance;
    }

    public void draw(Player player, ShapeRenderer renderer, ScreenProperties props) {
        scroll.x = Math.max(0, player.position.x - (float)width / 4);

        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(props.offsetX, props.offsetY, props.screenWidth, props.screenHeight);

        for (Obstacle obstacle: obstacles) {
            obstacle.draw(scroll, renderer, props);
        }
    }
}
