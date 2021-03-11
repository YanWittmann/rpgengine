import java.util.ArrayList;
public class Inventory extends Entity{
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<String> itemAmount = new ArrayList<String>();
    public Inventory(){
        name = "New Inventory";
        description = "Description";
        addEvent("pickup");
        addEvent("drop");
    }

    public Inventory(boolean isPlayerInventory){
        if(isPlayerInventory) {
            name = "PlayerInventory";
            description = "The inventory of the player of the adventure.";
        } else {
            name = "New Inventory";
            description = "Description";
        }
        addEvent("pickup");
        addEvent("drop");
    }

    public Inventory(String[] fileInput){
        try{
            name = fileInput[0];
            description = fileInput[1];
            uid = fileInput[2];
            for(int i=3;i<fileInput.length;i++){
                if(fileInput[i].contains("++ev++")){
                    fileInput[i] = fileInput[i].replace("++ev++","");
                    eventName.add(fileInput[i].split("---")[0]);
                    eventCode.add(fileInput[i].split("---")[1]);
                }else if(fileInput[i].contains("++tag++")){
                    tags.add(fileInput[i].replace("++tag++",""));
                }else if(fileInput[i].contains("++item++")){
                    fileInput[i] = fileInput[i].replace("++item++","");
                    items.add(fileInput[i].split("---")[0]);
                    itemAmount.add(fileInput[i].split("---")[1]);
                }else if(fileInput[i].contains("++variable++")){
                    fileInput[i] = fileInput[i].replace("++variable++","");
                    localVarUids.add(fileInput[i].split("---")[0]);
                    localVarName.add(fileInput[i].split("---")[1]);
                    localVarType.add(fileInput[i].split("---")[2]);
                    localVarValue.add(fileInput[i].split("---")[3]);
                }
            }
        }catch(Exception e){Popup.error(StaticStuff.PROJECT_NAME, "Conversation '"+name+"' contains invalid data.");}
    }

    public String generateSaveString(){
        String str = ""+name+"\n"+description+"\n"+uid;
        for(int i=0;i<eventName.size();i++)
            str = str + "\n++ev++" + eventName.get(i) + "---" + eventCode.get(i);
        for(int i=0;i<tags.size();i++)
            str = str + "\n++tag++" + tags.get(i);
        for(int i=0;i<localVarName.size();i++)
            str = str + "\n++variable++" + localVarUids.get(i) + "---" + localVarName.get(i) + "---" + localVarType.get(i) + "---" + localVarValue.get(i) + "\n";
        for(int i=0;i<items.size();i++)
            str = str + "\n++item++" + items.get(i) + "---" + itemAmount.get(i);
        return str;
    }

    public String generateInformation(){
        String str = "Name: "+name+"\nDescription: "+description+"\nEvents:";
        for(int i=0;i<eventName.size();i++)
            str = str + "\n   " +i+": "+ eventName.get(i);
        str = str + "\nTags:\n";
        for(int i=0;i<tags.size();i++)
            str = str + " " +i+": "+ tags.get(i)+";";
        str = str + "\nLocal variables:\n";
        for(int i=0;i<localVarName.size();i++)
            str = str + " " + localVarUids.get(i) + " - " + localVarName.get(i) + " - " + localVarType.get(i) + " - " + localVarValue.get(i) + "\n";
        str = str + "Items: "+items.size();
        return str;
    }

    public String getInventoryString(){
        removeInvalidItems();
        String str = "";
        if(items.size() >= 1) str = itemAmount.get(0) + "x " + Manager.getItemName(items.get(0)) + "; " + items.get(0);
        for(int i=1;i<items.size();i++)
            str = str + "\n" + itemAmount.get(i) + "x " + Manager.getItemName(items.get(i)) + "; " + items.get(i);
        return str;
    }

    private void removeInvalidItems(){
        for(int i=1;i<items.size();i++) if(!Manager.itemTypeExists(items.get(i))) removeItem(items.get(i));
    }

