
//RPG ENGINE BY YAN WITTMANN; http://yanwittmann.de/projects/rpgengine/site/

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Manager {
    public static String pathExtension = "../../", filename = "", version = "1.13";
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
    public static ProjectSettings projectSettings;

    private final GuiHub guiHub;
    private GuiTip guiTip;

    private final static boolean showLoadingScreen = true;
    public boolean unsavedChanges = false;
    public static boolean openActionEditor = false;

    private static Manager myself;
    private final Configuration cfg;

    public Manager() {
        myself = this;
        if (showLoadingScreen) {
            new Thread(() -> {
                GuiLoading loading = new GuiLoading();
                Sleep.milliseconds(4500);
                loading.dispose();
            }).start();
        }
        FileManager.clearTmp();
        initTypeStrings();
        cfg = new Configuration("res/config/main.cfg");
        StaticStuff.ee = cfg.get("ee");
        StaticStuff.setColorScheme(cfg.get("stylesheet"));
        if (cfg.get("showTips").equals("true"))
            new Thread(() -> {
                guiTip = new GuiTip();
                Sleep.milliseconds(3500);
                guiTip.open();
            }).start();
        if (showLoadingScreen)
            Sleep.milliseconds(3500);
        guiHub = new GuiHub(this);
    }

    public void openFile(String filename) {
        openActionEditor = false;
        int result = 1;
        if (!Manager.filename.equals(""))
            result = Popup.selectButton(StaticStuff.PROJECT_NAME, "You have already opened a file.\nDo you want to save this first?", new String[]{"Yes!", "No"});
        if (result == 0) save();
        locations.clear();
        npcs.clear();
        items.clear();
        inventories.clear();
        battleMaps.clear();
        talents.clear();
        events.clear();
        lootTable.clear();
        customCommands.clear();
        colors.clear();
        fileObjects.clear();
        popups.clear();
        FileManager.deleteFilesInDirectory("res/txt/actioneditor");
        setFilename(filename);
        readFile();
        openActionEditor = true;
    }

    public void createNewWithSettings(String filename, String author, String lang, String talentsType) {
        openActionEditor = false;
        int result = 1;
        if (!Manager.filename.equals(""))
            result = Popup.selectButton(StaticStuff.PROJECT_NAME, "You have already opened a file.\nDo you want to save this first?", new String[]{"Yes!", "No"});
        if (result == 0) save();
        locations.clear();
        npcs.clear();
        items.clear();
        battleMaps.clear();
        events.clear();
        generateEvents();
        lootTable.clear();
        colors.clear();
        generateColors();
        fileObjects.clear();
        popups.clear();
        variables = new Variables();
        images = new Images(filename);
        audios = new Audios(filename);
        player = new PlayerSettings();
        inventories.clear();
        generateInventories();
        setFilename(filename);
        images.addImage("projectIcon", "res/img/iconyellow.png");
        projectSettings = new ProjectSettings();
        projectSettings.setOrAdd("name", filename);
        projectSettings.setOrAdd("author", author);
        projectSettings.setOrAdd("language", lang);
        talents.clear();
        generateTalents(talentsType);
        customCommands.clear();
        generateCommands();
        FileManager.deleteFilesInDirectory("res/txt/actioneditor");
        unsavedChanges = true;
        updateGui();
        openActionEditor = true;
    }

    public void setFilename(String filename) {
        Manager.filename = filename;
        try {
            images.setNamespace(filename);
            audios.setNamespace(filename);
        } catch (Exception ignored) {
        }
        guiHub.setFilename(filename);
    }

    public void save() {
        try {
            FileManager.makeDirectory(pathExtension + "adventures/" + filename);
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/project");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/locations");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/npcs");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/inventories");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/itemtypes");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/battlemaps");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/audio");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/images");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/variables");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/talents");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/events");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/lootTable");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/customCommands");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/colors");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/fileObjects");
            FileManager.makeDirectory(pathExtension + "adventures/" + filename + "/popups");
            FileManager.writeToFile(pathExtension + "adventures/" + filename + "/project/player" + StaticStuff.DATA_FILE_ENDING, player.generateSaveString());
            FileManager.writeToFile(pathExtension + "adventures/" + filename + "/project/settings" + StaticStuff.DATA_FILE_ENDING, projectSettings.generateSaveString());
            for (Location location : locations)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/locations/" + location.uid + "" + StaticStuff.DATA_FILE_ENDING, location.generateSaveString());
            for (NPC npc : npcs)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/npcs/" + npc.uid + "" + StaticStuff.DATA_FILE_ENDING, npc.generateSaveString());
            for (Item item : items)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/itemtypes/" + item.uid + "" + StaticStuff.DATA_FILE_ENDING, item.generateSaveString());
            for (Inventory inventory : inventories)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/inventories/" + inventory.uid + "" + StaticStuff.DATA_FILE_ENDING, inventory.generateSaveString());
            for (BattleMap battleMap : battleMaps)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/battlemaps/" + battleMap.uid + "" + StaticStuff.DATA_FILE_ENDING, battleMap.generateSaveString());
            for (Talent talent : talents)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/talents/" + talent.uid + "" + StaticStuff.DATA_FILE_ENDING, talent.generateSaveString());
            for (Event event : events)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/events/" + event.uid + "" + StaticStuff.DATA_FILE_ENDING, event.generateSaveString());
            for (LootTable table : lootTable)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/lootTable/" + table.uid + "" + StaticStuff.DATA_FILE_ENDING, table.generateSaveString());
            for (CustomCommand customCommand : customCommands)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/customCommands/" + customCommand.uid + "" + StaticStuff.DATA_FILE_ENDING, customCommand.generateSaveString());
            for (ColorObject color : colors)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/colors/" + color.uid + "" + StaticStuff.DATA_FILE_ENDING, color.generateSaveString());
            for (CustomPopup popup : popups)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/popups/" + popup.uid + "" + StaticStuff.DATA_FILE_ENDING, popup.generateSaveString());
            for (FileObject fileObject : fileObjects) {
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/fileObjects/" + fileObject.uid + "" + StaticStuff.DATA_FILE_ENDING, fileObject.generateSaveString());
                FileManager.writeFileFromByteArray(pathExtension + "adventures/" + filename + "/fileObjects/" + fileObject.uid + ".file", fileObject.getByteArray());
            }
            FileManager.writeToFile(pathExtension + "adventures/" + filename + "/variables/vars" + StaticStuff.DATA_FILE_ENDING, variables.generateSaveString());
            FileManager.writeToFile(pathExtension + "adventures/" + filename + "/images/imagelist" + StaticStuff.DATA_FILE_ENDING, images.generateSaveString());
            FileManager.writeToFile(pathExtension + "adventures/" + filename + "/audio/audiolist" + StaticStuff.DATA_FILE_ENDING, audios.generateSaveString());
            FileManager.zipDirectory(pathExtension + "adventures/" + filename, pathExtension + "adventures/" + filename + "" + StaticStuff.ADVENTURE_FILE_ENDING);
            FileManager.deleteDirectoryRecursively(pathExtension + "adventures/" + filename);
            if (!projectSettings.getValue("password").equals("") && projectSettings.getValue("requirePasswordToPlay").equals("true")) {
                CryptoUtils.encrypt(CryptoUtils.prepareKey(projectSettings.getValue("password")), pathExtension + "adventures/" + filename + "" + StaticStuff.ADVENTURE_FILE_ENDING, pathExtension + "adventures/" + filename + "" + StaticStuff.ADVENTURE_FILE_ENDING);
            }
            Popup.message(StaticStuff.PROJECT_NAME, "Saved adventure under '" + filename + "'");
            unsavedChanges = false;
        } catch (Exception e) {
            Popup.error(StaticStuff.PROJECT_NAME, "Unable to save adventure.\n" + e);
        }
    }

    private void readFile() {
        if (filename.length() > 0) {
            if (!FileManager.fileExists(pathExtension + "adventures/" + filename + "" + StaticStuff.ADVENTURE_FILE_ENDING)) {
                exitError("File does not exist.", 99);
            }
            FileManager.deleteDirectoryRecursively(pathExtension + "adventures/" + filename);
            boolean isEncr = false;
            if (!FileManager.isArchive(pathExtension + "adventures/" + filename + "" + StaticStuff.ADVENTURE_FILE_ENDING)) {
                String key = String.valueOf(Popup.input("This project is either an encrypted adventure or a corrupted file.\nPlease enter the password:", "").hashCode());
                CryptoUtils.decrypt(CryptoUtils.prepareKey(key), pathExtension + "adventures/" + filename + "" + StaticStuff.ADVENTURE_FILE_ENDING, pathExtension + "adventures/" + filename + "_decr" + StaticStuff.ADVENTURE_FILE_ENDING);
                setFilename(filename + "_decr");
                isEncr = true;
            }
            if (filename.contains("_decr") && !FileManager.isArchive(pathExtension + "adventures/" + filename + "" + StaticStuff.ADVENTURE_FILE_ENDING)) {
                FileManager.delete(pathExtension + "adventures/" + filename + "" + StaticStuff.ADVENTURE_FILE_ENDING);
                Popup.error("Unpacking", "Unable to unpack adventure!\nThis could be because the password was wrong or because the file is corrupted.");
                return;
            }

            FileManager.unzip(pathExtension + "adventures/" + filename + "" + StaticStuff.ADVENTURE_FILE_ENDING, pathExtension + "adventures/");
            if (filename.contains("_decr")) {
                FileManager.delete(pathExtension + "adventures/" + filename + "" + StaticStuff.ADVENTURE_FILE_ENDING);
                setFilename(filename.replace("_decr", ""));
            }
            try {
                player = new PlayerSettings(FileManager.readFile(pathExtension + "adventures/" + filename + "/project/player" + StaticStuff.DATA_FILE_ENDING));
            } catch (Exception e) {
                player = new PlayerSettings();
            }
            try {
                projectSettings = new ProjectSettings(FileManager.readFile(pathExtension + "adventures/" + filename + "/project/settings" + StaticStuff.DATA_FILE_ENDING));
            } catch (Exception e) {
                projectSettings = new ProjectSettings();
            }
            if (!projectSettings.getValue("password").equals("") && !isEncr)
                if (!String.valueOf(Popup.input("This project is password-protected.\nPlease enter the password:", "").hashCode()).equals(projectSettings.getValue("password"))) {
                    exitError("Wrong password.", 98);
                }
            String[] files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/locations", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    locations.add(new Location(FileManager.readFile(pathExtension + "adventures/" + filename + "/locations/" + file)));
                } catch (Exception e) {
                    exitError("Location '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 100);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/npcs", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    npcs.add(new NPC(FileManager.readFile(pathExtension + "adventures/" + filename + "/npcs/" + file)));
                } catch (Exception e) {
                    exitError("NPC '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 101);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/itemtypes", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    items.add(new Item(FileManager.readFile(pathExtension + "adventures/" + filename + "/itemtypes/" + file)));
                } catch (Exception e) {
                    exitError("ItemType '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 102);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/inventories", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    inventories.add(new Inventory(FileManager.readFile(pathExtension + "adventures/" + filename + "/inventories/" + file)));
                } catch (Exception e) {
                    exitError("Inventory '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 104);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/battlemaps", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    battleMaps.add(new BattleMap(FileManager.readFile(pathExtension + "adventures/" + filename + "/battlemaps/" + file)));
                } catch (Exception e) {
                    exitError("BattleMap '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 105);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/talents", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    talents.add(new Talent(FileManager.readFile(pathExtension + "adventures/" + filename + "/talents/" + file)));
                } catch (Exception e) {
                    exitError("Talent '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 106);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/talents", "RPGdata");
            for (String file : files) {
                try {
                    talents.add(new Talent(FileManager.readFile(pathExtension + "adventures/" + filename + "/talents/" + file)));
                } catch (Exception e) {
                    exitError("Talent '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 106);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/events", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    events.add(new Event(FileManager.readFile(pathExtension + "adventures/" + filename + "/events/" + file)));
                } catch (Exception e) {
                    exitError("Event '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 107);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/lootTable", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    lootTable.add(new LootTable(FileManager.readFile(pathExtension + "adventures/" + filename + "/lootTable/" + file)));
                } catch (Exception e) {
                    exitError("LootTable '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 107);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/customCommands", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    customCommands.add(new CustomCommand(FileManager.readFile(pathExtension + "adventures/" + filename + "/customCommands/" + file)));
                } catch (Exception e) {
                    exitError("CustomCommand '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 108);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/colors", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    colors.add(new ColorObject(FileManager.readFile(pathExtension + "adventures/" + filename + "/colors/" + file)));
                } catch (Exception e) {
                    exitError("Color '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/fileObjects", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    fileObjects.add(new FileObject(FileManager.readFile(pathExtension + "adventures/" + filename + "/fileObjects/" + file), FileManager.readFileToByteArray(pathExtension + "adventures/" + filename + "/fileObjects/" + file.replace(StaticStuff.DATA_FILE_ENDING, "") + ".file")));
                } catch (Exception e) {
                    exitError("FileObject '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/popups", StaticStuff.DATA_FILE_ENDING_NO_DOT);
            for (String file : files) {
                try {
                    popups.add(new CustomPopup(FileManager.readFile(pathExtension + "adventures/" + filename + "/popups/" + file)));
                } catch (Exception e) {
                    exitError("Popup '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
            variables = new Variables(FileManager.readFile(pathExtension + "adventures/" + filename + "/variables/vars" + StaticStuff.DATA_FILE_ENDING));
            images = new Images(filename, FileManager.readFile(pathExtension + "adventures/" + filename + "/images/imagelist" + StaticStuff.DATA_FILE_ENDING));
            audios = new Audios(filename, FileManager.readFile(pathExtension + "adventures/" + filename + "/audio/audiolist" + StaticStuff.DATA_FILE_ENDING));
            FileManager.deleteDirectoryRecursively(pathExtension + "adventures/" + filename);
        }
        FileManager.deleteDirectoryRecursively(pathExtension + "adventures/" + filename);
        updateGui();
    }

    public void exitError(String message, int code) {
        Popup.error(StaticStuff.PROJECT_NAME, message);
        FileManager.deleteDirectoryRecursively(pathExtension + "adventures/" + filename);
        System.exit(code);
    }

    public void autoRenameUIDs() {
        for (Location location : locations)
            refactor(location.uid, generateAutoRenameUID("loc", location.name, location.uid));
        for (NPC npc : npcs) refactor(npc.uid, generateAutoRenameUID("npc", npc.name, npc.uid));
        for (Item item : items) refactor(item.uid, generateAutoRenameUID("itm", item.name, item.uid));
        for (Inventory inventory : inventories)
            refactor(inventory.uid, generateAutoRenameUID("inv", inventory.name, inventory.uid));
        for (BattleMap battleMap : battleMaps)
            refactor(battleMap.uid, generateAutoRenameUID("btm", battleMap.name, battleMap.uid));
        for (Talent talent : talents) refactor(talent.uid, generateAutoRenameUID("tln", talent.name, talent.uid));
        for (Event event : events) refactor(event.uid, generateAutoRenameUID("evt", event.name, event.uid));
        for (LootTable table : lootTable) refactor(table.uid, generateAutoRenameUID("ltb", table.name, table.uid));
        for (CustomCommand customCommand : customCommands)
            refactor(customCommand.uid, generateAutoRenameUID("ccd", customCommand.name, customCommand.uid));
        for (ColorObject color : colors) refactor(color.uid, generateAutoRenameUID("col", color.name, color.uid));
        for (FileObject fileObject : fileObjects)
            refactor(fileObject.uid, generateAutoRenameUID("flo", fileObject.name, fileObject.uid));
        for (CustomPopup popup : popups) refactor(popup.uid, generateAutoRenameUID("pop", popup.name, popup.uid));
    }

    private String generateAutoRenameUID(String type, String name, String oldUID) {
        name = name.toLowerCase().replace(" ", "0").replaceAll("[^a-z\\d]", "");
        StringBuilder newUID = new StringBuilder();
        if (name.length() > 12) newUID.append(name, 0, 12);
        else newUID.append(name);
        while (newUID.length() < 13) {
            newUID.append("0");
        }
        newUID.append(type);
        String ret = newUID.toString().replaceAll("[^a-z\\d]", "");
        if (ret.equals(oldUID)) return oldUID;
        if (ret.length() == 16)
            if (!entityExists(ret))
                return ret;
            else {
                ret = ret.replaceAll("0", "1");
                if (ret.equals(oldUID)) return oldUID;
                if (!entityExists(ret))
                    return ret;
            }
        return oldUID;
    }

    public int refactor(String find, String replace) {
        int occ = 0;
        for (Location location : locations) occ += location.refactor(find, replace);
        for (NPC npc : npcs) occ += npc.refactor(find, replace);
        for (Item item : items) occ += item.refactor(find, replace);
        for (Inventory inventory : inventories) occ += inventory.refactor(find, replace);
        for (BattleMap battleMap : battleMaps) occ += battleMap.refactor(find, replace);
        for (Talent talent : talents) occ += talent.refactor(find, replace);
        for (Event event : events) occ += event.refactor(find, replace);
        for (LootTable table : lootTable) occ += table.refactor(find, replace);
        for (CustomCommand customCommand : customCommands) occ += customCommand.refactor(find, replace);
        for (ColorObject color : colors) occ += color.refactor(find, replace);
        for (FileObject fileObject : fileObjects) occ += fileObject.refactor(find, replace);
        for (CustomPopup popup : popups) occ += popup.refactor(find, replace);
        occ += player.refactor(find, replace);
        occ += projectSettings.refactor(find, replace);
        occ += variables.refactor(find, replace);
        occ += images.refactor(find, replace);
        occ += audios.refactor(find, replace);
        unsavedChanges = true;
        return occ;
    }

    public ArrayList<String> find(String find) {
        ArrayList<String> found = new ArrayList<>();
        for (Location location : locations) location.find(find, found);
        for (NPC npc : npcs) npc.find(find, found);
        for (Item item : items) item.find(find, found);
        for (Inventory inventory : inventories) inventory.find(find, found);
        for (BattleMap battleMap : battleMaps) battleMap.find(find, found);
        for (Event event : events) event.find(find, found);
        for (LootTable table : lootTable) table.find(find, found);
        for (CustomCommand customCommand : customCommands) customCommand.find(find, found);
        for (ColorObject color : colors) color.find(find, found);
        for (FileObject fileObject : fileObjects) fileObject.find(find, found);
        for (CustomPopup popup : popups) popup.find(find, found);
        player.find(find, found);
        projectSettings.find(find, found);
        variables.find(find, found);
        return found;
    }

    public void cloneEntity(String uid) {
        if (!openNextEntity) {
            openNextEntity = true;
            return;
        }
        if (!StaticStuff.isValidUID(uid))
            return;
        boolean found = false;
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid)) {
                found = true;
                locations.add(new Location(locations.get(i).generateSaveString().split("\n")));
                locations.get(locations.size() - 1).uid = UID.generateUID();
                uid = locations.get(locations.size() - 1).uid;
                break;
            }
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid)) {
                found = true;
                npcs.add(new NPC(npcs.get(i).generateSaveString().split("\n")));
                npcs.get(npcs.size() - 1).uid = UID.generateUID();
                uid = npcs.get(npcs.size() - 1).uid;
                break;
            }
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid)) {
                found = true;
                items.add(new Item(items.get(i).generateSaveString().split("\n")));
                items.get(items.size() - 1).uid = UID.generateUID();
                uid = items.get(items.size() - 1).uid;
                break;
            }
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) {
                found = true;
                inventories.add(new Inventory(inventories.get(i).generateSaveString().split("\n")));
                inventories.get(inventories.size() - 1).uid = UID.generateUID();
                uid = inventories.get(inventories.size() - 1).uid;
                break;
            }
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) {
                found = true;
                battleMaps.add(new BattleMap(battleMaps.get(i).generateSaveString().split("\n")));
                battleMaps.get(battleMaps.size() - 1).uid = UID.generateUID();
                uid = battleMaps.get(battleMaps.size() - 1).uid;
                break;
            }
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).uid.equals(uid)) {
                found = true;
                events.add(new Event(events.get(i).generateSaveString().split("\n")));
                events.get(events.size() - 1).uid = UID.generateUID();
                uid = events.get(events.size() - 1).uid;
                break;
            }
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid)) {
                found = true;
                lootTable.add(new LootTable(lootTable.get(i).generateSaveString().split("\n")));
                lootTable.get(lootTable.size() - 1).uid = UID.generateUID();
                uid = lootTable.get(lootTable.size() - 1).uid;
                break;
            }
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) {
                found = true;
                customCommands.add(new CustomCommand(customCommands.get(i).generateSaveString().split("\n")));
                customCommands.get(customCommands.size() - 1).uid = UID.generateUID();
                uid = customCommands.get(customCommands.size() - 1).uid;
                break;
            }
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).uid.equals(uid)) {
                found = true;
                colors.add(new ColorObject(colors.get(i).generateSaveString().split("\n")));
                colors.get(colors.size() - 1).uid = UID.generateUID();
                uid = colors.get(colors.size() - 1).uid;
                break;
            }
        if (!found)
            Popup.error(StaticStuff.PROJECT_NAME, "UID '" + uid + "' either does not exist or cannot be cloned");
        else {
            unsavedChanges = true;
            updateGui();
            openEntity(uid);
        }
    }

    public void openEntity(String uid) {
        if (!openNextEntity) {
            openNextEntity = true;
            return;
        }
        if (!StaticStuff.isValidUID(uid))
            return;
        boolean found = false;
        for (Location location : locations)
            if (location.uid.equals(uid)) {
                found = true;
                location.openEditor();
            }
        for (NPC npc : npcs)
            if (npc.uid.equals(uid)) {
                found = true;
                npc.openEditor();
            }
        for (Item item : items)
            if (item.uid.equals(uid)) {
                found = true;
                item.openEditor();
            }
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid)) {
                found = true;
                inventory.openEditor();
            }
        for (BattleMap battleMap : battleMaps)
            if (battleMap.uid.equals(uid)) {
                found = true;
                battleMap.openEditor();
            }
        for (Talent talent : talents)
            if (talent.uid.equals(uid)) {
                found = true;
                talent.open();
            }
        for (Event event : events)
            if (event.uid.equals(uid)) {
                found = true;
                event.openEditor();
            }
        for (LootTable table : lootTable)
            if (table.uid.equals(uid)) {
                found = true;
                table.openEditor();
            }
        for (CustomCommand customCommand : customCommands)
            if (customCommand.uid.equals(uid)) {
                found = true;
                customCommand.openEditor();
            }
        for (ColorObject color : colors)
            if (color.uid.equals(uid)) {
                found = true;
                color.openEditor();
            }
        for (FileObject fileObject : fileObjects)
            if (fileObject.uid.equals(uid)) {
                found = true;
                fileObject.openEditor();
            }
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid)) {
                found = true;
                popup.openEditor();
            }
        if (variables.openVariable(uid)) found = true;
        if (images.openImage(uid)) found = true;
        if (audios.playAudio(uid)) found = true;
        if (!found) Popup.error(StaticStuff.PROJECT_NAME, "UID '" + uid + "' not found");
        else unsavedChanges = true;
    }

    public void openEntityEvent(String uid, int index) {
        if (!openNextEntity) {
            openNextEntity = true;
            return;
        }
        if (!StaticStuff.isValidUID(uid))
            return;
        boolean found = false;
        for (Location location : locations)
            if (location.uid.equals(uid)) {
                found = true;
                location.openEvent(index);
            }
        for (NPC npc : npcs)
            if (npc.uid.equals(uid)) {
                found = true;
                npc.openEvent(index);
            }
        for (Item item : items)
            if (item.uid.equals(uid)) {
                found = true;
                item.openEvent(index);
            }
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid)) {
                found = true;
                inventory.openEvent(index);
            }
        for (BattleMap battleMap : battleMaps)
            if (battleMap.uid.equals(uid)) {
                found = true;
                battleMap.openEvent(index);
            }
        for (Event event : events)
            if (event.uid.equals(uid)) {
                found = true;
                event.openEvent(index);
            }
        for (LootTable table : lootTable)
            if (table.uid.equals(uid)) {
                found = true;
                table.openEvent(index);
            }
        for (CustomCommand customCommand : customCommands)
            if (customCommand.uid.equals(uid)) {
                found = true;
                customCommand.openEvent(index);
            }
        for (ColorObject color : colors)
            if (color.uid.equals(uid)) {
                found = true;
                color.openEvent(index);
            }
        for (FileObject fileObject : fileObjects)
            if (fileObject.uid.equals(uid)) {
                found = true;
                fileObject.openEvent(index);
            }
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid)) {
                found = true;
                popup.openEvent(index);
            }
        if (!found) Popup.error(StaticStuff.PROJECT_NAME, "UID '" + uid + "' not found");
    }

    public boolean entityExists(String uid) {
        if (!StaticStuff.isValidUID(uid))
            return false;
        for (Location location : locations)
            if (location.uid.equals(uid)) return true;
        for (NPC npc : npcs)
            if (npc.uid.equals(uid)) return true;
        for (Item item : items)
            if (item.uid.equals(uid)) return true;
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid)) return true;
        for (BattleMap battleMap : battleMaps)
            if (battleMap.uid.equals(uid)) return true;
        for (Talent talent : talents)
            if (talent.uid.equals(uid)) return true;
        for (Event event : events)
            if (event.uid.equals(uid)) return true;
        for (LootTable table : lootTable)
            if (table.uid.equals(uid)) return true;
        for (CustomCommand customCommand : customCommands)
            if (customCommand.uid.equals(uid)) return true;
        for (ColorObject color : colors)
            if (color.uid.equals(uid)) return true;
        for (FileObject fileObject : fileObjects)
            if (fileObject.uid.equals(uid)) return true;
        for (CustomPopup popup : popups)
            if (popup.uid.equals(uid)) return true;
        if (variables.variableExists(uid)) return true;
        if (images.imageExists(uid)) return true;
        return audios.audioExists(uid);
    }

    private boolean openNextEntity = true;

    public void newEntity(String type) {
        unsavedChanges = true;
        openActionEditor = false;
        openNextEntity = true;
        switch (type) {
            case "Location" -> locations.add(new Location());
            case "NPC" -> npcs.add(new NPC());
            case "Variable" -> variables.addVariable("Name", "String", "Value");
            case "Image" -> {
                openNextEntity = images.addImage(Popup.input("Image name", ""), FileManager.filePicker());
                openNextEntity = false;
            }
            case "Audio" -> {
                openNextEntity = audios.addAudio(Popup.input("Audio name", ""), FileManager.filePicker());
                openNextEntity = false;
            }
            case "Item Type" -> items.add(new Item());
            case "Inventory" -> inventories.add(new Inventory());
            case "Battle Map" -> battleMaps.add(new BattleMap());
            case "Talent" -> talents.add(new Talent());
            case "EventCollection" -> events.add(new Event());
            case "CustomCommand" -> customCommands.add(new CustomCommand());
            case "Color" -> colors.add(new ColorObject());
            case "Popup" -> popups.add(new CustomPopup());
            case "File" -> {
                String name = FileManager.filePicker();
                if (name == null) return;
                if (!FileManager.fileExists(name)) {
                    Popup.error("File", "This file does not exist.");
                    return;
                }
                fileObjects.add(new FileObject(FileManager.getFilename(name), FileManager.readFileToByteArray(name)));
            }
            case "Loot Table" -> lootTable.add(new LootTable());
        }
        openEntity(StaticStuff.getLastCreatedUID());
        openActionEditor = true;
    }

    public void newImage(String path, String name) {
        images.addImage(name, path);
    }

    public void newAudio(String path, String name) {
        audios.addAudio(name, path);
    }

    public void deleteEntity(String uid) {
        if (!StaticStuff.isValidUID(uid))
            return;
        unsavedChanges = true;
        if (!deleteLocation(uid))
            if (!deleteNPC(uid))
                if (!deleteInventory(uid))
                    if (!deleteItemType(uid))
                        if (!deleteBattleMap(uid))
                            if (!deleteTalent(uid))
                                if (!deleteEventCollection(uid))
                                    if (!deleteCustomCommand(uid))
                                        if (!deleteColor(uid))
                                            if (!deleteFileObject(uid))
                                                if (!deleteLootTable(uid))
                                                    if (!deleteCustomPopup(uid))
                                                        if (!images.deleteImage(uid))
                                                            if (!audios.deleteAudio(uid)) {
                                                                variables.deleteVariable(uid);
                                                            }
        updateGui();
    }

    private boolean deleteLocation(String uid) {
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).uid.equals(uid)) {
                locations.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteColor(String uid) {
        for (int i = 0; i < colors.size(); i++)
            if (colors.get(i).uid.equals(uid)) {
                colors.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteFileObject(String uid) {
        for (int i = 0; i < fileObjects.size(); i++)
            if (fileObjects.get(i).uid.equals(uid)) {
                fileObjects.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteCustomPopup(String uid) {
        for (int i = 0; i < popups.size(); i++)
            if (popups.get(i).uid.equals(uid)) {
                popups.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteLootTable(String uid) {
        for (int i = 0; i < lootTable.size(); i++)
            if (lootTable.get(i).uid.equals(uid)) {
                lootTable.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteCustomCommand(String uid) {
        for (int i = 0; i < customCommands.size(); i++)
            if (customCommands.get(i).uid.equals(uid)) {
                customCommands.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteEventCollection(String uid) {
        for (int i = 0; i < events.size(); i++)
            if (events.get(i).uid.equals(uid)) {
                events.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteTalent(String uid) {
        for (int i = 0; i < talents.size(); i++)
            if (talents.get(i).uid.equals(uid)) {
                talents.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteBattleMap(String uid) {
        for (int i = 0; i < battleMaps.size(); i++)
            if (battleMaps.get(i).uid.equals(uid)) {
                battleMaps.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteItemType(String uid) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).uid.equals(uid)) {
                items.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteInventory(String uid) {
        for (int i = 0; i < inventories.size(); i++)
            if (inventories.get(i).uid.equals(uid)) {
                inventories.remove(i);
                return true;
            }
        return false;
    }

    private boolean deleteNPC(String uid) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).uid.equals(uid)) {
                npcs.remove(i);
                return true;
            }
        return false;
    }

    public static boolean itemTypeExists(String uid) {
        for (Item item : items)
            if (item.uid.equals(uid))
                return true;
        return false;
    }

    public static boolean locationExists(String uid) {
        for (Location location : locations)
            if (location.uid.equals(uid))
                return true;
        return false;
    }

    public static boolean inventoryDoesNotExist(String uid) {
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid))
                return false;
        return true;
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

    public static boolean variableExists(String uidOrName) {
        return variables.variableExists(uidOrName);
    }

    public static String getName(String uid) {
        for (NPC npc : npcs) if (npc.uid.equals(uid)) return npc.name;
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return inventory.name;
        for (Location location : locations) if (location.uid.equals(uid)) return location.name;
        for (Item item : items) if (item.uid.equals(uid)) return item.name;
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

    public String getTypeByUID(String uid) {
        for (NPC npc : npcs) if (npc.uid.equals(uid)) return "npc";
        for (Inventory inventory : inventories) if (inventory.uid.equals(uid)) return "inventory";
        for (Location location : locations) if (location.uid.equals(uid)) return "location";
        for (Item item : items) if (item.uid.equals(uid)) return "item";
        for (Talent talent : talents) if (talent.uid.equals(uid)) return "talent";
        for (BattleMap battleMap : battleMaps) if (battleMap.uid.equals(uid)) return "battleMap";
        for (Event event : events) if (event.uid.equals(uid)) return "eventCollection";
        for (LootTable table : lootTable) if (table.uid.equals(uid)) return "lootTable";
        for (CustomCommand customCommand : customCommands) if (customCommand.uid.equals(uid)) return "customCommand";
        for (ColorObject color : colors) if (color.uid.equals(uid)) return "color";
        for (FileObject fileObject : fileObjects) if (fileObject.uid.equals(uid)) return "fileObject";
        for (CustomPopup popup : popups) if (popup.uid.equals(uid)) return "popup";
        if (audioExists(uid)) return "audio";
        if (imageExists(uid)) return "image";
        if (variableExists(uid)) return "variable";
        return "null";
    }

    public static String getAudioName(String uid) {
        if (audioExists(uid)) return audios.getAudioName(uid);
        return "This image does not exist";
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
                if (item.image.equals(""))
                    Popup.error(StaticStuff.PROJECT_NAME, "This item does not have an image.");
                else return item.image;
        return "item does not exist";
    }

    public static String getLocationName(String uid) {
        for (Location location : locations)
            if (location.uid.equals(uid))
                return location.name;
        return "location does not exist";
    }

    public static String getInventoryName(String uid) {
        for (Inventory inventory : inventories)
            if (inventory.uid.equals(uid))
                return inventory.name;
        return "inventory does not exist";
    }

    /*public static String getBattleMapName(String uid) {
        for (BattleMap battleMap : battleMaps)
            if (battleMap.uid.equals(uid))
                return battleMap.name;
        return "battle map does not exist";
    }*/

    public static String[] getStringArrayImages() {
        String[] retVal = new String[Images.getAmount()];
        for (int i = 0; i < Images.getAmount(); i++)
            retVal[i] = Images.getName(i) + " - " + Images.getUID(i);
        return retVal;
    }

    public static String[] getStringArrayNPCs() {
        String[] retVal = new String[npcs.size()];
        for (int i = 0; i < npcs.size(); i++)
            retVal[i] = npcs.get(i).getName() + " - " + npcs.get(i).getUID();
        return retVal;
    }

    public static String[] getStringArrayItems() {
        String[] retVal = new String[items.size()];
        for (int i = 0; i < items.size(); i++)
            retVal[i] = items.get(i).getName() + " - " + items.get(i).getUID();
        return retVal;
    }

    public static String[] getStringArrayLocations() {
        String[] retVal = new String[locations.size()];
        for (int i = 0; i < locations.size(); i++)
            retVal[i] = locations.get(i).getName() + " - " + locations.get(i).getUID();
        return retVal;
    }

    public static String[] getStringArrayInventories() {
        String[] retVal = new String[inventories.size()];
        for (int i = 0; i < inventories.size(); i++)
            retVal[i] = inventories.get(i).getName() + " - " + inventories.get(i).getUID();
        return retVal;
    }

    public void updateGui() {
        if (filename == null) return;
        if (filename.equals("")) return;
        StringBuilder location;
        new StringBuilder();
        StringBuilder npc;
        StringBuilder itemType;
        String variable;
        String image;
        StringBuilder inventory;
        String audio;
        new StringBuilder();
        StringBuilder battleMap;
        StringBuilder talent;
        StringBuilder event;
        StringBuilder customCommand;
        StringBuilder lootTable;
        StringBuilder color;
        StringBuilder fileObject;
        StringBuilder popup;
        location = new StringBuilder(locations.size() + " location(s):");
        for (Location value : locations) location.append("\n").append(value.name).append(" --- ").append(value.uid);
        npc = new StringBuilder(npcs.size() + " npc(s):");
        for (NPC value : npcs) npc.append("\n").append(value.name).append(" --- ").append(value.uid);
        itemType = new StringBuilder(items.size() + " item type(s):");
        for (Item item : items) itemType.append("\n").append(item.name).append(" --- ").append(item.uid);
        inventory = new StringBuilder(inventories.size() + " inventories:");
        for (Inventory value : inventories) inventory.append("\n").append(value.name).append(" --- ").append(value.uid);
        battleMap = new StringBuilder(battleMaps.size() + " battleMap(s):");
        for (BattleMap map : battleMaps) battleMap.append("\n").append(map.name).append(" --- ").append(map.uid);
        talent = new StringBuilder(talents.size() + " talent(s):");
        for (Talent value : talents) talent.append("\n").append(value.generateInformation());
        event = new StringBuilder(events.size() + " eventCollection(s):");
        for (Event value : events) event.append("\n").append(value.name).append(" --- ").append(value.uid);
        lootTable = new StringBuilder(Manager.lootTable.size() + " loot table(s):");
        for (LootTable table : Manager.lootTable)
            lootTable.append("\n").append(table.name).append(" --- ").append(table.uid);
        customCommand = new StringBuilder(customCommands.size() + " customCommand(s):");
        for (CustomCommand command : customCommands)
            customCommand.append("\n").append(command.name).append(" --- ").append(command.uid);
        color = new StringBuilder(colors.size() + " color(s):");
        for (ColorObject colorObject : colors)
            color.append("\n").append(colorObject.name).append(" --- ").append(colorObject.uid);
        fileObject = new StringBuilder(fileObjects.size() + " file(s):");
        for (FileObject object : fileObjects)
            fileObject.append("\n").append(object.name).append(" --- ").append(object.uid);
        popup = new StringBuilder(popups.size() + " popup(s):");
        for (CustomPopup object : popups) popup.append("\n").append(object.name).append(" --- ").append(object.uid);
        variable = variables.generateMenuString();
        image = images.generateMenuString();
        audio = audios.generateMenuString();
        guiHub.updateTextAreas(new String[]{location.toString(), npc.toString(), itemType.toString(), inventory.toString(), image, audio, battleMap.toString(), talent.toString(), lootTable.toString(), variable, event.toString(), customCommand.toString(), color.toString(), fileObject.toString(), popup.toString()});
    }

    private void generateTalents(String which) {
        if (which.equals("Empty")) return;
        String[] talentList = FileManager.readFile("res/txt/talents" + which + projectSettings.getValue("language") + "" + StaticStuff.DATA_FILE_ENDING);
        for (String s : talentList) talents.add(new Talent(true, s.split(" {2}")));
    }

    private void generateCommands() {
        String[] talentList = FileManager.readFile("res/txt/commands" + projectSettings.getValue("language") + "" + StaticStuff.DATA_FILE_ENDING);
        String[] current;
        for (String s : talentList) {
            current = s.split(";;;");
            customCommands.add(new CustomCommand(current[0], current[1], current[2].replace("LINEBREAK", "\n"), current[3], current[4]));
        }
    }

    private void generateEvents() {
        events.add(new Event("General", "Events that get triggered automatically at specific events in the adventure."));
        events.get(0).addEvent("launch");
        events.get(0).addEvent("intro");
        events.get(0).addEvent("introOver");
        events.get(0).addEvent("exit");
        events.get(0).addEvent("audioStart");
        events.get(0).addEvent("audioStop");
        events.get(0).addEvent("audioEnd");
        events.get(0).addEvent("showAvailableCommands");
    }

    private void generateInventories() {
        Inventory playerInventory = new Inventory(true);
        inventories.add(playerInventory);
        player.setValue("inventory", playerInventory.uid);
    }

    private void generateColors() { // colors.add(new ColorObject("","",0,0,0));
        colors.add(new ColorObject("background", "The normal black background of the frames", 0, 0, 0));
        colors.add(new ColorObject("white_border", "The white border around frames", 255, 255, 255));
        colors.add(new ColorObject("def_text_color_main", "Default text color for non-buttons", 255, 255, 255));
        colors.add(new ColorObject("def_text_color_buttons", "Default text color for buttons", 0, 0, 0));
        colors.add(new ColorObject("purple", "Used as text color", 186, 57, 237));
        colors.add(new ColorObject("pink", "Used as text color", 235, 26, 99));
        colors.add(new ColorObject("red", "Used as text color", 214, 54, 54));
        colors.add(new ColorObject("dark_red", "Used as text color", 140, 34, 34));
        colors.add(new ColorObject("aqua", "Used as text color", 36, 240, 226));
        colors.add(new ColorObject("blue", "Used as text color", 52, 162, 217));
        colors.add(new ColorObject("dark_blue", "Used as text color", 36, 98, 156));
        colors.add(new ColorObject("green", "Used as text color", 104, 222, 53));
        colors.add(new ColorObject("dark_green", "Used as text color", 67, 143, 66));
        colors.add(new ColorObject("yellow", "Used as text color", 255, 240, 31));
        colors.add(new ColorObject("orange", "Used as text color", 245, 180, 17));
        colors.add(new ColorObject("gold", "Used as text color", 245, 202, 10));
        colors.add(new ColorObject("white", "Used as text color", 250, 250, 250));
        colors.add(new ColorObject("gray", "Used as text color", 181, 181, 181));
        colors.add(new ColorObject("black", "Used as text color", 33, 33, 33));
    }

    public static boolean isActionEditorOpenDirectlyInExternalEditor() {
        myself.cfg.refresh();
        return myself.cfg.get("actionEditorOpenDirectlyInExternalEditor").equals("true");
        //return projectSettings.getValue("actionEditorOpenDirectlyInExternalEditor").equals("true");
    }

    public static void toggleActionEditor() {
        myself.cfg.refresh();
        myself.cfg.set("actionEditorOpenDirectlyInExternalEditor", !myself.cfg.get("actionEditorOpenDirectlyInExternalEditor").equals("true") + "");
    }

    public static void toggleShowTips() {
        myself.cfg.refresh();
        myself.cfg.set("showTips", !myself.cfg.get("showTips").equals("true") + "");
    }

    public void setMainCfgSetting(String name, String value) {
        cfg.set(name, value);
    }

    private final static ArrayList<String> OBJECT_TYPES = new ArrayList<>();

    private void initTypeStrings() {
        OBJECT_TYPES.add("npc");
        OBJECT_TYPES.add("inventory");
        OBJECT_TYPES.add("location");
        OBJECT_TYPES.add("item");
        OBJECT_TYPES.add("talent");
        OBJECT_TYPES.add("battleMap");
        OBJECT_TYPES.add("eventCollection");
        OBJECT_TYPES.add("lootTable");
        OBJECT_TYPES.add("customCommand");
        OBJECT_TYPES.add("color");
        OBJECT_TYPES.add("fileObject");
        OBJECT_TYPES.add("popup");
        OBJECT_TYPES.add("audio");
        OBJECT_TYPES.add("image");
        OBJECT_TYPES.add("variable");
        OBJECT_TYPES.add("null");
    }

    private ArrayList<String> getAllUIDs() {
        ArrayList<String> uids = new ArrayList<>();
        for (Location location : locations)
            uids.add(location.uid);
        for (NPC npc : npcs)
            uids.add(npc.uid);
        for (Item item : items)
            uids.add(item.uid);
        for (Inventory inventory : inventories)
            uids.add(inventory.uid);
        for (BattleMap battleMap : battleMaps)
            uids.add(battleMap.uid);
        for (Talent talent : talents)
            uids.add(talent.uid);
        for (Event event : events)
            uids.add(event.uid);
        for (LootTable table : lootTable)
            uids.add(table.uid);
        for (CustomCommand customCommand : customCommands)
            uids.add(customCommand.uid);
        for (ColorObject color : colors)
            uids.add(color.uid);
        for (FileObject fileObject : fileObjects)
            uids.add(fileObject.uid);
        for (CustomPopup popup : popups)
            uids.add(popup.uid);
        uids.addAll(variables.getUids());
        uids.addAll(Images.getUids());
        uids.addAll(audios.getUids());
        return uids;
    }

    public void generateVis(String generateType) {
        VisJS vis = new VisJS(generateType, 1700, 900);

        for (int i = 0; i < OBJECT_TYPES.size(); i++)
            vis.addNodeType(i, new VisJS.NodeTypeBuilder().setColor(StaticStuff.generateColor(OBJECT_TYPES.get(i))));
        vis.setOption(VisJS.EDGE_ARROW_DIRECTION, VisJS.EDGE_ARROW_TO);
        vis.setOption(VisJS.PHYSICS_SOLVER, VisJS.PHYSICS_SOLVER_FORCE_ATLAS_2_BASED);
        vis.setOption(VisJS.PHYSICS_SOLVER_REPULSION, 300);

        switch (generateType) {
            case "uidNameConnections" -> getAllUIDs().forEach(uid -> addEntityConnections(uid, vis));
            case "uidNameConnectionsByType" -> {
                String type = Popup.dropDown(StaticStuff.PROJECT_NAME, "Select the type to display the connections of:", OBJECT_TYPES.toArray(new String[0]));
                if (type == null) return;
                getAllUIDs().stream().filter(uid -> getTypeByUID(uid).equals(type)).forEach(uid -> addEntityConnections(uid, vis));
            }
            case "objectsByTypes" -> {
                OBJECT_TYPES.forEach(objectType -> vis.addNode(objectType, OBJECT_TYPES.indexOf(objectType)));
                getAllUIDs().forEach(uid -> {
                    String type = getTypeByUID(uid);
                    String name = getName(uid);
                    vis.addNode(uid + "\\n" + name + "\\n" + type, OBJECT_TYPES.indexOf(type));
                    vis.addEdge(type, uid + "\\n" + name + "\\n" + type);
                });
            }
            default -> {
                Popup.error(StaticStuff.PROJECT_NAME, "This type does not exist");
                return;
            }
        }

        if (vis.getNodeCount() == 0) {
            Popup.warning(StaticStuff.PROJECT_NAME, "Generated graph has no nodes.\nCancelling action.");
            return;
        }

        String filename = generateType + "_" + vis.getNodeCount() + "_nodes.html";
        FileManager.writeToFile(filename, vis.generate().toArray(new String[0]));
        FileManager.openFile(filename);
    }

    private void addEntityConnections(String uid, VisJS vis) {
        String type = getTypeByUID(uid);
        String name = getName(uid);

        ArrayList<String> connections = find(uid);
        connections.addAll(find(name));

        String displayText = uid + "\\n" + name + "\\n" + type;
        vis.addNode(displayText, OBJECT_TYPES.indexOf(type));

        for (String connection : connections) {
            if (connection.length() >= 16) {
                String connectUID = connection.substring(0, 16);
                if (StaticStuff.isValidUIDSilent(connectUID)) {
                    String connectType = getTypeByUID(connectUID);
                    connectUID = connectUID + "\\n" + getName(connectUID) + "\\n" + getTypeByUID(connectUID);
                    if (connectUID.equals(displayText)) continue;
                    vis.addNode(connectUID, OBJECT_TYPES.indexOf(connectType));
                    vis.addEdge(displayText, connectUID, connection, true);
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) System.out.println(Arrays.toString(args));
        new Manager();
    }
}