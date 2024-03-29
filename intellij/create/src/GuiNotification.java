
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

public class GuiNotification extends JFrame {
    private JLabel l_message;
    int currentOpacity=100;
    String filename;
    boolean incHeight;
    private static int amountPopupsOpen = 0;

    public GuiNotification(String text){
        incHeight=true;
        //Toolkit.getDefaultToolkit().beep();
        setAlwaysOnTop(true);
        setFocusableWindowState(false);
        this.setTitle("Display");
        this.setSize(266,53);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(500,53));
        contentPane.setBackground(new Color(255,255,255));
        setUndecorated(true);

        l_message = new JLabel();
        l_message.setBounds(8,4,490,44);
        l_message.setBackground(new Color(214,217,223));
        l_message.setForeground(new Color(0,0,0));
        l_message.setEnabled(true);
        l_message.setFont(new Font("sansserif",0,15));
        l_message.setText(text);
        l_message.setVisible(true);
        l_message.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    close();
                }
            });

        contentPane.add(l_message);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - getWidth()-8;
        int y = (int) rect.getMaxY() - getHeight()-8;
        if(amountPopupsOpen<0) amountPopupsOpen=0;
        y=y-(amountPopupsOpen*60);
        if(y<=50){y=(int) rect.getMaxY() - getHeight()-8;x=x-510;}
        setLocation(x, y);

        if(incHeight)amountPopupsOpen++;
        this.setVisible(true);
        Thread opacity = new Thread() {
                public void run() {
                    try{Thread.sleep(3000);}catch(Exception e){}
                    for(currentOpacity=currentOpacity;currentOpacity>0;currentOpacity--){
                        try{Thread.sleep(40);}catch(Exception e){}
                        setOpacity(currentOpacity*0.01f);
                    }
                    if(incHeight)amountPopupsOpen--;
                    dispose();
                }  
            };
        opacity.start();
    }

    private void close(){
        for(currentOpacity=currentOpacity;currentOpacity>0;currentOpacity--){
            try{Thread.sleep(3);}catch(Exception e){}
            setOpacity(currentOpacity*0.01f);
        }
        if(incHeight)amountPopupsOpen--;
        dispose();
    }
}