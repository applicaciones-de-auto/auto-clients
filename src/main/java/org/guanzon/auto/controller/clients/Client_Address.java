/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.clients;

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
import org.guanzon.auto.model.clients.Model_Client_Address;
import org.guanzon.auto.validator.clients.ValidatorFactory;
import org.guanzon.auto.validator.clients.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Client_Address {
    final String ADDRESS_XML = "Model_Client_Address.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psMessagex;
    
    Addresses poAddresses;
    
    Model_Client_Address poModelClientAddress;
    ArrayList<Model_Client_Address> paDetail;
    
    public Client_Address(GRider foAppDrver){
        poAddresses = new Addresses(foAppDrver);
        poGRider = foAppDrver;
    }
    
    public JSONObject poJSON;
    
    public int getEditMode() {
        return pnEditMode;
    }
   
    public Model_Client_Address getDetailModel(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addAddress(String fsClientID){
        //Add Addresses 
        poAddresses.addAddresses();
        
        if(paDetail == null){
            paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.isEmpty()){
            paDetail.add(new Model_Client_Address(poGRider));
            paDetail.get(0).newRecord();
            paDetail.get(0).setClientID(fsClientID);
            poJSON.put("result", "success");
            poJSON.put("message", "Client Address add record.");

        } else {
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Client_Address, paDetail.get(paDetail.size()-1));
            validator.setGRider(poGRider);
            if(!validator.isEntryOkay()){
                poJSON.put("result", "error");
                poJSON.put("message", validator.getMessage());
                return poJSON;
            }
            paDetail.add(new Model_Client_Address(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();
            paDetail.get(paDetail.size()-1).setClientID(fsClientID);
        }
        
        
        //System.out.println("addAddress Client Address Size : " + getAddressList().size());
        //System.out.println("addAddress Addresses Size : " + poAddresses.getAddressesList().size());
        return poJSON;
    }
    
    public JSONObject OpenClientAddress(String fsValue){
        poJSON = new JSONObject();
        String lsSQL =  " SELECT "  +                                                 
                "   sAddrssID   " + //1                                     
                " , sClientID   " + //2                     
                "  FROM client_address   "   ;
        lsSQL = MiscUtil.addCondition(lsSQL, "sClientID = " + SQLUtil.toSQL(fsValue) + " GROUP BY sAddrssID");
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                paDetail = new ArrayList<>();
                poAddresses.resetAddressesList();
                while(loRS.next()){
                        paDetail.add(new Model_Client_Address(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sAddrssID"), loRS.getString("sClientID"));
                        poAddresses.openAddresses(loRS.getString("sAddrssID"));
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                //System.out.println("lnctr = " + lnctr);
                
            }else{
                paDetail = new ArrayList<>();
                addAddress(fsValue);
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", "No record selected.");
            }
            
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        
        //System.out.println("OpenClientAddress Client Address Size : " + getAddressList().size());
        //System.out.println("OpenClientAddress Addresses Size : " + poAddresses.getAddressesList().size());
        
        return poJSON;
    }
    
    public JSONObject saveAddresses(){
        JSONObject obj = new JSONObject();
        if(poAddresses == null){
            return obj;
        }
                     
        if (!pbWtParent) poGRider.beginTrans();
        
        obj = poAddresses.saveAddresses();
        if ("error".equals((String) obj.get("result"))){
                obj.put("message", obj.get("message"));
                return obj;
        }
        if (!pbWtParent) poGRider.commitTrans();
        
        return obj;
    }
    
    
    public JSONObject saveClientAddress(String fsClientID){
        JSONObject obj = new JSONObject();
        int lnSize = paDetail.size() -1;
        for (int lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            if(lnCtr>0){
                if(paDetail.get(lnCtr).getBrgyID().isEmpty() || paDetail.get(lnCtr).getTownID().isEmpty()){
                    paDetail.remove(lnCtr);
                    lnCtr++;
                    if(lnCtr > lnSize){
                        break;
                    }
                }
            }
            
            //set addressID
            checkAddress(paDetail.get(lnCtr).getFullAddress(), lnCtr, false);
            paDetail.get(lnCtr).setClientID(fsClientID);
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Client_Address, paDetail.get(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                obj.put("result", "error");
                obj.put("message", validator.getMessage());
                return obj;
            }
            obj = paDetail.get(lnCtr).saveRecord();
        } 
        
        return obj;
    }
    
    public ArrayList<Model_Client_Address> getAddressList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    
    public void setAddressList(ArrayList<Model_Client_Address> foObj){this.paDetail = foObj;}
    
    public void setAddress(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setAddress(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getAddress(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getAddress(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    public Object removeAddress(int fnRow){
        JSONObject loJSON = new JSONObject();
        if(paDetail.get(fnRow).getEntryBy().isEmpty()){
            poAddresses.removeAddresses(fnRow);
            paDetail.remove(fnRow);
            
            //System.out.println("removeAddress Client Address Size : " + getAddressList().size());
            //System.out.println("removeAddress Addresses Size : " + poAddresses.getAddressesList().size());
        } else {
            loJSON.put("result", "error");
            loJSON.put("message", "You cannot remove Address that already saved, Deactivate it instead.");
            return loJSON;
        }
        return loJSON;
    }
    
    /**
     * Check Existence of Address in general address table
     * @param fsValue The concatenate address description without space
     * @param fnRow The Client Address current row
     * @param fbCheck Check when checking or setting address
     * @return 
     */
    public JSONObject checkAddress(String fsValue, int fnRow, boolean fbCheck){
        fsValue = fsValue.replace(" ", "").toUpperCase();
        JSONObject obj = new JSONObject();
        try {
            String lsSQL =  "SELECT  " +                                              
                "   IFNULL(a.sAddrssID, '') sAddrssID" + //1                                   
                ",  IFNULL(a.sHouseNox, '') sHouseNox" + //2                                   
                ",  IFNULL(a.sAddressx, '') sAddressx" + //3                                   
                ",  IFNULL(a.sTownIDxx, '') sTownIDxx" + //4                                   
                ",  IFNULL(a.sZippCode, '') sZippCode" + //5                                   
                ",  IFNULL(a.sBrgyIDxx, '') sBrgyIDxx" + //6                                   
                ",  a.nLatitude " + //7                                   
                ",  a.nLongitud " + //8                                   
                ",  IFNULL(a.sRemarksx, '') sRemarksx" + //9                                   
                ",  IFNULL(a.sModified, '') sModified" + //10                                  
                ",  a.dModified " + //11                       
                ", IFNULL(d.sProvName, '') sProvName " + //12             
                ", IFNULL(c.sBrgyName, '') sBrgyName " + //13             
                ", IFNULL(b.sTownName, '') sTownName " + //14             
                ", IFNULL(d.sProvIDxx, '') sProvIDxx " + //15              
                ", REPLACE(CONCAT(IFNULL(a.sHouseNox,''), IFNULL(a.sAddressx,''),IFNULL(c.sBrgyName,''), IFNULL(b.sTownName,''), IFNULL(d.sProvName,'')), ' ', '') AS trimAddress" + //16
                " FROM addresses a                    " +                  
                " LEFT JOIN TownCity b ON b.sTownIDxx = a.sTownIDxx " +   
                " LEFT JOIN Barangay c ON c.sBrgyIDxx = a.sBrgyIDxx " +   
                " LEFT JOIN Province d ON d.sProvIDxx = b.sProvIDxx ";
            lsSQL = MiscUtil.addCondition(lsSQL, " UPPER(REPLACE(CONCAT(IFNULL(a.sHouseNox,''), IFNULL(a.sAddressx,''),IFNULL(c.sBrgyName,''), IFNULL(b.sTownName,''), IFNULL(d.sProvName,'')), ' ', '')) = " + SQLUtil.toSQL(fsValue)); //" CONCAT_WS(a.sHouseNox, REPLACE(a.sAddressx, ' ', ''),REPLACE(c.sBrgyName, ' ', ''), REPLACE(b.sTownName, ' ', ''), REPLACE(d.sProvName, ' ', '')) = "
            //System.out.println(lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);

            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                    //System.out.println("Address ID >>> " + loRS.getString("sAddrssID"));
                    if(!fbCheck){
                        paDetail.get(fnRow).setAddressID(loRS.getString("sAddrssID"));
                        poAddresses.setAddresses(poAddresses.getAddressesList().size()-1, "sRemarksx", paDetail.get(poAddresses.getAddressesList().size()-1).getRemarks());
                    } else {
                        poAddresses.removeAddresses(fnRow);
                        poAddresses.openAddresses(loRS.getString("sAddrssID"));
                        poAddresses.setAddresses(poAddresses.getAddressesList().size()-1, "sRemarksx", paDetail.get(poAddresses.getAddressesList().size()-1).getRemarks());
                        obj.put("result", "confirm");
                        obj.put("message", "Existing Address Information found. You want to link this Address?");
                        return obj;
                    }
                }
                } else {
                int lnSize = poAddresses.getAddressesList().size()-1;
                if(lnSize >= 0){
                    poAddresses.setAddresses(lnSize, "sHouseNox", paDetail.get(lnSize).getHouseNo());
                    poAddresses.setAddresses(lnSize, "sAddressx", paDetail.get(lnSize).getAddress());
                    poAddresses.setAddresses(lnSize, "sBrgyIDxx", paDetail.get(lnSize).getBrgyID());
                    poAddresses.setAddresses(lnSize, "sTownIDxx", paDetail.get(lnSize).getTownID());
                    poAddresses.setAddresses(lnSize, "sZippCode", paDetail.get(lnSize).getZippCode());
                    poAddresses.setAddresses(lnSize, "sRemarksx", paDetail.get(lnSize).getRemarks());
                }
                
                obj.put("result", "success");
                obj.put("message", "Added to Addresses");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Client_Address.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return obj;
    }
    
    /**
     * Check for Address that already linked thru other customer
     * @param fsAddressID The Address ID
     * @param fsValue The concatenated address description
     * @param fsClientID The current ClientID
     * @param fnRow The client address row
     * @return 
     */
    public JSONObject checkClientAddress(String fsAddressID,String fsValue, String fsClientID, int fnRow){
        JSONObject obj = new JSONObject();
        try {
            String lsSQL =  " SELECT   "                                                                                   
                + "   IFNULL(a.sAddrssID, '')  sAddrssID   " //1                                                
                + " , IFNULL(a.sClientID, '')  sClientID   " //2                                                
                + " , IFNULL(b.sHouseNox, '')  sHouseNox   " //3                                                
                + " , IFNULL(b.sAddressx, '')  sAddressx   " //4                                                
                + " , IFNULL(b.sTownIDxx, '')  sTownIDxx   " //5                                                
                + " , IFNULL(b.sBrgyIDxx, '')  sBrgyIDxx   " //6                                                
                + " , IFNULL(b.sZippCode, '')  sZippCode   " //7                                                
                + " , b.nLatitude     "                      //8                                                
                + " , b.nLongitud     "                      //9                                                
                + " , IFNULL(b.sRemarksx, '')  sRemarksx   " //10                                               
                + " , IFNULL(a.cOfficexx, '')  cOfficexx   " //11                                               
                + " , IFNULL(a.cProvince, '')  cProvince   " //12                                               
                + " , IFNULL(a.cPrimaryx, '')  cPrimaryx   " //13                                               
                + " , IFNULL(a.cBillingx, '')  cBillingx   " //14                                               
                + " , IFNULL(a.cShipping, '')  cShipping   " //15                                               
                + " , IFNULL(a.cCurrentx, '')  cCurrentx   " //16                                               
                + " , IFNULL(a.cRecdStat, '')  cRecdStat   " //17                                               
                + " , IFNULL(a.sEntryByx, '')  sEntryByx   " //18                                               
                + " , a.dEntryDte     "                      //19                                               
                + " , IFNULL(a.sModified, '')  sModified   " //20                                               
                + " , a.dModified     "                      //21                                               
                + " , IFNULL(e.sProvName, '')  sProvName   " //22                                               
                + " , IFNULL(d.sBrgyName, '')  sBrgyName   " //23                                               
                + " , IFNULL(c.sTownName, '')  sTownName   " //24                                               
                + " , IFNULL(e.sProvIDxx, '')  sProvIDxx   " //25                                               
                + "  FROM client_address a                 "                                                    
                + "  INNER JOIN addresses b ON b.sAddrssID = a.sAddrssID "                                     
                + "  LEFT JOIN TownCity c ON c.sTownIDxx = b.sTownIDxx   "                                     
                + "  LEFT JOIN Barangay d ON d.sBrgyIDxx = b.sBrgyIDxx AND d.sTownIDxx = b.sTownIDxx   "       
                + "  LEFT JOIN Province e ON e.sProvIDxx = c.sProvIDxx   " ;
                    
            lsSQL = MiscUtil.addCondition(lsSQL, " a.sAddrssID = " + SQLUtil.toSQL(fsAddressID) +
                                                                            " AND a.sClientID <> " + SQLUtil.toSQL(fsClientID)); 
            //System.out.println(lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            
            if (MiscUtil.RecordCount(loRS) > 0) {
                if (loRS.next()) {
                    String lsConcatAddresses = loRS.getString("sHouseNox") + loRS.getString("sAddressx") + loRS.getString("sBrgyName") + loRS.getString("sTownName") + loRS.getString("sProvName");
                    lsConcatAddresses = lsConcatAddresses.replace(" ", "").toUpperCase();

                    if(!fsValue.equals(lsConcatAddresses)){
                        obj.put("result", "confirm");
                        obj.put("message", "Existing record with the same Address Information found. You want to update Address?");
                        return obj;
                    }
                }
            }
        } catch (SQLException ex) { 
            Logger.getLogger(Client_Address.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return obj;
    }
    
    public JSONObject updateAddresses(int fnRow){
        JSONObject loJSON = new JSONObject();
        poAddresses.setAddresses(fnRow, "sHouseNox", paDetail.get(fnRow).getHouseNo());
        poAddresses.setAddresses(fnRow, "sAddressx", paDetail.get(fnRow).getAddress());
        poAddresses.setAddresses(fnRow, "sBrgyIDxx", paDetail.get(fnRow).getBrgyID());
        poAddresses.setAddresses(fnRow, "sTownIDxx", paDetail.get(fnRow).getTownID());
        poAddresses.setAddresses(fnRow, "sZippCode", paDetail.get(fnRow).getZippCode());
        poAddresses.setAddresses(fnRow, "sRemarksx", paDetail.get(fnRow).getRemarks());
         
        loJSON.put("result", "success");
        loJSON.put("message", "Update Addresses success.");
        
        return loJSON;
    }
    
    public JSONObject searchBarangay(String fsValue, int fnRow, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getTownID()== null){
            loJSON.put("result", "error");
            loJSON.put("message", "Town cannot be empty.");
            return loJSON;
        } else {
            if(paDetail.get(fnRow).getTownID().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Town cannot be empty.");
                return loJSON;
            }
        }
        
        if (fbByCode){
            if (fsValue.equals((String) paDetail.get(fnRow).getBrgyID())) {
                loJSON.put("result", "success");
                loJSON.put("message", "Search barangay success.");
                return loJSON;
            }
        }else{
            if(paDetail.get(fnRow).getBrgyID()!= null && !paDetail.get(fnRow).getBrgyID().toString().trim().isEmpty()){
                if (!paDetail.get(fnRow).getBrgyName().isEmpty()){
                    if (fsValue.equals(paDetail.get(fnRow).getBrgyName())){
                        loJSON.put("result", "success");
                        loJSON.put("message", "Search barangay success.");
                        return loJSON;
                    }
                }
            }
        }
        
       String lsSQL = "SELECT " +
                            "  a.sBrgyIDxx" +
                            ", a.sBrgyName" +
                            ", b.sTownName" + 
                            ", b.sZippCode" +
                            ", c.sProvName" + 
                            ", c.sProvIDxx" +
                            ", b.sTownIDxx" +
                        " FROM Barangay a" + 
                            ", TownCity b" +
                            ", Province c" +
                        " WHERE a.sTownIDxx = b.sTownIDxx" + 
                            " AND b.sProvIDxx = c.sProvIDxx" + 
                            " AND a.cRecdStat = '1'" + 
                            " AND b.cRecdStat = '1'" + 
                            " AND c.cRecdStat = '1'" + 
                            " AND a.sTownIDxx = " + SQLUtil.toSQL(paDetail.get(fnRow).getTownID());
        
        if (fbByCode){
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sBrgyIDxx = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sBrgyName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "ID»Barangay»Town»Province", 
                            "sBrgyIDxx»sBrgyName»sTownName»sProvName",
                            "sBrgyIDxx»sBrgyName»sTownName»sProvName",
                            fbByCode ? 0 : 1);
            
            if (loJSON != null) {
                if("error".equals(loJSON.get("result"))){
                    paDetail.get(fnRow).setBrgyID("");
                    paDetail.get(fnRow).setBrgyName("");
                } else {
                    paDetail.get(fnRow).setBrgyID((String) loJSON.get("sBrgyIDxx"));
                    paDetail.get(fnRow).setBrgyName((String) loJSON.get("sBrgyName"));
                }
            }else {
                paDetail.get(fnRow).setBrgyID("");
                paDetail.get(fnRow).setBrgyName("");
                loJSON  = new JSONObject();  
                loJSON.put("result", "error");
                loJSON.put("message", "No record selected.");
                return loJSON;
            }
        
            
        return loJSON;
    }
    
    public JSONObject searchTown(String fsValue, int fnRow, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getProvID()== null){
            loJSON.put("result", "error");
            loJSON.put("message", "Province cannot be empty.");
            return loJSON;
        } else {
            if(paDetail.get(fnRow).getProvID().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Province cannot be empty.");
                return loJSON;
            }
        }
        
        if (fbByCode){
            if (fsValue.equals((String) paDetail.get(fnRow).getTownID())) {
                loJSON = new JSONObject();
                loJSON.put("result", "success");
                loJSON.put("message", "Search town success.");
                return loJSON;
            }
        }else{
            
            String townProvince = String.valueOf(paDetail.get(fnRow).getValue("sTownName"));
            //System.out.println("fsValue = " + fsValue);
            //System.out.println("town = " + townProvince);
            if(!townProvince.isEmpty()){
                if (fsValue.equals(townProvince)){
                    loJSON = new JSONObject();
                    loJSON.put("result", "success");
                    loJSON.put("message", "Search town success.");
                    return loJSON;
                }
            }
        }
        
       String lsSQL = "SELECT " +
                            "  a.sTownIDxx" +
                            ", a.sTownName" + 
                            ", a.sZippCode" +
                            ", b.sProvName" + 
                            ", b.sProvIDxx" +
                        " FROM TownCity a" +
                            ", Province b" +
                        " WHERE a.sProvIDxx = b.sProvIDxx" + 
                            " AND a.cRecdStat = '1'";
        
        if (fbByCode) {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sProvIDxx = "  + SQLUtil.toSQL(paDetail.get(fnRow).getProvID()) + " AND a.sTownIDxx = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sProvIDxx = "  + SQLUtil.toSQL(paDetail.get(fnRow).getProvID()) + " AND a.sTownName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "ID»Town»Postal Code»Province", 
                            "sTownIDxx»sTownName»sZippCode»sProvName", 
                            "a.sTownIDxx»a.sTownName»a.sZippCode»b.sProvName", 
                            fbByCode ? 0 : 1);
            
            if (loJSON != null) {
                if("error".equals(loJSON.get("result"))){
                    paDetail.get(fnRow).setTownID("");
                    paDetail.get(fnRow).setTownName("");
                    paDetail.get(fnRow).setZippCode("");
                    paDetail.get(fnRow).setBrgyID("");
                    paDetail.get(fnRow).setBrgyName("");
                } else {
                    paDetail.get(fnRow).setTownID((String) loJSON.get("sTownIDxx"));
                    paDetail.get(fnRow).setTownName((String) loJSON.get("sTownName"));
                    paDetail.get(fnRow).setZippCode((String) loJSON.get("sZippCode"));
                    paDetail.get(fnRow).setBrgyID("");
                    paDetail.get(fnRow).setBrgyName("");
                }
            }else {
                paDetail.get(fnRow).setTownID("");
                paDetail.get(fnRow).setTownName("");
                paDetail.get(fnRow).setZippCode("");
                paDetail.get(fnRow).setBrgyID("");
                paDetail.get(fnRow).setBrgyName("");
                loJSON  = new JSONObject();  
                loJSON.put("result", "error");
                loJSON.put("message", "No record selected.");
                return loJSON;
            }
            
        return loJSON;
    }
    
    public JSONObject searchProvince(String fsValue, int fnRow, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        if (fbByCode){
            if (fsValue.equals((String) paDetail.get(fnRow).getProvID())) {
                loJSON.put("result", "success");
                loJSON.put("message", "Search province success.");
                return loJSON;
            }
        }else{
            String lsProvince = String.valueOf(paDetail.get(fnRow).getValue("sProvName"));
            
            if(!lsProvince.isEmpty()){
                if (fsValue.equals(lsProvince)){
                    loJSON.put("result", "success");
                    loJSON.put("message", "Search province success.");
                    return loJSON;
                }
            }
        }
        
       String lsSQL = " SELECT "
                    + " sProvName "
                    + ", sProvIDxx "
                    + " FROM Province  " 
                    + " WHERE cRecdStat = '1'";
        
        if (fbByCode) {
            lsSQL = MiscUtil.addCondition(lsSQL, "sProvIDxx = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, "sProvName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "ID»Province", 
                            "sProvIDxx»sProvName", 
                            "sProvIDxx»sProvName", 
                            fbByCode ? 0 : 1);
            
        if (loJSON != null) {
            if("error".equals(loJSON.get("result"))){
                paDetail.get(fnRow).setProvID("");
                paDetail.get(fnRow).setProvName("");
                paDetail.get(fnRow).setTownID("");
                paDetail.get(fnRow).setTownName("");
                paDetail.get(fnRow).setBrgyID("");
                paDetail.get(fnRow).setBrgyName("");
            } else {
                paDetail.get(fnRow).setProvID((String) loJSON.get("sProvIDxx"));
                paDetail.get(fnRow).setProvName((String) loJSON.get("sProvName"));
                paDetail.get(fnRow).setTownID("");
                paDetail.get(fnRow).setTownName("");
                paDetail.get(fnRow).setBrgyID("");
                paDetail.get(fnRow).setBrgyName("");
            }
        }else {
            paDetail.get(fnRow).setProvID("");
            paDetail.get(fnRow).setProvName("");
            paDetail.get(fnRow).setTownID("");
            paDetail.get(fnRow).setTownName("");
            paDetail.get(fnRow).setBrgyID("");
            paDetail.get(fnRow).setBrgyName("");
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No record selected.");
            return loJSON;
        }
        
        return loJSON;
    }
    
}
