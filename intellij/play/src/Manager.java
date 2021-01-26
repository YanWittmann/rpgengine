
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Manager {
    public static String filename = "", extraFilePath = "../generator/";
    public static ArrayList<Location> locations = new ArrayList<>();
    public static ArrayList<NPC> npcs = new ArrayList<>();
    public static ArrayList<Item> items = new ArrayList<>();
    public static ArrayList<BattleMap> battleMaps = new ArrayList<>();
    public static ArrayList<Inventory> inventories = new ArrayList<>();
    public static ArrayList<Talent> talents = new ArrayList<>();
    public static ArrayList<Event> events = new ArrayList<>();
    public static ArrayList<LootTable> lootTable = new ArrayList<>();
    public static ArrayList<CustomCommand> customCommands = new ArrayList<>();
    public static ArrayList<ColorObject> colors = new ArrayList<>();
    public static ArrayList<FileObject> fileObjects = new ArrayList<>();
    public static ArrayList<CustomPopup> popups = new ArrayList<>();
    public static Images images;
    public static Audios audios;
    public static Variables variables;
    public static PlayerSettings player;
    public static ProjectSettings project;
    private Event generalEventCollection;
    private final Interpreter interpreter;

    public static boolean ready = false;

    public Manager(Interpreter interpreter, String filename, String extraFilePath) {
        this.interpreter = interpreter;
        this.extraFilePath = extraFilePath;
        FileManager.clearTmp();
        openFile(filename);
    }

    public void openFile(String filename) {
        locations.clear();
        npcs.clear();
        items.clear();
        inventories.clear();
        battleMaps.clear();
        talents.clear();
        events.clear();
        customCommands.clear();
        colors.clear();
        lootTable.clear();
        fileObjects.clear();
        popups.clear();
        setFilename(filename);
        readFile();
    }

    public void setFilename(String filename) {
        this.filename = filename;
        try {
            images.setNamespace(filename);
            audios.setNamespace(filename);
        } catch (Exception e) {
        }
    }

    private void readFile() {
        if (filename.length() > 0) {
            if (!FileManager.fileExists(extraFilePath + "adventures/" + filename + "" + StaticStuff.adventureFileEnding)) {
                exitError("File does not exist.", 99);
            }
            FileManager.deleteDirectoryRecursively(extraFilePath + "adventures/" + filename);
            if (!FileManager.isArchive(extraFilePath + "adventures/" + filename + "" + StaticStuff.adventureFileEnding)) {
                String key = String.valueOf(StaticStuff.openPopup("<html>" + StaticStuff.prepareString(Interpreter.lang("loadAdventureFileEncr")), "").hashCode());
                CryptoUtils.decrypt(CryptoUtils.prepareKey(key), extraFilePath + "adventures/" + filename + "" + StaticStuff.adventureFileEnding, extraFilePath + "adventures/" + filename + "_decr" + StaticStuff.adventureFileEnding);
                setFilename(filename + "_decr");
            }
            if (filename.contains("_decr") && !FileManager.isArchive(extraFilePath + "adventures/" + filename + "" + StaticStuff.adventureFileEnding)) {
                FileManager.delete(extraFilePath + "adventures/" + filename + "" + StaticStuff.adventureFileEnding);
                StaticStuff.error("<html>" + Interpreter.lang("loadAdventureFileEncrWrongPassword"));
                System.exit(10);
            }

            FileManager.unzip(extraFilePath + "adventures/" + filename + "" + StaticStuff.adventureFileEnding, extraFilePath + "adventures/");
            if (filename.contains("_decr")) {
                FileManager.delete(extraFilePath + "adventures/" + filename + "" + StaticStuff.adventureFileEnding);
                setFilename(filename.replace("_decr", ""));
            }

            player = new PlayerSettings(FileManager.readFile(extraFilePath + "adventures/" + filename + "/project/player" + StaticStuff.dataFileEnding));
            project = new ProjectSettings(FileManager.readFile(extraFilePath + "adventures/" + filename + "/project/settings" + StaticStuff.dataFileEnding));
            String[] files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/locations", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    locations.add(new Location(FileManager.readFile(extraFilePath + "adventures/" + filename + "/locations/" + file)));
                } catch (Exception e) {
                    exitError("Location '" + file + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 100);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/npcs", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    npcs.add(new NPC(FileManager.readFile(extraFilePath + "adventures/" + filename + "/npcs/" + file)));
                } catch (Exception e) {
                    exitError("NPC '" + file + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 101);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/itemtypes", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    items.add(new Item(FileManager.readFile(extraFilePath + "adventures/" + filename + "/itemtypes/" + file)));
                } catch (Exception e) {
                    exitError("ItemType '" + file + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 102);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/inventories", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    inventories.add(new Inventory(FileManager.readFile(extraFilePath + "adventures/" + filename + "/inventories/" + file)));
                } catch (Exception e) {
                    exitError("Inventory '" + file + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 104);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/battlemaps", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    battleMaps.add(new BattleMap(FileManager.readFile(extraFilePath + "adventures/" + filename + "/battlemaps/" + file), this));
                } catch (Exception e) {
                    exitError("BattleMap '" + file + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 105);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/talents", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    talents.add(new Talent(FileManager.readFile(extraFilePath + "adventures/" + filename + "/talents/" + file)));
                } catch (Exception e) {
                    exitError("Talent '" + file + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 106);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/events", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    events.add(new Event(FileManager.readFile(extraFilePath + "adventures/" + filename + "/events/" + file)));
                } catch (Exception e) {
                    exitError("Event '" + file + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 107);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/lootTable", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    lootTable.add(new LootTable(FileManager.readFile(extraFilePath + "adventures/" + filename + "/lootTable/" + file)));
                } catch (Exception e) {
                    exitError("LootTable '" + file + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 107);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/customCommands", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    customCommands.add(new CustomCommand(FileManager.readFile(extraFilePath + "adventures/" + filename + "/customCommands/" + file)));
                } catch (Exception e) {
                    exitError("CustomCommand '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 108);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/colors", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    colors.add(new ColorObject(FileManager.readFile(extraFilePath + "adventures/" + filename + "/colors/" + file)));
                } catch (Exception e) {
                    exitError("Color '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/fileObjects", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    fileObjects.add(new FileObject(FileManager.readFile(extraFilePath + "adventures/" + filename + "/fileObjects/" + file), FileManager.readFileToByteArray(extraFilePath + "adventures/" + filename + "/fileObjects/" + file.replace(StaticStuff.dataFileEnding, "") + ".file")));
                } catch (Exception e) {
                    exitError("FileObject '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/popups", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    popups.add(new CustomPopup(FileManager.readFile(extraFilePath + "adventures/" + filename + "/popups/" + file)));
                } catch (Exception e) {
                    exitError("Color '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
            variables = new Variables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/variables/vars" + StaticStuff.dataFileEnding));
            images = new Images(filename, FileManager.readFile(extraFilePath + "adventures/" + filename + "/images/imagelist" + StaticStuff.dataFileEnding));
            audios = new Audios(filename, FileManager.readFile(extraFilePath + "adventures/" + filename + "/audio/audiolist" + StaticStuff.dataFileEnding));
            generalEventCollection = (Event) getEntity(getUID("General"));
            variables.addVariable("tmp", "String", "");
            variables.addVariable("foo", "String", "");
            variables.addVariable("bar", "String", "");
            ready = true;
        }
        FileManager.deleteDirectoryRecursively(extraFilePath + "adventures/" + filename);
    }

    public void exitError(String message, int code) {
        StaticStuff.error(message);
        FileManager.deleteDirectoryRecursively(extraFilePath + "adventures/" + filename);
        System.exit(code);
    }

    public ArrayList<String> getAllObjects() {
        ArrayList<String> objects = new ArrayList<String>();
        for (Location location : locations) objects.add(location.uid);
        for (NPC npc : npcs) objects.add(npc.uid);
        for (Item item : items) objects.add(item.uid);
        for (Inventory inv : inventories) objects.add(inv.uid);
        for (BattleMap battleMap : battleMaps) objects.add(battleMap.uid);
        for (Talent talent : talents) objects.add(talent.uid);
        for (Event event : events) objects.add(event.uid);
        for (LootTable loot : lootTable) objects.add(loot.uid);
        for (CustomCommand cc : customCommands) objects.add(cc.uid);
        for (ColorObject color : colors) objects.add(color.uid);
        for (FileObject fileObject : fileObjects) objects.add(fileObject.uid);
        for (CustomPopup popup : popups) objects.add(popup.uid);
        Collections.addAll(objects, audios.getUIDs());
        Collections.addAll(objects, images.getUIDs());
        Collections.addAll(objects, variables.getUIDs());
        return objects;
    }

    public static boolean lootTableExists(String uid) {
        for (LootTable table : lootTable)
            if (table.uid.equals(uid))
                return true;
        return false;
    }

    public static boolean itemTypeExists(String uid) {
        for (Item item : items)
            if (item.uid.equals(uid))
                return true;
        return false;
    }

    public static boolean battleMapExists(String uid) {
        for (BattleMap battleMap : battleMaps)
            if (battleMap.uid.equals(uid))
                return true;
        return false;
    }

    public static boolean locationExists(String uid) {
        for (Location location : locations)
            if (location.uid.equals(uid))
                return true;
        return false;
    }

    public static boolean inventoryExists(String uid) {
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid))
                return true;
        return false;
    }

    public static boolean npcExists(String uid) {
        for (NPC npc : npcs)
            if (npc.uid.equals(uid))
                return true;
        return false;
    }

    public static boolean imageExists(String uid) {
        return images.imageExists(uid);
    }

    public static BufferedImage getImage(String uid) {
        if (imageExists(uid)) return Images.getBufferedImage(uid);
        return Images.readImageFromFile("res/img/null.png");
    }

    public static String getImageName(String uid) {
        if (imageExists(uid)) return images.getImageName(uid);
        return "This image does not exist";
    }

    public static boolean audioExists(String uid) {
        return audios.audioExists(uid);
    }

    public static String getAudioName(String uid) {
        if (audioExists(uid)) return audios.getAudioName(uid);
        return "This image does not exist";
    }

    public static boolean variableExists(String uidOrName) {
        return variables.variableExists(uidOrName);
    }

    public static String getVariableValueByUID(String uid) {
        return variables.getValueByUID(uid);
    }

    public static String getVariableValueByName(String name) {
        return variables.getValueByName(name);
    }

    public static void setVariableValueByUID(String uid, String setValue) {
        variables.setValueByUID(uid, setValue);
    }

    public static void setVariableValueByName(String name, String setValue) {
        variables.setValueByName(name, setValue);
    }

    public static String[] getVariableNames() {
        String[] names = new String[variables.name.size()];
        for (int i = 0; i < names.length; i++) names[i] = variables.name.get(i);
        return names;
    }

    public static String getItemName(String uid) {
        for (Item item : items)
            if (item.uid.equals(uid))
                return item.name;
        return "item does not exist";
    }

    public static String getItemImageUID(String uid) {
        for (Item item : items)
            if (item.uid.equals(uid))
                if (item.image.equals("")) StaticStuff.error("This item does not have an image.");
                else return item.image;
        return "item does not exist";
    }

    public static String getLocationName(String uid) {
        for (Location location : locations)
            if (location.uid.equals(uid))
                return location.name;
        return "location does not exist";
    }

    public static String getLocationUID(String name) {
        for (Location location : locations)
            if (location.name.equals(name))
                return location.uid;
        return "location does not exist";
    }

    public static String getInventoryName(String uid) {
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid))
                return inventory.name;
        return "inventory does not exist";
    }

    public static String getBattleMapName(String uid) {
        for (BattleMap battleMap : battleMaps)
            if (battleMap.uid.equals(uid))
                return battleMap.name;
        return "battle map does not exist";
    }

    public static boolean openImage(String uid, String text, int size, boolean pauseProgram) {
        return images.openImage(uid, text, size, pauseProgram);
    }

    public String[] getTalentAttributes(String uid) {
        for (Talent talent : talents) if (talent.uid.equals(uid)) return talent.getAttributesArray();
        return "courage courage courage".split(" ");
    }

    public boolean isType(String uid, String type) {
        if (type.equals("npc")) for (NPC npc : npcs) if (npc.uid.equals(uid)) return true;
        if (type.equals("inventory"))
            for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return true;
        if (type.equals("location"))
            for (Location location : locations) if (location.uid.equals(uid)) return true;
        if (type.equals("item")) for (Item item : items) if (item.uid.equals(uid)) return true;
        if (type.equals("inventory"))
            for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return true;
        if (type.equals("talent"))
            for (Talent talent : talents) if (talent.uid.equals(uid)) return true;
        if (type.equals("battleMap"))
            for (BattleMap battleMap : battleMaps) if (battleMap.uid.equals(uid)) return true;
        if (type.equals("eventCollection"))
            for (Event event : events) if (event.uid.equals(uid)) return true;
        if (type.equals("lootTable"))
            for (LootTable table : lootTable) if (table.uid.equals(uid)) return true;
        if (type.equals("customCommand"))
            for (CustomCommand customCommand : customCommands) if (customCommand.uid.equals(uid)) return true;
        if (type.equals("color"))
            for (ColorObject color : colors) if (color.uid.equals(uid)) return true;
        if (type.equals("fileObject"))
            for (FileObject fileObject : fileObjects) if (fileObject.uid.equals(uid)) return true;
        if (type.equals("popup"))
            for (CustomPopup popup : popups) if (popup.uid.equals(uid)) return true;
        if (type.equals("audio")) if (audioExists(uid)) return true;
        if (type.equals("image")) if (imageExists(uid)) return true;
        if (type.equals("variable")) return variableExists(uid);
        return false;
    }

    public String getTypeByUID(String uid) {
        for (NPC npc : npcs) if (npc.uid.equals(uid)) return "npc";
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return "inventory";
        for (Location location : locations) if (location.uid.equals(uid)) return "location";
        for (Item item : items) if (item.uid.equals(uid)) return "item";
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return "inventory";
        for (Talent talent : talents) if (talent.uid.equals(uid)) return "talent";
        for (BattleMap battleMap : battleMaps) if (battleMap.uid.equals(uid)) return "battleMap";
        for (Event event : events) if (event.uid.equals(uid)) return "eventCollection";
        for (LootTable table : lootTable) if (table.uid.equals(uid)) return "lootTable";
        for (CustomCommand customCommand : customCommands) if (customCommand.uid.equals(uid)) return "customCommand";
        for (ColorObject color : colors) if (color.uid.equals(uid)) return "color";
        for (FileObject fileObject : fileObjects) if (fileObject.uid.equals(uid)) return "fileObject";
        for (CustomPopup popup : popups) if (popup.uid.equals(uid)) return "fileObject";
        if (audioExists(uid)) return "audio";
        if (imageExists(uid)) return "image";
        if (variableExists(uid)) return "variable";
        return "null";
    }

    public String getLocationFromNPC(String uid) {
        for (NPC npc : npcs) if (npc.uid.equals(uid)) return npc.location;
        return "null";
    }

    public void setLocationFromNPC(String uid, String value) {
        Log.add("Set location of " + uid + " to " + value);
        if (locationExists(value)) {
            for (NPC npc : npcs) if (npc.uid.equals(uid)) npc.location = value;
        }
    }

    public void setImage(String uid, String value) {
        Log.add("Set image " + value + " to " + uid);
        if (imageExists(value)) {
            for (NPC npc : npcs) if (npc.uid.equals(uid)) npc.image = value;
            for (Location location : locations) if (location.uid.equals(uid)) location.image = value;
            for (Item item : items) if (item.uid.equals(uid)) item.image = value;
        }
    }

    public ArrayList<String> getAllEventNames(String uid) {
        for (NPC npc : npcs) if (npc.uid.equals(uid)) return npc.eventName;
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return inventory.eventName;
        for (Location location : locations) if (location.uid.equals(uid)) return location.eventName;
        for (Item item : items) if (item.uid.equals(uid)) return item.eventName;
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return inventory.eventName;
        for (BattleMap battleMap : battleMaps) if (battleMap.uid.equals(uid)) return battleMap.eventName;
        for (Event event : events) if (event.uid.equals(uid)) return event.eventName;
        for (LootTable table : lootTable) if (table.uid.equals(uid)) return table.eventName;
        for (CustomCommand customCommand : customCommands)
            if (customCommand.uid.equals(uid)) return customCommand.eventName;
        for (ColorObject color : colors) if (color.uid.equals(uid)) return color.eventName;
        for (FileObject fileObject : fileObjects) if (fileObject.uid.equals(uid)) return fileObject.eventName;
        for (CustomPopup popup : popups) if (popup.uid.equals(uid)) return popup.eventName;
        return new ArrayList<String>();
    }

    public String getUID(String name) {
        for (NPC npc : npcs) if (npc.name.equals(name)) return npc.uid;
        for (Inventory inventory : inventories) if (inventory.name.equals(name)) return inventory.uid;
        for (Location location : locations) if (location.name.equals(name)) return location.uid;
        for (Item item : items) if (item.name.equals(name)) return item.uid;
        for (Inventory inventory : inventories) if (inventory.name.equals(name)) return inventory.uid;
        for (Talent talent : talents) if (talent.name.equals(name)) return talent.uid;
        for (BattleMap battleMap : battleMaps) if (battleMap.name.equals(name)) return battleMap.uid;
        for (Event event : events) if (event.name.equals(name)) return event.uid;
        for (LootTable table : lootTable) if (table.name.equals(name)) return table.uid;
        for (CustomCommand customCommand : customCommands)
            if (customCommand.name.equals(name)) return customCommand.uid;
        for (ColorObject color : colors) if (color.name.equals(name)) return color.uid;
        for (FileObject fileObject : fileObjects) if (fileObject.name.equals(name)) return fileObject.uid;
        for (CustomPopup popup : popups) if (popup.name.equals(name)) return popup.uid;
        if (audioExists(name)) return audios.getAudioUID(name);
        if (imageExists(name)) return images.getImageUID(name);
        if (variableExists(name)) return variables.getVariableUID(name);
        return "null";
    }

    public static String getName(String uid) {
        for (NPC npc : npcs) if (npc.uid.equals(uid)) return npc.name;
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return inventory.name;
        for (Location location : locations) if (location.uid.equals(uid)) return location.name;
        for (Item item : items) if (item.uid.equals(uid)) return item.name;
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return inventory.name;
        for (Talent talent : talents) if (talent.uid.equals(uid)) return talent.name;
        for (BattleMap battleMap : battleMaps) if (battleMap.uid.equals(uid)) return battleMap.name;
        for (Event event : events) if (event.uid.equals(uid)) return event.name;
        for (LootTable table : lootTable) if (table.uid.equals(uid)) return table.name;
        for (CustomCommand customCommand : customCommands) if (customCommand.uid.equals(uid)) return customCommand.name;
        for (ColorObject color : colors) if (color.uid.equals(uid)) return color.name;
        for (FileObject fileObject : fileObjects) if (fileObject.uid.equals(uid)) return fileObject.name;
        for (CustomPopup popup : popups) if (popup.uid.equals(uid)) return popup.name;
        if (audioExists(uid)) return audios.getAudioName(uid);
        if (imageExists(uid)) return images.getImageName(uid);
        if (variableExists(uid)) return variables.getVariableName(uid);
        return "null";
    }

    public static String getLocalVariableByNameOrUID(String uid, String varName) {
        if (StaticStuff.isValidUIDSilent(varName)) return getLocalVariableByUID(varName);
        else return getLocalVariableByName(uid, varName);
    }

    public static String getLocalVariableByUID(String varUID) {
        for (NPC npc : npcs)
            if (npc.localVarUids.contains(varUID))
                return npc.localVarValue.get(npc.localVarUids.indexOf(varUID));
        for (Inventory inventory : inventories)
            if (inventory.localVarUids.contains(varUID))
                return inventory.localVarValue.get(inventory.localVarUids.indexOf(varUID));
        for (Location location : locations)
            if (location.localVarUids.contains(varUID))
                return location.localVarValue.get(location.localVarUids.indexOf(varUID));
        for (Item item : items)
            if (item.localVarUids.contains(varUID))
                return item.localVarValue.get(item.localVarUids.indexOf(varUID));
        for (Inventory inventory : inventories)
            if (inventory.localVarUids.contains(varUID))
                return inventory.localVarValue.get(inventory.localVarUids.indexOf(varUID));
        for (BattleMap battleMap : battleMaps)
            if (battleMap.localVarUids.contains(varUID))
                return battleMap.localVarValue.get(battleMap.localVarUids.indexOf(varUID));
        for (Event event : events)
            if (event.localVarUids.contains(varUID))
                return event.localVarValue.get(event.localVarUids.indexOf(varUID));
        for (LootTable table : lootTable)
            if (table.localVarUids.contains(varUID))
                return table.localVarValue.get(table.localVarUids.indexOf(varUID));
        for (CustomCommand customCommand : customCommands)
            if (customCommand.localVarUids.contains(varUID))
                return customCommand.localVarValue.get(customCommand.localVarUids.indexOf(varUID));
        for (ColorObject color : colors)
            if (color.localVarUids.contains(varUID))
                return color.localVarValue.get(color.localVarUids.indexOf(varUID));
        for (FileObject fileObject : fileObjects)
            if (fileObject.localVarUids.contains(varUID))
                return fileObject.localVarValue.get(fileObject.localVarUids.indexOf(varUID));
        for (CustomPopup popup : popups)
            if (popup.localVarUids.contains(varUID))
                return popup.localVarValue.get(popup.localVarUids.indexOf(varUID));
        return "";
    }

    public static String getLocalVariableByName(String uid, String varName) {
        for (NPC npc : npcs)
            if (npc.uid.equals(uid)) if (npc.localVarName.contains(varName))
                return npc.localVarValue.get(npc.localVarName.indexOf(varName));
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid)) if (inventory.localVarName.contains(varName))
                return inventory.localVarValue.get(inventory.localVarName.indexOf(varName));
        for (Location location : locations)
            if (location.uid.equals(uid)) if (location.localVarName.contains(varName))
                return location.localVarValue.get(location.localVarName.indexOf(varName));
        for (Item item : items)
            if (item.uid.equals(uid))
                if (item.localVarName.contains(varName))
                    return item.localVarValue.get(item.localVarName.indexOf(varName));
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid)) if (inventory.localVarName.contains(varName))
                return inventory.localVarValue.get(inventory.localVarName.indexOf(varName));
        for (BattleMap battleMap : battleMaps)
            if (battleMap.uid.equals(uid)) if (battleMap.localVarName.contains(varName))
                return battleMap.localVarValue.get(battleMap.localVarName.indexOf(varName));
        for (Event event : events)
            if (event.uid.equals(uid)) if (event.localVarName.contains(varName))
                return event.localVarValue.get(event.localVarName.indexOf(varName));
        for (LootTable table : lootTable)
            if (table.uid.equals(uid)) if (table.localVarName.contains(varName))
                return table.localVarValue.get(table.localVarName.indexOf(varName));
        for (CustomCommand customCommand : customCommands)
            if (customCommand.uid.equals(uid)) if (customCommand.localVarName.contains(varName))
                return customCommand.localVarValue.get(customCommand.localVarName.indexOf(varName));
        for (ColorObject color : colors)
            if (color.uid.equals(uid)) if (color.localVarName.contains(varName))
                return color.localVarValue.get(color.localVarName.indexOf(varName));
        for (FileObject fileObject : fileObjects)
            if (fileObject.uid.equals(uid)) if (fileObject.localVarName.contains(varName))
                return fileObject.localVarValue.get(fileObject.localVarName.indexOf(varName));
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid)) if (popup.localVarName.contains(varName))
                return popup.localVarValue.get(popup.localVarName.indexOf(varName));
        return "";
    }

    public boolean hasLocalVariableByUID(String uid, String varUID) {
        for (NPC npc : npcs) if (npc.uid.equals(uid)) if (npc.localVarUids.contains(varUID)) return true;
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid)) if (inventory.localVarUids.contains(varUID)) return true;
        for (Location location : locations)
            if (location.uid.equals(uid)) if (location.localVarUids.contains(varUID)) return true;
        for (Item item : items) if (item.uid.equals(uid)) if (item.localVarUids.contains(varUID)) return true;
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid)) if (inventory.localVarUids.contains(varUID)) return true;
        for (BattleMap battleMap : battleMaps)
            if (battleMap.uid.equals(uid)) if (battleMap.localVarUids.contains(varUID)) return true;
        for (Event event : events) if (event.uid.equals(uid)) if (event.localVarUids.contains(varUID)) return true;
        for (LootTable table : lootTable)
            if (table.uid.equals(uid)) if (table.localVarUids.contains(varUID)) return true;
        for (CustomCommand customCommand : customCommands)
            if (customCommand.uid.equals(uid))
                if (customCommand.localVarUids.contains(varUID)) return true;
        for (ColorObject color : colors)
            if (color.uid.equals(uid)) if (color.localVarUids.contains(varUID)) return true;
        for (FileObject fileObject : fileObjects)
            if (fileObject.uid.equals(uid)) if (fileObject.localVarUids.contains(varUID)) return true;
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid)) if (popup.localVarUids.contains(varUID)) return true;
        return false;
    }

    public boolean hasLocalVariableByName(String uid, String varName) {
        for (NPC npc : npcs) if (npc.uid.equals(uid)) if (npc.localVarName.contains(varName)) return true;
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid)) if (inventory.localVarName.contains(varName)) return true;
        for (Location location : locations)
            if (location.uid.equals(uid)) if (location.localVarName.contains(varName)) return true;
        for (Item item : items) if (item.uid.equals(uid)) if (item.localVarName.contains(varName)) return true;
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid)) if (inventory.localVarName.contains(varName)) return true;
        for (BattleMap battleMap : battleMaps)
            if (battleMap.uid.equals(uid)) if (battleMap.localVarName.contains(varName)) return true;
        for (Event event : events) if (event.uid.equals(uid)) if (event.localVarName.contains(varName)) return true;
        for (LootTable table : lootTable)
            if (table.uid.equals(uid)) if (table.localVarName.contains(varName)) return true;
        for (CustomCommand customCommand : customCommands)
            if (customCommand.uid.equals(uid))
                if (customCommand.localVarName.contains(varName)) return true;
        for (ColorObject color : colors)
            if (color.uid.equals(uid)) if (color.localVarName.contains(varName)) return true;
        for (FileObject fileObject : fileObjects)
            if (fileObject.uid.equals(uid)) if (fileObject.localVarName.contains(varName)) return true;
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid)) if (popup.localVarName.contains(varName)) return true;
        return false;
    }

    public void setLocalVariableByVarNameObjectUID(String uid, String varName, String value) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid))
                if (npcs.get(i).localVarName.contains(varName)) npcs.get(i).setVariableByName(varName, value);
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) if (inventories.get(i).localVarName.contains(varName))
                inventories.get(i).setVariableByName(varName, value);
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid))
                if (locations.get(i).localVarName.contains(varName)) locations.get(i).setVariableByName(varName, value);
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid))
                if (items.get(i).localVarName.contains(varName)) items.get(i).setVariableByName(varName, value);
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) if (inventories.get(i).localVarName.contains(varName))
                inventories.get(i).setVariableByName(varName, value);
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) if (battleMaps.get(i).localVarName.contains(varName))
                battleMaps.get(i).setVariableByName(varName, value);
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).uid.equals(uid))
                if (events.get(i).localVarName.contains(varName)) events.get(i).setVariableByName(varName, value);
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid))
                if (lootTable.get(i).localVarName.contains(varName)) lootTable.get(i).setVariableByName(varName, value);
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) if (customCommands.get(i).localVarName.contains(varName))
                customCommands.get(i).setVariableByName(varName, value);
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).uid.equals(uid))
                if (colors.get(i).localVarName.contains(varName)) colors.get(i).setVariableByName(varName, value);
        for (FileObject fileObject : fileObjects)
            if (fileObject.uid.equals(uid)) if (fileObject.localVarName.contains(varName))
                fileObject.setVariableByName(varName, value);
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid)) if (popup.localVarName.contains(varName))
                popup.setVariableByName(varName, value);
    }

    public void setLocalVariableByVarUID(String varUID, String value) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).localVarUids.contains(varUID)) npcs.get(i).setVariableByUID(varUID, value);
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).localVarUids.contains(varUID)) inventories.get(i).setVariableByUID(varUID, value);
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).localVarUids.contains(varUID)) locations.get(i).setVariableByUID(varUID, value);
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).localVarUids.contains(varUID)) items.get(i).setVariableByUID(varUID, value);
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).localVarUids.contains(varUID)) inventories.get(i).setVariableByUID(varUID, value);
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).localVarUids.contains(varUID)) battleMaps.get(i).setVariableByUID(varUID, value);
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).localVarUids.contains(varUID)) events.get(i).setVariableByUID(varUID, value);
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).localVarUids.contains(varUID)) lootTable.get(i).setVariableByUID(varUID, value);
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).localVarUids.contains(varUID))
                customCommands.get(i).setVariableByUID(varUID, value);
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).localVarUids.contains(varUID)) colors.get(i).setVariableByUID(varUID, value);
        for (FileObject fileObject : fileObjects)
            if (fileObject.localVarUids.contains(varUID)) fileObject.setVariableByUID(varUID, value);
        for (CustomPopup popup : popups)
            if (popup.localVarUids.contains(varUID)) popup.setVariableByUID(varUID, value);
    }

    public boolean hasLocalVariableByVarUID(String varUID) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < inventories.size(); i++) if (inventories.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < locations.size(); i++) if (locations.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < items.size(); i++) if (items.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < inventories.size(); i++) if (inventories.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < battleMaps.size(); i++) if (battleMaps.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < events.size(); i++) if (events.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < lootTable.size(); i++) if (lootTable.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < colors.size(); i++) if (colors.get(i).localVarUids.contains(varUID)) return true;
        for (FileObject fileObject : fileObjects) if (fileObject.localVarUids.contains(varUID)) return true;
        for (CustomPopup popup : popups) if (popup.localVarUids.contains(varUID)) return true;
        return false;
    }

    public void setVariableByUIDorName(String uidOrName, String value) {
        if (StaticStuff.isValidUIDSilent(uidOrName)) setVariableValueByUID(uidOrName, value);
        else setVariableValueByName(uidOrName, value);
    }

    public void setName(String uid, String value) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) npcs.get(i).name = value;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) inventories.get(i).name = value;
        for (int i = 0; i < locations.size(); i++) if (locations.get(i).uid.equals(uid)) locations.get(i).name = value;
        for (int i = 0; i < items.size(); i++) if (items.get(i).uid.equals(uid)) items.get(i).name = value;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) inventories.get(i).name = value;
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) battleMaps.get(i).name = value;
        for (int i = 0; i < events.size(); i++) if (events.get(i).uid.equals(uid)) events.get(i).name = value;
        for (int i = 0; i < lootTable.size(); i++) if (lootTable.get(i).uid.equals(uid)) lootTable.get(i).name = value;
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) customCommands.get(i).name = value;
        for (int i = 0; i < colors.size(); i++) if (colors.get(i).uid.equals(uid)) colors.get(i).name = value;
        for (FileObject fileObject : fileObjects) if (fileObject.uid.equals(uid)) fileObject.name = value;
        for (CustomPopup popup : popups) if (popup.uid.equals(uid)) popup.name = value;
    }

    public void setDescription(String uid, String value) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) npcs.get(i).description = value;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) inventories.get(i).description = value;
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid)) locations.get(i).description = value;
        for (int i = 0; i < items.size(); i++) if (items.get(i).uid.equals(uid)) items.get(i).description = value;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) inventories.get(i).description = value;
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) battleMaps.get(i).description = value;
        for (int i = 0; i < events.size(); i++) if (events.get(i).uid.equals(uid)) events.get(i).description = value;
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid)) lootTable.get(i).description = value;
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) customCommands.get(i).description = value;
        for (int i = 0; i < colors.size(); i++) if (colors.get(i).uid.equals(uid)) colors.get(i).description = value;
        for (FileObject fileObject : fileObjects) if (fileObject.uid.equals(uid)) fileObject.description = value;
        for (CustomPopup popup : popups) if (popup.uid.equals(uid)) popup.description = value;
    }

    public boolean hasTag(String uid, String tag) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid)) if (npcs.get(i).tags.contains(tag)) return true;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) if (inventories.get(i).tags.contains(tag)) return true;
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid)) if (locations.get(i).tags.contains(tag)) return true;
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid)) if (items.get(i).tags.contains(tag)) return true;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) if (inventories.get(i).tags.contains(tag)) return true;
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) if (battleMaps.get(i).tags.contains(tag)) return true;
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).uid.equals(uid)) if (events.get(i).tags.contains(tag)) return true;
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid)) if (lootTable.get(i).tags.contains(tag)) return true;
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) if (customCommands.get(i).tags.contains(tag)) return true;
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).uid.equals(uid)) if (colors.get(i).tags.contains(tag)) return true;
        for (FileObject fileObject : fileObjects)
            if (fileObject.uid.equals(uid)) if (fileObject.tags.contains(tag)) return true;
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid)) if (popup.tags.contains(tag)) return true;
        return false;
    }

    public void addTag(String uid, String tag) {
        Log.add("Add tag " + tag + " to " + uid);
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid)) if (!npcs.get(i).tags.contains(tag)) npcs.get(i).tags.add(tag);
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid))
                if (!inventories.get(i).tags.contains(tag)) inventories.get(i).tags.add(tag);
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid))
                if (!locations.get(i).tags.contains(tag)) locations.get(i).tags.add(tag);
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid)) if (!items.get(i).tags.contains(tag)) items.get(i).tags.add(tag);
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid))
                if (!inventories.get(i).tags.contains(tag)) inventories.get(i).tags.add(tag);
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid))
                if (!battleMaps.get(i).tags.contains(tag)) battleMaps.get(i).tags.add(tag);
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).uid.equals(uid)) if (!events.get(i).tags.contains(tag)) events.get(i).tags.add(tag);
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid))
                if (!lootTable.get(i).tags.contains(tag)) lootTable.get(i).tags.add(tag);
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid))
                if (!customCommands.get(i).tags.contains(tag)) customCommands.get(i).tags.add(tag);
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).uid.equals(uid)) if (!colors.get(i).tags.contains(tag)) colors.get(i).tags.add(tag);
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid))
                if (!popup.tags.contains(tag)) popup.tags.add(tag);
    }

    public void removeTag(String uid, String tag) {
        Log.add("Remove tag " + tag + " from " + uid);
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid)) if (npcs.get(i).tags.contains(tag)) npcs.get(i).tags.remove(tag);
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid))
                if (inventories.get(i).tags.contains(tag)) inventories.get(i).tags.remove(tag);
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid))
                if (locations.get(i).tags.contains(tag)) locations.get(i).tags.remove(tag);
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid)) if (items.get(i).tags.contains(tag)) items.get(i).tags.remove(tag);
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid))
                if (inventories.get(i).tags.contains(tag)) inventories.get(i).tags.remove(tag);
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid))
                if (battleMaps.get(i).tags.contains(tag)) battleMaps.get(i).tags.remove(tag);
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).uid.equals(uid)) if (events.get(i).tags.contains(tag)) events.get(i).tags.remove(tag);
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid))
                if (lootTable.get(i).tags.contains(tag)) lootTable.get(i).tags.remove(tag);
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid))
                if (customCommands.get(i).tags.contains(tag)) customCommands.get(i).tags.remove(tag);
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).uid.equals(uid)) if (colors.get(i).tags.contains(tag)) colors.get(i).tags.remove(tag);
        for (FileObject object : fileObjects)
            if (object.uid.equals(uid))
                if (object.tags.contains(tag)) object.tags.remove(tag);
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid))
                if (popup.tags.contains(tag)) popup.tags.remove(tag);
    }

    public String[] getEventCode(String uid, String eventName) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid)) return npcs.get(i).getEventCode(eventName);
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) return inventories.get(i).getEventCode(eventName);
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid)) return locations.get(i).getEventCode(eventName);
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid)) return items.get(i).getEventCode(eventName);
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) return inventories.get(i).getEventCode(eventName);
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) return battleMaps.get(i).getEventCode(eventName);
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).uid.equals(uid)) return events.get(i).getEventCode(eventName);
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid)) return lootTable.get(i).getEventCode(eventName);
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) return customCommands.get(i).getEventCode(eventName);
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).uid.equals(uid)) return colors.get(i).getEventCode(eventName);
        for (FileObject fileObject : fileObjects)
            if (fileObject.uid.equals(uid)) return fileObject.getEventCode(eventName);
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid)) return popup.getEventCode(eventName);
        return new String[]{""};
    }

    public void openFileObjectFile(String uid) {
        Log.add("Opening fileObject file " + uid);
        for (FileObject fileObject : fileObjects) if (fileObject.uid.equals(uid)) fileObject.openFile();
    }

    public String[] getFileObjectAsTextArray(String uid) {
        Log.add("Getting text from fileObject " + uid);
        for (FileObject fileObject : fileObjects) if (fileObject.uid.equals(uid)) return fileObject.toStringArray();
        return new String[]{""};
    }

    public boolean isPlayerInventoryOverloaded() {
        try {
            return getInventoryWeight(player.getValue("inventory")) > Integer.parseInt(player.getValue("maxLoad"));
        } catch (Exception ignored) {
        }
        return false;
    }

    public int getInventoryWeight(String inventoryUid) {
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(inventoryUid)) return inventory.getInventoryWeight();
        return -1;
    }

    public boolean inventoryContains(String itemUid, String inventoryUid) {
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(inventoryUid))
                if (inventory.items.contains(itemUid)) return true;
        return false;
    }

    public int inventoryGetAmount(String itemUid, String inventoryUid) {
        int amount = 0;
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(inventoryUid)) if (inventory.items.contains(itemUid))
                amount = Integer.parseInt(inventory.itemAmount.get(inventory.items.indexOf(itemUid)));
        Log.add("Amount of " + itemUid + " in inventory " + inventoryUid + " is " + amount);
        return amount;
    }

    public void inventoryAddItem(String itemUid, String inventoryUid, int amount) {
        if (amount > 0) {
            Log.add("Adding " + amount + " of " + itemUid + " to " + inventoryUid);
            interpreter.executeEventFromObject(itemUid, "pickup", new String[]{"amount:" + amount, "inventory:" + inventoryUid});
            interpreter.executeEventFromObject(inventoryUid, "pickup", new String[]{"amount:" + amount, "item:" + itemUid});
        } else {
            Log.add("Removing " + amount + " of " + itemUid + " from " + inventoryUid);
            interpreter.executeEventFromObject(itemUid, "drop", new String[]{"amount:" + (amount * -1), "inventory:" + inventoryUid});
            interpreter.executeEventFromObject(inventoryUid, "drop", new String[]{"amount:" + (amount * -1), "item:" + itemUid});
        }
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(inventoryUid)) inventory.addItem(itemUid, amount);
    }

    public void inventorySetItem(String itemUid, String inventoryUid, int amount) {
        int before = inventoryGetAmount(itemUid, inventoryUid);
        if (before == 0 && amount == 0) ;
        else if (amount > before || before == 0) {
            interpreter.executeEventFromObject(itemUid, "pickup", new String[]{"amount:" + (amount - before), "inventory:" + inventoryUid});
            interpreter.executeEventFromObject(inventoryUid, "pickup", new String[]{"amount:" + (amount - before), "item:" + itemUid});
        } else {
            interpreter.executeEventFromObject(itemUid, "drop", new String[]{"amount:" + ((amount - before) * -1), "inventory:" + inventoryUid});
            interpreter.executeEventFromObject(inventoryUid, "drop", new String[]{"amount:" + ((amount - before) * -1), "item:" + itemUid});
        }
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(inventoryUid)) inventory.setItem(itemUid, amount);
    }

    public ArrayList<CustomCommand> getAllCC() {
        return customCommands;
    }

    public ArrayList<String> getAllCCFirstWords() {
        ArrayList<String> ret = new ArrayList<>();
        for (CustomCommand c : customCommands) {
            ret.add(StaticStuff.replaceLastCharacterIfEquals(c.getFirstWords(), " "));
        }
        StringBuilder autoComplete = new StringBuilder("");
        for (String s : ret)
            autoComplete.append(s + ",");
        interpreter.addAutoCompleteWords(StaticStuff.replaceLastCharacterIfEquals(autoComplete.toString(), ","));
        return ret;
    }

    public CustomCommand getCCObject(String name) {
        for (CustomCommand customCommand : customCommands)
            if (customCommand.syntaxName.equals(name)) return customCommand;
        return null;
    }

    public void playAudio(String uid) {
        Log.add("Play audio: " + uid);
        audios.playAudio(uid);
    }

    public BattleMap getBattleMap(String uid) {
        for (BattleMap battleMap : battleMaps) if (battleMap.uid.equals(uid)) return battleMap;
        return null;
    }

    public NPC getNPC(String uid) {
        for (NPC npc : npcs) if (npc.uid.equals(uid)) return npc;
        return null;
    }

    public int dropLootTable(String lootTableUID, String inventoryUID) {
        for (LootTable table : lootTable)
            if (table.uid.equals(lootTableUID)) return table.drop(interpreter, inventoryUID);
        return 0;
    }

    public static Entity getEntity(String uid) {
        for (NPC npc : npcs) if (npc.uid.equals(uid)) return npc;
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return inventory;
        for (Location location : locations) if (location.uid.equals(uid)) return location;
        for (Item item : items) if (item.uid.equals(uid)) return item;
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return inventory;
        for (BattleMap battleMap : battleMaps) if (battleMap.uid.equals(uid)) return battleMap;
        for (Event event : events) if (event.uid.equals(uid)) return event;
        for (LootTable table : lootTable) if (table.uid.equals(uid)) return table;
        for (CustomCommand customCommand : customCommands) if (customCommand.uid.equals(uid)) return customCommand;
        for (ColorObject color : colors) if (color.uid.equals(uid)) return color;
        for (FileObject fileObject : fileObjects) if (fileObject.uid.equals(uid)) return fileObject;
        for (CustomPopup popup : popups) if (popup.uid.equals(uid)) return popup;
        return null;
    }

    public ArrayList<Color> getAllColors() {
        ArrayList<Color> c = new ArrayList<>();
        for (ColorObject co : colors) c.add(co.color);
        return c;
    }

    public ArrayList<String> getAllColorNames() {
        ArrayList<String> c = new ArrayList<>();
        for (ColorObject co : colors) c.add(co.name);
        return c;
    }

    public ArrayList<String> getAllColorUIDs() {
        ArrayList<String> c = new ArrayList<>();
        for (ColorObject co : colors) c.add(co.uid);
        return c;
    }

    public static Color getColorByName(String name) {
        for (ColorObject co : colors) if (co.name.equals(name)) return co.color;
        return new Color(238, 0, 255);
    }

    public void endBattle() {
        interpreter.endBattle();
    }

    public void executeEventFromObject(String uid, String event, String args[]) {
        interpreter.executeEvent(uid, getEventCode(uid, event), args);
    }

    public void clearInventory(String uid) {
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) inventory.clearItems();
    }

    public Event getGeneralEventCollection() {
        return generalEventCollection;
    }

    private HashMap<String, GuiCustomPopup> customPopupsMap = new HashMap<>();

    public void openCustomPopup(String uid, String asName) {
        for (CustomPopup popup : popups) {
            if (popup.uid.equals(uid)) {
                customPopupsMap.put(asName, popup.openPopup(asName));
            }
        }
    }

    public void closeCustomPopup(String uidOrName) {
        for (Map.Entry<String, GuiCustomPopup> entry : customPopupsMap.entrySet()) {
            String key = entry.getKey();
            GuiCustomPopup value = entry.getValue();
            if (StaticStuff.isValidUIDSilent(uidOrName)) {
                if (value.getCustomPopup().uid.equals(uidOrName)) {
                    value.close();
                    customPopupsMap.remove(key);
                    return;
                }
            } else {
                if (key.equals(uidOrName)) {
                    value.close();
                    customPopupsMap.remove(key);
                    return;
                }
            }
        }
    }

    public String[] getCurrentCustomPopupNames() {
        String[] names = new String[customPopupsMap.size()];
        int counter = 0;
        for (Map.Entry<String, GuiCustomPopup> entry : customPopupsMap.entrySet()) {
            String key = entry.getKey();
            names[counter] = key;
            counter++;
        }
        return names;
    }

    public String[] getCurrentCustomPopupUIDs() {
        String[] uids = new String[customPopupsMap.size()];
        int counter = 0;
        for (Map.Entry<String, GuiCustomPopup> entry : customPopupsMap.entrySet()) {
            GuiCustomPopup value = entry.getValue();
            uids[counter] = value.getCustomPopup().uid;
            counter++;
        }
        return uids;
    }

    public GuiCustomPopup[] getCurrentCustomPopupObjects() {
        GuiCustomPopup[] objects = new GuiCustomPopup[customPopupsMap.size()];
        int counter = 0;
        for (Map.Entry<String, GuiCustomPopup> entry : customPopupsMap.entrySet()) {
            GuiCustomPopup value = entry.getValue();
            objects[counter] = value;
            counter++;
        }
        return objects;
    }

    public void setCustomPopupData(String uidOrName, String component, String attribute, String value) {
        for (Map.Entry<String, GuiCustomPopup> entry : customPopupsMap.entrySet()) {
            String key = entry.getKey();
            GuiCustomPopup entryValue = entry.getValue();
            if (StaticStuff.isValidUIDSilent(uidOrName)) {
                if (entryValue.getCustomPopup().uid.equals(uidOrName)) {
                    entryValue.setData(component, attribute, value);
                    return;
                }
            } else {
                if (key.equals(uidOrName)) {
                    entryValue.setData(component, attribute, value);
                    return;
                }
            }
        }
    }

    public final static String SAVESTATES_DIRECTORY = "../../adventures/savestates/adventures/";

    public void createSavestate(String extraName) {
        String localFilename = extraName + filename;
        localFilename = localFilename.replaceAll("([^-]+---)[^-]+---(.+)", "$1$2");
        try {
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename);
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/project");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/locations");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/npcs");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/inventories");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/itemtypes");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/battlemaps");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/audio");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/images");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/variables");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/talents");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/events");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/lootTable");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/customCommands");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/colors");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/fileObjects");
            FileManager.makeDirectory(SAVESTATES_DIRECTORY + localFilename + "/popups");
            FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/project/player" + StaticStuff.dataFileEnding, player.generateSaveString());
            FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/project/settings" + StaticStuff.dataFileEnding, project.generateSaveString());
            for (Location location : locations)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/locations/" + location.uid + "" + StaticStuff.dataFileEnding, location.generateSaveString());
            for (NPC npc : npcs)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/npcs/" + npc.uid + "" + StaticStuff.dataFileEnding, npc.generateSaveString());
            for (Item item : items)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/itemtypes/" + item.uid + "" + StaticStuff.dataFileEnding, item.generateSaveString());
            for (Inventory inventory : inventories)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/inventories/" + inventory.uid + "" + StaticStuff.dataFileEnding, inventory.generateSaveString());
            for (BattleMap battleMap : battleMaps)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/battlemaps/" + battleMap.uid + "" + StaticStuff.dataFileEnding, battleMap.generateSaveString());
            for (Talent talent : talents)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/talents/" + talent.uid + "" + StaticStuff.dataFileEnding, talent.generateSaveString());
            for (Event event : events)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/events/" + event.uid + "" + StaticStuff.dataFileEnding, event.generateSaveString());
            for (LootTable table : lootTable)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/lootTable/" + table.uid + "" + StaticStuff.dataFileEnding, table.generateSaveString());
            for (CustomCommand customCommand : customCommands)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/customCommands/" + customCommand.uid + "" + StaticStuff.dataFileEnding, customCommand.generateSaveString());
            for (ColorObject color : colors)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/colors/" + color.uid + "" + StaticStuff.dataFileEnding, color.generateSaveString());
            for (FileObject fileObject : fileObjects) {
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/fileObjects/" + fileObject.uid + "" + StaticStuff.dataFileEnding, fileObject.generateSaveString());
                FileManager.writeFileFromByteArray(SAVESTATES_DIRECTORY + localFilename + "/fileObjects/" + fileObject.uid + ".file", fileObject.getByteArray());
            }
            for (CustomPopup popup : popups)
                FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/colors/" + popup.uid + "" + StaticStuff.dataFileEnding, popup.generateSaveString());
            FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/variables/vars" + StaticStuff.dataFileEnding, variables.generateSaveString());
            FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/images/imagelist" + StaticStuff.dataFileEnding, images.generateSaveString(SAVESTATES_DIRECTORY + localFilename + "/images/"));
            FileManager.writeToFile(SAVESTATES_DIRECTORY + localFilename + "/audio/audiolist" + StaticStuff.dataFileEnding, audios.generateSaveString(SAVESTATES_DIRECTORY + localFilename + "/audio/"));
            FileManager.zipDirectory(SAVESTATES_DIRECTORY + localFilename, SAVESTATES_DIRECTORY + localFilename + "" + StaticStuff.adventureFileEnding);
            FileManager.deleteDirectoryRecursively(SAVESTATES_DIRECTORY + localFilename);
            if (!project.getValue("password").equals("") && project.getValue("requirePasswordToPlay").equals("true")) {
                CryptoUtils.encrypt(CryptoUtils.prepareKey(project.getValue("password")), SAVESTATES_DIRECTORY + localFilename + "" + StaticStuff.adventureFileEnding, SAVESTATES_DIRECTORY + localFilename + "" + StaticStuff.adventureFileEnding);
            }
            StaticStuff.openPopup(Interpreter.lang("savestateCreatedSuccess", extraName.replace("---", "")));
        } catch (Exception e) {
            e.printStackTrace();
            StaticStuff.error(Interpreter.lang("savestateCreatedFailed", extraName.replace("---", "")));
        }
    }
}