    public void addItem(String uid){
        if(!StaticStuff.isValidUID(uid)) return;
        if(!Manager.itemTypeExists(uid)){
            Popup.error(StaticStuff.PROJECT_NAME, "Invalid UID: '"+uid+"'\nThis ItemType does not exist."); return;
        }
        if(items.contains(uid)){
            Popup.error(StaticStuff.PROJECT_NAME, "Inventory already contains this ItemType: '"+uid+"'; "+Manager.getItemName(uid)); return;
        }
        int amount;
        try{
            amount = Integer.parseInt(Popup.input("Amount:", ""));
        }catch(Exception e){Popup.error(StaticStuff.PROJECT_NAME, "Not a valid number.");return;}
        items.add(uid);
        itemAmount.add(""+amount);
    }

    public void removeItem(String uid){
        if(!StaticStuff.isValidUID(uid))
            return;
        if(!items.contains(uid)){
            Popup.error(StaticStuff.PROJECT_NAME, "Inventory does not contain this ItemType: '"+uid+"'"); return;
        }
        int index = items.indexOf(uid);
        items.remove(index);
        itemAmount.remove(index);
    }

    public int additionalRefactor(String find, String replace){
        int occ = 0;
        occ += StaticStuff.refactorArrayList(find, replace, items);
        occ += StaticStuff.refactorArrayList(find, replace, itemAmount);
        return occ;
    }

    public void openEditor(){
        new GuiEntityEditor(this, new String[]{"Name","Description","Add event","Edit event","Remove event","Edit tags","Edit variables","Edit inventory"},"Inventory"){
            @Override
            public void buttonPressed(int index){
                String str; int choice;
                switch(index){
                    case 0:
                    str = Popup.input("New name:",name);
                    if(str == null) return; if(str.equals("")) return;
                    name = str;
                    break;
                    case 1:
                    str = Popup.input("New description:",description);
                    if(str == null) return; if(str.equals("")) return;
                    description = str;
                    break;
                    case 2:
                    str = Popup.input("Event name:","");
                    if(str == null) return; if(str.equals("")) return;
                    addEvent(str);
                    break;
                    case 3:
                    str = Popup.input("Choose an event by its ID:","");
                    if(str == null) return; if(str.equals("")) return;
                    new GuiActionEditor(getEntity(),Integer.parseInt(str));
                    break;
                    case 4:
                    str = Popup.input("Choose an event by its ID:","");
                    if(str == null) return; if(str.equals("")) return;
                    deleteEvent(Integer.parseInt(str));
                    break;
                    case 5:
                    choice = Popup.selectButton(StaticStuff.PROJECT_NAME, "What do you want to do?", new String[]{"Add tag","Remove tag","Edit tag"});
                    if(choice == 0)
                        getEntity().addTag(Popup.input("Tag name:", ""));
                    else if(choice == 1)
                        getEntity().deleteTag(Integer.parseInt(Popup.input("Tag index:", "")));
                    else if (choice == 2)
                        getEntity().editTag(Integer.parseInt(Popup.input("Tag index:", "")),Popup.input("Tag name:", ""));
                    break;
                    case 6:
                    choice = Popup.selectButton(StaticStuff.PROJECT_NAME, "What do you want to do?", new String[]{"Add variable","Remove variable","Edit variable"});
                    if(choice == 0){
                        getEntity().addVariable(Popup.input("Variable name:", ""),true);
                    }else if(choice == 1)
                        getEntity().removeVariable(StaticStuff.autoDetectUID("Variable uid:"));
                    else if (choice == 2)
                        getEntity().openVariable(StaticStuff.autoDetectUID("Variable uid:"), true);
                    break;
                    case 7:
                    new GuiInventoryEditor((Inventory)getEntity());
                    break;
                    default:
                    Popup.error(StaticStuff.PROJECT_NAME, "Invalid action\nButton "+index+" does not exist.");
                }
                update();
            }
        };
    }
}
