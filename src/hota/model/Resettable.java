package hota.model;

/**
 * Resettable
 * Contract for any object whose state can be fully restored between rounds.
 */
public interface Resettable {
    void reset();
}
