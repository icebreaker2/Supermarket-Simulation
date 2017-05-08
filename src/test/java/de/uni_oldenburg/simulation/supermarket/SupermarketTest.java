package de.uni_oldenburg.simulation.supermarket;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests {@link Supermarket}.
 */
public class SupermarketTest {

	Supermarket supermarket;

	@Before
	public void setUp() {
		new SupermarketWithUI().createController();
		supermarket = new Supermarket(0);
	}

	@Test
	public void getNumberOfWaitingCustomers() throws Exception {
		assertEquals(supermarket.getNumberOfWaitingCustomers(), 0);
	}
}