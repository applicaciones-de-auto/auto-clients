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
import org.guanzon.auto.model.clients.Model_Sales_Executive;
import org.guanzon.auto.validator.clients.ValidatorFactory;
import org.guanzon.auto.validator.clients.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Sales_Executive_Master implements GRecord {

    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    
    int pnEditMode;
    String psRecdStat;

    Model_Sales_Executive poModel;
    CachedRowSet poTransactions;
    JSONObject poJSON;

    public Sales_Executive_Master(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poModel = new Model_Sales_Executive(foAppDrver);
        
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
            poModel = new Model_Sales_Executive(poGRider);
            
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
        poModel = new Model_Sales_Executive(poGRider);
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
        ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Sales_Executive, poModel);
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
                if (!cancelform.loadCancelWindow(poGRider, poModel.getClientID(), poModel.getClientID(), "SALES EXECUTIVE")) {
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
                Logger.getLogger(Sales_Executive_Master.class.getName()).log(Level.SEVERE, null, ex);
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
        
        System.out.println("SEARCH SALES EXECUTIVE: " + lsSQL);
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
    public Model_Sales_Executive getModel() {
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
                poModel.setMobileNo("");
                poModel.setEmailAdd("");
                poModel.setAddress("");
            } else {
                poModel.setClientID((String) poJSON.get("sClientID"));
                poModel.setCompnyNm((String) poJSON.get("sCompnyNm"));
                poModel.setClientTp((String) poJSON.get("cClientTp"));
                poModel.setFrstName((String) poJSON.get("sFrstName"));
                poModel.setMiddName((String) poJSON.get("sMiddName"));
                poModel.setLastName((String) poJSON.get("sLastName"));
                poModel.setMobileNo((String) poJSON.get("sMobileNo"));
                poModel.setEmailAdd((String) poJSON.get("sEmailAdd"));
                poModel.setAddress((String) poJSON.get("sAddressx"));
            }
        } else {
            poModel.setClientID("");
            poModel.setCompnyNm("");
            poModel.setClientTp("");
            poModel.setFrstName("");
            poModel.setMiddName("");
            poModel.setLastName("");
            poModel.setMobileNo("");
            poModel.setEmailAdd("");
            poModel.setAddress("");
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    private String getSQ_VSPTransaction(){
        return    " SELECT  "                                                                                                
                + "   a.sTransNox "                                                                                          
                + " , a.sVSPNOxxx "                                                                                          
                + " , a.dTransact "                                                                                          
                + " , a.sBranchCd "                                                                                          
                + " , q.sBranchNm "                                                                                          
                + " , b.sClientID AS  sInqCltID "                                                                            
                + " , c.sCompnyNm AS  sInqCltNm "                                                                            
                + " , IFNULL(UPPER(CONCAT( IFNULL(CONCAT(dd.sHouseNox,' ') , ''), IFNULL(CONCAT(dd.sAddressx,' ') , ''), "   
                + "     	IFNULL(CONCAT(f.sBrgyName,' '), ''),  "                                                             
                + "     	IFNULL(CONCAT(e.sTownName, ', '),''), "                                                             
                + "     	IFNULL(CONCAT(g.sProvName),'') )), '') AS sInqCtAdd "                                                
                + " , h.sCompnyNm AS  sBuyCltNm  "                                                                           
                + " , IFNULL(UPPER(CONCAT( IFNULL(CONCAT(ii.sHouseNox,' ') , ''), IFNULL(CONCAT(ii.sAddressx,' ') , ''), "    
                + "     	IFNULL(CONCAT(k.sBrgyName,' '), ''),  "                                                             
                + "     	IFNULL(CONCAT(j.sTownName, ', '),''), "                                                             
                + "     	IFNULL(CONCAT(l.sProvName),'') ))	, '') AS sBuyCtAdd " 					                                   
                + " , p.sPlatform AS  sPlatForm  "                                                                          
                + " , r.sCompnyNm AS  sSaleExNm  "                                                                          
                + " , s.sCompnyNm AS  sSalesAgn  "                                                                          
                + " , m.sCSNoxxxx AS sCSNoxxxx 	 "																                                         
                + " , n.sPlateNox AS sPlateNox 	 "																                                         
                + " , m.sFrameNox AS sFrameNox   "																			                                   
                + " , m.sEngineNo AS sEngineNo   "                                                                          
                + " , o.sDescript AS sDescript   "                                                                          
                + " , t.sTransNox AS sUDRCodex   "                                                                          
                + " , t.sReferNox AS sUDRNoxxx   "                                                                          
                + " , t.dTransact AS dUDRDatex   "	                                                                         
                + " FROM vsp_master a "                                                                                      
                + " LEFT JOIN customer_inquiry b ON b.sTransNox = a.sInqryIDx  "                                             
                /*inquiring customer*/                                                                                   
                + " LEFT JOIN client_master c ON c.sClientID = b.sClientID  "                                                
                + " LEFT JOIN client_address d ON d.sClientID = c.sClientID AND d.cPrimaryx = '1' "                           
                + " LEFT JOIN addresses dd ON dd.sAddrssID = d.sAddrssID "                                                   
                + " LEFT JOIN TownCity e ON e.sTownIDxx = dd.sTownIDxx   "                                                   
                + " LEFT JOIN barangay f ON f.sBrgyIDxx = dd.sBrgyIDxx AND f.sTownIDxx = dd.sTownIDxx "                       
                + " LEFT JOIN Province g ON g.sProvIDxx = e.sProvIDxx "                                                      
                /*buying customer*/                                                                                      
                + " LEFT JOIN client_master h ON h.sClientID = a.sClientID "                                                  
                + " LEFT JOIN client_address i ON i.sClientID = c.sClientID AND i.cPrimaryx = '1' "                           
                + " LEFT JOIN addresses ii ON ii.sAddrssID = d.sAddrssID "                                                    
                + " LEFT JOIN TownCity j ON j.sTownIDxx = ii.sTownIDxx   "                                                    
                + " LEFT JOIN barangay k ON k.sBrgyIDxx = ii.sBrgyIDxx AND k.sTownIDxx = ii.sTownIDxx "                      
                + " LEFT JOIN Province l ON l.sProvIDxx = j.sProvIDxx "                                                       
                /*vehicle information*/  								                                                                 
                + " LEFT JOIN vehicle_serial m ON m.sSerialID = a.sSerialID "      										                       
                + " LEFT JOIN vehicle_serial_registration n ON n.sSerialID = m.sSerialID "                                     
                + " LEFT JOIN vehicle_master o ON o.sVhclIDxx = m.sVhclIDxx "                                                 
                /*inquiry information*/                                                                                  
                + " LEFT JOIN online_platforms p ON p.sTransNox = b.sSourceNo "                                                
                + " LEFT JOIN branch q ON q.sBranchCd = a.sBranchCd  "                                                       
                + " LEFT JOIN GGC_ISysDBF.Client_Master r ON r.sClientID = b.sEmployID "                                      
                + " LEFT JOIN client_master s ON s.sClientID = b.sAgentIDx "                                                  
                /*udr information*/                                                                                      
                + " INNER JOIN udr_master t ON t.sSourceCd = a.sTransNox AND t.cTranStat = '1' " ;
                   
    }        
    
    public JSONObject loadTransactions(){
        poJSON = new JSONObject();
        try {
            
            String lsSQL = getSQ_VSPTransaction();
            lsSQL = MiscUtil.addCondition(lsSQL, " b.sEmployID = " + SQLUtil.toSQL(poModel.getClientID()))
                                                    + " AND a.cTranStat = '1'  "
                                                    + " GROUP BY a.sTransNox ORDER BY a.dTransact DESC " ;
            
            System.out.println("VSP : "+ lsSQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            try {
                poTransactions = factory.createCachedRowSet();
                poTransactions.populate(loRS);
                MiscUtil.close(loRS);
            } catch (SQLException e) {
                poJSON.put("result", "error");
                poJSON.put("message", e.getMessage());
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Sales_Executive_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        return poJSON;
    }
    
    public int getVSPTransCount() throws SQLException{
        if (poTransactions != null){
            poTransactions.last();
            return poTransactions.getRow();
        }else{
            return 0;
        }
    }
    
    public Object getVSPTransDetail(int fnRow, int fnIndex) throws SQLException{
        if (fnIndex == 0) return null;
        
        poTransactions.absolute(fnRow);
        return poTransactions.getObject(fnIndex);
    }
    
    public Object getVSPTransDetail(int fnRow, String fsIndex) throws SQLException{
        return getVSPTransDetail(fnRow, MiscUtil.getColumnIndex(poTransactions, fsIndex));
    }
    
    public JSONObject validateExistingSE(){
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
            System.out.println("EXISTING SALES EXECUTIVE: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                        lsClientID = loRS.getString("sClientID");
                        lsCompnyNm = loRS.getString("sCompnyNm");
                }
                
                poJSON.put("result", "error") ;
                poJSON.put("message","Found an existing sales executive record for\n" + lsCompnyNm.toUpperCase() + " <Employee ID:" + lsClientID + ">\n\n Do you want to view the record?");
                poJSON.put("sClientID", lsClientID) ;
                return poJSON;
            }
        
        
        } catch (SQLException ex) {
            Logger.getLogger(Sales_Executive_Master.class.getName()).log(Level.SEVERE, null, ex);    
        }  catch (NullPointerException e) {
            // Handle the NullPointerException
            poJSON.put("result", "") ;
            System.out.println("Caught a NullPointerException: " + e.getMessage());
        }
        
        return poJSON;
    }
}
