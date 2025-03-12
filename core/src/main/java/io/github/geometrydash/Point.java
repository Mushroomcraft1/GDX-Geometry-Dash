package io.github.geometrydash;

public class Point {
    public float x;
    public float y;
    private float tX;
    private float tY;

    public Point(float initX, float initY) {
        x = initX;
        y = initY;
        tX = 0;
        tY = 0;
    }

    public boolean isOutOfBoundsY() {
        return y < 0 || y > Stage.height;
    }

    public void setTranslation(Point translation) {
        tX = translation.x;
        tY = translation.y;
    }

    public float screenX(ScreenProperties props) {
        float scale = props.screenWidth / Stage.width;

        return scale * (x + tX) + props.offsetX;
    }

    public float screenY(ScreenProperties props) {
        float scale = props.screenHeight / Stage.height;

        return scale * (y + tY) + props.offsetY;
    }

    public void printPos(ScreenProperties props) {
        System.out.println("\tPoint: SX " + screenX(props) + " SY " + screenY(props) + " tX " + tX + " tY " + tY + " x " + x + " y " + y);
    }

    void move(float dX, float dY) {
        x += dX;
        y += dY;
    }

    Point shiftX(float sX) {
        return new Point(x + sX, y);
    }

    Point shiftY(float sY) {
        return new Point(x, y + sY);
    }

    Point shiftXY(float sX, float sY) {
        return new Point(x + sX, y + sY);
    }
}
