package com.marlostrinidad.wegeek.nerdzone.RatingBar;

import java.util.Date;

public class ReviewModel {

    private Date timeStamp;
    private double totalStarGiven;

    public ReviewModel( Date timeStamp, double totalStarGiven) {
        this.timeStamp = timeStamp;
        this.totalStarGiven = totalStarGiven;
    }

    public ReviewModel() {
    }


    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getTotalStarGiven() {
        return totalStarGiven;
    }

    public void setTotalStarGiven(double totalStarGiven) {
        this.totalStarGiven = totalStarGiven;
    }
}