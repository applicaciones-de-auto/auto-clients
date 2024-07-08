/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.clients;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.model.clients.Model_Vehicle_Serial_Master;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Serial_Master implements GRecord {

    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    
    int pnEditMode;
    String psRecdStat;

    ArrayList<Model_Vehicle_Serial_Master> paModel;
    Model_Vehicle_Serial_Master poModel;
    
    JSONObject poJSON;

    public Vehicle_Serial_Master(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poModel = new Model_Vehicle_Serial_Master(foAppDrver);
        
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
        return poModel.newRecord();
    }

    @Override
    public JSONObject openRecord(String fsValue) {
        return poModel.openRecord(fsValue);
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
        if (!pbWthParent) {
            poGRider.beginTrans();
        }

        poJSON = poModel.saveRecord();
        
        if ("success".equals((String) poJSON.get("result"))) {
            if (!pbWthParent) {
                poGRider.commitTrans();
            }
        } else {
            if (!pbWthParent) {
                poGRider.rollbackTrans();
            }
        }
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
//            poJSON = poModel.setActive(false);

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
//            poJSON = poModel.setActive(true);

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
        String lsCondition = "";
        String lsSQL = "";

        if (psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
            }

            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "cRecdStat = " + SQLUtil.toSQL(psRecdStat);
        }
        
        if(fbByCode){
            lsSQL = MiscUtil.addCondition(poModel.makeSelectSQL(), " sSerialID = "
                + SQLUtil.toSQL(fsValue) + " AND " + lsCondition);
        } else {
            lsSQL = MiscUtil.addCondition(poModel.makeSelectSQL(), "( sPlateNox LIKE "
                + SQLUtil.toSQL(fsValue + "%") + " OR sCSNoxxxx LIKE "
                + SQLUtil.toSQL(fsValue + "%") + ") AND " + lsCondition);
        }

        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Vehicle Serial»Plate No.»CS No.»Description",
                "sSerialID»sPlateNox»sCSNoxxxx»sDescript",
                "sSerialID»sPlateNox»sCSNoxxxx»sDescript",
                fbByCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sSerialID"));
        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
            return poJSON;
        }
    }

    @Override
    public Model_Vehicle_Serial_Master getModel() {
        return poModel;
    }
    
    public JSONObject loadClientVehicleInfo(String fsValue){
        String lsSQL = MiscUtil.addCondition(poModel.getSQL(), "h.sClientID = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                paModel = new ArrayList<>();
                while(loRS.next()){
                        paModel.add(new Model_Vehicle_Serial_Master(poGRider));
                        paModel.get(paModel.size() - 1).openRecord(loRS.getString("sSerialID"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                System.out.println("lnctr = " + lnctr);
            }else{
                paModel = new ArrayList<>();
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", "No record selected.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }
    
    public ArrayList<Model_Vehicle_Serial_Master> getVehicleSerialList(){return paModel;}
    public void setVehicleSerialList(ArrayList<Model_Vehicle_Serial_Master> foObj){this.paModel = foObj;}
    
    public void setVehicleSerial(int fnRow, int fnIndex, Object foValue){ paModel.get(fnRow).setValue(fnIndex, foValue);}
    public void setVehicleSerial(int fnRow, String fsIndex, Object foValue){ paModel.get(fnRow).setValue(fsIndex, foValue);}
    public Object getVehicleSerial(int fnRow, int fnIndex){return paModel.get(fnRow).getValue(fnIndex);}
    public Object getVehicleSerial(int fnRow, String fsIndex){return paModel.get(fnRow).getValue(fsIndex);}
    
    /**
     * Loads the list of vehicles from the database base of client ID.
     * @param fsValue Identify as the client ID
     * @param fbisOwner Identify who will be retrieve
     * @return {@code true} if the list is successfully loaded, {@code false} otherwise.
     * @throws SQLException if a database error occurs.
    */
    public JSONObject LoadVehicleList(String fsValue, boolean fbisOwner) {
        poJSON = new JSONObject();
        if (poGRider == null){
            poJSON.put("result", "error");
            poJSON.put("message", "Application driver is not set.");
            return poJSON;
        }
        
        String lsSQL = poModel.getSQL() ;
        if(fbisOwner){
            lsSQL =  MiscUtil.addCondition(lsSQL, "a.sClientID = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL =  MiscUtil.addCondition(lsSQL, "a.sCoCltIDx = " + SQLUtil.toSQL(fsValue));
        }
        
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                paModel = new ArrayList<>();
                while(loRS.next()){
                        paModel.add(new Model_Vehicle_Serial_Master(poGRider));
                        paModel.get(paModel.size() - 1).openRecord(loRS.getString("sSerialID"));
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                System.out.println("lnctr = " + lnctr);
                
            }else{
                paModel = new ArrayList<>();
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", "No record selected.");
            }
            
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }

}


