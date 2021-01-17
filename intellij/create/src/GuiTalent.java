
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.border.Border;
import javax.swing.*;

public class GuiTalent extends JFrame {
    private JComboBox cb_attr1;
    private JComboBox cb_attr2;
    private JComboBox cb_attr3;
    private JLabel l_info;
    private JLabel l_name;
    private JLabel l_attributes;
    private JTextField tf_name;
    private JButton b_save;
    
    private String attributes[] = StaticStuff.appendArray("courage, wisdom, intuition, charisma, dexterity, agility, strength".split(", "),"");
    private Talent talent;
    
    public GuiTalent(Talent talent){
        this.talent = talent;
        this.setTitle(talent.name+" - Talent editor");
        this.setSize(242,312);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(242,312));
        contentPane.setBackground(new Color(240,240,240));
        setIconImage(new ImageIcon("res/img/icongreen.png").getImage());

        cb_attr1 = new JComboBox(attributes);
        cb_attr1.setSelectedItem(talent.attr1);
        cb_attr1.setBounds(97,100,120,35);
        cb_attr1.setBackground(new Color(214,217,223));
        cb_attr1.setForeground(new Color(0,0,0));
        cb_attr1.setEnabled(true);
        cb_attr1.setFont(new Font("sansserif",0,12));
        cb_attr1.setVisible(true);

        cb_attr2 = new JComboBox(attributes);
        cb_attr2.setSelectedItem(talent.attr2);
        cb_attr2.setBounds(97,150,120,35);
        cb_attr2.setBackground(new Color(214,217,223));
        cb_attr2.setForeground(new Color(0,0,0));
        cb_attr2.setEnabled(true);
        cb_attr2.setFont(new Font("sansserif",0,12));
        cb_attr2.setVisible(true);

        cb_attr3 = new JComboBox(attributes);
        cb_attr3.setSelectedItem(talent.attr3);
        cb_attr3.setBounds(97,200,120,35);
        cb_attr3.setBackground(new Color(214,217,223));
        cb_attr3.setForeground(new Color(0,0,0));
        cb_attr3.setEnabled(true);
        cb_attr3.setFont(new Font("sansserif",0,12));
        cb_attr3.setVisible(true);

        l_info = new JLabel();
        l_info.setBounds(18,14,90,35);
        l_info.setBackground(new Color(214,217,223));
        l_info.setForeground(new Color(0,0,0));
        l_info.setEnabled(true);
        l_info.setFont(new Font("sansserif",0,12));
        l_info.setText("<html><b>Talent editor");
        l_info.setVisible(true);

        l_name = new JLabel("<html><b>Name", SwingConstants.RIGHT);
        l_name.setBounds(5,58,80,35);
        l_name.setBackground(new Color(214,217,223));
        l_name.setForeground(new Color(0,0,0));
        l_name.setEnabled(true);
        l_name.setFont(new Font("sansserif",0,12));
        l_name.setVisible(true);

        l_attributes = new JLabel("<html><b>Attributes", SwingConstants.RIGHT);
        l_attributes.setBounds(5,105,80,35);
        l_attributes.setBackground(new Color(214,217,223));
        l_attributes.setForeground(new Color(0,0,0));
        l_attributes.setEnabled(true);
        l_attributes.setFont(new Font("sansserif",0,12));
        l_attributes.setVisible(true);

        tf_name = new JTextField();
        tf_name.setBounds(97,58,120,35);
        tf_name.setBackground(new Color(255,255,255));
        tf_name.setForeground(new Color(0,0,0));
        tf_name.setEnabled(true);
        tf_name.setFont(new Font("sansserif",0,12));
        tf_name.setText(talent.name);
        tf_name.setVisible(true);
        
        b_save = new JButton();
        b_save.setBounds(97,250,90,35);
        b_save.setBackground(new Color(214,217,223));
        b_save.setForeground(new Color(0,0,0));
        b_save.setEnabled(true);
        b_save.setFont(new Font("sansserif",0,12));
        b_save.setText("Save");
        b_save.setVisible(true);
        b_save.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    save();
                }
            });

        contentPane.add(cb_attr1);
        contentPane.add(cb_attr2);
        contentPane.add(cb_attr3);
        contentPane.add(l_info);
        contentPane.add(l_name);
        contentPane.add(l_attributes);
        contentPane.add(tf_name);
        contentPane.add(b_save);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
        
        Action escapeAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    save();
                }
            };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }
    
    private void save(){
        if(cb_attr1.getSelectedItem().toString().length() == 0) {
            Popup.error("Talent save", "First slot cannot be empty");
            return;
        } else if (cb_attr2.getSelectedItem().toString().length() == 0 && cb_attr3.getSelectedItem().toString().length() > 0) {
            Popup.error("Talent save", "Fill up the attributes from top to bottom");
            return;
        }
        talent.name = tf_name.getText();
        talent.attr1 = cb_attr1.getSelectedItem().toString();
        talent.attr2 = cb_attr2.getSelectedItem().toString();
        talent.attr3 = cb_attr3.getSelectedItem().toString();
        dispose();
    }

}