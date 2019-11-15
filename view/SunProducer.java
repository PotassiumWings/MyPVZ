package view;

import controller.*;

public class SunProducer implements Runnable{
    private Controller controller;
    
    SunProducer(Controller controller){
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (controller.isRunning){
            try {
                Thread.sleep((int) (Math.random()*1000)+5000);
                //5~6s随机生成一个
                new Thread(new Sun(controller)).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Thread.currentThread().interrupt();
    }
}