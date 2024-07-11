/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.clients;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.auto.model.clients.Model_Client_Email;
import org.guanzon.auto.validator.clients.ValidatorFactory;
import org.guanzon.auto.validator.clients.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Client_Email {
    final String MOBILE_XML = "Model_Client_Email.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_Client_Email> paMail;
    
    public Client_Email(GRider foAppDrver){
        poGRider = foAppDrver;
    }

    public int getEditMode() {
        return pnEditMode;
    }
    
    public Model_Client_Email getEMail(int fnIndex){
        if (fnIndex > paMail.size() - 1 || fnIndex < 0) return null;
        
        return paMail.get(fnIndex);
    }
    
    public JSONObject addEmail(String fsClientID){
        
        if(paMail == null){
           paMail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paMail.size()<=0){
            paMail.add(new Model_Client_Email(poGRider));
            paMail.get(0).newRecord();
            paMail.get(0).setValue("sClientID", fsClientID);
            poJSON.put("result", "success");
            poJSON.put("message", "Email address add record.");
        } else {
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Client_Email, paMail.get(paMail.size()-1));
            validator.setGRider(poGRider);
            if(!validator.isEntryOkay()){
                poJSON.put("result", "error");
                poJSON.put("message", validator.getMessage());
                return poJSON;
            }
            paMail.add(new Model_Client_Email(poGRider));
            paMail.get(paMail.size()-1).newRecord();

            paMail.get(paMail.size()-1).setClientID(fsClientID);
            
            poJSON.put("result", "success");
            poJSON.put("message", "Email address add record.");
        }
        return poJSON;
    }
    
    public JSONObject OpenClientEMail(String fsValue){
        paMail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL = "SELECT" +
                    "  sEmailIDx" +
                    ", sClientID" +
                        " FROM Client_eMail_Address" ;
        lsSQL = MiscUtil.addCondition(lsSQL, "sClientID = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paMail.add(new Model_Client_Email(poGRider));
                        paMail.get(paMail.size() - 1).openRecord(loRS.getString("sEmailIDx"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                //System.out.println("lnctr = " + lnctr);
            }else{
//                paMail = new ArrayList<>();
//                addEmail(fsValue);
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
    
    public JSONObject saveEmail(String fsClientID){
        JSONObject obj = new JSONObject();
        
        if(paMail == null){
            obj.put("result", "error");
            obj.put("continue", true);
            return obj;
        }
        
        int lnSize = paMail.size() -1;
        if(lnSize < 0){
            obj.put("result", "error");
            obj.put("continue", true);
            return obj;
        }

        int lnCtr;
        String lsSQL;
        
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            paMail.get(lnCtr).setClientID(fsClientID);

            paMail.get(lnCtr).setModifiedDte(poGRider.getServerDate());
            if(lnCtr>0){
                if(paMail.get(lnCtr).getEmailAdd().isEmpty()){
                    paMail.remove(lnCtr);
                }
            }
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Client_Email, paMail.get(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                obj.put("result", "error");
                obj.put("message", validator.getMessage());
                return obj;
            }
            obj = paMail.get(lnCtr).saveRecord();
        }    
        
        return obj;
    }
    
    public ArrayList<Model_Client_Email> getEmailList(){return paMail;}
    public void setEmailList(ArrayList<Model_Client_Email> foObj){this.paMail = foObj;}
    
    public void setEmail(int fnRow, int fnIndex, Object foValue){ paMail.get(fnRow).setValue(fnIndex, foValue);}
    public void setEmail(int fnRow, String fsIndex, Object foValue){ paMail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getEmail(int fnRow, int fnIndex){return paMail.get(fnRow).getValue(fnIndex);}
    public Object getEmail(int fnRow, String fsIndex){return paMail.get(fnRow).getValue(fsIndex);}
    
    public Object removeEmail(int fnRow){
        JSONObject loJSON = new JSONObject();
        if(paMail.get(fnRow).getEntryBy().isEmpty()){
            paMail.remove(fnRow);
        } else {
            loJSON.put("result", "error");
            loJSON.put("message", "You cannot remove Email that already saved, Deactivate it instead.");
            return loJSON;
        }
        return loJSON;
    }
}
