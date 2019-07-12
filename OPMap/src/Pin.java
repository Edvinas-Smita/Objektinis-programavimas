import java.awt.*;

public class Pin extends Point
{
	private String name;
	
	public Pin(Point point, String name)
	{
		super(point);
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return "Pin: [" + x + ", " + y + "]";
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
}
