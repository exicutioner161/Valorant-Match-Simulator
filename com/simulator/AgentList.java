package com.simulator;

import java.util.ArrayList;
import java.util.List;

public class AgentList {
   private static final ArrayList<Agent> list = new ArrayList<>();
   private static final String CONTROLLER = "controller";
   private static final String INITIATOR = "initiator";
   private static final String SENTINEL = "sentinel";
   private static final String DUELIST = "duelist";
   private static final Agent astra = new Agent("Astra", CONTROLLER, 600, -1, -1, 7, 2, 2, 6);
   private static final Agent breach = new Agent("Breach", INITIATOR, -1, 200, 500, 9, 6, 3, 1);
   private static final Agent brimstone = new Agent("Brimstone", CONTROLLER, 200, 200, 250, 8, 6, 2, 2);
   private static final Agent chamber = new Agent("Chamber", SENTINEL, -1, 800, 200, 8, 5, 5, 0);
   private static final Agent clove = new Agent("Clove", CONTROLLER, 150, 200, 250, 8, 5, 1, 4);
   private static final Agent cypher = new Agent("Cypher", SENTINEL, -1, 400, 200, 7, 1, 7, 2);
   private static final Agent deadlock = new Agent("Deadlock", SENTINEL, -1, 400, 400, 7, 1, 5, 4);
   private static final Agent fade = new Agent("Fade", INITIATOR, -1, 200, 500, 8, 3, 3, 4);
   private static final Agent gekko = new Agent("Gekko", INITIATOR, -1, 250, 300, 8, 2, 1, 7);
   private static final Agent harbor = new Agent("Harbor", CONTROLLER, -1, 350, 300, 7, 5, 3, 2);
   private static final Agent iso = new Agent("Iso", DUELIST, -1, 300, 200, 7, 6, 1, 3);
   private static final Agent jett = new Agent("Jett", DUELIST, -1, 150, 400, 8, 8, 2, 0);
   private static final Agent kayo = new Agent("KAYO", INITIATOR, -1, 500, 200, 8, 6, 3, 1);
   private static final Agent killjoy = new Agent("Killjoy", SENTINEL, -1, 200, 400, 9, 3, 7, 0);
   private static final Agent neon = new Agent("Neon", DUELIST, 150, 200, 300, 8, 7, 0, 3);
   private static final Agent omen = new Agent("Omen", CONTROLLER, 150, 250, 200, 7, 3, 6, 1);
   private static final Agent phoenix = new Agent("Phoenix", DUELIST, 250, 200, 150, 6, 6, 2, 2);
   private static final Agent raze = new Agent("Raze", DUELIST, -1, 400, 300, 8, 6, 1, 3);
   private static final Agent reyna = new Agent("Reyna", DUELIST, 200, -1, 500, 6, 8, 0, 2);
   private static final Agent sage = new Agent("Sage", SENTINEL, -1, 400, 400, 7, 1, 5, 4);
   private static final Agent skye = new Agent("Skye", INITIATOR, 250, 300, 150, 8, 2, 2, 6);
   private static final Agent sova = new Agent("Sova", INITIATOR, -1, 300, 400, 8, 1, 6, 3);
   private static final Agent tejo = new Agent("Tejo", INITIATOR, 150, 200, 400, 9, 6, 1, 3);
   private static final Agent viper = new Agent("Viper", CONTROLLER, -1, 200, 300, 9, 3, 5, 2);
   private static final Agent vyse = new Agent("Vyse", SENTINEL, -1, 200, 300, 8, 0, 4, 6);
   private static final Agent waylay = new Agent("Waylay", DUELIST, -1, 300, 300, 8, 7, 2, 1);
   private static final Agent yoru = new Agent("Yoru", DUELIST, 150, 500, 200, 8, 5, 3, 2);

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

   public List<Agent> getList() {
      return list;
   }
}