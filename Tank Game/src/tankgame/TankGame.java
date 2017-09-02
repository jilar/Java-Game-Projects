package tankgame;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.util.*;
import javax.sound.midi.*;
import javax.swing.*;
import myGames.*;

/**
 * 
 */
public class TankGame extends Game
{
    private GameSpace screen;
    
    private ArrayList<ArrayList> everything;
    private ArrayList<Thing> things;
    private ArrayList<PlayerParent> player1;
    private ArrayList<PlayerParent> player2;
    private ArrayList<Wall> Walls;
    final private int startPoint = -30;
    final private int backgroundspeed = 0;
    private Image[] enemy[], playerimg[],wall[], boss, powerup[],
            smallexpl, largeexpl,bullet[];
    private GameController gcontroller;
    private GameEvents events;
    private boolean gameover;
    private boolean destroy = false;
    private boolean twoplayers = true;
    private boolean isBoss = false;
    private int score1,score2;
    private URL[] explsoundurl;

    //creates and adds all the game panel to the applet
    //also sets up images, sounds, and creates and initializes state for most
    //variables and objects.
    @Override
    public void init()
    {      
        super.init();
        screen = new GameSpace(getSprite("Resources/Background.png"), new DrawAbs());
        screen.setBackSpeed(backgroundspeed);
        screen.setBackDirection(0.);
        

        add(screen, BorderLayout.CENTER);
        setBackground(Color.white);

        everything = new ArrayList<ArrayList>();
        things = new ArrayList<Thing>();
        everything.add(things);
        Walls = new ArrayList<Wall>();
        everything.add(Walls);
        player1 = new ArrayList<PlayerParent>();
        everything.add(player1);
        player2 = new ArrayList<PlayerParent>();
        everything.add(player2);
        

        events = new GameEvents();
        MapSetup();
       
        
        KeyControl keys = new KeyControl(events);
        addKeyListener(keys);
        
        gcontroller = new GameController();
        
        gameover = false;
        
        Object[] options = {"Okay!"};
        int n = JOptionPane.showOptionDialog(this, "Get ready for fun!", "Welcome",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);
        
    }
    
    //getting all image files
    @Override
    public void initImages()
    {
        try
        {
            playerimg = new Image[2][60];
            Image img=getSprite("Resources/Tank_blue_basic_strip60.png");
            BufferedImage bimg=convertToBuffered(img);
            for (int i = 0; i < 60; i++)
            {
                playerimg[0][i] = bimg.getSubimage((i*64),0,64,64);
            }
            img=getSprite("Resources/Tank_red_basic_strip60.png");
            bimg=convertToBuffered(img);
            for(int i = 0; i < 60; i++)
            {
                playerimg[1][i] =bimg.getSubimage((i*64),0,64,64);
            }
            wall=new Image[2][1];
            wall[0][0]=getSprite("Resources/Blue_wall1.png");
            wall[1][0]=getSprite("Resources/Blue_wall2.png");
            powerup = new Image[5][1];
            img=getSprite("Resources/Ball_strip9.png");
            bimg=convertToBuffered(img);
            for(int i = 0; i <5; i++)
            {
                powerup[i][0] =bimg.getSubimage(116+29*i,0,29,29);
            }
            smallexpl = new Image[6];
            for (int i = 0; i < 6; i++)
            {
                smallexpl[i] = getSprite("Resources/explosion1_" + (i + 1) + ".png");
            }
            largeexpl = new Image[7];
            for (int i = 0; i < 7; i++)
            {
                largeexpl[i] = getSprite("Resources/explosion2_" + (i + 1) + ".png");
            }
            bullet = new Image[4][1];
            img=getSprite("Resources/Shell_light_strip60.png");
            bimg=convertToBuffered(img);
            bullet[0][0] = bimg.getSubimage(336,0,24,24);
            bullet[1][0] =getSprite("Resources/GhostBullet.png");
            bullet[2][0] =getSprite("Resources/explosion1_2.png");
            img=getSprite("Resources/Shell_heavy_strip60.png");
            bimg=convertToBuffered(img);
            bullet[3][0] =bimg.getSubimage(336,0,24,24);
            
        } catch (Exception e)
        {
            System.out.println("Error in getting images: " + e.getMessage());
        }
    }
    
    //getting all sound files
    @Override
    public void initSound()
    {
        try
        {
        Sequence music;
        Sequencer seq;
        URL musicu = TankGame.class.getResource("Resources/attack.mid");
        explsoundurl = new URL[2];
        explsoundurl[0] = TankGame.class.getResource("Resources/snd_explosion1.wav");
        explsoundurl[1] = TankGame.class.getResource("Resources/snd_explosion2.wav");
        
           music =  MidiSystem.getSequence(musicu);
           seq = MidiSystem.getSequencer();
           seq.open();
           seq.setSequence(music);
           seq.start();
           seq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        }
        catch(Exception e)
        {
            System.out.println("Error in midi: " + e.getMessage());
        }
    }



