package com.example.pacharapoldeesawat.demohospital.Model;

/**
 * Created by pacharapoldeesawat on 10/29/2017 AD.
 */

public class Queue {

    private String id;
    private int time;
    private String type;
    private int queueNum;

    public Queue(){}

    public Queue(String id, int time, String type, int queueNum) {
        this.id = id;
        this.time = time;
        this.type = type;
        this.setQueueNum(queueNum);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQueueNum() {
        return queueNum;
    }

    public void setQueueNum(int queueNum) {
        this.queueNum = queueNum;
    }
}
