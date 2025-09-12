package com.simulator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for the Valorant Match Simulator application.
 *
 * This class goes through the simulation process by:
 * 1. Displaying the startup message
 * 2. Setting up simulation parameters (map, agents, attacking team, number of
 * matches, simulation mode)
 * 3. Creating and managing multiple concurrent simulation threads for optimal
 * performance
 * 4. Collecting and aggregating results from all threads
 * 5. Displaying comprehensive statistics including team stats, match records,
 * round wins, and performance metrics
 * 6. Logging execution time for performance analysis
 *
 * The method uses multi-threading to maximize simulation throughput,
 * distributing the workload evenly across available CPU cores. Results are
 * synchronized and collected from all threads to provide accurate aggregate
 * statistics.
 *
 * @author exicutioner161
 * @version 0.1.9-alpha
 * @see TeamComp
 * @see MatchSimulator
 */

public class Main {
   private static final Logger logger = Logger.getLogger(Main.class.getName());
   private static final NumberFormat numberFormat = NumberFormat.getInstance();
   private static final TeamComp teamOne = new TeamComp();
   private static final TeamComp teamTwo = new TeamComp();
   private static final MatchSimulator match = new MatchSimulator(teamOne, teamTwo);
   private static final List<ConcurrentSimulationThread> startedThreads = new ArrayList<>();
   private static short numThreads = ConcurrentSimulationThread.getOptimalThreadCount();
   private static long matches = 0;
   private static long startMilliseconds = 0;
   private static boolean fastSimulation = true;

   public static void startupMessage() {
      String largeSeparator = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
      System.out.println(largeSeparator);
      System.out.println("Valorant Match Simulator v0.1.9-alpha");
      System.out.println("By exicutioner161\n");
   }

   public static void exitIfRequested(String str) {
      if (str.equalsIgnoreCase("exit") || str.equals("-1")) {
         System.out.println("Exiting simulation setup.");
         System.exit(0);
      }
   }

   public static void chooseMap(Scanner input, TeamComp one, TeamComp two, MatchSimulator match) {
      System.out.println(
            """
                        Enter a map name. This will be used for map advantage calculations.
                        Available maps: Abyss, Ascent, Bind, Breeze, Corrode, Fracture, Haven, Icebox, Lotus, Pearl, Split, Sunset, or NONE.
                  """);
      String mapInput = "";
      while (!isValidMap(mapInput)) {
         System.out.println("Please enter a valid map name or NONE:");
         mapInput = input.nextLine().trim();
         exitIfRequested(mapInput);
         one.setMap(mapInput);
         two.setMap(mapInput);
         match.setMap(mapInput);
      }
   }

   private static boolean isValidMap(String mapInput) {
      String[] validMaps = { "ABYSS", "ASCENT", "BIND", "BREEZE", "CORRODE", "FRACTURE", "HAVEN", "ICEBOX", "LOTUS",
            "PEARL", "SPLIT", "SUNSET", "NONE" };
      for (String map : validMaps) {
         if (map.equalsIgnoreCase(mapInput)) {
            return true;
         }
      }
      return false;
   }

   public static void inputAgents(Scanner input, MatchSimulator match) {
      System.out.println("Enter 5 agents per team.");
      match.inputTeamAgents(input);
   }

   public static void chooseAttackingTeam(Scanner input, MatchSimulator match) {
      short team = 0;
      System.out.println("Enter the attacking team (1 or 2):");
      while (team != 1 && team != 2) {
         if (input.hasNextShort()) {
            team = input.nextShort();
            input.nextLine(); // Consume the newline
         } else {
            String in = input.nextLine().trim();
            exitIfRequested(in);
            System.out.println("Invalid input. Please enter 1 or 2.");
         }
      }
      match.setAttackingTeam(team);
   }

   public static long returnNumberOfMatchesChoice(Scanner input) {
      long numMatches = 0;
      System.out.println("Enter the number of matches to simulate (must be at least 1):");
      while (numMatches < 1) {
         if (input.hasNextLong()) {
            numMatches = input.nextLong();
            input.nextLine(); // Consume the newline
         } else {
            String in = input.nextLine().trim();
            exitIfRequested(in);
            System.out.println("Invalid input. Please enter a positive integer.");
         }
      }
      return numMatches;
   }

   public static boolean returnFastSimulationChoice(Scanner input) {
      System.out.println("Do you want to run a fast simulation? (Y/N):");
      String in;
      while (true) {
         in = input.nextLine().trim();
         if (in.equalsIgnoreCase("Y") || in.equalsIgnoreCase("YES")) {
            return true;
         } else if (in.equalsIgnoreCase("N") || in.equalsIgnoreCase("NO")) {
            return false;
         } else {
            exitIfRequested(in);
            System.out.println("Invalid input. Please enter Y or N:");
         }
      }
   }

   public static void setUpSimulationParameters(TeamComp teamOne, TeamComp teamTwo, MatchSimulator match) {
      System.out.println("Input simulation parameters. Type 'exit' to quit.");
      try (Scanner input = new Scanner(System.in)) {
         chooseMap(input, teamOne, teamTwo, match);
         System.out.println();

         inputAgents(input, match);
         System.out.println();

         chooseAttackingTeam(input, match);
         System.out.println();

         matches = returnNumberOfMatchesChoice(input);
         System.out.println();

         fastSimulation = returnFastSimulationChoice(input);
         System.out.println("\nSimulation parameters set up successfully.");
      } catch (Exception e) {
         logger.log(Level.SEVERE,
               "An error occurred while setting up simulation parameters.", e);
      }
   }

