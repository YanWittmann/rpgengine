public class Location extends Entity{
    String inventory, image;
    public Location(){
        name = "New Location";
        description = "Description";
        inventory = "";
        image = "";
        addEvent("entry");
        addEvent("exit");
        addEvent("walk");
        addEvent("examine");
    }

    public Location(String[] fileInput){
        try{
            name = fileInput[0];
            description = fileInput[1];
            uid = fileInput[2];
            inventory = fileInput[3];
            image = fileInput[4];
            for(int i=5;i<fileInput.length;i++){
                if(fileInput[i].contains("++ev++")){
                    fileInput[i] = fileInput[i].replace("++ev++","");
                    eventName.add(fileInput[i].split("---")[0]);
                    eventCode.add(fileInput[i].split("---")[1]);
                }else if(fileInput[i].contains("++tag++")){
                    tags.add(fileInput[i].replace("++tag++",""));
                }else if(fileInput[i].contains("++variable++")){
                    fileInput[i] = fileInput[i].replace("++variable++","");
                    localVarUids.add(fileInput[i].split("---")[0]);
                    localVarName.add(fileInput[i].split("---")[1]);
                    localVarType.add(fileInput[i].split("---")[2]);
                    localVarValue.add(fileInput[i].split("---")[3]);
                }
            }
        }catch(Exception e){Popup.error(StaticStuff.PROJECT_NAME, "Location '"+name+"' contains invalid data.");}
    }

    public String generateSaveString(){
        StringBuilder str = new StringBuilder("" + name + "\n" + description + "\n" + uid + "\n" + inventory + "\n" + image);
        for(int i=0;i<eventName.size();i++)
            str.append("\n++ev++").append(eventName.get(i)).append("---").append(eventCode.get(i));
        for (String tag : tags) str.append("\n++tag++").append(tag);
        for(int i=0;i<localVarName.size();i++)
            str.append("\n++variable++").append(localVarUids.get(i)).append("---").append(localVarName.get(i)).append("---").append(localVarType.get(i)).append("---").append(localVarValue.get(i)).append("\n");
        return str.toString();
    }

    public String generateInformation(){
        StringBuilder str = new StringBuilder("Name: " + name + "\nDescription: " + description + "\nInventory: " + inventory + "; " + Manager.getInventoryName(inventory) +
                "\nImage: " + image + "; " + Manager.getImageName(image) + "\nEvents:");
        for(int i=0;i<eventName.size();i++)
            str.append("\n   ").append(i).append(": ").append(eventName.get(i));
        str.append("\nTags:\n");
        for(int i=0;i<tags.size();i++)
            str.append(" ").append(i).append(": ").append(tags.get(i)).append(";");
        str.append("\nLocal variables:\n");
        for(int i=0;i<localVarName.size();i++)
            str.append(" ").append(localVarUids.get(i)).append(" - ").append(localVarName.get(i)).append(" - ").append(localVarType.get(i)).append(" - ").append(localVarValue.get(i)).append("\n");
        return str.toString();
    }

    public void setInventory(String uid){
        try{if(uid.equals("")) inventory = uid;}catch(Exception ignored){}
        if(!StaticStuff.isValidUID(uid)) return;
        if(Manager.inventoryDoesNotExist(uid)){
            Popup.error(StaticStuff.PROJECT_NAME, "Invalid UID: '"+uid+"'\nThis inventory does not exist."); return;
        }
        inventory = uid;
    }

    public void setImage(String uid){
        try{if(uid.equals("")) image = uid;}catch(Exception ignored){}
        if(!StaticStuff.isValidUID(uid)) return;
        if(!Manager.imageExists(uid)){
            Popup.error(StaticStuff.PROJECT_NAME, "Invalid UID: '"+uid+"'\nThis image does not exist."); return;
        }
        image = uid;
    }
    
    public int additionalRefactor(String find, String replace){
        int occ = 0;
        occ += StaticStuff.countOccurrences(image, find); image = image.replace(find, replace);
        occ += StaticStuff.countOccurrences(inventory, find); inventory = inventory.replace(find, replace);
        return occ;
    }
    
    public void openEditor(){
        new GuiEntityEditor(this, new String[]{"Name","Description","Add event","Edit event","Remove event","Edit tags","Edit variables","Inventory","Image"},"Location"){
            @Override
            public void buttonPressed(int index){
                String str; int choice;
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
                    case 7 -> setInventory(Popup.dropDown(StaticStuff.PROJECT_NAME, "Select an inventory UID", Manager.getStringArrayInventories()).replaceAll(".+ - ([^-]+)", "$1"));
                    case 8 -> setImage(Popup.dropDown(StaticStuff.PROJECT_NAME, "Select an image UID", Manager.getStringArrayImages()).replaceAll(".+ - ([^-]+)", "$1"));
                    default -> Popup.error(StaticStuff.PROJECT_NAME, "Invalid action\nButton " + index + " does not exist.");
                }
                update();
            }
        };
    }
}
