
import javax.swing.*;
import java.awt.*;

class FindRealTextSize {
    private JWindow w;
    private JLabel l;
    private JPanel p;
    private int x_size = 6000, y_size = 3000;
    private String text;

    public static void init() {
        if (finder == null) finder = new FindRealTextSize();
    }

    FindRealTextSize() {
        createComponents();
    }

    private static FindRealTextSize finder = null;

    public static int getTextWidth(String text, Font font) {
        if (finder == null) finder = new FindRealTextSize();
        finder.l.setText(text);
        finder.l.setFont(font);
        refresh(finder.l);
        refresh(finder.p);
        refresh(finder.w);
        return finder.l.getWidth();
    }

    public static int getTextHeight(String text, Font font) {
        if (finder == null) finder = new FindRealTextSize();
        finder.l.setText(text);
        finder.l.setFont(font);
        refresh(finder.l);
        refresh(finder.p);
        refresh(finder.w);
        return finder.l.getHeight();
    }

    private static void refresh(Component c) {
        c.invalidate();
        c.validate();
        c.repaint();
    }

    public void createComponents() {
        w = new JWindow();
        w.setBackground(new Color(0, 0, 0, 0));
        w.setAlwaysOnTop(false);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        p = new JPanel();
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;

        l = new JLabel("");
        l.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor("white_border"), Interpreter.getScaledValue(4), true));
        p.add(l, gbc);

        gbc.anchor = GridBagConstraints.CENTER;

        w.add(p);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        w.setSize(x_size, y_size);
        w.setLocation((int) width + 1000, (int) height + 1000);
        w.show();
    }
}
