package view;

import controller.*;

public class ZombieProducer implements Runnable{
    private Controller controller;
    
    ZombieProducer(Controller controller){
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(20000);
            //开局20s无生成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (controller.isRunning){
            try {
                Thread.sleep((int) (Math.random()*1000)+1000);
                //7~8s随机生成一个
                int row=(int)(Math.random()*5);
                Zombie tempZombie=new Zombie().normalZombie(controller,row);
                controller.addZombie(tempZombie,row);
                new Thread(tempZombie).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Thread.currentThread().interrupt();
    }
}