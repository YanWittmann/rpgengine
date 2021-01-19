
import java.util.ArrayList;

public class LootTable extends Entity {
    public LootTable(String[] fileInput) {
        type = "LootTable";
        try {
            name = fileInput[0];
            description = fileInput[1];
            uid = fileInput[2];
            for (int i = 3; i < fileInput.length; i++) {
                if (fileInput[i].contains("++ev++")) {
                    fileInput[i] = fileInput[i].replace("++ev++", "");
                    eventName.add(fileInput[i].split("---")[0]);
                    eventCode.add(fileInput[i].split("---")[1]);
                } else if (fileInput[i].contains("++tag++")) {
                    tags.add(fileInput[i].replace("++tag++", ""));
                } else if (fileInput[i].contains("++variable++")) {
                    fileInput[i] = fileInput[i].replace("++variable++", "");
                    localVarUids.add(fileInput[i].split("---")[0]);
                    localVarName.add(fileInput[i].split("---")[1]);
                    localVarType.add(fileInput[i].split("---")[2]);
                    localVarValue.add(fileInput[i].split("---")[3]);
                }
            }
        } catch (Exception e) {
            StaticStuff.error("LootTable '" + name + "' contains invalid data.");
        }
    }

    public String generateSaveString() {
        StringBuilder str = new StringBuilder("" + name + "\n" + description + "\n" + uid);
        for (int i = 0; i < eventName.size(); i++)
            str.append("\n++ev++").append(eventName.get(i)).append("---").append(eventCode.get(i));
        for (String tag : tags) str.append("\n++tag++").append(tag);
        for (int i = 0; i < localVarName.size(); i++)
            str.append("\n").append("\n++variable++").append(localVarUids.get(i)).append("---").append(localVarName.get(i)).append("---").append(localVarType.get(i)).append("---").append(localVarValue.get(i));
        return str.toString();
    }

    public int getDropIndex() {
        for (int i = 0; i < eventName.size(); i++) {
            if (eventName.get(i).equals("dropLoot")) return i;
        }
        return -1;
    }

    public int drop(Interpreter interpreter, String inventoryUID) {
        Log.add("Dropping loot table " + uid + " into inventory " + inventoryUID);
        String lootTable[] = getEventCode("dropLoot");
        ArrayList<String> pickRandom = new ArrayList<String>();
        int amountItems = 0;
        for (int i = 0; i < lootTable.length; i++) {
            lootTable[i] = lootTable[i].trim();
            if (lootTable[i].matches("#[^#]+# \\d+ \\d+")) { //drop item with chance
                if (Integer.parseInt(interpreter.prepareStringReplaceVar(lootTable[i].replaceAll("#[^#]+# \\d+ (\\d+)", "$1"))) >= StaticStuff.randomNumber(-1, 100)) {
                    Log.deactivate();
                    String itemUIDs[] = interpreter.evaluateSelector(lootTable[i].replaceAll("(#[^#]+#) \\d+ \\d+", "$1"));
                    int amount = Integer.parseInt(interpreter.prepareStringReplaceVar(lootTable[i].replaceAll("#[^#]+# (\\d+) \\d+", "$1")));
                    for (int j = 0; j < itemUIDs.length; j++) {
                        amountItems += amount;
                        interpreter.inventoryOperation("add #uid:" + itemUIDs[j] + "# to #uid:" + inventoryUID + "# amount " + amount);
                    }
                    interpreter.activateLog();
                    for (int j = 0; j < itemUIDs.length; j++)
                        Log.add("Drop " + amount + " x " + itemUIDs[j] + " to " + inventoryUID);
                }
            } else if (lootTable[i].equals("{")) {
                i++;
                lootTable[i] = lootTable[i].trim();
                int min = Integer.parseInt(interpreter.prepareStringReplaceVar(lootTable[i].replaceAll("roll (.+) to .+", "$1")));
                int max = Integer.parseInt(interpreter.prepareStringReplaceVar(lootTable[i].replaceAll("roll .+ to (.+)", "$1")));
                int rolls = StaticStuff.randomNumber(min, max), selected;
                while (!lootTable[i].equals("}")) {
                    i++;
                    lootTable[i] = lootTable[i].trim();
                    if (!lootTable[i].equals("}")) pickRandom.add(lootTable[i]);
                }
                for (int j = 0; j < rolls; j++) {
                    selected = StaticStuff.randomNumber(0, pickRandom.size() - 1);
                    if (Integer.parseInt(interpreter.prepareStringReplaceVar(pickRandom.get(selected).replaceAll("#[^#]+# \\d+ (\\d+)", "$1"))) >= StaticStuff.randomNumber(-1, 100)) {
                        Log.deactivate();
                        String itemUIDs[] = interpreter.evaluateSelector(pickRandom.get(selected).replaceAll("(#[^#]+#) \\d+ \\d+", "$1"));
                        int amount = Integer.parseInt(interpreter.prepareStringReplaceVar(pickRandom.get(selected).replaceAll("#[^#]+# (\\d+) \\d+", "$1")));
                        for (int k = 0; k < itemUIDs.length; k++) {
                            amountItems += amount;
                            interpreter.inventoryOperation("add #uid:" + itemUIDs[k] + "# to #uid:" + inventoryUID + "# amount " + amount);
                        }
                        interpreter.activateLog();
                        for (int k = 0; k < itemUIDs.length; k++)
                            Log.add("Drop " + amount + " x " + itemUIDs[k] + " to " + inventoryUID);
                    }
                }
                pickRandom.clear();
            }
        }
        GuiPlayerStats.updateOutput();
        Log.add("Dropped " + amountItems + " items into inventory " + inventoryUID);
        interpreter.executeEventFromObject(uid, "dropLootCode", new String[]{"inventory:" + inventoryUID});
        return amountItems;
    }
}
