
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

class PopupDice {
    private JWindow w;
    private int x_size, y_size;
    private int sides, diceSize = Interpreter.getScaledValue(100), diceSizePlus = 0, diceYChange = 0, duration, rollSides;
    public int selected = -1;
    private boolean autoRoll;
    private String text;
    private ImageIcon dice;
    private JLabel l_dice, l_diceValue;

    PopupDice(String text, int sides, int duration, boolean autoRoll, int rollSides) {
        this.sides = sides;
        this.duration = duration;
        this.autoRoll = autoRoll;
        this.rollSides = rollSides;
        this.text = StaticStuff.prepareString(text);
        int longestLineLength = StaticStuff.getLongestLineLength(text.replaceAll("\\[\\[[^:]+:([^\\]]+)\\]\\]", "$1").split("<br>"));
        if (longestLineLength < 8) longestLineLength = 8;
        this.x_size = longestLineLength * 20;
        this.x_size = Interpreter.getScaledValue(this.x_size + 120);
        this.y_size = (StaticStuff.countOccurrences(text, "<br>") * 49) + 350;
        this.y_size = Interpreter.getScaledValue(this.y_size + 100);
        if (sides == 20) {
            diceSizePlus = Interpreter.getScaledValue(50);
            diceYChange = Interpreter.getScaledValue(10);
        }
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
        JLabel l2 = new JLabel("<html><font color=\"" + StaticStuff.colorToHex(StaticStuff.getColor("background")) + "\">-<br>-<br>-<br>-<br>-<br>-</font>");
        l2.setFont(StaticStuff.getPixelatedFont());
        p.add(l2, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        //gbc.fill = GridBagConstraints.HORIZONTAL;

        l_diceValue = new JLabel("", SwingConstants.CENTER);
        l_diceValue.setBounds((x_size / 2) - (diceSize / 2), (y_size / 2) - (diceSize / 2) + 50, diceSize, diceSize);
        l_diceValue.setText("<html><b>1");
        l_diceValue.setForeground(new Color(255, 255, 255));
        l_diceValue.setFont(StaticStuff.getPixelatedFont(20f));
        l_diceValue.setEnabled(true);
        l_diceValue.setVisible(true);
        w.add(l_diceValue);

        ImageIcon selectedDice = new ImageIcon(Images.readImageFromFile("res/img/" + sides + "sided.png"));
        dice = getScaledImage(selectedDice, (diceSize + diceSizePlus), (diceSize + diceSizePlus));
        l_dice = new JLabel();
        l_dice.setBounds((x_size / 2) - ((diceSize + diceSizePlus) / 2), (y_size / 2) - ((diceSize + diceSizePlus) / 2) + 50 + diceYChange, (diceSize + diceSizePlus), (diceSize + diceSizePlus));
        l_dice.setIcon(dice);
        l_dice.setEnabled(true);
        l_dice.setVisible(true);
        l_dice.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                if (!autoRoll) {
                    autoRoll = true;
                    new Thread() {
                        public void run() {
                            diceAnimation();
                        }
                    }.start();
                }
            }
        });
        w.add(l_dice);

        w.add(p);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        w.setSize(x_size, y_size);
        w.setLocation((int) ((width / 2) - (x_size / 2)), (int) ((height / 2) - (y_size / 2)));

        addListener(w);
        addListener(l);
        addListener(l_dice);

        w.show();

        if (autoRoll)
            new Thread() {
                public void run() {
                    diceAnimation();
                }
            }.start();
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

    private void diceAnimation() {
        for (int i = 0; i < duration; i++) {
            setRandomDicePos();
            setRandomDiceNumber();
            Sleep.milliseconds(StaticStuff.randomNumber(50, 200));
        }
        l_diceValue.setText("<html><b>" + StaticStuff.prepareString("[[yellow:" + (StaticStuff.randomNumber(1, rollSides)) + "]]"));
        selected = Integer.parseInt(StaticStuff.removePrepareString(l_diceValue.getText()));
        l_dice.setBounds((x_size / 2) - ((diceSize + diceSizePlus) / 2), (y_size / 2) - ((diceSize + diceSizePlus) / 2) + 50 + diceYChange, (diceSize + diceSizePlus), (diceSize + diceSizePlus));
        l_diceValue.setBounds((x_size / 2) - (diceSize / 2), (y_size / 2) - (diceSize / 2) + 50, diceSize, diceSize);
        FileManager.writeToFile("res/txt/popupresult" + StaticStuff.dataFileEnding, "" + selected);
        Sleep.milliseconds(1500);
        w.dispose();
    }

    private void setRandomDiceNumber() {
        l_diceValue.setText("<html><b>" + StaticStuff.randomNumber(1, rollSides));
    }

    private void setRandomDicePos() {
        setDicePos(StaticStuff.randomNumber(-10, 10), StaticStuff.randomNumber(-10, 10));
    }

    private void setDicePos(int x, int y) {
        l_dice.setBounds((x_size / 2) - ((diceSize + diceSizePlus) / 2) + x, (y_size / 2) - ((diceSize + diceSizePlus) / 2) + 50 + y + diceYChange, (diceSize + diceSizePlus), (diceSize + diceSizePlus));
        l_diceValue.setBounds((x_size / 2) - (diceSize / 2) + x, (y_size / 2) - (diceSize / 2) + 50 + y, diceSize, diceSize);
    }

    //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon; Thanks to trolologuy!
    private ImageIcon getScaledImage(ImageIcon srcImg, int w, int h) {
        Image image = srcImg.getImage();
        Image newimg = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }
}
