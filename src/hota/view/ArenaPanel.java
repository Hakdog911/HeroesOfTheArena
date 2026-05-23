package hota.view;

import hota.controller.GameController;
import hota.controller.GameObserver;
import hota.model.Ability;
import hota.model.Hero;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;

/**
 * ArenaPanel
 * Main game screen — HP/mana bars, ability buttons, combat log.
 *
 * MVC role:
 * • Implements GameObserver — refreshes whenever the Controller notifies
 * • Forwards all user input to the Controller
 * • Never touches game logic directly
 *
 * Player action buttons:
 * ⚔ Basic Attack → playerAction(-1)
 * 💚 Heal → playerAction(-2)
 * 🛡 Defend → playerAction(-3)
 * ✨ Ability 0-2 → playerAction(0-2)
 */
public class ArenaPanel extends JPanel implements Renderable, GameObserver {

    private final GameController controller;

    // ── Player widgets ─────────────────────────────────────────────────────────
    private JProgressBar playerHpBar;
    private JProgressBar playerManaBar;
    private JLabel playerStatsLabel;
    private JLabel playerNameLabel;

    // ── Opponent widgets ───────────────────────────────────────────────────────
    private JProgressBar opponentHpBar;
    private JProgressBar opponentManaBar;
    private JLabel opponentStatsLabel;
    private JLabel opponentNameLabel;

    // ── Action buttons ─────────────────────────────────────────────────────────
    private JButton[] abilityButtons;
    private JButton attackButton;
    private JButton healButton;
    private JButton defendButton;

    // ── Combat log ─────────────────────────────────────────────────────────────
    private JTextArea combatLogArea;

    // ── Palette ────────────────────────────────────────────────────────────────
    private static final Color BG = new Color(12, 12, 22);
    private static final Color PANEL_BG = new Color(22, 22, 40);
    private static final Color BORDER_C = new Color(60, 60, 100);
    private static final Color HP_C = new Color(50, 210, 80);
    private static final Color LOW_HP_C = new Color(210, 60, 60);
    private static final Color MANA_C = new Color(55, 120, 230);
    private static final Color CD_C = new Color(160, 50, 40);
    private static final Color READY_C = new Color(70, 55, 130);
    private static final Color TEXT = Color.WHITE;
    private static final Color SUBTEXT = new Color(160, 160, 210);
    private static final Color PLAYER_LBL = new Color(80, 220, 120);
    private static final Color ENEMY_LBL = new Color(220, 80, 80);
    private static final Color LOG_FG = new Color(170, 220, 170);
    private static final Color SHIELD_C = new Color(100, 160, 220);

    // ── Base font sizes (scale with window) ───────────────────────────────────
    private static final int BASE_W = 780;

    // ── Constructor ────────────────────────────────────────────────────────────

