package com.sokoban.model;

import javax.swing.*;

import com.sokoban.util.R;
import com.sokoban.util.GameSave;
import com.sokoban.view.GameWindow;
import com.sokoban.view.NeonDialogUI;

/**
 * 游戏主类，负责管理游戏的核心逻辑和状态
 * 包含游戏窗口、关卡控制、进度保存等功能
 */
public class Game {
    public final GameWindow win;    // 游戏窗口对象
    public GameLevel currentLevel;  // 当前游戏关卡

    /**
     * 游戏构造函数，初始化游戏环境
     * 加载资源、创建初始关卡、设置窗口
     */
    public Game() {
        R.loadAsset();              // 加载游戏资源
        this.currentLevel = new GameLevel(this, 1);  // 创建第一关
        win = new GameWindow(this); // 创建游戏窗口
        resizeWindow();              // 调整窗口大小
        // 游戏窗口初始不可见，由主菜单控制显示
        win.setVisible(false);
    }

    /**
     * 获取当前关卡
     * @return 当前关卡对象
     */
    public GameLevel getCurrentLevel() {
        return currentLevel;
    }

    /**
     * 撤销上一步操作
     * 逻辑：
     * 1. 如果没有移动过（步数不足），提示不能回退。
     * 2. 如果回退次数达到3次，禁止回退并提示。
     * 3. 正常回退，消耗一次机会，步数减一，弹窗2秒消失。
     */
    public void previousMove() {
        GameLevel level = this.getCurrentLevel();

        // 1. 检查是否有步数可退
        // moveStack 初始会放一个 null，所以 size 为 1 代表初始状态
        if (level.moveStack.isEmpty()) {
            NeonDialogUI.showToast(win, "你还没有移动，无法回退！", "提示", 2000);
            return;
        }

        // 2. 检查回退次数限制 (3次)
        if (level.getUndoUsedCount() >= 3) {
            int option = NeonDialogUI.showConfirm(
                    win,
                    "回退次数(3次)已使用完。<br>是否重新开始本关卡？",
                    "次数耗尽"
            );
            if (option == JOptionPane.YES_OPTION) {
                this.reloadLevel();
            }
            return;
        }

        // 3. 执行回退
        level.previousMove();
        level.incrementUndoUsedCount(); // 增加使用计数
        this.win.repaint();

        // 自动保存
        GameSave.saveGame(this);

        // 4. 显示 Toast 提示 (2秒自动消失)
        // 计算剩余次数
        int used = level.getUndoUsedCount();
        String msg = String.format("已回退 <span style='color:#00f3ff; font-weight:bold;'>%d</span> / 3 次", used);

        NeonDialogUI.showToast(win, msg, "时间回溯", 2000);
    }

    /**
     * 重新加载当前关卡
     * 重置当前关卡到初始状态，并自动保存进度
     */
    public void reloadLevel() {
        this.currentLevel.reload();
        this.win.repaint();

        // 自动保存游戏进度
        GameSave.saveGame(this);
    }

    /**
     * 显示游戏关于信息
     * 使用HTML格式化的对话框显示游戏介绍和操作说明
     */
    public void showAbout() {
        String cssStyle = "<style>" +
                "body { background-color: #14151f; color: #fff; font-family: sans-serif; }" +
                ".card { padding: 15px; border: 1px solid #00f3ff; border-radius: 8px; background: rgba(0,243,255,0.05); }" +
                "h2 { color: #00f3ff; text-align: center; text-shadow: 0 0 10px #00f3ff; }" +
                ".keys { color: #bc13fe; font-weight: bold; }" +
                ".footer { color: #888; font-size: 10px; text-align: right; margin-top: 10px; }" +
                "</style>";

        String content = "<html>" + cssStyle +
                "<div class='card'>" +
                "  <h2>🎮 SOKOBAN</h2>" +
                "  <p>谨以此，回忆经典的益智游戏。</p>" +
                "  <hr color='#333'>" +
                "  <p>🎯 控制: <span class='keys'>WASD</span> 或 <span class='keys'>方向键</span></p>" +
                "  <p>📦 目标: 将所有箱子推入光圈</p>" +
                "  <div class='footer'>@copyright 2025 jaychen675@gmail.com</div>" +
                "</div></html>";

        // 调用 Swing 的弹窗，传入 HTML 内容
        NeonDialogUI.showMessage(win, "推箱子-v1.1", "ABOUT");
        JOptionPane.showMessageDialog(win, new JLabel(content), "关于游戏", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * 返回上一关
     * 如果当前不是第一关，则加载上一关
     * 注意：返回上一关不自动保存进度
     */
    public void previousLevel() {
        GameLevel current = this.currentLevel;
        if (current.getLevel() > 1) {
            this.currentLevel = new GameLevel(this, current.getLevel() - 1);
            resizeWindow();

            // 返回上一关不自动保存进度，只有手动保存才会保存当前关卡
        }
    }

    /**
     * 选择关卡功能
     * 允许玩家输入关卡编号直接跳转到指定关卡
     * 包含输入验证和错误处理
     */
    public void selectLevel() {
        String levelString = NeonDialogUI.showInput(
                win,
                "请输入跳转关卡 (1-50)<br><span style='font-size:10px; color:#bc13fe'>警告：当前进度将丢失</span>",
                "JUMP TO"
        );

        if (levelString == null) return;

        try {
            int level = Integer.parseInt(levelString);
            if (level < 1 || level > 50) throw new NumberFormatException();

            this.currentLevel = new GameLevel(this, level);
            resizeWindow();
            GameSave.saveGame(this);

        } catch (Exception e) {
            NeonDialogUI.showError(win, "无效的关卡编号 (1-50)");
            selectLevel(); // 递归重试
        }
    }

    /**
     * 完成当前关卡
     * 显示过关提示，并自动进入下一关
     */
    public void passCurrentLevel() {
        this.win.repaint();

        NeonDialogUI.showMessage(
                this.win,
                "<h1 style='color:#00f3ff'>MISSION COMPLETE</h1>" +
                        "<p>关卡 " + currentLevel.getLevel() + " 完成！</p>" +
                        "<p style='color:#aaa'>正在初始化下一层级...</p>",
                "SUCCESS"
        );

        this.nextLevel();
    }

    /**
     * 进入下一关
     * 加载下一关关卡，调整窗口大小，并自动保存进度
     */
    public void nextLevel() {
        GameLevel current = this.currentLevel;
        this.currentLevel = new GameLevel(this, current.getLevel() + 1);
        resizeWindow();

        // 自动保存游戏进度
        GameSave.saveGame(this);
    }

    /**
     * 调整游戏窗口大小
     * 根据当前关卡地图尺寸设置窗口大小
     */
    public void resizeWindow() {
        win.setSize(this.currentLevel.getMap().width, this.currentLevel.getMap().height);
        this.win.repaint();
    }

    /**
     * 启动游戏
     * 设置窗口位置居中，准备开始游戏
     */
    public void start() {
        this.win.setLocationRelativeTo(null);
        // 窗口可见性由主菜单控制
        this.restart();
    }

    /**
     * 重新开始游戏
     * 询问玩家是否确认重新开始
     * 如果确认，则删除存档并返回第一关
     */
    public void restart() {
        int option = NeonDialogUI.showConfirm(
                win,
                "确定要重置时间线吗？<br><span style='color:#ff0055'>所有进度将被抹除。</span>",
                "SYSTEM RESET"
        );

        if (option == JOptionPane.YES_OPTION) {
            GameSave.deleteSave();
            this.currentLevel = new GameLevel(this, 1);
            resizeWindow();
            GameSave.saveGame(this);
        }
    }
}