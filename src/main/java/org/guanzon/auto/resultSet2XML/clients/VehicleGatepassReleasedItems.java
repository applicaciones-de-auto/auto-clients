package org.guanzon.auto.resultSet2XML.clients;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arsiela
 */
public class VehicleGatepassReleasedItems {
    
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Vehicle_Gatepass_Released_Items.xml");
        String lsSQL =    " SELECT "                                             
                        + "    a.sTransNox "                                     
                        + "  , a.sItemType "                                     
                        + "  , a.sLaborCde "                                     
                        + "  , a.sStockIDx "                                     
                        + "  , a.nQuantity "                                     
                        + "  , a.nReleased "                                     
                        + "  , b.sLaborDsc "                                     
                        + "  , c.sDescript AS sStockDsc "                                     
                        + " FROM vehicle_released_items a "                      
                        + " LEFT JOIN labor b ON b.sLaborCde = a.sLaborCde "     
                        + " LEFT JOIN inventory c ON c.sStockIDx = a.sStockIDx "
                        + " WHERE 0=1";
        
        System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "vehicle_released_items", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
