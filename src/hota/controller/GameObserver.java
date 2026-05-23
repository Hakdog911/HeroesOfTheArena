package hota.controller;

/**
 * GameObserver
 * Implemented by the View to receive state-change notifications from the Controller.
 * This is the MVC decoupling point — Controller never imports Swing.
 */
public interface GameObserver {
    void onStateChanged();
}
