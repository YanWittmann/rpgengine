
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class GuiPlayerStats extends JFrame {
    private static JLabel l_attr[];
    private JLabel l_img[];
    private static JLabel l_inventoryHeader;
    private static JLabel l_inventorySlots[];
    private static JLabel l_playername;
    private static JLabel l_stats;
    private static JLabel l_playerClass;
    private JLabel l_statsHeader;
    private JPanel p_inventory;
    private JPanel p_status;
    private static Interpreter interpreter;
    private static PlayerSettings player;
    private static Manager manager;
    private static int amountItems;
    private static boolean battleMode = false;
    private int x_size = Interpreter.getScaledValue(379), y_size = Interpreter.getScaledValue(717), curves_size = Interpreter.getScaledValue(20);

    public GuiPlayerStats(Interpreter interpreter, PlayerSettings player, Manager manager) {
        this.interpreter = interpreter;
        this.player = player;
        this.manager = manager;
        this.setTitle(player.getValue("name"));
        this.setSize(x_size, y_size);

        JPanel contentPane = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                g.setColor(StaticStuff.getColor("white_border"));
                g.fillRoundRect(0, 0, x_size, y_size, curves_size, curves_size);
                g.setColor(StaticStuff.getColor("background"));
                g.fillRoundRect(3, 3, x_size - 6, y_size - 6, curves_size, curves_size);
            }
        };
        setUndecorated(true);
        contentPane.setPreferredSize(new Dimension(x_size, y_size));
        contentPane.setBackground(new Color(0, 0, 0, 0));
        setBackground(new Color(0, 0, 0, 0));
        setIconImage(new ImageIcon("res/img/iconblue.png").getImage());

        p_inventory = new JPanel(null);
        p_inventory.setBounds(Interpreter.getScaledValue(21), Interpreter.getScaledValue(259), Interpreter.getScaledValue(340), Interpreter.getScaledValue(278));
        p_inventory.setBackground(StaticStuff.getColor("background"));
        p_inventory.setForeground(StaticStuff.getColor("background"));
        p_inventory.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor("white_border"), Interpreter.getScaledValue(4), true));
        p_inventory.setEnabled(true);
        p_inventory.setVisible(false);

        p_status = new JPanel(null);
        p_status.setBounds(Interpreter.getScaledValue(20), Interpreter.getScaledValue(112), Interpreter.getScaledValue(340), Interpreter.getScaledValue(103));
        p_status.setBackground(StaticStuff.getColor("background"));
        p_status.setForeground(StaticStuff.getColor("background"));
        p_status.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor("white_border"), Interpreter.getScaledValue(4), true));
        p_status.setEnabled(true);
        p_status.setVisible(false);

        l_playername = new JLabel(StaticStuff.prepareString("[[gold:" + player.getValue("name") + "]]"), SwingConstants.CENTER);
        l_playername.setBounds(0, 0, x_size, Interpreter.getScaledValue(66));
        l_playername.setBackground(new Color(214, 217, 223));
        l_playername.setForeground(new Color(255, 255, 255));
        l_playername.setEnabled(true);
        l_playername.setFont(StaticStuff.getPixelatedFont(27f));
        l_playername.setVisible(false);

        l_playerClass = new JLabel("<html>" + StaticStuff.prepareString("[[gray:" + Interpreter.lang("playerAttr" + player.getValue("class")) + "]]"), SwingConstants.CENTER);
        l_playerClass.setBounds(0, Interpreter.getScaledValue(60), x_size, Interpreter.getScaledValue(20));
        l_playerClass.setBackground(new Color(214, 217, 223));
        l_playerClass.setForeground(new Color(255, 255, 255));
        l_playerClass.setEnabled(true);
        l_playerClass.setFont(StaticStuff.getPixelatedFont(10f));
        l_playerClass.setVisible(false);

        l_statsHeader = new JLabel();
        l_statsHeader.setBounds(Interpreter.getScaledValue(25), Interpreter.getScaledValue(73), Interpreter.getScaledValue(250), Interpreter.getScaledValue(32));
        l_statsHeader.setBackground(new Color(214, 217, 223));
        l_statsHeader.setForeground(new Color(255, 255, 255));
        l_statsHeader.setEnabled(true);
        l_statsHeader.setFont(StaticStuff.getPixelatedFont());
        l_statsHeader.setText("<html>" + StaticStuff.prepareString(Interpreter.lang("playerStatsStats")));
        l_statsHeader.setVisible(false);

        l_stats = new JLabel();
        l_stats.setBounds(Interpreter.getScaledValue(11), Interpreter.getScaledValue(7), Interpreter.getScaledValue(318), Interpreter.getScaledValue(89));
        l_stats.setBackground(new Color(214, 217, 223));
        l_stats.setForeground(new Color(255, 255, 255));
        l_stats.setEnabled(true);
        l_stats.setFont(StaticStuff.getPixelatedFont(10f));
        l_stats.setText("<html>" + Interpreter.lang("playerStatsInsertValues", player.getValue("health"), player.getValue("gold"), Manager.getName(player.getValue("location"))));
        l_stats.setVisible(false);

        l_inventoryHeader = new JLabel();
        l_inventoryHeader.setBounds(Interpreter.getScaledValue(25), Interpreter.getScaledValue(220), Interpreter.getScaledValue(500), Interpreter.getScaledValue(32));
        l_inventoryHeader.setBackground(new Color(214, 217, 223));
        l_inventoryHeader.setForeground(new Color(255, 255, 255));
        l_inventoryHeader.setEnabled(true);
        l_inventoryHeader.setFont(StaticStuff.getPixelatedFont());
        l_inventoryHeader.setText("<html>" + StaticStuff.prepareString(Interpreter.lang("playerStatsInventory")));
        l_inventoryHeader.setVisible(false);

        int invSlotsApart = Interpreter.getScaledValue(26);
        amountItems = (int) (270 / invSlotsApart);
        l_inventorySlots = new JLabel[amountItems];
        for (int i = 0; i < amountItems; i++) {
            l_inventorySlots[i] = new JLabel();
            l_inventorySlots[i].setBounds(Interpreter.getScaledValue(15), Interpreter.getScaledValue(8 + (invSlotsApart * i)), Interpreter.getScaledValue(500), invSlotsApart);
            l_inventorySlots[i].setBackground(new Color(214, 217, 223));
            l_inventorySlots[i].setForeground(new Color(255, 255, 255));
            l_inventorySlots[i].setEnabled(true);
            l_inventorySlots[i].setFont(StaticStuff.getPixelatedFont(10f));
            l_inventorySlots[i].setText("Item " + (i + 1));
            l_inventorySlots[i].setVisible(false);
            final int ii = i;
            l_inventorySlots[i].addMouseWheelListener(new MouseWheelListener() {
                public void mouseWheelMoved(MouseWheelEvent evt) {
                    scroll(evt);
                }
            });
            l_inventorySlots[i].addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        clickInventorySlot(ii);
                    }
                }
            });
            p_inventory.add(l_inventorySlots[i]);
            addListener(l_inventorySlots[i]);
        }

        l_img = new JLabel[7];
        l_attr = new JLabel[7];
        setupAttribute(0, 50, 582, Interpreter.lang("playerAttrMU"), "courage.png", contentPane);
        setupAttribute(1, 122, 582, Interpreter.lang("playerAttrKL"), "wisdom.png", contentPane);
        setupAttribute(2, 194, 582, Interpreter.lang("playerAttrIN"), "intuition.png", contentPane);
        setupAttribute(3, 266, 582, Interpreter.lang("playerAttrCH"), "charisma.png", contentPane);
        setupAttribute(4, 86, 662, Interpreter.lang("playerAttrFF"), "dexterity.png", contentPane);
        setupAttribute(5, 158, 662, Interpreter.lang("playerAttrGE"), "agility.png", contentPane);
        setupAttribute(6, 230, 662, Interpreter.lang("playerAttrKK"), "strength.png", contentPane);

        contentPane.add(l_playerClass);
        addListener(l_playerClass);
        contentPane.add(l_inventoryHeader);
        addListener(l_inventoryHeader);
        contentPane.add(l_playername);
        addListener(l_playername);
        p_status.add(l_stats);
        addListener(l_stats);
        contentPane.add(l_statsHeader);
        addListener(l_statsHeader);
        contentPane.add(p_inventory);
        addListener(p_inventory);
        contentPane.add(p_status);
        addListener(p_status);
        addListener(this);

        JLabel l_dummy = new JLabel("");
        l_dummy.setBounds(0, 0, 1, 1);
        l_dummy.setEnabled(true);
        l_dummy.setVisible(true);
        contentPane.add(l_dummy);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);

        //get save bounds
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gd.getDefaultConfiguration());
        Rectangle safeBounds = new Rectangle(bounds);
        safeBounds.x += insets.left;
        safeBounds.y += insets.top;
        safeBounds.width -= (insets.left + insets.right);
        safeBounds.height -= (insets.top + insets.bottom);

        setLocation(safeBounds.x + 10, safeBounds.y + 10);
        this.pack();
        this.setVisible(false);

        playerInventory = (Inventory) Manager.getEntity(player.getValue("inventory"));
    }

    private void setupAttribute(int id, int x, int y, String name, String filename, JPanel contentPane) {
        y = Interpreter.getScaledValue(y - 30);
        x = Interpreter.getScaledValue(x + 15);

        l_img[id] = new JLabel();
        l_img[id].setBounds(x, y, Interpreter.getScaledValue(36), Interpreter.getScaledValue(36));
        l_img[id].setBackground(new Color(214, 217, 223));
        l_img[id].setForeground(new Color(255, 255, 255));
        l_img[id].setEnabled(true);
        l_img[id].setFont(StaticStuff.getPixelatedFont());
        l_img[id].setIcon(getScaledImage(new ImageIcon(Objects.requireNonNull(Images.readImageFromFile("res/img/" + filename))), Interpreter.getScaledValue(36), Interpreter.getScaledValue(36)));
        l_img[id].setVisible(false);
        l_img[id].addMouseListener(new MouseListener() {
            GuiHoverText popup;

            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                popup.close();
            }

            public void mouseEntered(MouseEvent e) {
                popup = new GuiHoverText(name);
            }

            public void mouseClicked(MouseEvent e) {
            }
        });

        l_attr[id] = new JLabel("" + (id + 8), SwingConstants.CENTER);
        l_attr[id].setBounds(x + Interpreter.getScaledValue(2), y + Interpreter.getScaledValue(36), Interpreter.getScaledValue(36), Interpreter.getScaledValue(36));
        l_attr[id].setBackground(new Color(214, 217, 223));
        l_attr[id].setForeground(new Color(255, 255, 255));
        l_attr[id].setEnabled(true);
        l_attr[id].setFont(StaticStuff.getPixelatedFont(13f));
        l_attr[id].setVisible(false);

        addListener(l_img[id]);
        addListener(l_attr[id]);

        contentPane.add(l_img[id]);
        contentPane.add(l_attr[id]);
    }

    //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon; Thanks to trolologuy!
    private ImageIcon getScaledImage(ImageIcon srcImg, int w, int h) {
        Image image = srcImg.getImage();
        Image newimg = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    private int pX, pY;
    private boolean dragActive = false;
    private GuiPlayerStatsMinimized mini = null;
    private final GuiPlayerStats self = this;

    private void addListener(Component c) {
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON2 && !battleMode) {
                    for (int currentOpacity = 100; currentOpacity > 0; currentOpacity -= 3) {
                        try {
                            Thread.sleep(2);
                        } catch (Exception ignored) {
                        }
                        setOpacity(currentOpacity * 0.01f);
                    }
                    setVisible(false);
                    if (mini == null)
                        mini = new GuiPlayerStatsMinimized(player, self, Manager.getImage(Manager.project.getValue("image")));
                    mini.open(getX(), getY());
                }
                dragActive = true;
                pX = me.getX();
                pY = me.getY();
                toFront();
                repaint();
            }

            public void mouseReleased(MouseEvent me) {
                dragActive = false;
            }

            public void mouseDragged(MouseEvent me) {
                if (dragActive) setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });
        c.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                if (dragActive) setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });
    }

    static int scrollIndex = 0;

    private void scroll(MouseWheelEvent evt) {
        if (evt.getUnitsToScroll() < 0) {
            if (scrollIndex > 0) {
                scrollIndex--;
                updateInventory();
            }
        } else if (inventoryItems.length > scrollIndex + 2) {
            scrollIndex++;
            updateInventory();
        }
    }

    private static Inventory playerInventory;
    private static String[] inventoryItems;
    private static String[] inventoryItemsUIDs;

    public static void updateOutput() {
        l_attr[0].setText("<html>" + StaticStuff.prepareString(player.getValue("courage")));
        l_attr[1].setText("<html>" + StaticStuff.prepareString(player.getValue("wisdom")));
        l_attr[2].setText("<html>" + StaticStuff.prepareString(player.getValue("intuition")));
        l_attr[3].setText("<html>" + StaticStuff.prepareString(player.getValue("charisma")));
        l_attr[4].setText("<html>" + StaticStuff.prepareString(player.getValue("dexterity")));
        l_attr[5].setText("<html>" + StaticStuff.prepareString(player.getValue("agility")));
        l_attr[6].setText("<html>" + StaticStuff.prepareString(player.getValue("strength")));
        l_playername.setText("<html>" + StaticStuff.prepareString("[[gold:" + player.getValue("name") + "]]"));
        l_playerClass.setText("<html>" + StaticStuff.prepareString("[[gray:" + player.getValue("class") + "]]"));
        l_stats.setText("<html>" + StaticStuff.prepareString(Interpreter.lang("playerStatsInsertValues", player.getValue("health") + " [[white:/]] " + player.getValue("maxHealth"), player.getValue("gold"), manager.getName(player.getValue("location")))));
        inventoryItems = playerInventory.getItemsAsStringArray();
        inventoryItemsUIDs = playerInventory.getItemUIDsAsStringArray();
        if (!(inventoryItems.length > scrollIndex + 2)) scrollIndex = Math.max(0, inventoryItems.length - 2);
        if (manager.isPlayerInventoryOverloaded())
            l_inventoryHeader.setText("<html>" + StaticStuff.prepareString(Interpreter.lang("playerStatsInventory") + " ([[red:" + playerInventory.getInventoryWeight() + "]] / " + player.getValue("maxLoad") + ")"));
        else
            l_inventoryHeader.setText(Interpreter.lang("playerStatsInventory") + " (" + playerInventory.getInventoryWeight() + " / " + player.getValue("maxLoad") + ")");
        updateInventory();
    }

    private static void updateInventory() {
        int j = 0;
        for (int i = scrollIndex; i < amountItems; i++) { //<font color="#24f0e2">
            if (i >= inventoryItems.length) l_inventorySlots[j].setText("");
            else if (player.getValue("holdingMain").equals(inventoryItemsUIDs[i]) && player.getValue("holdingSecond").equals(inventoryItemsUIDs[i]))
                l_inventorySlots[j].setText(inventoryItems[i].replaceAll("x <font color=\"#([0-9a-z]{6})\">", "x <font color=\"c6fa1b\">")); //set HTML color to green / yellow
            else if (player.getValue("holdingMain").equals(inventoryItemsUIDs[i]))
                l_inventorySlots[j].setText(inventoryItems[i].replaceAll("x <font color=\"#([0-9a-z]{6})\">", "x <font color=\"72e31b\">")); //set HTML color to green
            else if (player.getValue("holdingSecond").equals(inventoryItemsUIDs[i]))
                l_inventorySlots[j].setText(inventoryItems[i].replaceAll("x <font color=\"#([0-9a-z]{6})\">", "x <font color=\"298f44\">")); //set HTML color to dark green
            else if (player.getValue("holdingArmor").equals(inventoryItemsUIDs[i]))
                l_inventorySlots[j].setText(inventoryItems[i].replaceAll("x <font color=\"#([0-9a-z]{6})\">", "x <font color=\"4548e6\">")); //set HTML color to blue
            else l_inventorySlots[j].setText(inventoryItems[i]);
            j++;
        }
    }

    public void open(int x, int y) {
        setLocation(x, y);
        updateOutput();
        setVisible(true);
        setOpacity(1f);
        openAnimation();
    }

    public void open() {
        if (mini != null) mini.hideFrame();
        updateOutput();
        setVisible(true);
        setOpacity(1f);
        openAnimation();
    }

    private void openAnimation() {
        for (int i = 0; i < 7; i++) {
            l_attr[i].setVisible(false);
            l_img[i].setVisible(false);
        }
        for (int i = 0; i < amountItems; i++)
            l_inventorySlots[i].setVisible(false);
        l_inventoryHeader.setVisible(false);
        l_stats.setVisible(false);
        l_statsHeader.setVisible(false);
        l_playerClass.setVisible(false);
        l_playername.setVisible(false);
        p_status.setVisible(false);
        p_inventory.setVisible(false);

        new Thread(() -> {
            String toDisplay = "<html>" + StaticStuff.prepareString("[[gold:" + player.getValue("name") + "]]");
            l_playername.setText("");
            l_playername.setVisible(true);
            for (int i = 1; i < toDisplay.length(); i++) {
                if (toDisplay.charAt(i) == '<' || toDisplay.charAt(i - 1) == '<') {
                    while (toDisplay.charAt(i) != '>') i++;
                    i++;
                }
                Sleep.milliseconds(StaticStuff.randomNumber(50, 150));
                l_playername.setText(toDisplay.substring(0, i));
            }
            l_playername.setText(toDisplay);
            l_playerClass.setVisible(true);
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 4; i++) {
                l_statsHeader.setVisible(false);
                Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                p_status.setVisible(false);
                Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                l_statsHeader.setVisible(true);
                Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                p_status.setVisible(true);
                Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
            }
            Sleep.milliseconds(100);
            l_stats.setVisible(true);
        }).start();

        new Thread(() -> {
            Sleep.milliseconds(300);
            for (int i = 0; i < 4; i++) {
                l_inventoryHeader.setVisible(false);
                Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                p_inventory.setVisible(false);
                Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                l_inventoryHeader.setVisible(true);
                Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                p_inventory.setVisible(true);
                Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
            }
        }).start();

        new Thread() {
            public void run() {
                for (int i = 0; i < amountItems && i < inventoryItems.length; i++) {
                    l_inventorySlots[i].setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                }
                for (int i = 0; i < amountItems && i < inventoryItems.length; i++) {
                    l_inventorySlots[i].setVisible(false);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                }
                for (int i = 0; i < amountItems && i < inventoryItems.length; i++) {
                    l_inventorySlots[i].setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                }
                for (int i = 0; i < amountItems && i < inventoryItems.length; i++) {
                    l_inventorySlots[i].setVisible(false);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                }
                for (int i = 0; i < amountItems && i < inventoryItems.length; i++) {
                    l_inventorySlots[i].setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                }
                for (int i = 0; i < amountItems; i++) {
                    l_inventorySlots[i].setVisible(true);
                }
            }
        }.start();

        new Thread() {
            public void run() {
                for (int i = 0; i < 7; i++) {
                    l_attr[i].setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                    l_img[i].setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 60));
                }
                for (int i = 0; i < 7; i++) {
                    l_attr[i].setVisible(false);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 20));
                    l_img[i].setVisible(false);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 20));
                }
                for (int i = 0; i < 7; i++) {
                    l_attr[i].setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 40));
                    l_img[i].setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(10, 40));
                }
            }
        }.start();
    }

    private void clickInventorySlot(int id) {
        new Thread() {
            public void run() {
                Log.add("Clicked on item slot with id: " + id);
                if (!BattleMap.isPlayerTurn && battleMode) {
                    Log.add("It's not the players turn.");
                    return;
                }
                if (scrollIndex + id >= inventoryItemsUIDs.length) {
                    Log.add("There is no item at that slot.");
                    return;
                }
                String clickedUID = inventoryItemsUIDs[scrollIndex + id];
                Log.add("Clicked item UID: " + clickedUID);
                int amountOptions = 4, current = 0;
                boolean isHoldingClickedItem = (player.getValue("holdingMain").equals(clickedUID) || player.getValue("holdingSecond").equals(clickedUID) || player.getValue("holdingArmor").equals(clickedUID));
                String options[] = new String[amountOptions];
                options[current] = Interpreter.lang("playerStatsItemClickExamine");
                current++;
                options[current] = Interpreter.lang("playerStatsItemClickUse");
                current++;
                if (!isHoldingClickedItem) {
                    options[current] = Interpreter.lang("playerStatsItemClickEquip");
                    current++;
                } else {
                    options[current] = Interpreter.lang("playerStatsItemClickUnequip");
                    current++;
                }
                options[current] = Interpreter.lang("playerStatsItemClickNothing");
                int choice = StaticStuff.openPopup(Interpreter.lang("popupItemClicked", Manager.getName(clickedUID)), options);
                if (choice == 0) {
                    GuiObjectDisplay.create(manager.getEntity(clickedUID), Interpreter.lang("playerStatsItemOpenItem"));
                } else if (choice == 1) {
                    interpreter.executePlayerCommand("useWithUID " + clickedUID);
                } else if ((choice == 2) && !isHoldingClickedItem) {
                    Item toEquip = (Item) manager.getEntity(clickedUID);
                    String slot = toEquip.getVariableValue("hands");
                    boolean current1 = false, current2 = false;
                    if (player.getValue("holdingMain").equals("")) current1 = true;
                    if (player.getValue("holdingSecond").equals("")) current2 = true;
                    if (slot.equals("1")) {
                        if (current1) player.setValue("holdingMain", clickedUID);
                        else if (current2) player.setValue("holdingSecond", clickedUID);
                        else StaticStuff.openPopup(Interpreter.lang("popupItemCanNotEquip"));
                    } else if (slot.equals("2")) {
                        if (current1 && current2) {
                            player.setValue("holdingMain", clickedUID);
                            player.setValue("holdingSecond", clickedUID);
                        } else StaticStuff.openPopup(Interpreter.lang("popupItemCanNotEquip"));
                    } else if (slot.equals("armor")) {
                        if (player.getValue("holdingArmor").equals("")) {
                            player.setValue("holdingArmor", clickedUID);
                        } else StaticStuff.openPopup(Interpreter.lang("popupItemCanNotEquip"));
                    }
                    updateInventory();
                } else if (choice == 2 && isHoldingClickedItem) {
                    if (player.getValue("holdingArmor").equals(clickedUID)) player.setValue("holdingArmor", "");
                    if (player.getValue("holdingMain").equals(clickedUID)) player.setValue("holdingMain", "");
                    if (player.getValue("holdingSecond").equals(clickedUID)) player.setValue("holdingSecond", "");
                    updateInventory();
                }
            }
        }.start();
    }

    public void battleMode(boolean active) {
        battleMode = active;
    }
}