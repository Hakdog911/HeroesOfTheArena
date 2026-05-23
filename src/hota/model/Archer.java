package hota.model;

/**
 * Archer
 * Swift ranged fighter — high accuracy, poison, multi-shot.
 */
public class Archer extends Hero {

    private final double accuracy;
    private int arrowCount;

    public Archer(String name) {
        super(name, 110, 80);
        this.accuracy   = 0.90;
        this.arrowCount = 20;
    }

    @Override
    protected void initAbilities() {
        abilities.add(new Ability("Multi-Shot",   30, 20, 2,
                "Fires multiple arrows, hitting the target multiple times."));
        abilities.add(new Ability("Poison Arrow", 15, 15, 2,
                "Shoots a poisoned arrow dealing damage over time."));
        abilities.add(new Ability("Eagle Eye",    45, 30, 3,
                "A precise, powerful shot that never misses."));
    }

    @Override public String getHeroClass() { return "Archer"; }

    public double getAccuracy()  { return accuracy;   }
    public int    getArrowCount(){ return arrowCount; }
}
