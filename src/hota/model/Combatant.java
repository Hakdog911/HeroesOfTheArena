package hota.model;

/**
 * Combatant
 * Contract for any entity that can participate in arena combat.
 */
public interface Combatant {
    String getName();
    void takeDamage(int damage);
    void heal(int amount);
    boolean isAlive();
    String getStats();
}
