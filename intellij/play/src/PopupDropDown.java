
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PopupDropDown {
    private JWindow w;
    private JComboBox cb_input;
    final private int x_size, y_size;
    public int selected = -1;
    private String text, options[], preselected;
    public String result;

    PopupDropDown(String text, String[] options, String preselected) {
        if (text.contains("<br>"))
            while (text.replace("<font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">", "").replace("</font>", "").replace("<br", "").split("<br>")[0].length() * 20 < 1200)
                text = "<font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">--</font>" + text.split("<br>")[0] + "<font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">--</font><br>" + text.split("<br>", 0)[1];
        else
            while (text.replace("<font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">", "").replace("</font>", "").length() * 20 < 1200)
                text = "<font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">- </font>" + text + "<font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\"> -</font>";
        this.text = StaticStuff.prepareString(text);
        this.options = options;
        this.preselected = preselected;
        this.x_size = Interpreter.getScaledValue(Math.max(StaticStuff.getLongestLineLength(StaticStuff.prepareStringForPlayer(text.replace("<br>", "(EOL)")).split("(EOL)")) * 16, 1000));
        this.y_size = Interpreter.getScaledValue((StaticStuff.countOccurrences(text, "<br>") * 49) + 110 + 130 + 40);
    }

    public void createComponents() {
        w = new JWindow();
        w.setBackground(new Color(0, 0, 0, 0));
        w.setAlwaysOnTop(true);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        JPanel p = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 0));
                g.fillRect(0, 0, x_size, y_size);
                g.setColor(StaticStuff.getColor("white_border"));
                g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
                g.setColor(StaticStuff.getColor("background"));
                g.fillRoundRect(3, 3, x_size - 6, y_size - 6, 20, 20);
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
        JLabel l_dummy = new JLabel("<html><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-</font>");
        p.add(l_dummy, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        cb_input = new JComboBox(options);
        cb_input.setFont(StaticStuff.getPixelatedFont());
        cb_input.setEnabled(true);
        p.add(cb_input, gbc);

        l_dummy = new JLabel("<html><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-</font>");
        p.add(l_dummy, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        JButton option = new JButton("<html>" + StaticStuff.prepareString("[[green:OK]]"));
        option.setFont(StaticStuff.getPixelatedFont());
        option.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                click();
            }
        });
        p.add(option, gbc);

        w.add(p);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        w.setSize(x_size, y_size);
        w.setLocation((int) ((width / 2) - (x_size / 2)), (int) ((height / 2) - (y_size / 2)));

        w.setLocationRelativeTo(null);

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

    private void click() {
        result = cb_input.getSelectedItem().toString();
        selected = 0;
        w.dispose();
    }
}
