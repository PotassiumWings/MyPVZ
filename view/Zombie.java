package view;

import javax.swing.*;
import java.awt.*;
import controller.*;

public class Zombie extends JLabel implements Runnable {
    private static final long serialVersionUID = 1L;

    private static int MOVE = 1;
    private static int ATTACK = 2;
    private static int LOSTHEAD = 3;
    private static int LOSTHEADATTACK = 4;
    private static int DIE = 5;
    private static int BOOM = 6;

    private static int BOOMINJURY = 1000;

    private String name;
    // hp2:临界值
    // attack: /s
    private int hp, hp2, state = 1;
    // 走一格时间/格长=走1的时间
    // private int v;

    private int x, y;
    private int row;

    private ImageIcon img;
    private int[] sumPic = new int[3];
    private int nowSumPic;
    private int nowPic = 0;
    private int type;

    private Controller controller;

    public int getXPos() {
        return x;
    }

    public Zombie() {

    }

    public Zombie(Controller controller, String name, int hp, int hp2, int row, int v) {
        setVisible(true);
        this.nowPic = 0;
        this.controller = controller;
        this.name = name;
        this.hp = hp + hp2;
        this.hp2 = hp2;
        this.y = row * 100 + 28;
        this.x = 800;
        this.row = row;
        this.sumPic[0] = 22;
        this.sumPic[1] = 31;
        this.sumPic[2] = 18;
        this.setState(MOVE);
        controller.getLayeredPane().add(this, new Integer(400));
    }

    @Override
    public void paintComponent(Graphics g) {
        ImageIcon Img;
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        img = new ImageIcon("img\\shadow.png");
        g.drawImage(img.getImage(), 70, 115, img.getIconWidth(), img.getIconHeight(), this);
        if (state == MOVE) {
            Img = new ImageIcon("img\\Zombie" + type + "\\Frame" + nowPic + ".png");
            g2.drawImage(Img.getImage(), 0, 0, Img.getIconWidth(), Img.getIconHeight(), this);
        } else if (state == ATTACK) {
            Img = new ImageIcon("img\\ZombieAttack\\Frame" + nowPic + ".png");
            g2.drawImage(Img.getImage(), 0, 0, Img.getIconWidth(), Img.getIconHeight(), this);
        } else if (state == LOSTHEAD) {
            Img = new ImageIcon("img\\ZombieLostHead\\Frame" + nowPic + ".png");
            g2.drawImage(Img.getImage(), 0, 0, Img.getIconWidth(), Img.getIconHeight(), this);
            // 头动画只持续前10帧
            if (nowPic < 10) {
                Img = new ImageIcon("img\\ZombieHead\\Frame" + nowPic + ".png");
                g2.drawImage(Img.getImage(), 60, 0, Img.getIconWidth(), Img.getIconHeight(), this);
            }
        } else if (state == LOSTHEADATTACK) {
            Img = new ImageIcon("img\\ZombieLostHeadAttack\\Frame" + nowPic + ".png");
            g2.drawImage(Img.getImage(), 0, 0, Img.getIconWidth(), Img.getIconHeight(), this);
        } else if (state == DIE) {
            Img = new ImageIcon("img\\ZombieDie\\Frame" + nowPic + ".png");
            g2.drawImage(Img.getImage(), 0, 0, Img.getIconWidth(), Img.getIconHeight(), this);
        } else if (state == BOOM) {
            Img = new ImageIcon("img\\ZombieBoom\\Frame" + nowPic + ".png");
            g2.drawImage(Img.getImage(), 0, 0, Img.getIconWidth(), Img.getIconHeight(), this);
        }
    }

    public Zombie normalZombie(Controller controller, int row) {
        Zombie tempZombie = new Zombie(controller, "NormalZombie", 200, 70, row, 4700 / 80);
        tempZombie.type = (int) Math.random() * 3;
        return tempZombie;
    }

    public void setState(int state) {
        this.state = state;
        nowPic = 0;
        if (state == MOVE) {
            this.nowSumPic = sumPic[type];
        } else if (state == ATTACK) {
            this.nowSumPic = 21;
        } else if (state == LOSTHEAD) {
            this.nowSumPic = 18;
        } else if (state == LOSTHEADATTACK) {
            this.nowSumPic = 11;
        } else if (state == DIE) {
            this.nowSumPic = 14;
        } else if (state == BOOM) {
            this.nowSumPic = 20;
        }
    }

    public void updateState() {
        if (hp <= 0) {
            if (this.state != DIE && this.state != BOOM)
                setState(DIE);
        } else if (hp <= hp2) {
            if (this.state == ATTACK)
                setState(LOSTHEADATTACK);
            else if(this.state != LOSTHEAD)
                setState(LOSTHEAD);
        }
    }

    public Plant getPlant() {
        assert (findPlant());
        return controller.getPlants()[row][(x + 60) / 80];
    }

    public boolean findPlant() {
        if ((x + 60) / 80 >= 9)
            return false;
        return controller.getPlants()[row][(x + 60) / 80] != null;
    }

    public void reduceHP(int x) {
        this.hp -= x;
        updateState();
        //System.out.println("reduced to "+hp+",state="+state);
    }

    public void boom() {
        if (this.hp > BOOMINJURY) {
            reduceHP(BOOMINJURY);
        } else {
            this.hp = 0;
            setState(BOOM);
        }
    }

    @Override
    public void run() {
        setState(MOVE);
        while (hp > hp2) {
            // MOVE
            while (this.state == MOVE) {
                if (findPlant()) {
                    setState(ATTACK);
                    break;
                }
                // sleep 60ms, x--
                for(int j = 0; j < 2; j++) {
                    for (int i = 0; i < 10 && this.state == MOVE; i++) {
                        try {
                            Thread.sleep(6);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    this.x--;
                    this.setBounds(x, y, 400, 300);
                    this.repaint();
                }
                //120ms 
                nowPic = (nowPic + 1) % nowSumPic;
            }

            // ATTACK
            while (this.state == ATTACK) {
                if (this.name == "NormalZombie") {
                    // 普通僵尸的攻击方式：1s 200伤害，5ms 1伤害
                    // sleep 120ms change
                    for (int i = 0; i < 24 && this.state == ATTACK && findPlant(); i++) {
                        getPlant().attacked(1);
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    nowPic = (nowPic + 1) % nowSumPic;
                    this.repaint();
                    if (!findPlant())
                        setState(MOVE);// 不掉胳膊
                }
            }

        }
        while (hp > 0) {
            // 临界状态
            while (this.state == LOSTHEAD || this.state == LOSTHEADATTACK) {
                //sleep 60ms change
                for(int j = 0; j < 2; j++) {
                    for (int i = 0; i < 10 && hp > 0; i++) {
                        try {
                            Thread.sleep(6);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    this.x--;
                    this.setBounds(x, y, 400, 300);
                    this.repaint();
                }
                //120ms 
                nowPic = (nowPic + 1) % nowSumPic;
                reduceHP(7);
            }

            controller.deleteZombie(this, row);

            // 死亡
            while (true) {
                for (int i = 0; i < nowSumPic; i++) {
                    //sleep 120ms change
                    try {
                        Thread.sleep(120);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.repaint();
                    nowPic = (nowPic + 1) % nowSumPic;
                }
                break;
            }
        }
        while (this.state == BOOM) {
            // 爆炸
            while (true) {
                for (int i = 0; i < nowSumPic; i++) {
                    //sleep 50ms change
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.repaint();
                    nowPic = (nowPic + 1) % nowSumPic;
                }
                break;
            }
        }
        setVisible(false);
        Thread.currentThread().interrupt();
    }
}