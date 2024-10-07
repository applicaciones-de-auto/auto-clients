/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.clients;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.clients.Vehicle_Gatepass_Master;
import org.guanzon.auto.controller.clients.Vehicle_Gatepass_Released_Items;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Labor;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Master;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Parts;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Gatepass implements GTransaction{
    final String XML = "Model_VehicleGatepass_Master.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    String psMessagex;
    public JSONObject poJSON;
    
    Vehicle_Gatepass_Master poController;
    Vehicle_Gatepass_Released_Items poVGPItems;
    
    VehicleSalesProposal_Master poVSPMaster;
    VehicleSalesProposal_Labor poVSPLabor;
    VehicleSalesProposal_Parts poVSPParts;
    
    public Vehicle_Gatepass(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new Vehicle_Gatepass_Master(foAppDrver,fbWtParent,fsBranchCd);
        poVGPItems = new Vehicle_Gatepass_Released_Items(foAppDrver);
        
        poVSPMaster =  new VehicleSalesProposal_Master(foAppDrver,fbWtParent,fsBranchCd);
        poVSPLabor = new VehicleSalesProposal_Labor(foAppDrver);
        poVSPParts = new VehicleSalesProposal_Parts(foAppDrver);
        
        poGRider = foAppDrver;
        pbWtParent = fbWtParent;
        psBranchCd = fsBranchCd.isEmpty() ? foAppDrver.getBranchCode() : fsBranchCd;
    }

    @Override
    public int getEditMode() {
        pnEditMode = poController.getEditMode();
        return pnEditMode;
    }
    
    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        return poController.setMaster(fnCol, foData);
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poController.setMaster(fsCol, foData);
    }

    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poController.getMaster(fnCol);
    }

    public Object getMaster(String fsCol) {
        return poController.getMaster(fsCol);
    }
    
    @Override
    public JSONObject newTransaction() {
        poJSON = new JSONObject();
        try{
            poJSON = poController.newTransaction();
            
            if("success".equals(poJSON.get("result"))){
                pnEditMode = poController.getEditMode();
            } else {
                pnEditMode = EditMode.UNKNOWN;
            }
               
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
            pnEditMode = EditMode.UNKNOWN;
        }
        return poJSON;
    }
    
    @Override
    public JSONObject openTransaction(String fsValue) {
        poJSON = new JSONObject();
        
        poJSON = poController.openTransaction(fsValue);
        if("success".equals(poJSON.get("result"))){
            pnEditMode = poController.getEditMode();
        } else {
            pnEditMode = EditMode.UNKNOWN;
        }
        
        poJSON = checkData(poVGPItems.openDetail(fsValue));
        if(!"success".equals((String) poJSON.get("result"))){
            pnEditMode = EditMode.UNKNOWN;
            return poJSON;
        }
        
        if(poController.getMasterModel().getSourceGr() != null){
            if(poController.getMasterModel().getSourceGr().equals("VEHICLE SALES")){
                poJSON = openVSPDetail(poController.getMasterModel().getSourceCD());
                if(!"success".equals(poJSON.get("result"))){
                    return poJSON;
                }
            }
        }
        
        return poJSON;
    }
    
    private JSONObject openVSPDetail(String fsValue){
        if(fsValue != null){
            if(!fsValue.trim().isEmpty()){
                poJSON = poVSPMaster.openTransaction(fsValue);
                if("success".equals(poJSON.get("result"))){
                    pnEditMode = poController.getEditMode();
                } else {
                    pnEditMode = EditMode.UNKNOWN;
                }

                poJSON = poVSPLabor.openDetail(fsValue);
                if(!"success".equals((String) checkData(poJSON).get("result"))){
                    pnEditMode = EditMode.UNKNOWN;
                    return poJSON;
                }

                poJSON = poVSPParts.openDetail(fsValue);
                if(!"success".equals((String) checkData(poJSON).get("result"))){
                    pnEditMode = EditMode.UNKNOWN;
                    return poJSON;
                }
            }
        }
        
        return poJSON;
    }

    @Override
    public JSONObject updateTransaction() {
        poJSON = new JSONObject();  
        poJSON = poController.updateTransaction();
        if("error".equals(poJSON.get("result"))){
            return poJSON;
        }
        pnEditMode = poController.getEditMode();
        return poJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        poJSON = new JSONObject();  
        
        if (!pbWtParent) poGRider.beginTrans();
        
//        poController.setTargetBranchCd(poController.getMasterModel().getBranchCD());
        poJSON =  poController.saveTransaction();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poVGPItems.setTargetBranchCd(poController.getMasterModel().getBranchCD());
        poJSON =  poVGPItems.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        if (!pbWtParent) poGRider.commitTrans();
        
        return poJSON;
    }
    
    private JSONObject checkData(JSONObject joValue){
        if(pnEditMode == EditMode.ADDNEW ||pnEditMode == EditMode.READY || pnEditMode == EditMode.UPDATE){
            if(joValue.containsKey("continue")){
                if(true == (boolean)joValue.get("continue")){
                    joValue.put("result", "success");
                    joValue.put("message", "Record saved successfully.");
                }
            }
        }
        return joValue;
    }

    @Override
    public JSONObject deleteTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject closeTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject postTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject voidTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject cancelTransaction(String fsValue) {
        poJSON = new JSONObject();  
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON =   poController.cancelTransaction(fsValue);
        if("error".equalsIgnoreCase((String) poJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return poJSON;
        }
        if (!pbWtParent) poGRider.commitTrans();
        
        return poJSON;
    }
    
    public JSONObject searchTransaction(String fsValue, boolean fIsActive) {
        poJSON = new JSONObject();  
        poJSON = poController.searchTransaction(fsValue, fIsActive);
        if(!"error".equals((String) poJSON.get("result"))){
            poJSON = openTransaction((String) poJSON.get("sTransNox"));
        }
        return poJSON;
    }

    @Override
    public JSONObject searchWithCondition(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchTransaction(String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchMaster(String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchMaster(int i, String string, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vehicle_Gatepass_Master getMasterModel() {
        return poController;
    }
    
    public Vehicle_Gatepass_Released_Items getVGPItemModel(){return poVGPItems;} 
    public ArrayList getVGPItemList(){return poVGPItems.getDetailList();}
    public Object addVGPItem(){ return poVGPItems.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeVGPItem(int fnRow){ return poVGPItems.removeDetail(fnRow);}
    
    public VehicleSalesProposal_Master getVSPModel(){
        return poVSPMaster;
    }
    
    public VehicleSalesProposal_Labor getVSPLaborModel(){return poVSPLabor;}
    public ArrayList getVSPLaborList(){return poVSPLabor.getDetailList();}
    
    public VehicleSalesProposal_Parts getVSPPartsModel(){ return poVSPParts;}
    public ArrayList getVSPPartsList(){return poVSPParts.getDetailList();}

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Search VSP Transaction
     * @param fsValue handles transaction no or vsp no
     * @param fbByCode when searching thru specific vsp set to true else false
     * @return 
     */
    public JSONObject searchVSP(String fsValue, boolean fbByCode) {
        JSONObject loJSON = poVSPMaster.searchTransaction(fsValue, fbByCode, false);
        if(!"error".equals(loJSON.get("result"))){
            if(((String) loJSON.get("sUDRNoxxx")) == null){
                    loJSON.put("result", "error");
                    loJSON.put("message", "VSP No. "+(String) loJSON.get("sVSPNOxxx")+" has not been issued VDR yet."
                                            + "\n\nLinking aborted.");
                    return loJSON;
            } else {
                if(((String) loJSON.get("sUDRNoxxx")).trim().isEmpty()){
                    loJSON.put("result", "error");
                    loJSON.put("message", "VSP No. "+(String) loJSON.get("sVSPNOxxx")+" has not been issued VDR yet."
                                            + "\n\nLinking aborted.");
                    return loJSON;
                }
            }
            
            poController.getMasterModel().setSourceCD((String) loJSON.get("sTransNox"));
            poController.getMasterModel().setSourceNo((String) loJSON.get("sVSPNOxxx"));
            poController.getMasterModel().setSourceGr("VEHICLE SALES");
            
            loJSON = openVSPDetail((String) loJSON.get("sTransNox"));
            if(!"success".equals(loJSON.get("result"))){
                return loJSON;
            }
            
            //populate Vehicle Gatepass Released Item
            populateVGPItems();
        } else {
            
        }
        
        return loJSON;
    }
    
    private JSONObject populateVGPItems(){
        JSONObject loJSON = new JSONObject();
        int lnCtr = 0;
        int lnVGPCtr = 0;
        boolean lbExist = false;
        for(lnCtr = 0; lnCtr <= poVSPLabor.getDetailList().size() - 1; lnCtr++){
            //Check existence
            for(lnVGPCtr = 0;lnVGPCtr <= poVGPItems.getDetailList().size() - 1;lnVGPCtr++){
                if(poVGPItems.getDetailModel(lnVGPCtr).getLaborCde().equals(poVSPLabor.getDetailModel(lnCtr).getLaborCde())){
                    lbExist = true;
                    break;
                }
            }
            if(!lbExist){
                addVGPItem();
                poVGPItems.getDetailModel(poVGPItems.getDetailList().size()-1).setItemType("l");
                poVGPItems.getDetailModel(poVGPItems.getDetailList().size()-1).setLaborCde(poVSPLabor.getDetailModel(lnCtr).getLaborCde());
                poVGPItems.getDetailModel(poVGPItems.getDetailList().size()-1).setQuantity(1);
                poVGPItems.getDetailModel(poVGPItems.getDetailList().size()-1).setReleased(1);
            }
            //set to default
            lbExist = false;
        }
        
        for(lnCtr = 0; lnCtr <= poVSPParts.getDetailList().size() - 1; lnCtr++){
            //Check existence
            for(lnVGPCtr = 0;lnVGPCtr <= poVGPItems.getDetailList().size() - 1;lnVGPCtr++){
                if(poVGPItems.getDetailModel(lnVGPCtr).getStockID().equals(poVSPParts.getDetailModel(lnCtr).getStockID())){
                    lbExist = true;
                    break;
                }
            }
            if(!lbExist){
                addVGPItem();
                poVGPItems.getDetailModel(poVGPItems.getDetailList().size()-1).setItemType("l");
                poVGPItems.getDetailModel(poVGPItems.getDetailList().size()-1).setStockID(poVSPParts.getDetailModel(lnCtr).getStockID());
                poVGPItems.getDetailModel(poVGPItems.getDetailList().size()-1).setQuantity(poVSPParts.getDetailModel(lnCtr).getQuantity());
                poVGPItems.getDetailModel(poVGPItems.getDetailList().size()-1).setReleased(poVSPParts.getDetailModel(lnCtr).getReleased());
            }
            //set to default
            lbExist = false;
        }
        
        return loJSON;
    }
    
}
