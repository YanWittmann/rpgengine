
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

class PopupImage {
    private JWindow w;
    private int x_size, y_size;
    private int imgX = 300, imgY = 300;
    private int pX, pY;
    public int selected = -1;
    private String text, buttons[];
    private BufferedImage image;

    PopupImage(String text, BufferedImage image, int size) {
        if (Interpreter.getScaledValue(size) > 1300) {
            Log.add("Why would you open such a big image? This could lead to a java.lang.OutOfMemoryError exception in this thread!");
            size = Interpreter.getScaledValue(1300);
        }
        this.buttons = new String[]{"OK"};
        this.image = image;
        this.imgX = size;
        imgY = (int) (Float.parseFloat(imgX + "") / (Float.parseFloat(image.getWidth() + "") / Float.parseFloat(image.getHeight() + "")));
        int longestLineLength = FindRealTextSize.getTextWidth(StaticStuff.removeTextFormatting(text), StaticStuff.getPixelatedFont());
        int longestButtonLength = FindRealTextSize.getTextWidth("OK", StaticStuff.getPixelatedFont());
        if (longestLineLength >= longestButtonLength) {
            if (imgX > longestLineLength) this.x_size = imgX + Interpreter.getScaledValue(50);
            else this.x_size = longestLineLength + Interpreter.getScaledValue(100);
        } else {
            if (imgX > longestLineLength) this.x_size = imgX + Interpreter.getScaledValue(50);
            else this.x_size = longestButtonLength + Interpreter.getScaledValue(100);
        }
        this.x_size = Math.max(this.x_size, Interpreter.getScaledValue(170));
        this.y_size = (StaticStuff.countOccurrences(text, "<br>") * Interpreter.getScaledValue(49)) + (buttons.length * Interpreter.getScaledValue(57)) + Interpreter.getScaledValue(120) + imgY;
        this.text = StaticStuff.prepareString(text);
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

        JPanel p = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
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
        JLabel l2 = new JLabel("<html><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-</font>");
        p.add(l2, gbc);

        JLabel l_image = new JLabel();
        ImageIcon img = new ImageIcon(image);
        l_image.setIcon(getScaledImage(img, Interpreter.getScaledValue(imgX), Interpreter.getScaledValue(imgY)));
        l_image.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor("white_border"), Interpreter.getScaledValue(4), true));
        p.add(l_image, gbc);

        l2 = new JLabel("<html><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-</font>");
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

        addListener(l);
        addListener(l_image);
        addListener(w);

        w.setLocationRelativeTo(null);
        w.show();
    }

    private void click(int index) {
        selected = index;
        w.dispose();
    }

    //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon; Thanks to trolologuy!
    public static ImageIcon getScaledImage(ImageIcon srcImg, int w, int h) {
        Image image = srcImg.getImage();
        Image newimg = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

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
}
