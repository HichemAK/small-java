package com.utils;

import java.util.ArrayList;

public class SymbolTable extends ArrayList<Row>{

    public void assign(Row r){
        int index = this.indexOf(r.getName());
        if (index != -1){
            this.get(index).setValue(r.getValue());
            return;
        }
        this.add(r);
    }

    public int getValue(String id) {

        int index = this.indexOf(id);
        return this.get(index).getValue();
    }

    @Override
    public int indexOf(Object o) {
        String str = (String) o;
        for(int i=0;i<this.size();i++){
            if(this.get(i).getName().equals(str)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        String str = (String) o;
        for(int i=0;i<this.size();i++){
            if(this.get(i).getName().equals(str)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "ID \t| VALUE\n";
        for(int i=0; i<this.size();i++){
            result += this.get(i).toString() + '\n';
        }
        result = result.substring(0, result.length()-1);
        return result;
    }
}
