package com.simulator;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simulates matches in a Valorant match between two teams. This class handles
 * game mechanics including round simulation, team composition creation, map
 * advantages, and match statistics.
 *
 * The simulator takes into account:
 * - Team compositions and their stylistic counters
 * - Map-specific attacker/defender advantages
 * - Team relative power levels
 * - Randomness to simulate unpredictability
 *
 * A standard match consists of:
 * - First to 13 rounds
 * - Side swap after 12 rounds
 * - Overtime rules when tied at 12-12
 * - Teams need a 2-round lead to win in overtime
 *
 * Features:
 * - Regular and fast simulation modes
 * - Team agent input functionality
 * - Map selection with corresponding advantages
 * - Detailed round and match statistics
 * - Probability-based round outcomes
 *
 * @author exicutioner161
 * @version 0.1.9-alpha
 * @see TeamComp
 */

public class MatchSimulator {
   private static final byte ROUNDS_PER_HALF = 12;
   private static final byte ROUNDS_TO_WIN = 13;
   private static final byte TEAM_SIZE = 5;
   private static final double FIFTY_FIFTY_CHANCE = 50.0;
   private static final Logger logger = Logger.getLogger(MatchSimulator.class.getName());
   private short currentRound;
   private short team1Rounds;
   private short team2Rounds;
   private short attackingTeam;
   private short currentRoundWinner;
   private double team1Chance;
   private double cachedMapAdvantage;
   private double cachedRelativePowerAdvantage;
   private boolean team1HasBetterOdds;
   private boolean mapAdvantageCalculated;
   private boolean relativePowerAdvantageCalculated;
   private String map;
   private final TeamComp teamOne;
   private final TeamComp teamTwo;

   public MatchSimulator(TeamComp first, TeamComp second) {
      currentRound = 1;
      team1Rounds = 0;
      team2Rounds = 0;
      attackingTeam = 1;
      mapAdvantageCalculated = false;
      relativePowerAdvantageCalculated = false;
      map = "ascent";
      teamOne = first;
      teamTwo = second;
   }

   // Match simulation methods
   public void simulateMatch() {
      setAttackerMapAdvantage();

      for (int i = 0; i < ROUNDS_PER_HALF; i++) {
         simulateRound();
      }

      switchSides();

      while (nobodyHas13Rounds() && !overtimeIsReached()) {
         simulateRound();
      }

      if (overtimeIsReached()) {
         while (roundDeltaIsNot2()) {
            switchSides();
            simulateRound();
         }
      }

      incrementMatchWins();
      addTotalRounds();
      printMatchWinner();
      resetMatch();
   }

   public void simulateMatchFast() {
      setAttackerMapAdvantage();

      for (int i = 0; i < ROUNDS_PER_HALF; i++) {
         simulateRoundFast();
      }

      switchSides();

      while (nobodyHas13Rounds() && !overtimeIsReached()) {
         simulateRoundFast();
      }

      if (overtimeIsReached()) {
         while (roundDeltaIsNot2()) {
            switchSides();
            simulateRoundFast();
         }
      }

      incrementMatchWins();
      addTotalRounds();
      resetMatch();
   }

   public void inputTeamAgents(Scanner input) {
      try {
         System.out.println("Enter your agents for Team 1:");
         teamInputLoop(input, teamOne);
         System.out.println("Enter your agents for Team 2:");
         teamInputLoop(input, teamTwo);
      } catch (Exception e) {
         logger.log(Level.SEVERE, "An error occurred while reading agent input", e);
      }
   }

   public void setMap(String mapOfMatch) {
      if (mapOfMatch == null) {
         throw new IllegalArgumentException("Invalid input in setMap. Map name cannot be null.");
      }
      map = mapOfMatch.toLowerCase();
      mapAdvantageCalculated = false;
   }