    //this creates Things when needed to make the gameplay pattern
    public class GameController
    {

        private int timer;

        public GameController()
        {
            timer = 0;
        }

        public void timeline()
        {
            switch (timer)
            {
                case 0:
                    
                    if(twoplayers)
                    {
                        player1.add(new Tank1(150, 150, 0, 6, playerimg[0],
                            events, 10, 0, 22, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_SHIFT,
                            KeyEvent.VK_DELETE, 10, 5, largeexpl.length));
                        player2.add(new Tank2(874,615, 0., 6, playerimg[1],
                            events, 10, 0, 22, KeyEvent.VK_A, KeyEvent.VK_D,
                            KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_SPACE,
                            KeyEvent.VK_SHIFT, 10, 5, largeexpl.length));
                        player2.get(0).setImgTick(30);
                    }
                    
                    break;
            }
            
            
            timer++;
        }

        public void resetTimer()
        {
            timer = 0;
        }
    }
  
    public class Tank1 extends PlayerParent
    {
        private int startx, starty, spawnDelay;
        private int mercyTimer;
        
        public Tank1(int x, int y, double direction, int speed, Image[] img,
                GameEvents events, int maxdamage, int damageto, int eps, 
                int left, int right, int up, int down, int fire, int spfire,
                int shotTime, int fastShotTime, int deadTime)
        {
            super(x, y, direction, speed, img, events, maxdamage, damageto, eps,
                    left, right, up, down, fire, spfire, shotTime, fastShotTime,
                    deadTime);
            startx = x;
            starty = y;
            spawnDelay = 30;
            mercyTimer = 20;
        }

