/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RainbowReef;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import myGames.Bullet;
import myGames.GameEvents;
import myGames.Unit;

/**
 *
 * @author Jeffrey Ilar
 */
public class Pop extends Bullet{
    Random random=new Random();
        private boolean moving;
        private int timer,sCounter;
        private double lastDirection;
        boolean col=false;
        
        
        public Pop(int x, int y, double direction, int speed, Image[] img,
        GameEvents events, int source,int source2, ArrayList ev, int damageto){
        super(x, y, direction, speed, img, events, source,source2,  ev,damageto);
        }
        
        public void move(){    
        plusImgTick();
        if (getImgTick()>44){
            setImgTick(0);
        }
        timer++;
        Unit temp;
        int i = 1;
        changeX((int)(getSpeed() * Math.sin(getDirection())));
        if(moving==true){
            changeY((int)((getSpeed() * (Math.cos(getDirection())))+0.007*timer*timer));
        }
        Iterator<ArrayList> it = getTargets().listIterator(1);
        while (it.hasNext())
        {
            Iterator<Unit> it2 = it.next().listIterator();
            if(i != getSource() && i!=getSource2())
            {
                while (it2.hasNext())
                {
                    temp = it2.next();
                    if(temp.collision(getX(), getY(), getWidth(), getHeight()))
                    {
                        getEvents().setCollision(this, temp);
                        break;
                    }
                }
            }
            i++;
          
        }
        }
        public void itHit(Unit u){
            
            if(getDirection()<0){
                setDirection(getDirection()+Math.toRadians(360));
            }
            setDirection(2*Math.PI-getDirection());
                   
            u.changeDamage(getDamageTo());
            if(getY()>=u.getY()+u.getHeight()-5 && getX()+getWidth()/2>u.getX()&& getX()+getWidth()/2<u.getX()+u.getWidth()){
            
                    double newDirection=Math.toRadians(180)-getDirection();
                    setDirection(newDirection);
                   
                
                    timer=0;
                    
            }else{
              if(getY()<50) {
              
                double newDirection=Math.toRadians(180)-getDirection();
                 int  x = random.nextInt(1)+1;
                 if (x%2==0){
                    setDirection(newDirection);
                 }else{
                     setDirection(-newDirection);
                 }   
                timer=0;
                
            }else{    
                if (u.getClass()==Katch.class){
 
                 int  n = random.nextInt(5);
                 timer=n;

                        if(getX()+getWidth()/2 < u.getX()+u.getWidth()/2){                        //over half of pop on left side of Katch
                             setDirection(Math.toRadians(30));
                        }else{                                                                    //over half of pop on right side of Katch
                            setDirection(Math.toRadians(330));
                            
                        }
                 }else {timer=20;
                
                 if(getX()+getWidth()/2 < u.getX()+u.getWidth()/2 &&getY()+getHeight()<=u.getY()+60){ 
                      setDirection(Math.toRadians(40));
                      
                 }else if (getX()+getWidth()/2 >= u.getX()+u.getWidth()/2 &&getY()+getHeight()<=u.getY()+60){
                      setDirection(Math.toRadians(320));
                       
                 }
                              
                }

             }    
            }
        }
        public boolean getMoving(){
            return moving;
        }    
        
        public void setMoving(Boolean t){
            moving=t;
        }
        
        public void setTimer(int i){
            timer=i;
        }
    }