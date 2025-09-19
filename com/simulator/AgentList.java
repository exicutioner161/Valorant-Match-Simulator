package com.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Represents a list of all agents in Valorant and manages their relative power
 * levels based on different maps. This class maintains a collection of all
 * available agents and provides functionality to adjust their relative power
 * levels according to my understanding of the current state of the Valorant
 * meta.
 * </p>
 *
 * <p>
 * The class includes:
 * </p>
 * <ul>
 * <li>A complete list of all Valorant agents with their base stats</li>
 * <li>Map-specific power adjustments for each agent</li>
 * <li>Methods to retrieve and manage agent information</li>
 * </ul>
 *
 * <p>
 * Each agent is initialized with four primary attributes:
 * </p>
 * <ul>
 * <li>Aggro</li>
 * <li>Control</li>
 * <li>Midrange</li>
 * <li>Relative Power</li>
 * </ul>
 *
 * <p>
 * The class supports the following maps:
 * </p>
 * <ul>
 * <li>Abyss</li>
 * <li>Ascent</li>
 * <li>Bind</li>
 * <li>Breeze</li>
 * <li>Corrode</li>
 * <li>Fracture</li>
 * <li>Haven</li>
 * <li>Icebox</li>
 * <li>Lotus</li>
 * <li>Pearl</li>
 * <li>Split</li>
 * <li>Sunset</li>
 * </ul>
 *
 * <p>
 * Key features:
 * </p>
 * <ul>
 * <li>Map-specific agent balancing</li>
 * <li>Agent retrieval by name (case-insensitive)</li>
 * <li>Immutable list view of agents</li>
 * <li>Reset capability to baseline statistics</li>
 * </ul>
 *
 * @author exicutioner161
 * @version 1.0
 * @see Agent
 */

public class AgentList {
      private static final String CONTROLLER = "CONTROLLER";
      private static final String INITIATOR = "INITIATOR";
      private static final String SENTINEL = "SENTINEL";
      private static final String DUELIST = "DUELIST";
      private static final String AGGRO = "AGGRO";
      private static final String CONTROL = "CONTROL";
      private static final String MIDRANGE = "MIDRANGE";
      private static final String NONE = "NONE";

      private final ArrayList<Agent> list = new ArrayList<>();
      private final Map<String, Agent> agentMap = new HashMap<>();

      private final Agent astra = new Agent("Astra", CONTROLLER, 2, 3, 6,
                  8, CONTROL);

      private final Agent breach = new Agent("Breach", INITIATOR, 7, 3, 1,
                  2, AGGRO);

      private final Agent brimstone = new Agent("Brimstone", CONTROLLER, 7, 2, 2,
                  6, NONE);

      private final Agent chamber = new Agent("Chamber", SENTINEL, 5, 6, 0,
                  7, NONE);

      private final Agent clove = new Agent("Clove", CONTROLLER, 5, 1, 5,
                  5, NONE);

      private final Agent cypher = new Agent("Cypher", SENTINEL, 1, 7, 3,
                  7, CONTROL);

      private final Agent deadlock = new Agent("Deadlock", SENTINEL, 1, 5, 5,
                  7, NONE);

      private final Agent fade = new Agent("Fade", INITIATOR, 3, 3, 5,
                  10, MIDRANGE);

      private final Agent gekko = new Agent("Gekko", INITIATOR, 3, 1, 7,
                  7, MIDRANGE);

      private final Agent harbor = new Agent("Harbor", CONTROLLER, 5, 3, 3,
                  3, NONE);

      private final Agent iso = new Agent("Iso", DUELIST, 7, 1, 3,
                  6, AGGRO);

      private final Agent jett = new Agent("Jett", DUELIST, 9, 2, 0,
                  7, AGGRO);

      private final Agent kayo = new Agent("KAY/O", INITIATOR, 7, 3, 1,
                  9, NONE);

      private final Agent killjoy = new Agent("Killjoy", SENTINEL, 3, 7, 1,
                  7, NONE);

