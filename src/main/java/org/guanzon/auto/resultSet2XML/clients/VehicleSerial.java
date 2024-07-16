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
public class VehicleSerial {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Vehicle_Serial_Master.xml");
        
        
        String lsSQL =     "  SELECT "                                                                                               
                        + "  a.sSerialID " //1                                                                                           
                        + ", a.sBranchCD " //2                                                                                           
                        + ", a.sFrameNox " //3                                                                                           
                        + ", a.sEngineNo " //4                                                                                           
                        + ", a.sVhclIDxx " //5                                                                                           
                        + ", a.sClientID " //6                                                                                           
                        + ", a.sCoCltIDx " //7                                                                                           
                        + ", a.sCSNoxxxx " //8                                                                                           
                        + ", a.sDealerNm " //9                                                                                           
                        + ", a.sCompnyID " //10                                                                                          
                        + ", a.sKeyNoxxx " //11                                                                                          
                        + ", a.cIsDemoxx " //12                                                                                          
                        + ", a.cLocation " //13                                                                                          
                        + ", a.cSoldStat " //14                                                                                          
                        + ", a.cVhclNewx " //15                                                                                          
                        + ", a.sRemarksx " //16                                                                                          
                        + ", a.sEntryByx " //17                                                                                          
                        + ", a.dEntryDte " //18                                                                                          
                        + ", a.sModified " //19                                                                                          
                        + ", a.dModified " //20                                                                                          
                        + ", b.sPlateNox " //21                                                                                          
                        + ", b.dRegister " //22                                                                                          
                        + ", b.sPlaceReg " //23                                                                                          
                        + ", c.sMakeIDxx " //24                                                                                          
                        + ", d.sMakeDesc " //25                                                                                          
                        + ", c.sModelIDx " //26                                                                                          
                        + ", e.sModelDsc " //27                                                                                          
                        + ", c.sTypeIDxx " //28                                                                                          
                        + ", f.sTypeDesc " //29                                                                                          
                        + ", c.sColorIDx " //30                                                                                          
                        + ", g.sColorDsc " //31                                                                                          
                        + ", c.sTransMsn " //32                                                                                          
                        + ", c.nYearModl " //33                                                                                          
                        + ", c.sDescript " //34                                                                                                                                                                                   
                        + ", h.sCompnyNm AS sOwnerNmx" //35                                                                                          
                        + ", i.sCompnyNm AS sCOwnerNm" //36                                                                                          
                        + ", IFNULL(CONCAT(IFNULL(CONCAT(jj.sHouseNox,' ') , ''), IFNULL(CONCAT(jj.sAddressx,' ') , ''), "              
                        + "	IFNULL(CONCAT(l.sBrgyName,' '), ''), "              
                        + "	IFNULL(CONCAT(k.sTownName, ', '),''), "              
                        + "	IFNULL(CONCAT(m.sProvName),'') ), '') AS sOwnerAdd " //37                     
                        + ", IFNULL(CONCAT(IFNULL(CONCAT(nn.sHouseNox,' ') , ''), IFNULL(CONCAT(nn.sAddressx,' ') , ''), "              
                        + "	IFNULL(CONCAT(p.sBrgyName,' '), ''), "              
                        + "	IFNULL(CONCAT(o.sTownName, ', '),''), "              
                        + "	IFNULL(CONCAT(q.sProvName),''))	, '') AS sCOwnerAd " //38                      
                        + ",CASE "              
                        + "  WHEN a.cSoldStat = '0' THEN 'NON SALES CUSTOMER' "              
                        + "  WHEN a.cSoldStat = '1' THEN 'AVAILABLE FOR SALE' "              
                        + "  WHEN a.cSoldStat = '2' THEN 'VSP' "              
                        + "  WHEN a.cSoldStat = '3' THEN 'SOLD' "              
                        + " ELSE '' "              
                        + " END AS sVhclStat " //39         
                        + " , r.sReferNox AS sUdrNoxxx" //40         
                        + " , r.dTransact AS sUdrDatex" //41         
                        + " , s.sCompnyNm AS sBuyerNmx " //42         
                        + "FROM vehicle_serial a "              
                        + "LEFT JOIN vehicle_serial_registration b ON a.sSerialID = b.sSerialID "              
                        + "LEFT JOIN vehicle_master c ON c.sVhclIDxx = a.sVhclIDxx "              
                        + "LEFT JOIN vehicle_make   d ON d.sMakeIDxx = c.sMakeIDxx "              
                        + "LEFT JOIN vehicle_model  e ON e.sModelIDx = c.sModelIDx "              
                        + "LEFT JOIN vehicle_type   f ON f.sTypeIDxx = c.sTypeIDxx "              
                        + "LEFT JOIN vehicle_color  g ON g.sColorIDx = c.sColorIDx "              
                        + "LEFT JOIN client_master  h ON h.sClientID = a.sClientID "              
                        + "LEFT JOIN client_master  i ON i.sClientID = a.sCoCltIDx "              
                         /* Owner Address */                                                                                             
                        + "LEFT JOIN client_address j ON j.sClientID = a.sClientID AND j.cPrimaryx = '1' "              
                        + "LEFT JOIN addresses     jj ON jj.sAddrssID = j.sAddrssID "              
                        + "LEFT JOIN TownCity       k ON k.sTownIDxx = jj.sTownIDxx "              
                        + "LEFT JOIN barangay       l ON l.sBrgyIDxx = jj.sBrgyIDxx AND l.sTownIDxx = jj.sTownIDxx "              
                        + "LEFT JOIN Province       m ON m.sProvIDxx = k.sProvIDxx "              
                         /* Co Owner Address */                                                                                          
                        + "LEFT JOIN client_address n ON n.sClientID = a.sCoCltIDx AND n.cPrimaryx = '1' "              
                        + "LEFT JOIN addresses     nn ON nn.sAddrssID = n.sAddrssID "              
                        + "LEFT JOIN TownCity       o ON o.sTownIDxx = nn.sTownIDxx "              
                        + "LEFT JOIN barangay       p ON p.sBrgyIDxx = nn.sBrgyIDxx AND p.sTownIDxx = nn.sTownIDxx "              
                        + "LEFT JOIN Province       q ON q.sProvIDxx = o.sProvIDxx "              
                         /* UDR INFO */                                                                                                  
                        + "LEFT JOIN udr_master     r ON r.sSerialID = a.sSerialID AND r.sClientID = a.sClientID AND r.cTranStat = '1' "
                        + "LEFT JOIN client_master  s ON s.sClientID = r.sClientID "
                        + " WHERE 0=1";
        
        System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "vehicle_serial", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
