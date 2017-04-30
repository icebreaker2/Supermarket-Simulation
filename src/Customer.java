import sim.portrayal.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.engine.*;

import java.awt.*;

public class Customer extends OvalPortrayal2D implements Steppable {
	private static final long serialVersionUID = 1;

	private boolean isInQueue = false;

	private long ownCheckoutDuration = -1;
	private long ownTotalDuration = -1;
	private final int canLeaveCheckout = 0;

	private Int2D last;

	public Customer() {
	}

	public void step(final SimState state) {
		Supermarket supermarket = (Supermarket) state;

		Int2D location = supermarket.supermarketGrid.getObjectLocation(this);
		int x = location.x;
		int y = location.y;

		// If next person reaches checkout or last in line
		if (reachedEndOfLine(x, y) && ownCheckoutDuration != canLeaveCheckout) {
			if (!isInQueue) {
				// compute the delay in addition to the own waiting time
				ownCheckoutDuration = (long) (Math.random() * 500);
				ownTotalDuration = ownCheckoutDuration + Supermarket.currentLineTotalDuration();

				// add current person to the list of waiting customers
				Supermarket.addCustomerToCheckoutQueue(this);
				isInQueue = true;
			} else {
				ownCheckoutDuration--;
			}
		} else {
			// Move dots to checkout or end of line
			supermarket.supermarketGrid.setObjectLocation(this, new Int2D(x - 1, y));
		}

		last = location;
	}

	private boolean reachedEndOfLine(int x, int y) {
		return (x == Supermarket.CHECKOUT_X && y == Supermarket.CHECKOUT_Y);
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

	public long getOwnCheckoutDuration() {
		return ownCheckoutDuration;
	}
}
