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
 * Note: This implementation creates temporary MatchSimulator
 * instances with empty TeamComp objects purely for accessing static data.
 * Consider refactoring MatchSimulator to expose static getter methods directly
 * to eliminate this overhead.
 *
 * @author exicutioner161
 * @version 0.1.8-alpha
 * @see MatchSimulator
 * @see Main
 */
package com.simulator;

public class SimulationStatisticsCollector {

	private SimulationStatisticsCollector() {
		// Private constructor to prevent instantiation
	}

	public static long getTeam1MatchWins() {
		// Create a temporary instance to access the static getter methods
		MatchSimulator temp = new MatchSimulator(new TeamComp(), new TeamComp());
		return temp.getTeam1MatchWins();
	}

	public static long getTeam2MatchWins() {
		MatchSimulator temp = new MatchSimulator(new TeamComp(), new TeamComp());
		return temp.getTeam2MatchWins();
	}

	public static long getTeam1RoundWins() {
		MatchSimulator temp = new MatchSimulator(new TeamComp(), new TeamComp());
		return temp.getTeam1TotalRounds();
	}

	public static long getTeam2RoundWins() {
		MatchSimulator temp = new MatchSimulator(new TeamComp(), new TeamComp());
		return temp.getTeam2TotalRounds();
	}

	public static long getTeam1FiftyFiftyWins() {
		MatchSimulator temp = new MatchSimulator(new TeamComp(), new TeamComp());
		return temp.getTeam1FiftyFiftyWins();
	}

	public static long getTeam2FiftyFiftyWins() {
		MatchSimulator temp = new MatchSimulator(new TeamComp(), new TeamComp());
		return temp.getTeam2FiftyFiftyWins();
	}
}