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
        
        
        String lsSQL =     " SELECT "                                                                                              
                + "  IFNULL(a.sClientID,'') sClientID " //1                                                             
                + ", IFNULL(a.sLastName,'') sLastName " //2                                                             
                + ", IFNULL(a.sFrstName,'') sFrstName " //3                                                             
                + ", IFNULL(a.sMiddName,'') sMiddName " //4                                                             
                + ", IFNULL(a.sMaidenNm,'') sMaidenNm " //5                                                             
                + ", IFNULL(a.sSuffixNm,'') sSuffixNm " //6                                                             
                + ", IFNULL(a.sTitlexxx,'') sTitlexxx " //7                                                             
                + ", IFNULL(a.cGenderCd,'') cGenderCd " //8                                                             
                + ", IFNULL(a.cCvilStat,'') cCvilStat " //9                                                             
                + ", IFNULL(a.sCitizenx,'') sCitizenx " //10                                                            
                + ", a.dBirthDte"                       //11                                                                                  
                + ", IFNULL(a.sBirthPlc,'') sBirthPlc " //12                                                            
                + ", IFNULL(a.sTaxIDNox,'') sTaxIDNox " //13                                                            
                + ", IFNULL(a.sLTOIDxxx,'') sLTOIDxxx " //14                                                            
                + ", IFNULL(a.sAddlInfo,'') sAddlInfo " //15                                                            
                + ", IFNULL(a.sCompnyNm,'') sCompnyNm " //16                                                            
                + ", IFNULL(a.sClientNo,'') sClientNo " //17                                                            
                + ", IFNULL(a.cClientTp,'') cClientTp " //18                                                            
                + ", IFNULL(a.cRecdStat,'') cRecdStat " //19                                                            
                + ", IFNULL(a.sEntryByx,'') sEntryByx " //20                                                            
                + ", a.dEntryDte"                       //21                                                                                  
                + ", IFNULL(a.sModified,'') sModified " //22                                                            
                + ", a.dModified"                       //23                                                                                  
                + ", IFNULL(a.sSpouseID,'') sSpouseID " //24                                                            
                + ", IFNULL(b.sCntryNme, '') sCntryNme "   //25                                                         
                + ", TRIM(CONCAT(c.sTownName, ', ', d.sProvName)) sTownName"   //26                                     
                + ", TRIM(CONCAT(a.sLastName, ', ', a.sFrstName, ' ', a.sSuffixNm, ' ', a.sMiddName)) sCustName"   //27 
                + ", TRIM(CONCAT(e.sLastName, ', ', e.sFrstName)) sSpouseNm"   //28                                     
                + ", IFNULL(CONCAT( IFNULL(CONCAT(g.sHouseNox,' ') , ''), "                                             
                + "    IFNULL(CONCAT(g.sAddressx,' ') , ''), "                                                          
                + "    IFNULL(CONCAT(h.sBrgyName,' '), ''),  "                                                          
                + "    IFNULL(CONCAT(i.sTownName, ', '),''), "                                                          
                + "    IFNULL(CONCAT(j.sProvName),'') )	, '') AS sAddressx   "  //29    
                //+ " CASE WHEN a.cClientTp = '0' THEN 'CLIENT' ELSE 'COMPANY' END AS cClientTp "  //30
                + " FROM client_master a"                                                                          
                + " LEFT JOIN Country b ON a.sCitizenx = b.sCntryCde"                                                   
                + " LEFT JOIN TownCity c ON a.sBirthPlc = c.sTownIDxx"                                                  
                + " LEFT JOIN Province d ON c.sProvIDxx = d.sProvIDxx"                                                  
                + " LEFT JOIN client_master e ON e.sClientID = a.sSpouseID"                                             
                + " LEFT JOIN client_address f ON f.sClientID = a.sClientID AND f.cPrimaryx = '1'"                      
                + " LEFT JOIN addresses g ON g.sAddrssID = f.sAddrssID"                                                 
                + " LEFT JOIN barangay h ON h.sBrgyIDxx = g.sBrgyIDxx"                                                  
                + " LEFT JOIN towncity i ON i.sTownIDxx = g.sTownIDxx"                                                  
                + " LEFT JOIN province j ON j.sProvIDxx = i.sProvIDxx"
                + " WHERE 0=1";
        
        System.out.println(lsSQL);
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
