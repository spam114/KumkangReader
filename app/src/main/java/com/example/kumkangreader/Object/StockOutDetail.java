package com.example.kumkangreader.Object;

import java.io.Serializable;

public class StockOutDetail implements Serializable {

    public String PartCode;
    public String PartSpec;
    public String PartName;
    public String PartSpecName;
    public String OutQty;
    public String ScanQty;

    public StockOutDetail(String PartCode, String PartSpec, String PartName, String PartSpecName, String OutQty, String ScanQty){
        this.PartCode=PartCode;
        this.PartSpec=PartSpec;
        this.PartName=PartName;
        this.PartSpecName=PartSpecName;
        this.OutQty=OutQty;
        this.ScanQty=ScanQty;

    }
}
