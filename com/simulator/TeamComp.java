package com.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * Represents a team composition in Valorant, managing a collection of agents
 * and their combined stylistic attributes (aggro, control, and midrange). This
 * class handles team creation, style calculation, and statistical tracking of
 * the team's overall capabilities.
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
 * @version 0.1.8-alpha
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
      AGGR, CONTR, MIDR
   }

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

   // Team composition logic methods
   public void addAgent(String in) {
      String trimmedInput = in.trim();
      if (canInputAgent(trimmedInput)) {
         teamComposition.add(agentList.getAgentByName(trimmedInput));
         numAgents++;
         if (numAgents == TEAM_SIZE) {
            addStats();
         }
         return;
      }
      LOGGER.log(java.util.logging.Level.WARNING, "Invalid input: {0}", trimmedInput);
   }

   public boolean canInputAgent(String in) {
      return agentList.getAgentByName(in.trim()) != null;
   }

   private void calculateStyle(double styleRoll) {
      if (styleRoll < trueAggro) {
         style = Style.AGGR;
      } else if (styleRoll < maxTotalTruePoints - trueMidrange) {
         style = Style.CONTR;
      } else {
         style = Style.MIDR;
      }
   }

   public void setStyle() {
      double styleRoll = ThreadLocalRandom.current().nextDouble() * maxTotalTruePoints;
      calculateStyle(styleRoll);
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
      if (mapInput == null) {
         LOGGER.warning("Invalid input: Map input is null");
         return;
      }
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
      String trimmedName = name.trim();
      for (Agent agent : teamComposition) {
         if (agent != null && agent.getName().equalsIgnoreCase(trimmedName)) {
            return agent;
         }
      }
      LOGGER.log(java.util.logging.Level.WARNING, "Agent {0} not found in team composition.", trimmedName);
      return null;
   }

   public double getTotalBaseAggro() {
      return baseAggro;
   }

   public double getTotalBaseControl() {
      return baseControl;
   }

   public double getTotalBaseMidrange() {
      return baseMidrange;
   }

   public double getTotalTrueAggro() {
      return trueAggro;
   }

   public double getTotalTrueControl() {
      return trueControl;
   }

   public double getTotalTrueMidrange() {
      return trueMidrange;
   }

   public double getTotalRelativePower() {
      return totalRelativePower;
   }

   // Stats methods
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