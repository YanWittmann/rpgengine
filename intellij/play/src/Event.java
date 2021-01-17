public class Event extends Entity {
    public Event(String[] fileInput) {
        type = "Event";
        try {
            name = fileInput[0];
            description = fileInput[1];
            uid = fileInput[2];
            for (int i = 3; i < fileInput.length; i++) {
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
                }
            }
        } catch (Exception e) {
            StaticStuff.error("Event '" + name + "' contains invalid data.");
        }
    }

    public String generateSaveString() {
        String str = "" + name + "\n" + description + "\n" + uid + "\n";
        for (int i = 0; i < eventName.size(); i++)
            str = str + "\n++ev++" + eventName.get(i) + "---" + eventCode.get(i);
        for (int i = 0; i < tags.size(); i++)
            str = str + "\n++tag++" + tags.get(i);
        for (int i = 0; i < localVarName.size(); i++)
            str = str + "\n++variable++" + localVarUids.get(i) + "---" + localVarName.get(i) + "---" + localVarType.get(i) + "---" + localVarValue.get(i) + "\n";
        return str;
    }

    public String generateInformation() {
        String str = "Name: " + name + "\nDescription: " + description + "\nEvents: ";
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
}
