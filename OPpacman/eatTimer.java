import greenfoot.Actor;
import greenfoot.World;
import greenfoot.Color;
import greenfoot.GreenfootImage;

/**
 *
 * 
 * @author Edvinas Smita - VU MIF PS 1k. 2gr.
 * 
 * 
 */

public class eatTimer extends Actor
{
    public void updateTimer(int x) {
        if (x != 0) {
            GreenfootImage bar = new GreenfootImage(x, 15);
            bar.setColor(new Color(255 * (570 - x) / 570, 255 * x / 570, 0));
            bar.fillRect(0, 0, x, 15);
            setImage(bar);
            setLocation(x / 2, 7);
        } else {
            GreenfootImage bar = new GreenfootImage(1, 1);
            setImage(bar);
            setLocation(0, 0);
        }
    }
    
    public void addedToWorld(World world) {
        updateTimer(0);
    }
}
