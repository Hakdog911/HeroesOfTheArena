# ⚔️ Heroes of the Arena

A turn-based RPG combat game built with Java and Swing. Pick your hero class, battle through an AI opponent progression, and use abilities, attacks, heals, and shields to claim victory.

---

## 📸 Overview

Heroes of the Arena is a single-player desktop game where you choose a hero — **Warrior**, **Mage**, or **Archer** — and fight AI-controlled opponents in a fixed progression: **Warrior → Mage → Archer**. Each hero has unique stats and a set of three cooldown-based abilities. Combat is turn-based: you act, then the AI responds after a short delay. After each victory, a result dialog offers you the next challenger, a rematch, or a return to hero selection. Every completed battle is automatically saved to a log file.

---

## 🗂️ Project Structure

```
src/
├── Main.java                         # Entry point (root, outside packages)
└── hota/
    ├── controller/
    │   ├── GameController.java       # Game logic, AI, observer notifications
    │   └── GameObserver.java         # Observer interface
    ├── model/
    │   ├── Ability.java              # Ability with cooldown, mana cost & tooltip
    │   ├── Archer.java               # Skirmisher — accuracy & poison
    │   ├── Combatant.java            # Interface: combat actions
    │   ├── GameModel.java            # Game state: heroes, turn, combat log
    │   ├── Hero.java                 # Abstract base hero class
    │   ├── Mage.java                 # Glass cannon — spell power & mana regen
    │   ├── Resettable.java           # Interface: reset state between rounds
    │   └── Warrior.java              # Tank — high HP, armor abilities
    ├── util/
    │   └── BattleLogger.java         # Saves/reads battle logs in logs/
    └── view/
        ├── ArenaFrame.java           # Main game window; handles result dialog & progression
        ├── ArenaPanel.java           # UI panel — HP/mana bars, action buttons, combat log
        ├── BattleResultDialog.java   # End-of-battle popup: next opponent / rematch / quit
        ├── HeroSelectionFrame.java   # Opening hero selection screen
        └── Renderable.java           # Interface: render & refresh
test/                                 # Unit tests
```

---

## 🧙 Hero Classes

| Class   | HP  | Mana | Role                         |
| ------- | --- | ---- | ---------------------------- |
| Warrior | 150 | 60   | Tanky bruiser, high defense  |
| Mage    | 90  | 120  | High-damage spells           |
| Archer  | 110 | 80   | Swift, precise, poisoner     |

### Abilities

**Warrior**

- `Shield Bash` — 25 damage, 15 mana, 2-turn cooldown
- `Berserker Strike` — 40 damage, 25 mana, 3-turn cooldown
- `War Cry` — 20 damage, 10 mana, 4-turn cooldown

**Mage**

- `Fireball` — 35 damage, 20 mana, 2-turn cooldown
- `Mana Surge` — 50 damage, 40 mana, 3-turn cooldown
- `Frost Nova` — 20 damage, 15 mana, 2-turn cooldown

**Archer**

- `Multi-Shot` — 30 damage, 20 mana, 2-turn cooldown
- `Poison Arrow` — 15 damage, 15 mana, 2-turn cooldown
- `Eagle Eye` — 45 damage, 30 mana, 3-turn cooldown

---

## 🎮 Gameplay

Each turn you choose one of four actions:

- **⚔ Basic Attack** — deals 12 flat damage, always available, no mana cost
- **💚 Heal (+20 HP)** — restores 20 HP, no mana cost
- **🛡 Defend (Shield)** — reduces the next incoming hit by 15 damage, no mana cost
- **✨ Ability** — use one of your three class abilities (if off cooldown and enough mana)

After your action, the AI opponent takes its turn automatically after a ~1.2-second delay. The AI uses a random available ability when possible, but will also **defend when its HP drops below 40%**. The first hero to reach 0 HP loses.

### Opponent Progression

After defeating an opponent, `BattleResultDialog` presents three options:

