package com.example.kumkangreader.Object;

import java.io.Serializable;

public class OutputData implements Serializable {

    public String ItemTag;
    public String CoilNo;
    public String PartCode;
    public String PartName;
    public String PartSpec;
    public String PartSpecName;
    public String Qty;

    public OutputData(
            String ItemTag,
            String CoilNo,
            String PartCode,
            String PartName,
            String PartSpec,
            String PartSpecName,
            String Qty
    ){
        this.ItemTag=ItemTag;
        this.CoilNo=CoilNo;
        this.PartCode=PartCode;
        this.PartName=PartName;
        this.PartSpec=PartSpec;
        this.PartSpecName=PartSpecName;
        this.Qty=Qty;
    }
}