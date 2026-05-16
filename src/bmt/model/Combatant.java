package bmt.model;

public interface Combatant {
    String getName();

    void takeDamage(int damage);

    void heal(int amount);

    boolean isAlive();

    String getStats();
}
