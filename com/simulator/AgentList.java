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
   private static final Agent astra = new Agent("Astra", CONTROLLER, 2, 2, 6, 7);
   private static final Agent breach = new Agent("Breach", INITIATOR, 6, 3, 1, 1);
   private static final Agent brimstone = new Agent("Brimstone", CONTROLLER, 6, 2, 2, 6);
   private static final Agent chamber = new Agent("Chamber", SENTINEL, 5, 5, 0, 7);
   private static final Agent clove = new Agent("Clove", CONTROLLER, 5, 1, 4, 3);
   private static final Agent cypher = new Agent("Cypher", SENTINEL, 1, 7, 2, 7);
   private static final Agent deadlock = new Agent("Deadlock", SENTINEL, 1, 5, 4, 8);
   private static final Agent fade = new Agent("Fade", INITIATOR, 3, 3, 4, 8);
   private static final Agent gekko = new Agent("Gekko", INITIATOR, 2, 1, 7, 6);
   private static final Agent harbor = new Agent("Harbor", CONTROLLER, 5, 3, 2, 3);
   private static final Agent iso = new Agent("Iso", DUELIST, 6, 1, 3, 6);
   private static final Agent jett = new Agent("Jett", DUELIST, 8, 2, 0, 6);
   private static final Agent kayo = new Agent("KAYO", INITIATOR, 6, 3, 1, 9);
   private static final Agent killjoy = new Agent("Killjoy", SENTINEL, 3, 7, 0, 7);
   private static final Agent neon = new Agent("Neon", DUELIST, 7, 0, 3, 9);
   private static final Agent omen = new Agent("Omen", CONTROLLER, 3, 6, 1, 10);
   private static final Agent phoenix = new Agent("Phoenix", DUELIST, 6, 2, 2, 4);
   private static final Agent raze = new Agent("Raze", DUELIST, 6, 1, 3, 9);
   private static final Agent reyna = new Agent("Reyna", DUELIST, 8, 0, 2, 4);
   private static final Agent sage = new Agent("Sage", SENTINEL, 1, 5, 4, 3);
   private static final Agent skye = new Agent("Skye", INITIATOR, 2, 2, 6, 5);
   private static final Agent sova = new Agent("Sova", INITIATOR, 1, 6, 3, 8);
   private static final Agent tejo = new Agent("Tejo", INITIATOR, 6, 1, 3, 4);
   private static final Agent viper = new Agent("Viper", CONTROLLER, 3, 5, 2, 10);
   private static final Agent vyse = new Agent("Vyse", SENTINEL, 0, 4, 6, 8);
   private static final Agent waylay = new Agent("Waylay", DUELIST, 7, 2, 1, 5);
   private static final Agent yoru = new Agent("Yoru", DUELIST, 5, 3, 2, 9);

   static {
      astra.increaseMidrange(1);
      gekko.multiplyMidrange(1.3);
      gekko.increaseAggro(1);
      skye.multiplyMidrange(0.8);
      skye.increaseAggro(1);
      vyse.increaseAggro(1);
      vyse.increaseControl(1);
      fade.increaseMidrange(1);
      deadlock.increaseMidrange(1);
      deadlock.multiplyMidrange(1.2);
      deadlock.increaseAggro(1);
      deadlock.increaseControl(1);
      sage.multiplyAggro(1.1);
      sova.multiplyControl(1.1);
      sova.multiplyAggro(1.1);
      cypher.multiplyControl(1.3);
      cypher.multiplyMidrange(1.1);
      viper.increaseMidrange(1);
      viper.multiplyMidrange(1.8);
      omen.increaseMidrange(1);
      killjoy.increaseMidrange(1);
      killjoy.multiplyMidrange(1.1);
      killjoy.multiplyAggro(1.2);
      chamber.multiplyControl(1.2);
      chamber.multiplyAggro(1.2);
      chamber.increaseMidrange(1);
      yoru.multiplyAggro(1.2);
      yoru.multiplyControl(1.2);
      yoru.increaseMidrange(1);
      harbor.multiplyControl(0.8);
      harbor.multiplyAggro(0.8);
      harbor.multiplyMidrange(0.8);
      harbor.increaseControl(1);
      harbor.increaseMidrange(1);
      clove.multiplyMidrange(0.7);
      clove.increaseMidrange(1);
      clove.increaseControl(1);
      raze.multiplyAggro(1.4);
      raze.increaseAggro(1);
      iso.multiplyAggro(1.2);
      iso.increaseAggro(1);
      iso.multiplyMidrange(1.1);
      tejo.increaseAggro(1);
      phoenix.increaseMidrange(1);
      phoenix.multiplyAggro(0.9);
      brimstone.multiplyAggro(1.1);
      brimstone.increaseAggro(1);
      kayo.multiplyAggro(1.2);
      kayo.increaseAggro(1);
      breach.multiplyAggro(0.6);
      breach.increaseAggro(1);
      neon.increaseAggro(1);
      neon.multiplyAggro(1.3);
      neon.multiplyMidrange(1.3);
      waylay.multiplyAggro(0.8);
      waylay.multiplyMidrange(0.9);
      waylay.increaseAggro(1);
      reyna.multiplyAggro(0.9);
      reyna.increaseAggro(1);
      jett.multiplyAggro(0.9);
      jett.increaseAggro(1);
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
      return list;
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