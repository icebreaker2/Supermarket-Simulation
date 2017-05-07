package de.uni_oldenburg.simulation.supermarket;

import ec.util.MersenneTwisterFast;
import sim.engine.RandomSequence;
import sim.util.distribution.Normal;

/**
 * This class provides various auxiliary functions
 */
public class Auxiliary {

	private static MersenneTwisterFast mersenneTwisterFast = new MersenneTwisterFast();

	/**
	 * Solves a normal deviated function for x with a wanted probability.
	 * @param mean mean of the normal deviation
	 * @param variance variance of the normal deviation
	 * @param wantedProbability is the wanted probability corresponding to x
	 * @param maxNumberOfX is a highly unlikely max number for the interval to search for
	 * @return the solved x for the normal deviation
	 */
	public static int solveNormalDeviationForX(double mean, double variance, double wantedProbability, int maxNumberOfX) {
		// we need a fix to solve for x but it is quite easy for our integers of customers
		double lastProbability = 0;
		for (int x = 0; x < maxNumberOfX; x++) {
			Normal normalDeviation = new Normal(mean, Math.pow(variance, 2), mersenneTwisterFast);
			double probabilty = normalDeviation.cdf(x); // solves P(X<=x)
			if (probabilty < wantedProbability) {
				lastProbability = probabilty;
			} else {
				if (wantedProbability - lastProbability > probabilty - wantedProbability) { // difference from the last probability is greater than this one
					return x;
				} else { // else return the last number of customers or zero if x is 0
					return (x - 1 < 0 ? 0 : x - 1);
				}
			}
		}
		return maxNumberOfX;
	}
}
