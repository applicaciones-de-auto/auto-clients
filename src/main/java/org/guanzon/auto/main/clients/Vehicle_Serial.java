/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.clients;

import java.sql.ResultSet;
import java.util.ArrayList;
import javax.sql.rowset.RowSetFactory;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.controller.clients.Vehicle_Registration;
import org.guanzon.auto.controller.clients.Vehicle_Serial_Master;
import org.guanzon.auto.general.SearchDialog;
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
    Vehicle_Registration poVhclReg;
    Client poClient;
    
    public Vehicle_Serial(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new Vehicle_Serial_Master(foAppDrver,fbWtParent,fsBranchCd);
        poVhclReg = new Vehicle_Registration(foAppDrver,fbWtParent,fsBranchCd);
        poClient = new Client(foAppDrver,fbWtParent,fsBranchCd);
        
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
            poVhclReg.newRecord();
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
        poJSON = poVhclReg.openRecord(fsValue);
        
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
        poJSON = poVhclReg.updateRecord();
        pnEditMode = poController.getEditMode();
        return poJSON;
    }

    @Override
    public JSONObject saveRecord() {
        poJSON = poController.saveRecord();
        if(!"error".equals((String) poJSON.get("result"))){
            poJSON = poVhclReg.saveRecord();
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
    public JSONObject searchRecord(String fsValue,  boolean fbByActive) {
        poJSON = new JSONObject();  
        poJSON = poController.searchRecord(fsValue, fbByActive);
        if(!"error".equals(poJSON.get("result"))){
            poJSON = openRecord((String) poJSON.get("sSerialID"));
        }
        return poJSON;
    }

    @Override
    public Vehicle_Serial_Master getModel() {
        return poController;
    }
    
    public Vehicle_Registration getVhclRegController() {
        return poVhclReg;
    }
    
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
                    if(poController.getModel().getClientID().equals((String) loJSON.get("sClientID"))){
                        loJSON.put("result", "error");
                        loJSON.put("message", "Selected new owner is the same with current owner.");
                    }
                    
                    if(poController.getModel().getCoCltID().equals((String) loJSON.get("sClientID"))){
                        loJSON.put("result", "error");
                        loJSON.put("message", "Selected new owner cannot be the same with current co - owner.");
                    }
                }
                
                poController.getModel().setClientID((String) loJSON.get("sClientID"));
                poController.getModel().setOwnerNmx((String) loJSON.get("sCompnyNm"));
                poController.getModel().setOwnerAdd((String) loJSON.get("xAddressx"));
            } else {
                
                if(poController.getModel().getClientID().equals((String) loJSON.get("sClientID"))){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Selected co - owner cannot be the same with current owner.");
                }
                
                poController.getModel().setCoCltID((String) loJSON.get("sClientID"));
                poController.getModel().setCOwnerNm((String) loJSON.get("sCompnyNm"));
                poController.getModel().setCOwnerAd((String) loJSON.get("xAddressx"));
            }
        }
        return loJSON;
    }
    
    /**
     * For searching available vehicle when key is pressed.
     * @return {@code true} if a matching available vehicle is found, {@code false} otherwise.
    */
    public JSONObject searchAvailableVhcl(String fsValue){
        String lsHeader = "Vehicle Serial»CS No»Vehicle Description»Plate No»Frame Number»Engine Number";
        String lsColName = "sSerialID»sCSNoxxxx»sDescript»sPlateNox»sFrameNox»sEngineNo"; 
        String lsColCrit = "a.sSerialID»a.sCSNoxxxx»b.sPlateNox»c.sDescript»a.sFrameNox»a.sEngineNo";
        String lsSQL = poController.getModel().getSQL();
        lsSQL = MiscUtil.addCondition(lsSQL, " a.cSoldStat = '1' AND (ISNULL(a.sClientID) OR  TRIM(a.sClientID) = '' )" );
        
        ResultSet loRS;
        loRS = poGRider.executeQuery(lsSQL);
        JSONObject loJSON = ShowDialogFX.Search(poGRider, 
                                        lsSQL, 
                                        fsValue, 
                                        lsHeader, 
                                        lsColName, 
                                        lsColCrit, 
                                        0);
        
        
        if (loJSON != null){
            loJSON = openRecord((String) loJSON.get("sSerialID"));
            pnEditMode = EditMode.UPDATE;
        } else {
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No vehicle found");
        }
               
        return loJSON;
    }
    
    private String getSQ_SearchVhclMake(){
        return  " SELECT " +  
                " IFNULL(a.sMakeIDxx,'') sMakeIDxx  " +   
                " , IFNULL(b.sMakeDesc,'') sMakeDesc " +   
                " , IFNULL(a.sVhclIDxx,'') sVhclIDxx  " +   
                " FROM vehicle_master a " + 
                " LEFT JOIN vehicle_make b ON b.sMakeIDxx = a.sMakeIDxx " ;
    }
    
    /**
     * For searching vehicle make when key is pressed.
     * @param fsValue the search value for the vehicle make.
     * @return {@code true} if a matching vehicle make is found, {@code false} otherwise.
    */
    public JSONObject searchVehicleMake(String fsValue) {
        String lsHeader = "Make ID»Vehicle Make";
        String lsColName = "sMakeIDxx»sMakeDesc"; 
        String lsColCrit = "a.sMakeIDxx»b.sMakeDesc";
        
        String lsSQL = getSQ_SearchVhclMake();
        String lsOrigVal = poController.getModel().getMakeID();
        String lsNewVal = "";
        
        ResultSet loRS;
        JSONObject loJSON = null;
        lsSQL = getSQ_SearchVhclMake() + " GROUP BY a.sMakeIDxx ";
        //System.out.println(lsSQL);
        loRS = poGRider.executeQuery(lsSQL);
        loJSON = ShowDialogFX.Search(poGRider, 
                                        lsSQL, 
                                        fsValue, 
                                        lsHeader, 
                                        lsColName, 
                                        lsColCrit, 
                                        0);
        if (loJSON == null){
        } else {
            lsNewVal = (String) loJSON.get("sMakeIDxx");
            setMaster("sMakeIDxx", (String) loJSON.get("sMakeIDxx"));
            setMaster("sMakeDesc", (String) loJSON.get("sMakeDesc"));
        }
          
            
        if(!lsNewVal.equals(lsOrigVal)){
            setMaster("sVhclIDxx", "");
            setMaster("sModelIDx", "");
            setMaster("sModelDsc", "");
            setMaster("sTypeIDxx", "");
            setMaster("sTypeDesc", "");
            setMaster("sColorIDx", "");
            setMaster("sColorDsc", "");
            setMaster("sTransMsn", "");
            setMaster("nYearModl", "");
            setMaster("sFrameNox", "");
            setMaster("sEngineNo", "");
            
            if (loJSON == null){
                loJSON  = new JSONObject();  
                loJSON.put("result", "error");
                loJSON.put("message", "No vehicle make found");
                setMaster("sMakeIDxx","");
                return loJSON;
            }
            
        }     
        return loJSON;
    }
        
    private String getSQ_SearchVhclModel(){
        return  " SELECT " +  
                " IFNULL(a.sModelIDx,'') sModelIDx  " +   
                " , IFNULL(b.sModelDsc,'') sModelDsc " +  
                " , IFNULL(a.sVhclIDxx,'') sVhclIDxx  " +    
                " FROM vehicle_master a " + 
                " LEFT JOIN vehicle_model b ON b.sModelIDx = a.sModelIDx " ;
    }    
    
    /**
     * For searching vehicle model when key is pressed.
     * @param fsValue the search value for the vehicle model.
     * @return {@code true} if a matching vehicle model is found, {@code false} otherwise.
    */
    public JSONObject searchVehicleModel(String fsValue) {
        String lsHeader = "Model ID»Vehicle Model";
        String lsColName = "sModelIDx»sModelDsc"; 
        String lsColCrit = "a.sModelIDx»b.sModelDsc";
        
        String lsSQL = MiscUtil.addCondition( getSQ_SearchVhclModel(), " a.sMakeIDxx = " + SQLUtil.toSQL((String) getMaster("sMakeIDxx")));
        String lsOrigVal = getMaster(25).toString();
        String lsNewVal = "";

        ResultSet loRS;
        JSONObject loJSON = null;
        lsSQL = lsSQL  + " GROUP BY a.sModelIDx " ;
        //System.out.println(lsSQL);
        loRS = poGRider.executeQuery(lsSQL);
        
        loJSON = ShowDialogFX.Search(poGRider, 
                                        lsSQL, 
                                        fsValue, 
                                        lsHeader, 
                                        lsColName, 
                                        lsColCrit, 
                                        0);
        if (loJSON == null){
        } else {
            lsNewVal = (String) loJSON.get("sModelIDx");
            setMaster("sModelIDx", (String) loJSON.get("sModelIDx"));
            setMaster("sModelDsc", (String) loJSON.get("sModelDsc"));
        }
            
        if(!lsNewVal.equals(lsOrigVal)){
            setMaster("sVhclIDxx", "");
            setMaster("sTypeIDxx", "");
            setMaster("sTypeDesc", "");
            setMaster("sColorIDx", "");
            setMaster("sColorDsc", "");
            setMaster("sTransMsn", "");
            setMaster("nYearModl", "");
            setMaster("sFrameNox", "");
            setMaster("sEngineNo", "");
            
            
            if (loJSON == null){
                loJSON  = new JSONObject();  
                loJSON.put("result", "error");
                loJSON.put("message", "No vehicle model found");
                setMaster("sModelIDx","");
                return loJSON;
            }
            
        }       
        return loJSON;
    }
    
    private String getSQ_SearchVhclType(){
        return  " SELECT " +  
                " IFNULL(a.sTypeIDxx,'') sTypeIDxx  " +   
                " , IFNULL(b.sTypeDesc,'') sTypeDesc " +  
                " , IFNULL(a.sVhclIDxx,'') sVhclIDxx  " +    
                " FROM vehicle_master a " + 
                " LEFT JOIN vehicle_type b ON b.sTypeIDxx = a.sTypeIDxx " ;
    }
    
    /**
     * For searching vehicle type when key is pressed.
     * @param fsValue the search value for the vehicle type.
     * @return {@code true} if a matching vehicle type is found, {@code false} otherwise.
    */
    public JSONObject searchVehicleType(String fsValue) {
        String lsHeader = "Type ID»Vehicle Type";
        String lsColName = "sTypeIDxx»sTypeDesc"; 
        String lsColCrit = "a.sTypeIDxx»b.sTypeDesc";

        String lsSQL = MiscUtil.addCondition( getSQ_SearchVhclType(), 
                                                " a.sMakeIDxx = " + SQLUtil.toSQL((String) getMaster("sMakeIDxx")) +
                                                " AND a.sModelIDx = " + SQLUtil.toSQL((String) getMaster("sModelIDx"))
                                            );
        String lsOrigVal = getMaster(27).toString();
        String lsNewVal = "";
        
        ResultSet loRS;
        JSONObject loJSON = null;
//        if (!pbWithUI) {   
//            lsSQL = (MiscUtil.addCondition(lsSQL, " b.sTypeDesc LIKE " + SQLUtil.toSQL(fsValue + "%") 
//                                           )  +      " GROUP BY a.sTypeIDxx " );
//            System.out.println(lsSQL);
//            lsSQL += " LIMIT 1";
//            loRS = poGRider.executeQuery(lsSQL);
//            
//            if (loRS.next()){
//                lsNewVal = loRS.getString("sTypeIDxx");
//                setMaster("sTypeIDxx", loRS.getString("sTypeIDxx"));
//                setMaster("sTypeDesc", loRS.getString("sTypeDesc"));
//            }
//        } else {
//            lsSQL = lsSQL  +  " GROUP BY a.sTypeIDxx " ;
//            System.out.println(lsSQL);
//            loRS = poGRider.executeQuery(lsSQL);
//            //loJSON = showFXDialog.jsonBrowse(poGRider, loRS, "Vehicle Type", "sTypeDesc");
//            loJSON = showFXDialog.jsonSearch(poGRider, 
//                                             lsSQL,
//                                             "%" + fsValue +"%",
//                                             "Type ID»Vehicle Type", 
//                                             "sTypeIDxx»sTypeDesc",
//                                             "a.sTypeIDxx»b.sTypeDesc",
//                                            1);
//            if (loJSON == null){
//            } else {
//                lsNewVal = (String) loJSON.get("sTypeIDxx");
//                setMaster("sTypeIDxx", (String) loJSON.get("sTypeIDxx"));
//                setMaster("sTypeDesc", (String) loJSON.get("sTypeDesc"));
//            }
//        } 
//        
//        if(!lsNewVal.equals(lsOrigVal)){
//            setMaster("sVhclIDxx", "");
//            setMaster("sColorIDx", "");
//            setMaster("sColorDsc", "");
//            setMaster("sTransMsn", "");
//            setMaster("nYearModl", "");
//            
//            if (!pbWithUI) {
//                if (!loRS.next()){
//                    psMessage = "No record found.";
//                    setMaster("sTypeIDxx","");
//                    return false;
//                }
//            } else {
//                if (loJSON == null){
//                    psMessage = "No record found/selected.";
//                    setMaster("sTypeIDxx","");
//                    return false;
//                }
//            }
//        }     
        return loJSON;
    }
    /**
     * For searching vehicle transmission when key is pressed.
     * @param fsValue the search value for the vehicle transmission.
     * @return {@code true} if a matching vehicle transmission is found, {@code false} otherwise.
    */
