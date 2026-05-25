package model;

public class Mage extends Hero {

    private final int spellPower;
    private final int manaRegen;

    public Mage(String name) {
        super(name, 90, 120);
        this.spellPower = 20;
        this.manaRegen = 10;
    }

    @Override
    protected void initAbilities() {
        abilities.add(new Ability("Fireball",   35, 20, 2, "Launches a blazing fireball at the enemy."));
        abilities.add(new Ability("Mana Surge", 50, 40, 3, "Channels all mana into a devastating arcane burst."));
        abilities.add(new Ability("Frost Nova", 20, 15, 2, "Freezes the enemy, dealing cold damage."));
    }

    @Override public String getHeroClass(){return "Mage";}

    public int getSpellPower(){return spellPower;}
    public int getManaRegen(){return manaRegen;}
}
