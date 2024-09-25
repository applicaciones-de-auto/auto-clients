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
public class VehicleGatepass {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Vehicle_Gatepass.xml");
        String lsSQL =    " SELECT "
                        + "    a.sTransNox "                                                                 
                        + "  , a.dTransact "                                                                 
                        + "  , a.sRemarksx "                                                                 
                        + "  , a.sSourceGr "                                                                 
                        + "  , a.sSourceCD "                                                                 
                        + "  , a.sSourceNo "                                                                 
                        + "  , a.cPrintedx "                                                                 
                        + "  , a.cTranStat "                                                                 
                        + "  , a.sEntryByx "                                                                 
                        + "  , a.dEntryDte "                                                                 
                        + "  , a.sModified "                                                                 
                        + "  , a.dModified "                                                                 
                        + "  , b.sBranchCD "                                                                  
                        + "  , c.sTransNox AS sUDRCodex "                                                    
                        + "  , c.sReferNox AS sUDRNoxxx "                                                     
                        + "  , d.sSerialID "                                                       
                        + "  , d.sCSNoxxxx "                                                                 
                        + "  , e.sPlateNox "                                                                 
                        + "  , d.sFrameNox "                                                                 
                        + "  , d.sEngineNo "                                                                 
                        + "  , d.sKeyNoxxx "                                                                 
                        + "  , f.sDescript AS sVhclFDsc "                                                   
                        + "  , g.sClientID "                                                  
                        + "  , g.sCompnyNm AS sBuyCltNm "                                                    
                        + "  , g.cClientTp "                                                                 
                        + "  , TRIM(IFNULL(CONCAT( IFNULL(CONCAT(i.sHouseNox,' ') , ''), "                   
                        + "   IFNULL(CONCAT(i.sAddressx,' ') , ''),   "                                      
                        + "   IFNULL(CONCAT(j.sBrgyName,' '), ''),    "                                      
                        + "   IFNULL(CONCAT(k.sTownName, ', '),''),   "                                      
                        + "   IFNULL(CONCAT(l.sProvName),'') )	, '')) AS sAddressx "                        
                        + " FROM vehicle_gatepass a  "                                                       
                        + " LEFT JOIN vsp_master b ON b.sTransNox = a.sSourceCD "                            
                        + " LEFT JOIN udr_master c ON c.sSourceNo = a.sSourceCD "                            
                        + " LEFT JOIN vehicle_serial d ON c.sSerialID = b.sSerialID "                        
                        + " LEFT JOIN vehicle_serial_registration e ON e.sSerialID = b.sSerialID "           
                        + " LEFT JOIN vehicle_master f ON f.sVhclIDxx = d.sVhclIDxx "                        
                        + " LEFT JOIN client_master g ON g.sClientID = b.sClientID  "                        
                        + " LEFT JOIN client_address h ON h.sClientID = g.sClientID AND h.cPrimaryx = 1 "    
                        + " LEFT JOIN addresses i ON i.sAddrssID = h.sAddrssID "                             
                        + " LEFT JOIN barangay j ON j.sBrgyIDxx = i.sBrgyIDxx  "                             
                        + " LEFT JOIN towncity k ON k.sTownIDxx = i.sTownIDxx  "                             
                        + " LEFT JOIN province l ON l.sProvIDxx = k.sProvIDxx  "
                        + " WHERE 0=1";
        
        System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "vehicle_gatepass", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
