
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class GuiHoverText extends JFrame {
    int currentOpacity = 100;

    public GuiHoverText(String text) {
        text = "<html>" + text.replace("<html>", "");
        setAlwaysOnTop(true);
        setFocusableWindowState(false);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        this.setTitle("Display");
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        int textwidth = StaticStuff.getTextWidthWithFontRemoveFormatting(text, StaticStuff.getPixelatedFont());
        int textheight = (int) (StaticStuff.getPixelatedFont().getStringBounds(text, frc).getHeight());
        int sizeX = textwidth + 16, sizeY = textheight;
        this.setSize(sizeX, sizeY);

        JPanel contentPane = new JPanel(null) {
            public void paintComponent(Graphics g) {
                if (StaticStuff.getColorReady()) g.setColor(StaticStuff.getColor("white_border"));
                else g.setColor(Color.WHITE);
                g.fillRoundRect(0, 0, sizeX, sizeY, 20, 20);
                if (StaticStuff.getColorReady()) g.setColor(StaticStuff.getColor("background"));
                else g.setColor(Color.BLACK);
                g.fillRoundRect(3, 3, sizeX - 6, sizeY - 6, 20, 20);
            }
        };
        contentPane.setPreferredSize(new Dimension(sizeX, sizeY));
        contentPane.setBackground(new Color(20, 20, 20));

        JLabel l_message = new JLabel(text, SwingConstants.CENTER);
        l_message.setBounds(2, -7, sizeX, sizeY + 5);
        l_message.setBackground(new Color(214, 217, 223));
        l_message.setForeground(new Color(255, 255, 255));
        l_message.setEnabled(true);
        l_message.setFont(StaticStuff.getPixelatedFont());
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

        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        int x = (int) b.getX();
        int y = (int) b.getY();
        setLocation(x + 10, y + 10);

        this.setVisible(true);
        Thread opacity = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }
                for (currentOpacity = currentOpacity; currentOpacity > 0; currentOpacity--) {
                    try {
                        Thread.sleep(5);
                    } catch (Exception ignored) {
                    }
                    setOpacity(currentOpacity * 0.01f);
                }
                dispose();
            }
        };
        opacity.start();
    }

    public void close() {
        for (currentOpacity = currentOpacity; currentOpacity > 0; currentOpacity--) {
            try {
                Thread.sleep(3);
            } catch (Exception ignored) {
            }
            setOpacity(currentOpacity * 0.01f);
        }
        dispose();
    }
}
