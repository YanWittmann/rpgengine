
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuiBattleMap extends JFrame {
    private final Interpreter interpreter;
    private final BattleMap battleMap;

    private final JPanel contentPane;

    private JLabel l_BattleTitle;

    private JLabel[][] l_goundTiles;
    private JLabel[][] l_extraGoundTiles;
    private JLabel[][] l_obstacles;
    private JLabel[][] l_overlay;
    private JLabel[][] l_clickable;
    private JLabel l_playerPos, l_attackAnimation, l_nextTurn, l_close;
    private final ArrayList<JLabel> l_npcs = new ArrayList<JLabel>();
    private final ArrayList<JLabel> l_items = new ArrayList<JLabel>();
    private final JLabel dummy = new JLabel();

    private ImageIcon groundTile, scaledGroundTile, selectedTile, scaledSelectedTile, clickedTile, scaledClickedTile, playerTile, scaledPlayerTile;
    private ImageIcon overlayWalkable, scaledOverlayWalkable, overlayCurrentNPC, scaledOverlayCurrentNPC;

    private final int tileSize;
    private final int x_offset = Interpreter.getScaledValue(40);
    private final int y_offset = Interpreter.getScaledValue(40);
    private final int currentMode = 0;
    private int x_size = Interpreter.getScaledValue(1140), y_size = Interpreter.getScaledValue(920);

    public GuiBattleMap(BattleMap battleMap, Interpreter interpreter) {
        this.battleMap = battleMap;
        this.interpreter = interpreter;
        this.setTitle(StaticStuff.projectName + " - Battle time!");
        int scale = Interpreter.getScaledValue(282 / battleMap.size);
        tileSize = 3 * scale;
        x_size = tileSize * battleMap.size + Interpreter.getScaledValue(80);
        y_size = tileSize * battleMap.size + Interpreter.getScaledValue(80);
        this.setSize(x_size, y_size);

        contentPane = new JPanel(null) {
            public void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255));
                g.fillRoundRect(0, 0, x_size, y_size, 20, 20);
                g.setColor(new Color(0, 0, 0));
                g.fillRoundRect(3, 3, x_size - 6, y_size - 6, 20, 20);
            }
        };
        contentPane.setPreferredSize(new Dimension(x_size, y_size));
        setUndecorated(true);
        setIconImage(new ImageIcon("res/img/icongreen.png").getImage());
        setBackground(new Color(0, 0, 0, 0));

        if (battleMap.name.length() > 0)
            new Thread() {
                public void run() {
                    Sleep.milliseconds(1000);
                    l_BattleTitle = new JLabel("<html>" + StaticStuff.prepareString(battleMap.name), SwingConstants.CENTER);
                    l_BattleTitle.setBounds(50, y_size / 2, x_size - 100, 60);
                    l_BattleTitle.setBackground(new Color(214, 217, 223));
                    l_BattleTitle.setForeground(new Color(255, 255, 255));
                    l_BattleTitle.setEnabled(true);
                    l_BattleTitle.setFont(StaticStuff.getPixelatedFont(26f));
                    l_BattleTitle.setVisible(false);
                    contentPane.add(l_BattleTitle);
                    String toDisplay = l_BattleTitle.getText();
                    l_BattleTitle.setText("");
                    l_BattleTitle.setVisible(true);
                    for (int i = 1; i < toDisplay.length(); i++) {
                        if (toDisplay.charAt(i) == '<' || toDisplay.charAt(i - 1) == '<') {
                            while (toDisplay.charAt(i) != '>' && i < toDisplay.length()) i++;
                            i++;
                        }
                        Sleep.milliseconds(StaticStuff.randomNumber(20, 150));
                        l_BattleTitle.setText(toDisplay.substring(0, i));
                    }
                    l_BattleTitle.setText(toDisplay);
                    Sleep.milliseconds(1500);
                    toDisplay = l_BattleTitle.getText();
                    for (int i = toDisplay.length() - 2; i > 0; i--) {
                        if (toDisplay.charAt(i) == '>' || toDisplay.charAt(i - 1) == '>') {
                            while (toDisplay.charAt(i) != '<' && i > 0) i--;
                            if (i > 0) i--;
                        }
                        Sleep.milliseconds(StaticStuff.randomNumber(20, 50));
                        l_BattleTitle.setText(toDisplay.substring(0, i));
                    }
                    l_BattleTitle.setText("");

                    if (battleMap.description.length() > 0) {
                        l_BattleTitle.setFont(StaticStuff.getPixelatedFont(20f));
                        toDisplay = "<html><center>" + StaticStuff.prepareString(battleMap.description);
                        for (int i = 1; i < toDisplay.length(); i++) {
                            if (toDisplay.charAt(i) == '<' || toDisplay.charAt(i - 1) == '<') {
                                while (toDisplay.charAt(i) != '>' && i < toDisplay.length()) i++;
                                i++;
                            }
                            Sleep.milliseconds(StaticStuff.randomNumber(0, 70));
                            l_BattleTitle.setText(toDisplay.substring(0, i));
                        }
                        l_BattleTitle.setText(toDisplay);
                        Sleep.milliseconds(toDisplay.replaceAll("<[^>]+>", "").length() * 60);
                        toDisplay = l_BattleTitle.getText();
                        for (int i = toDisplay.length() - 2; i > 0; i--) {
                            if (toDisplay.charAt(i) == '>' || toDisplay.charAt(i - 1) == '>') {
                                while (toDisplay.charAt(i) != '<' && i > 0) i--;
                                if (i > 0) i--;
                            }
                            Sleep.milliseconds(StaticStuff.randomNumber(0, 30));
                            if (toDisplay.substring(0, i).equals("<html>") || toDisplay.substring(0, i).equals("<html"))
                                break;
                            l_BattleTitle.setText(toDisplay.substring(0, i));
                        }
                        l_BattleTitle.setText("");
                    }
                    l_BattleTitle.setVisible(false);
                    contentPane.remove(l_BattleTitle);

                    new Thread() {
                        public void run() {
                            letTheBattleBegin();
                        }
                    }.start();
                }
            }.start();
        else letTheBattleBegin();

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);

        addListener(this);
        addListener(contentPane);
        addDropListener(this);
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

    private void letTheBattleBegin() {
        selectedTile = new ImageIcon(Objects.requireNonNull(Images.readImageFromFile("res/img/selected.png")));
        scaledSelectedTile = getScaledImage(selectedTile, tileSize, tileSize);
        clickedTile = new ImageIcon(Objects.requireNonNull(Images.readImageFromFile("res/img/clicked.png")));
        scaledClickedTile = getScaledImage(clickedTile, tileSize, tileSize);
        l_clickable = new JLabel[battleMap.size][battleMap.size];
        for (int i = 0; i < battleMap.size; i++) {
            for (int j = 0; j < battleMap.size; j++) {
                l_clickable[i][j] = new JLabel();
                l_clickable[i][j].setBounds(x_offset + (tileSize * i), y_offset + (tileSize * j), tileSize, tileSize);
                l_clickable[i][j].setBackground(new Color(214, 217, 223));
                l_clickable[i][j].setForeground(new Color(0, 0, 0));
                l_clickable[i][j].setEnabled(true);
                l_clickable[i][j].setVisible(true);
                final int x = i, y = j;
                final JLabel label = l_clickable[i][j];
                l_clickable[i][j].addMouseListener(new MouseListener() {
                    public void mouseReleased(MouseEvent e) {
                        tileReleased(label);
                    }

                    public void mousePressed(MouseEvent e) {
                        tilePressed(label);
                    }

                    public void mouseExited(MouseEvent e) {
                        tileExited(label);
                    }

                    public void mouseEntered(MouseEvent e) {
                        tileEntered(label);
                    }

                    public void mouseClicked(MouseEvent e) {
                        tileClicked(e, x, y);
                    }
                });
                contentPane.add(l_clickable[i][j]);
            }
        }

        overlayWalkable = new ImageIcon(Objects.requireNonNull(Images.readImageFromFile("res/img/walkableTile.png")));
        scaledOverlayWalkable = getScaledImage(overlayWalkable, tileSize, tileSize);
        overlayCurrentNPC = new ImageIcon(Objects.requireNonNull(Images.readImageFromFile("res/img/currentNPC.png")));
        scaledOverlayCurrentNPC = getScaledImage(overlayCurrentNPC, tileSize, tileSize);
        l_overlay = new JLabel[battleMap.size][battleMap.size];
        for (int i = 0; i < battleMap.size; i++) {
            for (int j = 0; j < battleMap.size; j++) {
                l_overlay[i][j] = new JLabel();
                l_overlay[i][j].setBounds(x_offset + (tileSize * i), y_offset + (tileSize * j), tileSize, tileSize);
                l_overlay[i][j].setBackground(new Color(214, 217, 223));
                l_overlay[i][j].setForeground(new Color(0, 0, 0));
                l_overlay[i][j].setEnabled(true);
                l_overlay[i][j].setVisible(true);
                contentPane.add(l_overlay[i][j]);
            }
        }

        l_attackAnimation = new JLabel();
        l_attackAnimation.setBounds(-1000, -1000, tileSize, tileSize);
        l_attackAnimation.setIcon(scaledPlayerTile);
        l_attackAnimation.setEnabled(true);
        l_attackAnimation.setVisible(true);
        contentPane.add(l_attackAnimation);

        int nextTurnScale = 8;
        l_nextTurn = new JLabel();
        l_nextTurn.setBounds((x_size / 2) - Interpreter.getScaledValue(256 / nextTurnScale), y_size - Interpreter.getScaledValue(280 / nextTurnScale), Interpreter.getScaledValue(512 / nextTurnScale), Interpreter.getScaledValue(200 / nextTurnScale));
        l_nextTurn.setIcon(getScaledImage(new ImageIcon(Objects.requireNonNull(Images.readImageFromFile("res/img/nextturn.png"))), Interpreter.getScaledValue(512 / nextTurnScale), Interpreter.getScaledValue(200 / nextTurnScale)));
        l_nextTurn.setEnabled(true);
        l_nextTurn.setVisible(true);
        l_nextTurn.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                if (BattleMap.isPlayerTurn)
                    new Thread(() -> {
                        if (BattleMap.isPlayerTurn && !battleMap.playerIsWalking) battleMap.beginNextTurn();
                    }).start();
            }
        });
        contentPane.add(l_nextTurn);

        playerTile = new ImageIcon(Objects.requireNonNull(Images.getBufferedImage(Manager.player.getValue("battleMapImage"))));
        scaledPlayerTile = getScaledImage(playerTile, tileSize, tileSize);
        l_playerPos = new JLabel();
        l_playerPos.setBounds(x_offset + (tileSize * Integer.parseInt(battleMap.playerPos.split("AAA")[0])), y_offset + (tileSize * Integer.parseInt(battleMap.playerPos.split("AAA")[1])), tileSize, tileSize);
        l_playerPos.setBackground(new Color(214, 217, 223));
        l_playerPos.setForeground(new Color(0, 0, 0));
        l_playerPos.setIcon(scaledPlayerTile);
        l_playerPos.setEnabled(true);
        l_playerPos.setVisible(true);
        contentPane.add(l_playerPos);

        for (int i = 0; i < battleMap.npcs.size(); i++) {
            String[] splitted = battleMap.npcs.get(i).split("AAA");
            l_npcs.add(new JLabel());
            if (Integer.parseInt(splitted[1]) < battleMap.size && Integer.parseInt(splitted[2]) < battleMap.size) {
                l_npcs.get(l_npcs.size() - 1).setBounds(x_offset + (tileSize * Integer.parseInt(splitted[1])), y_offset + (tileSize * Integer.parseInt(splitted[2])), tileSize, tileSize);
                l_npcs.get(l_npcs.size() - 1).setBackground(new Color(214, 217, 223));
                l_npcs.get(l_npcs.size() - 1).setForeground(new Color(0, 0, 0));
                l_npcs.get(l_npcs.size() - 1).setIcon(getScaledImage(new ImageIcon(Manager.getImage(splitted[3])), tileSize, tileSize));
                l_npcs.get(l_npcs.size() - 1).setEnabled(true);
                l_npcs.get(l_npcs.size() - 1).setVisible(true);
                contentPane.add(l_npcs.get(l_npcs.size() - 1));
            } else {
                battleMap.npcs.remove(i);
                l_npcs.remove(i);
                i--;
            }
        }

        for (int i = 0; i < battleMap.items.size(); i++) {
            String[] splitted = battleMap.items.get(i).split("AAA");
            l_items.add(new JLabel());
            if (Integer.parseInt(splitted[1]) < battleMap.size && Integer.parseInt(splitted[2]) < battleMap.size) {
                l_items.get(l_items.size() - 1).setBounds(x_offset + (tileSize * Integer.parseInt(splitted[1])), y_offset + (tileSize * Integer.parseInt(splitted[2])), tileSize, tileSize);
                l_items.get(l_items.size() - 1).setBackground(new Color(214, 217, 223));
                l_items.get(l_items.size() - 1).setForeground(new Color(0, 0, 0));
                l_items.get(l_items.size() - 1).setIcon(getScaledImage(new ImageIcon(Manager.getImage(Manager.getItemImageUID(splitted[0]))), tileSize, tileSize));
                l_items.get(l_items.size() - 1).setEnabled(true);
                l_items.get(l_items.size() - 1).setVisible(false);
                contentPane.add(l_items.get(l_items.size() - 1));
            } else {
                battleMap.items.remove(i);
                l_items.remove(i);
                i--;
            }
        }

        l_obstacles = new JLabel[battleMap.size][battleMap.size];
        for (int i = 0; i < battleMap.size; i++) {
            for (int j = 0; j < battleMap.size; j++) {
                l_obstacles[i][j] = new JLabel();
                l_obstacles[i][j].setBounds(x_offset + (tileSize * i), y_offset + (tileSize * j), tileSize, tileSize);
                l_obstacles[i][j].setBackground(new Color(214, 217, 223));
                l_obstacles[i][j].setForeground(new Color(0, 0, 0));
                l_obstacles[i][j].setEnabled(true);
                l_obstacles[i][j].setVisible(true);
                contentPane.add(l_obstacles[i][j]);
            }
        }
        for (int i = 0; i < battleMap.obstacles.size(); i++) {
            try {
                String splitted[] = battleMap.obstacles.get(i).split("AAA");
                l_obstacles[Integer.parseInt(splitted[1])][Integer.parseInt(splitted[2])].setIcon(getScaledImage(new ImageIcon(Manager.getImage(splitted[0])), tileSize, tileSize));
            } catch (Exception e) {
                battleMap.obstacles.remove(i);
                i--;
            }
        }

        l_extraGoundTiles = new JLabel[battleMap.size][battleMap.size];
        for (int i = 0; i < battleMap.size; i++) {
            for (int j = 0; j < battleMap.size; j++) {
                l_extraGoundTiles[i][j] = new JLabel();
                l_extraGoundTiles[i][j].setBounds(x_offset + (tileSize * i), y_offset + (tileSize * j), tileSize, tileSize);
                l_extraGoundTiles[i][j].setBackground(new Color(214, 217, 223));
                l_extraGoundTiles[i][j].setForeground(new Color(0, 0, 0));
                l_extraGoundTiles[i][j].setEnabled(true);
                l_extraGoundTiles[i][j].setVisible(true);
                contentPane.add(l_extraGoundTiles[i][j]);
            }
        }
        for (int i = 0; i < battleMap.extraGroundTiles.size(); i++) {
            try {
                String splitted[] = battleMap.extraGroundTiles.get(i).split("AAA");
                l_extraGoundTiles[Integer.parseInt(splitted[1])][Integer.parseInt(splitted[2])].setIcon(getScaledImage(new ImageIcon(Manager.getImage(splitted[0])), tileSize, tileSize));
            } catch (Exception e) {
                battleMap.extraGroundTiles.remove(i);
                i--;
            }
        }

        l_goundTiles = new JLabel[battleMap.size][battleMap.size];
        groundTile = new ImageIcon(Manager.getImage(battleMap.groundTileUID));
        scaledGroundTile = getScaledImage(groundTile, tileSize, tileSize);
        for (int i = 0; i < battleMap.size; i++) {
            for (int j = 0; j < battleMap.size; j++) {
                l_goundTiles[i][j] = new JLabel();
                l_goundTiles[i][j].setBounds(x_offset + (tileSize * i), y_offset + (tileSize * j), tileSize, tileSize);
                l_goundTiles[i][j].setBackground(new Color(214, 217, 223));
                l_goundTiles[i][j].setForeground(new Color(0, 0, 0));
                l_goundTiles[i][j].setEnabled(true);
                l_goundTiles[i][j].setVisible(true);
                l_goundTiles[i][j].setIcon(scaledGroundTile);
                contentPane.add(l_goundTiles[i][j]);
            }
        }

        l_close = new JLabel("<html>" + StaticStuff.prepareString(Interpreter.lang("battleMapClose")), SwingConstants.RIGHT);
        l_close.setBounds(x_size - Interpreter.getScaledValue(110), Interpreter.getScaledValue(10), Interpreter.getScaledValue(100), Interpreter.getScaledValue(20));
        l_close.setBackground(new Color(214, 217, 223));
        l_close.setForeground(new Color(255, 255, 255));
        l_close.setEnabled(true);
        l_close.setFont(StaticStuff.getPixelatedFont(12f));
        l_close.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                new Thread() {
                    public void run() {
                        if (0 == StaticStuff.openPopup(Interpreter.lang("battleMapCloseCheck"), new String[]{Interpreter.lang("battleMapCloseCheckClose"), Interpreter.lang("battleMapCloseCheckNotClose")})) {
                            interpreter.executeEventFromGeneralEventCollection("exit", new String[]{});
                            FileManager.clearTmp();
                            System.exit(0);
                        }
                    }
                }.start();
            }
        });
        contentPane.add(l_close);

        contentPane.remove(dummy);
        contentPane.add(dummy);
        updateBoard("all");
        //battleMap.beginNextTurn();
        battleMap.battleGuiIsReadyToStart();
    }

    private void tilePressed(JLabel label) {
        label.setIcon(scaledClickedTile);
    }

    private void tileReleased(JLabel label) {
        label.setIcon(null);
    }

    private void tileEntered(JLabel label) {
        label.setIcon(scaledSelectedTile);
    }

    private void tileExited(JLabel label) {
        label.setIcon(null);
    }

    private void tileClicked(MouseEvent e, int x, int y) {
        new Thread() {
            public void run() {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Log.add(x + " " + y + " left-clicked");
                    battleMap.leftClickOnTile(x, y);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    Log.add(x + " " + y + " right-clicked");
                    battleMap.rightClickOnTile(x, y);
                    String openObjects[] = battleMap.getTileContent(x, y);
                    for (int i = 0; i < openObjects.length; i++)
                        GuiObjectDisplay.create(battleMap.manager.getEntity(openObjects[i]), "");
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    Log.add(x + " " + y + " mouse-wheel-clicked");
                }
            }
        }.start();
    }

    public void setOverlay(int x, int y, int n) {
        if (n == 0) {
            l_overlay[x][y].setIcon(null);
        } else if (n == 1) {
            l_overlay[x][y].setIcon(scaledOverlayWalkable);
        } else if (n == 2) {
            l_overlay[x][y].setIcon(scaledOverlayCurrentNPC);
        }
    }

    public void clearOverlay() {
        for (int i = 0; i < battleMap.size; i++)
            for (int j = 0; j < battleMap.size; j++)
                l_overlay[i][j].setIcon(null);
    }

    public void attackAnimation(Coordinates from, Coordinates to, String imageUID) {
        l_attackAnimation.setIcon(getScaledImage(new ImageIcon(Manager.getImage(imageUID)), tileSize, tileSize));

        int origPosX = x_offset + (from.x * tileSize), origPosY = x_offset + (from.y * tileSize);
        int destPosX = x_offset + (to.x * tileSize), destPosY = x_offset + (to.y * tileSize);
        int diffX = destPosX - origPosX, diffY = destPosY - origPosY;
        double distance = calculateDistanceBetweenPoints(origPosX, origPosY, destPosX, destPosY);
        double deltaEachX = diffX / 100;
        double deltaEachY = diffY / 100;

        int duration = (int) (distance / (tileSize / 2));

        for (int i = 0; i < 100; i++) {
            l_attackAnimation.setBounds(origPosX + (int) (deltaEachX * i), origPosY + (int) (deltaEachY * i), tileSize, tileSize);
            Sleep.milliseconds(duration);
        }

        l_attackAnimation.setBounds(-1000, -1000, tileSize, tileSize);

        /*int origPosX = x_offset+(from.x*tileSize), origPosY = x_offset+(from.y*tileSize);
        int destPosX = x_offset+(to.x*tileSize), destPosY = x_offset+(to.y*tileSize);
        int diffX = destPosX-origPosX, diffY = destPosY-origPosY;
        float diff = (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2)) / 8;
        int duration = (int) diff;
        l_attackAnimation.setIcon(getScaledImage(new ImageIcon(Manager.getImage(imageUID)),tileSize,tileSize));
        for(int i=0;i<duration;i++){
        l_attackAnimation.setBounds(origPosX+((int)((diffX/diff)*i)),origPosY+((int)((diffY/diff)*i)),tileSize,tileSize);
        Sleep.milliseconds(duration);
        }
        l_attackAnimation.setBounds(-1000,-1000, tileSize, tileSize);*/
    }

    public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    public void updateBoard(String what) {
        generateBoard(what);
    }

    private void generateBoard(String what) {
        for (int i = 0; i < battleMap.size; i++)
            for (int j = 0; j < battleMap.size; j++) {
                contentPane.remove(l_clickable[i][j]);
                contentPane.add(l_clickable[i][j]);
            }

        if (what.contains("overlay") || what.contains("all")) {
            overlayWalkable = new ImageIcon(Images.readImageFromFile("res/img/walkableTile.png"));
            scaledOverlayWalkable = getScaledImage(overlayWalkable, tileSize, tileSize);
            overlayCurrentNPC = new ImageIcon(Images.readImageFromFile("res/img/currentNPC.png"));
            scaledOverlayCurrentNPC = getScaledImage(overlayCurrentNPC, tileSize, tileSize);
            for (int i = 0; i < battleMap.size; i++) {
                for (int j = 0; j < battleMap.size; j++) {
                    contentPane.remove(l_overlay[i][j]);
                    contentPane.add(l_overlay[i][j]);
                }
            }
        } else
            for (int i = 0; i < battleMap.size; i++)
                for (int j = 0; j < battleMap.size; j++) {
                    contentPane.remove(l_overlay[i][j]);
                    contentPane.add(l_overlay[i][j]);
                }

        if (what.contains("attackanimation") || what.contains("all")) {
            contentPane.remove(l_attackAnimation);
            l_attackAnimation.setBounds(-1000, -1000, tileSize, tileSize);
            l_attackAnimation.setIcon(scaledPlayerTile);
            l_attackAnimation.setEnabled(true);
            l_attackAnimation.setVisible(true);
            contentPane.add(l_attackAnimation);
        } else {
            contentPane.remove(l_attackAnimation);
            contentPane.add(l_attackAnimation);
        }

        if (what.contains("player") || what.contains("all")) {
            playerTile = new ImageIcon(Images.getBufferedImage(battleMap.manager.player.getValue("battleMapImage")));
            scaledPlayerTile = getScaledImage(playerTile, tileSize, tileSize);
            contentPane.remove(l_playerPos);
            l_playerPos.setBounds(x_offset + (tileSize * Integer.parseInt(battleMap.playerPos.split("AAA")[0])), y_offset + (tileSize * Integer.parseInt(battleMap.playerPos.split("AAA")[1])), tileSize, tileSize);
            l_playerPos.setBackground(new Color(214, 217, 223));
            l_playerPos.setForeground(new Color(0, 0, 0));
            l_playerPos.setIcon(scaledPlayerTile);
            l_playerPos.setEnabled(true);
            l_playerPos.setVisible(true);
            contentPane.add(l_playerPos);
        } else {
            contentPane.remove(l_playerPos);
            contentPane.add(l_playerPos);
        }

        if (what.contains("npc") || what.contains("all")) {
            for (int i = 0; i < l_npcs.size(); i++)
                contentPane.remove(l_npcs.get(i));
            l_npcs.clear();
            for (int i = 0; i < battleMap.npcs.size(); i++) {
                String splitted[] = battleMap.npcs.get(i).split("AAA");
                l_npcs.add(new JLabel());
                if (Integer.parseInt(splitted[1]) < battleMap.size && Integer.parseInt(splitted[2]) < battleMap.size) {
                    l_npcs.get(l_npcs.size() - 1).setBounds(x_offset + (tileSize * Integer.parseInt(splitted[1])), y_offset + (tileSize * Integer.parseInt(splitted[2])), tileSize, tileSize);
                    l_npcs.get(l_npcs.size() - 1).setIcon(getScaledImage(new ImageIcon(Manager.getImage(splitted[3])), tileSize, tileSize));
                    contentPane.add(l_npcs.get(l_npcs.size() - 1));
                } else {
                    battleMap.npcs.remove(i);
                    l_npcs.remove(i);
                    i--;
                }
            }
        } else {
            for (int i = 0; i < l_npcs.size(); i++) {
                contentPane.remove(l_npcs.get(l_npcs.size() - 1));
                contentPane.add(l_npcs.get(l_npcs.size() - 1));
            }
        }

        if (what.contains("item") || what.contains("all")) {
            for (int i = 0; i < l_items.size(); i++)
                contentPane.remove(l_items.get(i));
            l_items.clear();
            for (int i = 0; i < battleMap.items.size(); i++) {
                String splitted[] = battleMap.items.get(i).split("AAA");
                l_items.add(new JLabel());
                if (Integer.parseInt(splitted[1]) < battleMap.size && Integer.parseInt(splitted[2]) < battleMap.size) {
                    l_items.get(l_items.size() - 1).setBounds(x_offset + (tileSize * Integer.parseInt(splitted[1])), y_offset + (tileSize * Integer.parseInt(splitted[2])), tileSize, tileSize);
                    l_items.get(l_items.size() - 1).setIcon(getScaledImage(new ImageIcon(Manager.getImage(Manager.getItemImageUID(splitted[0]))), tileSize, tileSize));
                    contentPane.add(l_items.get(l_items.size() - 1));
                } else {
                    battleMap.items.remove(i);
                    l_items.remove(i);
                    i--;
                }
            }
        } else {
            for (int i = 0; i < l_items.size(); i++) {
                contentPane.remove(l_items.get(l_items.size() - 1));
                contentPane.add(l_items.get(l_items.size() - 1));
            }
        }

        if (what.contains("obstacle") || what.contains("all")) {
            for (int i = 0; i < battleMap.size; i++) {
                for (int j = 0; j < battleMap.size; j++) {
                    l_obstacles[i][j].setIcon(null);
                    contentPane.remove(l_obstacles[i][j]);
                    contentPane.add(l_obstacles[i][j]);
                }
            }
            for (int i = 0; i < battleMap.obstacles.size(); i++) {
                try {
                    String splitted[] = battleMap.obstacles.get(i).split("AAA");
                    l_obstacles[Integer.parseInt(splitted[1])][Integer.parseInt(splitted[2])].setIcon(getScaledImage(new ImageIcon(Manager.getImage(splitted[0])), tileSize, tileSize));
                } catch (Exception e) {
                    battleMap.obstacles.remove(i);
                    i--;
                }
            }
        } else {
            for (int i = 0; i < battleMap.size; i++) {
                for (int j = 0; j < battleMap.size; j++) {
                    contentPane.remove(l_obstacles[i][j]);
                    contentPane.add(l_obstacles[i][j]);
                }
            }
        }

        if (what.contains("extra") || what.contains("all")) {
            for (int i = 0; i < battleMap.size; i++) {
                for (int j = 0; j < battleMap.size; j++) {
                    l_extraGoundTiles[i][j].setIcon(null);
                    contentPane.remove(l_extraGoundTiles[i][j]);
                    contentPane.add(l_extraGoundTiles[i][j]);
                }
            }
            for (int i = 0; i < battleMap.extraGroundTiles.size(); i++) {
                try {
                    String splitted[] = battleMap.extraGroundTiles.get(i).split("AAA");
                    l_extraGoundTiles[Integer.parseInt(splitted[1])][Integer.parseInt(splitted[2])].setIcon(getScaledImage(new ImageIcon(Manager.getImage(splitted[0])), tileSize, tileSize));
                } catch (Exception e) {
                    battleMap.extraGroundTiles.remove(i);
                    i--;
                }
            }
        } else {
            for (int i = 0; i < battleMap.size; i++) {
                for (int j = 0; j < battleMap.size; j++) {
                    contentPane.remove(l_extraGoundTiles[i][j]);
                    contentPane.add(l_extraGoundTiles[i][j]);
                }
            }
        }

        if (what.contains("ground") || what.contains("all")) {
            groundTile = new ImageIcon(Manager.getImage(battleMap.groundTileUID));
            scaledGroundTile = getScaledImage(groundTile, tileSize, tileSize);
            for (int i = 0; i < battleMap.size; i++) {
                for (int j = 0; j < battleMap.size; j++) {
                    l_goundTiles[i][j].setIcon(scaledGroundTile);
                    contentPane.remove(l_goundTiles[i][j]);
                    contentPane.add(l_goundTiles[i][j]);
                }
            }
        } else {
            for (int i = 0; i < battleMap.size; i++) {
                for (int j = 0; j < battleMap.size; j++) {
                    contentPane.remove(l_goundTiles[i][j]);
                    contentPane.add(l_goundTiles[i][j]);
                }
            }
        }

        avoidTileMovement();

        invalidate();
        validate();
        repaint();
    }

    private void avoidTileMovement() {
        contentPane.remove(dummy);
        contentPane.add(dummy);
        contentPane.remove(dummy);
        contentPane.add(dummy);
    }

    //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon; Thanks to trolologuy!
    private ImageIcon getScaledImage(ImageIcon srcImg, int w, int h) {
        Image image = srcImg.getImage();
        Image newimg = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
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

    private void dropFile(String path) {
        if (interpreter.getSetting("debugMode").equals("true")) {
            Log.add("Drag and drop event: execute code from file: " + path);
            interpreter.executeEvent("", FileManager.readFile(path), new String[]{});
        } else {
            Log.add("Drag and drop event failed: Debug mode disabled");
        }
    }

}