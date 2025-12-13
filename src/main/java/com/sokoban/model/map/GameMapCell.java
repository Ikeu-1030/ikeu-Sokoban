package com.sokoban.model.map;

import java.awt.image.BufferedImage;

import com.sokoban.util.R;

/**
 * GameMapCell 类，表示游戏地图中的一个单元格，继承自 GameCell 类
 * 包含不同类型的地图单元格（空白、障碍物、路径、目的地）及其图片资源
 */
public class GameMapCell extends GameCell {
    // 定义地图单元格的常量类型
    public static final int TYPE_BLANK=0,TYPE_BLOCK=1,TYPE_WAY=2,TYPE_TARGET=4;
    // 存储不同类型单元格对应的图片数组
    private static final BufferedImage[] maps = new BufferedImage[5];
    // 标记图片资源是否已初始化
    private static boolean initialized = false;

    // 当前单元格的类型
    public final int type;

    /**
     * 构造函数，创建一个地图单元格
     * @param x 单元格的x坐标
     * @param y 单元格的y坐标
     * @param type 单元格的类型（使用类中定义的常量）
     */
    public GameMapCell(int x, int y,int type) {
        super(x,y);
        this.type = type;
        ensureInitialized();
    }

    /**
     * 确保图片资源已初始化，使用同步方法保证线程安全
     */
    private static synchronized void ensureInitialized() {
        if (!initialized) {
            System.out.println("初始化 GameMapCell 图片数组");
            // 加载不同类型单元格对应的图片
            maps[TYPE_BLANK] = R.getImage("/images/Sokoban/blank.jpg");
            maps[TYPE_BLOCK] = R.getImage("/images/Sokoban/block.jpg");
            maps[TYPE_WAY] = R.getImage("/images/Sokoban/way.jpg");
            maps[TYPE_TARGET] = R.getImage("/images/Sokoban/target.jpg");
            initialized = true;
            System.out.println("GameMapCell 图片数组初始化完成");
        }
    }

    /**
     * 获取单元格对应的图片
     * @return 返回单元格类型的图片，如果类型无效则返回null
     */
    @Override
    public BufferedImage getCellImage() {
        ensureInitialized();
        // 检查类型是否有效
        if (type < 0 || type >= maps.length) {
            return null;
        }
        return maps[type];
    }
}