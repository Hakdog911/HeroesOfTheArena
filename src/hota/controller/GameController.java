package hota.controller;

import hota.model.Ability;
import hota.model.GameModel;
import hota.model.Hero;
import hota.model.Resettable;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GameController
 * Handles all game logic: turn management, win/loss detection, and AI.
 *
 * MVC role:
 * • Receives player input actions from the View
 * • Mutates the Model
 * • Notifies registered GameObservers when state changes
 * • Zero Swing/UI imports except javax.swing.Timer (needed for EDT-safe delay)
 *
 * Player actions:
 * -1 = basic attack
 * -2 = heal
 * -3 = defend (damage-reduction shield)
 * 0+ = ability index
 */
public class GameController implements Resettable {

    private final GameModel model;
    private final Random random;
    private final List<GameObserver> observers;

    /** Delay in ms between the player's action and the AI's response. */
    private static final int AI_DELAY_MS = 1200;

    public GameController(GameModel model) {
        this.model = model;
        this.random = new Random();
        this.observers = new ArrayList<>();
    }

    // ── Observer management ────────────────────────────────────────────────────

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

    // ── Game lifecycle ─────────────────────────────────────────────────────────

    public void startGame() {
        model.setGameOver(false);
        model.setPlayerWon(false);
        model.setCurrentTurn(1);
        model.setPlayerTurn(true);
        model.clearLog();
        log("═══════════════════════════════");
        log("   ⚔  HEROES OF THE ARENA  ⚔  ");
        log("═══════════════════════════════");
        log("  " + model.getPlayer().getName()
                + "  vs  " + model.getOpponent().getName());
        log("───────────────────────────────");
        log("\nTurn 1 — Your move!");
        notifyObservers();
    }

    /**
     * Processes the player's chosen action for this turn.
     *
     * @param actionIndex  -1 = attack | -2 = heal | -3 = defend | 0+ = ability
     */
    public void playerAction(int actionIndex) {
        if (model.isGameOver() || !model.isPlayerTurn()) return;

        Hero player   = model.getPlayer();
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

        // Disable buttons immediately, then run AI after delay
        model.setPlayerTurn(false);
        notifyObservers();

        Timer aiTimer = new Timer(AI_DELAY_MS, e -> {
            aiTurn();
            notifyObservers();
        });
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    // ── AI turn ────────────────────────────────────────────────────────────────

    private void aiTurn() {
        Hero player = model.getPlayer();
        Hero opponent = model.getOpponent();

        opponent.tickCooldowns();
        log("\n─── " + opponent.getName() + "'s turn ───");

        // Collect usable abilities
        List<Ability> available = new ArrayList<>();
        for (Ability a : opponent.getAbilities()) {
            if (a.isReady() && opponent.getMana() >= a.getManaCost()) {
                available.add(a);
            }
        }

        // 30% chance to defend if HP is below 40%
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
            log("\n─── Turn " + model.getCurrentTurn() + " — Your move! ───");
        }
    }

    // ── Win condition ──────────────────────────────────────────────────────────

    public boolean checkWinCondition() {
        if (!model.getOpponent().isAlive()) {
            model.setGameOver(true);
            model.setPlayerWon(true);
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log("🏆  " + model.getPlayer().getName() + " WINS!");
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return true;
        }
        if (!model.getPlayer().isAlive()) {
            model.setGameOver(true);
            model.setPlayerWon(false);
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log("💀  " + model.getPlayer().getName() + " was defeated...");
            log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return true;
        }
        return false;
    }

    // ── Resettable ─────────────────────────────────────────────────────────────

    @Override
    public void reset() {
        model.reset();
        notifyObservers();
    }

    // ── Read-through accessors for the View ────────────────────────────────────

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

    public boolean isPlayerWon() {
        return model.isPlayerWon();
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

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void log(String message) {
        model.appendLog(message);
        System.out.println(message);
    }
}
