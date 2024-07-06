/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.clients.controller;

import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.model.clients.Model_Vehicle_Registration;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Vehicle_Registration implements GRecord {

    GRider poGRider;
    boolean pbWthParent;
    String psBranchCd;
    
    int pnEditMode;
    String psRecdStat;

    Model_Vehicle_Registration poModel;
    JSONObject poJSON;

    public Vehicle_Registration(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poModel = new Model_Vehicle_Registration(foAppDrver);
        
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
        return poModel.newRecord();
    }

    @Override
    public JSONObject openRecord(String fsValue) {
        return poModel.openRecord(fsValue);
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
        if (!pbWthParent) {
            poGRider.beginTrans();
        }

        poJSON = poModel.saveRecord();

        if ("success".equals((String) poJSON.get("result"))) {
            if (!pbWthParent) {
                poGRider.commitTrans();
            }
        } else {
            if (!pbWthParent) {
                poGRider.rollbackTrans();
            }
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
            poJSON = poModel.setActive(false);

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poJSON = poModel.saveRecord();
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
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }

    @Override
    public JSONObject searchRecord(String fsValue, boolean fbByCode) {
        String lsCondition = "";
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        if (psRecdStat.length() > 1) {
//            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
//                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
//            }
//
//            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
//        } else {
//            lsCondition = "cRecdStat = " + SQLUtil.toSQL(psRecdStat);
//        }
//
//        String lsSQL = MiscUtil.addCondition(poModel.makeSelectSQL(), " sDescript LIKE "
//                + SQLUtil.toSQL(fsValue + "%") + " AND " + lsCondition);
//
//        poJSON = ShowDialogFX.Search(poGRider,
//                lsSQL,
//                fsValue,
//                "Vehicle Serial»Plate No.»CS No.»Description",
//                "sSerialID»sPlateNox»sCSNoxxxx»sDescript",
//                "sSerialID»sPlateNox»sCSNoxxxx»sDescript",
//                fbByCode ? 0 : 1);
//
//        if (poJSON != null) {
//            return poModel.openRecord((String) poJSON.get("sSerialID"));
//        } else {
//            poJSON.put("result", "error");
//            poJSON.put("message", "No record loaded to update.");
//            return poJSON;
//        }
    }

    @Override
    public Model_Vehicle_Registration getModel() {
        return poModel;
    }
    
    public String getSQL(){
        return " SELECT " +
                "  sSerialID," +
                "  sCSRValNo," +
                "  sPNPClrNo," +
                "  sCRNoxxxx," +
                "  sCRENoxxx," +
                "  sRegORNox," +
                "  sFileNoxx," +
                "  sPlateNox," +
                "  dRegister," +
                "  sPlaceReg," +
                "  sEntryByx," +
                "  dEntryDte," +
                "  sModified," +
                "  dModified" +
                "FROM vehicle_serial_registration";
    }
    
    
}
