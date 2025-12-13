package com.sokoban.model;

import com.sokoban.model.spirit.Box;
import com.sokoban.model.spirit.Boy;

/**
 * 游戏移动类，用于记录游戏中的移动状态，包括男孩的位置和方向以及盒子的位置
 */
public class GameMove {
    final public int boyX,boyY;
    final public int boyDirection;
    final public Box moveBox;
    public int boxX;
    public int boxY;
    private GameMove(Boy boy, Box moveBox) {
        super();
        this.boyX = boy.x;
        this.boyY = boy.y;
        this.boyDirection=boy.currentDirection;
        this.moveBox=moveBox;
        if(moveBox!=null) {
            this.boxX = moveBox.x;
            this.boxY = moveBox.y;
        }
    }
    public static GameMove of(Boy boy) {
        return new GameMove(boy,null);
    }
    public static GameMove of(Boy boy,Box box) {
        return new GameMove(boy,box);
    }
    @Override
    public String toString() {
        return "GameMove [boyX=" + boyX + ", boyY=" + boyY + ", boxX=" + boxX + ", boxY=" + boxY + "]";
    }

}
