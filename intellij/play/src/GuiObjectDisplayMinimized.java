
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GuiObjectDisplayMinimized extends JFrame {
    private JLabel l_objectName;
    private JLabel l_variable;
    private int x_size = 2, y_size = 2;
    private int currentY = 5;
    private String desc;
    private BufferedImage image;
    private boolean imageActive = false;
    private GuiObjectDisplayMinimized self = this;
    private Entity entity;

    public GuiObjectDisplayMinimized(Entity entity) {
        if (entity == null) return;
        this.entity = entity;
        try {
            this.setTitle(entity.name);

            int textWidth = StaticStuff.getLongestLineWidth(entity.name.split("<br>"), StaticStuff.getPixelatedFont());
            int textHeight = StaticStuff.getTextHeightWithFont(entity.name, StaticStuff.getPixelatedFont());
            x_size = Interpreter.getScaledValue(textWidth + 100);
            y_size = Interpreter.getScaledValue(textHeight + 32);

            JPanel contentPane = new JPanel(null) {
                public void paintComponent(Graphics g) {
                    g.setColor(StaticStuff.getColor("white_border"));
                    g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
                    if (entity.type.equals("Color"))
                        g.setColor(StaticStuff.getColor(entity.name));
                    else g.setColor(StaticStuff.getColor("background"));
                    g.fillRoundRect(Interpreter.getScaledValue(3), Interpreter.getScaledValue(3), x_size - Interpreter.getScaledValue(6), y_size - Interpreter.getScaledValue(6), 20, 20);
                }
            };
            setUndecorated(true);
            setAlwaysOnTop(true);
            contentPane.setBackground(new Color(0, 0, 0, 0));
            setBackground(new Color(0, 0, 0, 0));
            setIconImage(new ImageIcon("res/img/icongreen.png").getImage());
            this.setSize(x_size, y_size);
            contentPane.setPreferredSize(new Dimension(x_size, y_size));

            l_objectName = new JLabel("<html><center><b>" + StaticStuff.prepareString(entity.name), SwingConstants.CENTER);
            l_objectName.setBounds(Interpreter.getScaledValue(4), Interpreter.getScaledValue(-2), x_size, textHeight);
            l_objectName.setBackground(new Color(214, 217, 223));
            l_objectName.setForeground(new Color(255, 255, 255));
            l_objectName.setEnabled(true);
            l_objectName.setFont(StaticStuff.getPixelatedFont());
            l_objectName.setVisible(true);
            contentPane.add(l_objectName);

            l_variable = new JLabel("test", SwingConstants.CENTER);
            Log.add(textHeight);
            l_variable.setBounds(Interpreter.getScaledValue(4), textHeight - Interpreter.getScaledValue(15), x_size, textHeight);
            l_variable.setBackground(new Color(214, 217, 223));
            l_variable.setForeground(new Color(255, 255, 255));
            l_variable.setEnabled(true);
            l_variable.setFont(StaticStuff.getPixelatedFont(10f));
            l_variable.setVisible(true);
            contentPane.add(l_variable);
            containsValidVariables = getAvailableVariables();
            nextVariable();

            this.add(contentPane);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setLocationRelativeTo(null);
            setLocation(getX() + StaticStuff.randomNumber(Interpreter.getScaledValue(-200), Interpreter.getScaledValue(200)), getY() + StaticStuff.randomNumber(Interpreter.getScaledValue(-200), Interpreter.getScaledValue(200)));
            this.pack();
            this.setVisible(true);

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    nextVariable();
                }
            });

            addListener(this);
            addListener(l_objectName);
            Log.add("Opened minimized object " + entity.uid);
        } catch (Exception e) {
            Log.add("Unable to open minimized object: " + e);
        }
    }

    private int variableIndex = -1;
    private ArrayList<String> containsValidVariables;

    private void nextVariable() {
        if (containsValidVariables == null)
            l_variable.setText("<html>" + StaticStuff.prepareString(Interpreter.lang("popupObjectNoVariables")));
        variableIndex = (variableIndex + 1) % containsValidVariables.size();
        if (Interpreter.lang("varNames" + containsValidVariables.get(variableIndex)).equals("missingString"))
            l_variable.setText("<html>" + StaticStuff.prepareString("[[aqua:" + containsValidVariables.get(variableIndex) + "]] = [[blue:" + entity.getVariableValueSilent(containsValidVariables.get(variableIndex)) + "]]"));
        else
            l_variable.setText("<html>" + StaticStuff.prepareString("[[aqua:" + Interpreter.lang("varNames" + containsValidVariables.get(variableIndex)) + "]] = [[blue:" + entity.getVariableValueSilent(containsValidVariables.get(variableIndex)) + "]]"));
    }

    private ArrayList<String> getAvailableVariables() {
        ArrayList<String> vars = new ArrayList<>();
        for (String s : Interpreter.getObjectFrameVariables())
            if (entity.variableExists(s))
                vars.add(s);
        if (vars.size() == 0)
            return null;
        return vars;
    }

    public void updateFrameObject(Entity entity) {
        if (containsValidVariables == null)
            l_variable.setText("<html>" + StaticStuff.prepareString(Interpreter.lang("popupObjectNoVariables")));
        if (Interpreter.lang("varNames" + containsValidVariables.get(variableIndex)).equals("missingString"))
            l_variable.setText("<html>" + StaticStuff.prepareString("[[aqua:" + containsValidVariables.get(variableIndex) + "]] = [[blue:" + entity.getVariableValueSilent(containsValidVariables.get(variableIndex)) + "]]"));
        else
            l_variable.setText("<html>" + StaticStuff.prepareString("[[aqua:" + Interpreter.lang("varNames" + containsValidVariables.get(variableIndex)) + "]] = [[blue:" + entity.getVariableValueSilent(containsValidVariables.get(variableIndex)) + "]]"));
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
                    dispose();
                    GuiObjectDisplay.close(entity);
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
}
