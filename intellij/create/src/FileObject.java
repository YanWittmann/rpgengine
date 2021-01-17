
public class FileObject extends Entity {
    private byte[] fileData = null;

    public FileObject() {
        name = "New File";
        description = "Description";
    }

    public FileObject(String name, String desc) {
        this.name = name;
        description = desc;
    }

    public FileObject(String name, byte array[]) {
        this.name = name;
        description = "A " + name.replaceAll("[^\\.]+\\.(.+)", "$1") + " file";
        fileData = array;
    }

    public FileObject(String[] fileInput, byte array[]) {
        try {
            fileData = array;
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
            e.printStackTrace();
            Popup.error(StaticStuff.projectName, "FileObject '" + name + "' contains invalid data.");
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

    public int additionalRefactor(String find, String replace) {
        return 0;
    }

    public byte[] getByteArray() {
        return fileData;
    }

    public void openEditor() {
        new GuiEntityEditor(this, new String[]{"Name", "Description", "Add event", "Edit event", "Remove event", "Edit tags", "Edit variables", "Open file", "Edit file"}, "File") {
            @Override
            public void buttonPressed(int index) {
                String str;
                int choice;
                switch (index) {
                    case 0:
                        str = Popup.input("New name:", name);
                        if (str == null) return;
                        if (str.equals("")) return;
                        name = str;
                        break;
                    case 1:
                        str = Popup.input("New description:", description);
                        if (str == null) return;
                        if (str.equals("")) return;
                        description = str;
                        break;
                    case 2:
                        str = Popup.input("Event name:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        addEvent(str);
                        break;
                    case 3:
                        str = Popup.input("Choose an event by its ID:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        new GuiActionEditor(getEntity(), Integer.parseInt(str));
                        break;
                    case 4:
                        str = Popup.input("Choose an event by its ID:", "");
                        if (str == null) return;
                        if (str.equals("")) return;
                        deleteEvent(Integer.parseInt(str));
                        break;
                    case 5:
                        choice = Popup.selectButton(StaticStuff.projectName, "What do you want to do?", new String[]{"Add tag", "Remove tag", "Edit tag"});
                        if (choice == 0)
                            getEntity().addTag(Popup.input("Tag name:", ""));
                        else if (choice == 1)
                            getEntity().deleteTag(Integer.parseInt(Popup.input("Tag index:", "")));
                        else if (choice == 2)
                            getEntity().editTag(Integer.parseInt(Popup.input("Tag index:", "")), Popup.input("Tag name:", ""));
                        break;
                    case 6:
                        choice = Popup.selectButton(StaticStuff.projectName, "What do you want to do?", new String[]{"Add variable", "Remove variable", "Edit variable"});
                        if (choice == 0) {
                            getEntity().addVariable(Popup.input("Variable name:", ""), true);
                        } else if (choice == 1)
                            getEntity().removeVariable(StaticStuff.autoDetectUID("Variable uid:"));
                        else if (choice == 2)
                            getEntity().openVariable(StaticStuff.autoDetectUID("Variable uid:"), true);
                        break;
                    case 7:
                        FileManager.writeFileFromByteArray("res/tmp/" + name, fileData);
                        FileManager.openFile("res/tmp/" + name);
                        break;
                    case 8:
                        String name = FileManager.filePicker();
                        if (name == null) return;
                        if (!FileManager.fileExists(name)) {
                            Popup.error("File", "This file does not exist.");
                            return;
                        }
                        setFileData(FileManager.readFileToByteArray(name));
                        setName(FileManager.getFilename(name));
                        setDescription(FileManager.getFilename(name));
                        break;
                    default:
                        Popup.error(StaticStuff.projectName, "Invalid action\nButton " + index + " does not exist.");
                }
                update();
            }
        };
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setDescription(String description) {
        this.description = "A " + description.replaceAll("[^\\.]+\\.(.+)", "$1") + " file";
    }

    protected void setFileData(byte[] array) {
        fileData = array;
    }
}