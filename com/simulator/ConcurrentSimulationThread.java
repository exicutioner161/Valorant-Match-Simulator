package com.simulator;

/**
 * <p>
 * Thread-safe simulation runner that creates its own MatchSimulator instance to
 * avoid sharing state between threads. Each thread maintains independent
 * statistics that are aggregated after completion.
 * </p>
 *
 * @author exicutioner161
 * @version 0.2.1-alpha
 * @see TeamComp
 * @see MatchSimulator
 */

public class ConcurrentSimulationThread extends Thread {
	private static final short NUM_THREADS = (short) Runtime.getRuntime().availableProcessors();
	private final TeamComp team1;
	private final TeamComp team2;
	private final long simulationsToRun;
	private final boolean fastSimulation;
	// Propagated match configuration to ensure local simulator uses the same map
	// and attacker
	private final String map;
	private final int attackingTeam;

	/**
	 * <p>
	 * Constructs a new concurrent simulation thread with the specified parameters.
	 * </p>
	 *
	 * <p>
	 * Creates a thread that will run the specified number of match simulations
	 * using its own isolated MatchSimulator instance to avoid thread conflicts.
	 * The thread can be configured to run either fast or detailed simulations.
	 * </p>
	 *
	 * @param team1            the first team composition for the simulations
	 * @param team2            the second team composition for the simulations
	 * @param simulationsToRun the number of matches this thread should simulate
	 * @param fastSimulation   true for fast simulation mode, false for detailed
	 *                         mode
	 * @param map              the map name to use for balancing (nullable/blank for
	 *                         baseline)
	 * @param attackingTeam    the team that starts attacking (1 or 2)
	 */
	public ConcurrentSimulationThread(TeamComp team1, TeamComp team2,
			long simulationsToRun, boolean fastSimulation, String map, int attackingTeam) {
		this.team1 = team1;
		this.team2 = team2;
		this.simulationsToRun = simulationsToRun;
		this.fastSimulation = fastSimulation;
		this.map = map;
		this.attackingTeam = attackingTeam;
	}

	/**
	 * <p>
	 * Executes the assigned number of match simulations in this thread.
	 * </p>
	 *
	 * <p>
	 * Creates a local MatchSimulator instance to avoid shared state between
	 * threads and runs the specified number of simulations. The simulation
	 * mode (fast or detailed) is determined by the fastSimulation flag set
	 * during construction.
	 * </p>
	 *
	 * <p>
	 * Statistics from each match are automatically collected in the global
	 * SimulationStatisticsCollector for thread-safe aggregation.
	 * </p>
	 */
	@Override
	public void run() {
		// Create per-thread copies of the team compositions to avoid shared mutable
		// state
		TeamComp localTeam1 = (map != null && !map.isBlank()) ? new TeamComp(map) : new TeamComp();
		TeamComp localTeam2 = (map != null && !map.isBlank()) ? new TeamComp(map) : new TeamComp();

		createTeamComps(localTeam1, localTeam2);

		MatchSimulator localMatch = new MatchSimulator(localTeam1, localTeam2);
		if (map != null && !map.isBlank()) {
			localMatch.setMap(map);
		}
		localMatch.setAttackingTeam(attackingTeam == 1 ? 1 : 2);

		if (fastSimulation) {
			simulateMatchesFast(localMatch);
		} else {
			simulateMatches(localMatch);
		}
	}

	/**
	 * <p>
	 * Copies agent selections from the shared team compositions into the provided
	 * local team objects.
	 * </p>
	 *
	 * <p>
	 * This method ensures each thread operates on its own {@link TeamComp}
	 * instances, avoiding shared mutable state during simulations.
	 * </p>
	 *
	 * @param localTeam1 the thread-local copy of team 1 to populate
	 * @param localTeam2 the thread-local copy of team 2 to populate
	 */
	public void createTeamComps(TeamComp localTeam1, TeamComp localTeam2) {
		for (Agent a : team1.getTeamComp()) {
			if (a != null) {
				localTeam1.addAgent(a.getName());
			}
		}
		for (Agent a : team2.getTeamComp()) {
			if (a != null) {
				localTeam2.addAgent(a.getName());
			}
		}
	}

	/**
	 * <p>
	 * Runs the configured number of detailed simulations (with round-by-round
	 * printing).
	 * </p>
	 *
	 * @param localMatch the per-thread {@link MatchSimulator} instance to run
	 */
	public void simulateMatches(MatchSimulator localMatch) {
		for (long i = 0; i < simulationsToRun; i++) {
			localMatch.simulateMatch();
		}
	}

	/**
	 * <p>
	 * Runs the configured number of fast simulations (no console output) for
	 * maximum throughput.
	 * </p>
	 *
	 * @param localMatch the per-thread {@link MatchSimulator} instance to run fast
	 */
	public void simulateMatchesFast(MatchSimulator localMatch) {
		for (long i = 0; i < simulationsToRun; i++) {
			localMatch.simulateMatchFast();
		}
	}

	/**
	 * <p>
	 * Gets the optimal number of threads for concurrent simulation.
	 * </p>
	 *
	 * <p>
	 * Returns the number of available processors on the current system,
	 * which provides an optimal balance between parallelism and resource
	 * utilization for CPU-bound simulation tasks.
	 * </p>
	 *
	 * @return the number of available processors as the optimal thread count
	 */
	public static short getOptimalThreadCount() {
		return NUM_THREADS;
	}
}