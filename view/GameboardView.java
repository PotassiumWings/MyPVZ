package view;

import controller.*;

import java.awt.Graphics;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.*;

public class GameboardView extends JLayeredPane {

    private static final long serialVersionUID = 1L;
    private JFrame GameFrame;
    private ImageIcon panel, cardboard;
    private JPanel Panel;
    private JPanel Cardboard;

    Controller controller;

    private int x = -10;
    private boolean flag = false;
    private int direction = 1;
    private JLabel SunLabel;

    class PaintThread implements Runnable {
        JFrame frame;

        PaintThread(LaunchFrame launchFrame) {
            this.frame = launchFrame;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //左右移动对准
            for (int i = 0; i < 895; i++) {
                // System.out.println("now x:" + x + ",i:" + i);
                if (x <= -560 && !flag) {
                    flag = true;
                    direction = -1;
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                int j=i-551;
                int delaytime=
                    i<1?10:
                    i<2?9:
                    i<3?8:
                    i<4?7:
                    i<5?6:
                    i<6?5:
                    i<7?4:
                    i<8?3:
                    i<9?2:
                    i<534?1:
                    i<537?2:
                    i<540?3:
                    i<544?4:
                    i<547?5:
                    i<549?6:
                    i<550?8:14;
                if(j>0){
                    delaytime=
                    j<1?12:
                    j<2?11:
                    j<3?10:
                    j<4?9:
                    j<5?8:
                    j<6?7:
                    j<7?6:
                    j<8?5:
                    j<9?4:
                    j<10?3:
                    j<12?2:
                    j<314?1:
                    j<319?2:
                    j<324?3:
                    j<327?4:
                    j<330?5:
                    j<333?6:
                    j<334?8:14;
                }
                try {
                    Thread.sleep(delaytime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                x-=direction;
                Panel.repaint();
            }

            //cardboard淡入
            for (int y = -40 ; y <= 5; y++) {
                Cardboard.setBounds(20,y,cardboard.getIconWidth(), cardboard.getIconHeight());
                try {
                    Thread.sleep(10);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Cardboard.repaint();
            }

            //倒计时
            JLabel label;
            for (int i=1 ; i<=3 ; i++){
                try {
                    label = new JLabel(new ImageIcon("img\\PrepareGrowPlants"+i+".png"));
                    GameboardView.this.add(label,1);
                    label.setBounds(250,200,300,200);
                    Thread.sleep(700);
                    label.setVisible(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    GameboardView(LaunchFrame launchframe){
        this.controller=new Controller();
        this.GameFrame=launchframe;
        this.GameFrame.setContentPane(GameboardView.this);
        this.setVisible(true);

        //bg
        panel=new ImageIcon("img\\background1.jpg");
        Panel=new JPanel(){
			private static final long serialVersionUID = 1L;
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(panel.getImage(),x,0,
                            this.getWidth(),this.getHeight(),
                            this);
            }
        };
        Panel.setVisible(true);
        Panel.setBounds(0,0,panel.getIconWidth(),panel.getIconHeight());
        GameboardView.this.add(Panel,-1);

        //cardboard
        cardboard=new ImageIcon("img\\SeedBank.png");
        Cardboard=new JPanel(){
            private static final long serialVersionUID = 1L;
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(cardboard.getImage(),0,0,
                            cardboard.getIconWidth(),
                            cardboard.getIconHeight(),this);
            }
        };
        Cardboard.setVisible(true);
        Cardboard.setLayout(null);
        GameboardView.this.add(Cardboard,0);

        //sun
        SunLabel = new JLabel(""+controller.getSunCount().getText(),JLabel.CENTER);
        SunLabel.setBounds(20, 60, 35, 20);
        Cardboard.add(SunLabel);

        controller.setLayeredPane(this);
        //controller.setFrame(launchframe);
        controller.setSunCount(SunLabel);

        Executor exec = Executors.newSingleThreadExecutor();
        if(!flag){
            Thread Animation=new Thread(new PaintThread(launchframe));
            exec.execute(Animation);

            Card card1 = new Card("SunFlower",controller);
            card1.setRectangle(83, 7, card1.getCardWidth(), card1.getCardHeight());
            card1.setBounds(83, 7, card1.getCardWidth(), card1.getCardHeight());
            controller.addCard(card1);
            Cardboard.add(card1);
            
            Card card2 = new Card("PeaShooter",controller);
            card2.setRectangle(142, 7, card2.getCardWidth(), card2.getCardHeight());
            card2.setBounds(142, 7, card2.getCardWidth(), card2.getCardHeight());
            controller.addCard(card2);
            Cardboard.add(card2);
        }else{
            x = -215;
            Cardboard.setBounds(20, 5, cardboard.getIconWidth(), 
                                cardboard.getIconHeight());
            Panel.repaint();
            Panel.setVisible(true);
            controller.checkCards();
        }

        Thread sunThread=new Thread(new SunProducer(controller));//produce sun
        sunThread.start();

        Thread zombieThread=new Thread(new ZombieProducer(controller));//produce zombie
        zombieThread.start();

        JPanel topPanel = controller.getTopPanel();
        topPanel.setVisible(false);
        topPanel.setOpaque(false);
        topPanel.setBounds(0,0, panel.getIconWidth(), panel.getIconHeight());
        GameboardView.this.add(topPanel, new Integer(114514));

    }
    
}