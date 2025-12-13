package com.sokoban.model.map;

import java.awt.Graphics;

/**
 * GameMap类表示游戏地图，包含地图的基本属性和操作方法
 */
public class GameMap {
    // 定义地图的行数和列数，均为20
    private static final int rows = 20;
    private static final int cols = 20;

    // 存储地图单元格的二维数组
    public final GameMapCell[][] data = new GameMapCell[cols][rows];

    // 地图的宽度和高度属性
    final public int width;
    final public int height;

    /**
     * 构造函数，初始化地图的宽度和高度
     * 地图的宽度和高度由单元格数量和单元格宽度共同决定
     */
    public GameMap() {
        width = rows * GameMapCell.width;
        height = cols * GameMapCell.width;
    }

    /**
     * 向地图中添加一个单元格
     * @param cell 要添加的GameMapCell对象
     */
    public void add(GameMapCell cell) {
        data[cell.y][cell.x] = cell;
    }

    /**
     * 获取指定位置的地图单元格
     * @param x 单元格的x坐标
     * @param y 单元格的y坐标
     * @return 指定位置的GameMapCell对象，如果位置无效则返回null
     */
    public GameMapCell get(int x, int y) {
        if (x >= 0 && x < cols && y >= 0 && y < rows) {
            return data[y][x];
        }
        return null;
    }

    /**
     * 绘制地图上的所有单元格
     * @param g 图形上下文对象，用于绘制
     */
    public void paint(Graphics g) {
        GameMapCell[][] data = this.data;
        GameMapCell cell;
        // 遍历地图上的所有单元格
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                cell = data[i][j];
                // 如果单元格存在，则绘制它
                if (cell != null) {
                    cell.paint(g);
                }
            }
        }
    }
}