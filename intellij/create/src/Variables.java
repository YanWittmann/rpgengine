
import java.util.ArrayList;

public class Variables {
    public ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> type = new ArrayList<>();
    private ArrayList<String> value = new ArrayList<>();
    private ArrayList<String> uids = new ArrayList<>();

    public Variables() {
    }

    public Variables(String[] fileInput) {
        try {
            for (int i = 0; i < fileInput.length; i++) {
                if (fileInput[i].contains("++variable++")) {
                    fileInput[i] = fileInput[i].replace("++variable++", "");
                    uids.add(fileInput[i].split("---")[0]);
                    name.add(fileInput[i].split("---")[1]);
                    type.add(fileInput[i].split("---")[2]);
                    if (fileInput[i].split("---").length == 4) value.add(fileInput[i].split("---")[3]);
                    else value.add("");
                }
            }
        } catch (Exception e) {
            try {
                Popup.error(StaticStuff.projectName, "Variable '" + name.get(name.size() - 1) + "' contains invalid data.");
            } catch (Exception e2) {
                Popup.error(StaticStuff.projectName, "A variable contains invalid data.");
            }
        }
    }

    public String generateSaveString() {
        String str = "";
        for (int i = 0; i < name.size(); i++)
            str = str + "++variable++" + uids.get(i) + "---" + name.get(i) + "---" + type.get(i) + "---" + value.get(i) + "\n";
        return str;
    }

    public String generateMenuString() {
        String str = uids.size() + " variable(s):";
        for (int i = 0; i < name.size(); i++)
            str = str + "\n" + name.get(i) + "  " + uids.get(i) + "  " + type.get(i) + "  " + value.get(i);
        return str;
    }

    public void addVariable(String name, String type, String value) {
        this.name.add(name);
        this.type.add(type);
        this.value.add(value);
        this.uids.add(UID.generateUID());
        StaticStuff.copyString(uids.get(uids.size() - 1));
    }

    public void setVariable(int index, String name, String type, String value) {
        this.name.set(index, name);
        this.type.set(index, type);
        this.value.set(index, value);
    }

    public String getName(int index) {
        return name.get(index);
    }

    public String getType(int index) {
        return type.get(index);
    }

    public String getValue(int index) {
        return value.get(index);
    }

    public boolean openVariable(String uid) {
        int index = uids.indexOf(uid);
        if (index == -1) return false;
        new GuiVariables(this, index);
        return true;
    }

    public boolean deleteVariable(String uid) {
        int index = uids.indexOf(uid);
        if (index == -1) return false;
        uids.remove(index);
        name.remove(index);
        type.remove(index);
        value.remove(index);
        return true;
    }

    public int refactor(String find, String replace) {
        int occ = 0;
        occ += StaticStuff.refactorArrayList(find, replace, name);
        occ += StaticStuff.refactorArrayList(find, replace, type);
        occ += StaticStuff.refactorArrayList(find, replace, value);
        occ += StaticStuff.refactorArrayList(find, replace, uids);
        return occ;
    }
}
