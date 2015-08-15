import java.awt.*;
import java.awt.geom.*;
public class Checkpoint {
	public double x;
	public double y;
	
	private double width = 8;
	
	public Checkpoint(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Shape getShape(){
		return new Rectangle2D.Double(x, y, width, width);	
	}
}