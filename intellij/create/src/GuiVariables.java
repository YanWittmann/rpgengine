
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

public class GuiVariables extends JFrame{
    private JButton b_save;
    private JComboBox cb_type;
    private JTextField tf_name;
    private JTextField tf_value;
    private Variables vars;
    private int index;

    public GuiVariables(Variables vars, int index){
        this.vars = vars;
        this.index = index;
        this.setTitle(vars.name+" - Variable editor");
        this.setSize(530,79);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(530,79));
        contentPane.setBackground(new Color(240,240,240));
        setIconImage(new ImageIcon("res/img/icongreen.png").getImage());

        b_save = new JButton();
        b_save.setBounds(413,22,90,35);
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

        cb_type = new JComboBox(new String[]{"Integer","Float","String"});
        cb_type.setSelectedItem(vars.getType(index));
        cb_type.setBounds(145,22,90,35);
        cb_type.setBackground(new Color(214,217,223));
        cb_type.setForeground(new Color(0,0,0));
        cb_type.setEnabled(true);
        cb_type.setFont(new Font("sansserif",0,12));
        cb_type.setVisible(true);

        tf_name = new JTextField();
        tf_name.setBounds(18,22,120,35);
        tf_name.setBackground(new Color(255,255,255));
        tf_name.setForeground(new Color(0,0,0));
        tf_name.setEnabled(true);
        tf_name.setFont(new Font("sansserif",0,12));
        tf_name.setText(vars.getName(index));
        tf_name.setVisible(true);

        tf_value = new JTextField();
        tf_value.setBounds(244,22,150,35);
        tf_value.setBackground(new Color(255,255,255));
        tf_value.setForeground(new Color(0,0,0));
        tf_value.setEnabled(true);
        tf_value.setFont(new Font("sansserif",0,12));
        tf_value.setText(vars.getValue(index));
        tf_value.setVisible(true);

        contentPane.add(b_save);
        contentPane.add(cb_type);
        contentPane.add(tf_name);
        contentPane.add(tf_value);

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
        if(String.valueOf(cb_type.getSelectedItem()).equals("Integer")){
            try{
                Integer.parseInt(tf_value.getText());
                vars.setVariable(index, tf_name.getText(), String.valueOf(cb_type.getSelectedItem()), tf_value.getText());
                dispose();
            }catch(Exception e){Popup.error(StaticStuff.projectName,"Invalid integer value: "+tf_value.getText());}
        }else if(String.valueOf(cb_type.getSelectedItem()).equals("Float")){
            if(tf_value.getText().matches("-?\\d+\\.?\\d*")){
                vars.setVariable(index, tf_name.getText(), String.valueOf(cb_type.getSelectedItem()), tf_value.getText());
                dispose();
            }else Popup.error(StaticStuff.projectName,"Invalid float value: "+tf_value.getText());
        }else{
            vars.setVariable(index, tf_name.getText(), String.valueOf(cb_type.getSelectedItem()), tf_value.getText());
            dispose();
        }
    }
}