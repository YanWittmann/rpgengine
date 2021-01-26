public class Talent {
    public String name, attr1, attr2, attr3, uid;

    public Talent() {
        uid = UID.generateUID();
        StaticStuff.setLastCreatedUID(uid);
        name = "New Talent";
        attr1 = "";
        attr2 = "";
        attr3 = "";
        try {
            StaticStuff.copyString(uid);
        } catch (Exception e) {
        }
    }

    public Talent(String name, String attr1, String attr2, String attr3) {
        this.uid = UID.generateUID();
        this.name = name;
        this.attr1 = prepareAttrString(attr1);
        this.attr2 = prepareAttrString(attr2);
        this.attr3 = prepareAttrString(attr3);
        try {
            StaticStuff.copyString(uid);
        } catch (Exception e) {
        }
    }

    public Talent(boolean isNewAdventure, String[] attr) {
        this.uid = UID.generateUID();
        this.name = attr[0];
        this.attr1 = "";
        this.attr2 = "";
        this.attr3 = "";
        if (attr.length > 1)
            this.attr1 = prepareAttrString(attr[1]);
        if (attr.length > 2)
            this.attr2 = prepareAttrString(attr[2]);
        if (attr.length > 3)
            this.attr3 = prepareAttrString(attr[3]);
        try {
            StaticStuff.copyString(uid);
        } catch (Exception e) {
        }
    }

    public Talent(String[] fileInput) {
        try {
            name = fileInput[0];
            uid = fileInput[1];
            this.attr1 = "";
            this.attr2 = "";
            this.attr3 = "";
            try {
                attr1 = fileInput[2];
                attr2 = fileInput[3];
                attr3 = fileInput[4];
            } catch (Exception e2) {
            }
        } catch (Exception e) {
            Popup.error(StaticStuff.projectName, "Talent '" + name + "' contains invalid data.");
        }
    }

    public String generateSaveString() {
        return name + "\n" + uid + "\n" + attr1 + "\n" + attr2 + "\n" + attr3;
    }

    public String generateInformation() {
        String attributes = attr1.substring(0, 3).toUpperCase();
        if (attr2.length() > 2)
            attributes += ", " + attr2.substring(0, 3).toUpperCase();
        if (attr3.length() > 2)
            attributes += ", " + attr3.substring(0, 3).toUpperCase();
        return name + " (" + attributes + ") --- " + uid;
    }

    private String prepareAttrString(String a) {
        return a.replace("MU", "courage").replace("KL", "wisdom").replace("IN", "intuition").replace("CH", "charisma").replace("FF", "dexterity").replace("GE", "agility").replace("KK", "strength");
    }

    public void open() {
        new GuiTalent(this);
    }

    public int refactor(String find, String replace) {
        int occ = 0;
        occ += StaticStuff.countOccurrences(name, find);
        name = name.replace(find, replace);
        occ += StaticStuff.countOccurrences(attr1, find);
        attr1 = attr1.replace(find, replace);
        occ += StaticStuff.countOccurrences(attr2, find);
        attr2 = attr2.replace(find, replace);
        occ += StaticStuff.countOccurrences(attr3, find);
        attr3 = attr3.replace(find, replace);
        occ += StaticStuff.countOccurrences(uid, find);
        uid = uid.replace(find, replace);
        return occ;
    }
} //courage, wisdom, intuition, charisma, dexterity, agility, strength
