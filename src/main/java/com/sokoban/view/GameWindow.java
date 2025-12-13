package com.sokoban.view;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import com.sokoban.model.Direction;
import com.sokoban.model.Game;
import com.sokoban.util.GameSave;

/**
 * 游戏窗口类，继承自JFrame，作为推箱子游戏的主窗口
 * 包含游戏画布、菜单栏以及游戏逻辑的引用
 */
public class GameWindow extends JFrame {
    public final GameCanvas canvas;    // 游戏画布，用于绘制游戏界面
    public final GameMenuBar menuBar;  // 游戏菜单栏，包含游戏选项
    public final Game game;            // 游戏逻辑对象，处理游戏核心逻辑

    /**
     * 构造函数，初始化游戏窗口
     * @param game 游戏逻辑对象
     */
    public GameWindow(Game game) {
        this.game = game;
        menuBar = new GameMenuBar(game);  // 创建菜单栏
        canvas = new GameCanvas(game);    // 创建游戏画布
        this.init();                     // 初始化窗口
    }

    /**
     * 初始化窗口设置
     * 设置窗口标题、大小、关闭操作等
     */
    private void init() {
        this.setTitle("推箱子");           // 设置窗口标题
        // this.setResizable(false);        // 禁止调整窗口大小
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 设置关闭操作
        this.setJMenuBar(menuBar);       // 添加菜单栏
        this.setLayout(new BorderLayout()); // 使用边界布局
        this.add(canvas, BorderLayout.CENTER); // 将画布添加到窗口中央
        this.addKeyListener();           // 添加键盘监听器

        // 动态计算窗口大小
        // 给地图留出边距 (Padding)
        int mapWidth = game.getCurrentLevel().getMap().width;
        int mapHeight = game.getCurrentLevel().getMap().height;

        // 最小窗口大小 600x500，防止第一关地图太小导致窗口太小
        int winWidth = Math.max(600, mapWidth + 200);
        int winHeight = Math.max(500, mapHeight + 150);

        this.setSize(winWidth, winHeight);
        this.setLocationRelativeTo(null); // 居中显示
    }

    /**
     * 添加键盘监听器
     * 同时为窗口和画布添加相同的键盘监听器
     */
    private void addKeyListener() {
        // 同时给窗口和画布添加键盘监听
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyEvent(e);
            }
        };

        this.addKeyListener(keyAdapter);
        this.canvas.addKeyListener(keyAdapter);
    }

    private void handleKeyEvent(KeyEvent e) {
        GameWindow win = GameWindow.this;
        switch (e.getKeyCode()) {
            // 箭头键控制
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:  // W 键支持
                win.handleDirectionPressed(Direction.UP);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:  // S 键支持
                win.handleDirectionPressed(Direction.DOWN);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:  // A 键支持
                win.handleDirectionPressed(Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:  // D 键支持
                win.handleDirectionPressed(Direction.RIGHT);
                break;
            case KeyEvent.VK_R:
                // R键重玩当前关卡
                win.game.reloadLevel();
                break;
            case KeyEvent.VK_U:
                // U键撤销上一步
                win.game.previousMove();
                break;
        }
    }

    private void handleDirectionPressed(int direction) {
        game.getCurrentLevel().handleKeyPress(direction);
        this.canvas.repaint();
        
        // 自动保存游戏进度
        GameSave.saveGame(game);
    }
}
