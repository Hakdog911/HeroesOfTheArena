package bmt.view;

import bmt.controller.GameController;
import bmt.controller.GameObserver;
import bmt.model.Ability;
import bmt.model.Hero;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;

public class ArenaPanel extends JPanel implements Renderable, GameObserver {

    private final GameController controller;

    private JProgressBar playerHpBar;
    private JProgressBar playerManaBar;
    private JLabel playerStatsLabel;

    private JProgressBar opponentHpBar;
    private JProgressBar opponentManaBar;
    private JLabel opponentStatsLabel;

    private JButton[] abilityButtons;
    private JButton attackButton;
    private JButton defendButton;
    private JButton resetButton;

    private JTextArea combatLogArea;

    private static final Color BG = new Color(12, 12, 22);
    private static final Color PANEL_BG = new Color(22, 22, 40);
    private static final Color BORDER_C = new Color(60, 60, 100);
    private static final Color HP_C = new Color(50, 210, 80);
    private static final Color MANA_C = new Color(55, 120, 230);
    private static final Color CD_C = new Color(210, 70, 50);
    private static final Color READY_C = new Color(70, 55, 130);
    private static final Color TEXT = Color.WHITE;
    private static final Color SUBTEXT = new Color(160, 160, 210);
    private static final Color PLAYER_LBL = new Color(80, 220, 120);
    private static final Color ENEMY_LBL = new Color(220, 80, 80);
    private static final Color LOG_FG = new Color(170, 220, 170);

    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 21);
    private static final Font MONO_FONT = new Font("Monospaced", Font.PLAIN, 19);
    private static final Font BTN_FONT = new Font("SansSerif", Font.BOLD, 20);

    public ArenaPanel(GameController controller) {
        this.controller = controller;
        controller.addObserver(this);

        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        buildUI();
        render();
    }

    @Override
    public void render() {
        refresh();
    }

    @Override
    public void refresh() {
        Hero player = controller.getPlayer();
        Hero opponent = controller.getOpponent();
        boolean over = controller.isGameOver();

        updateBar(playerHpBar, player.getHp(), player.getMaxHp(), "HP");
        updateBar(playerManaBar, player.getMana(), player.getMaxMana(), "MP");
        updateBar(opponentHpBar, opponent.getHp(), opponent.getMaxHp(), "HP");
        updateBar(opponentManaBar, opponent.getMana(), opponent.getMaxMana(), "MP");

        playerStatsLabel.setText(player.getStats());
        opponentStatsLabel.setText(opponent.getStats());

        List<Ability> abilities = player.getAbilities();
        for (int i = 0; i < abilityButtons.length; i++) {
            Ability ab = abilities.get(i);
            boolean ready = ab.isReady() && player.getMana() >= ab.getManaCost();
            boolean onCd = !ab.isReady();

            Color bg = onCd ? CD_C : READY_C;
            abilityButtons[i].setBackground(bg);
            abilityButtons[i].setBorder(makeBorder(bg));

            String cdSuffix = onCd ? "  ⏳ " + ab.getCurrentCooldown() + "t" : "";
            abilityButtons[i].setText("✨ " + ab.getAbilityName() + "  (MP:" + ab.getManaCost() + ")" + cdSuffix);

            abilityButtons[i].setToolTipText(ab.getTooltip());

            abilityButtons[i].setEnabled(!over && ready);
        }

        attackButton.setEnabled(!over);
        defendButton.setEnabled(!over);

        combatLogArea.setText("");
        for (String line : controller.getCombatLog()) {
            combatLogArea.append(line + "\n");
        }
        combatLogArea.setCaretPosition(combatLogArea.getDocument().getLength());

        repaint();
    }

    @Override
    public void onStateChanged() {
        if (SwingUtilities.isEventDispatchThread()) {
            refresh();
        } else {
            SwingUtilities.invokeLater(this::refresh);
        }
    }

    private void buildUI() {
        JPanel statsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        statsRow.setBackground(BG);
        statsRow.add(buildHeroPanel(true));
        statsRow.add(buildHeroPanel(false));
        add(statsRow, BorderLayout.NORTH);

        combatLogArea = new JTextArea(10, 42);
        combatLogArea.setEditable(false);
        combatLogArea.setBackground(new Color(10, 10, 20));
        combatLogArea.setForeground(LOG_FG);
        combatLogArea.setFont(MONO_FONT);
        combatLogArea.setMargin(new Insets(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(combatLogArea);
        scroll.setBorder(titledBorder("⚔  Combat Log"));
        add(scroll, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        actionPanel.setBackground(BG);
        actionPanel.setBorder(titledBorder("⚡  Actions"));

        attackButton = makeButton("⚔  Basic Attack", new Color(160, 55, 55));
        attackButton.setToolTipText("<html><b>Basic Attack</b><br>Deals 12 flat damage. Always available.</html>");
        attackButton.addActionListener(e -> controller.playerAction(-1));

        defendButton = makeButton("🛡  Defend (+15 HP)", new Color(50, 95, 165));
        defendButton.setToolTipText("<html><b>Defend</b><br>Recover 15 HP. No mana cost.</html>");
        defendButton.addActionListener(e -> controller.playerAction(-2));

        resetButton = makeButton("🔄  Rematch", new Color(60, 60, 60));
        resetButton.setToolTipText("Reset the match and play again.");
        resetButton.addActionListener(e -> {
            controller.reset();
            controller.startGame();
        });

        List<Ability> abilities = controller.getPlayer().getAbilities();
        abilityButtons = new JButton[abilities.size()];
        for (int i = 0; i < abilities.size(); i++) {
            Ability ab = abilities.get(i);
            abilityButtons[i] = makeButton("✨ " + ab.getAbilityName()
                    + "  (MP:" + ab.getManaCost() + ")", READY_C);
            abilityButtons[i].setToolTipText(ab.getTooltip());
            final int idx = i;
            abilityButtons[i].addActionListener(e -> controller.playerAction(idx));
        }

        actionPanel.add(attackButton);
        actionPanel.add(defendButton);
        for (JButton btn : abilityButtons)
            actionPanel.add(btn);
        actionPanel.add(resetButton);

        add(actionPanel, BorderLayout.SOUTH);

        UIManager.put("ToolTip.font", new Font("SansSerif", Font.PLAIN, 12));
        UIManager.put("ToolTip.background", new Color(30, 30, 50));
        UIManager.put("ToolTip.foreground", new Color(210, 210, 240));
        ToolTipManager.sharedInstance().setInitialDelay(200);
        ToolTipManager.sharedInstance().setDismissDelay(8000);
    }

    private JPanel buildHeroPanel(boolean isPlayer) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        Hero hero = isPlayer ? controller.getPlayer() : controller.getOpponent();
        String prefix = isPlayer ? "👤  YOU — " : "🤖  ENEMY — ";

        JLabel nameLabel = new JLabel(prefix + hero.getName() + "  [" + hero.getHeroClass() + "]");
        nameLabel.setForeground(isPlayer ? PLAYER_LBL : ENEMY_LBL);
        nameLabel.setFont(TITLE_FONT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar hpBar = makeBar(HP_C, hero.getHp(), hero.getMaxHp(), "HP");
        JProgressBar manaBar = makeBar(MANA_C, hero.getMana(), hero.getMaxMana(), "MP");

        JLabel statsLabel = new JLabel(hero.getStats());
        statsLabel.setForeground(SUBTEXT);
        statsLabel.setFont(MONO_FONT);
        statsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (isPlayer) {
            playerHpBar = hpBar;
            playerManaBar = manaBar;
            playerStatsLabel = statsLabel;
        } else {
            opponentHpBar = hpBar;
            opponentManaBar = manaBar;
            opponentStatsLabel = statsLabel;
        }

        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(hpBar);
        panel.add(Box.createVerticalStrut(3));
        panel.add(manaBar);
        panel.add(Box.createVerticalStrut(5));
        panel.add(statsLabel);
        return panel;
    }

    private static JProgressBar makeBar(Color color, int value, int max, String label) {
        JProgressBar bar = new JProgressBar(0, max);
        bar.setValue(value);
        bar.setStringPainted(true);
        bar.setString(label + ": " + value + "/" + max);
        bar.setForeground(color);
        bar.setBackground(new Color(25, 25, 45));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        return bar;
    }

    private static void updateBar(JProgressBar bar, int value, int max, String label) {
        bar.setMaximum(max);
        bar.setValue(value);
        bar.setString(label + ": " + value + "/" + max);
    }

    private static JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(TEXT);
        btn.setFocusPainted(false);
        btn.setFont(BTN_FONT);
        btn.setBorder(makeBorder(bg));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static Border makeBorder(Color bg) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    private static javax.swing.border.TitledBorder titledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_C),
                title, 0, 0,
                new Font("SansSerif", Font.BOLD, 12),
                new Color(180, 180, 220));
    }
}
