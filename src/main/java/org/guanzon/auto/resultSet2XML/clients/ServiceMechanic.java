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
public class ServiceMechanic {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Service_Mechanic.xml");
        String lsSQL =    " SELECT " 
                        + "    a.sClientID "
                        + "  , a.cTchSkill " 
                        + "  , a.cBrpSkill " 
                        + "  , a.cRecdStat " 
                        + "  , b.sLastName "
                        + "  , b.sFrstName "
                        + "  , b.sMiddName "
                        + "  , b.sCompnyNm "
                        + "  , b.cClientTp "                                                                                                                                           
                        + "  , IFNULL(b.sAddressx,IFNULL(CONCAT( IFNULL(CONCAT(c.sAddressx,' '), ''), IFNULL(CONCAT(e.sBrgyName,' '), ''), IFNULL(CONCAT(d.sTownName, ', '),''), IFNULL(CONCAT(f.sProvName),'') ), '')) AS sAddressx  "  
                        + " FROM service_mechanic a "
                        + " LEFT JOIN GGC_ISysDBF.Client_Master b ON b.sClientID = a.sClientID "                                                                                                              
                        + " LEFT JOIN GGC_ISysDBF.Client_Address c ON c.sClientID = a.sClientID AND c.nPriority = 1  "                                                                                                                    
                        + " LEFT JOIN GGC_ISysDBF.TownCity d ON d.sTownIDxx = c.sTownIDxx "                                                                                                                                            
                        + " LEFT JOIN GGC_ISysDBF.Barangay e ON e.sBrgyIDxx = c.sBrgyIDxx AND e.sTownIDxx = c.sTownIDxx "                                                                                                                
                        + " LEFT JOIN GGC_ISysDBF.Province f ON f.sProvIDxx = d.sProvIDxx "
                        + " WHERE 0=1";
        
//        String lsSQL =    " SELECT " 
//                        + "    a.sClientID "
//                        + "  , a.cTchSkill " 
//                        + "  , a.cBrpSkill " 
//                        + "  , a.cRecdStat " 
//                        + "  , b.sLastName "
//                        + "  , b.sFrstName "
//                        + "  , b.sMiddName "
//                        + "  , b.sCompnyNm "
//                        + "  , b.cClientTp "                                                                                                                                           
//                        + "  , IFNULL(b.sAddressx,IFNULL(CONCAT( IFNULL(CONCAT(f.sAddressx,' '), ''), IFNULL(CONCAT(h.sBrgyName,' '), ''), IFNULL(CONCAT(g.sTownName, ', '),''), IFNULL(CONCAT(i.sProvName),'') ), '')) AS sAddressx  " 
//                        + "  , IFNULL(b.sMobileNo,IFNULL(c.sMobileNo,'') ) AS sMobileNo "                                                                                                                                               
//                        + "  , IFNULL(b.sEmailAdd,IFNULL(e.sEmailAdd,'') ) AS sEmailAdd "       
//                        + " FROM service_mechanic a "
//                        + " LEFT JOIN ggc_isysdbf.client_master b ON b.sClientID = a.sClientID "
//                        + " LEFT JOIN GGC_ISysDBF.Client_Mobile c ON c.sClientID = a.sClientID AND c.nPriority = 1 AND c.cRecdStat = 1 "                                                                                                  
//                        + " LEFT JOIN GGC_ISysDBF.Client_eMail_Address e ON e.sClientID = a.sClientID AND e.nPriority = 1 "                                                                                                               
//                        + " LEFT JOIN GGC_ISysDBF.Client_Address f ON f.sClientID = a.sClientID AND f.nPriority = 1  "                                                                                                                    
//                        + " LEFT JOIN GGC_ISysDBF.TownCity g ON g.sTownIDxx = f.sTownIDxx "                                                                                                                                            
//                        + " LEFT JOIN GGC_ISysDBF.barangay h ON h.sBrgyIDxx = f.sBrgyIDxx AND h.sTownIDxx = f.sTownIDxx "                                                                                                                
//                        + " LEFT JOIN GGC_ISysDBF.Province i ON i.sProvIDxx = g.sProvIDxx " 
//                        + " WHERE 0=1";
        
        System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "service_mechanic", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
