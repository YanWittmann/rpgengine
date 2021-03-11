
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

public class GuiShowText extends JFrame {
    private final JLabel l_title;
    private final JTextArea ta_text;
    private final JPanel contentPane = new JPanel(null);
    private final String[] display;
    private Manager manager;

    public GuiShowText(String title, String[] display, Manager manager) {
        this.manager = manager;
        int width = 816;
        int height = 346;
        this.setTitle(title);
        this.setSize(width, height);
        this.display = display;

        contentPane.setPreferredSize(new Dimension(width, height));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/icongreen.png").getImage());

        l_title = new JLabel();
        l_title.setBounds(20, 12, 770, 22);
        l_title.setBackground(StaticStuff.getColor("buttons"));
        l_title.setForeground(StaticStuff.getColor("text_color"));
        l_title.setEnabled(true);
        l_title.setFont(StaticStuff.getBaseFont());
        l_title.setText(title);
        l_title.setVisible(true);

        ta_text = new JTextArea();
        ta_text.setBounds(10, 40, width - 20, 346 - 48);
        ta_text.setBackground(StaticStuff.getColor("text_background"));
        ta_text.setForeground(StaticStuff.getColor("text_color"));
        ta_text.setEnabled(true);
        ta_text.setFont(StaticStuff.getBaseFont());
        ta_text.setBorder(BorderFactory.createBevelBorder(1));
        ta_text.setVisible(true);
        ta_text.addMouseWheelListener(this::scroll);
        updateTextArea();

        contentPane.add(l_title);
        contentPane.add(ta_text);

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
    }

    private static final int EDIT = 0;
    private static final int EDIT_CLOSE = 1;
    private static final int EDIT_STAY_IN_FOREGROUND = 2;

    private void editAction(int intention) {
        String text = ta_text.getText();
        text = StaticStuff.getLineAtIndex(text, ta_text.getCaretPosition());
        String uid = text.replaceAll("([^:]+):.+", "$1");

        if (text.contains(": Tags") || text.contains(": Local var name") || text.contains(": Local var type") || text.contains(": Local var uid") || text.contains(": Local var value")
                || text.contains(": Name") || text.contains(": UID") || text.contains(": Description")) {
            manager.openEntity(uid);
        } else if (text.contains(": Event name") || text.contains(": In event code of event")) {
            try {
                manager.openEntityEvent(uid, Integer.parseInt(text.replaceAll(".+(\\d+)", "$1")));
            } catch (Exception e) {
                new GuiNotification("Unable to open event");
            }
        }

        if (intention == EDIT_CLOSE) dispose();
        if (intention == EDIT_STAY_IN_FOREGROUND) {
            setAlwaysOnTop(true);
            Sleep.milliseconds(500);
            setAlwaysOnTop(false);
        }
    }

    private void updateTextArea() {
        ta_text.setText("");
        for (int i = scrollIndex; i < display.length; i++) ta_text.append(display[i] + "\n");
    }

    private int scrollIndex = 0;

    private void scroll(MouseWheelEvent evt) {
        if (evt.getUnitsToScroll() < 0) {
            if (scrollIndex > 0) scrollIndex -= 2;
        } else
            scrollIndex += 2;
        updateTextArea();
    }
}