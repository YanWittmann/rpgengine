
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
    private static Interpreter self;
    private GuiMainConsole console;
    private Manager manager;
    private PlayerSettings player;
    private ProjectSettings settings;
    private String filename = "";
    private String extraFilePath = "../../";
    private final String version = "1.11.1";
    private boolean autoRoll = false, showLoadingScreen = true, loadingScreenDone = false, showIntro = true, mayOpenStartPopup = false;
    private static Language lang;
    //public Configuration cfg;
    public int amountPopupsOpen = 0;
    public GuiPlayerStats playerStatus;
    final GuiLoading loading = new GuiLoading();

    public Interpreter() {
        self = this;
        new Thread(() -> {
            loading.setVisible(true);
            Sleep.milliseconds(4500);
            mayOpenStartPopup = true;
            while (!loadingScreenDone) Sleep.milliseconds(300);
            loadingScreenDone = false;
            loading.setVisible(false);
        }).start();
    }

    private void setup() {
        StaticStuff.prepare(this);
        setSizeScale(Double.parseDouble(StaticStuff.getScreenWidth() + "") * 0.0520833333333333d);
        if (selectedArgsLang == null)
            lang = new Language("res/lang", true);
        else
            lang = new Language(selectedArgsLang, "res/lang");
        //cfg = new Configuration("res/config/main.cfg");
        if (argsFilename.equals("")) {
            String[] files = FileManager.getFilesWithEnding(extraFilePath + "adventures/", StaticStuff.adventureFileEndingNoDot);
            if (files.length == 0) {
                StaticStuff.error(lang("errorNoAdventuresFound"));
                System.exit(100);
            }
            loadingScreenDone = true;
            while (!mayOpenStartPopup) Sleep.milliseconds(300);
            filename = StaticStuff.openPopup(StaticStuff.prepareString(lang("popupChooseAdventure")), StaticStuff.replaceAllLines(files, StaticStuff.adventureFileEnding, ""), files[0]);
        } else {
            filename = argsFilename;
            loadingScreenDone = true;
            loading.setVisible(false);
        }
        filename = letUserPickSavestateIfAvailable(filename);
        manager = new Manager(this, filename, extraFilePath);
        StaticStuff.prepareColors(manager.getAllColors(), manager.getAllColorNames(), manager.getAllColorUIDs());
        settings = Manager.project;
        if (forceDebugMode && settings.getValue("debugModeForceable").equals("true"))
            settings.setValue("debugMode", "true");
        player = Manager.player;
        while (!checkForPermissions(settings.getValue("permissions")))
            if (StaticStuff.openPopup(lang("permissionRequestRetry"), new String[]{lang("permissionRequestRetryNotExit"), lang("permissionRequestRetryExit")}) == 1)
                System.exit(400);
        if (!userPickedSavestate)
            executeEventFromObject(manager.getGeneralEventCollection().getUID(), "launch", new String[]{});
        showIntro = Manager.project.getValue("showIntro").equals("true");
        if (showIntro && !userPickedSavestate) create = new GuiCharacterCreation(this);
        else finishSetup();
    }

    private GuiCharacterCreation create;

    public void finishSetup() {
        new Thread(() -> {
            loading.setVisible(true);
            while (!loadingScreenDone) Sleep.milliseconds(300);
            loadingScreenDone = false;
            loading.dispose();
        }).start();
        console = new GuiMainConsole(this);
        customCommands = manager.getAllCC();
        Sleep.milliseconds(200);
        loadingScreenDone = true;
        player.setValue("holdingMain", "");
        player.setValue("holdingSecond", "");
        player.setValue("holdingArmor", "");
        if (create != null) {
            player.setValue("courage", create.rollAttributeValue[0]);
            player.setValue("wisdom", create.rollAttributeValue[1]);
            player.setValue("intuition", create.rollAttributeValue[2]);
            player.setValue("charisma", create.rollAttributeValue[3]);
            player.setValue("dexterity", create.rollAttributeValue[4]);
            player.setValue("agility", create.rollAttributeValue[5]);
            player.setValue("strength", create.rollAttributeValue[6]);
            player.setValue("maxLoad", create.rollAttributeValue[6] * 50);
            player.setValue("health", create.rollAttributeValue[5] + Integer.parseInt(player.getValue("health")));
            player.addIfNotContain("maxHealth", player.getValue("health"));
            if (create.selectedClassID == 0) {
                player.setValue("class", "elf");
                player.setValue("speed", 3);
                player.setValue("dmgNoWeapon", "1W2 - 1");
            }
            if (create.selectedClassID == 1) {
                player.setValue("class", "warrior");
                player.setValue("speed", 3);
                player.setValue("dmgNoWeapon", "1W3 - 1");
            }
            if (create.selectedClassID == 2) {
                player.setValue("class", "mage");
                player.setValue("speed", 2);
                player.setValue("dmgNoWeapon", "1W2 - 1");
            }
            if (create.selectedClassID == 3) {
                player.setValue("class", "novadi");
                player.setValue("speed", 2);
                player.setValue("dmgNoWeapon", "1W2 - 1");
            }
            if (create.selectedClassID == 4) {
                player.setValue("class", "stray");
                player.setValue("speed", 2);
                player.setValue("dmgNoWeapon", "1W2 - 1");
            }
            if (create.selectedClassID == 5) {
                player.setValue("class", "thorwaler");
                player.setValue("speed", 2);
                player.setValue("dmgNoWeapon", "1W3 - 1");
            }
            if (create.selectedClassID == 6) {
                player.setValue("class", "dwarf");
                player.setValue("speed", 1);
                player.setValue("dmgNoWeapon", "1W3 - 1");
            }
            player.setupTalents(settings.getValue("language"));
            System.out.println(player.getValue("name"));
            if (player.getValue("name").length() == 0) {
                String name;
                do name = StaticStuff.openPopup(lang("chCreationEnterName"), "");
                while (name.equals("") || name.length() > 12);
                player.setValue("name", name);
            }
        } else {
            player.setValue("class", "elf");
            player.setValue("speed", 3);
            player.setValue("health", 20 + Integer.parseInt(player.getValue("health")));
            player.addIfNotContain("maxHealth", player.getValue("health"));
            player.setValue("dmgNoWeapon", "1W3 - 1");
            player.setupTalents(settings.getValue("language"));
            if (player.getValue("name").length() == 0)
                player.setValue("name", "Yan");
            player.setValue("maxLoad", "1000");
        }
        playerStatus = new GuiPlayerStats(self, player, manager);
        activateLog();
        if (showIntro && !userPickedSavestate) {
            executeEventFromObject(manager.getGeneralEventCollection().getUID(), "intro", new String[]{});
            new GuiIntro(this, player, settings, version, Manager.getImage(settings.getValue("image")));
        } else openConsole();
    }

    public void openConsole() {
        addAutoCompleteWords(Manager.project.getValue("autocomplete"));
        StaticStuff.setCustomCommands(manager.getAllCCFirstWords());
        console.setVisible(true);
        playerStatus.open();
        if (!userPickedSavestate)
            new Thread(() -> executeEventFromObject(manager.getGeneralEventCollection().getUID(), "introOver", new String[]{})).start();
    }

    private boolean permissionWeb = false, permissionFileRead = false, permissionFileWrite = false, permissionFileReadAnywhere = false, permissionFileWriteAnywhere = false, permissionFileOpen = false;

    private boolean checkForPermissions(String permissions) {
        if (permissions.contains("web"))
            if (StaticStuff.openPopup(lang("permissionRequestWeb"), new String[]{lang("permissionRequestAccept"), lang("permissionRequestDeny")}) == 1)
                return false;
            else permissionWeb = true;
        if (permissions.contains("fileread") && !permissions.contains("filereadanywhere"))
            if (StaticStuff.openPopup(lang("permissionRequestFileRead"), new String[]{lang("permissionRequestAccept"), lang("permissionRequestDeny")}) == 1)
                return false;
            else permissionFileRead = true;
        if (permissions.contains("filewrite") && !permissions.contains("filewriteanywhere"))
            if (StaticStuff.openPopup(lang("permissionRequestFileWrite"), new String[]{lang("permissionRequestAccept"), lang("permissionRequestDeny")}) == 1)
                return false;
            else permissionFileWrite = true;
        if (permissions.contains("filereadanywhere"))
            if (StaticStuff.openPopup(lang("permissionRequestFileWriteAnywhere"), new String[]{lang("permissionRequestAccept"), lang("permissionRequestDeny")}) == 1)
                return false;
            else permissionFileWriteAnywhere = true;
        if (permissions.contains("filewriteanywhere"))
            if (StaticStuff.openPopup(lang("permissionRequestFileReadAnywhere"), new String[]{lang("permissionRequestAccept"), lang("permissionRequestDeny")}) == 1)
                return false;
            else permissionFileReadAnywhere = true;
        if (permissions.contains("fileopen"))
            if (StaticStuff.openPopup(lang("permissionRequestFileOpen"), new String[]{lang("permissionRequestAccept"), lang("permissionRequestDeny")}) == 1)
                return false;
            else permissionFileOpen = true;
        return true;
    }

    public void print(String str) {
        String[] toPrint = str.split("LINEBREAK");
        for (String s : toPrint) GuiMainConsole.appendToOutput(s);
    }

    public void activateLog() {
        if (settings.getValue("debugMode").equals("true")) Log.activate();
    }

    private ArrayList<CustomCommand> customCommands;
    private boolean userInputAllowed = true;

    public void executePlayerCommand(String cmd) {
        boolean found = cmd.equals("savegame");
        if (found) createSavestate();
        if (userInputAllowed) {
            for (CustomCommand customCommand : customCommands) {
                if (customCommand.matches(cmd)) {
                    found = true;
                    String[] newArgs = customCommand.getArgs(cmd);
                    executeEventFromObject(customCommand.uid, "commandExecuted", newArgs);
                }
            }
        }
        if (settings.getValue("debugMode").equals("true"))
            executeEvent("", new String[]{cmd}, new String[]{});
        else if (!found) new GuiHoverText("<html>" + StaticStuff.prepareString(lang("mainFramePlayerInputNoCommand")));
    }

    public static void executePlayerCommandFromLogger(String cmd) {
        if (self.userInputAllowed) {
            for (int i = 0; i < self.customCommands.size(); i++) {
                if (self.customCommands.get(i).matches(cmd)) {
                    String[] newArgs = self.customCommands.get(i).getArgs(cmd);
                    self.executeEventFromObject(self.customCommands.get(i).uid, "commandExecuted", newArgs);
                }
            }
        }
        if (self.settings.getValue("debugMode").equals("true"))
            self.executeEvent("", new String[]{cmd}, new String[]{});
    }

    public void createSavestate() {
        String saveName = StaticStuff.openPopup(lang("savestateEnterName"), "");
        while (saveName.equals("") || saveName.contains(".") || saveName.contains("/") || saveName.contains("-")) {
            saveName = StaticStuff.openPopup(lang("savestateInvalidName"), "");
        }
        manager.createSavestate(saveName + "---");
    }

    private boolean userPickedSavestate = false;

    private String letUserPickSavestateIfAvailable(String adventure) {
        ArrayList<String> available = new ArrayList<>();
        Collections.addAll(available, getSavestateFiles(adventure));
        for (int i = available.size() - 1; i >= 0; i--) {
            if (available.get(i).contains("---" + adventure + StaticStuff.adventureFileEnding))
                available.set(i, available.get(i).replaceAll("(.+)---" + adventure + StaticStuff.adventureFileEnding, "$1"));
            else available.remove(i);
        }
        if (available.size() > 0) {
            available.add(lang("savestateStartNewGame"));
            String picked = StaticStuff.openPopup((available.size() > 2 ? lang("savestateSavestatesAvailable") : lang("savestateSavestateAvailable")) + "<br>" + lang("savestateSelectSavestate"), available.toArray(new String[0]), "");
            if (picked.equals(lang("savestateStartNewGame"))) {
                return adventure;
            } else {
                picked = picked + "---" + adventure;
                extraFilePath = Manager.SAVESTATES_DIRECTORY.replace("savestates/adventures/", "savestates/");
                Manager.extraFilePath = extraFilePath;
                userPickedSavestate = true;
                return picked;
            }
        }
        return adventure;
    }

    private String[] getSavestateFiles(String adventure) {
        FileManager.makeDirectory(Manager.SAVESTATES_DIRECTORY);
        return FileManager.getFiles(Manager.SAVESTATES_DIRECTORY);
    }

    private void playerWalk(String newLocation) {
        if (newLocation.length() == 0) {
            print(lang("errorChatNoNewLocation"));
            return;
        }
        executeEventFromObject(player.getValue("location"), "walk", new String[]{"newLocation:" + newLocation});
        GuiPlayerStats.updateOutput();
    }

    public void execute(String type, String commandOrEvent, String as, String[] args) {
        String[] asUID;
        if (as.equals("self"))
            asUID = null;
        else
            asUID = evaluateSelector(as);

        if (type.equals("event")) { //execute an event
            commandOrEvent = "{" + prepareStringReplaceVar(commandOrEvent.substring(1));
            String selector = prepareStringReplaceVar(commandOrEvent.replaceAll("\\{event\\|(.+)\\|.+\\}", "$1")); //\\{event\\|(.+)\\|.+\\}
            String eventName = prepareStringReplaceVar(commandOrEvent.replaceAll("\\{event\\|.+\\|(.+)\\}", "$1"));
            String[] objectUID = evaluateSelector(selector);
            if (!eventName.equals("all")) { //execute only one event
                if (asUID == null)
                    for (String s : objectUID) executeEventFromObjectAs(s, s, eventName, args);
                else
                    for (String s : asUID)
                        for (String value : objectUID) executeEventFromObjectAs(s, value, eventName, args);
            } else { //execute all events of the object
                if (asUID == null)
                    for (String s : objectUID) {
                        ArrayList<String> events = manager.getAllEventNames(s);
                        for (String event : events) executeEventFromObjectAs(s, s, event, args);
                    }
                else
                    for (String s : asUID)
                        for (String value : objectUID) {
                            ArrayList<String> events = manager.getAllEventNames(value);
                            for (String event : events) executeEventFromObjectAs(s, value, event, args);
                        }
            }
        } else { //execute code
            String[] codeToExecute = commandOrEvent.substring(1, commandOrEvent.length() - 1).split(" && ");
            if (asUID == null) executeEvent("", codeToExecute, args);
            else
                for (String s : asUID) executeEvent(s, codeToExecute, args);
        }
    }

    public static void executeEventFromObjectStatic(String uid, String event, String[] args) {
        self.executeEvent(uid, self.manager.getEventCode(uid, event), args);
    }

    public static void executeEventFromGeneralEventCollectionStatic(String event, String[] args) {
        self.executeEventFromGeneralEventCollection(event, args);
    }

    public void executeEventFromGeneralEventCollection(String event, String[] args) {
        executeEvent(manager.getGeneralEventCollection().getUID(), manager.getEventCode(manager.getGeneralEventCollection().getUID(), event), args);
    }

    public void executeEventFromObject(String uid, String event, String[] args) {
        executeEvent(uid, manager.getEventCode(uid, event), args);
    }

    public void executeEventFromObjectAs(String as, String uid, String event, String[] args) {
        executeEvent(as, manager.getEventCode(uid, event), args);
    }

    public void executeEvent(String uid, String[] pCode, String[] args) {
        Log.addIndent();
        //for each stuff
        ArrayList<String> bracketsHierarchy = new ArrayList<>();
        ArrayList<String[]> forEachUIDs = new ArrayList<>();
        String lineResult = "";
        int beforeI, endI = 0;
        //if - else stuff
        ArrayList<Boolean> lastIfSuccessful = new ArrayList<>();
        try {
            for (int i = 0; i < pCode.length; i++) {
                pCode[i] = pCode[i].replace("\t", " ").trim();
                if (!(pCode[i] == null)) {
                    if (!pCode[i].equals("")) {
                        if (forEachUIDs.size() > 0) //there is currently a for each going on
                            for (int j = 0; j < forEachUIDs.size(); j++) {
                                for (int k = 0; k < forEachUIDs.get(j).length; k++) {
                                    beforeI = i;
                                    label:
                                    while (true) {
                                        lineResult = executeLine(i, forEachUIDs.get(j)[k], pCode[i], args, bracketsHierarchy, forEachUIDs, lastIfSuccessful);
                                        i++;
                                        if (lineResult.length() > 0)
                                            Log.add("Return value from code line inside of for each: " + lineResult);
                                        switch (lineResult) {
                                            case "forend":
                                                endI = i;
                                                i = beforeI;
                                                break label;
                                            case "return":
                                                Log.add("Leaving event and returning");
                                                Log.removeIndent();
                                                return;
                                            case "break":
                                                Log.add("Exiting 'for each' via break");
                                                break label;
                                            case "continue":
                                                if (!(k < forEachUIDs.get(j).length - 1)) { //was already last object
                                                    Log.add("Has already been last object; leaving 'for each' via continue");
                                                    lineResult = "break";
                                                } else {
                                                    Log.add("Next object in 'for each' via continue");
                                                    i = beforeI;
                                                }
                                                break label;
                                            case "ifskip":
                                                lastIfSuccessful.add(false);
                                                for (int l = 0, skipBrackets = 0; l < pCode.length - 1; l++) {
                                                    i++;
                                                    String testString = pCode[i].trim();
                                                    if (testString.equals(")")) {
                                                        skipBrackets--;
                                                    } else if (testString.matches("\\)( ?else ?\\()?") && !lastIfSuccessful.get(lastIfSuccessful.size() - 1) && skipBrackets == 0) {
                                                        skipBrackets--;
                                                    } else if (testString.matches("if (.+) \\(") || testString.matches("is (.+) \\("))
                                                        skipBrackets++;
                                                    if (skipBrackets == -1) {
                                                        lastIfSuccessful.remove(lastIfSuccessful.size() - 1);
                                                        break;
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                    if (lineResult.equals("break")) break;
                                }
                                forEachUIDs.remove(forEachUIDs.size() - 1);
                                bracketsHierarchy.remove(bracketsHierarchy.size() - 1);
                                i = endI - 1; //changed this from +1 to -1 since it continued at the wrong line.
                                Log.removeIndent();
                                if (lineResult.equals("break")) {
                                    while (i < pCode.length - 1) {
                                        i++;
                                        String testString = pCode[i].trim();
                                        if (testString.equals("}"))
                                            break;
                                    }
                                    break;
                                }
                            }
                        else //execute line normally
                            lineResult = executeLine(i, uid, pCode[i], args, bracketsHierarchy, forEachUIDs, lastIfSuccessful);
                        if (lineResult.length() > 0) Log.add("Return value from code line: " + lineResult);
                        if (lineResult.equals("")) ;
                        else if (lineResult.equals("ifskip")) {
                            lastIfSuccessful.add(false);
                            for (int skipBrackets = 0; i < pCode.length - 1; ) {
                                i++;
                                String testString = pCode[i].trim();
                                if (testString.equals(")")) {
                                    skipBrackets--;
                                } else if (testString.matches("\\)( ?else ?\\()?") && !lastIfSuccessful.get(lastIfSuccessful.size() - 1) && skipBrackets == 0) {
                                    skipBrackets--;
                                } else if (testString.matches("if (.+) \\(") || testString.matches("is (.+) \\("))
                                    skipBrackets++;
                                if (skipBrackets == -1) {
                                    lastIfSuccessful.remove(lastIfSuccessful.size() - 1);
                                    break;
                                }
                            }
                        } else if (lineResult.equals("forskip")) {
                            for (; i < pCode.length - 1; ) {
                                i++;
                                String testString = pCode[i].trim();
                                if (testString.equals("}"))
                                    break;
                            }
                        } else if (lineResult.contains("jumpfirst")) {
                            String looFor = lineResult.replace("jumpfirst:", "") + ":";
                            for (i = 0; i < pCode.length - 1; i++) {
                                if (pCode[i].trim().equals(looFor)) break;
                            }
                        } else if (lineResult.contains("jumplast")) {
                            String looFor = lineResult.replace("jumplast:", "") + ":";
                            for (i = pCode.length - 1; i >= 0; i--) {
                                if (pCode[i].trim().equals(looFor)) break;
                            }
                        } else if (lineResult.contains("jumpnext")) {
                            String looFor = lineResult.replace("jumpnext:", "") + ":";
                            for (; i < pCode.length - 1; i++) {
                                if (pCode[i].trim().equals(looFor)) break;
                            }
                        } else if (lineResult.equals("return")) {
                            Log.add("Leaving event and returning");
                            Log.removeIndent();
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.add("Malformed command: " + e);
            e.printStackTrace();
        }
        Log.removeIndent();
        try {
            GuiPlayerStats.updateOutput();
        } catch (Exception e) {
            Log.add("Unable to update player GUI (has it opened yet?)");
        }
    }

    private String executeLine(int lineIndex, String uid, String line, String[] args, ArrayList<String> bracketsHierarchy, ArrayList<String[]> forEachUIDs, ArrayList<Boolean> lastIfSuccessful) {
        String[] codeWords;
        String code;
        code = line;
        if (uid.length() > 0) code = code.trim().replace("{this}", uid);
        for (String arg : args) {
            if (arg.matches("[^:]+:.+"))
                code = code.replace("{" + arg.replaceAll(":.+", "") + "}", arg.replaceAll(".+:", ""));
        }
        codeWords = code.split(" ");
        Log.add("Line " + lineIndex + " as " + uid + ": " + code);
        if (codeWords[0].matches("i[sf]")) {
            boolean res = false;
            if (code.matches("i[sf] (.+ [=><!contaismhequl]+ .+(?: \\|\\| )?)* \\("))
                res = checkCondition(code.replaceAll("i[sf] (.+ [=><!contaismhequl]+ .+(?: \\|\\| )?)* \\(", "$1"));
            else if (code.matches("i[sf] talent [^(]+ \\("))
                res = checkCondition(code.replaceAll("i[sf] (talent [^(]+) \\(", "$1"));
            else Log.add("Invalid condition: " + code);
            if (res) lastIfSuccessful.add(res);
            if (res) Log.add("Taking condition branch 'true'");
            else {
                Log.add("Taking condition branch 'false'");
                return "ifskip";
            }
        } else if (codeWords[0].matches("i[sf]not")) {
            boolean res = false;
            if (code.matches("i[sf]not (.+ [=><!contaismhequl]+ .+(?: \\|\\| )?)* \\("))
                res = !checkCondition(code.replaceAll("i[sf]not (.+ [=><!contaismhequl]+ .+(?: \\|\\| )?)* \\(", "$1"));
            else if (code.matches("i[sf]not talent [^(]+ \\("))
                res = !checkCondition(code.replaceAll("i[sf]not (talent [^(]+) \\(", "$1"));
            else Log.add("Invalid condition: " + code);
            if (res) lastIfSuccessful.add(res);
            if (res) Log.add("Taking condition branch 'true'");
            else {
                Log.add("Taking condition branch 'false'");
                return "ifskip";
            }
        } else if (code.matches("\\) ?else ?\\(?")) {
            boolean skip = lastIfSuccessful.get(lastIfSuccessful.size() - 1);
            if (skip) return "ifskip";
            lastIfSuccessful.remove(lastIfSuccessful.size() - 1);
        } else if (code.charAt(0) == '{') {
            if (code.matches("\\{(?:[^\\}\\{]*(?:\\{[^\\}]+\\})*[^\\}\\{]*)\\}(?:\\.[^\\(]+\\([^\\)]*\\))* = .+")) { //\\{[^\\}]+\\}(?:\\.[^\\(]+\\([^\\)]*\\))* = .+
                setValueEvaluate(code.replaceAll("(\\{(?:[^\\}\\{]*(?:\\{[^\\}]+\\})*[^\\}\\{]*)\\}(?:\\.[^\\(]+\\([^\\)]*\\))*) = .+", "$1"), code.replaceAll("\\{(?:[^\\}\\{]*(?:\\{[^\\}]+\\})*[^\\}\\{]*)\\}(?:\\.[^\\(]+\\([^\\)]*\\))* = (.+)", "$1"), true);
            } else if (code.matches("\\{(?:[^\\}\\{]*(?:\\{[^\\}]+\\})*[^\\}\\{]*)\\}(?:\\.[^\\(]+\\([^\\)]*\\))* == .+")) { //"\\{[^\\}]+\\}(?:\\.[^\\(]+\\([^\\)]*\\))* == .+"
                setValueEvaluate(code.replaceAll("(\\{(?:[^\\}\\{]*(?:\\{[^\\}]+\\})*[^\\}\\{]*)\\}(?:\\.[^\\(]+\\([^\\)]*\\))*) == .+", "$1"), code.replaceAll("\\{(?:[^\\}\\{]*(?:\\{[^\\}]+\\})*[^\\}\\{]*)\\}(?:\\.[^\\(]+\\([^\\)]*\\))* == (.+)", "$1"), false);
            }
        } else if (code.equals("return")) {
            return "return";
        } else if (code.equals("continue")) {
            return "continue";
        } else if (code.equals("break")) {
            return "break";
        } else if (code.matches("popup .+ open as .+")) { //open popup
            String asName = code.replaceAll("popup .+ open as (.+)", "$1");
            for (String s : evaluateSelector(code.replaceAll("popup (.+) open as .+", "$1")))
                manager.openCustomPopup(s, asName);
        } else if (code.matches("popup .+ close")) { //close popup
            String closeName = code.replaceAll("popup (.+) close", "$1");
            if (closeName.matches("#.+#")) { //close via selector
                for (String s : evaluateSelector(closeName))
                    manager.closeCustomPopup(s);
            } else { //close via name
                manager.closeCustomPopup(closeName);
            }
        } else if (code.matches("popup .+ set .+ attribute .+ to .+")) { //edit popup
            String setName = code.replaceAll("popup (.+) set .+ attribute .+ to .+", "$1");
            String component = code.replaceAll("popup .+ set (.+) attribute .+ to .+", "$1");
            String attribute = code.replaceAll("popup .+ set .+ attribute (.+) to .+", "$1");
            String value = code.replaceAll("popup .+ set .+ attribute .+ to (.+)", "$1");
            if (setName.matches("#.+#")) { //edit via selector
                for (String s : evaluateSelector(setName))
                    manager.setCustomPopupData(s, component, attribute, value);
            } else { //edit via name
                manager.setCustomPopupData(setName, component, attribute, value);
            }
        } else if (codeWords[0].equals("goto")) {
            codeWords[1] = evaluateSelector(code.replace("goto ", ""))[0];
            if (Manager.locationExists(codeWords[1])) {
                String comeFrom = player.getValue("location");
                executeEventFromObject(player.getValue("location"), "exit", new String[]{"comeFromLocation:" + comeFrom, "gotoLocation:" + codeWords[1]});
                player.setValue("location", codeWords[1]);
                executeEventFromObject(codeWords[1], "entry", new String[]{"comeFromLocation:" + comeFrom, "gotoLocation:" + codeWords[1]});
            }
        } else if (codeWords[0].equals("print")) {
            print(prepareStringReplaceVar(code.replace("print ", "")));
        } else if (codeWords[0].equals("printwait")) {
            code = code.replace("printwait ", "");
            print(prepareStringReplaceVar(code));
            int duration = (int) ((Float.parseFloat(StaticStuff.removeTextFormatting(code).length() + "") * 1000f) / 15f);
            duration = (int) ((Float.parseFloat(duration + "") / 100f) * textSpeedFactor) + 800;
            Log.add("Sleeping for " + duration + " milliseconds");
            Sleep.milliseconds(duration);
        } else if (codeWords[0].equals("evaluate")) {
            evaluatePrint(code.replace("evaluate ", ""));
        } else if (codeWords[0].equals("wait") || codeWords[0].equals("pause") || codeWords[0].equals("sleep")) {
            sleepTime(code);
        } else if (codeWords[0].equals("execute") && codeWords.length > 3) {
            //code = prepareStringReplaceVar(code); //EXECUTE STUFF
            execute(code.replaceAll("execute (event|code) .+ as .+ \\{(?:.+)?\\}", "$1"), code.replaceAll("execute (?:event|code) (.+) as .+ \\{(?:.+)?\\}", "$1"),
                    code.replaceAll("execute (?:event|code) .+ as (.+) \\{(?:.+)?\\}", "$1"), code.replaceAll("execute (?:event|code) .+ as .+ \\{(.+)?\\}", "$1").split(", "));
        } else if (codeWords[0].equals("jumpto")) {
            if (codeWords[1].equals("first")) {
                return "jumpfirst:" + codeWords[2];
            } else if (codeWords[1].equals("last")) {
                return "jumplast:" + codeWords[2];
            } else if (codeWords[1].equals("next")) {
                return "jumpnext:" + codeWords[2];
            }
        } else if (codeWords[0].equals("alert")) {
            StaticStuff.openPopup(prepareStringReplaceVar(code.replace("alert ", "")));
        } else if (codeWords[0].equals("tag")) {
            tagModify(codeWords[1], code.replaceAll("tag #(.+)?# (add|remove) ", ""), codeWords[2]);
        } else if (codeWords[0].equals("audio") && codeWords[1].equals("play")) {
            playAudio(code.replace("audio play ", ""));
        } else if (codeWords[0].equals("audio") && codeWords[1].equals("stop")) {
            stopAudio(code.replace("audio stop ", ""));
        } else if (codeWords[0].equals("audio") && codeWords[1].equals("list")) {
            Audio.print = true;
        } else if (codeWords[0].equals("inventory") || codeWords[0].equals("inv")) {
            inventoryOperation(code.replaceAll("inv(entory)? ", ""));
        } else if (codeWords[0].equals("log") && codeWords[1].equals("add")) {
            boolean beforeState = Log.isActive();
            Log.setActive(true);
            Log.add(code.replace("log add ", ""));
            Log.setActive(beforeState);
        } else if (codeWords[0].equals("log") && codeWords[1].equals("dump") && codeWords.length == 2) {
            Log.dump("");
        } else if (codeWords[0].equals("log") && codeWords[1].equals("dump") && codeWords.length == 3) {
            Log.dump(codeWords[2]);
        } else if (codeWords[0].equals("open") && codeWords[1].equals("image")) {
            code = code.replace("open image ", "");
            String[] selected = evaluateSelector(prepareStringReplaceVar(code.replaceAll("(.+) text (.+) size (.+)", "$1")));
            for (String s : selected)
                if (manager.isType(s, "image"))
                    Manager.openImage(s, prepareStringReplaceVar(code.replaceAll("(.+) text (.+) size (.+)", "$2")), Integer.parseInt(prepareStringReplaceVar(code.replaceAll("(.+) text (.+) size (.+)", "$3"))), false);
        } else if (codeWords[0].equals("open") && codeWords[1].equals("object")) {
            code = code.replace("open object ", "");
            String[] selected = evaluateSelector(code.replaceAll("(#(?:.+)?#) ?(.+)?", "$1"));
            String extraText = "";
            if (code.matches("(#(?:.+)?#) (.+)")) extraText = code.replaceAll("(#(?:.+)?#) (.+)", "$2");
            for (String s : selected) GuiObjectDisplay.create(Manager.getEntity(s), extraText);
        } else if (codeWords[0].equals("close") && codeWords[1].equals("object")) {
            code = code.replace("open object ", "");
            String[] selected = evaluateSelector(code.replaceAll("(#(?:.+)?#) ?(.+)?", "$1"));
            for (String s : selected) GuiObjectDisplay.close(Manager.getEntity(s));
        } else if (codeWords[0].equals("open") && codeWords[1].equals("file")) {
            openFileCommand(code);
        } else if (codeWords[0].equals("open") && codeWords[1].equals("stats")) {
            playerStatus.open();
        } else if (codeWords[0].equals("selector")) {
            evaluateSelector(code.replace("selector ", ""));
        } else if (codeWords[0].equals("player") && codeWords[1].equals("input")) {
            userInputAllowed = Boolean.parseBoolean(codeWords[2]);
            console.setPlayerInputActive(userInputAllowed);
            Log.add("Set input allowed to: " + userInputAllowed);
        } else if (codeWords[0].equals("battle")) {
            battleOperation(code.replaceAll("battle (.+)", "$1"));
        } else if (codeWords[0].equals("drop")) {
            dropLootTable(code.replaceFirst("drop ", ""));
        } else if (code.matches("for each .+ \\{")) {
            String[] forEachEntity;
            if (code.matches("for each #(.*)# \\{"))
                forEachEntity = evaluateSelector(code.replaceAll("for each #(.*)# \\{", "$1"));
            else
                forEachEntity = evaluateVariableWithVariablesInside(code.replaceAll("for each (.+) \\{", "$1"));
            if (forEachEntity.length == 0) { // no entities found
                return "forskip";
            } else { // do for each entity found
                forEachUIDs.add(forEachEntity);
                bracketsHierarchy.add("for");
                Log.addIndent();
            }
        } else if (bracketsHierarchy.size() > 0) {
            if (code.equals("}") && bracketsHierarchy.get(bracketsHierarchy.size() - 1).equals("for")) {
                return "forend";
            }
        } else if (code.equals("}")) {
            return "forend";
        } else if (codeWords[0].equals("clear")) {
            clearCommands(code);
        }
        return "";
    }

    private boolean checkCondition(String condition) {
        String[] conditions = condition.split(" \\|\\| ");
        for (String s : conditions)
            if (checkOneCondition(s)) return true;
        return false;
    }

    private boolean checkOneCondition(String condition) {
        String[] codeWords = condition.split(" ");
        Log.add("Checking condition: " + condition);
        if (codeWords[0].equals("talent")) { //roll a talent
            String name = condition.replaceAll("talent (.+) DC (.+) (true|false) (true|false) (.+)", "$1");
            String dc = condition.replaceAll("talent (.+) DC (.+) (true|false) (true|false) (.+)", "$2");
            String visible = condition.replaceAll("talent (.+) DC (.+) (true|false) (true|false) (.+)", "$3");
            String autoRoll = condition.replaceAll("talent (.+) DC (.+) (true|false) (true|false) (.+)", "$4");
            String text = condition.replaceAll("talent (.+) DC (.+) (true|false) (true|false) (.+)", "$5");
            if (!rollTalent(name, dc, visible, autoRoll, text))
                return false;
        } else if (codeWords[0].equals("selector") || codeWords[1].charAt(0) == '#') {
            if (evaluateSelector(codeWords[1]).length == 0) //check for selector
                return false;
        } else if (codeWords[0].equals("expression") || !condition.contains("selector") || !condition.contains("talent")) {
            String p1 = prepareStringReplaceVar(condition.replaceAll("(?:expression )?(.+) ([=><!contaismhequl]+) (.+)", "$1"));
            String p2 = prepareStringReplaceVar(condition.replaceAll("(?:expression )?(.+) ([=><!contaismhequl]+) (.+)", "$3"));
            String method = condition.replaceAll("(?:expression )?(.+) ([=><!contaismhequl]+) (.+)", "$2");
            switch (method) {
                case "==":
                case "=":
                case "equals":
                    if (!p1.equals(p2)) return false;
                    break;
                case "contains":
                    if (!p1.contains(p2)) return false;
                    break;
                case "matches":
                    if (!p1.replace("ESCAPEDCURVEDBRACKETSOPEN", "{").replace("ESCAPEDCURVEDBRACKETSCLOSED", "}").matches(p2.replace("ESCAPEDCURVEDBRACKETSOPEN", "{").replace("ESCAPEDCURVEDBRACKETSCLOSED", "}")))
                        return false;
                    break;
                case ">":
                    if (!(Integer.parseInt(p1) > Integer.parseInt(p2))) return false;
                    break;
                case "<":
                    if (!(Integer.parseInt(p1) < Integer.parseInt(p2))) return false;
                    break;
                case ">=":
                    if (!(Integer.parseInt(p1) >= Integer.parseInt(p2))) return false;
                    break;
                case "<=":
                    if (!(Integer.parseInt(p1) >= Integer.parseInt(p2))) return false;
                    break;
                case "!=":
                case "<>":
                    if (Integer.parseInt(p1) == Integer.parseInt(p2)) return false;
                    break;
            }
        }
        return true;
    }

    private void openFileCommand(String code) {
        String param = code.replaceAll("open file (.+)", "$1");
        if (param.charAt(0) == '{') { //open file from advfiles folder
            if (!permissionFileOpen) {
                Log.add("You need the permission fileopen to open files from the advfiles folder.");
                return;
            }
            if (code.matches("open file \\{file\\|([^\\}]+)\\}"))
                code = code.replaceAll("open file \\{file\\|([^\\}]+)\\}", "$1");
            else code = code.replace("open file ", "");
            if (code.contains("..")) {
                Log.add("You cannot open files outside of the advfiles folder.");
                return;
            }
            Log.add("Opening file " + "res/advfiles/" + code);
            FileManager.makeDirectory("res/advfiles/");
            FileManager.openFile("res/advfiles/" + code);
        } else { //open fileObject
            String[] objects = evaluateSelector(param);
            for (String obj : objects) {
                if (manager.isType(obj, "fileObject")) {
                    manager.openFileObjectFile(obj);
                } else {
                    Log.add(obj + " is not a fileObject");
                }
            }
        }
    }

    private void clearCommands(String p) {
        if (p.equals("clear console") || p.equals("clear chat")) {
            console.clearChat();
        } else if (p.matches("clear inventory #.*#")) {
            String[] uids = evaluateSelector(prepareStringReplaceVar(p.replaceAll("clear inventory (#.*#)", "$1")));
            for (String uid : uids) {
                manager.clearInventory(uid);
                Log.add("Removing all items from inventory " + uid);
            }
        }
    }

    public void evaluatePrint(String eval) {
        String[] result = null;
        if (eval.charAt(0) == '{') result = evaluateVariableWithVariablesInside(eval);
        if (eval.charAt(0) == '#') result = evaluateSelector(eval);
        assert result != null;
        for (String s : result) print(s);
    }

    public void dropLootTable(String code) {
        String[] lootTableUID = evaluateSelector(code.replaceAll("(#[^#]+#) to #[^#]+#", "$1"));
        String[] inventoryUID = evaluateSelector(code.replaceAll("#[^#]+# to (#[^#]+#)", "$1"));
        for (int i = 0; i < lootTableUID.length; i++) {
            for (int j = 0; j < inventoryUID.length; j++) {
                manager.dropLootTable(lootTableUID[i], inventoryUID[i]);
            }
        }
    }

    private BattleMap currentBattleMap;
    private static boolean battleActive = false;

    public void openBattleMap(String selector) {
        try {
            String battleMapUID = evaluateSelector(selector)[0];
            currentBattleMap = manager.getBattleMap(battleMapUID);
            GuiBattleMap currentBattleMapGui = new GuiBattleMap(currentBattleMap, this);
            console.battleMode(true);
            playerStatus.battleMode(true);
            playerStatus.open();
            currentBattleMap.startBattle(currentBattleMapGui);
            userInputAllowed = false;
            battleActive = true;
            Log.add("Started battle with UID: " + battleMapUID);
        } catch (Exception e) {
            Log.add("Unable to start battle: " + selector + "\n" + e);
            e.printStackTrace();
        }
    }

    public void endBattle() {
        console.battleMode(false);
        playerStatus.battleMode(false);
        currentBattleMap.stopBattle();
        userInputAllowed = true;
        battleActive = false;
    }

    private void battleOperation(String p) {
        String[] splitted = p.split(" ");
        if (splitted.length >= 2) {
            if (splitted[0].matches("(?:start|open)")) {
                openBattleMap(p.replaceAll("(?:start|open) (.+)", "$1"));
            } else if (splitted[0].equals("stop") || splitted[0].equals("end")) {
                endBattle();
                manager.executeEventFromObject(currentBattleMap.uid, "end", new String[]{"outcome:" + p.replaceAll("(?:stop|end) (.+)", "$1")});
            } else if (splitted[0].equals("set") && p.replaceAll("set (.+)", "$1").matches("(.+) x (.+) ([npcitemxragoudlbs]+) ([xyimageud]+) (.+)")) {
                currentBattleMap.setBattleDataViaXYType(p.replaceAll("set (.+)", "$1"), this, manager);
            } else if (splitted[0].equals("set")) {
                currentBattleMap.setBattleDataViaUID(p.replaceAll("set (.+)", "$1"), this, manager);
            } else if (splitted[0].equals("active")) {
                currentBattleMap.setBattleActive(p.replaceAll("active (.+)", "$1").equals("true"));
            } else if (splitted[0].equals("canend")) {
                currentBattleMap.setCanEnd(p.replaceAll("canend (.+)", "$1").equals("true"));
            } else if (splitted[0].equals("freewalk")) {
                currentBattleMap.setFreewalk(p.replaceAll("freewalk (.+)", "$1").equals("true"));
            } else if (splitted[0].equals("add")) {
                currentBattleMap.addBattleDataCommand(p.replaceAll("add (.+)", "$1"), this, manager);
            } else if (splitted[0].equals("remove")) {
                currentBattleMap.removeBattleDataCommand(p.replaceAll("remove (.+)", "$1"), this);
            } else if (splitted[0].equals("refresh")) {
                currentBattleMap.refreshGui(prepareStringReplaceVar(p.replaceAll("refresh (.+)", "$1")));
            } else if (splitted[0].equals("action")) {
                currentBattleMap.action(p.replaceAll("action (.+)", "$1"), this);
            }
        }
    }

    public static boolean isBattleActive() {
        return battleActive;
    }

    public void inventoryOperation(String text) {
        Log.add("Inventory operation: " + text);
        String operationType = text.split(" ")[0];
        switch (operationType) {
            case "add" -> {
                Pattern patt = Pattern.compile("add (.+) to (.+) amount (.+)");
                Matcher m = patt.matcher(text);
                if (m.find()) {
                    String item = m.group(1), inventory = m.group(2);
                    int amount = Integer.parseInt(m.group(3));
                    String[] itemObjects = evaluateSelector(item);
                    String[] inventoryObjects = evaluateSelector(inventory);
                    for (String itemObject : itemObjects)
                        if (manager.isType(itemObject, "item"))
                            for (String inventoryObject : inventoryObjects)
                                if (manager.isType(inventoryObject, "inventory"))
                                    manager.inventoryAddItem(itemObject, inventoryObject, amount);
                }
            }
            case "remove" -> {
                Pattern patt = Pattern.compile("remove (.+) from (.+) amount (.+)");
                Matcher m = patt.matcher(text);
                if (m.find()) {
                    String item = m.group(1), inventory = m.group(2);
                    int amount = Integer.parseInt(m.group(3)) * -1;
                    String[] itemObjects = evaluateSelector(item);
                    String[] inventoryObjects = evaluateSelector(inventory);
                    for (String itemObject : itemObjects)
                        if (manager.isType(itemObject, "item"))
                            for (String inventoryObject : inventoryObjects)
                                if (manager.isType(inventoryObject, "inventory"))
                                    manager.inventoryAddItem(itemObject, inventoryObject, amount);
                }
            }
            case "move" -> { //inventory move [SELECTOR] from [SELECTOR] amount [VALUE/VARIABLE] to [SELECTOR]
                Pattern patt = Pattern.compile("move (.+) from (.+) amount (.+) to (.+)");
                Matcher m = patt.matcher(text);
                if (m.find()) {
                    String item = m.group(1), fromInventory = m.group(2), toInventory = m.group(4);
                    int amount = Integer.parseInt(prepareStringReplaceVar(m.group(3)));
                    String[] itemObjects = evaluateSelector(item);
                    String[] fromInventoryObjects = evaluateSelector(fromInventory);
                    String toInventoryObject = evaluateSelector(toInventory)[0];
                    if (manager.isType(toInventoryObject, "inventory")) {
                        for (String itemObject : itemObjects)
                            if (manager.isType(itemObject, "item"))
                                for (String fromInventoryObject : fromInventoryObjects)
                                    if (manager.isType(fromInventoryObject, "inventory"))
                                        manager.inventoryAddItem(itemObject, fromInventoryObject, amount * -1);
                        for (String itemObject : itemObjects)
                            if (manager.isType(itemObject, "item"))
                                manager.inventoryAddItem(itemObject, toInventoryObject, amount * fromInventoryObjects.length);
                    }
                }
            }
            case "set" -> {
                Pattern patt = Pattern.compile("set (.+) to (.+) amount (.+)");
                Matcher m = patt.matcher(text);
                if (m.find()) {
                    String item = m.group(1), inventory = m.group(2);
                    int amount = Integer.parseInt(m.group(3));
                    String[] itemObjects = evaluateSelector(item);
                    String[] inventoryObjects = evaluateSelector(inventory);
                    for (String itemObject : itemObjects)
                        if (manager.isType(itemObject, "item"))
                            for (String inventoryObject : inventoryObjects)
                                if (manager.isType(inventoryObject, "inventory"))
                                    manager.inventorySetItem(itemObject, inventoryObject, amount);
                }
            }
        }
        GuiPlayerStats.updateOutput();
    }

    public void playAudio(String audio) {
        String[] selectedObjects = evaluateSelector(audio);
        for (String selectedObject : selectedObjects) {
            executeEventFromObject(manager.getGeneralEventCollection().getUID(), "audioStart", new String[]{"uid:" + selectedObject});
            if (Manager.audioExists(selectedObject)) manager.playAudio(selectedObject);
        }
    }

    public void stopAudio(String audio) {
        String[] selectedObjects = evaluateSelector(audio);
        for (String selectedObject : selectedObjects) {
            if (Manager.audioExists(selectedObject)) {
                executeEventFromObject(manager.getGeneralEventCollection().getUID(), "audioStop", new String[]{"uid:" + selectedObject});
                Audio.stopToPlay = selectedObject;
                Sleep.milliseconds(250);
            }
        }
    }

    public static void audioHasStopped(String uid) {
        Log.add("Audio has stopped playing: " + uid.replace("[FILE]", ""));
        if (!uid.contains("[FILE]")) {
            self.executeEventFromObject(self.manager.getGeneralEventCollection().getUID(), "audioEnd", new String[]{"uid:" + uid});
        }
    }

    public void tagModify(String selector, String tag, String addRemove) {
        String[] selectedObjects = evaluateSelector(selector);
        tag = prepareStringReplaceVar(tag);
        if (addRemove.equals("add")) {
            for (String selectedObject : selectedObjects) manager.addTag(selectedObject, tag);
        } else {
            for (String selectedObject : selectedObjects) manager.removeTag(selectedObject, tag);
        }
    }

    public void sleepTime(String time) {
        Pattern patt = Pattern.compile("(?:.+ )?((?:[0-9]+)|(?:\\{[a-zA-Z0-9]+\\})) ?(ms|s|m|h])");
        Matcher m = patt.matcher(time);
        if (m.find()) {
            int duration = Integer.parseInt(prepareStringReplaceVar(m.group(1)));
            String type = m.group(2);
            Log.add("Sleep " + duration + " " + type);
            switch (type) {
                case "ms" -> Sleep.milliseconds(duration);
                case "s" -> Sleep.seconds(duration);
                case "m" -> Sleep.minutes(duration);
                case "h" -> Sleep.hours(duration);
            }
        }
    }

    public void setValueEvaluate(String variable, String values, boolean setList) {
        if (!(variable.charAt(0) == '{')) {
            Log.add("Invalid variable: " + variable);
            return;
        }
        String[] results;
        if (setList)
            results = evaluateVariableWithVariablesInside(values);
        else
            results = new String[]{prepareStringReplaceVar(values)};
        int res;
        if (results.length > 0)
            for (int i = 0; i < results.length; i++) {
                res = StaticStuff.evaluateMathExpression(results[i]);
                if (res != -969657) results[i] = "" + res;
            }
        else {
            res = StaticStuff.evaluateMathExpression(values);
            if (res != -969657) values = "" + res;
            results = new String[]{values};
        }
        setValue("{" + prepareStringReplaceVar(variable.substring(1)), results); //replace variables only inside of variable; leave main variable as it is
        GuiPlayerStats.updateOutput();
    }

    private void setValue(String variable, String[] values) {
        try {
            Log.add("trying to set variable '" + variable + "' to" + (values.length > 1 ? "" : " '" + values[0] + "'"));
            Log.addIndent();
            String valueToSet = "";
            if (values.length > 1) {
                for (String s : values) {
                    valueToSet += "LINEBREAK" + s;
                    Log.add(s);
                }
                valueToSet = valueToSet.replaceFirst("LINEBREAK", "");
            } else valueToSet = values[0];

            String[] variableParts = variable.replaceAll("\\{([^\\{]+)\\}.*", "$1").split("\\|");
            if (Manager.variableExists(variableParts[0])) {
                manager.setVariableByUIDorName(variableParts[0], valueToSet);
            } else if (manager.hasLocalVariableByVarUID(variableParts[0])) {
                manager.setLocalVariableByVarUID(variableParts[0], valueToSet);
            } else if (variableParts[0].equals("selector") || variableParts[0].equals("value")) {
                String[] selector = evaluateSelector(variableParts[1]);
                if (variable.contains(".name()")) {
                    for (String s : selector) manager.setName(s, valueToSet);
                } else if (variable.contains(".description()")) {
                    for (String s : selector) manager.setDescription(s, valueToSet);
                } else if (variable.contains(".image()")) {
                    for (String s : selector) manager.setImage(s, valueToSet);
                } else if (variable.contains(".location()")) {
                    for (String s : selector) manager.setLocationFromNPC(s, valueToSet);
                } else if (variable.contains(".variable")) {
                    String variableName = variable.replaceAll("\\{[^\\{]+\\}\\.[^\\(]+\\(([^\\(]*)\\)", "$1");
                    for (String s : selector) manager.setLocalVariableByVarNameObjectUID(s, variableName, valueToSet);
                }
            } else if (variableParts[0].equals("file")) {
                if (!permissionFileWrite && !permissionFileWriteAnywhere) {
                    Log.add("Missing permission 'filewrite'");
                    return;
                }
                if (variableParts[1].contains("..") && !permissionFileWriteAnywhere) {
                    Log.add("Missing permission 'filewriteanywhere'");
                    return;
                }
                FileManager.writeToFile("res/advfiles/" + variableParts[1], valueToSet.split("LINEBREAK"));
            } else if (variableParts[0].equals("player")) {
                player.setValue(variableParts[1], valueToSet);
            } else if (variableParts[0].equals("project")) {
                settings.setValue(variableParts[1], valueToSet);
            } else if (variableParts[0].equals("lastInput")) {
                StaticStuff.lastInput = valueToSet;
            } else if (variableParts[0].equals("amount")) {
                String[] itemObjects = evaluateSelector(prepareStringReplaceVar(variableParts[1]));
                String[] inventoryObjects = evaluateSelector(prepareStringReplaceVar(variableParts[2]));
                int amount = Integer.parseInt(valueToSet);
                for (String itemObject : itemObjects)
                    for (String inventoryObject : inventoryObjects)
                        manager.inventorySetItem(itemObject, inventoryObject, amount);
            }
            Log.removeIndent();
        } catch (Exception e) {
            e.printStackTrace();
            Log.add("Unable to set variable " + variable);
        }
    }

    public boolean rollTalent(String talent, String dc, String visible, String autoRoll, String message) {
        talent = prepareStringReplaceVar(talent.replace("-", " "));
        String talentUID = manager.getUID(talent);
        String[] attributes = manager.getTalentAttributes(talentUID);
        if (this.autoRoll && !Boolean.parseBoolean(autoRoll)) autoRoll = "true";
        dc = prepareStringReplaceVar(dc);
        int diffClass = 0;
        if (dc.matches("-?\\d+")) diffClass = Integer.parseInt(dc);
        else Log.add("Invalid DC value (does not match -?\\d+): " + dc);

        Log.add("Rolling talent " + talent + " with DC " + dc);
        if (attributes.length > 0)
            if (!rollAttributeWithDC(attributes, 0, diffClass, visible.equals("true"), autoRoll.equals("true"), talent, message)) {
                if (visible.equals("true")) StaticStuff.openPopup(lang("popupTalentRollFail"));
                return false;
            }
        if (attributes.length > 1)
            if (!rollAttributeWithDC(attributes, 1, diffClass, visible.equals("true"), autoRoll.equals("true"), talent, message)) {
                if (visible.equals("true")) StaticStuff.openPopup(lang("popupTalentRollFail"));
                return false;
            }
        if (attributes.length > 2)
            if (!rollAttributeWithDC(attributes, 2, diffClass, visible.equals("true"), autoRoll.equals("true"), talent, message)) {
                if (visible.equals("true")) StaticStuff.openPopup(lang("popupTalentRollFail"));
                return false;
            }

        if (visible.equals("true")) StaticStuff.openPopup(lang("popupTalentRollSuccess"));
        return true;
    }

    private boolean rollAttributeWithDC(String[] attributes, int index, int dc, boolean visible, boolean autoRoll, String talentName, String message) {
        String attributesText = "";
        switch (attributes.length) { //setup message
            case 1 -> attributesText = "[[aqua:" + attributes[0] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[0])) + "[[gray:)]]]]";
            case 2 -> {
                if (index == 0)
                    attributesText = "[[aqua:" + attributes[0] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[0])) + "[[gray:)]]]]" + " - [[gray:" + attributes[1] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[0])) + "[[gray:)]]]]";
                if (index == 1)
                    attributesText = "[[gray:" + attributes[0] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[0])) + "[[gray:)]]]]" + " - [[aqua:" + attributes[1] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[0])) + "[[gray:)]]]]";
            }
            case 3 -> {
                if (index == 0)
                    attributesText = "[[aqua:" + attributes[0] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[0])) + "[[gray:)]]]]" + " - [[gray:" + attributes[1] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[1])) + "[[gray:)]]]]" + " - [[gray:" + attributes[2] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[2])) + "[[gray:)]]]]";
                if (index == 1)
                    attributesText = "[[gray:" + attributes[0] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[0])) + "[[gray:)]]]]" + " - [[aqua:" + attributes[1] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[1])) + "[[gray:)]]]]" + " - [[gray:" + attributes[2] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[2])) + "[[gray:)]]]]";
                if (index == 2)
                    attributesText = "[[gray:" + attributes[0] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[0])) + "[[gray:)]]]]" + " - [[gray:" + attributes[1] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[1])) + "[[gray:)]]]]" + " - [[aqua:" + attributes[2] + " [[gray:(]]" + Integer.parseInt(player.getValue(attributes[2])) + "[[gray:)]]]]";
            }
        }
        String displayText = (!message.equals("none") ? "[[gray:" + message + "]] - " : "") + "[[gold:" + talentName + "]] - [[gray:DC:]] [[gold:" + dc + "]]<br>" + attributesText;
        int playerValueTotal = Integer.parseInt(player.getValue(attributes[index])) + (visible ? StaticStuff.rollDice(displayText, 20, 10, autoRoll) : StaticStuff.randomNumber(1, 20));
        return playerValueTotal >= dc;
    }

    public String[] evaluateSelector(String selector) {
        Log.addIndent();
        Log.add("Evaluate selector: " + selector);
        if (selector.matches("#\\{.+\\}#"))
            selector = prepareStringReplaceVar(selector.replaceAll("#(\\{.+\\})#", "$1"));
        if (StaticStuff.isValidUIDSilent(selector.replace("#", ""))) {
            Log.add("Result of selector: " + selector.replace("#", ""));
            Log.removeIndent();
            return new String[]{selector.replace("#", "")};
        }
        if (!selector.matches("#(.+)?#")) selector = "#" + selector + "#";
        ArrayList<String> allObjects;
        int amount;
        String[] result;
        if (selector.matches("#[\\da-z]{16}#")) {
            String selUID = selector.replaceAll("#([\\da-z]{16})#", "$1");
            if (!manager.getTypeByUID(selUID).equals("null")) {
                result = new String[]{selUID};
                Log.add("Result of selector: " + result[0]);
            } else {
                result = new String[]{};
                Log.add("Result of selector: no objects");
            }
            Log.removeIndent();
            return result;
        } else {
            String[] selectorParts = selector.replaceAll("#(.+)#", "$1").split(";");
            allObjects = manager.getAllObjects();
            amount = allObjects.size();
            String sortType = "random";
            for (int i = 0; i < selectorParts.length; i++) {
                if (selectorParts[i].startsWith("uid:")) {
                    selectorParts[i] = prepareStringReplaceVar(selectorParts[i]);
                    for (int j = 0; j < allObjects.size(); j++) {
                        if (!allObjects.get(j).equals(selectorParts[i].replaceAll(".+:", ""))) {
                            allObjects.remove(j);
                            j--;
                        }
                    }
                } else if (selectorParts[i].startsWith("tag:")) {
                    selectorParts[i] = prepareStringReplaceVar(selectorParts[i]);
                    for (int j = 0; j < allObjects.size(); j++) {
                        if (!manager.hasTag(allObjects.get(j), selectorParts[i].replaceAll(".+:", ""))) {
                            allObjects.remove(j);
                            j--;
                        }
                    }
                } else if (selectorParts[i].startsWith("location:")) {
                    selectorParts[i] = prepareStringReplaceVar(selectorParts[i]);
                    for (int j = 0; j < allObjects.size(); j++) {
                        if (!manager.getLocationFromNPC(allObjects.get(j)).equals(selectorParts[i].replaceAll(".+:", ""))) {
                            allObjects.remove(j);
                            j--;
                        }
                    }
                } else if (selectorParts[i].startsWith("sort:")) {
                    selectorParts[i] = prepareStringReplaceVar(selectorParts[i]);
                    sortType = selectorParts[i].replaceAll(".+:", "");
                } else if (selectorParts[i].startsWith("limit:")) {
                    selectorParts[i] = prepareStringReplaceVar(selectorParts[i]);
                    while (allObjects.size() > Integer.parseInt(selectorParts[i].replaceAll(".+:", ""))) {
                        switch (sortType) {
                            case "random" -> allObjects.remove(StaticStuff.randomNumber(0, allObjects.size() - 1));
                            case "last" -> allObjects.remove(0);
                            case "first" -> allObjects.remove(allObjects.size() - 1);
                        }
                    }
                } else if (selectorParts[i].startsWith("name:")) {
                    selectorParts[i] = prepareStringReplaceVar(selectorParts[i]);
                    for (int j = 0; j < allObjects.size(); j++) {
                        if (!Manager.getName(allObjects.get(j)).equals(selectorParts[i].replaceAll(".+:", ""))) {
                            allObjects.remove(j);
                            j--;
                        }
                    }
                } else if (selectorParts[i].startsWith("expr:")) {
                    String comp = selectorParts[i].replaceAll("expr:.+(==|>|<|>=|<=|!=).+", "$1"), value1 = selectorParts[i].replaceAll("expr:(.+)(?:==|>|<|>=|<=|!=).+", "$1"),
                            value2 = selectorParts[i].replaceAll("expr:.+(?:==|>|<|>=|<=|!=)(.+)", "$1");
                    String v1 = "", v2 = "";
                    if (!value1.contains("{this}")) v1 = getValueOrVar(value1, "");
                    if (!value2.contains("{this}")) v2 = getValueOrVar(value2, "");
                    for (int j = 0; j < allObjects.size(); j++) {
                        if (value1.equals("")) v1 = getValueOrVar(value1, allObjects.get(j));
                        if (value2.equals("")) v2 = getValueOrVar(value2, allObjects.get(j));
                        if (v1.equals("-969657") || v2.equals("-969657")) {
                            if (comp.equals("==")) {
                                if (!(value1.equals(value2))) {
                                    allObjects.remove(j);
                                    j--;
                                }
                            } else {
                                allObjects.remove(j);
                                j--;
                            }
                        } else if (comp.equals("==")) {
                            if (!(v1.equals(v2))) {
                                allObjects.remove(j);
                                j--;
                            }
                        } else if (comp.equals(">")) {
                            if (!(Integer.parseInt(v1) > Integer.parseInt(v2))) {
                                allObjects.remove(j);
                                j--;
                            }
                        } else if (comp.equals("<")) {
                            if (!(Integer.parseInt(v1) < Integer.parseInt(v2))) {
                                allObjects.remove(j);
                                j--;
                            }
                        } else if (comp.equals(">=")) {
                            if (!(Integer.parseInt(v1) >= Integer.parseInt(v2))) {
                                allObjects.remove(j);
                                j--;
                            }
                        } else if (comp.equals("<=")) {
                            if (!(Integer.parseInt(v1) <= Integer.parseInt(v2))) {
                                allObjects.remove(j);
                                j--;
                            }
                        } else if (comp.equals("!=")) {
                            if (Integer.parseInt(v1) == Integer.parseInt(v2)) {
                                allObjects.remove(j);
                                j--;
                            }
                        }
                    }
                } else if (selectorParts[i].startsWith("type:")) {
                    selectorParts[i] = prepareStringReplaceVar(selectorParts[i]);
                    for (int j = 0; j < allObjects.size(); j++) {
                        if (!manager.isType(allObjects.get(j), selectorParts[i].replaceAll(".+:", ""))) {
                            allObjects.remove(j);
                            j--;
                        }
                    }
                } else if (selectorParts[i].startsWith("inventory:")) {
                    selectorParts[i] = prepareStringReplaceVar(selectorParts[i].replace("inventory:", ""));
                    for (int j = 0; j < allObjects.size(); j++) {
                        if (!manager.inventoryContains(allObjects.get(j), selectorParts[i])) {
                            allObjects.remove(j);
                            j--;
                        }
                    }
                }
            }
            Object[] ret = allObjects.toArray();
            result = new String[ret.length];
            for (int i = 0; i < ret.length; i++) result[i] = ret[i].toString();
        }
        if (result.length == amount) Log.add("Result of selector: all objects (" + amount + ")");
        else if (result.length == 0) Log.add("Result of selector: no objects");
        else for (String s : result) Log.add("Result of selector: " + s);
        Log.removeIndent();
        return result;
    }

    public String getValueOrVar(String v, String uid) {
        if (v.matches("(-?\\+?)?\\d+(\\.\\d+)?")) return v;
        else {
            String replaced = prepareStringReplaceVar(v);
            if (StaticStuff.isValidUIDSilent(replaced)) {
                if (manager.hasLocalVariableByUID(uid, replaced)) return Manager.getLocalVariableByUID(replaced);
                if (Manager.variableExists(replaced)) return Manager.getVariableValueByUID(replaced);
            } else {
                if (manager.hasLocalVariableByName(uid, replaced)) return Manager.getLocalVariableByName(uid, replaced);
                if (Manager.variableExists(replaced)) return Manager.getVariableValueByName(replaced);
            }
            if (replaced.contains("'")) return replaced.replaceAll("'(.+)'", "$1");
            if (replaced.equals(v))
                return "-969657";
            else return replaced;
        }
    }

    public String[] evaluateVariableWithVariablesInside(String s) {
        return evaluateVariable("{" + prepareStringReplaceVar(s.substring(1)));
    }

    public String prepareStringReplaceVar(String s) {
        String currentMatch, recMatch, result[];
        s = s.replace("\\{", "ESCAPEDCURVEDBRACKETSOPEN").replace("\\}", "ESCAPEDCURVEDBRACKETSCLOSED");
        while (s.matches("(.*\\{[^\\{]+\\}(?:\\.[a-zA-Z]+\\([^)]*\\))*.*)*") && s.length() > 0) {
            currentMatch = s.replaceAll(".*(\\{[^\\{\\}]+\\}(?:\\.[a-zA-Z]+\\([^)]*\\))*).*", "$1"); //.*(\\{[^\\{]+\\}(?:\\.[a-zA-Z]+\\([^)]*\\))*).*
            while (currentMatch.substring(1).replaceAll(".*(\\{[^\\{\\}]+\\}(?:\\.[a-zA-Z]+\\([^)]*\\))*)?.*", "$1").length() > 0) { //.*(\\{[^\\{]+\\}(?:\\.[a-zA-Z]+\\([^)]*\\))*)?.*
                recMatch = currentMatch.substring(1).replaceAll(".*(\\{[^\\{\\}]+\\}(?:\\.[a-zA-Z]+\\([^)]*\\))*)?.*", "$1"); //.*(\\{[^\\{]+\\}(?:\\.[a-zA-Z]+\\([^)]*\\))*)?.*
                currentMatch = currentMatch.replace(recMatch, prepareStringReplaceVar(recMatch));
            }
            result = evaluateVariable(currentMatch);
            if (result.length > 0) s = s.replace(currentMatch, result[0]);
            else s = s.replace(currentMatch, "");
        }
        return s;
    }

    public String[] evaluateVariable(String s) {
        try {
            String[] results = null;

            String base = s.replaceAll("\\{([^\\{]+)\\}.*", "$1").replaceAll("\\\\\\|", "ESCAPEDSPLITTER").replace("\\{", "ESCAPEDCURVEDBRACKETSOPEN").replace("\\}", "ESCAPEDCURVEDBRACKETSCLOSED");
            String[] baseParam = base.split("\\|");
            for (int i = 0; i < baseParam.length; i++)
                baseParam[i] = baseParam[i].replace("ESCAPEDSPLITTER", "|");

            String modif = "", modifiers[] = null;
            if (s.matches("\\{[^\\{]+\\}.+")) {
                modif = s.replaceAll("\\{[^\\{]+\\}(.*)", "$1").replaceAll("\\\\\\|", "ESCAPEDSPLITTER").replaceFirst("\\.", "");
                modifiers = modif.split("\\.");
            }

            if (baseParam[0].equals("lastInput")) {
                results = StaticStuff.lastInput.split("LINEBREAK");
            } else if (baseParam[0].equals("inBattle")) {
                results = new String[]{"" + battleActive};
            } else if (baseParam[0].equals("random")) {
                results = new String[]{"" + StaticStuff.randomNumber(Integer.parseInt(baseParam[1]), Integer.parseInt(baseParam[2]))};
            } else if (baseParam.length == 2 && baseParam[0].equals("player")) {
                if (baseParam[0].equals("player") && baseParam[1].equals("overloaded"))
                    results = new String[]{"" + manager.isPlayerInventoryOverloaded()};
                else results = player.getValue(baseParam[1]).split("LINEBREAK");
            } else if (baseParam[0].equals("project")) {
                results = settings.getValue(baseParam[1]).split("LINEBREAK");
            } else if (Manager.variableExists(baseParam[0])) {
                String variableValue = Manager.getVariableValueByName(baseParam[0]);
                if (variableValue.equals("")) variableValue = Manager.getVariableValueByUID(baseParam[0]);
                results = variableValue.split("LINEBREAK");
            } else if (baseParam[0].equals("selector")) {
                results = evaluateSelector(baseParam[1]);
            } else if (baseParam[0].equals("input")) {
                if (baseParam[1].equals("text") && baseParam.length == 4)
                    results = new String[]{StaticStuff.openPopup(baseParam[2], baseParam[3])};
                else if (baseParam[1].equals("text") && baseParam.length == 3)
                    results = new String[]{StaticStuff.openPopup(baseParam[2], "")};
                else if (baseParam[1].equals("dropDown"))
                    results = new String[]{StaticStuff.openPopup(baseParam[2], baseParam[3].split(";"), "")};
                else if (baseParam[1].equals("button")) {
                    if (baseParam.length == 4)
                        results = new String[]{"" + StaticStuff.openPopup(baseParam[2], baseParam[3].split(";"))};
                    else if (baseParam.length == 3)
                        results = new String[]{"" + StaticStuff.openPopup(baseParam[2].split(";"), false)};
                } else if (baseParam[1].equals("dice"))
                    results = new String[]{"" + StaticStuff.rollDice(baseParam[2], Integer.parseInt(baseParam[3]), Integer.parseInt(baseParam[4]), Boolean.parseBoolean(baseParam[5]))};
                else if (baseParam[1].equals("line")) results = StaticStuff.waitForLineInput().split("LINEBREAK");
            } else if (baseParam[0].equals("value")) {
                results = new String[]{baseParam[1]};
            } else if (baseParam[0].equals("web")) {
                if (permissionWeb) results = FileManager.getStringArrayFromURL(baseParam[1]);
                else results = new String[]{"Missing permission 'web'"};
            } else if (baseParam[0].equals("file")) {
                if (baseParam[1].charAt(0) == '{') { //file from advfiles folder
                    if (baseParam[1].contains(".."))
                        if (permissionFileReadAnywhere) results = FileManager.readFile("res/advfiles/" + baseParam[1]);
                        else results = new String[]{"Missing permission 'filereadanywhere'"};
                    else if (permissionFileRead || permissionFileReadAnywhere)
                        results = FileManager.readFile("res/advfiles/" + baseParam[1]);
                    else results = new String[]{"Missing permission 'fileread'"};
                } else { //fileObject
                    String obj = evaluateSelector(baseParam[1])[0];
                    if (manager.isType(obj, "fileObject")) {
                        results = manager.getFileObjectAsTextArray(obj);
                    } else {
                        Log.add(obj + " is not a fileObject");
                        results = new String[]{};
                    }
                }
            } else if (baseParam[0].equals("amount")) {
                results = new String[]{"" + manager.inventoryGetAmount(evaluateSelector(baseParam[1])[0], evaluateSelector(baseParam[2])[0])};
            } else if (baseParam[0].equals("empty") || baseParam[0].equals("null")) {
                results = new String[]{};
            } else if (baseParam[0].equals("currentbattle")) {
                if (currentBattleMap != null)
                    results = new String[]{currentBattleMap.uid};
                else results = new String[]{""};
            } else if (baseParam[0].equals("talent")) { //{talent|[VALUE(talentname)]|[VALUE(dc)]|[true;false(visible)]|[true;false(autoroll)]|[VALUE(message);none]}
                results = new String[]{"" + rollTalent(baseParam[1], baseParam[2], baseParam[3], baseParam[4], baseParam[5])};
            } else if (baseParam[0].equals("popup")) {
                if (baseParam[1].equals("names")) {
                    results = manager.getCurrentCustomPopupNames();
                } else if (baseParam[1].equals("uids")) {
                    results = manager.getCurrentCustomPopupUIDs();
                }
            }

            if (modifiers != null)
                for (String modifier : modifiers) {
                    String modifierName = modifier.replaceAll("([a-zA-Z]+)\\([^\\)]*\\)", "$1");
                    String[] modifierParam = modifier.replaceAll("[a-zA-Z]+\\(([^\\)]*)\\)", "$1").split("\\|");
                    for (int j = 0; j < modifierParam.length; j++)
                        modifierParam[j] = modifierParam[j].replace("ESCAPEDSPLITTER", "|");

                    if (modifierName.equals("charAt")) {
                        int index = Integer.parseInt(modifierParam[0]);
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + results[j].charAt(index);
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("toUppercase")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = results[j].toUpperCase();
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("toLowercase")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = results[j].toLowerCase();
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("contains")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + results[j].contains(modifierParam[0].replace("%1", results[j]).replace("%2", "" + j));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("matches")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + results[j].replace("ESCAPEDCURVEDBRACKETSOPEN", "{").replace("ESCAPEDCURVEDBRACKETSCLOSED", "}").matches(modifierParam[0].replace("%1", results[j].replace("ESCAPEDCURVEDBRACKETSOPEN", "{").replace("ESCAPEDCURVEDBRACKETSCLOSED", "}")).replace("%2", "" + j));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("equals")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + results[j].equals(modifierParam[0].replace("%1", results[j]).replace("%2", "" + j));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("endsWith")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + results[j].endsWith(modifierParam[0].replace("%1", results[j]).replace("%2", "" + j));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("replace")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = results[j].replace(modifierParam[0].replace("%1", results[j]).replace("%2", "" + j), modifierParam[1].replace("%1", results[j]).replace("%2", "" + j));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("replaceAll")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = results[j].replace("ESCAPEDCURVEDBRACKETSOPEN", "{").replace("ESCAPEDCURVEDBRACKETSCLOSED", "}").replaceAll(modifierParam[0].replace("%1", results[j].replace("ESCAPEDCURVEDBRACKETSOPEN", "{").replace("ESCAPEDCURVEDBRACKETSCLOSED", "}")).replace("%2", "" + j), modifierParam[1].replace("%1", results[j]).replace("%2", "" + j));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("indexOf")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + results[j].indexOf(modifierParam[0].replace("%1", results[j]).replace("%2", "" + j));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("indexOfInList")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                if (results[j].equals(modifierParam[0].replace("%1", results[j]).replace("%2", "" + j)))
                                    results = new String[]{"" + j};
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("length")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + results[j].length();
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("substring")) {
                        int from = Integer.parseInt(modifierParam[0]), to = Integer.parseInt(modifierParam[1]);
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + results[j].substring(from, to);
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("name")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = Manager.getEntity(results[j]).name;
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("description")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = Manager.getEntity(results[j]).description;
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("location")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = ((NPC) Manager.getEntity(results[j])).location;
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("image")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = Manager.getEntity(results[j]).image;
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("inventory")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = Manager.getEntity(results[j]).getInventory();
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("type")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = manager.getTypeByUID(results[j]);
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("isType")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + manager.getTypeByUID(results[j]).equals(modifierParam[0].replace("%1", results[j]).replace("%2", "" + j));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("variable")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = Manager.getEntity(results[j]).getVariableValue(modifierParam[0].replace("%1", results[j]).replace("%2", "" + j));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("round")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + Math.round(Float.parseFloat(results[j]));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("math")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + StaticStuff.evaluateMathExpression(modifierParam[0].replace("%1", results[j]).replace("%2", "" + j).replace("[", "(").replace("]", ")"));
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("evalMath")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + StaticStuff.evaluateMathExpression(results[j]);
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("get")) {
                        try {
                            if (modifierParam[0].equals("index")) {
                                results = new String[]{results[Integer.parseInt(modifierParam[1])]};
                                continue;
                            } else if (modifierParam[0].equals("random") && modifierParam.length == 1) {
                                results = new String[]{results[StaticStuff.randomNumber(0, results.length - 1)]};
                                continue;
                            } else if (modifierParam[0].equals("random") && modifierParam.length == 3) {
                                results = new String[]{results[StaticStuff.randomNumber(Integer.parseInt(modifierParam[1]), Integer.parseInt(modifierParam[2]))]};
                                continue;
                            }
                            int count = 0, count2 = 0;
                            String[] picked = new String[results.length];
                            if (modifierParam[0].equals("equals")) for (int j = 0; j < results.length; j++)
                                if (results[j].equals(modifierParam[1].replace("%1", results[j]).replace("%2", "" + j))) {
                                    picked[count] = results[j];
                                    count++;
                                }
                            if (modifierParam[0].equals("contains")) for (int j = 0; j < results.length; j++)
                                if (results[j].contains(modifierParam[1].replace("%1", results[j]).replace("%2", "" + j))) {
                                    picked[count] = results[j];
                                    count++;
                                }
                            if (modifierParam[0].equals("matches")) for (int j = 0; j < results.length; j++)
                                if (results[j].replace("ESCAPEDCURVEDBRACKETSOPEN", "{").replace("ESCAPEDCURVEDBRACKETSCLOSED", "}").matches(modifierParam[1].replace("%1", results[j].replace("ESCAPEDCURVEDBRACKETSOPEN", "{").replace("ESCAPEDCURVEDBRACKETSCLOSED", "}")).replace("%2", "" + j))) {
                                    picked[count] = results[j];
                                    count++;
                                }
                            results = new String[count];
                            for (int j = 0; j < count; j++) {
                                if (picked[j] != null) {
                                    results[count2] = picked[j];
                                    count2++;
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    } else if (modifierName.equals("remove")) {
                        try {
                            int count = results.length, count2 = 0, unpick = 0, origCount = count;
                            String[] picked = new String[results.length];
                            if (modifierParam[0].equals("index")) {
                                unpick = Integer.parseInt(modifierParam[1]);
                                for (int j = 0; j < results.length; j++)
                                    if (j != unpick) picked[j] = results[j];
                                count--;
                            } else if (modifierParam[0].equals("random") && modifierParam.length == 1) {
                                unpick = StaticStuff.randomNumber(0, results.length - 1);
                                for (int j = 0; j < results.length; j++)
                                    if (j != unpick) picked[j] = results[j];
                                count--;
                            } else if (modifierParam[0].equals("random") && modifierParam.length == 3) {
                                unpick = StaticStuff.randomNumber(Integer.parseInt(modifierParam[1]), Integer.parseInt(modifierParam[2]));
                                for (int j = 0; j < results.length; j++)
                                    if (j != unpick) picked[j] = results[j];
                                count--;
                            } else count = 0;
                            if (modifierParam[0].equals("equals")) for (int j = 0; j < results.length; j++)
                                if (!results[j].equals(modifierParam[1].replace("%1", results[j]).replace("%2", "" + j))) {
                                    picked[count] = results[j];
                                    count++;
                                }
                            if (modifierParam[0].equals("contains")) for (int j = 0; j < results.length; j++)
                                if (!results[j].contains(modifierParam[1].replace("%1", results[j]).replace("%2", "" + j))) {
                                    picked[count] = results[j];
                                    count++;
                                }
                            if (modifierParam[0].equals("matches")) for (int j = 0; j < results.length; j++)
                                if (!results[j].matches(modifierParam[1].replace("%1", results[j]).replace("%2", "" + j))) {
                                    picked[count] = results[j];
                                    count++;
                                }
                            results = new String[count];
                            for (int j = 0; j < origCount; j++) {
                                if (picked[j] != null) {
                                    results[count2] = picked[j];
                                    count2++;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (modifierName.equals("append")) {
                        try {
                            String[] appendValues;
                            String modifierList = modifier.replaceAll("[a-zA-Z]+\\(([^\\)]*)\\)", "$1");//.replaceAll("[^\\|]+\\|(.+)","$1");
                            appendValues = evaluateVariable("{" + modifierList + "}");
                            if (appendValues.length == 0) appendValues = new String[]{modifierList};
                            String[] oldResults = new String[results.length];
                            if (results.length >= 0) System.arraycopy(results, 0, oldResults, 0, results.length);
                            results = new String[results.length + appendValues.length];
                            if (oldResults.length >= 0) System.arraycopy(oldResults, 0, results, 0, oldResults.length);
                            int counter = 0;
                            for (int j = oldResults.length; j < appendValues.length + oldResults.length; j++) {
                                results[j] = appendValues[counter];
                                counter++;
                            }
                        } catch (Exception ignored) {
                        }
                    } else if (modifierName.equals("count")) {
                        results = new String[]{"" + results.length};
                    } else if (modifierName.equals("average")) {
                        float sum = 0;
                        for (String result : results) sum += Float.parseFloat(result);
                        if (modifierParam[0].equals("float"))
                            results = new String[]{"" + (sum / Float.parseFloat(results.length + ""))};
                        else if (modifierParam[0].equals("round"))
                            results = new String[]{"" + Math.round(sum / Float.parseFloat(results.length + ""))};
                    } else if (modifierName.equals("sum")) {
                        float sum = 0;
                        for (String result : results) sum += Float.parseFloat(result);
                        if (modifierParam[0].equals("float")) results = new String[]{"" + sum};
                        else if (modifierParam[0].equals("round")) results = new String[]{"" + Math.round(sum)};
                    } else if (modifierName.equals("min")) {
                        float min = 999999;
                        for (String result : results)
                            if (min > Float.parseFloat(result)) min = Float.parseFloat(result);
                        if (modifierParam[0].equals("float")) results = new String[]{"" + min};
                        else if (modifierParam[0].equals("round")) results = new String[]{"" + Math.round(min)};
                    } else if (modifierName.equals("max")) {
                        float max = -999999;
                        for (String result : results)
                            if (max < Float.parseFloat(result)) max = Float.parseFloat(result);
                        if (modifierParam[0].equals("float")) results = new String[]{"" + max};
                        else if (modifierParam[0].equals("round")) results = new String[]{"" + Math.round(max)};
                    } else if (modifierName.equals("sort")) {
                        StaticStuff.sortArray(results);
                    } else if (modifierName.equals("set")) {
                        int index = Integer.parseInt(modifierParam[0]);
                        results[index] = modifierParam[1].replace("%1", results[index]).replace("%2", "" + index);
                    } else if (modifierName.equals("remove")) {
                        String[] oldResults = new String[results.length];
                        for (int j = 0; j < results.length; j++)
                            oldResults[j] = results[j].replace("%1", results[j]).replace("%2", "" + j);
                        results = new String[results.length - 1];
                        int add = 0, remove = Integer.parseInt(modifierParam[0]);
                        for (int j = 0; j < results.length; j++) {
                            if (remove == j) add = 1;
                            results[j] = oldResults[j + add];
                        }
                    } else if (modifierName.equals("string")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = modifierParam[0].replace("%1", results[j]).replace("%2", "" + j);
                            } catch (Exception ignored) {
                            }
                    } else if (modifierName.equals("battleInfo") && results.length > 0) {
                        if (results[0].length() == 0) results = new String[]{"battle map does not exist"};
                        else if (modifierParam.length == 1)
                            results = ((BattleMap) Manager.getEntity(results[0])).getBattleInfo(modifierParam[0], "uid");
                        else if (modifierParam.length == 2)
                            results = ((BattleMap) Manager.getEntity(results[0])).getBattleInfo(modifierParam[0], modifierParam[1]);
                    } else if (modifierName.equals("split")) {
                        if (results[0].length() == 0) results = new String[]{};
                        else if (modifierParam.length == 1)
                            results = results[0].split(modifierParam[0]);
                    } else if (modifierName.equals("isUID")) {
                        for (int j = 0; j < results.length; j++)
                            try {
                                results[j] = "" + StaticStuff.isValidUIDSilent(results[j]);
                            } catch (Exception ignored) {
                            }
                    }
                }

            if (results == null) results = new String[]{};
            Log.add((results.length > 1 ? "Results " : "Result ") + "of expression '" + s + "':");
            Log.addIndent();
            for (String result : results) Log.add(result);
            Log.removeIndent();

            return results;
        } catch (Exception e) {
            Log.add("An error occured while solving variable expression '" + s + "'");
            e.printStackTrace();
            return new String[]{};
        }
    }

    /*private String isolateSelector(String selector) {
        while ((selector.length() - selector.replace("#", "").length()) != 2)
            selector = selector.replaceAll("(#.+#).+", "$1");
        return selector;
    }*/

    public String getSetting(String settingName) {
        return settings.getValue(settingName);
    }

    public void addAutoCompleteWords(String words) {
        console.addAutoCompleteWords(words.split(","));
    }

    public void showPossiblePlayerCommands() {
        executeEventFromObject(manager.getGeneralEventCollection().getUID(), "showAvailableCommands", new String[]{});
    }

    public static String lang(String textName) {
        if (lang == null) {
            lang = new Language("res/lang", true);
        }
        return lang.get(textName);
    }

    public static String lang(String textName, String param1) {
        if (lang == null) {
            lang = new Language("res/lang", true);
        }
        return lang.get(textName, param1);
    }

    public static String lang(String textName, String param1, String param2) {
        if (lang == null) {
            lang = new Language("res/lang", true);
        }
        return lang.get(textName, param1, param2);
    }

    public static String lang(String textName, String param1, String param2, String param3) {
        if (lang == null) {
            lang = new Language("res/lang", true);
        }
        return lang.get(textName, param1, param2, param3);
    }

    public static String getLanguage() {
        return lang.selectedLangStr;
    }

    private static String[] objectFrameVariables = null;

    public static String[] getObjectFrameVariables() {
        if (objectFrameVariables == null) {
            objectFrameVariables = self.settings.getValue("objectFrameVariables").split(",");
        }
        return objectFrameVariables;
    }

    //scale stuff
    private static double sizeScale = 100;
    private static double userSizeScale = 100;

    public static void setSizeScale(double newSize) {
        sizeScale = Math.ceil(newSize);
        StaticStuff.recalculatePixelatedFont();
    }

    public static float getSizeScale() {
        return (float) ((sizeScale / 100) * (userSizeScale / 100));
    }

    public static void setUserSizeScale(double newSize) {
        userSizeScale = Math.ceil(newSize);
        StaticStuff.recalculatePixelatedFont();
    }

    public static int getScaledValue(int value) {
        if (sizeScale == 100 && userSizeScale == 100) return value;
        return (int) ((((Float.parseFloat(value + "") / 100f) * sizeScale) / 100f) * userSizeScale);
    }

    public static float getScaledValue(float value) {
        if (sizeScale == 100 && userSizeScale == 100) return value;
        return (float) ((((value / 100f) * sizeScale) / 100f) * userSizeScale);
    }

    public static int getInvertedScaledValue(int value) { // 50 = 200; 70 = 142
        if (sizeScale == 100 && userSizeScale == 100) return value;
        float inverted = (1f / getSizeScale());
        return (int) (Float.parseFloat(value + "") * inverted);
    }

    //debugging:
    /*private static final ArrayList<Long> times = new ArrayList<>();
    public static long start = 0, finish;

    public static void startTimer() {
        start = System.nanoTime();
    }

    public static void stopTimer() {
        long finish = System.nanoTime();
        times.add(finish - start);
        //System.out.println(times.get(times.size() - 1) + "  " + averageTime());
        System.out.println(times.get(times.size() - 1));
    }

    public static float averageTime() {
        float total = 0;
        for (Long f : times) total += f;
        total = total / times.size();
        return total;
    }*/

    //arguments
    private int textSpeedFactor = 100;
    private String selectedArgsLang = null;
    private boolean forceDebugMode = false;
    private String argsFilename = "";

    private void prepareArgs(String[] args) {
        for (String param : args) {
            try {
                String name = param.replaceAll("([^:]+):.+", "$1");
                String value = param.replaceAll("([^:]+):(.+)", "$2");
                switch (name) {
                    case "lang" -> selectedArgsLang = value;
                    case "scale" -> userSizeScale = Integer.parseInt(value);
                    case "forceDebug" -> forceDebugMode = value.equals("true");
                    case "textSpeed" -> textSpeedFactor = Integer.parseInt(value);
                    case "filename" -> argsFilename = value;
                }
            } catch (Exception e) {
                Popup.error("An error occurred", "Unable to setup arguments:\n" + e);
                e.printStackTrace();
            }
        }
    }

    //main()
    public static void main(String[] args) {
        try {
            Interpreter interpreter = new Interpreter();
            interpreter.prepareArgs(args);
            interpreter.setup();
        } catch (Exception e) {
            e.printStackTrace();
            Popup.error("An error occurred", "Unable to launch player:\n" + e);
        }
    }
}
