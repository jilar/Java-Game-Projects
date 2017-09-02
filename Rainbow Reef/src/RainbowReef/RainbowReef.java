package RainbowReef;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
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
public class RainbowReef extends Game
{
    private GameSpace screen;
    
    private ArrayList<ArrayList> everything;
    private ArrayList<Pop> things;
    private ArrayList<PlayerParent> player1;
    private ArrayList<Wall> Walls;
    private Image[] enemy[], playerimg[],wall[], 
            bullet[],life,win,lose;
   
    private GameEvents events;
    private boolean gameover;
    private boolean destroy = false;
    private int[] dataToPass={0,2,0};                                              //score,lives,and big legs
    private URL[] Sounds;
    private int gamescreen;                
    private int angle=0;

    //creates and adds all the game panel to the applet
    //also sets up images, sounds, and creates and initializes state for most
    //variables and objects.
    @Override
    public void init()
    {      
        super.init();
        screen = new GameSpace(getSprite("Resources/Background1.png"), new DrawAbs());

        

        add(screen, BorderLayout.CENTER);
        setBackground(Color.white);

        everything = new ArrayList<ArrayList>();
        things = new ArrayList<Pop>();
        everything.add(things);
       
        player1 = new ArrayList<PlayerParent>();
        everything.add(player1);
         Walls = new ArrayList<Wall>();
        everything.add(Walls);
        

        events = new GameEvents();
        MapSetup("Reef1.txt");
        things.add(new Pop(310,360-30, Math.toRadians(0), 0, bullet[0], events,        //check
                                0,0, everything, 1));
       
        
        KeyControl keys = new KeyControl(events);
        addKeyListener(keys);
        
        player1.add(new Katch(300, 360, 0, 13, playerimg[0],
                            events, 9999, 0, 3, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_SPACE,
                            KeyEvent.VK_DELETE, 10, 5, 0,things,screen));
        
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
            playerimg = new Image[1][24];
            Image img=getSprite("Resources/Katch_strip24.png");
            BufferedImage bimg=convertToBuffered(img);
            for (int i = 0; i < 24; i++)
            {
                playerimg[0][i] = bimg.getSubimage((i*80),0,80,30);
            }
            wall=new Image[8][1];
            wall[0][0]=getSprite("Resources/Wall.png");
            for (int i = 1; i < 8; i++)
            {
                wall[i][0]=getSprite("Resources/Block"+i+".png");
            }
            
            bullet = new Image[1][45];
            img=getSprite("Resources/Pop_strip45.png");
            bimg=convertToBuffered(img);
            for (int i = 0; i < 45; i++){
                bullet[0][i]=bimg.getSubimage((35*i), 0, 35, 35);
            }
            enemy = new Image[2][24];
            img=getSprite("Resources/Bigleg_small_strip24.png");
            bimg=convertToBuffered(img);
            for (int i = 0; i < 24; i++){
                enemy[0][i]=bimg.getSubimage((40*i), 0, 40, 40);
            }
            img=getSprite("Resources/Bigleg_strip24.png");
            bimg=convertToBuffered(img);
            for (int i = 0; i < 24; i++){
                enemy[1][i]=bimg.getSubimage((80*i), 0, 80, 80);
            }
            
            life=new Image[1];
            life[0]=getSprite("Resources/Katch_small.png");
            win=new Image[1];
            win[0]=getSprite("Resources/Congratulations.png");
            lose= new Image[1];
            lose[0]=getSprite("Resources/gameOver.png");
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
        URL musicu = RainbowReef.class.getResource("Resources/Music.mid");
        Sounds = new URL[2];
        Sounds[0] = RainbowReef.class.getResource("Resources/Sound_block.wav");
        Sounds[1] = RainbowReef.class.getResource("Resources/Sound_bigleg.wav");
        
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
        
        int locx=things.get(0).getX();
        int locy=things.get(0).getY();
        
        
        Rectangle rectangle = new Rectangle(locx,locy-40,3,50);
        AffineTransform trans = new AffineTransform();
        trans.rotate(Math.toRadians(angle), rectangle.getX() + rectangle.width/2, rectangle.getY() + rectangle.height/2);
        Shape transformed = trans.createTransformedShape(rectangle);
        
        if(player1.get(0).getMvUp()&&!things.get(0).getMoving()){
            g2.fill(transformed);
            if (angle<=80){
            angle++;
            }
        }else if(player1.get(0).getMvDown()&&!things.get(0).getMoving()){
            g2.fill(transformed);
            if (angle>=-80){
            angle--;
            }
        }
        
        for(int i =0;i<dataToPass[1];i++){
            g2.drawImage(life[0], 25+(i*40), 380, null);
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
            
            destroy = false;
           
            events.deleteObservers();
            this.requestFocus();
        }
        
        if (things.get(0).getY()>400 ||things.get(0).getY()<0||things.get(0).getX()>600||things.get(0).getX()<0){
            if(things.get(0).getY()>400){
            dataToPass[1]--;
            }
            things.get(0).setMoving(false);
            things.get(0).setSpeed(0);
            things.get(0).setImgTick(0);
            things.get(0).setX(player1.get(0).getX());
            things.get(0).setY(player1.get(0).getY()-30);    
            things.get(0).setDirection(0);
        }
        
        if(dataToPass[1]<0){
            g2.drawImage(lose[0], 170, 150, null);
        }
        
        if(dataToPass[2]==0 && !gameover){
            
            Walls.clear();
            MapSetup("Reef2.txt");
            player1.get(0).setX(300); 
            things.get(0).setMoving(false);
            things.get(0).setSpeed(0);
            things.get(0).setImgTick(0);
            things.get(0).setX(player1.get(0).getX());
            things.get(0).setY(player1.get(0).getY()-30);    
            things.get(0).setDirection(0);
            things.get(0).setX(player1.get(0).getX());
            things.get(0).setY(player1.get(0).getY()-30); 
            gamescreen++;
           
        }
        
        if (gamescreen==2){
            g2.drawImage(win[0], 0, 0, null);
        }
        
        if(dataToPass[1]<0 || gamescreen==2){
            gameover=true;
        }
        
        String s=String.valueOf(dataToPass[0]);
        if (!gameover){
            g2.drawString("Score:"+s, 450 , 400);
        }else {
            g2.setFont(new Font("TimesRoman", Font.PLAIN, 50));
             g2.drawString("Score:"+s, 250 , 100);
        }
    }
    
   
    public void MapSetup(String filename) {
        File file;
        FileInputStream fis;
        BufferedReader reader;
        String line;
        int l=0;
        try {			
            file = new File(filename);
            fis = new FileInputStream(file);
            reader = new BufferedReader( new FileReader(file));
            while ((line=reader.readLine())!=null) {
                for(int i=0;i<line.length();i++){
                    char item=line.charAt(i);
                    if(item=='W'){
                       Walls.add(new Wall(i*20+10,l*20+10,0,0,wall[0],events,9999,0,0,null,0,dataToPass));  
//                    
                    }else if(item=='o'){
                       Walls.add(new Wall(i*20+20,l*20+20,0,0,enemy[0],events,1,0,0,Sounds[1],300,dataToPass));
                       dataToPass[2]++;
                    }else if(item=='O'){
                       Walls.add(new Wall(i*20+20,l*20+10,0,0,enemy[1],events,1,0,5,Sounds[1],1000,dataToPass));
                       dataToPass[2]++;
                    }
                    else if(item=='w'){
                        Random random=new Random();
                        int  n = random.nextInt(7)+1;
                       Walls.add(new Wall(i*20+20,l*20+10,0,0,wall[n],events,1,0,0,Sounds[0],100,dataToPass));
                    }
                }
                l++;
            }
        } catch (Exception e) {
                System.out.println("Could not find BattleGround.txt");
        }
        
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