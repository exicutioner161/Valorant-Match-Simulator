package com.simulator;

import java.util.ArrayList;
import java.util.List;

public class AgentList {
   private AgentList() {
      // Utility class - do not instantiate
   }

   private static final ArrayList<Agent> list = new ArrayList<>();
   private static final String CONTROLLER = "controller";
   private static final String INITIATOR = "initiator";
   private static final String SENTINEL = "sentinel";
   private static final String DUELIST = "duelist";
   private static final Agent astra = new Agent("Astra", CONTROLLER, 2, 3, 6, 8);
   private static final Agent breach = new Agent("Breach", INITIATOR, 7, 3, 1, 1);
   private static final Agent brimstone = new Agent("Brimstone", CONTROLLER, 7, 2, 2, 6);
   private static final Agent chamber = new Agent("Chamber", SENTINEL, 5, 6, 0, 6);
   private static final Agent clove = new Agent("Clove", CONTROLLER, 5, 1, 5, 5);
   private static final Agent cypher = new Agent("Cypher", SENTINEL, 1, 7, 3, 7);
   private static final Agent deadlock = new Agent("Deadlock", SENTINEL, 1, 5, 5, 7);
   private static final Agent fade = new Agent("Fade", INITIATOR, 3, 3, 5, 10);
   private static final Agent gekko = new Agent("Gekko", INITIATOR, 3, 1, 7, 6);
   private static final Agent harbor = new Agent("Harbor", CONTROLLER, 5, 3, 3, 3);
   private static final Agent iso = new Agent("Iso", DUELIST, 7, 1, 3, 6);
   private static final Agent jett = new Agent("Jett", DUELIST, 9, 2, 0, 7);
   private static final Agent kayo = new Agent("KAYO", INITIATOR, 7, 3, 1, 9);
   private static final Agent killjoy = new Agent("Killjoy", SENTINEL, 3, 7, 1, 7);
   private static final Agent neon = new Agent("Neon", DUELIST, 8, 0, 3, 9);
   private static final Agent omen = new Agent("Omen", CONTROLLER, 3, 6, 2, 10);
   private static final Agent phoenix = new Agent("Phoenix", DUELIST, 6, 2, 3, 4);
   private static final Agent raze = new Agent("Raze", DUELIST, 7, 1, 3, 9);
   private static final Agent reyna = new Agent("Reyna", DUELIST, 9, 0, 2, 4);
   private static final Agent sage = new Agent("Sage", SENTINEL, 1, 5, 5, 3);
   private static final Agent skye = new Agent("Skye", INITIATOR, 3, 2, 6, 5);
   private static final Agent sova = new Agent("Sova", INITIATOR, 1, 6, 4, 10);
   private static final Agent tejo = new Agent("Tejo", INITIATOR, 7, 1, 3, 4);
   private static final Agent viper = new Agent("Viper", CONTROLLER, 3, 5, 3, 10);
   private static final Agent vyse = new Agent("Vyse", SENTINEL, 1, 4, 6, 8);
   private static final Agent waylay = new Agent("Waylay", DUELIST, 8, 2, 1, 4);
   private static final Agent yoru = new Agent("Yoru", DUELIST, 5, 3, 3, 9);

   static {
      list.add(astra);
      list.add(breach);
      list.add(brimstone);
      list.add(chamber);
      list.add(clove);
      list.add(cypher);
      list.add(deadlock);
      list.add(fade);
      list.add(gekko);
      list.add(harbor);
      list.add(iso);
      list.add(jett);
      list.add(kayo);
      list.add(killjoy);
      list.add(neon);
      list.add(omen);
      list.add(phoenix);
      list.add(raze);
      list.add(reyna);
      list.add(sage);
      list.add(skye);
      list.add(sova);
      list.add(tejo);
      list.add(viper);
      list.add(vyse);
      list.add(waylay);
      list.add(yoru);
   }

   public static List<Agent> getList() {
      return java.util.Collections.unmodifiableList(list);
   }

   public static Agent getAgentByName(String name) {
      for (Agent agent : list) {
         if (agent.getName().equalsIgnoreCase(name)) {
            return agent;
         }
      }
      return null;
   }
}