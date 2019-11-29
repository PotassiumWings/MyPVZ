package view;

import controller.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Card extends JLabel implements MouseListener, Runnable {

    private static final long serialVersionUID = 1L;
    private boolean inCooling;
    private boolean isChoosed;
    private int cd;
    private int totTime;

    private String cardName;
    private int price;
    private int cardWidth;
    private int cardHeight;

    private ImageIcon card;
    private ImageIcon cardLight;
    private ImageIcon cardDark;
    private ImageIcon cardCooling;

    private ImageIcon preImg;
    private ImageIcon blurImg;

    private Map<String, Plant> plantMap;
    private Controller controller;
    // private int y;

    private Rectangle rectangle;// 卡片位置

    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setRectangle(int x, int y, int w, int h) {
        this.rectangle = new Rectangle(x, y, w, h);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Card(String name, Controller controller) {
        this.controller = controller;
        this.plantMap = controller.getPlantMap();
        this.cardName = name;
        this.cardLight = new ImageIcon("Img\\Cards\\" + cardName + "0.png");
        this.cardDark = new ImageIcon("Img\\Cards\\" + cardName + "1.png");
        this.cardCooling = new ImageIcon("Img\\Cards\\" + cardName + "2.png");
        this.preImg = new ImageIcon("img\\" + cardName + "\\" + cardName + "_0.png ");
        this.blurImg = new ImageIcon("img\\Blurs\\" + cardName + ".png ");
        this.card = cardDark;
        this.inCooling = true;
        this.addMouseListener(this);
        this.cardHeight = cardLight.getIconHeight();
        this.cardWidth = cardLight.getIconWidth();
        this.price = plantMap.get(cardName).getPrice();
        this.cd = plantMap.get(cardName).getCardCD();
        // this.y = card.getIconHeight();
    }

    public boolean sunCountEnough(int SunCount) {
        return SunCount >= plantMap.get(this.cardName).getPrice();
    }

    public void check(int SunCount) {// 能否选择这个卡，能：light，否：dark
        if (sunCountEnough(SunCount) && !inCooling) {
            card = cardLight;
            this.repaint();
        } else {
            card = cardDark;
            this.repaint();
        }
    }

    public void setInCooling(boolean b) {
        this.inCooling = b;
    }

    public boolean getInCooling() {
        return inCooling;
    }

    public int getPrice() {
        return price;
    }

    public String getCardName() {
        return cardName;
    }

    public int getCardWidth() {
        return cardWidth;
    }

    public int getCardHeight() {
        return cardHeight;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int h = card.getIconHeight(), w = card.getIconWidth();
        g.drawImage(card.getImage(), 0, 0, w, h, this);
        if (inCooling) {
            g.drawImage(cardCooling.getImage(), 0, 0, w, h - h * totTime / cd, 0, 0, w, h - h * totTime / cd, this);
        }
    }

    public void setChoosed(boolean choosed) {
        this.isChoosed = choosed;
    }

    public void selected() {// 卡牌被选中
        if (!inCooling && controller.getIntSunCount() >= this.getPrice()) {
            controller.getTopPanel().setVisible(true);
            controller.setPreImg(this.preImg);
            controller.setBlurImg(this.blurImg);
            controller.setCard(this);
            controller.setSelectedIndex(index);
            controller.nowPlant = new Plant().getPlant(this.getCardName());
            // System.out.println("Selected :" + controller.nowPlant.getName());
            inCooling = true;
            isChoosed = true;
            check(0);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isChoosed) {
            isChoosed = false;
        } else {
            selected();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void run() {
        while (!controller.isRunning) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (controller.isRunning) {
            while (controller.isRunning && (!inCooling || (inCooling && isChoosed))) {
                check(controller.getIntSunCount());
                try {
                    Thread.sleep(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            check(controller.getIntSunCount());
            while (controller.isRunning) {
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                totTime += 3;
                if (totTime >= cd) {
                    break;
                }
                check(controller.getIntSunCount());
            }
            totTime = 0;
            inCooling = false;
        }
    }

}