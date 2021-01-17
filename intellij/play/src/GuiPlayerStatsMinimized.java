
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class GuiPlayerStatsMinimized extends JFrame {
    private static boolean battleMode = false;
    private JLabel l_projectIcon;
    private GuiPlayerStats playerStats;
    private int x_size = Interpreter.getScaledValue(51), y_size = x_size, icon_offset = Interpreter.getScaledValue(10);

    public GuiPlayerStatsMinimized(PlayerSettings player, GuiPlayerStats playerStats, BufferedImage image) {
        this.setTitle(player.getValue("name"));
        this.setSize(x_size, y_size);
        this.playerStats = playerStats;

        JPanel contentPane = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                g.setColor(StaticStuff.getColor("white_border"));
                g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
                g.setColor(StaticStuff.getColor("background"));
                g.fillRoundRect(3, 3, x_size - 6, y_size - 6, 20, 20);
            }
        };
        setUndecorated(true);
        contentPane.setPreferredSize(new Dimension(x_size, y_size));
        contentPane.setBackground(new Color(0, 0, 0, 0));
        setBackground(new Color(0, 0, 0, 0));
        setIconImage(new ImageIcon("res/img/iconblue.png").getImage());
        addListener(this);

        l_projectIcon = new JLabel("", SwingConstants.CENTER);
        l_projectIcon.setBounds(icon_offset, icon_offset, x_size - (icon_offset * 2), y_size - (icon_offset * 2));
        l_projectIcon.setBackground(new Color(214, 217, 223));
        l_projectIcon.setForeground(new Color(255, 255, 255));
        l_projectIcon.setEnabled(true);
        l_projectIcon.setVisible(true);
        l_projectIcon.setIcon(getScaledImage(new ImageIcon(image), x_size - (icon_offset * 2), y_size - (icon_offset * 2)));
        contentPane.add(l_projectIcon);

        JLabel l_dummy = new JLabel("");
        l_dummy.setBounds(0, 0, 1, 1);
        l_dummy.setEnabled(true);
        l_dummy.setVisible(true);
        contentPane.add(l_dummy);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        setLocation(150, 10);
        this.pack();
        this.setVisible(false);

    }

    //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon; Thanks to trolologuy!
    private ImageIcon getScaledImage(ImageIcon srcImg, int w, int h) {
        Image image = srcImg.getImage();
        Image newimg = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    int pX, pY;
    private boolean dragActive = false;

    private void addListener(Component c) {
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON2 && !battleMode) {
                    for (int currentOpacity = 100; currentOpacity > 0; currentOpacity -= 3) {
                        try {
                            Thread.sleep(2);
                        } catch (Exception e) {
                        }
                        setOpacity(currentOpacity * 0.01f);
                    }
                    setVisible(false);
                    playerStats.open(getX(), getY());
                }
                dragActive = true;
                pX = me.getX();
                pY = me.getY();
                toFront();
                repaint();
            }

            public void mouseReleased(MouseEvent me) {
                dragActive = false;
            }

            public void mouseDragged(MouseEvent me) {
                if (dragActive) setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });
        c.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                if (dragActive) setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });
    }

    public void open(int x, int y) {
        setLocation(x, y);
        setVisible(true);
        setOpacity(1f);
        openAnimation();
    }

    private void openAnimation() {
        new Thread() {
            public void run() {
                l_projectIcon.setVisible(false);
                for (int i = 0; i < 8; i++) {
                    l_projectIcon.setVisible((i % 2) == 0);
                    try {
                        Thread.sleep(30 + (20 * i));
                    } catch (Exception e) {
                    }
                }
                l_projectIcon.setVisible(true);
            }
        }.start();
    }

    public void hideFrame() {
        setVisible(false);
    }

    public void battleMode(boolean active) {
        battleMode = active;
    }
}
