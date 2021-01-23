
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GuiCharacterCreation extends JFrame {
    private final JPanel p_attributes;
    private final JLabel l_availableClasses;
    private final JLabel[] l_availableClassesList = new JLabel[7];
    private final JLabel[] l_diceRoll = new JLabel[7];
    private JLabel l_diceRollAll;
    private final JLabel l_done;
    private final JLabel l_goodAttributes;
    private final JLabel[] l_goodAttributesList = new JLabel[7];
    private final JLabel l_title;
    private final int x_size = Interpreter.getScaledValue(1365);
    private int y_size = Interpreter.getScaledValue(700);

    public GuiCharacterCreation(Interpreter interpreter) {
        this.setTitle(StaticStuff.projectName + " - " + lang("chCreationTitle"));
        this.setSize(x_size, y_size);
        setUndecorated(false);
        this.setBackground(StaticStuff.getColor("background"));

        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(x_size, y_size));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/iconyellow.png").getImage());

        l_title = new JLabel("<html>" + StaticStuff.prepareString("[[gray:" + lang("chCreationTitle") + "]]"), SwingConstants.CENTER);
        l_title.setBounds(0, Interpreter.getScaledValue(30), x_size, Interpreter.getScaledValue(50));
        l_title.setBackground(new Color(214, 217, 223));
        l_title.setForeground(new Color(255, 255, 255));
        l_title.setEnabled(true);
        l_title.setFont(StaticStuff.getPixelatedFont());
        l_title.setVisible(true);
        new Thread(() -> {
            Sleep.milliseconds(3000);
            l_title.setText(lang("chCreationTitle"));
        }).start();

        p_attributes = new JPanel(null);
        p_attributes.setBounds(0, 0, x_size, Interpreter.getScaledValue(700));
        p_attributes.setBackground(StaticStuff.getColor("background"));
        p_attributes.setForeground(StaticStuff.getColor("background"));
        p_attributes.setEnabled(true);
        p_attributes.setFont(new Font("sansserif", Font.PLAIN, 12));
        p_attributes.setVisible(true);

        for (int i = 0; i < 8; i++) {
            if (i == 7) {
                l_diceRollAll = new JLabel("<html>" + StaticStuff.prepareString("[[black:" + lang("chCreationRollAll") + "]]"), SwingConstants.CENTER);
                l_diceRollAll.setBounds(Interpreter.getScaledValue((167 * i) + 80), Interpreter.getScaledValue(120), Interpreter.getScaledValue(120), Interpreter.getScaledValue(35));
                l_diceRollAll.setBackground(Color.WHITE);
                l_diceRollAll.setForeground(Color.WHITE);
                l_diceRollAll.setEnabled(true);
                l_diceRollAll.setFont(StaticStuff.getPixelatedFont());
                l_diceRollAll.setVisible(false);
                l_diceRollAll.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        clickRollAll();
                    }
                });
                p_attributes.add(l_diceRollAll);
                final int ii = i;
                new Thread(() -> {
                    Sleep.milliseconds(1000 + 100 * ii + StaticStuff.randomNumber(0, 130));
                    l_diceRollAll.setVisible(true);
                }).start();
            } else {
                l_diceRoll[i] = new JLabel("<html>" + StaticStuff.prepareString("[[black:" + lang("chCreationRoll") + (i + 1) + "]]"), SwingConstants.CENTER);
                l_diceRoll[i].setBounds(Interpreter.getScaledValue((167 * i) + 130), Interpreter.getScaledValue(120), Interpreter.getScaledValue(120), Interpreter.getScaledValue(35));
                l_diceRoll[i].setBackground(Color.WHITE);
                l_diceRoll[i].setForeground(Color.WHITE);
                l_diceRoll[i].setEnabled(true);
                l_diceRoll[i].setFont(StaticStuff.getPixelatedFont());
                l_diceRoll[i].setVisible(true);
                final int ii = i;
                l_diceRoll[i].addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        clickRoll(ii);
                    }

                    public void mouseEntered(MouseEvent e) {
                        if(attributesRollAmount == 7) clickRoll(ii);
                    }

                    public void mouseExited(MouseEvent e) {
                    }
                });
                p_attributes.add(l_diceRoll[i]);
                rollResults[i] = -1;
                rollPlaced[i] = -1;
                new Thread(() -> {
                    Sleep.milliseconds(1000 + 100 * ii + StaticStuff.randomNumber(0, 130));
                    l_diceRoll[ii].setText("<html>" + StaticStuff.prepareString(lang("chCreationRoll") + "[[purple:" + (ii + 1) + "]]"));
                }).start();
            }
        }

        l_goodAttributes = new JLabel("<html>" + StaticStuff.prepareString("[[gray:" + lang("chCreationGoodAttributes") + "]]"), SwingConstants.CENTER);
        l_goodAttributes.setBounds(0, Interpreter.getScaledValue(227), Interpreter.getScaledValue(682), Interpreter.getScaledValue(35));
        l_goodAttributes.setBackground(new Color(214, 217, 223));
        l_goodAttributes.setForeground(new Color(255, 255, 255));
        l_goodAttributes.setEnabled(true);
        l_goodAttributes.setFont(StaticStuff.getPixelatedFont());
        l_goodAttributes.setVisible(true);

        for (int i = 0; i < 7; i++) {
            l_goodAttributesList[i] = new JLabel("<html>" + StaticStuff.prepareString("[[black:" + lang("playerAttr" + i) + "]]"), SwingConstants.RIGHT);
            l_goodAttributesList[i].setBounds(0, Interpreter.getScaledValue(280 + (i * 50)), Interpreter.getScaledValue(341), Interpreter.getScaledValue(30));
            l_goodAttributesList[i].setBackground(new Color(214, 217, 223));
            l_goodAttributesList[i].setForeground(new Color(255, 255, 255));
            l_goodAttributesList[i].setEnabled(true);
            l_goodAttributesList[i].setFont(StaticStuff.getPixelatedFont());
            l_goodAttributesList[i].setVisible(true);
            final int ii = i;
            l_goodAttributesList[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    attributeClicked = ii;
                }
            });
            p_attributes.add(l_goodAttributesList[i]);
        }

        l_availableClasses = new JLabel("<html>" + StaticStuff.prepareString("[[gray:" + lang("chCreationAvailableClasses") + "]]"), SwingConstants.CENTER);
        l_availableClasses.setBounds(Interpreter.getScaledValue(683), Interpreter.getScaledValue(228), Interpreter.getScaledValue(682), Interpreter.getScaledValue(35));
        l_availableClasses.setBackground(new Color(214, 217, 223));
        l_availableClasses.setForeground(new Color(255, 255, 255));
        l_availableClasses.setEnabled(true);
        l_availableClasses.setFont(StaticStuff.getPixelatedFont());
        l_availableClasses.setVisible(true);

        String[] classDataInput = FileManager.readFile("res/txt/classes" + Interpreter.getLanguage() + "" + StaticStuff.dataFileEnding);
        classData = new String[7][];
        for (int i = 0; i < 7; i++) classData[i] = classDataInput[i].split(";;;");
        classDataNames = new String[7];
        classDataCond = new String[7][];
        for (int i = 0; i < 7; i++) {
            classDataNames[i] = classData[i][0];
            classDataCond[i] = classData[i][1].split("-");
        }
        for (int i = 0; i < 7; i++) {
            l_availableClassesList[i] = new JLabel("<html>" + StaticStuff.prepareString("[[black:" + classDataNames[i] + "]]"), SwingConstants.CENTER);
            l_availableClassesList[i].setBounds(Interpreter.getScaledValue(683), Interpreter.getScaledValue(280 + (i * 50)), Interpreter.getScaledValue(682), Interpreter.getScaledValue(30));
            l_availableClassesList[i].setBackground(new Color(214, 217, 223));
            l_availableClassesList[i].setForeground(new Color(255, 255, 255));
            l_availableClassesList[i].setEnabled(true);
            l_availableClassesList[i].setFont(StaticStuff.getPixelatedFont());
            l_availableClassesList[i].setVisible(true);
            final int ii = i;
            l_availableClassesList[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    new Thread(() -> clickClass(ii)).start();
                }

                public void mouseEntered(MouseEvent e) {
                    if (attributesSetAmount != 7)
                        l_availableClassesList[ii].setText("<html>" + StaticStuff.prepareString("[[gray:" + classDataNames[ii] + "]]"));
                }

                public void mouseExited(MouseEvent e) {
                    if (attributesSetAmount != 7)
                        l_availableClassesList[ii].setText("<html>" + StaticStuff.prepareString("[[black:" + classDataNames[ii] + "]]"));
                }
            });
            p_attributes.add(l_availableClassesList[i]);
        }

        l_done = new JLabel("", SwingConstants.CENTER);
        l_done.setBounds(0, Interpreter.getScaledValue(560), x_size, Interpreter.getScaledValue(35));
        l_done.setBackground(new Color(214, 217, 223));
        l_done.setForeground(new Color(255, 255, 255));
        l_done.setEnabled(true);
        l_done.setFont(StaticStuff.getPixelatedFont());
        l_done.setText("<html>" + StaticStuff.prepareString("[[gray:" + lang("chCreationDone") + "]]"));
        l_done.setVisible(false);
        l_done.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                doneDisplay = true;
                dispose();
                new Thread(interpreter::finishSetup).start();
            }
        });

        p_attributes.add(l_availableClasses);
        p_attributes.add(l_goodAttributes);
        contentPane.add(l_done);
        contentPane.add(l_title);
        contentPane.add(p_attributes);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
        addListener(this);
    }

    private int pX, pY;

    private void addListener(Component c) {
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                pX = me.getX();
                pY = me.getY();
                toFront();
                repaint();
            }

            public void mouseDragged(MouseEvent me) {
                setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });
        c.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                setLocation(getLocation().x + me.getX() - pX, getLocation().y + me.getY() - pY);
            }
        });
    }

    boolean canSelectClass = false, doneDisplay = false, exit = false;

    private void clickClass(int id) {
        if (!canSelectClass || !classAvailable[id]) {
            String temp = classDataCond[id][0].replaceAll("[0-9]", "");
            StringBuilder condString = new StringBuilder(classDataCond[id][0].replaceAll("[A-Za-z]+", lang("playerAttr" + temp) + ": "));
            for (int i = 1; i < classDataCond[id].length; i++) {
                temp = classDataCond[id][i].replaceAll("[0-9]", "");
                condString.append("<br>").append(classDataCond[id][i].replaceAll("[A-Za-z]+", lang("playerAttr" + temp) + ": "));
            }
            StaticStuff.openPopup(StaticStuff.prepareString("[[yellow:" + classDataNames[id] + "]]") + "<br>" + condString);
        } else {
            for (int i = 0; i < 7; i++) {
                if (classAvailable[i])
                    l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[yellow:" + classDataNames[i] + "]]"));
                else
                    l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[gray:" + classDataNames[i] + "]]"));
            }
            selectedClassID = id;
            l_availableClassesList[selectedClassID].setText("<html>" + StaticStuff.prepareString("[[green:" + classDataNames[id] + "]]"));
            if (!doneDisplay) {
                doneDisplay = true;
                l_done.setVisible(true);
                for (int i = 0; i < 8; i++) {
                    Sleep.milliseconds(40 + (i * 8));
                    l_done.setText("<html>" + StaticStuff.prepareString("[[aqua:" + lang("chCreationDone") + "]]"));
                    Sleep.milliseconds(40 + (i * 8));
                    l_done.setText("<html>" + StaticStuff.prepareString("[[gray:" + lang("chCreationDone") + "]]"));
                }
                Sleep.milliseconds(500);
                new Thread(() -> {
                    int amountMinus = 0;
                    while (doneDisplay) {
                        Sleep.milliseconds(150);
                        l_done.setText("<html>" + StaticStuff.prepareString("[[aqua:" + makeFancyMinusText(lang("chCreationDone"), amountMinus) + "]]"));
                        amountMinus = (amountMinus + 1) % 4;
                        Sleep.milliseconds(150);
                        l_done.setText("<html>" + StaticStuff.prepareString("[[aqua:" + makeFancyMinusText(lang("chCreationDone"), amountMinus) + "]]"));
                        amountMinus = (amountMinus + 1) % 4;
                        Sleep.milliseconds(150);
                        l_done.setText("<html>" + StaticStuff.prepareString("[[gray:" + makeFancyMinusText(lang("chCreationDone"), amountMinus) + "]]"));
                        amountMinus = (amountMinus + 1) % 4;
                        Sleep.milliseconds(150);
                        l_done.setText("<html>" + StaticStuff.prepareString("[[gray:" + makeFancyMinusText(lang("chCreationDone"), amountMinus) + "]]"));
                        amountMinus = (amountMinus + 1) % 4;
                    }
                    exit = true;
                }).start();
            }
        }
    }

    public static String makeFancyMinusText(String text, int amount) {
        StringBuilder textBuilder = new StringBuilder(text);
        for (int i = 0; i < amount; i++) textBuilder = new StringBuilder("[[black:- ]]" + textBuilder + "[[black: -]]");
        text = textBuilder.toString();
        text = "- " + text + " -";
        return text;
    }

    private boolean checkIfMinRequirements() {
        l_diceRollAll.setVisible(false);
        int[] numbersAmount = new int[]{0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 7; i++) {
            numbersAmount[rollResults[i] - 7]++;
            l_diceRoll[i].setText("<html>" + StaticStuff.prepareString("[[aqua:" + rollResults[i] + "]]"));
            Sleep.milliseconds(20);
        }
        if (numbersAmount[6] + numbersAmount[5] + numbersAmount[4] < 2 || numbersAmount[6] + numbersAmount[5] < 1 ||
                (numbersAmount[6] == 0 && numbersAmount[5] < 3 && numbersAmount[4] < 1) || (numbersAmount[6] < 2 && numbersAmount[5] < 2 && numbersAmount[4] < 2)) {
            int randomIndex;
            for (int i = 0; i < 7; i++) {
                randomIndex = StaticStuff.randomNumber(0, 6);
                if ((rollResults[randomIndex] - 7) == 5 || (rollResults[randomIndex] - 7) == 4) {
                    rollResults[randomIndex] = 6 + 7;
                    for (int j = 0; j < 7; j++) {
                        l_diceRoll[j].setText("<html>" + StaticStuff.prepareString("[[yellow:" + rollResults[j] + "]]"));
                        l_diceRoll[randomIndex].setText("<html>" + StaticStuff.prepareString("[[gold:" + rollResults[randomIndex] + "]]"));
                        Sleep.milliseconds(50);
                    }
                    return false;
                }
                if ((rollResults[randomIndex] - 7) <= 3) {
                    rollResults[randomIndex] = 5 + 7;
                    for (int j = 0; j < 7; j++) {
                        l_diceRoll[j].setText("<html>" + StaticStuff.prepareString("[[yellow:" + rollResults[j] + "]]"));
                        l_diceRoll[randomIndex].setText("<html>" + StaticStuff.prepareString("[[gold:" + rollResults[randomIndex] + "]]"));
                        Sleep.milliseconds(50);
                    }
                    return false;
                }
            }
        }
        for (int j = 0; j < 7; j++) {
            l_diceRoll[j].setText("<html>" + StaticStuff.prepareString("[[yellow:" + rollResults[j] + "]]"));
            Sleep.milliseconds(50);
        }
        return true;
    }

    int attributeClicked = -1, attributesSetAmount = 0, attributesRollAmount = 0, selectedClassID = -1;
    int[] rollResults = new int[7];
    int[] rollPlaced = new int[7];
    int[] rollAttributeValue = new int[7];
    String[][] classData;
    String[] classDataNames;
    String[][] classDataCond;
    boolean rolling = false;
    boolean newAttributeClicked = false;
    boolean[] classAvailable = new boolean[7];
    boolean rollAll = false;

    private void clickRoll(int id) {
        if (!rolling || rollAll)
            if (rollResults[id] == -1) {
                new Thread(() -> {
                    rolling = true;
                    if (!rollAll)
                        rollResults[id] = StaticStuff.rollDice(lang("chCreationRoll") + (id + 1), 6, 10, rollAll) + 7;
                    else rollResults[id] = StaticStuff.randomNumber(1, 6) + 7;
                    //rollResults[id] = Integer.parseInt(StaticStuff.openPopup("Roll value:", ""));
                    if (!rollAll) new GuiHoverText((rollResults[id] - 7) + " + 7 = " + rollResults[id]);
                    l_diceRoll[id].setText("<html>" + StaticStuff.prepareString("[[yellow:" + rollResults[id] + "]]"));
                    rolling = false;
                    attributesRollAmount++;
                    if (attributesRollAmount == 7) {
                        while (!checkIfMinRequirements()) ;
                        for (int i = 20; i < 35; i++) {
                            l_goodAttributes.setText("<html>" + StaticStuff.prepareString("[[black:" + lang("chCreationGoodAttributes") + "]]"));
                            Sleep.milliseconds(StaticStuff.randomNumber(1, 4) * i);
                            l_goodAttributes.setText("<html>" + StaticStuff.prepareString("[[aqua:" + lang("chCreationGoodAttributes") + "]]"));
                            Sleep.milliseconds(StaticStuff.randomNumber(1, 4) * i);
                        }
                        Sleep.milliseconds(200);
                        for (int i = 0; i < 7; i++) {
                            l_goodAttributesList[i].setText("<html>" + StaticStuff.prepareString("[[white:" + lang("playerAttr" + i) + "]]"));
                            Sleep.milliseconds(40);
                        }
                        for (int i = 0; i < 7; i++) {
                            l_goodAttributesList[i].setText("<html>" + StaticStuff.prepareString("[[black:" + lang("playerAttr" + i) + "]]"));
                            Sleep.milliseconds(40);
                        }
                        for (int i = 0; i < 7; i++) {
                            l_goodAttributesList[i].setText("<html>" + StaticStuff.prepareString("[[white:" + lang("playerAttr" + i) + "]]"));
                            Sleep.milliseconds(50);
                        }
                        for (int i = 0; i < 7; i++) {
                            l_goodAttributesList[i].setText("<html>" + StaticStuff.prepareString("[[black:" + lang("playerAttr" + i) + "]]"));
                            Sleep.milliseconds(60);
                        }
                        for (int i = 0; i < 7; i++) {
                            l_goodAttributesList[i].setText("<html>" + StaticStuff.prepareString("[[white:" + lang("playerAttr" + i) + "]]"));
                            Sleep.milliseconds(100);
                        }
                        new Thread(() -> {
                            while (!exit) {
                                try {
                                    Sleep.milliseconds(StaticStuff.randomNumber(1000, 5000));
                                    int chosen = StaticStuff.randomNumber(1, 8); //1;2;3-5;6-8
                                    if (chosen == 1 && canSelectClass) {
                                        for (int i = 0; i < 7; i++) {
                                            l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[black:" + classDataNames[i] + "]]"));
                                            Sleep.milliseconds(60);
                                        }
                                        for (int i = 0; i < 7; i++) {
                                            if (classAvailable[i])
                                                l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[yellow:" + classDataNames[i] + "]]"));
                                            else
                                                l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[gray:" + classDataNames[i] + "]]"));
                                            Sleep.milliseconds(100);
                                        }
                                        Sleep.milliseconds(100);
                                        if (selectedClassID != -1)
                                            l_availableClassesList[selectedClassID].setText("<html>" + StaticStuff.prepareString("[[green:" + classDataNames[selectedClassID] + "]]"));
                                    } else if (chosen == 2) {
                                        for (int i = 0; i < 7; i++) {
                                            l_goodAttributesList[i].setText("<html>" + StaticStuff.prepareString("[[black:" + lang("playerAttr" + i) + "]]"));
                                            Sleep.milliseconds(60);
                                        }
                                        for (int i = 0; i < 7; i++) {
                                            l_goodAttributesList[i].setText("<html>" + StaticStuff.prepareString("[[white:" + lang("playerAttr" + i) + "]]"));
                                            Sleep.milliseconds(100);
                                        }
                                    } else if (chosen > 2 && chosen < 6 && canSelectClass) {
                                        for (int i = 20; i < 30; i++) {
                                            l_availableClasses.setText("<html>" + StaticStuff.prepareString("[[gray:" + lang("chCreationAvailableClasses") + "]]"));
                                            Sleep.milliseconds(StaticStuff.randomNumber(1, 4) * i);
                                            l_availableClasses.setText("<html>" + StaticStuff.prepareString("[[aqua:" + lang("chCreationAvailableClasses") + "]]"));
                                            Sleep.milliseconds(StaticStuff.randomNumber(1, 4) * i);
                                        }
                                    } else if (chosen > 6 && chosen < 9) {
                                        for (int i = 20; i < 30; i++) {
                                            l_goodAttributes.setText("<html>" + StaticStuff.prepareString("[[black:" + lang("chCreationGoodAttributes") + "]]"));
                                            Sleep.milliseconds(StaticStuff.randomNumber(1, 4) * i);
                                            l_goodAttributes.setText("<html>" + StaticStuff.prepareString("[[aqua:" + lang("chCreationGoodAttributes") + "]]"));
                                            Sleep.milliseconds(StaticStuff.randomNumber(1, 4) * i);
                                        }
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                            exit = true;
                        }).start();
                    }
                }).start();
            } else if (rollPlaced[id] == -1) {
                new Thread(() -> {
                    newAttributeClicked = true;
                    for (int i = 0; i < 7; i++)
                        if (rollResults[i] != -1)
                            l_diceRoll[i].setText("<html>" + StaticStuff.prepareString("[[yellow:" + rollResults[i] + "]]"));
                    l_diceRoll[id].setText("<html>" + StaticStuff.prepareString("[[yellow:\\[ ]][[aqua:" + rollResults[id] + "]][[yellow: \\]]]"));
                    Sleep.milliseconds(200);
                    attributeClicked = -1;
                    newAttributeClicked = false;
                    while (attributeClicked == -1 && !newAttributeClicked) Sleep.milliseconds(200);
                    if (newAttributeClicked) return;
                    boolean taken = false;
                    for (int i = 0; i < 7; i++)
                        if (attributeClicked == rollPlaced[i]) {
                            taken = true;
                            break;
                        }
                    if (!taken) {
                        rollPlaced[id] = attributeClicked;
                        rollAttributeValue[attributeClicked] = rollResults[id];
                        int origPosX = l_diceRoll[id].getX(), origPosY = l_diceRoll[id].getY();
                        int destPosX = l_goodAttributesList[attributeClicked].getX() + Interpreter.getScaledValue(350), destPosY = l_goodAttributesList[attributeClicked].getY();
                        float diffX = destPosX - origPosX, diffY = destPosY - origPosY;
                        for (int i = 0; i < 50; i++) {
                            l_diceRoll[id].setBounds((int) (origPosX + ((diffX / 50) * i)), (int) (origPosY + ((diffY / 50) * i)), Interpreter.getScaledValue(120), Interpreter.getScaledValue(35));
                            Sleep.milliseconds(10);
                        }
                        l_diceRoll[id].setBounds(destPosX, destPosY, Interpreter.getScaledValue(120), Interpreter.getScaledValue(35));
                        l_diceRoll[id].setText("<html>" + StaticStuff.prepareString("[[yellow:" + rollResults[id] + "]]"));
                        attributesSetAmount++;
                        if (attributesSetAmount == 7) {
                            for (int i = 0; i < 7; i++) //reset color of available classes
                                l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[black:" + classDataNames[i] + "]]"));
                            Sleep.milliseconds(800); //fancy animation for the classes
                            for (int i = 20; i < 35; i++) {
                                l_availableClasses.setText("<html>" + StaticStuff.prepareString("[[gray:" + lang("chCreationAvailableClasses") + "]]"));
                                Sleep.milliseconds(StaticStuff.randomNumber(1, 4) * i);
                                l_availableClasses.setText("<html>" + StaticStuff.prepareString("[[aqua:" + lang("chCreationAvailableClasses") + "]]"));
                                Sleep.milliseconds(StaticStuff.randomNumber(1, 4) * i);
                            }
                            Sleep.milliseconds(200);
                            checkClassesAvailable(false);
                            for (int i = 0; i < 7; i++) {
                                if (classAvailable[i])
                                    l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[yellow:" + classDataNames[i] + "]]"));
                                else
                                    l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[gray:" + classDataNames[i] + "]]"));
                                Sleep.milliseconds(40);
                            }
                            for (int i = 0; i < 7; i++) {
                                l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[black:" + classDataNames[i] + "]]"));
                                Sleep.milliseconds(40);
                            }
                            for (int i = 0; i < 7; i++) {
                                if (classAvailable[i])
                                    l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[yellow:" + classDataNames[i] + "]]"));
                                else
                                    l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[gray:" + classDataNames[i] + "]]"));
                                Sleep.milliseconds(50);
                            }
                            for (int i = 0; i < 7; i++) {
                                l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[black:" + classDataNames[i] + "]]"));
                                Sleep.milliseconds(60);
                            }
                            for (int i = 0; i < 7; i++) {
                                if (classAvailable[i])
                                    l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[yellow:" + classDataNames[i] + "]]"));
                                else
                                    l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[gray:" + classDataNames[i] + "]]"));
                                Sleep.milliseconds(100);
                            }
                            Sleep.milliseconds(300);
                            for (int i = 0; i < 100; i++) {
                                p_attributes.setBounds(0, i * -1, x_size, Interpreter.getScaledValue(700));
                                setSize(Interpreter.getScaledValue(1381), Interpreter.getScaledValue(739 - (i / 2)));
                                Sleep.milliseconds(20);
                            }
                            canSelectClass = true;
                        }
                    }
                }).start();
            }
    }

    private void clickRollAll() {
        new Thread(() -> {
            l_diceRollAll.setVisible(false);
            rollAll = true;
            Sleep.milliseconds(400);
            for(int i=0;i<7;i++) {
                clickRoll(i);
                Sleep.milliseconds(50);
            }
        }).start();
    }

    private void checkClassesAvailable(boolean updateDisplay) {
        int minAmount;
        boolean available;
        for (int i = 0; i < 7; i++) {
            available = true; //("MU","courage")("KL","wisdom")("IN","intuition")("CH","charisma")("FF","dexterity")("GE","agility")("KK","strength")
            for (int j = 0; j < classDataCond[i].length; j++) {
                minAmount = Integer.parseInt(classDataCond[i][j].replaceAll("[a-zA-Z]", ""));
                if (classDataCond[i][j].contains("MU")) if (rollAttributeValue[0] < minAmount) {
                    available = false;
                    break;
                }
                if (classDataCond[i][j].contains("KL")) if (rollAttributeValue[1] < minAmount) {
                    available = false;
                    break;
                }
                if (classDataCond[i][j].contains("IN")) if (rollAttributeValue[2] < minAmount) {
                    available = false;
                    break;
                }
                if (classDataCond[i][j].contains("CH")) if (rollAttributeValue[3] < minAmount) {
                    available = false;
                    break;
                }
                if (classDataCond[i][j].contains("FF")) if (rollAttributeValue[4] < minAmount) {
                    available = false;
                    break;
                }
                if (classDataCond[i][j].contains("GE")) if (rollAttributeValue[5] < minAmount) {
                    available = false;
                    break;
                }
                if (classDataCond[i][j].contains("KK")) if (rollAttributeValue[6] < minAmount) {
                    available = false;
                    break;
                }
            }
            classAvailable[i] = available;
            if (updateDisplay)
                if (available)
                    l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[yellow:" + classDataNames[i] + "]]"));
                else
                    l_availableClassesList[i].setText("<html>" + StaticStuff.prepareString("[[gray:" + classDataNames[i] + "]]"));
        }
    }

    private String lang(String text) {
        return Interpreter.lang(text);
    }
}