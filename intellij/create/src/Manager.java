
//RPG ENGINE BY YAN WITTMANN; http://yanwittmann.de/projects/rpgengine/site/

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Manager {
    public static String pathExtension = "../../", filename = "", version = "1.11.2";
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

    private boolean showLoadingScreen = true;
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
            Sleep.milliseconds(3500);
        }
        FileManager.clearTmp();
        cfg = new Configuration("res/config/main.cfg");
        StaticStuff.ee = cfg.get("ee");
        StaticStuff.setColorScheme(cfg.get("stylesheet"));
        guiHub = new GuiHub(this);
    }

    public void openFile(String filename) {
        openActionEditor = false;
        int result = 1;
        if (!Manager.filename.equals(""))
            result = Popup.selectButton(StaticStuff.projectName, "You have already opened a file.\nDo you want to save this first?", new String[]{"Yes!", "No"});
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
            result = Popup.selectButton(StaticStuff.projectName, "You have already opened a file.\nDo you want to save this first?", new String[]{"Yes!", "No"});
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

    public void createNew(String filename) { //UNUSED SINCE THERE IS A NEW 'CREATE NEW FILE' DIALOGUE
        openActionEditor = false;
        int result = 1;
        if (!Manager.filename.equals(""))
            result = Popup.selectButton(StaticStuff.projectName, "You have already opened a file.\nDo you want to save this first?", new String[]{"Yes!", "No"});
        if (result == 0) save();
        locations.clear();
        npcs.clear();
        items.clear();
        battleMaps.clear();
        talents.clear();
        generateTalents("RPG");
        events.clear();
        generateEvents();
        lootTable.clear();
        customCommands.clear();
        generateCommands();
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
            FileManager.writeToFile(pathExtension + "adventures/" + filename + "/project/player" + StaticStuff.dataFileEnding, player.generateSaveString());
            FileManager.writeToFile(pathExtension + "adventures/" + filename + "/project/settings" + StaticStuff.dataFileEnding, projectSettings.generateSaveString());
            for (Location location : locations)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/locations/" + location.uid + "" + StaticStuff.dataFileEnding, location.generateSaveString());
            for (NPC npc : npcs)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/npcs/" + npc.uid + "" + StaticStuff.dataFileEnding, npc.generateSaveString());
            for (Item item : items)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/itemtypes/" + item.uid + "" + StaticStuff.dataFileEnding, item.generateSaveString());
            for (Inventory inventory : inventories)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/inventories/" + inventory.uid + "" + StaticStuff.dataFileEnding, inventory.generateSaveString());
            for (BattleMap battleMap : battleMaps)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/battlemaps/" + battleMap.uid + "" + StaticStuff.dataFileEnding, battleMap.generateSaveString());
            for (Talent talent : talents)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/talents/" + talent.uid + "" + StaticStuff.dataFileEnding, talent.generateSaveString());
            for (Event event : events)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/events/" + event.uid + "" + StaticStuff.dataFileEnding, event.generateSaveString());
            for (LootTable table : lootTable)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/lootTable/" + table.uid + "" + StaticStuff.dataFileEnding, table.generateSaveString());
            for (CustomCommand customCommand : customCommands)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/customCommands/" + customCommand.uid + "" + StaticStuff.dataFileEnding, customCommand.generateSaveString());
            for (ColorObject color : colors)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/colors/" + color.uid + "" + StaticStuff.dataFileEnding, color.generateSaveString());
            for (CustomPopup popup : popups)
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/popups/" + popup.uid + "" + StaticStuff.dataFileEnding, popup.generateSaveString());
            for (FileObject fileObject : fileObjects) {
                FileManager.writeToFile(pathExtension + "adventures/" + filename + "/fileObjects/" + fileObject.uid + "" + StaticStuff.dataFileEnding, fileObject.generateSaveString());
                FileManager.writeFileFromByteArray(pathExtension + "adventures/" + filename + "/fileObjects/" + fileObject.uid + ".file", fileObject.getByteArray());
            }
            FileManager.writeToFile(pathExtension + "adventures/" + filename + "/variables/vars" + StaticStuff.dataFileEnding, variables.generateSaveString());
            FileManager.writeToFile(pathExtension + "adventures/" + filename + "/images/imagelist" + StaticStuff.dataFileEnding, images.generateSaveString());
            FileManager.writeToFile(pathExtension + "adventures/" + filename + "/audio/audiolist" + StaticStuff.dataFileEnding, audios.generateSaveString());
            FileManager.zipDirectory(pathExtension + "adventures/" + filename, pathExtension + "adventures/" + filename + "" + StaticStuff.adventureFileEnding);
            FileManager.deleteDirectoryRecursively(pathExtension + "adventures/" + filename);
            if (!projectSettings.getValue("password").equals("") && projectSettings.getValue("requirePasswordToPlay").equals("true")) {
                CryptoUtils.encrypt(CryptoUtils.prepareKey(projectSettings.getValue("password")), pathExtension + "adventures/" + filename + "" + StaticStuff.adventureFileEnding, pathExtension + "adventures/" + filename + "" + StaticStuff.adventureFileEnding);
            }
            Popup.message(StaticStuff.projectName, "Saved adventure under '" + filename + "'");
            unsavedChanges = false;
        } catch (Exception e) {
            Popup.error(StaticStuff.projectName, "Unable to save adventure.\n" + e);
        }
    }

    private void readFile() {
        if (filename.length() > 0) {
            if (!FileManager.fileExists(pathExtension + "adventures/" + filename + "" + StaticStuff.adventureFileEnding)) {
                exitError("File does not exist.", 99);
            }
            FileManager.deleteDirectoryRecursively(pathExtension + "adventures/" + filename);
            boolean isEncr = false;
            if (!FileManager.isArchive(pathExtension + "adventures/" + filename + "" + StaticStuff.adventureFileEnding)) {
                String key = String.valueOf(Popup.input("This project is either an encrypted adventure or a corrupted file.\nPlease enter the password:", "").hashCode());
                CryptoUtils.decrypt(CryptoUtils.prepareKey(key), pathExtension + "adventures/" + filename + "" + StaticStuff.adventureFileEnding, pathExtension + "adventures/" + filename + "_decr" + StaticStuff.adventureFileEnding);
                setFilename(filename + "_decr");
                isEncr = true;
            }
            if (filename.contains("_decr") && !FileManager.isArchive(pathExtension + "adventures/" + filename + "" + StaticStuff.adventureFileEnding)) {
                FileManager.delete(pathExtension + "adventures/" + filename + "" + StaticStuff.adventureFileEnding);
                Popup.error("Unpacking", "Unable to unpack adventure!\nThis could be because the password was wrong or because the file is corrupted.");
                return;
            }

            FileManager.unzip(pathExtension + "adventures/" + filename + "" + StaticStuff.adventureFileEnding, pathExtension + "adventures/");
            if (filename.contains("_decr")) {
                FileManager.delete(pathExtension + "adventures/" + filename + "" + StaticStuff.adventureFileEnding);
                setFilename(filename.replace("_decr", ""));
            }
            try {
                player = new PlayerSettings(FileManager.readFile(pathExtension + "adventures/" + filename + "/project/player" + StaticStuff.dataFileEnding));
            } catch (Exception e) {
                player = new PlayerSettings();
            }
            try {
                projectSettings = new ProjectSettings(FileManager.readFile(pathExtension + "adventures/" + filename + "/project/settings" + StaticStuff.dataFileEnding));
            } catch (Exception e) {
                projectSettings = new ProjectSettings();
            }
            if (!projectSettings.getValue("password").equals("") && !isEncr)
                if (!String.valueOf(Popup.input("This project is password-protected.\nPlease enter the password:", "").hashCode()).equals(projectSettings.getValue("password"))) {
                    exitError("Wrong password.", 98);
                }
            String[] files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/locations", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    locations.add(new Location(FileManager.readFile(pathExtension + "adventures/" + filename + "/locations/" + file)));
                } catch (Exception e) {
                    exitError("Location '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 100);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/npcs", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    npcs.add(new NPC(FileManager.readFile(pathExtension + "adventures/" + filename + "/npcs/" + file)));
                } catch (Exception e) {
                    exitError("NPC '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 101);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/itemtypes", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    items.add(new Item(FileManager.readFile(pathExtension + "adventures/" + filename + "/itemtypes/" + file)));
                } catch (Exception e) {
                    exitError("ItemType '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 102);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/inventories", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    inventories.add(new Inventory(FileManager.readFile(pathExtension + "adventures/" + filename + "/inventories/" + file)));
                } catch (Exception e) {
                    exitError("Inventory '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 104);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/battlemaps", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    battleMaps.add(new BattleMap(FileManager.readFile(pathExtension + "adventures/" + filename + "/battlemaps/" + file)));
                } catch (Exception e) {
                    exitError("BattleMap '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 105);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/talents", StaticStuff.dataFileEndingNoDot);
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
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/events", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    events.add(new Event(FileManager.readFile(pathExtension + "adventures/" + filename + "/events/" + file)));
                } catch (Exception e) {
                    exitError("Event '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 107);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/lootTable", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    lootTable.add(new LootTable(FileManager.readFile(pathExtension + "adventures/" + filename + "/lootTable/" + file)));
                } catch (Exception e) {
                    exitError("LootTable '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 107);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/customCommands", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    customCommands.add(new CustomCommand(FileManager.readFile(pathExtension + "adventures/" + filename + "/customCommands/" + file)));
                } catch (Exception e) {
                    exitError("CustomCommand '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 108);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/colors", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    colors.add(new ColorObject(FileManager.readFile(pathExtension + "adventures/" + filename + "/colors/" + file)));
                } catch (Exception e) {
                    exitError("Color '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/fileObjects", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    fileObjects.add(new FileObject(FileManager.readFile(pathExtension + "adventures/" + filename + "/fileObjects/" + file), FileManager.readFileToByteArray(pathExtension + "adventures/" + filename + "/fileObjects/" + file.replace(StaticStuff.dataFileEnding, "") + ".file")));
                } catch (Exception e) {
                    exitError("FileObject '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
            files = FileManager.getFilesWithEnding(pathExtension + "adventures/" + filename + "/popups", StaticStuff.dataFileEndingNoDot);
            for (String file : files) {
                try {
                    popups.add(new CustomPopup(FileManager.readFile(pathExtension + "adventures/" + filename + "/popups/" + file)));
                } catch (Exception e) {
                    exitError("Popup '" + file + "' contains invalid data.\nRPG Engine will exit:\n" + e, 109);
                }
            }
            variables = new Variables(FileManager.readFile(pathExtension + "adventures/" + filename + "/variables/vars" + StaticStuff.dataFileEnding));
            images = new Images(filename, FileManager.readFile(pathExtension + "adventures/" + filename + "/images/imagelist" + StaticStuff.dataFileEnding));
            audios = new Audios(filename, FileManager.readFile(pathExtension + "adventures/" + filename + "/audio/audiolist" + StaticStuff.dataFileEnding));
            FileManager.deleteDirectoryRecursively(pathExtension + "adventures/" + filename);
        }
        FileManager.deleteDirectoryRecursively(pathExtension + "adventures/" + filename);
        updateGui();
    }

    public void exitError(String message, int code) {
        Popup.error(StaticStuff.projectName, message);
        FileManager.deleteDirectoryRecursively(pathExtension + "adventures/" + filename);
        System.exit(code);
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
        if (!found) Popup.error(StaticStuff.projectName, "UID '" + uid + "' either does not exist or cannot be cloned");
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
        if (!found) Popup.error(StaticStuff.projectName, "UID '" + uid + "' not found");
        else unsavedChanges = true;
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

    /*public static boolean battleMapExists(String uid) {
        for (BattleMap battleMap : battleMaps)
            if (battleMap.uid.equals(uid))
                return true;
        return false;
    }

    public static boolean talentExists(String uid) {
        for (Talent talent : talents)
            if (talent.uid.equals(uid))
                return true;
        return false;
    }*/

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
                    Popup.error(StaticStuff.projectName, "This item does not have an image.");
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
        String[] talentList = FileManager.readFile("res/txt/talents" + which + projectSettings.getValue("language") + "" + StaticStuff.dataFileEnding);
        for (String s : talentList) talents.add(new Talent(true, s.split(" {2}")));
    }

    private void generateCommands() {
        String[] talentList = FileManager.readFile("res/txt/commands" + projectSettings.getValue("language") + "" + StaticStuff.dataFileEnding);
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

    public void setMainCfgSetting(String name, String value) {
        cfg.set(name, value);
    }

    public static void main(String[] args) {
        if (args.length > 0) System.out.println(Arrays.toString(args));
        new Manager();
    }
}