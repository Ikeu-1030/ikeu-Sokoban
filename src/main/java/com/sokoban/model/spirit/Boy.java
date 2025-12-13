package com.sokoban.model.spirit;

import java.awt.image.BufferedImage;

import com.sokoban.model.Direction;
import com.sokoban.model.map.GameCell;
import com.sokoban.util.R;

/**
 * Boy类表示游戏中的男孩角色，继承自GameCell类并实现Direction接口。
 * 该类处理男孩角色的移动、方向控制和图像显示等功能。
 */
public class Boy extends GameCell implements Direction {

    // 定义男孩角色四个方向的静态图像常量
    private static final BufferedImage IMG_BOY_UP       = R.getImage("/images/character-up.png");    // 向上的图像
    private static final BufferedImage IMG_BOY_DOWN     = R.getImage("/images/character-down.png");  // 向下的图像
    private static final BufferedImage IMG_BOY_LEFT     = R.getImage("/images/character-left.png");  // 向左的图像
    private static final BufferedImage IMG_BOY_RIGHT    = R.getImage("/images/character-right.png"); // 向右的图像

    public int currentDirection = DOWN;  // 当前方向，默认向下


    /**
     * Boy类的构造函数
     * @param x 初始x坐标
     * @param y 初始y坐标
     */
    public Boy(int x, int y) {
        super(x, y);
    }

    /**
     * 向上移动男孩角色
     * 减少y坐标值，并将当前方向设为向上
     */
    public void moveUp() {
        this.y--;
        this.currentDirection=UP;
    }
    /**
     * 重置男孩角色的位置和方向
     * @param x 新的x坐标
     * @param y 新的y坐标
     * @param direction 新的方向
     */
    public void reset(int x,int y,int direction) {
        this.x=x;
        this.y=y;
        this.currentDirection=direction;
    }
    /**
     * 向下移动男孩角色
     * 增加y坐标值，并将当前方向设为向下
     */
    public void moveDown() {
        this.y++;
        this.currentDirection=DOWN;
    }

    /**
     * 向左移动男孩角色
     * 减少x坐标值，并将当前方向设为向左
     */
    public void moveLeft() {
        this.x--;
        this.currentDirection=LEFT;
    }

    /**
     * 向右移动男孩角色
     * 增加x坐标值，并将当前方向设为向右
     */
    public void moveRight() {
        this.x++;
        this.currentDirection=RIGHT;
    }

    /**
     * 根据当前方向获取男孩角色的图像
     * @return 对应方向的BufferedImage图像
     */
    @Override
    public BufferedImage getCellImage() {
        switch (currentDirection) {
            case UP:
                return IMG_BOY_UP;
            case RIGHT:
                return IMG_BOY_RIGHT;
            case DOWN:
                return IMG_BOY_DOWN;
            case LEFT:
                return IMG_BOY_LEFT;
        }
        return null;
    }
}