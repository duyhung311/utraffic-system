package com.hcmut.admin.utraffictest.model;

public class Deal {
    private int id;
    private int type;
    private String time;
    private double cost;
    private String name;
    public Deal(int id,int type,String time,double cost,String name){
        this.id=id;
        this.type = type;
        this.time=time;
        this.cost=cost;
        this.name=name;
    }
    public int getId() {
        return id;
    }
    public int getType() {
        return type;
    }
    public String getTime() {
        return time;
    }
    public double getCost() {
        return cost;
    }
    public String getName() {
        return name;
    }




}
