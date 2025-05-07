package com.x.program.center.jaxrs.captcha;

import com.x.base.core.project.bean.tuple.Quadruple;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Random;
import javax.imageio.ImageIO;

public class CaptchaGenerator {

    private final int width;
    private final int height;
    private int noiseLineCount = 15;
    private final Random random = new SecureRandom();

    public CaptchaGenerator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static CaptchaGenerator of(int width, int height) {
        return new CaptchaGenerator(width, height);
    }

    public Quadruple<Integer, Integer, BufferedImage, Integer> generator() {
        // 1. 生成随机数和答案
        int num1 = random.nextInt(50); // 0 to 99
        int num2 = random.nextInt(50); // 0 to 99
        int answer = num1 + num2;
        String expression = String.format("%d+%d=?", num1, num2);

        // 2. 创建图像缓冲区
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿，让文字更清晰
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 3. 绘制背景
        g2d.setColor(getRandomColor(200, 250)); // 浅色背景
        g2d.fillRect(0, 0, width, height);

        // 4. 绘制干扰线
        g2d.setColor(getRandomColor(160, 200)); // 干扰线颜色
        g2d.setStroke(new BasicStroke(1.5f)); // 设置线条粗细
        for (int i = 0; i < noiseLineCount; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }

        // 5. 绘制验证码文本
        // 设置字体 (可以随机选择字体增加难度)
        g2d.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, height - 15)); // 字体大小根据图片高度调整

        // 设置文字颜色 (深色)
        g2d.setColor(getRandomColor(20, 130));

        // 计算文字位置使其居中 (近似)
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(expression);
        int textAscent = fm.getAscent(); // 文字基线以上的高度
        int textDescent = fm.getDescent(); // 文字基线以下的高度

        // 中心X坐标 + 轻微随机偏移
        int x = (width - textWidth) / 2 + random.nextInt(6) - 3;
        // 中心Y坐标 (基于Ascent调整，使文字垂直居中) + 轻微随机偏移
        int y = (height - (textAscent + textDescent)) / 2 + textAscent + random.nextInt(6) - 3;

        // 绘制字符串
        g2d.drawString(expression, x, y);

        // 6. 清理资源
        g2d.dispose();

        // 7. 返回结果
        return Quadruple.of(height, width, image, answer);
    }

    // --- 辅助方法：获取随机颜色 ---
    private Color getRandomColor(int fc, int bc) {
        // 给定范围获得随机颜色
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        if (fc < 0) {
            fc = 0;
        }
        if (bc < 0) {
            bc = 0;
        }
        if (fc > bc) {
            fc = bc - 1; // 保证fc <= bc
        }

        int r = fc + random.nextInt(bc - fc + 1);
        int g = fc + random.nextInt(bc - fc + 1);
        int b = fc + random.nextInt(bc - fc + 1);
        return new Color(r, g, b);
    }

    // --- Getters/Setters (可选) ---
    public void setNoiseLineCount(int noiseLineCount) {
        this.noiseLineCount = noiseLineCount;
    }

}
