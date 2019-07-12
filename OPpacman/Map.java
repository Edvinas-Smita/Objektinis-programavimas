import greenfoot.World;
import greenfoot.Greenfoot;

/**
 *
 * 
 * @author Edvinas Smita - VU MIF PS 1k. 2gr.
 * 
 * 
 */

public class Map extends TileSystem
{
    private int currentMap = 1;
    public Map() {
        setupMap("map1.png");
        
        addMovers();
        
        addObject(pointCounter, 0, 0);
        addObject(timer, 0, 0);
    }
    
    public void reset() {
        removeObjects(getObjects(Player.class));
        removeObjects(getObjects(Ghost.class));
        
        Greenfoot.delay(50);
        addMovers();
        Greenfoot.delay(50);
    }
    public void nextMap() {
        removeObjects(getObjects(Player.class));
        removeObjects(getObjects(Ghost.class));
        removeObjects(getObjects(powerup.class));
        removeObjects(getObjects(point.class));
        
        if (++currentMap > 3) {
            victory();
        } else {
            setupMap("map" + currentMap + ".png");
            addMovers();
            Greenfoot.delay(200);
        }
    }
    
    private void addMovers() {
        addObject(player, 285, 345);
        
        addObject(ghost1, 285, 285);
        addObject(ghost2, 255, 285);
        addObject(ghost3, 315, 285);
        addObject(ghost4, 285, 225);
    }
    
    private void victory() {
        setBackground("victory.png");
        pointCounter.victoryScreen();
        Greenfoot.stop();
    }
    
    public void dedForReal() {
        removeObjects(getObjects(Player.class));
        removeObjects(getObjects(Ghost.class));
        removeObjects(getObjects(powerup.class));
        removeObjects(getObjects(point.class));
        
        setBackground("fail.png");
        
        pointCounter.dedScreen();
    }
}
