/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.clients;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.general.CancelForm;
import org.guanzon.auto.model.clients.Model_Service_Mechanic;
import org.guanzon.auto.validator.clients.ValidatorFactory;
import org.guanzon.auto.validator.clients.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Service_Mechanic_Master implements GRecord {
    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    
    int pnEditMode;
    String psRecdStat;

    Model_Service_Mechanic poModel;
    JSONObject poJSON;

    public Service_Mechanic_Master(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poModel = new Model_Service_Mechanic(foAppDrver);
        
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
            poModel = new Model_Service_Mechanic(poGRider);
            
            poModel.newRecord();
            if (poModel == null){
                poJSON.put("result", "error");
                poJSON.put("message", "initialized new record failed.");
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
        poModel = new Model_Service_Mechanic(poGRider);
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
        poJSON = new JSONObject();  
        ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Service_Mechanic, poModel);
        validator.setGRider(poGRider);
        if (!validator.isEntryOkay()){
            poJSON.put("result", "error");
            poJSON.put("message", validator.getMessage());
            return poJSON;
        }
        
        poJSON =  poModel.saveRecord();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWthParent) poGRider.rollbackTrans();
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
        poJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.UPDATE) {
            try {
                poJSON = poModel.setActive(false);
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                }
                
                CancelForm cancelform = new CancelForm();
                if (!cancelform.loadCancelWindow(poGRider, poModel.getClientID(), poModel.getClientID(), "SERVICE MECHANIC")) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Deactivation failed.");
                    return poJSON;
                }
                
                poJSON = poModel.saveRecord();
                if ("success".equals((String) poJSON.get("result"))) {
                    poJSON.put("result", "success");
                    poJSON.put("message", "Deactivation success.");
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Deactivation failed.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Service_Mechanic_Master.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            if ("success".equals((String) poJSON.get("result"))) {
                poJSON.put("result", "success");
                poJSON.put("message", "Activation success.");
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "Activation failed.");
            }
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }

    @Override
    public JSONObject searchRecord(String fsValue, boolean fbByActive) {
        String lsSQL = poModel.getSQL();
        
        if(fbByActive){
            lsSQL = MiscUtil.addCondition(lsSQL,  " b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                    + " AND a.cRecdStat = '1' ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println("SEARCH SERVICE MECHANIC: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Employee ID»Name»Address",
                "sClientID»sCompnyNm»sAddressx",
                "a.sClientID»b.sCompnyNm»IFNULL(b.sAddressx,IFNULL(CONCAT( IFNULL(CONCAT(f.sAddressx,' '), ''), IFNULL(CONCAT(h.sBrgyName,' '), ''), IFNULL(CONCAT(g.sTownName, ', '),''), IFNULL(CONCAT(i.sProvName),'') ), ''))",
                1);

        if (poJSON != null) {
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }

    @Override
    public Model_Service_Mechanic getModel() {
        return poModel;
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
    
    public JSONObject searchEmployee(String fsValue, boolean fbByCode){
        poJSON = new JSONObject();
        String lsSQL =   "  SELECT "                                                                                                                                                                                                 
                        + "   a.sEmployID "                                                                                                                                                                                           
                        + " , b.sClientID "                                                                                                                                                                                           
                        + " , b.sLastName "                                                                                                                                                                                           
                        + " , b.sFrstName "                                                                                                                                                                                           
                        + " , b.sMiddName "                                                                                                                                                                                           
                        + " , b.cClientTp "                                                                                                                                                                                           
                        + " , b.sCompnyNm "                                                                                                                                                                                           
                        + " , c.sDeptIDxx "                                                                                                                                                                                           
                        + " , c.sDeptName "                                                                                                                                                                                           
                        + " , e.sBranchCd "                                                                                                                                                                                           
                        + " , e.sBranchNm "                                                                                                                                                                                           
                        + " , IFNULL(b.sMobileNo,IFNULL(f.sMobileNo,'')) sMobileNo "                                                                                                                                                  
                        + " , IFNULL(b.sEmailAdd,IFNULL(g.sEmailAdd,'')) sEmailAdd "                                                                                                                                                  
                        + " , IFNULL(b.sAddressx,IFNULL(CONCAT( IFNULL(CONCAT(h.sAddressx,' '), ''), IFNULL(CONCAT(j.sBrgyName,' '), ''), IFNULL(CONCAT(i.sTownName, ', '),''), IFNULL(CONCAT(k.sProvName),'') ), '')) AS sAddressx " 
                        + " FROM GGC_ISysDBF.Employee_Master001 a  "                                                                                                                                                                  
                        + " LEFT JOIN GGC_ISysDBF.Client_Master b ON b.sClientID = a.sEmployID "                                                                                                                                      
                        + " LEFT JOIN GGC_ISysDBF.Department c ON c.sDeptIDxx = a.sDeptIDxx    "                                                                                                                                      
                        + " LEFT JOIN GGC_ISysDBF.Branch_Others d ON d.sBranchCD = a.sBranchCd "                                                                                                                                      
                        + " LEFT JOIN GGC_ISysDBF.Branch e ON e.sBranchCD = a.sBranchCd        "                                                                                                                                      
                        + " LEFT JOIN GGC_ISysDBF.Client_Mobile f ON f.sClientID = b.sClientID AND f.nPriority = 1 AND f.cRecdStat = 1  "                                                                                             
                        + " LEFT JOIN GGC_ISysDBF.Client_eMail_Address g ON g.sClientID = b.sClientID AND g.nPriority = 1 "                                                                                                           
                        + " LEFT JOIN GGC_ISysDBF.Client_Address h ON h.sClientID = b.sClientID AND h.nPriority = 1 "                                                                                                                 
                        + " LEFT JOIN GGC_ISysDBF.TownCity i ON i.sTownIDxx = h.sTownIDxx "                                                                                                                                           
                        + " LEFT JOIN GGC_ISysDBF.Barangay j ON j.sBrgyIDxx = h.sBrgyIDxx AND j.sTownIDxx = i.sTownIDxx "                                                                                                             
                        + " LEFT JOIN GGC_ISysDBF.Province k ON k.sProvIDxx = i.sProvIDxx "                                                                                                                                           
                        + " WHERE b.cRecdStat = '1' AND a.cRecdStat = '1' AND ISNULL(a.dFiredxxx) "                                                                                                                                   
                        + " AND d.sBranchCD = " + SQLUtil.toSQL(poGRider.getBranchCode());                                                                                                                                                                                      
        
        if (fbByCode)
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sEmployID LIKE " + SQLUtil.toSQL(fsValue + "%"));   
        else {
            if (!fsValue.isEmpty()) {
                lsSQL = MiscUtil.addCondition(lsSQL, "b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")); 
            }
        }
        
        System.out.println("SEARCH EMPLOYEE: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Employee ID»Name»Address»Department»Branch",
                "sEmployID»sCompnyNm»sAddressx»sDeptName»sBranchNm",
                "a.sEmployID»b.sCompnyNm»IFNULL(b.sAddressx,IFNULL(CONCAT( IFNULL(CONCAT(h.sAddressx,' '), ''), IFNULL(CONCAT(j.sBrgyName,' '), ''), IFNULL(CONCAT(i.sTownName, ', '),''), IFNULL(CONCAT(k.sProvName),'') ), ''))"
                        + "»c.sDeptName»e.sBranchNm",
                fbByCode ? 0 : 1);
        
        if (poJSON != null) {
            if("error".equals((String) poJSON.get("result"))){
                poModel.setClientID("");
                poModel.setCompnyNm("");
                poModel.setClientTp("");
                poModel.setFrstName("");
                poModel.setMiddName("");
                poModel.setLastName("");
                poModel.setAddress("");
            } else {
                poModel.setClientID((String) poJSON.get("sClientID"));
                poModel.setCompnyNm((String) poJSON.get("sCompnyNm"));
                poModel.setClientTp((String) poJSON.get("cClientTp"));
                poModel.setFrstName((String) poJSON.get("sFrstName"));
                poModel.setMiddName((String) poJSON.get("sMiddName"));
                poModel.setLastName((String) poJSON.get("sLastName"));
                poModel.setAddress((String) poJSON.get("sAddressx"));
            }
        } else {
            poModel.setClientID("");
            poModel.setCompnyNm("");
            poModel.setClientTp("");
            poModel.setFrstName("");
            poModel.setMiddName("");
            poModel.setLastName("");
            poModel.setAddress("");
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    public JSONObject validateExistingSM(){
        poJSON = new JSONObject();
        String lsClientID = "";
        String lsCompnyNm = "";
        try{
            if (poModel.getClientID().isEmpty()){
                poJSON.put("result", "") ;
                return poJSON;
            }

            String lsSQL = poModel.getSQL();
            lsSQL = MiscUtil.addCondition(lsSQL," a.sClientID = " + SQLUtil.toSQL(poModel.getClientID())) ;
            System.out.println("EXISTING SERVICE MECHANIC: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                        lsClientID = loRS.getString("sClientID");
                        lsCompnyNm = loRS.getString("sCompnyNm");
                }
                
                poJSON.put("result", "error") ;
                poJSON.put("message","Found an existing service mechanic record for\n" + lsCompnyNm.toUpperCase() + " <Employee ID:" + lsClientID + ">\n\n Do you want to view the record?");
                poJSON.put("sClientID", lsClientID) ;
                return poJSON;
            }
        
        
        } catch (SQLException ex) {
            Logger.getLogger(Service_Mechanic_Master.class.getName()).log(Level.SEVERE, null, ex);    
        }  catch (NullPointerException e) {
            // Handle the NullPointerException
            poJSON.put("result", "") ;
            System.out.println("Caught a NullPointerException: " + e.getMessage());
        }
        
        return poJSON;
    }
    
}
