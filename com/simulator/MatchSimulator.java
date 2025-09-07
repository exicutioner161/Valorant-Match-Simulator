package com.simulator;

import java.util.Random;
import java.util.Scanner;

/**
 * Simulates matches in a Valorant match between two teams.
 * This class handles game mechanics including round simulation, team composition,
 * map advantages, and match statistics.
 *
 * The simulator takes into account:
 * - Team compositions and their stylistic counters
 * - Map-specific attacker/defender advantages
 * - Team relative power levels
 * - Round and match management
 * - Score tracking and statistics
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
 * @version 0.1.4-alpha
 * @see TeamComp
 */

public class MatchSimulator {
   private int currentRound;
   private int team1Rounds;
   private int team2Rounds;
   private int attackingTeam;
   private int team1MatchWins;
   private int team2MatchWins;
   private int team1TotalRounds;
   private int team2TotalRounds;
   private int currentRoundWinner;
   private int team1FiftyFiftyWins;
   private int team2FiftyFiftyWins;
   private double team1Chance;
   private double cachedMapAdvantage;
   private double cachedRelativePowerAdvantage;
   private boolean team1HasBetterOdds;
   private boolean mapAdvantageCalculated;
   private boolean relativePowerAdvantageCalculated;
   private String map;
   private final Scanner input = new Scanner(System.in);
   private final Random random = new Random();
   private final TeamComp one;
   private final TeamComp two;

   public MatchSimulator(TeamComp first, TeamComp second) {
      currentRound = 1;
      team1Rounds = 0;
      team2Rounds = 0;
      team1MatchWins = 0;
      team2MatchWins = 0;
      team1TotalRounds = 0;
      team2TotalRounds = 0;
      attackingTeam = 1;
      team1FiftyFiftyWins = 0;
      team2FiftyFiftyWins = 0;
      mapAdvantageCalculated = false;
      relativePowerAdvantageCalculated = false;
      map = "ascent";
      one = first;
      two = second;
   }

