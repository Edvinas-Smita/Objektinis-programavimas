import greenfoot.Actor;
import greenfoot.World;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

/**
 *
 * 
 * @author Edvinas Smita - VU MIF PS 1k. 2gr.
 * 
 * 
 */

public class Player extends Actor
{
    private int canKillForNCycles = 0;
    private int currentDirection = 0;
    private int mouthCycles = 0;
    private Boolean mouthIsOpen = true;
    
    private String lastKey;
    
    private Boolean stahp = false;
    
    private TileSystem tile;
    private Map map;
    public void addedToWorld(World world) {
        tile = (TileSystem)getWorld();
        map = (Map)getWorld();
    }
    
    public void act() 
    {
        rotate();
        moveIt();
        getPoints();
        if (stahp) {
            stahp = false;
            return;
        }
        getPowerups();
        interactWithGhosts();
    }
    
    private void moveIt() {
        if (tile.move(this, currentDirection, false)) {
            if (mouthIsOpen && mouthCycles == 15){
                setImage("PACclose.png");
                mouthCycles = 0;
                mouthIsOpen = false;
            }
            else if (!mouthIsOpen && mouthCycles == 15) {
                setImage("PACopen.png");
                mouthCycles = 0;
                mouthIsOpen = true;
            }
            ++mouthCycles;
        }
    }
    
    private void rotate() {
        int x = getX();
        int y = getY();
        if ((x - 15) % 30 == 0 && (y - 15) % 30 == 0) {
            int cpDir = currentDirection;
            String key = Greenfoot.getKey();
            if (key == null) {
                key = lastKey;
            } else {
                lastKey = key;
            }
            if (key == null) {
                return;
            }
            else if (key.equals("right"))
            {
                currentDirection = 0;
            }
            else if (key.equals("left"))
            {
                currentDirection = 2;
            }
            else if (key.equals("down"))
            {
                currentDirection = 1;
            }
            else if (key.equals("up"))
            {
                currentDirection = 3;
            }
            if (!tile.canMove(x / 30, y / 30, currentDirection, false)) {
                currentDirection = cpDir;
                lastKey = key;
            }
            setRotation(currentDirection * 90);
        }
    }
    
    private void getPoints() {
        if (isTouching(point.class)) {
            removeTouching(point.class);
            tile.pointCounter.updateScore(10, 0);
            if (--tile.nPoints == 0) {
                stahp = true;
                map.nextMap();
            }
        }
    }
    
    private void getPowerups() {
        if (isTouching(powerup.class)) {
            removeTouching(powerup.class);
            canKillForNCycles = 570;
        }
        else if (canKillForNCycles>0) {
            tile.timer.updateTimer(--canKillForNCycles);
        }
    }
    
    private void interactWithGhosts() {
        if (isTouching(Ghost.class)) {
            if (canKillForNCycles > 0) {
                ((Ghost)getOneIntersectingObject(Ghost.class)).DIE();
                tile.pointCounter.updateScore(200, 0);
            } else {
                DIE();
            }
        }
    }
    public Boolean canKill() {
        return canKillForNCycles>0;
    }
    
    private void DIE() {
        if (tile.pointCounter.lives > 0) {
            tile.pointCounter.updateScore(0, 1);
            Greenfoot.delay(50);
            map.reset();
        } else {
            map.dedForReal();
        }
    }
}
