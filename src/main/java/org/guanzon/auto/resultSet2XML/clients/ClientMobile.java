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
public class ClientMobile  {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Client_Mobile.xml");
        
        
        String lsSQL =    "  SELECT     "         
                        + "  sMobileID  "//1      
                        + ", sClientID  "//2      
                        + ", sMobileNo  "//3      
                        + ", cMobileTp  "//4      
                        + ", cOwnerxxx  "//5      
                        + ", cIncdMktg  "//6      
                        + ", cVerified  "//7      
                        + ", dLastVeri  "//8      
                        + ", cInvalidx  "//9      
                        + ", dInvalidx  "//10     
                        + ", cPrimaryx  "//11     
                        + ", cSubscrbr  "//12     
                        + ", sRemarksx  "//13     
                        + ", cRecdStat  "//14     
                        + ", sEntryByx  "//15     
                        + ", dEntryDte  "//16     
                        + ", sModified  "//17     
                        + ", dModified  "//18     
                        + "FROM client_mobile  "
                        + "WHERE 0=1 ";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "client_mobile", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
