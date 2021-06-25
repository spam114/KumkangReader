package com.example.kumkangreader.Object;

import java.io.Serializable;

public class Inventory implements Serializable {

    public String ItemTag = "";
    public String SeqNo="";
    public String PartName="";
    public String PartSpecName="";
    public String Qty="";

    public Inventory() {
        super();
    }
}