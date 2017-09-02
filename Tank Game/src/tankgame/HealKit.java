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
public class HealKit extends NotUnit{
    private int type=5;
    
    public HealKit(int x, int y, double direction, int speed, Image[] img,
            GameEvents events, int source,int source2, ArrayList ev)
    {
        super(x, y, direction, speed, img, events, source,source2, ev);
    }
    
    @Override
    public void itHit(Unit u)
    {
        PlayerParent p = (PlayerParent) u;
        p.setBuff(true);
        setDone(true);
    }
    
}