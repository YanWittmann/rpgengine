import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;

public class GuiCustomPopup extends JFrame {
    private int x_size = 2, y_size = 2;
    private CustomPopup customPopup;
    private JPanel contentPane;
    private HashMap<String, JLabel> labels = new HashMap<>();
    private HashMap<String, JLabel> images = new HashMap<>();
    private HashMap<String, JButton> buttons = new HashMap<>();
    private String name = "unnamed";

    public GuiCustomPopup(CustomPopup customPopup, String as) {
        this.customPopup = customPopup;
        this.name = as;
        String[] popupData = customPopup.getEventCode("popupData");

        String[] sizeSplitted = StaticStuff.insertVariables(popupData[0]).split(" ");
        x_size = Integer.parseInt(sizeSplitted[0]);
        y_size = Integer.parseInt(sizeSplitted[1]);

        for (int i = 1; i < popupData.length; i++) {
            if (popupData[i].matches("popup;.+"))
                setPopupData(popupData[i]);
            else
                createComponentWithData(popupData[i]);
        }

        try {
            contentPane = new JPanel(null) {
                public void paintComponent(Graphics g) {
                    g.setColor(StaticStuff.getColor("white_border"));
                    g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
                    g.setColor(StaticStuff.getColor(backgroundColor));
                    g.fillRoundRect(Interpreter.getScaledValue(3), Interpreter.getScaledValue(3), x_size - Interpreter.getScaledValue(6), y_size - Interpreter.getScaledValue(6), 20, 20);
                }
            };
            setUndecorated(true);
            setAlwaysOnTop(alwaysOnTop);
            contentPane.setBackground(new Color(0, 0, 0, 0));
            setBackground(new Color(0, 0, 0, 0));
            setIconImage(new ImageIcon("res/img/icongreen.png").getImage());
            this.setSize(x_size, y_size);
            contentPane.setPreferredSize(new Dimension(x_size, y_size));

            addAllComponents();

            this.add(contentPane);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setLocationRelativeTo(null);
            this.pack();
            this.setVisible(true);

            addAllListeners(this);

            Interpreter.executeEventFromObjectStatic(customPopup.uid, "popupOpened", new String[]{"name:" + name});
            Log.add("Opened popup " + name);
        } catch (Exception e) {
            Log.add("Unable to open custom popup");
        }
    }

    private void addAllComponents() {
        for (Map.Entry<String, JLabel> entry : labels.entrySet()) {
            String key = entry.getKey();
            JLabel value = entry.getValue();
            contentPane.add(value);
        }
        for (Map.Entry<String, JLabel> entry : images.entrySet()) {
            String key = entry.getKey();
            JLabel value = entry.getValue();
            contentPane.add(value);
        }
        for (Map.Entry<String, JButton> entry : buttons.entrySet()) {
            String key = entry.getKey();
            JButton value = entry.getValue();
            contentPane.add(value);
        }
    }

    private String backgroundColor = "background";
    private boolean alwaysOnTop = false, canBeClosed = true;

    private void setPopupData(String line) {
        String[] splitted = StaticStuff.insertVariables(line.replaceAll("popup; ?(.+)", "$1")).split("; ?");
        for (String attribute : splitted) {
            String[] attr = attribute.split(":");
            switch (attr[0]) {
                case "background" -> backgroundColor = attr[1];
                case "alwaysOnTop" -> alwaysOnTop = attr[1].equals("true");
                case "canBeClosed" -> canBeClosed = attr[1].equals("true");
            }
        }
    }

