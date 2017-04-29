import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;

import java.awt.*;
import javax.swing.*;

public class SupermarketWithUI extends GUIState {
	public Display2D display;
	public JFrame displayFrame;

	FastValueGridPortrayal2D customer = new FastValueGridPortrayal2D("Customer");
	FastValueGridPortrayal2D checkstand = new FastValueGridPortrayal2D("Checkstand");

	public static void main(String[] args) {
		new SupermarketWithUI().createController();
	}

	public SupermarketWithUI() {
		super(new Supermarket(System.currentTimeMillis()));
	}

	public SupermarketWithUI(SimState state) {
		super(state);
	}

	// allow the user to inspect the model
	public Object getSimulationInspectedObject() {
		return state;
	}  // non-volatile

	public static String getName() {
		return "Supermarket Simulation";
	}

	public void setupPortrayals() {

		Supermarket supermarket = (Supermarket) state;

		// tell the portrayals what to portray and how to portray them
		customer.setField(supermarket.toHomeGrid);
		customer.setMap(new sim.util.gui.SimpleColorMap(
				0,
				Supermarket.LIKELY_MAX_PHEROMONE,
				// home pheromones are beneath all, just make them opaque
				Color.white, //new Color(0,255,0,0),
				new Color(0, 255, 0, 255)) {
			public double filterLevel(double level) {
				return Math.sqrt(Math.sqrt(level));
			}
		});  // map with custom level filtering
		checkstand.setField(supermarket.toFoodGrid);
		checkstand.setMap(new sim.util.gui.SimpleColorMap(
				0,
				Supermarket.LIKELY_MAX_PHEROMONE,
				new Color(0, 0, 255, 0),
				new Color(0, 0, 255, 255)) {
			public double filterLevel(double level) {
				return Math.sqrt(Math.sqrt(level));
			}
		});

		// reschedule the displayer
		display.reset();

		// redraw the display
		display.repaint();
	}

	public void start() {
		super.start();  // set up everything but replacing the display
		// set up our portrayals
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		// we now have new grids.  Set up the portrayals to reflect that
		setupPortrayals();
	}

	public void init(Controller c) {
		super.init(c);

		// Make the Display2D.  We'll have it display stuff later.
		display = new Display2D(400, 400, this); // at 400x400, we've got 4x4 per array position
		displayFrame = display.createFrame();
		c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
		displayFrame.setVisible(true);

		// attach the portrayals from bottom to top
		display.attach(customer, "Customer");
		display.attach(checkstand, "Checkstand");

		// specify the backdrop color  -- what gets painted behind the displays
		display.setBackdrop(Color.white);
	}

	public void quit() {
		super.quit();

		// disposing the displayFrame automatically calls quit() on the display,
		// so we don't need to do so ourselves here.
		if (displayFrame != null) displayFrame.dispose();
		displayFrame = null;  // let gc
		display = null;       // let gc
	}

}




