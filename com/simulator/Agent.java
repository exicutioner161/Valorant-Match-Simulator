package com.simulator;

public class Agent {
   private final String name;
   private final String role;
   private final int ability1; // Signature ability / "E" ability
   private final int ability2; // "Q" ability
   private final int ability3; // "C" ability
   private final int maxUltPoints; // Ultimate ability
   private int currentUltPoints; // Current ultimate points
   private final int aggr;
   private final int cont;
   private final int midr;

   public Agent(String nam, String rol, int eAbility, int qAbility, int cAbility, int maximumUltPoints, int aggro,
         int control, int midrange) {
      name = nam;
      role = rol;
      ability1 = eAbility;
      ability2 = qAbility;
      ability3 = cAbility;
      maxUltPoints = maximumUltPoints;
      currentUltPoints = 0;
      aggr = aggro;
      cont = control;
      midr = midrange;
   }

   public void incrementUltPoints() {
      if (currentUltPoints < maxUltPoints) {
         currentUltPoints++;
      } else {
         currentUltPoints = maxUltPoints;
      }
   }

   public void resetUltPoints() {
      currentUltPoints = 0;
   }

   public String getName() {
      return name;
   }

   public String getRole() {
      return role;
   }

   public int getAbility1() {
      return ability1;
   }

   public int getAbility2() {
      return ability2;
   }

   public int getAbility3() {
      return ability3;
   }

   public int getMaxUltPoints() {
      return maxUltPoints;
   }

   public int getCurrentUltPoints() {
      return currentUltPoints;
   }

   public int getAggro() {
      return aggr;
   }

   public int getControl() {
      return cont;
   }

   public int getMidrange() {
      return midr;
   }

   @Override
   public String toString() {
      return name + ", " + aggr + "/" + cont + "/" + midr;
   }
}