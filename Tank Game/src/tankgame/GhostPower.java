package tankgame;

import java.awt.Image;
import java.util.ArrayList;
import myGames.*;
/**
 *
 * @author Jeffrey Ilar
 */
public class GhostPower extends NotUnit{
    private int type=1;
    
    public GhostPower(int x, int y, double direction, int speed, Image[] img,
            GameEvents events, int source,int source2, ArrayList ev)
    {
        super(x, y, direction, speed, img, events, source,source2, ev);
    }
    
    @Override
    public void itHit(Unit u)
    {
        PlayerParent p = (PlayerParent) u;
        p.setPower(type);
        p.setPowerTimer(500);
            
        setDone(true);
    }
    
}

