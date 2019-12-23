package com.utils;

public class Info {
    public String name, type;
    public String value;

    public Info(String name, String type, String value){
        this.name = name;
        this.type = type;
        this.value = value;
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
