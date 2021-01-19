public class NPC extends Entity {
    String location, inventory;

    public NPC(String[] fileInput) {
        type = "NPC";
        try {
            name = fileInput[0];
            description = fileInput[1];
            uid = fileInput[2];
            location = fileInput[3];
            inventory = fileInput[4];
            image = fileInput[5];
            for (int i = 6; i < fileInput.length; i++) {
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
                    if (fileInput[i].split("---").length == 4)
                        localVarValue.add(fileInput[i].split("---")[3]);
                    else localVarValue.add("");
                }
            }
        } catch (Exception e) {
            StaticStuff.error("Location '" + name + "' contains invalid data.");
        }
    }

    public String generateSaveString() {
        StringBuilder str = new StringBuilder("" + name + "\n" + description + "\n" + uid + "\n" + location + "\n" + inventory + "\n" + image);
        for (int i = 0; i < eventName.size(); i++)
            str.append("\n++ev++").append(eventName.get(i)).append("---").append(eventCode.get(i));
        for (String tag : tags) str.append("\n++tag++").append(tag);
        for (int i = 0; i < localVarName.size(); i++)
            str.append("\n").append("++variable++").append(localVarUids.get(i)).append("---").append(localVarName.get(i)).append("---").append(localVarType.get(i)).append("---").append(localVarValue.get(i));
        return str.toString();
    }

    public String generateInformation() {
        String str = "Name: " + name + "\nDescription: " + description + "\nLocation: " + location + "; " + Manager.getLocationName(location) +
                "\nInventory: " + inventory + "; " + Manager.getInventoryName(inventory) + "\nImage: " + image + "; " + Manager.getImageName(image) + "\nEvents:";
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

    public void setLocation(String uid) {
        try {
            if (uid.equals("")) location = uid;
        } catch (Exception e) {
        }
        if (!StaticStuff.isValidUID(uid)) return;
        if (!Manager.locationExists(uid)) {
            StaticStuff.error("Invalid UID: '" + uid + "'\nThis location does not exist.");
            return;
        }
        location = uid;
    }

    public void setInventory(String uid) {
        try {
            if (uid.equals("")) inventory = uid;
        } catch (Exception e) {
        }
        if (!StaticStuff.isValidUID(uid)) return;
        if (!Manager.inventoryExists(uid)) {
            StaticStuff.error("Invalid UID: '" + uid + "'\nThis inventory does not exist.");
            return;
        }
        inventory = uid;
    }

    public void setImage(String uid) {
        try {
            if (uid.equals("")) image = uid;
        } catch (Exception e) {
        }
        if (!StaticStuff.isValidUID(uid)) return;
        if (!Manager.imageExists(uid)) {
            StaticStuff.error("Invalid UID: '" + uid + "'\nThis image does not exist.");
            return;
        }
        image = uid;
    }

    public boolean damageNPC(int damage) {
        int health = Integer.parseInt(getVariableValue("health"));
        health -= damage;
        if (health <= 0) {
            health = 0;
            setVariableByName("health", health);
            return false;
        } else {
            setVariableByName("health", health);
            return true;
        }
    }

    public String getInventory() {
        return inventory;
    }
}
