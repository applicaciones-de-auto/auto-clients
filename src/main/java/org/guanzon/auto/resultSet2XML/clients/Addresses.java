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
public class Addresses {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Addresses.xml");
        
        String lsSQL =    "  SELECT      "                                                                       
                        + "  a.sAddrssID " //1                                                                   
                        + ", a.sHouseNox " //2                                                                   
                        + ", a.sAddressx " //3                                                                   
                        + ", a.sTownIDxx " //4                                                                   
                        + ", a.sZippCode " //5                                                                   
                        + ", a.sBrgyIDxx " //6                                                                   
                        + ", a.nLatitude " //7                                                                   
                        + ", a.nLongitud " //8                                                                   
                        + ", a.sRemarksx " //9                                                                   
                        + ", a.sModified " //10                                                                  
                        + ", a.dModified " //11                                                                  
                        + ", IFNULL(d.sProvName, '') sProvName " //12                                            
                        + ", IFNULL(c.sBrgyName, '') sBrgyName " //13                                            
                        + ", IFNULL(b.sTownName, '') sTownName " //14                                            
                        + ", IFNULL(d.sProvIDxx, '') sProvIDxx " //15                                            
                        + ", REPLACE(CONCAT(IFNULL(a.sHouseNox,''),         "                                    
                        + "  IFNULL(a.sAddressx,''),IFNULL(c.sBrgyName,''), "                                    
                        + "  IFNULL(b.sTownName,''), IFNULL(d.sProvName,'')), ' ', '') AS trimAddress   " //16   
                        + "FROM addresses a   "                                                                  
                        + "LEFT JOIN TownCity b ON b.sTownIDxx = a.sTownIDxx "                                   
                        + "LEFT JOIN Barangay c ON c.sBrgyIDxx = a.sBrgyIDxx "                                   
                        + "LEFT JOIN Province d ON d.sProvIDxx = b.sProvIDxx "                                   
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "addresses", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
