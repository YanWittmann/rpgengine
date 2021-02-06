
import java.util.ArrayList;
import java.util.Arrays;

public class BattleMap extends Entity {
    ArrayList<String> npcs = new ArrayList<>();
    ArrayList<NPC> npcObjects = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> extraGroundTiles = new ArrayList<>();
    ArrayList<String> obstacles = new ArrayList<>();
    int size;
    String groundTileUID, playerPos;
    public Manager manager;

    public BattleMap(String[] fileInput, Manager manager) {
        this.manager = manager;
        type = "BattleMap";
        try {
            name = fileInput[0];
            description = fileInput[1];
            uid = fileInput[2];
            size = Integer.parseInt(fileInput[3]);
            groundTileUID = fileInput[4];
            playerPos = fileInput[5];
            for (int i = 6; i < fileInput.length; i++) {
                if (fileInput[i].contains("++ev++")) {
                    fileInput[i] = fileInput[i].replace("++ev++", "");
                    eventName.add(fileInput[i].split("---")[0]);
                    eventCode.add(fileInput[i].split("---")[1]);
                } else if (fileInput[i].contains("++tag++")) {
                    tags.add(fileInput[i].replace("++tag++", ""));
                } else if (fileInput[i].contains("++variable++")) {
                    fileInput[i] = fileInput[i].replace("++variable++", "");
                    localVarUids.add(fileInput[i].split("---")[0]);
                    localVarName.add(fileInput[i].split("---")[1]);
                    localVarType.add(fileInput[i].split("---")[2]);
                    localVarValue.add(fileInput[i].split("---")[3]);
                } else if (fileInput[i].contains("++extraGroundTile++")) {
                    extraGroundTiles.add(fileInput[i].replace("++extraGroundTile++", ""));
                } else if (fileInput[i].contains("++obstacles++")) {
                    obstacles.add(fileInput[i].replace("++obstacles++", ""));
                } else if (fileInput[i].contains("++npc++")) {
                    npcs.add(fileInput[i].replace("++npc++", ""));
                    npcObjects.add(manager.getNPC(fileInput[i].replace("++npc++", "").split("AAA")[0]));
                } else if (fileInput[i].contains("++item++")) {
                    items.add(fileInput[i].replace("++item++", ""));
                }
            }
        } catch (Exception e) {
            StaticStuff.error("BattleMap '" + name + "' contains invalid data:\n" + e);
        }
    }

    public String generateSaveString() {
        StringBuilder str = new StringBuilder("" + name + "\n" + description + "\n" + uid + "\n" + size + "\n" + groundTileUID + "\n" + playerPos);
        for (int i = 0; i < eventName.size(); i++)
            str.append("\n++ev++").append(eventName.get(i)).append("---").append(eventCode.get(i));
        for (String tag : tags) str.append("\n++tag++").append(tag);
        for (int i = 0; i < localVarName.size(); i++)
            str.append("\n++variable++").append(localVarUids.get(i)).append("---").append(localVarName.get(i)).append("---").append(localVarType.get(i)).append("---").append(localVarValue.get(i));
        for (String extraGroundTile : extraGroundTiles) str.append("\n++extraGroundTile++").append(extraGroundTile);
        for (String obstacle : obstacles) str.append("\n++obstacles++").append(obstacle);
        for (String npc : npcs) str.append("\n++npc++").append(npc);
        for (String item : items) str.append("\n++item++").append(item);
        return str.toString();
    }

    private GuiBattleMap gui;
    private final ArrayList<NPC> turnOrder = new ArrayList<>();
    private int currentObjectTurn = 0;
    public static boolean isPlayerTurn = false, playerCanWalk = false, playerCanAttack = false, battleIsActive = false, battleCanEnd = true, freewalk = false;

    public void startBattle(GuiBattleMap gui) {
        battleIsActive = true;
        currentObjectTurn = -1;
        this.gui = gui;
        turnOrder.clear();
        for (int i = 0; i < npcs.size(); i++) turnOrder.add(npcObjects.get(i));
        turnOrder.add(null); //player = null
        ArrayList<Integer> courage = new ArrayList<Integer>();
        for (int i = 0; i < turnOrder.size(); i++)
            if (turnOrder.get(i) == null) courage.add(Integer.parseInt(Manager.player.getValue("courage")));
            else courage.add(Integer.parseInt(npcObjects.get(i).getVariableValue("courage")));
        bubbleSort(courage, turnOrder);
        Log.addIndent();
        Log.add("Turn order:");
        for (int i = 0; i < turnOrder.size(); i++)
            if (turnOrder.get(i) == null)
                Log.add(Manager.player.getValue("name") + " (player) (courage: " + courage.get(i) + ")");
            else Log.add(turnOrder.get(i).name + " (courage: " + courage.get(i) + ")");
        Log.removeIndent();
    }

    public void stopBattle() {
        battleIsActive = false;
        gui.dispose();
        Log.add("Closing battle frame");
    }

    public void battleGuiIsReadyToStart() {
        manager.executeEventFromObject(uid, "start", new String[]{});
        beginNextTurn();
    }

    private int playerWalkDistance = 0, currentNPCIndex;

    public void beginNextTurn() {
        isPlayerTurn = false;
        playerCanWalk = false;
        playerCanAttack = false;
        entityIsAttacking = false;
        entityIsWalking = false;
        gui.clearOverlay();
        if (!battleIsActive) if (battleOver(checkBattleOver())) return;
        checkIfStillActive();
        if (battleOver(checkBattleOver())) return;
        currentObjectTurn = (currentObjectTurn + 1) % turnOrder.size();
        Coordinates currentObjectLocation = getLocationByTurnOrderIndex(currentObjectTurn);
        if (turnOrder.get(currentObjectTurn) == null) { //player turn
            isPlayerTurn = true;
            playerCanAttack = true;
            Log.add("Player turn!");
            playerWalkDistance = Math.max(2, (int) (Integer.parseInt(Manager.player.getValue("speed")) + Math.ceil(0.2f * Integer.parseInt(Manager.player.getValue("dexterity")))));
            if (manager.isPlayerInventoryOverloaded())
                playerWalkDistance = Math.max(2, (int) Math.ceil(((double) playerWalkDistance) / 2));
            if (Manager.player.getValue("battleMapImage") != null)
                if (Manager.player.getValue("battleMapImage").length() > 0)
                    Manager.openImage(Manager.player.getValue("battleMapImage"), Interpreter.lang("battleMapShowTurnYou", Manager.player.getValue("name")), 200, true);

            if (generateWalkableTiles(getLocationByUID(""), playerWalkDistance, true, false)) {
                playerCanWalk = true;
                entityIsWalking = true;
            }
        } else { //npc turn
            if (Integer.parseInt(turnOrder.get(currentObjectTurn).getVariableValue("health")) <= 0) {
                beginNextTurn();
                return;
            }
            gui.setOverlay(currentObjectLocation.x, currentObjectLocation.y, 2);

            String npcImage = getNPCImageViaUID(turnOrder.get(currentObjectTurn).uid);
            if (npcImage != null) if (npcImage.length() > 0)
                Manager.openImage(npcImage, Interpreter.lang("battleMapShowTurn", turnOrder.get(currentObjectTurn).name), 200, true);
            Sleep.milliseconds(1000);
            gui.setOverlay(currentObjectLocation.x, currentObjectLocation.y, 0);

            entityIsWalking = true;
            Log.add("NPC turn: " + turnOrder.get(currentObjectTurn).name);
            Log.addIndent();
            currentNPCIndex = turnOrderIndexToNPCIndex(currentObjectTurn);
            playerWalkDistance = Integer.parseInt(turnOrder.get(currentObjectTurn).getVariableValue("speed"));
            float desiredDistance = getDesiredDistance(currentObjectTurn, true, !turnOrder.get(currentObjectTurn).tags.contains("canNotChangeWeapon"));
            String equipped = turnOrder.get(currentObjectTurn).getVariableValue("equippedWeapon");
            Item equippedItem;
            boolean viewCanBeObstructed = false, obstacles[][] = generateObstacleMap();
            if (equipped.length() > 0) {
                equippedItem = (Item) Manager.getEntity(equipped);
                if(equippedItem != null) {
                    viewCanBeObstructed = equippedItem.tags.contains("viewCanBeObstructed");
                    Log.add("NPC's (" + turnOrder.get(currentObjectTurn).name + ") desired distance to player: " + desiredDistance + " using weapon: " + equippedItem.name);
                } else Log.add("NPC cannot find itemType using uid " + equipped + ". This should not be the case.");
            } else
                Log.add("NPC's (" + turnOrder.get(currentObjectTurn).name + ") desired distance to player: " + desiredDistance + " using its hand to attack");

            Coordinates npcLoc = getLocationByTurnOrderIndex(currentObjectTurn);
            generateWalkableTiles(getLocationByTurnOrderIndex(currentObjectTurn), playerWalkDistance, true, false);
            Coordinates goal = getTileToWalkToForNPC(getLocationByTurnOrderIndex(currentObjectTurn), getLocationByUID(""), playerWalkDistance, false, true, desiredDistance, viewCanBeObstructed);

            int x = goal.x, y = goal.y;

            ArrayList<Character> path;
            if (arrayListContains(walkableTiles, goal) || (x == npcLoc.x && y == npcLoc.y)) {
                path = getPathTo(x, y);
                Log.add("NPC (" + turnOrder.get(currentObjectTurn).name + ") walks to " + x + " " + y + " with path " + path.toString());
            } else {
                Log.add("NPC (" + turnOrder.get(currentObjectTurn).name + ") cannot walk to desired tile " + x + " " + y);
                path = getPathFromTo(npcLoc, goal, obstacles);
            }

            Sleep.milliseconds(300);
            gui.clearOverlay();

            long timeDiff;
            for (int i = 0; i < path.size() && i < (playerWalkDistance - 1); i++) {
                timeDiff = StaticStuff.time();
                walkInDirection(currentNPCIndex, currentObjectTurn, path.get(i));
                Coordinates current = getLocationByTurnOrderIndex(currentObjectTurn);
                manager.executeEventFromObject(uid, "walkOnTile", new String[]{"x:" + current.x, "y:" + current.y, "entity:" + turnOrder.get(currentObjectTurn).uid});
                if (hasTileItem(current.x, current.y))
                    manager.executeEventFromObject(uid, "walkOnItem", new String[]{"x:" + current.x, "y:" + current.y, "entity:" + turnOrder.get(currentObjectTurn).uid, "item:" + getItemUID(current.x, current.y)});
                timeDiff = StaticStuff.time() - timeDiff;
                if (!(timeDiff > 300))
                    Sleep.milliseconds(300 - (int) (timeDiff));
            }
            gui.updateBoard("npc");
            entityIsWalking = false;
            entityIsAttacking = true;

            Sleep.milliseconds(600);
            npcLoc = getLocationByTurnOrderIndex(currentObjectTurn);
            Coordinates playerLoc = getLocationByUID("");
            float distance = getFloatDistance(npcLoc.x, npcLoc.y, playerLoc.x, playerLoc.y);
            boolean viewIsObstructed = isViewObstructed(playerLoc, npcLoc, obstacles) || viewCanBeObstructed;
            if (distance <= desiredDistance && (!viewIsObstructed || viewCanBeObstructed)) {
                attack(npcObjects.get(currentNPCIndex).uid, "", npcObjects.get(currentNPCIndex).getVariableValue("equippedWeapon"));
            } else {
                Log.add("NPC (" + turnOrder.get(currentObjectTurn).name + ") cannot attack");
            }
            Log.removeIndent();
            entityIsAttacking = false;
            Sleep.milliseconds(600);
            if (!battleOver(checkBattleOver()))
                beginNextTurn();
        }
    }

