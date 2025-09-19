package com.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * <p>
 * Represents a team composition in a Valorant match simulator.
 * </p>
 *
 * <p>
 * A team composition consists of 5 agents. No more, no less.<br/>
 * There are three main styles:
 * </p>
 * <ul>
 * <li>AGGRO - focused on fast pushes and entries.</li>
 * <li>CONTROL - focused on map control and utility.</li>
 * <li>MIDRANGE - focused on flexibility and utility attrition.</li>
 * </ul>
 *
 * <p>
 * The counter system follows a rock-paper-scissors pattern:
 * </p>
 * <ul>
 * <li>Aggro counters Control</li>
 * <li>Control counters Midrange</li>
 * <li>Midrange counters Aggro</li>
 * </ul>
 *
 * <p>
 * Team statistics are automatically calculated when the team reaches full
 * capacity (5 agents) and include both base stats and map-adjusted "true" stats
 * that account for agent effectiveness on specific maps.
 * </p>
 *
 * @author exicutioner161
 * @version 1.0
 * @see Agent
 * @see AgentList
 */

public class TeamComp {
   private double baseAggro;
   private double baseControl;
   private double baseMidrange;
   private double trueAggro;
   private double trueControl;
   private double trueMidrange;
   private double maxTotalTruePoints;
   private double totalRelativePower;
   private byte numAgents;
   private Style style;
   private final AgentList agentList;
   private final ArrayList<Agent> teamComposition;
   private static final Logger LOGGER = Logger.getLogger(TeamComp.class.getName());
   private static final byte TEAM_SIZE = 5;

   public enum Style {
      AGGRO, CONTROL, MIDRANGE
   }

   /**
    * Constructs a team composition for a given map with five specified agents.
    *
    * Initializes the internal agent list balanced for {@code mapInput} and
    * sequentially adds the five provided agent names to the team, calculating
    * stats upon reaching full capacity.
    *
    * @param mapInput the map name used to balance agent relative power
    * @param agent1   first agent name
    * @param agent2   second agent name
    * @param agent3   third agent name
    * @param agent4   fourth agent name
    * @param agent5   fifth agent name
    * @throws IllegalArgumentException if any agent name is invalid
    */
   public TeamComp(String mapInput, String agent1, String agent2,
         String agent3, String agent4, String agent5) {
      teamComposition = new ArrayList<>();
      baseAggro = 0;
      baseControl = 0;
      baseMidrange = 0;
      trueAggro = 0;
      trueControl = 0;
      trueMidrange = 0;
      numAgents = 0;
      maxTotalTruePoints = 0;
      totalRelativePower = 0;
      agentList = new AgentList(mapInput);
      addAgent(agent1);
      addAgent(agent2);
      addAgent(agent3);
      addAgent(agent4);
      addAgent(agent5);

   }

   /**
    * Constructs an empty team composition balanced for the specified map.
    *
    * Initializes an empty composition and prepares agent data balanced for the
    * given map. Agents can be added subsequently via {@link #addAgent(String)}.
    *
    * @param mapInput the map name used to balance agent relative power
    */
   public TeamComp(String mapInput) {
      teamComposition = new ArrayList<>();
      baseAggro = 0;
      baseControl = 0;
      baseMidrange = 0;
      trueAggro = 0;
      trueControl = 0;
      trueMidrange = 0;
      numAgents = 0;
      maxTotalTruePoints = 0;
      totalRelativePower = 0;
      agentList = new AgentList(mapInput);
   }

   /**
    * Constructs a new team composition with baseline agent statistics.
    *
    * Initializes an empty team composition with all statistics set to zero
    * and creates an AgentList with baseline agent power levels (no map
    * adjustments). The team can hold up to 5 agents.
    */
   /**
    * Constructs an empty team composition using baseline agent statistics.
    *
    * No map adjustments are applied; agents can be added via
    * {@link #addAgent(String)}.
    */
   public TeamComp() {
      teamComposition = new ArrayList<>();
      baseAggro = 0;
      baseControl = 0;
      baseMidrange = 0;
      trueAggro = 0;
      trueControl = 0;
      trueMidrange = 0;
      numAgents = 0;
      maxTotalTruePoints = 0;
      totalRelativePower = 0;
      agentList = new AgentList();
   }

