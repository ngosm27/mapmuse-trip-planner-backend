package com.example.demo.user;

import java.util.List;

public class UserPreferences {

    private Basics basics;
    private Budget budget;
    private Experience experience;
    private FoodAndLifestyle foodAndLifestyle;

    public static class Basics {
        private String name;
        private String ageRange;
        private String location;

        // getters & setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAgeRange() { return ageRange; }
        public void setAgeRange(String ageRange) { this.ageRange = ageRange; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    public static class Budget {
        private String budgetLevel;
        private String spendingStyle;

        public String getBudgetLevel() { return budgetLevel; }
        public void setBudgetLevel(String budgetLevel) { this.budgetLevel = budgetLevel; }

        public String getSpendingStyle() { return spendingStyle; }
        public void setSpendingStyle(String spendingStyle) { this.spendingStyle = spendingStyle; }
    }

    public static class Experience {
        private List<String> tripTypes;
        private String travelPace;
        private String wakeUp;
        private String downtime;
        private String idealDay;

        public List<String> getTripTypes() { return tripTypes; }
        public void setTripTypes(List<String> tripTypes) { this.tripTypes = tripTypes; }

        public String getTravelPace() { return travelPace; }
        public void setTravelPace(String travelPace) { this.travelPace = travelPace; }

        public String getWakeUp() { return wakeUp; }
        public void setWakeUp(String wakeUp) { this.wakeUp = wakeUp; }

        public String getDowntime() { return downtime; }
        public void setDowntime(String downtime) { this.downtime = downtime; }

        public String getIdealDay() { return idealDay; }
        public void setIdealDay(String idealDay) { this.idealDay = idealDay; }
    }

    public static class FoodAndLifestyle {
        private String dietaryRestrictions;  // nullable
        private int foodAdventure;
        private String alcohol;

        public String getDietaryRestrictions() { return dietaryRestrictions; }
        public void setDietaryRestrictions(String dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }

        public int getFoodAdventure() { return foodAdventure; }
        public void setFoodAdventure(int foodAdventure) { this.foodAdventure = foodAdventure; }

        public String getAlcohol() { return alcohol; }
        public void setAlcohol(String alcohol) { this.alcohol = alcohol; }
    }

    // --- Root getters & setters ---

    public Basics getBasics() { return basics; }
    public void setBasics(Basics basics) { this.basics = basics; }

    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }

    public Experience getExperience() { return experience; }
    public void setExperience(Experience experience) { this.experience = experience; }

    public FoodAndLifestyle getFoodAndLifestyle() { return foodAndLifestyle; }
    public void setFoodAndLifestyle(FoodAndLifestyle foodAndLifestyle) { this.foodAndLifestyle = foodAndLifestyle; }
}