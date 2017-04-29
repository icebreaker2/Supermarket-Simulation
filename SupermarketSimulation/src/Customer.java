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

	// at present we have only one algorithm: value iteration.  I might
	// revise this and add our alternate (TD) algorithm.  See the papers.


	public void depositPheromone(final SimState state) {
		final Supermarket supermarket = (Supermarket) state;

		Int2D location = supermarket.buggrid.getObjectLocation(this);
		int x = location.x;
		int y = location.y;

		if (Supermarket.ALGORITHM == Supermarket.ALGORITHM_VALUE_ITERATION) {
			// test all around
			if (hasFoodItem)  // deposit food pheromone
			{
				double max = supermarket.toFoodGrid.field[x][y];
				for (int dx = -1; dx < 2; dx++)
					for (int dy = -1; dy < 2; dy++) {
						int _x = dx + x;
						int _y = dy + y;
						if (_x < 0 || _y < 0 || _x >= Supermarket.GRID_WIDTH || _y >= Supermarket.GRID_HEIGHT)
							continue;  // nothing to see here
						double m = supermarket.toFoodGrid.field[_x][_y] *
								(dx * dy != 0 ? // diagonal corners
										1 : 0.5) +
								reward;
						if (m > max) max = m;
					}
				supermarket.toFoodGrid.field[x][y] = max;
			} else {
				double max = supermarket.toHomeGrid.field[x][y];
				for (int dx = -1; dx < 2; dx++)
					for (int dy = -1; dy < 2; dy++) {
						int _x = dx + x;
						int _y = dy + y;
						if (_x < 0 || _y < 0 || _x >= Supermarket.GRID_WIDTH || _y >= Supermarket.GRID_HEIGHT)
							continue;  // nothing to see here
						double m = supermarket.toHomeGrid.field[_x][_y] *
								(dx * dy != 0 ? // diagonal corners
										1 : 0.5) +
								reward;
						if (m > max) max = m;
					}
				supermarket.toHomeGrid.field[x][y] = max;
			}
		}
		reward = 0.0;
	}

	public void act(final SimState state) {
		final Supermarket supermarket = (Supermarket) state;

		Int2D location = supermarket.buggrid.getObjectLocation(this);
		int x = location.x;
		int y = location.y;

		if (hasFoodItem)  // follow home pheromone
		{
			double max = Supermarket.IMPOSSIBLY_BAD_PHEROMONE;
			int max_x = x;
			int max_y = y;
			int count = 2;
			for (int dx = -1; dx < 2; dx++)
				for (int dy = -1; dy < 2; dy++) {
					int _x = dx + x;
					int _y = dy + y;
					if ((dx == 0 && dy == 0) ||
							_x < 0 || _y < 0 ||
							_x >= Supermarket.GRID_WIDTH || _y >= Supermarket.GRID_HEIGHT ||
							supermarket.obstacles.field[_x][_y] == 1) continue;  // nothing to see here
					double m = supermarket.toHomeGrid.field[_x][_y];
					if (m > max) {
						count = 2;
					}
					// no else, yes m > max is repeated
					if (m > max || (m == max && state.random.nextBoolean(1.0 / count++)))  // this little magic makes all "==" situations equally likely
					{
						max = m;
						max_x = _x;
						max_y = _y;
					}
				}
			if (max == 0 && last != null)  // nowhere to go!  Maybe go straight
			{
				if (state.random.nextBoolean(1)) {
					int xm = x + (x - last.x);
					int ym = y + (y - last.y);
					if (xm >= 0 && xm < Supermarket.GRID_WIDTH && ym >= 0 && ym < Supermarket.GRID_HEIGHT && supermarket.obstacles.field[xm][ym] == 0) {
						max_x = xm;
						max_y = ym;
					}
				}
			} else if (state.random.nextBoolean(supermarket.randomActionProbability))  // Maybe go randomly
			{
				int xd = (state.random.nextInt(3) - 1);
				int yd = (state.random.nextInt(3) - 1);
				int xm = x + xd;
				int ym = y + yd;
				if (!(xd == 0 && yd == 0) && xm >= 0 && xm < Supermarket.GRID_WIDTH && ym >= 0 && ym < Supermarket.GRID_HEIGHT && supermarket.obstacles.field[xm][ym] == 0) {
					max_x = xm;
					max_y = ym;
				}
			}
			supermarket.buggrid.setObjectLocation(this, new Int2D(max_x, max_y));
			if (supermarket.sites.field[max_x][max_y] == Supermarket.HOME)  // reward me next time!  And change my status
			{
				hasFoodItem = !hasFoodItem;
			}
		} else {
			double max = Supermarket.IMPOSSIBLY_BAD_PHEROMONE;
			int max_x = x;
			int max_y = y;
			int count = 2;
			for (int dx = -1; dx < 2; dx++)
				for (int dy = -1; dy < 2; dy++) {
					int _x = dx + x;
					int _y = dy + y;
					if ((dx == 0 && dy == 0) ||
							_x < 0 || _y < 0 ||
							_x >= Supermarket.GRID_WIDTH || _y >= Supermarket.GRID_HEIGHT ||
							supermarket.obstacles.field[_x][_y] == 1) continue;  // nothing to see here
					double m = supermarket.toFoodGrid.field[_x][_y];
					if (m > max) {
						count = 2;
					}
					// no else, yes m > max is repeated
					if (m > max || (m == max && state.random.nextBoolean(1.0 / count++)))  // this little magic makes all "==" situations equally likely
					{
						max = m;
						max_x = _x;
						max_y = _y;
					}
				}
			if (max == 0 && last != null)  // nowhere to go!  Maybe go straight
			{
				if (state.random.nextBoolean(1)) {
					int xm = x + (x - last.x);
					int ym = y + (y - last.y);
					if (xm >= 0 && xm < Supermarket.GRID_WIDTH && ym >= 0 && ym < Supermarket.GRID_HEIGHT && supermarket.obstacles.field[xm][ym] == 0) {
						max_x = xm;
						max_y = ym;
					}
				}
			} else if (state.random.nextBoolean(supermarket.randomActionProbability))  // Maybe go randomly
			{
				int xd = (state.random.nextInt(3) - 1);
				int yd = (state.random.nextInt(3) - 1);
				int xm = x + xd;
				int ym = y + yd;
				if (!(xd == 0 && yd == 0) && xm >= 0 && xm < Supermarket.GRID_WIDTH && ym >= 0 && ym < Supermarket.GRID_HEIGHT && supermarket.obstacles.field[xm][ym] == 0) {
					max_x = xm;
					max_y = ym;
				}
			}
			supermarket.buggrid.setObjectLocation(this, new Int2D(max_x, max_y));
			if (supermarket.sites.field[max_x][max_y] == Supermarket.FOOD)  // reward me next time!  And change my status
			{
				hasFoodItem = !hasFoodItem;
			}
		}
		last = location;
	}

	public void step(final SimState state) {
		depositPheromone(state);
		act(state);
	}

	// a few tweaks by Sean
	private Color noFoodColor = Color.black;
	private Color foodColor = Color.red;

	public final void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		if (hasFoodItem)
			graphics.setColor(foodColor);
		else
			graphics.setColor(noFoodColor);

		// this code was stolen from OvalPortrayal2D
		int x = (int) (info.draw.x - info.draw.width / 2.0);
		int y = (int) (info.draw.y - info.draw.height / 2.0);
		int width = (int) (info.draw.width);
		int height = (int) (info.draw.height);
		graphics.fillOval(x, y, width, height);

	}
}
