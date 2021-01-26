
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiCreateNewAdventure extends JFrame {
    private JMenuBar menuBar;
    private JLabel l_createTitle;
    private JButton b_create;
    private JComboBox cb_language;
    private JComboBox cb_talents;
    private JLabel l_author;
    private JLabel l_language;
    private JLabel l_nameTitle;
    private JLabel l_talentsTitle;
    private JTextField tf_advName;
    private JTextField tf_author;
    private Manager manager;

    public GuiCreateNewAdventure(Manager manager) {
        this.manager = manager;
        this.setTitle(StaticStuff.projectName + " Create a new adventure!");
        this.setSize(321, 264);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(321, 264));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/icongreen.png").getImage());

        l_createTitle = new JLabel();
        l_createTitle.setBounds(16, 15, 140, 25);
        l_createTitle.setBackground(StaticStuff.getColor("buttons"));
        l_createTitle.setForeground(StaticStuff.getColor("text_color"));
        l_createTitle.setEnabled(true);
        l_createTitle.setFont(StaticStuff.getBaseFont());
        l_createTitle.setText("<html><b>Create a new adventure");
        l_createTitle.setVisible(true);

        b_create = new JButton();
        b_create.setBounds(116, 210, 88, 29);
        b_create.setBackground(StaticStuff.getColor("buttons"));
        b_create.setForeground(StaticStuff.getColor("text_color"));
        b_create.setEnabled(true);
        b_create.setFont(StaticStuff.getBaseFont());
        b_create.setText("Start");
        b_create.setVisible(true);
        b_create.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                start();
            }
        });

        cb_language = new JComboBox(new String[]{"english", "german"});
        cb_language.setBounds(132, 120, 149, 31);
        cb_language.setBackground(StaticStuff.getColor("buttons"));
        cb_language.setForeground(StaticStuff.getColor("text_color"));
        cb_language.setEnabled(true);
        cb_language.setFont(StaticStuff.getBaseFont());
        cb_language.setVisible(true);

        cb_talents = new JComboBox(new String[]{"D&D", "DSA", "Pathfinder", "Empty"});
        cb_talents.setBounds(132, 155, 149, 31);
        cb_talents.setBackground(StaticStuff.getColor("buttons"));
        cb_talents.setForeground(StaticStuff.getColor("text_color"));
        cb_talents.setEnabled(true);
        cb_talents.setFont(StaticStuff.getBaseFont());
        cb_talents.setVisible(true);

        l_author = new JLabel();
        l_author.setBounds(33, 88, 70, 24);
        l_author.setBackground(StaticStuff.getColor("buttons"));
        l_author.setForeground(StaticStuff.getColor("text_color"));
        l_author.setEnabled(true);
        l_author.setFont(StaticStuff.getBaseFont());
        l_author.setText("Author");
        l_author.setVisible(true);

        l_language = new JLabel();
        l_language.setBounds(33, 123, 70, 24);
        l_language.setBackground(StaticStuff.getColor("buttons"));
        l_language.setForeground(StaticStuff.getColor("text_color"));
        l_language.setEnabled(true);
        l_language.setFont(StaticStuff.getBaseFont());
        l_language.setText("Language");
        l_language.setVisible(true);

        l_nameTitle = new JLabel();
        l_nameTitle.setBounds(33, 53, 70, 24);
        l_nameTitle.setBackground(StaticStuff.getColor("buttons"));
        l_nameTitle.setForeground(StaticStuff.getColor("text_color"));
        l_nameTitle.setEnabled(true);
        l_nameTitle.setFont(StaticStuff.getBaseFont());
        l_nameTitle.setText("Name");
        l_nameTitle.setVisible(true);

        l_talentsTitle = new JLabel();
        l_talentsTitle.setBounds(33, 158, 120, 24);
        l_talentsTitle.setBackground(StaticStuff.getColor("buttons"));
        l_talentsTitle.setForeground(StaticStuff.getColor("text_color"));
        l_talentsTitle.setEnabled(true);
        l_talentsTitle.setFont(StaticStuff.getBaseFont());
        l_talentsTitle.setText("Talents preset");
        l_talentsTitle.setVisible(true);

        tf_advName = new JTextField();
        tf_advName.setBounds(132, 50, 149, 31);
        tf_advName.setBackground(StaticStuff.getColor("text_background"));
        tf_advName.setForeground(StaticStuff.getColor("text_color"));
        tf_advName.setEnabled(true);
        tf_advName.setFont(StaticStuff.getBaseFont());
        tf_advName.setText(StaticStuff.generateRandomMessageFromFile("res/txt/filename" + StaticStuff.dataFileEnding));
        tf_advName.setVisible(true);
        tf_advName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                start();
            }
        });

        tf_author = new JTextField();
        tf_author.setBounds(132, 85, 149, 31);
        tf_author.setBackground(StaticStuff.getColor("text_background"));
        tf_author.setForeground(StaticStuff.getColor("text_color"));
        tf_author.setEnabled(true);
        tf_author.setFont(StaticStuff.getBaseFont());
        tf_author.setText(StaticStuff.getWindowsUsername());
        tf_author.setVisible(true);
        tf_author.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                start();
            }
        });

        contentPane.add(l_createTitle);
        contentPane.add(b_create);
        contentPane.add(cb_language);
        contentPane.add(cb_talents);
        contentPane.add(l_author);
        contentPane.add(l_language);
        contentPane.add(l_nameTitle);
        contentPane.add(l_talentsTitle);
        contentPane.add(tf_advName);
        contentPane.add(tf_author);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    private void start() {
        String name = tf_advName.getText();
        String author = tf_author.getText();
        String lang = String.valueOf(cb_language.getSelectedItem());
        String talentsType = String.valueOf(cb_talents.getSelectedItem());
        if (name == null) return;
        if (name.equals("")) return;
        if (author == null) return;
        if (author.equals("")) return;
        if (lang == null) return;
        if (lang.equals("")) return;
        if (talentsType == null) return;
        if (talentsType.equals("")) return;
        manager.createNewWithSettings(name, author, lang, talentsType);
        dispose();
    }
}