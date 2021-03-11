public class NPC extends Entity {
    String location, inventory, image;

    public NPC() {
        name = "New NPC";
        description = "Description";
        location = "";
        inventory = "";
        image = "";
        addEvent("talkTo");
        addEvent("examine");
        addVariable("health", "Integer", "50", false);
        addVariable("courage", "Integer", "10", false);
        addVariable("speed", "Integer", "4", false);
        addVariable("dmgNoWeapon", "String", "1D3 - 1", false);
        addVariable("equippedWeapon", "String", "", false);
        addVariable("armor", "Integer", "0", false);
    }

    public NPC(String[] fileInput) {
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
            Popup.error(StaticStuff.PROJECT_NAME, "Location '" + name + "' contains invalid data.");
        }
    }

    public String generateSaveString() {
        StringBuilder str = new StringBuilder("" + name + "\n" + description + "\n" + uid + "\n" + location + "\n" + inventory + "\n" + image);
        for (int i = 0; i < eventName.size(); i++)
            str.append("\n++ev++").append(eventName.get(i)).append("---").append(eventCode.get(i));
        for (String tag : tags) str.append("\n++tag++").append(tag);
        for (int i = 0; i < localVarName.size(); i++)
            str.append("\n++variable++").append(localVarUids.get(i)).append("---").append(localVarName.get(i)).append("---").append(localVarType.get(i)).append("---").append(localVarValue.get(i)).append("\n");
        return str.toString();
    }

    public String generateInformation() {
        StringBuilder str = new StringBuilder("Name: " + name + "\nDescription: " + description + "\nLocation: " + location + "; " + Manager.getLocationName(location) +
                "\nInventory: " + inventory + "; " + Manager.getInventoryName(inventory) + "\nImage: " + image + "; " + Manager.getImageName(image) + "\nEvents:");
        for (int i = 0; i < eventName.size(); i++)
            str.append("\n   ").append(i).append(": ").append(eventName.get(i));
        str.append("\nTags:\n");
        for (int i = 0; i < tags.size(); i++)
            str.append(" ").append(i).append(": ").append(tags.get(i)).append(";");
        str.append("\nLocal variables:\n");
        for (int i = 0; i < localVarName.size(); i++)
            str.append(" ").append(localVarUids.get(i)).append(" - ").append(localVarName.get(i)).append(" - ").append(localVarType.get(i)).append(" - ").append(localVarValue.get(i)).append("\n");
        return str.toString();
    }

    public void setLocation(String uid) {
        try {
            if (uid.equals("")) location = uid;
        } catch (Exception ignored) {
        }
        if (!StaticStuff.isValidUID(uid)) return;
        if (!Manager.locationExists(uid)) {
            Popup.error(StaticStuff.PROJECT_NAME, "Invalid UID: '" + uid + "'\nThis location does not exist.");
            return;
        }
        location = uid;
    }

    public void setInventory(String uid) {
        try {
            if (uid.equals("")) inventory = uid;
        } catch (Exception ignored) {
        }
        if (!StaticStuff.isValidUID(uid)) return;
        if (Manager.inventoryDoesNotExist(uid)) {
            Popup.error(StaticStuff.PROJECT_NAME, "Invalid UID: '" + uid + "'\nThis inventory does not exist.");
            return;
        }
        inventory = uid;
    }

    public void setImage(String uid) {
        try {
            if (uid.equals("")) image = uid;
        } catch (Exception ignored) {
        }
        if (!StaticStuff.isValidUID(uid)) return;
        if (!Manager.imageExists(uid)) {
            Popup.error(StaticStuff.PROJECT_NAME, "Invalid UID: '" + uid + "'\nThis image does not exist.");
            return;
        }
        image = uid;
    }

    public int additionalRefactor(String find, String replace) {
        int occ = 0;
        occ += StaticStuff.countOccurrences(image, find);
        image = image.replace(find, replace);
        occ += StaticStuff.countOccurrences(inventory, find);
        inventory = inventory.replace(find, replace);
        occ += StaticStuff.countOccurrences(location, find);
        location = location.replace(find, replace);
        return occ;
    }

    public void openEditor() {
        new GuiEntityEditor(this, new String[]{"Name", "Description", "Add event", "Edit event", "Remove event", "Edit tags", "Edit variables", "Location", "Inventory", "Image"}, "NPC") {
            @Override
            public void buttonPressed(int index) {
                String str;
                int choice;
                switch (index) {
                    case 0 -> {
                        str = Popup.input("New name:", name);
                        if (str == null) return;
                        if (str.equals("")) return;
                        name = str;
                    }
                    case 1 -> {
                        str = Popup.input("New description:", description);
                        if (str == null) return;
                        if (str.equals("")) return;
                        description = str;
                    }
                    case 2 -> {
                        str = Popup.input("Event name:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        addEvent(str);
                    }
                    case 3 -> {
                        str = Popup.input("Choose an event by its ID:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        new GuiActionEditor(getEntity(), Integer.parseInt(str));
                    }
                    case 4 -> {
                        str = Popup.input("Choose an event by its ID:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        deleteEvent(Integer.parseInt(str));
                    }
                    case 5 -> {
                        choice = Popup.selectButton(StaticStuff.PROJECT_NAME, "What do you want to do?", new String[]{"Add tag", "Remove tag", "Edit tag"});
                        if (choice == 0)
                            getEntity().addTag(Popup.input("Tag name:", ""));
                        else if (choice == 1)
                            getEntity().deleteTag(Integer.parseInt(Popup.input("Tag index:", "")));
                        else if (choice == 2)
                            getEntity().editTag(Integer.parseInt(Popup.input("Tag index:", "")), Popup.input("Tag name:", ""));
                    }
                    case 6 -> {
                        choice = Popup.selectButton(StaticStuff.PROJECT_NAME, "What do you want to do?", new String[]{"Add variable", "Remove variable", "Edit variable"});
                        if (choice == 0) {
                            getEntity().addVariable(Popup.input("Variable name:", ""), true);
                        } else if (choice == 1)
                            getEntity().removeVariable(StaticStuff.autoDetectUID("Variable uid:"));
                        else if (choice == 2)
                            getEntity().openVariable(StaticStuff.autoDetectUID("Variable uid:"), true);
                    }
                    case 7 -> ((NPC) getEntity()).setLocation(Popup.dropDown(StaticStuff.PROJECT_NAME, "Select a location UID", Manager.getStringArrayLocations()).replaceAll(".+ - ([^-]+)", "$1"));
                    case 8 -> ((NPC) getEntity()).setInventory(Popup.dropDown(StaticStuff.PROJECT_NAME, "Select an inventory UID", Manager.getStringArrayInventories()).replaceAll(".+ - ([^-]+)", "$1"));
                    case 9 -> setImage(Popup.dropDown(StaticStuff.PROJECT_NAME, "Select an image UID", Manager.getStringArrayImages()).replaceAll(".+ - ([^-]+)", "$1"));
                    default -> Popup.error(StaticStuff.PROJECT_NAME, "Invalid action\nButton " + index + " does not exist.");
                }
                update();
            }
        };
    }
}
