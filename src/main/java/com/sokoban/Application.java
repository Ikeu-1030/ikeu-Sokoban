package com.sokoban;

import javax.swing.SwingUtilities;

import com.sokoban.model.Game;
import com.sokoban.view.MainMenu;

public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Game g=new Game();
                // 创建并显示主菜单
                MainMenu mainMenu = new MainMenu(g);
                mainMenu.setVisible(true);
            }
        });
    }
}
