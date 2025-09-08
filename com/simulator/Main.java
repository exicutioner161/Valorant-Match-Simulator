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
 * The program offers two simulation modes: - Fast simulation: Simplified
 * calculation without detailed output - Regular simulation: Detailed
 * calculation with round-by-round output
 *
 * @author exicutioner161
 * @version 0.1.5-alpha
 * @see TeamComp
 * @see MatchSimulator
 */

public class Main {
   public static void main(String[] arg) {
      TeamComp one = new TeamComp();
      TeamComp two = new TeamComp();
      MatchSimulator match = new MatchSimulator(one, two);
      long matches = 1;
      String in;
      boolean fastSimulation = true;
      Logger logger = Logger.getLogger(Main.class.getName());
      NumberFormat numberFormat = NumberFormat.getInstance();

      // Set up simulation parameters
      System.out.println(
            "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nEnter the agents for each team and the map name. Type 'exit' to quit.");
      try (Scanner input = new Scanner(System.in)) {
         System.out.println(
               """
                     Enter a map name. This will be used for map advantage calculations.
                     Available maps: Abyss, Ascent, Bind, Breeze, Corrode, Fracture, Haven, Icebox, Lotus, Pearl, Split, Sunset, or N/A.""");
         String mapInput = input.nextLine().trim();
         one.setMap(mapInput);
         two.setMap(mapInput);
         match.setMap(mapInput);

         System.out.println("Enter 5 agents per team.");
         match.inputTeamAgents(input);

         System.out.println("Enter the attacking team (1 or 2):");
         if (input.hasNextInt()) {
            match.setAttackingTeam(input.nextInt());
         } else {
            System.out.println("Invalid input. Defaulting to Team 1.");
            match.setAttackingTeam(1);
            input.nextLine(); // Consume newline
         }

         System.out.println("Enter the number of matches to simulate:");
         if (input.hasNextLong()) {
            matches = input.nextLong();
         } else {
            System.out.println("Invalid input. Defaulting to 1 match.");
         }
         input.nextLine(); // Consume newline

         System.out.println("Do you want to run a fast simulation? (Y/N)");
         in = input.nextLine();
         if (in.equalsIgnoreCase("N") || in.equalsIgnoreCase("no")) {
            fastSimulation = false;
         }
      } catch (Exception e) {
         logger.log(Level.SEVERE, "An error occurred while reading input", e);
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
      one.printStats();
      System.out.println("------------------------------------------\nTeam 2 stats:");
      two.printStats();

      System.out.printf(
            "Number of matches simulated: %s%nTeam 1 match record vs Team 2: %s-%s%nTotal rounds won by Team 1 vs Team 2: %s-%s%n50/50 wins won by Team 1 vs Team 2: %s-%s%nMap: %s%n",
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