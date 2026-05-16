package bmt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameModel implements Resettable {

    private final Hero player;
    private final Hero opponent;
    private int currentTurn;
    private boolean gameOver;
    private boolean playerTurn;
    private final List<String> combatLog;

    public GameModel(Hero player, Hero opponent) {
        this.player = player;
        this.opponent = opponent;
        this.currentTurn = 1;
        this.gameOver = false;
        this.playerTurn = true;
        this.combatLog = new ArrayList<>();
    }

    public void appendLog(String message) {
        combatLog.add(message);
    }

    public void clearLog() {
        combatLog.clear();
    }

    public void setGameOver(boolean v) {
        gameOver = v;
    }

    public void setPlayerTurn(boolean v) {
        playerTurn = v;
    }

    public void incrementTurn() {
        currentTurn++;
    }

    public void setCurrentTurn(int v) {
        currentTurn = v;
    }

    public Hero getPlayer() {
        return player;
    }

    public Hero getOpponent() {
        return opponent;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public List<String> getCombatLog() {
        return Collections.unmodifiableList(combatLog);
    }

    @Override
    public void reset() {
        player.reset();
        opponent.reset();
        currentTurn = 1;
        gameOver = false;
        playerTurn = true;
        combatLog.clear();
    }
}