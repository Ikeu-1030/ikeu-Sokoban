package com.sokoban.model;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sokoban.model.map.GameMap;
import com.sokoban.model.map.GameMapCell;
import com.sokoban.model.spirit.Box;
import com.sokoban.model.spirit.Boy;
import com.sokoban.util.R;

/**
 * 游戏关卡类，实现了Direction接口，用于管理游戏关卡的状态和逻辑
 */
public class GameLevel implements Direction {
    // 关卡地图文件路径常量
    private static final String LEVEL_MAP_PATH = "/asset/maps/%d.map";
    // 游戏元素类型常量
    private static final int BOY = 5, BOX = 3;
    private int level;  // 当前关卡编号
    private Box[] boxes;  // 箱子数组
    private Boy boy;  // 玩家角色
    private GameMap map;  // 游戏地图
    final private Game game;  // 游戏主体
    public LinkedList<GameMove> moveStack = new LinkedList<>();  // 移动历史记录栈
    private int stepCount = 0;  // 记录步数
    private int undoUsedCount = 0;  // 记录本关卡已回退的次数

    /**
     * 构造方法，初始化游戏关卡
     * @param game 游戏主体
     * @param level 关卡编号
     */
    public GameLevel(Game game, int level) {
        this.game = game;
        this.setLevel(level);
        moveStack.add(null);  // 初始化移动栈，添加null作为初始状态
        this.stepCount = 0; // 初始化
    }

    /**
     * 重新加载当前关卡
     */
    public void reload() {
        this.setLevel(this.level);
        this.stepCount = 0; // 重置步数
        this.undoUsedCount = 0;
    }

    // 获取和增加回退次数的方法
    public int getUndoUsedCount() {
        return undoUsedCount;
    }

    public void incrementUndoUsedCount() {
        this.undoUsedCount++;
    }

    // 获取步数的方法
    public int getStepCount() {
        return stepCount;
    }

    /**
     * 获取当前关卡编号
     * @return 关卡编号
     */
    public int getLevel() {
        return level;
    }

