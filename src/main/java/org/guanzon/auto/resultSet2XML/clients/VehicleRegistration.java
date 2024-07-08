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
public class VehicleRegistration  {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Vehicle_Registration.xml");
        
        
        String lsSQL =    "  SELECT     "                        
                        + "  sSerialID  " //1                    
                        + ", sCSRValNo  " //2                    
                        + ", sPNPClrNo  " //3                    
                        + ", sCRNoxxxx  " //4                    
                        + ", sCRENoxxx  " //5                    
                        + ", sRegORNox  " //6                    
                        + ", sFileNoxx  " //7                    
                        + ", sPlateNox  " //8                    
                        + ", dRegister  " //9                    
                        + ", sPlaceReg  " //10                   
                        + ", sEntryByx  " //11                   
                        + ", dEntryDte  " //12                   
                        + ", sModified  " //13                   
                        + ", dModified  " //14                   
                        + "FROM vehicle_serial_registration "    
                        + " WHERE 0=1 " ;
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "vehicle_registration", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
