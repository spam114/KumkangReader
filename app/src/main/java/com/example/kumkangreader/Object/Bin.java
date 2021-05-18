package com.example.kumkangreader.Object;

import java.io.Serializable;

public class Bin implements Serializable {

    public String BinNo;
    public String PartCode;
    public String PartSpec;
    public String MSpec;
    public String PartName;
    public String Uncommitted;
    public String Weight;
    public String LinkCode;
    public String Color;
    public String PublishDate;
    public String Used;

    public int RowNum;
    public int ColNum;

    public Bin(
            String BinNo,
            String PartCode,
            String PartSpec,
            String MSpec,
            String PartName,
            String Uncommitted,
            String Weight,
            String LinkCode,
            String Color,
            String PublishDate,
            String Used,
            int RowNum,
            int ColNum
    ) {
        this.BinNo = BinNo;
        this.PartCode = PartCode;
        this.PartSpec = PartSpec;
        this.MSpec = MSpec;
        this.PartName = PartName;
        this.Uncommitted = Uncommitted;
        this.Weight = Weight;
        this.LinkCode = LinkCode;
        this.Color = Color;
        this.PublishDate = PublishDate;
        this.Used = Used;
        this.RowNum=RowNum;
        this.ColNum=ColNum;
    }
}