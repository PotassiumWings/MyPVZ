package controller;

import view.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class Controller {
    //appearance
    private JFrame frame;
    private static int cardHeight = 90, cardWidth = 45;
    private static int tileHeight = 100, tileWidth = 80;

    //plants
    private Plant[][] plants = new Plant[5][9];
    public Plant nowPlant = null;

    //Cards
    private static int numRow = 5, numCol = 9;// 行列个数
    private static int cardNum = 1;// 卡牌区个数
    private Card[] Cards = new Card[10];//需要初始化
    private Card card;//当前选中的card

    //sun
    private JLabel sunCount = new JLabel();

    //mouse
    private Point mouseP;
    private JLayeredPane layeredPane;
    private TopPanel topPanel = new TopPanel();
    private ImageIcon preImg;
    private ImageIcon blurImg;

    //map
    private Map<String, Plant> plantMap = new HashMap<String, Plant>();

    //state
    public boolean isRunning=false;

    //Zombies
    public List<ArrayList<Zombie>> Zombies=new LinkedList<>();

    public void addZombie(Zombie zombie,int row){
        Zombies.get(row).add(zombie);
    }

    public void deleteZombie(Zombie zombie,int row){
        for(Zombie tempZombie: Zombies.get(row)){
            if(tempZombie==zombie){
                Zombies.get(row).remove(tempZombie);
                return;
            }
        }
        assert(false);
    }

    public Zombie getAttackedZombie(int row,int x){
        for(Zombie tempZombie: Zombies.get(row)){
            if(tempZombie.getXPos()>=x-2&&tempZombie.getXPos()<=x+2){
                return tempZombie;
            }
        }
        return null;
    }

    public void endGame(){
        System.out.println("You Lost.");
        for(int i = 0; i < numRow; i++){
            for(int j = 0; j < numCol; j++){
                if(plants[i][j] != null){
                    plants[i][j].die();
                }
            }
            for(Zombie zombie: Zombies.get(i)) {
                zombie.endThread();
            }
        }
        this.isRunning = false;
    }

    public Plant[][] getPlants(){
        return this.plants;
    }

    public void setLayeredPane(JLayeredPane gameboardView) {
        this.layeredPane = gameboardView;
    }

    public JLayeredPane getLayeredPane(){
        return layeredPane;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public JFrame getFrame() {
        return this.frame;
    }

    public void setSunCount(JLabel cnt) {
        this.sunCount = cnt;
    }
    public void setSunCount(int cnt) {
        this.sunCount.setText("" + cnt);
    }
    public void addSunCount(int cnt){
        setSunCount(cnt+getIntSunCount());
    }
    public JLabel getSunCount() {
        return this.sunCount;
    }
    public int getIntSunCount(){
        return Integer.parseInt(this.sunCount.getText());
    }

    public Map<String, Plant> getPlantMap(){
        return plantMap;
    }

    public Point getP() {
        return topPanel.getMousePosition();
    }

    public void setP(Point p) {
        this.mouseP = p;
    }

	public JPanel getTopPanel() {
		return topPanel;
	}

	public void setPreImg(ImageIcon preImg) {
        this.preImg=preImg;
	}

	public void setBlurImg(ImageIcon blurImg) {
        this.blurImg=blurImg;
    }
    
    public void setCard(Card card){
        this.card=card;
    }

    public Card getCard(){
        return card;
    }

    public boolean haveZombie(int row){
        for(Zombie zombie: Zombies.get(row)){
            if(zombie.getXPos()<720){
                return true;
            }
        }
        return false;
    }

    //鼠标移动效果，选中的植物
    class TopPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        Graphics2D g2;

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g2 = (Graphics2D) g;
            Point mouseP = getP();
            if (mouseP != null) {
                int c = (mouseP.x - 40) / 80;
                int r = (mouseP.y - 90) / 100;
                if (isOnGrass(r, c) && plants[r][c] == null) {
                    g2.drawImage(blurImg.getImage(), 40 + c * 80, 90 + r * 100, this);
                }
                g2.drawImage(preImg.getImage(), mouseP.x - 35, mouseP.y - 40, 
                             preImg.getIconWidth(),
                             preImg.getIconWidth(), this);
            }
        }

        public void update(Graphics g) {
            Image offScreenImage = this.createImage(800, 600);
            Graphics gImage = offScreenImage.getGraphics();
            paint(gImage);
            if (mouseP != null) {
                g.drawImage(offScreenImage, mouseP.x - 35, mouseP.y - 40, null);
            }
        }
    }

    public void setCardMap() {
        plantMap.put("SunFlower", new Plant().SunFlower());
        plantMap.put("PeaShooter", new Plant().PeaShooter());
        plantMap.put("Repeater", new Plant().Repeater());
    }

    public void addCard(Card card){
        assert(cardNum<10);
        Cards[cardNum]=card;
        card.setIndex(cardNum);
        new Thread(card).start();
        cardNum++;
    }

    public void plantDeath(int row, int column) {
        if (plants[row][column] != null) {
            System.out.println("Error occurred:deleted a non-exist plant from (" + row + "," + column + ")");
        }
        //System.out.println(plants[row][column].getName() + " is dead at(" + row + "," + column + ")");
        plants[row][column] = null;
    }

    private boolean isOnGrass(int r, int c) {// (r,c)->grass
        return r >= 0 && r < numRow && c >= 0 && c < numCol;
    }

    //鼠标选中的植物卡牌下标
    private int selectedIndex = -1;

    public void setSelectedIndex(int x){
        this.selectedIndex = x;
    }

    public int getSelectedIndex(){
        return selectedIndex;
    }

    class myMouseListener extends MouseAdapter implements MouseMotionListener {
        private TopPanel topPanel;

        myMouseListener(TopPanel topPanel) {
            this.topPanel = topPanel;
        }

        public void mouseClicked(MouseEvent e) {
            Point p = getP();
            if(p==null)return;

            for(int i=0;i<cardNum;i++){
                if((Cards[i].getRectangle()).contains(p)){
                    return;
                }
            }

            int x=p.x,y=p.y;
            int grassR=(y-cardHeight)/tileHeight,grassC=(x-cardWidth)/tileWidth;
            //种植
            if(nowPlant!=null&&isOnGrass(grassR,grassC)&&plants[grassR][grassC]==null){
                plant(grassR,grassC,nowPlant);
                int sun=getIntSunCount();
                sun-=nowPlant.getPrice();
                setSunCount(sun);
            }else{
                //取消选中
                if(selectedIndex!=-1){
                    Cards[selectedIndex].setInCooling(false);
                    Cards[selectedIndex].check(getIntSunCount());
                }
            }
            selectedIndex=-1;
            topPanel.setVisible(false);
            nowPlant=null;
        }
        public void mouseMoved(MouseEvent e){
            setP(e.getPoint());
            topPanel.repaint();
        }
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseDragged(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

    public void plant(int r,int c,Plant plant){
        plant.setController(this);
        plant.setVisible(true);
        plant.setBounds(45+c*80,90+r*100,300,300);
        plant.setPos(r, c);
        layeredPane.add(plant,new Integer(50));
        plants[r][c]=plant;
        new Thread(plant).start();
    }

    public boolean findZombie(int row,int column){
        return false;//TO-DO
    }

    public void checkCards(){
        for(int i=0;i<cardNum;i++){
            Cards[i].check(getIntSunCount());
        }
    }
    
    public Controller(){
        setCardMap();
        for (int i=0 ; i<5 ; i++) {
            Zombies.add(new ArrayList<>());
        }
        cardNum=0;
        this.setSunCount(500);
        this.topPanel.addMouseMotionListener(new myMouseListener(this.topPanel));
        this.topPanel.addMouseListener(new myMouseListener(this.topPanel));
        this.topPanel.setVisible(false);
        isRunning=true;
        for(int i=0;i<cardNum;i++){
            Cards[i].setInCooling(false);
        }
    }
}