    private void createComponentWithData(String line) {
        if (line.length() == 0) return;
        if (line.charAt(0) == '#') return;
        line = StaticStuff.insertVariables(line);
        String type = line.replaceAll("([^;]); ?.+", "$1");
        String[] splitted = StaticStuff.insertVariables(line.replaceAll(type + "; ?(.+)", "$1")).split("; ?");
        switch (type) {
            case "text" -> {
                JLabel labelText = new JLabel("");
                labelText.setHorizontalAlignment(SwingConstants.LEFT);
                labelText.setBounds(0, 0, Interpreter.getScaledValue(100), Interpreter.getScaledValue(50));
                labelText.setBackground(new Color(214, 217, 223));
                labelText.setForeground(new Color(255, 255, 255));
                labelText.setEnabled(true);
                labelText.setFont(StaticStuff.getPixelatedFont());
                labelText.setVisible(true);
                boolean hasHoverEvent = false;
                for (int i = 1; i < splitted.length; i++) {
                    String[] attrVal = splitted[i].split(":");
                    switch (attrVal[0]) {
                        case "text":
                            labelText.setText("<html>" + StaticStuff.prepareString(attrVal[1]));
                            break;
                        case "fontsize":
                            labelText.setFont(StaticStuff.getPixelatedFont(Float.parseFloat("" + attrVal[1])));
                            break;
                        case "x":
                            labelText.setBounds(Integer.parseInt(attrVal[1]), labelText.getY(), labelText.getWidth(), labelText.getHeight());
                            break;
                        case "y":
                            labelText.setBounds(labelText.getX(), Integer.parseInt(attrVal[1]), labelText.getWidth(), labelText.getHeight());
                            break;
                        case "width":
                            labelText.setBounds(labelText.getX(), labelText.getY(), Integer.parseInt(attrVal[1]), labelText.getHeight());
                            break;
                        case "height":
                            labelText.setBounds(labelText.getX(), labelText.getY(), labelText.getWidth(), Integer.parseInt(attrVal[1]));
                            break;
                        case "visible":
                            labelText.setVisible(attrVal[1].equals("true"));
                            break;
                        case "border":
                            labelText.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor(attrVal[1]), Interpreter.getScaledValue(4), true));
                            break;
                        case "anchor":
                            switch (attrVal[1]) {
                                case "left" -> labelText.setHorizontalAlignment(SwingConstants.LEFT);
                                case "center" -> labelText.setHorizontalAlignment(SwingConstants.CENTER);
                                case "right" -> labelText.setHorizontalAlignment(SwingConstants.RIGHT);
                            }
                            break;
                        case "clickListener":
                            boolean finalHasHoverEvent = hasHoverEvent;
                            labelText.addMouseListener(new MouseAdapter() {
                                public void mouseClicked(MouseEvent evt) {
                                    Interpreter.executeEventFromObjectStatic(customPopup.uid, attrVal[1], new String[]{"componentType:" + type, "componentName:" + splitted[0], "popupName:" + name});
                                }

                                String before;

                                public void mouseEntered(MouseEvent e) {
                                    if (finalHasHoverEvent) {
                                        before = labelText.getText();
                                        labelText.setText("<html>" + StaticStuff.prepareString("[[aqua:" + before.replaceAll("<[^>]+>", "") + "]]"));
                                    }
                                }

                                public void mouseExited(MouseEvent e) {
                                    if (finalHasHoverEvent)
                                        labelText.setText(before);
                                }
                            });
                            break;
                        case "hoverEffect":
                            hasHoverEvent = attrVal[1].equals("true");
                            break;
                    }
                }
                addAllListeners(labelText);
                labels.put(splitted[0], labelText);
            }
            case "image" -> {
                JLabel labelImage = new JLabel("");
                labelImage.setHorizontalAlignment(SwingConstants.LEFT);
                labelImage.setBounds(0, 0, Interpreter.getScaledValue(100), Interpreter.getScaledValue(50));
                labelImage.setBackground(new Color(214, 217, 223));
                labelImage.setForeground(new Color(255, 255, 255));
                labelImage.setEnabled(true);
                labelImage.setFont(StaticStuff.getPixelatedFont());
                labelImage.setVisible(true);
                for (int i = 1; i < splitted.length; i++) {
                    String[] attrVal = splitted[i].split(":");
                    switch (attrVal[0]) {
                        case "fontsize":
                            labelImage.setFont(StaticStuff.getPixelatedFont(Float.parseFloat("" + attrVal[1])));
                            break;
                        case "x":
                            labelImage.setBounds(Integer.parseInt(attrVal[1]), labelImage.getY(), labelImage.getWidth(), labelImage.getHeight());
                            break;
                        case "y":
                            labelImage.setBounds(labelImage.getX(), Integer.parseInt(attrVal[1]), labelImage.getWidth(), labelImage.getHeight());
                            break;
                        case "width":
                            labelImage.setBounds(labelImage.getX(), labelImage.getY(), Integer.parseInt(attrVal[1]), labelImage.getHeight());
                            break;
                        case "height":
                            labelImage.setBounds(labelImage.getX(), labelImage.getY(), labelImage.getWidth(), Integer.parseInt(attrVal[1]));
                            break;
                        case "visible":
                            labelImage.setVisible(attrVal[1].equals("true"));
                            break;
                        case "border":
                            labelImage.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor(attrVal[1]), Interpreter.getScaledValue(4), true));
                            break;
                        case "image":
                            ImageIcon img = new ImageIcon(Manager.getImage(attrVal[1]));
                            labelImage.setIcon(PopupImage.getScaledImage(img, labelImage.getWidth(), labelImage.getHeight()));
                            break;
                        case "anchor":
                            switch (attrVal[1]) {
                                case "left" -> labelImage.setHorizontalAlignment(SwingConstants.LEFT);
                                case "center" -> labelImage.setHorizontalAlignment(SwingConstants.CENTER);
                                case "right" -> labelImage.setHorizontalAlignment(SwingConstants.RIGHT);
                            }
                            break;
                        case "clickListener":
                            labelImage.addMouseListener(new MouseAdapter() {
                                public void mouseClicked(MouseEvent evt) {
                                    Interpreter.executeEventFromObjectStatic(customPopup.uid, attrVal[1], new String[]{"componentType:" + type, "componentName:" + splitted[0], "popupName:" + name});
                                }
                            });
                            break;
                    }
                }
                addAllListeners(labelImage);
                images.put(splitted[0], labelImage);
            }
            case "button" -> {
                JButton button = new JButton("");
                button.setHorizontalAlignment(SwingConstants.CENTER);
                button.setBounds(0, 0, Interpreter.getScaledValue(100), Interpreter.getScaledValue(50));
                button.setBackground(new Color(214, 217, 223));
                button.setForeground(new Color(255, 255, 255));
                button.setEnabled(true);
                button.setFont(StaticStuff.getPixelatedFont());
                button.setVisible(true);
                for (int i = 1; i < splitted.length; i++) {
                    String[] attrVal = splitted[i].split(":");
                    switch (attrVal[0]) {
                        case "text":
                            button.setText("<html>" + StaticStuff.prepareString("[[def_text_color_buttons:" + attrVal[1] + "]]"));
                            break;
                        case "fontsize":
                            button.setFont(StaticStuff.getPixelatedFont(Float.parseFloat("" + attrVal[1])));
                            break;
                        case "x":
                            button.setBounds(Integer.parseInt(attrVal[1]), button.getY(), button.getWidth(), button.getHeight());
                            break;
                        case "y":
                            button.setBounds(button.getX(), Integer.parseInt(attrVal[1]), button.getWidth(), button.getHeight());
                            break;
                        case "width":
                            button.setBounds(button.getX(), button.getY(), Integer.parseInt(attrVal[1]), button.getHeight());
                            break;
                        case "height":
                            button.setBounds(button.getX(), button.getY(), button.getWidth(), Integer.parseInt(attrVal[1]));
                            break;
                        case "visible":
                            button.setVisible(attrVal[1].equals("true"));
                            break;
                        case "border":
                            button.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor(attrVal[1]), Interpreter.getScaledValue(4), true));
                            break;
                        case "anchor":
                            switch (attrVal[1]) {
                                case "left" -> button.setHorizontalAlignment(SwingConstants.LEFT);
                                case "center" -> button.setHorizontalAlignment(SwingConstants.CENTER);
                                case "right" -> button.setHorizontalAlignment(SwingConstants.RIGHT);
                            }
                            break;
                        case "clickListener":
                            button.addMouseListener(new MouseAdapter() {
                                public void mouseClicked(MouseEvent evt) {
                                    Interpreter.executeEventFromObjectStatic(customPopup.uid, attrVal[1], new String[]{"componentType:" + type, "componentName:" + splitted[0], "popupName:" + name});
                                }
                            });
                            break;
                    }
                }
                addAllListeners(button);
                buttons.put(splitted[0], button);
            }
        }
    }

    int pX, pY;
    private boolean dragActive = false;

    private void addAllListeners(Component c) {
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON2 && canBeClosed) {
                    for (int currentOpacity = 100; currentOpacity > 0; currentOpacity -= 3) {
                        try {
                            Thread.sleep(2);
                        } catch (Exception ignored) {
                        }
                        setOpacity(currentOpacity * 0.01f);
                    }
                    dispose();
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

    public CustomPopup getCustomPopup() {
        return customPopup;
    }

    public void close() {
        Log.add("Closing popup " + name);
        dispose();
    }

    public void setData(String component, String attribute, String value) {
        Log.add("Setting data for popup '" + name + "': Set attribute " + attribute + " of component " + component + " to '" + value + "'");

        for (Map.Entry<String, JLabel> entry : labels.entrySet()) {
            String key = entry.getKey();
            JLabel jComponent = entry.getValue();
            if (key.equals(component)) {
                String[] attrVal = new String[]{attribute, value};
                switch (attrVal[0]) {
                    case "text":
                        jComponent.setText("<html>" + StaticStuff.prepareString(attrVal[1]));
                        break;
                    case "fontsize":
                        jComponent.setFont(StaticStuff.getPixelatedFont(Float.parseFloat("" + attrVal[1])));
                        break;
                    case "x":
                        jComponent.setBounds(Integer.parseInt(attrVal[1]), jComponent.getY(), jComponent.getWidth(), jComponent.getHeight());
                        break;
                    case "y":
                        jComponent.setBounds(jComponent.getX(), Integer.parseInt(attrVal[1]), jComponent.getWidth(), jComponent.getHeight());
                        break;
                    case "width":
                        jComponent.setBounds(jComponent.getX(), jComponent.getY(), Integer.parseInt(attrVal[1]), jComponent.getHeight());
                        break;
                    case "height":
                        jComponent.setBounds(jComponent.getX(), jComponent.getY(), jComponent.getWidth(), Integer.parseInt(attrVal[1]));
                        break;
                    case "visible":
                        jComponent.setVisible(attrVal[1].equals("true"));
                        break;
                    case "border":
                        jComponent.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor(attrVal[1]), Interpreter.getScaledValue(4), true));
                        break;
                    case "anchor":
                        switch (attrVal[1]) {
                            case "left" -> jComponent.setHorizontalAlignment(SwingConstants.LEFT);
                            case "center" -> jComponent.setHorizontalAlignment(SwingConstants.CENTER);
                            case "right" -> jComponent.setHorizontalAlignment(SwingConstants.RIGHT);
                        }
                        break;
                }
            }
        }
        for (Map.Entry<String, JLabel> entry : images.entrySet()) {
            String key = entry.getKey();
            JLabel jComponent = entry.getValue();
            if (key.equals(component)) {
                String[] attrVal = new String[]{attribute, value};
                switch (attrVal[0]) {
                    case "fontsize":
                        jComponent.setFont(StaticStuff.getPixelatedFont(Float.parseFloat("" + attrVal[1])));
                        break;
                    case "x":
                        jComponent.setBounds(Integer.parseInt(attrVal[1]), jComponent.getY(), jComponent.getWidth(), jComponent.getHeight());
                        break;
                    case "y":
                        jComponent.setBounds(jComponent.getX(), Integer.parseInt(attrVal[1]), jComponent.getWidth(), jComponent.getHeight());
                        break;
                    case "width":
                        jComponent.setBounds(jComponent.getX(), jComponent.getY(), Integer.parseInt(attrVal[1]), jComponent.getHeight());
                        break;
                    case "height":
                        jComponent.setBounds(jComponent.getX(), jComponent.getY(), jComponent.getWidth(), Integer.parseInt(attrVal[1]));
                        break;
                    case "visible":
                        jComponent.setVisible(attrVal[1].equals("true"));
                        break;
                    case "border":
                        jComponent.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor(attrVal[1]), Interpreter.getScaledValue(4), true));
                        break;
                    case "image":
                        ImageIcon img = new ImageIcon(Manager.getImage(attrVal[1]));
                        jComponent.setIcon(PopupImage.getScaledImage(img, jComponent.getWidth(), jComponent.getHeight()));
                        break;
                    case "anchor":
                        switch (attrVal[1]) {
                            case "left" -> jComponent.setHorizontalAlignment(SwingConstants.LEFT);
                            case "center" -> jComponent.setHorizontalAlignment(SwingConstants.CENTER);
                            case "right" -> jComponent.setHorizontalAlignment(SwingConstants.RIGHT);
                        }
                        break;
                }
            }
        }
        for (Map.Entry<String, JButton> entry : buttons.entrySet()) {
            String key = entry.getKey();
            JButton jComponent = entry.getValue();
            if (key.equals(component)) {
                String[] attrVal = new String[]{attribute, value};
                switch (attrVal[0]) {
                    case "text":
                        jComponent.setText("<html>" + StaticStuff.prepareString("[[def_text_color_buttons:" + attrVal[1] + "]]"));
                        break;
                    case "fontsize":
                        jComponent.setFont(StaticStuff.getPixelatedFont(Float.parseFloat("" + attrVal[1])));
                        break;
                    case "x":
                        jComponent.setBounds(Integer.parseInt(attrVal[1]), jComponent.getY(), jComponent.getWidth(), jComponent.getHeight());
                        break;
                    case "y":
                        jComponent.setBounds(jComponent.getX(), Integer.parseInt(attrVal[1]), jComponent.getWidth(), jComponent.getHeight());
                        break;
                    case "width":
                        jComponent.setBounds(jComponent.getX(), jComponent.getY(), Integer.parseInt(attrVal[1]), jComponent.getHeight());
                        break;
                    case "height":
                        jComponent.setBounds(jComponent.getX(), jComponent.getY(), jComponent.getWidth(), Integer.parseInt(attrVal[1]));
                        break;
                    case "visible":
                        jComponent.setVisible(attrVal[1].equals("true"));
                        break;
                    case "border":
                        jComponent.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor(attrVal[1]), Interpreter.getScaledValue(4), true));
                        break;
                    case "anchor":
                        switch (attrVal[1]) {
                            case "left" -> jComponent.setHorizontalAlignment(SwingConstants.LEFT);
                            case "center" -> jComponent.setHorizontalAlignment(SwingConstants.CENTER);
                            case "right" -> jComponent.setHorizontalAlignment(SwingConstants.RIGHT);
                        }
                        break;
                }
            }
        }
    }
}
