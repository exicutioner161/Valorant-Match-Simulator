package com.simulator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Main class for the Valorant Match Simulator application.
 * </p>
 *
 * <p>
 * This class serves as the entry point and orchestrates the entire simulation
 * process,
 * supporting both GUI and console modes of operation. It handles user
 * interaction,
 * simulation configuration, multi-threaded execution, and result presentation.
 * </p>
 *
 * <p>
 * The simulator allows users to:
 * </p>
 * <ul>
 * <li>Configure team compositions with 5 agents each</li>
 * <li>Select maps for advantage calculations</li>
 * <li>Choose attacking teams and simulation parameters</li>
 * <li>Run concurrent simulations across multiple threads</li>
 * <li>View detailed statistics and performance metrics</li>
 * </ul>
 *
 * <p>
 * Key Features:
 * </p>
 * <ul>
 * <li>Multi-threaded simulation execution for performance</li>
 * <li>Support for all Valorant maps and agents</li>
 * <li>Fast simulation mode for large batch runs</li>
 * <li>Comprehensive logging and timing measurements</li>
 * <li>Graceful error handling and input validation</li>
 * </ul>
 *
 * <p>
 * Usage:
 * </p>
 * <ul>
 * <li>Default: Launches GUI mode</li>
 * <li>Runs in console mode with --console argument</li>
 * </ul>
 *
 * @author exicutioner161
 * @version 1.0
 * @see MatchSimulator
 * @see TeamComp
 * @see SimulationStatisticsCollector
 * @see ConcurrentSimulationThread
 * @see SimulatorApp
 */

public class Main {
   private static final Logger logger = Logger.getLogger(Main.class.getName());
   private static final NumberFormat numberFormat = NumberFormat.getInstance();
   private static final TeamComp teamOne = new TeamComp();
   private static final TeamComp teamTwo = new TeamComp();
   private static final MatchSimulator match = new MatchSimulator(teamOne, teamTwo);
   private static final List<ConcurrentSimulationThread> startedThreads = new ArrayList<>();
   private static int numThreads = (int) Math.max(1, (ConcurrentSimulationThread.getOptimalThreadCount() * 0.8));
   private static long matches = 0;
   private static long startMilliseconds = 0;
   private static boolean fastSimulation = true;
   private static boolean consoleMode = false;

   /**
    * <p>
    * Displays the application startup message and version information.
    * </p>
    *
    * <p>
    * Clears the console with multiple newlines and shows the application title,
    * version number, and author information. This creates a clean, professional
    * appearance when the application starts.
    * </p>
    */
   public static void startupMessage() {
      String largeSeparator = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
      System.out.println(largeSeparator);
      System.out.println("Valorant Match Simulator v1.0");
      System.out.println("By exicutioner161\n");
   }

   /**
    * <p>
    * Checks if the user has requested to exit the application.
    * </p>
    *
    * <p>
    * Monitors for exit commands ("exit" case-insensitive or "-1") and
    * immediately terminates the application with a goodbye message.
    * This provides a consistent way to exit from any input prompt.
    * </p>
    *
    * @param str The input string to check for exit commands
    */
   public static void exitIfRequested(String str) {
      if (str.equalsIgnoreCase("exit") || str.equals("-1")) {
         System.out.println("Exiting simulation setup.");
         System.exit(0);
      }
   }

   /**
    * <p>
    * Prompts the user to select a map for the match simulation.
    * </p>
    *
    * <p>
    * This method displays available Valorant maps and allows the user to select
    * one for map advantage calculations. The selected map is set for both team
    * compositions and the match simulator. If "NONE" is selected, no map
    * advantage will be applied.
    * </p>
    *
    * @param input the Scanner for user input
    * @param one   the first team composition
    * @param two   the second team composition
    * @param match the match simulator to configure
    */
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

   /**
    * <p>
    * Validates whether the input string is a recognized Valorant map name.
    * </p>
    *
    * @param mapInput the user input to validate (case-insensitive)
    * @return true if the input matches a valid map name or "NONE", false otherwise
    */
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