    public ArenaPanel(GameController controller) {
        this.controller = controller;
        controller.addObserver(this);

        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        buildUI();
        render();

        // Responsive font scaling on resize / fullscreen
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                scaleFonts();
            }
        });
    }

    // ── Renderable ─────────────────────────────────────────────────────────────

    @Override
    public void render() {
        refresh();
    }

    @Override
    public void refresh() {
        Hero player = controller.getPlayer();
        Hero opponent = controller.getOpponent();
        boolean over = controller.isGameOver();
        boolean myTurn = controller.isPlayerTurn();

        // ── Progress bars ──────────────────────────────────────────────────────
        updateBar(playerHpBar, player.getHp(), player.getMaxHp(), "HP");
        updateBar(playerManaBar, player.getMana(), player.getMaxMana(), "MP");
        updateBar(opponentHpBar, opponent.getHp(), opponent.getMaxHp(), "HP");
        updateBar(opponentManaBar, opponent.getMana(), opponent.getMaxMana(), "MP");

        // Turn HP bar red when low
        playerHpBar.setForeground(
                player.getHp() < player.getMaxHp() * 0.25 ? LOW_HP_C : HP_C);
        opponentHpBar.setForeground(
                opponent.getHp() < opponent.getMaxHp() * 0.25 ? LOW_HP_C : HP_C);

        // ── Stats labels ───────────────────────────────────────────────────────

        // Shield indicator on name label
        playerNameLabel.setText("👤  YOU — " + player.getName() + "  [" + player.getHeroClass() + "]"
                + (player.getShieldAmount() > 0 ? "  🛡" : ""));
        opponentNameLabel.setText("🤖  ENEMY — " + opponent.getName() + (opponent.getShieldAmount() > 0 ? "  🛡" : ""));

        // ── Ability buttons ────────────────────────────────────────────────────
        List<Ability> abilities = player.getAbilities();
        for (int i = 0; i < abilityButtons.length; i++) {
            Ability ab = abilities.get(i);
            boolean onCd = !ab.isReady();
            boolean ready = ab.isReady() && player.getMana() >= ab.getManaCost();

            Color bg = onCd ? CD_C : READY_C;
            abilityButtons[i].setBackground(bg);
            abilityButtons[i].setBorder(makeBorder(bg));

            String cdTag = onCd ? "  ⏳ " + ab.getCurrentCooldown() + "t" : "";
            abilityButtons[i].setText("✨ " + ab.getAbilityName()
                    + "  (MP:" + ab.getManaCost() + ")" + cdTag);
            abilityButtons[i].setToolTipText(ab.getTooltip());
            abilityButtons[i].setEnabled(!over && myTurn && ready);
        }

        // ── Basic action buttons ───────────────────────────────────────────────
        attackButton.setEnabled(!over && myTurn);
        healButton.setEnabled(!over && myTurn);
        defendButton.setEnabled(!over && myTurn);

        // ── Combat log ─────────────────────────────────────────────────────────
        StringBuilder sb = new StringBuilder();
        for (String line : controller.getCombatLog())
            sb.append(line).append("\n");
        combatLogArea.setText(sb.toString());
        combatLogArea.setCaretPosition(combatLogArea.getDocument().getLength());

        repaint();
    }

    // ── GameObserver ───────────────────────────────────────────────────────────

    @Override
    public void onStateChanged() {
        if (SwingUtilities.isEventDispatchThread()) {
            refresh();
        } else {
            SwingUtilities.invokeLater(this::refresh);
        }
    }

    // ── UI construction ────────────────────────────────────────────────────────

    private void buildUI() {
        // North — hero stat panels
        JPanel statsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        statsRow.setBackground(BG);
        statsRow.add(buildHeroPanel(true));
        statsRow.add(buildHeroPanel(false));
        add(statsRow, BorderLayout.NORTH);

        // Centre — combat log
        combatLogArea = new JTextArea(10, 42);
        combatLogArea.setEditable(false);
        combatLogArea.setBackground(new Color(10, 10, 20));
        combatLogArea.setForeground(LOG_FG);
        combatLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        combatLogArea.setMargin(new Insets(8, 8, 8, 8));
        combatLogArea.setLineWrap(true);
        combatLogArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(combatLogArea);
        scroll.setBorder(titledBorder("⚔  Combat Log"));
        add(scroll, BorderLayout.CENTER);

        // South — action buttons (2 rows × 3 columns)
        JPanel actionPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        actionPanel.setBackground(BG);
        actionPanel.setBorder(titledBorder("⚡  Actions"));

        attackButton = makeButton("⚔  Basic Attack", new Color(160, 55, 55));
        attackButton.setToolTipText(
                "<html><b>Basic Attack</b><br>Deals 12 flat damage. Always available.</html>");
        attackButton.addActionListener(e -> controller.playerAction(-1));

        healButton = makeButton("💚  Heal (+20 HP)", new Color(40, 120, 70));
        healButton.setToolTipText(
                "<html><b>Heal</b><br>Restores 20 HP. No mana cost.</html>");
        healButton.addActionListener(e -> controller.playerAction(-2));

        defendButton = makeButton("🛡  Defend (Shield)", new Color(50, 95, 165));
        defendButton.setToolTipText(
                "<html><b>Defend</b><br>Reduces the next incoming hit by 15 damage.</html>");
        defendButton.addActionListener(e -> controller.playerAction(-3));

        List<Ability> abilities = controller.getPlayer().getAbilities();
        abilityButtons = new JButton[abilities.size()];
        for (int i = 0; i < abilities.size(); i++) {
            Ability ab = abilities.get(i);
            abilityButtons[i] = makeButton("✨ " + ab.getAbilityName() + "  (MP:" + ab.getManaCost() + ")", READY_C);
            abilityButtons[i].setToolTipText(ab.getTooltip());
            final int idx = i;
            abilityButtons[i].addActionListener(e -> controller.playerAction(idx));
        }

        actionPanel.add(attackButton);
        actionPanel.add(healButton);
        actionPanel.add(defendButton);
        for (JButton btn : abilityButtons)
            actionPanel.add(btn);

        add(actionPanel, BorderLayout.SOUTH);

        // Tooltip styling
        UIManager.put("ToolTip.font", new Font("SansSerif", Font.PLAIN, 12));
        UIManager.put("ToolTip.background", new Color(25, 25, 50));
        UIManager.put("ToolTip.foreground", new Color(210, 210, 240));
        ToolTipManager.sharedInstance().setInitialDelay(200);
        ToolTipManager.sharedInstance().setDismissDelay(9000);
    }

    private JPanel buildHeroPanel(boolean isPlayer) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        Hero hero = isPlayer ? controller.getPlayer() : controller.getOpponent();
        String prefix = isPlayer ? "👤  YOU — " : "🤖  ENEMY — ";

        JLabel nameLabel = new JLabel(prefix + hero.getName() + "  [" + hero.getHeroClass() + "]");
        nameLabel.setForeground(isPlayer ? PLAYER_LBL : ENEMY_LBL);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar hpBar = makeBar(HP_C, hero.getHp(), hero.getMaxHp(), "HP");
        JProgressBar manaBar = makeBar(MANA_C, hero.getMana(), hero.getMaxMana(), "MP");

        JLabel statsLabel = new JLabel(hero.getStats());
        statsLabel.setForeground(SUBTEXT);
        statsLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (isPlayer) {
            playerNameLabel = nameLabel;
            playerHpBar = hpBar;
            playerManaBar = manaBar;
            playerStatsLabel = statsLabel;
        } else {
            opponentNameLabel = nameLabel;
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

    // ── Responsive scaling ─────────────────────────────────────────────────────

    private void scaleFonts() {
        int w = getWidth();
        if (w <= 0)
            return;
        float s = Math.max(0.75f, Math.min(2.0f, w / (float) BASE_W));

        Font mono = new Font("Monospaced", Font.PLAIN, Math.round(11 * s));
        Font btn = new Font("SansSerif", Font.BOLD, Math.round(12 * s));

        if (playerStatsLabel != null)
            playerStatsLabel.setFont(mono);
        if (opponentStatsLabel != null)
            opponentStatsLabel.setFont(mono);
        if (combatLogArea != null)
            combatLogArea.setFont(mono);
        if (attackButton != null)
            attackButton.setFont(btn);
        if (healButton != null)
            healButton.setFont(btn);
        if (defendButton != null)
            defendButton.setFont(btn);
        if (abilityButtons != null)
            for (JButton b : abilityButtons)
                b.setFont(btn);

        revalidate();
        repaint();
    }

    // ── Widget helpers ─────────────────────────────────────────────────────────

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
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
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