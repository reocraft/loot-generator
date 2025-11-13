package edu.grinnell.csc207.lootgenerator;
import java.util.*;
import java.io.*;

public class LootGenerator {
    /** The path to the dataset (either the small or large set). */
    private static final String DATA_SET = "data/small";
    private ArrayList<Monsters> monsters;
    private String monsterFile = DATA_SET + "/monstats.txt";
    public LootGenerator() {
        monsters = new ArrayList<Monsters>();
        loadMonsters(monsterFile);
    }

    public void loadMonsters(String monsterFile) {
        try (Scanner sc = new Scanner(new File(monsterFile))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                String[] parts = line.split("\t");
                String name = parts[0];
                String type = parts[1];
                int level = Integer.parseInt(parts[2]);
                String treasureClass = parts[3];
                Monsters monster = new Monsters(name, type, level, treasureClass);
                monsters.add(monster);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Monster file not found.");
        }
    }

    public void run() {
        if (monsters.isEmpty()) {
            System.out.println("No monsters loaded.");
            return;
        }
        Random rand = new Random();
        Monsters randomMonster = monsters.get(rand.nextInt(monsters.size()));
        System.out.println("You killed a " + randomMonster.getName() + "!");
    }

    
    public static void main(String[] args) {
        System.out.println("This program kills monsters and generates loot!");
        LootGenerator lg = new LootGenerator();
        lg.run();
    }
}
