
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GuiEntityEditor extends JFrame {
    private JButton b_actions;
    private JLabel l_entityName;
    private JTextArea ta_entityData;
    private int width, height;
    private Entity entity;
    private String entityType;
    private JPanel contentPane = new JPanel(null);

    public GuiEntityEditor(Entity entity, String[] buttons, String entityType) {
        this.entityType = entityType;
        this.entity = entity;
        width = 816;
        height = 346;
        if (buttons.length > 1) height += 40;
        if (buttons.length > 2) height += 40;
        this.setTitle(entity.name + " - " + entityType + " editor");
        this.setSize(width, height);

        contentPane.setPreferredSize(new Dimension(width, height));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/icongreen.png").getImage());

        JButton firstButton = null;
        for (int i = 0; i < buttons.length; i++) {
            b_actions = new JButton();
            if (i == 0) firstButton = b_actions;
            b_actions.setBounds(20 + (int) (Math.floor(i / 3) * 200), 303 + ((i % 3) * 40), 196, 32);
            b_actions.setBackground(StaticStuff.getColor("buttons"));
            b_actions.setForeground(StaticStuff.getColor("text_color"));
            b_actions.setEnabled(true);
            b_actions.setFont(StaticStuff.getBaseFont());
            b_actions.setText(buttons[i] + " (" + i + ")");
            b_actions.setVisible(true);
            int ii = i;
            b_actions.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        buttonPressed(ii);
                    } catch (Exception e) {
                        Popup.error(StaticStuff.projectName, "Something did not work:\n" + e);
                    }
                }
            });
            contentPane.add(b_actions);
        }

        l_entityName = new JLabel();
        l_entityName.setBounds(20, 12, 770, 22);
        l_entityName.setBackground(StaticStuff.getColor("buttons"));
        l_entityName.setForeground(StaticStuff.getColor("text_color"));
        l_entityName.setEnabled(true);
        l_entityName.setFont(StaticStuff.getBaseFont());
        l_entityName.setText("<html><b>" + entity.name + "</b> - <b>" + entity.uid + "</b> - <b>" + entityType + "</b>");
        l_entityName.setVisible(true);

        ta_entityData = new JTextArea();
        ta_entityData.setBounds(10, 40, width - 20, 250);
        ta_entityData.setBackground(StaticStuff.getColor("text_background"));
        ta_entityData.setForeground(StaticStuff.getColor("text_color"));
        ta_entityData.setEnabled(true);
        ta_entityData.setFont(StaticStuff.getBaseFont());
        ta_entityData.setBorder(BorderFactory.createBevelBorder(1));
        ta_entityData.setVisible(true);
        ta_entityData.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent evt) {
                scroll(evt);
            }
        });
        updateTextArea();

        contentPane.add(l_entityName);
        contentPane.add(ta_entityData);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);

        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

        Action refreshAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                update();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "REFRESH");
        getRootPane().getActionMap().put("REFRESH", refreshAction);

        Action[] triggerButtonAction = new Action[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            final int ii = i;
            triggerButtonAction[i] = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    buttonPressed(ii);
                }
            };
            getRootPane().getActionMap().put("TRIGGER_BUTTON_" + i, triggerButtonAction[i]);
        }
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_0, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "TRIGGER_BUTTON_0");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "TRIGGER_BUTTON_1");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "TRIGGER_BUTTON_2");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "TRIGGER_BUTTON_3");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "TRIGGER_BUTTON_4");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "TRIGGER_BUTTON_5");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "TRIGGER_BUTTON_6");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_7, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "TRIGGER_BUTTON_7");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_8, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "TRIGGER_BUTTON_8");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_9, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "TRIGGER_BUTTON_9");

        Action editLine = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                editAction(EDIT);
            }
        };
        getRootPane().getActionMap().put("EDIT", editLine);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "EDIT");

        Action editLineClose = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                editAction(EDIT_CLOSE);
            }
        };
        getRootPane().getActionMap().put("EDIT_CLOSE", editLineClose);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "EDIT_CLOSE");

        Action editLineStayInForeground = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                editAction(EDIT_STAY_IN_FOREGROUND);
            }
        };
        getRootPane().getActionMap().put("EDIT_STAY_IN_FOREGROUND", editLineStayInForeground);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "EDIT_STAY_IN_FOREGROUND");

        try {
            assert firstButton != null;
            firstButton.requestFocus();
        } catch (Exception ignored) {
        }
        extraSetup();
    }

    private static final int EDIT = 0;
    private static final int EDIT_CLOSE = 1;
    private static final int EDIT_STAY_IN_FOREGROUND = 2;
    private void editAction(int intention) {
        String text = ta_entityData.getText();
        //if (ta_entityData.getSelectedText() != null) text = ta_entityData.getSelectedText();

        text = StaticStuff.getLineAtIndex(text, ta_entityData.getCaretPosition());

        text = text.trim();
        if(text.startsWith("Name: ")) {
            String str = Popup.input("New name:", entity.name);
            if (str == null) return;
            if (str.equals("")) return;
            entity.name = str;
        } else if(text.startsWith("Description: ")) {
            String str = Popup.input("New description:", entity.description);
            if (str == null) return;
            if (str.equals("")) return;
            entity.description = str;
        } else if(text.startsWith("Location: ")) {
            getEntity().setLocation(Popup.dropDown(StaticStuff.projectName, "Select a location UID", Manager.getStringArrayLocations()).replaceAll(".+ - ([^-]+)", "$1"));
        } else if(text.startsWith("Inventory: ")) {
            getEntity().setInventory(Popup.dropDown(StaticStuff.projectName, "Select an inventory UID", Manager.getStringArrayInventories()).replaceAll(".+ - ([^-]+)", "$1"));
        } else if(text.startsWith("Image: ")) {
            getEntity().setImage(Popup.dropDown(StaticStuff.projectName, "Select an image UID", Manager.getStringArrayImages()).replaceAll(".+ - ([^-]+)", "$1"));
        } else if(text.equals("Tags:")) {
            String str = Popup.input("Tag name:", "");
            if (str == null) return;
            if (str.equals("")) return;
            getEntity().addTag(str);
        } else if(text.equals("Events:")) {
            String str = Popup.input("Event name:", "");
            if (str == null) return;
            if (str.equals("")) return;
            entity.addEvent(str);
        } else if(text.matches("\\d+: .+")) { //event
            new GuiActionEditor(getEntity(), Integer.parseInt(text.replaceAll("(\\d+): .+", "$1")));
        } else if(text.equals("Local variables:")) {
            String str = Popup.input("Variable name:", "");
            if (str == null) return;
            if (str.equals("")) return;
            getEntity().addVariable(str, true);
        }  else if(StaticStuff.countOccurrences(text, " - ") == 3 && text.split(" - ")[0].matches("[\\da-zA-Z]{16}")) { //variable
            getEntity().openVariable(text.split(" - ")[0], true);
        }
        if(intention == EDIT_CLOSE) dispose();
        if(intention == EDIT_STAY_IN_FOREGROUND) {
            setAlwaysOnTop(true);
            Sleep.milliseconds(500);
            setAlwaysOnTop(false);
        }
        else updateTextArea();
    }

    public void setBackgroundColor(Color color) {
        contentPane.setBackground(color);
    }

    public Entity getEntity() {
        return entity;
    }

    public void update() {
        updateTitle();
        updateTextArea();
    }

    private void updateTitle() {
        setTitle(entity.name + " - " + entityType + " editor");
        l_entityName.setText("<html><b>" + entity.name + "</b> - <b>" + entity.uid + "</b> - <b>" + entityType + "</b>");
    }

    private void updateTextArea() {
        String[] toDisplay = entity.generateInformation().split("\n");
        ta_entityData.setText("");
        for (int i = scrollIndex; i < toDisplay.length; i++) ta_entityData.append(toDisplay[i] + "\n");
    }

    private int scrollIndex = 0;

    private void scroll(MouseWheelEvent evt) {
        if (evt.getUnitsToScroll() < 0) {
            if (scrollIndex > 0) scrollIndex -= 2;
        } else
            scrollIndex += 2;
        updateTextArea();
    }

    public void extraSetup() {}

    public void buttonPressed(int index) {
        System.out.println("pressed button with index " + index + ". Overwrite 'private void buttonPressed(int index)' with your own method.");
    }
}