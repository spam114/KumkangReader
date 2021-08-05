package com.example.kumkangreader.Object;

import java.io.Serializable;

public class Stock implements Serializable {

    public String PartCode = "";
    public String PartName = "";
    public String PartSpecName="";
    public String Qty="";
    public String Size1 = "";//두께
    public String Size2 = "";//폭
    public String Weight="";//중량

    public Stock() {
        super();
    }
}
