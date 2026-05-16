package bmt.controller;

import bmt.model.Ability;
import bmt.model.GameModel;
import bmt.model.Hero;
import bmt.model.Resettable;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController implements Resettable {

    private final GameModel model;
    private final Random random;
    private final List<GameObserver> observers;

    private static final int AI_DELAY_MS = 1000;

    public GameController(GameModel model) {
        this.model = model;
        this.random = new Random();
        this.observers = new ArrayList<>();
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (GameObserver o : observers)
            o.onStateChanged();
    }

    public void startGame() {
        model.setGameOver(false);
        model.setCurrentTurn(1);
        model.setPlayerTurn(true);
        model.clearLog();
        log("=== HEROES OF THE ARENA ===");
        log("Battle Start!  " + model.getPlayer().getName() + "  vs  " + model.getOpponent().getName());
        log("─── Turn 1 ───");
        notifyObservers();
    }

    public void playerAction(int actionIndex) {
        if (model.isGameOver() || !model.isPlayerTurn())
            return;

        Hero player = model.getPlayer();
        Hero opponent = model.getOpponent();

        player.tickCooldowns();

        String result;
        if (actionIndex == -1) {
            result = player.attack(opponent);
        } else if (actionIndex == -2) {
            result = player.defend();
        } else {
            Ability chosen = player.getAbilities().get(actionIndex);
            result = player.useAbility(chosen, opponent);
        }
        log(result);

        if (checkWinCondition()) {
            notifyObservers();
            return;
        }

        model.setPlayerTurn(false);
        notifyObservers();

        Timer aiTimer = new Timer(AI_DELAY_MS, e -> {
            aiTurn();
            notifyObservers();
        });
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    private void aiTurn() {
        Hero player = model.getPlayer();
        Hero opponent = model.getOpponent();

        opponent.tickCooldowns();
        log("─── " + opponent.getName() + "'s turn ───");

        List<Ability> available = new ArrayList<>();
        for (Ability a : opponent.getAbilities()) {
            if (a.isReady() && opponent.getMana() >= a.getManaCost()) {
                available.add(a);
            }
        }

        String result;
        if (!available.isEmpty() && random.nextBoolean()) {
            Ability chosen = available.get(random.nextInt(available.size()));
            result = opponent.useAbility(chosen, player);
        } else {
            result = opponent.attack(player);
        }
        log(result);

        if (!checkWinCondition()) {
            model.incrementTurn();
            model.setPlayerTurn(true);
            log("─── Turn " + model.getCurrentTurn() + " ───");
        }
    }

    public boolean checkWinCondition() {
        Hero player = model.getPlayer();
        Hero opponent = model.getOpponent();

        if (!opponent.isAlive()) {
            model.setGameOver(true);
            log("🏆  " + player.getName() + " WINS!  "
                    + opponent.getName() + " has been defeated!");
            return true;
        }
        if (!player.isAlive()) {
            model.setGameOver(true);
            log("💀  GAME OVER!  " + player.getName() + " has been defeated!");
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        model.reset();
        notifyObservers();
    }

    public GameModel getModel() {
        return model;
    }

    public Hero getPlayer() {
        return model.getPlayer();
    }

    public Hero getOpponent() {
        return model.getOpponent();
    }

    public boolean isGameOver() {
        return model.isGameOver();
    }

    public boolean isPlayerTurn() {
        return model.isPlayerTurn();
    }

    public List<String> getCombatLog() {
        return model.getCombatLog();
    }

    public int getCurrentTurn() {
        return model.getCurrentTurn();
    }

    private void log(String message) {
        model.appendLog(message);
        System.out.println(message);
    }
}