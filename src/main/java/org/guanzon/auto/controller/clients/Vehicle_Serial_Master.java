/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.clients;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    boolean pbWtParent;
    int pnEditMode;
    String psBranchCd;
    String psRecdStat;
    
    ArrayList<Model_Vehicle_Serial_Master> paModel;
    Model_Vehicle_Serial_Master poModel;
    
    JSONObject poJSON;

    public Vehicle_Serial_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;
        
        poModel = new Model_Vehicle_Serial_Master(foGRider);
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
            org.json.simple.JSONObject obj;

            poModel = new Model_Vehicle_Serial_Master(poGRider);
            
            Connection loConn = null;
            loConn = setConnection();
            poModel.setSerialID(MiscUtil.getNextCode(poModel.getTable(), "sSerialID", true, loConn, poGRider.getBranchCode()+"VS"));
            poModel.newRecord();
            
            if (poModel == null){
                poJSON.put("result", "error");
                poJSON.put("message", "initialized new record failed.");
                return poJSON;
            }else{
                poJSON.put("result", "success");
                poJSON.put("message", "initialized new record.");
                pnEditMode = EditMode.ADDNEW;
            }
               
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        
        return poJSON;
    }

    @Override
    public JSONObject openRecord(String fsValue) {
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        
        poModel = new Model_Vehicle_Serial_Master(poGRider);
        poJSON = poModel.openRecord(fsValue);
        return poJSON;
    }

    @Override
    public JSONObject updateRecord() {
        poJSON = new JSONObject();
        if (pnEditMode != EditMode.READY && pnEditMode != EditMode.UPDATE){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid edit mode.");
            return poJSON;
        }
        pnEditMode = EditMode.UPDATE;
        poJSON.put("result", "success");
        poJSON.put("message", "Update mode success.");
        return poJSON;
    }

    @Override
    public JSONObject saveRecord() {
        poJSON = validateEntry();
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON = poModel.saveRecord();

        if ("success".equals((String) poJSON.get("result"))) {
            if (!pbWtParent) {poGRider.commitTrans();}
        } else {
            if (!pbWtParent) {poGRider.rollbackTrans();}
        }
        
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
    public JSONObject searchRecord(String fsValue, boolean fbByCode) {
        String lsSQL = "";
        
        if(fbByCode){
            lsSQL = MiscUtil.addCondition(poModel.getSQL(), " a.sSerialID = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(poModel.getSQL(), "( b.sPlateNox LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                                            + " OR a.sCSNoxxxx LIKE " + SQLUtil.toSQL(fsValue + "%") + ") ");
        }

        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Serial ID»Plate No.»CS No.»Engine No»Frame No»Vehicle Status",
                "sSerialID»sPlateNox»sCSNoxxxx»sEngineNo»sFrameNox»sVhclStat",
                "a.sSerialID»b.sPlateNox»a.sCSNoxxxx»a.sEngineNo»a.sFrameNox»a.cSoldStat",
                0);

        if (poJSON != null) {
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
            return poJSON;
        }
        
        return poJSON;
    }

    @Override
    public Model_Vehicle_Serial_Master getModel() {
        return poModel;
    }
    
    private Connection setConnection(){
        Connection foConn;
        
        if (pbWtParent){
            foConn = (Connection) poGRider.getConnection();
            if (foConn == null) foConn = (Connection) poGRider.doConnect();
        }else foConn = (Connection) poGRider.doConnect();
        
        return foConn;
    }
    
    
    private JSONObject validateEntry(){
        JSONObject jObj = new JSONObject();
        try {
            if(poModel.getVhclID() == null){
                jObj.put("result", "error");
                jObj.put("message", "Vehicle ID cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getVhclID().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Vehicle ID cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getMakeID() == null){
                jObj.put("result", "error");
                jObj.put("message", "Make cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getMakeID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Make cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getModelID() == null){
                jObj.put("result", "error");
                jObj.put("message", "Model cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getModelID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Model cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getColorID() == null){
                jObj.put("result", "error");
                jObj.put("message", "Color cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getColorID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Color cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getTypeID() == null){
                jObj.put("result", "error");
                jObj.put("message", "Type cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getTypeID().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Type cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getTransMsn() == null){
                jObj.put("result", "error");
                jObj.put("message", "Transmission cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getTransMsn().trim().isEmpty()){
                    jObj.put("result", "error");
                    jObj.put("message", "Transmission cannot be Empty.");
                    return jObj;
                }
            }
            
            if(poModel.getYearModl() == null || poModel.getYearModl() == 0){
                jObj.put("result", "error");
                jObj.put("message", "Year cannot be Empty.");
                return jObj;
            }
            
            if(poModel.getEngineNo() == null){
                jObj.put("result", "error");
                jObj.put("message", "Engine No cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getEngineNo().trim().isEmpty() || poModel.getEngineNo().replace(" ", "").length() < 3 ){
                    jObj.put("result", "error");
                    jObj.put("message", "Invalid Engine Number.");
                    return jObj;
                }
            }
            
            if(poModel.getFrameNo() == null){
                jObj.put("result", "error");
                jObj.put("message", "Frame No cannot be Empty.");
                return jObj;
            } else {
                if(poModel.getFrameNo().trim().isEmpty() || poModel.getFrameNo().replace(" ","").length() < 6 ){
                    jObj.put("result", "error");
                    jObj.put("message", "Frame Engine Number.");
                    return jObj;
                }
            }
            
            String lsID = "";
            String lsDesc  = "";
            String lsSQL =    "  SELECT "                 
                            + "  sSerialID "             
                            + ", sCSNoxxxx "           
                            + " FROM vehicle_serial " ;  

            lsSQL = MiscUtil.addCondition(lsSQL, " sCSNoxxxx = " + SQLUtil.toSQL(poModel.getCSNo()) 
                                                    + " AND sSerialID <> " + SQLUtil.toSQL(poModel.getSerialID()) 
                                                    );
            System.out.println("EXISTING VEHICLE SERIAL CS NO CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sSerialID");
                        lsDesc = loRS.getString("sCSNoxxxx");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Vehicle Serial CS No Record.\n\nSerial ID: " + lsID + "\nCS No: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            lsID = "";
            lsDesc  = "";
            lsSQL =    "  SELECT "                 
                    + "  sSerialID "             
                    + ", sPlateNox "           
                    + " FROM vehicle_serial_registration " ;  

            lsSQL = MiscUtil.addCondition(lsSQL, " sPlateNox = " + SQLUtil.toSQL(poModel.getPlateNo()) 
                                                    + " AND sSerialID <> " + SQLUtil.toSQL(poModel.getSerialID()) 
                                                    );
            System.out.println("EXISTING VEHICLE SERIAL PLATE NO CHECK: " + lsSQL);
            loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sSerialID");
                        lsDesc = loRS.getString("sPlateNox");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Vehicle Serial Plate No Record.\n\nSerial ID: " + lsID + "\nPlate No: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            lsID = "";
            lsDesc  = "";
            lsSQL =    "  SELECT "                 
                    + "  sSerialID "             
                    + ", sEngineNo "           
                    + " FROM vehicle_serial " ;  

            lsSQL = MiscUtil.addCondition(lsSQL, " sEngineNo = " + SQLUtil.toSQL(poModel.getEngineNo()) 
                                                    + " AND sSerialID <> " + SQLUtil.toSQL(poModel.getSerialID()) 
                                                    );
            System.out.println("EXISTING VEHICLE SERIAL ENGINE NO CHECK: " + lsSQL);
            loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sSerialID");
                        lsDesc = loRS.getString("sEngineNo");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Vehicle Serial Engine No Record.\n\nSerial ID: " + lsID + "\nEngine No: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
            lsID = "";
            lsDesc  = "";
            lsSQL =    "  SELECT "                 
                    + "  sSerialID "             
                    + ", sFrameNox "           
                    + " FROM vehicle_serial " ;  

            lsSQL = MiscUtil.addCondition(lsSQL, " sFrameNox = " + SQLUtil.toSQL(poModel.getEngineNo()) 
                                                    + " AND sSerialID <> " + SQLUtil.toSQL(poModel.getSerialID()) 
                                                    );
            System.out.println("EXISTING VEHICLE SERIAL FRAME NO CHECK: " + lsSQL);
            loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsID = loRS.getString("sSerialID");
                        lsDesc = loRS.getString("sFrameNox");
                    }
                    
                    MiscUtil.close(loRS);
                    
                    jObj.put("result", "error");
                    jObj.put("message", "Existing Vehicle Serial Frame No Record.\n\nSerial ID: " + lsID + "\nFrame No: " + lsDesc.toUpperCase() );
                    return jObj;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle_Serial_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        jObj.put("result", "success");
        jObj.put("message", "Valid Entry");
        return jObj;
    }
    
    public JSONObject searchMake(String fsValue) {
        JSONObject jObj;
         
        String lsSQL = poModel.getVhclDescSQL();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " c.sMakeDesc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.cRecdStat = '1' ");

        System.out.println("SEARCH MAKE: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sMakeIDxx»sMakeDesc",
                "a.sMakeIDxx»c.sMakeDesc",
                1);

        if (jObj != null) {
            if("error".equals(jObj.get("result"))){
                poModel.setMakeID("");
                poModel.setMakeDesc("");
                poModel.setModelID("");
                poModel.setModelDsc("");
                poModel.setTypeID("");
                poModel.setTypeDesc("");
                poModel.setColorID("");
                poModel.setColorDsc("");
                poModel.setTransMsn("");
                poModel.setYearModl(0);
                poModel.setVhclID("");
            }
            
        } else {
            poModel.setMakeID("");
            poModel.setMakeDesc("");
            poModel.setModelID("");
            poModel.setModelDsc("");
            poModel.setTypeID("");
            poModel.setTypeDesc("");
            poModel.setColorID("");
            poModel.setColorDsc("");
            poModel.setTransMsn("");
            poModel.setYearModl(0);
            poModel.setVhclID("");
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
    public JSONObject searchModel(String fsValue) {
        JSONObject jObj = new JSONObject();
            
        if(poModel.getMakeID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Make cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getMakeID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Make cannot be Empty.");
                return jObj;
            }
        }
         
        String lsSQL = poModel.getVhclDescSQL();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " b.sModelDsc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.cRecdStat = '1' ");

        System.out.println("SEARCH MODEL: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sModelIDx»sModelDsc",
                "a.sModelIDx»b.sModelDsc",
                1);

        if (jObj != null) {
            if("error".equals(jObj.get("result"))){
                poModel.setModelID("");
                poModel.setModelDsc("");
                poModel.setTypeID("");
                poModel.setTypeDesc("");
                poModel.setColorID("");
                poModel.setColorDsc("");
                poModel.setTransMsn("");
                poModel.setYearModl(0);
                poModel.setVhclID("");
            }
        } else {
            poModel.setModelID("");
            poModel.setModelDsc("");
            poModel.setTypeID("");
            poModel.setTypeDesc("");
            poModel.setColorID("");
            poModel.setColorDsc("");
            poModel.setTransMsn("");
            poModel.setYearModl(0);
            poModel.setVhclID("");
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
    public JSONObject searchType(String fsValue) {
        JSONObject jObj = new JSONObject();
        
        if(poModel.getMakeID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Make cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getMakeID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Make cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getModelID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Model cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getModelID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Model cannot be Empty.");
                return jObj;
            }
        }
         
        String lsSQL = poModel.getVhclDescSQL();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " e.sTypeDesc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) 
                                            + " AND a.cRecdStat = '1' ");

        System.out.println("SEARCH TYPE: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sTypeIDxx»sTypeDesc",
                "a.sTypeIDxx»e.sTypeDesc",
                1);

        if (jObj != null) {
            if("error".equals(jObj.get("result"))){
                poModel.setTypeID("");
                poModel.setTypeDesc("");
                poModel.setColorID("");
                poModel.setColorDsc("");
                poModel.setTransMsn("");
                poModel.setYearModl(0);
                poModel.setVhclID("");
            }
        } else {
            poModel.setTypeID("");
            poModel.setTypeDesc("");
            poModel.setColorID("");
            poModel.setColorDsc("");
            poModel.setTransMsn("");
            poModel.setYearModl(0);
            poModel.setVhclID("");
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
    public JSONObject searchTransMsn(String fsValue) {
        JSONObject jObj = new JSONObject();
        
        if(poModel.getMakeID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Make cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getMakeID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Make cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getModelID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Model cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getModelID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Model cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getColorID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Color cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getColorID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Color cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getTypeID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Type cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getTypeID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Type cannot be Empty.");
                return jObj;
            }
        }
         
        String lsSQL = poModel.getVhclDescSQL();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransMsn LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) 
                                            + " AND a.sTypeIDxx = " + SQLUtil.toSQL(poModel.getTypeID()) 
                                            + " AND a.cRecdStat = '1' ");

        System.out.println("SEARCH TRANSMISSION: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Transmission",
                "sTransMsn",
                "a.sTransMsn",
                1);

        if (jObj != null) {
            if("error".equals(jObj.get("result"))){
                poModel.setTransMsn("");
                poModel.setColorID("");
                poModel.setColorDsc("");
                poModel.setYearModl(0);
                poModel.setVhclID("");
            }
        } else {
            poModel.setTransMsn("");
            poModel.setColorID("");
            poModel.setColorDsc("");
            poModel.setYearModl(0);
            poModel.setVhclID("");
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
    public JSONObject searchColor(String fsValue) {
        JSONObject jObj = new JSONObject();
        
        if(poModel.getMakeID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Make cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getMakeID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Make cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getModelID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Model cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getModelID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Model cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getColorID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Color cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getColorID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Color cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getTypeID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Type cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getTypeID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Type cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getTransMsn() == null){
            jObj.put("result", "error");
            jObj.put("message", "Transmission cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getTransMsn().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Transmission cannot be Empty.");
                return jObj;
            }
        }

         
        String lsSQL = poModel.getVhclDescSQL();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " d.sColorDsc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) 
                                            + " AND a.sTypeIDxx = " + SQLUtil.toSQL(poModel.getTypeID()) 
                                            + " AND a.sTransMsn = " + SQLUtil.toSQL(poModel.getTransMsn()) 
                                            + " AND a.cRecdStat = '1' ");

        System.out.println("SEARCH TYPE: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sColorIDx»sColorDsc",
                "a.sColorIDx»d.sColorDsc",
                1);

        if (jObj != null) {
            if("error".equals(jObj.get("result"))){
                poModel.setColorID("");
                poModel.setColorDsc("");
                poModel.setYearModl(0);
                poModel.setVhclID("");
            } 
        } else {
            poModel.setColorID("");
            poModel.setColorDsc("");
            poModel.setYearModl(0);
            poModel.setVhclID("");
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
    public JSONObject searchYearModel(String fsValue) {
        JSONObject jObj = new JSONObject();
        
        if(poModel.getMakeID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Make cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getMakeID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Make cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getModelID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Model cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getModelID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Model cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getTypeID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Type cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getTypeID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Type cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getTransMsn() == null){
            jObj.put("result", "error");
            jObj.put("message", "Transmission cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getTransMsn().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Transmission cannot be Empty.");
                return jObj;
            }
        }

        if(poModel.getColorID() == null){
            jObj.put("result", "error");
            jObj.put("message", "Color cannot be Empty.");
            return jObj;
        } else {
            if(poModel.getColorID().trim().isEmpty()){
                jObj.put("result", "error");
                jObj.put("message", "Color cannot be Empty.");
                return jObj;
            }
        }
         
        String lsSQL = poModel.getVhclDescSQL();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " a.nYearModl LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) 
                                            + " AND a.sTypeIDxx = " + SQLUtil.toSQL(poModel.getTypeID()) 
                                            + " AND a.sTransMsn = " + SQLUtil.toSQL(poModel.getTransMsn()) 
                                            + " AND a.sColorIDx = " + SQLUtil.toSQL(poModel.getColorID()) 
                                            + " AND a.cRecdStat = '1' ");

        System.out.println("SEARCH YEAR MODEL: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Year Model",
                "nYearModl",
                "a.nYearModl",
                1);

        if (jObj != null) {
            if("error".equals(jObj.get("result"))){
                poModel.setYearModl(0);
                poModel.setVhclID("");
            } 
        } else {
            poModel.setYearModl(0);
            poModel.setVhclID("");
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
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


