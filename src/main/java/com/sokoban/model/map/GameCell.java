package com.sokoban.model.map;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * GameCell 是一个抽象类，代表游戏中的一个单元格
 * 它定义了单元格的基本属性和行为，包括位置、大小和绘制方法
 */
public abstract class GameCell {
    // 静态常量，定义单元格的宽度为30像素
    static public final int width = 30;
    // 公共属性，表示单元格在游戏网格中的x坐标
    public int x;
    // 公共属性，表示单元格在游戏网格中的y坐标
    public int y;

    /**
     * 构造函数，用于创建一个新的GameCell实例
     * @param x 单元格在x轴上的坐标
     * @param y 单元格在y轴上的坐标
     */
    public GameCell(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    /**
     * 绘制单元格的方法
     * 使用Graphics对象在指定位置绘制单元格的图像
     * @param g 用于绘制的Graphics对象
     */
    public final void paint(Graphics g) {
        // 调用getCellImage()获取单元格图像，并在(x * width, y * width)位置绘制
        g.drawImage(this.getCellImage(), x * width, y * width, width, width, null);
    }

    /**
     * 抽象方法，获取单元格的图像
     * 由子类实现，返回特定类型单元格的图像
     * @return 单元格的BufferedImage对象
     */
    public abstract BufferedImage getCellImage();
}