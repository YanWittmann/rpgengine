
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

public class GuiLauncher extends JFrame {
    private JLabel l_createDesc;
    private JLabel l_createStart;
    private JLabel l_createTitle;
    private JLabel l_manageButtons[];
    private JLabel l_manageTitle;
    private JLabel l_playDesc;
    private JLabel l_playStart;
    private JLabel l_playTitle;
    private JLabel l_title;
    private JLabel l_close;
    private JComboBox cb_versionsPlay;
    private JComboBox cb_versionsCreate;
    private Launcher launcher;
    private int xSize = 1310, ySize = 753;
    private boolean ready = false, isSelectedPlay = false, isSelectedCreate = false, isSelectedOther[], isVisiblePlay = false, isVisibleCreate = false, isVisibleOther[];
    private static GuiLauncher self;

    public GuiLauncher(Launcher launcher) {
        self = this;
        this.launcher = launcher;
        this.setTitle("RPG Engine - Launcher");
        this.setSize(xSize, ySize);
        initMinusText();

        JPanel contentPane = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255));
                g.fillRoundRect(0, 0, xSize, ySize, 20, 20);
                g.setColor(new Color(0, 0, 0));
                g.fillRoundRect(3, 3, xSize - 6, ySize - 6, 20, 20);
            }
        };
        setUndecorated(true);
        contentPane.setPreferredSize(new Dimension(xSize, ySize));
        contentPane.setBackground(new Color(0, 0, 0, 0));
        setBackground(new Color(0, 0, 0, 0));
        setIconImage(new ImageIcon("files/res/img/iconyellow.png").getImage());

        l_close = new JLabel("<html>" + StaticStuff.prepareString("[[red:Close]]"), SwingConstants.CENTER);
        l_close.setBounds(xSize - 120, 20, 100, 30);
        l_close.setBackground(new Color(214, 217, 223));
        l_close.setForeground(new Color(255, 255, 255));
        l_close.setEnabled(true);
        l_close.setFont(StaticStuff.getPixelatedFont());
        l_close.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                new Thread(() -> {
                    if (buttonsAcitve)
                        System.exit(0);
                }).start();
            }
        });

        l_title = new JLabel("RPG ENGINE", SwingConstants.CENTER);
        l_title.setBounds(0, 47, 1310, 52);
        l_title.setBackground(new Color(214, 217, 223));
        l_title.setForeground(new Color(255, 255, 255));
        l_title.setEnabled(true);
        l_title.setFont(StaticStuff.getPixelatedFont().deriveFont(30f));
        l_title.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                new Thread(launcher::toggleFastMode).start();
            }
        });

        l_createTitle = new JLabel("<html>" + StaticStuff.prepareString("[[gold:Create]]"), SwingConstants.CENTER);
        l_createTitle.setBounds(0, 175, 437, 60);
        l_createTitle.setBackground(new Color(214, 217, 223));
        l_createTitle.setForeground(new Color(255, 255, 255));
        l_createTitle.setEnabled(true);
        l_createTitle.setFont(StaticStuff.getPixelatedFont().deriveFont(20f));

        l_createDesc = new JLabel("<html><center>" + StaticStuff.prepareString("[[gray:Create adventures that you can then share with your friends and other people]]"), SwingConstants.CENTER);
        l_createDesc.setBounds(10, 245, 417, 300);
        l_createDesc.setBackground(new Color(214, 217, 223));
        l_createDesc.setForeground(new Color(255, 255, 255));
        l_createDesc.setEnabled(true);
        l_createDesc.setFont(StaticStuff.getPixelatedFont());

        l_createStart = new JLabel("<html>" + StaticStuff.prepareString("[[aqua:Start version]]"), SwingConstants.CENTER);
        l_createStart.setBounds(0, 555, 436, 60);
        l_createStart.setBackground(new Color(214, 217, 223));
        l_createStart.setForeground(new Color(255, 255, 255));
        l_createStart.setEnabled(true);
        l_createStart.setFont(StaticStuff.getPixelatedFont());
        l_createStart.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                isSelectedCreate = false;
                setMinusTextComponent(null, null);
                if (isVisibleCreate)
                    l_createStart.setText("<html>" + StaticStuff.prepareString("[[aqua:Start version]]"));
            }

            public void mouseEntered(MouseEvent e) {
                isSelectedCreate = true;
                if (isVisibleCreate) {
                    l_createStart.setText("<html>" + StaticStuff.prepareString("[[gold:Start version]]"));
                    setMinusTextComponent(l_createStart, "<html>" + StaticStuff.prepareString("[[gold:Start version]]"));
                }
            }

            public void mouseClicked(MouseEvent e) {
                new Thread() {
                    public void run() {
                        if (isVisibleCreate)
                            launcher.startVersion(cb_versionsCreate.getSelectedItem().toString(), "create");
                    }
                }.start();
            }
        });

        l_playTitle = new JLabel("<html>" + StaticStuff.prepareString("[[green:Play]]"), SwingConstants.CENTER);
        l_playTitle.setBounds(436, 155, 437, 60);
        l_playTitle.setBackground(new Color(214, 217, 223));
        l_playTitle.setForeground(new Color(255, 255, 255));
        l_playTitle.setEnabled(true);
        l_playTitle.setFont(StaticStuff.getPixelatedFont().deriveFont(27f));

        l_playDesc = new JLabel("<html><center>" + StaticStuff.prepareString("[[gray:Play adventures that other people have created, manage your adventures using the button on the right!]]"), SwingConstants.CENTER);
        l_playDesc.setBounds(446, 244, 417, 300);
        l_playDesc.setBackground(new Color(214, 217, 223));
        l_playDesc.setForeground(new Color(255, 255, 255));
        l_playDesc.setEnabled(true);
        l_playDesc.setFont(StaticStuff.getPixelatedFont());

        l_playStart = new JLabel("<html>" + StaticStuff.prepareString("[[aqua:Start version]]"), SwingConstants.CENTER);
        l_playStart.setBounds(436, 555, 436, 60);
        l_playStart.setBackground(new Color(214, 217, 223));
        l_playStart.setForeground(new Color(255, 255, 255));
        l_playStart.setEnabled(true);
        l_playStart.setFont(StaticStuff.getPixelatedFont());
        l_playStart.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                isSelectedPlay = false;
                setMinusTextComponent(null, null);
                if (isVisiblePlay)
                    l_playStart.setText("<html>" + StaticStuff.prepareString("[[aqua:Start version]]"));
            }

            public void mouseEntered(MouseEvent e) {
                isSelectedPlay = true;
                if (isVisiblePlay) {
                    l_playStart.setText("<html>" + StaticStuff.prepareString("[[gold:Start version (shift)]]"));
                    setMinusTextComponent(l_playStart, "<html>" + StaticStuff.prepareString("[[gold:Start version (shift)]]"));
                }
            }

            public void mouseClicked(MouseEvent e) {
                new Thread() {
                    public void run() {
                        if (isVisiblePlay)
                            if (e.isShiftDown()) {
                                launcher.startVersionWithParameters(cb_versionsPlay.getSelectedItem().toString(), "play");
                            } else {
                                launcher.startVersion(cb_versionsPlay.getSelectedItem().toString(), "play");
                            }
                    }
                }.start();
            }
        });

        cb_versionsPlay = new JComboBox();
        cb_versionsPlay.setBounds(504, 630, 300, 40);
        cb_versionsPlay.setBackground(new Color(214, 217, 223));
        cb_versionsPlay.setForeground(new Color(0, 0, 0));
        cb_versionsPlay.setEnabled(true);
        cb_versionsPlay.setFont(StaticStuff.getPixelatedFont().deriveFont(14f));

        cb_versionsCreate = new JComboBox();
        cb_versionsCreate.setBounds(68, 630, 300, 40);
        cb_versionsCreate.setBackground(new Color(214, 217, 223));
        cb_versionsCreate.setForeground(new Color(0, 0, 0));
        cb_versionsCreate.setEnabled(true);
        cb_versionsCreate.setFont(StaticStuff.getPixelatedFont().deriveFont(14f));

        l_manageTitle = new JLabel("<html>" + StaticStuff.prepareString("[[blue:Other]]"), SwingConstants.CENTER);
        l_manageTitle.setBounds(874, 175, 430, 60);
        l_manageTitle.setBackground(new Color(214, 217, 223));
        l_manageTitle.setForeground(new Color(255, 255, 255));
        l_manageTitle.setEnabled(true);
        l_manageTitle.setFont(StaticStuff.getPixelatedFont().deriveFont(20f));

        String manageButtonsText[] = new String[]{"Manage adventures", "Open documentation", "Report a bug", "Request a feature", "Contact me!", "Buy me a coffee :)", "Website"};
        l_manageButtons = new JLabel[manageButtonsText.length];
        isSelectedOther = new boolean[manageButtonsText.length];
        isVisibleOther = new boolean[manageButtonsText.length];
        for (int i = 0; i < l_manageButtons.length; i++) {
            l_manageButtons[i] = new JLabel("<html>" + StaticStuff.prepareString("[[aqua:" + manageButtonsText[i] + "]]"), SwingConstants.CENTER);
            l_manageButtons[i].setBounds(874, 244 + (i * 60), 430, 60);
            l_manageButtons[i].setBackground(new Color(214, 217, 223));
            l_manageButtons[i].setForeground(new Color(255, 255, 255));
            l_manageButtons[i].setEnabled(true);
            l_manageButtons[i].setFont(StaticStuff.getPixelatedFont());
            int ii = i;
            l_manageButtons[i].addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                    isSelectedOther[ii] = false;
                    setMinusTextComponent(null, null);
                    if (isVisibleOther[ii])
                        l_manageButtons[ii].setText("<html>" + StaticStuff.prepareString("[[aqua:" + manageButtonsText[ii] + "]]"));
                }

                public void mouseEntered(MouseEvent e) {
                    isSelectedOther[ii] = true;
                    if (isVisibleOther[ii]) {
                        l_manageButtons[ii].setText("<html>" + StaticStuff.prepareString("[[gold:" + manageButtonsText[ii].toUpperCase() + "]]"));
                        setMinusTextComponent(l_manageButtons[ii], "<html>" + StaticStuff.prepareString("[[gold:" + manageButtonsText[ii].toUpperCase() + "]]"));
                    }
                }

                public void mouseClicked(MouseEvent e) {
                    new Thread() {
                        public void run() {
                            if (isVisibleOther[ii])
                                clickManageButton(ii);
                        }
                    }.start();
                }
            });
            l_manageButtons[i].setVisible(false);
            contentPane.add(l_manageButtons[i]);
        }

        l_createDesc.setVisible(false);
        l_createStart.setVisible(false);
        l_createTitle.setVisible(false);
        l_manageTitle.setVisible(false);
        l_playDesc.setVisible(false);
        l_playStart.setVisible(false);
        l_playTitle.setVisible(false);
        l_title.setVisible(false);
        l_close.setVisible(false);
        cb_versionsPlay.setVisible(false);
        cb_versionsCreate.setVisible(false);

        contentPane.add(l_createDesc);
        contentPane.add(l_createStart);
        contentPane.add(l_createTitle);
        contentPane.add(l_manageTitle);
        contentPane.add(l_playDesc);
        contentPane.add(l_playStart);
        contentPane.add(l_playTitle);
        contentPane.add(l_title);
        contentPane.add(l_close);
        contentPane.add(cb_versionsPlay);
        contentPane.add(cb_versionsCreate);

        JLabel l_dummy = new JLabel("");
        l_dummy.setBounds(0, 0, 1, 1);
        l_dummy.setEnabled(true);
        l_dummy.setVisible(true);
        contentPane.add(l_dummy);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(false);

        addListener(this);
    }

    public void showMe(boolean fast) {
        if (fast) {
            openAnimation(fast);
            setVisible(true);
        }
        java.awt.EventQueue.invokeLater(() -> {
            self.toFront();
            self.repaint();
            setAlwaysOnTop(true);
            Sleep.milliseconds(100);
            setAlwaysOnTop(false);
        });
        if (!fast) {
            setVisible(true);
            openAnimation(fast);
        }
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
                        } catch (Exception ignored) {
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

    private void openAnimation(boolean fast) {
        l_title.setVisible(false);
        l_close.setVisible(false);
        l_manageTitle.setVisible(false);
        for (JLabel lManageButton : l_manageButtons) lManageButton.setVisible(false);
        l_createTitle.setVisible(false);
        l_createDesc.setVisible(false);
        l_createStart.setVisible(false);
        l_playTitle.setVisible(false);
        l_playDesc.setVisible(false);
        l_playStart.setVisible(false);
        cb_versionsPlay.setVisible(false);
        cb_versionsCreate.setVisible(false);

        new Thread(() -> {
            if (!fast) {
                for (int i = 0; i < 10; i++) {
                    l_title.setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(50, 100));
                    l_title.setVisible(false);
                    Sleep.milliseconds(StaticStuff.randomNumber(50, 100));
                }
                Sleep.milliseconds(100);
            }
            l_title.setVisible(true);
        }).start();

        new Thread(() -> {
            if (!fast) {
                Sleep.milliseconds(800);
                for (int i = 0; i < 4; i++) {
                    l_createTitle.setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(30, 60));
                    l_createDesc.setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(30, 60));
                    l_createStart.setVisible(true);
                    isVisibleCreate = true;
                    Sleep.milliseconds(StaticStuff.randomNumber(30, 60));
                    l_createTitle.setVisible(false);
                    Sleep.milliseconds(StaticStuff.randomNumber(30, 60));
                    l_createDesc.setVisible(false);
                    Sleep.milliseconds(StaticStuff.randomNumber(30, 60));
                    l_createStart.setVisible(false);
                    isVisibleCreate = true;
                }
                Sleep.milliseconds(100);
            }
            l_createTitle.setVisible(true);
            l_createDesc.setVisible(true);
            l_createStart.setVisible(true);
            isVisibleCreate = true;
            cb_versionsCreate.setVisible(true);
        }).start();

        new Thread(() -> {
            if (!fast) {
                Sleep.milliseconds(1000);
                for (int i = 0; i < 3; i++) {
                    l_playTitle.setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(30, 60));
                    l_playDesc.setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(30, 60));
                    l_playStart.setVisible(true);
                    isVisiblePlay = true;
                    Sleep.milliseconds(StaticStuff.randomNumber(30, 60));
                    l_playTitle.setVisible(false);
                    Sleep.milliseconds(StaticStuff.randomNumber(30, 60));
                    l_playDesc.setVisible(false);
                    Sleep.milliseconds(StaticStuff.randomNumber(30, 60));
                    l_playStart.setVisible(false);
                    isVisiblePlay = false;
                }
                Sleep.milliseconds(100);
            }
            l_playTitle.setVisible(true);
            l_playDesc.setVisible(true);
            l_playStart.setVisible(true);
            isVisiblePlay = true;
            cb_versionsPlay.setVisible(true);
        }).start();

        new Thread(() -> {
            if (!fast) {
                Sleep.milliseconds(1200);
                for (int i = 0; i < 2; i++) {
                    l_manageTitle.setVisible(true);
                    Sleep.milliseconds(StaticStuff.randomNumber(20, 50));
                    for (JLabel l_manageButton : l_manageButtons) {
                        l_manageButton.setVisible(true);
                        isVisibleOther[i] = true;
                        Sleep.milliseconds(StaticStuff.randomNumber(20, 50));
                    }
                    l_manageTitle.setVisible(false);
                    Sleep.milliseconds(StaticStuff.randomNumber(20, 50));
                    for (JLabel l_manageButton : l_manageButtons) {
                        l_manageButton.setVisible(false);
                        isVisibleOther[i] = false;
                        Sleep.milliseconds(StaticStuff.randomNumber(20, 50));
                    }
                }
                Sleep.milliseconds(100);
            }
            l_manageTitle.setVisible(true);
            for (int i = 0; i < l_manageButtons.length; i++) {
                l_manageButtons[i].setVisible(true);
                isVisibleOther[i] = true;
                Sleep.milliseconds(StaticStuff.randomNumber(20, 50));
            }
            Sleep.milliseconds(100);
            l_close.setVisible(true);
            ready = true;
        }).start();
    }

    private final String[] minusText = new String[]{
            StaticStuff.prepareString("[[gray:sbo]]"),
            StaticStuff.prepareString("[[aqua:sbo]]"),
            StaticStuff.prepareString("[[gray:sbc]]"),
            StaticStuff.prepareString("[[aqua:sbc]]"),
    };
    private JLabel minusLabel = null;
    private String minusBaseText = null;
    private int minusCounter = 0;

    private void setMinusTextComponent(JLabel l, String baseText) {
        minusLabel = l;
        if (baseText == null)
            minusBaseText = null;
        else {
            minusBaseText = baseText.replace("<html>", "");
            minusLabel.setText(getMinusText());
        }
    }

    private void initMinusText() {
        new Thread() {
            public void run() {
                while (true) {
                    Sleep.milliseconds(500);
                    minusCounter = (minusCounter + 1) % (minusText.length / 2);
                    if (minusBaseText != null && minusBaseText != null) {
                        minusLabel.setText(getMinusText());
                    }
                }
            }
        }.start();
    }

    private String getMinusText() {
        return "<html>" + minusText[minusCounter] + minusBaseText.toUpperCase() + minusText[minusCounter + (minusText.length / 2)];
    }

    private void clickManageButton(int id) { // "Manage adventures", "Open documentation", "Report a bug", "Request a feature", "Contact me!", "Buy me a coffee :)"
        switch (id) {
            case 0: //Manage adventures
                launcher.manageAdventures();
                break;

            case 1: //Open documentation
                StaticStuff.openURL("http://yanwittmann.de/projects/rpgengine/documentation/");
                break;

            case 2: //Report a bug
                StaticStuff.mailto("mailto:rpg@yanwittmann.de?subject=Bug%20report%20%7C%20RPG%20Engine&body=Type%3A%20%5BEDITOR%5D%2F%5BPLAYER%5D%0D%0APriority%3A%20%5BLOW%5D%2F%5BMEDIUM%5D%2F%5BHIGH%5D%2F%5BCRITICAL%5D%0D%0A%0D%0ADescription%2Fsummary%3A%0D%0A%0D%0AVisual%20proof%3A%0D%0A%0D%0ASteps%20to%20reproduce%3A%0D%0A%0D%0AOther%20notes%3A%0D%0A");
                break;

            case 3: //Request a feature
                StaticStuff.mailto("mailto:rpg@yanwittmann.de?subject=Feature%20request%20%7C%20RPG%20Engine&body=Type%3A%20%5BNEW%5D%2F%5BMOD%5D%0D%0APriority%3A%20%5BLOW%5D%2F%5BMEDIUM%5D%2F%5BHIGH%5D%2F%5BCRITICAL%5D%0D%0A%0D%0ADescribe%20your%20request%3A%0D%0A%0D%0AWhy%20it%20should%20be%20implemented%3A%0D%0A%0D%0AOther%20notes%3A%0D%0A");
                break;

            case 4: //Contact me!
                StaticStuff.mailto("mailto:rpg@yanwittmann.de?subject=Contact%20%7C%20RPG%20Engine&body=%3A)");
                break;

            case 5: //Buy me a coffee :)
                StaticStuff.openURL("https://paypal.me/yanwittmann");
                break;

            case 6: //Website
                StaticStuff.openURL("http://yanwittmann.de/projects/rpgengine/site/");
                break;
        }
    }

    public void setAvailableVersions(String versions[], String what) {
        if (what.equals("play"))
            for (int i = 0; i < versions.length; i++)
                cb_versionsPlay.addItem(versions[i]);
        else
            for (int i = 0; i < versions.length; i++)
                cb_versionsCreate.addItem(versions[i]);
    }

    private boolean buttonsAcitve = true;

    public void setButtonsActive(boolean active) {
        buttonsAcitve = active;
    }
}