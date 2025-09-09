package com.simulator;

import java.text.NumberFormat;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for the Valorant Match Simulator application. This class handles
 * user input for match parameters and executes match simulations.
 *
 * The simulation process includes: 1. Setting up teams and map selection 2.
 * Getting agent selections for both teams 3. Determining attacking/defending
 * sides 4. Running specified number of match simulations 5. Outputting detailed
 * statistics
 *
 * The program offers two simulation modes: - Fast simulation: Quick stats
 * without detailed output - Regular simulation: Detailed round-by-round stats
 *
 * @author exicutioner161
 * @version 0.1.6-alpha
 * @see TeamComp
 * @see MatchSimulator
 * @see TeamComp
 */

public class Main {
   public static void startupMessage() {
      String largeSeparator = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
      System.out.println(largeSeparator);
      System.out.println("Valorant Match Simulator v0.1.5-alpha");
      System.out.println("By exicutioner161\n");
   }

   public static void chooseMap(Scanner input, TeamComp one, TeamComp two, MatchSimulator match) {
      System.out.println(
            """
                  Enter a map name. This will be used for map advantage calculations.
                  Available maps: Abyss, Ascent, Bind, Breeze, Corrode, Fracture, Haven, Icebox, Lotus, Pearl, Split, Sunset, or N/A.""");
      String mapInput = input.nextLine().trim();
      one.setMap(mapInput);
      two.setMap(mapInput);
      match.setMap(mapInput);
   }

   public static void inputAgents(Scanner input, MatchSimulator match) {
      System.out.println("Enter 5 agents per team.");
      match.inputTeamAgents(input);
   }

   public static void chooseAttackingTeam(Scanner input, MatchSimulator match) {
      int team = 0;
      while (team != 1 && team != 2) {
         System.out.println("Enter the attacking team (1 or 2):");
         if (input.hasNextInt()) {
            team = input.nextInt();
         } else {
            System.out.println("Invalid input. Please enter 1 or 2.");
         }
         input.nextLine(); // Consume newline
      }
      match.setAttackingTeam(team);
   }

   public static long returnNumberOfMatchesChoice(Scanner input) {
      long matches = 0;
      while (matches < 1) {
         System.out.println("Enter the number of matches to simulate (must be at least 1):");
         if (input.hasNextLong()) {
            matches = input.nextLong();
         } else {
            System.out.println("Invalid input. Please enter a positive integer.");
         }
         input.nextLine(); // Consume newline
      }
      return matches;
   }

   public static boolean returnFastSimulationChoice(Scanner input) {
      boolean fastSimulation = true;
      System.out.println("Do you want to run a fast simulation? (Y/N)");
      String in = input.nextLine();
      if (in.equalsIgnoreCase("N") || in.equalsIgnoreCase("no")) {
         fastSimulation = false;
      } else if (!in.equalsIgnoreCase("Y") && !in.equalsIgnoreCase("yes")) {
         System.out.println("Invalid input. Defaulting to fast simulation.");
      }
      return fastSimulation;
   }

   public static void main(String[] arg) {
      TeamComp teamOne = new TeamComp();
      TeamComp teamTwo = new TeamComp();
      MatchSimulator match = new MatchSimulator(teamOne, teamTwo);
      long matches = 1;
      boolean fastSimulation = true;
      Logger logger = Logger.getLogger(Main.class.getName());
      NumberFormat numberFormat = NumberFormat.getInstance();

      // Startup message
      startupMessage();

      // Set up simulation parameters
      System.out.println("Input simulation parameters. Type 'exit' to quit.");
      try (Scanner input = new Scanner(System.in)) {
         chooseMap(input, teamOne, teamTwo, match);
         inputAgents(input, match);
         chooseAttackingTeam(input, match);
         matches = returnNumberOfMatchesChoice(input);
         fastSimulation = returnFastSimulationChoice(input);
         System.out.println();
      } catch (Exception e) {
         logger.log(Level.SEVERE, "An error occurred while reading input. This is never supposed to happen.", e);
      }

      // Start measuring elapsed time
      double start = System.nanoTime();

      // Run simulations
      if (fastSimulation) {
         for (long i = 0; i < matches; i++) {
            match.simulateMatchFast();
         }
      } else {
         for (long i = 0; i < matches; i++) {
            match.simulateMatch();
         }
      }

      // Print simulation results
      System.out.println("Team 1 stats:");
      teamOne.printStats();
      System.out.println("------------------------------------------\nTeam 2 stats:");
      teamTwo.printStats();

      System.out.printf(
            "Number of matches simulated: %s%nTeam 1 match record vs Team 2: %s-%s%nTotal rounds won by Team 1 vs Team 2: %s-%s%n50/50 rounds won by Team 1 vs Team 2: %s-%s%nMap: %s%n",
            numberFormat.format(matches), numberFormat.format(match.getTeam1MatchWins()),
            numberFormat.format(match.getTeam2MatchWins()), numberFormat.format(match.getTeam1TotalRounds()),
            numberFormat.format(match.getTeam2TotalRounds()), numberFormat.format(match.getTeam1FiftyFiftyWins()),
            numberFormat.format(match.getTeam2FiftyFiftyWins()), match.getMap().toUpperCase());

      // Calculate and log elapsed time
      double elapsed = (System.nanoTime() - start) / 1000000;
      logger.log(Level.INFO, "{0} ms elapsed ({1} seconds)",
            new Object[] { elapsed, numberFormat.format(Math.round(elapsed) / 1000.0) });
   }
}