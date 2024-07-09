package com.ph_3nix.memorygame.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.ph_3nix.memorygame.buttons.Replay;

import sun.nio.cs.Surrogate;

/**
 * Created by david on 2/21/2018.
 */

public class Scoreboard {

    private int levelScore, roundScore, turnScore;
    private float x, y;
    private String gameType;
    Preferences highscores;
    BitmapFont theFont, theFont2, font, theFontHighscore;
    GlyphLayout textLayout;
    public FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    //FreeTypeFontGenerator generator2;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter2;

    public Scoreboard() {
        highscores = Gdx.app.getPreferences("highscores");
        generator = new FreeTypeFontGenerator(Gdx.files.internal("Hanken-Book.ttf"));
    }

    public void newScoreboard(float xPos, float yPos, String gameType) {
        this.levelScore = 0;
        this.roundScore = 0;
        this.x = xPos;
        this.y = yPos;
        this.gameType = gameType;
        textLayout = new GlyphLayout();
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size= (int) (xPos/18);
        parameter.color= new Color(230,230, 255, 1);

        //generator2 = new FreeTypeFontGenerator(Gdx.files.internal("Hanken-Book.ttf"));
        theFont = generator.generateFont(parameter);
        //parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size= (int) (xPos/30);
        parameter.color= new Color(230,230, 255, 1);
        theFont2 = generator.generateFont(parameter);
        //parameter.size= (int) (xPos/18);
        //parameter.color= new Color(0,0, 0, 1);
        //theFontHighscore = generator.generateFont(parameter);
    }

    public void newHighScoreboard(float xPos, float yPos, String gameType) {
        this.newScoreboard(xPos, yPos, gameType);   // update later when highscore board is different looking than scoreboard
    }

    public void dispose() {
        generator.dispose();
        theFont.dispose();
        theFont2.dispose();
        font.dispose();
    }

    public void setLevelScore(int levelScore) {
        this.roundScore = 0;
        this.turnScore = 0;
        this.levelScore += levelScore;
    }

    public void setGameType(String newGameType) {
        this.gameType = newGameType;
    }

    public void resetTurnScore() {
        this.turnScore = 0;
    }

    public void incrementTurnScore() {
        this.turnScore++;
        if(this.turnScore > this.roundScore)
            this.roundScore = this.turnScore;
    }

    public void checkHighscore(int currentScore, int levelNum) {
        if(currentScore > highscores.getInteger(this.gameType+"highscore", 0)) {
            highscores.flush();
            highscores.putInteger(this.gameType+"highscore", currentScore);
            highscores.flush();
            highscores.putInteger(this.gameType+"levelNum", levelNum);
            highscores.flush();
        }
    }

    public void drawScore(Batch batch, int levelScore) {
        int currentScore = this.levelScore+this.roundScore;
        int highTurnScore = highscores.getInteger(this.gameType+"highscore", 0);
        int highLevelNum = highscores.getInteger(this.gameType+"levelNum", 0);

        String scoreText = "score: "+currentScore+"    level: "+levelScore;
        String highscoreText = "HIGH SCORE: "+highTurnScore+"    level: "+highLevelNum;

        textLayout.setText(theFont, scoreText);
        theFont.draw(batch, scoreText, this.x-textLayout.width-(this.x/27), this.y/*-(this.y/81)*/);

        checkHighscore(currentScore, levelScore);

        textLayout.setText(theFont2, highscoreText);
        theFont2.draw(batch, highscoreText, this.x - textLayout.width - (this.x / 27), this.y /*- (this.y / 81)*/ - theFont.getLineHeight());
    }

    public void drawHint(Batch batch) {
        String theHint1 = "Repeat the pattern";
        String theHint2 = "in the order it was shown.";

        textLayout.setText(theFont, theHint1);
        theFont.draw(batch, theHint1, (this.x-textLayout.width)/2, this.y-(textLayout.height+(this.y/81))*3);

        textLayout.setText(theFont, theHint2);
        theFont.draw(batch, theHint2, (this.x-textLayout.width)/2, this.y-(textLayout.height+(this.y/81))*4);
    }

    public void drawHint2(Batch batch, int repeatNo) {
        String theHint1, theHint2;
        if( repeatNo > 1 ) {
            theHint1="If stumped, use one of";
            theHint2 = "of your "+repeatNo+ " repeats.";
        }
        else {
            theHint1="If stumped, use";
            theHint2 = "your "+repeatNo+ " repeat.";
        }

        textLayout.setText(theFont, theHint1);
        theFont.draw(batch, theHint1, (this.x-textLayout.width)/2, this.y-(textLayout.height+(this.y/81))*3);

        textLayout.setText(theFont, theHint2);
        theFont.draw(batch, theHint2, (this.x-textLayout.width)/2, this.y-(textLayout.height+(this.y/81))*4);
    }

    public void drawHighScore(Batch batch) {
        int highTurnScore = highscores.getInteger(this.gameType+"highscore", 7);
        int highLevelNum = highscores.getInteger(this.gameType+"levelNum", 7);
        String scoreText = "HIGH SCORE: "+highTurnScore+" (lvl "+highLevelNum+")";
        textLayout.setText(theFont2, scoreText);
        //theFont.draw(batch, scoreText, this.x-textLayout.width-(this.x/27), this.y-(this.y/81));
        theFont2.draw(batch, scoreText, this.x-textLayout.width-(this.x/27), this.y-(this.y/81));
    }

    public void drawReplayNum(Batch batch, Replay replay, int num) {
        textLayout.setText(theFont2, ""+num);
        theFont2.draw(batch, ""+num, replay.getX1()+(replay.getX2()-replay.getX1())/2 - textLayout.width/2, replay.getY1()+(replay.getY2()-replay.getY1())/2 );
    }

    public int getTurnScore() {
        return this.turnScore;
    }

    public String getGameType() { return this.gameType; }

    public FreeTypeFontGenerator getFontGenerator() {
        return generator;
    }

    public void drawButtonText(Batch batch, String text, float xMid, float yMid, int fontSize) {
        parameter.size= fontSize;
        parameter.color= new Color(230,230, 255, 1);
        font = generator.generateFont(parameter);
        textLayout.setText(theFont, text);
        font.draw(batch, text, xMid - textLayout.width/2, yMid - font.getLineHeight()/2);
    }

    public int getAdCount() {
        return highscores.getInteger("adCount", 0);
    }

    public void setAdCount(int adCount) {
        highscores.putInteger("adCount", adCount+1);
        highscores.flush();
    }
    public float getVolume() {
        return highscores.getFloat("volume", 1);
    }

    public void setVolume(float volume) {
        highscores.putFloat("volume", volume);
        highscores.flush();
    }
}
