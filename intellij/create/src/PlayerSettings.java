
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PlayerSettings extends Frame {
    public static ArrayList<String> settings = new ArrayList<String>();

    public PlayerSettings() {
        settings.clear();
        addAllRequired();
        setupSize();
        initComponents();
    }

    public PlayerSettings(String[] fileInput) {
        settings.clear();
        for (int i = 0; i < fileInput.length; i++)
            if (fileInput[i].contains(":")) settings.add(fileInput[i]);
        addAllRequired();
        setupSize();
        initComponents();
    }

    private void addAllRequired() {
        addIfNotContain("gold", "100");
        addIfNotContain("health", "0");
        addIfNotContain("maxHealth", "80");
        addIfNotContain("location", "");
        addIfNotContain("inventory", "");
        addIfNotContain("battleMapImage", "");
        addIfNotContain("courage", "10");
        addIfNotContain("wisdom", "10");
        addIfNotContain("intuition", "10");
        addIfNotContain("charisma", "10");
        addIfNotContain("dexterity", "10");
        addIfNotContain("agility", "10");
        addIfNotContain("strength", "10");
    }

    public void setValue(String option, String value) {
        addIfNotContain(option, value);
        for (int i = 0; i < settings.size(); i++)
            if (settings.get(i).contains(option + ":"))
                settings.set(i, settings.get(i).replaceAll("[^:]+:(.+)", "$1") + value);
    }

    private void addIfNotContain(String option, String value) {
        for (int i = 0; i < settings.size(); i++)
            if (settings.get(i).contains(option + ":")) return;
        settings.add(option + ":" + value);
    }

    public void openSettings() {
        openGui();
    }

    public String[] generateSaveString() {
        String[] dest = new String[settings.toArray().length];
        System.arraycopy(settings.toArray(), 0, dest, 0, settings.toArray().length);
        return dest;
    }

    public String generateDisplayString() {
        String ret = "";
        for (int i = 0; i < settings.size(); i++)
            ret += settings.get(i) + "\n";
        return ret;
    }

    public int refactor(String find, String replace) {
        int occ = 0;
        occ += StaticStuff.refactorArrayList(find, replace, settings);
        return occ;
    }

    private JButton b_cancle;
    private JButton b_save;
    private JLabel l_info;
    private JTextArea ta_settings;

    public void openGui() {
        ta_settings.setText(generateDisplayString());
        setVisible(true);
    }

    public void save(String save) {
        settings.clear();
        String set[] = save.split("\n");
        for (int i = 0; i < set.length; i++)
            if (set[i].contains(":")) settings.add(set[i]);
        addAllRequired();
        dispose();
    }

    public void cancle() {
        setVisible(false);
    }

    private int size_x = 582, size_y = 907;

    private void setupSize() {
        size_x = Math.min(StaticStuff.getScreenWidth() - 1000, 582);
        size_y = Math.min(StaticStuff.getScreenHeight() - 200, 907);
    }

    private void initComponents() {
        this.setTitle(StaticStuff.projectName);
        this.setSize(size_x, size_y);

        setBackground(StaticStuff.getColor("background"));
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(size_x, size_y));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/icongreen.png").getImage());

        b_cancle = new JButton();
        b_cancle.setBounds(354, 862, 90, 35);
        b_cancle.setBackground(StaticStuff.getColor("buttons"));
        b_cancle.setForeground(StaticStuff.getColor("text_color"));
        b_cancle.setEnabled(true);
        b_cancle.setFont(StaticStuff.getBaseFont());
        b_cancle.setText("Cancle");
        b_cancle.setVisible(true);
        b_cancle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancle();
            }
        });

        b_save = new JButton();
        b_save.setBounds(457, 862, 90, 35);
        b_save.setBackground(StaticStuff.getColor("buttons"));
        b_save.setForeground(StaticStuff.getColor("text_color"));
        b_save.setEnabled(true);
        b_save.setFont(StaticStuff.getBaseFont());
        b_save.setText("Save");
        b_save.setVisible(true);
        b_save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                save(ta_settings.getText());
            }
        });

        l_info = new JLabel();
        l_info.setBounds(15, 11, 90, 35);
        l_info.setBackground(StaticStuff.getColor("buttons"));
        l_info.setForeground(StaticStuff.getColor("text_color"));
        l_info.setEnabled(true);
        l_info.setFont(StaticStuff.getBaseFont());
        l_info.setText("<html><b>Player settings");
        l_info.setVisible(true);

        ta_settings = new JTextArea();
        ta_settings.setBounds(23, 53, 534, 801);
        ta_settings.setBackground(StaticStuff.getColor("text_background"));
        ta_settings.setForeground(StaticStuff.getColor("text_color"));
        ta_settings.setEnabled(true);
        ta_settings.setFont(StaticStuff.getBaseFont());
        ta_settings.setText(generateDisplayString());
        ta_settings.setBorder(BorderFactory.createBevelBorder(1));
        ta_settings.setVisible(true);

        contentPane.add(b_cancle);
        contentPane.add(b_save);
        contentPane.add(l_info);
        contentPane.add(ta_settings);

        this.add(contentPane);
        this.setLocationRelativeTo(null);
        this.pack();
        //this.setVisible(true);

        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                save(ta_settings.getText());
            }
        };
        contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "ESCAPE");
        contentPane.getActionMap().put("ESCAPE", escapeAction);

        addComponentListener(new ComponentAdapter() { // ta_settings (23,53,534,801) l_info (15,11,90,35) b_save (457,862,90,35) b_cancle (354,862,90,35)
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
        //get width and height (original: 598,946)
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
        l_info.setLocation(15, 11);
        ta_settings.setBounds(23, 53, width - 64, height - 145);
        b_save.setLocation(width - 141, height - 84);
        b_cancle.setLocation(width - 244, height - 84);
    }
}