   /**
    * <p>
    * Prompts the user to input agent selections for both teams.
    * </p>
    *
    * <p>
    * This method delegates the agent input process to the MatchSimulator,
    * which handles the collection of 5 agents per team.
    * </p>
    *
    * @param input the Scanner for user input
    * @param match the match simulator to configure with agent selections
    */
   public static void inputAgents(Scanner input, MatchSimulator match) {
      System.out.println("Enter 5 agents per team.");
      match.inputTeamAgents(input);
   }

   /**
    * <p>
    * Prompts the user to select which team starts as attackers.
    * </p>
    *
    * <p>
    * This method validates user input to ensure only team 1 or 2 can be selected
    * as the initial attacking team. The selection is then applied to the match
    * simulator configuration.
    * </p>
    *
    * @param input the Scanner for user input
    * @param match the match simulator to configure with the attacking team
    */
   public static void chooseAttackingTeam(Scanner input, MatchSimulator match) {
      int team = 0;
      System.out.println("Enter the attacking team (1 or 2):");
      while (team != 1 && team != 2) {
         if (input.hasNextInt()) {
            team = input.nextInt();
            input.nextLine(); // Consume the newline
         } else {
            String in = input.nextLine().trim();
            exitIfRequested(in);
            System.out.println("Invalid input. Please enter 1 or 2.");
         }
      }
      match.setAttackingTeam(team);
   }

   /**
    * <p>
    * Prompts the user to specify the number of matches to simulate.
    * </p>
    *
    * <p>
    * This method validates that the input is a positive integer (at least 1)
    * and continues prompting until valid input is provided.
    * </p>
    *
    * @param input the Scanner for user input
    * @return the number of matches to simulate (guaranteed to be >= 1)
    */
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

   /**
    * <p>
    * Prompts the user to choose between fast or detailed simulation mode.
    * </p>
    *
    * <p>
    * Fast simulation skips detailed round-by-round output for better performance
    * when running large numbers of matches.
    * </p>
    *
    * @param input the Scanner for user input
    * @return true if fast simulation is requested, false for detailed simulation
    */
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

   /**
    * <p>
    * Orchestrates the complete setup of simulation parameters through user
    * interaction.
    * </p>
    *
    * <p>
    * This method guides the user through all necessary configuration steps:
    * </p>
    * <ul>
    * <li>Map selection for advantage calculations</li>
    * <li>Agent selection for both teams</li>
    * <li>Attacking team designation</li>
    * <li>Number of matches to simulate</li>
    * <li>Simulation mode (fast or detailed)</li>
    * </ul>
    *
    * <p>
    * All user input is validated and the configuration is applied to the
    * provided team compositions and match simulator.
    * </p>
    *
    * @param teamOne the first team composition to configure
    * @param teamTwo the second team composition to configure
    * @param match   the match simulator to configure
    */
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

   /**
    * <p>
    * Adjusts the number of threads to not exceed the number of matches.
    * </p>
    *
    * <p>
    * This prevents creating more threads than matches to simulate, which
    * would result in idle threads and wasted resources.
    * </p>
    */
   public static void adjustNumThreadsIfNecessary() {
      if (numThreads > matches) {
         numThreads = (short) matches;
      }
   }

   /**
    * <p>
    * Calculates the number of simulations assigned to a specific thread.
    * </p>
    *
    * <p>
    * Distributes the total matches evenly across threads, with remainder
    * matches distributed to the first few threads to ensure all matches
    * are simulated exactly once.
    * </p>
    *
    * @param threadIndex          the index of the thread
    * @param simulationsPerThread the base number of simulations per thread
    * @param remainderSimulations the number of extra simulations to distribute
    * @return the total number of simulations for this specific thread
    */
   public static long getSimulationsForThread(int threadIndex, long simulationsPerThread, long remainderSimulations) {
      if (threadIndex < remainderSimulations) {
         return simulationsPerThread + 1;
      } else {
         return simulationsPerThread;
      }
   }

