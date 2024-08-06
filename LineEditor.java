import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LineEditor {
    private static List<String> buffer = new ArrayList<>();
    private static int cursorLine = -1;
    private static int cursorPos = -1;

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.err.println("Usage: java LineEditor <filename> [directoryname]");
            return;
        }

        String filename = args.length > 1 ? args[1] : "file.txt";
        String directory = args.length == 3 ? args[2] : ".";

        File file = new File(directory, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Created new file: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
                return;
            }
        }

        try {
            readFile(file);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        String command;
        while (true) {
            System.out.print("editor> ");
            command = scanner.nextLine();
            if (command.equals("exit")) break;
            processCommand(command, file);
        }

        scanner.close();
    }

    private static void readFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            buffer.clear();
            while ((line = reader.readLine()) != null) {
                buffer.add(line);
            }
        }
    }

    private static void writeFile(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : buffer) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static void processCommand(String command, File file) {
        String[] parts = command.split(" ", 2);
        String cmd = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";

        try {
            switch (cmd) {
                case "read":
                    printBuffer();
                    break;
                case "insert":
                    insertLine(argument);
                    break;
                case "update":
                    updateLine(argument);
                    break;
                case "delete":
                    deleteLine(argument);
                    break;
                case "search":
                    searchWord(argument);
                    break;
                case "save":
                    writeFile(file);
                    break;
                default:
                    System.out.println("Unknown command: " + cmd);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void printBuffer() {
        for (int i = 0; i < buffer.size(); i++) {
            System.out.println((i + 1) + ": " + buffer.get(i));
        }
    }

    private static void insertLine(String line) {
        if (cursorLine == -1 || cursorLine > buffer.size()) {
            buffer.add(line);
        } else {
            buffer.add(cursorLine, line);
        }
        cursorLine++;
    }

    private static void updateLine(String line) {
        if (cursorLine == -1 || cursorLine >= buffer.size()) {
            System.err.println("Invalid cursor position.");
            return;
        }
        buffer.set(cursorLine, line);
    }

    private static void deleteLine(String line) {
        if (cursorLine == -1 || cursorLine >= buffer.size()) {
            System.err.println("Invalid cursor position.");
            return;
        }
        buffer.remove(cursorLine);
    }

    private static void searchWord(String word) {
        for (int i = 0; i < buffer.size(); i++) {
            int pos = buffer.get(i).indexOf(word);
            if (pos != -1) {
                cursorLine = i;
                cursorPos = pos;
                System.out.println("Found at line " + (i + 1) + ", position " + pos);
                return;
            }
        }
        System.out.println("Word not found.");
    }
}
