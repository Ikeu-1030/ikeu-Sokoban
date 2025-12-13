package com.sokoban.util;

import java.io.*;
import java.util.LinkedList;
import com.sokoban.model.*;
import com.sokoban.model.map.*;
import com.sokoban.model.spirit.*;

/**
 * 游戏存档类，用于保存和加载游戏状态
 */
public class GameSave {
    private static final String SAVE_FILE = "sokoban_save.dat";

    /**
     * 保存游戏状态
     * @param game 游戏对象
     */
    public static void saveGame(Game game) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {

            GameLevel level = game.getCurrentLevel();
            SaveData saveData = new SaveData();

            // 保存关卡号
            saveData.level = level.getLevel();

            // 保存男孩状态
            saveData.boyX = level.getBoy().x;
            saveData.boyY = level.getBoy().y;
            saveData.boyDirection = level.getBoy().currentDirection;

            // 保存箱子状态
            Box[] boxes = level.getBox();
            saveData.boxCount = boxes.length;
            saveData.boxes = new BoxData[boxes.length];
            for (int i = 0; i < boxes.length; i++) {
                saveData.boxes[i] = new BoxData();
                saveData.boxes[i].x = boxes[i].x;
                saveData.boxes[i].y = boxes[i].y;
                saveData.boxes[i].isOnHome = boxes[i].isOnHome();
            }

            // 保存移动历史
            LinkedList<GameMove> moveStack = level.moveStack;
            saveData.moveCount = moveStack.size();
            saveData.moves = new MoveData[moveStack.size()];
            int i = 0;
            for (GameMove move : moveStack) {
                saveData.moves[i] = new MoveData();
                if (move != null) {
                    saveData.moves[i].boyX = move.boyX;
                    saveData.moves[i].boyY = move.boyY;
                    saveData.moves[i].boyDirection = move.boyDirection;
                    if (move.moveBox != null) {
                        saveData.moves[i].hasBox = true;
                        saveData.moves[i].boxX = move.boxX;
                        saveData.moves[i].boxY = move.boxY;
                    }
                }
                i++;
            }

            oos.writeObject(saveData);
        } catch (IOException e) {
            System.err.println("保存游戏失败: " + e.getMessage());
        }
    }

    /**
     * 加载游戏状态
     * @param game 游戏对象
     * @return 是否成功加载
     */
    public static boolean loadGame(Game game) {
        File saveFile = new File(SAVE_FILE);
        if (!saveFile.exists()) {
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SAVE_FILE))) {

            SaveData saveData = (SaveData) ois.readObject();

            // 创建新关卡
            GameLevel level = new GameLevel(game, saveData.level);

            // 恢复男孩状态
            level.getBoy().reset(saveData.boyX, saveData.boyY, saveData.boyDirection);

            // 恢复箱子状态
            Box[] boxes = level.getBox();
            for (int i = 0; i < boxes.length && i < saveData.boxCount; i++) {
                boxes[i].moveTo(saveData.boxes[i].x, saveData.boxes[i].y);
                if (saveData.boxes[i].isOnHome) {
                    boxes[i].setWin();
                } else {
                    boxes[i].setNomal();
                }
            }

            // 恢复移动历史
            level.moveStack.clear();
            for (int i = 0; i < saveData.moveCount; i++) {
                MoveData moveData = saveData.moves[i];
                if (moveData.boyX == 0 && moveData.boyY == 0 && i == 0) {
                    // 第一个null元素
                    level.moveStack.add(null);
                } else {
                    Boy boy = new Boy(moveData.boyX, moveData.boyY);
                    boy.currentDirection = moveData.boyDirection;

                    Box box = null;
                    if (moveData.hasBox) {
                        box = new Box(moveData.boxX, moveData.boxY);
                    }

                    if (box != null) {
                        level.moveStack.add(GameMove.of(boy, box));
                    } else {
                        level.moveStack.add(GameMove.of(boy));
                    }
                }
            }

            // 设置当前关卡
            game.currentLevel = level;
            game.resizeWindow();

            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载游戏失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 检查是否存在存档文件
     * @return 是否存在存档
     */
    public static boolean hasSave() {
        return new File(SAVE_FILE).exists();
    }

    /**
     * 删除存档文件
     */
    public static void deleteSave() {
        new File(SAVE_FILE).delete();
    }

    /**
     * 存档数据结构
     */
    private static class SaveData implements Serializable {
        private static final long serialVersionUID = 1L;

        int level;
        int boyX, boyY, boyDirection;
        int boxCount;
        BoxData[] boxes;
        int moveCount;
        MoveData[] moves;
    }

    /**
     * 箱子数据结构
     */
    private static class BoxData implements Serializable {
        private static final long serialVersionUID = 1L;

        int x, y;
        boolean isOnHome;
    }

    /**
     * 移动数据结构
     */
    private static class MoveData implements Serializable {
        private static final long serialVersionUID = 1L;

        int boyX, boyY, boyDirection;
        boolean hasBox;
        int boxX, boxY;
    }
}
