package hota.view;

import hota.model.Archer;
import hota.model.Hero;
import hota.model.Mage;
import hota.model.Warrior;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * BattleResultDialog
 * Modal popup shown when a battle ends (win or loss).
 *
 * On WIN:  Shows victory message + next opponent class (if any) + rematch option.
 * On LOSS: Shows defeat message + rematch option only.
 *
 * Calls back into ArenaFrame with the player's choice via a Consumer.
 */
public class BattleResultDialog extends JDialog {

    // Opponent progression order: Warrior → Mage → Archer
    private static final String[] PROGRESSION = { "Warrior", "Mage", "Archer" };

    private static final Color BG        = new Color(12, 12, 22);
    private static final Color PANEL_BG  = new Color(22, 22, 40);
    private static final Color BORDER_C  = new Color(60, 60, 100);
    private static final Color GOLD      = new Color(220, 180, 60);
    private static final Color RED       = new Color(200, 60, 60);
    private static final Color GREEN     = new Color(60, 200, 100);
    private static final Color BTN_NEXT  = new Color(50, 130, 80);
    private static final Color BTN_REMA  = new Color(100, 80, 150);
    private static final Color BTN_QUIT  = new Color(80, 40, 40);

    /**
     * @param parent          The owning ArenaFrame
     * @param playerWon       True if the player won
     * @param playerName      Player hero name (for display)
     * @param playerClass     Player hero class string ("Warrior", "Mage", "Archer")
     * @param opponentClass   Defeated/current opponent class string
     * @param onChoice        Callback: receives the new opponent Hero, or null for quit
     */
    public BattleResultDialog(
            JFrame parent,
            boolean playerWon,
            String playerName,
            String playerClass,
            String opponentClass,
            Consumer<Hero> onChoice) {

        super(parent, playerWon ? "Victory!" : "Defeat...", true);

        setBackground(BG);
        getContentPane().setBackground(BG);
        setResizable(false);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C, 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        // ── Result banner ──────────────────────────────────────────────────────
        String icon    = playerWon ? "🏆" : "💀";
        String headline = playerWon
                ? icon + "  Victory!"
                : icon + "  Defeated!";
        String subline = playerWon
                ? playerName + " triumphed over the " + opponentClass + "!"
                : playerName + " was defeated by the " + opponentClass + "...";

        JLabel headlineLabel = centeredLabel(headline, 26, Font.BOLD, playerWon ? GOLD : RED);
        JLabel sublineLabel  = centeredLabel(subline,  14, Font.PLAIN, new Color(180, 180, 210));

        root.add(headlineLabel);
        root.add(Box.createVerticalStrut(6));
        root.add(sublineLabel);
        root.add(Box.createVerticalStrut(24));

        // ── Separator ──────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_C);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        root.add(sep);
        root.add(Box.createVerticalStrut(20));

        // ── Buttons ────────────────────────────────────────────────────────────
        String nextClass = nextOpponentClass(opponentClass);
        boolean hasNext  = playerWon && nextClass != null;

        if (hasNext) {
            // Show next-opponent info
            JLabel challengeLabel = centeredLabel(
                    "Challenge the " + nextClass + " next?", 13, Font.ITALIC,
                    new Color(160, 200, 160));
            root.add(challengeLabel);
            root.add(Box.createVerticalStrut(12));

            JButton nextBtn = dialogButton(
                    "⚔  Fight " + nextClass + " (Next)", BTN_NEXT);
            nextBtn.addActionListener(e -> {
                dispose();
                onChoice.accept(createHero(nextClass, "Shadow " + nextClass));
            });
            root.add(nextBtn);
            root.add(Box.createVerticalStrut(8));
        }

        // Rematch — same opponent class
        JButton rematchBtn = dialogButton("🔄  Rematch vs " + opponentClass, BTN_REMA);
        rematchBtn.addActionListener(e -> {
            dispose();
            onChoice.accept(createHero(opponentClass, "Shadow " + opponentClass));
        });
        root.add(rematchBtn);
        root.add(Box.createVerticalStrut(8));

        // Quit to selection
        JButton quitBtn = dialogButton("🚪  Return to Hero Selection", BTN_QUIT);
        quitBtn.addActionListener(e -> {
            dispose();
            onChoice.accept(null); // null signals "go back to selection screen"
        });
        root.add(quitBtn);

        setContentPane(root);
        pack();
        setLocationRelativeTo(parent);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Returns the next class in the progression after the given class,
     * or null if the player has beaten the final opponent.
     */
    public static String nextOpponentClass(String current) {
        for (int i = 0; i < PROGRESSION.length - 1; i++) {
            if (PROGRESSION[i].equals(current)) return PROGRESSION[i + 1];
        }
        return null; // already at last opponent
    }

    private static Hero createHero(String heroClass, String name) {
        return switch (heroClass) {
            case "Mage"   -> new Mage(name);
            case "Archer" -> new Archer(name);
            default       -> new Warrior(name);
        };
    }

    private static JLabel centeredLabel(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", style, size));
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private static JButton dialogButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(280, 40));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        return btn;
    }
}
