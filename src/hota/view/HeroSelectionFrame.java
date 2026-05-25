package view;

import model.Archer;
import model.Hero;
import model.Mage;
import model.Warrior;

import javax.swing.*;
import java.awt.*;

public class HeroSelectionFrame extends JFrame {

    private static final Color BG = new Color(12, 12, 22);
    private static final Color GOLD = new Color(220, 180, 60);
    private static final Color DIM = new Color(140, 140, 185);

    private JLabel titleLabel;
    private JLabel infoLabel;
    private JLabel progressionLabel;
    private JButton startBtn;
    private JTextField nameField;
    private JComboBox<String> classCombo;
    private JLabel nameLabel;
    private JLabel classLabel;

    public HeroSelectionFrame() {
        super("Heroes of the Arena — Select Your Hero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(510, 390);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        add(buildPanel());
        setVisible(true);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW) .put(KeyStroke.getKeyStroke("F11"), "toggleFullscreen");
        getRootPane().getActionMap().put("toggleFullscreen", new AbstractAction() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        toggleFullscreen();
                    }
                });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                scaleFonts();
            }
        });
    }

    private JPanel buildPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        titleLabel = new JLabel("⚔  HEROES OF THE ARENA  ⚔", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(GOLD);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        panel.add(titleLabel, g);

        progressionLabel = new JLabel("Progression:  Warrior  →  Mage  →  Archer", SwingConstants.CENTER);
        progressionLabel.setForeground(new Color(120, 170, 120));
        progressionLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        g.gridy = 1;
        panel.add(progressionLabel, g);

        g.gridy = 2; g.gridwidth = 1; g.gridx = 0;
        nameLabel = styledLabel("Your Name:");
        panel.add(nameLabel, g);
        nameField = new JTextField("Hero");
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.gridx = 1;
        panel.add(nameField, g);

        g.gridy = 3; g.gridx = 0;
        classLabel = styledLabel("Choose Class:");
        panel.add(classLabel, g);
        String[] classes = {"Warrior  (HP:150 — tanky bruiser)", "Mage     (HP:90  — high-damage spells)", "Archer   (HP:110 — swift & precise)"};
        classCombo = new JComboBox<>(classes);
        classCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.gridx = 1;
        panel.add(classCombo, g);

        infoLabel = new JLabel("Your first opponent will always be a Warrior.", SwingConstants.CENTER);
        infoLabel.setForeground(DIM);
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        g.gridy = 4; g.gridx = 0; g.gridwidth = 2;
        panel.add(infoLabel, g);

        startBtn = new JButton("⚔  ENTER THE ARENA");
        startBtn.setBackground(new Color(140, 45, 45));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        startBtn.setFocusPainted(false);
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        g.gridy = 5;
        panel.add(startBtn, g);

        startBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) name = "Hero";

            int idx = classCombo.getSelectedIndex();
            Hero player = createHero(idx, name);

            Hero opponent = new Warrior("Shadow Warrior");

            dispose();
            new ArenaFrame(player, opponent);
        });

        return panel;
    }

    private static Hero createHero(int idx, String name) {
        return switch (idx) {
            case 1 -> new Mage(name);
            case 2 -> new Archer(name);
            default -> new Warrior(name);
        };
    }

    private static JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return l;
    }

    private void toggleFullscreen() {
        java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment() .getDefaultScreenDevice();

        if (gd.getFullScreenWindow() == null) {
            dispose();
            setUndecorated(true);
            gd.setFullScreenWindow(this);
        } else {
            gd.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setSize(510, 390);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    private void scaleFonts() {
        int w = getWidth();
        if (w <= 0) return;
        float s = Math.max(0.8f, Math.min(2.5f, w / 510f));

        if (titleLabel != null) titleLabel.setFont(new Font("SansSerif", Font.BOLD,  Math.round(22 * s)));
        if (progressionLabel != null) progressionLabel.setFont(new Font("SansSerif", Font.ITALIC,Math.round(11 * s)));
        if (nameLabel != null) nameLabel.setFont(new Font("SansSerif", Font.PLAIN, Math.round(14 * s)));
        if (classLabel != null) classLabel.setFont(new Font("SansSerif", Font.PLAIN, Math.round(14 * s)));
        if (infoLabel != null) infoLabel.setFont(new Font("SansSerif", Font.ITALIC,Math.round(12 * s)));
        if (startBtn != null) startBtn.setFont(new Font("SansSerif", Font.BOLD,  Math.round(15 * s)));
        if (nameField != null) nameField.setFont(new Font("SansSerif", Font.PLAIN, Math.round(14 * s)));
        if (classCombo != null) classCombo.setFont(new Font("SansSerif", Font.PLAIN, Math.round(13 * s)));
        revalidate();
        repaint();
    }
}