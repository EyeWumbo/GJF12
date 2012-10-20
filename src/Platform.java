import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Platform extends MapEntity{
	int distance;
	public Platform(int x, int y, String imageLocation) {
		super(x, y, imageLocation);
	}

	public void setTravelDistance(int distance){
		this.distance = distance;
	}
	
	@Override
	public void render(GameContainer gc, Graphics g)
	{
		image.draw(xCoord, yCoord);
		
	}

	@Override
	public void init(GameContainer gc)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameContainer gc, int delta)
	{
		xCoord += Math.sin(Math.toRadians(delta)) * distance;
		
	}
}
