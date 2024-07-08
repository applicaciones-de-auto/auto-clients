/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.resultSet2XML.clients;

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
        
        
        String lsSQL =    "  SELECT      "                                                                   
                        + "  a.sAddrssID " //1                                                               
                        + ", a.sClientID " //2                                                               
                        + ", a.cOfficexx " //3                                                               
                        + ", a.cProvince " //4                                                               
                        + ", a.cPrimaryx " //5                                                               
                        + ", a.cBillingx " //6                                                               
                        + ", a.cShipping " //7                                                               
                        + ", a.cCurrentx " //8                                                               
                        + ", a.cRecdStat " //9                                                               
                        + ", a.sEntryByx " //10                                                              
                        + ", a.dEntryDte " //11                                                              
                        + ", a.sModified " //12                                                              
                        + ", a.dModified " //13                                                              
                        + ", b.sHouseNox " //14                                                              
                        + ", b.sAddressx " //15                                                              
                        + ", b.sTownIDxx " //16                                                              
                        + ", b.sZippCode " //17                                                              
                        + ", b.sBrgyIDxx " //18                                                              
                        + ", b.nLatitude " //19                                                              
                        + ", b.nLongitud " //20                                                              
                        + ", b.sRemarksx " //21                                                              
                        + ", d.sBrgyName " //22                                                              
                        + ", c.sTownName " //23                                                              
                        + ", e.sProvName " //24                                                              
                        + ", e.sProvIDxx " //25                                                              
                        + "FROM client_address a   "                                                         
                        + "INNER JOIN addresses b ON b.sAddrssID = a.sAddrssID  "                            
                        + "LEFT JOIN TownCity c ON c.sTownIDxx = b.sTownIDxx    "                            
                        + "LEFT JOIN Barangay d ON d.sBrgyIDxx = b.sBrgyIDxx AND d.sTownIDxx = b.sTownIDxx " 
                        + "LEFT JOIN Province e ON e.sProvIDxx = c.sProvIDxx    "                            
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
