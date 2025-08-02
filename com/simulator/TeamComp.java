package com.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class TeamComp {
   private int aggro;
   private int control;
   private int midrange;
   private int numAgents = 0;
   private Style style;
   private final ArrayList<Agent> teamComposition;
   private final Random random = new Random();
   private static final AgentList list = new AgentList();
   private static final Logger LOGGER = Logger.getLogger(TeamComp.class.getName());

   public enum Style {
      AGGR, CONTR, MIDR
   }

   public TeamComp() {
      teamComposition = new ArrayList<>();
      aggro = 0;
      control = 0;
      midrange = 0;
   }

   // Team composition logic methods
   public void addAgent(String in) {
      for (Agent ag : list.getList()) {
         boolean inputIsAnAgent = ag.getName().equalsIgnoreCase(in);
         if (inputIsAnAgent) {
            teamComposition.add(ag);
            numAgents++;
            if (numAgents == 5)
               addStats();
            return;
         }
      }
      LOGGER.log(java.util.logging.Level.WARNING, "Invalid input: {0}. Ending run.", in);
      System.exit(0);
   }

   public void setStyle() {
      double styleRoll = random.nextDouble() * 50;
      // Style assignment boundaries:
      // Aggro: [0, aggro)
      // Control: [aggro, 50 - midrange)
      // Midrange: [50 - midrange, 50)
      if (styleRoll < aggro) {
         style = Style.AGGR;
      } else if (styleRoll < 50 - midrange) {
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
      for (Agent agent : teamComposition) {
         if (agent.getName().equalsIgnoreCase(name)) {
            return agent;
         }
      }
      LOGGER.log(java.util.logging.Level.WARNING, "Agent {0} not found in team composition.", name);
      return null;
   }

   public int getTotalAggro() {
      return aggro;
   }

   public int getTotalControl() {
      return control;
   }

   public int getTotalMidrange() {
      return midrange;
   }

   // Statistics methods
   public void addStats() {
      for (Agent ag : teamComposition) {
         aggro += ag.getAggro();
         control += ag.getControl();
         midrange += ag.getMidrange();
      }
   }

   public void resetStats() {
      aggro = 0;
      control = 0;
      midrange = 0;
      addStats();
   }

   public void printStats() {
      System.out.printf("Total Points in Each Style:%nAggro: %d%nControl: %d%nMidrange: %d%n%nAgent Stats:%n", aggro,
            control, midrange);
      for (Agent ag : teamComposition) {
         System.out.println(ag.toString());
      }
      System.out.println("");
   }
}