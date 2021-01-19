
import java.util.ArrayList;

public class Inventory extends Entity {
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<String> itemAmount = new ArrayList<String>();

    public Inventory(String[] fileInput) {
        type = "Inventory";
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
                } else if (fileInput[i].contains("++item++")) {
                    fileInput[i] = fileInput[i].replace("++item++", "");
                    items.add(fileInput[i].split("---")[0]);
                    itemAmount.add(fileInput[i].split("---")[1]);
                } else if (fileInput[i].contains("++variable++")) {
                    fileInput[i] = fileInput[i].replace("++variable++", "");
                    localVarUids.add(fileInput[i].split("---")[0]);
                    localVarName.add(fileInput[i].split("---")[1]);
                    localVarType.add(fileInput[i].split("---")[2]);
                    localVarValue.add(fileInput[i].split("---")[3]);
                }
            }
        } catch (Exception e) {
            StaticStuff.error("Conversation '" + name + "' contains invalid data.");
        }
    }

    public String generateSaveString() {
        StringBuilder str = new StringBuilder("" + name + "\n" + description + "\n" + uid);
        for (int i = 0; i < eventName.size(); i++)
            str.append("\n++ev++").append(eventName.get(i)).append("---").append(eventCode.get(i));
        for (String tag : tags) str.append("\n++tag++").append(tag);
        for (int i = 0; i < localVarName.size(); i++)
            str.append("\n").append("++variable++").append(localVarUids.get(i)).append("---").append(localVarName.get(i)).append("---").append(localVarType.get(i)).append("---").append(localVarValue.get(i));
        for (int i = 0; i < items.size(); i++)
            str.append("\n++item++").append(items.get(i)).append("---").append(itemAmount.get(i));
        return str.toString();
    }

    public void setItem(String uid, int amount) {
        if (!StaticStuff.isValidUID(uid)) return;
        if (!Manager.itemTypeExists(uid)) {
            StaticStuff.error("Invalid UID: '" + uid + "'\nThis ItemType does not exist.");
            return;
        }
        if (items.contains(uid)) {
            int index = items.indexOf(uid);
            if (amount <= 0) removeItem(uid);
            else itemAmount.set(index, "" + amount);
        } else {
            if (amount == 0) return;
            items.add(uid);
            itemAmount.add("" + amount);
        }
    }

    public void addItem(String uid, int amount) {
        if (!StaticStuff.isValidUID(uid)) return;
        if (!Manager.itemTypeExists(uid)) {
            StaticStuff.error("Invalid UID: '" + uid + "'\nThis ItemType does not exist.");
            return;
        }
        if (items.contains(uid)) {
            int index = items.indexOf(uid);
            if ((amount + Integer.parseInt(itemAmount.get(index))) <= 0) removeItem(uid);
            else itemAmount.set(index, (amount + Integer.parseInt(itemAmount.get(index)) + ""));
        } else {
            items.add(uid);
            itemAmount.add("" + amount);
            if (Integer.parseInt(itemAmount.get(itemAmount.size() - 1)) <= 0) removeItem(uid);
        }
    }

    public void removeItem(String uid) {
        if (!StaticStuff.isValidUID(uid))
            return;
        if (!items.contains(uid)) {
            StaticStuff.error("Inventory does not contain this ItemType: '" + uid + "'");
            return;
        }
        int index = items.indexOf(uid);
        items.remove(index);
        itemAmount.remove(index);
    }

    public void clearItems() {
        items.clear();
        itemAmount.clear();
    }

    public String[] getItemsAsStringArray() {
        String itemsString[] = new String[items.size()];
        for (int i = 0; i < itemsString.length; i++) {
            String colorValue = Manager.getEntity(items.get(i)).getVariableValueSilent("color");
            if (colorValue.equals("-1"))
                itemsString[i] = "<html>" + StaticStuff.prepareString("[[gray:" + itemAmount.get(i) + "]]x [[aqua:" + Manager.getName(items.get(i)) + "]]");
            else
                itemsString[i] = "<html>" + StaticStuff.prepareString("[[gray:" + itemAmount.get(i) + "]]x <font color=\"#" + colorValue + "\">" + Manager.getName(items.get(i)) + "</font>");
        }
        return itemsString;
    }

    public String[] getItemUIDsAsStringArray() {
        String uids[] = new String[items.size()];
        for (int i = 0; i < uids.length; i++)
            uids[i] = items.get(i);
        return uids;
    }

    public int getInventoryWeight() {
        int weight = 0;
        for (int i = 0; i < items.size(); i++)
            weight += (Integer.parseInt(Manager.getLocalVariableByNameOrUID(items.get(i), "weight")) * Integer.parseInt(itemAmount.get(i)));
        return weight;
    }

    public String getMaxDamageItem() {
        int maxDamage = -1, estimatedDamage;
        String item = "";
        for (int i = 0; i < items.size(); i++) {
            estimatedDamage = StaticStuff.estimatedRollValue(Manager.getEntity(items.get(i)).getVariableValue("damage"));
            if (maxDamage < estimatedDamage) {
                maxDamage = estimatedDamage;
                item = items.get(i);
            }
        }
        return item;
    }

    public String getMaxDamageWithMinRangeAndLOS(float pRange, float losRange, boolean viewCanBeObstructed) {
        int maxDamage = -1, estimatedDamage, range = (int) pRange;
        String item = "";
        Log.add(viewCanBeObstructed ? ("Looking for item with minimum los range " + losRange + " or with normal range of " + pRange) : ("Looking for item with minimum range: " + range));
        for (int i = 0; i < items.size(); i++) {
            Entity itemObject = Manager.getEntity(items.get(i));
            //Log.add("viewCanBeObstructed = "+viewCanBeObstructed);
            //Log.add("itemObject.tags.contains(\"viewCanBeObstructed\") = "+itemObject.tags.contains("viewCanBeObstructed"));
            //if(!viewCanBeObstructed || (viewCanBeObstructed && itemObject.tags.contains("viewCanBeObstructed"))){
            if (!viewCanBeObstructed && (range <= Float.parseFloat(itemObject.getVariableValue("range")))) {
                estimatedDamage = StaticStuff.estimatedRollValue(Manager.getEntity(items.get(i)).getVariableValue("damage"));
                if (maxDamage < estimatedDamage) {
                    maxDamage = estimatedDamage;
                    item = items.get(i);
                }
            } else if (viewCanBeObstructed && (losRange <= Float.parseFloat(itemObject.getVariableValue("range")))) {
                estimatedDamage = StaticStuff.estimatedRollValue(Manager.getEntity(items.get(i)).getVariableValue("damage"));
                if (maxDamage < estimatedDamage) {
                    maxDamage = estimatedDamage;
                    item = items.get(i);
                }
            }
            //}
        }
        if (item.equals("")) item = getMaxDamageItem();
        return item;
    }
}