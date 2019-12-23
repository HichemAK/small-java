package com.utils;

public class Row{
    private String name;
    private String value;
    private String type;
    public Row(String name, String type, String value){
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " \t| " + type + " \t| " + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Row){
            Row r = (Row) obj;
            return r.name.equals(this.name);
        }
        else if(obj instanceof String){
            String name = (String) obj;
            return this.name.equals(name);
        }
        return false;
    }
}
