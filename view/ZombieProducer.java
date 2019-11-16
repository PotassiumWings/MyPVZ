package view;

import controller.*;

public class ZombieProducer implements Runnable{
    private Controller controller;
    
    ZombieProducer(Controller controller){
        this.controller = controller;
    }

    public int count = 1;

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
            //开局20s无生成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (controller.isRunning){
            try {
                Thread.sleep((int) (Math.random() * 250) + 5000 );
                int row = (int) (Math.random() * 5);
                Zombie tempZombie = new Zombie().normalZombie(controller, row);
                //if(Math.random()>=0.5)tempZombie = new Zombie().normalZombie(controller, row);
                //else tempZombie = new Zombie().xsyZombie(controller, row);
                controller.addZombie(tempZombie, row);
                new Thread(tempZombie).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Thread.currentThread().interrupt();
    }
}