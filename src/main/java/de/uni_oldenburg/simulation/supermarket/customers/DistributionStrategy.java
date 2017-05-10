package de.uni_oldenburg.simulation.supermarket.customers;

import de.uni_oldenburg.simulation.supermarket.Supermarket;

/**
 * Interface for a distribution strategy
 */
public interface DistributionStrategy {

	public void executeStrategyStep(Supermarket simstep);

}