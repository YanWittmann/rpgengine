
import java.util.ArrayList;

public class Variables {
    public ArrayList<String> name = new ArrayList<String>();
    private ArrayList<String> type = new ArrayList<String>();
    private ArrayList<String> value = new ArrayList<String>();
    private ArrayList<String> uids = new ArrayList<String>();

    public Variables(String[] fileInput) {
        name.clear();
        type.clear();
        value.clear();
        uids.clear();
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
                StaticStuff.error("Variable '" + name.get(name.size() - 1) + "' contains invalid data.\n" + e);
            } catch (Exception e2) {
                StaticStuff.error("A variable contains invalid data.\n" + e);
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
            str = str + "\n" + name.get(i) + "  " + uids.get(i) + "  " + type.get(i) + "  " + value.get(i) + "\n";
        return str;
    }

    public void addVariable(String name, String type, String value) {
        this.name.add(name);
        this.type.add(type);
        this.value.add(value);
        this.uids.add(UID.generateUID());
    }

    public void setVariable(int index, String name, String type, String value) {
        this.name.set(index, name);
        this.type.set(index, type);
        this.value.set(index, value);
    }

    public String getName(int index) {
        return this.name.get(index);
    }

    public String getType(int index) {
        return this.type.get(index);
    }

    public String getValue(int index) {
        return this.value.get(index);
    }

    public String getValueByUID(String uid) {
        int index = this.uids.indexOf(uid);
        if(index == -1) return "";
        return this.value.get(index);
    }

    public String getValueByName(String name) {
        return this.value.get(this.name.indexOf(name));
    }

    public void setValueByUID(String uid, String setValue) {
        value.set(this.uids.indexOf(uid), setValue);
    }

    public void setValueByName(String name, String setValue) {
        value.set(this.name.indexOf(name), setValue);
    }

    public boolean variableExists(String nameUid) {
        return (this.uids.contains(nameUid) || this.name.contains(nameUid));
    }

    public String getVariableName(String uid) {
        if (this.uids.contains(uid))
            return name.get(this.uids.indexOf(uid));
        return "";
    }

    public String getVariableUID(String name) {
        if (this.name.contains(name))
            return this.uids.get(this.name.indexOf(name));
        return "";
    }

    public String[] getUIDs() {
        String[] ret = new String[uids.size()];
        for (int i = 0; i < uids.size(); i++) ret[i] = uids.get(i);
        return ret;
    }
}
