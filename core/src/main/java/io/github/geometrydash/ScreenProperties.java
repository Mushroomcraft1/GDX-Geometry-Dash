package io.github.geometrydash;

import com.badlogic.gdx.Gdx;

public class ScreenProperties {
    public float screenWidth = 0;
    public float screenHeight = 0;
    public float offsetX = 0;
    public float offsetY = 0;
    public float actualWidth = 0;
    public float actualHeight = 0;

    public void updateSize(float ratio) {
        actualWidth = Gdx.graphics.getWidth();
        actualHeight = Gdx.graphics.getHeight();
        if (actualWidth / ratio <= actualHeight) {
            screenHeight = actualWidth / ratio;
            screenWidth = actualWidth;
            offsetY = (actualHeight - screenHeight) / 2;
            offsetX = 0;
        } else {
            screenHeight = actualHeight;
            screenWidth = actualHeight * ratio;
            offsetY = 0;
            offsetX = (actualWidth - screenWidth) / 2;
        }
    }
}
