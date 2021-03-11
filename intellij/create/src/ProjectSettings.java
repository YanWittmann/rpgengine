
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ProjectSettings extends Frame {
    public static ArrayList<String> settings = new ArrayList<String>();

    public ProjectSettings() {
        settings.clear();
        addAllRequired();
        setupSize();
        initComponents();
    }

    public ProjectSettings(String[] fileInput) {
        settings.clear();
        for (int i = 0; i < fileInput.length; i++)
            if (fileInput[i].contains(":")) settings.add(fileInput[i]);
        addAllRequired();
        setupSize();
        initComponents();
    }

    private void addAllRequired() {
        addIfNotContain("name", Manager.filename);
        addIfNotContain("description", "My great adventure");
        addIfNotContain("version", "1.0");
        addIfNotContain("author", StaticStuff.getWindowsUsername());
        addIfNotContain("image", Images.getTitleImageUID());
        addIfNotContain("language", "english");
        addIfNotContain("autocomplete", "");
        addIfNotContain("objectFrameVariables", "weight,damage,range,value,health,hands,armor");
        addIfNotContain("permissions", "");
        addIfNotContain("showIntro", "true");
        addIfNotContain("debugMode", "false");
        addIfNotContain("debugModeForceable", "false");
        addIfNotContain("password", "");
        addIfNotContain("requirePasswordToPlay", "false");
    }

    private void addIfNotContain(String option, String value) {
        for (int i = 0; i < settings.size(); i++)
            if (settings.get(i).contains(option + ":")) return;
        settings.add(option + ":" + value);
    }

    public void setOrAdd(String option, String value) {
        for (int i = 0; i < settings.size(); i++)
            if (settings.get(i).contains(option + ":")) {
                settings.set(i, option + ":" + value);
                return;
            }
        settings.add(option + ":" + value);
    }

    public String getValue(String name) {
        for (int i = 0; i < settings.size(); i++)
            if (settings.get(i).contains(name + ":")) return settings.get(i).replaceAll(".+:", "");
        return "notFound";
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

    public void find(String find, ArrayList<String> found) {
        if (StaticStuff.findInArrayList(find, settings)) found.add("Player settings: Tags");
    }

    public void save(String save) {
        String oldPassword = getValue("password");
        settings.clear();
        String set[] = save.split("\n");
        for (int i = 0; i < set.length; i++) {
            if (set[i].contains("password:") && !set[i].equals("password:"))
                if (!oldPassword.equals(set[i].replace("password:", ""))) {
                    set[i] = "password:" + set[i].replace("password:", "").hashCode();
                }
            if (set[i].contains(":")) settings.add(set[i]);
        }
        addAllRequired();
        dispose();
    }

    private JButton b_cancle;
    private JButton b_save;
    private JLabel l_info;
    private JTextArea ta_settings;

    public void openGui() {
        setVisible(true);
        ta_settings.setText(generateDisplayString());
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
        this.setTitle("Project settings");
        this.setSize(size_x, size_y);

        setBackground(StaticStuff.getColor("background"));
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(size_x, size_y));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/icongreen.png").getImage());

        b_cancle = new JButton();
        b_cancle.setBounds(354, 862, 90, 35);
        b_cancle.setBackground(StaticStuff.getColor("background"));
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
        b_save.setBackground(StaticStuff.getColor("background"));
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
        l_info.setBackground(StaticStuff.getColor("background"));
        l_info.setForeground(StaticStuff.getColor("text_color"));
        l_info.setEnabled(true);
        l_info.setFont(StaticStuff.getBaseFont());
        l_info.setText("<html><b>Project settings");
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
