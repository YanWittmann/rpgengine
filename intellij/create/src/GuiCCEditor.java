
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GuiCCEditor extends JFrame {
    private JMenuBar menuBar;
    private JButton b_cancle;
    private JButton b_save;
    private JLabel l_parameters;
    private JLabel l_syntax;
    private JLabel l_title;
    private JTextField tf_parameters;
    private JTextField tf_syntax;
    private CustomCommand cc;

    public GuiCCEditor(CustomCommand cc) {
        this.cc = cc;
        this.setTitle(cc.name + " - CustomCommand editor");
        this.setSize(500, 250);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(500, 250));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/iconblue.png").getImage());

        b_cancle = new JButton();
        b_cancle.setBounds(254, 203, 90, 35);
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
        b_save.setBounds(364, 203, 90, 35);
        b_save.setBackground(StaticStuff.getColor("buttons"));
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

        l_parameters = new JLabel();
        l_parameters.setBounds(29, 134, 200, 23);
        l_parameters.setBackground(StaticStuff.getColor("buttons"));
        l_parameters.setForeground(StaticStuff.getColor("text_color"));
        l_parameters.setEnabled(true);
        l_parameters.setFont(StaticStuff.getBaseFont());
        l_parameters.setText("<html><b>Parameters (split with ';'):");
        l_parameters.setVisible(true);

        l_syntax = new JLabel();
        l_syntax.setBounds(29, 58, 111, 23);
        l_syntax.setBackground(StaticStuff.getColor("buttons"));
        l_syntax.setForeground(StaticStuff.getColor("text_color"));
        l_syntax.setEnabled(true);
        l_syntax.setFont(StaticStuff.getBaseFont());
        l_syntax.setText("<html><b>Command syntax:");
        l_syntax.setVisible(true);

        l_title = new JLabel();
        l_title.setBounds(14, 15, 151, 30);
        l_title.setBackground(StaticStuff.getColor("buttons"));
        l_title.setForeground(StaticStuff.getColor("text_color"));
        l_title.setEnabled(true);
        l_title.setFont(StaticStuff.getBaseFont());
        l_title.setText("<html><b>CustomCommand editor");
        l_title.setVisible(true);

        tf_parameters = new JTextField();
        tf_parameters.setBounds(18, 160, 460, 35);
        tf_parameters.setBackground(StaticStuff.getColor("text_background"));
        tf_parameters.setForeground(StaticStuff.getColor("text_color"));
        tf_parameters.setEnabled(true);
        tf_parameters.setFont(StaticStuff.getBaseFont());
        tf_parameters.setText(cc.parameters);
        tf_parameters.setVisible(true);

        tf_syntax = new JTextField();
        tf_syntax.setBounds(18, 84, 460, 35);
        tf_syntax.setBackground(StaticStuff.getColor("text_background"));
        tf_syntax.setForeground(StaticStuff.getColor("text_color"));
        tf_syntax.setEnabled(true);
        tf_syntax.setFont(StaticStuff.getBaseFont());
        tf_syntax.setText(cc.syntaxRegex);
        tf_syntax.setVisible(true);
        tf_syntax.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                syntaxChange();
            }
        });

        contentPane.add(b_cancle);
        contentPane.add(b_save);
        contentPane.add(l_parameters);
        contentPane.add(l_syntax);
        contentPane.add(l_title);
        contentPane.add(tf_parameters);
        contentPane.add(tf_syntax);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    private void cancle() {
        dispose();
    }

    private void save() {
        if (checkIfValidRegex(tf_syntax.getText())) {
            cc.syntaxRegex = tf_syntax.getText();
            cc.parameters = tf_parameters.getText();
            dispose();
        } else {
            Popup.error(StaticStuff.projectName, "Unable to save.\nInvalid regular expression found:\n" + tf_syntax.getText());
        }
    }

    private Color white = StaticStuff.getColor("text_background"), red = StaticStuff.getColor("error_red");

    private void syntaxChange() {
        if (checkIfValidRegex(tf_syntax.getText())) tf_syntax.setBackground(white);
        else tf_syntax.setBackground(red);
    }

    private boolean checkIfValidRegex(String regex) {
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException exception) {
            return false;
        }
        return true;
    }

}