   public static void adjustNumThreadsIfNecessary() {
      if (numThreads > matches) {
         numThreads = (short) matches;
      }
   }

   public static long getSimulationsForThread(int threadIndex, long simulationsPerThread, long remainderSimulations) {
      if (threadIndex < remainderSimulations) {
         return simulationsPerThread + 1;
      } else {
         return simulationsPerThread;
      }
   }

   public static void checkAvailableThreads(int numThreads) {
      if (numThreads < 1) {
         logger.log(Level.SEVERE, "No threads available! Number of threads available: {0}", numThreads);
         System.exit(0);
      }
   }

   public static void handleCreationOfNewThreads(long simulationsForThisThread) {
      if (simulationsForThisThread < 1) {
         return;
      }
      createNewConcurrentSimulationThread(startedThreads, simulationsForThisThread);
   }

   public static void createNewConcurrentSimulationThread(
         List<ConcurrentSimulationThread> startedThreads,
         long simulationsForThisThread) {
      ConcurrentSimulationThread thread = new ConcurrentSimulationThread(teamOne, teamTwo,
            simulationsForThisThread, fastSimulation);
      thread.start();
      startedThreads.add(thread);
   }

   public static void waitForAllThreadsToFinish() {
      for (ConcurrentSimulationThread currentThread : startedThreads) {
         try {
            if (currentThread != null) {
               currentThread.join();
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.WARNING, "Thread execution was interrupted", e);
         }
      }
   }

   public static void printTeamStats(TeamComp teamOne, TeamComp teamTwo) {
      System.out.println("\nTeam 1 stats:");
      teamOne.printStats();
      System.out.println("------------------------------------------\nTeam 2 stats:");
      teamTwo.printStats();
   }

   public static void printMatchStats() {
      long totalMatchesSimulated = SimulationStatisticsCollector.getTeam1MatchWins()
            + SimulationStatisticsCollector.getTeam2MatchWins();
      System.out.printf(
            "Number of matches simulated: %s%nTeam 1 match record vs Team 2: %s-%s%nTotal rounds won by Team 1 vs Team 2: %s-%s%n50/50 rounds won by Team 1 vs Team 2: %s-%s%nMap: %s%n",
            numberFormat.format(totalMatchesSimulated),
            numberFormat.format(SimulationStatisticsCollector.getTeam1MatchWins()),
            numberFormat.format(SimulationStatisticsCollector.getTeam2MatchWins()),
            numberFormat.format(SimulationStatisticsCollector.getTeam1RoundWins()),
            numberFormat.format(SimulationStatisticsCollector.getTeam2RoundWins()),
            numberFormat.format(SimulationStatisticsCollector.getTeam1FiftyFiftyWins()),
            numberFormat.format(SimulationStatisticsCollector.getTeam2FiftyFiftyWins()),
            match.getMap().toUpperCase());
   }

   public static void startLoggingElapsedTime() {
      startMilliseconds = System.currentTimeMillis();
      logger.log(Level.INFO, "Simulation started...");
   }

   public static void finishLoggingElapsedTime() {
      long elapsedMilliseconds = System.currentTimeMillis() - startMilliseconds;
      double elapsedSeconds = elapsedMilliseconds / 1000.0;
      logger.log(Level.INFO, "{0} ms elapsed ({1} seconds)",
            new Object[] { elapsedMilliseconds, String.format("%.3f", elapsedSeconds) });
   }

   public static void main(String[] arg) {
      // Startup message
      startupMessage();

      // Set up simulation parameters
      setUpSimulationParameters(teamOne, teamTwo, match);

      // Start measuring elapsed time
      startLoggingElapsedTime();

      // Adjust numThreads if necessary before calculating work distribution
      adjustNumThreadsIfNecessary();

      // Run concurrent simulations
      long simulationsPerThread = matches / numThreads;
      long remainderSimulations = matches % numThreads;

      System.out.printf("Running %s simulations across %d threads...%n", numberFormat.format(matches), numThreads);

      // Create and start threads
      checkAvailableThreads(numThreads);
      for (int i = 0; i < numThreads; i++) {
         long simulationsForThisThread = getSimulationsForThread(i, simulationsPerThread, remainderSimulations);
         handleCreationOfNewThreads(simulationsForThisThread);
      }

      // Wait for all threads to complete
      waitForAllThreadsToFinish();
      long totalSimulatedMatches = SimulationStatisticsCollector.getTeam1MatchWins()
            + SimulationStatisticsCollector.getTeam2MatchWins();
      if (totalSimulatedMatches != matches) {
         logger.log(Level.WARNING,
               "Discrepancy in total matches simulated! Expected: {0}, Actual: {1}, Running new single-threaded simulations to compensate.",
               new Object[] { matches,
                     SimulationStatisticsCollector.getTeam1MatchWins()
                           + SimulationStatisticsCollector.getTeam2MatchWins() });
      }

      // Run any remaining simulations in the main thread
      for (long i = 0; i < matches - totalSimulatedMatches; i++) {
         if (fastSimulation) {
            match.simulateMatchFast();
         } else {
            match.simulateMatch();
         }
      }

      // Print simulation results
      printTeamStats(teamOne, teamTwo);
      printMatchStats();
      finishLoggingElapsedTime();
   }
}