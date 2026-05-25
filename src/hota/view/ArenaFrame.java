package view;

import controller.GameController;
import controller.GameObserver;
import model.GameModel;
import model.Hero;

import javax.swing.*;

public class ArenaFrame extends JFrame implements GameObserver {

    private GameController controller;
    private ArenaPanel arenaPanel;
    private boolean resultShown = false;

    public ArenaFrame(Hero player, Hero opponent) {
        buildArena(player, opponent);
    }

    private void buildArena(Hero player, Hero opponent) {
        resultShown = false;

        getContentPane().removeAll();

        GameModel model = new GameModel(player, opponent);
        controller = new GameController(model);
        arenaPanel = new ArenaPanel(controller);

        controller.addObserver(this);

        setTitle("Heroes of the Arena — " + player.getName() + " vs " + opponent.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 660);
        setLocationRelativeTo(null);
        add(arenaPanel);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F11"), "toggleFullscreen");
        getRootPane().getActionMap().put("toggleFullscreen",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        toggleFullscreen();
                    }
                });

        revalidate();
        repaint();
        setVisible(true);

        controller.startGame();
    }

    @Override
    public void onStateChanged() {
        if (!controller.isGameOver() || resultShown) return;
        resultShown = true;

        Timer t = new Timer(600, e -> showResultDialog());
        t.setRepeats(false);
        t.start();
    }

    private void showResultDialog() {
        Hero player = controller.getPlayer();
        Hero opponent = controller.getOpponent();
        boolean playerWon = controller.isPlayerWon();
        String logPath = controller.getSavedLogPath() != null
                ? controller.getSavedLogPath().toAbsolutePath().toString()
                : null;

        BattleResultDialog dialog = new BattleResultDialog(this, playerWon, player.getName(), player.getHeroClass(), opponent.getHeroClass(), logPath, newOpponent -> {
                    if (newOpponent == null) {
                        dispose();
                        new HeroSelectionFrame();
                    } else {
                        player.reset();
                        buildArena(player, newOpponent);
                    }
                });

        dialog.setVisible(true);
    }

    private void toggleFullscreen() {
        java.awt.GraphicsDevice gd =
                java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
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