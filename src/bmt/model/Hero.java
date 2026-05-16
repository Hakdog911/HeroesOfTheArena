package bmt.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Hero implements Combatant, Resettable {

    protected String name;
    protected int hp;
    protected int maxHp;
    protected int mana;
    protected int maxMana;
    protected List<Ability> abilities;

    public Hero(String name, int hp, int mana) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.mana = mana;
        this.maxMana = mana;
        this.abilities = new ArrayList<>();
        initAbilities();
    }

    protected abstract void initAbilities();

    public abstract String getHeroClass();

    public String attack(Combatant target) {
        int baseDamage = 12;
        target.takeDamage(baseDamage);
        return name + " attacks " + target.getName() + " for " + baseDamage + " damage!";
    }

    public String useAbility(Ability ability, Combatant target) {
        if (!ability.isReady()) {
            return ability.getAbilityName() + " is on cooldown ("
                    + ability.getCurrentCooldown() + " turns left)!";
        }
        if (mana < ability.getManaCost()) {
            return "Not enough mana to use " + ability.getAbilityName() + "!";
        }
        mana -= ability.getManaCost();
        return ability.use(this, target);
    }

    public String defend() {
        int healAmount = 15;
        heal(healAmount);
        return name + " takes a defensive stance and recovers " + healAmount + " HP!";
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
        hp = Math.max(0, hp - damage);
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
        return "[" + getHeroClass() + "] " + name + " | HP: " + hp + "/" + maxHp + " | Mana: " + mana + "/" + maxMana;
    }

    @Override
    public void reset() {
        hp = maxHp;
        mana = maxMana;
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

    public List<Ability> getAbilities() {
        return abilities;
    }
}
