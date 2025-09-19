package com.simulator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * A utility class for collecting and tracking statistics during Valorant match
 * simulations. This class uses static fields and methods to maintain global
 * counters for
 * various match outcomes.
 * </p>
 *
 * <p>
 * The class tracks the following statistics for both teams:
 * </p>
 * <ul>
 * <li>Match wins - total number of matches won by each team.</li>
 * <li>Round wins - total number of rounds won by each team.</li>
 * <li>Fifty-fifty wins - total number of rounds won by each team where both
 * sides
 * had a 50% chance of winning.</li>
 * </ul>
 *
 * <p>
 * This class cannot be instantiated and only provides static methods for
 * accessing and modifying statistics.
 * </p>
 *
 * @author exicutioner161
 * @version 1.0
 * @see MatchSimulator
 */

public class SimulationStatisticsCollector {
	private static AtomicLong team1MatchWins;
	private static AtomicLong team2MatchWins;
	private static AtomicLong team1RoundWins;
	private static AtomicLong team2RoundWins;
	private static AtomicLong team1FiftyFiftyWins;
	private static AtomicLong team2FiftyFiftyWins;

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 *
	 * This class is designed to be used only through its static methods
	 * and should not be instantiated.
	 */
	private SimulationStatisticsCollector() {
		// Private constructor to prevent instantiation
	}

	/**
	 * Increments the match win counter for team 1.
	 *
	 * This method is thread-safe and can be called concurrently from
	 * multiple simulation threads.
	 */
	public static void incrementTeam1MatchWins() {
		team1MatchWins.incrementAndGet();
	}

	/**
	 * Increments the match win counter for team 2.
	 *
	 * This method is thread-safe and can be called concurrently from
	 * multiple simulation threads.
	 */
	public static void incrementTeam2MatchWins() {
		team2MatchWins.incrementAndGet();
	}

	/**
	 * Adds the specified number of round wins to team 1's total.
	 *
	 * This method accumulates the total rounds won by team 1 across
	 * all simulated matches for statistical analysis.
	 *
	 * @param rounds the number of rounds won by team 1 in a match
	 */
	public static void increaseTeam1RoundWins(long rounds) {
		team1RoundWins.addAndGet(rounds);
	}

	/**
	 * Adds the specified number of round wins to team 2's total.
	 *
	 * This method accumulates the total rounds won by team 2 across
	 * all simulated matches for statistical analysis.
	 *
	 * @param rounds the number of rounds won by team 2 in a match
	 */
	public static void increaseTeam2RoundWins(long rounds) {
		team2RoundWins.addAndGet(rounds);
	}

	/**
	 * Increments the fifty-fifty round win counter for team 1.
	 *
	 * Tracks rounds where both teams had equal (50%) probability of winning
	 * but team 1 ultimately prevailed. This statistic helps analyze the
	 * impact of random chance in simulation outcomes.
	 */
	public static void incrementTeam1FiftyFiftyWins() {
		team1FiftyFiftyWins.incrementAndGet();
	}

	/**
	 * Increments the fifty-fifty round win counter for team 2.
	 *
	 * Tracks rounds where both teams had equal (50%) probability of winning
	 * but team 2 ultimately prevailed. This statistic helps analyze the
	 * impact of random chance in simulation outcomes.
	 */
	public static void incrementTeam2FiftyFiftyWins() {
		team2FiftyFiftyWins.incrementAndGet();
	}

	/**
	 * Gets the total number of matches won by team 1.
	 *
	 * @return the cumulative match wins for team 1 across all simulations
	 */
	public static long getTeam1MatchWins() {
		return team1MatchWins.get();
	}

	/**
	 * Gets the total number of matches won by team 2.
	 *
	 * @return the cumulative match wins for team 2 across all simulations
	 */
	public static long getTeam2MatchWins() {
		return team2MatchWins.get();
	}

	/**
	 * Gets the total number of rounds won by team 1.
	 *
	 * @return the cumulative round wins for team 1 across all simulations
	 */
	public static long getTeam1RoundWins() {
		return team1RoundWins.get();
	}

	/**
	 * Gets the total number of rounds won by team 2.
	 *
	 * @return the cumulative round wins for team 2 across all simulations
	 */
	public static long getTeam2RoundWins() {
		return team2RoundWins.get();
	}

	/**
	 * Gets the total number of fifty-fifty rounds won by team 1.
	 *
	 * Returns the count of rounds where both teams had equal probability
	 * of winning (50%) but team 1 was selected as the winner. This metric
	 * helps evaluate the role of randomness in simulation outcomes.
	 *
	 * @return the cumulative fifty-fifty round wins for team 1
	 */
	public static long getTeam1FiftyFiftyWins() {
		return team1FiftyFiftyWins.get();
	}

	/**
	 * Gets the total number of fifty-fifty rounds won by team 2.
	 *
	 * Returns the count of rounds where both teams had equal probability
	 * of winning (50%) but team 2 was selected as the winner. This metric
	 * helps evaluate the role of randomness in simulation outcomes.
	 *
	 * @return the cumulative fifty-fifty round wins for team 2
	 */
	public static long getTeam2FiftyFiftyWins() {
		return team2FiftyFiftyWins.get();
	}
}