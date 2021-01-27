
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PopupButtons {
    private JWindow w;
    private int x_size, y_size;
    public int selected = -1;
    private final String text;
    private final String[] buttons;

    PopupButtons(String text, String[] buttons) {
        this.text = StaticStuff.prepareString(text);
        this.buttons = buttons;
        //int longestLineLength = StaticStuff.getLongestLineLength(text.replaceAll("\\[\\[[^:]+:([^\\]]+)\\]\\]","$1").split("<br>"));
        //int longestButtonLength = StaticStuff.getLongestLineLength(buttons);
        int longestLineLength = StaticStuff.getLongestLineWidth(text.split("<br>"), StaticStuff.getPixelatedFont());
        int longestButtonLength = StaticStuff.getLongestLineWidth(buttons, StaticStuff.getPixelatedFont());
        if (longestLineLength >= longestButtonLength) {
            this.x_size = longestLineLength + 100;
        } else {
            this.x_size = longestButtonLength + 100;
        }
        if (this.x_size < 170) this.x_size = 170;
        this.y_size = (StaticStuff.countOccurrences(text, "<br>") * 49) + (buttons.length * 57) + 49 + 140;
        this.x_size = Interpreter.getScaledValue(this.x_size);
        this.y_size = Interpreter.getScaledValue(this.y_size);
        for (int i = 0; i < buttons.length; i++)
            buttons[i] = "<html>" + StaticStuff.prepareString("[[def_text_color_buttons:" + buttons[i] + "]]");
    }

    public void createComponents() {
        w = new JWindow();
        w.setBackground(new Color(0, 0, 0, 0));
        w.setAlwaysOnTop(true);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        //figure out required size
        JPanel p = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 0));
                g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
            }
        };
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

        int maxSize = 0;
        w.setVisible(true);
        maxSize = Math.max(maxSize, l.getWidth());
        w.setVisible(false);

        JButton options;
        for (int i = 0; i < buttons.length; i++) {
            options = new JButton("<html>" + StaticStuff.prepareString(buttons[i]));
            options.setFont(StaticStuff.getPixelatedFont());
            final int ii = i;
            options.addActionListener(evt -> click(ii));
            p.add(options, gbc);

            w.setVisible(true);
            maxSize = Math.max(maxSize, options.getWidth());
            w.setVisible(false);
        }

        this.x_size = maxSize + Interpreter.getScaledValue(100);
        p.remove(l);
        w.remove(p);

        //actually create components
        p = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                g.setColor(StaticStuff.getColor("white_border"));
                g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
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

        addListener(w);
        addListener(l);

        w.setVisible(true);
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