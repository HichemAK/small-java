package com.utils;

public class Info {
    public String name, type;
    public int length;

    public Info(String name, String type, int length){
        this.name = name;
        this.type = type;
        this.length = length;
    }

    public Info(String name, String type){
        this.name = name;
        this.type = type;
    }
    public Info(String name){
        this.name = name;
        this.type = null;
    }

    @Override
    public String toString() {
        return this.name + ":" + this.type;
    }
}
