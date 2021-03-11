
import java.util.ArrayList;

public class BattleMap extends Entity {
    ArrayList<String> npcs = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> extraGroundTiles = new ArrayList<>();
    ArrayList<String> obstacles = new ArrayList<>();
    int size;
    String groundTileUID, playerStartingPos;

    public BattleMap() {
        name = "New BattleMap";
        description = "Description";
        size = 5;
        groundTileUID = "";
        playerStartingPos = "0AAA0";
        addEvent("start");
        addEvent("end");
        addEvent("walkOnTile");
        addEvent("damage");
        addEvent("dies");
        addEvent("walkOnItem");
        addEvent("rightClick");
    }

    public BattleMap(String[] fileInput) {
        try {
            name = fileInput[0];
            description = fileInput[1];
            uid = fileInput[2];
            size = Integer.parseInt(fileInput[3]);
            groundTileUID = fileInput[4];
            playerStartingPos = fileInput[5];
            for (int i = 6; i < fileInput.length; i++) {
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
                } else if (fileInput[i].contains("++extraGroundTile++")) {
                    extraGroundTiles.add(fileInput[i].replace("++extraGroundTile++", ""));
                } else if (fileInput[i].contains("++obstacles++")) {
                    obstacles.add(fileInput[i].replace("++obstacles++", ""));
                } else if (fileInput[i].contains("++npc++")) {
                    npcs.add(fileInput[i].replace("++npc++", ""));
                } else if (fileInput[i].contains("++item++")) {
                    items.add(fileInput[i].replace("++item++", ""));
                }
            }
        } catch (Exception e) {
            Popup.error(StaticStuff.PROJECT_NAME, "BattleMap '" + name + "' contains invalid data:\n" + e);
        }
    }

    public String generateSaveString() {
        String str = "" + name + "\n" + description + "\n" + uid + "\n" + size + "\n" + groundTileUID + "\n" + playerStartingPos;
        for (int i = 0; i < eventName.size(); i++)
            str = str + "\n++ev++" + eventName.get(i) + "---" + eventCode.get(i);
        for (int i = 0; i < tags.size(); i++)
            str = str + "\n++tag++" + tags.get(i);
        for (int i = 0; i < localVarName.size(); i++)
            str = str + "\n++variable++" + localVarUids.get(i) + "---" + localVarName.get(i) + "---" + localVarType.get(i) + "---" + localVarValue.get(i);
        for (int i = 0; i < extraGroundTiles.size(); i++)
            str = str + "\n++extraGroundTile++" + extraGroundTiles.get(i);
        for (int i = 0; i < obstacles.size(); i++)
            str = str + "\n++obstacles++" + obstacles.get(i);
        for (int i = 0; i < npcs.size(); i++)
            str = str + "\n++npc++" + npcs.get(i);
        for (int i = 0; i < items.size(); i++)
            str = str + "\n++item++" + items.get(i);
        return str;
    }

    public String generateInformation() {
        String str = "Name: " + name + "\nDescription: " + description + "\nEvents:";
        for (int i = 0; i < eventName.size(); i++)
            str = str + "\n   " + i + ": " + eventName.get(i);
        str = str + "\nTags:\n";
        for (int i = 0; i < tags.size(); i++)
            str = str + " " + i + ": " + tags.get(i) + ";";
        str = str + "\nLocal variables:\n";
        for (int i = 0; i < localVarName.size(); i++)
            str = str + " " + localVarUids.get(i) + " - " + localVarName.get(i) + " - " + localVarType.get(i) + " - " + localVarValue.get(i) + "\n";
        return str;
    }

    private boolean hasGroundTile(int x, int y) {
        for (int i = 0; i < extraGroundTiles.size(); i++)
            if (extraGroundTiles.get(i).split("AAA")[1].equals("" + x) && extraGroundTiles.get(i).split("AAA")[2].equals("" + y))
                return true;
        return false;
    }

    private boolean hasObstacle(int x, int y) {
        for (int i = 0; i < obstacles.size(); i++)
            if (obstacles.get(i).split("AAA")[1].equals("" + x) && obstacles.get(i).split("AAA")[2].equals("" + y))
                return true;
        return false;
    }

    private boolean hasNPC(int x, int y) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).split("AAA")[1].equals("" + x) && npcs.get(i).split("AAA")[2].equals("" + y)) return true;
        return false;
    }

    private boolean hasItem(int x, int y) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).split("AAA")[1].equals("" + x) && items.get(i).split("AAA")[2].equals("" + y)) return true;
        return false;
    }

    public boolean addGroundTile(String uid, int x, int y) {
        try {
            if (!hasGroundTile(x, y)) {
                extraGroundTiles.add(uid + "AAA" + x + "AAA" + y);
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addObstacle(String uid, int x, int y) {
        try {
            if (!hasObstacle(x, y)) {
                obstacles.add(uid + "AAA" + x + "AAA" + y);
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void removeEverything(int x, int y) {
        for (int i = 0; i < obstacles.size(); i++)
            if (obstacles.get(i).split("AAA")[1].equals("" + x) && obstacles.get(i).split("AAA")[2].equals("" + y))
                obstacles.remove(i);
        for (int i = 0; i < extraGroundTiles.size(); i++)
            if (extraGroundTiles.get(i).split("AAA")[1].equals("" + x) && extraGroundTiles.get(i).split("AAA")[2].equals("" + y))
                extraGroundTiles.remove(i);
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).split("AAA")[1].equals("" + x) && npcs.get(i).split("AAA")[2].equals("" + y))
                npcs.remove(i);
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).split("AAA")[1].equals("" + x) && items.get(i).split("AAA")[2].equals("" + y))
                items.remove(i);
    }

    public boolean addNPC(int x, int y) {
        if (!hasNPC(x, y)) {
            String npc = Popup.dropDown(StaticStuff.PROJECT_NAME, "Select an NPC UID", Manager.getStringArrayNPCs()).replaceAll(".+ - ([^-]+)", "$1");
            if (StaticStuff.isValidUID(npc)) {
                if (Manager.npcExists(npc)) {
                    String image = Popup.dropDown(StaticStuff.PROJECT_NAME, "Select an image UID", Manager.getStringArrayImages()).replaceAll(".+ - ([^-]+)", "$1");
                    if (StaticStuff.isValidUID(image)) {
                        if (Manager.npcExists(npc)) {
                            npcs.add(npc + "AAA" + x + "AAA" + y + "AAA" + image);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean addItem(int x, int y) {
        if (!hasItem(x, y)) {
            String item = Popup.dropDown(StaticStuff.PROJECT_NAME, "Select an item UID", Manager.getStringArrayItems()).replaceAll(".+ - ([^-]+)", "$1");
            if (StaticStuff.isValidUID(item)) {
                if (Manager.itemTypeExists(item)) {
                    items.add(item + "AAA" + x + "AAA" + y);
                    return true;
                }
            }
        }
        return false;
    }

    public int additionalRefactor(String find, String replace) {
        int occ = 0;
        occ += StaticStuff.countOccurrences(playerStartingPos, find);
        playerStartingPos = playerStartingPos.replace(find, replace);
        occ += StaticStuff.countOccurrences(groundTileUID, find);
        groundTileUID = groundTileUID.replace(find, replace);
        occ += StaticStuff.countOccurrences(size + "", find);
        size = Integer.parseInt((size + "").replace(find, replace));
        occ += StaticStuff.refactorArrayList(find, replace, extraGroundTiles);
        occ += StaticStuff.refactorArrayList(find, replace, items);
        occ += StaticStuff.refactorArrayList(find, replace, npcs);
        occ += StaticStuff.refactorArrayList(find, replace, obstacles);
        return occ;
    }

    public void openEditor() {
        new GuiEntityEditor(this, new String[]{"Name", "Description", "Add event", "Edit event", "Remove event", "Edit tags", "Edit variables", "Edit battle map"}, "BattleMap") {
            @Override
            public void buttonPressed(int index) {
                String str;
                int choice;
                switch (index) {
                    case 0:
                        str = Popup.input("New name:", name);
                        if (str == null) return;
                        if (str.equals("")) return;
                        name = str;
                        break;
                    case 1:
                        str = Popup.input("New description:", description);
                        if (str == null) return;
                        if (str.equals("")) return;
                        description = str;
                        break;
                    case 2:
                        str = Popup.input("Event name:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        addEvent(str);
                        break;
                    case 3:
                        str = Popup.input("Choose an event by its ID:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        new GuiActionEditor(getEntity(), Integer.parseInt(str));
                        break;
                    case 4:
                        str = Popup.input("Choose an event by its ID:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        deleteEvent(Integer.parseInt(str));
                        break;
                    case 5:
                        choice = Popup.selectButton(StaticStuff.PROJECT_NAME, "What do you want to do?", new String[]{"Add tag", "Remove tag", "Edit tag"});
                        if (choice == 0)
                            getEntity().addTag(Popup.input("Tag name:", ""));
                        else if (choice == 1)
                            getEntity().deleteTag(Integer.parseInt(Popup.input("Tag index:", "")));
                        else if (choice == 2)
                            getEntity().editTag(Integer.parseInt(Popup.input("Tag index:", "")), Popup.input("Tag name:", ""));
                        break;
                    case 6:
                        choice = Popup.selectButton(StaticStuff.PROJECT_NAME, "What do you want to do?", new String[]{"Add variable", "Remove variable", "Edit variable"});
                        if (choice == 0) {
                            getEntity().addVariable(Popup.input("Variable name:", ""), true);
                        } else if (choice == 1)
                            getEntity().removeVariable(StaticStuff.autoDetectUID("Variable uid:"));
                        else if (choice == 2)
                            getEntity().openVariable(StaticStuff.autoDetectUID("Variable uid:"), true);
                        break;
                    case 7:
                        new GuiBattleMapEditor((BattleMap) getEntity());
                        break;
                    default:
                        Popup.error(StaticStuff.PROJECT_NAME, "Invalid action\nButton " + index + " does not exist.");
                }
                update();
            }
        };
    }
}
