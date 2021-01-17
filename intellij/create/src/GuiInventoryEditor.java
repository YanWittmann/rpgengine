
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GuiInventoryEditor extends JFrame {
    private JButton b_add;
    private JButton b_close;
    private JButton b_remove;
    private JLabel l_info;
    private JTextArea ta_items;
    private Inventory inv;

    public GuiInventoryEditor(Inventory inv) {
        this.inv = inv;
        this.setTitle(inv.name + " - Inventory editor");
        this.setSize(534, 907);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(534, 907));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/iconblue.png").getImage());

        b_add = new JButton();
        b_add.setBounds(106, 865, 90, 35);
        b_add.setBackground(StaticStuff.getColor("buttons"));
        b_add.setForeground(StaticStuff.getColor("text_color"));
        b_add.setEnabled(true);
        b_add.setFont(StaticStuff.getBaseFont());
        b_add.setText("Add (0)");
        b_add.setVisible(true);
        b_add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                add();
            }
        });

        b_remove = new JButton();
        b_remove.setBounds(211, 865, 120, 35);
        b_remove.setBackground(StaticStuff.getColor("buttons"));
        b_remove.setForeground(StaticStuff.getColor("text_color"));
        b_remove.setEnabled(true);
        b_remove.setFont(StaticStuff.getBaseFont());
        b_remove.setText("Remove (1)");
        b_remove.setVisible(true);
        b_remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                remove();
            }
        });

        b_close = new JButton();
        b_close.setBounds(344, 865, 90, 35);
        b_close.setBackground(StaticStuff.getColor("buttons"));
        b_close.setForeground(StaticStuff.getColor("text_color"));
        b_close.setEnabled(true);
        b_close.setFont(StaticStuff.getBaseFont());
        b_close.setText("Close");
        b_close.setVisible(true);
        b_close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                close();
            }
        });

        l_info = new JLabel();
        l_info.setBounds(12, 14, 215, 25);
        l_info.setBackground(StaticStuff.getColor("buttons"));
        l_info.setForeground(StaticStuff.getColor("text_color"));
        l_info.setEnabled(true);
        l_info.setFont(StaticStuff.getBaseFont());
        l_info.setText("<html><b>The inventory contains these items:");
        l_info.setVisible(true);
        l_info.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
                l_info.setText("<html><b>You noticed me! :)");
                new Configuration("res/config/main.cfg").set("ee", "true");
                StaticStuff.ee = "true";
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                l_info.setText("<html><b>The inventory contains these items:");
            }

            public void mouseEntered(MouseEvent e) {
                l_info.setText("<html><b>The inventory contains these items :)");
            }

            public void mouseClicked(MouseEvent e) {
            }
        });

        ta_items = new JTextArea();
        ta_items.setBounds(22, 51, 485, 808);
        ta_items.setBackground(StaticStuff.getColor("text_background"));
        ta_items.setForeground(StaticStuff.getColor("text_color"));
        ta_items.setEnabled(true);
        ta_items.setFont(StaticStuff.getBaseFont());
        ta_items.setText(inv.getInventoryString());
        ta_items.setBorder(BorderFactory.createBevelBorder(1));
        ta_items.setVisible(true);

        contentPane.add(b_add);
        contentPane.add(b_close);
        contentPane.add(b_remove);
        contentPane.add(l_info);
        contentPane.add(ta_items);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

        Action addItemAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                add();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_0, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "addItemAction");
        getRootPane().getActionMap().put("addItemAction", addItemAction);

        Action removeItemAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                remove();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "removeItemAction");
        getRootPane().getActionMap().put("removeItemAction", removeItemAction);

    }

    private void add() {
        inv.addItem(Popup.dropDown(StaticStuff.projectName, "Select an item UID", Manager.getStringArrayItems()).split(" - ")[1]);
        ta_items.setText(inv.getInventoryString());
    }

    private void remove() {
        inv.removeItem(Popup.dropDown(StaticStuff.projectName, "Select an item UID", Manager.getStringArrayItems()).split(" - ")[1]);
        ta_items.setText(inv.getInventoryString());
    }

    private void close() {
        dispose();
    }
}