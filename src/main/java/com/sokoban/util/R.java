package com.sokoban.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * 资源加载类，用于加载和管理游戏中的图像资源
 */
public class R {
    // 存储资源类型的Class对象
    private static final Class<?> TYPE = R.class;
    // 使用HashMap存储图像资源，初始容量为16
    static final private Map<String, BufferedImage> IMAGES_MAP = new HashMap<>(16);

    /**
     * 加载所有游戏资源的方法
     * 该方法会调用loadImages()方法来加载图像资源
     * 如果加载过程中出现异常，将其包装为RuntimeException抛出
     */
    public static void loadAsset() {
        try {
            loadImages();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载图像资源的方法
     * 从指定路径加载多个图像文件到IMAGES_MAP中
     * @throws IOException 如果读取图像文件时发生I/O错误
     */
    private static void loadImages() throws IOException {
        // 定义需要加载的图像文件名数组
        String[] imageNames = {
                "block.jpg", "blank.jpg", "way.jpg", "target.jpg",
                "box-normal.jpg", "box-win.jpg", "character-up.png", "character-down.png",
                "character-left.png", "character-right.png", "background.png"
        };

        // 遍历图像文件名数组，加载每个图像文件
        for (String name : imageNames) {
            // 通过类加载器获取图像文件的输入流
            InputStream is = TYPE.getResourceAsStream("/images/" + name);
            if (is != null) {
                // 使用ImageIO读取图像文件并转换为BufferedImage对象
                BufferedImage image = ImageIO.read(is);
                // 将图像对象存储到IMAGES_MAP中，键为文件名
                IMAGES_MAP.put(name, image);
                // 关闭输入流
                is.close();
            }
        }
    }

    /**
     * 获取指定路径的资源输入流
     * @param path 资源路径
     * @return 资源的输入流，如果找不到则返回null
     */
    public static InputStream getResourceAsStream(String path) {
        return TYPE.getResourceAsStream(path);
    }

    /**
     * 根据名称获取图像资源
     * 支持多种文件名格式的查询，并提供别名映射
     * @param name 图像文件名或别名
     * @return 对应的BufferedImage对象，如果找不到则返回null
     */
    public static BufferedImage getImage(String name) {
        // 提取文件名（去除路径部分）
        String fileName = name;
        if (name.contains("/")) {
            String[] parts = name.split("/");
            fileName = parts[parts.length - 1];
        }

        // 从IMAGES_MAP中获取图像
        BufferedImage image = IMAGES_MAP.get(fileName);
        if (image == null) {
            // 处理别名映射，将旧名称映射到新名称
            if (name.equals("box.jpg") || fileName.equals("box.jpg")) {
                return IMAGES_MAP.get("box-normal.jpg");
            } else if (name.equals("boxwin.jpg") || fileName.equals("boxwin.jpg")) {
                return IMAGES_MAP.get("box-win.jpg");
            } else if (name.equals("character_up.png") || fileName.equals("character_up.png")) {
                return IMAGES_MAP.get("character-up.png");
            } else if (name.equals("character_down.png") || fileName.equals("character_down.png")) {
                return IMAGES_MAP.get("character-down.png");
            } else if (name.equals("character_left.png") || fileName.equals("character_left.jpg")) {
                return IMAGES_MAP.get("character-left.png");
            } else if (name.equals("character_right.png") || fileName.equals("character_right.png")) {
                return IMAGES_MAP.get("character-right.png");
            }
        }
        return image;
    }
}