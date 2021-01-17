
import java.util.ArrayList;

public class PlayerSettings {
    public static ArrayList<String> settings = new ArrayList<String>();

    public PlayerSettings(String[] fileInput) {
        settings.clear();
        for (int i = 0; i < fileInput.length; i++)
            if (fileInput[i].contains(":")) settings.add(fileInput[i]);
        addAllRequired();
    }

    private void addAllRequired() {
        addIfNotContain("name", "Yan");
        addIfNotContain("battleMapImage", "");
        addIfNotContain("location", "");
        addIfNotContain("gold", "100");
        addIfNotContain("health", "50");
        addIfNotContain("holdingMain", "50");
        addIfNotContain("holdingSecond", "50");
        addIfNotContain("holdingArmor", "50");
    }

    public boolean addIfNotContain(String option, String value) {
        for (int i = 0; i < settings.size(); i++)
            if (settings.get(i).contains(option + ":")) return true;
        settings.add(option + ":" + value);
        return false;
    }

    public String getValue(String name) {
        for (int i = 0; i < settings.size(); i++)
            if (settings.get(i).contains(name + ":")) return settings.get(i).replaceAll(".+:", "");
        return "";
    }

    public void setValue(String name, String value) {
        try {
            if (name.equals("health"))
                value = "" + Math.min(Integer.parseInt(getValue("maxHealth")), Integer.parseInt(value));
        } catch (Exception e) {
            Log.add("Setting health to " + value + " but there was an error that prevented a security check from happening.");
        }
        if (addIfNotContain(name, value))
            for (int i = 0; i < settings.size(); i++)
                if (settings.get(i).contains(name + ":")) settings.set(i, name + ":" + value);
    }

    public void setValue(String name, int value) {
        try {
            if (name.equals("health")) value = Math.min(Integer.parseInt(getValue("maxHealth")), value);
        } catch (Exception e) {
            Log.add("Setting health to " + value + " but there was an error that prevented a security check from happening.");
        }
        if (addIfNotContain(name, "" + value))
            for (int i = 0; i < settings.size(); i++)
                if (settings.get(i).contains(name + ":")) settings.set(i, name + ":" + value);
    }

    public static String[] getNames() {
        String names[] = new String[settings.size()];
        for (int i = 0; i < names.length; i++) names[i] = settings.get(i).replaceAll(":.+", "").replace(":", "");
        return names;
    }

    public void setupTalents(String language) {
        String input[] = FileManager.readFile("res/txt/talentData/" + getValue("class") + language + "" + StaticStuff.dataFileEnding);
        String currentTalent[];
        for (int i = 0; i < input.length; i++) {
            currentTalent = input[i].split(";");
            setValue(currentTalent[0], currentTalent[1]);
        }
    }

    public boolean damagePlayer(int damage) {
        int health = Integer.parseInt(getValue("health"));
        health -= damage;
        if (health <= 0) {
            health = 0;
            setValue("health", health);
            GuiPlayerStats.updateOutput();
            return false;
        } else {
            setValue("health", health);
            GuiPlayerStats.updateOutput();
            return true;
        }
    }

    /*
    0 elf
    1 warrior
    2 mage
    3 novadi
    4 stray
    5 thorwaler
    6 dwarf
     */
}
