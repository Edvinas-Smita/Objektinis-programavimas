import greenfoot.Actor;
import greenfoot.World;
import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

/**
 *
 * 
 * @author Edvinas Smita - VU MIF PS 1k. 2gr.
 * 
 * 
 */

public class Ghost extends Actor
{
    private int currentDirection = Greenfoot.getRandomNumber(4);
    private int aggressiveness;
    private Player player;
    private Boolean canEnterSpawn = true;
    
    private Boolean DED = false;
    private int RIPtime = 0;

    private World world;
    private TileSystem tile;
    public void addedToWorld(World world) {
        world = getWorld();
        tile = (TileSystem)world;
        drawSpoopyGhost();
    }
    
    public Ghost(Player playa, int aggro) {
        player = playa;
        aggressiveness = aggro;
    }
    
    public void act() {
        if (DED && ++RIPtime > 300) {
            DED = false;
            setLocation(255 + Greenfoot.getRandomNumber(3) * 30, 285);
        } else {
            moveIt();
        }
    }
    
    private void drawSpoopyGhost() {
        GreenfootImage pinky = player.canKill() ? new GreenfootImage("Edible.png") : new GreenfootImage("PinkyGhost.png");
        pinky.setColor(Color.WHITE);
        switch (currentDirection) {
            case 0: {
                pinky.fillOval(5, 5, 8, 9);
                pinky.fillOval(20, 5, 8, 9);
                pinky.setColor(Color.BLACK);
                pinky.fillRect(10, 9, 4, 4);
                pinky.fillRect(25, 9, 4, 4);
                setImage(pinky);
                break;
            }
            case 1: {
                pinky.fillOval(6, 10, 8, 9);
                pinky.fillOval(15, 10, 8, 9);
                pinky.setColor(Color.BLACK);
                pinky.fillRect(9, 15, 4, 4);
                pinky.fillRect(17, 15, 4, 4);
                setImage(pinky);
                break;
            }
            case 2: {
                pinky.fillOval(0, 5, 8, 9);
                pinky.fillOval(15, 5, 8, 9);
                pinky.setColor(Color.BLACK);
                pinky.fillRect(0, 9, 4, 4);
                pinky.fillRect(15, 9, 4, 4);
                setImage(pinky);
                break;
            }
            case 3: {
                pinky.fillOval(6, 1, 8, 9);
                pinky.fillOval(15, 1, 8, 9);
                pinky.setColor(Color.BLACK);
                pinky.fillRect(9, 1, 4, 4);
                pinky.fillRect(17, 1, 4, 4);
                setImage(pinky);
                break;
            }
        }
    }
    
    private void setRandomDir() {
        if (player.canKill()) {
            currentDirection = tile.DijkstraPathing(this);
            int cD = Greenfoot.getRandomNumber(4);
            while (cD == currentDirection || !tile.canMove(getX() / 30, getY() / 30, cD, canEnterSpawn)) {
                cD = Greenfoot.getRandomNumber(4);
            }
            currentDirection = cD;
        }
        else if (9 - aggressiveness > 0 && !(Greenfoot.getRandomNumber(9 - aggressiveness) == 0)) {
            currentDirection = Greenfoot.getRandomNumber(4);
            while (!tile.canMove(getX() / 30, getY() / 30, currentDirection, canEnterSpawn)) {
                currentDirection = Greenfoot.getRandomNumber(4);
            }
        } else {
            currentDirection = tile.DijkstraPathing(this);
            if (currentDirection == -1) {
                currentDirection = Greenfoot.getRandomNumber(4);
                while (!tile.canMove(getX() / 30, getY() / 30, currentDirection, canEnterSpawn)) {
                    currentDirection = Greenfoot.getRandomNumber(4);
                }
            }
        }
        drawSpoopyGhost();
    }
    private void moveIt() {
        int x = getX();
        int y = getY();
        if ((x - 15) % 30 == 0 && (y - 15) % 30 == 0) {
            if (canEnterSpawn && tile.getIsPossibleNode(getX() / 30, getY() / 30)) {
                canEnterSpawn = false;
            }
            if (!canEnterSpawn && !tile.getIsPossibleNode(getX() / 30, getY() / 30)) {
                canEnterSpawn = true;
            }
            if (tile.isAnIntersection(x / 30, y / 30)) {
                setRandomDir();
            }
            else if (player.canKill()) {
                setRandomDir();
            }
        }
        if (!tile.move(this, currentDirection, canEnterSpawn)) {
                setRandomDir();
        }
    }
    
    public void DIE() {
        canEnterSpawn = true;
        DED = true;
        RIPtime = 0;
        setImage(new GreenfootImage(1, 1));
        setLocation(0, 0);
    }
}
