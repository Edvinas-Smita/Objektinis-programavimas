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

public class PointCounter extends Actor
{
    public int score = 0;
    public int lives = 3;
    
    public void updateScore(int points, int deaths) 
    {
        GreenfootImage textImage = new GreenfootImage("Score: " + (score += points) + "; Lives: " + (lives -= deaths), 20, Color.RED, new Color(0, 0, 0, 0));
        setImage(textImage);
        setLocation(textImage.getWidth() / 2 + 1, 555);
    }
    
    public void addedToWorld(World world) {
        updateScore(0, 0);
    }
    
    public void victoryScreen() {
        GreenfootImage textImage = new GreenfootImage("Score: " + score + "\n Remaining lives: " + lives, 72, new Color(0, 127, 14), new Color(0, 0, 0, 0));
        setImage(textImage);
        setLocation(285, 321);
    }
    
    public void dedScreen() {
        GreenfootImage textImage = new GreenfootImage("Score: " + score, 72, new Color(127, 0, 0), new Color(0, 0, 0, 0));
        setImage(textImage);
        setLocation(285, 400);
    }
}
