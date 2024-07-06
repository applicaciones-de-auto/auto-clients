/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.clients.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.auto.model.clients.Model_Client_Social_Media;
import org.guanzon.auto.validator.clients.ValidatorFactory;
import org.guanzon.auto.validator.clients.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Client_Social_Media {
    final String MOBILE_XML = "Model_Client_Social_Media.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_Client_Social_Media> paSocMed;
    
    public Client_Social_Media(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    public int getEditMode() {
        return pnEditMode;
    }
    
    public Model_Client_Social_Media getSocial(int fnIndex){
        if (fnIndex > paSocMed.size() - 1 || fnIndex < 0) return null;
        
        return paSocMed.get(fnIndex);
    }
    
    public JSONObject addSocialMedia(String fsClientID){
        
        if(paSocMed == null){
            paSocMed = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paSocMed.isEmpty()){
            paSocMed.add(new Model_Client_Social_Media(poGRider));
            paSocMed.get(0).newRecord();
            paSocMed.get(0).setClientID(fsClientID);
            poJSON.put("result", "success");
            poJSON.put("message", "Social media add record.");
        } else {
            ValidatorInterface validator = ValidatorFactory.make(  ValidatorFactory.TYPE.Client_Social_Media, paSocMed.get(paSocMed.size()-1));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                poJSON.put("result", "error");
                poJSON.put("message", validator.getMessage());
                return poJSON;
            }
            paSocMed.add(new Model_Client_Social_Media( poGRider));
            paSocMed.get(paSocMed.size()-1).newRecord();
            paSocMed.get(paSocMed.size()-1).setClientID(fsClientID);
            poJSON.put("result", "success");
            poJSON.put("message", "Social media add record.");
        }
        return poJSON;
    }
    
    public JSONObject OpenClientSocialAccount(String fsValue){
        paSocMed = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL = "SELECT" +
                    "  sSocialID" +
                    ", sClientID" +
                        " FROM Client_Social_Media" ;
        lsSQL = MiscUtil.addCondition(lsSQL, "sClientID = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                paSocMed = new ArrayList<>();
                while(loRS.next()){
                        paSocMed.add(new Model_Client_Social_Media(poGRider));
                        paSocMed.get(paSocMed.size() - 1).openRecord(loRS.getString("sSocialID"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                System.out.println("lnctr = " + lnctr);
            }else{
//                paSocMed = new ArrayList<>();
//                addSocialMedia(fsValue);
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
    
    public JSONObject saveSocialAccount (String fsClientID){
        JSONObject obj = new JSONObject();
        
        if(paSocMed == null){
            obj.put("result", "error");
            obj.put("continue", true);
            return obj;
        }
        
        int lnSize = paSocMed.size() -1;
        if(lnSize < 0){
            obj.put("result", "error");
            obj.put("continue", true);
            return obj;
        }

        int lnCtr;
        String lsSQL;
        
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            paSocMed.get(lnCtr).setClientID(fsClientID);
            if(lnCtr>0){
                if(paSocMed.get(lnCtr).getAccount().isEmpty()){
                    paSocMed.remove(lnCtr);
                }
            }
            ValidatorInterface validator = ValidatorFactory.make(  ValidatorFactory.TYPE.Client_Social_Media, paSocMed.get(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                obj.put("result", "error");
                obj.put("message", validator.getMessage());
                return obj;

            }
            obj = paSocMed.get(lnCtr).saveRecord();
        }    
        
        return obj;
    }
    
    public ArrayList<Model_Client_Social_Media> getSocialMediaList(){return paSocMed;}
    public void setSocialMediaList(ArrayList<Model_Client_Social_Media> foObj){this.paSocMed = foObj;}
    
    public void setSocialMed(int fnRow, int fnIndex, Object foValue){ paSocMed.get(fnRow).setValue(fnIndex, foValue);}
    public void setSocialMed(int fnRow, String fsIndex, Object foValue){ paSocMed.get(fnRow).setValue(fsIndex, foValue);}
    public Object getSocialMed(int fnRow, int fnIndex){return paSocMed.get(fnRow).getValue(fnIndex);}
    public Object getSocialMed(int fnRow, String fsIndex){return paSocMed.get(fnRow).getValue(fsIndex);}
    
    public Object removeSocialMed(int fnRow){
        JSONObject loJSON = new JSONObject();
        if(paSocMed.get(fnRow).getEntryBy().isEmpty()){
            paSocMed.remove(fnRow);
        } else {
            loJSON.put("result", "error");
            loJSON.put("message", "You cannot remove Social Media that already saved, Deactivate it instead.");
            return loJSON;
        }
        return loJSON;
    }
    
}
