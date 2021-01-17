
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PopupText {
    private JWindow w;
    private int x_size, y_size;
    public int selected = -1;
    private String text, buttons[];

    PopupText(String text) {
        this.text = StaticStuff.prepareString(text);
        this.buttons = new String[]{"<html>" + StaticStuff.prepareString("[[def_text_color_buttons:OK]]")};
        int textWidth = StaticStuff.getLongestLineWidth(text.split("<br>"), StaticStuff.getPixelatedFont());
        this.x_size = Math.max(textWidth, 180);
        this.y_size = (StaticStuff.countOccurrences(text, "<br>") * 49) + (buttons.length * 57) + 49 + 130;
        this.y_size = Interpreter.getScaledValue(this.y_size);
    }

    public void createComponents() {
        w = new JWindow();
        w.setBackground(new Color(0, 0, 0, 0));
        w.setAlwaysOnTop(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        //figure out required size
        JPanel p = new JPanel(new BorderLayout());
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel l = new JLabel(("<html><center><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-</font><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("white")) + "\">" +
                text.replace("<br>", "<font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-<br>-</font>") +
                "</font><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-</font>"));
        l.setFont(StaticStuff.getPixelatedFont());
        l.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor("white_border"), Interpreter.getScaledValue(4), true));
        p.add(l, gbc);
        w.add(p);

        w.setSize(x_size + 700, y_size + 700);
        w.setLocation(9999, 9999);
        w.show();
        this.x_size = l.getWidth() + Interpreter.getScaledValue(100);
        w.hide();
        p.remove(l);
        w.remove(p);

        //actually create components
        p = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                if (StaticStuff.getColorReady()) g.setColor(StaticStuff.getColor("white_border"));
                else g.setColor(Color.WHITE);
                g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
                if (StaticStuff.getColorReady()) g.setColor(StaticStuff.getColor("background"));
                else g.setColor(Color.BLACK);
                g.setColor(StaticStuff.getColor("background"));
                g.fillRoundRect(3, 3, x_size - 6, y_size - 6, 20, 20);
            }
        };
        p.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;


        l = new JLabel(("<html><center><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-</font><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("white")) + "\">" +
                text.replace("<br>", "<font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-<br>-</font>") +
                "</font><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-</font>"));
        l.setFont(StaticStuff.getPixelatedFont());
        l.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor("white_border"), Interpreter.getScaledValue(4), true));
        p.add(l, gbc);
        JLabel l2 = new JLabel("<html><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-</font>");
        p.add(l2, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        //gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton options;
        for (int i = 0; i < buttons.length; i++) {
            options = new JButton("<html>" + StaticStuff.prepareString(buttons[i]));
            options.setFont(StaticStuff.getPixelatedFont());
            final int ii = i;
            options.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    click(ii);
                }
            });
            p.add(options, gbc);
        }

        w.add(p);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        w.setSize(x_size, y_size);
        w.setLocation((int) ((width / 2) - (x_size / 2)), (int) ((height / 2) - (y_size / 2)));

        w.setLocationRelativeTo(null);
        w.setLocation(w.getX() + StaticStuff.getRandomPopupMovement(), w.getY() + StaticStuff.getRandomPopupMovement());

        addListener(w);
        addListener(l);

        w.show();
    }

    private int pX, pY;

    private void addListener(Component c) {
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                pX = me.getX();
                pY = me.getY();
                w.toFront();
                w.repaint();
            }

            public void mouseDragged(MouseEvent me) {
                w.setLocation(w.getLocation().x + me.getX() - pX, w.getLocation().y + me.getY() - pY);
            }
        });
        c.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                w.setLocation(w.getLocation().x + me.getX() - pX, w.getLocation().y + me.getY() - pY);
            }
        });
    }

    private void click(int index) {
        selected = index;
        w.dispose();
    }
}
