package com.ph_3nix.memorygame.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by david on 2/13/2018.
 */

public class Menu {
    private Texture menuTexture;
    private Sprite menuSprite;
    private float x1, x2, y1, y2, clickY1, clickY2, width, height, switchMod;
    private String name;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont theFont;
    private GlyphLayout textLayout;
    private boolean stretchWidth;
    private int id;

    public Menu(float bWidth, float gridHeight, float position, float hpPad, float vpPad, String menuButtonName, FreeTypeFontGenerator fontGenerator, boolean stretchWidth, boolean noVolume, int id) {
        this.x1 = 0+(bWidth*hpPad);
        this.x2 = bWidth-(bWidth*hpPad);
        this.y1 = (gridHeight * position)+(gridHeight*vpPad);
        this.y2 = (gridHeight * (position + 1))-(gridHeight*vpPad);
        this.stretchWidth = stretchWidth;
        this.width = bWidth - (bWidth*hpPad*2);
        this.height = gridHeight - (gridHeight*vpPad*2);
        this.name = menuButtonName;
        this.switchMod = 0;
        this.id = id;

        if(menuButtonName == "easy" || menuButtonName == "hard") {
            this.clickY1 = this.y1-gridHeight/2;
            this.clickY2 = this.y2+gridHeight/2;
        }
        else {
            this.clickY1 = this.y1;
            this.clickY2 = this.y2;
        }

        textLayout = new GlyphLayout();
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        if( this.name == "easy" || this.name == "hard")
            parameter.size = (int) (this.height);
        else if(this.name == "tabs")
            parameter.size = (int) (this.height*0.629629 ) ;
        else if(this.name == "pH03 Games")
            parameter.size = (int) (this.height*0.2222222 ) ;
        else
            parameter.size = (int) (this.height*0.666667);
        parameter.color = new Color(230,230, 255, 1);
        theFont = fontGenerator.generateFont(parameter);

        menuTexture = new Texture(this.name == "pH03 Games" ? ("rate.png") : (menuButtonName+(this.name == "audio" ? (noVolume ? "_no." : ".") : ".")+"png"));
        menuSprite = new Sprite(menuTexture);
        menuSprite.setSize( stretchWidth ? this.width : this.height, this.height);
        menuSprite.setPosition(this.x1, this.y1);
    }

    public void dispose() {
        menuTexture.dispose();
    }

    public void drawButton(Batch batch) {
        menuSprite.draw(batch);
        if( this.name == "tabs") {
            textLayout.setText(theFont, "size");
            theFont.draw(batch, textLayout, this.x1 + this.width / 4 - textLayout.width / 2 - switchMod, this.y1 + this.height / 2 + theFont.getLineHeight() * (float) 0.458333333);
            textLayout.setText(theFont, "speed");
            theFont.draw(batch, textLayout, this.x1 + this.width * (float) 0.75 - textLayout.width /2 - switchMod, this.y1 + this.height / 2 + theFont.getLineHeight() * (float) 0.458333333);
        }
        else if( this.name != "menu-color" && this.name != "audio") {
            textLayout.setText(theFont, this.name);
            theFont.draw(batch, textLayout,this.x1 + this.width / 2 - textLayout.width / 2, this.y1 + this.height / 2 + theFont.getLineHeight() * (float) 0.458333333);
        }
    }

    public void flipHor() {
        this.menuSprite.flip(true, false);
        if(switchMod == 0 ) {
            switchMod = this.width / 36;
        } else {
            switchMod = 0;
        }
    }

    public void newTexture( Texture newTexture ) {
        menuSprite = new Sprite(newTexture);
        menuSprite.setSize( this.stretchWidth ? this.width : this.height, this.height);
        menuSprite.setPosition(this.x1, this.y1);
    }

    public void setFontSize( float newScale) {
        theFont.getData().setScale(newScale);
    }

    public float getHeight() { return this.height; }

    public String getName() {
        return this.name;
    }

    public float getX1() {
        return x1;
    }

    public float getX2() {
        return x2;
    }

    public float getY1() {
        return clickY1;
    }

    public float getY2() {
        return clickY2;
    }

    public int getId() { return id; }
}
