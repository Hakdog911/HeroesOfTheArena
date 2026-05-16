package bmt.model;

public class Mage extends Hero {

    private int spellPower;
    private int manaRegen;

    public Mage(String name) {
        super(name, 90, 120);
        this.spellPower = 20;
        this.manaRegen = 10;
    }

    @Override
    protected void initAbilities() {
        abilities.add(new Ability("Fireball", 35, 20, 2, "Launches a blazing fireball at the enemy."));
        abilities.add(new Ability("Mana Surge", 50, 40, 3, "Channels all mana into a devastating arcane burst."));
        abilities.add(new Ability("Frost Nova", 20, 15, 2, "Freezes the enemy, dealing cold damage."));
    }

    @Override
    public String getHeroClass() {
        return "Mage";
    }

    public String castFireball(Combatant target) {
        target.takeDamage(spellPower);
        return useAbility(abilities.get(0), target) + " (+" + spellPower + " spell power bonus!)";
    }

    public String manaSurge(Combatant target) {
        mana = Math.max(0, mana - 10);
        return useAbility(abilities.get(1), target);
    }

    public void regenMana() {
        mana = Math.min(maxMana, mana + manaRegen);
    }

    public int getSpellPower() {
        return spellPower;
    }

    public int getManaRegen() {
        return manaRegen;
    }
}
