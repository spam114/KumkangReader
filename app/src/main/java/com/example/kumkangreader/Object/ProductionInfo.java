package com.example.kumkangreader.Object;

import java.io.Serializable;

public class ProductionInfo implements Serializable {

    public String OutputQty;
    public String OutputQtyBD;
    public String CostCenter;
    public String CostCenterName;
    public String WorksOrderNo;
    public String InputQty;
    public String InputQtyBD;
    public String IssueOutputQty;
    public String IssueOutputQtyBD;
    public String LocationNo;
    public String ScrappedQty;
    public String CenterSpec;
    public String PartName;
    public String PartSpecName;

    public ProductionInfo(
            String OutputQty,
            String OutputQtyBD,
            String CostCenter,
            String CostCenterName,
            String WorksOrderNo,
            String InputQty,
            String InputQtyBD,
            String IssueOutputQty,
            String IssueOutputQtyBD,
            String LocationNo,
            String ScrappedQty,
            String CenterSpec,
            String PartName,
            String PartSpecName
    ){
        this.OutputQty=OutputQty;
        this.OutputQtyBD=OutputQtyBD;
        this.CostCenter=CostCenter;
        this.CostCenterName=CostCenterName;
        this.WorksOrderNo=WorksOrderNo;
        this.InputQty=InputQty;
        this.InputQtyBD=InputQtyBD;
        this.IssueOutputQty=IssueOutputQty;
        this.IssueOutputQtyBD=IssueOutputQtyBD;
        this.LocationNo=LocationNo;
        this.ScrappedQty=ScrappedQty;
        this.CenterSpec=CenterSpec;
        this.PartName=PartName;
        this.PartSpecName=PartSpecName;

    }
}