   // Match simulation methods
   public void simulateMatch() {
      setAttackerMapAdvantage();

      for (int i = 0; i < 12; i++) {
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

      for (int i = 0; i < 12; i++) {
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

   public void inputTeam1Agents() {
      int validAgentsAdded = 0;

      while (validAgentsAdded < 5) {
         String in = input.nextLine();
         if (in.equalsIgnoreCase("exit")) {
            System.exit(0);
         } else {
            if (one.canInputAgent(in)) {
               one.addAgent(in);
               validAgentsAdded++;
            } else {
               System.out.println("Invalid agent name. Please try again.");
            }
         }
      }
   }

   public void inputTeam2Agents() {
      int validAgentsAdded = 0;

      while (validAgentsAdded < 5) {
         String in = input.nextLine();
         if (in.equalsIgnoreCase("exit")) {
            System.exit(0);
         } else {
            if (two.canInputAgent(in)) {
               two.addAgent(in);
               validAgentsAdded++;
            } else {
               System.out.println("Invalid agent name. Please try again.");
            }
         }
      }
   }

   public void setMap(String mapOfMatch) {
      map = mapOfMatch.toLowerCase();
      mapAdvantageCalculated = false; // Reset cache
   }

   // Round simulation methods
   public void simulateRound() {
      setTeamStyles();
      team1Chance = 50 + calculateTeam1Advantage();
      team1HasBetterOdds = random.nextDouble() < (team1Chance / 100.0);

      if (team1Chance == 50) {
         if (random.nextBoolean()) {
            team1Rounds++;
            currentRoundWinner = 1;
            currentRound++;
            team1FiftyFiftyWins++;
            printRoundStats();
         } else {
            team2Rounds++;
            currentRoundWinner = 2;
            currentRound++;
            team2FiftyFiftyWins++;
            printRoundStats();
         }
      } else if (team1HasBetterOdds) {
         team1Rounds++;
         currentRoundWinner = 1;
         printRoundStats();
         currentRound++;
      } else {
         team2Rounds++;
         currentRoundWinner = 2;
         printRoundStats();
         currentRound++;

      }
   }

   public void simulateRoundFast() {
      setTeamStyles();
      team1Chance = 50 + calculateTeam1Advantage();
      team1HasBetterOdds = random.nextDouble() < (team1Chance / 100.0);

      if (team1Chance == 50) {
         if (random.nextBoolean()) {
            team1Rounds++;
            currentRoundWinner = 1;
            team1FiftyFiftyWins++;
         } else {
            team2Rounds++;
            currentRoundWinner = 2;
            team2FiftyFiftyWins++;
         }
      } else if (team1HasBetterOdds) {
         team1Rounds++;
         currentRoundWinner = 1;
      } else {
         team2Rounds++;
         currentRoundWinner = 2;
      }

      currentRound++;
   }

   public void setTeamStyles() {
      one.setStyle();
      two.setStyle();
   }

   // Round logic methods
   public void findAndSetRoundWinner() {
      if (team1Chance == 50) {
         if (random.nextBoolean()) {
            team1Rounds++;
            currentRoundWinner = 1;
            team1FiftyFiftyWins++;
         } else {
            team2Rounds++;
            currentRoundWinner = 2;
            team2FiftyFiftyWins++;
         }
      } else if (team1HasBetterOdds) {
         team1Rounds++;
         currentRoundWinner = 1;
      } else {
         team2Rounds++;
         currentRoundWinner = 2;
      }
   }

   public double calculateTeam1Advantage() {
      double adv = random.nextDouble() * 4 + 1;
      setRelativePowerAdvantage();

      if (attackingTeam == 1) { // Team 1 is attacking
         double team1AttackerAdv = cachedRelativePowerAdvantage + cachedMapAdvantage;
         if (one.canCounter(two)) {
            return adv + team1AttackerAdv;
         } else if (two.canCounter(one)) {
            return -adv + team1AttackerAdv;
         }
         return team1AttackerAdv;
      } else { // Team 2 is attacking
         double team2AttackerAdv = cachedRelativePowerAdvantage - cachedMapAdvantage;
         if (one.canCounter(two)) {
            return adv + team2AttackerAdv;
         } else if (two.canCounter(one)) {
            return -adv + team2AttackerAdv;
         }
         return team2AttackerAdv;
      }
   }

   public void setRelativePowerAdvantage() {
      cachedRelativePowerAdvantage = 0;
      int count = 0;
      double totalRelativePowerDelta = Math.abs(one.getTotalRelativePower() - two.getTotalRelativePower());

      // Team 1 has more relative power
      if (one.getTotalRelativePower() > two.getTotalRelativePower()) {
         while (count < totalRelativePowerDelta) {
            cachedRelativePowerAdvantage += 0.2;
            count++;
         }
      } else if (one.getTotalRelativePower() < two.getTotalRelativePower()) { // Team 2 has more relative power
         while (count < totalRelativePowerDelta) {
            cachedRelativePowerAdvantage -= 0.2;
            count++;
         }
      }
      relativePowerAdvantageCalculated = true;
   }

   public void setAttackerMapAdvantage() {
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
   public boolean nobodyHas13Rounds() {
      return team1Rounds < 13 && team2Rounds < 13;
   }

   public boolean overtimeIsReached() {
      return team1Rounds == 12 && team2Rounds == 12;
   }

   public boolean roundDeltaIsNot2() {
      return Math.abs(team1Rounds - team2Rounds) != 2;
   }

   // Game state management methods
   public void resetMatch() {
      team1Rounds = 0;
      team2Rounds = 0;
      currentRound = 1;
      attackingTeam = 1;
   }

   public void switchSides() {
      if (attackingTeam == 1) {
         attackingTeam = 2;
      } else {
         attackingTeam = 1;
      }
   }

   public void setAttackingTeam(int team) {
      if (team != 1 && team != 2) {
         attackingTeam = 1;
      } else {
         attackingTeam = team;
      }
   }

   public void incrementMatchWins() {
      if (team1Rounds > team2Rounds) {
         team1MatchWins++;
      } else {
         team2MatchWins++;
      }
   }

   public void addTotalRounds() {
      team1TotalRounds += team1Rounds;
      team2TotalRounds += team2Rounds;
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
            team2Rounds, one.getStyle().toString(), two.getStyle().toString());
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

   public int getTeam1TotalRounds() {
      return team1TotalRounds;
   }

   public int getTeam2TotalRounds() {
      return team2TotalRounds;
   }

   public int getTeam1FiftyFiftyWins() {
      return team1FiftyFiftyWins;
   }

   public int getTeam2FiftyFiftyWins() {
      return team2FiftyFiftyWins;
   }

   public int getTeam1MatchWins() {
      return team1MatchWins;
   }

   public int getTeam2MatchWins() {
      return team2MatchWins;
   }

   public int getCurrentTeam1Rounds() {
      return team1Rounds;
   }

   public int getCurrentTeam2Rounds() {
      return team2Rounds;
   }

   public int getCurrentRound() {
      return currentRound;
   }

   public int getAttackingTeam() {
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