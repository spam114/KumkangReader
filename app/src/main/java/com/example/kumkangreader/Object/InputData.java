package com.example.kumkangreader.Object;

import java.io.Serializable;

public class InputData implements Serializable {

    public String ItemTag;
    public String CoilNo;
    public String PartCode;
    public String PartName;
    public String PartSpec;
    public String PartSpecName;
    public String MSpec;
    public String Qty;
    public String UseFlag;
    public String SeqNo;

    public InputData(
             String ItemTag,
             String CoilNo,
             String PartCode,
             String PartName,
             String PartSpec,
             String PartSpecName,
             String MSpec,
             String Qty,
             String UseFlag,
             String SeqNo
    ){
        this.ItemTag=ItemTag;
        this.CoilNo=CoilNo;
        this.PartCode=PartCode;
        this.PartName=PartName;
        this.PartSpec=PartSpec;
        this.PartSpecName=PartSpecName;
        this.MSpec=MSpec;
        this.Qty=Qty;
        this.UseFlag=UseFlag;
        this.SeqNo=SeqNo;
    }
}