
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GuiBattleMapEditor extends JFrame {
    private BattleMap battleMap;

    private JPanel contentPane;
    private JButton b_addGroundTiles;
    private JButton b_addNPC;
    private JButton b_addObstacle;
    private JButton b_clearTile;
    private JButton b_setGroundTiles;
    private JButton b_setPlayerStartingPos;
    private JButton b_setSize;
    private JButton b_addItem;
    private JLabel l_info;
    private JLabel l_currentState;

    private JLabel l_goundTiles[][];
    private JLabel l_extraGoundTiles[][];
    private JLabel l_obstacles[][];
    private JLabel l_clickable[][];
    private JLabel l_playerStartingPos;
    private ArrayList<JLabel> l_npcs = new ArrayList<JLabel>();
    private ArrayList<JLabel> l_items = new ArrayList<JLabel>();
    private ImageIcon groundTile, scaledGroundTile, selectedTile, scaledSelectedTile, clickedTile, scaledClickedTile, startTile, scaledStartTile;

    private int tileSize, scale, currentMode = 0, buttonWidth = 160;

    public GuiBattleMapEditor(BattleMap battleMap) {
        int size_x = 1100;
        int size_y = Math.min(StaticStuff.getScreenHeight() - 200, 920);
        this.battleMap = battleMap;
        this.setTitle(battleMap.name + " - Battle Map editor");
        this.setSize(size_x, size_y);

        contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(size_x, size_y));
        contentPane.setBackground(StaticStuff.getColor("background"));
        setIconImage(new ImageIcon("res/img/iconblue.png").getImage());

        b_addNPC = new JButton();
        b_addNPC.setBounds(50, 80, buttonWidth, 35);
        b_addNPC.setBackground(StaticStuff.getColor("buttons"));
        b_addNPC.setForeground(StaticStuff.getColor("text_color"));
        b_addNPC.setEnabled(true);
        b_addNPC.setFont(StaticStuff.getBaseFont());
        b_addNPC.setText("Add NPC (0)");
        b_addNPC.setVisible(true);
        b_addNPC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addNPC();
            }
        });

        b_addGroundTiles = new JButton();
        b_addGroundTiles.setBounds(50, 184, buttonWidth, 35);
        b_addGroundTiles.setBackground(StaticStuff.getColor("buttons"));
        b_addGroundTiles.setForeground(StaticStuff.getColor("text_color"));
        b_addGroundTiles.setEnabled(true);
        b_addGroundTiles.setFont(StaticStuff.getBaseFont());
        b_addGroundTiles.setText("Other ground tiles (2)");
        b_addGroundTiles.setVisible(true);
        b_addGroundTiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addGroundTile();
            }
        });

        b_addObstacle = new JButton();
        b_addObstacle.setBounds(50, 236, buttonWidth, 35);
        b_addObstacle.setBackground(StaticStuff.getColor("buttons"));
        b_addObstacle.setForeground(StaticStuff.getColor("text_color"));
        b_addObstacle.setEnabled(true);
        b_addObstacle.setFont(StaticStuff.getBaseFont());
        b_addObstacle.setText("Add obstacle (3)");
        b_addObstacle.setVisible(true);
        b_addObstacle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addObstacle();
            }
        });

        b_clearTile = new JButton();
        b_clearTile.setBounds(50, 288, buttonWidth, 35);
        b_clearTile.setBackground(StaticStuff.getColor("buttons"));
        b_clearTile.setForeground(StaticStuff.getColor("text_color"));
        b_clearTile.setEnabled(true);
        b_clearTile.setFont(StaticStuff.getBaseFont());
        b_clearTile.setText("Clear tile (4)");
        b_clearTile.setVisible(true);
        b_clearTile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                clearTile();
            }
        });

        b_setGroundTiles = new JButton();
        b_setGroundTiles.setBounds(50, 132, buttonWidth, 35);
        b_setGroundTiles.setBackground(StaticStuff.getColor("buttons"));
        b_setGroundTiles.setForeground(StaticStuff.getColor("text_color"));
        b_setGroundTiles.setEnabled(true);
        b_setGroundTiles.setFont(StaticStuff.getBaseFont());
        b_setGroundTiles.setText("Set ground tiles (1)");
        b_setGroundTiles.setVisible(true);
        b_setGroundTiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setGroundTiles();
            }
        });

        b_setSize = new JButton();
        b_setSize.setBounds(50, 340, buttonWidth, 35);
        b_setSize.setBackground(StaticStuff.getColor("buttons"));
        b_setSize.setForeground(StaticStuff.getColor("text_color"));
        b_setSize.setEnabled(true);
        b_setSize.setFont(StaticStuff.getBaseFont());
        b_setSize.setText("Set size (5)");
        b_setSize.setVisible(true);
        b_setSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setSize();
            }
        });

        b_setPlayerStartingPos = new JButton();
        b_setPlayerStartingPos.setBounds(50, 392, buttonWidth, 35);
        b_setPlayerStartingPos.setBackground(StaticStuff.getColor("buttons"));
        b_setPlayerStartingPos.setForeground(StaticStuff.getColor("text_color"));
        b_setPlayerStartingPos.setEnabled(true);
        b_setPlayerStartingPos.setFont(StaticStuff.getBaseFont());
        b_setPlayerStartingPos.setText("Player starting pos (6)");
        b_setPlayerStartingPos.setVisible(true);
        b_setPlayerStartingPos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setPlayerStartingPos();
            }
        });

        b_addItem = new JButton();
        b_addItem.setBounds(50, 444, buttonWidth, 35);
        b_addItem.setBackground(StaticStuff.getColor("buttons"));
        b_addItem.setForeground(StaticStuff.getColor("text_color"));
        b_addItem.setEnabled(true);
        b_addItem.setFont(StaticStuff.getBaseFont());
        b_addItem.setText("Add an item (7)");
        b_addItem.setVisible(true);
        b_addItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addItem();
            }
        });

        l_info = new JLabel();
        l_info.setBounds(38, 20, 98, 29);
        l_info.setBackground(StaticStuff.getColor("buttons"));
        l_info.setForeground(StaticStuff.getColor("text_color"));
        l_info.setEnabled(true);
        l_info.setFont(StaticStuff.getBaseFont());
        l_info.setText("<html><b>BattleMap Editor");
        l_info.setVisible(true);
        l_info.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                l_info.setFont(StaticStuff.getBaseFont());
                l_info.setBounds(38, 20, 98, 29);
            }

            public void mouseEntered(MouseEvent e) {
                l_info.setFont(new Font("sansserif", 0, 200));
                l_info.setBounds(38, 20, 2000, 190);
            }

            public void mouseClicked(MouseEvent e) {
                l_info.setFont(new Font("sansserif", 0, 5));
                l_info.setBounds(38, 20, 98, 29);
            }
        });

        l_currentState = new JLabel();
        l_currentState.setBounds(38, 850, 180, 60);
        l_currentState.setBackground(StaticStuff.getColor("text_color"));
        l_currentState.setForeground(StaticStuff.getColor("text_color"));
        l_currentState.setEnabled(true);
        l_currentState.setFont(StaticStuff.getBaseFont());
        l_currentState.setText("<html><b>Use the buttons above to modify the map.");
        l_currentState.setVisible(true);

        int width = getWidth();
        int height = getHeight();
        scale = Math.min(((width - 294) / 3) / battleMap.size, ((height - 120) / 3) / battleMap.size);
        tileSize = 3 * scale;
        tileSize = 3 * scale;

        selectedTile = new ImageIcon(Images.readImageFromFile("res/img/selected.png"));
        scaledSelectedTile = getScaledImage(selectedTile, tileSize, tileSize);
        clickedTile = new ImageIcon(Images.readImageFromFile("res/img/clicked.png"));
        scaledClickedTile = getScaledImage(clickedTile, tileSize, tileSize);
        l_clickable = new JLabel[battleMap.size][battleMap.size];
        for (int i = 0; i < battleMap.size; i++) {
            for (int j = 0; j < battleMap.size; j++) {
                l_clickable[i][j] = new JLabel();
                l_clickable[i][j].setBounds(250 + (tileSize * i), 40 + (tileSize * j), tileSize, tileSize);
                l_clickable[i][j].setBackground(StaticStuff.getColor("buttons"));
                l_clickable[i][j].setForeground(StaticStuff.getColor("text_color"));
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
                        tileClicked(x, y);
                    }
                });
                contentPane.add(l_clickable[i][j]);
            }
        }

        startTile = new ImageIcon(Images.readImageFromFile("res/img/playerStartingPos.png"));
        scaledStartTile = getScaledImage(startTile, tileSize, tileSize);
        l_playerStartingPos = new JLabel();
        l_playerStartingPos.setBounds(250 + (tileSize * Integer.parseInt(battleMap.playerStartingPos.split("AAA")[0])), 40 + (tileSize * Integer.parseInt(battleMap.playerStartingPos.split("AAA")[1])), tileSize, tileSize);
        l_playerStartingPos.setBackground(StaticStuff.getColor("buttons"));
        l_playerStartingPos.setForeground(StaticStuff.getColor("text_color"));
        l_playerStartingPos.setIcon(scaledStartTile);
        l_playerStartingPos.setEnabled(true);
        l_playerStartingPos.setVisible(true);
        contentPane.add(l_playerStartingPos);

        for (int i = 0; i < battleMap.npcs.size(); i++) {
            String splitted[] = battleMap.npcs.get(i).split("AAA");
            l_npcs.add(new JLabel());
            if (Integer.parseInt(splitted[1]) < battleMap.size && Integer.parseInt(splitted[2]) < battleMap.size) {
                l_npcs.get(l_npcs.size() - 1).setBounds(250 + (tileSize * Integer.parseInt(splitted[1])), 40 + (tileSize * Integer.parseInt(splitted[2])), tileSize, tileSize);
                l_npcs.get(l_npcs.size() - 1).setBackground(StaticStuff.getColor("buttons"));
                l_npcs.get(l_npcs.size() - 1).setForeground(StaticStuff.getColor("text_color"));
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
            String splitted[] = battleMap.items.get(i).split("AAA");
            l_items.add(new JLabel());
            if (Integer.parseInt(splitted[1]) < battleMap.size && Integer.parseInt(splitted[2]) < battleMap.size) {
                l_items.get(l_items.size() - 1).setBounds(250 + (tileSize * Integer.parseInt(splitted[1])), 40 + (tileSize * Integer.parseInt(splitted[2])), tileSize, tileSize);
                l_items.get(l_items.size() - 1).setBackground(StaticStuff.getColor("buttons"));
                l_items.get(l_items.size() - 1).setForeground(StaticStuff.getColor("text_color"));
                l_items.get(l_items.size() - 1).setIcon(getScaledImage(new ImageIcon(Manager.getImage(Manager.getItemImageUID(splitted[0]))), tileSize, tileSize));
                l_items.get(l_items.size() - 1).setEnabled(true);
                l_items.get(l_items.size() - 1).setVisible(true);
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
                l_obstacles[i][j].setBounds(250 + (tileSize * i), 40 + (tileSize * j), tileSize, tileSize);
                l_obstacles[i][j].setBackground(StaticStuff.getColor("buttons"));
                l_obstacles[i][j].setForeground(StaticStuff.getColor("text_color"));
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
                l_extraGoundTiles[i][j].setBounds(250 + (tileSize * i), 40 + (tileSize * j), tileSize, tileSize);
                l_extraGoundTiles[i][j].setBackground(StaticStuff.getColor("buttons"));
                l_extraGoundTiles[i][j].setForeground(StaticStuff.getColor("text_color"));
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
                l_goundTiles[i][j].setBounds(250 + (tileSize * i), 40 + (tileSize * j), tileSize, tileSize);
                l_goundTiles[i][j].setBackground(StaticStuff.getColor("buttons"));
                l_goundTiles[i][j].setForeground(StaticStuff.getColor("text_color"));
                l_goundTiles[i][j].setEnabled(true);
                l_goundTiles[i][j].setVisible(true);
                l_goundTiles[i][j].setIcon(scaledGroundTile);
                contentPane.add(l_goundTiles[i][j]);
            }
        }

        contentPane.add(b_addGroundTiles);
        contentPane.add(b_addNPC);
        contentPane.add(b_addObstacle);
        contentPane.add(b_clearTile);
        contentPane.add(b_setGroundTiles);
        contentPane.add(b_setPlayerStartingPos);
        contentPane.add(b_setSize);
        contentPane.add(b_addItem);
        contentPane.add(l_info);
        contentPane.add(l_currentState);

        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);

        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

        Action addNPCAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                addNPC();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_0, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "addNPCAction");
        getRootPane().getActionMap().put("addNPCAction", addNPCAction);

        Action setGroundTilesAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setGroundTiles();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "setGroundTilesAction");
        getRootPane().getActionMap().put("setGroundTilesAction", setGroundTilesAction);

        Action otherGroundTilesAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                addGroundTile();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "otherGroundTilesAction");
        getRootPane().getActionMap().put("otherGroundTilesAction", otherGroundTilesAction);

        Action addObstacleAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                addObstacle();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "addObstacleAction");
        getRootPane().getActionMap().put("addObstacleAction", addObstacleAction);

        Action clearTileAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                clearTile();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "clearTileAction");
        getRootPane().getActionMap().put("clearTileAction", clearTileAction);

        Action setSizeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setSize();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "setSizeAction");
        getRootPane().getActionMap().put("setSizeAction", setSizeAction);

        Action setPlayerStartPosAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setPlayerStartingPos();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "setPlayerStartPosAction");
        getRootPane().getActionMap().put("setPlayerStartPosAction", setPlayerStartPosAction);

        Action addItemAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_7, java.awt.event.InputEvent.CTRL_DOWN_MASK, false), "addItemAction");
        getRootPane().getActionMap().put("addItemAction", addItemAction);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                new Thread() {
                    public void run() {
                        resizeWindow(e);
                    }
                }.start();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }
        });

        resizeWindow(null);
    }

    int updateIndex = 0;

    private void resizeWindow(ComponentEvent e) {
        //get index
        int currentUpdateIndex = StaticStuff.randomNumber(0, 1000000);
        updateIndex = currentUpdateIndex;

        //get width and height
        int width = 300;
        int height = 300;
        if (e == null) {
            width = getWidth();
            height = getHeight();
        } else {
            width = e.getComponent().getWidth();
            height = e.getComponent().getHeight();
        }

        //set components location & size, get scale
        scale = Math.min(((width - 294) / 3) / battleMap.size, ((height - 120) / 3) / battleMap.size);
        tileSize = 3 * scale;
        l_currentState.setLocation(38, height - 110);

        //wait for a bit
        Sleep.milliseconds(200);

        //update board
        if (updateIndex == currentUpdateIndex) {
            removeBoard();
            generateBoard();
        }
    }

    private void tilePressed(JLabel label) {
        label.setIcon(scaledClickedTile);
    }

    private void tileReleased(JLabel label) {
        label.setIcon(scaledSelectedTile);
    }

    private void tileEntered(JLabel label) {
        label.setIcon(scaledSelectedTile);
    }

    private void tileExited(JLabel label) {
        label.setIcon(null);
    }

    private void tileClicked(int x, int y) {
        switch (currentMode) {
            case 1:
                if (battleMap.addGroundTile(setUid, x, y)) {
                    removeBoard();
                    generateBoard();
                } else Popup.error(StaticStuff.projectName, "Unable to add ground tile.");
                return;
            case 2:
                if (battleMap.addObstacle(setUid, x, y)) {
                    removeBoard();
                    generateBoard();
                } else Popup.error(StaticStuff.projectName, "Unable to add obstacle.");
                return;
            case 3:
                battleMap.removeEverything(x, y);
                removeBoard();
                generateBoard();
                return;
            case 4:
                if (battleMap.addNPC(x, y)) {
                    removeBoard();
                    generateBoard();
                } else Popup.error(StaticStuff.projectName, "Unable to add NPC.");
                return;
            case 5:
                battleMap.playerStartingPos = x + "AAA" + y;
                removeBoard();
                generateBoard();
                return;
            case 6:
                if (battleMap.addItem(x, y)) {
                    removeBoard();
                    generateBoard();
                } else Popup.error(StaticStuff.projectName, "Unable to add item.");
                return;
            default:
                //open data
        }
    }

    String setUid;

    private void addGroundTile() {
        if (currentMode != 1) {
            String uid = Popup.dropDown(StaticStuff.projectName, "Select an image UID", Manager.getStringArrayImages()).replaceAll(".+ - ([^-]+)", "$1");
            if (StaticStuff.isValidUID(uid)) {
                if (Manager.imageExists(uid)) {
                    setUid = uid;
                    currentMode = 1;
                    l_currentState.setText("<html><b>Click on a tile to set ground.");
                } else
                    Popup.error(StaticStuff.projectName, "Invalid UID: '" + setUid + "'\nThis image does not exist.");
            }
        } else {
            currentMode = 0;
            l_currentState.setText("<html><b>" + StaticStuff.generateRandomMessage());
        }
    }

    private void addNPC() {
        if (currentMode != 4) {
            currentMode = 4;
            l_currentState.setText("<html><b>Click on a tile to add a NPC to it.");
        } else {
            currentMode = 0;
            l_currentState.setText("<html><b>" + StaticStuff.generateRandomMessage());
        }
    }

    private void addObstacle() {
        if (currentMode != 2) {
            String uid = Popup.dropDown(StaticStuff.projectName, "Select an image UID", Manager.getStringArrayImages()).replaceAll(".+ - ([^-]+)", "$1");
            if (StaticStuff.isValidUID(uid)) {
                if (Manager.imageExists(uid)) {
                    setUid = uid;
                    currentMode = 2;
                    l_currentState.setText("<html><b>Click on a tile to add obstacle.");
                } else
                    Popup.error(StaticStuff.projectName, "Invalid UID: '" + setUid + "'\nThis image does not exist.");
            }
        } else {
            currentMode = 0;
            l_currentState.setText("<html><b>" + StaticStuff.generateRandomMessage());
        }
    }

    private void clearTile() {
        if (currentMode != 3) {
            currentMode = 3;
            l_currentState.setText("<html><b>Click on a tile to clear everything from it.");
        } else {
            currentMode = 0;
            l_currentState.setText("<html><b>" + StaticStuff.generateRandomMessage());
        }
    }

    private void setPlayerStartingPos() {
        if (currentMode != 5) {
            currentMode = 5;
            l_currentState.setText("<html><b>Click on a tile to set the starting position.");
        } else {
            currentMode = 0;
            l_currentState.setText("<html><b>" + StaticStuff.generateRandomMessage());
        }
    }

    private void setSize() {
        try {
            int newSize = Integer.parseInt(Popup.input("Set new size:", "" + battleMap.size));
            removeBoard();
            battleMap.size = newSize;
            scale = 282 / battleMap.size;
            tileSize = 3 * scale;
            generateBoard();
        } catch (Exception e) {
        }
    }

    private void setGroundTiles() {
        String uid = Popup.dropDown(StaticStuff.projectName, "Select an image UID", Manager.getStringArrayImages()).replaceAll(".+ - ([^-]+)", "$1");
        if (StaticStuff.isValidUID(uid)) {
            if (Manager.imageExists(uid)) {
                removeBoard();
                battleMap.groundTileUID = uid;
                groundTile = new ImageIcon(Manager.getImage(battleMap.groundTileUID));
                generateBoard();
            } else Popup.error(StaticStuff.projectName, "Invalid UID: '" + uid + "'\nThis image does not exist.");
        }
    }

    private void addItem() {
        if (currentMode != 6) {
            currentMode = 6;
            l_currentState.setText("<html><b>Click on a tile to add an item to it.");
        } else {
            currentMode = 0;
            l_currentState.setText("<html><b>" + StaticStuff.generateRandomMessage());
        }
    }

    private void removeBoard() {
        for (int i = 0; i < battleMap.size; i++) {
            for (int j = 0; j < battleMap.size; j++) {
                contentPane.remove(l_goundTiles[i][j]);
                contentPane.remove(l_clickable[i][j]);
                contentPane.remove(l_extraGoundTiles[i][j]);
                contentPane.remove(l_obstacles[i][j]);
            }
        }
        for (JLabel l_npc : l_npcs) contentPane.remove(l_npc);
        l_npcs.clear();
        for (JLabel l_item : l_items) contentPane.remove(l_item);
        l_items.clear();
        contentPane.remove(l_playerStartingPos);
    }

    private void generateBoard() {
        scaledSelectedTile = getScaledImage(selectedTile, tileSize, tileSize);
        scaledClickedTile = getScaledImage(clickedTile, tileSize, tileSize);
        l_clickable = new JLabel[battleMap.size][battleMap.size];
        for (int i = 0; i < battleMap.size; i++) {
            for (int j = 0; j < battleMap.size; j++) {
                l_clickable[i][j] = new JLabel();
                l_clickable[i][j].setBounds(250 + (tileSize * i), 40 + (tileSize * j), tileSize, tileSize);
                l_clickable[i][j].setBackground(StaticStuff.getColor("buttons"));
                l_clickable[i][j].setForeground(StaticStuff.getColor("text_color"));
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
                        tileClicked(x, y);
                    }
                });
                contentPane.add(l_clickable[i][j]);
            }
        }

        if (!(Integer.parseInt(battleMap.playerStartingPos.split("AAA")[0]) < battleMap.size && Integer.parseInt(battleMap.playerStartingPos.split("AAA")[1]) < battleMap.size)) {
            battleMap.playerStartingPos = "0AAA0";
        }
        scaledStartTile = getScaledImage(startTile, tileSize, tileSize);
        l_playerStartingPos = new JLabel();
        l_playerStartingPos.setBounds(250 + (tileSize * Integer.parseInt(battleMap.playerStartingPos.split("AAA")[0])), 40 + (tileSize * Integer.parseInt(battleMap.playerStartingPos.split("AAA")[1])), tileSize, tileSize);
        l_playerStartingPos.setBackground(StaticStuff.getColor("buttons"));
        l_playerStartingPos.setForeground(StaticStuff.getColor("text_color"));
        l_playerStartingPos.setIcon(scaledStartTile);
        l_playerStartingPos.setEnabled(true);
        l_playerStartingPos.setVisible(true);
        contentPane.add(l_playerStartingPos);

        for (int i = 0; i < battleMap.npcs.size(); i++) {
            String splitted[] = battleMap.npcs.get(i).split("AAA");
            l_npcs.add(new JLabel());
            if (Integer.parseInt(splitted[1]) < battleMap.size && Integer.parseInt(splitted[2]) < battleMap.size) {
                l_npcs.get(l_npcs.size() - 1).setBounds(250 + (tileSize * Integer.parseInt(splitted[1])), 40 + (tileSize * Integer.parseInt(splitted[2])), tileSize, tileSize);
                l_npcs.get(l_npcs.size() - 1).setBackground(StaticStuff.getColor("buttons"));
                l_npcs.get(l_npcs.size() - 1).setForeground(StaticStuff.getColor("text_color"));
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
            String splitted[] = battleMap.items.get(i).split("AAA");
            l_items.add(new JLabel());
            if (Integer.parseInt(splitted[1]) < battleMap.size && Integer.parseInt(splitted[2]) < battleMap.size) {
                l_items.get(l_items.size() - 1).setBounds(250 + (tileSize * Integer.parseInt(splitted[1])), 40 + (tileSize * Integer.parseInt(splitted[2])), tileSize, tileSize);
                l_items.get(l_items.size() - 1).setBackground(StaticStuff.getColor("buttons"));
                l_items.get(l_items.size() - 1).setForeground(StaticStuff.getColor("text_color"));
                l_items.get(l_items.size() - 1).setIcon(getScaledImage(new ImageIcon(Manager.getImage(Manager.getItemImageUID(splitted[0]))), tileSize, tileSize));
                l_items.get(l_items.size() - 1).setEnabled(true);
                l_items.get(l_items.size() - 1).setVisible(true);
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
                l_obstacles[i][j].setBounds(250 + (tileSize * i), 40 + (tileSize * j), tileSize, tileSize);
                l_obstacles[i][j].setBackground(StaticStuff.getColor("buttons"));
                l_obstacles[i][j].setForeground(StaticStuff.getColor("text_color"));
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
                l_extraGoundTiles[i][j].setBounds(250 + (tileSize * i), 40 + (tileSize * j), tileSize, tileSize);
                l_extraGoundTiles[i][j].setBackground(StaticStuff.getColor("buttons"));
                l_extraGoundTiles[i][j].setForeground(StaticStuff.getColor("text_color"));
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

        scaledGroundTile = getScaledImage(groundTile, tileSize, tileSize);
        l_goundTiles = new JLabel[battleMap.size][battleMap.size];
        for (int i = 0; i < battleMap.size; i++) {
            for (int j = 0; j < battleMap.size; j++) {
                l_goundTiles[i][j] = new JLabel();
                l_goundTiles[i][j].setBounds(250 + (tileSize * i), 40 + (tileSize * j), tileSize, tileSize);
                l_goundTiles[i][j].setBackground(StaticStuff.getColor("buttons"));
                l_goundTiles[i][j].setForeground(StaticStuff.getColor("text_color"));
                l_goundTiles[i][j].setEnabled(true);
                l_goundTiles[i][j].setFont(StaticStuff.getBaseFont());
                l_goundTiles[i][j].setText("test");
                l_goundTiles[i][j].setVisible(true);
                l_goundTiles[i][j].setIcon(scaledGroundTile);
                contentPane.add(l_goundTiles[i][j]);
            }
        }
        invalidate();
        validate();
        repaint();
    }

    //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon; Thanks to trolologuy!
    private ImageIcon getScaledImage(ImageIcon srcImg, int w, int h) {
        Image image = srcImg.getImage();
        Image newimg = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

}