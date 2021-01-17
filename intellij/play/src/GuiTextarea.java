import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiTextarea extends JFrame {
    private JMenuBar menuBar;
    private JTextField ta_input;
    private JTextArea ta_main;
    private static ArrayList<JLabel> l_mainLabels = new ArrayList<>();
    private static ArrayList<String> textBuffer = new ArrayList<String>();
    public static String waitForUserInputLastString = "";
    private Interpreter interpreter;
    private int pX, pY;
    private static JPanel contentPane = new JPanel(null);
    private static GuiTextarea self;

    public GuiTextarea(Interpreter interpreter) {
        this.self = this;
        this.interpreter = interpreter;
        this.setTitle(StaticStuff.projectName);
        this.setSize(Interpreter.getScaledValue(1360), Interpreter.getScaledValue(780));

        contentPane.setPreferredSize(new Dimension(Interpreter.getScaledValue(1360), Interpreter.getScaledValue(780)));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/iconyellow.png").getImage());

        ta_input = new JTextField();
        ta_input.setBounds(4, 732, 1352, 35);
        ta_input.setBackground(new Color(255, 255, 255));
        ta_input.setForeground(new Color(0, 0, 0));
        ta_input.setEnabled(true);
        ta_input.setFont(StaticStuff.getPixelatedFont());
        ta_input.setText("");
        ta_input.setBorder(BorderFactory.createLineBorder(StaticStuff.getColor("white_border"), 1));
        ta_input.setVisible(true);
        ta_input.setFocusTraversalKeysEnabled(false);
        ta_input.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                keyPressedEvent(evt);
            }
        });

        contentPane.add(ta_input);

        int heightFactor = Interpreter.getScaledValue(49);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = e.getComponent().getWidth();
                int height = e.getComponent().getHeight();
                ta_input.setBounds(4, height - 78 + (35 - Interpreter.getScaledValue(35)), width - 24, Interpreter.getScaledValue(35));
                //l_main.setBounds(4,-10+((height-78)%heightFactor) + (35 - Interpreter.getScaledValue(35)),15000,height-78);
                int currentAmountLines = (int) ((height - 78 - ((height - 78) % heightFactor)) / heightFactor);

                setAmountMainLabels(currentAmountLines + 3);
                distributeMainLabels(height - 78 + (35 - Interpreter.getScaledValue(35)));

                updateOutput();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                new Thread() {
                    public void run() {
                        closeOperation();
                    }
                }.start();
            }
        });

        addDragListener(this);

        addDropListener(this);
        addDropListener(ta_input);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
    }

    private void addDragListener(Component c) {
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON1) {
                    pX = me.getX();
                    pY = me.getY();
                    dragActive = true;
                } else dragActive = false;
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

    private boolean dragActive = false;

    private static void setAmountMainLabels(int amount) {
        if (l_mainLabels.size() >= amount) return;
        int counter = l_mainLabels.size();
        do {
            l_mainLabels.add(new JLabel());
            l_mainLabels.get(counter).setBounds(4, -10, self.getWidth(), 727);
            l_mainLabels.get(counter).setBackground(StaticStuff.getColor("background"));
            l_mainLabels.get(counter).setForeground(new Color(255, 255, 255));
            l_mainLabels.get(counter).setEnabled(true);
            l_mainLabels.get(counter).setFont(StaticStuff.getPixelatedFont());
            ;
            l_mainLabels.get(counter).setHorizontalAlignment(JLabel.LEFT);
            l_mainLabels.get(counter).setVerticalAlignment(JLabel.TOP);
            l_mainLabels.get(counter).setVisible(true);
            l_mainLabels.get(counter).addMouseWheelListener(new MouseWheelListener() {
                public void mouseWheelMoved(MouseWheelEvent evt) {
                    scroll(evt);
                }
            });
            contentPane.add(l_mainLabels.get(counter));
            self.addDragListener(l_mainLabels.get(counter));
            counter++;
        } while (l_mainLabels.size() < amount);
    }

    private static int changeMainLabelHeightPerStep = Interpreter.getScaledValue(49);

    private static void distributeMainLabels(int height) {
        for (int i = 0; i < l_mainLabels.size(); i++) {
            l_mainLabels.get(i).setBounds(4, -1000, 0, 0);
        }
        for (int i = 0; height > 0; i++) {
            height -= changeMainLabelHeightPerStep;
            setAmountMainLabels(i);
            l_mainLabels.get(i).setBounds(4, height, self.getWidth(), changeMainLabelHeightPerStep + 2);
        }
    }

    private static void setMainLabelText(int index, String text) {
        l_mainLabels.get(index).setText(text);
    }

    private static void clearAllMainLabels() {
        for (int i = 0; i < l_mainLabels.size(); i++) {
            l_mainLabels.get(i).setText("");
        }
    }

    //thank you rustyx for this code (https://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path):
    private void addDropListener(Component c) {
        c.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    new Thread() {
                        public void run() {
                            for (File file : droppedFiles) {
                                dropFile(file.getAbsolutePath());
                            }
                        }
                    }.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void closeOperation() {
        if (0 == StaticStuff.openPopup(Interpreter.lang("battleMapCloseCheck"), new String[]{Interpreter.lang("battleMapCloseCheckClose"), Interpreter.lang("battleMapCloseCheckNotClose")})) {
            interpreter.executeEventFromGeneralEventCollection("exit", new String[]{});
            FileManager.clearTmp();
            System.exit(0);
        }
    }

    private void dropFile(String path) {
        if (interpreter.getSetting("debugMode").equals("true")) {
            Log.add("Drag and drop event: execute code from file: " + path);
            interpreter.executeEvent("", FileManager.readFile(path), new String[]{});
        } else {
            Log.add("Drag and drop event failed: Debug mod  e disabled");
        }
    }

    public void visible(boolean show) {
        setVisible(show);
    }

    static int scrollIndex = 0;

    private static void scroll(MouseWheelEvent evt) {
        if (evt.getUnitsToScroll() > 0) {
            if (scrollIndex > 0) scrollIndex--;
        } else
            scrollIndex++;
        updateOutput();
    }

    int getIndex = -1;
    String ownText = "";

    private void keyPressedEvent(KeyEvent evt) {
        if (evt.getKeyCode() == 10) { //enter
            if (playerInputActive) {
                playerAppend(ta_input.getText());
                ta_input.setText("");
                getIndex = -1;
            } else {
                new GuiHoverText("<html>" + StaticStuff.prepareString(Interpreter.lang("mainFramePlayerInputDisabled")));
            }
        } else if (evt.getKeyCode() == 27) { //escape
            ta_input.setText("");
            getIndex = -1;
        } else if (evt.getKeyCode() == 9) { //tab
            autoComplete();
        } else if (evt.getKeyCode() == 38) { //up
            if (getIndex == -1) ownText = ta_input.getText();
            if (getIndex < textBuffer.size() - 1)
                do getIndex++;
                while (getIndex < textBuffer.size() - 1 && !(textBuffer.get(getIndex).toString().charAt(0) == ' ') && !(textBuffer.get(getIndex).toString().charAt(1) == '-'));
            ta_input.setText(StaticStuff.removePrepareString(textBuffer.get(getIndex).replaceAll(" - (.+)", "$1")));
        } else if (evt.getKeyCode() == 40) { //down
            if (getIndex == 0) {
                getIndex--;
                ta_input.setText(StaticStuff.removePrepareString(ownText));
            } else if (getIndex > 0) {
                do getIndex--;
                while (getIndex > 0 && !(textBuffer.get(getIndex).toString().charAt(0) == ' ') && !(textBuffer.get(getIndex).toString().charAt(1) == '-'));
                if (getIndex == 0 && !(textBuffer.get(getIndex).toString().charAt(0) == ' ') && !(textBuffer.get(getIndex).toString().charAt(1) == '-')) {
                    ta_input.setText(StaticStuff.removePrepareString(ownText));
                    getIndex--;
                } else {
                    ta_input.setText(StaticStuff.removePrepareString(textBuffer.get(getIndex).replaceAll(" - (.+)", "$1")));
                }
            }
        }
    }

    private ArrayList<String> autoCompleteWords = new ArrayList<>();

    public void addAutoCompleteWords(String[] words) {
        for (String s : words) autoCompleteWords.add(s);
    }

    private void autoComplete() {
        if (ta_input.getText().length() <= 0) return;
        int curorPos = ta_input.getCaretPosition();
        String text = ta_input.getText();
        String beforeCursor = text.substring(0, curorPos), afterCursor = text.substring(curorPos, text.length());
        String words[] = beforeCursor.split(" ");
        String completeWord = words[words.length - 1];
        for (String s : autoCompleteWords) {
            if (s.matches(completeWord + ".+")) {
                ta_input.setText(StaticStuff.replaceLast(beforeCursor, completeWord, s) + afterCursor);
                return;
            }
        }
    }

    public void playerAppend(String text) {
        if (GuiTextarea.waitForUserInputLastString.equals("waitingForInput")) {
            Log.add("Player entered value for {input|line}: " + text);
            waitForUserInputLastString = StaticStuff.removePrepareString(text);
            addToTextArea(" - " + StaticStuff.prepareStringForPlayer(text));
            scrollIndex = 0;
            updateOutput();
            StaticStuff.lastInput = text;
            return;
        } else {
            waitForUserInputLastString = StaticStuff.removePrepareString(text);
            addToTextArea(" - " + StaticStuff.prepareStringForPlayer(text));
            scrollIndex = 0;
            updateOutput();
        }
        executePlayerCommand(text);
        StaticStuff.lastInput = text;
    }

    public static void appendToOutput(String text) {
        addToTextArea(StaticStuff.prepareString(text));
        scrollIndex = 0;
        updateOutput();
    }

    private static void addToTextArea(String text) {
        Log.add("Printing: " + text);
        textBuffer.add(0, text);
    }

    private static int lineLength = 90, amountLines = 15;

    private static void updateOutput() {
        clearAllMainLabels();
        amountLines = l_mainLabels.size();
        int width = self.getWidth();
        int startHeight = self.getHeight() - 78 + (35 - Interpreter.getScaledValue(35));
        for (int i = 0; i < amountLines && textBuffer.size() > scrollIndex + i; i++) {
            String lineText = "<html><font color=\"" + StaticStuff.colorToHex(Manager.getColorByName("white")) + "\">" + textBuffer.get(scrollIndex + i) + "</font>";
            setMainLabelText(i, lineText);
            int lineTextWidth = StaticStuff.getTextWidthWithFont(StaticStuff.removePrepareString(lineText.replaceAll("\\[\\[[^:]+:([^\\]]+)\\]\\]", "$1")), StaticStuff.getPixelatedFont());
            int amountLinesRequired = (int) Math.ceil(Double.parseDouble(lineTextWidth + "") / Double.parseDouble(width + ""));
            startHeight -= changeMainLabelHeightPerStep * amountLinesRequired;
            l_mainLabels.get(i).setBounds(4, startHeight, self.getWidth(), (changeMainLabelHeightPerStep + 2) * amountLinesRequired);
            //startHeight -= changeMainLabelHeightPerStep;
        }

        /*for(int i=0;i<amountLines && textBuffer.size() > scrollIndex+i;i++) {
        String line = textBuffer.get(scrollIndex+i);
        System.out.println("line " + line);
        System.out.println("line.length() " + line.length() + " ? " + maxPerLine);
        if(line.length() > maxPerLine) {
        String current;
        System.out.println("in here");
        do {
        current = line.substring(0, maxPerLine);
        line = line.replace(current, "");
        linesBuffer.add(current);
        } while (line.length() > maxPerLine);
        if(line.length() > 0) {
        linesBuffer.add(line);
        }
        for(int j=linesBuffer.size()-1;j<=0;j--) {
        setMainLabelText(i + linebreakModifier,"<html><font color=\""+StaticStuff.colorToHex(Manager.getColorByName("white"))+"\">" + linesBuffer.get(j) +"</font>");
        linebreakModifier++;
        System.out.println("linesBuffer.get(j) " + linesBuffer.get(j));
        }
        linesBuffer.clear();
        } else {
        setMainLabelText(i + linebreakModifier,"<html><font color=\""+StaticStuff.colorToHex(Manager.getColorByName("white"))+"\">" + line +"</font>");
        }
        }*/

        /*String display = "";
        try{
        display = textBuffer.get(scrollIndex);
        for(int i=1;i<amountLines && i<textBuffer.size();i++) display += "<br>"+textBuffer.get(scrollIndex+i);
        }catch(Exception e){}
        String splitted[] = display.split("<br>"); display = splitted[splitted.length-1];
        for(int i=splitted.length-2;i>=0;i--) display += "<br><font color=\""+StaticStuff.colorToHex(Manager.getColorByName("white"))+"\">"+splitted[i]+"</font>";
        for(int i=0;i<amountLines-splitted.length;i++) display = "<br>"+display;
        l_main.setText("<html><p style=\"line-height: 70%;\">"+display+"</p></html>");*/
    }

    private boolean playerInputActive = true;

    public void setPlayerInputActive(boolean active) {
        playerInputActive = active;
    }

    private void executePlayerCommand(String cmd) {
        new Thread() {
            public void run() {
                interpreter.executePlayerCommand(cmd);
            }
        }.start();
    }

    public void clearChat() {
        textBuffer.clear();
        scrollIndex = 0;
        updateOutput();
    }

    public void battleMode(boolean active) {
        setVisible(!active);
    }
}