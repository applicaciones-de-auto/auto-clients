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
public class SalesAgent {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Sales_Agent.xml");
        
        
        String lsSQL =    "   SELECT "                                                                                               
                        + "   a.sClientID "                                                                                          
                        + " , a.sAgentTyp "                                                                                          
                        + " , a.cRecdStat "                                                                                          
                        + " , a.sModified "                                                                                          
                        + " , a.dModified "                                                                                          
                        + " , b.cClientTp "                                                                                          
                        + " , b.sLastName "                                                                                          
                        + " , b.sFrstName "                                                                                          
                        + " , b.sMiddName "                                                                                          
                        + " , b.sCompnyNm "                                                                                          
//                        + " , c.sMobileNo "                                                                                          
//                        + " , d.sAccountx "                                                                                          
//                        + " , e.sEmailAdd "                                                                                          
                        + " , IFNULL(CONCAT( IFNULL(CONCAT(g.sHouseNox,' ') , ''), "                                                 
                        + " IFNULL(CONCAT(g.sAddressx,' ') , ''),  "                                                                 
                        + " IFNULL(CONCAT(i.sBrgyName,' '), ''),   "                                                                 
                        + " IFNULL(CONCAT(h.sTownName, ', '),''),  "                                                                 
                        + " IFNULL(CONCAT(j.sProvName),'') )	, '') AS sAddressx  "                                                  
                        + " FROM sales_agent a   "                                                                                   
                        + " LEFT JOIN client_master b ON b.sClientID = a.sClientID "                                                 
//                        + " LEFT JOIN client_mobile c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1 AND c.cRecdStat = 1  "        
//                        + " LEFT JOIN client_social_media d ON  d.sClientID = a.sClientID AND d.cRecdStat = 1   "                    
//                        + " LEFT JOIN client_email_address e ON  e.sClientID = a.sClientID AND e.cPrimaryx = 1 AND e.cRecdStat = 1 " 
                        + " LEFT JOIN client_address f ON f.sClientID = a.sClientID AND f.cPrimaryx = 1 "                            
                        + " LEFT JOIN addresses g ON g.sAddrssID = f.sAddrssID "                                                     
                        + " LEFT JOIN TownCity h ON h.sTownIDxx = g.sTownIDxx  "                                                     
                        + " LEFT JOIN barangay i ON i.sBrgyIDxx = g.sBrgyIDxx AND i.sTownIDxx = g.sTownIDxx  "                       
                        + " LEFT JOIN Province j ON j.sProvIDxx = h.sProvIDxx  "
                        + " WHERE 0=1";
        
        System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "sales_agent", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