   private void teamInputLoop(Scanner input, TeamComp team) {
      int validAgentsAdded = 0;
      while (validAgentsAdded < TEAM_SIZE) {
         String agentName = input.nextLine();
         if (agentName.equalsIgnoreCase("exit")) {
            System.exit(0);
         } else if (team.canInputAgent(agentName)) {
            team.addAgent(agentName);
            validAgentsAdded++;
         } else {
            System.out.println("Invalid agent name. Please try again.");
         }
      }
   }

   // Round simulation methods
   private void simulateRound() {
      setTeamStyles();
      team1Chance = FIFTY_FIFTY_CHANCE + calculateTeam1Advantage();
      team1HasBetterOdds = ThreadLocalRandom.current().nextDouble() < (team1Chance / 100.0);
      findAndSetRoundWinner();
      printRoundStats();
      currentRound++;
   }

   private void simulateRoundFast() {
      setTeamStyles();
      team1Chance = FIFTY_FIFTY_CHANCE + calculateTeam1Advantage();
      team1HasBetterOdds = ThreadLocalRandom.current().nextDouble() < (team1Chance / 100.0);
      findAndSetRoundWinner();
      currentRound++;
   }

   private void setTeamStyles() {
      teamOne.setStyle();
      teamTwo.setStyle();
   }

   // Round logic methods
   private void findAndSetRoundWinner() {
      if (team1Chance == FIFTY_FIFTY_CHANCE) {
         if (ThreadLocalRandom.current().nextBoolean()) { // True 50/50 scenario
            team1Rounds++;
            currentRoundWinner = 1;
            SimulationStatisticsCollector.incrementTeam1FiftyFiftyWins();
         } else {
            team2Rounds++;
            currentRoundWinner = 2;
            SimulationStatisticsCollector.incrementTeam2FiftyFiftyWins();
         }
      } else if (team1HasBetterOdds) { // Team 1 has better odds
         team1Rounds++;
         currentRoundWinner = 1;
      } else { // Team 2 has better odds
         team2Rounds++;
         currentRoundWinner = 2;
      }
   }

   private double calculateTeam1Advantage() {
      double stylisticAdvantage = ThreadLocalRandom.current().nextDouble() * 4 + 1;
      boolean team1CanCounter = teamOne.canCounter(teamTwo);
      boolean team2CanCounter = teamTwo.canCounter(teamOne);
      if (attackingTeam == 1) { // Team 1 is attacking
         double attackingAdvantage = getRelativePowerAdvantage() + cachedMapAdvantage;
         if (team1CanCounter && !team2CanCounter) {
            return attackingAdvantage + stylisticAdvantage;
         } else if (!team1CanCounter && team2CanCounter) {
            return attackingAdvantage - stylisticAdvantage;
         }
         return attackingAdvantage;
      } else if (attackingTeam == 2) { // Team 2 is attacking
         double attackingAdvantage = getRelativePowerAdvantage() - cachedMapAdvantage;
         if (!team1CanCounter && team2CanCounter) {
            return attackingAdvantage + stylisticAdvantage;
         } else if (team1CanCounter && !team2CanCounter) {
            return attackingAdvantage - stylisticAdvantage;
         }
         return attackingAdvantage;
      }

      // This should never be reached
      logger.log(Level.SEVERE, "Invalid attacking team in calculateTeam1Advantage: {0}", attackingTeam);
      return 0.0;
   }

   private void setRelativePowerAdvantage() {
      cachedRelativePowerAdvantage = 0;
      double totalRelativePowerDelta = Math.abs(teamOne.getTotalRelativePower() - teamTwo.getTotalRelativePower());
      for (int i = 0; i < totalRelativePowerDelta; i++) {
         cachedRelativePowerAdvantage += 0.2;
      }
      if (teamTwo.getTotalRelativePower() > teamOne.getTotalRelativePower()) { // Team 2 has more relative power
         cachedRelativePowerAdvantage = -cachedRelativePowerAdvantage;
      }
      relativePowerAdvantageCalculated = true;
   }

