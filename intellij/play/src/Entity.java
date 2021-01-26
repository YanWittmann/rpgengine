
import java.util.ArrayList;

public abstract class Entity {
    String uid;
    String name;
    String description;
    String image;
    String type = "unset";
    ArrayList<String> eventName = new ArrayList<>();
    ArrayList<String> eventCode = new ArrayList<>();
    ArrayList<String> tags = new ArrayList<>();
    protected ArrayList<String> localVarUids = new ArrayList<>();
    protected ArrayList<String> localVarName = new ArrayList<>();
    protected ArrayList<String> localVarType = new ArrayList<>();
    protected ArrayList<String> localVarValue = new ArrayList<>();

    public String generateEventEditorString(String str) {
        return str.replace(",,,", "\n");
    }

    public void addEvent(String name) {
        if (name == null) return;
        if (!eventName.contains(name)) {
            eventName.add(name);
            eventCode.add("Code for event '" + name + "' in " + uid);
        } else StaticStuff.error("This event already exists");
    }

    public void deleteEvent(int index) {
        if (index != -1) {
            eventName.remove(index);
            eventCode.remove(index);
        } else StaticStuff.error("This event does not exist");
    }

    public void setEvents(String fileInput[]) {
        eventName.clear();
        eventCode.clear();
        for (int i = 0; i < fileInput.length; i++) {
            if (fileInput[i].contains("++ev++")) {
                fileInput[i] = fileInput[i].replace("++ev++", "");
                eventName.add(fileInput[i].split("---")[0]);
                eventCode.add(fileInput[i].split("---")[1]);
            }
        }
    }

    public abstract String generateSaveString();

    public void addTag(String name) {
        tags.add(name);
    }

    public void deleteTag(int index) {
        tags.remove(index);
    }

    public void editTag(int index, String name) {
        tags.set(index, name);
    }

    public boolean variableExists(String nameOrUID) {
        return localVarName.contains(nameOrUID) || localVarUids.contains(nameOrUID);
    }

    public void addVariable(String name, boolean open) {
        localVarUids.add(UID.generateUID());
        localVarName.add(name);
        localVarType.add("String");
        localVarValue.add("Hello world");
    }

    public void addVariable(String name, String type, String value, boolean open) {
        localVarUids.add(UID.generateUID());
        localVarName.add(name);
        localVarType.add(type);
        localVarValue.add(value);
    }

    public void setVariableByName(String name, String value) {
        if (localVarName.contains(name)) {
            int index = localVarName.indexOf(name);
            localVarValue.set(index, value);
            GuiObjectDisplay.updateFrame(this);
        } else StaticStuff.error("This variable does not exist.");
    }

    public void setVariableByName(String name, int value) {
        if (localVarName.contains(name)) {
            int index = localVarName.indexOf(name);
            localVarValue.set(index, value + "");
            GuiObjectDisplay.updateFrame(this);
        } else StaticStuff.error("This variable does not exist.");
    }

    public void setVariableByUID(String uid, String value) {
        if (localVarUids.contains(uid)) {
            int index = localVarUids.indexOf(uid);
            localVarValue.set(index, value);
            GuiObjectDisplay.updateFrame(this);
        } else StaticStuff.error("This variable does not exist.");
    }

    public void setVariable(String uid, String name, String type, String value) {
        if (StaticStuff.isValidUID(uid))
            if (localVarUids.contains(uid)) {
                int index = localVarUids.indexOf(uid);
                localVarName.set(index, name);
                localVarType.set(index, type);
                localVarValue.set(index, value);
                GuiObjectDisplay.updateFrame(this);
            } else StaticStuff.error("This variable does not exist.");
    }

    public void removeVariable(String uid) {
        if (StaticStuff.isValidUID(uid))
            if (localVarUids.contains(uid)) {
                int index = localVarUids.indexOf(uid);
                localVarUids.remove(index);
                localVarName.remove(index);
                localVarType.remove(index);
                localVarValue.remove(index);
            } else StaticStuff.error("This variable does not exist.");
    }

    public String getVarData(int which, String uid) {
        if (StaticStuff.isValidUID(uid))
            if (localVarUids.contains(uid)) {
                int index = localVarUids.indexOf(uid);
                switch (which) {
                    case 0:
                        return localVarType.get(index);
                    case 1:
                        return localVarName.get(index);
                    case 2:
                        return localVarValue.get(index);
                }
            } else StaticStuff.error("This variable does not exist.");
        return "";
    }

    public void setVariables(String fileInput[]) {
        localVarUids.clear();
        localVarName.clear();
        localVarType.clear();
        localVarValue.clear();
        for (int i = 0; i < fileInput.length; i++) {
            if (fileInput[i].contains("++variable++")) {
                fileInput[i] = fileInput[i].replace("++variable++", "");
                localVarUids.add(fileInput[i].split("---")[0]);
                localVarName.add(fileInput[i].split("---")[1]);
                localVarType.add(fileInput[i].split("---")[2]);
                localVarValue.add(fileInput[i].split("---")[3]);
            }
        }
    }

    public String getVariableValue(String name) {
        Log.add("Getting variable " + name + " from " + this.name);
        for (int i = 0; i < localVarName.size(); i++) if (localVarName.get(i).equals(name)) return localVarValue.get(i);
        return "-1";
    }

    public String getVariableValueSilent(String name) {
        for (int i = 0; i < localVarName.size(); i++) if (localVarName.get(i).equals(name)) return localVarValue.get(i);
        return "-1";
    }

    public String getName() {
        return name;
    }

    public String getUID() {
        return uid;
    }

    public String[] getEventCode(String name) {
        int index = eventName.indexOf(name);
        if (index == -1) return new String[]{};
        return eventCode.get(index).split(",,,");
    }

    public String getInventory() {
        return uid + " cannot have an inventory";
    }
}