      private final Agent neon = new Agent("Neon", DUELIST, 8, 0, 3,
                  10, MIDRANGE);

      private final Agent omen = new Agent("Omen", CONTROLLER, 3, 6, 2,
                  10, NONE);

      private final Agent phoenix = new Agent("Phoenix", DUELIST, 6, 2, 3,
                  4, NONE);

      private final Agent raze = new Agent("Raze", DUELIST, 7, 1, 3,
                  9, AGGRO);

      private final Agent reyna = new Agent("Reyna", DUELIST, 9, 0, 2,
                  4, NONE);

      private final Agent sage = new Agent("Sage", SENTINEL, 1, 5, 5,
                  6, NONE);

      private final Agent skye = new Agent("Skye", INITIATOR, 3, 2, 6,
                  5, NONE);

      private final Agent sova = new Agent("Sova", INITIATOR, 1, 6, 4,
                  10, NONE);

      private final Agent tejo = new Agent("Tejo", INITIATOR, 7, 1, 3,
                  7, NONE);

      private final Agent viper = new Agent("Viper", CONTROLLER, 3, 5, 3,
                  10, MIDRANGE);

      private final Agent vyse = new Agent("Vyse", SENTINEL, 1, 4, 6,
                  8, CONTROL);

      private final Agent waylay = new Agent("Waylay", DUELIST, 8, 2, 1,
                  6, NONE);

      private final Agent yoru = new Agent("Yoru", DUELIST, 5, 3, 3,
                  10, AGGRO);

      /**
       * Constructs an AgentList with agents balanced for a specific map.
       *
       * This constructor initializes all agents with their baseline statistics
       * and then applies map-specific balance adjustments based on the provided
       * map name.
       *
       * @param mapInput the name of the map to balance agents for (case-insensitive)
       */
      public AgentList(String mapInput) {
            balanceAgentsByMapAndUpdateList(mapInput);
      }

      /**
       * Constructs an AgentList with agents at their baseline statistics.
       *
       * This constructor initializes all agents with their default power levels
       * without applying any map-specific adjustments.
       */
      public AgentList() {
            addAgentsToStorage();
      }

      /************* AgentList management methods *************/
      /**
       * Populates the agent list and map with all available Valorant agents.
       *
       * This method clears the existing list and map, then adds all agent instances
       * to both the list (for ordered iteration) and the map (for fast name-based
       * lookup). All agents are added with their baseline statistics.
       */
      private void addAgentsToStorage() {
            list.clear();
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
            agentMap.clear();
            for (Agent agent : list) {
                  agentMap.put(agent.getName().toLowerCase(), agent);
            }
      }

      /**
       * Resets all agents in the list to their baseline relative power levels.
       *
       * This method iterates through all agents and calls their reset method
       * to restore their original power values before any map-specific adjustments.
       */
      private void resetAgentsToBaseline() {
            for (Agent agent : list) {
                  agent.resetToBaselineRelativePower();
            }
      }

      /**
       * Balances all agents based on the specified map and updates the storage.
       *
       * This method first resets all agents to their baseline statistics, then
       * applies map-specific balance adjustments based on the provided map name.
       * After balancing, it refreshes the agent storage to ensure consistency.
       *
       * @param map the name of the map to balance agents for (case-insensitive)
       */
      public final void balanceAgentsByMapAndUpdateList(String map) {
            resetAgentsToBaseline();
            switch (map.trim().toLowerCase()) {
                  case "abyss" -> abyssBalance();
                  case "ascent" -> ascentBalance();
                  case "bind" -> bindBalance();
                  case "breeze" -> breezeBalance();
                  case "corrode" -> corrodeBalance();
                  case "fracture" -> fractureBalance();
                  case "haven" -> havenBalance();
                  case "icebox" -> iceboxBalance();
                  case "lotus" -> lotusBalance();
                  case "pearl" -> pearlBalance();
                  case "split" -> splitBalance();
                  case "sunset" -> sunsetBalance();
                  default -> {
                        // Keep agent stats at baseline for invalid map inputs
                  }
            }
            addAgentsToStorage();
      }

