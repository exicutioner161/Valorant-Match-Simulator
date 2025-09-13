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
 * @version 0.2.0-alpha
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

   /**
    * Constructs a new Agent with the specified attributes and statistics.
    *
    * This constructor initializes all agent properties including base statistics,
    * relative power, and splash attributes. It validates input parameters and
    * sets up both base and true style values. The splash attribute is processed
    * to determine if the agent has a style boost.
    *
    * @param name          the agent's name (must not be null)
    * @param role          the agent's role classification (must not be null)
    * @param aggro         the base aggro style value (must be non-negative)
    * @param control       the base control style value (must be non-negative)
    * @param midrange      the base midrange style value (must be non-negative)
    * @param relativePower the agent's relative power level in the meta
    * @param splash        the splash attribute ("AGGRO", "CONTROL", "MIDRANGE", or
    *                      any other value for "NONE")
    * @throws NullPointerException     if name or role is null
    * @throws IllegalArgumentException if any style value is negative
    */
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

   /**
    * Gets the agent's name.
    *
    * @return the agent's name as a string
    */
   public String getName() {
      return name;
   }

   /**
    * Gets the agent's role classification.
    *
    * @return the agent's role (e.g., "DUELIST", "CONTROLLER", "INITIATOR",
    *         "SENTINEL")
    */
   public String getRole() {
      return role;
   }

   /**
    * Gets the agent's base aggro style value.
    *
    * This is the original aggro value before any splash bonuses are applied.
    *
    * @return the base aggro style value
    */
   public double getBaseAggro() {
      return baseAggro;
   }

   /**
    * Gets the agent's base control style value.
    *
    * This is the original control value before any splash bonuses are applied.
    *
    * @return the base control style value
    */
   public double getBaseControl() {
      return baseControl;
   }

   /**
    * Gets the agent's base midrange style value.
    *
    * This is the original midrange value before any splash bonuses are applied.
    *
    * @return the base midrange style value
    */
   public double getBaseMidrange() {
      return baseMidrange;
   }

   /**
    * Gets the agent's true aggro style value.
    *
    * This is the effective aggro value after splash bonuses are applied.
    * Used in match calculations for determining style advantages.
    *
    * @return the true aggro style value (may include splash bonus)
    */
   public double getTrueAggro() {
      return trueAggro;
   }

   /**
    * Gets the agent's true control style value.
    *
    * This is the effective control value after splash bonuses are applied.
    * Used in match calculations for determining style advantages.
    *
    * @return the true control style value (may include splash bonus)
    */
   public double getTrueControl() {
      return trueControl;
   }

   /**
    * Gets the agent's true midrange style value.
    *
    * This is the effective midrange value after splash bonuses are applied.
    * Used in match calculations for determining style advantages.
    *
    * @return the true midrange style value (may include splash bonus)
    */
   public double getTrueMidrange() {
      return trueMidrange;
   }

   /**
    * Gets the agent's current relative power level.
    *
    * This value can be modified from the baseline through map-specific
    * balance adjustments and represents the agent's current meta strength.
    *
    * @return the current relative power level
    */
   public double getCurrentRelativePower() {
      return currentRelativePower;
   }

   /**
    * Applies the agent's splash bonus to their true style values.
    *
    * This method modifies the true style values by adding the appropriate
    * splash bonus based on the agent's splash attribute. The bonuses are:
    * - AGGRO: +2 to true aggro
    * - CONTROL: +2 to true control
    * - MIDRANGE: +3 to true midrange
    * - NONE: No bonuses applied
    *
    * This method is thread-safe and should be called after agent initialization.
    */
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

   /**
    * Checks if the agent has a splash bonus.
    *
    * @return true if the agent has a style splash bonus, false otherwise
    */
   public boolean hasSplash() {
      return hasSplash;
   }

   /**
    * Modifies the agent's current relative power by the specified amount.
    *
    * This method is used for map-specific balance adjustments to increase
    * or decrease an agent's power level from their baseline value.
    *
    * @param amount the amount to add to the current relative power (can be
    *               negative)
    */
   public synchronized void changeCurrentRelativePower(double amount) {
      currentRelativePower += amount;
   }

   /**
    * Resets the agent's relative power to their baseline value.
    *
    * This method restores the agent's power level to their original
    * baseline value, removing any map-specific adjustments.
    */
   public synchronized void resetToBaselineRelativePower() {
      currentRelativePower = baselineRelativePower;
   }

   /**
    * Rounds a double value to four decimal places.
    *
    * This utility method provides consistent precision for displaying
    * agent statistics and ensures clean formatting in string representations.
    *
    * @param value the double value to round
    * @return the value rounded to four decimal places
    */
   private double roundToFourDecimals(double value) {
      return Math.round(value * 10000.0) / 10000.0;
   }

   /**
    * Returns a string representation of the agent showing their base statistics.
    *
    * The format includes the agent's name, base style values
    * (aggro/control/midrange),
    * current relative power, and splash attribute. Base values are displayed with
    * four decimal place precision for consistency.
    *
    * @return a formatted string containing the agent's base statistics and
    *         attributes
    */
   @Override
   public String toString() { // Return base stats for the agent
      return name + ", " + roundToFourDecimals(baseAggro) + "/" + roundToFourDecimals(baseControl) + "/"
            + roundToFourDecimals(baseMidrange) + ", Relative Power: " + currentRelativePower + ", Splash: " + splash;
   }
}