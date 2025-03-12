package io.github.geometrydash;

import com.badlogic.gdx.Gdx;

public class MousePosition {
    public float x;
    public float y;

    public MousePosition(ScreenProperties properties) {
        x = (Gdx.input.getX() - properties.offsetX) / properties.screenWidth * Stage.width;
        y = (properties.actualHeight - Gdx.input.getY() - properties.offsetY) / properties.screenHeight * Stage.height;
    }
}
