/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.clients;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.model.clients.Model_Client_Master;
import org.guanzon.auto.general.SearchDialog;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Arsiela
 */
public class Client_Master implements GRecord{
    final String XML = "Model_Client_Master.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    Model_Client_Master poClient;
    
    String FILE_PATH = "D://GGC_Maven_Systems/json/Client_Master.json";
    
    
    public Client_Master(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poGRider = foAppDrver;
        pbWtParent = fbWtParent;
        psBranchCd = fsBranchCd.isEmpty() ? foAppDrver.getBranchCode() : fsBranchCd;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;        
    }

    @Override
    public void setRecordStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        JSONObject obj = new JSONObject();
        obj.put("pnEditMode", pnEditMode);
        if (pnEditMode != EditMode.UNKNOWN){
            // Don't allow specific fields to assign values
            if(!(fnCol == poClient.getColumn("sClientID") ||
                fnCol == poClient.getColumn("cRecdStat") ||
                fnCol == poClient.getColumn("sModified") ||
                fnCol == poClient.getColumn("dModified"))){
                poClient.setValue(fnCol, foData);
                obj.put(fnCol, pnEditMode);
            }
        }
        return obj;
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return setMaster(poClient.getColumn(fsCol), foData);
    }


    @Override
    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poClient.getValue(fnCol);
    }

    @Override
    public Object getMaster(String fsCol) {
        return getMaster(poClient.getColumn(fsCol));
    }

    @Override
    public JSONObject newRecord() {
        poJSON = new JSONObject();
        try{
            pnEditMode = EditMode.ADDNEW;
            org.json.simple.JSONObject obj;

            poClient = new Model_Client_Master(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poClient.setClientID(MiscUtil.getNextCode(poClient.getTable(), "sClientID", true, loConn, psBranchCd));
            poClient.newRecord();
            
            if (poClient == null){
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
    
//    private JSONObject saveJSONFile(){
//        JSONObject jObj = new JSONObject();
//        JSONObject jClassObj = poClient.getColValues();
//        jClassObj.put("pnEditMode", pnEditMode);
//        // Write the JSONObject to a file
//        try (FileWriter file = new FileWriter(FILE_PATH)) {
//            file.write(jClassObj.toJSONString());
//            System.out.println("JSON file created successfully.");
//            jObj.put("result","success");
//            jObj.put("message","JSON file created successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//            jObj.put("result","error");
//            jObj.put("message","Invalid JSON Path File");
//        }
//        return jObj;
//    }
    
//    public JSONObject loadJSONFile(){
//        poClient = new Model_Client_Master(poGRider);
//        JSONObject jObj = new JSONObject();
//        File Delfile = new File(FILE_PATH);
//        String tempValue = "";
//        JSONParser parser = new JSONParser();
//        if (Delfile.exists() && Delfile.isFile()) {
//
//            try (FileReader reader = new FileReader(FILE_PATH)) {
//                Object obj = parser.parse(reader);
//                JSONObject jsonObject = (JSONObject) obj;
//                System.out.println(jsonObject);
//                pnEditMode = Integer.valueOf((String) jsonObject.get("pnEditMode"));
//                
//                if(pnEditMode != EditMode.UNKNOWN){
//                    switch(pnEditMode) {
//                        case EditMode.ADDNEW:
//                            jObj = poClient.newRecord();
//                            break;
//                        case EditMode.READY:
//                            jObj = poClient.openRecord((String) jsonObject.get("sClientID"));
//                            break;
//                        case EditMode.UPDATE:
//                            poClient.openRecord((String) jsonObject.get("sClientID"));
//                            jObj = updateRecord();
//                            break;
//                    
//                    }
//                    if("error".equals(jObj.get("result"))){
//                        return jObj;
//                    }
//                    jObj = poClient.updateColValues(jsonObject);
//                }
//                
//            } catch (IOException | ParseException e) {
//                e.printStackTrace();
//            }
//        } 
//        return jObj;
//    }

    @Override
    public JSONObject openRecord(String fsValue) {
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        
        poClient = new Model_Client_Master(poGRider);
        poJSON = poClient.openRecord(fsValue);
        //poJSON = saveJSONFile();
        if("error".equals(poJSON.get("result"))){
            return poJSON;
        }
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
        poJSON = new JSONObject();  
//        ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Client_Master, poClient);
//        validator.setGRider(poGRider);
//        if (!validator.isEntryOkay()){
//            poJSON.put("result", "error");
//            poJSON.put("message", validator.getMessage());
//            return poJSON;
//        }

//        poJSON = saveJSONFile();
        if("error".equals(poJSON.get("result"))){
            return poJSON;
        }
        
        poJSON =  poClient.saveRecord();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        return poJSON;
    }

    @Override
    public JSONObject deleteRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject deactivateRecord(String fsValue) {
        pnEditMode = EditMode.READY;
        JSONObject obj = new JSONObject();
        obj.put("pnEditMode", pnEditMode);
        return obj;
    }

    @Override
    public JSONObject activateRecord(String fsValue) {
        pnEditMode = EditMode.READY;
        JSONObject obj = new JSONObject();
        obj.put("pnEditMode", pnEditMode);
        return obj;
    }

    @Override
    public JSONObject searchRecord(String fsValue, boolean fbByCode) {
        String lsHeader = "ID»Name»Address»Client Type"; // »Client Type
        String lsColName = "sClientID»sCompnyNm»xAddressx»sClientTp"; //"sClientID»sCompnyNm»xAddressx»sLastName»sFrstName»sMiddName»sSuffixNm»sClientTp
        //String lsColCrit = "a.sClientID»a.sCompnyNm»CONCAT(bb.sHouseNox, ' ', bb.sAddressx, ', ', c.sTownName, ' ', d.sProvName)";
        String lsSQL = "SELECT " +
                            "  a.sClientID" +
                            ", UPPER(a.sCompnyNm) sCompnyNm" +
                            ", UPPER(CONCAT(bb.sHouseNox, ' ', bb.sAddressx,' ', c.sBrgyName, ', ', d.sTownName, ' ', e.sProvName)) xAddressx" +
                            ", a.sLastName" + 
                            ", a.sFrstName" + 
                            ", a.sMiddName" + 
                            ", a.sSuffixNm" + 
                            ", a.cClientTp" + 
                            ", CASE WHEN a.cClientTp = '0' THEN 'CLIENT' ELSE 'COMPANY' END AS sClientTp " +  
                        " FROM Client_Master a" + 
                            " LEFT JOIN Client_Address b" + 
                                " ON a.sClientID = b.sClientID AND b.cPrimaryx = '1'" + 
                            " LEFT JOIN Addresses bb" + 
                                " ON bb.sAddrssID = b.sAddrssID" + 
                            " LEFT JOIN barangay c " + 
                                "ON c.sBrgyIDxx = bb.sBrgyIDxx " +
                            " LEFT JOIN TownCity d" + 
                                " ON bb.sTownIDxx = d.sTownIDxx" + 
                            " LEFT JOIN Province e" +
                                " ON d.sProvIDxx = e.sProvIDxx";
        JSONObject loJSON;
        String lsValue;
            
        System.out.println(lsSQL);
//        loJSON = ShowDialogFX.Search(poGRider, 
//                                        lsSQL, 
//                                        fsValue, 
//                                        lsHeader, 
//                                        lsColName, 
//                                        lsColCrit, 
//                                        fbByCode ? 0 :1);
        
        loJSON = SearchDialog.jsonSearch(
                poGRider,
                lsSQL,
                    fsValue,
                lsHeader,//"Client ID»Customer Name", //»Address
                lsColName, //"sClientID»sCompnyNm", //»CONCAT(bb.sHouseNox, ' ', bb.sAddressx, ', ', c.sTownName, ' ', d.sProvName)
                "0.2D»0.3D»0.5D»0.2D", 
                "CUSTOMER",
                0);
            
        
        if (loJSON != null && !"error".equals((String) loJSON.get("result"))) {
            System.out.println("sClientID = " + (String) loJSON.get("sClientID"));
            lsValue = (String) loJSON.get("sClientID");
        }else {
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No client information found");
            return loJSON;
        }
        System.out.println("loJSON = " + loJSON.toJSONString());
        return loJSON; //openRecord(lsValue);
    }

    @Override
    public Model_Client_Master getModel() {
        return poClient;
    }
    
    private Connection setConnection(){
        Connection foConn;
        
        if (pbWtParent){
            foConn = (Connection) poGRider.getConnection();
            if (foConn == null) foConn = (Connection) poGRider.doConnect();
        }else foConn = (Connection) poGRider.doConnect();
        
        return foConn;
    }
    
    private JSONObject checkData(JSONObject joValue){
        if(pnEditMode == EditMode.READY || pnEditMode == EditMode.UPDATE){
            if(joValue.containsKey("continue")){
                if(true == (boolean)joValue.get("continue")){
                    joValue.put("result", "success");
                    joValue.put("message", "Record saved successfully.");
                }
            }
        }
        return joValue;
    }
    
    public JSONObject searchSpouse(String fsValue){
        JSONObject loJSON;
        String lsSQL = MiscUtil.addCondition(poClient.getSQL(), "a.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")
                                               + " AND a.sClientID <> " + SQLUtil.toSQL(poClient.getClientID())
                                               + " AND a.cGenderCd <> " + SQLUtil.toSQL(poClient.getGender())
                                               + " AND a.cClientTp <> '1' ");
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "Client ID»Spouse Name»Address", 
                            "sClientID»sCompnyNm»sAddressx",
                            "a.sClientID»a.sCompnyNm",
                            0);
            
        if (loJSON != null) {
            poClient.setSpouseID((String) loJSON.get("sClientID"));
            poClient.setSpouseNm((String) loJSON.get("sCompnyNm"));

            loJSON.put("result", "success");
            loJSON.put("message", "Search spouse success.");
            return loJSON;
        }else {
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No record selected.");
            return loJSON;
        }
    }
    
    public JSONObject searchCitizenShip(String fsValue){
        JSONObject loJSON;
        String lsSQL = " SELECT " 
                       + "  sCntryCde" 
                       + ", sCntryNme" 
                       + " FROM Country";
                
        lsSQL = MiscUtil.addCondition(lsSQL, "sCntryNme LIKE " + SQLUtil.toSQL(fsValue + "%"));
         
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "Country", 
                            "sCntryNme",
                            "sCntryNme",
                            0);
            
            if (loJSON != null) {
                poClient.setCitizen((String) loJSON.get("sCntryCde"));
                poClient.setCntryNme((String) loJSON.get("sCntryNme"));
                
                loJSON.put("result", "success");
                loJSON.put("message", "Search country success.");
                return loJSON;
            }else {
                
                loJSON  = new JSONObject();  
                loJSON.put("result", "error");
                loJSON.put("message", "No record selected.");
                return loJSON;
            }
    }
    
    public JSONObject searchBirthPlc(String fsValue){
        JSONObject loJSON;
        String lsSQL =  " SELECT " 
                        + "  IFNULL(a.sTownIDxx, '') sTownIDxx " 
                        + ", IFNULL(a.sTownName, '') sTownName " 
                        + ", IFNULL(a.sZippCode, '') sZippCode "                      
                        + ", IFNULL(b.sProvName, '') sProvName "  
                        + " FROM TownCity a"  
                        + " LEFT JOIN Province b on b.sProvIDxx = a.sProvIDxx";
        
        lsSQL =  MiscUtil.addCondition(lsSQL, "TRIM(CONCAT(a.sTownName, ', ', b.sProvName)) LIKE " + SQLUtil.toSQL("%" +fsValue + "%"));
       
        System.out.println(lsSQL);
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                    "Code»Town»Province",   
                    "sTownIDxx»sTownName»sProvName»",
                     "a.sTownIDxx»TRIM(CONCAT(a.sTownName, ', ', b.sProvName))»b.sProvName",
                            1);
            
            if (loJSON != null) {
                poClient.setBirthPlc((String) loJSON.get("sTownIDxx"));
                poClient.setTownName((String) loJSON.get("sTownName")+ ", " + (String) loJSON.get("sProvName"));
                
                loJSON.put("result", "success");
                loJSON.put("message", "Search birthplace success.");
                return loJSON;
            }else {
                poClient.setBirthPlc("");
                poClient.setTownName("");
                loJSON  = new JSONObject();  
                loJSON.put("result", "error");
                loJSON.put("message", "No record selected.");
                return loJSON;
            }
    }
    
    
}
