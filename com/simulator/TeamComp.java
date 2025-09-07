package com.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Represents a team composition in Valorant, managing a
 * collection of agents and their combined stylistic attributes (aggro, control, and midrange).
 * This class handles team creation, style calculation, and
 * statistical tracking of the team's overall capabilities.
 *
 * The team composition is limited to 5 agents, each contributing to the team's
 * total stylistic scores. The team's playstyle is determined based on the
 * distribution of these stylistic attributes. 
 *
 * Team styles follow a rock-paper-scissors triangular relationship:
 * - aggro beats control
 * - control beats midrange
 * - midrange beats aggro
 *
 * @author exicutioner161
 * @version 0.1.4-alpha
 * @see Agent
 * @see AgentList
 * @see MatchSimulator
 */

public class TeamComp {
   private double aggro;
   private double control;
   private double midrange;
   private double maxTotalPoints;
   private double totalRelativePower;
   private int numAgents;
   private Style style;
   private final AgentList agentList;
   private final ArrayList<Agent> teamComposition;
   private final Random random = new Random();
   private static final Logger LOGGER = Logger.getLogger(TeamComp.class.getName());

   public enum Style {
      AGGR, CONTR, MIDR
   }

   public TeamComp(String mapInput) {
      teamComposition = new ArrayList<>();
      aggro = 0;
      control = 0;
      midrange = 0;
      numAgents = 0;
      maxTotalPoints = 0;
      totalRelativePower = 0;
      agentList = new AgentList(mapInput);
   }

   public TeamComp() {
      teamComposition = new ArrayList<>();
      aggro = 0;
      control = 0;
      midrange = 0;
      numAgents = 0;
      maxTotalPoints = 0;
      totalRelativePower = 0;
      agentList = new AgentList();
   }

   // Team composition logic methods
   public void addAgent(String in) {
      if (canInputAgent(in.trim())) {
         teamComposition.add(agentList.getAgentByName(in.trim()));
         numAgents++;
         if (numAgents == 5) {
            addStats();
         }
         return;
      }
      LOGGER.log(java.util.logging.Level.WARNING, "Invalid input: {0}", in.trim());
   }

   public boolean canInputAgent(String in) {
      return agentList.getAgentByName(in.trim()) != null;
   }

   public void setStyle() {
      double styleRoll = random.nextDouble() * maxTotalPoints;
      // Style assignment boundaries:
      // Aggro: [0, aggro)
      // Control: [aggro, maxTotalPoints - midrange)
      // Midrange: [maxTotalPoints - midrange, maxTotalPoints)
      if (styleRoll < aggro) {
         style = Style.AGGR;
      } else if (styleRoll < maxTotalPoints - midrange) {
         style = Style.CONTR;
      } else {
         style = Style.MIDR;
      }
   }

   public boolean canCounter(TeamComp input) {
      // Team 1 aggro vs Team 2 control
      boolean aggroAndControl = style.equals(Style.AGGR) && input.getStyle().equals(Style.CONTR);
      // Team 1 control vs Team 2 midrange
      boolean controlAndMidrange = style.equals(Style.CONTR) && input.getStyle().equals(Style.MIDR);
      // Team 1 midrange vs Team 2 aggro
      boolean midrangeAndAggro = style.equals(Style.MIDR) && input.getStyle().equals(Style.AGGR);

      return aggroAndControl || controlAndMidrange || midrangeAndAggro;
   }

   public void setMap(String mapInput) {
      agentList.balanceAgentsByMapAndUpdateList(mapInput.toLowerCase());
   }

   // Getter methods
   public Style getStyle() {
      return style;
   }

   public List<Agent> getTeamComp() {
      return teamComposition;
   }

   public Agent getAgent(String name) {
      for (Agent agent : teamComposition) {
         if (agent != null && agent.getName().equalsIgnoreCase(name) && canInputAgent(name)) {
            return agent;
         }
      }
      LOGGER.log(java.util.logging.Level.WARNING, "Agent {0} not found in team composition.", name);
      return null;
   }

   public double getTotalAggro() {
      return aggro;
   }

   public double getTotalControl() {
      return control;
   }

   public double getTotalMidrange() {
      return midrange;
   }

   public double getTotalRelativePower() {
      return totalRelativePower;
   }

   // Stats methods
   public void addStats() {
      for (Agent ag : teamComposition) {
         aggro += ag.getAggro();
         control += ag.getControl();
         midrange += ag.getMidrange();
         totalRelativePower += ag.getCurrentRelativePower();
      }
      maxTotalPoints = aggro + control + midrange;
   }

   public void resetStats() {
      aggro = 0;
      control = 0;
      midrange = 0;
      totalRelativePower = 0;
      maxTotalPoints = 0;
      addStats();
   }

   public void printStats() {
      System.out.printf(
            "Total Points in Each Style:%nAggro: %.1f%nControl: %.1f%nMidrange: %.1f%nTotal Relative Power: %.1f%n%nAgent Stats:%n",
            aggro, control, midrange, totalRelativePower);
      for (Agent ag : teamComposition) {
         System.out.println(ag.toString());
      }
      System.out.println("");
   }
}