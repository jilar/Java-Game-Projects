/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame;

/**
 *
 * @author Jeffrey Ilar
 */
import java.awt.Image;
import java.util.ArrayList;
import myGames.*;
/**
 *
 * @author Jeffrey Ilar
 */
public class StrongBullet extends NotUnit{
    private int type=4;
    
    public StrongBullet(int x, int y, double direction, int speed, Image[] img,
            GameEvents events, int source,int source2, ArrayList ev)
    {
        super(x, y, direction, speed, img, events, source,source2, ev);
    }
    
    @Override
    public void itHit(Unit u)
    {
        PlayerParent p = (PlayerParent) u;
        p.setPowerTimer(0);   
        p.setPower(type);
        p.setAmmo(25);
            
        setDone(true);
    }
    
}