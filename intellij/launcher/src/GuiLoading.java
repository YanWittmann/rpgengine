
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GuiLoading extends JFrame {
    private JMenuBar menuBar;
    private JLabel l_animation;
    private int ee = 0;
    private int pX, pY;

    public GuiLoading() {
        this.setTitle("RPG ENGINE");
        this.setSize(498, 290);
        setUndecorated(true);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(498, 290));
        contentPane.setBackground(new Color(255, 255, 255));
        setIconImage(new ImageIcon("files/res/img/iconyellow.png").getImage());

        l_animation = new JLabel();
        l_animation.setBounds(0, 0, 498, 290);
        l_animation.setBackground(new Color(214, 217, 223));
        l_animation.setForeground(new Color(0, 0, 0));
        l_animation.setText("<html><b>--> There should be an image right here... Did you delete the gif?<br>Why would you do this?");
        l_animation.setEnabled(true);
        l_animation.setVisible(true);

        contentPane.add(l_animation);

        l_animation.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                pX = me.getX();
                pY = me.getY();
            }

            public void mouseDragged(MouseEvent me) {
                setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });
        l_animation.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(false);

        try {
            ImageIcon ii = new ImageIcon("files/res/img/loading.gif");
            l_animation.setIcon(ii);
            l_animation.setBounds(0, 0, ii.getIconWidth(), ii.getIconHeight());
            this.setSize(ii.getIconWidth(), ii.getIconHeight());
            contentPane.setPreferredSize(new Dimension(ii.getIconWidth(), ii.getIconHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}