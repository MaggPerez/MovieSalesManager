package com.example.magdaleno_perez_assignment4;
//I certify that this submission is my original work - Magdaleno Perez

import com.google.gson.annotations.SerializedName;

public class Movie {
    //Might need toString override
    @SerializedName("title")
    private String title;
    @SerializedName("year")
    private int year;
    @SerializedName("sales")
    private double sales;

    public Movie(String title, int year, double sales) {
        this.title = title;
        this.year = year;
        this.sales = sales;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getSales() {
        return sales;
    }

    public void setSales(float sales) {
        this.sales = sales;
    }

    @Override
    public String toString() {
        return title + " " + year + " " + sales;
    }
}
