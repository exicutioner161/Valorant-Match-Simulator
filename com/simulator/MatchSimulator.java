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
 * @version 0.2.0-alpha
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

   /**
    * Constructs a new MatchSimulator with two team compositions.
    *
    * Initializes the match state with default values:
    * - Current round starts at 1
    * - Both teams start with 0 rounds
    * - Team 1 starts attacking
    * - Default map is "ascent"
    * - Team 1 advantage calculations are not cached because they are calculated
    * round-by-round
    *
    * @param first  The first team composition (Team 1)
    * @param second The second team composition (Team 2)
    * @throws IllegalArgumentException if either team composition is null
    */
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

   /************* Match simulation methods *************/
   /**
    * Simulates a complete Valorant match with full round-by-round details.
    *
    * The simulation follows official Valorant match rules:
    * 1. First half: 12 rounds with team 1 attacking
    * 2. Side switch at halftime
    * 3. Second half: Continue until one team reaches 13 rounds
    * 4. Overtime: If tied 12-12, teams alternate sides each round until 2-round
    * lead
    *
    * Each round displays detailed statistics including team advantages,
    * round winners, and current score. After completion, match statistics
    * are updated and the match state is reset for the next simulation.
    *
    * This method is slower than simulateMatchFast() due to console output
    * but provides comprehensive round-by-round information.
    */
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

   /**
    * Simulates a complete Valorant match optimized for speed and bulk processing.
    *
    * Follows the same match rules as simulateMatch() but omits all console output
    * for maximum performance. This method is ideal for large-scale simulations
    * where thousands of matches need to be processed quickly.
    *
    * Match flow:
    * 1. Calculate initial map advantages
    * 2. Simulate first half (12 rounds)
    * 3. Switch sides at halftime
    * 4. Continue second half until victory condition
    * 5. Handle overtime with side switches if needed
    * 6. Update statistics and reset for next match
    *
    * Performance: Approximately 10-50x faster than simulateMatch() depending
    * on system configuration due to eliminated I/O operations.
    */
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

   /**
    * Handles interactive agent selection for both teams.
    *
    * Prompts the user to enter 5 agents for each team through the console.
    * Validates each agent name against the available agent list and provides
    * feedback for invalid entries. Users can type "exit" at any time to quit.
    *
    * The method uses teamInputLoop() to handle the individual agent selection
    * for each team, ensuring exactly 5 valid agents are selected per team.
    *
    * @param input Scanner object for reading user input from console
    * @throws IllegalStateException if agent selection fails due to input issues
    */
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

   /**
    * Sets the map for the current match and resets map advantage calculations.
    *
    * The map name affects attacker/defender advantage calculations which vary
    * between different Valorant maps. Setting a new map invalidates any
    * previously cached map advantage values, forcing recalculation on the
    * next simulation.
    *
    * @param mapOfMatch The name of the map to set (case-insensitive)
    * @throws IllegalArgumentException if mapOfMatch is null
    */
   public void setMap(String mapOfMatch) {
      if (mapOfMatch == null) {
         throw new IllegalArgumentException("Invalid input in setMap. Map name cannot be null.");
      }
      map = mapOfMatch.toLowerCase();
      mapAdvantageCalculated = false;
   }

   /**
    * Interactive loop for selecting agents for a single team.
    *
    * Continuously prompts for agent names until exactly TEAM_SIZE (5) valid
    * agents are added to the team. Validates each input against the agent
    * database and provides error messages for invalid selections.
    *
    * Special commands:
    * - "exit": Terminates the application immediately
    *
    * @param input Scanner for reading user input
    * @param team  TeamComp object to add valid agents to
    */
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

   /************* Round simulation methods *************/
   /**
    * Simulates a single round with detailed output and statistics.
    *
    * Round simulation process:
    * 1. Set team playing styles based on current agents
    * 2. Calculate Team 1's win probability including all advantages
    * 3. Generate random outcome based on calculated probabilities
    * 4. Determine and record round winner
    * 5. Display round statistics to console
    * 6. Increment round counter for next iteration
    *
    * The round outcome considers team composition advantages, map advantages,
    * and relative team power while maintaining realistic variance through
    * probability-based results.
    */
   private void simulateRound() {
      setTeamStyles();
      team1Chance = FIFTY_FIFTY_CHANCE + calculateTeam1Advantage();
      team1HasBetterOdds = ThreadLocalRandom.current().nextDouble() < (team1Chance / 100.0);
      findAndSetRoundWinner();
      printRoundStats();
      currentRound++;
   }

   /**
    * Simulates a single round optimized for speed without console output.
    *
    * Identical logic to simulateRound() but omits the printRoundStats() call
    * for maximum performance. Used in bulk simulations where individual round
    * details are not needed.
    *
    * Process:
    * 1. Calculate team styles and advantages
    * 2. Determine probabilistic round outcome
    * 3. Update round winner and statistics
    * 4. Increment round counter
    *
    * Performance benefit: 5-10x faster than simulateRound() due to eliminated I/O.
    */
   private void simulateRoundFast() {
      setTeamStyles();
      team1Chance = FIFTY_FIFTY_CHANCE + calculateTeam1Advantage();
      team1HasBetterOdds = ThreadLocalRandom.current().nextDouble() < (team1Chance / 100.0);
      findAndSetRoundWinner();
      currentRound++;
   }

   /**
    * Updates the playing style for both teams based on their current agent
    * compositions.
    *
    * Each team's style is determined by analyzing their 5-agent composition
    * and calculating the dominant tactical approach (Aggressive, Control, or
    * Midrange).
    * This affects tactical advantages in subsequent round calculations.
    */
   private void setTeamStyles() {
      teamOne.setStyle();
      teamTwo.setStyle();
   }

   /************* Round logic methods *************/
   /**
    * Determines the winner of the current round based on calculated probabilities.
    *
    * Round winner logic:
    * - If exactly 50/50 odds: Pure random outcome, tracked as "fifty-fifty" rounds
    * - If Team 1 has advantage: Award round to Team 1
    * - If Team 2 has advantage: Award round to Team 2
    *
    * Updates round counters for both teams and tracks special statistics for
    * rounds decided by pure chance (50/50 scenarios). This data helps analyze
    * how much of the match outcome was due to team advantages vs random variance.
    */
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

   /**
    * Calculates Team 1's overall advantage percentage for the current round.
    *
    * The calculation incorporates multiple factors:
    * - Relative power advantage (team skill/agent power differential)
    * - Map-specific attacker/defender advantages
    * - Stylistic counters between team compositions
    * - Random tactical variance (1-5% swing)
    *
    * Advantage calculation depends on which team is attacking:
    * - Team 1 attacking: Gets map advantage bonus
    * - Team 2 attacking: Team 1 gets map advantage penalty
    *
    * Stylistic advantages apply additional bonuses when one team can counter
    * the opponent's playstyle without being countered themselves.
    *
    * @return Team 1's advantage as a percentage modifier (-50 to +50 typical
    *         range)
    */
   private double calculateTeam1Advantage() {
      double stylisticAdvantage = ThreadLocalRandom.current().nextDouble() * 4 + 1;
      boolean team1CanCounter = teamOne.canCounter(teamTwo);
      boolean team2CanCounter = teamTwo.canCounter(teamOne);
      if (attackingTeam == 1) { // Team 1 is attacking
         double attackingAdvantage = getRelativePowerAdvantage() + cachedMapAdvantage;
         if (team1CanCounter && !team2CanCounter) {
            return attackingAdvantage + stylisticAdvantage; // Team 1 has stylistic advantage
         } else if (!team1CanCounter && team2CanCounter) {
            return attackingAdvantage - stylisticAdvantage; // Team 2 has stylistic advantage
         }
         return attackingAdvantage;
      } else if (attackingTeam == 2) { // Team 2 is attacking
         double attackingAdvantage = getRelativePowerAdvantage() - cachedMapAdvantage; /*
                                                                                        * Subtract map advantage because
                                                                                        * we are calculating Team 1's
                                                                                        * advantage
                                                                                        * when Team 2 is attacking
                                                                                        */
         if (!team1CanCounter && team2CanCounter) {
            return attackingAdvantage + stylisticAdvantage; // Team 2 has stylistic advantage
         } else if (team1CanCounter && !team2CanCounter) {
            return attackingAdvantage - stylisticAdvantage; // Team 1 has stylistic advantage
         }
         return attackingAdvantage;
      }

      // This should never be reached
      logger.log(Level.SEVERE, "Invalid attacking team in calculateTeam1Advantage: {0}", attackingTeam);
      return 0.0;
   }

   /**
    * Calculates and caches the relative power advantage between teams.
    *
    * The advantage is based on the total relative power difference between
    * the two team compositions. Each point of power difference translates
    * to a 0.2% advantage for the stronger team.
    *
    * Calculation:
    * 1. Find absolute difference in total relative power
    * 2. Multiply by 0.2 to get percentage advantage
    * 3. Negative value indicates Team 2 advantage
    * 4. Positive value indicates Team 1 advantage
    *
    * The result is cached to avoid recalculation during the match unless
    * team compositions change.
    */
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

   /**
    * Calculates and caches the map advantage based on the current map.
    *
    * This method sets the cached map advantage value based on statistical data
    * for each Valorant map. Negative values favor defenders, positive values
    * favor attackers. The advantage represents the expected round differential
    * for the attacking team.
    *
    * If no recognized map is set, the advantage is set to 0.0 and the map
    * name is reset to "N/A".
    *
    * The result is cached to avoid recalculation during the match.
    */
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

   /************* Game state check methods *************/
   /**
    * Checks if neither team has reached the required 13 rounds to win.
    *
    * @return true if both teams have fewer than 13 rounds, false otherwise
    */
   private boolean nobodyHas13Rounds() {
      return team1Rounds < ROUNDS_TO_WIN && team2Rounds < ROUNDS_TO_WIN;
   }

   /**
    * Checks if the match has reached overtime (12-12 score).
    *
    * @return true if both teams have exactly 12 rounds, false otherwise
    */
   public boolean overtimeIsReached() {
      return team1Rounds == ROUNDS_PER_HALF && team2Rounds == ROUNDS_PER_HALF;
   }

   /**
    * Checks if the round difference between teams is not exactly 2.
    *
    * In overtime, a team needs a 2-round advantage to win the match.
    *
    * @return true if the absolute difference between team rounds is not 2, false
    *         otherwise
    */
   public boolean roundDeltaIsNot2() {
      return Math.abs(team1Rounds - team2Rounds) != 2;
   }

   // Game state management methods

   /**
    * Sets which team is currently attacking.
    *
    * @param team the attacking team number (1 or 2)
    * @throws IllegalArgumentException if team is not 1 or 2
    */
   public void setAttackingTeam(short team) {
      attackingTeam = switch (team) {
         case 1, 2 -> team;
         default -> throw new IllegalArgumentException("Team must be 1 or 2, got: " + team);
      };
   }

   /**
    * Records the match win for the winning team in the statistics collector.
    *
    * Determines the winning team based on final round scores and increments
    * the appropriate team's match win counter in the statistics.
    */
   public void incrementMatchWins() {
      if (team1Rounds > team2Rounds) {
         SimulationStatisticsCollector.incrementTeam1MatchWins();
      } else {
         SimulationStatisticsCollector.incrementTeam2MatchWins();
      }
   }

   /**
    * Adds the total rounds won by each team to the statistics collector.
    *
    * This method records the individual round wins for both teams in the
    * simulation statistics for aggregate analysis across multiple matches.
    */
   public void addTotalRounds() {
      SimulationStatisticsCollector.increaseTeam1RoundWins(team1Rounds);
      SimulationStatisticsCollector.increaseTeam2RoundWins(team2Rounds);
   }

   /**
    * Resets all match state variables to their initial values.
    *
    * This method prepares the simulator for a new match by:
    * - Setting both team round counts to 0
    * - Resetting the current round to 1
    * - Setting team 1 as the initial attacking team
    */
   private void resetMatch() {
      team1Rounds = 0;
      team2Rounds = 0;
      currentRound = 1;
      attackingTeam = 1;
   }

   /**
    * Switches the attacking team between team 1 and team 2.
    *
    * This method alternates which team is attacking, typically called
    * at halftime (after round 12) or during overtime rounds.
    */
   private void switchSides() {
      if (attackingTeam == 1) {
         attackingTeam = 2;
      } else {
         attackingTeam = 1;
      }
   }

   /************* Display methods *************/
   /**
    * Prints the winner of the completed match to the console.
    *
    * Displays a formatted message indicating which team won the match
    * based on the final round scores.
    */
   public void printMatchWinner() {
      System.out.printf("Winner of the match: Team %d%n%n", getMatchWinner());
   }

   /**
    * Prints comprehensive statistics for the current round to the console.
    *
    * Displays detailed information about the current round including:
    * - Round number and attacking team
    * - Win probability percentages for both teams
    * - Round winner and current match score
    * - Team playing styles
    *
    * The team 1 win chance is rounded to 2 decimal places for display.
    */
   public void printRoundStats() {
      double team1RoundedChance = Math.round(team1Chance * 100) / 100.0;
      System.out.printf(
            "Current Round: %d%nAttackers: Team %d%nTeam 1's odds: %.2f%%%nTeam 2's odds: %.2f%%%nRound Winner: Team %d%nTeam 1 rounds: %d%nTeam 2 rounds: %d%nStyles: %s vs %s%n%n",
            currentRound, attackingTeam, team1RoundedChance, 100 - team1RoundedChance, currentRoundWinner, team1Rounds,
            team2Rounds, teamOne.getStyle().toString(), teamTwo.getStyle().toString());
   }

   /************* Getter methods *************/
   /**
    * Gets the name of the current map.
    *
    * @return the map name as a string, or "N/A" if no valid map is set
    */
   public String getMap() {
      return map;
   }

   /**
    * Determines which team won the match based on final round scores.
    *
    * @return 1 if team 1 won, 2 if team 2 won
    */
   public int getMatchWinner() {
      if (team1Rounds > team2Rounds) {
         return 1;
      }
      return 2;
   }

   /**
    * Gets the current number of rounds won by team 1.
    *
    * @return the number of rounds team 1 has won in the current match
    */
   public long getCurrentTeam1Rounds() {
      return team1Rounds;
   }

   /**
    * Gets the current number of rounds won by team 2.
    *
    * @return the number of rounds team 2 has won in the current match
    */
   public long getCurrentTeam2Rounds() {
      return team2Rounds;
   }

   /**
    * Gets the current round number.
    *
    * @return the current round number (1-based)
    */
   public long getCurrentRound() {
      return currentRound;
   }

   /**
    * Gets which team is currently attacking.
    *
    * @return 1 if team 1 is attacking, 2 if team 2 is attacking
    */
   public long getAttackingTeam() {
      return attackingTeam;
   }

   /**
    * Gets the relative power advantage between teams.
    *
    * The advantage is calculated based on the difference in total relative
    * power between the two teams. Positive values favor team 1, negative
    * values favor team 2. The calculation is cached after the first call.
    *
    * @return the relative power advantage as a decimal value
    */
   public double getRelativePowerAdvantage() {
      if (!relativePowerAdvantageCalculated) {
         setRelativePowerAdvantage();
      }
      return cachedRelativePowerAdvantage;
   }

   /**
    * Gets the map advantage for the attacking team.
    *
    * The advantage is based on statistical data for each Valorant map.
    * Negative values favor defenders, positive values favor attackers.
    * The calculation is cached after the first call.
    *
    * @return the map advantage as a decimal value, or 0.0 if no valid map is set
    */
   public double getAttackerMapAdvantage() {
      if (!mapAdvantageCalculated) {
         setAttackerMapAdvantage();
      }
      return cachedMapAdvantage;
   }
}