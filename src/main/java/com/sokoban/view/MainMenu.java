package com.sokoban.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.sokoban.model.Game;
import com.sokoban.model.GameLevel;
import com.sokoban.util.GameSave;
import com.sokoban.util.R;

/**
 * 游戏主菜单界面
 * 继承自JFrame，用于创建推箱子游戏的主菜单窗口
 */
public class MainMenu extends JFrame {
    private static final long serialVersionUID = 1L; // 序列化ID，用于版本控制
    private final Game game; // 游戏主对象引用

    /**
     * 构造函数
     * @param game 游戏主对象
     */
    public MainMenu(Game game) {
        this.game = game;
        initUI(); // 初始化用户界面
    }

    /**
     * 初始化用户界面
     * 设置窗口属性、添加各种组件和布局
     */
    private void initUI() {
        setTitle("推箱子游戏 - 主菜单"); // 设置窗口标题
        // 初始设置一个默认尺寸，背景图片加载后会自动调整
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置默认关闭操作
        setLocationRelativeTo(null); // 窗口居中显示
        setResizable(false); // 禁止调整窗口大小

        // 创建主面板，使用边界布局
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false); // 设置为透明，以便显示背景图片

        // 添加标题
        JLabel titleLabel = new JLabel("推箱子", SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 36)); // 设置字体样式
        titleLabel.setForeground(new Color(25, 25, 112)); // 设置字体颜色
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0)); // 设置边距
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建菜单面板（隐藏原始按钮，但保留功能）
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setOpaque(false); // 确保面板透明
        menuPanel.setVisible(false); // 隐藏菜单面板
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0); // 设置组件间距
        gbc.fill = GridBagConstraints.HORIZONTAL; // 设置填充方式

        // 开始新游戏按钮（隐藏）
        JButton newGameButton = createMenuButton("开始新游戏", new Color(248, 248, 248));
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNewGame(); // 点击按钮时开始新游戏
            }
        });

        // 继续游戏按钮（隐藏）
        JButton continueGameButton = createMenuButton("继续游戏", new Color(250, 250, 250));
        continueGameButton.setEnabled(GameSave.hasSave()); // 只有存在存档时才启用
        continueGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                continueGame(); // 点击按钮时继续游戏
            }
        });

        // 退出游戏按钮（隐藏）
        JButton exitButton = createMenuButton("退出游戏", new Color(255, 255, 255));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // 点击按钮时退出游戏
            }
        });

        // 添加按钮到菜单面板
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuPanel.add(newGameButton, gbc);

        gbc.gridy = 1;
        menuPanel.add(continueGameButton, gbc);

        gbc.gridy = 2;
        menuPanel.add(exitButton, gbc);

        mainPanel.add(menuPanel, BorderLayout.CENTER);

        // 添加底部版权信息
        JLabel copyrightLabel = new JLabel("@copyright 2010-2018 jaychen675@gmail.com", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("宋体", Font.PLAIN, 12)); // 设置字体样式
        copyrightLabel.setForeground(new Color(105, 105, 105)); // 设置字体颜色
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // 设置边距
        mainPanel.add(copyrightLabel, BorderLayout.SOUTH);

        // 添加背景图片
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        
        // 创建透明按钮面板
        JPanel buttonPanel = new JPanel(null); // 使用绝对布局
        buttonPanel.setOpaque(false); // 设置为透明
        
        // 根据背景图添加透明按钮
        // 继续游戏按钮 - 根据背景图调整坐标和大小
        JButton continueButton = new JButton();
        continueButton.setBounds(105, 405, 180, 50); // 根据背景图调整坐标和大小
        continueButton.setOpaque(false); // 设置按钮透明
        continueButton.setContentAreaFilled(false); // 不填充内容区域
        continueButton.setBorderPainted(false); // 不绘制边框
        continueButton.setFocusPainted(false); // 不绘制焦点
        continueButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 设置鼠标样式
        continueButton.addActionListener(e -> continueGame()); // 添加点击事件
        
        // 退出游戏按钮 - 根据背景图调整坐标和大小
        exitButton = new JButton();
        exitButton.setBounds(105, 500, 180, 50); // 根据背景图调整坐标和大小
        exitButton.setOpaque(false); // 设置按钮透明
        exitButton.setContentAreaFilled(false); // 不填充内容区域
        exitButton.setBorderPainted(false); // 不绘制边框
        exitButton.setFocusPainted(false); // 不绘制焦点
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 设置鼠标样式
        exitButton.addActionListener(e -> System.exit(0)); // 添加点击事件

        buttonPanel.add(continueButton); // 将继续游戏按钮添加到面板
        buttonPanel.add(exitButton); // 将退出游戏按钮添加到面板
        
        // 将主面板和按钮面板添加到背景面板
        backgroundPanel.add(mainPanel, BorderLayout.CENTER); // 主面板居中
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER); // 按钮面板居中
        
        setContentPane(backgroundPanel); // 设置背景面板为内容面板
    }

    /**
     * 创建菜单按钮
     * @param text 按钮文本
     * @param color 按钮背景色
     * @return 配置好的JButton对象
     */
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("宋体", Font.BOLD, 18));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 添加鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * 开始新游戏
     * 删除存档，创建第一关游戏关卡，并显示游戏窗口
     */
    private void startNewGame() {
        GameSave.deleteSave(); // 删除存档
        game.currentLevel = new GameLevel(game, 1); // 从第一关开始
        game.resizeWindow();

        // 显示游戏窗口
        game.win.setVisible(true);

        // 关闭主菜单
        this.dispose();
    }

    /**
     * 继续游戏
     * 尝试加载存档，如果失败则提示并开始新游戏
     */
    private void continueGame() {
        if (GameSave.loadGame(game)) {
            // 显示游戏窗口
            game.win.setVisible(true);

            // 关闭主菜单
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "<html><div style='text-align: center; padding: 10px;'>"
                + "<h3 style='color: #d32f2f; margin-bottom: 10px;'>⚠️ 加载存档失败</h3>"
                + "<p style='font-size: 14px; margin: 5px 0;'>未能找到有效的游戏存档</p>"
                + "<p style='font-size: 14px; color: #666; margin: 5px 0;'>将为您开始新游戏</p>"
                + "</div></html>",
                "加载失败",
                JOptionPane.ERROR_MESSAGE);
            startNewGame();
        }
    }

    /**
     * 背景面板类
     * 继承自JPanel，用于处理背景图片的加载和显示
     */
    private class BackgroundPanel extends JPanel {
        private static final long serialVersionUID = 1L; // 序列化ID
        private Image backgroundImage; // 背景图片对象
        private int imageWidth, imageHeight; // 图片尺寸
        
        // 辅助方法：调整窗口大小以适应屏幕
        private void adjustWindowSizeToScreen() {
            SwingUtilities.invokeLater(() -> {
                // 获取屏幕尺寸
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int screenWidth = screenSize.width;
                int screenHeight = screenSize.height;
                
                // 确保窗口不超过屏幕大小
                int windowWidth = Math.min(imageWidth, screenWidth - 50);
                int windowHeight = Math.min(imageHeight, screenHeight - 50);
                
                MainMenu.this.setSize(windowWidth, windowHeight);
                MainMenu.this.setLocationRelativeTo(null);
                
                System.out.println("窗口大小调整为: " + windowWidth + "x" + windowHeight);
            });
        }

        public BackgroundPanel() {
            try {
                // 尝试通过多种方式加载背景图片
                
                // 方法1：通过R类加载
                InputStream is = R.getResourceAsStream("/images/background.png");
                if (is != null) {
                    backgroundImage = ImageIO.read(is);
                    imageWidth = backgroundImage.getWidth(null);
                    imageHeight = backgroundImage.getHeight(null);
                    System.out.println("通过R类成功加载背景图片，尺寸: " + imageWidth + "x" + imageHeight);
                    
                    // 调整主窗口尺寸以匹配背景图片
                    adjustWindowSizeToScreen();
                    
                    return;
                }
                
                // 方法2：通过类加载器加载
                URL url = getClass().getResource("/images/background.png");
                if (url != null) {
                    backgroundImage = ImageIO.read(url);
                    imageWidth = backgroundImage.getWidth(null);
                    imageHeight = backgroundImage.getHeight(null);
                    System.out.println("通过类加载器成功加载背景图片，尺寸: " + imageWidth + "x" + imageHeight);
                    
                    // 调整主窗口尺寸以匹配背景图片
                    adjustWindowSizeToScreen();
                    
                    return;
                }
                
                // 方法3：尝试从文件系统加载
                File file = new File("src/main/resources/images/background.png");
                if (file.exists()) {
                    backgroundImage = ImageIO.read(file);
                    imageWidth = backgroundImage.getWidth(null);
                    imageHeight = backgroundImage.getHeight(null);
                    System.out.println("从文件系统成功加载背景图片，尺寸: " + imageWidth + "x" + imageHeight);
                    
                    // 调整主窗口尺寸以匹配背景图片
                    adjustWindowSizeToScreen();
                    
                    return;
                }
                
                // 方法4：尝试从当前工作目录加载
                file = new File("images/background.png");
                if (file.exists()) {
                    backgroundImage = ImageIO.read(file);
                    imageWidth = backgroundImage.getWidth(null);
                    imageHeight = backgroundImage.getHeight(null);
                    System.out.println("从当前工作目录成功加载背景图片，尺寸: " + imageWidth + "x" + imageHeight);
                    
                    // 调整主窗口尺寸以匹配背景图片
                    adjustWindowSizeToScreen();
                    
                    return;
                }
                
                System.err.println("所有方法都无法加载背景图片");
            } catch (IOException e) {
                System.err.println("加载背景图片时出错: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // 绘制背景图片，使用原始尺寸
                g.drawImage(backgroundImage, 0, 0, imageWidth, imageHeight, this);
                System.out.println("绘制背景图片: " + imageWidth + "x" + imageHeight);
            } else {
                // 如果没有背景图片，使用渐变背景
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(240, 248, 255);
                Color color2 = new Color(176, 224, 230);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                System.out.println("绘制渐变背景: " + w + "x" + h);
            }
        }
        
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            // 确保背景图片被绘制
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, imageWidth, imageHeight, this);
            }
        }
    }
}
