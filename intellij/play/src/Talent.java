public class Talent {
    public String name, attr1, attr2, attr3, uid;

    public Talent() {
        uid = UID.generateUID();
        name = "New Talent";
        attr1 = "";
        attr2 = "";
        attr3 = "";
    }

    public Talent(String name, String attr1, String attr2, String attr3) {
        this.uid = UID.generateUID();
        this.name = name;
        this.attr1 = prepareAttrString(attr1);
        this.attr2 = prepareAttrString(attr2);
        this.attr3 = prepareAttrString(attr3);
    }

    public Talent(String[] fileInput) {
        try {
            name = fileInput[0];
            uid = fileInput[1];
            attr1 = "";
            attr2 = "";
            attr3 = "";
            try {
                attr1 = fileInput[2];
                attr2 = fileInput[3];
                attr3 = fileInput[4];
            } catch (Exception e) {
            }
        } catch (Exception e) {
            StaticStuff.error("Talent '" + name + "' contains invalid data.");
        }
    }

    public String getAttributes() {
        return attr1 + " " + attr2 + " " + attr3;
    }

    private int amount = -1;

    public int getAmountAttributes() {
        if (this.amount == -1) {
            int amount = 0;
            if (attr1.length() > 0) amount++;
            if (attr2.length() > 0) amount++;
            if (attr3.length() > 0) amount++;
            this.amount = amount;
        }
        return this.amount;
    }

    public String[] getAttributesArray() {
        String attributes[] = new String[getAmountAttributes()];
        if (attr1.length() > 0) attributes[0] = attr1;
        if (attr2.length() > 0) attributes[1] = attr2;
        if (attr3.length() > 0) attributes[2] = attr3;
        return attributes;
    }

    private String prepareAttrString(String a) {
        return a.replace("MU", "courage").replace("KL", "wisdom").replace("IN", "intuition").replace("CH", "charisma").replace("FF", "dexterity").replace("GE", "agility").replace("KK", "strength");
    }


    public String generateSaveString() {
        return name + "\n" + uid + "\n" + attr1 + "\n" + attr2 + "\n" + attr3;
    }
} //courage, wisdom, intuition, charisma, dexterity, agility, strength
