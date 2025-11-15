package edu.grinnell.csc207.lootgenerator;

import java.util.*;
import java.io.*;

public class LootGenerator {
    /** The path to the dataset (either the small or large set). */
    private static final String DATA_SET = "data/large";
    private ArrayList<Monsters> monsters;
    private HashMap<String, String[]> treasureClasses = new HashMap<String, String[]>();
    private HashMap<String, int[]> dropStats = new HashMap<String, int[]>();
    private ArrayList<magicStat> mPrefixes = new ArrayList<magicStat>();
    private ArrayList<magicStat> mSuffixes = new ArrayList<magicStat>();
    private String monsterFile = DATA_SET + "/monstats.txt";
    private String tcFile = DATA_SET + "/TreasureClassEx.txt";
    private String statsFile = DATA_SET + "/armor.txt";
    private String pMagicFile = DATA_SET + "/MagicPrefix.txt";
    private String sMagicFile = DATA_SET + "/MagicSuffix.txt";

    public class magicStat {
        public int maxVal;
        public int minVal;
        public String stat;
        public String title;

        public magicStat(String title, String stat, int minVal, int maxVal) {
            this.title = title;
            this.stat = stat;
            this.minVal = minVal;
            this.maxVal = maxVal;
        }
    }

    public LootGenerator() {
        monsters = new ArrayList<Monsters>();
        loadMonsters(monsterFile);
        // System.out.println(monsters.toString());
        loadTCs(tcFile);
        loadStats(statsFile);
        loadMagicP(pMagicFile);
        loadMagicS(sMagicFile);
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

    public void loadTCs(String tcFile) {
        try (Scanner sc = new Scanner(new File(tcFile))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                String[] parts = line.split("\t");
                String tcName = parts[0];
                String[] drops = Arrays.copyOfRange(parts, 1, parts.length);
                treasureClasses.put(tcName, drops);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Treasure class file not found.");
        }
    }

    public void loadStats(String statsFile) {
        try (Scanner sc = new Scanner(new File(statsFile))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                String[] parts = line.split("\t");
                String dropName = parts[0];
                int[] stats = { Integer.parseInt(parts[1]), Integer.parseInt(parts[2]) };
                dropStats.put(dropName, stats);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: armor stat file not found.");
        }
    }

    public void loadMagicP(String pMagicFile) {
        try (Scanner sc = new Scanner(new File(pMagicFile))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                String[] parts = line.split("\t");
                magicStat magicStat = new magicStat(parts[0], parts[1], Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]));
                mPrefixes.add(magicStat);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Magic Preffix file not found.");
        }
    }

    public void loadMagicS(String sMagicFile) {
        try (Scanner sc = new Scanner(new File(sMagicFile))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                String[] parts = line.split("\t");
                magicStat magicStat = new magicStat(parts[0], parts[1], Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]));
                mSuffixes.add(magicStat);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Magic Suffix file not found.");
        }
    }

    public String getDrop(String tcName) {
        String[] tcDrop = treasureClasses.get(tcName);
        Random rand = new Random();
        String chosenDrop = tcDrop[rand.nextInt(tcDrop.length)];
        if (treasureClasses.containsKey(chosenDrop)) {
            return getDrop(chosenDrop);
        }
        return chosenDrop;
    }

    public int genStats(String dropName) {
        int minVal = dropStats.get(dropName)[0];
        int maxVal = dropStats.get(dropName)[1];
        Random rand = new Random();
        int result = rand.nextInt(minVal, maxVal + 1);
        return result;
    }

    public magicStat getPrefix(ArrayList<magicStat> list) {
        Random rand = new Random();
        if(list.size() == 0){
            System.out.println("Empty magic prefix??? ");
        }
        int index = rand.nextInt(0, list.size());
        int i = 0;
        magicStat mPrefix = list.get(0);
        while (i < index) {
            mPrefix = list.get(i);
            i++;
        }
        return mPrefix;
    }

    public magicStat getSuffix(ArrayList<magicStat> list) {
        Random rand = new Random();
        int index = rand.nextInt(list.size());
        int i = 0;
        magicStat mSuffix = list.get(0);
        while (i < index) {
            mSuffix = list.get(i);
            i++;
        }
        return mSuffix;
    }

    public void run() {
        if (monsters.isEmpty()) {
            System.out.println("No monsters loaded.");
            return;
        }
        Random rand = new Random();
        int doGenP = rand.nextInt(0, 2);
        int doGenS = rand.nextInt(0, 2);
        String prefix = "";
        String suffix = "";

        // declaring affixes with dummy values
        magicStat magicPrefix = new magicStat(prefix, suffix, doGenP, doGenS);
        magicStat magicSuffix = new magicStat(prefix, suffix, doGenP, doGenS);


        Monsters randomMonster = monsters.get(rand.nextInt(monsters.size()));
        System.out.println("Fighting " + randomMonster.getName());
        System.out.println("You killed a " + randomMonster.getName() + "!");
        System.out.println(randomMonster.getName() + " Dropped:\n");
        String obtainedDrop = getDrop(randomMonster.getTreasureClass());

        if (doGenP == 1) {
            magicPrefix = getPrefix(mPrefixes);
            prefix = magicPrefix.title + ' ';
        }
        if (doGenS == 1) {
            magicSuffix = getPrefix(mSuffixes);
            suffix = " " + magicSuffix.title;
        }
        System.out.println(prefix + obtainedDrop + " " + suffix);
        System.out.println("Defense: " + genStats(obtainedDrop));

        if(doGenP == 1){
            System.out.println(rand.nextInt(magicPrefix.minVal, magicPrefix.maxVal + 1) + " " + magicPrefix.stat);
        }
        if(doGenS == 1){
            System.out.println(rand.nextInt(magicSuffix.minVal, magicSuffix.maxVal + 1) + " " + magicSuffix.stat);
        }
    }

    public static void main(String[] args) {
        // System.out.println("This program kills monsters and generates loot!");
        LootGenerator lg = new LootGenerator();
        lg.run();
    }
}
