package com.sokoban.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.sokoban.model.Game;
import com.sokoban.util.GameSave;

/**
 * 现代化的霓虹风格菜单栏
 * 特性：自定义绘制、悬停动画过渡、扁平化设计
 */
public class GameMenuBar extends JMenuBar implements ActionListener {

    private final Game game;

    // --- 霓虹主题色板 ---
    public static final Color NEON_BG = new Color(20, 21, 31);       // 深色背景
    public static final Color NEON_CYAN = new Color(0, 243, 255);    // 霓虹蓝
    public static final Color NEON_PINK = new Color(188, 19, 254);   // 霓虹粉
    public static final Color TEXT_COLOR = new Color(240, 240, 255); // 亮白文字
    public static final Font MENU_FONT = new Font("Microsoft YaHei UI", Font.BOLD, 14);

    public GameMenuBar(Game game) {
        this.game = game;

        // 1. 基础设置：去除默认边框，设置背景
        this.setPreferredSize(new Dimension(600, 40));
        this.setBackground(NEON_BG);
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 30)));
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0)); // 左对齐流式布局

        // 2. 初始化菜单
        add(createNeonMenu("游戏 (GAME)",
                new String[]{"回退", "重玩当前关卡", "重新开始游戏", "-", "保存游戏", "-", "退出"},
                new String[]{"previousMove", "replayLevel", "restartGame", "-", "saveGame", "-", "exit"}
        ));

        add(createNeonMenu("关卡 (LEVEL)",
                new String[]{"上一关", "选择关卡"},
                new String[]{"previousLevel", "selectLevel"}
        ));

        add(createNeonMenu("帮助 (HELP)",
                new String[]{"关于"},
                new String[]{"about"}
        ));
    }

    /**
     * 工厂方法：创建带有霓虹样式的顶级菜单
     */
    private JMenu createNeonMenu(String title, String[] itemNames, String[] commands) {
        NeonMenu menu = new NeonMenu(title);

        for (int i = 0; i < itemNames.length; i++) {
            if (itemNames[i].equals("-")) {
                menu.addSeparator();
            } else {
                NeonMenuItem item = new NeonMenuItem(itemNames[i]);
                item.setActionCommand(commands[i]);
                item.addActionListener(this);
                menu.add(item);
            }
        }
        return menu;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // 绘制底部发光线条
        g2d.setColor(new Color(0, 243, 255, 50));
        g2d.fillRect(0, getHeight() - 2, getWidth(), 2);
    }

    // --- 自定义组件类 ---

    /**
     * 自定义顶级菜单 (模拟CSS hover效果)
     */
    private class NeonMenu extends JMenu {
        private boolean isHovered = false;

        public NeonMenu(String s) {
            super(s);
            setFont(MENU_FONT);
            setForeground(TEXT_COLOR);
            setOpaque(false); // 透明背景以显示MenuBar背景
            setBorder(new EmptyBorder(5, 15, 5, 15)); // 内边距

            // 自定义弹出菜单样式
            getPopupMenu().setBorder(BorderFactory.createLineBorder(NEON_CYAN, 1));
            getPopupMenu().setBackground(NEON_BG);

            // 鼠标监听
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isHovered || isSelected()) {
                // 悬停/选中时的辉光背景
                g2d.setColor(new Color(0, 243, 255, 30));
                g2d.fill(new RoundRectangle2D.Float(2, 2, getWidth() - 4, getHeight() - 4, 10, 10));
                setForeground(NEON_CYAN); // 文字变蓝
            } else {
                setForeground(TEXT_COLOR);
            }
            super.paintComponent(g);
        }
    }

    /**
     * 自定义菜单项 (带动画过渡)
     */
    private class NeonMenuItem extends JMenuItem {
        private float hoverAlpha = 0f; // 模拟CSS opacity
        private final Timer hoverTimer;      // 动画计时器

        public NeonMenuItem(String text) {
            super(text);
            setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
            setForeground(TEXT_COLOR);
            setBackground(NEON_BG);
            setOpaque(false); // 自定义绘制
            setBorder(new EmptyBorder(8, 20, 8, 20));

            // 初始化动画计时器 (60FPS)
            hoverTimer = new Timer(16, e -> {
                if (isSelected() || getModel().isArmed()) {
                    if (hoverAlpha < 1.0f) hoverAlpha += 0.1f;
                } else {
                    if (hoverAlpha > 0f) hoverAlpha -= 0.1f;
                }
                if (hoverAlpha > 1.0f) hoverAlpha = 1.0f;
                if (hoverAlpha < 0f) hoverAlpha = 0f;
                repaint();
            });
            hoverTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. 绘制背景 (深色)
            g2d.setColor(NEON_BG);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // 2. 绘制悬停高亮 (模拟CSS transition)
            if (hoverAlpha > 0) {
                // 渐变背景
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(188, 19, 254, (int) (50 * hoverAlpha)),
                        getWidth(), 0, new Color(0, 243, 255, (int) (20 * hoverAlpha))
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 左侧指示条
                g2d.setColor(new Color(0, 243, 255, (int) (255 * hoverAlpha)));
                g2d.fillRect(0, 0, 3, getHeight());
            }

            // 3. 绘制文字
            g2d.setFont(getFont());
            g2d.setColor(hoverAlpha > 0.5 ? NEON_CYAN : TEXT_COLOR); // 文字颜色过渡
            FontMetrics fm = g2d.getFontMetrics();
            int x = 20; // 左边距
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2d.drawString(getText(), x, y);
        }
    }

    // --- 事件处理逻辑 ---
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand(); // 获取命令字符串

        switch (cmd) {
            case "previousMove":  // 上一步
                game.previousMove();
                break;

            case "replayLevel":   // 重玩
                game.reloadLevel();
                break;

            case "restartGame":   // 重开
                game.restart();
                break;

            case "saveGame":      // 保存
                GameSave.saveGame(game);
                NeonDialogUI.showMessage(game.win, "游戏进度已保存", "SYSTEM SAVED");
                break;

            case "exit":          // 退出
                System.exit(0);
                break;

            case "about":         // 关于
                game.showAbout();
                break;

            case "previousLevel": // 上一关
                game.previousLevel();
                break;

            case "selectLevel":   // 选关
                game.selectLevel();
                break;

            default:
                System.out.println("未知的菜单命令: " + cmd);
                break;
        }
    }
}