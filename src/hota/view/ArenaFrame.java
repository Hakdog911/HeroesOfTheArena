package hota.view;

import hota.controller.GameController;
import hota.controller.GameObserver;
import hota.model.GameModel;
import hota.model.Hero;

import javax.swing.*;

/**
 * ArenaFrame
 * Owns the arena JFrame and the MVC stack.
 * Listens for game-over events and shows BattleResultDialog.
 */
public class ArenaFrame extends JFrame implements GameObserver {

    private GameController controller;
    private ArenaPanel arenaPanel;
    private boolean resultShown = false;

    public ArenaFrame(Hero player, Hero opponent) {
        buildArena(player, opponent);
    }

    private void buildArena(Hero player, Hero opponent) {
        resultShown = false;

        // Dispose any previous content
        getContentPane().removeAll();

        // ── Wire MVC ──────────────────────────────────────────────────────────
        GameModel model = new GameModel(player, opponent);
        controller = new GameController(model);
        arenaPanel = new ArenaPanel(controller);

        controller.addObserver(this); // ArenaFrame watches for game-over

        setTitle("Heroes of the Arena — " + player.getName() + " vs " + opponent.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 660);
        setLocationRelativeTo(null);
        add(arenaPanel);

        revalidate();
        repaint();
        setVisible(true);

        controller.startGame();
    }

    // ── GameObserver ───────────────────────────────────────────────────────────

    @Override
    public void onStateChanged() {
        if (!controller.isGameOver() || resultShown)
            return;
        resultShown = true;

        // Small delay so the final log line is visible before the popup
        Timer t = new Timer(600, e -> showResultDialog());
        t.setRepeats(false);
        t.start();
    }

    private void showResultDialog() {
        Hero player = controller.getPlayer();
        Hero opponent = controller.getOpponent();
        boolean playerWon = controller.isPlayerWon();

        BattleResultDialog dialog = new BattleResultDialog(
                this,
                playerWon,
                player.getName(),
                player.getHeroClass(),
                opponent.getHeroClass(),
                newOpponent -> {
                    if (newOpponent == null) {
                        // Return to hero selection
                        dispose();
                        new HeroSelectionFrame();
                    } else {
                        // Rematch or next opponent — keep same player, reset HP/mana
                        player.reset();
                        buildArena(player, newOpponent);
                    }
                });

        dialog.setVisible(true);
    }

    // ── Fullscreen toggle ──────────────────────────────────────────────────────

    private void toggleFullscreen() {
        java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        if (gd.getFullScreenWindow() == null) {
            dispose();
            setUndecorated(true);
            gd.setFullScreenWindow(this);
        } else {
            gd.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setSize(780, 660);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }
}
