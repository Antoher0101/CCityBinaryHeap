package com.antoher.oop.models;

import com.antoher.oop.annotations.ColumnName;

public class CCity implements Comparable<CCity> {

    @ColumnName(name = "Население")
    private int population;

    @ColumnName(name = "Площадь")
    private double area;

    @ColumnName(name = "Название")
    private String name;

    @ColumnName(name = "Есть аэропорт")
    private boolean hasAirport;

    public CCity(int population, double area, String name, boolean hasAirport) {
        this.population = population;
        this.area = area;
        this.name = name;
        this.hasAirport = hasAirport;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasAirport() {
        return hasAirport;
    }

    public void setHasAirport(boolean hasAirport) {
        this.hasAirport = hasAirport;
    }

    @Override
    public int compareTo(CCity o) {
        if (population < o.population)
            return -1;
        else if (population > o.population) {
            return 1;
        }
        return 0;
    }
}
