package tankgame;

import java.awt.Image;
import java.util.ArrayList;
import myGames.*;
/**
 *
 * @author Jeffrey Ilar
 */
public class GhostBullet extends NotUnit{
    private int type=2;
    
    public GhostBullet(int x, int y, double direction, int speed, Image[] img,
            GameEvents events, int source, int source2, ArrayList ev)
    {
        super(x, y, direction, speed, img, events, source,source2, ev);
    }
    
    @Override
    public void itHit(Unit u)
    {
        PlayerParent p = (PlayerParent) u;
        p.setPowerTimer(0);   
        p.setAmmo(30);
        p.setPower(type);            
        setDone(true);
    }
    
}