   /************* Team Composition logic methods *************/
   /**
    * Adds an agent to the team composition by name.
    *
    * This method validates the agent name and adds the agent to the team
    * if valid. When the team reaches full capacity (5 agents), statistics
    * are automatically calculated. Invalid agent names are logged as warnings.
    *
    * @param in the name of the agent to add (case-insensitive, automatically
    *           trimmed)
    */
   public final void addAgent(String in) {
      String trimmedInput = in.trim();
      if (canInputAgent(trimmedInput)) {
         teamComposition.add(agentList.getAgentByName(trimmedInput));
         numAgents++;
         if (numAgents == TEAM_SIZE) {
            // Apply splash bonuses once the team is complete before computing stats
            for (Agent ag : teamComposition) {
               if (ag != null) {
                  ag.applyStyleSplash();
               }
            }
            addStats();
         }
         return;
      }
      LOGGER.log(java.util.logging.Level.WARNING, "Invalid input: {0}", trimmedInput);
   }

   /**
    * Checks if an agent can be added to the team composition.
    *
    * Validates that the provided agent name corresponds to a valid agent
    * in the agent list.
    *
    * @param in the agent name to validate (automatically trimmed)
    * @return true if the agent exists and can be added, false otherwise
    */
   public boolean canInputAgent(String in) {
      return agentList.getAgentByName(in.trim()) != null;
   }

   /**
    * Determines the team's playing style based on a random roll against true style
    * values.
    *
    * Uses the team's true style statistics to probabilistically assign a style:
    * - AGGRO: Roll falls within the aggro range (0 to trueAggro)
    * - CONTROL: Roll falls within the control range (trueAggro to maxPoints -
    * trueMidrange)
    * - MIDRANGE: Roll falls within the midrange range (remaining probability
    * space)
    *
    * @param styleRoll the random value used to determine style (0 to
    *                  maxTotalTruePoints)
    */
   private void calculateStyle(double styleRoll) {
      if (styleRoll < trueAggro) {
         style = Style.AGGRO;
      } else if (styleRoll < maxTotalTruePoints - trueMidrange) {
         style = Style.CONTROL;
      } else {
         style = Style.MIDRANGE;
      }
   }

   /**
    * Randomly assigns a playing style to the team based on their style
    *
    * Generates a random value and uses it to probabilistically determine the
    * team's
    * style for the current round. Teams with higher values in a particular style
    * are more likely to adopt that style.
    */
   public void setStyle() {
      // If stats haven't been calculated yet, set a default style
      if (maxTotalTruePoints <= 0) {
         style = Style.MIDRANGE; // Default to midrange if no stats available
         return;
      }

      double styleRoll = ThreadLocalRandom.current().nextDouble() * maxTotalTruePoints;
      calculateStyle(styleRoll);
   }

   /**
    * Sets the team's playing style.
    *
    * @param newStyle the new style to assign (AGGRO, CONTROL, or MIDRANGE),
    *                 or null to default to MIDRANGE
    */
   public void setStyle(Style newStyle) {
      this.style = (newStyle != null) ? newStyle : Style.MIDRANGE;
   }

   /**
    * Determines whether this team's current style counters the opponent's
    * style.
    *
    * Implements the rock-paper-scissors logic: AGGRO > CONTROL, CONTROL >
    * MIDRANGE, MIDRANGE > AGGRO. If either team's style is not initialized, this
    * returns {@code false}.
    *
    * @param otherTeam the opponent team composition to compare against
    * @return {@code true} if this team's style counters the opponent; otherwise
    *         {@code false}
    */
   public boolean canCounter(TeamComp otherTeam) {
      if (this.style == null || otherTeam == null || otherTeam.style == null) {
         return false; // Can't counter if styles aren't initialized
      }

      // Team 1 aggro vs Team 2 control
      boolean aggroAndControl = style.equals(Style.AGGRO) && otherTeam.getStyle().equals(Style.CONTROL);
      // Team 1 control vs Team 2 midrange
      boolean controlAndMidrange = style.equals(Style.CONTROL) && otherTeam.getStyle().equals(Style.MIDRANGE);
      // Team 1 midrange vs Team 2 aggro
      boolean midrangeAndAggro = style.equals(Style.MIDRANGE) && otherTeam.getStyle().equals(Style.AGGRO);

      return aggroAndControl || controlAndMidrange || midrangeAndAggro;
   }

   /**
    * Sets the map for agent balancing and updates all agent statistics.
    *
    * This method applies map-specific balance adjustments to all agents
    * in the agent list. If the map input is null, a warning is logged
    * and no changes are made.
    *
    * @param mapInput the name of the map to balance agents for (case-insensitive)
    */
   public void setMap(String mapInput) {
      if (mapInput == null) {
         LOGGER.warning("Invalid input: Map input is null");
         return;
      }
      agentList.balanceAgentsByMapAndUpdateList(mapInput.toLowerCase());
   }

