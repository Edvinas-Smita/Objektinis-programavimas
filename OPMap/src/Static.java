public class Static
{
	public static boolean doubleBetween(double a, double b, double c)
	{
		return a < b && b < c;
	}
	
	public static boolean doubleWithinBounds(double a, double b, double bounds)
	{
		return doubleBetween(a - bounds, b, a + bounds);
	}
}
