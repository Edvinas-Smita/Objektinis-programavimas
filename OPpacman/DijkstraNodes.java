/**
 *
 * 
 * @author Edvinas Smita - VU MIF PS 1k. 2gr.
 * 
 * 
 */

public class DijkstraNodes  
{
    private DijkstraNodes nextNode0 = null;
    private DijkstraNodes nextNode1 = null;
    private DijkstraNodes nextNode2 = null;
    private DijkstraNodes nextNode3 = null;
    
    private int originX;
    private int originY;
    private int goalX;
    private int goalY;
    private TileSystem tile;
    private DijkstraNodes parent;
    
    public DijkstraNodes(int oX, int oY, int gX, int gY, TileSystem oT, DijkstraNodes papa) {
        originX = oX;
        originY = oY;
        goalX = gX;
        goalY = gY;
        tile = oT;
        parent = papa;
    }
    
    private Boolean checkForBounds(int x, int y) {
        if (x < 0 || x > 18 || y < 0 || y > 18) {
            return false;
        }
        return true;
    }
    
    public int expandOnce() {
        int returnDirection;
        if (checkForBounds(originX + 1, originY)
            && nextNode0 == null
            && !tile.pathFound) {
            if (originX + 1 == goalX && originY == goalY) {
                tile.pathFound = true;
                parent.clearAllButPath(this);
                return 2;
            } else if (!tile.checkIfNoded(originX + 1, originY)) {
                tile.setNode(originX + 1, originY);
                nextNode0 = new DijkstraNodes(originX + 1, originY, goalX, goalY, tile, this);
            }
        } else if (nextNode0 != null) {
            returnDirection = nextNode0.expandFurther();
            if (returnDirection != -1) {
                return returnDirection;
            }
        }
        if (checkForBounds(originX, originY + 1)
            && nextNode1 == null
            && !tile.pathFound) {
            if (originX == goalX && originY + 1 == goalY) {
                tile.pathFound = true;
                parent.clearAllButPath(this);
                return 3;
            } else if (!tile.checkIfNoded(originX, originY + 1)) {
                tile.setNode(originX, originY + 1);
                nextNode1 = new DijkstraNodes(originX, originY + 1, goalX, goalY, tile, this);
            }
        } else if (nextNode1 != null) {
            returnDirection = nextNode1.expandFurther();
            if (returnDirection != -1) {
                return returnDirection;
            }
        }
        if (checkForBounds(originX - 1, originY)
            && nextNode2 == null
            && !tile.pathFound) {
            if (originX - 1 == goalX && originY == goalY) {
                tile.pathFound = true;
                parent.clearAllButPath(this);
                return 0;
            } else if (!tile.checkIfNoded(originX - 1, originY)) {
                tile.setNode(originX - 1, originY);
                nextNode2 = new DijkstraNodes(originX - 1, originY, goalX, goalY, tile, this);
            }
        } else if (nextNode2 != null) {
            returnDirection = nextNode2.expandFurther();
            if (returnDirection != -1) {
                return returnDirection;
            }
        }
        if (checkForBounds(originX, originY - 1)
            && nextNode3 == null
            && !tile.pathFound) {
            if (originX == goalX && originY - 1 == goalY) {
                tile.pathFound = true;
                parent.clearAllButPath(this);
                return 1;
            } else if (!tile.checkIfNoded(originX, originY - 1)) {
                tile.setNode(originX, originY - 1);
                nextNode3 = new DijkstraNodes(originX, originY - 1, goalX, goalY, tile, this);
            }
        } else if (nextNode3 != null) {
            returnDirection = nextNode3.expandFurther();
            if (returnDirection != -1) {
                return returnDirection;
            }
        }
        return -1;
    }
    
    public int expandFurther() {
        int returnDirection;
        if (!tile.pathFound) {
            returnDirection = this.expandOnce();
            if (returnDirection != -1) {
                return returnDirection;
            }
        }
        return -1;
    }
    
    private void clearAllButPath(DijkstraNodes path)
    {
        if (parent != null)
        {
            parent.clearAllButPath(this);
        }
        if (nextNode0 != null && nextNode0 != path)
        {
            nextNode0.clearAll();
            tile.removeNode(originX + 1, originY);
            nextNode0 = null;
        }
        if (nextNode1 != null && nextNode1 != path)
        {
            nextNode1.clearAll();
            tile.removeNode(originX, originY + 1);
            nextNode1 = null;
        }
        if (nextNode2 != null && nextNode2 != path)
        {
            nextNode2.clearAll();
            tile.removeNode(originX - 1, originY);
            nextNode2 = null;
        }
        if (nextNode3 != null && nextNode3 != path)
        {
            nextNode3.clearAll();
            tile.removeNode(originX, originY - 1);
            nextNode3 = null;
        }
    }
    
    public void clearAll()
    {
        if (nextNode0 != null)
        {
            nextNode0.clearAll();
            tile.removeNode(originX + 1, originY);
            nextNode0 = null;
        }
        if (nextNode1 != null)
        {
            nextNode1.clearAll();
            tile.removeNode(originX, originY + 1);
            nextNode1 = null;
        }
        if (nextNode2 != null)
        {
            nextNode2.clearAll();
            tile.removeNode(originX - 1, originY);
            nextNode2 = null;
        }
        if (nextNode3 != null)
        {
            nextNode3.clearAll();
            tile.removeNode(originX, originY - 1);
            nextNode3 = null;
        }
    }
}
