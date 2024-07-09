package com.ph_3nix.memorygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.ph_3nix.memorygame.buttons.ButtonBoard;
import com.ph_3nix.memorygame.buttons.Menu;
import com.ph_3nix.memorygame.gameplay.Scoreboard;

import java.util.ArrayList;

public class MemoryGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture menuBackground, selected, unselected, memoryTexture, squaredTexture, audio, audio_no;
	OrthographicCamera camera;

	ArrayList<Menu> mainMenu;
	ButtonBoard buttonBoard;
	Scoreboard scoreboard;
	int levelNum, repeatNum, completedIteration;
	int pTurnNum, pTurnSleepTime, cTurnNum, miniTurnNum, adCount, assetCount;
	float blinkTime, adYOffset, squaredY, startTime, volume;
	boolean playerTurn, memoryMode, easyMode, speedMode, completed, squaredDown, playCSfx;
	String gametype, gamemode;
	Sound playerMoveSfx, shoveInitSfx, gameOverSfx;
	//ShapeRenderer menuBackground;


	private void loadAssets() {
		menuBackground = new Texture("menu-color.png");
		selected = new Texture("easy.png");
		unselected = new Texture("hard.png");
		audio = new Texture("audio.png");
		audio_no = new Texture("audio_no.png");

		mainMenu = new ArrayList<Menu>();
		scoreboard = new Scoreboard();
		playerMoveSfx = Gdx.audio.newSound(Gdx.files.internal("thwap.mp3"));
		shoveInitSfx = Gdx.audio.newSound(Gdx.files.internal("flung.mp3"));
		gameOverSfx = Gdx.audio.newSound(Gdx.files.internal("oops.mp3"));
		scoreboard.newScoreboard(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 12 * 6 /*- (Gdx.graphics.getHeight()/6/6)*/, gamemode+gametype);
		adCount = scoreboard.getAdCount();
		volume = scoreboard.getVolume();

		mainMenu.add(new Menu(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/2 /3, 1, 0.0833333f, 0.0833333f, "play", scoreboard.getFontGenerator(), true, false, 1));
		mainMenu.add(new Menu(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/2 /3, 0, 0, 0.2499999f, "pH03 Games", scoreboard.getFontGenerator(), true, false, 4));
		mainMenu.add(new Menu(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/2 /3/3/2, 13, 0.0833333f, 0, "hard", scoreboard.getFontGenerator(), false, false, 5));
		mainMenu.add(new Menu(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/2 /3/3/2, 15, 0.0833333f, 0, "easy", scoreboard.getFontGenerator(), false, false, 6));
		mainMenu.add(new Menu(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/2 /3/3, 8, 0.0277777f, 0.1666666f, "audio", scoreboard.getFontGenerator(), false, volume == 0 ? true : false, 3));
		mainMenu.add(new Menu(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/2 /6, 6, 0, 0, "tabs", scoreboard.getFontGenerator(), true, false, 2));

	}
	
	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		batch = new SpriteBatch();

		levelNum = 0;
		blinkTime = 2;
		pTurnSleepTime = 500;
		adYOffset = 1;   // change to 1 when no ads, (10/11) when yes ads
		buttonBoard = new ButtonBoard();
		gametype = "easy";   // change to variable logic when multiple gametypes
		gamemode = "size";
		easyMode = true;
		speedMode = false;
		squaredDown = true;
		playCSfx = false;
		Gdx.input.setCatchBackKey(true);
		squaredY = Gdx.graphics.getHeight()* /*0.625*/ 0.666666667f;

		memoryTexture = new Texture("memory_text.png");
		squaredTexture = new Texture("squared_text.png");
		assetCount = 0;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(19/255f, 0, 38/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if( assetCount < 2 ) {
			batch.draw(memoryTexture, 0, Gdx.graphics.getHeight()* /*0.70833333333f*/ 0.75f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()*0.1666666666f);
			batch.draw(squaredTexture, 0, squaredY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()*0.1666666666f);
			if(assetCount == 1)
				loadAssets();
			assetCount++;
		}
		else {
			if(Gdx.input.isKeyPressed(Input.Keys.BACK) && levelNum != 0 )
				gameover();

			if (levelNum == 0) {
				gameMenuLogic();
			} else {  // create new game board - new level
				if (completed) {  // animation on level completed
					if (completedIteration < buttonBoard.getNewOrder().length) {
						blinkTime += Gdx.graphics.getDeltaTime();
						if (blinkTime > 0) {
							buttonBoard.shoveFall(completedIteration);
							shoveInitSfx.play(volume);
							completedIteration++;
							blinkTime = -0.15f;
						}
					}
					if (buttonBoard.checkAllOffscreen() == buttonBoard.getNewOrder().length) {
						completed = false;
						blinkTime = 2;
						if (adCount >= (speedMode ? 14 : 7)) {   // displays ad every 8th level completion (15th for speedmode)
							//requestHandler.showOrLoadInterstitual();
							adCount = 0;
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
							}
							;
						} else
							adCount++;
							scoreboard.setAdCount(adCount);
					}
					scoreboard.drawScore(batch, levelNum);
					scoreboard.drawReplayNum(batch, buttonBoard.getReplay(), repeatNum);
					buttonBoard.drawBoard(batch);
				} else if (!playerTurn && pTurnNum != 0) {
					createNewBoard();
				} else {
					if (blinkTime < 1) {  //blinks what was the correct tile to player
						blinkCorrectButton();
					} else {
						if (!playerTurn && memoryMode) {   // pc iterates steps first
							iteratePcTurn();
						} else {
							startTime += Gdx.graphics.getDeltaTime();
							if (startTime > 3 && levelNum == 1 && pTurnNum < 1 && ((int) startTime + 1) % 2 == 0)
								scoreboard.drawHint(batch);
							else if (startTime > 5 && levelNum > 1 && repeatNum > 0)
								scoreboard.drawHint2(batch, repeatNum);
							if (Gdx.input.justTouched()) {  // if touched - logic for move correct, wrong, or level complete
								float theX = Gdx.input.getX();
								float theY = Gdx.graphics.getHeight() - Gdx.input.getY();
								if (buttonBoard.checkReplay(theX, theY) && repeatNum > 0) {
									reIteratePcTurn();   // player has asked for pc to repeat the pattern
								} else {
									int touchResult = buttonBoard.checkPress(theX, theY, pTurnNum);
									if (touchResult == 1) {  // player is correct
										correctButtonTouched();
									} else if (touchResult == 2) {   // player is wrong
										gameOverSfx.play(volume);
										buttonBoard.askewWrong(theX, theY);
										blinkTime = 0;
										adCount++;
										scoreboard.setAdCount(adCount);
									}
								}
							}
						}
					}
					scoreboard.drawScore(batch, levelNum);
					scoreboard.drawReplayNum(batch, buttonBoard.getReplay(), repeatNum);
					buttonBoard.drawBoard(batch);
				}
			}
		}

		camera.update();

		batch.end();
		//menuBackground.begin(ShapeRenderer.ShapeType.Filled);
		//menuBackground.setColor((float) 204/255, (float) 241/255, 1, 1);
		//menuBackground.rect(0,0,Gdx.graphics.getWidth(), (float) (Gdx.graphics.getHeight()*0.6666666));
		//menuBackground.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		//background.dispose();
		buttonBoard.dispose();
		scoreboard.dispose();
		for (Menu menu : mainMenu) {
			menu.dispose();
		}
		menuBackground.dispose();
		selected.dispose();
		unselected.dispose();
		audio.dispose();
		audio_no.dispose();
		memoryTexture.dispose();
		squaredTexture.dispose();
		playerMoveSfx.dispose();
		shoveInitSfx.dispose();
	}


	/*************************************************************************************************/
	/*************************		main game-loop logic functions		******************************/
	void beginNewGame() {
		//scoreboard.dispose();   // dispose highscore board
		//requestHandler.showOrLoadInterstitual();
		levelNum = 1;
		repeatNum = 0;
		playerTurn = false;
		pTurnNum = -1;
		memoryMode = true;
		completed = false;
		pTurnSleepTime = 500;
		startTime = 0;
		scoreboard.newScoreboard(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/**10/11*/, gamemode+gametype);
	}

	void gameover() {
		//scoreboard.dispose();  // dispose score board
		levelNum = 0;
		squaredDown = true;
		squaredY = Gdx.graphics.getHeight()*0.625f;
		scoreboard.newScoreboard(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 12 * 6 - (Gdx.graphics.getHeight()/6/6), gamemode+gametype);

	}

	public void gameMenuLogic() {
		batch.draw(menuBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()*0.5f);
		batch.draw(memoryTexture, 0, Gdx.graphics.getHeight()* /*0.70833333333f*/ 0.75f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()*0.1666666666f);
		batch.draw(squaredTexture, 0, squaredY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()*0.1666666666f);

		// game menu "squared" png placement logic
		if(squaredDown) {
			if(squaredY < Gdx.graphics.getHeight()*0.52f) {
				squaredDown = false;
				squaredY += (60*Gdx.graphics.getDeltaTime() > 1.3f ? 1.3f : 60*Gdx.graphics.getDeltaTime());
			}
			else {
				squaredY -= (60*Gdx.graphics.getDeltaTime() > 1.3f ? 1.3f : 60*Gdx.graphics.getDeltaTime());
			}
		}
		else {
			if(squaredY > Gdx.graphics.getHeight()*0.666666667f) {
				squaredDown = true;
				squaredY -= (60*Gdx.graphics.getDeltaTime() > 1.3f ? 1.3f : 60*Gdx.graphics.getDeltaTime());
			}
			else {
				squaredY += (60*Gdx.graphics.getDeltaTime() > 1.3f ? 1.3f : 60*Gdx.graphics.getDeltaTime());
			}
		}

		for (Menu menu : mainMenu) {
			menu.drawButton(batch);
		}
		scoreboard.drawHighScore(batch);
		float theX = Gdx.input.getX();
		float theY = Gdx.graphics.getHeight() - Gdx.input.getY();
		if (Gdx.input.justTouched()) {
			for (Menu menuButton : mainMenu) {
				if (theX > menuButton.getX1() && theX < menuButton.getX2() && theY > menuButton.getY1() && theY < menuButton.getY2()) {
					switch (menuButton.getId()) {
						case 1 :
							beginNewGame();
							break;
						case 2 :
							if (theX > Gdx.graphics.getWidth() / 2) {
								if (!speedMode) {
									speedMode = true;
									gamemode = "speed";
									menuButton.flipHor();
								}
							} else {
								if (speedMode) {
									speedMode = false;
									gamemode = "size";
									menuButton.flipHor();
								}
							}
							scoreboard.setGameType(gamemode + gametype);
							break;
						case 3 :
							if (volume > 0) {
								volume = 0;
								scoreboard.setVolume(volume);
								menuButton.newTexture(audio_no);
							} else {
								volume = 1;
								scoreboard.setVolume(volume);
								menuButton.newTexture(audio);
							}
							break;
						case 4 :
							Gdx.net.openURI("https://www.amazon.com/s/ref=bl_dp_s_web_0?ie=UTF8&search-alias=aps&field-brandtextbin=pH03+Games&node=2350149011");
							break;
						default :
							gametype = menuButton.getName();
							for (Menu deselectButton : mainMenu) {
								if (deselectButton.getName() == "easy" || deselectButton.getName() == "hard") {
									deselectButton.newTexture(unselected);
									deselectButton.setFontSize(1 / 1.33333f);
								}
							}
							menuButton.newTexture(selected);
							menuButton.setFontSize(1.33333f);
							easyMode = (gametype == "easy" ? true : false);
							scoreboard.setGameType(gamemode + gametype);
					}
				}
			}
		}
	}

	public void createNewBoard() {

		if( speedMode)
			buttonBoard.newBoard(2, 3);
		else
			buttonBoard.newBoard(levelNum / 2 + 1, (int) (levelNum / 2 + ((levelNum % 2 == 0) ? 0 : 1)) + 1);
		buttonBoard.drawBoard(batch);
		scoreboard.drawScore(batch, levelNum);
		scoreboard.drawReplayNum(batch, buttonBoard.getReplay(), repeatNum);
		cTurnNum = 0;
		pTurnNum = 0;
		miniTurnNum = 3;
		startTime = 0;
	}

	public void blinkCorrectButton() {
		buttonBoard.blinkCorrectAwns(pTurnNum);
		blinkTime = blinkTime+Gdx.graphics.getDeltaTime();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		if( blinkTime >= 1)
			gameover();
	}

	public void iteratePcTurn() {
		if(playCSfx) {
			playerMoveSfx.play(volume*0.7f, 2, 0);
			playCSfx = false;
		}
		int maxLen;

		if (easyMode)
			if (levelNum > 1 || speedMode)
				maxLen = miniTurnNum;
			else
				maxLen = 2;
		else
			maxLen = buttonBoard.getNewOrder().length;

		if (cTurnNum == 0) {
			if(speedMode) {
				try {
					Thread.sleep(500-pTurnSleepTime-((int) Gdx.graphics.getDeltaTime()*1000));
				}
				catch (InterruptedException e) {}
			}
			buttonBoard.untouchAll();
			cTurnNum++;
		} else if (cTurnNum-1 == maxLen) {
			if(speedMode) {
				try {
					Thread.sleep(500-pTurnSleepTime-((int) Gdx.graphics.getDeltaTime()*1000));
				}
				catch (InterruptedException e) {}
			}
			playerTurn = true;
			buttonBoard.untouchAll();
		} else{
			buttonBoard.iterateTurn(cTurnNum-1);
			playCSfx = true;
			cTurnNum++;
		}

		if (cTurnNum != 0) {
			try {
				Thread.sleep(pTurnSleepTime-((int) Gdx.graphics.getDeltaTime()*1000));
			} catch (InterruptedException e) {
			}
		}
	}

	public void reIteratePcTurn() {
		pTurnNum = 0;
		cTurnNum = 0;
		startTime = 0;
		playerTurn = false;
		scoreboard.resetTurnScore();
		repeatNum--;
	}

	public void correctButtonTouched() {
		playerMoveSfx.play(volume);
		scoreboard.incrementTurnScore();
		pTurnNum++;
		startTime = 0;
		if (pTurnNum == buttonBoard.getNewOrder().length) {   // also, level complete
			scoreboard.setLevelScore(buttonBoard.getCols() * buttonBoard.getRows());
			repeatNum++;
			levelNum++;
			playerTurn = false;
			completed = true;
			completedIteration = 0;
			blinkTime = 0;
			if(speedMode && pTurnSleepTime > 50)
				pTurnSleepTime -= 200/levelNum;

		} else {
			if (easyMode && pTurnNum == miniTurnNum) {
				if(speedMode)
					miniTurnNum= (miniTurnNum+3 > buttonBoard.getNewOrder().length) ? buttonBoard.getNewOrder().length : miniTurnNum+3 ;
				else
					miniTurnNum= (miniTurnNum+2 > buttonBoard.getNewOrder().length) ? buttonBoard.getNewOrder().length : miniTurnNum+2 ;
				pTurnNum = 0;
				cTurnNum = 0;
				playerTurn = false;
				scoreboard.resetTurnScore();
			}
		}
	}
}
