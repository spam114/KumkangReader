package com.example.kumkangreader.Object;

import java.io.Serializable;

public class ScrapData implements Serializable {

    public String WorderLot;
    public String PartCode;
    public String PartSpec;
    public String MSpec;
    public String ScrapCode;
    public String ScrapGrade;
    public String ScrappedQty;
    public String ScrappedWeight;
    public String MoveNo;
    public String MoveSeqNo;
    public String MLOT;
    public String QRCode;
    public String OutsourcingCost;
    public String ScrapCodeName;
    public String PartName;
    public String MeasureUnit;
    public String UnitWeight;

    public ScrapData(
            String WorderLot,
            String PartCode,
            String PartSpec,
            String MSpec,
            String ScrapCode,
            String ScrapGrade,
            String ScrappedQty,
            String ScrappedWeight,
            String MoveNo,
            String MoveSeqNo,
            String MLOT,
            String QRCode,
            String OutsourcingCost,
            String ScrapCodeName,
            String PartName,
            String MeasureUnit,
            String UnitWeight
    ) {
        this.WorderLot = WorderLot;
        this.PartCode = PartCode;
        this.PartSpec = PartSpec;
        this.MSpec = MSpec;
        this.ScrapCode = ScrapCode;
        this.ScrapGrade = ScrapGrade;
        this.ScrappedQty = ScrappedQty;
        this.ScrappedWeight = ScrappedWeight;
        this.MoveNo = MoveNo;
        this.MoveSeqNo = MoveSeqNo;
        this.MLOT = MLOT;
        this.QRCode = QRCode;
        this.OutsourcingCost = OutsourcingCost;
        this.ScrapCodeName = ScrapCodeName;
        this.PartName = PartName;
        this.MeasureUnit = MeasureUnit;
        this.UnitWeight = UnitWeight;
    }
}