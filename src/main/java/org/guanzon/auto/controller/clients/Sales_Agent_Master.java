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
import org.guanzon.auto.model.clients.Model_Sales_Agent;
import org.guanzon.auto.validator.clients.ValidatorFactory;
import org.guanzon.auto.validator.clients.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Sales_Agent_Master  implements GRecord {

    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    
    int pnEditMode;
    String psRecdStat;

    Model_Sales_Agent poModel;
    CachedRowSet poTransactions;
    JSONObject poJSON;

    public Sales_Agent_Master(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poModel = new Model_Sales_Agent(foAppDrver);
        
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
            poModel = new Model_Sales_Agent(poGRider);
            
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
        poModel = new Model_Sales_Agent(poGRider);
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
        ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Sales_Agent, poModel);
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
                if (!cancelform.loadCancelWindow(poGRider, poModel.getClientID(), poModel.getClientID(), "REFERRAL AGENT")) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Disapprove failed.");
                    return poJSON;
                }
                
                poJSON = poModel.saveRecord();
                if ("success".equals((String) poJSON.get("result"))) {
                    poJSON.put("result", "success");
                    poJSON.put("message", "Disapprove success.");
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Disapprove failed.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Sales_Agent_Master.class.getName()).log(Level.SEVERE, null, ex);
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
                poJSON.put("message", "Approve success.");
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "Approve failed.");
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
        
        System.out.println("SEARCH REFERRAL AGENT: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "Referral ID»Name»Address",
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
    public Model_Sales_Agent getModel() {
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
                + " , b.sAgentIDx AS  sAgentIDx  "                                                                          
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
            lsSQL = MiscUtil.addCondition(lsSQL, " b.sAgentIDx = " + SQLUtil.toSQL(poModel.getClientID()))
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
            Logger.getLogger(Sales_Agent_Master.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public JSONObject validateExistingRA(){
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
            System.out.println("EXISTING REFERRAL AGENT: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                        lsClientID = loRS.getString("sClientID");
                        lsCompnyNm = loRS.getString("sCompnyNm");
                }
                
                poJSON.put("result", "error") ;
                poJSON.put("message","Found an existing referral agent record for\n" + lsCompnyNm.toUpperCase() + " <Agent ID:" + lsClientID + ">\n\n Do you want to view the record?");
                poJSON.put("sClientID", lsClientID) ;
                return poJSON;
            }
        
        
        } catch (SQLException ex) {
            Logger.getLogger(Sales_Agent_Master.class.getName()).log(Level.SEVERE, null, ex);    
        }  catch (NullPointerException e) {
            // Handle the NullPointerException
            poJSON.put("result", "") ;
            System.out.println("Caught a NullPointerException: " + e.getMessage());
        }
        
        return poJSON;
    }
    
}
