package model;

public class Ability implements Resettable {

    private final String abilityName;
    private final int damage;
    private final int manaCost;
    private final int cooldown;
    private int currentCooldown;
    private final String description;

    public Ability(String abilityName, int damage, int manaCost, int cooldown, String description) {
        this.abilityName = abilityName;
        this.damage = damage;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
        this.description = description;
    }

    public String use(Combatant user, Combatant target) {
        if (damage > 0)
            target.takeDamage(damage);
        currentCooldown = cooldown;
        return user.getName() + " used " + abilityName + " on " + target.getName() + (damage > 0 ? " for " + damage + " damage!" : "!");
    }

    public boolean isReady() {
        return currentCooldown == 0;
    }

    public void reduceCooldown() {
        if (currentCooldown > 0)
            currentCooldown--;
    }

    @Override
    public void reset() {
        currentCooldown = 0;
    }

    public String getTooltip() {
        StringBuilder sb = new StringBuilder("<html><b>").append(abilityName).append("</b><br>").append(description)
                .append("<br><br>");

        if (damage > 0)
            sb.append("Damage: <b>").append(damage).append("</b>  |  ");
        sb.append("Mana: <b>").append(manaCost).append("</b>  |  ").append("Cooldown: <b>").append(cooldown)
                .append(" turns</b><br>");

        if (isReady()) {
            sb.append("<font color='#44dd88'>✔ Ready</font>");
        } else {
            sb.append("<font color='#ff6655'>⏳ On cooldown — ").append(currentCooldown)
                    .append(currentCooldown == 1 ? " turn" : " turns").append(" remaining</font>");
        }

        return sb.append("</html>").toString();
    }

    public String getAbilityName() {
        return abilityName;
    }

    public int getDamage() {
        return damage;
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public String getDescription() {
        return description;
    }
}