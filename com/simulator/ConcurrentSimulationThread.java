package com.simulator;

/**
 * Thread-safe simulation runner that creates its own MatchSimulator instance to
 * avoid sharing state between threads. Each thread maintains independent
 * statistics that are aggregated after completion.
 *
 * @author exicutioner161
 * @version 0.1.9-alpha
 * @see TeamComp
 * @see MatchSimulator
 */
public class ConcurrentSimulationThread extends Thread {
	private static final short NUM_THREADS = (short) Runtime.getRuntime().availableProcessors();
	private final TeamComp team1;
	private final TeamComp team2;
	private final long simulationsToRun;
	private final boolean fastSimulation;

	public ConcurrentSimulationThread(TeamComp team1, TeamComp team2,
			long simulationsToRun, boolean fastSimulation) {
		this.team1 = team1;
		this.team2 = team2;
		this.simulationsToRun = simulationsToRun;
		this.fastSimulation = fastSimulation;
	}

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

	public static short getOptimalThreadCount() {
		return NUM_THREADS;
	}
}