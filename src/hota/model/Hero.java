package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Hero implements Combatant, Resettable {

    protected String name;
    protected int hp;
    protected int maxHp;
    protected int mana;
    protected int maxMana;
    protected List<Ability> abilities;

    private int shieldAmount;

    public Hero(String name, int hp, int mana) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.mana = mana;
        this.maxMana = mana;
        this.shieldAmount = 0;
        this.abilities = new ArrayList<>();
        initAbilities();
    }

    protected abstract void initAbilities();

    public abstract String getHeroClass();

    public String attack(Combatant target) {
        int dmg = 12;
        target.takeDamage(dmg);
        return name + " attacks " + target.getName() + " for " + dmg + " damage!";
    }

    public String healSelf() {
        int amount = 20;
        heal(amount);
        return name + " used Heal and restored " + amount + " HP!";
    }

    public String defendStance() {
        shieldAmount = 15;
        return name + " takes a defensive stance! Next hit reduced by " + shieldAmount + " damage.";
    }

    public String useAbility(Ability ability, Combatant target) {
        if (!ability.isReady()) {
            return ability.getAbilityName() + " is on cooldown (" + ability.getCurrentCooldown() + " turns left)!";
        }
        if (mana < ability.getManaCost()) {
            return "Not enough mana to use " + ability.getAbilityName() + "!";
        }
        mana -= ability.getManaCost();
        return ability.use(this, target);
    }

    public void tickCooldowns() {
        for (Ability a : abilities)
            a.reduceCooldown();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void takeDamage(int damage) {
        int reduced = Math.max(0, damage - shieldAmount);
        if (shieldAmount > 0)
            shieldAmount = 0;
        hp = Math.max(0, hp - reduced);
    }

    @Override
    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    @Override
    public boolean isAlive() {
        return hp > 0;
    }

    @Override
    public String getStats() {
        return shieldAmount > 0 ? "🛡 Shield: " + shieldAmount : "";
    }

    @Override
    public void reset() {
        hp = maxHp;
        mana = maxMana;
        shieldAmount = 0;
        for (Ability a : abilities) 
        a.reset();
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public int getShieldAmount() {
        return shieldAmount;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }
}