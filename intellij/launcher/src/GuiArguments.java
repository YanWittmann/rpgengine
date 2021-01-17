
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiArguments extends JFrame {
    private JButton b_addArgs;
    private JButton b_removeArgs;
    private JButton b_editArgs;
    private JButton b_start;
    private JLabel l_title;
    private JList list_args;
    private JPanel contentPane = new JPanel(null);
    private Launcher launcher;

    //Constructor 
    public GuiArguments(Launcher launcher, String arguments[], String versionToStart) {
        this.launcher = launcher;
        this.setTitle("RPG Engine - Launcher - Arguments");
        this.setSize(224, 319);

        contentPane.setPreferredSize(new Dimension(224, 319));
        contentPane.setBackground(new Color(0, 0, 0));
        setIconImage(new ImageIcon("files/res/img/iconred.png").getImage());
        setUndecorated(true);

        b_addArgs = new JButton();
        b_addArgs.setBounds(16, 213, 90, 35);
        b_addArgs.setBackground(new Color(214, 217, 223));
        b_addArgs.setForeground(new Color(0, 0, 0));
        b_addArgs.setEnabled(true);
        b_addArgs.setFont(new Font("sansserif", 0, 12));
        b_addArgs.setText("Add");
        b_addArgs.setVisible(true);
        b_addArgs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addArgs();
            }
        });

        b_removeArgs = new JButton();
        b_removeArgs.setBounds(118, 213, 90, 35);
        b_removeArgs.setBackground(new Color(214, 217, 223));
        b_removeArgs.setForeground(new Color(0, 0, 0));
        b_removeArgs.setEnabled(true);
        b_removeArgs.setFont(new Font("sansserif", 0, 12));
        b_removeArgs.setText("Remove");
        b_removeArgs.setVisible(true);
        b_removeArgs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeArgs();
            }
        });

        b_editArgs = new JButton();
        b_editArgs.setBounds(118, 259, 90, 35);
        b_editArgs.setBackground(new Color(214, 217, 223));
        b_editArgs.setForeground(new Color(0, 0, 0));
        b_editArgs.setEnabled(true);
        b_editArgs.setFont(new Font("sansserif", 0, 12));
        b_editArgs.setText("Edit");
        b_editArgs.setVisible(true);
        b_editArgs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editArgs();
            }
        });

        b_start = new JButton();
        b_start.setBounds(16, 259, 90, 35);
        b_start.setBackground(new Color(214, 217, 223));
        b_start.setForeground(new Color(0, 0, 0));
        b_start.setEnabled(true);
        b_start.setFont(new Font("sansserif", 0, 12));
        b_start.setText("Start");
        b_start.setVisible(true);
        b_start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                startAdventure(versionToStart);
            }
        });

        l_title = new JLabel();
        l_title.setBounds(17, 17, 137, 16);
        l_title.setBackground(new Color(214, 217, 223));
        l_title.setForeground(new Color(255, 255, 255));
        l_title.setEnabled(true);
        l_title.setFont(new Font("sansserif", 0, 12));
        l_title.setText("<html><b>Start with parameters");
        l_title.setVisible(true);

        list_args = new JList(arguments);
        list_args.setBounds(28, 55, 145, 141);
        list_args.setBackground(new Color(255, 255, 255));
        list_args.setForeground(new Color(0, 0, 0));
        list_args.setEnabled(true);
        list_args.setFont(new Font("sansserif", 0, 12));
        list_args.setVisible(true);

        //adding components to contentPane panel
        contentPane.add(b_addArgs);
        contentPane.add(b_removeArgs);
        contentPane.add(b_editArgs);
        contentPane.add(b_start);
        contentPane.add(l_title);
        contentPane.add(list_args);

        //adding panel to JFrame and seting of window position and close operation
        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    private void addArgs() {
        String newArgs = JOptionPane.showInputDialog("Enter a new argument ('name:value'):");
        if (newArgs == null) return;
        if (newArgs.equals("")) return;
        if (!newArgs.matches("[^:]+:.+")) return;
        String[] old = new String[list_args.getModel().getSize() + 1];
        for (int i = 0; i < list_args.getModel().getSize(); i++) {
            old[i] = String.valueOf(list_args.getModel().getElementAt(i));
        }
        old[old.length - 1] = newArgs;

        list_args.setVisible(false);
        contentPane.remove(list_args);
        list_args = new JList(old);
        list_args.setBounds(28, 55, 145, 141);
        list_args.setBackground(new Color(255, 255, 255));
        list_args.setForeground(new Color(0, 0, 0));
        list_args.setEnabled(true);
        list_args.setFont(new Font("sansserif", 0, 12));
        list_args.setVisible(true);
        contentPane.add(list_args);
    }

    private void removeArgs() {
        int index = list_args.getSelectedIndex();
        if (index == -1) return;
        String[] old = new String[list_args.getModel().getSize()];
        for (int i = 0; i < list_args.getModel().getSize(); i++)
            old[i] = String.valueOf(list_args.getModel().getElementAt(i));
        String[] newArgs = new String[list_args.getModel().getSize() - 1];
        int counter = 0;
        for (int i = 0; i < list_args.getModel().getSize(); i++) {
            if (index == i) continue;
            newArgs[counter] = old[i];
            counter++;
        }

        list_args.setVisible(false);
        contentPane.remove(list_args);
        list_args = new JList(newArgs);
        list_args.setBounds(28, 55, 145, 141);
        list_args.setBackground(new Color(255, 255, 255));
        list_args.setForeground(new Color(0, 0, 0));
        list_args.setEnabled(true);
        list_args.setFont(new Font("sansserif", 0, 12));
        list_args.setVisible(true);
        contentPane.add(list_args);
    }

    private void editArgs() {
        int index = list_args.getSelectedIndex();
        if (index == -1) return;
        String[] old = new String[list_args.getModel().getSize()];
        for (int i = 0; i < list_args.getModel().getSize(); i++)
            old[i] = String.valueOf(list_args.getModel().getElementAt(i));
        String argsName = old[index].replaceAll("([^:]+):.+", "$1");
        String newArgs = JOptionPane.showInputDialog("Enter the new value for the argument '" + argsName + "':");
        if (newArgs == null) return;
        if (newArgs.equals("")) return;
        old[index] = argsName + ":" + newArgs;

        list_args.setVisible(false);
        contentPane.remove(list_args);
        list_args = new JList(old);
        list_args.setBounds(28, 55, 145, 141);
        list_args.setBackground(new Color(255, 255, 255));
        list_args.setForeground(new Color(0, 0, 0));
        list_args.setEnabled(true);
        list_args.setFont(new Font("sansserif", 0, 12));
        list_args.setVisible(true);
        contentPane.add(list_args);
    }

    private void startAdventure(String version) {
        String[] args = new String[list_args.getModel().getSize()];
        for (int i = 0; i < list_args.getModel().getSize(); i++)
            args[i] = String.valueOf(list_args.getModel().getElementAt(i));
        launcher.setArguments(args);
        launcher.startVersion(version, "play");
    }

}