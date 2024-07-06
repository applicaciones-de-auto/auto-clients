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
        
        
        String lsSQL =   " SELECT " + //
                "   IFNULL(a.sSerialID,'') sSerialID " + //1
                " , IFNULL(a.sBranchCD,'') sBranchCD " + //2
                " , IFNULL(a.sFrameNox,'') sFrameNox " + //3
                " , IFNULL(a.sEngineNo,'') sEngineNo " + //4
                " , IFNULL(a.sVhclIDxx,'') sVhclIDxx " + //5
                " , IFNULL(a.sClientID,'') sClientID " + //6
                " , IFNULL(a.sCoCltIDx,'') sCoCltIDx " + //7
                " , IFNULL(a.sCSNoxxxx,'') sCSNoxxxx " + //8
                " , IFNULL(a.sDealerNm,'') sDealerNm " + //9
                " , IFNULL(a.sCompnyID,'') sCompnyID " + //10
                " , IFNULL(a.sKeyNoxxx,'') sKeyNoxxx " + //11
                " , IFNULL(a.cIsDemoxx,'') cIsDemoxx " + //12
                " , IFNULL(a.cLocation,'') cLocation " + //13
                " , IFNULL(a.cSoldStat,'') cSoldStat " + //14
                " , IFNULL(a.cVhclNewx,'') cVhclNewx " + //15
                " , a.sEntryByx " + //16
                " , a.dEntryDte " + //17
                " , a.sModified " + //18
                " , a.dModified " + //19
                " , IFNULL(b.sPlateNox,'') sPlateNox " + //20
                " , IFNULL(b.dRegister,CAST('1900-01-01' AS DATE)) dRegister " + //21
                " , IFNULL(b.sPlaceReg,'') sPlaceReg " + //22
                " , IFNULL(c.sMakeIDxx,'') sMakeIDxx " + //23
                " , IFNULL(d.sMakeDesc,'') sMakeDesc " + //24 
                " , IFNULL(c.sModelIDx,'') sModelIDx " + //25
                " , IFNULL(e.sModelDsc,'') sModelDsc " + //26   
                " , IFNULL(c.sTypeIDxx,'') sTypeIDxx " + //27 
                " , IFNULL(f.sTypeDesc,'') sTypeDesc " + //28   
                " , IFNULL(c.sColorIDx,'') sColorIDx " + //29 
                " , IFNULL(g.sColorDsc,'') sColorDsc " + //30
                " , IFNULL(c.sTransMsn,'') sTransMsn " + //31
                " , IFNULL(c.nYearModl,'') nYearModl " + //32
                " , IFNULL(c.sDescript,'') sDescript " + //33
                " , IFNULL(a.sRemarksx,'') sRemarksx " + //34
                " , IFNULL(h.sCompnyNm,'') sOwnerNam " + //35
                " , IFNULL(i.sCompnyNm,'') sCoOwnerN " + //36
                " , IFNULL(CONCAT(IFNULL(CONCAT(jj.sHouseNox,' ') , ''), IFNULL(CONCAT(jj.sAddressx,' ') , ''), " +
                " 	IFNULL(CONCAT(l.sBrgyName,' '), ''), " +
                " 	IFNULL(CONCAT(k.sTownName, ', '),''), " +
                " 	IFNULL(CONCAT(m.sProvName),'') )	, '') AS sOwnerAdd " + //37 
                " , IFNULL(CONCAT(IFNULL(CONCAT(nn.sHouseNox,' ') , ''), IFNULL(CONCAT(nn.sAddressx,' ') , ''), " +
                " 	IFNULL(CONCAT(p.sBrgyName,' '), ''), " +
                " 	IFNULL(CONCAT(o.sTownName, ', '),''), " +
                " 	IFNULL(CONCAT(q.sProvName),'') )	, '') AS sCoOwnerA " + //38 
                " ,CASE " +
                    "    WHEN a.cSoldStat = '0' THEN 'NON SALES CUSTOMER' " +
                    "    WHEN a.cSoldStat = '1' THEN 'AVAILABLE FOR SALE' " +
                    "    WHEN a.cSoldStat = '2' THEN 'VSP' " +
                    "    WHEN a.cSoldStat = '3' THEN 'SOLD' " +
                    "    ELSE '' " +
                    " END AS sVhclStat  " + //39
                " , IFNULL(r.sReferNox,'') sUdrNoxxx " + //40
                " , IFNULL(r.dTransact,'') sUdrDatex " + //41
                " , IFNULL(s.sCompnyNm,'') sSoldToxx " + //42
                "   FROM vehicle_serial a " + 
                "   LEFT JOIN vehicle_serial_registration b ON a.sSerialID = b.sSerialID  " +
                "   LEFT JOIN vehicle_master c ON c.sVhclIDxx = a.sVhclIDxx  " +
                "   LEFT JOIN vehicle_make d ON d.sMakeIDxx = c.sMakeIDxx  " +
                "   LEFT JOIN vehicle_model e ON e.sModelIDx = c.sModelIDx  " +
                "   LEFT JOIN vehicle_type f ON f.sTypeIDxx = c.sTypeIDxx  " +
                "   LEFT JOIN vehicle_color g ON g.sColorIDx = c.sColorIDx  " +
                "   LEFT JOIN client_master h ON h.sClientID = a.sClientID  " +
                "   LEFT JOIN client_master i ON i.sClientID = a.sCoCltIDx  " +
                // Owner Address
                "   LEFT JOIN client_address j ON j.sClientID = a.sClientID AND j.cPrimaryx = '1' " + //AND h.cRecdStat = '1' " +
                "   LEFT JOIN addresses jj ON jj.sAddrssID = j.sAddrssID " + 
                "   LEFT JOIN TownCity k on k.sTownIDxx = jj.sTownIDxx " + //AND i.cRecdStat = '1'
                "   LEFT JOIN barangay l ON l.sBrgyIDxx = jj.sBrgyIDxx and l.sTownIDxx = jj.sTownIDxx " + // AND j.cRecdStat = '1'  " +
                "   LEFT JOIN Province m ON m.sProvIDxx = k.sProvIDxx " + // and k.cRecdStat = '1' " +
                //Co Owner Address
                "   LEFT JOIN client_address n ON n.sClientID = a.sCoCltIDx AND n.cPrimaryx = '1' " + //AND h.cRecdStat = '1' " +
                "   LEFT JOIN addresses nn ON nn.sAddrssID = n.sAddrssID " + 
                "   LEFT JOIN TownCity o on o.sTownIDxx = nn.sTownIDxx " + //AND i.cRecdStat = '1'
                "   LEFT JOIN barangay p ON p.sBrgyIDxx = nn.sBrgyIDxx and p.sTownIDxx = nn.sTownIDxx " + // AND j.cRecdStat = '1'  " +
                "   LEFT JOIN Province q ON q.sProvIDxx = o.sProvIDxx "  +// and k.cRecdStat = '1' " +
                //UDR INFO
                "   LEFT JOIN udr_master r ON r.sSerialID = a.sSerialID  AND r.sClientID = a.sClientID AND r.cTranStat = '1'"  +
                "   LEFT JOIN client_master s ON s.sClientID = r.sClientID "  
                + " WHERE 0=1";
        
        
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