        //moves based on the keys pressed, but only with the basic update
        @Override
        public void move()
        {
            if(getMvRight())
            {
                    if(getImgTick()>0){
                        minusImgTick();
                    }else{
                        setImgTick(59);
                    }
            }
            
            if(getMvLeft())
            {
                if (getX() + getSpeed() > 0)
                {
                    if(getImgTick()<59){
                        plusImgTick();
                    }else{
                        setImgTick(0);
                    }
                    
                }
            }
            
            if(getMvUp())
            {
                double radian = (getImgTick() * 6 * Math.PI / 180);
                int x = (int) (getSpeed() * Math.cos(radian));
                int y = (int) (getSpeed() * Math.sin(radian));
                changeY(-y);
                changeX(x);
                if(getPower()!=1){
                    Wall temp;
                    Iterator<Wall> it = Walls.listIterator();
                    while (it.hasNext()){
                        temp = it.next();
                        if (temp.collision(getX(), getY(), getWidth(), getHeight()))
                        {
                            changeY(y);
                            changeX(-x);
                        }
                    }
                }
                 if (player2.get(0).collision(getX(), getY(), getWidth(), getHeight()))
                    {
                        changeY(y);
                        changeX(-x);
                    }

            }
            
            if(getMvDown())
            {
                double radian = (getImgTick() * 6 * Math.PI / 180);
                int x = (int) (getSpeed() * Math.cos(radian));
                int y = (int) (getSpeed() * Math.sin(radian));
                changeY(y);
                changeX(-x);
                
                if(getPower()!=1){
                    Wall temp;
                    Iterator<Wall> it = Walls.listIterator();
                    while (it.hasNext()){
                    temp = it.next();
                        if (temp.collision(getX(), getY(), getWidth(), getHeight())){
                            
                            changeY(-y);
                            changeX(x);
                            }
                    }
                }
                if (player2.get(0).collision(getX(), getY(), getWidth(), getHeight())){
                    changeY(-y);
                    changeX(x);
                }
            }
            
            if (getDamage() >= getMax())
            {
                if(getPower() > 0)
                {
                    setPower(0);
                }
                score2++;
                setDone(true);
            }

            if(getShotDelay() > 0)
            {
                changeShotDelay(-1);
            }
            
            if(mercyTimer > 0)
            {
                mercyTimer--;
            }
            
            if(getPowerTimer()>0){
                decPowerTimer();
                if(getPowerTimer()==0){
                    setPower(0);
                }
            }
            if(getBuff()==true){
                setBuff(false);
                setDamage(0);        
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
                double radian= (getImgTick()*6*Math.PI/180);  
                    if(getPower() <= 1)
                    {
                        things.add(new Bullet(getX(),getY()-5, radian-1.5708, -10, bullet[0], events,        //check
                                2,2, everything, 1));
                    
                    }
                    else if(getPower() == 2)
                    {
                        things.add(new Bullet(getX(),getY()-5, radian-1.5708, -10, bullet[1], events,
                                2,1, everything, 1));
                        decAmmo();
                         if(getAmmo()==0){
                             setPower(0);
                         }
                    }
                    else if(getPower() == 3)
                    {
                         things.add(new Bullet(getX(),getY()-5, 0, 0, bullet[2], events,
                                2,2, everything, 4));
                         decAmmo();
                         if(getAmmo()==0){
                             setPower(0);
                         }
                    }
                     else if(getPower() == 4)
                    {
                          things.add(new Bullet(getX(),getY()-5, radian-1.5708, -10, bullet[3], events,
                                2,2, everything, 3));
                         decAmmo();
                         if(getAmmo()==0){
                             setPower(0);
                         }
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
            if(getDeadTimer() == 0)
            {
                things.add(new Explosion(getX(), getY(), largeexpl, events, 2,
                        explsoundurl[1]));
            }
            
            setDeadTimer(getDeadTimer() + 1);
            
            if(getDeadTimer() == getDeadTime() + spawnDelay)
            {
                    setX(startx);
                    setY(starty);
                    setDone(false);
                    setDeadTimer(0);
                    setDamage(0);
                    mercyTimer = 30;
                
            }
        }
        
        @Override
        public void draw(Graphics2D g2, ImageObserver obs)
        {
            if(mercyTimer%2 == 0 ^ getPowerTimer()%2!=0)
            {
                super.draw(g2, obs);
            }
        }
        
        @Override
        public boolean collision(int x, int y, int w, int h)
        {
            if(mercyTimer == 0)
            {
                return super.collision(x, y, w, h);
            }
            
            return false;
        }

        @Override
        public void hitMe(Thing t)
        {
            t.itHit(this);
        }
    }
    
     public class Tank2 extends PlayerParent
    {
        private int startx, starty, spawnDelay;
        private int mercyTimer;
        
        public Tank2(int x, int y, double direction, int speed, Image[] img,
                GameEvents events, int maxdamage, int damageto, int eps, 
                int left, int right, int up, int down, int fire, int spfire,
                int shotTime, int fastShotTime, int deadTime)
        {
            super(x, y, direction, speed, img, events, maxdamage, damageto, eps,
                    left, right, up, down, fire, spfire, shotTime, fastShotTime,
                    deadTime);
            startx = x;
            starty = y;
            spawnDelay = 30;
            mercyTimer = 20;
        }

        //moves based on the keys pressed, but only with the basic update
        @Override
        public void move()
        {
            if(getMvRight())
            {{
                    if(getImgTick()>0){
                        minusImgTick();
                    }else{
                        setImgTick(59);
                    }
            
                }
            }
            
            if(getMvLeft())
            {
                if (getX() + getSpeed() > 0)
                {
                    if(getImgTick()<59){
                        plusImgTick();
                    }else{
                        setImgTick(0);
                    }
                    
                }
            }
            
            if(getMvUp())
            {
                double radian = (getImgTick() * 6 * Math.PI / 180);
                int x = (int) (getSpeed() * Math.cos(radian));
                int y = (int) (getSpeed() * Math.sin(radian));
                changeY(-y);
                changeX(x);
                
                if(getPower() != 1){
                    Wall temp;
                    Iterator<Wall> it = Walls.listIterator();
                    while (it.hasNext()){
                    temp = it.next();
                        if (temp.collision(getX(), getY(), getWidth(), getHeight())){
                        changeY(y);
                        changeX(-x);
                        }
                    }
                }
                
                if (player1.get(0).collision(getX(), getY(), getWidth(), getHeight())){
                        changeY(y);
                        changeX(-x);
                }
            }
            
            if(getMvDown())
            {
                double radian = (getImgTick() * 6 * Math.PI / 180);
                int x = (int) (getSpeed() * Math.cos(radian));
                int y = (int) (getSpeed() * Math.sin(radian));
                changeY(y);
                changeX(-x);
                
                if(getPower()!=1){
                    Wall temp;
                    Iterator<Wall> it = Walls.listIterator();
                    while (it.hasNext()){
                        temp = it.next();
                        if (temp.collision(getX(), getY(), getWidth(), getHeight()))
                        {
                            changeY(-y);
                            changeX(x);
                        }
                    }
                }
                if (player1.get(0).collision(getX(), getY(), getWidth(), getHeight()))
                    {
                        changeY(-y);
                        changeX(x);
                    }
            }
            
            if (getDamage() >= getMax())
            {
                score1++;
                if(getPower() > 0)
                {
                    setPower(0);
                }
                
                setDone(true);
            }

            if(getShotDelay() > 0)
            {
                changeShotDelay(-1);
            }
            
            if(mercyTimer > 0)
            {
                mercyTimer--;
            }
            if(getPowerTimer()>0){
                decPowerTimer();
                if(getPowerTimer()==0){
                    setPower(0);
                }
            }
            if(getBuff()==true){
                setBuff(false);
                setDamage(0);        
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
                double radian= (getImgTick()*6*Math.PI/180);    
                    if(getPower() <= 1)
                    {
                        things.add(new Bullet(getX(),getY()-5, radian-1.5708, -10, bullet[0], events,        //check
                                3,3, everything, 1));
                    }
                    else if(getPower() == 2)
                    {
                        things.add(new Bullet(getX(),getY()-5, radian-1.5708, -10, bullet[1], events,
                                3,1, everything, 1));
                        decAmmo();
                         if(getAmmo()==0){
                             setPower(0);
                         }
                    }
                    else if(getPower() == 3)
                    {
                         things.add(new Bullet(getX(),getY()-5, 0, 0, bullet[2], events,
                                3,3, everything, 3));
                         decAmmo();
                         if(getAmmo()==0){
                             setPower(0);
                         }
                    }
                    else if(getPower() == 4)
                    {
                          things.add(new Bullet(getX(),getY()-5, radian-1.5708, -10, bullet[3], events,
                                3,3, everything, 3));
                         decAmmo();
                         if(getAmmo()==0){
                             setPower(0);
                         }
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
            if(getDeadTimer() == 0)
            {
                things.add(new Explosion(getX(), getY(), largeexpl, events, 2,
                        explsoundurl[1]));
            }
            
            setDeadTimer(getDeadTimer() + 1);
            
            if(getDeadTimer() == getDeadTime() + spawnDelay)
            {
            
                    setX(startx);
                    setY(starty);
                    setDone(false);
                    setDeadTimer(0);
                    setDamage(0);
                    mercyTimer = 30;
                
            }
        }
        
        
        @Override
        public void draw(Graphics2D g2, ImageObserver obs)
        {
            if(mercyTimer%2 == 0 ^ getPowerTimer()%2!=0)
            {
                super.draw(g2, obs);
            }
        }
        
        @Override
        public boolean collision(int x, int y, int w, int h)
        {
            if(mercyTimer == 0)
            {
                return super.collision(x, y, w, h);
            }
            
            return false;
        }

       
        @Override
        public void hitMe(Thing t)
        {
            t.itHit(this);
        }
    }
    
     public class Wall extends Unit{
          int deadTimer;
          int spawning;
          int spawnDelay=30;
        public Wall(int x, int y, double direction, int speed, Image[] img,
                GameEvents events, int maxdamage, int damageto,
                int eps)
        {
            super(x, y, direction, speed, img, events, maxdamage, damageto, eps);
             int deadTimer;
            
        }

        @Override
        public void hitMe(Thing t)
        {
            t.itHit(this);
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
            if(getDamage() > getMax())
            {
                setDone(true);
            }
            if (spawning>0){
                spawning--;
            }
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
             if(deadTimer == 0)
            {
                things.add(new Explosion(getX(),getY(), smallexpl, getEvents(), 1,
                    explsoundurl[0]));
            }
            
            deadTimer++;
            
            if(deadTimer == 500)
            {
                    setDone(false);
                    deadTimer=0;
                    this.setDamage(0);
                    spawning=50;
            }
        }
        
        public void draw(Graphics2D g2, ImageObserver obs)
        {
            if(spawning%2 == 0)
            {
                super.draw(g2, obs);
            }
        }
     }
    
//    //adds an event type to GameEvents
//    public class PlaneEvents extends GameEvents
//    {
//
//        public void setBoss(int type)
//        {
//            setType(type);
//            setChanged();
//
//            notifyObservers(this);
//        }
//    }

    //Updates all Things and then draws everything
    //when the game is resetting, this method will also 
    @Override
    public void drawAll(int w, int h, Graphics2D g2)
    {
        Thing temp;
        screen.drawBackground(g2);
        Iterator<ArrayList> it = everything.listIterator();
        
        while (it.hasNext())
        {
            Iterator<Thing> it2 = it.next().listIterator();
            while (it2.hasNext())
            {
                if (gameover)
                {
                    break;
                }
                temp = it2.next();
                temp.update(w, h);
           
                if (temp.getRDone())
                {
                    it2.remove();
                }
            }
            if (gameover)
            {
                break;
            }
        }

        screen.drawHere(everything, g2);
 
        if(destroy)
        {
            it = everything.listIterator();
            while(it.hasNext())
            {
                Iterator<Thing> it2 = it.next().listIterator();
                while(it2.hasNext())
                {
                    it2.next();
                    it2.remove();
                }
                
            }
            
            gcontroller = new GameController();
            destroy = false;
           
            events.deleteObservers();
            this.requestFocus();
        }
        
        gcontroller.timeline();
    }
    
   
    public void MapSetup() {
        File file;
        FileInputStream fis;
        BufferedReader reader;
        String line;
        int l=0;
        try {			
            file = new File("Battleground.txt");
            fis = new FileInputStream(file);
            reader = new BufferedReader( new FileReader(file));
            while ((line=reader.readLine())!=null) {
                for(int i=0;i<line.length();i++){
                    char item=line.charAt(i);
                    if(item=='W'){
                       Walls.add(new Wall(i*32+16,l*32+16,0,0,wall[1],events,9999,0,5));
                    }else if(item=='w'){
                       Walls.add(new Wall(i*32+16,l*32+16,0,0,wall[0],events,2,0,5));
                    }else if(item=='b'){
                       things.add(new StrongBullet(i*32+16,l*32+16, 0., 0, powerup[4], events, 0,0, everything));
                    }
                    else if(item=='h'){
                       things.add(new HealKit(i*32+16,l*32+16, 0., 0, powerup[3], events, 0,0, everything));
                    }else if(item=='G'){
                       things.add(new GhostPower(i*32+16,l*32+16, 0., 0, powerup[0], events, 0,0, everything));
                    }else if(item=='m'){
                       things.add(new LandMinePower(i*32+16,l*32+16, 0., 0, powerup[2], events, 0,0, everything));
                    }else if(item=='g'){
                       things.add(new GhostBullet(i*32+16,l*32+16, 0., 0, powerup[1], events, 0,0, everything));
                    }
                    
                }
                l++;
            }
        } catch (Exception e) {
                System.out.println("Could not find BattleGround.txt");
        }
    }
    
    @Override
    public void paint(Graphics g)
    { 
        Graphics2D g2 = createGraphics2D(1028, 816);
        drawAll(1028, 816, g2);
            int remaining=player1.get(0).getDamage()*5;
            int locx=player1.get(0).getX();
            int locy=player1.get(0).getY();
            g2.setColor(Color.green);
            g2.fillRect(locx-25,locy+30,50-remaining,10);   
            g2.drawRect(locx-25,locy+30,50,10);
            String s=String.valueOf(score1);
            g2.drawString(s, locx-40 , locy-30);
            
            remaining=player2.get(0).getDamage()*5;
            locx=player2.get(0).getX();
            locy=player2.get(0).getY();
 
            g2.fillRect(locx-25,locy+30,50-remaining,10);   
            g2.drawRect(locx-25,locy+30,50,10);
            String s2=String.valueOf(score2);
            g2.drawString(s2, locx-40 , locy-30);
        g2.dispose();
        
       
       
        Point point=getDrawLoc(player1.get(0));
        Image screen1=bimg.getSubimage(point.x, point.y,295,480);
        Image screen1_1=bimg.getSubimage(point.x, point.y,295,312);
        Image screen1_2=convertToBuffered(screen1).getSubimage(0, 295,236,168);
        g.drawImage(screen1_1, 0, 0, this);
        g.drawImage(screen1_2, 0, 295, this);
        Point point2=getDrawLoc(player2.get(0));
        Image screen2=bimg.getSubimage(point2.x, point2.y,295,480);
        Image screen2_1=bimg.getSubimage(point2.x, point2.y,295,312);
        Image screen2_2=convertToBuffered(screen2).getSubimage(58, 295,236,168);
        g.drawImage(screen2_1, 306, 0, this);
        g.drawImage(screen2_2, 364, 295, this);
        g.setColor(Color.black);
        g.fillRect(295,0,10,312);   
        g.drawRect(295,0,10,312);
        Image mini=bimg.getScaledInstance(128, 102, BufferedImage.SCALE_SMOOTH);
        g.drawImage(mini, 236, 312, this);
        
        
    }
    
    public Point getDrawLoc(PlayerParent player){
        int x =player.getX()-300/2;
        int y= player.getY()-480/2;
        if(x<0){
            x=0;
        }else if((x+300)>1028){
            x=728;
        }
        if(y<0){
            y=0;
        }else if((y+480)>816){
            y=336;
        }
        return new Point(x,y);        
    }            
    
}