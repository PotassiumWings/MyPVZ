package view;

import javax.swing.*;
import java.awt.*;
import controller.*;

class Zombie extends JLabel implements Runnable {
    private static final long serialVersionUID = 1L;

    private static int MOVE=1;
    private static int ATTACK=2;
    private static int LOSTHEAD=3;
    private static int LOSTHEADATTACK=4;
    private static int DIE=5;
    private static int BOOM=6;

    private String name;
    //hp2:临界值
    //attack: /s
    private int hp,hp2,attack,state=1;
    //走一格时间
    private int v;

    private double x,y,row;

    private ImageIcon img;
    private int []sumPic=new int[3];
    private int nowPic=0;
    private int type;
    
    private Controller controller;

    public Zombie(Controller controller,String name,int hp,int hp2,int attack,int row,int v){
        this.controller=controller;
        this.name=name;
        this.hp=hp;
        this.attack=attack;
        this.y=row*100+28;
        this.x=800;
        this.row=row;
        this.sumPic[0]=22;
        this.sumPic[1]=31;
        this.sumPic[2]=18;
    }

    @Override
    public void paintComponent(Graphics g){
        ImageIcon Img;
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        img=new ImageIcon("img\\shadow.png");
        g.drawImage(img.getImage(),70,115,img.getIconWidth(),img.getIconHeight(),this);
        if(state==MOVE){
            Img=new ImageIcon("img\\Zombie"+type+"\\Frame"+nowPic+".png");
            g2.drawImage(Img.getImage(),0,0,Img.getIconWidth(),Img.getIconHeight(),this);
        }else if(state==ATTACK){

        }else if(state==LOSTHEAD){

        }else if(state==LOSTHEADATTACK){

        }else if(state==BOOM){
            
        }
    }

    public Zombie normalZombie(Controller controller,int row){
        Zombie tempZombie=new Zombie(controller,"NormalZombie",200,70,100,row,4700);
        tempZombie.type=(int)Math.random()*3;
        return tempZombie;
    }

    @Override
    public void run() {
        while(hp>0){

        }
    }

}