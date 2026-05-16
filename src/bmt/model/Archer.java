package bmt.model;

public class Archer extends Hero {

    private double accuracy;
    private int arrowCount;

    public Archer(String name) {
        super(name, 110, 80);
        this.accuracy = 0.90;
        this.arrowCount = 20;
    }

    @Override
    protected void initAbilities() {
        abilities
                .add(new Ability("Multi-Shot", 30, 20, 2, "Fires multiple arrows, hitting the target multiple times."));
        abilities.add(new Ability("Poison Arrow", 15, 15, 2, "Shoots a poisoned arrow dealing damage over time."));
        abilities.add(new Ability("Eagle Eye", 45, 30, 3, "A precise, powerful shot that never misses."));
    }

    @Override
    public String getHeroClass() {
        return "Archer";
    }

    public String multiShot(Combatant target) {
        if (arrowCount < 3)
            return "Not enough arrows for Multi-Shot!";
        arrowCount -= 3;
        return useAbility(abilities.get(0), target)
                + " (" + arrowCount + " arrows remaining)";
    }

    public String poisonArrow(Combatant target) {
        if (arrowCount < 1)
            return "Out of arrows!";
        arrowCount--;
        int poisonDamage = 8;
        target.takeDamage(poisonDamage);
        return useAbility(abilities.get(1), target) + " (Poison deals extra " + poisonDamage + " damage!)";
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getArrowCount() {
        return arrowCount;
    }
}