/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.clients;

import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.controller.clients.Vehicle_Serial_Master;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Serial implements GRecord{
    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psRecdStat;
    
    public JSONObject poJSON;
    
    Vehicle_Serial_Master poController;
    
    public Vehicle_Serial(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new Vehicle_Serial_Master(foAppDrver,fbWtParent,fsBranchCd);
        
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
    public void setRecordStatus(String fsValue) {
        psRecdStat = fsValue;
    }

    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        JSONObject obj = new JSONObject(); 
        obj.put("pnEditMode", pnEditMode);
        obj = poController.setMaster(fnCol, foData);
        return obj;
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poController.setMaster(fsCol, foData);
    }

    @Override
    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poController.getMaster(fnCol);
    }

    @Override
    public Object getMaster(String fsCol) {
        return poController.getMaster(fsCol);
    }

    @Override
    public JSONObject newRecord() {
        poJSON = new JSONObject();
        try{
            poJSON = poController.newRecord();
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
    public JSONObject openRecord(String fsValue) {
        poJSON = new JSONObject();
        
        poJSON = poController.openRecord(fsValue);
        
        if("success".equals(poJSON.get("result"))){
            pnEditMode = poController.getEditMode();
        } else {
            pnEditMode = EditMode.UNKNOWN;
        }
        return poJSON;
    }

    @Override
    public JSONObject updateRecord() {
        poJSON = new JSONObject();  
        poJSON = poController.updateRecord();
        pnEditMode = poController.getEditMode();
        return poJSON;
    }

    @Override
    public JSONObject saveRecord() {
        poJSON = poController.saveRecord();
        return poJSON;
    }

    @Override
    public JSONObject deleteRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject deactivateRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject activateRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchRecord(String fsValue,  boolean fbByActive) {
        poJSON = new JSONObject();  
        poJSON = poController.searchRecord(fsValue, fbByActive);
        if(!"error".equals(poJSON.get("result"))){
            poJSON = openRecord((String) poJSON.get("sSerialID"));
        }
        return poJSON;
    }
    
    public JSONObject searchRecord(String fsValue,  boolean fbByActive, boolean fbIsVhclSales) {
        poJSON = new JSONObject();  
        poJSON = poController.searchRecord(fsValue, fbByActive, fbIsVhclSales);
        if(!"error".equals(poJSON.get("result"))){
            poJSON = openRecord((String) poJSON.get("sSerialID"));
        }
        return poJSON;
    }

    @Override
    public Vehicle_Serial_Master getModel() {
        return poController;
    }
    
    /**
     * For searching vehicle make when key is pressed.
     * @param fsValue the search value for the vehicle make.
     * @return {@code true} if a matching vehicle make is found, {@code false} otherwise.
    */
    public JSONObject searchMake(String fsValue){
        return poController.searchMake(fsValue);
    }
    
    /**
     * For searching vehicle make when key is pressed.
     * @param fsValue the search value for the vehicle make.
     * @return {@code true} if a matching vehicle make is found, {@code false} otherwise.
    */
    public JSONObject searchModel(String fsValue){
        return poController.searchModel(fsValue);
    }
    
    /**
     * For searching vehicle type when key is pressed.
     * @param fsValue the search value for the vehicle type.
     * @return {@code true} if a matching vehicle type is found, {@code false} otherwise.
    */
    public JSONObject searchType(String fsValue) {
        return poController.searchType(fsValue);
    }
    
    /**
     * For searching vehicle transmission when key is pressed.
     * @param fsValue the search value for the vehicle transmission.
     * @return {@code true} if a matching vehicle transmission is found, {@code false} otherwise.
    */
    public JSONObject searchTransMsn(String fsValue) {
        return poController.searchTransMsn(fsValue);
    }
    
    /**
     * For searching vehicle color when key is pressed.
     * @param fsValue the search value for the vehicle color.
     * @return {@code true} if a matching vehicle transmission is found, {@code false} otherwise.
    */
    public JSONObject searchColor(String fsValue) {
        return poController.searchColor(fsValue);
    }
    /**
     * For searching vehicle year model when key is pressed.
     * @param fsValue the search value for the vehicle year model.
     * @return {@code true} if a matching vehicle year model is found, {@code false} otherwise.
    */
    public JSONObject searchYearModel(String fsValue) {
        return poController.searchYearModel(fsValue);
    }
    
//    /**
//     * Check Engine Number Pattern
//     * @return 
//     */
//    public JSONObject checkEngineNo(){
//        return poController.checkEngineNo();
//    }
//    
//    /**
//     * Check Make Frame Number Pattern
//     * @return 
//     */
//    public JSONObject checkMakeFrameNo(){
//        return poController.checkMakeFrameNo();
//    }
//    
//    /**
//     * Check Make Frame Number Pattern
//     * @return 
//     */
//    public JSONObject checkModelFrameNo(){
//        return poController.checkModelFrameNo();
//    }
    
    /**
     * Search Ownership / Co - Ownership
     * @param fsValue Owner / Co - Owner Name
     * @param isOwner set TRUE if searching for OWNER, Otherwise set FALSE when searching for CO-OWNER
     * @param isTransfer
     * @return 
     */
    public JSONObject searchOwner(String fsValue, boolean isOwner, boolean isTransfer){
        return poController.searchOwner(fsValue,isOwner,isTransfer);
        
    }
    
    /**
     * For searching dealership when key is pressed.
     * @param fsValue the search value for the dealership.
     * @return {@code true} if a matching dealership is found, {@code false} otherwise: set only for sDealerNm column.
    */
    public JSONObject searchDealer(String fsValue) {
        return poController.searchDealer(fsValue);
    }
    
    /**
     * For searching registered place when key is pressed.
     * @param fsValue the search value for the dealership.
     * @return {@code true} if a matching registered place is found, {@code false} otherwise: set only for sPlaceReg column.
    */
    public JSONObject searchRegsplace(String fsValue){
        return poController.searchRegsplace(fsValue);
        
    }
    
    /**
     * For searching available vehicle when key is pressed.
     * @return {@code true} if a matching available vehicle is found, {@code false} otherwise.
    */
    public JSONObject searchAvailableVhcl(){
        return poController.searchAvailableVhcl();
        
    }
    
    /**
     * Check Existing Vehicle Serial Record
     * @return 
     */
    public JSONObject validateExistingRecord(){
        return poController.validateExistingRecord();
    }
    
    /*Client Information*/
    private JSONObject checkData(JSONObject joValue){
        if(pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.READY || pnEditMode == EditMode.UPDATE){
            if(joValue.containsKey("continue")){
                if(true == (boolean)joValue.get("continue")){
                    joValue.put("result", "success");
                    joValue.put("message", "Record saved successfully.");
                }
            }
        }
        return joValue;
    }
    
    /**
     * Loads the list of vehicles from the database base of client ID.
     * @param fsValue Identify as the client ID
     * @param fbisOwner Identify who will be retrieve
     * @return {@code true} if the list is successfully loaded, {@code false} otherwise.
    */
    public JSONObject LoadVehicleList(String fsValue, boolean fbisOwner) {
        poJSON = new JSONObject();
        if (poGRider == null){
            poJSON.put("result", "error");
            poJSON.put("message", "Application driver is not set.");
            return poJSON;
        }
        
        poJSON = checkData(poController.LoadVehicleList(fsValue, fbisOwner));
        return poJSON;
    }
    
    public ArrayList getVehicleSerialList(){return poController.getVehicleSerialList();}
    public void setVehicleSerialList(ArrayList foObj){this.poController.setVehicleSerialList(foObj);}
    
    public void setVehicleSerial(int fnRow, int fnIndex, Object foValue){ poController.setVehicleSerial(fnRow, fnIndex, foValue);}
    public void setVehicleSerial(int fnRow, String fsIndex, Object foValue){ poController.setVehicleSerial(fnRow, fsIndex, foValue);}
    public Object getVehicleSerial(int fnRow, int fnIndex){return poController.getVehicleSerial(fnRow, fnIndex);}
    public Object getVehicleSerial(int fnRow, String fsIndex){return poController.getVehicleSerial(fnRow, fsIndex);}
}
