package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import controller.*;

public class Sun extends JLabel implements Runnable {

    private static final long serialVersionUID = 1L;

    private Image image, offScreenImage;

    private int x, y;
    private boolean isCollected;
    private int nowPic;
    private static int totPic = 21;
    private Controller controller;

    private boolean drop;// 掉落还是随机生成

    // 随机生成：
    private int ymax;// 下

    // 向日葵掉落：
    private int ymin;
    private int startX, startY, endX, endY;

    public Sun(Controller controller) {
        this.x = (int) (Math.random() * 660) + 30;
        this.ymin = this.y = 40;
        this.ymax = (int) (Math.random() * 400) + 130;
        this.controller = controller;
        this.drop = false;
        setVisible(true);
        controller.getLayeredPane().add(this, new Integer(500));
    }

    public Sun(Controller controller, int r, int c) {
        this(controller);
        this.drop = true;
        this.ymin = r * 100 + 55;
        startX = c * 80 + 34 + (int) (Math.random() * 2);
        startY = r * 100 + 80;
        endX = c * 80 + 30 + (int) (Math.random() * 10);
        endY = r * 100 + 100;
        this.x = startX;
        this.y = startY;
    }

    public void picChange() {
        nowPic = (nowPic + 1) % totPic;
    }

    public void shrink() {
        for (int i = 1; i <= 50; i++) {
            if (i % 8 == 0) {
                picChange();
            }
            alpha -= 2;
            try {
                Thread.sleep(7);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
        setVisible(false);
    }

    @Override
    public void run() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isCollected) {
                    isCollected = true;
                }
            }
        });

        // dropping
        if (drop) {
            // 掉落，用线段近似二次函数
            int vx = 1, vy = 2;
            boolean flag1 = false, flag2 = false, flag3 = false, flag4 = false;
            int dx = (endX >= startX) ? 1 : -1, dy = -1;
            for (int i = 1; (this.x <= endX && dx == 1) || (this.x >= endX && dx == -1) || this.y <= endY; i++) {
                if (i % 5 == 0) {
                    this.picChange();
                }
                this.setBounds(x, y, 78, 78);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                x += dx * vx;
                y += dy * vy;
                if (!flag1) {
                    if (y <= (ymin + startY) / 2) {
                        flag1 = true;
                        vy = 1;
                    }
                    ;
                } else if (!flag2) {
                    if (y <= ymin) {
                        vy = -1;
                        flag2 = true;
                    }
                } else if (!flag3) {
                    if ((x >= (endX * 3 + startX) / 4 || y >= (ymin + endY) / 2)) {
                        vy = -2;
                        flag3 = true;
                    }
                } else if (!flag4) {
                    if ((x >= (endX * 7 + startX) / 8 || y >= (startY + endY) / 2)) {
                        vy = -3;
                        flag4 = true;
                    }
                }
                this.repaint();
            }
        } else {
            // 随机生成，直线
            for (int i = 1; this.y < ymax && !isCollected; i++) {
                if (i % 50 == 0) {
                    this.picChange();
                }
                this.setBounds(x, y, 78, 78);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (i % 10 == 0)
                    this.y++;
                this.repaint();
            }
        }

        // onGround
        for (int i = 1; i <= 5000 && !isCollected; i++) {
            if (i % 50 == 0) {
                this.picChange();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            this.repaint();
        }

        // collected
        Point tempPoint = new Point(x, y);
        if (isCollected) {
            controller.addSunCount(25);
            controller.checkCards();
            int dir = x < 33 ? -1 : 1;
            for (int i = 1; (dir == 1 && x > 33 || x <= 33 && dir == -1) && y > 33; i++) {
                if (i % 10 == 0) {
                    this.picChange();
                }
                double ty = (x - 33) * (tempPoint.getY() - 33) / (tempPoint.getX() - 33) + 33;
                if ((ty <= y && dir == 1) || (ty >= y && dir == -1)) {
                    y -= 6;
                } else {
                    x -= 6;
                    if ((int) ((100 * x - 3200) / (tempPoint.getX() - 33)) != (int) ((100 * x - 3300)
                            / (tempPoint.getX() - 33))) {
                        alpha -= (Math.random() >= 0.45) ? 1 : 0;
                        // System.out.println("alpha reduced from point ("+x+","+y+")");
                    }
                }

                this.setBounds(x, y, 78, 78);
                this.repaint();
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }else{
            //disappear
            this.shrink();
        }
        setVisible(false);
        Thread.currentThread().interrupt();
    }

    private double alpha = 100;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon img = new ImageIcon("img\\Sun\\"+nowPic+".png");
        image = img.getImage();
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.SrcOver.derive((float) alpha / 100));
        g2.drawImage(image, 0, 0, img.getIconWidth()*6/7, img.getIconWidth()*6/7,  this);
    }
    public void update(Graphics g) {
        if(offScreenImage == null) offScreenImage = this.createImage(800, 600);
        Graphics gImage = offScreenImage.getGraphics();
        paint(gImage);
        g.drawImage(offScreenImage, 0, 0, null);
    }
    
}