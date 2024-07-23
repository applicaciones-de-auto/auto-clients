/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.clients;

import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.model.clients.Model_Vehicle_Registration;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Registration implements GRecord {

    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    
    int pnEditMode;
    String psRecdStat;

    Model_Vehicle_Registration poModel;
    JSONObject poJSON;

    public Vehicle_Registration(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poModel = new Model_Vehicle_Registration(foAppDrver);
        
        poGRider = foAppDrver;
        pbWthParent = fbWtParent;
        psBranchCd = fsBranchCd.isEmpty() ? foAppDrver.getBranchCode() : fsBranchCd;
        pnEditMode = EditMode.UNKNOWN;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    @Override
    public void setRecordStatus(String fsValue) {
        psRecdStat = fsValue;
    }

    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        return poModel.setValue(fnCol, foData);
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poModel.setValue(fsCol, foData);
    }

    @Override
    public Object getMaster(int fnCol) {
        return poModel.getValue(fnCol);
    }

    @Override
    public Object getMaster(String fsCol) {
        return poModel.getValue(fsCol);
    }

    @Override
    public JSONObject newRecord() {
        poJSON = new JSONObject();
        try{
            pnEditMode = EditMode.ADDNEW;
            poModel = new Model_Vehicle_Registration(poGRider);
            
            poModel.newRecord();
            if (poModel == null){
                poJSON.put("result", "error");
                poJSON.put("message", "Vehicle Registration: initialized new record failed.");
                return poJSON;
            }else{
                poJSON.put("result", "success");
                poJSON.put("message", "initialized new record.");
            }      

        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        
        return poJSON;
    }

    @Override
    public JSONObject openRecord(String fsValue) {
        poModel = new Model_Vehicle_Registration(poGRider);
        JSONObject loJSON = poModel.openRecord(fsValue);
        
        if(!"error".equals((String) loJSON.get("result"))){
            pnEditMode = poModel.getEditMode();
        } else {
            pnEditMode = EditMode.UNKNOWN;
        }
        return loJSON;
    }

    @Override
    public JSONObject updateRecord() {
        JSONObject loJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.UPDATE) {
            loJSON.put("result", "success");
            loJSON.put("message", "Edit mode has changed to update.");
        } else {
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded to update.");
        } 
        return loJSON;
    }

    @Override
    public JSONObject saveRecord() {
        boolean lbSave = false;
        poJSON = new JSONObject();
        
        if(pnEditMode == EditMode.ADDNEW){
            if(poModel.getPlateNo() != null){
                if(!poModel.getPlateNo().trim().isEmpty()){
                    lbSave = true;
                }
            }
            
            if(poModel.getPlaceReg() != null){
                if(!poModel.getPlaceReg().trim().isEmpty()){
                    lbSave = true;
                }
            } 
            
            if(!lbSave){
                poJSON.put("result", "error");
                poJSON.put("message", "no record to save.");
                return poJSON;
            }
        }
        
//        if (!pbWthParent) {poGRider.beginTrans();}
        poJSON = poModel.saveRecord();

//        if ("success".equals((String) poJSON.get("result"))) {
//            if (!pbWthParent) {poGRider.commitTrans();}
//        } else {
//            if (!pbWthParent) {poGRider.rollbackTrans();}
//        }
        
        return poJSON;
    }

    @Override
    public JSONObject deleteRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject deactivateRecord(String fsValue) {
        poJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.UPDATE) {
            poJSON = poModel.setActive(false);

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poJSON = poModel.saveRecord();
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }

    @Override
    public JSONObject activateRecord(String fsValue) {
        poJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.UPDATE) {
            poJSON = poModel.setActive(true);

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poJSON = poModel.saveRecord();
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }

    @Override
    public JSONObject searchRecord(String fsValue, boolean fbByCode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Model_Vehicle_Registration getModel() {
        return poModel;
    }
    
    
}
