package com.simulator;

/**
 * Thread-safe simulation runner that creates its own MatchSimulator instance to
 * avoid sharing state between threads. Each thread maintains independent
 * statistics that are aggregated after completion.
 *
 * @author exicutioner161
 * @version 0.2.0-alpha
 * @see TeamComp
 * @see MatchSimulator
 */

public class ConcurrentSimulationThread extends Thread {
	private static final short NUM_THREADS = (short) Runtime.getRuntime().availableProcessors();
	private final TeamComp team1;
	private final TeamComp team2;
	private final long simulationsToRun;
	private final boolean fastSimulation;

	/**
	 * Constructs a new concurrent simulation thread with the specified parameters.
	 *
	 * Creates a thread that will run the specified number of match simulations
	 * using its own isolated MatchSimulator instance to avoid thread conflicts.
	 * The thread can be configured to run either fast or detailed simulations.
	 *
	 * @param team1            the first team composition for the simulations
	 * @param team2            the second team composition for the simulations
	 * @param simulationsToRun the number of matches this thread should simulate
	 * @param fastSimulation   true for fast simulation mode, false for detailed
	 *                         mode
	 */
	public ConcurrentSimulationThread(TeamComp team1, TeamComp team2,
			long simulationsToRun, boolean fastSimulation) {
		this.team1 = team1;
		this.team2 = team2;
		this.simulationsToRun = simulationsToRun;
		this.fastSimulation = fastSimulation;
	}

	/**
	 * Executes the assigned number of match simulations in this thread.
	 *
	 * Creates a local MatchSimulator instance to avoid shared state between
	 * threads and runs the specified number of simulations. The simulation
	 * mode (fast or detailed) is determined by the fastSimulation flag set
	 * during construction.
	 *
	 * Statistics from each match are automatically collected in the global
	 * SimulationStatisticsCollector for thread-safe aggregation.
	 */
	@Override
	public void run() {
		MatchSimulator localMatch = new MatchSimulator(team1, team2);
		if (fastSimulation) {
			for (long i = 0; i < simulationsToRun; i++) {
				localMatch.simulateMatchFast();
			}
		} else {
			for (long i = 0; i < simulationsToRun; i++) {
				localMatch.simulateMatch();
			}
		}
	}

	/**
	 * Gets the optimal number of threads for concurrent simulation.
	 *
	 * Returns the number of available processors on the current system,
	 * which provides an optimal balance between parallelism and resource
	 * utilization for CPU-bound simulation tasks.
	 *
	 * @return the number of available processors as the optimal thread count
	 */
	public static short getOptimalThreadCount() {
		return NUM_THREADS;
	}
}