
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
    public static Images images;
    public static Audios audios;
    public static Variables variables;
    public static PlayerSettings player;
    public static ProjectSettings project;
    private Event generalEventCollection;
    private Interpreter interpreter;

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
            String files[] = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/locations", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    locations.add(new Location(FileManager.readFile(extraFilePath + "adventures/" + filename + "/locations/" + files[i])));
                } catch (Exception e) {
                    exitError("Location '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 100);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/npcs", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    npcs.add(new NPC(FileManager.readFile(extraFilePath + "adventures/" + filename + "/npcs/" + files[i])));
                } catch (Exception e) {
                    exitError("NPC '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 101);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/itemtypes", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    items.add(new Item(FileManager.readFile(extraFilePath + "adventures/" + filename + "/itemtypes/" + files[i])));
                } catch (Exception e) {
                    exitError("ItemType '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 102);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/inventories", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    inventories.add(new Inventory(FileManager.readFile(extraFilePath + "adventures/" + filename + "/inventories/" + files[i])));
                } catch (Exception e) {
                    exitError("Inventory '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 104);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/battlemaps", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    battleMaps.add(new BattleMap(FileManager.readFile(extraFilePath + "adventures/" + filename + "/battlemaps/" + files[i]), this));
                } catch (Exception e) {
                    exitError("BattleMap '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 105);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/talents", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    talents.add(new Talent(FileManager.readFile(extraFilePath + "adventures/" + filename + "/talents/" + files[i])));
                } catch (Exception e) {
                    exitError("Talent '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 106);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/events", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    events.add(new Event(FileManager.readFile(extraFilePath + "adventures/" + filename + "/events/" + files[i])));
                } catch (Exception e) {
                    exitError("Event '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 107);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/lootTable", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    lootTable.add(new LootTable(FileManager.readFile(extraFilePath + "adventures/" + filename + "/lootTable/" + files[i])));
                } catch (Exception e) {
                    exitError("LootTable '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 107);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/customCommands", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    customCommands.add(new CustomCommand(FileManager.readFile(extraFilePath + "adventures/" + filename + "/customCommands/" + files[i])));
                } catch (Exception e) {
                    exitError("CustomCommand '" + files[i] + "' contains invalid data.\nRPG Engine will exit:\n" + e, 108);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/colors", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    colors.add(new ColorObject(FileManager.readFile(extraFilePath + "adventures/" + filename + "/colors/" + files[i])));
                } catch (Exception e) {
                    exitError("Color '" + files[i] + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/fileObjects", StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    fileObjects.add(new FileObject(FileManager.readFile(extraFilePath + "adventures/" + filename + "/fileObjects/" + files[i]), FileManager.readFileToByteArray(extraFilePath + "adventures/" + filename + "/fileObjects/" + files[i].replace(StaticStuff.dataFileEnding, "") + ".file")));
                } catch (Exception e) {
                    exitError("FileObject '" + files[i] + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
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

    public void reload(String what) {
        String whatToReload[] = what.split(" ");
        for (int i = 0; i < whatToReload.length; i++) Log.add("Reloading: " + whatToReload[i]);
        if (filename.length() > 0) {
            if (!FileManager.fileExists(extraFilePath + "adventures/" + filename + "" + StaticStuff.adventureFileEnding)) {
                exitError("File does not exist.", 99);
            }
            FileManager.deleteDirectoryRecursively(extraFilePath + "adventures/" + filename);
            FileManager.unzip(extraFilePath + "adventures/" + filename + "" + StaticStuff.adventureFileEnding, extraFilePath + "adventures/");
            String files[] = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/locations", "" + StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    for (int j = 0; j < locations.size(); j++)
                        if (locations.get(j).uid.equals(files[i].replace("" + StaticStuff.dataFileEnding, ""))) {
                            if (what.contains("events") || what.contains("all"))
                                locations.get(i).setEvents(FileManager.readFile(extraFilePath + "adventures/" + filename + "/locations/" + files[i]));
                            if (what.contains("localvars") || what.contains("all"))
                                locations.get(i).setVariables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/locations/" + files[i]));
                            if (what.contains("names") || what.contains("all"))
                                locations.get(i).name = FileManager.readFile(extraFilePath + "adventures/" + filename + "/locations/" + files[i])[0];
                            break;
                        }
                } catch (Exception e) {
                    exitError("Location '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 100);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/npcs", "" + StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    for (int j = 0; j < npcs.size(); j++)
                        if (npcs.get(j).uid.equals(files[i].replace("" + StaticStuff.dataFileEnding, ""))) {
                            if (what.contains("events") || what.contains("all"))
                                npcs.get(i).setEvents(FileManager.readFile(extraFilePath + "adventures/" + filename + "/npcs/" + files[i]));
                            if (what.contains("localvars") || what.contains("all"))
                                npcs.get(i).setVariables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/npcs/" + files[i]));
                            if (what.contains("names") || what.contains("all"))
                                npcs.get(i).name = FileManager.readFile(extraFilePath + "adventures/" + filename + "/npcs/" + files[i])[0];
                            break;
                        }
                } catch (Exception e) {
                    exitError("NPC '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 101);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/itemtypes", "" + StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    for (int j = 0; j < items.size(); j++)
                        if (items.get(j).uid.equals(files[i].replace("" + StaticStuff.dataFileEnding, ""))) {
                            if (what.contains("events") || what.contains("all"))
                                items.get(i).setEvents(FileManager.readFile(extraFilePath + "adventures/" + filename + "/itemtypes/" + files[i]));
                            if (what.contains("localvars") || what.contains("all"))
                                items.get(i).setVariables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/itemtypes/" + files[i]));
                            if (what.contains("names") || what.contains("all"))
                                items.get(i).name = FileManager.readFile(extraFilePath + "adventures/" + filename + "/itemtypes/" + files[i])[0];
                            break;
                        }
                } catch (Exception e) {
                    exitError("ItemType '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 102);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/inventories", "" + StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    for (int j = 0; j < inventories.size(); j++)
                        if (inventories.get(j).uid.equals(files[i].replace("" + StaticStuff.dataFileEnding, ""))) {
                            if (what.contains("events") || what.contains("all"))
                                inventories.get(i).setEvents(FileManager.readFile(extraFilePath + "adventures/" + filename + "/inventories/" + files[i]));
                            if (what.contains("localvars") || what.contains("all"))
                                inventories.get(i).setVariables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/inventories/" + files[i]));
                            if (what.contains("names") || what.contains("all"))
                                inventories.get(i).name = FileManager.readFile(extraFilePath + "adventures/" + filename + "/inventories/" + files[i])[0];
                            break;
                        }
                } catch (Exception e) {
                    exitError("Inventory '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 104);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/battlemaps", "" + StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    for (int j = 0; j < battleMaps.size(); j++)
                        if (battleMaps.get(j).uid.equals(files[i].replace("" + StaticStuff.dataFileEnding, ""))) {
                            if (what.contains("events") || what.contains("all"))
                                battleMaps.get(i).setEvents(FileManager.readFile(extraFilePath + "adventures/" + filename + "/battlemaps/" + files[i]));
                            if (what.contains("localvars") || what.contains("all"))
                                battleMaps.get(i).setVariables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/battlemaps/" + files[i]));
                            if (what.contains("names") || what.contains("all"))
                                battleMaps.get(i).name = FileManager.readFile(extraFilePath + "adventures/" + filename + "/battlemaps/" + files[i])[0];
                            break;
                        }
                } catch (Exception e) {
                    exitError("BattleMap '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 105);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/events", "" + StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    for (int j = 0; j < events.size(); j++)
                        if (events.get(j).uid.equals(files[i].replace("" + StaticStuff.dataFileEnding, ""))) {
                            if (what.contains("events") || what.contains("all"))
                                events.get(i).setEvents(FileManager.readFile(extraFilePath + "adventures/" + filename + "/events/" + files[i]));
                            if (what.contains("localvars") || what.contains("all"))
                                events.get(i).setVariables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/events/" + files[i]));
                            if (what.contains("names") || what.contains("all"))
                                events.get(i).name = FileManager.readFile(extraFilePath + "adventures/" + filename + "/events/" + files[i])[0];
                            break;
                        }
                } catch (Exception e) {
                    exitError("Event '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 107);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/lootTable", "" + StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    for (int j = 0; j < lootTable.size(); j++)
                        if (lootTable.get(j).uid.equals(files[i].replace("" + StaticStuff.dataFileEnding, ""))) {
                            if (what.contains("events") || what.contains("all"))
                                events.get(i).setEvents(FileManager.readFile(extraFilePath + "adventures/" + filename + "/events/" + files[i]));
                            if (what.contains("localvars") || what.contains("all"))
                                events.get(i).setVariables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/events/" + files[i]));
                            if (what.contains("names") || what.contains("all"))
                                events.get(i).name = FileManager.readFile(extraFilePath + "adventures/" + filename + "/events/" + files[i])[0];
                            break;
                        }
                } catch (Exception e) {
                    exitError("Loot Table '" + files[i] + "' contains invalid data.<br>RPG Engine will exit:<br>" + e, 107);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/customCommands", "" + StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    for (int j = 0; j < customCommands.size(); j++)
                        if (customCommands.get(j).uid.equals(files[i].replace("" + StaticStuff.dataFileEnding, ""))) {
                            if (what.contains("events") || what.contains("all"))
                                customCommands.get(i).setEvents(FileManager.readFile(extraFilePath + "adventures/" + filename + "/customCommands/" + files[i]));
                            if (what.contains("localvars") || what.contains("all"))
                                customCommands.get(i).setVariables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/customCommands/" + files[i]));
                            if (what.contains("names") || what.contains("all"))
                                customCommands.get(i).name = FileManager.readFile(extraFilePath + "adventures/" + filename + "/customCommands/" + files[i])[0];
                            break;
                        }
                } catch (Exception e) {
                    exitError("CustomCommand '" + files[i] + "' contains invalid data.\nRPG Engine will exit:\n" + e, 108);
                }
            }
            files = FileManager.getFilesWithEnding(extraFilePath + "adventures/" + filename + "/colors", "" + StaticStuff.dataFileEndingNoDot);
            for (int i = 0; i < files.length; i++) {
                try {
                    for (int j = 0; j < colors.size(); j++)
                        if (colors.get(j).uid.equals(files[i].replace("" + StaticStuff.dataFileEnding, ""))) {
                            if (what.contains("events") || what.contains("all"))
                                colors.get(i).setEvents(FileManager.readFile(extraFilePath + "adventures/" + filename + "/colors/" + files[i]));
                            if (what.contains("localvars") || what.contains("all"))
                                colors.get(i).setVariables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/colors/" + files[i]));
                            if (what.contains("names") || what.contains("all"))
                                colors.get(i).name = FileManager.readFile(extraFilePath + "adventures/" + filename + "/colors/" + files[i])[0];
                            break;
                        }
                } catch (Exception e) {
                    exitError("Color '" + files[i] + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
        }
        if (what.contains("variables") || what.contains("all")) variables = new
                Variables(FileManager.readFile(extraFilePath + "adventures/" + filename + "/variables/vars" + StaticStuff.dataFileEnding));
        if (what.contains("images") || what.contains("all")) images = new
                Images(filename, FileManager.readFile(extraFilePath + "adventures/" + filename + "/images/imagelist" + StaticStuff.dataFileEnding));
        if (what.contains("audios") || what.contains("all")) audios = new
                Audios(filename, FileManager.readFile(extraFilePath + "adventures/" + filename + "/audio/audiolist" + StaticStuff.dataFileEnding));
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
        for (String audio : audios.getUIDs()) objects.add(audio);
        for (String image : images.getUIDs()) objects.add(image);
        for (String variable : variables.getUIDs()) objects.add(variable);
        return objects;
    }

    public static boolean lootTableExists(String uid) {
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid))
                return true;
        return false;
    }

    public static boolean itemTypeExists(String uid) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid))
                return true;
        return false;
    }

    public static boolean battleMapExists(String uid) {
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid))
                return true;
        return false;
    }

    public static boolean locationExists(String uid) {
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid))
                return true;
        return false;
    }

    public static boolean inventoryExists(String uid) {
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid))
                return true;
        return false;
    }

    public static boolean npcExists(String uid) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid))
                return true;
        return false;
    }

    public static boolean imageExists(String uid) {
        return images.imageExists(uid);
    }

    public static BufferedImage getImage(String uid) {
        if (imageExists(uid)) return images.getBufferedImage(uid);
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
        String names[] = new String[variables.name.size()];
        for (int i = 0; i < names.length; i++) names[i] = variables.name.get(i);
        return names;
    }

    public static String getItemName(String uid) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid))
                return items.get(i).name;
        return "item does not exist";
    }

    public static String getItemImageUID(String uid) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid))
                if (items.get(i).image.equals("")) StaticStuff.error("This item does not have an image.");
                else return items.get(i).image;
        return "item does not exist";
    }

    public static String getLocationName(String uid) {
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid))
                return locations.get(i).name;
        return "location does not exist";
    }

    public static String getLocationUID(String name) {
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).name.equals(name))
                return locations.get(i).uid;
        return "location does not exist";
    }

    public static String getInventoryName(String uid) {
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid))
                return inventories.get(i).name;
        return "inventory does not exist";
    }

    public static String getBattleMapName(String uid) {
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid))
                return battleMaps.get(i).name;
        return "battle map does not exist";
    }

    public static boolean openImage(String uid, String text, int size, boolean pauseProgram) {
        return images.openImage(uid, text, size, pauseProgram);
    }

    public String[] getTalentAttributes(String uid) {
        for (int i = 0; i < talents.size(); i++)
            if (talents.get(i).uid.equals(uid)) return talents.get(i).getAttributesArray();
        return "courage courage courage".split(" ");
    }

    public boolean isType(String uid, String type) {
        if (type.equals("npc")) for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) return true;
        if (type.equals("inventory"))
            for (int i = 0; i < inventories.size(); i++) if (inventories.get(i).uid.equals(uid)) return true;
        if (type.equals("location"))
            for (int i = 0; i < locations.size(); i++) if (locations.get(i).uid.equals(uid)) return true;
        if (type.equals("item")) for (int i = 0; i < items.size(); i++) if (items.get(i).uid.equals(uid)) return true;
        if (type.equals("inventory"))
            for (int i = 0; i < inventories.size(); i++) if (inventories.get(i).uid.equals(uid)) return true;
        if (type.equals("talent"))
            for (int i = 0; i < talents.size(); i++) if (talents.get(i).uid.equals(uid)) return true;
        if (type.equals("battleMap"))
            for (int i = 0; i < battleMaps.size(); i++) if (battleMaps.get(i).uid.equals(uid)) return true;
        if (type.equals("eventCollection"))
            for (int i = 0; i < events.size(); i++) if (events.get(i).uid.equals(uid)) return true;
        if (type.equals("lootTable"))
            for (int i = 0; i < lootTable.size(); i++) if (lootTable.get(i).uid.equals(uid)) return true;
        if (type.equals("customCommand"))
            for (int i = 0; i < customCommands.size(); i++) if (customCommands.get(i).uid.equals(uid)) return true;
        if (type.equals("color"))
            for (int i = 0; i < colors.size(); i++) if (colors.get(i).uid.equals(uid)) return true;
        if (type.equals("fileObject"))
            for (int i = 0; i < fileObjects.size(); i++) if (fileObjects.get(i).uid.equals(uid)) return true;
        if (type.equals("audio")) if (audioExists(uid)) return true;
        if (type.equals("image")) if (imageExists(uid)) return true;
        if (type.equals("variable")) if (variableExists(uid)) return true;
        return false;
    }

    public String getTypeByUID(String uid) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) return "npc";
        for (int i = 0; i < inventories.size(); i++) if (inventories.get(i).uid.equals(uid)) return "inventory";
        for (int i = 0; i < locations.size(); i++) if (locations.get(i).uid.equals(uid)) return "location";
        for (int i = 0; i < items.size(); i++) if (items.get(i).uid.equals(uid)) return "item";
        for (int i = 0; i < inventories.size(); i++) if (inventories.get(i).uid.equals(uid)) return "inventory";
        for (int i = 0; i < talents.size(); i++) if (talents.get(i).uid.equals(uid)) return "talent";
        for (int i = 0; i < battleMaps.size(); i++) if (battleMaps.get(i).uid.equals(uid)) return "battleMap";
        for (int i = 0; i < events.size(); i++) if (events.get(i).uid.equals(uid)) return "eventCollection";
        for (int i = 0; i < lootTable.size(); i++) if (lootTable.get(i).uid.equals(uid)) return "lootTable";
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) return "customCommand";
        for (int i = 0; i < colors.size(); i++) if (colors.get(i).uid.equals(uid)) return "color";
        for (int i = 0; i < fileObjects.size(); i++) if (fileObjects.get(i).uid.equals(uid)) return "fileObject";
        if (audioExists(uid)) return "audio";
        if (imageExists(uid)) return "image";
        if (variableExists(uid)) return "variable";
        return "null";
    }

    public String getLocationFromNPC(String uid) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) return npcs.get(i).location;
        return "null";
    }

    public void setLocationFromNPC(String uid, String value) {
        Log.add("Set location of " + uid + " to " + value);
        if (locationExists(value)) {
            for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) npcs.get(i).location = value;
        }
    }

    public void setImage(String uid, String value) {
        Log.add("Set image " + value + " to " + uid);
        if (imageExists(value)) {
            for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) npcs.get(i).image = value;
            for (int i = 0; i < locations.size(); i++)
                if (locations.get(i).uid.equals(uid)) locations.get(i).image = value;
            for (int i = 0; i < items.size(); i++) if (items.get(i).uid.equals(uid)) items.get(i).image = value;
        }
    }

    public ArrayList<String> getAllEventNames(String uid) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) return npcs.get(i).eventName;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) return inventories.get(i).eventName;
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid)) return locations.get(i).eventName;
        for (int i = 0; i < items.size(); i++) if (items.get(i).uid.equals(uid)) return items.get(i).eventName;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) return inventories.get(i).eventName;
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) return battleMaps.get(i).eventName;
        for (int i = 0; i < events.size(); i++) if (events.get(i).uid.equals(uid)) return events.get(i).eventName;
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid)) return lootTable.get(i).eventName;
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) return customCommands.get(i).eventName;
        for (int i = 0; i < colors.size(); i++) if (colors.get(i).uid.equals(uid)) return colors.get(i).eventName;
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) return fileObjects.get(i).eventName;
        return new ArrayList<String>();
    }

    public String getDescription(String uid) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) return npcs.get(i).description;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) return inventories.get(i).description;
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid)) return locations.get(i).description;
        for (int i = 0; i < items.size(); i++) if (items.get(i).uid.equals(uid)) return items.get(i).description;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) return inventories.get(i).description;
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) return battleMaps.get(i).description;
        for (int i = 0; i < events.size(); i++) if (events.get(i).uid.equals(uid)) return events.get(i).description;
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid)) return lootTable.get(i).description;
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) return customCommands.get(i).description;
        for (int i = 0; i < colors.size(); i++) if (colors.get(i).uid.equals(uid)) return colors.get(i).description;
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) return fileObjects.get(i).description;
        return "null";
    }

    public String getUID(String name) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).name.equals(name)) return npcs.get(i).uid;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).name.equals(name)) return inventories.get(i).uid;
        for (int i = 0; i < locations.size(); i++) if (locations.get(i).name.equals(name)) return locations.get(i).uid;
        for (int i = 0; i < items.size(); i++) if (items.get(i).name.equals(name)) return items.get(i).uid;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).name.equals(name)) return inventories.get(i).uid;
        for (int i = 0; i < talents.size(); i++) if (talents.get(i).name.equals(name)) return talents.get(i).uid;
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).name.equals(name)) return battleMaps.get(i).uid;
        for (int i = 0; i < events.size(); i++) if (events.get(i).name.equals(name)) return events.get(i).uid;
        for (int i = 0; i < lootTable.size(); i++) if (lootTable.get(i).name.equals(name)) return lootTable.get(i).uid;
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).name.equals(name)) return customCommands.get(i).uid;
        for (int i = 0; i < colors.size(); i++) if (colors.get(i).name.equals(name)) return colors.get(i).uid;
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).name.equals(name)) return fileObjects.get(i).uid;
        if (audioExists(name)) return audios.getAudioUID(name);
        if (imageExists(name)) return images.getImageUID(name);
        if (variableExists(name)) return variables.getVariableUID(name);
        return "null";
    }

    public static String getName(String uid) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) return npcs.get(i).name;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) return inventories.get(i).name;
        for (int i = 0; i < locations.size(); i++) if (locations.get(i).uid.equals(uid)) return locations.get(i).name;
        for (int i = 0; i < items.size(); i++) if (items.get(i).uid.equals(uid)) return items.get(i).name;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) return inventories.get(i).name;
        for (int i = 0; i < talents.size(); i++) if (talents.get(i).uid.equals(uid)) return talents.get(i).name;
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) return battleMaps.get(i).name;
        for (int i = 0; i < events.size(); i++) if (events.get(i).uid.equals(uid)) return events.get(i).name;
        for (int i = 0; i < lootTable.size(); i++) if (lootTable.get(i).uid.equals(uid)) return lootTable.get(i).name;
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) return customCommands.get(i).name;
        for (int i = 0; i < colors.size(); i++) if (colors.get(i).uid.equals(uid)) return colors.get(i).name;
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) return fileObjects.get(i).name;
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
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).localVarUids.contains(varUID))
                return npcs.get(i).localVarValue.get(npcs.get(i).localVarUids.indexOf(varUID));
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).localVarUids.contains(varUID))
                return inventories.get(i).localVarValue.get(inventories.get(i).localVarUids.indexOf(varUID));
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).localVarUids.contains(varUID))
                return locations.get(i).localVarValue.get(locations.get(i).localVarUids.indexOf(varUID));
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).localVarUids.contains(varUID))
                return items.get(i).localVarValue.get(items.get(i).localVarUids.indexOf(varUID));
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).localVarUids.contains(varUID))
                return inventories.get(i).localVarValue.get(inventories.get(i).localVarUids.indexOf(varUID));
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).localVarUids.contains(varUID))
                return battleMaps.get(i).localVarValue.get(battleMaps.get(i).localVarUids.indexOf(varUID));
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).localVarUids.contains(varUID))
                return events.get(i).localVarValue.get(events.get(i).localVarUids.indexOf(varUID));
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).localVarUids.contains(varUID))
                return lootTable.get(i).localVarValue.get(lootTable.get(i).localVarUids.indexOf(varUID));
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).localVarUids.contains(varUID))
                return customCommands.get(i).localVarValue.get(customCommands.get(i).localVarUids.indexOf(varUID));
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).localVarUids.contains(varUID))
                return colors.get(i).localVarValue.get(colors.get(i).localVarUids.indexOf(varUID));
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).localVarUids.contains(varUID))
                return fileObjects.get(i).localVarValue.get(fileObjects.get(i).localVarUids.indexOf(varUID));
        return "";
    }

    public static String getLocalVariableByName(String uid, String varName) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid)) if (npcs.get(i).localVarName.contains(varName))
                return npcs.get(i).localVarValue.get(npcs.get(i).localVarName.indexOf(varName));
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) if (inventories.get(i).localVarName.contains(varName))
                return inventories.get(i).localVarValue.get(inventories.get(i).localVarName.indexOf(varName));
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid)) if (locations.get(i).localVarName.contains(varName))
                return locations.get(i).localVarValue.get(locations.get(i).localVarName.indexOf(varName));
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid)) if (items.get(i).localVarName.contains(varName))
                return items.get(i).localVarValue.get(items.get(i).localVarName.indexOf(varName));
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) if (inventories.get(i).localVarName.contains(varName))
                return inventories.get(i).localVarValue.get(inventories.get(i).localVarName.indexOf(varName));
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) if (battleMaps.get(i).localVarName.contains(varName))
                return battleMaps.get(i).localVarValue.get(battleMaps.get(i).localVarName.indexOf(varName));
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).uid.equals(uid)) if (events.get(i).localVarName.contains(varName))
                return events.get(i).localVarValue.get(events.get(i).localVarName.indexOf(varName));
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid)) if (lootTable.get(i).localVarName.contains(varName))
                return lootTable.get(i).localVarValue.get(lootTable.get(i).localVarName.indexOf(varName));
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) if (customCommands.get(i).localVarName.contains(varName))
                return customCommands.get(i).localVarValue.get(customCommands.get(i).localVarName.indexOf(varName));
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).uid.equals(uid)) if (colors.get(i).localVarName.contains(varName))
                return colors.get(i).localVarValue.get(colors.get(i).localVarName.indexOf(varName));
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) if (fileObjects.get(i).localVarName.contains(varName))
                return fileObjects.get(i).localVarValue.get(fileObjects.get(i).localVarName.indexOf(varName));
        return "";
    }

    public boolean hasLocalVariableByUID(String uid, String varUID) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid)) if (npcs.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) if (inventories.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid)) if (locations.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid)) if (items.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) if (inventories.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) if (battleMaps.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).uid.equals(uid)) if (events.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid)) if (lootTable.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid))
                if (customCommands.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).uid.equals(uid)) if (colors.get(i).localVarUids.contains(varUID)) return true;
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) if (fileObjects.get(i).localVarUids.contains(varUID)) return true;
        return false;
    }

    public boolean hasLocalVariableByName(String uid, String varName) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid)) if (npcs.get(i).localVarName.contains(varName)) return true;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) if (inventories.get(i).localVarName.contains(varName)) return true;
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid)) if (locations.get(i).localVarName.contains(varName)) return true;
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid)) if (items.get(i).localVarName.contains(varName)) return true;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) if (inventories.get(i).localVarName.contains(varName)) return true;
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) if (battleMaps.get(i).localVarName.contains(varName)) return true;
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).uid.equals(uid)) if (events.get(i).localVarName.contains(varName)) return true;
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid)) if (lootTable.get(i).localVarName.contains(varName)) return true;
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid))
                if (customCommands.get(i).localVarName.contains(varName)) return true;
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).uid.equals(uid)) if (colors.get(i).localVarName.contains(varName)) return true;
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) if (fileObjects.get(i).localVarName.contains(varName)) return true;
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
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) if (fileObjects.get(i).localVarName.contains(varName))
                fileObjects.get(i).setVariableByName(varName, value);
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
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).localVarUids.contains(varUID)) fileObjects.get(i).setVariableByUID(varUID, value);
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
        for (int i = 0; i < fileObjects.size(); i++) if (fileObjects.get(i).localVarUids.contains(varUID)) return true;
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
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) fileObjects.get(i).name = value;
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
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) fileObjects.get(i).description = value;
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
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) if (fileObjects.get(i).tags.contains(tag)) return true;
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
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid))
                if (!fileObjects.get(i).tags.contains(tag)) fileObjects.get(i).tags.add(tag);
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
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid))
                if (fileObjects.get(i).tags.contains(tag)) fileObjects.get(i).tags.remove(tag);
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
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) return fileObjects.get(i).getEventCode(eventName);
        return new String[]{""};
    }

    public void openFileObjectFile(String uid) {
        Log.add("Opening fileObject file " + uid);
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) fileObjects.get(i).openFile();
    }

    public String[] getFileObjectAsTextArray(String uid) {
        Log.add("Getting text from fileObject " + uid);
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) return fileObjects.get(i).toStringArray();
        return new String[]{""};
    }

    public boolean isPlayerInventoryOverloaded() {
        try {
            return getInventoryWeight(player.getValue("inventory")) > Integer.parseInt(player.getValue("maxLoad"));
        } catch (Exception e) {
        }
        return false;
    }

    public int getInventoryWeight(String inventoryUid) {
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(inventoryUid)) return inventories.get(i).getInventoryWeight();
        return -1;
    }

    public boolean inventoryContains(String itemUid, String inventoryUid) {
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(inventoryUid))
                if (inventories.get(i).items.contains(itemUid)) return true;
        return false;
    }

    public int inventoryGetAmount(String itemUid, String inventoryUid) {
        int amount = 0;
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(inventoryUid)) if (inventories.get(i).items.contains(itemUid))
                amount = Integer.parseInt(inventories.get(i).itemAmount.get(inventories.get(i).items.indexOf(itemUid)));
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
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(inventoryUid)) inventories.get(i).addItem(itemUid, amount);
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
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(inventoryUid)) inventories.get(i).setItem(itemUid, amount);
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
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).syntaxName.equals(name)) return customCommands.get(i);
        return null;
    }

    public void playAudio(String uid) {
        Log.add("Play audio: " + uid);
        audios.playAudio(uid);
    }

    public BattleMap getBattleMap(String uid) {
        for (int i = 0; i < battleMaps.size(); i++) if (battleMaps.get(i).uid.equals(uid)) return battleMaps.get(i);
        return null;
    }

    public NPC getNPC(String uid) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) return npcs.get(i);
        return null;
    }

    public int dropLootTable(String lootTableUID, String inventoryUID) {
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(lootTableUID)) return lootTable.get(i).drop(interpreter, inventoryUID);
        return 0;
    }

    public static Entity getEntity(String uid) {
        for (int i = 0; i < npcs.size(); i++) if (npcs.get(i).uid.equals(uid)) return npcs.get(i);
        for (int i = 0; i < inventories.size(); i++) if (inventories.get(i).uid.equals(uid)) return inventories.get(i);
        for (int i = 0; i < locations.size(); i++) if (locations.get(i).uid.equals(uid)) return locations.get(i);
        for (int i = 0; i < items.size(); i++) if (items.get(i).uid.equals(uid)) return items.get(i);
        for (int i = 0; i < inventories.size(); i++) if (inventories.get(i).uid.equals(uid)) return inventories.get(i);
        for (int i = 0; i < battleMaps.size(); i++) if (battleMaps.get(i).uid.equals(uid)) return battleMaps.get(i);
        for (int i = 0; i < events.size(); i++) if (events.get(i).uid.equals(uid)) return events.get(i);
        for (int i = 0; i < lootTable.size(); i++) if (lootTable.get(i).uid.equals(uid)) return lootTable.get(i);
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) return customCommands.get(i);
        for (int i = 0; i < colors.size(); i++) if (colors.get(i).uid.equals(uid)) return colors.get(i);
        for (int i = 0; i < fileObjects.size(); i++) if (fileObjects.get(i).uid.equals(uid)) return fileObjects.get(i);
        return null;
    }

    public ArrayList<Color> getAllColors() {
        ArrayList<Color> c = new ArrayList<Color>();
        for (ColorObject co : colors) c.add(co.color);
        return c;
    }

    public ArrayList<String> getAllColorNames() {
        ArrayList<String> c = new ArrayList<String>();
        for (ColorObject co : colors) c.add(co.name);
        return c;
    }

    public ArrayList<String> getAllColorUIDs() {
        ArrayList<String> c = new ArrayList<String>();
        for (ColorObject co : colors) c.add(co.uid);
        return c;
    }

    public static Color getColorByName(String name) {
        for (ColorObject co : colors) if (co.name.equals(name)) return co.color;
        return new Color(0, 0, 0);
    }

    public void endBattle() {
        interpreter.endBattle();
    }

    public void executeEventFromObject(String uid, String event, String args[]) {
        interpreter.executeEvent(uid, getEventCode(uid, event), args);
    }

    public void clearInventory(String uid) {
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) inventories.get(i).clearItems();
    }

    public Event getGeneralEventCollection() {
        return generalEventCollection;
    }
}