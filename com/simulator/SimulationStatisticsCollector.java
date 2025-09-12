/**
 * A utility class that provides static methods to collect and access aggregated
 * simulation statistics from MatchSimulator instances.
 *
 * This class serves as a facade for accessing cumulative statistics across
 * multiple match simulations. Since MatchSimulator uses static fields to track
 * totals, this collector creates temporary instances solely to access the
 * aggregated data through their getter methods.
 *
 * The class follows the utility pattern with a private constructor to prevent
 * instantiation, as all functionality is provided through static methods.
 *
 * Statistics Available:
 *
 *   Match wins for both teams
 *   Round wins for both teams
 *   Fifty-fifty situation wins for both teams
 *
 * @author exicutioner161
 * @version 0.1.9-alpha
 * @see MatchSimulator
 * @see Main
 */
package com.simulator;

public class SimulationStatisticsCollector {
	private static long team1MatchWins;
	private static long team2MatchWins;
	private static long team1RoundWins;
	private static long team2RoundWins;
	private static long team1FiftyFiftyWins;
	private static long team2FiftyFiftyWins;

	private SimulationStatisticsCollector() {
		// Private constructor to prevent instantiation
	}

	public static void incrementTeam1MatchWins() {
		team1MatchWins++;
	}

	public static void incrementTeam2MatchWins() {
		team2MatchWins++;
	}

	public static void increaseTeam1RoundWins(long rounds) {
		team1RoundWins += rounds;
	}

	public static void increaseTeam2RoundWins(long rounds) {
		team2RoundWins += rounds;
	}

	public static void incrementTeam1FiftyFiftyWins() {
		team1FiftyFiftyWins++;
	}

	public static void incrementTeam2FiftyFiftyWins() {
		team2FiftyFiftyWins++;
	}

	public static long getTeam1MatchWins() {
		return team1MatchWins;
	}

	public static long getTeam2MatchWins() {
		return team2MatchWins;
	}

	public static long getTeam1RoundWins() {
		return team1RoundWins;
	}

	public static long getTeam2RoundWins() {
		return team2RoundWins;
	}

	public static long getTeam1FiftyFiftyWins() {
		return team1FiftyFiftyWins;
	}

	public static long getTeam2FiftyFiftyWins() {
		return team2FiftyFiftyWins;
	}
}