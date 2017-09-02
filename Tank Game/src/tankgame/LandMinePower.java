package tankgame;

import java.awt.Image;
import java.util.ArrayList;
import myGames.*;
/**
 *
 * @author Jeffrey Ilar
 */
public class LandMinePower extends NotUnit{
    private int type=3;
    
    public LandMinePower(int x, int y, double direction, int speed, Image[] img,
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
        p.setAmmo(5);
            
        setDone(true);
    }
    
}

