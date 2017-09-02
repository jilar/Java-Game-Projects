/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RainbowReef;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.Observable;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import myGames.GameEvents;
import myGames.Thing;
import myGames.Unit;

/**
 *
 * @author Jeffrey Ilar
 */
public class Wall extends Unit{
          //int deadTimer;
          //int spawning;
         // int spawnDelay=30;
        private URL snd;
        private int scoreValue;
        private int[] dataToPass;
        public Wall(int x, int y, double direction, int speed, Image[] img,
                GameEvents events, int maxdamage, int damageto,
                int eps,URL snd, int scoreValue, int[] dataToPass)
        {
            super(x, y, direction, speed, img, events, maxdamage, damageto, eps);
            this.snd=snd;
            this.scoreValue=scoreValue;
            this.dataToPass=dataToPass;
        }

        @Override
        public void hitMe(Thing t)
        {
            t.itHit(this);
            if (snd!=null){
                try
                {
                AudioInputStream explSound;
                Clip clip;
                explSound = AudioSystem.getAudioInputStream(snd);
                clip = AudioSystem.getClip();
                clip.open(explSound);
                clip.start();
                 }
                catch(Exception e)
                 {
                    System.out.println("Error in explosion sound: " + e.getMessage());
                }
            }    
            dataToPass[0]+=scoreValue;
            if (scoreValue>100){
                dataToPass[2]--;
            }
            
        }

        @Override
        public void itHit(Unit u)
        {
            u.changeDamage(getDamageTo());
            setDone(true);
        }

        @Override
        public void move()
        {
            if(getDamage() >= getMax())
            {
                setDone(true);
            }
            if(getImgTick()<getImageSet().length-1){
                plusImgTick();
            }
            else{
                setImgTick(0);
            }            
//            if (spawning>0){
//                spawning--;
//            }
        }

        @Override
        public void update(Observable o, Object arg)
        {
            GameEvents ev = (GameEvents) arg;
            
            if(ev.getType() == 1)
            {
                if(ev.getTarget() == this)
                {
                    hitMe((Thing)ev.getCaller());
                }
            }
        }

        //explodes when dead
        @Override
        public void dead()
        {
//             if(deadTimer == 0)
            {
//                things.add(new Explosion(getX(),getY(), smallexpl, getEvents(), 1,
//                    explsoundurl[0]));
            }
            
//            deadTimer++;
//            
//            if(deadTimer == 500)
//            {
//                    setDone(false);
//                    deadTimer=0;
//                    this.setDamage(0);
//                    spawning=50;
//            }
        }
        
//        public void draw(Graphics2D g2, ImageObserver obs)
//        {
//            if(spawning%2 == 0)
//            {
//                super.draw(g2, obs);
//            }
//        }
     }
