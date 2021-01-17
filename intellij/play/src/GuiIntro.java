
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

public class GuiIntro extends JFrame {
    private JMenuBar menuBar;
    private JLabel l_adventureTitle;
    private JLabel l_author;
    private JLabel l_creditsAndInfo;
    private JLabel l_restText;
    private JLabel l_start;
    private JLabel l_projectIcon;
    private PlayerSettings player;
    private ProjectSettings project;
    private Interpreter interpreter;
    private boolean exit = false;
    public static boolean skipIntro = false;
    private int x_size = Interpreter.getScaledValue(1400), y_size = Interpreter.getScaledValue(790);

    public GuiIntro(Interpreter interpreter, PlayerSettings player, ProjectSettings project, String version, BufferedImage image) {
        this.player = player;
        this.project = project;
        this.interpreter = interpreter;
        this.setTitle(StaticStuff.projectName + " - Intro");
        this.setSize(x_size, y_size);

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(x_size, y_size));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/iconyellow.png").getImage());
        setUndecorated(true);

        l_adventureTitle = new JLabel("<html>" + StaticStuff.prepareString(project.getValue("name")), SwingConstants.CENTER);
        l_adventureTitle.setBounds(0, Interpreter.getScaledValue(350), x_size, Interpreter.getScaledValue(60));
        l_adventureTitle.setBackground(new Color(214, 217, 223));
        l_adventureTitle.setForeground(new Color(255, 255, 255));
        l_adventureTitle.setEnabled(true);
        l_adventureTitle.setFont(StaticStuff.getPixelatedFont(26f));
        l_adventureTitle.setVisible(false);

        l_author = new JLabel("<html>" + StaticStuff.prepareString(Interpreter.lang("introAuthor") + "[[aqua:" + project.getValue("author") + " ]][[gray:(v. " + project.getValue("version") + ")]]"), SwingConstants.CENTER);
        l_author.setBounds(0, Interpreter.getScaledValue(139), x_size, Interpreter.getScaledValue(40));
        l_author.setBackground(new Color(214, 217, 223));
        l_author.setForeground(new Color(255, 255, 255));
        l_author.setEnabled(true);
        l_author.setFont(StaticStuff.getPixelatedFont());
        l_author.setVisible(false);

        l_projectIcon = new JLabel("", SwingConstants.CENTER);
        l_projectIcon.setBounds(Interpreter.getScaledValue(685), Interpreter.getScaledValue(113), Interpreter.getScaledValue(30), Interpreter.getScaledValue(30));
        l_projectIcon.setBackground(new Color(214, 217, 223));
        l_projectIcon.setForeground(new Color(255, 255, 255));
        l_projectIcon.setEnabled(true);
        l_projectIcon.setVisible(false);
        l_projectIcon.setIcon(getScaledImage(new ImageIcon(image), Interpreter.getScaledValue(30), Interpreter.getScaledValue(30)));

        l_restText = new JLabel("<html><center>" + StaticStuff.prepareString(project.getValue("description") + "<br><br>" + Interpreter.lang("introPlayerName", "[[aqua:" + player.getValue("name") + "]]",
                "[[aqua:" + player.getValue("class") + "]]")), SwingConstants.CENTER);
        l_restText.setBounds(Interpreter.getScaledValue(0), Interpreter.getScaledValue(214), x_size, Interpreter.getScaledValue(450));
        l_restText.setBackground(new Color(214, 217, 223));
        l_restText.setForeground(new Color(255, 255, 255));
        l_restText.setEnabled(true);
        l_restText.setFont(StaticStuff.getPixelatedFont());
        l_restText.setVisible(false);