    private String getNPCImageViaUID(String uid) {
        for (int i = 0; i < npcObjects.size(); i++)
            if (npcObjects.get(i).uid.equals(uid)) return npcs.get(i).split("AAA")[3];
        return "";
    }

    private void attack(String attackerUID, String victimUID, String itemUID) {
        Log.addIndent();
        entityIsAttacking = true;
        boolean attackerIsPlayer = attackerUID.equals("") || attackerUID.equals("player");
        boolean victimIsPlayer = victimUID.equals("") || victimUID.equals("player");
        Log.add((attackerUID.equals("") ? "Player" : attackerUID) + " attacks " + (victimUID.equals("") ? "player" : victimUID) + " using " + itemUID);
        int damageDealt;
        if (itemUID.equals("player hands"))
            damageDealt = StaticStuff.evaluateRoll(manager.player.getValue("dmgNoWeapon"), true, false);
        else if (itemUID.equals("npc hands"))
            damageDealt = StaticStuff.evaluateRoll(manager.getEntity(attackerUID).getVariableValue("dmgNoWeapon"), true, true);
        else
            damageDealt = StaticStuff.evaluateRoll(manager.getEntity(itemUID).getVariableValue("damage"), true, !attackerIsPlayer);
        damageDealt = Math.max(0, damageDealt);
        try {
            gui.attackAnimation(getLocationByUID(attackerUID), getLocationByUID(victimUID), manager.getEntity(itemUID).image);
        } catch (Exception e) {
        }
        if (victimIsPlayer) { //player is beeing attacked
            int armorValue = 0;
            String armorS = manager.player.getValue("holdingArmor");
            if (!armorS.equals("")) {
                armorS = manager.getEntity(armorS).getVariableValue("armor");
                if (!armorS.equals(""))
                    armorValue = Integer.parseInt(armorS);
            }
            if (armorValue > 0) damageDealt = Math.max(0, damageDealt - armorValue);
            Log.add("Rolled a " + damageDealt + " as damage (with armor, armor value: " + armorValue + ")");
            new GuiHoverText(Interpreter.lang("battleMapDamageEntity", damageDealt + ""));
            boolean alive = manager.player.damagePlayer(damageDealt);
            if (!alive) {
                Log.add("Player is no longer alive!");
                manager.executeEventFromObject(uid, "dies", new String[]{"entity:" + (victimUID.equals("") ? "player" : victimUID)});
            }
        } else { //npc is beeing attacked
            Entity victim = manager.getEntity(victimUID);
            int armorValue = Integer.parseInt(victim.getVariableValue("armor"));
            if (armorValue > 0) damageDealt = Math.max(0, damageDealt - armorValue);
            Log.add("Rolled a " + damageDealt + " as damage (with armor, armor value: " + armorValue + ")");
            new GuiHoverText(Interpreter.lang("battleMapDamageEntity", damageDealt + ""));
            boolean alive = ((NPC) victim).damageNPC(damageDealt);
            if (!alive) {
                Log.add("NPC " + victimUID + " is no longer alive!");
                gui.updateBoard("npc");
                manager.executeEventFromObject(uid, "dies", new String[]{"entity:" + (victimUID.equals("") ? "player" : victimUID)});
            }
        }
        manager.executeEventFromObject(uid, "damage", new String[]{"attacker:" + (attackerUID.equals("") ? "player" : attackerUID), "victim:" + (victimUID.equals("") ? "player" : victimUID), "damage:" + damageDealt, "item:" + itemUID});
        Log.removeIndent();
        entityIsAttacking = false;
        battleOver(checkBattleOver());
    }

    private boolean battleOver(int state) {
        if (state == 0) return false; //fight not over
        else if (state == 1) { //player dead; enemies win
            if (Interpreter.isBattleActive()) {
                StaticStuff.openPopup(Interpreter.lang("battleMapFightOver") + "<br>" + Interpreter.lang("battleMapPlayerDefeated"));
                manager.endBattle();
                manager.executeEventFromObject(uid, "end", new String[]{"outcome:fail"});
            }
        } else if (state == 2) { //all enemies dead; player wins
            if (Interpreter.isBattleActive()) {
                StaticStuff.openPopup(Interpreter.lang("battleMapFightOver") + "<br>" + Interpreter.lang("battleMapPlayerWins"));
                manager.endBattle();
                manager.executeEventFromObject(uid, "end", new String[]{"outcome:win"});
            }
        }
        return true;
    }

    private int checkBattleOver() {
        if (!battleCanEnd) return 0;
        if (Integer.parseInt(manager.player.getValue("health")) <= 0)
            return 1; //player dead
        for (NPC npc : npcObjects) {
            if (Integer.parseInt(npc.getVariableValue("health")) > 0)
                return 0; //fight not over
        }
        return 2; //all enemies dead
    }

    private boolean arrayListContains(ArrayList<Coordinates> list, Coordinates find) {
        for (Coordinates coordinates : list) if (coordinates.isCoordinates(find)) return true;
        return false;
    }

    private float getDesiredDistance(int objectTurn, boolean viewCanBeObstructed, boolean tryForceNewEquip) {
        NPC npc = turnOrder.get(objectTurn);
        Inventory inv = (Inventory) Manager.getEntity(npc.inventory);
        if(inv == null) {
            Log.add("NPC cannot equip item because it doesn't have an inventory");
            return 5;
        }
        int speed = Integer.parseInt(npc.getVariableValue("speed"));
        String equipped = npc.getVariableValue("equippedWeapon");
        if (equipped.equals("") || tryForceNewEquip) { //has nothing equipped, equip something if has items
            if (inv.items.size() > 0) {
                Coordinates npcLoc = getLocationByTurnOrderIndex(objectTurn), playerLoc = getLocationByUID(""); //getFloatDistance(npcLoc.x, npcLoc.y, playerLoc.x, playerLoc.y)
                Entity item = Manager.getEntity(inv.getMaxDamageWithMinRangeAndLOS(getDistanceFromTo(npcLoc, playerLoc) - speed + 1, getFloatDistance(npcLoc.x, npcLoc.y, playerLoc.x, playerLoc.y) - speed + 1, viewCanBeObstructed));
                npc.setVariableByName("equippedWeapon", item.uid); //getDistanceFromTo(npcLoc, playerLoc) - getFloatDistance(npcLoc.x, npcLoc.y, playerLoc.x, playerLoc.y))
                Log.add("NPC equipped item: " + npc.getVariableValue("equippedWeapon"));
                return Float.parseFloat(item.getVariableValue("range"));
            } else return 1.0f;
        } else
            return Float.parseFloat(Manager.getEntity(equipped).getVariableValue("range"));
    }

    ArrayList<Coordinates> walkableTiles = new ArrayList<Coordinates>();
    ArrayList<ArrayList<Character>> pathToWalkableTiles = new ArrayList<ArrayList<Character>>();

