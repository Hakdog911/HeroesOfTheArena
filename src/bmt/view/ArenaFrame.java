package bmt.view;

import bmt.controller.GameController;
import bmt.model.GameModel;
import bmt.model.Hero;

import javax.swing.*;

public class ArenaFrame extends JFrame {

    public ArenaFrame(Hero player, Hero opponent) {
        super("Heroes of the Arena — " + player.getName() + " vs " + opponent.getName());

        GameModel model = new GameModel(player, opponent);
        GameController controller = new GameController(model);
        ArenaPanel arenaPanel = new ArenaPanel(controller);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 640);
        setLocationRelativeTo(null);
        add(arenaPanel);
        setVisible(true);

        controller.startGame();
    }
}
