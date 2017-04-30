import sim.portrayal.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.engine.*;

import java.awt.*;

public class Customer extends OvalPortrayal2D implements Steppable {
	private static final long serialVersionUID = 1;

	public Customer() {
	}

	public void step(final SimState state) {
		Supermarket supermarket = (Supermarket) state;

		Int2D location = supermarket.supermarketGrid.getObjectLocation(this);

		// Look forward
		Bag objectsAtNextLocation = supermarket.supermarketGrid.getObjectsAtLocation(0, location.y + 1);

		// Step down if possible (no self checkout)
		if (objectsAtNextLocation == null && location.y != supermarket.CHECKOUT_POSITION) {
			supermarket.supermarketGrid.setObjectLocation(this, 0, location.y+1);
		}
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