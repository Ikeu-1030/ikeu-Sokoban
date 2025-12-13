package com.sokoban.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 霓虹风格弹窗工具类
 * 使用 HTML/CSS 样式渲染内容
 */
public class NeonDialogUI {

    // 设置全局弹窗背景色（Hack方法）
    private static void updateUIManager() {
        UIManager.put("OptionPane.background", new Color(20, 21, 31));
        UIManager.put("Panel.background", new Color(20, 21, 31));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background", new Color(43, 45, 66));
        UIManager.put("Button.foreground", Color.WHITE);
    }

    /**
     * 获取带有霓虹 CSS 样式的 HTML 字符串
     */
    private static String getNeonHTML(String title, String content, String iconEmoji) {
        return "<html><body style='width: 250px; font-family: sans-serif; background-color: #14151f; color: white; padding: 10px;'>"
                + "<div style='text-align: center; border-bottom: 2px solid #00f3ff; padding-bottom: 10px; margin-bottom: 10px;'>"
                + "  <h2 style='color: #00f3ff; margin: 0; text-shadow: 0px 0px 5px #00f3ff;'>" + iconEmoji + " " + title + "</h2>"
                + "</div>"
                + "<div style='text-align: center; font-size: 14px; color: #edf2f4; line-height: 1.5;'>"
                + content
                + "</div>"
                + "<div style='margin-top: 15px; border-top: 1px dashed #bc13fe; height: 1px;'></div>"
                + "</body></html>";
    }

    // 自动消失的 Toast 弹窗
    public static void showToast(Component parent, String content, String title, int durationMs) {
        updateUIManager();
        String html = getNeonHTML(title, content, "⏳");

        // 创建一个 JOptionPane，但不直接显示
        JOptionPane optionPane = new JOptionPane(new JLabel(html), JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);

        // 创建 JDialog
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setModal(false); // 设置为非模态，允许背后操作（可选，如果想强制等待则设为true，但这里是Toast通常非模态）

        // 创建定时器，指定时间后关闭窗口
        Timer timer = new Timer(durationMs, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    public static void showMessage(Component parent, String content, String title) {
        updateUIManager();
        String html = getNeonHTML(title, content, "✨");

        // 使用 JLabel 承载 HTML
        JLabel label = new JLabel(html);

        JOptionPane.showMessageDialog(parent, label, title, JOptionPane.PLAIN_MESSAGE);
    }

    public static void showError(Component parent, String content) {
        updateUIManager();
        String html = "<html><body style='width: 250px; background-color: #14151f; color: white;'>"
                + "<h3 style='color: #ff0055; text-align: center;'>⚠ SYSTEM ERROR</h3>"
                + "<p style='text-align: center;'>" + content + "</p>"
                + "</body></html>";

        JOptionPane.showMessageDialog(parent, new JLabel(html), "Error", JOptionPane.PLAIN_MESSAGE);
    }

    public static String showInput(Component parent, String content, String title) {
        updateUIManager();
        String html = getNeonHTML(title, content, "⌨");

        Object result = JOptionPane.showInputDialog(parent, new JLabel(html), title, JOptionPane.PLAIN_MESSAGE);
        return (String) result;
    }

    public static int showConfirm(Component parent, String content, String title) {
        updateUIManager();
        String html = getNeonHTML(title, content, "❓");

        return JOptionPane.showConfirmDialog(parent, new JLabel(html), title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