    /**
     * 处理键盘按键输入
     * @param keyCode 按键代码
     */
    public void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case UP:
                handleUp();
                break;
            case RIGHT:
                handleRight();
                break;
            case DOWN:
                handleDown();
                break;
            case LEFT:
                handleLeft();
                break;
        }

        // 检查是否所有箱子都到达目标位置
        int totalBox = boxes.length;
        for (Box box : boxes) {
            GameMapCell TARGETCell = map.get(box.x, box.y);
            if (TARGETCell.type == GameMapCell.TYPE_TARGET) {
                box.setWin();
                totalBox--;
            }
        }

        // 如果所有箱子都到达目标位置，通过当前关卡
        if (totalBox == 0) {
            game.passCurrentLevel();
        }
    }

    /**
     * 获取指定位置的箱子
     * @param x x坐标
     * @param y y坐标
     * @return 该位置的箱子，如果没有则返回null
     */
    private Box getBoxAt(int x, int y) {
        for (Box box : boxes) {
            if (box.x == x && box.y == y) {
                return box;
            }
        }
        return null;
    }

    /**
     * 撤销上一步操作
     */
    public void previousMove() {
        if (moveStack.isEmpty()) return;

        // 获取最后一步
        GameMove previous = moveStack.pollLast();

        // 如果是初始的 null (栈底)，说明已经没有步数了，把 null 放回去保持栈结构（或者由调用者判断）
        if (previous == null) {
            // 如果外部逻辑判断了 stack.size > 1，这里可能不会执行到
            // 但为了安全，如果 pop 出来是 null，最好重置一下或忽略
            this.stepCount = 0;
            return;
        }

        // 恢复男孩的位置和方向
        boy.reset(previous.boyX, previous.boyY, previous.boyDirection);

        // 如果移动了箱子，恢复箱子的位置
        if (previous.moveBox != null) {
            Box b = previous.moveBox;
            b.moveTo(previous.boxX, previous.boxY);
            GameMapCell TARGETCell = map.get(b.x, b.y);
            if (TARGETCell.type == GameMapCell.TYPE_TARGET) {
                b.setWin();
            } else {
                b.setNomal(); // 离开目标点恢复普通状态
            }
        }

        // 步数减一
        if (stepCount > 0) stepCount--;
    }

    /**
     * 检查箱子是否可以移动到指定位置
     * @param x x坐标
     * @param y y坐标
     * @return 是否可以移动
     */
    private boolean canBoxMoveIn(int x, int y) {
        GameMapCell cell = map.get(x, y);
        if (cell != null) {
            switch (cell.type) {
                case GameMapCell.TYPE_WAY:
                case GameMapCell.TYPE_TARGET:
                    return getBoxAt(x, y) == null;
            }
        }
        return false;
    }

    /**
     * 处理向上移动
     */
    private void handleUp() {
        int bx = boy.x, by = boy.y;
        GameMapCell cell = map.get(bx, by - 1);
        if (cell == null) return; // 越界保护

        switch (cell.type) {
            case GameMapCell.TYPE_BLOCK:
                return; // 撞墙
            case GameMapCell.TYPE_WAY:
            case GameMapCell.TYPE_TARGET:
                Box box = getBoxAt(cell.x, cell.y);
                if (box == null) {
                    moveStack.add(GameMove.of(boy));
                    boy.moveUp();
                    stepCount++;
                } else {
                    if (canBoxMoveIn(box.x, box.y - 1)) {
                        moveStack.add(GameMove.of(boy, box));
                        box.moveUp();
                        boy.moveUp();
                        stepCount++;
                    }
                }
                break;
        }
    }

    /**
     * 处理向右移动
     */
    private void handleRight() {
        int bx = boy.x, by = boy.y;
        GameMapCell cell = map.get(bx + 1, by);
        if (cell == null) return;

        switch (cell.type) {
            case GameMapCell.TYPE_BLOCK:
                return;
            case GameMapCell.TYPE_WAY:
            case GameMapCell.TYPE_TARGET:
                Box box = getBoxAt(cell.x, cell.y);
                if (box == null) {
                    // 修复点：先保存，再移动
                    moveStack.add(GameMove.of(boy));
                    boy.moveRight();
                    stepCount++;
                } else {
                    if (canBoxMoveIn(box.x + 1, box.y)) {
                        moveStack.add(GameMove.of(boy, box));
                        box.moveRight();
                        boy.moveRight();
                        stepCount++;
                    }
                }
                break;
        }
    }

    /**
     * 处理向下移动
     */
    private void handleDown() {
        int bx = boy.x, by = boy.y;
        GameMapCell cell = map.get(bx, by + 1);
        if (cell == null) return;

        switch (cell.type) {
            case GameMapCell.TYPE_BLOCK:
                return;
            case GameMapCell.TYPE_WAY:
            case GameMapCell.TYPE_TARGET:
                Box box = getBoxAt(cell.x, cell.y);
                if (box == null) {
                    // 修复点：先保存，再移动
                    moveStack.add(GameMove.of(boy));
                    boy.moveDown();
                    stepCount++;
                } else {
                    if (canBoxMoveIn(box.x, box.y + 1)) {
                        moveStack.add(GameMove.of(boy, box));
                        box.moveDown();
                        boy.moveDown();
                        stepCount++;
                    }
                }
                break;
        }
    }

    /**
     * 处理向左移动
     */
    private void handleLeft() {
        int bx = boy.x, by = boy.y;
        GameMapCell cell = map.get(bx - 1, by);
        if (cell == null) return;

        switch (cell.type) {
            case GameMapCell.TYPE_BLOCK:
                return;
            case GameMapCell.TYPE_WAY:
            case GameMapCell.TYPE_TARGET:
                Box box = getBoxAt(cell.x, cell.y);
                if (box == null) {
                    // 修复点：先保存，再移动
                    moveStack.add(GameMove.of(boy));
                    boy.moveLeft();
                    stepCount++;
                } else {
                    if (canBoxMoveIn(box.x - 1, box.y)) {
                        moveStack.add(GameMove.of(boy, box));
                        box.moveLeft();
                        boy.moveLeft();
                        stepCount++;
                    }
                }
                break;
        }
    }

    /**
     * 设置关卡
     * @param level 关卡编号
     */
    private void setLevel(int level) {
        this.level = level;
        try {
            this.loadLevelData(level);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载关卡数据
     * @param level 关卡编号
     * @throws IOException 文件读取异常
     */
    private void loadLevelData(int level) throws IOException {
        GameMap map = this.map = new GameMap();
        List<Box> boxes = new ArrayList<>();
        try (InputStream is = R.getResourceAsStream(String.format(LEVEL_MAP_PATH, level));
             BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
            String line;
            int y = 0;
            int c;
            while ((line = br.readLine()) != null) {
                for (int x = line.length(); x-- > 0;) {
                    c = line.charAt(x) - '0';
                    if (c == BOY) {
                        boy = new Boy(x, y);
                        c = GameMapCell.TYPE_WAY;
                    } else if (c == BOX) {
                        boxes.add(new Box(x, y));
                        c = GameMapCell.TYPE_WAY;
                    }
                    GameMapCell cell = new GameMapCell(x, y, c);
                    map.add(cell);
                }
                y++;
            }
        }
        this.boxes = boxes.toArray(new Box[boxes.size()]);
    }

    /**
     * 获取箱子数组
     * @return 箱子数组
     */
    public Box[] getBox() {
        return boxes;
    }

    /**
     * 设置箱子数组
     * @param boxes 箱子数组
     */
    public void setBox(Box[] boxes) {
        this.boxes = boxes;
    }

    /**
     * 获取男孩角色
     * @return 男孩角色
     */
    public Boy getBoy() {
        return boy;
    }

    /**
     * 设置男孩角色
     * @param boy 男孩角色
     */
    public void setBoy(Boy boy) {
        this.boy = boy;
    }

    /**
     * 获取游戏地图
     * @return 游戏地图
     */
    public GameMap getMap() {
        return map;
    }

    /**
     * 设置游戏地图
     * @param map 游戏地图
     */
    public void setMap(GameMap map) {
        this.map = map;
    }

    /**
     * 绘制游戏画面
     * @param g 图形上下文
     */
    public void paint(Graphics g) {
        this.map.paint(g);
        this.boy.paint(g);
        for (Box box : boxes) {
            box.paint(g);
        }
    }
}