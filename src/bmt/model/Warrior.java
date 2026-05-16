package bmt.model;

public class Warrior extends Hero {

    private int armor;
    private int rage;

    public Warrior(String name) {
        super(name, 150, 60);
        this.armor = 10;
        this.rage  = 0;
    }

    @Override
    protected void initAbilities() {
        abilities.add(new Ability("Shield Bash", 25, 15, 2, "Bashes the enemy with a shield, dealing heavy damage."));
        abilities.add(new Ability("Berserker Strike", 40, 25, 3, "A powerful rage-fueled strike with massive damage."));
        abilities.add(new Ability("War Cry", 0, 10, 4, "Rallies morale, healing the Warrior for 30 HP."));
    }

    @Override public String getHeroClass() {return "Warrior";}

    public String shieldBash(Combatant target) {
        armor += 5;
        return useAbility(abilities.get(0), target) + " (Armor increased to " + armor + ")";
    }

    public String berserkerStrike(Combatant target) {
        rage = 0;
        return useAbility(abilities.get(1), target);
    }

    public int getArmor() {return armor;}
    public int getRage()  {return rage;}
}