   private void setAttackerMapAdvantage() {
      cachedMapAdvantage = switch (map) {
         case "abyss" -> -0.1;
         case "ascent" -> -5.05;
         case "bind" -> -3.81;
         case "breeze" -> 1.11;
         case "corrode" -> -0.96;
         case "fracture" -> 1;
         case "haven" -> -1.68;
         case "icebox" -> -1.35;
         case "lotus" -> 0.57;
         case "pearl" -> -1.6;
         case "split" -> -3.3;
         case "sunset" -> -1.39;
         default -> 0.0;
      };
      if (cachedMapAdvantage == 0.0) {
         map = "N/A";
      }
      mapAdvantageCalculated = true;
   }

   // Game state check methods
   private boolean nobodyHas13Rounds() {
      return team1Rounds < ROUNDS_TO_WIN && team2Rounds < ROUNDS_TO_WIN;
   }

   private boolean overtimeIsReached() {
      return team1Rounds == ROUNDS_PER_HALF && team2Rounds == ROUNDS_PER_HALF;
   }

   private boolean roundDeltaIsNot2() {
      return Math.abs(team1Rounds - team2Rounds) != 2;
   }

   // Game state management methods
   public void setAttackingTeam(short team) {
      attackingTeam = switch (team) {
         case 1, 2 -> team;
         default -> throw new IllegalArgumentException("Team must be 1 or 2, got: " + team);
      };
   }

   public void incrementMatchWins() {
      if (team1Rounds > team2Rounds) {
         SimulationStatisticsCollector.incrementTeam1MatchWins();
      } else {
         SimulationStatisticsCollector.incrementTeam2MatchWins();
      }
   }

   public void addTotalRounds() {
      SimulationStatisticsCollector.increaseTeam1RoundWins(team1Rounds);
      SimulationStatisticsCollector.increaseTeam2RoundWins(team2Rounds);
   }

   private void resetMatch() {
      team1Rounds = 0;
      team2Rounds = 0;
      currentRound = 1;
      attackingTeam = 1;
   }

   private void switchSides() {
      if (attackingTeam == 1) {
         attackingTeam = 2;
      } else {
         attackingTeam = 1;
      }
   }

   // Display methods
   public void printMatchWinner() {
      System.out.printf("Winner of the match: Team %d%n%n", getMatchWinner());
   }

   public void printRoundStats() {
      double team1RoundedChance = Math.round(team1Chance * 100) / 100.0;
      System.out.printf(
            "Current Round: %d%nAttackers: Team %d%nTeam 1's odds: %.2f%%%nTeam 2's odds: %.2f%%%nRound Winner: Team %d%nTeam 1 rounds: %d%nTeam 2 rounds: %d%nStyles: %s vs %s%n%n",
            currentRound, attackingTeam, team1RoundedChance, 100 - team1RoundedChance, currentRoundWinner, team1Rounds,
            team2Rounds, teamOne.getStyle().toString(), teamTwo.getStyle().toString());
   }

   // Getter methods
   public String getMap() {
      return map;
   }

   public int getMatchWinner() {
      if (team1Rounds > team2Rounds) {
         return 1;
      }
      return 2;
   }

   public long getCurrentTeam1Rounds() {
      return team1Rounds;
   }

   public long getCurrentTeam2Rounds() {
      return team2Rounds;
   }

   public long getCurrentRound() {
      return currentRound;
   }

   public long getAttackingTeam() {
      return attackingTeam;
   }

   public double getRelativePowerAdvantage() {
      if (!relativePowerAdvantageCalculated) {
         setRelativePowerAdvantage();
      }
      return cachedRelativePowerAdvantage;
   }

   public double getAttackerMapAdvantage() {
      if (!mapAdvantageCalculated) {
         setAttackerMapAdvantage();
      }
      return cachedMapAdvantage;
   }
}