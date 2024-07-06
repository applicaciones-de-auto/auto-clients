/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.clients.controller;

import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.model.clients.Model_Addresses;
import org.guanzon.auto.validator.clients.ValidatorFactory;
import org.guanzon.auto.validator.clients.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Addresses {
    final String ADDRESS_XML = "Model_Addresses.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psMessagex;
    
    Model_Addresses poAddresses;
    ArrayList<Model_Addresses> paAddresses;
    
    public Addresses(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    public JSONObject poJSON;
    
    public int getEditMode() {
        return pnEditMode;
    }
   
    public Model_Addresses getAddress(int fnIndex){
        if (fnIndex > paAddresses.size() - 1 || fnIndex < 0) return null;
        
        return paAddresses.get(fnIndex);
    }
    
    public void resetAddressesList(){
        paAddresses = new ArrayList <>();
    }
    
    public JSONObject openAddresses(String fsValue){
        poJSON = new JSONObject();
        
        paAddresses.add(new Model_Addresses(poGRider));
        paAddresses.get(paAddresses.size() - 1).openRecord(fsValue);
        poJSON.put("result", "success");
        poJSON.put("message", "Record loaded successfully.");
        return poJSON;
    }
    
    public JSONObject addAddresses(){
        if(paAddresses == null){
            paAddresses = new ArrayList <>();
        }
        
        poJSON = new JSONObject();
        if (paAddresses.isEmpty()){
            paAddresses.add(new Model_Addresses(poGRider));
            paAddresses.get(0).newRecord();
            poJSON.put("result", "success");
            poJSON.put("message", "Address add record.");

        } else {
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Addresses, paAddresses.get(paAddresses.size()-1));
            validator.setGRider(poGRider);
            if(!validator.isEntryOkay()){
                poJSON.put("result", "error");
                poJSON.put("message", validator.getMessage());
                return poJSON;
            }
            paAddresses.add(new Model_Addresses(poGRider));
            paAddresses.get(paAddresses.size()-1).newRecord();
        }
        return poJSON;
    }
    
    public JSONObject saveAddresses(){
        JSONObject obj = new JSONObject();
        
        if(paAddresses == null){
            paAddresses = new ArrayList <>();
            obj.put("continue", true);
            obj.put("result", "error");
            return obj;
        }
        
        int lnCtr;
        String lsSQL;
        
        for (lnCtr = 0; lnCtr <= paAddresses.size() -1; lnCtr++){
            if(lnCtr>0){
                if(paAddresses.get(lnCtr).getBrgyID().isEmpty() || paAddresses.get(lnCtr).getTownID().isEmpty()){
                    paAddresses.remove(lnCtr);
                }
            }
            
            paAddresses.get(lnCtr).setModifiedDte(poGRider.getServerDate());
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Addresses, paAddresses.get(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                obj.put("result", "error");
                obj.put("message", validator.getMessage());
                return obj;
            
            }
            obj = paAddresses.get(lnCtr).saveRecord();

        }    
        return obj;
    }
    
    public ArrayList<Model_Addresses> getAddressesList(){return paAddresses;}
    public void setAddressesList(ArrayList<Model_Addresses> foObj){this.paAddresses = foObj;}
    
    public void setAddresses(int fnRow, int fnIndex, Object foValue){ paAddresses.get(fnRow).setValue(fnIndex, foValue);}
    public void setAddresses(int fnRow, String fsIndex, Object foValue){ paAddresses.get(fnRow).setValue(fsIndex, foValue);}
    public Object getAddresses(int fnRow, int fnIndex){return paAddresses.get(fnRow).getValue(fnIndex);}
    public Object getAddresses(int fnRow, String fsIndex){return paAddresses.get(fnRow).getValue(fsIndex);}
    
    public Object removeAddresses(int fnRow){
        JSONObject loJSON = new JSONObject();
        paAddresses.remove(fnRow);
        return loJSON;
    }
}
