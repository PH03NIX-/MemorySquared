package com.ph_3nix.memorygame.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Created by david on 2/8/2018.
 */

public class ButtonBoard {

    private float boardWidth, boardHeight;
    protected ArrayList<Button> buttons;
    protected Replay replayButton;
    private int colNum, rowNum;
    private int[] newOrder;
    private Texture buttonTexture;
    private Texture buttonTexturePressed;
    private Texture replayTexture;

    public ButtonBoard() {
        buttonTexture = new Texture("tile.png");
        buttonTexturePressed = new Texture("tile-pressed.png");
        replayTexture = new Texture("replay.png");
    }

    public void newBoard(int cols, int rows) {
        buttons = new ArrayList<Button>();

        this.colNum = cols;
        this.rowNum = rows;
        boardWidth = Gdx.graphics.getWidth();
        boardHeight = Gdx.graphics.getHeight()/2;
        Random random = new Random();

        int orderNum = 0;
        for( float y=0; y < boardHeight; y = y + boardHeight/rows ) {
            for( float x = 0; x < boardWidth; x = x+boardWidth/cols) {
                buttons.add( new Button(boardWidth/cols,boardHeight/rows, x, y, orderNum, buttonTexture, buttonTexturePressed));
                orderNum++;
            }
        }
        newOrder = new int[cols*rows];
        Arrays.fill(newOrder, -1);
        for( int i = 0; i < cols*rows; i++ ) {
            int x;
            for(x = random.nextInt( cols*rows); doesArrayContain(newOrder, x); x = random.nextInt( cols*rows) ) {
            }
            newOrder[i] = x;
        }

        replayButton = new Replay( (float) (boardHeight*0.08333325), (float) (boardHeight+(boardHeight*0.08333325)), boardWidth/6, boardHeight/6, replayTexture);
    }

    private boolean doesArrayContain(int[] theArray, int theNum) {
        boolean hasIt = false;
        for( int i=0; i < theArray.length; i++ ) {
            if( theArray[i] == theNum ) {
                hasIt = true;
            }
        }
        return hasIt;
    }


    public void drawBoard(Batch batch) {
        replayButton.drawReplay(batch);
        Collections.sort(buttons);
        for( Button button : buttons)
            button.drawButtons(batch);
    }

    public void untouchAll() {
        for( Button button : buttons)
            button.setTouched(false);
    }

    public void iterateTurn(int turnNum) {
        for(Button button : buttons ) {
            if(button.getOrder() == newOrder[turnNum]) {
                button.setTouched();
            }
        }
    }

    public void shoveFall(int turnNum) {
        for(Button button : buttons ) {
            if(button.getOrder() == newOrder[turnNum]) {
                button.setZIndex(turnNum);
                button.setFalling(true);
            }
        }
    }

    public int checkAllOffscreen() {
        int allGone = 0;
        for(Button button : buttons ) {
            allGone += button.checkOffscreen();
        }
        return allGone;
    }

    public int checkPress(float theX, float theY, int turnNum) {
        int result = 0;
        for( Button button : buttons) {
            if( !button.getTouched()) {
                if (theX > button.getX1() && theX < button.getX2() && theY > button.getY1() && theY < button.getY2()) {
                    result++;
                    if (newOrder[turnNum] != button.getOrder()) {
                        result++;
                    }
                    button.setTouched();
                }
            }
        }
        return result;
    }

    public void askewWrong(float theX, float theY) {
        int skewRight, skewUp;
        skewRight = -1;
        skewUp = -1;
        if( theX < boardWidth/2 ) {
            skewRight = 1;
        }
        if( theY < boardHeight/2) {
            skewUp = 1;
        }
        for( Button button : buttons) {
            if (theX > button.getX1() && theX < button.getX2() && theY > button.getY1() && theY < button.getY2()) {
                button.setAskew(skewRight, skewUp);
            }
        }
    }

    public void blinkCorrectAwns(int turnNum) {
        for( Button button : buttons) {
            if( button.getOrder() == newOrder[turnNum])
                button.blink();
        }
    }

    public int[] getNewOrder() {
        return newOrder;
    }

    public int getCols() {
        return this.colNum;
    }

    public int getRows() {
        return this.rowNum;
    }

    public void dispose() {
        buttonTexture.dispose();
        buttonTexturePressed.dispose();
        replayTexture.dispose();
    }

    public boolean checkReplay(float theX, float theY) {
        boolean doReplay = false;
        if (theX > replayButton.getX1() && theX < replayButton.getX2() && theY > replayButton.getY1() && theY < replayButton.getY2()) {
            doReplay = true;
        }
        return doReplay;
    }

    public Replay getReplay() {
        return replayButton;
    }

}
