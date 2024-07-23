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
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.general.SearchDialog;
import org.guanzon.auto.model.clients.Model_Vehicle_Serial_Master;
import org.guanzon.auto.validator.clients.ValidatorFactory;
import org.guanzon.auto.validator.clients.ValidatorInterface;
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
    Vehicle_Registration poVhclReg;
    Client_Master poClient;
    
    JSONObject poJSON;

    public Vehicle_Serial_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;
        
        poModel = new Model_Vehicle_Serial_Master(foGRider);
        poClient = new Client_Master(foGRider,fbWthParent,fsBranchCd);
        poVhclReg = new Vehicle_Registration(foGRider,fbWthParent,fsBranchCd);
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
            poModel = new Model_Vehicle_Serial_Master(poGRider);
            
            Connection loConn = null;
            loConn = setConnection();
            poModel.setSerialID(MiscUtil.getNextCode(poModel.getTable(), "sSerialID", true, loConn, poGRider.getBranchCode()+"VS"));
            poModel.setBranchCD(poGRider.getBranchCode());
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
            
            poJSON = poVhclReg.newRecord();        

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
        
        poVhclReg.openRecord(fsValue);
        
        return poJSON;
    }

    @Override
    public JSONObject updateRecord() {
        poJSON = new JSONObject();
        if(poVhclReg.getEditMode() == EditMode.UNKNOWN){
            poJSON = poVhclReg.newRecord();
            
            if (poVhclReg == null){
                poJSON.put("result", "error");
                poJSON.put("message", "Vehicle Registration: Initialized new record failed.");
                return poJSON;
            }else{
                if("error".equals((String) poJSON.get("result"))){
                    return poJSON;
                }
            }
        }
        
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
        
        ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Vehicle_Serial, poModel);
        validator.setGRider(poGRider);
        if (!validator.isEntryOkay()){
            poJSON.put("result", "error");
            poJSON.put("message", validator.getMessage());
            return poJSON;
        }
        
