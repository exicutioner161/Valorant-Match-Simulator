package com.simulator;

import java.util.Objects;

/**
 * This class represents an Agent in Valorant with their stylistic attributes
 * and relative power in the current state of the game.
 * Features:
 * - Static attributes (name, role, baseAggro, baseControl, baseMidrange)
 * - A dynamic attribute (relative power) that can be modified
 * depending on the map and current meta.
 * - Splash attributes for certain agents that provide minor boosts to specific
 * styles. They can be one of: AGGRO, CONTROL, MIDRANGE, or NONE. They increase
 * an agent's true style value of the corresponding style by 1 point.
 * - True style values that are used in calculating advantages in matches
 * - Base style values are displayed to the user.
 * - Thread-safe implementation using synchronized methods for mutable state.
 *
 * @author exicutioner161
 * @version 0.1.9-alpha
 * @see AgentList
 */

public class Agent {
   private final String name;
   private final String role;
   private final String splash;
   private final double baselineRelativePower;
   private final double baseAggro;
   private final double baseControl;
   private final double baseMidrange;
   private final boolean hasSplash;
   private volatile double trueAggro;
   private volatile double trueControl;
   private volatile double trueMidrange;

   private volatile double currentRelativePower;

   public Agent(String name, String role, double aggro, double control, double midrange, double relativePower,
         String splash) {
      this.name = Objects.requireNonNull(name, "Invalid agent name. Name cannot be null.");
      this.role = Objects.requireNonNull(role, "Invalid role for Agent: " + name + ". Role cannot be null.");
      if (aggro < 0 || control < 0 || midrange < 0) {
         throw new IllegalArgumentException("Invalid stats for Agent: " + name + ". stats cannot be negative.");
      }

      baseAggro = aggro;
      baseControl = control;
      baseMidrange = midrange;
      trueAggro = aggro;
      trueControl = control;
      trueMidrange = midrange;
      baselineRelativePower = relativePower;
      currentRelativePower = relativePower;

      splash = splash.trim();
      if (splash != null && (splash.equals("AGGRO") || splash.equals("CONTROL") || splash.equals("MIDRANGE"))) {
         this.splash = splash;
         hasSplash = true;
      } else {
         this.splash = "NONE";
         hasSplash = false;
      }
   }

   public String getName() {
      return name;
   }

   public String getRole() {
      return role;
   }

   public double getBaseAggro() {
      return baseAggro;
   }

   public double getBaseControl() {
      return baseControl;
   }

   public double getBaseMidrange() {
      return baseMidrange;
   }

   public double getTrueAggro() {
      return trueAggro;
   }

   public double getTrueControl() {
      return trueControl;
   }

   public double getTrueMidrange() {
      return trueMidrange;
   }

   public double getCurrentRelativePower() {
      return currentRelativePower;
   }

   public synchronized void applyStyleSplash() {
      switch (splash) {
         case "AGGRO" -> trueAggro = baseAggro + 2;
         case "CONTROL" -> trueControl = baseControl + 2;
         case "MIDRANGE" -> trueMidrange = baseMidrange + 3;
         default -> {
            // No splash to apply
         }
      }
   }

   public boolean hasSplash() {
      return hasSplash;
   }

   public synchronized void changeCurrentRelativePower(double amount) {
      currentRelativePower += amount;
   }

   public synchronized void resetToBaselineRelativePower() {
      currentRelativePower = baselineRelativePower;
   }

   private double roundToFourDecimals(double value) {
      return Math.round(value * 10000.0) / 10000.0;
   }

   @Override
   public String toString() { // Return base stats for the agent
      return name + ", " + roundToFourDecimals(baseAggro) + "/" + roundToFourDecimals(baseControl) + "/"
            + roundToFourDecimals(baseMidrange) + ", Relative Power: " + currentRelativePower + ", Splash: " + splash;
   }
}