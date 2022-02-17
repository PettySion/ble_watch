package com.szip.healthy.Model;

public class ReportData {

    private int averageData;
    private int maxData;
    private int minData;
    private long time;

    public ReportData(int averageData, int maxData, int minData, long time) {
        this.averageData = averageData;
        this.maxData = maxData;
        this.minData = minData;
        this.time = time;
    }

    public ReportData(int averageData, long time) {
        this.averageData = averageData;
        this.time = time;
    }

    public int getAverageData() {
        return averageData;
    }

    public int getMaxData() {
        return maxData;
    }

    public int getMinData() {
        return minData;
    }

    public long getTime() {
        return time;
    }
}