//        poJSON = validateEngineFrame();
//        if ("error".equals((String) poJSON.get("result"))) {
//            return poJSON;
//        }
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON = poModel.saveRecord();
        if ("success".equals((String) poJSON.get("result"))) {
            String lsMsg = (String) poJSON.get("message");
            
            //set values to vehicle registration table
            poVhclReg.getModel().setSerialID(poModel.getSerialID());
            poVhclReg.getModel().setPlateNo(poModel.getPlateNo());
            poVhclReg.getModel().setPlaceReg(poModel.getPlaceReg());
            poVhclReg.setMaster("dRegister",getMaster("dRegister")); //poModel.getRegisterDte()
            poJSON = poVhclReg.saveRecord();
            if("no record to save.".equals((String) poJSON.get("message"))){
                poJSON.put("result", "success");
                poJSON.put("message", lsMsg);
            } else {
                if("No updates has been made.".equals(poJSON.get("message"))){
                    poJSON.put("message", lsMsg);
                } else {
                    poJSON.put("message", (String) poJSON.get("message"));
                }
            }
            
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
        return searchRecord (fsValue, fbByCode, true);
    }
    
    public JSONObject searchRecord(String fsValue, boolean fbByCode, boolean fbIsVhclSales) {
        String lsHeader = "Vehicle Serial ID»CS No.»Plate No.»Owner Name»Engine No»Frame No»Vehicle Description»Vehicle Status";
        String lsColName = "sSerialID»sCSNoxxxx»sPlateNox»sOwnerNmx»sEngineNo»sFrameNox»sDescript»sVhclStat"; 
        String lsColCrit = "a.sSerialID»a.sCSNoxxxx»b.sPlateNox»h.sCompnyNm»a.sEngineNo»a.sFrameNox»c.sDescript»a.cSoldStat";
        
        String lsSQL = "";
        
        if(fbByCode){
            lsSQL = MiscUtil.addCondition(poModel.getSQL(), " a.sSerialID = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(poModel.getSQL(), "( b.sPlateNox LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                                            + " OR a.sCSNoxxxx LIKE " + SQLUtil.toSQL(fsValue + "%") + ") ");
        }
        
        if(!fbIsVhclSales){
            lsSQL =  lsSQL + " AND (a.sClientID != NULL OR TRIM(a.sClientID) <> '' )";
        }
        System.out.println(lsSQL);
//        poJSON = ShowDialogFX.Search(poGRider,
//                lsSQL,
//                fsValue,
//                lsHeader,
//                lsColName,
//                lsColCrit,
//                0);
        
        poJSON = SearchDialog.jsonSearch(
                poGRider,
                lsSQL,
                    fsValue,
                lsHeader,//"Client ID»Customer Name", //»Address
                lsColName, //"sClientID»sCompnyNm", //»CONCAT(bb.sHouseNox, ' ', bb.sAddressx, ', ', c.sTownName, ' ', d.sProvName)
                "0.2D»0.2D»0.2D»0.3D»0.3D»0.3D»0.4D»0.4D", 
                "VEHICLE INFORMATION",
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
    
    
//    private JSONObject validateEngineFrame(){
//        JSONObject jObj = new JSONObject();
//        
//        jObj = checkEngineNo();
//        if("error".equals((String) jObj.get("result"))){
//            return jObj;
//        }
//
//        jObj = checkMakeFrameNo();
//        if("error".equals((String) jObj.get("result"))){
//            return jObj;
//        }
//
//        jObj = checkModelFrameNo();
//        if("error".equals((String) jObj.get("result"))){
//            return jObj;
//        }
//            
//        jObj.put("result", "success");
//        jObj.put("message", "Valid Entry");
//        return jObj;
//    }
    
    public JSONObject searchMake(String fsValue) {
        JSONObject jObj = new JSONObject();
        String lsOrigVal = poModel.getMakeID();
        String lsNewVal = "";
        
        String lsSQL = poModel.getSQLVhclDesc();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " c.sMakeDesc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.cRecdStat = '1' ")
                                            + " GROUP BY a.sMakeIDxx ";

        System.out.println("SEARCH MAKE: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sMakeIDxx»sMakeDesc",
                "a.sMakeIDxx»c.sMakeDesc",
                1);
        
        if (jObj != null) {
            if(!"error".equals(jObj.get("result"))){
                lsNewVal = (String) jObj.get("sMakeIDxx");
                poModel.setMakeID(lsNewVal);
                poModel.setMakeDesc((String) jObj.get("sMakeDesc"));
            } else {
                poModel.setMakeID("");
                poModel.setMakeDesc("");
            }
            
            if("error".equals(jObj.get("result")) || !lsNewVal.equals(lsOrigVal)){
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
            jObj = new JSONObject();
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
    /**
     * For searching vehicle model when key is pressed.
     * @param fsValue the search value for the vehicle model.
     * @return {@code true} if a matching vehicle model is found, {@code false} otherwise.
    */
    public JSONObject searchModel(String fsValue) {
        JSONObject jObj = new JSONObject();
        String lsOrigVal = poModel.getMakeID();
        String lsNewVal = "";
            
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
         
        String lsSQL = poModel.getSQLVhclDesc();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " b.sModelDsc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.cRecdStat = '1' ")
                                            + " GROUP BY a.sModelIDx ";

        System.out.println("SEARCH MODEL: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sModelIDx»sModelDsc",
                "a.sModelIDx»b.sModelDsc",
                1);

        if (jObj != null) {
            if(!"error".equals(jObj.get("result"))){
                lsNewVal = (String) jObj.get("sModelIDx");
                poModel.setModelID(lsNewVal);
                poModel.setModelDsc((String) jObj.get("sModelDsc"));
            } else {
                poModel.setModelID("");
                poModel.setModelDsc("");
            }
            
            if("error".equals(jObj.get("result")) || !lsNewVal.equals(lsOrigVal)){
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
            jObj = new JSONObject();
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
    public JSONObject searchType(String fsValue) {
        JSONObject jObj = new JSONObject();
        String lsOrigVal = poModel.getTypeID();
        String lsNewVal = "";
        
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
         
        String lsSQL = poModel.getSQLVhclDesc();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " e.sTypeDesc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) 
                                            + " AND a.cRecdStat = '1' ")
                                            + " GROUP BY a.sTypeIDxx ";

        System.out.println("SEARCH TYPE: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sTypeIDxx»sTypeDesc",
                "a.sTypeIDxx»e.sTypeDesc",
                1);

        if (jObj != null) {
            if(!"error".equals(jObj.get("result"))){
                lsNewVal = (String) jObj.get("sTypeIDxx");
                poModel.setTypeID(lsNewVal);
                poModel.setTypeDesc((String) jObj.get("sTypeDesc"));
            } else {
                poModel.setTypeID("");
                poModel.setTypeDesc("");
            }
            
            if("error".equals(jObj.get("result")) || !lsNewVal.equals(lsOrigVal)){
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
            jObj = new JSONObject();
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
    public JSONObject searchTransMsn(String fsValue) {
        JSONObject jObj = new JSONObject();
        String lsOrigVal = poModel.getTransMsn();
        String lsNewVal = "";
        
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
         
        String lsSQL = poModel.getSQLVhclDesc();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransMsn LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) 
                                            + " AND a.sTypeIDxx = " + SQLUtil.toSQL(poModel.getTypeID()) 
                                            + " AND a.cRecdStat = '1' ")
                                            + " GROUP BY a.sTransMsn ";

        System.out.println("SEARCH TRANSMISSION: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Transmission",
                "sTransMsn",
                "a.sTransMsn",
                0);

        if (jObj != null) {
            if(!"error".equals(jObj.get("result"))){
                lsNewVal = (String) jObj.get("sTransMsn");
                poModel.setTransMsn(lsNewVal);
            } else {
                poModel.setTransMsn("");
            }
            
            if("error".equals(jObj.get("result")) || !lsNewVal.equals(lsOrigVal)){
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
            jObj = new JSONObject();
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
    public JSONObject searchColor(String fsValue) {
        JSONObject jObj = new JSONObject();
        String lsOrigVal = poModel.getColorID();
        String lsNewVal = "";
        
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

         
        String lsSQL = poModel.getSQLVhclDesc();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " d.sColorDsc LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) 
                                            + " AND a.sTypeIDxx = " + SQLUtil.toSQL(poModel.getTypeID()) 
                                            + " AND a.sTransMsn = " + SQLUtil.toSQL(poModel.getTransMsn()) 
                                            + " AND a.cRecdStat = '1' ")
                                            + " GROUP BY a.sColorIDx ";

        System.out.println("SEARCH COLOR: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Description",
                "sColorIDx»sColorDsc",
                "a.sColorIDx»d.sColorDsc",
                1);

        if (jObj != null) {
            if(!"error".equals(jObj.get("result"))){
                lsNewVal = (String) jObj.get("sColorIDx");
                poModel.setColorID(lsNewVal);
                poModel.setColorDsc((String) jObj.get("sColorDsc"));
            } else {
                poModel.setColorID("");
                poModel.setColorDsc("");
            }
            
            if("error".equals(jObj.get("result")) || !lsNewVal.equals(lsOrigVal)){
                poModel.setYearModl(0);
                poModel.setVhclID("");
            } 
            
        } else {
            poModel.setColorID("");
            poModel.setColorDsc("");
            poModel.setYearModl(0);
            poModel.setVhclID("");
            jObj = new JSONObject();
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
         
        String lsSQL = poModel.getSQLVhclDesc();
        
        lsSQL = MiscUtil.addCondition(lsSQL, " a.nYearModl LIKE " + SQLUtil.toSQL(fsValue + "%") 
                                            + " AND a.sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
                                            + " AND a.sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) 
                                            + " AND a.sTypeIDxx = " + SQLUtil.toSQL(poModel.getTypeID()) 
                                            + " AND a.sTransMsn = " + SQLUtil.toSQL(poModel.getTransMsn()) 
                                            + " AND a.sColorIDx = " + SQLUtil.toSQL(poModel.getColorID()) 
                                            + " AND a.cRecdStat = '1' ")
                                            + " GROUP BY a.nYearModl ";

        System.out.println("SEARCH YEAR MODEL: " + lsSQL);
        jObj = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Year Model",
                "nYearModl",
                "a.nYearModl",
                0);

        if (jObj != null) {
            if(!"error".equals(jObj.get("result"))){
                poModel.setYearModl(Integer.parseInt(String.valueOf(jObj.get("nYearModl"))));
                poModel.setVhclID((String) jObj.get("sVhclIDxx"));
            } else {
                poModel.setYearModl(0);
                poModel.setVhclID("");
            }
        } else {
            poModel.setYearModl(0);
            poModel.setVhclID("");
            jObj = new JSONObject();
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
//    public JSONObject checkEngineNo(){
//        JSONObject jObj = new JSONObject();
//        if(poModel.getMakeID() == null){
//            jObj.put("result", "error");
//            jObj.put("message", "Make cannot be Empty.");
//            return jObj;
//        } else {
//            if(poModel.getMakeID().trim().isEmpty()){
//                jObj.put("result", "error");
//                jObj.put("message", "Make cannot be Empty.");
//                return jObj;
//            }
//        }
//
//        if(poModel.getModelID() == null){
//            jObj.put("result", "error");
//            jObj.put("message", "Model cannot be Empty.");
//            return jObj;
//        } else {
//            if(poModel.getModelID().trim().isEmpty()){
//                jObj.put("result", "error");
//                jObj.put("message", "Model cannot be Empty.");
//                return jObj;
//            }
//        }
//        
//        if(poModel.getEngineNo().trim().isEmpty() || poModel.getEngineNo().replace(" ", "").length() < 3 ){
//                jObj.put("result", "error");
//                jObj.put("message", "Invalid Engine Number.");
//                return jObj;
//        }
//        
//        int lnLength = 0;
//        String lsSQL =    "  SELECT "                          
//                        + "  sModelIDx "                       
//                        + ", nEntryNox "                       
//                        + ", sEngnPtrn "                       
//                        + ", nEngnLenx "                           
//                        + "FROM vehicle_model_engine_pattern ";
//  
//        
//        String lsEngNo = poModel.getEngineNo().substring(0, 3);
//        lsSQL = MiscUtil.addCondition(lsSQL, " sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) 
//                                                + " AND sEngnPtrn LIKE " + SQLUtil.toSQL(lsEngNo) 
//                                                );
//        System.out.println("ENGINE NO CHECK: " + lsSQL);
//        ResultSet loRS = poGRider.executeQuery(lsSQL);
//
//        if (MiscUtil.RecordCount(loRS) > 0){
//            try {
//                while(loRS.next()){
//                    lnLength = loRS.getInt("nEngnLenx");
//                }
//
//                MiscUtil.close(loRS);
//                if(lnLength != poModel.getEngineNo().length()) {
//                    jObj.put("result", "error");
//                    jObj.put("message", "Engine Number Length does not equal to Model Engine Pattern Length." );
//                    return jObj;
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(Vehicle_Serial_Master.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
//            jObj.put("result", "error");
//            jObj.put("message", "Engine Number does not exist in Model Engine Pattern." );
//            return jObj;
//        }
//        
//        return jObj;
//    }
    
//    public JSONObject checkMakeFrameNo(){
//        JSONObject jObj = new JSONObject();
//        if(poModel.getMakeID() == null){
//            jObj.put("result", "error");
//            jObj.put("message", "Make cannot be Empty.");
//            return jObj;
//        } else {
//            if(poModel.getMakeID().trim().isEmpty()){
//                jObj.put("result", "error");
//                jObj.put("message", "Make cannot be Empty.");
//                return jObj;
//            }
//        }
//        
//        if(poModel.getFrameNo().trim().isEmpty() || poModel.getFrameNo().replace(" ", "").length() < 6 ){
//                jObj.put("result", "error");
//                jObj.put("message", "Invalid Frame Number.");
//                return jObj;
//        }
//        
//        String lsFrameNo = poModel.getFrameNo().substring(0, 3);
//        String lsSQL =    "  SELECT "                        
//                        + "  sMakeIDxx "                     
//                        + ", nEntryNox "                     
//                        + ", sFrmePtrn "                       
//                        + "FROM vehicle_make_frame_pattern ";
//
//        lsSQL = MiscUtil.addCondition(lsSQL, " sMakeIDxx = " + SQLUtil.toSQL(poModel.getMakeID()) 
//                                                + " AND sFrmePtrn = " + SQLUtil.toSQL(lsFrameNo) 
//                                                );
//        System.out.println("MAKE FRAME CHECK: " + lsSQL);
//        ResultSet loRS = poGRider.executeQuery(lsSQL);
//
//        if (MiscUtil.RecordCount(loRS) == 0){
//            jObj.put("result", "error");
//            jObj.put("message", "Frame Number does not exist in Make Frame Pattern." );
//            return jObj;
//        }
//        
//        return jObj;
//    }
    
//    public JSONObject checkModelFrameNo(){
//        JSONObject jObj = new JSONObject();
//        if(poModel.getMakeID() == null){
//            jObj.put("result", "error");
//            jObj.put("message", "Make cannot be Empty.");
//            return jObj;
//        } else {
//            if(poModel.getMakeID().trim().isEmpty()){
//                jObj.put("result", "error");
//                jObj.put("message", "Make cannot be Empty.");
//                return jObj;
//            }
//        }
//
//        if(poModel.getModelID() == null){
//            jObj.put("result", "error");
//            jObj.put("message", "Model cannot be Empty.");
//            return jObj;
//        } else {
//            if(poModel.getModelID().trim().isEmpty()){
//                jObj.put("result", "error");
//                jObj.put("message", "Model cannot be Empty.");
//                return jObj;
//            }
//        }
//        
//        if(poModel.getFrameNo().trim().isEmpty() || poModel.getFrameNo().replace(" ", "").length() < 6 ){
//                jObj.put("result", "error");
//                jObj.put("message", "Invalid Frame Number.");
//                return jObj;
//        }
//        
//        int lnLength = 0;
//        String lsSQL =    "  SELECT "                        
//                        + "  sModelIDx "                     
//                        + ", nEntryNox "                     
//                        + ", sFrmePtrn "                    
//                        + ", nFrmeLenx "                         
//                        + "FROM vehicle_model_frame_pattern ";
//
//        String lsFrameNo = poModel.getFrameNo().substring(3, 6);
//        lsSQL = MiscUtil.addCondition(lsSQL, " sModelIDx = " + SQLUtil.toSQL(poModel.getModelID()) 
//                                                + " AND sFrmePtrn = " + SQLUtil.toSQL(lsFrameNo) 
//                                                );
//        System.out.println("MODEL FRAME CHECK: " + lsSQL);
//        ResultSet loRS = poGRider.executeQuery(lsSQL);
//        
//        if (MiscUtil.RecordCount(loRS) > 0){
//            try {
//                while(loRS.next()){
//                    lnLength = loRS.getInt("nFrmeLenx");
//                }
//
//                MiscUtil.close(loRS);
//                if(lnLength != poModel.getFrameNo().length()) {
//                    jObj.put("result", "error");
//                    jObj.put("message", "Frame Number Length does not equal to Model Frame Pattern Length." );
//                    return jObj;
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(Vehicle_Serial_Master.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
//            jObj.put("result", "error");
//            jObj.put("message", "Frame Number does not exist in Model Frame Pattern." );
//            return jObj;
//        }
//        
//        return jObj;
//    }
    
    /**
     * Search Ownership / Co - Ownership
     * @param fsValue Owner / Co - Owner Name
     * @param isOwner set TRUE if searching for OWNER, Otherwise set FALSE when searching for CO-OWNER
     * @param isTransfer
     * @return 
     */
    public JSONObject searchOwner(String fsValue, boolean isOwner, boolean isTransfer){
        JSONObject loJSON = new JSONObject();
        
        loJSON = poClient.searchRecord(fsValue, false);
        if (loJSON != null && !"error".equals((String) loJSON.get("result"))) {
            if(isOwner){
                if(isTransfer){
                    if(poModel.getClientID().equals((String) loJSON.get("sClientID"))){
                        loJSON.put("result", "error");
                        loJSON.put("message", "Selected new owner is the same with current owner.");
                    }
                    
                    if(poModel.getCoCltID().equals((String) loJSON.get("sClientID"))){
                        loJSON.put("result", "error");
                        loJSON.put("message", "Selected new owner cannot be the same with current co - owner.");
                    }
                }
                
                if(poModel.getCoCltID().equals((String) loJSON.get("sClientID"))){
                    loJSON = new JSONObject();
                    loJSON.put("result", "error");
                    loJSON.put("message", "Selected Owner cannot be the same with current co-owner.");
                }
                
                poModel.setClientID((String) loJSON.get("sClientID"));
                poModel.setOwnerNmx((String) loJSON.get("sCompnyNm"));
                poModel.setOwnerAdd((String) loJSON.get("xAddressx"));
            } else {
                
                if(poModel.getClientID().equals((String) loJSON.get("sClientID"))){
                    loJSON = new JSONObject();
                    loJSON.put("result", "error");
                    loJSON.put("message", "Selected co - owner cannot be the same with current owner.");
                }
                
                poModel.setCoCltID((String) loJSON.get("sClientID"));
                poModel.setCOwnerNm((String) loJSON.get("sCompnyNm"));
                poModel.setCOwnerAd((String) loJSON.get("xAddressx"));
            }
        }
        return loJSON;
    }
    
    public JSONObject searchDealer(String fsValue) {
        String lsSQL = MiscUtil.addCondition(poModel.getSQLClientMaster(), " a.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                                              + " AND a.cClientTp = '1' AND a.cRecdStat = '1' ");
        System.out.println(lsSQL);
        JSONObject jObj = ShowDialogFX.Search(poGRider, 
                                         lsSQL,
                                         fsValue,
                                         "Dealership ID»Dealership", 
                                         "sClientID»sCompnyNm",
                                         "a.sClientID»a.sCompnyNm",
                                        1);
        if (jObj != null) {
            if(!"error".equals(jObj.get("result"))){
                poModel.setCompnyID((String) jObj.get("sClientID"));
                poModel.setDealerNm((String) jObj.get("sCompnyNm"));
            } else {
                poModel.setCompnyID("");
                poModel.setDealerNm(fsValue);
                jObj.put("result", "error");
                jObj.put("message", "No record loaded. Dealer Name will be set only.");
            }
        } else {
            poModel.setCompnyID("");
            poModel.setDealerNm(fsValue);
            jObj = new JSONObject();
            jObj.put("result", "error");
            jObj.put("message", "No record loaded. Dealer Name will be set only.");
            return jObj;
        }
        
        return jObj;
    }
    
    /**
     * For searching registered place when key is pressed.
     * @param fsValue the search value for the dealership.
     * @return {@code true} if a matching registered place is found, {@code false} otherwise: set only for sPlaceReg column.
    */
    public JSONObject searchRegsplace(String fsValue){
        String lsSQL =    " SELECT " 
                        + " a.sTownName " 
                        + ", b.sProvName " 
                        + " FROM towncity a " 
                        + " LEFT JOIN province b ON b.sProvIDxx = a.sProvIDxx ";
        
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTownName LIKE " + SQLUtil.toSQL(fsValue + "%")
                                               + " OR b.sProvName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        System.out.println(lsSQL);
        JSONObject jObj = ShowDialogFX.Search(poGRider, 
                                             lsSQL,
                                             fsValue,
                                             "Town»Province", 
                                             "sTownName»sProvName",
                                             "a.sTownName»b.sProvName",
                                            0);
            
        if (jObj != null) {
            if(!"error".equals(jObj.get("result"))){
                poModel.setPlaceReg(((String) jObj.get("sTownName") + " " + (String) jObj.get("sProvName")));
            } else {
                poModel.setPlaceReg("");
            }
        } else {
            poModel.setPlaceReg("");
            jObj = new JSONObject();
            jObj.put("result", "error");
            jObj.put("message", "No record loaded.");
            return jObj;
        }
        
        return jObj;
    }
    
    /**
     * For searching available vehicle when key is pressed.
     * @return {@code true} if a matching available vehicle is found, {@code false} otherwise.
    */
    public JSONObject searchAvailableVhcl(){
        String lsHeader = "Vehicle Serial ID»CS No.»Plate No.»Engine No»Frame No»Vehicle Description»Vehicle Status";
        String lsColName = "sSerialID»sCSNoxxxx»sPlateNox»sEngineNo»sFrameNox»sDescript»sVhclStat"; 
        String lsColCrit = "a.sSerialID»a.sCSNoxxxx»b.sPlateNox»a.sEngineNo»a.sFrameNox»c.sDescript»a.cSoldStat";
        
        String lsSQL = poModel.getSQL();
        lsSQL = MiscUtil.addCondition(lsSQL, " (a.cSoldStat = '0' OR a.cSoldStat = '1' ) AND (ISNULL(a.sClientID) OR  TRIM(a.sClientID) = '' )" );
        System.out.println(lsSQL);
//        ResultSet loRS;
//        loRS = poGRider.executeQuery(lsSQL);
//        JSONObject loJSON = ShowDialogFX.Search(poGRider, 
//                                        lsSQL, 
//                                        "", 
//                                        lsHeader, 
//                                        lsColName, 
//                                        lsColCrit, 
//                                        0);
        
        JSONObject loJSON = SearchDialog.jsonSearch(
                poGRider,
                lsSQL,
                    "",
                lsHeader,
                lsColName, 
                "0.2D»0.2D»0.2D»0.3D»0.3D»0.4D»0.4D", 
                "VEHICLE INFORMATION",
                0);
        
        
        if (loJSON != null){
            loJSON = openRecord((String) loJSON.get("sSerialID"));
            pnEditMode = poModel.getEditMode();
        } else {
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No vehicle found");
        }
               
        return loJSON;
    }
    
//    public JSONObject loadClientVehicleInfo(String fsValue){
//        String lsSQL = MiscUtil.addCondition(poModel.getSQL(), "h.sClientID = " + SQLUtil.toSQL(fsValue));
//        ResultSet loRS = poGRider.executeQuery(lsSQL);
//        
//        System.out.println(lsSQL);
//       try {
//            int lnctr = 0;
//            if (MiscUtil.RecordCount(loRS) > 0) {
//                paModel = new ArrayList<>();
//                while(loRS.next()){
//                        paModel.add(new Model_Vehicle_Serial_Master(poGRider));
//                        paModel.get(paModel.size() - 1).openRecord(loRS.getString("sSerialID"));
//                        
//                        pnEditMode = EditMode.UPDATE;
//                        lnctr++;
//                        poJSON.put("result", "success");
//                        poJSON.put("message", "Record loaded successfully.");
//                    } 
//                
//                System.out.println("lnctr = " + lnctr);
//            }else{
//                paModel = new ArrayList<>();
//                poJSON.put("result", "error");
//                poJSON.put("continue", true);
//                poJSON.put("message", "No record selected.");
//            }
//            MiscUtil.close(loRS);
//        } catch (SQLException e) {
//            poJSON.put("result", "error");
//            poJSON.put("message", e.getMessage());
//        }
//        return poJSON;
//    }
    
    public ArrayList<Model_Vehicle_Serial_Master> getVehicleSerialList(){return paModel;}
    public void setVehicleSerialList(ArrayList<Model_Vehicle_Serial_Master> foObj){this.paModel = foObj;}
    
    public void setVehicleSerial(int fnRow, int fnIndex, Object foValue){ paModel.get(fnRow).setValue(fnIndex, foValue);}
    public void setVehicleSerial(int fnRow, String fsIndex, Object foValue){ paModel.get(fnRow).setValue(fsIndex, foValue);}
    public Object getVehicleSerial(int fnRow, int fnIndex){return paModel.get(fnRow).getValue(fnIndex);}
    public Object getVehicleSerial(int fnRow, String fsIndex){return paModel.get(fnRow).getValue(fsIndex);}
    
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


