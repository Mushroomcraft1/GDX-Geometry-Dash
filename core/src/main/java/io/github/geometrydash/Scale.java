package io.github.geometrydash;
public class Scale {
    public float width;
    public float height;

    public Scale(float initW, float initH) {
        width = initW;
        height = initH;
    }

    public float screenWidth(ScreenProperties props) {
        float scale = props.screenWidth / Stage.width;

        return scale * width;
    }

    public float screenHeight(ScreenProperties props) {
        float scale = props.screenWidth / Stage.width;

        return scale * height;
    }
}
