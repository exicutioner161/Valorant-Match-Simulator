package com.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class TeamComp {
   private double aggro;
   private double control;
   private double midrange;
   private double maxTotalPoints;
   private double totalRelativePower;
   private int numAgents;
   private Style style;
   private final ArrayList<Agent> teamComposition;
   private final Random random = new Random();
   private static final Logger LOGGER = Logger.getLogger(TeamComp.class.getName());

   public enum Style {
      AGGR, CONTR, MIDR
   }

   public TeamComp() {
      teamComposition = new ArrayList<>();
      aggro = 0;
      control = 0;
      midrange = 0;
      numAgents = 0;
      maxTotalPoints = 0;
      totalRelativePower = 0;
   }

   // Team composition logic methods
   public void addAgent(String in) {
      if (canInputAgent(in)) {
         teamComposition.add(AgentList.getAgentByName(in));
         numAgents++;
         if (numAgents == 5) {
            addStats();
         }
         return;
      }
      LOGGER.log(java.util.logging.Level.WARNING, "Invalid input: {0}", in);
   }

   public boolean canInputAgent(String in) {
      for (Agent ag : AgentList.getList()) {
         if (ag.getName().equalsIgnoreCase(in)) {
            return true;
         }
      }
      return false;
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
      boolean aggroAndControl = style.equals(Style.AGGR) && input.getStyle().equals(Style.CONTR);
      boolean controlAndMidrange = style.equals(Style.CONTR) && input.getStyle().equals(Style.MIDR);
      boolean midrangeAndAggro = style.equals(Style.MIDR) && input.getStyle().equals(Style.AGGR);
      return aggroAndControl || controlAndMidrange || midrangeAndAggro;
   }

   // Getter methods
   public Style getStyle() {
      return style;
   }

   public List<Agent> getTeamComp() {
      return teamComposition;
   }

   public Agent getAgent(String name) {
      return AgentList.getAgentByName(name);
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

   // Statistics methods
   public void addStats() {
      for (Agent ag : teamComposition) {
         aggro += ag.getAggro();
         control += ag.getControl();
         midrange += ag.getMidrange();
         totalRelativePower += ag.getRelativePower();
      }
      maxTotalPoints = aggro + control + midrange;
   }

   public void resetStats() {
      aggro = 0;
      control = 0;
      midrange = 0;
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