        l_start = new JLabel("<html>" + StaticStuff.prepareString("[[aqua:" + Interpreter.lang("introStart") + "]]"), SwingConstants.CENTER);
        l_start.setBounds(0, Interpreter.getScaledValue(671), x_size, Interpreter.getScaledValue(40));
        l_start.setBackground(new Color(214, 217, 223));
        l_start.setForeground(new Color(255, 255, 255));
        l_start.setEnabled(true);
        l_start.setFont(StaticStuff.getPixelatedFont());
        l_start.setVisible(false);
        l_start.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                exit = true;
                dispose();
                interpreter.openConsole();
            }
        });

        l_creditsAndInfo = new JLabel("<html>" + StaticStuff.prepareString(Interpreter.lang("introSkip")));
        l_creditsAndInfo.setBounds(Interpreter.getScaledValue(30), Interpreter.getScaledValue(740), Interpreter.getScaledValue(1000), Interpreter.getScaledValue(35));
        l_creditsAndInfo.setBackground(new Color(214, 217, 223));
        l_creditsAndInfo.setForeground(new Color(255, 255, 255));
        l_creditsAndInfo.setEnabled(true);
        l_creditsAndInfo.setFont(StaticStuff.getPixelatedFont(10f));
        l_creditsAndInfo.setVisible(true);
        l_creditsAndInfo.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (!skipIntro)
                    skipIntro = true;
            }
        });

        contentPane.add(l_adventureTitle);
        contentPane.add(l_author);
        contentPane.add(l_creditsAndInfo);
        contentPane.add(l_restText);
        contentPane.add(l_start);
        contentPane.add(l_projectIcon);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);

        new Thread() {
            public void run() {
                Sleep.millisecondsIntro(2000);
                String toDisplay = l_adventureTitle.getText();
                l_adventureTitle.setText("");
                l_adventureTitle.setVisible(true);
                for (int i = 1; i < toDisplay.length(); i++) {
                    if (toDisplay.charAt(i) == '<' || toDisplay.charAt(i - 1) == '<') {
                        while (toDisplay.charAt(i) != '>' && i < toDisplay.length()) i++;
                        i++;
                    }
                    Sleep.millisecondsIntro(StaticStuff.randomNumber(20, 300));
                    l_adventureTitle.setText(toDisplay.substring(0, i));
                }
                Sleep.millisecondsIntro(2000);
                for (int i = l_adventureTitle.getY(); i > Interpreter.getScaledValue(47); i -= 10) {
                    Sleep.millisecondsIntro(StaticStuff.randomNumber(10, 60));
                    l_adventureTitle.setBounds(Interpreter.getScaledValue(StaticStuff.randomNumber(-10, 10)), Interpreter.getScaledValue(i + StaticStuff.randomNumber(-10, 10)), x_size, Interpreter.getScaledValue(60));
                }
                l_adventureTitle.setBounds(0, Interpreter.getScaledValue(47), x_size, Interpreter.getScaledValue(60));
                Sleep.millisecondsIntro(1000);
                l_projectIcon.setVisible(false);
                for (int i = 0; i < 7; i++) {
                    l_projectIcon.setVisible((i % 2) == 0);
                    try {
                        Thread.sleep(30 + (20 * i));
                    } catch (Exception e) {
                    }
                }
                l_projectIcon.setVisible(true);
                Sleep.millisecondsIntro(500);
                toDisplay = l_author.getText();
                l_author.setText("");
                l_author.setVisible(true);
                for (int i = 1; i < toDisplay.length(); i++) {
                    if (toDisplay.charAt(i) == '<' || toDisplay.charAt(i - 1) == '<') {
                        while (toDisplay.charAt(i) != '>' && i < toDisplay.length()) i++;
                        i++;
                    }
                    Sleep.millisecondsIntro(StaticStuff.randomNumber(0, 140));
                    l_author.setText(toDisplay.substring(0, i));
                }
                Sleep.millisecondsIntro(1000);
                toDisplay = l_restText.getText();
                l_restText.setText("");
                l_restText.setVisible(true);
                try {
                    for (int i = 1; i < toDisplay.length(); i++) {
                        if (toDisplay.charAt(i) == '<' || toDisplay.charAt(i - 1) == '<') {
                            while (toDisplay.charAt(i) != '>' && i < toDisplay.length()) i++;
                            i++;
                        }
                        Sleep.millisecondsIntro(StaticStuff.randomNumber(100, 300));
                        l_restText.setText(toDisplay.substring(0, i));
                        while (toDisplay.charAt(i) != '<' && toDisplay.charAt(i + 1) != 'b' && i < toDisplay.length())
                            i++;
                    }
                } catch (Exception e) {
                }
                l_restText.setText(toDisplay);
                Sleep.millisecondsIntro(4000);
                l_start.setVisible(true);
                Sleep.millisecondsIntro(200);
                l_creditsAndInfo.setVisible(true);
                l_creditsAndInfo.setText("made with the " + StaticStuff.projectName + " by Yan Wittmann (v. " + version + ")");
                skipIntro = true;
                Sleep.milliseconds(1000);
                int amountMinus = 0;
                while (!exit) {
                    Sleep.milliseconds(150);
                    l_start.setText("<html>" + StaticStuff.prepareString("[[aqua:" + makeFancyMinusText(Interpreter.lang("introStart"), amountMinus) + "]]"));
                    amountMinus = (amountMinus + 1) % 4;
                    Sleep.milliseconds(150);
                    l_start.setText("<html>" + StaticStuff.prepareString("[[aqua:" + makeFancyMinusText(Interpreter.lang("introStart"), amountMinus) + "]]"));
                    amountMinus = (amountMinus + 1) % 4;
                    Sleep.milliseconds(150);
                    l_start.setText("<html>" + StaticStuff.prepareString("[[gray:" + makeFancyMinusText(Interpreter.lang("introStart"), amountMinus) + "]]"));
                    amountMinus = (amountMinus + 1) % 4;
                    Sleep.milliseconds(150);
                    l_start.setText("<html>" + StaticStuff.prepareString("[[gray:" + makeFancyMinusText(Interpreter.lang("introStart"), amountMinus) + "]]"));
                    amountMinus = (amountMinus + 1) % 4;
                }
            }
        }.start();
        addListener(this);
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
                if (me.getButton() == MouseEvent.BUTTON2) {
                    for (int currentOpacity = 100; currentOpacity > 0; currentOpacity -= 3) {
                        try {
                            Thread.sleep(2);
                        } catch (Exception e) {
                        }
                        setOpacity(currentOpacity * 0.01f);
                    }
                    setVisible(false);
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

    public static String makeFancyMinusText(String text, int amount) {
        for (int i = 0; i < amount; i++) text = "[[black:- ]]" + text + "[[black: -]]";
        text = "- " + text + " -";
        return text;
    }
}