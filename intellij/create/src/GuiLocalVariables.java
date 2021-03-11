
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class GuiLocalVariables extends JFrame{
    private JButton b_save;
    private JComboBox cb_type;
    private JTextField tf_name;
    private JTextField tf_value;
    private Entity entity;
    private String uid;

    public GuiLocalVariables(Entity entity, String uid){
        this.entity = entity;
        this.uid = uid;
        this.setTitle(uid+" - Local variable editor");
        this.setSize(530,79);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(530,79));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/icongreen.png").getImage());

        b_save = new JButton();
        b_save.setBounds(413,22,90,35);
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

        cb_type = new JComboBox(new String[]{"Integer","Float","String"});
        cb_type.setSelectedItem(entity.getVarData(0,uid));
        cb_type.setBounds(145,22,90,35);
        cb_type.setBackground(StaticStuff.getColor("buttons"));
        cb_type.setForeground(StaticStuff.getColor("text_color"));
        cb_type.setEnabled(true);
        cb_type.setFont(StaticStuff.getBaseFont());
        cb_type.setVisible(true);

        tf_name = new JTextField();
        tf_name.setBounds(18,22,120,35);
        tf_name.setBackground(StaticStuff.getColor("text_background"));
        tf_name.setForeground(StaticStuff.getColor("text_color"));
        tf_name.setEnabled(true);
        tf_name.setFont(StaticStuff.getBaseFont());
        tf_name.setText(entity.getVarData(1,uid));
        tf_name.setVisible(true);

        tf_value = new JTextField();
        tf_value.setBounds(244,22,150,35);
        tf_value.setBackground(StaticStuff.getColor("text_background"));
        tf_value.setForeground(StaticStuff.getColor("text_color"));
        tf_value.setEnabled(true);
        tf_value.setFont(StaticStuff.getBaseFont());
        tf_value.setText(entity.getVarData(2,uid));
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
                entity.setVariable(uid, tf_name.getText(), String.valueOf(cb_type.getSelectedItem()), tf_value.getText());
                dispose();
            }catch(Exception e){Popup.error(StaticStuff.PROJECT_NAME,"Invalid integer value: "+tf_value.getText());}
        }else if(String.valueOf(cb_type.getSelectedItem()).equals("Float")){
            if(tf_value.getText().matches("-?\\d+\\.?\\d*")){
                entity.setVariable(uid, tf_name.getText(), String.valueOf(cb_type.getSelectedItem()), tf_value.getText());
                dispose();
            }else Popup.error(StaticStuff.PROJECT_NAME,"Invalid float value: "+tf_value.getText());
        }else{
            entity.setVariable(uid, tf_name.getText(), String.valueOf(cb_type.getSelectedItem()), tf_value.getText());
            dispose();
        }
    }
}
