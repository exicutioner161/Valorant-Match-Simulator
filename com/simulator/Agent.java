package com.simulator;

/**
 * Represents an Agent in Valorant with their stylistic attributes and relative power in the current state of the game.
 * Each agent has static attributes (name, role, aggro, control, midrange) and a dynamic attribute (relative power)
 * that can be modified depending on the map and current meta.
 *
 * @author exicutioner161
 * @version 0.1.4-alpha
 * @see AgentList
 */

public class Agent {
   private final String name;
   private final String role;
   private final double baselineRelativePower;
   private double currentRelativePower;

   private final double aggr;
   private final double cont;
   private final double midr;

   public Agent(String name, String role, double aggro, double control, double midrange, double relPower) {
      this.name = name;
      this.role = role;
      aggr = aggro;
      cont = control;
      midr = midrange;
      baselineRelativePower = relPower;
      currentRelativePower = relPower;

   }

   public String getName() {
      return name;
   }

   public String getRole() {
      return role;
   }

   public double getAggro() {
      return aggr;
   }

   public double getControl() {
      return cont;
   }

   public double getMidrange() {
      return midr;
   }

   public double getCurrentRelativePower() {
      return currentRelativePower;
   }

   public void changeCurrentRelativePower(double amount) {
      currentRelativePower += amount;
   }

   public void resetToBaselineRelativePower() {
      currentRelativePower = baselineRelativePower;
   }

   private double roundToFourDecimals(double value) {
      return Math.round(value * 10000.0) / 10000.0;
   }

   @Override
   public String toString() {
      return name + ", " + roundToFourDecimals(aggr) + "/" + roundToFourDecimals(cont) + "/" + roundToFourDecimals(midr)
            + ", Relative Power: " + currentRelativePower;
   }
}