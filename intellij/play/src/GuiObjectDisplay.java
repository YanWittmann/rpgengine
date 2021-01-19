
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class GuiObjectDisplay extends JFrame {
    private JLabel l_desc;
    private JLabel l_extraText;
    private JLabel l_image;
    private JLabel l_objectName;
    private JLabel l_objectType;
    private int x_size = Interpreter.getScaledValue(444), y_size = Interpreter.getScaledValue(430);
    private int imgX = Interpreter.getScaledValue(300), imgY = 192;
    private int currentY = 5;
    private String desc;
    private BufferedImage image;
    private boolean imageActive = false;
    private GuiObjectDisplay self = this;
    private Entity entity;

    private boolean clickState = false;
    private JPanel contentPane;

    public GuiObjectDisplay(Entity entity, String extraText) {
        if (entity == null) return;
        this.entity = entity;
        try {
            this.setTitle(entity.name);
            if (entity.image != null)
                if (entity.image.length() > 0) {
                    image = Images.getBufferedImage(entity.image);
                    imgX = Interpreter.getScaledValue((int) (Float.parseFloat(imgY + "") / (Float.parseFloat(image.getHeight() + "") / Float.parseFloat(image.getWidth() + ""))));
                    imgY = Interpreter.getScaledValue(imgY);
                    imageActive = true;
                }

            contentPane = new JPanel(new BorderLayout()) {
                public void paintComponent(Graphics g) {
                    g.setColor(StaticStuff.getColor("white_border"));
                    g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
                    if (entity.type.equals("Color"))
                        g.setColor(StaticStuff.getColor(entity.name));
                    else if (clickState) g.setColor(StaticStuff.getColor("gray"));
                    else g.setColor(StaticStuff.getColor("background"));
                    g.fillRoundRect(Interpreter.getScaledValue(3), Interpreter.getScaledValue(3), x_size - Interpreter.getScaledValue(6), y_size - Interpreter.getScaledValue(6), 20, 20);
                }
            };
            setUndecorated(true);
            contentPane.setBackground(new Color(0, 0, 0, 0));
            setBackground(new Color(0, 0, 0, 0));
            setIconImage(new ImageIcon("res/img/icongreen.png").getImage());

            JLabel l_dummy = new JLabel("");
            l_dummy.setBounds(0, 0, 1, 1);
            l_dummy.setEnabled(true);
            l_dummy.setVisible(true);

            l_objectType = new JLabel("<html>" + StaticStuff.prepareString("[[gray:" + Interpreter.lang("objectName" + entity.type) + "]]"));
            l_objectType.setBounds(Interpreter.getScaledValue(11), Interpreter.getScaledValue(7), Interpreter.getScaledValue(406), Interpreter.getScaledValue(20));
            l_objectType.setBackground(new Color(214, 217, 223));
            l_objectType.setForeground(new Color(255, 255, 255));
            l_objectType.setEnabled(true);
            l_objectType.setFont(StaticStuff.getPixelatedFont(10f));
            l_objectType.setVisible(true);

            l_objectName = new JLabel("<html><center><b>" + StaticStuff.prepareString(entity.name), SwingConstants.CENTER);
            l_objectName.setBounds(Interpreter.getScaledValue(20), currentY, Interpreter.getScaledValue(406), Interpreter.getScaledValue(92));
            currentY += Interpreter.getScaledValue(100);
            l_objectName.setBackground(new Color(214, 217, 223));
            l_objectName.setForeground(new Color(255, 255, 255));
            l_objectName.setEnabled(true);
            l_objectName.setFont(StaticStuff.getPixelatedFont());
            l_objectName.setVisible(true);

            int dummy;
            String objectVarText = "";
            desc = StaticStuff.prepareStringWithLineLength(StaticStuff.insertVariables(entity.description), 44);
            if (desc.replace("<br>", "").length() != 0) objectVarText = "<br>";
            for (String s : Interpreter.getObjectFrameVariables())
                if (Interpreter.lang("varNames" + s).equals("missingString"))
                    objectVarText = generateDescAppendString(s, objectVarText, entity, s);
                else
                    objectVarText = generateDescAppendString(s, objectVarText, entity, Interpreter.lang("varNames" + s));
            if (desc.replace("<br>", "").length() > 0 || objectVarText.replace("<br>", "").length() > 0) {
                if (desc.replace("<br>", "").length() == 0) desc = "";
                l_desc = new JLabel();
                dummy = Interpreter.getScaledValue(30 * ((desc + objectVarText).length() - (desc + objectVarText).replace("<br>", "<br").length() + 1));
                l_desc.setBounds(Interpreter.getScaledValue(20), currentY, Interpreter.getScaledValue(406), dummy);
                currentY += dummy + Interpreter.getScaledValue(8);
                l_desc.setBackground(new Color(214, 217, 223));
                l_desc.setForeground(new Color(255, 255, 255));
                l_desc.setEnabled(true);
                l_desc.setFont(StaticStuff.getPixelatedFont(10f));
                l_desc.setText("<html>" + StaticStuff.prepareString(desc + objectVarText));
                l_desc.setVisible(true);
            }

            if (imageActive) {
                l_image = new JLabel("", SwingConstants.CENTER);
                l_image.setBounds(Interpreter.getScaledValue((x_size / 2) - (imgX / 2)), currentY, imgX, imgY);
                currentY += Interpreter.getScaledValue(200);
                l_image.setBackground(new Color(214, 217, 223));
                l_image.setForeground(new Color(255, 255, 255));
                l_image.setEnabled(true);
                ImageIcon img = new ImageIcon(image);
                l_image.setIcon(getScaledImage(img, imgX, imgY));
                l_image.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor("white_border"), Interpreter.getScaledValue(4), true));
                l_image.setVisible(true);
                l_image.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        if (evt.getClickCount() == 1) { //single click
                            new PopupImage(StaticStuff.prepareString(entity.name), image, 400).createComponents();
                        }
                    }
                });
            }

            if (extraText.length() > 0) {
                l_extraText = new JLabel();
                desc = StaticStuff.prepareStringWithLineLength(StaticStuff.insertVariables(extraText), 44);
                dummy = Interpreter.getScaledValue(10 * (desc.length() - desc.replace("<br>", "<br").length() + 1));
                l_extraText.setBounds(Interpreter.getScaledValue(20), currentY, Interpreter.getScaledValue(404), dummy);
                currentY += dummy + Interpreter.getScaledValue(8);
                l_extraText.setBackground(new Color(214, 217, 223));
                l_extraText.setForeground(new Color(255, 255, 255));
                l_extraText.setEnabled(true);
                l_extraText.setFont(StaticStuff.getPixelatedFont(10f));
                l_extraText.setText("<html>" + StaticStuff.prepareString(desc));
                l_extraText.setVisible(true);
                contentPane.add(l_extraText);
            }

            l_extraText = new JLabel();
            desc = StaticStuff.prepareStringWithLineLength(Interpreter.lang("popupObjectShowHowTo"), 44);
            dummy = Interpreter.getScaledValue(10 * (desc.length() - desc.replace("<br>", "<br").length() + 1));
            l_extraText.setBounds(Interpreter.getScaledValue(20), currentY, Interpreter.getScaledValue(404), dummy);
            currentY += dummy + Interpreter.getScaledValue(8);
            l_extraText.setBackground(new Color(214, 217, 223));
            l_extraText.setForeground(new Color(255, 255, 255));
            l_extraText.setEnabled(true);
            l_extraText.setFont(StaticStuff.getPixelatedFont(10f));
            l_extraText.setText("<html>" + StaticStuff.prepareString(desc));
            l_extraText.setVisible(true);
            contentPane.add(l_extraText);
            l_extraText.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    if (evt.getClickCount() == 1) {
                        new Thread(() -> StaticStuff.openPopup(Interpreter.lang("popupObjectHowToTitle") + "<br>" + Interpreter.lang("popupObjectHowToClose") + "<br>" + Interpreter.lang("popupObjectHowToOpenImage") + "<br>" + Interpreter.lang("popupObjectHowToForeground") + "<br>" + Interpreter.lang("popupObjectHowToMinimize"))).start();
                    }
                }
            });

            contentPane.add(l_objectType);
            contentPane.add(l_objectName);
            if (entity.description.replace("<br>", "").length() > 0 || objectVarText.replace("<br>", "").length() > 0)
                contentPane.add(l_desc);
            if (imageActive) contentPane.add(l_image);
            contentPane.add(l_dummy);

            y_size = currentY + Interpreter.getScaledValue(5);
            this.setSize(x_size, y_size);
            contentPane.setPreferredSize(new Dimension(x_size, y_size));

            this.add(contentPane);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setLocationRelativeTo(null);
            setLocation(getX() + StaticStuff.randomNumber(Interpreter.getScaledValue(-200), Interpreter.getScaledValue(200)), getY() + StaticStuff.randomNumber(Interpreter.getScaledValue(-200), Interpreter.getScaledValue(200)));
            this.pack();
            this.setVisible(true);

            addListener(this);
            addListener(l_objectType);
            addListener(l_objectName);
            if (entity.description.replace("<br>", "").length() > 0 || objectVarText.replace("<br>", "").length() > 0)
                addListener(l_desc);
            if (extraText.length() > 0) addListener(l_extraText);
            if (imageActive) addListener(l_image);
            Log.add("Opened object " + entity.uid);
        } catch (Exception e) {
            Log.add("Unable to open object: " + e);
        }
    }

    private String generateDescAppendString(String value, String current, Entity entity, String displayAs) {
        if (entity.variableExists(value)) {
            current += "[" + "[[aqua:" + displayAs + "]] = [[blue:" + entity.getVariableValueSilent(value) + "]]" + "]";
            int amount = current.length() - current.replaceAll("\\[([^\\]\\[])", "$1").length();
            if (amount % 4 == 0 && amount > 0) current += "<br>";
            else current += " ";
        }
        return current;
    }

    //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon; Thanks to trolologuy!
    private ImageIcon getScaledImage(ImageIcon srcImg, int w, int h) {
        Image image = srcImg.getImage();
        Image newimg = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    int pX, pY;
    private boolean dragActive = false, alwaysOnTop = false, isMinimized = false;
    private GuiObjectDisplayMinimized minimized = null;

    private void addListener(Component c) {
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON2) {
                    for (int currentOpacity = 100; currentOpacity > 0; currentOpacity -= 3) {
                        try {
                            Thread.sleep(2);
                        } catch (Exception ignored) {
                        }
                        setOpacity(currentOpacity * 0.01f);
                    }
                    removeObjectFrame(self);
                }
                dragActive = true;
                pX = me.getX();
                pY = me.getY();
                toFront();
                repaint();
                if (me.getClickCount() == 2) { //double click
                    alwaysOnTop = !alwaysOnTop;
                    setAlwaysOnTop(alwaysOnTop);
                    new Thread(() -> {
                        clickState = true;
                        new Thread(() -> contentPane.repaint()).start();
                        Sleep.milliseconds(200);
                        clickState = false;
                        new Thread(() -> contentPane.repaint()).start();
                    }).start();
                } else if (me.getClickCount() == 3) { //triple clicktest
                    isMinimized = true;
                    minimized = new GuiObjectDisplayMinimized(entity);
                    for (int currentOpacity = 100; currentOpacity > 0; currentOpacity -= 3) {
                        try {
                            Thread.sleep(2);
                        } catch (Exception ignored) {
                        }
                        setOpacity(currentOpacity * 0.01f);
                    }
                    dispose();
                }
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

    public static void create(Entity entity, String extraText) {
        if (!objectFrameIsOpen(entity))
            registerObjectFrame(entity, new GuiObjectDisplay(entity, extraText));
        else Log.add("Object frame " + entity.uid + " is already visible!");
    }

    private static HashMap<Entity, GuiObjectDisplay> displays = new HashMap<>();

    private static void registerObjectFrame(Entity entity, GuiObjectDisplay display) {
        if (!objectFrameIsOpen(entity))
            displays.put(entity, display);
    }

    public static void close(Entity entity) {
        removeObjectFrame(entity);
    }

    private static void removeObjectFrame(Entity entity) {
        if (objectFrameIsOpen(entity)) {
            Log.add("Closing object frame " + entity.uid);
            GuiObjectDisplay frame = getObjectFrameFromEntity(entity);
            frame.dispose();
            displays.remove(entity);
        }
    }

    private static void removeObjectFrame(GuiObjectDisplay display) {
        Entity entity = display.entity;
        if (objectFrameIsOpen(entity)) {
            Log.add("Closing object frame " + entity.uid);
            display.dispose();
            displays.remove(entity);
        }
    }

    private static boolean objectFrameIsOpen(Entity entity) {
        return displays.containsKey(entity);
    }

    private static boolean objectFrameIsOpen(GuiObjectDisplay display) {
        return displays.containsValue(display);
    }

    private static GuiObjectDisplay getObjectFrameFromEntity(Entity entity) {
        if (objectFrameIsOpen(entity))
            return displays.get(entity);
        else return null;
    }

    public static void updateFrame(Entity entity) {
        if (objectFrameIsOpen(entity)) {
            Log.add("Updating object frame " + entity.uid);
            GuiObjectDisplay display = getObjectFrameFromEntity(entity);
            if (display.isMinimized && display.minimized != null) {
                display.minimized.updateFrameObject(entity);
            } else getObjectFrameFromEntity(entity).updateFrameObject(entity);
        }
    }

    private void updateFrameObject(Entity entity) {
        int dummy;
        String objectVarText = "";
        desc = StaticStuff.prepareStringWithLineLength(StaticStuff.insertVariables(entity.description), 44);
        if (desc.replace("<br>", "").length() != 0) objectVarText = "<br>";
        for (String s : Interpreter.getObjectFrameVariables())
            if (Interpreter.lang("varNames" + s).equals("missingString"))
                objectVarText = generateDescAppendString(s, objectVarText, entity, s);
            else
                objectVarText = generateDescAppendString(s, objectVarText, entity, Interpreter.lang("varNames" + s));
        if (desc.replace("<br>", "").length() > 0 || objectVarText.replace("<br>", "").length() > 0) {
            if (desc.replace("<br>", "").length() == 0) desc = "";
            desc = "<html>" + StaticStuff.prepareString(desc + objectVarText);
            l_desc.setText(desc);
        }
    }
}
