import sim.portrayal.*;
import sim.portrayal.simple.*;
import sim.util.*;
import sim.engine.*;

import java.awt.*;

public class Customer extends OvalPortrayal2D implements Steppable {
	private static final long serialVersionUID = 1;

	private boolean isInQueue = false;
	private int processingDelay = -1;
	private final int canLeaveCheckstand = 0;

	Int2D last;

	public Customer() {
	}

	public void step(final SimState state) {
		final Supermarket supermarket = (Supermarket) state;

		Int2D location = supermarket.supermarketgrid.getObjectLocation(this);
		int x = location.x;
		int y = location.y;

		if (x == supermarket.CHECKSTAND_X && y == supermarket.CHECKSTAND_Y && processingDelay != canLeaveCheckstand) { // wait at the checkstand
			if (!isInQueue) {
				// TODO implement the queue and compute the processing delay at the checkstand. I suggest the following:
			/*
				1. Compute a normal deviated delay in steps
				2. set processingDelay to the computed delay and add the delays of the customers in that are top of this customer (see the queue of the customers in front)
				3. decrease processingDelay each step
				4. If processingDelay is 0 again the customer leaves to the left as he would before
				See the delay of random steps hardcoded
			 */
				processingDelay = 500 + (int) (Math.random()*1000); // delaying steps

				isInQueue = !isInQueue;
			} else {
				processingDelay--;
			}

		} else {
			// now go straight left to the checkstand
			supermarket.supermarketgrid.setObjectLocation(this, new Int2D(x - 1, y)); // run straight to the checkstand
		}


		last = location;
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
