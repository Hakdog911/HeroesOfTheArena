package model;

public class Warrior extends Hero {

    private int armor;

    public Warrior(String name) {
        super(name, 150, 60);
        this.armor = 10;
    }

    @Override
    protected void initAbilities() {
        abilities.add(new Ability("Shield Bash", 25, 15, 2, "Bashes the enemy with a shield, dealing heavy damage."));
        abilities.add(new Ability("Berserker Strike", 40, 25, 3, "A powerful rage-fueled strike with massive damage."));
        abilities.add(new Ability("War Cry", 20, 10, 4, "A fierce battle cry that damages nearby enemies."));
    }

    @Override public String getHeroClass(){return "Warrior";}

    public int getArmor(){return armor;}
}
