
import java.util.ArrayList;

public class PlayerSettings {
    public static ArrayList<String> settings = new ArrayList<String>();

    public PlayerSettings(String[] fileInput) {
        settings.clear();
        for (String s : fileInput) if (s.contains(":")) settings.add(s);
        addAllRequired();
    }

    private void addAllRequired() {
        addIfNotContain("name", "");
        addIfNotContain("battleMapImage", "");
        addIfNotContain("location", "");
        addIfNotContain("gold", "100");
        addIfNotContain("health", "50");
        addIfNotContain("holdingMain", "50");
        addIfNotContain("holdingSecond", "50");
        addIfNotContain("holdingArmor", "50");
        addIfNotContain("globalTalentModifier", "0");
    }

    public boolean addIfNotContain(String option, String value) {
        for (String setting : settings) if (setting.contains(option + ":")) return true;
        settings.add(option + ":" + value);
        return false;
    }

    public String getValue(String name) {
        for (String setting : settings) if (setting.contains(name + ":")) return setting.replaceAll(".+:", "");
        return "";
    }

    public int getValueInt(String name) {
        for (String setting : settings) if (setting.contains(name + ":")) {
            try {
                return Integer.parseInt(setting.replaceAll(".+:", ""));
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
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
        String[] names = new String[settings.size()];
        for (int i = 0; i < names.length; i++) names[i] = settings.get(i).replaceAll(":.+", "").replace(":", "");
        return names;
    }

    public void setupTalents(String language) {
        String[] input = FileManager.readFile("res/txt/talentData/" + getValue("class") + language + "" + StaticStuff.dataFileEnding);
        String[] currentTalent;
        for (String s : input) {
            currentTalent = s.split(";");
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

    public String[] generateSaveString() {
        String[] dest = new String[settings.toArray().length];
        System.arraycopy(settings.toArray(), 0, dest, 0, settings.toArray().length);
        return dest;
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
