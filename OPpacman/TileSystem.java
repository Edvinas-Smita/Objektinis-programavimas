import greenfoot.World;
import greenfoot.Actor;
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

public class TileSystem extends World
{
    public Player player = new Player();
    
    public Ghost ghost1 = new Ghost(player, 0);
    public Ghost ghost2 = new Ghost(player, 3);
    public Ghost ghost3 = new Ghost(player, 6);
    public Ghost ghost4 = new Ghost(player, 9);
    
    public PointCounter pointCounter = new PointCounter();
    public eatTimer timer = new eatTimer();
    
    public Boolean pathFound = false;
    public int nPoints = 0;
    public int nPowerups = 0;
    
    private Boolean[][] possibleNode = new Boolean[19][19];
    private Boolean[][] noded = new Boolean[19][19];
    
    public TileSystem() {
        super(570, 570, 1);
        Greenfoot.setSpeed(55);
    }
    
    private void pickPossibleNodes() {
        for (int i = 0; i < 19; ++i) {
            for (int j = 0; j < 19; ++j) {
                if (getColorAt(30 * j, 30 * i).equals(Color.BLACK)) {
                    possibleNode[j][i] = true;
                }
                else {
                    possibleNode[j][i] = false;
                }
            }
        }
    }
    
    public Boolean getIsPossibleNode(int x, int y) {
        return possibleNode[x][y];
    }
    public Boolean checkIfNoded(int x, int y) {
        return noded[x][y] || !possibleNode[x][y];
    }
    public void setNode(int x, int y)
    {
        noded[x][y] = true;
    }
    public void removeNode(int x, int y)
    {
        noded[x][y] = false;
    }
    
    public void fillPoints() {
        for (int i = 0; i < 19; ++i) {
            for (int j = 0; j < 19; ++j) {
                if (getIsPossibleNode(j, i)) {
                    if (Greenfoot.getRandomNumber(50) == 0 && nPowerups < 5)
                    {
                        ++nPowerups;
                        addObject(new powerup(), 30 * j + 15, 30 * i + 15);
                    }
                    else {
                        ++nPoints;
                        addObject(new point(), 30 * j + 15, 30 * i + 15);
                    }
                }
            }
        }
    }
    public void setupMap(String mapImg) {
        GreenfootImage background = new GreenfootImage(570, 570);
        background.setColor(Color.BLACK);
        background.fill();
        background.drawImage(new GreenfootImage(mapImg), 0, 0);
        
        setBackground(background);
        pickPossibleNodes();
        fillPoints();
    }
    
    public Boolean canMove(int x, int y, int direction, Boolean canEnterSpawn) {
        int maxX = getWidth();
        int maxY = getHeight();
        Color color;
        switch (direction) {
            case 0:{
                if ((x + 1) * 30 >= maxX)
                {
                    return true;
                }
                color = getColorAt((x + 1) * 30, y * 30);
                if (color.equals(Color.BLUE) || color.equals(Color.CYAN)) {
                    if (color.equals(Color.CYAN) && canEnterSpawn) {
                        return true;
                    }
                    return false;
                }
                break;
            }
            case 1:{
                if ((y + 1) * 30 >= maxY)
                {
                    return true;
                }
                color = getColorAt(x * 30, (y + 1) * 30);
                if (color.equals(Color.BLUE) || color.equals(Color.CYAN)) {
                    if (color.equals(Color.CYAN) && canEnterSpawn) {
                        return true;
                    }
                    return false;
                }
                break;
            }
            case 2:{
                if ((x - 1) * 30 < 0)
                {
                    return true;
                }
                color = getColorAt((x - 1) * 30, y * 30);
                if (color.equals(Color.BLUE) || color.equals(Color.CYAN)) {
                    if (color.equals(Color.CYAN) && canEnterSpawn) {
                        return true;
                    }
                    return false;
                }
                break;
            }
            case 3:{
                if ((y - 1) * 30 < 0)
                {
                    return true;
                }
                color = getColorAt(x * 30, (y - 1) * 30);
                if (color.equals(Color.BLUE) || color.equals(Color.CYAN)) {
                    if (color.equals(Color.CYAN) && canEnterSpawn) {
                        return true;
                    }
                    return false;
                }
                break;
            }
        }
        return true;
    }
    public Boolean isAnIntersection(int x, int y) {
        int wayCount = 0;
        for (int i = 0; i<4; ++i) {
            wayCount = canMove(x, y, i, true) ? wayCount + 1 : wayCount;
        }
        if (wayCount>=3) {
            return true;
        }
        return false;
    }
    
    public Boolean move(Actor actor, int direction, Boolean canEnterSpawn) {
        int x = actor.getX();
        int y = actor.getY();
        if ((x - 15) % 30 == 0
            && (y - 15) % 30 == 0
            && !canMove(x / 30, y / 30, direction, canEnterSpawn)) {
            return false;
        }
        int maxX = getWidth();
        int maxY = getHeight();
        switch (direction) {
            case 0:{
                if (x + 1 >= maxX)
                {
                    actor.setLocation(0, y);
                    break;
                }
                actor.setLocation(x + 1, y);
                break;
            }
            case 1:{
                if (y + 1 >= maxY)
                {
                    actor.setLocation(x, 0);
                    break;
                }
                actor.setLocation(x, y + 1);
                break;
            }
            case 2:{
                if (x - 1 < 0)
                {
                    actor.setLocation(maxX - 1, y);
                    break;
                }
                actor.setLocation(x - 1, y);
                break;
            }
            case 3:{
                if (y - 1 < 0)
                {
                    actor.setLocation(x, maxY - 1);
                    break;
                }
                actor.setLocation(x, y - 1);
                break;
            }
        }
        return true;
    }
    
    public int DijkstraPathing(Ghost ghost) {
        if (ghost == null || player == null) {
            return -1;
        }
        int ghostX = ghost.getX();
        int ghostY = ghost.getY();
        int ghostTileX = ghostX / 30;
        int ghostTileY = ghostY / 30;
        
        int playerX = player.getX();
        int playerY = player.getY();
        int playerTileX = playerX / 30;
        int playerTileY = playerY / 30;
        
        pathFound = false;
        for (int i = 0; i < 19; ++i) {
            for (int j = 0; j < 19; ++j) {
                noded[j][i] = false;
            }
        }
        
        DijkstraNodes node = new DijkstraNodes(playerTileX, playerTileY, ghostTileX, ghostTileY, this, null);
        
        int returnCode = node.expandOnce();
        if (returnCode == -1) {
            for (int i = 0; i < 10; ++i) {
                returnCode = node.expandFurther();
                if (returnCode != -1) {
                    break;
                }
            }
        }
        /*for (int i = 0; i<19; ++i) {
            for (int j = 0; j < 19; ++j) {
                System.out.print((getIsPossibleNode(j, i) ? (noded[j][i] ? 'X' : ' ') : 'O') + " ");
            }
            System.out.println();
        }*/
        node.clearAll();
        return returnCode;
    }
}