    public boolean generateWalkableTiles(Coordinates startPos, int walkRange, boolean refreshScreen, boolean canWalkOnPlayer) {
        Log.add("Generating walkable tiles from " + startPos.x + " " + startPos.y + " with speed: " + walkRange);
        boolean obstacles[][] = generateObstacleMap();
        walkableTiles.clear();
        pathToWalkableTiles.clear();
        ArrayList<Character> temp;
        if (refreshScreen) gui.clearOverlay();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!hasObstacle(i, j) && !hasNPC(i, j) && (!hasPlayer(i, j) || canWalkOnPlayer)) {
                    temp = getPathFromTo(new int[]{startPos.x, startPos.y}, new int[]{i, j}, obstacles);
                    if (temp.size() > 0 && temp.size() < walkRange) {
                        walkableTiles.add(new Coordinates(i, j));
                        pathToWalkableTiles.add(temp);
                        if (refreshScreen) gui.setOverlay(i, j, 1);
                    }
                }
            }
        }
        return walkableTiles.size() > 1;
    }

    public int getDistanceFromTo(Coordinates startPos, Coordinates endPos) {
        boolean obstacles[][] = generateObstacleMap();
        return getPathFromTo(new int[]{startPos.x, startPos.y}, new int[]{endPos.x, endPos.y}, obstacles).size();
    }

    private boolean cannotWalkToPlayer = false;

    public Coordinates getTileToWalkToForNPC(Coordinates startPos, Coordinates endPos, int walkRange, boolean refreshScreen, boolean canWalkOnPlayer, float range, boolean viewCanBeObstructed) {
        Log.add("Generating walkable tiles from " + startPos.x + " " + startPos.y + " with speed: " + walkRange + " and range from goal " + range);
        cannotWalkToPlayer = false;
        //generateWalkableTiles(startPos, size*size, refreshScreen, canWalkOnPlayer);
        if (walkableTiles.size() > 0) {
            boolean obstacles[][] = generateObstacleMap();
            int bestDistanceIndex = -1;
            float distanceToPlayer, bestDistance = 99999;
            if (getFloatDistance(startPos.x, startPos.y, endPos.x, endPos.y) == range) return startPos;
            for (int i = 0; i < walkableTiles.size(); i++) { //find best location to go to to attack player
                distanceToPlayer = getFloatDistance(walkableTiles.get(i).x, walkableTiles.get(i).y, endPos.x, endPos.y);
                if (distanceToPlayer <= range && Math.abs(distanceToPlayer - range) < bestDistance && !isViewObstructed(walkableTiles.get(i), endPos, obstacles)) {
                    Log.add(walkableTiles.get(i).x + " " + walkableTiles.get(i).y + " has dist to player: " + distanceToPlayer + " (diff: " + Math.abs(distanceToPlayer - range) + ")");
                    bestDistance = Math.abs(distanceToPlayer - range);
                    bestDistanceIndex = i;
                } else if (distanceToPlayer <= range && Math.abs(distanceToPlayer - range) < bestDistance && viewCanBeObstructed) {
                    Log.add(walkableTiles.get(i).x + " " + walkableTiles.get(i).y + " has dist to player: " + distanceToPlayer + " (diff : " + Math.abs(distanceToPlayer - range) + ")");
                    bestDistance = Math.abs(distanceToPlayer - range);
                    bestDistanceIndex = i;
                }
            }
            if (bestDistanceIndex == -1) { //cannot find tile from where can attack player, try walking to him
                cannotWalkToPlayer = true;
                return endPos;
            }
            return new Coordinates(walkableTiles.get(bestDistanceIndex).x, walkableTiles.get(bestDistanceIndex).y);
        }
        cannotWalkToPlayer = true;
        return startPos;
    }

    public static boolean isViewObstructed(Coordinates c1, Coordinates c2, boolean obstalces[][]) {
        float dX, dY, dist;
        dX = Float.parseFloat(c2.x - c1.x + "");
        dY = Float.parseFloat(c2.y - c1.y + "");
        dist = getFloatDistance(c1.x, c1.y, c2.x, c2.y) * 4;
        dX = dX / dist;
        dY = dY / dist;
        boolean obstacleFound = false, diagonal = (dX == dY);
        for (int i = 0; i < (dist + 1); i++) {
            try {
                int currentX = Math.round((c1.x + (dX * i))), currentY = Math.round((c1.y + (dY * i)));
                if (obstalces[currentX][currentY]) {
                    obstacleFound = true;
                } else if (diagonal && currentX != c2.x && currentY != c2.y && obstalces[Math.round(c1.x + (dX * (i + 1)))][currentY] && obstalces[currentX][Math.round(c1.y + (dY * (i + 1)))]) {
                    obstacleFound = true;
                }
            } catch (Exception e) {
            }
        }
        //Log.add("Obstacle found: "+obstacleFound);
        return obstacleFound;
    }

    public void rightClickOnTile(int x, int y) {
        manager.executeEventFromObject(uid, "rightClick", new String[]{"x:" + x, "y:" + y});
    }

    int clickx = -1, clicky = -1;
    boolean playerIsWalking = false, entityIsWalking = false, entityIsAttacking = false;

    public void leftClickOnTile(int x, int y) {
        if (playerCanWalk && canWalkOnTile(x, y)) {
            gui.clearOverlay();
            Log.add("Player walk to " + x + " " + y);
            ArrayList<Character> path = getPathTo(x, y);
            long timeDiff;
            playerIsWalking = true;
            for (int i = 0; i < path.size(); i++) {
                timeDiff = StaticStuff.time();
                walkInDirection("", path.get(i));
                Coordinates current = getLocationByTurnOrderIndex(currentObjectTurn);
                manager.executeEventFromObject(uid, "walkOnTile", new String[]{"x:" + current.x, "y:" + current.y, "entity:player"});
                if (hasTileItem(current.x, current.y))
                    manager.executeEventFromObject(uid, "walkOnItem", new String[]{"x:" + current.x, "y:" + current.y, "entity:player", "item:" + getItemUID(current.x, current.y)});
                timeDiff = StaticStuff.time() - timeDiff;
                if (!(timeDiff > 300))
                    Sleep.milliseconds(300 - (int) (timeDiff));
            }
            playerCanWalk = false;
            playerIsWalking = false;
            playerWalkDistance = playerWalkDistance - path.size();
            if (playerWalkDistance > 0)
                if (generateWalkableTiles(getLocationByUID(""), playerWalkDistance, true, false)) playerCanWalk = true;
                else entityIsWalking = false;
            gui.updateBoard("player");
        } else if (playerCanAttack) {
            String tileContent[] = getTileContent(x, y);
            float range = 0.0f;
            boolean viewCanBeObstructed = false;
            String usedItem = manager.player.getValue("holdingMain");
            if (usedItem.equals(""))
                usedItem = manager.player.getValue("holdingSecond");
            if (!usedItem.equals("")) {
                Entity en = manager.getEntity(usedItem);
                range = Float.parseFloat(en.getVariableValue("range"));
                viewCanBeObstructed = en.tags.contains("viewCanBeObstructed");
            }
            if (usedItem.equals("")) {
                usedItem = manager.player.getValue("holdingSecond");
                if (!usedItem.equals("")) {
                    Entity en = manager.getEntity(usedItem);
                    range = Float.parseFloat(en.getVariableValue("range"));
                    viewCanBeObstructed = en.tags.contains("viewCanBeObstructed");
                }
            }
            if (usedItem.equals("")) {
                usedItem = "player hands";
                range = 1.0f;
                viewCanBeObstructed = false;
            }
            if (usedItem.equals("")) {
                usedItem = "0";
                range = 0.0f;
                viewCanBeObstructed = false;
            }
            Coordinates playerLoc = getLocationByUID("");
            Log.add(viewCanBeObstructed + " " + isViewObstructed(getLocationByUID(""), new Coordinates(x, y), generateObstacleMap()));
            if (range >= getFloatDistance(x, y, playerLoc.x, playerLoc.y))
                if (viewCanBeObstructed || (!viewCanBeObstructed && !isViewObstructed(getLocationByUID(""), new Coordinates(x, y), generateObstacleMap())))
                    for (int i = 0; i < tileContent.length; i++) {
                        if (manager.getTypeByUID(tileContent[i]).equals("npc")) {
                            playerCanAttack = false;
                            playerCanWalk = false;
                            entityIsWalking = false;
                            gui.clearOverlay();
                            attack("", tileContent[i], usedItem);
                            break;
                        }
                    }
                else {
                    Log.add("Cannot attack npc (view is obstructed)");
                    new GuiHoverText(Interpreter.lang("battleMapCannotAttackViewObstructed"));
                }
            else {
                for (int i = 0; i < tileContent.length; i++) {
                    if (manager.getTypeByUID(tileContent[i]).equals("npc")) {
                        Log.add("Cannot attack npc (" + range + " < " + getFloatDistance(x, y, playerLoc.x, playerLoc.y) + ")");
                        new GuiHoverText(Interpreter.lang("battleMapCannotAttackTooFarAway"));
                        break;
                    }
                }
            }
        } else if (freewalk && !battleIsActive) {
            gui.clearOverlay();
            generateWalkableTiles(getLocationByUID(""), 999999, false, false);
            if (canWalkOnTile(x, y)) {
                ArrayList<Character> path = getPathTo(x, y);
                if (path.contains(' ')) {
                    Log.add("Player cannot walk freely to " + x + " " + y);
                    return;
                }
                Log.add("Player freely walks to " + x + " " + y);
                long timeDiff;
                for (int i = 0; i < path.size(); i++) {
                    timeDiff = StaticStuff.time();
                    walkInDirection("", path.get(i));
                    Coordinates current = getLocationByUID("");
                    manager.executeEventFromObject(uid, "walkOnTile", new String[]{"x:" + current.x, "y:" + current.y, "entity:player"});
                    if (hasTileItem(current.x, current.y))
                        manager.executeEventFromObject(uid, "walkOnItem", new String[]{"x:" + current.x, "y:" + current.y, "entity:player", "item:" + getItemUID(current.x, current.y)});
                    timeDiff = StaticStuff.time() - timeDiff;
                    if (!(timeDiff > 300))
                        Sleep.milliseconds(300 - (int) (timeDiff));
                }
                gui.updateBoard("player");
            } else {
                Log.add("Player cannot walk freely to " + x + " " + y);
            }
        } else ;
    }

    private ArrayList<Character> getPathTo(int x, int y) {
        for (int i = 0; i < walkableTiles.size(); i++) {
            if (walkableTiles.get(i).isCoordinates(x, y)) return pathToWalkableTiles.get(i);
        }
        return new ArrayList<Character>();
    }

    private boolean canWalkOnTile(int x, int y) {
        for (int i = 0; i < walkableTiles.size(); i++) {
            if (walkableTiles.get(i).isCoordinates(x, y)) return true;
        }
        return false;
    }

    public void walkInDirection(String uid, char direction) {
        Coordinates originalPos = getLocationByUID(uid);
        if (direction == 'u') setPositionOf(uid, originalPos.x, originalPos.y - 1);
        if (direction == 'd') setPositionOf(uid, originalPos.x, originalPos.y + 1);
        if (direction == 'l') setPositionOf(uid, originalPos.x - 1, originalPos.y);
        if (direction == 'r') setPositionOf(uid, originalPos.x + 1, originalPos.y);
    }

    public void walkInDirection(int npcIndex, int turnOrderIndex, char direction) { //npcs only!
        Coordinates originalPos = getLocationByTurnOrderIndex(turnOrderIndex);
        if (direction == 'u') setPositionOf(npcIndex, originalPos.x, originalPos.y - 1);
        if (direction == 'd') setPositionOf(npcIndex, originalPos.x, originalPos.y + 1);
        if (direction == 'l') setPositionOf(npcIndex, originalPos.x - 1, originalPos.y);
        if (direction == 'r') setPositionOf(npcIndex, originalPos.x + 1, originalPos.y);
    }

    public void setPositionOf(String uid, int x, int y) {
        if (uid.equals("") || uid.equals("player")) { //player
            playerPos = x + "AAA" + y;
            gui.updateBoard("player");
        } else { //npc
            int index = -1;
            for (NPC npc : npcObjects) if (npc.uid.equals(uid)) index = npcObjects.indexOf(npc);
            if (index == -1) return;
            String splitted[] = npcs.get(index).split("AAA");
            npcs.set(index, splitted[0] + "AAA" + x + "AAA" + y + "AAA" + splitted[3]);
            gui.updateBoard("npc");
        }
    }

    public void setPositionOf(int npcIndex, int x, int y) { //npcs only!
        String splitted[] = npcs.get(npcIndex).split("AAA");
        npcs.set(npcIndex, splitted[0] + "AAA" + x + "AAA" + y + "AAA" + splitted[3]);
        gui.updateBoard("npc");
    }

    public void setImageOf(String entityUID, String type, String imageUID) {
        if (entityUID == null) entityUID = type;
        if (entityUID.equals("null")) entityUID = type;
        if (type == null) type = entityUID;
        if (type.equals("null")) type = entityUID;
        if (type.equals("npc")) { //38b6c40e45eb40b8 AAA 3 AAA 1 AAA 511309fde13f415f
            int index = -1;
            for (NPC npc : npcObjects) if (npc.uid.equals(entityUID)) index = npcObjects.indexOf(npc);
            if (index == -1) return;
            String splitted[] = npcs.get(index).split("AAA");
            npcs.set(index, splitted[0] + "AAA" + splitted[1] + "AAA" + splitted[2] + "AAA" + imageUID);
            gui.updateBoard("npc");
        } else if (type.equals("groundtiles")) {
            groundTileUID = imageUID;
            gui.updateBoard("ground");
        }
    }

    private int turnOrderIndexToNPCIndex(int turnOrderIndex) {
        for (int i = 0; i < npcs.size(); i++)
            if (turnOrder.get(turnOrderIndex).uid.equals(npcs.get(i).split("AAA")[0])) return i;
        return 0;
    }

    private ArrayList<Character> getPathFromTo(Coordinates start, Coordinates goal, boolean[][] obstacles) {
        return getPathFromTo(new int[]{start.x, start.y}, new int[]{goal.x, goal.y}, obstacles);
    }

    private ArrayList<Character> getPathFromTo(int start[], int goal[], boolean[][] obstacles) {
        ArrayList<Character> path = new ArrayList<Character>();
        int fCost[][] = new int[size][size], maxTries = size * size;
        int currentPos[] = new int[]{goal[0], goal[1]};
        char comeFromDirection[][] = new char[size][size]; //d u l r
        boolean imZiel = false;

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                fCost[i][j] = 9999999;
                comeFromDirection[i][j] = ' ';
            }

        while (!imZiel) {
            maxTries--;
            if (maxTries == 0) return path;
            fCost[currentPos[0]][currentPos[1]] = 9999998;
            try {
                if (fCost[currentPos[0] - 1][currentPos[1]] == 9999999 && (!obstacles[currentPos[0] - 1][currentPos[1]] || (currentPos[0] - 1 == start[0] && currentPos[1] == start[1]))) {
                    fCost[currentPos[0] - 1][currentPos[1]] = getFCost(currentPos[0], currentPos[1], start[0], start[1], goal[0], goal[1]);
                    comeFromDirection[currentPos[0] - 1][currentPos[1]] = 'r';
                }
            } catch (Exception e) {
            }
            try {
                if (fCost[currentPos[0]][currentPos[1] - 1] == 9999999 && (!obstacles[currentPos[0]][currentPos[1] - 1] || (currentPos[0] == start[0] && currentPos[1] - 1 == start[1]))) {
                    fCost[currentPos[0]][currentPos[1] - 1] = getFCost(currentPos[0], currentPos[1], start[0], start[1], goal[0], goal[1]);
                    comeFromDirection[currentPos[0]][currentPos[1] - 1] = 'd';
                }
            } catch (Exception e) {
            }
            try {
                if (fCost[currentPos[0] + 1][currentPos[1]] == 9999999 && (!obstacles[currentPos[0] + 1][currentPos[1]] || (currentPos[0] + 1 == start[0] && currentPos[1] == start[1]))) {
                    fCost[currentPos[0] + 1][currentPos[1]] = getFCost(currentPos[0], currentPos[1], start[0], start[1], goal[0], goal[1]);
                    comeFromDirection[currentPos[0] + 1][currentPos[1]] = 'l';
                }
            } catch (Exception e) {
            }
            try {
                if (fCost[currentPos[0]][currentPos[1] + 1] == 9999999 && (!obstacles[currentPos[0]][currentPos[1] + 1] || (currentPos[0] == start[0] && currentPos[1] + 1 == start[1]))) {
                    fCost[currentPos[0]][currentPos[1] + 1] = getFCost(currentPos[0], currentPos[1], start[0], start[1], goal[0], goal[1]);
                    comeFromDirection[currentPos[0]][currentPos[1] + 1] = 'u';
                }
            } catch (Exception e) {
            }

            currentPos = findLowestFCost(fCost);
            if (currentPos[0] == start[0] && currentPos[1] == start[1])
                imZiel = true;
        }

        imZiel = false;
        maxTries = size * size;
        currentPos = copyArray(start);
        while (!imZiel && path.size() < maxTries) {
            path.add(comeFromDirection[currentPos[0]][currentPos[1]]);
            if (comeFromDirection[currentPos[0]][currentPos[1]] == 'r') currentPos[0]++;
            else if (comeFromDirection[currentPos[0]][currentPos[1]] == 'l') currentPos[0]--;
            else if (comeFromDirection[currentPos[0]][currentPos[1]] == 'd') currentPos[1]++;
            else if (comeFromDirection[currentPos[0]][currentPos[1]] == 'u') currentPos[1]--;
            if (currentPos[0] == goal[0] && currentPos[1] == goal[1])
                imZiel = true;
        }

        return path;
    }

    private int[] findLowestFCost(int[][] fCost) {
        int smallestValue = 999999;
        int returnValue[] = new int[2];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (fCost[i][j] < smallestValue) smallestValue = fCost[i][j];
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (fCost[i][j] == smallestValue) returnValue = new int[]{i, j};
            }
        }
        return returnValue;
    }

    private boolean[][] generateObstacleMap() {
        boolean array[][] = new boolean[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                array[i][j] = hasObstacle(i, j);
        return array;
    }

    private int[][] copyArray(int[][] oldArray) {
        int newArray[][] = new int[oldArray.length][oldArray[0].length];
        for (int i = 0; i < oldArray.length; i++)
            for (int j = 0; j < oldArray[i].length; j++)
                newArray[i][j] = oldArray[i][j];
        return newArray;
    }

    private int[] copyArray(int[] oldArray) {
        int newArray[] = new int[oldArray.length];
        for (int i = 0; i < oldArray.length; i++)
            newArray[i] = oldArray[i];
        return newArray;
    }

    private int getDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private static float getFloatDistance(int x1, int y1, int x2, int y2) {
        return Float.parseFloat("" + Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)));
    }

    private int getFCost(int x1, int y1, int x2, int y2, int x3, int y3) {
        return Integer.parseInt(String.valueOf((int) (Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)) * 10))) + Integer.parseInt(String.valueOf((int) (Math.sqrt(Math.pow((x3 - x1), 2) + Math.pow((y3 - y1), 2)) * 10)));
    }

    private void bubbleSort(ArrayList<Integer> sortList, ArrayList<NPC> modifyWithOther) {
        int n = sortList.size(), temp;
        NPC tempNPC;
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (sortList.get(j) < sortList.get(j + 1)) {
                    temp = sortList.get(j);
                    sortList.set(j, sortList.get(j + 1));
                    sortList.set(j + 1, temp);
                    tempNPC = modifyWithOther.get(j);
                    modifyWithOther.set(j, modifyWithOther.get(j + 1));
                    modifyWithOther.set(j + 1, tempNPC);
                }
    }

    private Coordinates getLocationByTurnOrderIndex(int index) {
        if (turnOrder.get(index) == null) return getLocationByUID("");
        else return getLocationByUID(turnOrder.get(index).uid);
    }

    private Coordinates getLocationByUID(String uid) {
        if (uid.equals("") || uid.equals("player"))
            return new Coordinates(Integer.parseInt(playerPos.split("AAA")[0]), Integer.parseInt(playerPos.split("AAA")[1]));
        for (int i = 0; i < npcObjects.size(); i++)
            if (npcObjects.get(i).uid.equals(uid))
                return new Coordinates(Integer.parseInt(npcs.get(i).split("AAA")[1]), Integer.parseInt(npcs.get(i).split("AAA")[2]));
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).split("AAA")[0].equals(uid))
                return new Coordinates(Integer.parseInt(items.get(i).split("AAA")[1]), Integer.parseInt(items.get(i).split("AAA")[2]));
        return new Coordinates(0, 0);
    }

    private boolean npcExists(String uid) {
        for (int i = 0; i < npcObjects.size(); i++)
            if (npcObjects.get(i).uid.equals(uid)) return true;
        return false;
    }

    private boolean hasGroundTile(int x, int y) {
        for (int i = 0; i < extraGroundTiles.size(); i++)
            if (extraGroundTiles.get(i).split("AAA")[1].equals("" + x) && extraGroundTiles.get(i).split("AAA")[2].equals("" + y))
                return true;
        return false;
    }

    private boolean hasObstacle(int x, int y) {
        for (int i = 0; i < obstacles.size(); i++)
            if (obstacles.get(i).split("AAA")[1].equals("" + x) && obstacles.get(i).split("AAA")[2].equals("" + y))
                return true;
        return false;
    }

    private boolean hasNPC(int x, int y) {
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).split("AAA")[1].equals("" + x) && npcs.get(i).split("AAA")[2].equals("" + y)) return true;
        return false;
    }

    private boolean hasItem(int x, int y) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).split("AAA")[1].equals("" + x) && items.get(i).split("AAA")[2].equals("" + y)) return true;
        return false;
    }

    private boolean hasPlayer(int x, int y) {
        if (playerPos.split("AAA")[0].equals("" + x) && playerPos.split("AAA")[1].equals("" + y)) return true;
        return false;
    }

    public boolean addGroundTile(String uid, int x, int y) {
        try {
            if (!hasGroundTile(x, y)) {
                extraGroundTiles.add(uid + "AAA" + x + "AAA" + y);
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addObstacle(String uid, int x, int y) {
        try {
            if (!hasObstacle(x, y)) {
                obstacles.add(uid + "AAA" + x + "AAA" + y);
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void removeEverything(int x, int y) {
        for (int i = 0; i < obstacles.size(); i++)
            if (obstacles.get(i).split("AAA")[1].equals("" + x) && obstacles.get(i).split("AAA")[2].equals("" + y))
                obstacles.remove(i);
        for (int i = 0; i < extraGroundTiles.size(); i++)
            if (extraGroundTiles.get(i).split("AAA")[1].equals("" + x) && extraGroundTiles.get(i).split("AAA")[2].equals("" + y))
                extraGroundTiles.remove(i);
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).split("AAA")[1].equals("" + x) && npcs.get(i).split("AAA")[2].equals("" + y))
                npcs.remove(i);
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).split("AAA")[1].equals("" + x) && items.get(i).split("AAA")[2].equals("" + y))
                items.remove(i);
    }

    public void removeFromTile(String type, int x, int y) {
        if (type.equals("obstacle")) {
            for (int i = 0; i < obstacles.size(); i++)
                if (obstacles.get(i).split("AAA")[1].equals("" + x) && obstacles.get(i).split("AAA")[2].equals("" + y)) {
                    waitUntilRemoveFromBattleMapCommand(obstacles.get(i).split("AAA")[0]);
                    Log.add("Removing " + obstacles.get(i).split("AAA")[0] + " from the battleMap tile " + x + " " + y);
                    obstacles.remove(i);
                    gui.updateBoard("obstacle");
                }
        } else if (type.equals("extragroundtile")) {
            for (int i = 0; i < extraGroundTiles.size(); i++)
                if (extraGroundTiles.get(i).split("AAA")[1].equals("" + x) && extraGroundTiles.get(i).split("AAA")[2].equals("" + y)) {
                    Log.add("Removing extraGroundTile " + extraGroundTiles.get(i).split("AAA")[0] + " from the battleMap tile " + x + " " + y);
                    extraGroundTiles.remove(i);
                    gui.updateBoard("extra");
                }
        } else if (type.equals("npc")) {
            for (int i = 0; i < npcs.size(); i++)
                if (npcs.get(i).split("AAA")[1].equals("" + x) && npcs.get(i).split("AAA")[2].equals("" + y)) {
                    waitUntilRemoveFromBattleMapCommand(npcs.get(i).split("AAA")[0]);
                    waitUntilRemoveNPCFromBattleMapCommand(npcs.get(i).split("AAA")[0]);
                    Log.add("Removing npc " + npcs.get(i).split("AAA")[0] + " from the battleMap tile " + x + " " + y);
                    npcs.remove(i);
                    turnOrder.remove(npcObjects);
                    npcObjects.remove(i);
                    gui.updateBoard("npc");
                }
        } else if (type.equals("item"))
            for (int i = 0; i < items.size(); i++)
                if (items.get(i).split("AAA")[1].equals("" + x) && items.get(i).split("AAA")[2].equals("" + y)) {
                    Log.add("Removing item " + items.get(i).split("AAA")[0] + " from the battleMap tile " + x + " " + y);
                    items.remove(i);
                    gui.updateBoard("item");
                }
    }

    private void waitUntilRemoveFromBattleMapCommand(String selector) {
        if (entityIsWalking) {
            Log.add("There is an entity that is currently walking, waiting until walking is done to remove " + selector);
            while (entityIsWalking) Sleep.milliseconds(300);
        } else if (entityIsAttacking) {
            Log.add("There is an entity that is currently attacking, waiting until attacking is done to remove " + selector);
            while (entityIsAttacking) Sleep.milliseconds(300);
        }
    }

    private void waitUntilRemoveNPCFromBattleMapCommand(String selector) {
        if (turnOrder.get(currentObjectTurn).uid.equals(selector)) {
            Log.add("It' currently NPC's turn, waiting until turn is done to remove " + selector);
            NPC current;
            while (true) {
                current = turnOrder.get(currentObjectTurn);
                if (current == null) break;
                if (!current.uid.equals(selector)) break;
                Sleep.milliseconds(300);
            }
        }
    }

    public boolean addNPC(int x, int y, String npc, String image) {
        if (!hasNPC(x, y)) {
            if (StaticStuff.isValidUID(npc)) {
                if (Manager.npcExists(npc)) {
                    if (StaticStuff.isValidUID(image)) {
                        if (Manager.npcExists(npc)) {
                            npcs.add(npc + "AAA" + x + "AAA" + y + "AAA" + image);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean addItem(int x, int y, String item) {
        if (!hasItem(x, y)) {
            if (StaticStuff.isValidUID(item)) {
                if (Manager.itemTypeExists(item)) {
                    items.add(item + "AAA" + x + "AAA" + y);
                    return true;
                }
            }
        }
        return false;
    }

    public String[] getTileContent(int x, int y) {
        String[] temp = new String[8];
        int idx = 0;
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).split("AAA")[1].equals("" + x) && npcs.get(i).split("AAA")[2].equals("" + y)) {
                temp[idx] = npcs.get(i).split("AAA")[0];
                idx++;
            }
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).split("AAA")[1].equals("" + x) && items.get(i).split("AAA")[2].equals("" + y)) {
                temp[idx] = items.get(i).split("AAA")[0];
                idx++;
            }
        for (int i = 0; i < obstacles.size(); i++)
            if (obstacles.get(i).split("AAA")[1].equals("" + x) && obstacles.get(i).split("AAA")[2].equals("" + y)) {
                temp[idx] = obstacles.get(i).split("AAA")[0];
                idx++;
            }
        for (int i = 0; i < extraGroundTiles.size(); i++)
            if (extraGroundTiles.get(i).split("AAA")[1].equals("" + x) && extraGroundTiles.get(i).split("AAA")[2].equals("" + y)) {
                temp[idx] = extraGroundTiles.get(i).split("AAA")[0];
                idx++;
            }
        String returnValue[] = new String[idx]; //replaced: String returnValue[] = new String[idx+1];
        for (int i = 0; i < returnValue.length; i++)
            returnValue[i] = temp[i];
        return returnValue;
    }

    public boolean hasTileItem(int x, int y) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).split("AAA")[1].equals("" + x) && items.get(i).split("AAA")[2].equals("" + y)) return true;
        return false;
    }

    public String getItemUID(int x, int y) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).split("AAA")[1].equals("" + x) && items.get(i).split("AAA")[2].equals("" + y))
                return items.get(i).split("AAA")[0];
        return "";
    }

    public String[] getBattleInfo(String type, String details) {
        String values[] = null;
        String splitted[][] = null;
        if (type.equals("npc")) { //38b6c40e45eb40b8 AAA 3 AAA 1 AAA 511309fde13f415f
            values = new String[npcs.size()];
            splitted = new String[npcs.size()][];
            for (int i = 0; i < npcs.size(); i++)
                splitted[i] = npcs.get(i).split("AAA");
        } else if (type.equals("item")) { //57659c141bf64b0f AAA 2 AAA 2
            values = new String[items.size()];
            splitted = new String[items.size()][];
            for (int i = 0; i < items.size(); i++)
                splitted[i] = items.get(i).split("AAA");
        } else if (type.equals("extragroundtiles")) { //c859375027884d30 AAA 3 AAA 3 (?)
            values = new String[extraGroundTiles.size()];
            splitted = new String[extraGroundTiles.size()][];
            for (int i = 0; i < extraGroundTiles.size(); i++)
                splitted[i] = extraGroundTiles.get(i).split("AAA");
        } else if (type.equals("obstacles")) { //c859375027884d30 AAA 3 AAA 3
            values = new String[obstacles.size()];
            splitted = new String[obstacles.size()][];
            for (int i = 0; i < obstacles.size(); i++)
                splitted[i] = obstacles.get(i).split("AAA");
        } else if (type.equals("player")) { //4AAA2
            values = new String[1];
            if (details.equals("x"))
                values[0] = playerPos.split("AAA")[0];
            else if (details.equals("y"))
                values[0] = playerPos.split("AAA")[1];
            else if (details.equals("image"))
                values[0] = Manager.player.getValue("battleMapImage");
            return values;
        } else if (type.equals("groundtiles")) {
            return new String[]{groundTileUID};
        }
        if (values != null)
            for (int i = 0; i < values.length; i++) {
                if (details.equals("uid") || details.length() == 0)
                    values[i] = splitted[i][0];
                else if (details.equals("x"))
                    values[i] = splitted[i][1];
                else if (details.equals("y"))
                    values[i] = splitted[i][2];
                else if (details.equals("image") && splitted[i].length >= 4)
                    values[i] = splitted[i][3];
                else if ((type.equals("obstacles") || type.equals("extragroundtiles")) && details.equals("image"))
                    values[i] = splitted[i][0];
            }
        else return new String[]{};
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null && details.equals("image")) values[i] = "entity cannot have an image";
            else if (values[i] == null) values[i] = "could not get battle info";
        }
        return values;
    }

    public void setBattleDataViaXYType(String info, Interpreter interpreter, Manager manager) {
        int x = Integer.parseInt(interpreter.prepareStringReplaceVar(info.replaceAll("(.+) x (.+) ([npcitemxragoudlbs]+) ([xyimageud]+) (.+)", "$1")));
        int y = Integer.parseInt(interpreter.prepareStringReplaceVar(info.replaceAll("(.+) x (.+) ([npcitemxragoudlbs]+) ([xyimageud]+) (.+)", "$2")));
        String type = info.replaceAll("(.+) x (.+) ([npcitemxragoudlbs]+) ([xyimageud]+) (.+)", "$3");
        String details = info.replaceAll("(.+) x (.+) ([npcitemxragoudlbs]+) ([xyimageud]+) (.+)", "$4");
        String value = info.replaceAll("(.+) x (.+) ([npcitemxragoudlbs]+) ([xyimageud]+) (.+)", "$5");

        String[] selectedTileContents = getTileContent(x, y);
        String splittedXY[] = null;
        for (String s : selectedTileContents) {
            if (type.equals("obstacle")) {
                if (details.equals("x") || details.equals("y"))
                    value = interpreter.prepareStringReplaceVar(value);
                else if (details.equals("xy")) {
                    splittedXY = value.split(" x ");
                    splittedXY[0] = interpreter.prepareStringReplaceVar(splittedXY[0]);
                    splittedXY[1] = interpreter.prepareStringReplaceVar(splittedXY[1]);
                } else value = interpreter.evaluateSelector(value)[0];
                Log.add("Setting " + details + " to the value of " + value + " for " + type);
                int index = getObstacleIndexFromLocation(x, y);
                if (details.equals("x"))
                    setObstalceLocationViaIndex(index, Integer.parseInt(interpreter.prepareStringReplaceVar(value)), y);
                else if (details.equals("y"))
                    setObstalceLocationViaIndex(index, x, Integer.parseInt(interpreter.prepareStringReplaceVar(value)));
                else if (details.equals("xy"))
                    setObstalceLocationViaIndex(index, Integer.parseInt(splittedXY[0]), Integer.parseInt(splittedXY[1]));
                else if (details.equals("image"))
                    setObstacleImageViaIndex(index, value);
            } else if (type.equals("extragroundtile")) {
                if (details.equals("x") || details.equals("y"))
                    value = interpreter.prepareStringReplaceVar(value);
                else if (details.equals("xy")) {
                    splittedXY = value.split(" x ");
                    splittedXY[0] = interpreter.prepareStringReplaceVar(splittedXY[0]);
                    splittedXY[1] = interpreter.prepareStringReplaceVar(splittedXY[1]);
                } else value = interpreter.evaluateSelector(value)[0];
                Log.add("Setting " + details + " to the value of " + value + " for " + type);
                int index = getExtraGroundTileIndexFromLocation(x, y);
                if (details.equals("x"))
                    setExtraGroundTileLocationViaIndex(index, Integer.parseInt(interpreter.prepareStringReplaceVar(value)), y);
                else if (details.equals("y"))
                    setExtraGroundTileLocationViaIndex(index, x, Integer.parseInt(interpreter.prepareStringReplaceVar(value)));
                else if (details.equals("xy"))
                    setExtraGroundTileLocationViaIndex(index, Integer.parseInt(splittedXY[0]), Integer.parseInt(splittedXY[1]));
                else if (details.equals("image"))
                    setExtraGroundTileImageViaIndex(index, value);
            } else { //need to check if UID really is said type
                String entityType = manager.getTypeByUID(s);
                if (entityType.equals("npc") && type.equals("npc")) {
                    setBattleDataViaUID(s + " " + details + " " + value, interpreter, manager);
                } else if (entityType.equals("item") && type.equals("item")) {
                    if (details.equals("x") || details.equals("y"))
                        value = interpreter.prepareStringReplaceVar(value);
                    else if (details.equals("xy")) {
                        splittedXY = value.split(" x ");
                        splittedXY[0] = interpreter.prepareStringReplaceVar(splittedXY[0]);
                        splittedXY[1] = interpreter.prepareStringReplaceVar(splittedXY[1]);
                    }
                    Log.add("Setting " + details + " to the value of " + value + " for " + type);
                    int index = getItemIndexFromLocation(x, y);
                    if (details.equals("x"))
                        setItemLocationViaIndex(index, Integer.parseInt(interpreter.prepareStringReplaceVar(value)), y);
                    else if (details.equals("y"))
                        setItemLocationViaIndex(index, Integer.parseInt(interpreter.prepareStringReplaceVar(value)), y);
                    else if (details.equals("xy"))
                        setItemLocationViaIndex(index, Integer.parseInt(splittedXY[0]), Integer.parseInt(splittedXY[1]));
                    else if (details.equals("uid"))
                        setItemUIDViaIndex(index, value);
                }
            }
        }
    }

    public void setBattleDataViaUID(String info, Interpreter interpreter, Manager manager) {
        String uid = info.replaceAll("([a-z0-9]{16}|#.+#) (x|y|xy|image|uid) (.+)", "$1");
        String details = interpreter.prepareStringReplaceVar(info.replaceAll("([a-z0-9]{16}|#.+#) (x|y|xy|image|uid) (.+)", "$2"));
        String value = info.replaceAll("([a-z0-9]{16}|#.+#) (x|y|xy|image|uid) (.+)", "$3");

        String uids[], splittedXY[] = null;
        if (uid.equals("player")) {
            uids = new String[]{"player"};
        } else if (uid.equals("groundtiles")) {
            uids = new String[]{"groundtiles"};
        } else {
            uids = interpreter.evaluateSelector(uid);
        }
        if (uids.length == 0) return;

        if (details.equals("x") || details.equals("y") || details.equals("xy")) { //check if player or npc is walking or attacking
            if (entityIsWalking) {
                Log.add("There is an entity that is currently walking, waiting until walking is done to set " + details + " to the value of " + value + " for entities " + Arrays.toString(uids));
                while (entityIsWalking) Sleep.milliseconds(300);
            } else if (entityIsAttacking) {
                Log.add("There is an entity that is currently attacking, waiting until attacking is done to set " + details + " to the value of " + value + " for entities " + Arrays.toString(uids));
                while (entityIsAttacking) Sleep.milliseconds(300);
            }
        }

        if (uid.equals("player")) { //is player
            if (details.equals("x") || details.equals("y"))
                value = interpreter.prepareStringReplaceVar(value);
            else if (details.equals("xy")) {
                splittedXY = value.split(" x ");
                splittedXY[0] = interpreter.prepareStringReplaceVar(splittedXY[0]);
                splittedXY[1] = interpreter.prepareStringReplaceVar(splittedXY[1]);
            } else value = interpreter.evaluateSelector(value)[0];
            Log.add("Setting " + details + " to the value of " + value + " for player");
            if (details.equals("x")) {
                setPositionOf("player", Integer.parseInt(value), getLocationByUID("player").y);
            } else if (details.equals("y")) {
                setPositionOf("player", getLocationByUID("player").x, Integer.parseInt(value));
            } else if (details.equals("xy")) {
                setPositionOf("player", Integer.parseInt(splittedXY[0]), Integer.parseInt(splittedXY[1]));
            } else if (details.equals("image")) {
                manager.player.setValue("battleMapImage", value);
            } else {
                Log.add("'" + details + "' is not a valid value for player");
            }
        } else { //is npc or other
            if (details.equals("x") || details.equals("y"))
                value = interpreter.prepareStringReplaceVar(value);
            else if (details.equals("xy")) {
                splittedXY = value.split(" x ");
                splittedXY[0] = interpreter.prepareStringReplaceVar(splittedXY[0]);
                splittedXY[1] = interpreter.prepareStringReplaceVar(splittedXY[1]);
            } else value = interpreter.evaluateSelector(value)[0];
            if (details.equals("x")) {
                for (String current : uids) {
                    Log.add("Setting " + details + " to the value of " + value + " for entity " + current);
                    String entityType = manager.getTypeByUID(current);
                    if (entityType.equals("npc"))
                        setPositionOf(current, Integer.parseInt(value), getLocationByUID(current).y);
                    else if (entityType.equals("item"))
                        setItemLocationViaUID(current, Integer.parseInt(value), getLocationByUID(current).y);
                    else Log.add("'" + details + "' is not a valid value for " + entityType);
                }
            } else if (details.equals("y")) {
                for (String current : uids) {
                    Log.add("Setting " + details + " to the value of " + value + " for entity " + current);
                    String entityType = manager.getTypeByUID(current);
                    if (entityType.equals("npc"))
                        setPositionOf(current, getLocationByUID(current).x, Integer.parseInt(value));
                    else if (entityType.equals("item"))
                        setItemLocationViaUID(current, getLocationByUID(current).x, Integer.parseInt(value));
                    else Log.add("'" + details + "' is not a valid value for " + entityType);
                }
            } else if (details.equals("xy")) {
                for (String current : uids) {
                    Log.add("Setting " + details + " to the value of " + value + " for entity " + current);
                    String entityType = manager.getTypeByUID(current);
                    if (entityType.equals("npc"))
                        setPositionOf(current, Integer.parseInt(splittedXY[0]), Integer.parseInt(splittedXY[1]));
                    else if (entityType.equals("item"))
                        setItemLocationViaUID(current, Integer.parseInt(splittedXY[0]), Integer.parseInt(splittedXY[1]));
                    else Log.add("'" + details + "' is not a valid value for " + entityType);
                }
            } else if (details.equals("image")) {
                for (String current : uids) {
                    Log.add("Setting " + details + " to the value of " + value + " for entity " + current);
                    String entityType = manager.getTypeByUID(current);
                    if (entityType.equals("npc") || current.equals("groundtiles"))
                        setImageOf(current, entityType, value);
                    else
                        Log.add("'" + details + "' is not a valid value for " + entityType + "; you might need to use a different set method");
                }
            } else if (details.equals("uid")) {
                for (String current : uids) {
                    Log.add("Setting " + details + " to the value of " + value + " for entity " + current);
                    String entityType = manager.getTypeByUID(current);
                    if (entityType.equals("npc"))
                        setNPCUIDViaUID(current, value);
                    else if (entityType.equals("item"))
                        setItemUIDViaUID(current, value);
                    else Log.add("'" + details + "' is not a valid value for " + entityType);
                }
            }
        }
    }

    public int getObstacleIndexFromLocation(int x, int y) {
        String sx = x + "", sy = y + "";
        for (int i = 0; i < obstacles.size(); i++) {
            String currentItem[] = obstacles.get(i).split("AAA");
            if (currentItem[1].equals(sx) && currentItem[2].equals(sy))
                return i;
        }
        return -1;
    }

    public void setExtraGroundTileLocationViaIndex(int index, int x, int y) {
        if (index == -1) return;
        String splitted[] = extraGroundTiles.get(index).split("AAA");
        extraGroundTiles.set(index, splitted[0] + "AAA" + x + "AAA" + y);
        gui.updateBoard("extra");
    }

    public void setExtraGroundTileImageViaIndex(int itemIndex, String newUID) {
        if (itemIndex == -1) return;
        String splitted[] = extraGroundTiles.get(itemIndex).split("AAA");
        extraGroundTiles.set(itemIndex, newUID + "AAA" + splitted[1] + "AAA" + splitted[2]);
        gui.updateBoard("extra");
    }

    public int getExtraGroundTileIndexFromLocation(int x, int y) {
        String sx = x + "", sy = y + "";
        for (int i = 0; i < extraGroundTiles.size(); i++) {
            String currentGroundTile[] = extraGroundTiles.get(i).split("AAA");
            if (currentGroundTile[1].equals(sx) && currentGroundTile[2].equals(sy))
                return i;
        }
        return -1;
    }

    public void setObstalceLocationViaIndex(int index, int x, int y) {
        if (index == -1) return;
        String splitted[] = obstacles.get(index).split("AAA");
        obstacles.set(index, splitted[0] + "AAA" + x + "AAA" + y);
        gui.updateBoard("obstacle");
    }

    public void setObstacleImageViaIndex(int itemIndex, String newUID) {
        if (itemIndex == -1) return;
        String splitted[] = obstacles.get(itemIndex).split("AAA");
        obstacles.set(itemIndex, newUID + "AAA" + splitted[1] + "AAA" + splitted[2]);
        gui.updateBoard("obstacle");
    }

    public int getItemIndexFromLocation(int x, int y) {
        String sx = x + "", sy = y + "";
        for (int i = 0; i < items.size(); i++) {
            String currentItem[] = items.get(i).split("AAA");
            if (currentItem[1].equals(sx) && currentItem[2].equals(sy))
                return i;
        }
        return -1;
    }

    public void setItemLocationViaIndex(int itemIndex, int x, int y) {
        if (itemIndex == -1) return;
        String splitted[] = items.get(itemIndex).split("AAA");
        items.set(itemIndex, splitted[0] + "AAA" + x + "AAA" + y);
        gui.updateBoard("item");
    }

    public void setItemLocationViaUID(String uid, int x, int y) {
        int index = -1;
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).split("AAA")[0].equals(uid)) index = i;
        if (index == -1) return;
        String splitted[] = items.get(index).split("AAA");
        items.set(index, splitted[0] + "AAA" + x + "AAA" + y);
        gui.updateBoard("item");
    }

    public void setItemUIDViaIndex(int itemIndex, String newUID) {
        if (itemIndex == -1) return;
        String splitted[] = items.get(itemIndex).split("AAA");
        items.set(itemIndex, newUID + "AAA" + splitted[1] + "AAA" + splitted[2]);
        gui.updateBoard("item");
    }

    public void setItemUIDViaUID(String uid, String newUID) {
        int index = -1;
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).split("AAA")[0].equals(uid)) index = i;
        if (index == -1) return;
        String splitted[] = items.get(index).split("AAA");
        items.set(index, newUID + "AAA" + splitted[1] + "AAA" + splitted[2]);
        gui.updateBoard("item");
    }

    public void setNPCUIDViaUID(String uid, String newUID) {
        int index = -1;
        for (int i = 0; i < npcs.size(); i++)
            if (npcs.get(i).split("AAA")[0].equals(uid)) index = i;
        if (index == -1) return;
        String splitted[] = items.get(index).split("AAA");
        npcs.set(index, newUID + "AAA" + splitted[1] + "AAA" + splitted[2] + "AAA" + splitted[3]);
        gui.updateBoard("npc");
    }

    public void setBattleActive(boolean active) {
        battleIsActive = active;
        if (!battleIsActive)
            Log.add("State of battle map " + uid + " will be set to " + battleIsActive + " at beginning of next turn.");
        else Log.add("State of battle map " + uid + " will be set to " + battleIsActive + " in a few milliseconds.");
    }

    public void setCanEnd(boolean canEnd) {
        battleCanEnd = canEnd;
        Log.add("Set battle can end to " + battleCanEnd + " which means that the battle will " + (battleCanEnd ? "end" : "not end") + " if there are no more enemies or the player health reaches 0");
    }

    public void setFreewalk(boolean freewalk) {
        this.freewalk = freewalk;
        Log.add("Set freewalk to " + freewalk + " which means that if the battlemap is inactive the player " + (freewalk ? "can walk around freely" : "cannot walk around  freely any more"));
    }

    private void checkIfStillActive() {
        if (!battleIsActive) {
            Log.add("Battle " + uid + " is now set to inactive, pausing until active again.");
            while (!battleIsActive) Sleep.milliseconds(300);
            Log.add("Battle " + uid + " is now set to active again, continuing with the next turn.");
        }
    }

    public void addBattleDataCommand(String info, Interpreter interpreter, Manager manager) {
        //battle add [X] x [Y] [npc;item;extragroundtile;obstacle] [SELECTOR(npc|item|image)] <img [SELECTOR(image)>
        if (info.matches("(.+) x (.+) ([npc]+) (.+)(?: img (.+))")) { //add object with extra image
            int x = Integer.parseInt(interpreter.prepareStringReplaceVar(info.replaceAll("(.+) x (.+) ([npc]+) (.+)(?: img (.+))", "$1")));
            int y = Integer.parseInt(interpreter.prepareStringReplaceVar(info.replaceAll("(.+) x (.+) ([npc]+) (.+)(?: img (.+))", "$2")));
            String type = info.replaceAll("(.+) x (.+) ([npc]+) (.+)(?: img (.+))", "$3");
            String selector = interpreter.evaluateSelector(info.replaceAll("(.+) x (.+) ([npc]+) (.+)(?: img (.+))", "$4"))[0];
            String image = info.replaceAll("(.+) x (.+) ([npc]+) (.+)(?: img (.+))", "$5");

            if (type.equals("npc")) {
                if (!(hasNPC(x, y) || hasObstacle(x, y) || hasPlayer(x, y))) {
                    waitUntilAddToBattleMapCommand(selector, x, y);
                    NPC addedNPC = manager.getNPC(selector);
                    if (addedNPC == null) Log.add("NPC " + selector + " does not exist");
                    else if (npcObjects.contains(addedNPC))
                        Log.add("NPC " + selector + " already exists on the battleMap");
                    else {
                        Log.add("Adding NPC " + selector + " to " + x + " " + y + " with image " + image);
                        addNPC(x, y, selector, image);
                        npcObjects.add(addedNPC);
                        turnOrder.add(addedNPC);
                        gui.updateBoard("npc");
                    }
                } else Log.add("Unable to add NPC " + selector + " to " + x + " " + y);
            }
        } else { //add object without extra image
            int x = Integer.parseInt(interpreter.prepareStringReplaceVar(info.replaceAll("(.+) x (.+) ([itemxragoundlbsc]+) (.+)", "$1")));
            int y = Integer.parseInt(interpreter.prepareStringReplaceVar(info.replaceAll("(.+) x (.+) ([itemxragoundlbsc]+) (.+)", "$2")));
            String type = info.replaceAll("(.+) x (.+) ([itemxragoundlbsc]+) (.+)", "$3");
            String selector = interpreter.evaluateSelector(info.replaceAll("(.+) x (.+) ([itemxragoundlbsc]+) (.+)", "$4"))[0];

            if (type.equals("item")) {
                if (!(hasItem(x, y) || hasObstacle(x, y))) {
                    Log.add("Adding item " + selector + " to " + x + " " + y);
                    addItem(x, y, selector);
                    gui.updateBoard("item");
                } else Log.add("Unable to add item " + selector + " to " + x + " " + y);
            } else if (type.equals("extragroundtile")) {
                if (!hasGroundTile(x, y)) {
                    Log.add("Adding extragroundtile " + selector + " to " + x + " " + y);
                    addGroundTile(selector, x, y);
                    gui.updateBoard("extra");
                } else Log.add("Unable to add extragroundtile " + selector + " to " + x + " " + y);
            } else if (type.equals("obstacle")) {
                if (!(hasObstacle(x, y) || hasNPC(x, y) || hasItem(x, y) || hasPlayer(x, y))) {
                    waitUntilAddToBattleMapCommand(selector, x, y);
                    Log.add("Adding obstacle " + selector + " to " + x + " " + y);
                    addObstacle(selector, x, y);
                    gui.updateBoard("obstacle");
                } else Log.add("Unable to add obstacle " + selector + " to " + x + " " + y);
            }
        }
    }

    private void waitUntilAddToBattleMapCommand(String selector, int x, int y) {
        if (entityIsWalking) {
            Log.add("There is an entity that is currently walking, waiting until walking is done to set " + x + " " + y + " for " + selector);
            while (entityIsWalking) Sleep.milliseconds(300);
        } else if (entityIsAttacking) {
            Log.add("There is an entity that is currently attacking, waiting until attacking is done to set " + x + " " + y + " for " + selector);
            while (entityIsAttacking) Sleep.milliseconds(300);
        }
    }

    public void removeBattleDataCommand(String info, Interpreter interpreter) { //battle remove [X] x [Y] [npc;item;extragroundtile;obstacle]   OR   battle remove [SELECTOR(npc)]
        int x, y;
        String type;
        if (info.matches("(.+) x (.+) ([npcitemxragoudlbs]+)")) {
            x = Integer.parseInt(interpreter.prepareStringReplaceVar(info.replaceAll("(.+) x (.+) ([npcitemxragoudlbs]+)", "$1")));
            y = Integer.parseInt(interpreter.prepareStringReplaceVar(info.replaceAll("(.+) x (.+) ([npcitemxragoudlbs]+)", "$2")));
            type = info.replaceAll("(.+) x (.+) ([npcitemxragoudlbs]+)", "$3");
            removeFromTile(type, x, y);
        } else {
            type = "npc";
            String uids[] = interpreter.evaluateSelector(info);
            for (int i = 0; i < uids.length; i++) {
                Coordinates c = getLocationByUID(uids[i]);
                x = c.x;
                y = c.y;
                removeFromTile(type, x, y);
            }
        }
    }

    public void refreshGui(String what) {
        Log.add("Updating battleMap interface: " + what);
        gui.updateBoard(what);
    }

    public void action(String info, Interpreter interpreter) {
        if (info.matches("(.+) (attack|walk|approach) (.+)")) {
            if (battleIsActive) waitUntilActionCommand();

            String actionObject = info.replaceAll("(.+) (attack|walk|approach) (.+)", "$1");
            String actionObjects[];
            if (actionObject.equals("player"))
                actionObjects = new String[]{"player"};
            else {
                actionObjects = interpreter.evaluateSelector(actionObject);
                for (int i = 0; i < actionObjects.length; i++) {
                    if (!npcExists(actionObjects[i])) {
                        Log.add(actionObjects[i] + " does not exist on the battle map, skipping for action");
                        actionObjects[i] = "none";
                    }
                }
            }
            String whatAction = info.replaceAll("(.+) (attack|walk|approach) (.+)", "$2");
            String rest = info.replaceAll("(.+) (attack|walk|approach) (.+)", "$3");

            if (whatAction.equals("walk")) {
                int x = Integer.parseInt(interpreter.prepareStringReplaceVar(rest.replaceAll("(.+) x (.+)", "$1")));
                int y = Integer.parseInt(interpreter.prepareStringReplaceVar(rest.replaceAll("(.+) x (.+)", "$2")));
                for (String obj : actionObjects) {
                    if (!obj.equals("none"))
                        walkAction(obj, x, y);
                }
            } else if (whatAction.equals("approach")) { //[SELECTOR(npc)] approach [SELECTOR(npc);player] distance [VALUE]
                int x = Integer.parseInt(interpreter.prepareStringReplaceVar(rest.replaceAll("(.+) x (.+) distance (.+)", "$1")));
                int y = Integer.parseInt(interpreter.prepareStringReplaceVar(rest.replaceAll("(.+) x (.+) distance (.+)", "$2")));
                int distance = Integer.parseInt(interpreter.prepareStringReplaceVar(rest.replaceAll("(.+) x (.+) distance (.+)", "$3")));
                for (String obj : actionObjects) {
                    if (!obj.equals("none")) {
                        Coordinates goal = getCoordinatesWithDistance(getLocationByUID(obj), new Coordinates(x, y), distance);
                        walkAction(obj, goal.x, goal.y);
                    }
                }
            } else if (whatAction.equals("attack")) {
                if (rest.contains(" using ")) {
                    String usedWeapon = interpreter.evaluateSelector(rest.replaceAll("(.+) using (.+)", "$2"))[0];
                    if (rest.contains("player")) {
                        for (String attacker : actionObjects)
                            if (!attacker.equals("none"))
                                attack(attacker, "", usedWeapon);
                    } else {
                        String victims[] = interpreter.evaluateSelector(rest.replaceAll("(.+) using (.+)", "$1"));
                        for (String attacker : actionObjects)
                            if (!attacker.equals("none"))
                                for (String victim : victims)
                                    attack(attacker, victim, usedWeapon);
                    }
                } else { //use equipped weapon
                    if (rest.contains("player")) {
                        for (String attacker : actionObjects) {
                            if (!attacker.equals("none")) {
                                String usedWeapon = npcObjects.get(currentNPCIndex).getVariableValue("equippedWeapon");
                                if (usedWeapon.equals("-1") || usedWeapon.equals("")) usedWeapon = "npc hands";
                                attack(attacker, "", usedWeapon);
                            }
                        }
                    } else {
                        String victims[] = interpreter.evaluateSelector(rest);
                        for (String attacker : actionObjects) {
                            if (!attacker.equals("none"))
                                for (String victim : victims) {
                                    String usedWeapon = npcObjects.get(currentNPCIndex).getVariableValue("equippedWeapon");
                                    if (usedWeapon.equals("-1") || usedWeapon.equals("")) usedWeapon = "npc hands";
                                    attack(attacker, victim, usedWeapon);
                                }
                        }
                    }
                }
            }
        } else Log.add("Malformed action command: battle action " + info);
    }

    private void walkAction(String uid, int x, int y) {
        Coordinates npcLoc = getLocationByUID(uid);
        generateWalkableTiles(npcLoc, 99999, false, false);
        Coordinates goal = new Coordinates(x, y);

        ArrayList<Character> path = null;
        if (arrayListContains(walkableTiles, goal) || (x == npcLoc.x && y == npcLoc.y)) {
            path = getPathTo(x, y);
            Log.add("NPC (" + uid + ") walks to " + x + " " + y + " with path " + path.toString());

            Sleep.milliseconds(600);
            gui.clearOverlay();

            long timeDiff;
            for (int i = 0; i < path.size(); i++) {
                timeDiff = StaticStuff.time();
                walkInDirection(uid, path.get(i));
                Coordinates current = getLocationByUID(uid);
                manager.executeEventFromObject(uid, "walkOnTile", new String[]{"x:" + current.x, "y:" + current.y, "entity:" + uid});
                if (hasTileItem(current.x, current.y))
                    manager.executeEventFromObject(uid, "walkOnItem", new String[]{"x:" + current.x, "y:" + current.y, "entity:" + uid, "item:" + getItemUID(current.x, current.y)});
                timeDiff = StaticStuff.time() - timeDiff;
                if (!(timeDiff > 300))
                    Sleep.milliseconds(300 - (int) (timeDiff));
            }
        } else {
            Log.add("NPC (" + uid + ") cannot walk to desired tile " + x + " " + y);
            Sleep.milliseconds(600);
            gui.clearOverlay();
            setPositionOf(uid, x, y);
        }
        gui.updateBoard("npc");
    }

    private Coordinates getCoordinatesWithDistance(Coordinates start, Coordinates goal, int distance) {
        ArrayList<Coordinates> coordinates = new ArrayList<>();
        ArrayList<Integer> distances = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (hasNPC(i, j) || hasObstacle(i, j) || hasPlayer(i, j)) continue;
                Coordinates current = new Coordinates(i, j);
                coordinates.add(current);
                distances.add(getDistanceFromTo(goal, current));
            }
        }
        int currentBestDifferenceToGoal = 999999, currentBestDistanceToStart = 999999, currentBestIndex = -1;
        for (int i = 0; i < coordinates.size(); i++) {
            int distToStart = getDistanceFromTo(coordinates.get(i), start);
            int distToGoal = diff(distance, distances.get(i));
            if (distToGoal < currentBestDifferenceToGoal && distToStart < currentBestDistanceToStart) {
                System.out.println("New best (1): " + coordinates.get(i).toString() + "; distToGoal = " + distToGoal + "; distToStart = " + distToStart);
                currentBestDifferenceToGoal = distToGoal;
                currentBestDistanceToStart = distToStart;
                currentBestIndex = i;
            } else if (distToGoal <= currentBestDifferenceToGoal && distToStart < currentBestDistanceToStart) {
                System.out.println("New best (2): " + coordinates.get(i).toString() + "; distToGoal = " + distToGoal + "; distToStart = " + distToStart);
                currentBestDifferenceToGoal = distToGoal;
                currentBestDistanceToStart = distToStart;
                currentBestIndex = i;
            }
        }
        if (currentBestIndex == -1)
            return new Coordinates(0, 0);
        else return coordinates.get(currentBestIndex);
    }

    private int diff(int v1, int v2) {
        return Math.abs(v1 - v2);
    }

    private void waitUntilActionCommand() {
        if (battleIsActive) {
            Log.add("The battle is currently active, waiting until battle is inactive to perform this action command.");
            while (battleIsActive) Sleep.milliseconds(300);
        }
    }
}