   /**
    * <p>
    * Validates that at least one thread is available for simulation.
    * </p>
    *
    * <p>
    * If no threads are available, logs a severe error and terminates
    * the application since simulation cannot proceed.
    * </p>
    *
    * @param numThreads the number of available threads
    */
   public static void checkAvailableThreads(int numThreads) {
      if (numThreads < 1) {
         logger.log(Level.SEVERE, "No threads available! Number of threads available: {0}", numThreads);
         System.exit(0);
      }
   }

   /**
    * <p>
    * Creates a new simulation thread if the thread has simulations assigned.
    * </p>
    *
    * <p>
    * This method only creates a thread if it has at least one simulation
    * to run, preventing the creation of unnecessary idle threads.
    * </p>
    *
    * @param simulationsForThisThread the number of simulations assigned to this
    *                                 thread
    */
   public static void handleCreationOfNewThreads(long simulationsForThisThread) {
      if (simulationsForThisThread < 1) {
         return;
      }
      createNewConcurrentSimulationThread(startedThreads, simulationsForThisThread);
   }

   /**
    * <p>
    * Creates and starts a new concurrent simulation thread.
    * </p>
    *
    * <p>
    * Instantiates a new ConcurrentSimulationThread with the current team
    * configurations and simulation parameters, starts the thread, and
    * adds it to the list of active threads for tracking.
    * </p>
    *
    * @param startedThreads           the list to add the new thread to for
    *                                 tracking
    * @param simulationsForThisThread the number of matches this thread should
    *                                 simulate
    */
   public static void createNewConcurrentSimulationThread(
         List<ConcurrentSimulationThread> startedThreads,
         long simulationsForThisThread) {
      // Propagate current map and attacking team settings
      String currentMap = match.getMap();
      int currentAttacker = (int) match.getAttackingTeam();
      ConcurrentSimulationThread thread = new ConcurrentSimulationThread(teamOne, teamTwo,
            simulationsForThisThread, fastSimulation, currentMap, currentAttacker);
      thread.start();
      startedThreads.add(thread);
   }

   /**
    * <p>
    * Waits for all simulation threads to complete execution.
    * </p>
    *
    * <p>
    * This method joins each thread in the started threads list, ensuring
    * that the main thread waits for all simulations to complete before
    * proceeding to result aggregation and display.
    * </p>
    *
    * <p>
    * Handles InterruptedException by setting the interrupt flag and logging
    * a warning, allowing graceful degradation if threads are interrupted.
    * </p>
    */
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

   /**
    * <p>
    * Displays comprehensive statistics for both team compositions.
    * </p>
    *
    * <p>
    * This method prints detailed information about each team including
    * agent compositions, relative power calculations, and other relevant
    * statistics used in the simulation.
    * </p>
    *
    * @param teamOne the first team composition to display
    * @param teamTwo the second team composition to display
    */
   public static void printTeamStats(TeamComp teamOne, TeamComp teamTwo) {
      System.out.println("\nTeam 1 stats:");
      teamOne.printStats();
      System.out.println("------------------------------------------\nTeam 2 stats:");
      teamTwo.printStats();
   }

   /**
    * <p>
    * Displays comprehensive match simulation statistics.
    * </p>
    *
    * <p>
    * This method presents formatted statistics including:
    * </p>
    * <ul>
    * <li>Total number of matches simulated</li>
    * <li>Match win records for both teams</li>
    * <li>Total round wins for both teams</li>
    * <li>50/50 round outcomes for both teams</li>
    * <li>Map used for the simulation</li>
    * </ul>
    *
    * <p>
    * All numbers are formatted with locale-appropriate separators for readability.
    * </p>
    */
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

