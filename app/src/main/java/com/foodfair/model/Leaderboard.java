package com.foodfair.model;

import com.google.firebase.Timestamp;

import java.util.Map;

public class Leaderboard {

    public static final String FIELD_PERIOD_NUMBER = "period-number";
    public static final String FIELD_PERIOD_START = "period-start";
    public static final String FIELD_PERIOD_END = "period-end";
    public static final String FIELD_RANKING = "ranking";

    private Integer periodNum;
    private Timestamp periodStart;
    private Timestamp periodEnd;
    private Map<String, Object>[] ranking;

    public Leaderboard() {}

    public Leaderboard(Integer periodNum, Timestamp periodStart, Timestamp periodEnd,
                       Map<String, Object>[] ranking){
        this.periodNum = periodNum;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.ranking = ranking;
    }

    public Integer getPeriodNum() {
        return periodNum;
    }

    public void setPeriodNum(Integer periodNum) {
        this.periodNum = periodNum;
    }

    public Timestamp getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Timestamp periodStart) {
        this.periodStart = periodStart;
    }

    public Timestamp getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Timestamp periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Map<String, Object>[] getRanking() {
        return ranking;
    }

    public void setRanking(Map<String, Object>[] ranking) {
        this.ranking = ranking;
    }
}
