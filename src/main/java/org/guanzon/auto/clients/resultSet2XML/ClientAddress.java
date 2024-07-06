/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.clients.resultSet2XML;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;

/**
 *
 * @author Arsiela
 */
public class ClientAddress {
    
    public static void main (String [] args){
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Maven_Systems";
        }
        else{
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        
        GRider instance = new GRider("gRider");

        if (!instance.logUser("gRider", "M001000001")){
            System.err.println(instance.getErrMsg());
            System.exit(1);
        }

        System.out.println("Connected");
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Client_Address.xml");
        
        
        String lsSQL =   " SELECT   "                                                                                   
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
                + "  LEFT JOIN Province e ON e.sProvIDxx = c.sProvIDxx   "
                + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "client_address", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