   /**
    * <p>
    * Initializes timing measurement for the simulation.
    * </p>
    *
    * <p>
    * Records the current system time as the start time and logs
    * the simulation start event for performance analysis.
    * </p>
    */
   public static void startLoggingElapsedTime() {
      startMilliseconds = System.currentTimeMillis();
      logger.log(Level.INFO, "Simulation started...");
   }

   /**
    * <p>
    * Completes timing measurement and logs the elapsed simulation time.
    * </p>
    *
    * <p>
    * Calculates the total elapsed time since startLoggingElapsedTime()
    * was called and logs both milliseconds and formatted seconds for
    * performance analysis.
    * </p>
    */
   public static void finishLoggingElapsedTime() {
      long elapsedMilliseconds = System.currentTimeMillis() - startMilliseconds;
      double elapsedSeconds = elapsedMilliseconds / 1000.0;
      logger.log(Level.INFO, "{0} ms elapsed ({1} seconds)",
            new Object[] { elapsedMilliseconds, String.format("%.3f", elapsedSeconds) });
   }

   public static void launchGUI(String[] args) {
      try {
         System.out.println("Launching Valorant Match Simulator GUI...");
         SimulatorApp.main(args);
         consoleMode = false; // GUI launched successfully
      } catch (Exception e) {
         System.err.println("Failed to launch GUI mode: " + e.getMessage());
         System.out.println("Falling back to console mode...");
         consoleMode = true;
      }
   }

   public static void runConsoleMode() {
      // Console mode - original simulation logic
      // Startup message
      startupMessage();

      // Set up simulation parameters
      setUpSimulationParameters(teamOne, teamTwo, match);

      // Start measuring elapsed time
      startLoggingElapsedTime();

      // Adjust numThreads if necessary before calculating work distribution
      adjustNumThreadsIfNecessary();

      // Ensure numThreads is at least 1 to avoid division by zero
      if (numThreads < 1) {
         numThreads = 1;
      }

      // Run concurrent simulations
      long simulationsPerThread = matches / numThreads;
      long remainderSimulations = matches % numThreads;

      System.out.printf("Running %s simulations across %d threads...%n", numberFormat.format(matches), numThreads);

      long totalSimulatedMatches = 0;

      while (totalSimulatedMatches < matches) {
         // Clear the startedThreads list to avoid joining finished threads multiple
         // times
         startedThreads.clear();
         // Create and start threads
         checkAvailableThreads(numThreads);
         for (int i = 0; i < numThreads; i++) {
            long simulationsForThisThread = getSimulationsForThread(i, simulationsPerThread, remainderSimulations);
            handleCreationOfNewThreads(simulationsForThisThread);
         }

         // Wait for all threads to complete
         waitForAllThreadsToFinish();
         totalSimulatedMatches = SimulationStatisticsCollector.getTeam1MatchWins()
               + SimulationStatisticsCollector.getTeam2MatchWins();
         simulationsPerThread = (matches - totalSimulatedMatches) / numThreads;
         remainderSimulations = (matches - totalSimulatedMatches) % numThreads;
      }

      // Print simulation results
      printTeamStats(teamOne, teamTwo);
      printMatchStats();
      finishLoggingElapsedTime();
   }

   /**
    * <p>
    * Main method for the Valorant Match Simulator application.
    * </p>
    *
    * <p>
    * Provides two modes of operation:
    * </p>
    * <ul>
    * <li>GUI Mode: Launches JavaFX interface (default)</li>
    * <li>Console Mode: Traditional command-line simulation (use --console
    * argument)</li>
    * </ul>
    *
    * @param args Command line arguments - use "--console" for console mode
    */
   public static void main(String[] args) {
      // Debug: Print arguments received
      System.out.println("Arguments received: " + java.util.Arrays.toString(args));

      // Check for console mode argument
      if (args.length > 0) {
         for (String arg : args) {
            if ("--console".equalsIgnoreCase(arg)) {
               consoleMode = true;
               System.out.println("Console mode detected!");
               break;
            }
         }
      }

      if (consoleMode) {
         runConsoleMode();
      } else {
         launchGUI(args);
      }
   }
}