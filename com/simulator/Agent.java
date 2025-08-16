package com.simulator;

public class Agent {
   private final String name;
   private final String role;
   private double relativePower;
   private double aggr;
   private double cont;
   private double midr;

   public Agent(String nam, String rol, double aggro, double control, double midrange, double relPower) {
      name = nam;
      role = rol;
      aggr = aggro;
      cont = control;
      midr = midrange;
      relativePower = relPower;
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

   public double getRelativePower() {
      return relativePower;
   }

   public void increaseRelativePower(double amount) {
      if (amount > 0) {
         relativePower += amount;
      }
   }

   public void increaseAggro(double amount) {
      if (amount > 0) {
         aggr += amount;
      }
   }

   public void increaseControl(double amount) {
      if (amount > 0) {
         cont += amount;
      }
   }

   public void increaseMidrange(double amount) {
      if (amount > 0) {
         midr += amount;
      }
   }

   public void multiplyAggro(double factor) {
      if (factor > 0) {
         aggr *= factor;
      }
   }

   public void multiplyControl(double factor) {
      if (factor > 0) {
         cont *= factor;
      }
   }

   public void multiplyMidrange(double factor) {
      if (factor > 0) {
         midr *= factor;
      }
   }

   @Override
   public String toString() {
      return name + ", " + (Math.round(aggr * 10000.0) / 10000.0) + "/" + (Math.round(cont * 10000.0) / 10000.0) + "/"
            + (Math.round(midr * 10000.0) / 10000.0) + ", Relative Power: " + relativePower;
   }
}