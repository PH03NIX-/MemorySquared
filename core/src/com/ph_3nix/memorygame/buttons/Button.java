package com.ph_3nix.memorygame.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import javax.xml.soap.Text;

/**
 * Created by david on 2/8/2018.
 */


public class Button implements Comparable<Button> {
    private float x1, x2, y1, y2, zIndex, height, width, fallingSpeed;
    private int order;
    private Sprite buttonSprite;
    private boolean touched, visible, falling, offscreen;
    private Texture buttonTexture;
    private Texture buttonTexturePressed;

    Button(float width, float height, float x, float y, int order, Texture buttonTexture, Texture buttonTexturePressed ) {
        this.x1 = x;
        this.x2 = x+width;
        this.y1 = y;
        this.y2 = y+height;
        this.zIndex = 0;
        this.height = height;
        this.width = width;
        this.order = order;
        this.touched = false;
        this.buttonTexture = buttonTexture;
        this.buttonTexturePressed = buttonTexturePressed;
        this.visible = true;
        this.falling = false;
        this.offscreen = false;
        this.fallingSpeed = 0;

        buttonSprite = new Sprite(buttonTexture);
        buttonSprite.setSize(width, height);
        buttonSprite.setPosition(x, y);
        buttonSprite.setOrigin(this.x1+(this.x2-this.x1)/2, this.y1-(this.y2-this.y1)/2);
    }

    @Override
    public int compareTo( Button button) {
        return (this.getZIndex() < button.getZIndex() ? -1 :
            this.getZIndex() == button.getZIndex() ? 0 : 1);
    }

    public void drawButtons(Batch batch) {
        if( this.falling ) {
            this.iterateFall(Gdx.graphics.getDeltaTime());
        }
        buttonSprite.draw(batch);
    }

    public void setTouched() {
        setTouched(true);
    }

    public void setTouched(boolean newTouched) {
        if(newTouched)
            buttonSprite.setTexture(buttonTexturePressed);
        else
            buttonSprite.setTexture(buttonTexture);

        this.touched = newTouched;
    }

    public void blink() {
        if( this.visible ) {
            buttonSprite.setAlpha(0.35f);
            this.visible = false;
        }
        else {
            buttonSprite.setAlpha(1f);
            this.visible = true;
        }
    }

    public void setAskew( int skewRight, int skewUp) {
        this.setZIndex(10);
        buttonSprite.setPosition(this.getX1()+skewRight*(this.getX2()-this.getX1())/16, this.getY1()+skewUp*(this.getY2()-this.getY1())/16);
        buttonSprite.rotate(3*skewRight);
    }

    public void iterateFall(float deltaTime) {
        this.setY1( this.getY1() +20 - deltaTime*0.5f*9.8f* (float)Math.pow(this.fallingSpeed, 2));
        buttonSprite.setOrigin((this.x1+this.x2)/2, this.y1-this.height/2);
        buttonSprite.setPosition(this.getX1(), this.getY1());
        /*buttonSprite.rotate(4);*/
        /*if( this.order == 0 )
            System.out.println("fall speed: "+this.fallingSpeed);*/
        /*this.fallingSpeed = (float) Math.pow(this.fallingSpeed, 2);*/
        this.fallingSpeed += 1 ;
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

    private void setY1( float newY1 ) {
        this.y1 = newY1;
    }

    public float getY2() {
        return y2;
    }

    public float getZIndex() { return this.zIndex; }

    public void setZIndex( int newZ) { this.zIndex = newZ; }

    public float getOrder() {
        return order;
    }

    public boolean getTouched() {
        return touched;
    }

    public void setFalling(boolean isIt) {this.falling = isIt; }

    public int checkOffscreen() {
        int offscreen = 0;
            if( this.getY1() + this.height < 0 ) {
                if(this.offscreen) {
                    offscreen = 1;
                }
                this.offscreen = true;
            }

        return offscreen;
    }


}
