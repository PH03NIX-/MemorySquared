package com.ph_3nix.memorygame.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by david on 3/24/2018.
 */

public class Replay {
    private float x1, y1, x2, y2;
    Sprite replaySprite;

    Replay(float x1, float y1, float width, float height, Texture texture) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x1 + width;
        this.y2 = y1 + height;

        replaySprite = new Sprite(texture);
        replaySprite.setPosition(this.x1, this.y1);
        replaySprite.setSize(width, height);
    }

    public void drawReplay(Batch batch) {
        replaySprite.draw(batch);
    }

    public float getX1() {
        return x1;
    }

    public float getX2() {
        return x2;
    }

    public float getY1() {
        return y1;
    }

    public float getY2() {
        return y2;
    }

}
