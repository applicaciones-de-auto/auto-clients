/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.clients;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.controller.clients.Client_Address;
import org.guanzon.auto.controller.clients.Client_Email;
import org.guanzon.auto.controller.clients.Client_Master;
import org.guanzon.auto.controller.clients.Client_Mobile;
import org.guanzon.auto.controller.clients.Client_Social_Media;
import org.guanzon.auto.validator.clients.ValidatorFactory;
import org.guanzon.auto.validator.clients.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Client implements GRecord{
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    String psClientType = "0";
    
    Client_Master poClient;
    
    Client_Address poAddress;
    Client_Email poEmail;
    Client_Mobile poMobile;
    Client_Social_Media poSocMed;
    
    public JSONObject poJSON;
    
    public Client(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poClient = new Client_Master(foAppDrver,fbWtParent,fsBranchCd );
        poAddress = new Client_Address(foAppDrver);
        poMobile = new Client_Mobile(foAppDrver);
        poEmail = new Client_Email(foAppDrver);
        poSocMed = new Client_Social_Media(foAppDrver);
        
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
        obj = poClient.setMaster(fnCol, foData);
        return obj;
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poClient.setMaster(fsCol, foData);
    }

    @Override
    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poClient.getMaster(fnCol);
    }

    @Override
    public Object getMaster(String fsCol) {
        return poClient.getMaster(fsCol);
    }

    @Override
    public JSONObject newRecord() {
        poJSON = new JSONObject();
        try{
            pnEditMode = EditMode.ADDNEW;
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

    @Override
    public JSONObject openRecord(String fsValue) {
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        
        poJSON = poClient.openRecord(fsValue);
        
        poJSON = checkData(poAddress.OpenClientAddress(fsValue));
        if(!"success".equals(poJSON.get("result"))){
            pnEditMode = EditMode.UNKNOWN;
        }
        poJSON = checkData(poMobile.OpenClientMobile(fsValue));
        if(!"success".equals(poJSON.get("result"))){
            pnEditMode = EditMode.UNKNOWN;
        }
        poJSON = checkData(poEmail.OpenClientEMail(fsValue));
        if(!"success".equals(poJSON.get("result"))){
            pnEditMode = EditMode.UNKNOWN;
        }
        poJSON = checkData(poSocMed.OpenClientSocialAccount(fsValue));
        if(!"success".equals(poJSON.get("result"))){
            pnEditMode = EditMode.UNKNOWN;
        } 
        
        return poJSON;
    }

    @Override
    public JSONObject updateRecord() {
        poJSON = new JSONObject();  
        poJSON = poClient.updateRecord();
        pnEditMode = poClient.getEditMode();
        return poJSON;
    }

    @Override
    public JSONObject saveRecord() {
        poJSON = new JSONObject();  
        
        poJSON = validateEntry();
        if("error".equalsIgnoreCase((String)poJSON.get("result"))){
            return poJSON;
        }
        
        //Save Addresses
        poJSON = poAddress.saveAddresses();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON =  poClient.saveRecord();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON = poAddress.saveClientAddress(poClient.getModel().getClientID());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
      
        poJSON =  poMobile.saveMobile(poClient.getModel().getClientID());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON =  poEmail.saveEmail(poClient.getModel().getClientID());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON =  poSocMed.saveSocialAccount(poClient.getModel().getClientID());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        if (!pbWtParent) poGRider.commitTrans();
        return poJSON;
    }
    
    private JSONObject validateEntry(){
        JSONObject obj = new JSONObject();
        
        ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Client_Master, poClient.getModel());
        validator.setGRider(poGRider);
        if (!validator.isEntryOkay()){
            poJSON.put("result", "error");
            poJSON.put("message", validator.getMessage());
            return poJSON;
        }
        
        //VALIDATE : Client Address
        if(poAddress.getAddressList() == null){
            obj.put("result", "error");
            obj.put("message", "No address detected. Please encode client address.");
            return obj;
        }
        
        int lnSize = poAddress.getAddressList().size() -1;
        //Save Client Address
        if (lnSize < 0){
            obj.put("result", "error");
            obj.put("message", "No address detected. Please encode client address.");
            return obj;
        }
        
        //Do not Allow to save CLIENT Info if there's no Primary Address 
        int lnCntp = 0;
        for (int lnCtr = 0; lnCtr <= lnSize; lnCtr++) {
            if ((String.valueOf(poAddress.getAddress(lnCtr,"cPrimaryx"))).equals("1")) {
                lnCntp += 1;
            }
        }
        if (lnCntp <= 0) {
            obj.put("result", "error");
            obj.put("message", "Please Add Primary Address.");
            return obj;
        }
        
        //VALIDATE : Client Mobile
        obj = new JSONObject(); lnSize = 0; lnCntp = 0;
        
        if(poMobile.getMobileList()== null){
            obj.put("result", "error");
            obj.put("message", "No mobile number detected. Please encode mobile number.");
            return obj;
        }
        
        lnSize = poMobile.getMobileList().size() -1;
        if (lnSize < 0){
            obj.put("result", "error");
            obj.put("message", "No mobile number detected. Please encode mobile number.");
            return obj;
        }
        
        for (int lnCtr = 0; lnCtr <= lnSize; lnCtr++) {
            if ((String.valueOf( poMobile.getMobile(lnCtr,"cPrimaryx"))).equals("1")) {
                lnCntp += 1;
            }
        }
        if (lnCntp <= 0) {
            obj.put("result", "error");
            obj.put("message", "Please Add Primary Contact Number.");
            return obj;
        }
        
        //VALIDATE : Client Mobile based on client type
        try {
            String lsCompnyNm = "";
            String lsClientID = "";
            String lsSQL = "";

            for (int lnCtr = 0; lnCtr <= lnSize; lnCtr++) {
                lsSQL = "SELECT " +
                            "  a.sClientID " +
                            ", a.sCompnyNm " +
                            ", a.cClientTp " +
                            ", b.sMobileID " +
                            ", b.sMobileNo " +
                            "FROM client_master a " +
                            "LEFT JOIN client_mobile b ON b.sClientID = a.sClientID " ;
                lsSQL = MiscUtil.addCondition(lsSQL, "b.sMobileNo = " + SQLUtil.toSQL(poMobile.getDetailModel(lnCtr).getMobileNo())) +
                                                        " AND b.sMobileID <> " + SQLUtil.toSQL(poMobile.getDetailModel(lnCtr).getMobileID()) +
                                                        " AND a.cClientTp = " + SQLUtil.toSQL(poClient.getModel().getClientTp()) ;

                System.out.println("EXISTING CONTACT NUMBER WITH THE SAME CLIENT TYPE CHECK: " + lsSQL);
                ResultSet loRS = poGRider.executeQuery(lsSQL);
                if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsCompnyNm = loRS.getString("sCompnyNm");
                            lsClientID = loRS.getString("sClientID");
                        }

                        MiscUtil.close(loRS);
                        obj.put("result", "error");
                        obj.put("message", "Existing Contact Number : "+poMobile.getDetailModel(lnCtr).getMobileNo()+" for.\n\nID: " + lsClientID + "\nName: " + lsCompnyNm.toUpperCase() );
                        return obj;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        obj = validateDetail();
        if("error".equals((String) obj.get("result"))){
            return obj;
        }
        
        return obj;
    }
    
    /**
     * Validate all detail
     * @return 
     */
    private JSONObject validateDetail(){
        JSONObject loJSON = new JSONObject();
        ValidatorInterface validator;
        int lnSize = 0;
        
        lnSize = poAddress.getAddressList().size() -1;
        for (int lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            poAddress.checkAddress(poAddress.getDetailModel(lnCtr).getFullAddress(), lnCtr, false);
            poAddress.getDetailModel(lnCtr).setClientID(poClient.getModel().getClientID());
            validator = ValidatorFactory.make(ValidatorFactory.TYPE.Client_Address, poAddress.getDetailModel(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                loJSON.put("result", "error");
                loJSON.put("message", validator.getMessage());
                return loJSON;
            }
        }
        
        lnSize = poMobile.getMobileList().size() -1;
        for (int lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            poMobile.getDetailModel(lnCtr).setClientID(poClient.getModel().getClientID());
            validator = ValidatorFactory.make(ValidatorFactory.TYPE.Client_Mobile, poMobile.getDetailModel(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                loJSON.put("result", "error");
                loJSON.put("message", validator.getMessage());
                return loJSON;
            }
        }
        
        lnSize = poEmail.getEmailList().size() -1;
        for (int lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            poEmail.getDetailModel(lnCtr).setClientID(poClient.getModel().getClientID());
            validator = ValidatorFactory.make(ValidatorFactory.TYPE.Client_Email, poEmail.getDetailModel(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                loJSON.put("result", "error");
                loJSON.put("message", validator.getMessage());
                return loJSON;
            }
        }
        
        lnSize = poSocMed.getSocialMediaList().size() -1;
        for (int lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            poSocMed.getDetailModel(lnCtr).setClientID(poClient.getModel().getClientID());
            validator = ValidatorFactory.make(  ValidatorFactory.TYPE.Client_Social_Media, poSocMed.getDetailModel(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                loJSON.put("result", "error");
                loJSON.put("message", validator.getMessage());
                return loJSON;
            }
        }
        
        return loJSON;
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
        poJSON = new JSONObject();  
        poJSON = poClient.searchRecord(fsValue, fbByCode);
        if(!"error".equals(poJSON.get("result"))){
            poJSON = openRecord((String) poJSON.get("sClientID"));
        }
        return poJSON;
    }

    public JSONObject searchClient(String fsValue, boolean fbByCode) {
        poJSON = new JSONObject();  
        poJSON = poClient.searchClient(fsValue, fbByCode);
        if(!"error".equals(poJSON.get("result"))){
            poJSON = openRecord((String) poJSON.get("sClientID"));
            if(!"error".equals(poJSON.get("result"))){
                poJSON = updateRecord();
            }
            
        }
        return poJSON;
    }

    @Override
    public Client_Master getModel() {
        return poClient;
    }
    
    private JSONObject checkData(JSONObject joValue){
        if(pnEditMode == EditMode.ADDNEW ||pnEditMode == EditMode.READY || pnEditMode == EditMode.UPDATE){
            if(joValue.containsKey("continue")){
                if(true == (boolean)joValue.get("continue")){
                    joValue.put("result", "success");
                    joValue.put("message", "Record saved successfully.");
                }
            }
        }
        return joValue;
    }
    
//    public JSONObject loadJSONFile(){
//        poJSON = poClient.loadJSONFile();
//        pnEditMode = poClient.getEditMode();
//        
//        return poJSON;
//    }
    
    public Client_Address getAddressModel(){ return poAddress;}
    public ArrayList getAddressList(){return poAddress.getAddressList();}
    public void setAddressList(ArrayList foObj){this.poAddress.setAddressList(foObj);}
    
    public Client_Mobile getMobileModel(){ return poMobile;}
    public ArrayList getMobileList(){return poMobile.getMobileList();}
    public void setMobileList(ArrayList foObj){this.poMobile.setMobileList(foObj);}
    
    public Client_Email getEmailModel(){ return poEmail;}
    public ArrayList getEmailList(){return poEmail.getEmailList();}
    public void setEmailList(ArrayList foObj){this.poEmail.setEmailList(foObj);}
    
    public Client_Social_Media getSocMedModel(){ return poSocMed;}
    public ArrayList getSocialMediaList(){return poSocMed.getSocialMediaList();}
    public void setSocialMediaList(ArrayList foObj){this.poSocMed.setSocialMediaList(foObj);}
    
    public void setAddress(int fnRow, int fnIndex, Object foValue){ poAddress.setAddress(fnRow, fnIndex, foValue);}
    public void setAddress(int fnRow, String fsIndex, Object foValue){ poAddress.setAddress(fnRow, fsIndex, foValue);}
    public Object getAddress(int fnRow, int fnIndex){return poAddress.getAddress(fnRow, fnIndex);}
    public Object getAddress(int fnRow, String fsIndex){return poAddress.getAddress(fnRow, fsIndex);}
    
    public Object addAddress(){ return poAddress.addAddress(poClient.getModel().getClientID());}
    public Object removeAddress(int fnRow){ return poAddress.removeAddress(fnRow);}
    
    public void setMobile(int fnRow, int fnIndex, Object foValue){ poMobile.setMobile(fnRow, fnIndex, foValue);}
    public void setMobile(int fnRow, String fsIndex, Object foValue){ poMobile.setMobile(fnRow, fsIndex, foValue);}
    public Object getMobile(int fnRow, int fnIndex){return poMobile.getMobile(fnRow, fnIndex);}
    public Object getMobile(int fnRow, String fsIndex){return poMobile.getMobile(fnRow, fsIndex);}
    
    public Object addMobile(){ return poMobile.addContact(poClient.getModel().getClientID());}
    public Object removeMobile(int fnRow){ return poMobile.removeMobile(fnRow);}
    
    public void setEmail(int fnRow, int fnIndex, Object foValue){ poEmail.setEmail(fnRow, fnIndex, foValue);}
    public void setEmail(int fnRow, String fsIndex, Object foValue){ poEmail.setEmail(fnRow, fsIndex, foValue);}
    public Object getEmail(int fnRow, int fnIndex){return poEmail.getEmail(fnRow, fnIndex);}
    public Object getEmail(int fnRow, String fsIndex){return poEmail.getEmail(fnRow, fsIndex);}
    
    public Object addEmail(){ return poEmail.addEmail(poClient.getModel().getClientID());}
    public Object removeEmail(int fnRow){ return poEmail.removeEmail(fnRow);}
    
    public void setSocialMed(int fnRow, int fnIndex, Object foValue){ poSocMed.setSocialMed(fnRow, fnIndex, foValue);}
    public void setSocialMed(int fnRow, String fsIndex, Object foValue){ poSocMed.setSocialMed(fnRow, fsIndex, foValue);}
    public Object getSocialMed(int fnRow, int fnIndex){return poSocMed.getSocialMed(fnRow, fnIndex);}
    public Object getSocialMed(int fnRow, String fsIndex){return poSocMed.getSocialMed(fnRow, fsIndex);}
    
    public Object addSocialMed(){ return poSocMed.addSocialMedia(poClient.getModel().getClientID());}
    public Object removeSocialMed(int fnRow){ return poSocMed.removeSocialMed(fnRow);}
    
    
    /**
     * Check Existence of Address in general address table
     * @param fsValue The concatenate address description without space
     * @param fnRow The Client Address current row
     * @param fbCheck Check when checking or setting address
     * @return 
     */
    public JSONObject checkClientAddress(String fsValue, int fnRow, boolean fbCheck){
        return poAddress.checkAddress(fsValue, fnRow, fbCheck);
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
        return poAddress.checkClientAddress(fsAddressID, fsValue, fsClientID, fnRow);
    }
    
    /**
     * Update Addresses
     * @param fnRow Current Address Row
     * @return 
     */
    public JSONObject updateAddresses(int fnRow){
        return poAddress.updateAddresses(fnRow);
    }
    
    /**
     * Search Barangay
     * @param fsValue searching for value
     * @param fnRow current row to be set
     * @param fbByCode set fbByCode into TRUE if you're searching brgy by CODE, otherwise set FALSE.
     * @return 
     */
    public JSONObject searchBarangay(String fsValue, int fnRow, boolean fbByCode){
        return poAddress.searchBarangay(fsValue, fnRow, fbByCode);
    }
    
    /**
     * Search Town
     * @param fsValue searching for value
     * @param fnRow current row to be set
     * @param fbByCode set fbByCode into TRUE if you're searching Town by CODE, otherwise set FALSE.
     * @return 
     */
    public JSONObject searchTown(String fsValue, int fnRow, boolean fbByCode){
        return poAddress.searchTown(fsValue, fnRow, fbByCode);
    }
    
    /**
     * Search Province
     * @param fsValue searching for value
     * @param fnRow current row to be set
     * @param fbByCode set fbByCode into TRUE if you're searching Province by CODE, otherwise set FALSE.
     * @return 
     */
    public JSONObject searchProvince(String fsValue, int fnRow, boolean fbByCode){
        return poAddress.searchProvince(fsValue, fnRow, fbByCode);
    }
    
    /**
     * Search Spouse
     * @param fsValue searching for value
     * @return 
     */
    public JSONObject searchSpouse(String fsValue){
        return poClient.searchSpouse(fsValue);
    }
    
    /**
     * Search CitizenShip
     * @param fsValue searching for value
     * @return 
     */
    public JSONObject searchCitizenShip(String fsValue){
        return poClient.searchCitizenShip(fsValue);
    }
    
    /**
     * Search Birth Place
     * @param fsValue searching for value
     * @return 
     */
    public JSONObject searchBirthPlc(String fsValue){
        return poClient.searchBirthPlc(fsValue);
    }
    
    /**
     * Check Existing Client Record
     * @param fbIsClient set TRUE when client type is Client else FALSE when Company
     * @return 
     */
    public JSONObject validateExistingClientInfo(boolean fbIsClient){
        return poClient.validateExistingClientInfo(fbIsClient);
    }
}
