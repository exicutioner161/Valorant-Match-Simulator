package com.simulator;

import java.text.NumberFormat;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
   public static void main(String[] arg) {
      TeamComp one = new TeamComp();
      TeamComp two = new TeamComp();
      MatchSimulator match = new MatchSimulator(one, two);
      int matches = 1;
      String in;
      boolean fastSimulation = true;
      Logger logger = Logger.getLogger(Main.class.getName());
      NumberFormat numberFormat = NumberFormat.getInstance();

      // Set up simulation parameters
      System.out.println(
            "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nEnter the agents for each team and the map name. Type 'exit' to quit.");
      try (Scanner input = new Scanner(System.in)) {
         System.out.println("Enter 5 agents for Team 1:");
         match.inputTeam1Agents();

         System.out.println("Enter 5 agents for Team 2:");
         match.inputTeam2Agents();

         System.out.println(
               """
                     Enter a map name. This will be used for map advantage calculations.
                     Available maps: Ascent, Bind, Breeze, Corrode, Fracture, Haven, Icebox, Lotus, Pearl, Split, Sunset, or N/A.""");
         match.setMap(input.nextLine());

         System.out.println("Enter the attacking team (1 or 2):");
         if (input.hasNextInt()) {
            match.setAttackingTeam(input.nextInt());
         } else {
            System.out.println("Invalid input. Defaulting to Team 1.");
         }

         System.out.println("Enter the number of matches to simulate:");
         if (input.hasNextInt()) {
            matches = input.nextInt();
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
      System.out.println("Team 2 stats:");
      two.printStats();

      System.out.println("Number of matches simulated: " + numberFormat.format(matches) + "\nMatch record: "
            + numberFormat.format(match.getTeam1MatchWins()) + "-" + numberFormat.format(match.getTeam2MatchWins())
            + "\nTotal rounds won by Team 1 vs Team 2: " + numberFormat.format(match.getTeam1TotalRounds()) + "-"
            + numberFormat.format(match.getTeam2TotalRounds()) + "\n50/50 wins won by Team 1 vs Team 2: "
            + numberFormat.format(match.getTeam1FiftyFiftyWins()) + "-"
            + numberFormat.format(match.getTeam2FiftyFiftyWins()) + "\nMap: " + match.getMap().toUpperCase() + "\n");

      // Calculate and log elapsed time
      double elapsed = (System.nanoTime() - start) / 1000000;
      logger.log(Level.INFO, "{0} ms elapsed ({1} seconds)",
            new Object[] { elapsed, numberFormat.format(Math.round(elapsed) / 1000.0) });
   }
}