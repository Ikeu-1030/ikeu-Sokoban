package com.sokoban.model.spirit;

import java.awt.image.BufferedImage;

import com.sokoban.model.map.GameCell;
import com.sokoban.util.R;

/**
 * Box类表示游戏中的箱子，继承自GameCell类并实现Cloneable接口
 * 箱子有两种状态：普通状态和胜利状态（已到达目标位置）
 */
public class Box extends GameCell implements Cloneable {
    // 定义箱子的两种状态常量
    private static final int           STATUS_WIN = 1, STATUS_NORMAL = 2;
    // 箱子状态
    private int                        status     = STATUS_NORMAL;
    final static private BufferedImage BOX_NOMAL  = R.getImage("/images/box-normal.jpg");
    final static private BufferedImage BOX_WIN    = R.getImage("/images/box-win.jpg");

    public Box(int x, int y) {
        super(x, y);
    }

    public void moveUp() {
        this.y--;
    }

    public void moveDown() {
        this.y++;
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
        System.out.println("Box.moveTo()");
    }

    public void moveLeft() {
        this.x--;
    }

    public void setNomal() {
        this.status = STATUS_NORMAL;
    }

    public void setWin() {
        this.status = STATUS_WIN;
    }

    public boolean isOnHome() {
        return this.status == STATUS_WIN;
    }

    public void moveRight() {
        this.x++;
    }

    @Override  // 重写父类的clone方法，实现对象的克隆
    public Box clone() {  // 定义Box类的克隆方法
        try {  // 尝试执行克隆操作
            return (Box) super.clone();  // 调用父类的clone方法进行克隆，并强制转换为Box类型
        } catch (Exception e) {  // 捕获克隆过程中可能出现的异常
            throw new RuntimeException(e);  // 将捕获的异常包装为RuntimeException抛出
        }
    }

/**
 * 获取单元格图像的方法
 * 根据当前状态返回对应的图像
 * @return 返回BufferedImage类型的单元格图像
 */
    @Override
    public BufferedImage getCellImage() {
    // 判断当前状态是否为普通状态
        if (status == STATUS_NORMAL) {
        // 如果是普通状态，返回普通状态的箱子图像
            return BOX_NOMAL;
        }
    // 否则返回获胜状态的箱子图像
        return BOX_WIN;
    }
}