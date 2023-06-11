package com.antoher.oop.models;

import com.antoher.oop.annotations.ColumnName;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class CCity implements Comparable<CCity> {
    @ColumnName(name = "ИД", collapsed = true)
    private int id;

    @ColumnName(name = "Население")
    private int population;

    @ColumnName(name = "Площадь")
    private double area;

    @ColumnName(name = "Город")
    private String name;

    @ColumnName(name = "Есть аэропорт")
    private boolean hasAirport;

    private static String sortColumn;

    public static String getSortColumn() {
        return sortColumn;
    }

    public static void setSortColumn(String col) {
        sortColumn = col;
    }

    public CCity(int population, double area, String name, boolean hasAirport) {
        this.population = population;
        this.area = area;
        this.name = name;
        this.hasAirport = hasAirport;
        id = new AtomicInteger().incrementAndGet();
    }

    public CCity(CCity other) {
        this.id = other.getId();
        this.population = other.getPopulation();
        this.area = other.getArea();
        this.name = other.getName();
        this.hasAirport = other.isHasAirport();
    }

    public CCity() {
        id = new AtomicInteger().incrementAndGet();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        if (sortColumn == null) {
            return Integer.compare(this.id, o.id);
        }

        if (sortColumn.equals(getColumnName("name")))
            return this.name.compareTo(o.name);
        if (sortColumn.equals(getColumnName("population")))
            return Integer.compare(this.population, o.population);
        if (sortColumn.equals(getColumnName("area")))
            return Double.compare(this.area, o.area);
        if (sortColumn.equals(getColumnName("hasAirport")))
            return Boolean.compare(this.hasAirport, o.hasAirport);
        return 0;
    }

    private String getColumnName(String fieldName) {
        String columnName = "";
        try {
            Field field = getClass().getDeclaredField(fieldName);
            ColumnName annotation = field.getAnnotation(ColumnName.class);
            columnName = annotation.name();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return columnName;
    }
}
