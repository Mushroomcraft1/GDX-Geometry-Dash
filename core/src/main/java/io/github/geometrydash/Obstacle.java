package io.github.geometrydash;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Obstacle {
    float spikeWidth = 20;
    public Point position;
    public Scale size;
    public Triangle[] triangles;

    public Obstacle(Obstacles type, Point initPos, Scale initSize) {
        position = initPos;
        size = initSize;

        switch (type) {
            case Platform: {
                triangles = new Triangle[2];
                Point bottomLeft = initPos.copy();
                Point bottomRight = initPos.shiftX(initSize.width);
                Point topLeft = initPos.shiftY(initSize.height);
                Point topRight = initPos.shiftXY(initSize.width, initSize.height);

                Point[] tri1points = {bottomLeft, bottomRight, topLeft};
                Point[] tri2points = {topLeft.copy(), topRight, bottomRight.copy()};

                triangles[0] = new Triangle(Behaviour.Platform, tri1points);
                triangles[1] = new Triangle(Behaviour.Platform, tri2points);
                break;
            }
            case Spike: {
                int nOfSpikes = (int)(initSize.width / spikeWidth);
                triangles = new Triangle[nOfSpikes];

                float xPos = initPos.x;
                for (int i = 0; i < nOfSpikes; ++i) {
                    Point bottomLeft = new Point(xPos, initPos.y);
                    Point bottomRight = bottomLeft.shiftX(spikeWidth);
                    Point top = bottomLeft.shiftXY(spikeWidth / 2, initSize.height);

                    Point[] triPoints = {bottomLeft, bottomRight, top};

                    triangles[i] = new Triangle(Behaviour.Kill, triPoints);

                    xPos += spikeWidth;
                }

                break;
            }
        }
    }

    public void draw(Point scroll, ShapeRenderer renderer, ScreenProperties props) {
        Point translation = new Point(- scroll.x, - scroll.y);
        for (Triangle triangle : triangles) {
            triangle.draw(translation, renderer, props);
        }
    }
}
