package com.sokoban.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import com.sokoban.model.Game;
import com.sokoban.model.GameLevel;
import com.sokoban.model.map.GameMap;

/**
 * GameCanvas类，继承自JComponent，用于绘制游戏画布
 * 负责渲染游戏背景、当前关卡、关卡标题和控制提示
 */
public class GameCanvas extends JComponent {
    private final Game game;

    // 定义UI配色方案
    private static final Color BG_COLOR_START = new Color(43, 45, 66); // 深蓝灰
    private static final Color BG_COLOR_END = new Color(20, 21, 31);   // 更深的底色
    private static final Color HUD_BG_COLOR = new Color(255, 255, 255, 30); // 半透明白色
    private static final Color TEXT_COLOR = new Color(237, 242, 244);
    private static final Color ACCENT_COLOR = new Color(239, 35, 60);  // 强调色(红)

    // 字体
    private static final Font TITLE_FONT = new Font("Microsoft YaHei UI", Font.BOLD, 24);
    private static final Font INFO_FONT = new Font("Microsoft YaHei UI", Font.PLAIN, 16);
    private static final Font HINT_FONT = new Font("Microsoft YaHei UI", Font.ITALIC, 14);

    public GameCanvas(final Game game) {
        this.game = game;
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // 这是一个好习惯，调用父类清理

        Graphics2D g2d = (Graphics2D) g;

        // 1. 开启抗锯齿 (让文字和图形边缘平滑)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 2. 绘制渐变背景
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, BG_COLOR_START, 0, h, BG_COLOR_END);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);

        GameLevel currentLevel = game.getCurrentLevel();
        if (currentLevel != null) {
            GameMap map = currentLevel.getMap();

            // 3. 计算居中偏移量
            // 地图实际像素宽 = map.width, 画布宽 = w
            // 偏移量 = (画布宽 - 地图宽) / 2
            int offsetX = (w - map.width) / 2;
            int offsetY = (h - map.height) / 2;

            // 保存当前的坐标系状态
            java.awt.geom.AffineTransform oldTransform = g2d.getTransform();

            // 平移坐标系到居中位置
            g2d.translate(offsetX, offsetY);

            // 绘制地图背景阴影 (提升立体感)
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(5, 5, map.width, map.height);

            // 绘制实际地图元素
            currentLevel.paint(g);

            // 绘制地图边框
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(0, 0, map.width, map.height);

            // 恢复坐标系 (因为HUD不需要偏移)
            g2d.setTransform(oldTransform);

            // 4. 绘制 HUD (信息面板)
            drawHUD(g2d, currentLevel);
        }
    }

    /**
     * 绘制抬头显示信息 (关卡、步数)
     */
    private void drawHUD(Graphics2D g2d, GameLevel level) {
        String levelText = "Level " + level.getLevel();
        String stepText = "Steps: " + level.getStepCount();

        // 绘制左上角的关卡信息卡片
        int cardW = 140;
        int cardH = 70;
        int cardX = 20;
        int cardY = 20;

        // 卡片背景
        g2d.setColor(HUD_BG_COLOR);
        g2d.fill(new RoundRectangle2D.Float(cardX, cardY, cardW, cardH, 15, 15));

        // 装饰线条
        g2d.setColor(ACCENT_COLOR);
        g2d.fillRect(cardX, cardY + 15, 4, 40);

        // 文字
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(TITLE_FONT);
        g2d.drawString(levelText, cardX + 15, cardY + 30);

        g2d.setFont(INFO_FONT);
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawString(stepText, cardX + 15, cardY + 55);

        // 底部操作提示
        if (level.getLevel() == 1) {
            String hint = "按 R 重玩  |  按 U 撤销  |  方向键移动";
            g2d.setFont(HINT_FONT);
            g2d.setColor(new Color(255, 255, 255, 150));
            int hintW = g2d.getFontMetrics().stringWidth(hint);
            g2d.drawString(hint, (getWidth() - hintW) / 2, getHeight() - 20);
        }
    }
}