//    public boolean searchVehicleTrnsMn(String fsValue) throws SQLException{
////        lsSQL = (MiscUtil.addCondition(lsSQL, " a.sTransMsn LIKE " + SQLUtil.toSQL(fsValue + "%") +
////                                                  " AND a.sMakeIDxx = " + SQLUtil.toSQL((String) getMaster("sMakeIDxx")) +
////                                                  " AND a.sModelIDx = " + SQLUtil.toSQL((String) getMaster("sModelIDx")) +
////                                                  " AND a.sTypeIDxx = " + SQLUtil.toSQL((String) getMaster("sTypeIDxx")) 
////                                        )  +      " GROUP BY a.sTransMsn " );
//        String lsSQL = MiscUtil.addCondition( getSQ_SearchVhclTrnsMn(), " a.sMakeIDxx = " + SQLUtil.toSQL((String) getMaster("sMakeIDxx")) +
//                                                  " AND a.sModelIDx = " + SQLUtil.toSQL((String) getMaster("sModelIDx")) +
//                                                  " AND a.sTypeIDxx = " + SQLUtil.toSQL((String) getMaster("sTypeIDxx")) 
//                                            ) ;
//        String lsOrigVal = getMaster(31).toString();
//        String lsNewVal = "";
//        
//        ResultSet loRS;
//        JSONObject loJSON = null;
//        if (!pbWithUI) {   
//            lsSQL = (MiscUtil.addCondition(lsSQL, " a.sTransMsn LIKE " + SQLUtil.toSQL(fsValue + "%") 
//                                            )  +      " GROUP BY a.sTransMsn " );
//            lsSQL += " LIMIT 1";
//            
//            System.out.println(lsSQL);
//            loRS = poGRider.executeQuery(lsSQL);
//            
//            if (loRS.next()){
//                lsNewVal = loRS.getString("sTransMsn");
//                setMaster("sTransMsn", loRS.getString("sTransMsn"));
//            }
//        } else {
//            lsSQL = lsSQL +  " GROUP BY a.sTransMsn " ;
//            System.out.println(lsSQL);
//            loRS = poGRider.executeQuery(lsSQL);
//            //loJSON = showFXDialog.jsonBrowse(poGRider, loRS, "Vehicle Transmission", "sTransMsn");
//            loJSON = showFXDialog.jsonSearch(poGRider, 
//                                             lsSQL,
//                                             "%" + fsValue +"%",
//                                             "Vehicle Transmission", 
//                                             "sTransMsn",
//                                             "a.sTransMsn",
//                                            0);
//            if (loJSON == null){
//            } else {
//                lsNewVal = (String) loJSON.get("sTransMsn");
//                setMaster("sTransMsn", (String) loJSON.get("sTransMsn"));
//            }
//        }  
//        
//        if(!lsNewVal.equals(lsOrigVal)){
//            setMaster("sVhclIDxx", "");
//            setMaster("sColorIDx", "");
//            setMaster("sColorDsc", "");
//            setMaster("nYearModl", "");
//            
//            if (!pbWithUI) {
//                if (!loRS.next()){
//                    psMessage = "No record found.";
//                    setMaster("sTransMsn","");
//                    return false;
//                }
//            } else {
//                if (loJSON == null){
//                    psMessage = "No record found/selected.";
//                    setMaster("sTransMsn","");
//                    return false;
//                }
//            }
//            
//        }      
//        return true;
//    }
//    /**
//     * For searching vehicle color when key is pressed.
//     * @param fsValue the search value for the vehicle color.
//     * @return {@code true} if a matching vehicle color is found, {@code false} otherwise.
//    */
//    public boolean searchVehicleColor(String fsValue) throws SQLException{
////        lsSQL = (MiscUtil.addCondition(lsSQL, " b.sColorDsc LIKE " + SQLUtil.toSQL(fsValue + "%") +
////                                                  " AND a.sMakeIDxx = " + SQLUtil.toSQL((String) getMaster("sMakeIDxx")) +
////                                                  " AND a.sModelIDx = " + SQLUtil.toSQL((String) getMaster("sModelIDx")) +
////                                                  " AND a.sTypeIDxx = " + SQLUtil.toSQL((String) getMaster("sTypeIDxx")) +
////                                                  " AND a.sTransMsn = " + SQLUtil.toSQL((String) getMaster("sTransMsn"))
////                                        )  +      " GROUP BY a.sColorIDx " );
//        String lsSQL = MiscUtil.addCondition( getSQ_SearchVhclColor(), " a.sMakeIDxx = " + SQLUtil.toSQL((String) getMaster("sMakeIDxx")) +
//                                                  " AND a.sModelIDx = " + SQLUtil.toSQL((String) getMaster("sModelIDx")) +
//                                                  " AND a.sTypeIDxx = " + SQLUtil.toSQL((String) getMaster("sTypeIDxx")) +
//                                                  " AND a.sTransMsn = " + SQLUtil.toSQL((String) getMaster("sTransMsn"))
//                                        );
//        
//        String lsOrigVal = getMaster(29).toString();
//        String lsNewVal = "";
//        
//        ResultSet loRS;
//        JSONObject loJSON = null;
//        if (!pbWithUI) {   
//            lsSQL = (MiscUtil.addCondition(lsSQL, " b.sColorDsc LIKE " + SQLUtil.toSQL(fsValue + "%") 
//                                          )  +      " GROUP BY a.sColorIDx " );
//            lsSQL += " LIMIT 1";
//            System.out.println(lsSQL);
//            loRS = poGRider.executeQuery(lsSQL);
//            
//            if (loRS.next()){
//                lsNewVal = loRS.getString("sColorIDx");
//                setMaster("sColorIDx", loRS.getString("sColorIDx"));
//                setMaster("sColorDsc", loRS.getString("sColorDsc"));
//            }
//        } else {
//            lsSQL = lsSQL  +  " GROUP BY a.sColorIDx " ;
//            System.out.println(lsSQL);
//            
//            loRS = poGRider.executeQuery(lsSQL);
//            //loJSON = showFXDialog.jsonBrowse(poGRider, loRS, "Vehicle Color", "sColorDsc");
//            loJSON = showFXDialog.jsonSearch(poGRider, 
//                                             lsSQL,
//                                             "%" + fsValue +"%",
//                                             "Color ID»Vehicle Color", 
//                                             "sColorIDx»sColorDsc",
//                                             "a.sColorIDx»b.sColorDsc",
//                                            1);
//            if (loJSON == null){
//            } else {
//                lsNewVal = (String) loJSON.get("sColorIDx");
//                setMaster("sColorIDx", (String) loJSON.get("sColorIDx"));
//                setMaster("sColorDsc", (String) loJSON.get("sColorDsc"));
//            }
//        }
//        
//        if(!lsNewVal.equals(lsOrigVal)){
//            setMaster("sVhclIDxx", "");
//            setMaster("nYearModl", "");
//            
//            if (!pbWithUI) {
//                if (!loRS.next()){
//                    psMessage = "No record found.";
//                    setMaster("sColorIDx","");
//                    return false;
//                }
//            } else {
//                if (loJSON == null){
//                    psMessage = "No record found/selected.";
//                    setMaster("sColorIDx","");
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//    
//    /**
//     * For searching vehicle year model when key is pressed.
//     * @param fsValue the search value for the vehicle year model.
//     * @return {@code true} if a matching vehicle year model is found, {@code false} otherwise.
//    */
//    public boolean searchVehicleYearMdl(String fsValue) throws SQLException{
//        String lsSQL = MiscUtil.addCondition( getSQ_SearchVhclYearMdl(), " a.sMakeIDxx = " + SQLUtil.toSQL((String) getMaster("sMakeIDxx")) +
//                                                  " AND a.sModelIDx = " + SQLUtil.toSQL((String) getMaster("sModelIDx")) +
//                                                  " AND a.sTypeIDxx = " + SQLUtil.toSQL((String) getMaster("sTypeIDxx")) +
//                                                  " AND a.sColorIDx = " + SQLUtil.toSQL((String) getMaster("sColorIDx")) +
//                                                  " AND a.sTransMsn = " + SQLUtil.toSQL((String) getMaster("sTransMsn"))
//                                        ) ;
//        
////        lsSQL = (MiscUtil.addCondition(lsSQL, " a.nYearModl LIKE " + SQLUtil.toSQL(fsValue + "%") +
////                                                  " AND a.sMakeIDxx = " + SQLUtil.toSQL((String) getMaster("sMakeIDxx")) +
////                                                  " AND a.sModelIDx = " + SQLUtil.toSQL((String) getMaster("sModelIDx")) +
////                                                  " AND a.sTypeIDxx = " + SQLUtil.toSQL((String) getMaster("sTypeIDxx")) +
////                                                  " AND a.sColorIDx = " + SQLUtil.toSQL((String) getMaster("sColorIDx")) +
////                                                  " AND a.sTransMsn = " + SQLUtil.toSQL((String) getMaster("sTransMsn"))
////                                        )  +      " GROUP BY a.nYearModl " );
//        
//        ResultSet loRS;
//        if (!pbWithUI) {   
//            lsSQL = (MiscUtil.addCondition( lsSQL , " AND a.nYearModl LIKE " + SQLUtil.toSQL(fsValue + "%"))
//                    )+  " GROUP BY a.nYearModl " ;
//            lsSQL += " LIMIT 1";
//            
//            System.out.println(lsSQL);
//            loRS = poGRider.executeQuery(lsSQL);
//            
//            if (loRS.next()){
//                setMaster("nYearModl", loRS.getString("nYearModl"));
//                setMaster("sVhclIDxx", loRS.getString("sVhclIDxx"));
//                
//            } else {
//                psMessage = "No record found.";
//                return false;
//            }
//        } else {
//            lsSQL = lsSQL + " GROUP BY a.nYearModl " ;
//            System.out.println(lsSQL);
//            loRS = poGRider.executeQuery(lsSQL);
//            
//            //JSONObject loJSON = showFXDialog.jsonBrowse(poGRider, loRS, "Vehicle Year Model", "nYearModl");
//            JSONObject loJSON = showFXDialog.jsonSearch(poGRider, 
//                                             lsSQL,
//                                             "%" + fsValue +"%",
//                                             "Vehicle Year Model", 
//                                             "nYearModl",
//                                             "a.nYearModl",
//                                            0);
//            if (loJSON == null){
//                psMessage = "No record found/selected.";
//                return false;
//            } else {
//                setMaster("nYearModl", (String) loJSON.get("nYearModl"));
//                setMaster("sVhclIDxx", (String) loJSON.get("sVhclIDxx"));
//            }
//        }        
//        return true;
//    }
//    
//    /**
//     * For searching dealership when key is pressed.
//     * @param fsValue the search value for the dealership.
//     * @return {@code true} if a matching dealership is found, {@code false} otherwise: set only for sDealerNm column.
//    */
//    public boolean searchDealer(String fsValue) throws SQLException{
//        String lsSQL = getSQ_SearchDealer() + " WHERE a.cRecdStat = '1' ";
//        ResultSet loRS;
//        if (!pbWithUI) {   
//            lsSQL += " LIMIT 1";
//            loRS = poGRider.executeQuery(lsSQL);
//            
//            if (loRS.next()){
//                setMaster("sCompnyID", loRS.getString("sClientID")); //sCompnyID
//                setMaster("sDealerNm", loRS.getString("sCompnyNm"));
//            } else {
//                setMaster("sCompnyID", "");
//                setMaster("sDealerNm", fsValue);
//                psMessage = "No record found.";
//                //return false;
//            }
//        } else {
//            loRS = poGRider.executeQuery(lsSQL);
//            JSONObject loJSON = showFXDialog.jsonSearch(poGRider, 
//                                             lsSQL,
//                                             "%" + fsValue +"%",
//                                             "Dealership ID»Dealership", 
//                                             "sClientID»sCompnyNm",
//                                             "sClientID»sCompnyNm",
//                                            1);
//            if (loJSON == null){
//                psMessage = "No record found/selected.";
//                setMaster("sCompnyID", "");
//                setMaster("sDealerNm", fsValue);
//                //return false;
//            } else {
//                setMaster("sCompnyID", (String) loJSON.get("sClientID")); //sCompnyID
//                setMaster("sDealerNm", (String) loJSON.get("sCompnyNm"));
//            }
//        }        
//        return true;
//    }
//    
//    /**
//     * For searching registered place when key is pressed.
//     * @param fsValue the search value for the dealership.
//     * @return {@code true} if a matching registered place is found, {@code false} otherwise: set only for sPlaceReg column.
//    */
//    public boolean searchRegsplace(String fsValue) throws SQLException{
//        String lsSQL = getSQ_Regsplace();
//        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTownName LIKE " + SQLUtil.toSQL(fsValue + "%")
//                                               + " OR b.sProvName LIKE " + SQLUtil.toSQL(fsValue + "%"));
//        ResultSet loRS;
//        if (!pbWithUI) {   
//            lsSQL += " LIMIT 1";
//            loRS = poGRider.executeQuery(lsSQL);
//            
//            if (loRS.next()){
//                setMaster("sPlaceReg", (loRS.getString("sTownName") + " " + loRS.getString("sProvName")));
//            } else {
//                psMessage = "No record found.";
//                return false;
//            }
//        } else {
//            loRS = poGRider.executeQuery(lsSQL);
//            //JSONObject loJSON = showFXDialog.jsonBrowse(poGRider, loRS, "Place of Registration", "sTownName");
//            JSONObject loJSON = showFXDialog.jsonSearch(poGRider, 
//                                             lsSQL,
//                                             "%" + fsValue +"%",
//                                             "Town»Province", 
//                                             "sTownName»sProvName",
//                                             "a.sTownName»b.sProvName",
//                                            0);
//            if (loJSON == null){
//                psMessage = "No record found/selected.";
//                return false;
//            } else {    
//                setMaster("sPlaceReg", ((String) loJSON.get("sTownName") + " " + (String) loJSON.get("sProvName")));
//            }
//        }
//        
//        return true;
//    }
//    
//    private String getSQ_SearchVhclDsc(){
//        return  "SELECT" +  
//                " IFNULL(a.sMakeIDxx,'') sMakeIDxx " +   
//                " , IFNULL(b.sMakeDesc,'') sMakeDesc  " + 
//                " , IFNULL(a.sModelIDx,'') sModelIDx  " + 
//                " , IFNULL(c.sModelDsc,'') sModelDsc  " + 
//                " , IFNULL(a.sTypeIDxx,'') sTypeIDxx  " +
//                " , IFNULL(d.sTypeDesc,'') sTypeDesc  " +  
//                " , IFNULL(a.sColorIDx,'') sColorIDx  " + 
//                " , IFNULL(e.sColorDsc,'') sColorDsc  " + 
//                " , IFNULL(a.sTransMsn,'') sTransMsn  " + 
//                " , IFNULL(a.nYearModl,'') nYearModl  " + 
//                "   FROM vehicle_master a " +
//                "   LEFT JOIN vehicle_make b ON b.sMakeIDxx = a.sMakeIDxx " +
//                "   LEFT JOIN vehicle_model c ON c.sModelIDx = a.sModelIDx " +
//                "   LEFT JOIN vehicle_type d ON d.sTypeIDxx = a.sTypeIDxx " +
//                "   LEFT JOIN vehicle_color e ON e.sColorIDx = a.sColorIDx " ;
//    }
//    
//    private String getSQ_MakeFrame(){
//        return  "SELECT " +
//                " IFNULL(sFrmePtrn,'') sFrmePtrn " +
//                " FROM vehicle_make_frame_pattern ";
//                
//    }
//    
//    private String getSQ_ModelFrame(){
//        return  "SELECT " +
//                " IFNULL(sFrmePtrn,'') sFrmePtrn " +
//                " FROM vehicle_model_frame_pattern ";
//    }
//    
//    private String getSQ_ModelEngine(){
//        return  "SELECT " +
//                " IFNULL(sEngnPtrn,'') sEngnPtrn " +
//                " FROM vehicle_model_engine_pattern ";
//    }
//    
//    private String getSQ_StandardSets(){
//        return  "SELECT " +
//                " IFNULL(sValuexxx,'') sValuexxx " +
//                " FROM xxxstandard_sets ";
//    }
//    
//    private String getSQ_VhchlRegs(){
//        return  "SELECT " +
//                " IFNULL(sSerialID,'') sSerialID " +
//                " FROM vehicle_serial_registration ";
//    }
//    
//    //Validate Engine Frame per Make based on standard sets
//    public boolean vhclExistRegs(){
//        try {
//            String lsSQL = getSQ_VhchlRegs();
//            ResultSet loRS;
//            lsSQL = MiscUtil.addCondition(lsSQL, " sSerialID = " + SQLUtil.toSQL(poVehicle.getString("sSerialID")) );
//            loRS = poGRider.executeQuery(lsSQL);
//            if (MiscUtil.RecordCount(loRS) == 0){
//                MiscUtil.close(loRS);
//                return false;
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(ClientVehicleInfo.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return true;
//    }
    
    
    
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
     * @throws SQLException if a database error occurs.
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