   /************* Getter methods *************/
   /**
    * Gets the team's current playing style.
    *
    * @return the team's style (AGGRO, CONTROL, or MIDRANGE), or null if not yet
    *         determined
    */
   public Style getStyle() {
      return style;
   }

   /**
    * Gets the list of agents in the team composition.
    *
    * @return an ArrayList containing all agents currently in the team
    */
   public List<Agent> getTeamComp() {
      return teamComposition;
   }

   /**
    * Retrieves a specific agent from the team composition by name.
    *
    * Performs a case-insensitive search for the specified agent within
    * the current team composition. If the agent is not found, a warning
    * is logged and null is returned.
    *
    * @param name the name of the agent to find (case-insensitive, automatically
    *             trimmed)
    * @return the Agent object if found, null otherwise
    */
   public Agent getAgent(String name) {
      String trimmedName = name.trim();
      for (Agent agent : teamComposition) {
         if (agent != null && agent.getName().equalsIgnoreCase(trimmedName)) {
            return agent;
         }
      }
      LOGGER.log(java.util.logging.Level.WARNING, "Agent {0} not found in team composition.", trimmedName);
      return null;
   }

   /**
    * Gets the team's total base aggro value.
    *
    * @return the sum of all agents' base aggro values (before splash bonuses)
    */
   public double getTotalBaseAggro() {
      return baseAggro;
   }

   /**
    * Gets the team's total base control value.
    *
    * @return the sum of all agents' base control values (before splash bonuses)
    */
   public double getTotalBaseControl() {
      return baseControl;
   }

   /**
    * Gets the team's total base midrange value.
    *
    * @return the sum of all agents' base midrange values (before splash bonuses)
    */
   public double getTotalBaseMidrange() {
      return baseMidrange;
   }

   /**
    * Gets the team's total true aggro value.
    *
    * @return the sum of all agents' true aggro values (including splash bonuses)
    */
   public double getTotalTrueAggro() {
      return trueAggro;
   }

   /**
    * Gets the team's total true control value.
    *
    * @return the sum of all agents' true control values (including splash bonuses)
    */
   public double getTotalTrueControl() {
      return trueControl;
   }

   /**
    * Gets the team's total true midrange value.
    *
    * @return the sum of all agents' true midrange values (including splash
    *         bonuses)
    */
   public double getTotalTrueMidrange() {
      return trueMidrange;
   }

   /**
    * Gets the team's total relative power.
    *
    * @return the sum of all agents' current relative power values
    */
   public double getTotalRelativePower() {
      return totalRelativePower;
   }

   /************* Stats methods *************/
   /**
    * Calculates and updates all team statistics based on current agents.
    *
    * This method sums up all agent statistics (both base and true values)
    * and calculates the total relative power. It also determines the maximum
    * total true points used for style probability calculations. This method
    * is automatically called when the team reaches full capacity (5 agents).
    */
   public void addStats() {
      baseAggro = 0;
      baseControl = 0;
      baseMidrange = 0;
      trueAggro = 0;
      trueControl = 0;
      trueMidrange = 0;
      totalRelativePower = 0;
      for (Agent ag : teamComposition) {
         baseAggro += ag.getBaseAggro();
         baseControl += ag.getBaseControl();
         baseMidrange += ag.getBaseMidrange();
         trueAggro += ag.getTrueAggro();
         trueControl += ag.getTrueControl();
         trueMidrange += ag.getTrueMidrange();
         totalRelativePower += ag.getCurrentRelativePower();

      }
      maxTotalTruePoints = trueAggro + trueControl + trueMidrange;
   }

   /**
    * Resets all team statistics to zero and recalculates them.
    *
    * This method clears all statistical values and then calls addStats()
    * to recalculate them based on the current team composition. Useful
    * when agent statistics have been modified externally.
    */
   public void resetStats() {
      baseAggro = 0;
      baseControl = 0;
      baseMidrange = 0;
      trueAggro = 0;
      trueControl = 0;
      trueMidrange = 0;
      totalRelativePower = 0;
      maxTotalTruePoints = 0;
      addStats();
   }

   /**
    * Prints detailed team statistics to the console.
    *
    * Displays the team's total style points (aggro, control, midrange),
    * total relative power, and individual agent statistics. All values
    * are formatted with one decimal place for readability.
    */
   public void printStats() {
      System.out.printf(
            "Total Points in Each Style:%nAggro: %.1f%nControl: %.1f%nMidrange: %.1f%nTotal Relative Power: %.1f%n%nAgent Stats:%n",
            baseAggro, baseControl, baseMidrange, totalRelativePower);
      for (Agent ag : teamComposition) {
         System.out.println(ag.toString());
      }
      System.out.println("");
   }
}