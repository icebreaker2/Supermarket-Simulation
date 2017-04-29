import sim.portrayal.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.engine.*;

import java.awt.*;

public class Customer extends OvalPortrayal2D implements Steppable {
	private static final long serialVersionUID = 1;

	public boolean getHasFoodItem() {
		return hasFoodItem;
	}

	public void setHasFoodItem(boolean val) {
		hasFoodItem = val;
	}

	public boolean hasFoodItem = false;
	double reward = 1;

	int x;
	int y;

	Int2D last;

	public Customer() {
	}

	public void act(final SimState state) {
		final Supermarket supermarket = (Supermarket) state;

		Int2D location = supermarket.supermarketgrid.getObjectLocation(this);
		int x = location.x;
		int y = location.y;

		// TODO if there is a customer at the left side (next customer in the queue) then wait and visualize the waiting
		// TODO Maybe the grid and visualization is not needed at all.

		// now go straight left
		supermarket.supermarketgrid.setObjectLocation(this, new Int2D(x-1, y-1));
		last = location;
	}

	public void step(final SimState state) {
		act(state);
	}

	public final void draw(Object object, Graphics2D graphics, DrawInfo2D info) {

		graphics.setColor(Color.black);
		// this code was stolen from OvalPortrayal2D
		int x = (int) (info.draw.x - info.draw.width / 2.0);
		int y = (int) (info.draw.y - info.draw.height / 2.0);
		int width = (int) (info.draw.width);
		int height = (int) (info.draw.height);
		graphics.fillOval(x, y, width, height);

	}
}