      /************* Map-specific balancing methods *************/

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void abyssBalance() {
            // Abyss:
            // No change for Fade
            // No change for Iso
            // No change for Neon
            // No change for Phoenix
            // No change for Reyna
            // No change for Sage
            // No change for Skye
            // No change for Tejo
            // No change for Viper
            // No change for Waylay
            astra.changeCurrentRelativePower(1);
            breach.changeCurrentRelativePower(-1);
            brimstone.changeCurrentRelativePower(-2);
            chamber.changeCurrentRelativePower(1);
            clove.changeCurrentRelativePower(-1);
            cypher.changeCurrentRelativePower(1);
            deadlock.changeCurrentRelativePower(2);
            gekko.changeCurrentRelativePower(1);
            harbor.changeCurrentRelativePower(1);
            jett.changeCurrentRelativePower(2);
            kayo.changeCurrentRelativePower(1);
            killjoy.changeCurrentRelativePower(-1);
            omen.changeCurrentRelativePower(-1);
            raze.changeCurrentRelativePower(-2);
            sova.changeCurrentRelativePower(2);
            vyse.changeCurrentRelativePower(2);
            yoru.changeCurrentRelativePower(-1);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void ascentBalance() {
            // Ascent:
            // No change for Astra
            // No change for Deadlock
            // No change for Cypher
            // No change for Fade
            // No change for Harbor
            // No change for Iso
            // No change for Neon
            // No change for Reyna
            // No change for Skye
            // No change for Tejo
            // No change for Yoru
            breach.changeCurrentRelativePower(1);
            brimstone.changeCurrentRelativePower(-2);
            chamber.changeCurrentRelativePower(2);
            clove.changeCurrentRelativePower(1);
            gekko.changeCurrentRelativePower(-1);
            jett.changeCurrentRelativePower(3);
            kayo.changeCurrentRelativePower(3);
            killjoy.changeCurrentRelativePower(3);
            omen.changeCurrentRelativePower(2);
            phoenix.changeCurrentRelativePower(1);
            raze.changeCurrentRelativePower(-2);
            sage.changeCurrentRelativePower(1);
            sova.changeCurrentRelativePower(2);
            viper.changeCurrentRelativePower(2);
            vyse.changeCurrentRelativePower(2);
            waylay.changeCurrentRelativePower(1);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void bindBalance() {
            // Bind:
            // No change for Breach
            // No change for Harbor
            // No change for Kayo
            // No change for Reyna
            // No change for Sage
            // No change for Waylay
            astra.changeCurrentRelativePower(1);
            brimstone.changeCurrentRelativePower(4);
            chamber.changeCurrentRelativePower(2);
            clove.changeCurrentRelativePower(1);
            cypher.changeCurrentRelativePower(1);
            deadlock.changeCurrentRelativePower(1);
            fade.changeCurrentRelativePower(2);
            gekko.changeCurrentRelativePower(1);
            iso.changeCurrentRelativePower(2);
            jett.changeCurrentRelativePower(-1);
            killjoy.changeCurrentRelativePower(-4);
            neon.changeCurrentRelativePower(-1);
            omen.changeCurrentRelativePower(-2);
            phoenix.changeCurrentRelativePower(1);
            raze.changeCurrentRelativePower(4);
            skye.changeCurrentRelativePower(1);
            sova.changeCurrentRelativePower(-1);
            tejo.changeCurrentRelativePower(1);
            viper.changeCurrentRelativePower(4);
            vyse.changeCurrentRelativePower(3);
            yoru.changeCurrentRelativePower(2);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void breezeBalance() {
            // Breeze:
            // No change for Iso
            // No change for Skye
            astra.changeCurrentRelativePower(-1);
            breach.changeCurrentRelativePower(-2);
            brimstone.changeCurrentRelativePower(-3);
            chamber.changeCurrentRelativePower(1);
            clove.changeCurrentRelativePower(-2);
            cypher.changeCurrentRelativePower(2);
            deadlock.changeCurrentRelativePower(-1);
            fade.changeCurrentRelativePower(-1);
            gekko.changeCurrentRelativePower(1);
            harbor.changeCurrentRelativePower(1);
            jett.changeCurrentRelativePower(2);
            kayo.changeCurrentRelativePower(1);
            killjoy.changeCurrentRelativePower(-2);
            neon.changeCurrentRelativePower(-1);
            omen.changeCurrentRelativePower(-2);
            phoenix.changeCurrentRelativePower(-2);
            raze.changeCurrentRelativePower(-2);
            reyna.changeCurrentRelativePower(-1);
            sage.changeCurrentRelativePower(-3);
            sova.changeCurrentRelativePower(2);
            tejo.changeCurrentRelativePower(-1);
            viper.changeCurrentRelativePower(2);
            vyse.changeCurrentRelativePower(1);
            waylay.changeCurrentRelativePower(-1);
            yoru.changeCurrentRelativePower(1);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void corrodeBalance() {
            // Corrode:
            // No change for Astra
            // No change for Breach
            // No change for Clove
            // No change for Harbor
            // No change for Iso
            // No change for Jett
            // No change for Reyna
            // No change for Yoru
            brimstone.changeCurrentRelativePower(1);
            chamber.changeCurrentRelativePower(2);
            cypher.changeCurrentRelativePower(2);
            deadlock.changeCurrentRelativePower(2);
            fade.changeCurrentRelativePower(1);
            gekko.changeCurrentRelativePower(1);
            kayo.changeCurrentRelativePower(1);
            killjoy.changeCurrentRelativePower(-1);
            neon.changeCurrentRelativePower(2);
            omen.changeCurrentRelativePower(1);
            phoenix.changeCurrentRelativePower(1);
            raze.changeCurrentRelativePower(1);
            sage.changeCurrentRelativePower(2);
            skye.changeCurrentRelativePower(2);
            sova.changeCurrentRelativePower(1);
            tejo.changeCurrentRelativePower(-1);
            viper.changeCurrentRelativePower(1);
            vyse.changeCurrentRelativePower(3);
            waylay.changeCurrentRelativePower(1);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void fractureBalance() {
            // Fracture:
            // No change for Astra
            // No change for Harbor
            // No change for Iso
            // No change for Jett
            // No change for Phoenix
            // No change for Reyna
            // No change for Sage
            // No change for Skye
            // No change for Waylay
            breach.changeCurrentRelativePower(2);
            brimstone.changeCurrentRelativePower(4);
            chamber.changeCurrentRelativePower(1);
            clove.changeCurrentRelativePower(1);
            cypher.changeCurrentRelativePower(2);
            deadlock.changeCurrentRelativePower(2);
            fade.changeCurrentRelativePower(1);
            gekko.changeCurrentRelativePower(-1);
            kayo.changeCurrentRelativePower(2);
            killjoy.changeCurrentRelativePower(1);
            neon.changeCurrentRelativePower(2);
            omen.changeCurrentRelativePower(-2);
            raze.changeCurrentRelativePower(3);
            sova.changeCurrentRelativePower(1);
            tejo.changeCurrentRelativePower(1);
            viper.changeCurrentRelativePower(-1);
            vyse.changeCurrentRelativePower(2);
            yoru.changeCurrentRelativePower(-2);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void havenBalance() {
            // Haven:
            // No change for Deadlock
            // No change for Harbor
            // No change for Jett
            astra.changeCurrentRelativePower(2);
            breach.changeCurrentRelativePower(4);
            brimstone.changeCurrentRelativePower(-2);
            chamber.changeCurrentRelativePower(1);
            clove.changeCurrentRelativePower(-1);
            cypher.changeCurrentRelativePower(3);
            fade.changeCurrentRelativePower(-1);
            gekko.changeCurrentRelativePower(-2);
            iso.changeCurrentRelativePower(4);
            kayo.changeCurrentRelativePower(-2);
            killjoy.changeCurrentRelativePower(2);
            neon.changeCurrentRelativePower(1);
            omen.changeCurrentRelativePower(3);
            phoenix.changeCurrentRelativePower(1);
            raze.changeCurrentRelativePower(-2);
            reyna.changeCurrentRelativePower(-2);
            sage.changeCurrentRelativePower(-2);
            skye.changeCurrentRelativePower(-2);
            sova.changeCurrentRelativePower(3);
            tejo.changeCurrentRelativePower(1);
            viper.changeCurrentRelativePower(3);
            vyse.changeCurrentRelativePower(2);
            waylay.changeCurrentRelativePower(1);
            yoru.changeCurrentRelativePower(3);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void iceboxBalance() {
            // Icebox:
            // No change for Clove
            astra.changeCurrentRelativePower(-2);
            breach.changeCurrentRelativePower(-2);
            brimstone.changeCurrentRelativePower(-3);
            chamber.changeCurrentRelativePower(1);
            cypher.changeCurrentRelativePower(-4);
            deadlock.changeCurrentRelativePower(-2);
            fade.changeCurrentRelativePower(-1);
            gekko.changeCurrentRelativePower(2);
            harbor.changeCurrentRelativePower(2);
            iso.changeCurrentRelativePower(1);
            jett.changeCurrentRelativePower(1);
            kayo.changeCurrentRelativePower(2);
            killjoy.changeCurrentRelativePower(3);
            neon.changeCurrentRelativePower(-1);
            omen.changeCurrentRelativePower(-1);
            phoenix.changeCurrentRelativePower(-1);
            raze.changeCurrentRelativePower(-1);
            reyna.changeCurrentRelativePower(2);
            sage.changeCurrentRelativePower(5);
            skye.changeCurrentRelativePower(-2);
            sova.changeCurrentRelativePower(3);
            tejo.changeCurrentRelativePower(-2);
            viper.changeCurrentRelativePower(4);
            vyse.changeCurrentRelativePower(-1);
            waylay.changeCurrentRelativePower(-2);
            yoru.changeCurrentRelativePower(-2);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void lotusBalance() {
            // Lotus:
            // No change for Iso
            // No change for Jett
            // No change for Waylay
            astra.changeCurrentRelativePower(1);
            breach.changeCurrentRelativePower(-1);
            brimstone.changeCurrentRelativePower(-3);
            chamber.changeCurrentRelativePower(2);
            clove.changeCurrentRelativePower(1);
            cypher.changeCurrentRelativePower(1);
            deadlock.changeCurrentRelativePower(2);
            fade.changeCurrentRelativePower(4);
            gekko.changeCurrentRelativePower(1);
            harbor.changeCurrentRelativePower(-2);
            kayo.changeCurrentRelativePower(1);
            killjoy.changeCurrentRelativePower(1);
            neon.changeCurrentRelativePower(1);
            omen.changeCurrentRelativePower(2);
            phoenix.changeCurrentRelativePower(-1);
            raze.changeCurrentRelativePower(3);
            reyna.changeCurrentRelativePower(-1);
            sage.changeCurrentRelativePower(-1);
            skye.changeCurrentRelativePower(-2);
            sova.changeCurrentRelativePower(-2);
            tejo.changeCurrentRelativePower(2);
            viper.changeCurrentRelativePower(3);
            vyse.changeCurrentRelativePower(3);
            yoru.changeCurrentRelativePower(1);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void pearlBalance() {
            // Pearl:
            // No change for Chamber
            // No change for Cypher
            // No change for Deadlock
            // No change for Iso
            // No change for Waylay
            astra.changeCurrentRelativePower(3);
            breach.changeCurrentRelativePower(-2);
            brimstone.changeCurrentRelativePower(-2);
            clove.changeCurrentRelativePower(-2);
            fade.changeCurrentRelativePower(1);
            gekko.changeCurrentRelativePower(-1);
            harbor.changeCurrentRelativePower(1);
            jett.changeCurrentRelativePower(1);
            kayo.changeCurrentRelativePower(2);
            killjoy.changeCurrentRelativePower(2);
            neon.changeCurrentRelativePower(2);
            omen.changeCurrentRelativePower(-2);
            phoenix.changeCurrentRelativePower(2);
            raze.changeCurrentRelativePower(-1);
            reyna.changeCurrentRelativePower(-1);
            sage.changeCurrentRelativePower(1);
            skye.changeCurrentRelativePower(-1);
            sova.changeCurrentRelativePower(1);
            tejo.changeCurrentRelativePower(-2);
            viper.changeCurrentRelativePower(1);
            vyse.changeCurrentRelativePower(2);
            yoru.changeCurrentRelativePower(1);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void splitBalance() {
            // Split:
            // No change for Breach
            // No change for Clove
            // No change for Deadlock
            // No change for Gekko
            // No change for Iso
            // No change for Jett
            // No change for Waylay
            astra.changeCurrentRelativePower(2);
            brimstone.changeCurrentRelativePower(-1);
            chamber.changeCurrentRelativePower(1);
            cypher.changeCurrentRelativePower(1);
            fade.changeCurrentRelativePower(2);
            harbor.changeCurrentRelativePower(2);
            kayo.changeCurrentRelativePower(2);
            killjoy.changeCurrentRelativePower(-2);
            neon.changeCurrentRelativePower(-1);
            omen.changeCurrentRelativePower(1);
            phoenix.changeCurrentRelativePower(1);
            raze.changeCurrentRelativePower(4);
            reyna.changeCurrentRelativePower(-2);
            sage.changeCurrentRelativePower(1);
            skye.changeCurrentRelativePower(1);
            sova.changeCurrentRelativePower(-3);
            tejo.changeCurrentRelativePower(1);
            viper.changeCurrentRelativePower(3);
            vyse.changeCurrentRelativePower(1);
            yoru.changeCurrentRelativePower(2);
      }

      /**
       * Applies agent balance adjustments specific to the map.
       */
      public void sunsetBalance() {
            // Sunset:
            // No change for Astra
            // No change for Brimstone
            // No change for Clove
            // No change for Iso
            // No change for Jett
            // No change for Phoenix
            // No change for Skye
            // No change for Waylay
            breach.changeCurrentRelativePower(1);
            chamber.changeCurrentRelativePower(2);
            cypher.changeCurrentRelativePower(2);
            deadlock.changeCurrentRelativePower(2);
            fade.changeCurrentRelativePower(2);
            gekko.changeCurrentRelativePower(1);
            harbor.changeCurrentRelativePower(2);
            kayo.changeCurrentRelativePower(2);
            killjoy.changeCurrentRelativePower(-3);
            neon.changeCurrentRelativePower(2);
            omen.changeCurrentRelativePower(1);
            raze.changeCurrentRelativePower(1);
            reyna.changeCurrentRelativePower(-1);
            sage.changeCurrentRelativePower(2);
            sova.changeCurrentRelativePower(2);
            tejo.changeCurrentRelativePower(1);
            viper.changeCurrentRelativePower(2);
            vyse.changeCurrentRelativePower(2);
            yoru.changeCurrentRelativePower(1);
      }

      /************* Getter methods *************/
      /**
       * Returns an immutable view of the agent list.
       *
       * This method provides access to all agents in the list while preventing
       * external modification. The returned list reflects the current state of
       * agent balancing and cannot be modified directly.
       *
       * @return an unmodifiable List containing all Agent objects
       * @throws UnsupportedOperationException if any modification operation is
       *                                       attempted on the returned list
       */
      public List<Agent> getList() {
            return java.util.Collections.unmodifiableList(list);
      }

      /**
       * Retrieves an agent by name using case-insensitive lookup.
       *
       * This method performs a case-insensitive search for an agent by name,
       * automatically trimming whitespace from the input. It provides null
       * safety by returning null for null input rather than throwing an exception.
       *
       * @param name the name of the agent to retrieve (case-insensitive,
       *             automatically trimmed)
       * @return the Agent object with the specified name, or null if not found or if
       *         name is null
       */
      public Agent getAgentByName(String name) {
            if (name == null) {
                  return null;
            }
            return agentMap.get(name.trim().toLowerCase());
      }
}