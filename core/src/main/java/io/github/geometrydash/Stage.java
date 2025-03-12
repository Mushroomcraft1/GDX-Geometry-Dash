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



        obstacles.add(new Obstacle(Obstacles.Platform,  new Point(100, 0), new Scale(20,50)));
        obstacles.add(new Obstacle(Obstacles.Platform,  new Point(150, 0), new Scale(20,100)));
        obstacles.add(new Obstacle(Obstacles.Platform,  new Point(200, 0), new Scale(20,150)));
    }


    public Triangle colliding(Triangle triangle1) {
        for (Obstacle obstacle : obstacles) {
            for (Triangle triangle2 : obstacle.triangles) {
                if (triangle1.isCollidingWith(triangle2)) {
                    return triangle2;
                }
            }
        }
        return null;
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
