package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class BattleLogger {
    private static final String LOG_DIR = "logs";
    private static final DateTimeFormatter FILENAME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter HEADER_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Path saveBattleLog(
            String playerName,
            String opponentName,
            List<String> log,
            boolean playerWon) throws BattleLogException {

        Path logDir = Paths.get(LOG_DIR);
        try {
            Files.createDirectories(logDir);
        } catch (IOException e) {
            throw new BattleLogException("Could not create logs directory: " + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new BattleLogException("Permission denied when creating logs directory. " + "Check your folder permissions.", e);
        }

        String timestamp = LocalDateTime.now().format(FILENAME_FORMAT);
        String filename  = "battle_" + timestamp + ".txt";
        Path   filePath  = logDir.resolve(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {

            writer.write("═══════════════════════════════════════════");
            writer.newLine();
            writer.write("  HEROES OF THE ARENA — Battle Log");
            writer.newLine();
            writer.write("═══════════════════════════════════════════");
            writer.newLine();
            writer.write("Date    : " + LocalDateTime.now().format(HEADER_FORMAT));
            writer.newLine();
            writer.write("Players : " + playerName + " vs " + opponentName);
            writer.newLine();
            writer.write("Result  : " + (playerWon ? playerName + " WON 🏆" : playerName + " was defeated 💀"));
            writer.newLine();
            writer.write("───────────────────────────────────────────");
            writer.newLine();
            writer.newLine();

            for (String line : log) {
                writer.write(line);
                writer.newLine();
            }

            writer.newLine();
            writer.write("═══════════════════════════════════════════");
            writer.newLine();
            writer.write("  End of Battle Log");
            writer.newLine();
            writer.write("═══════════════════════════════════════════");
            writer.newLine();

        } catch (IOException e) {
            throw new BattleLogException("Failed to write battle log to file '" + filename + "': " + e.getMessage(), e);
        }

        return filePath;
    }

    public static List<String> readBattleLog(Path filePath) throws BattleLogException {
        if (!Files.exists(filePath)) {
            throw new BattleLogException("Log file not found: " + filePath.toAbsolutePath(), null);
        }

        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new BattleLogException("Could not read log file '" + filePath.getFileName() + "': " + e.getMessage(), e);
        }
    }

    public static class BattleLogException extends Exception {

        public BattleLogException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
