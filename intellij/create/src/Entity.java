
import java.util.ArrayList;

public abstract class Entity {
    String uid;
    String name;
    String description;
    ArrayList<String> eventName = new ArrayList<String>();
    ArrayList<String> eventCode = new ArrayList<String>();
    ArrayList<String> tags = new ArrayList<String>();
    protected ArrayList<String> localVarUids = new ArrayList<String>();
    protected ArrayList<String> localVarName = new ArrayList<String>();
    protected ArrayList<String> localVarType = new ArrayList<String>();
    protected ArrayList<String> localVarValue = new ArrayList<String>();

    public Entity() {
        uid = UID.generateUID();
        try {
            StaticStuff.copyString(uid);
        } catch (Exception e) {
        }
    }

    public String generateEventEditorString(String str) {
        return str.replace(",,,", "\n");
    }

    public void setEventsFromEditor(int eventIndex, String code) {
        if (code.equals("")) code = "none";
        eventCode.set(eventIndex, code.replace("\n", ",,,"));
    }

    public void addEvent(String name) {
        if (name == null) return;
        if (!eventName.contains(name)) {
            eventName.add(name);
            eventCode.add("Code for event '" + name + "' in " + uid);
            if (Manager.openActionEditor) new GuiActionEditor(this, eventName.size() - 1);
        } else Popup.error(StaticStuff.projectName + " - Error", "This event already exists");
    }

    public void deleteEvent(int index) {
        if (index != -1) {
            eventName.remove(index);
            eventCode.remove(index);
        } else Popup.error(StaticStuff.projectName + " - Error", "This event does not exist");
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

    public void addVariable(String name, boolean open) {
        localVarUids.add(UID.generateUID());
        localVarName.add(name);
        localVarType.add("String");
        localVarValue.add("Hello world");
        if (open) {
            openVariable(localVarUids.get(localVarUids.size() - 1), false);
            StaticStuff.copyString(localVarUids.get(localVarUids.size() - 1));
        }
    }

    public void addVariable(String name, String type, String value, boolean open) {
        localVarUids.add(UID.generateUID());
        localVarName.add(name);
        localVarType.add(type);
        localVarValue.add(value);
        if (open) {
            openVariable(localVarUids.get(localVarUids.size() - 1), false);
            StaticStuff.copyString(localVarUids.get(localVarUids.size() - 1));
        }
    }

    public void setVariable(String uid, String name, String type, String value) {
        if (StaticStuff.isValidUID(uid))
            if (localVarUids.contains(uid)) {
                int index = localVarUids.indexOf(uid);
                localVarName.set(index, name);
                localVarType.set(index, type);
                localVarValue.set(index, value);
            } else Popup.error(StaticStuff.projectName, "This variable does not exist.");
    }

    public void removeVariable(String uid) {
        if (StaticStuff.isValidUID(uid))
            if (localVarUids.contains(uid)) {
                int index = localVarUids.indexOf(uid);
                localVarUids.remove(index);
                localVarName.remove(index);
                localVarType.remove(index);
                localVarValue.remove(index);
            } else Popup.error(StaticStuff.projectName, "This variable does not exist.");
    }

    public boolean openVariable(String uid, boolean confirmationRequired) {
        if (!confirmationRequired) new GuiLocalVariables(this, uid);
        else {
            if (StaticStuff.isValidUID(uid))
                if (localVarUids.contains(uid)) {
                    new GuiLocalVariables(this, uid);
                    return true;
                } else Popup.error(StaticStuff.projectName, "This variable does not exist.");
        }
        return false;
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
            } else Popup.error(StaticStuff.projectName, "This variable does not exist.");
        return "";
    }

    public String getName() {
        return name;
    }

    public String getUID() {
        return uid;
    }

    public int refactor(String find, String replace) {
        int occ = 0;
        occ += StaticStuff.refactorArrayList(find, replace, tags);
        occ += StaticStuff.refactorArrayList(find, replace, localVarName);
        occ += StaticStuff.refactorArrayList(find, replace, localVarType);
        occ += StaticStuff.refactorArrayList(find, replace, localVarUids);
        occ += StaticStuff.refactorArrayList(find, replace, localVarValue);
        occ += StaticStuff.refactorArrayList(find, replace, eventName);
        occ += StaticStuff.refactorArrayList(find, replace, eventCode);
        occ += StaticStuff.countOccurrences(name, find);
        name = name.replace(find, replace);
        occ += StaticStuff.countOccurrences(uid, find);
        uid = uid.replace(find, replace);
        occ += StaticStuff.countOccurrences(description, find);
        description = description.replace(find, replace);
        occ += additionalRefactor(find, replace);
        return occ;
    }

    public abstract int additionalRefactor(String find, String replace);

    public abstract String generateInformation();

    public abstract void openEditor();
}
