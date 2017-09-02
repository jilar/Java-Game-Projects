/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RainbowReef;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import myGames.GameEvents;
import myGames.GameSpace;
import myGames.PlayerParent;
import myGames.Thing;

/**
 *
 * @author Jeffrey Ilar
 */
 public class Katch extends PlayerParent
    {
        private int startx, starty, spawnDelay;
        private int mercyTimer;
        private ArrayList<Pop> things;
        private GameSpace screen;
        private double angle;
 
        
        public Katch(int x, int y, double direction, int speed, Image[] img,
                GameEvents events, int maxdamage, int damageto, int eps, 
                int left, int right, int up, int down, int fire, int spfire,
                int shotTime, int fastShotTime, int deadTime,ArrayList<Pop> things,GameSpace screen)
        {
            super(x, y, direction, speed, img, events, maxdamage, damageto, eps,
                    left, right, up, down, fire, spfire, shotTime, fastShotTime,
                    deadTime);
            startx = x;
            starty = y;
            spawnDelay = 30;
            mercyTimer = 20;
            this.things=things;
            this.screen=screen;
        }

        //moves based on the keys pressed, but only with the basic update
        @Override
        public void move()
        {
            if(getMvRight())
            {
                setImgTick(23); // Eyes of Pop look right (very subtle change)
                  if (getX() + getSpeed() < screen.getWidth()-50){
                    changeX(getSpeed());
                  if (!things.get(0).getMoving()){
                    things.get(0).changeX(getSpeed());
                  }
                }             
                  
            }
            
            if(getMvLeft())
            {
                setImgTick(0); // Eyes of Pop look left  (very sublte change)
                if (getX() - getSpeed() > 50)
                {
                    changeX(-getSpeed());
                    if (!things.get(0).getMoving()){
                    things.get(0).changeX(-getSpeed());
                    }
                } 

            }
            
            if(getMvUp())
            {
                if(!things.get(0).getMoving()&&angle<=80){
                  angle++;
                  things.get(0).setDirection(-Math.toRadians(angle));
                }  
            }
            
            if(getMvDown())
            {
                if(!things.get(0).getMoving()&&angle>=-80){
                  angle--;
                  things.get(0).setDirection(-Math.toRadians(angle));
                }  

            }


            if(getShotDelay() > 0)
            {
                changeShotDelay(-1);
            }
        }
        
        //shoots with button pressed, but only with the basic update
        @Override
        public void action()
        {
            if(getIsFiring())
            {
                if(getShotDelay() == 0)
                {
                    if(!things.get(0).getMoving())
                    {
                     things.get(0).setSpeed(-13);
                     things.get(0).setTimer(0);
                     things.get(0).setMoving(true);
                   
                    }
                    
                    setShotDelay(getShotTime());
                }
            }
            else
            {
                if(getShotDelay() > getFastShotTime())
                {
                    setShotDelay(getFastShotTime());
                }
            }
        }

        @Override
        public void dead()
        {   
            
            setDeadTimer(getDeadTimer() + 1);
            
            if(getDeadTimer() == getDeadTime() + spawnDelay)
            {
                    setX(startx);
                    setY(starty);
                    setDone(false);
                    setDeadTimer(0);
                    setDamage(0);
                    mercyTimer = 30;
                    setPowerTimer(0);
                
            }
        }
        

        
        @Override
        public boolean collision(int x, int y, int w, int h)
        {

                return super.collision(x, y, w, h);
        }

        @Override
        public void hitMe(Thing t)
        {
            t.itHit(this);
        }
    }