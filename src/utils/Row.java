package utils;

public class Row{
    private String name;
    private int value;
    public Row(String name, int value){
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " \t| " + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Row){
            Row r = (Row) obj;
            return r.name.equals(this.name);
        }
        else if(obj instanceof String){
            System.out.println("HAHAHA");
            String name = (String) obj;
            return this.name.equals(name);
        }
        return false;
    }
}
