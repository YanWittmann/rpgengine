
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GuiActionEditor extends JFrame {
    private JButton b_save;
    private JButton b_editorOperation;
    private JLabel l_cond;
    private JLabel l_eventName;
    private JLabel l_info;
    private JTextArea ta_code;
    private JTextField tf_eventName;
    private final Entity entity;
    private final int event;
    private final String eventName;
    private final boolean isActionEditorOpenDirectlyInExternalEditor = Manager.isActionEditorOpenDirectlyInExternalEditor();

    public GuiActionEditor(Entity entity, int event) {
        int size_x = 1000;
        int size_y = Math.min(StaticStuff.getScreenHeight() - 200, 1000);
        this.entity = entity;
        this.event = event;
        this.eventName = entity.eventName.get(event);
        if (event < 0) {
            Popup.error(StaticStuff.projectName + " - Error", "This event does not exist");
            dispose();
            return;
        }

        ta_code = new JTextArea();
        ta_code.setBounds(30, 45, 940, 785);
        ta_code.setBackground(StaticStuff.getColor("text_background"));
        ta_code.setForeground(StaticStuff.getColor("text_color"));
        ta_code.setEnabled(true);
        ta_code.setFont(new Font("sansserif", Font.PLAIN, 12));
        ta_code.setText(entity.generateEventEditorString(entity.eventCode.get(event)));
        ta_code.setBorder(BorderFactory.createBevelBorder(1));
        ta_code.setVisible(true);

        if (!isActionEditorOpenDirectlyInExternalEditor) {
            this.setTitle(entity.name + " - Action editor");
            this.setSize(size_x, size_y);

            JPanel contentPane = new JPanel(null);
            contentPane.setPreferredSize(new Dimension(size_x, size_y));
            contentPane.setBackground(StaticStuff.getColor("background"));
            setIconImage(new ImageIcon("res/img/iconblue.png").getImage());

            l_cond = new JLabel();
            l_cond.setBounds(31, 10, 600, 35);
            l_cond.setBackground(StaticStuff.getColor("background"));
            l_cond.setForeground(StaticStuff.getColor("text_color"));
            l_cond.setEnabled(true);
            l_cond.setFont(StaticStuff.getBaseFont());
            l_cond.setText("<html><b>Code");
            l_cond.setVisible(true);

            l_info = new JLabel();
            l_info.setBounds(30, 847, 250, 39);
            l_info.setBackground(StaticStuff.getColor("background"));
            l_info.setForeground(StaticStuff.getColor("text_color"));
            l_info.setEnabled(true);
            l_info.setFont(StaticStuff.getBaseFont());
            l_info.setText("<html><b>" + entity.name + "  ---  " + entity.uid);
            l_info.setVisible(true);
            l_info.addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                    l_info.setText("<html><b>" + entity.name + "  ---  " + entity.uid);
                }

                public void mouseEntered(MouseEvent e) {
                    l_info.setText("<html><b>" + entity.uid + "  ---  " + entity.name);
                }

                public void mouseClicked(MouseEvent e) {
                }
            });

            l_eventName = new JLabel();
            l_eventName.setBounds(320, 849, 90, 35);
            l_eventName.setBackground(StaticStuff.getColor("background"));
            l_eventName.setForeground(StaticStuff.getColor("text_color"));
            l_eventName.setEnabled(true);
            l_eventName.setFont(StaticStuff.getBaseFont());
            l_eventName.setText("Event name");
            l_eventName.setVisible(true);

            tf_eventName = new JTextField();
            tf_eventName.setBounds(420, 849, 179, 34);
            tf_eventName.setBackground(StaticStuff.getColor("text_background"));
            tf_eventName.setForeground(StaticStuff.getColor("text_color"));
            tf_eventName.setEnabled(true);
            tf_eventName.setFont(StaticStuff.getBaseFont());
            tf_eventName.setText(eventName);
            tf_eventName.setVisible(true);

            b_editorOperation = new JButton();
            b_editorOperation.setBounds(610, 849, 170, 35);
            b_editorOperation.setBackground(StaticStuff.getColor("background"));
            b_editorOperation.setForeground(StaticStuff.getColor("text_color"));
            b_editorOperation.setEnabled(true);
            b_editorOperation.setFont(StaticStuff.getBaseFont());
            b_editorOperation.setText("Open in text editor");
            b_editorOperation.setVisible(true);
            b_editorOperation.addActionListener(evt -> editorOperation());

            b_save = new JButton();
            b_save.setBounds(880, 849, 90, 35);
            b_save.setBackground(StaticStuff.getColor("background"));
            b_save.setForeground(StaticStuff.getColor("text_color"));
            b_save.setEnabled(true);
            b_save.setFont(StaticStuff.getBaseFont());
            b_save.setText("Save");
            b_save.setVisible(true);
            b_save.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    save();
                }
            });

            contentPane.add(b_save);
            contentPane.add(b_editorOperation);
            contentPane.add(l_cond);
            contentPane.add(l_eventName);
            contentPane.add(l_info);
            contentPane.add(ta_code);
            contentPane.add(tf_eventName);

            this.add(contentPane);
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            this.setLocationRelativeTo(null);
            this.pack();
            this.setVisible(true);
        } else {
            FileManager.writeToFile("res/txt/actioneditor/" + entity.hashCode() + "_" + eventName + ".advtemp", entity.generateNotepadEventEditorString(entity.eventCode.get(event), eventName));
            FileManager.openFile("res/txt/actioneditor/" + entity.hashCode() + "_" + eventName + ".advtemp");
            FileManager.addWatchFile("res/txt/actioneditor/" + entity.hashCode() + "_" + eventName + ".advtemp", this);
            return;
        }

        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                save();
                dispose();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                save();
                dispose();
            }
        });

        addComponentListener(new ComponentAdapter() { // l_cond (31,10,600,35) ta_code (30,45,940,785) l_info (30,847,250,39) l_eventName (320,849,90,35) tf_eventName (420,849,179,34) b_editorOperation (610,849,170,35) b_save (880,849,90,35)
            @Override
            public void componentResized(ComponentEvent e) {
                resizeWindow(e);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }
        });

        resizeWindow(null);
    }

    private void resizeWindow(ComponentEvent e) {
        //get width and height (original: 1000,1000)
        int width = 300;
        int height = 300;
        if (e == null) {
            width = getWidth();
            height = getHeight();
        } else {
            width = e.getComponent().getWidth();
            height = e.getComponent().getHeight();
        }

        //set components location & size
        ta_code.setBounds(30, 45, width - 60, height - 115 - 40);
        l_info.setLocation(30, height - 53 - 40);
        l_eventName.setLocation(320, height - 51 - 40);
        tf_eventName.setLocation(420, height - 51 - 40);
        b_editorOperation.setLocation(610, height - 51 - 40);
        b_save.setLocation(880, height - 51 - 40);
    }

    private boolean currentEditorState = false;

    private void editorOperation() {
        if (!currentEditorState) {
            currentEditorState = true;
            l_cond.setText("<html><b>Save event and reopen it to continue editing here and not in editor");
            b_editorOperation.setText("Get from text editor");
            ta_code.setEnabled(false);
            tf_eventName.setEnabled(false);
            FileManager.writeToFile("res/txt/actioneditor/" + entity.hashCode() + "_" + eventName + ".advtemp", ta_code.getText());
            FileManager.openFile("res/txt/actioneditor/" + entity.hashCode() + "_" + eventName + ".advtemp");
            FileManager.addWatchFile("res/txt/actioneditor/" + entity.hashCode() + "_" + eventName + ".advtemp", this);
            /*try {
            Process p = Runtime.getRuntime().exec("notepad " + "res/txt/actioneditor/" + entity.hashCode() + ".advtemp");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            stdInput.close();
            } catch(Exception e) {
            FileManager.openFile("res/txt/actioneditor/" + entity.hashCode() + ".advtemp");
            }*/
        } else {
            setEditorText(FileManager.readFile("res/txt/actioneditor/" + entity.hashCode() + "_" + eventName + ".advtemp"));
        }
    }

    public void setEditorText(String[] text) {
        StringBuilder putToActionEditor = new StringBuilder();
        for (String s : text)
            putToActionEditor.append(s).append("\n");
        ta_code.setText(StaticStuff.replaceLast(putToActionEditor.toString(), "\n", ""));
        if (isActionEditorOpenDirectlyInExternalEditor) save();
    }

    private void save() {
        if(tf_eventName != null) entity.eventName.set(event, tf_eventName.getText());
        entity.setEventsFromEditor(event, ta_code.getText());
        if (currentEditorState && !isActionEditorOpenDirectlyInExternalEditor)
            FileManager.removeWatchFile("res/txt/actioneditor/" + entity.hashCode() + "_" + eventName + ".advtemp");
        if (isActionEditorOpenDirectlyInExternalEditor)
            new GuiNotification("Event saved: " + entity.name + " - " + eventName);
        dispose();
    }
}