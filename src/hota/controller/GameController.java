package controller;

import model.Ability;
import model.GameModel;
import model.Hero;
import model.Resettable;
import util.BattleLogger;

import javax.swing.Timer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController implements Resettable {

    private final GameModel model;
    private final Random random;
    private final List<GameObserver> observers;

    private Path savedLogPath;

    private static final int AI_DELAY_MS = 1200;

    public GameController(GameModel model) {
        this.model = model;
        this.random = new Random();
        this.observers = new ArrayList<>();
    }

    public void addObserver(GameObserver observer) { observers.add(observer); }
    public void removeObserver(GameObserver observer) { observers.remove(observer); }

    private void notifyObservers() {
        for (GameObserver o : observers) o.onStateChanged();
    }
 
    public void startGame() {
        model.setGameOver(false);
        model.setPlayerWon(false);
        model.setCurrentTurn(1);
        model.setPlayerTurn(true);
        model.clearLog();
        log("═══════════════════════════════");
        log("   ⚔  HEROES OF THE ARENA  ⚔  ");
        log("═══════════════════════════════");
        log("  " + model.getPlayer().getName() + "  vs  " + model.getOpponent().getName());
        log("───────────────────────────────");
        log("Turn 1 — Your move!");
        notifyObservers();
    }

    public void playerAction(int actionIndex) {
        if (model.isGameOver() || !model.isPlayerTurn()) return;

        Hero player = model.getPlayer();
        Hero opponent = model.getOpponent();

        player.tickCooldowns();

        String result = switch (actionIndex) {
            case -1 -> player.attack(opponent);
            case -2 -> player.healSelf();
            case -3 -> player.defendStance();
            default -> {
                Ability chosen = player.getAbilities().get(actionIndex);
                yield player.useAbility(chosen, opponent);
            }
        };

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
        Hero player   = model.getPlayer();
        Hero opponent = model.getOpponent();

        opponent.tickCooldowns();
        log("─── " + opponent.getName() + "'s turn ───");

        List<Ability> available = new ArrayList<>();
        for (Ability a : opponent.getAbilities()) {
            if (a.isReady() && opponent.getMana() >= a.getManaCost()) {
                available.add(a);
            }
        }

        boolean lowHp = opponent.getHp() < opponent.getMaxHp() * 0.4;
        String result;

        if (lowHp && random.nextInt(10) < 3) {
            result = opponent.defendStance();
        } else if (!available.isEmpty() && random.nextBoolean()) {
            Ability chosen = available.get(random.nextInt(available.size()));
            result = opponent.useAbility(chosen, player);
        } else {
            result = opponent.attack(player);
        }

        log(result);

        if (!checkWinCondition()) {
            model.incrementTurn();
            model.setPlayerTurn(true);
            log("─── Turn " + model.getCurrentTurn() + " — Your move! ───");
        }
    }

    public boolean checkWinCondition() {
        if (!model.getOpponent().isAlive()) {
            model.setGameOver(true);
            model.setPlayerWon(true);
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log("🏆  " + model.getPlayer().getName() + " WINS!");
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            saveBattleLog();
            return true;
        }
        if (!model.getPlayer().isAlive()) {
            model.setGameOver(true);
            model.setPlayerWon(false);
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log("💀  " + model.getPlayer().getName() + " was defeated...");
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            saveBattleLog();
            return true;
        }
        return false;
    }

    private void saveBattleLog() {
        try {
            savedLogPath = BattleLogger.saveBattleLog(
                    model.getPlayer().getName(),
                    model.getOpponent().getName(),
                    model.getCombatLog(),
                    model.isPlayerWon());
        } catch (BattleLogger.BattleLogException e) {
            model.appendLog("⚠ Could not save battle log: " + e.getMessage());
        }
    }

    @Override
    public void reset() {
        model.reset();
        notifyObservers();
    }

    public GameModel getModel(){return model;}
    public Hero getPlayer(){return model.getPlayer();}
    public Hero getOpponent(){return model.getOpponent();}
    public boolean isGameOver(){return model.isGameOver();}
    public boolean isPlayerWon(){return model.isPlayerWon();}
    public boolean isPlayerTurn(){return model.isPlayerTurn();}
    public List<String> getCombatLog(){return model.getCombatLog();}
    public int getCurrentTurn(){return model.getCurrentTurn();}
    public Path getSavedLogPath(){return savedLogPath;}

    private void log(String message) {
        model.appendLog(message);
    }
}