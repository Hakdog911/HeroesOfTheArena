package hota;

import hota.view.HeroSelectionFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HeroSelectionFrame::new);
    }
}