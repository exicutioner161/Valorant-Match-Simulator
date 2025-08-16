package com.simulator;

import java.util.Random;
import java.util.Scanner;

public class MatchSimulator {
   private int currentRound;
   private int t1Rounds;
   private int t2Rounds;
   private int attackingTeam;
   private int t1MatchWins;
   private int t2MatchWins;
   private int t1TotalRounds;
   private int t2TotalRounds;
   private int currentRoundWinner;
   private int team1FiftyFiftyWins;
   private int team2FiftyFiftyWins;
   private double t1Chance;
   private boolean t1HasBetterOdds;
   private double cachedMapAdvantage;
   private double cachedRelativePowerAdvantage;
   private boolean mapAdvantageCalculated = false;
   private boolean relativePowerAdvantageCalculated = false;
   private String map;
   private final Scanner input = new Scanner(System.in);
   private final Random random = new Random();
   private final TeamComp one;
   private final TeamComp two;

   public MatchSimulator(TeamComp first, TeamComp second) {
      currentRound = 1;
      t1Rounds = 0;
      t2Rounds = 0;
      t1MatchWins = 0;
      t2MatchWins = 0;
      t1TotalRounds = 0;
      t2TotalRounds = 0;
      attackingTeam = 1;
      team1FiftyFiftyWins = 0;
      team2FiftyFiftyWins = 0;
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
      for (int i = 0; i < 5; i++) {
         String in = input.nextLine();
         if (in.equalsIgnoreCase("exit")) {
            System.exit(0);
         } else {
            one.addAgent(in);
         }
      }
   }

   public void inputTeam2Agents() {
      for (int i = 0; i < 5; i++) {
         String in = input.nextLine();
         if (in.equalsIgnoreCase("exit")) {
            System.exit(0);
         } else {
            two.addAgent(in);
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
      t1Chance = 50 + getTeam1Advantage();
      t1HasBetterOdds = random.nextDouble() < (t1Chance / 100.0);

      if (t1Chance == 50) {
         if (random.nextBoolean()) {
            t1Rounds++;
            currentRoundWinner = 1;
            team1FiftyFiftyWins++;
         } else {
            t2Rounds++;
            currentRoundWinner = 2;
            team2FiftyFiftyWins++;
         }
      } else if (t1HasBetterOdds) {
         t1Rounds++;
         currentRoundWinner = 1;
      } else {
         t2Rounds++;
         currentRoundWinner = 2;
      }

      printRoundStats();
      currentRound++;
   }

   public void simulateRoundFast() {
      setTeamStyles();
      t1Chance = 50 + getTeam1Advantage();
      t1HasBetterOdds = random.nextDouble() < (t1Chance / 100.0);

      if (t1Chance == 50) {
         if (random.nextBoolean()) {
            t1Rounds++;
            currentRoundWinner = 1;
            team1FiftyFiftyWins++;
         } else {
            t2Rounds++;
            currentRoundWinner = 2;
            team2FiftyFiftyWins++;
         }
      } else if (t1HasBetterOdds) {
         t1Rounds++;
         currentRoundWinner = 1;
      } else {
         t2Rounds++;
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
      if (t1Chance == 50) {
         if (random.nextBoolean()) {
            t1Rounds++;
            currentRoundWinner = 1;
            team1FiftyFiftyWins++;
         } else {
            t2Rounds++;
            currentRoundWinner = 2;
            team2FiftyFiftyWins++;
         }
      } else if (t1HasBetterOdds) {
         t1Rounds++;
         currentRoundWinner = 1;
      } else {
         t2Rounds++;
         currentRoundWinner = 2;
      }
   }

   public double getTeam1Advantage() {
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
      while (one.getTotalRelativePower() > two.getTotalRelativePower() && count < totalRelativePowerDelta) {
         cachedRelativePowerAdvantage += 0.2;
         count++;
      }

      // Team 2 has more relative power
      while (one.getTotalRelativePower() < two.getTotalRelativePower() && count < totalRelativePowerDelta) {
         cachedRelativePowerAdvantage -= 0.2;
         count++;
      }
      relativePowerAdvantageCalculated = true;
   }

   public double getRelativePowerAdvantage() {
      if (!relativePowerAdvantageCalculated) {
         setRelativePowerAdvantage();
      }
      return cachedRelativePowerAdvantage;
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
      mapAdvantageCalculated = true;
   }

   public double getAttackerMapAdvantage() {
      if (!mapAdvantageCalculated) {
         setAttackerMapAdvantage();
      }
      return cachedMapAdvantage;
   }

   // Game state check methods
   public boolean nobodyHas13Rounds() {
      return t1Rounds < 13 && t2Rounds < 13;
   }

   public boolean overtimeIsReached() {
      return t1Rounds == 12 && t2Rounds == 12;
   }

   public boolean roundDeltaIsNot2() {
      return Math.abs(t1Rounds - t2Rounds) != 2;
   }

   // Game state management methods
   public void resetMatch() {
      t1Rounds = 0;
      t2Rounds = 0;
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

   public void incrementMatchWins() {
      if (t1Rounds > t2Rounds) {
         t1MatchWins++;
      } else {
         t2MatchWins++;
      }
   }

   public void addTotalRounds() {
      t1TotalRounds += t1Rounds;
      t2TotalRounds += t2Rounds;
   }

   // Display methods
   public void printMatchWinner() {
      System.out.printf("Winner of the match: Team %d%n%n", getMatchWinner());
   }

   public void printRoundStats() {
      double t1RoundedChance = Math.round(t1Chance * 100) / 100.0;
      System.out.printf(
            "Current Round: %d%nAttackers: Team %d%nTeam 1's odds: %.2f%%%nTeam 2's odds: %.2f%%%nRound Winner: Team %d%nTeam 1 rounds: %d%nTeam 2 rounds: %d%nStyles: %s vs %s%n%n",
            currentRound, attackingTeam, t1RoundedChance, 100 - t1RoundedChance, currentRoundWinner, t1Rounds, t2Rounds,
            one.getStyle().toString(), two.getStyle().toString());
   }

   // Getter methods
   public int getMatchWinner() {
      if (t1Rounds > t2Rounds) {
         return 1;
      }
      return 2;
   }

   public String getMap() {
      return map;
   }

   public int getTeam1TotalRounds() {
      return t1TotalRounds;
   }

   public int getTeam2TotalRounds() {
      return t2TotalRounds;
   }

   public int getTeam1FiftyFiftyWins() {
      return team1FiftyFiftyWins;
   }

   public int getTeam2FiftyFiftyWins() {
      return team2FiftyFiftyWins;
   }

   public int getTeam1MatchWins() {
      return t1MatchWins;
   }

   public int getTeam2MatchWins() {
      return t2MatchWins;
   }

   public int getCurrentTeam1Rounds() {
      return t1Rounds;
   }

   public int getCurrentTeam2Rounds() {
      return t2Rounds;
   }

   public int getCurrentRound() {
      return currentRound;
   }

   public int getAttackingTeam() {
      return attackingTeam;
   }
}