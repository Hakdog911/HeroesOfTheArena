# ⚔️ Heroes of the Arena

A turn-based RPG combat game built with Java and Swing. Pick your hero class, face an AI opponent, and battle using abilities, basic attacks, and defensive maneuvers.

---

## 📸 Overview

Heroes of the Arena is a single-player desktop game where you choose a hero — **Warrior**, **Mage**, or **Archer** — and fight an AI-controlled opponent of the same class. Each hero has unique stats and a set of three cooldown-based abilities. Combat is turn-based: you act, then the AI responds after a short delay.

---

## 🗂️ Project Structure

```
src/
└── bmt/
    ├── Main.java                     # Entry point
    ├── model/
    │   ├── Combatant.java            # Interface: combat actions
    │   ├── Resettable.java           # Interface: reset state
    │   ├── Ability.java              # Ability with cooldown & mana cost
    │   ├── Hero.java                 # Abstract base hero class
    │   ├── Warrior.java              # Tank — high HP, armor abilities
    │   ├── Mage.java                 # Glass cannon — spell power & mana regen
    │   ├── Archer.java               # Skirmisher — accuracy & poison
    │   └── GameModel.java            # Game state: heroes, turn, combat log
    ├── controller/
    │   ├── GameObserver.java         # Observer interface
    │   └── GameController.java       # Game logic, AI, observer notifications
    └── view/
        ├── Renderable.java           # Interface: render & refresh
        ├── HeroSelectionFrame.java   # Hero selection screen
        ├── ArenaFrame.java           # Main game window
        └── ArenaPanel.java           # UI panel — HP bars, buttons, combat log
```

---

## 🧙 Hero Classes

| Class   | HP  | Mana | Role                        |
|---------|-----|------|-----------------------------|
| Warrior | 150 | 60   | Tanky bruiser, high defense |
| Mage    | 90  | 120  | High-damage spells          |
| Archer  | 110 | 80   | Swift, precise, poisoner    |

### Abilities

**Warrior**
- `Shield Bash` — 25 damage, 15 mana, 2-turn cooldown
- `Berserker Strike` — 40 damage, 25 mana, 3-turn cooldown
- `War Cry` — 0 damage, heals 30 HP, 10 mana, 4-turn cooldown

**Mage**
- `Fireball` — 35 damage, 20 mana, 2-turn cooldown
- `Mana Surge` — 50 damage, 40 mana, 3-turn cooldown
- `Frost Nova` — 20 damage, 15 mana, 2-turn cooldown

**Archer**
- `Multi-Shot` — 30 damage, 20 mana, 2-turn cooldown
- `Poison Arrow` — 15 + 8 poison damage, 15 mana, 2-turn cooldown
- `Eagle Eye` — 45 damage, 30 mana, 3-turn cooldown

---

## 🎮 Gameplay

Each turn you choose one action:

- **⚔ Basic Attack** — deals 12 flat damage, always available, no mana cost
- **🛡 Defend** — recover 15 HP, no mana cost
- **✨ Ability** — use one of your three class abilities (if off cooldown and enough mana)

After your action, the AI opponent takes its turn automatically after a 1-second delay. The AI picks a random available ability or falls back to a basic attack. The first hero to reach 0 HP loses. You can rematch at any time with the **🔄 Rematch** button.

---

## 🏗️ Architecture

The project follows a clean **MVC (Model-View-Controller)** pattern:

- **Model** (`bmt.model`) — pure game data and logic, no UI dependencies
- **Controller** (`bmt.controller`) — drives game flow, AI behavior, and uses the **Observer pattern** to notify the view of state changes
- **View** (`bmt.view`) — Swing UI; `ArenaPanel` implements `GameObserver` and re-renders on every state change

### Design Patterns Used

| Pattern    | Where                                          |
|------------|------------------------------------------------|
| MVC        | Full package separation of concerns            |
| Observer   | `GameController` → `GameObserver` → `ArenaPanel` |
| Template Method | `Hero.initAbilities()` defined by subclasses |
| Strategy   | `Ability` encapsulates its own cooldown logic  |

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code with Java extension) or the JDK CLI

### Running from an IDE

1. Clone or download the repository.
2. Open the project in your IDE.
3. Run `bmt.Main`.

### Running from the command line

```bash
# Compile
javac -d out src/bmt/**/*.java src/bmt/Main.java

# Run
java -cp out bmt.Main
```

> **Note:** Adjust the source paths if your layout differs.

---

## 🕹️ Controls

| Action           | How                                      |
|------------------|------------------------------------------|
| Select hero      | Choose name and class in the start screen |
| Basic attack     | Click **⚔ Basic Attack**                |
| Defend           | Click **🛡 Defend (+15 HP)**             |
| Use ability      | Click an ability button (grayed = unavailable) |
| Rematch          | Click **🔄 Rematch** after the battle ends |
| Fullscreen       | Press `F11` on the hero selection screen |

---

## 🔧 Extending the Game

**Adding a new hero class:**
1. Create a class extending `Hero` in `bmt.model`.
2. Override `initAbilities()` to define three `Ability` instances.
3. Override `getHeroClass()` to return the class name.
4. Register it in `HeroSelectionFrame.createHero()`.

**Adding a new ability:**
Instantiate a new `Ability` with a name, damage, mana cost, cooldown, and description string. Add it to the `abilities` list inside `initAbilities()`.

---

## 📄 License

This project is open source. Feel free to use, modify, and distribute it for educational or personal purposes.
