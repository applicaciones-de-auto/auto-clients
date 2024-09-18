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
public class ClientMaster {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Client_Master.xml");
        
        String lsSQL =    "  SELECT      "                                                                                      
                        + "  a.sClientID "  //1                                                                                 
                        + ", a.sLastName "  //2                                                                                 
                        + ", a.sFrstName "  //3                                                                                 
                        + ", a.sMiddName "  //4                                                                                 
                        + ", a.sMaidenNm "  //5                                                                                 
                        + ", a.sSuffixNm "  //6                                                                                 
                        + ", a.sTitlexxx "  //7                                                                                 
                        + ", a.cGenderCd "  //8                                                                                 
                        + ", a.cCvilStat "  //9                                                                                 
                        + ", a.sCitizenx "  //10                                                                                
                        + ", a.dBirthDte "  //11                                                                                
                        + ", a.sBirthPlc "  //12                                                                                
                        + ", a.sTaxIDNox "  //13                                                                                
                        + ", a.sLTOIDxxx "  //14                                                                                
                        + ", a.sAddlInfo "  //15                                                                                
                        + ", a.sCompnyNm "  //16                                                                                
                        + ", a.sClientNo "  //17                                                                                
                        + ", a.sSpouseID "  //18                                                                                
                        + ", a.cClientTp "  //19                                                                                
                        + ", a.cRecdStat "  //20                                                                                
                        + ", a.sEntryByx "  //21                                                                                
                        + ", a.dEntryDte "  //22                                                                                
                        + ", a.sModified "  //23                                                                                
                        + ", a.dModified "  //24                                                                                
                        + ", IFNULL(b.sNational, '') sNational   " //25                                                         
                        + ", TRIM(CONCAT(c.sTownName, ', ', d.sProvName)) sTownName   "   //26       
                        + ", e.sCompnyNm    sSpouseNm " //27
                        + ",  IFNULL(CONCAT( IFNULL(CONCAT(g.sHouseNox,' ') , ''),    "                                         
                        + "   IFNULL(CONCAT(g.sAddressx,' ') , ''),                   "                                         
                        + "   IFNULL(CONCAT(h.sBrgyName,' '), ''),                    "                                         
                        + "   IFNULL(CONCAT(i.sTownName, ', '),''),                   "                                         
                        + "   IFNULL(CONCAT(j.sProvName),'') )	, '') AS sAddressx    "  //28                                   
                        + "FROM client_master  a                                      "                                         
                        + "LEFT JOIN Country b ON a.sCitizenx = b.sCntryCde           "                                         
                        + "LEFT JOIN TownCity c ON a.sBirthPlc = c.sTownIDxx          "                                         
                        + "LEFT JOIN Province d ON c.sProvIDxx = d.sProvIDxx          "                                         
                        + "LEFT JOIN client_master e ON e.sClientID = a.sSpouseID     "                                         
                        + "LEFT JOIN client_address f ON f.sClientID = a.sClientID AND f.cPrimaryx = '1' "                      
                        + "LEFT JOIN addresses g ON g.sAddrssID = f.sAddrssID         "                                         
                        + "LEFT JOIN barangay h ON h.sBrgyIDxx = g.sBrgyIDxx          "                                         
                        + "LEFT JOIN towncity i ON i.sTownIDxx = g.sTownIDxx          "                                         
                        + "LEFT JOIN province j ON j.sProvIDxx = i.sProvIDxx          "            
                        + " WHERE 0=1";
        
        //System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "client_master", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