| Option | What happens |
| ------ | ------------ |
| ⚔ Fight next opponent | Advances to the next class in the progression |
| 🔄 Rematch | Fights the same opponent class again |
| 🚪 Return to Hero Selection | Goes back to the opening screen |

Progression order: **Warrior → Mage → Archer**. Beating the Archer completes the full run.

### Battle Logs

Every completed battle is automatically saved to `logs/battle_<timestamp>.txt`. The file path is shown in the result dialog so you can find it easily. Logs include the full combat log, result, and a timestamp header.

---

## 🏗️ Architecture

The project follows a clean **MVC (Model-View-Controller)** pattern:

- **Model** (`hota.model`) — pure game data and logic, no UI dependencies
- **Controller** (`hota.controller`) — drives game flow, AI behavior, and uses the **Observer pattern** to notify the view of state changes
- **View** (`hota.view`) — Swing UI; `ArenaPanel` re-renders on every state change; `ArenaFrame` listens for game-over and shows `BattleResultDialog`; `HeroSelectionFrame` is the entry screen
- **Util** (`hota.util`) — `BattleLogger` handles all file I/O for saving and reading battle logs, with a custom `BattleLogException` for meaningful error reporting

### Design Patterns Used

| Pattern         | Where                                                        |
| --------------- | ------------------------------------------------------------ |
| MVC             | Full package separation of concerns                          |
| Observer        | `GameController` → `GameObserver` → `ArenaPanel`/`ArenaFrame` |
| Template Method | `Hero.initAbilities()` overridden by each subclass           |
| Strategy        | `Ability` encapsulates its own cooldown & mana logic         |
| Factory Method  | `createHero()` in `HeroSelectionFrame` and `BattleResultDialog` |

---

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- **Maven** (recommended), or any Java IDE (IntelliJ IDEA, Eclipse, VS Code with Java extension)

### Running with Maven

```bash
# Compile
mvn compile

# Run
mvn exec:java -Dexec.mainClass="Main"

# Package into a JAR
mvn package

# Run the JAR
java -jar target/HeroesOfTheArena-1.0-SNAPSHOT.jar
```

### Running from an IDE

1. Clone or download the repository.
2. Open the project in your IDE.
3. Run `Main` (the root-level entry point).

### Running from the command line (without Maven)

```bash
# Compile
javac -d out src/Main.java src/hota/**/*.java

# Run
java -cp out Main
```

> **Note:** Adjust the source paths if your layout differs.

---

## 🕹️ Controls

| Action        | How                                                        |
| ------------- | ---------------------------------------------------------- |
| Select hero   | Choose name and class in the opening screen                |
| Basic attack  | Click **⚔ Basic Attack**                                   |
| Heal          | Click **💚 Heal (+20 HP)**                                  |
| Defend        | Click **🛡 Defend (Shield)** — blocks 15 damage next hit   |
| Use ability   | Click an ability button (grayed out = on cooldown or no mana) |
| Next opponent | Click **⚔ Fight [Class]** in the result dialog             |
| Rematch       | Click **🔄 Rematch** in the result dialog                   |
| Fullscreen    | Press `F11` on any screen                                  |

---

## 🔧 Extending the Game

**Adding a new hero class:**

1. Create a class extending `Hero` in `hota.model`.
2. Override `initAbilities()` to define three `Ability` instances.
3. Override `getHeroClass()` to return the class name.
4. Register it in the `createHero()` methods inside `HeroSelectionFrame` and `BattleResultDialog`.
5. Add it to the `PROGRESSION` array in `BattleResultDialog` if it should be part of the opponent chain.

**Adding a new ability:** Instantiate a new `Ability` with a name, damage, mana cost, cooldown, and description string. Add it to the `abilities` list inside `initAbilities()`. The tooltip is generated automatically.

---

## 📄 License

This project is open source. Feel free to use, modify, and distribute it for educational or personal purposes.
