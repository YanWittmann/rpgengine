import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class PopupSmallButtons extends JFrame {
    private int x_size = 80, y_size = 2;
    public int selected = -1;
    private final static float SMALL_BUTTONS_TEXT_SIZE = 17f;

    public PopupSmallButtons(String[] options, boolean closeIfMouseTooFarAway) {
        try {
            for (String s : options) {
                s = "<html>" + StaticStuff.prepareString("[[white:" + s + "]]");
                int currentWidth = FindRealTextSize.getTextWidth(s, StaticStuff.getPixelatedFont(SMALL_BUTTONS_TEXT_SIZE));
                x_size = Math.max(x_size, currentWidth);
            }
            x_size += 28;
            int textHeight = 28;
            y_size = Interpreter.getScaledValue(22) + (options.length * textHeight);

            JPanel contentPane = new JPanel(null) {
                public void paintComponent(Graphics g) {
                    g.setColor(StaticStuff.getColor("white_border"));
                    g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
                    g.setColor(StaticStuff.getColor("background"));
                    g.fillRoundRect(Interpreter.getScaledValue(3), Interpreter.getScaledValue(3), x_size - Interpreter.getScaledValue(6), y_size - Interpreter.getScaledValue(6), 20, 20);
                }
            };
            setUndecorated(true);
            setAlwaysOnTop(true);
            contentPane.setBackground(new Color(0, 0, 0, 0));
            setBackground(new Color(0, 0, 0, 0));
            setIconImage(new ImageIcon("res/img/icongreen.png").getImage());
            this.setSize(x_size, y_size);
            contentPane.setPreferredSize(new Dimension(x_size, y_size));

            JLabel[] l_button = new JLabel[options.length];
            for (int i = 0; i < options.length; i++) {
                l_button[i] = new JLabel(options[i].replaceAll("\\[\\[[^:]+:", "").replace("]]", ""));
                l_button[i].setBounds(Interpreter.getScaledValue(18), (Interpreter.getScaledValue(7) + (i * textHeight)), x_size, textHeight);
                l_button[i].setBackground(StaticStuff.getColor("def_text_color_main"));
                l_button[i].setForeground(StaticStuff.getColor("def_text_color_main"));
                l_button[i].setEnabled(true);
                l_button[i].setFont(StaticStuff.getPixelatedFont(SMALL_BUTTONS_TEXT_SIZE));
                l_button[i].setVisible(true);
                int finalI = i;
                l_button[i].addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        click(finalI);
                    }

                    final String withFormatting = "<html>" + StaticStuff.prepareString("[[aqua:" + options[finalI] + "]]");
                    final String withoutFormatting = options[finalI].replaceAll("\\[\\[[^:]+:", "").replace("]]", "");

                    public void mouseEntered(MouseEvent e) {
                        l_button[finalI].setText(withFormatting);
                    }

                    public void mouseExited(MouseEvent e) {
                        l_button[finalI].setText(withoutFormatting);
                    }
                });
                contentPane.add(l_button[i]);
            }

            this.add(contentPane);
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            int[] xy = StaticStuff.getMouseLocation();
            int[] dimensions = StaticStuff.getScreenDimensions();
            if(xy[0] + x_size > dimensions[0]) xy[0] = dimensions[0] - x_size - 20;
            if(xy[1] + y_size > dimensions[1]) xy[1] = dimensions[1] - y_size - 20;
            this.setLocation(xy[0], xy[1]);
            this.pack();
            this.setVisible(true);

            addAllListeners(this);

            if (closeIfMouseTooFarAway)
                new Thread(() -> {
                    do {
                        Sleep.milliseconds(500);
                    } while (!checkIfShouldClose());
                    click(-2);
                }).start();
        } catch (Exception e) {
            Log.add("Unable to open small buttons popup");
        }
    }

    public boolean checkIfShouldClose() {
        int[] xy = StaticStuff.getMouseLocation();
        int x = xy[0];
        int y = xy[1];
        int distX = 9999, distY = 9999;

        if (getX() > x) //mouse is on left side of frame
            distX = Math.min(distX, getX() - x);
        else if (getX() <= x && getX() + getWidth() >= x) //mouse is inside of frame
            distX = 0;
        else //mouse is on right side of frame
            distX = Math.min(distX, x - (getX() + getWidth()));

        if (getY() > y) //mouse is below frame
            distY = Math.min(distY, getY() - y);
        else if (getY() <= y && getY() + getWidth() >= y) //mouse is inside of frame
            distY = 0;
        else //mouse is above frame
            distY = Math.min(distY, y - (getY() + getWidth()));

        return Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)) > 200;
    }

    private void click(int index) {
        selected = index;
        for (int currentOpacity = 100; currentOpacity > 0; currentOpacity -= 3) {
            try {
                Thread.sleep(2);
            } catch (Exception ignored) {
            }
            setOpacity(currentOpacity * 0.01f);
        }
        dispose();
    }

    int pX, pY;
    private boolean dragActive = false;

    private void addAllListeners(Component c) {
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
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

    public void close() {
        Log.add("Closing small buttons popup");
        dispose();
    }
}
