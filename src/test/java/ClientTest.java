
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.clients.Client;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arsiela
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClientTest {
    static Client model;
    JSONObject json;
    boolean result;
    
    public ClientTest(){}
    
    @BeforeClass
    public static void setUpClass() {   
        
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
            System.err.println(instance.getMessage() + instance.getErrMsg());
            System.exit(1);
        }
        
        System.out.println("Connected");
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
        
        
        JSONObject json;
        
        System.out.println("sBranch code = " + instance.getBranchCode());
        model = new Client(instance,false, instance.getBranchCode());
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
    /**
     * COMMENTED TESTING TO CLEAN AND BUILD PROPERLY
     * WHEN YOU WANT TO CHECK KINDLY UNCOMMENT THE TESTING CASES (@Test).
     * ARSIELA 07-29-2024
     */
/*    
    @Test
    public void test01NewRecord() {
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------NEW RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.newRecord();
        if ("success".equals((String) json.get("result"))){
            json = model.setMaster("cClientTp","0");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sLastName","Lavarias");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sFrstName","Arsiela");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sMiddName","Reloza");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sSuffixNm","");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sMaidenNm","TEST");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }


           // String sCompnyNm = model.getMaster("sLastName") + ", " + model.getMaster("sFrstName") + " " + model.getMaster("sSuffixNm") +  " " + model.getMaster("sMiddName");
    //        String sCompnyNm = "CARS PANGASINAN";
    //        json = model.setMaster("sCompnyNm", sCompnyNm);
    //        if ("error".equals((String) json.get("result"))){
    //            System.err.println((String) json.get("message"));
    //            System.exit(1);
    //        }

            json = model.setMaster("cGenderCd", "0");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("cCvilStat", "0");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sCitizenx", "01");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("dBirthDte", "1990-06-03");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sBirthPlc", "0335");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }       

            json = model.setMaster("sAddlInfo", "TEST COMPANY");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            } 

            json = model.setMaster("sSpouseID", "");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            System.out.println("mobile size = " + model.getMobileList().size());
            for(int lnctr = 0; lnctr < model.getMobileList().size(); lnctr++){
                model.setMobile(lnctr, "sMobileNo", ("09123456785"));
            }

            System.out.println("address size = " + model.getAddressList().size());
            for(int lnctr = 0; lnctr < model.getAddressList().size(); lnctr++){
                model.setAddress(lnctr, "sHouseNox", "123");
                model.setAddress(lnctr, "sAddressx", "sample");
                model.setAddress(lnctr, "sBrgyIDxx", "1200145");
                model.setAddress(lnctr, "sTownIDxx", "0335");
                model.setAddress(lnctr, "nLatitude", 0.0);
                model.setAddress(lnctr, "nLongitud", 0.0);


                model.setAddress(lnctr, "sBrgyName", "CAWAYAN BOGTONG");
                model.setAddress(lnctr, "sTownName", "MALASIQUI");
                model.setAddress(lnctr, "sProvName", "PANGASINAN");


                String lsFullAddress = (String) model.getAddress(lnctr, "sHouseNox") + (String) model.getAddress(lnctr, "sAddressx")
                                     + (String) model.getAddress(lnctr, "sBrgyName") + (String) model.getAddress(lnctr, "sTownName")
                                     + (String) model.getAddress(lnctr, "sProvName");
                model.checkClientAddress(lsFullAddress, lnctr, true);
            }

            System.out.println("email size = " + model.getEmailList().size());
            for(int lnctr = 0; lnctr < model.getEmailList().size(); lnctr++){
                model.setEmail(lnctr, "sEmailAdd", "samplemail@gmail.com");
                model.setEmail(lnctr, "cOwnerxxx", "0");
                model.setEmail(lnctr, "cPrimaryx", "0");
                model.setEmail(lnctr, "cRecdStat", "0"); 
            }

            System.out.println("social media size = " + model.getSocialMediaList().size());
            for(int lnctr = 0; lnctr < model.getSocialMediaList().size(); lnctr++){
                model.setSocialMed(lnctr, "sAccountx", "fb1@facebook.com");
                model.setSocialMed(lnctr, "cSocialTp", "0");         
            }

    //        System.out.println("--------------------------------------------------------------------");
    //        System.out.println("------------------------------COMPANY RECORD--------------------------------------");
    //        System.out.println("--------------------------------------------------------------------");
    //
    //        System.out.println("mobile size = " + model.getMobileList().size());
    //        for(int lnctr = 0; lnctr < model.getMobileList().size(); lnctr++){
    //            model.setMobile(lnctr, "sMobileNo", ("09305864899"));
    //        }
    //        
    //        System.out.println("address size = " + model.getAddressList().size());
    //        for(int lnctr = 0; lnctr < model.getAddressList().size(); lnctr++){
    //            model.setAddress(lnctr, "sHouseNox", "111");
    //            model.setAddress(lnctr, "sAddressx", "SAGUR");
    //            model.setAddress(lnctr, "sBrgyIDxx", "1200145");
    //            model.setAddress(lnctr, "sTownIDxx", "0335");
    //            model.setAddress(lnctr, "nLatitude", 0.0);
    //            model.setAddress(lnctr, "nLongitud", 0.0);
    //            
    //            
    //            model.setAddress(lnctr, "sBrgyName", "CAWAYAN BOGTONG");
    //            model.setAddress(lnctr, "sTownName", "MALASIQUI");
    //            model.setAddress(lnctr, "sProvName", "PANGASINAN");
    //            
    //            
    //            String lsFullAddress = (String) model.getAddress(lnctr, "sHouseNox") + (String) model.getAddress(lnctr, "sAddressx")
    //                                 + (String) model.getAddress(lnctr, "sBrgyName") + (String) model.getAddress(lnctr, "sTownName")
    //                                 + (String) model.getAddress(lnctr, "sProvName");
    //            model.checkClientAddress(lsFullAddress, lnctr, true);
    //        }
    //        
    //        System.out.println("email size = " + model.getEmailList().size());
    //        for(int lnctr = 0; lnctr < model.getEmailList().size(); lnctr++){
    //            model.setEmail(lnctr, "sEmailAdd", "company@gmail.com");
    //            model.setEmail(lnctr, "cOwnerxxx", "0");
    //            model.setEmail(lnctr, "cPrimaryx", "0");
    //            model.setEmail(lnctr, "cRecdStat", "0"); 
    //        }
    //        
    //        System.out.println("social media size = " + model.getSocialMediaList().size());
    //        for(int lnctr = 0; lnctr < model.getSocialMediaList().size(); lnctr++){
    //            model.setSocialMed(lnctr, "sAccountx", "company@facebook.com");
    //            model.setSocialMed(lnctr, "cSocialTp", "0");         
    //        }
                 
        } else {
            System.err.println("result = " + (String) json.get("result"));
            fail((String) json.get("message"));
        }
        
    }
    
    @Test
    public void test01NewRecordSave(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------NEW RECORD SAVING--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.saveRecord();
        System.err.println((String) json.get("message"));
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            result = false;
        } else {
            System.out.println((String) json.get("message"));
            result = true;
        }
        assertTrue(result);
    }
    
    @Test
    public void test02OpenRecord(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------RETRIEVAL--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.openRecord("M00124000001");
        
        if (!"success".equals((String) json.get("result"))){
            result = false;
        } else {
            System.out.println("--------------------------------------------------------------------");
            System.out.println("CLIENT MASTER");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("sClientID  :  " + model.getMaster("sClientID"));
            System.out.println("sLastName  :  " + model.getMaster("sLastName"));
            System.out.println("sFrstName  :  " + model.getMaster("sFrstName"));
            System.out.println("sMiddName  :  " + model.getMaster("sMiddName"));
            System.out.println("sMaidenNm  :  " + model.getMaster("sMaidenNm"));
            System.out.println("sSuffixNm  :  " + model.getMaster("sSuffixNm"));
            System.out.println("sTitlexxx  :  " + model.getMaster("sTitlexxx"));
            System.out.println("cGenderCd  :  " + model.getMaster("cGenderCd"));
            System.out.println("cCvilStat  :  " + model.getMaster("cCvilStat"));
            System.out.println("sCitizenx  :  " + model.getMaster("sCitizenx"));
            System.out.println("dBirthDte  :  " + model.getMaster("dBirthDte"));
            System.out.println("sBirthPlc  :  " + model.getMaster("sBirthPlc"));
            System.out.println("sTaxIDNox  :  " + model.getMaster("sTaxIDNox"));
            System.out.println("sLTOIDxxx  :  " + model.getMaster("sLTOIDxxx"));
            System.out.println("sAddlInfo  :  " + model.getMaster("sAddlInfo"));
            System.out.println("sCompnyNm  :  " + model.getMaster("sCompnyNm"));
            System.out.println("sClientNo  :  " + model.getMaster("sClientNo"));
            System.out.println("cClientTp  :  " + model.getMaster("cClientTp"));
            System.out.println("cRecdStat  :  " + model.getMaster("cRecdStat"));
            System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
            System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
            System.out.println("sModified  :  " + model.getMaster("sModified"));
            System.out.println("dModified  :  " + model.getMaster("dModified"));
            System.out.println("sCntryNme  :  " + model.getMaster("sCntryNme"));
            System.out.println("sTownName  :  " + model.getMaster("sTownName"));
            System.out.println("sCustName  :  " + model.getMaster("sCustName"));
            System.out.println("sSpouseID  :  " + model.getMaster("sSpouseID"));
            System.out.println("sSpouseNm  :  " + model.getMaster("sSpouseNm"));
            System.out.println("sAddressx  :  " + model.getMaster("sAddressx"));


            System.out.println("--------------------------------------------------------------------");
            System.out.println("ADDRESS");
            System.out.println("--------------------------------------------------------------------");
            for(int lnCtr = 0;lnCtr <= model.getAddressList().size()-1; lnCtr++){
                System.out.println("sAddrssID  :  " + model.getAddress(lnCtr, "sAddrssID"));
                System.out.println("sClientID  :  " + model.getAddress(lnCtr, "sClientID"));
                System.out.println("sHouseNox  :  " + model.getAddress(lnCtr, "sHouseNox"));
                System.out.println("sAddressx  :  " + model.getAddress(lnCtr, "sAddressx"));
                System.out.println("sTownIDxx  :  " + model.getAddress(lnCtr, "sTownIDxx"));
                System.out.println("sBrgyIDxx  :  " + model.getAddress(lnCtr, "sBrgyIDxx"));
                System.out.println("sZippCode  :  " + model.getAddress(lnCtr, "sZippCode"));
                System.out.println("nLatitude  :  " + model.getAddress(lnCtr, "nLatitude"));
                System.out.println("nLongitud  :  " + model.getAddress(lnCtr, "nLongitud"));
                System.out.println("sRemarksx  :  " + model.getAddress(lnCtr, "sRemarksx"));
                System.out.println("cOfficexx  :  " + model.getAddress(lnCtr, "cOfficexx"));
                System.out.println("cProvince  :  " + model.getAddress(lnCtr, "cProvince"));
                System.out.println("cPrimaryx  :  " + model.getAddress(lnCtr, "cPrimaryx"));
                System.out.println("cBillingx  :  " + model.getAddress(lnCtr, "cBillingx"));
                System.out.println("cShipping  :  " + model.getAddress(lnCtr, "cShipping"));
                System.out.println("cCurrentx  :  " + model.getAddress(lnCtr, "cCurrentx"));
                System.out.println("cRecdStat  :  " + model.getAddress(lnCtr, "cRecdStat"));
                System.out.println("sEntryByx  :  " + model.getAddress(lnCtr, "sEntryByx"));
                System.out.println("dEntryDte  :  " + model.getAddress(lnCtr, "dEntryDte"));
                System.out.println("sModified  :  " + model.getAddress(lnCtr, "sModified"));
                System.out.println("dModified  :  " + model.getAddress(lnCtr, "dModified"));
                System.out.println("sProvName  :  " + model.getAddress(lnCtr, "sProvName"));
                System.out.println("sBrgyName  :  " + model.getAddress(lnCtr, "sBrgyName"));
                System.out.println("sTownName  :  " + model.getAddress(lnCtr, "sTownName"));
                System.out.println("sProvIDxx  :  " + model.getAddress(lnCtr, "sProvIDxx"));
            }
            System.out.println("--------------------------------------------------------------------");
            System.out.println("MOBILE");
            System.out.println("--------------------------------------------------------------------");
            for(int lnCtr = 0;lnCtr <= model.getMobileList().size()-1; lnCtr++){
                System.out.println("sMobileID  :  " + model.getMobile(lnCtr, "sMobileID"));
                System.out.println("sClientID  :  " + model.getMobile(lnCtr, "sClientID"));
                System.out.println("sMobileNo  :  " + model.getMobile(lnCtr, "sMobileNo"));
                System.out.println("cMobileTp  :  " + model.getMobile(lnCtr, "cMobileTp"));
                System.out.println("cOwnerxxx  :  " + model.getMobile(lnCtr, "cOwnerxxx"));
                System.out.println("cIncdMktg  :  " + model.getMobile(lnCtr, "cIncdMktg"));
                System.out.println("cVerified  :  " + model.getMobile(lnCtr, "cVerified"));
                System.out.println("dLastVeri  :  " + model.getMobile(lnCtr, "dLastVeri"));
                System.out.println("cInvalidx  :  " + model.getMobile(lnCtr, "cInvalidx"));
                System.out.println("dInvalidx  :  " + model.getMobile(lnCtr, "dInvalidx"));
                System.out.println("cPrimaryx  :  " + model.getMobile(lnCtr, "cPrimaryx"));
                System.out.println("cSubscrbr  :  " + model.getMobile(lnCtr, "cSubscrbr"));
                System.out.println("sRemarksx  :  " + model.getMobile(lnCtr, "sRemarksx"));
                System.out.println("cRecdStat  :  " + model.getMobile(lnCtr, "cRecdStat"));
                System.out.println("sEntryByx  :  " + model.getMobile(lnCtr, "sEntryByx"));
                System.out.println("dEntryDte  :  " + model.getMobile(lnCtr, "dEntryDte"));
                System.out.println("sModified  :  " + model.getMobile(lnCtr, "sModified"));
                System.out.println("dModified  :  " + model.getMobile(lnCtr, "dModified"));
            }

            System.out.println("--------------------------------------------------------------------");
            System.out.println("EMAIL");
            System.out.println("--------------------------------------------------------------------");
            for(int lnCtr = 0;lnCtr <= model.getEmailList().size()-1; lnCtr++){
                System.out.println("sEmailIDx  :  " + model.getEmail(lnCtr, "sEmailIDx"));
                System.out.println("sClientID  :  " + model.getEmail(lnCtr, "sClientID"));
                System.out.println("sEmailAdd  :  " + model.getEmail(lnCtr, "sEmailAdd"));
                System.out.println("cOwnerxxx  :  " + model.getEmail(lnCtr, "cOwnerxxx"));
                System.out.println("cPrimaryx  :  " + model.getEmail(lnCtr, "cPrimaryx"));
                System.out.println("cRecdStat  :  " + model.getEmail(lnCtr, "cRecdStat"));
                System.out.println("sEntryByx  :  " + model.getEmail(lnCtr, "sEntryByx"));
                System.out.println("dEntryDte  :  " + model.getEmail(lnCtr, "dEntryDte"));
                System.out.println("sModified  :  " + model.getEmail(lnCtr, "sModified"));
                System.out.println("dModified  :  " + model.getEmail(lnCtr, "dModified"));

            }
            System.out.println("--------------------------------------------------------------------");
            System.out.println("SOCIAL MEDIA");
            System.out.println("--------------------------------------------------------------------");
            for(int lnCtr = 0;lnCtr <= model.getSocialMediaList().size()-1; lnCtr++){
                System.out.println("sSocialID : " + model.getSocialMed(lnCtr, "sSocialID"));
                System.out.println("sClientID : " + model.getSocialMed(lnCtr, "sClientID"));
                System.out.println("sAccountx : " + model.getSocialMed(lnCtr, "sAccountx"));
                System.out.println("cSocialTp : " + model.getSocialMed(lnCtr, "cSocialTp"));
                System.out.println("cRecdStat : " + model.getSocialMed(lnCtr, "cRecdStat"));
                System.out.println("sEntryByx : " + model.getSocialMed(lnCtr, "sEntryByx"));
                System.out.println("dEntryDte : " + model.getSocialMed(lnCtr, "dEntryDte"));
                System.out.println("sModified : " + model.getSocialMed(lnCtr, "sModified"));
                System.out.println("dModified : " + model.getSocialMed(lnCtr, "dModified"));
            }
            result = true;
        }
        assertTrue(result);
    }
    
    @Test
    public void test03UpdateRecord(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------UPDATE RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.updateRecord();
        System.err.println((String) json.get("message"));
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            result = false;
        } else {
            result = true;
        }
        
        json = model.setMaster("sLastName","TEST");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("sFrstName","JOSH");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        json = model.setMaster("sMiddName","EDIT");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        System.out.println("mobile size = " + model.getMobileList().size());
        for(int lnctr = 0; lnctr < model.getMobileList().size(); lnctr++){
            model.setMobile(lnctr, "sMobileNo", ("0930111111" + String.valueOf(lnctr)));
        }
        
        System.out.println("address size = " + model.getAddressList().size());
        for(int lnctr = 0; lnctr < model.getAddressList().size(); lnctr++){
            model.setAddress(lnctr, "sHouseNox", "333");
            model.setAddress(lnctr, "sAddressx", "53355");
            model.setAddress(lnctr, "sBrgyIDxx", "1200145");
            model.setAddress(lnctr, "sTownIDxx", "0335");
            model.setAddress(lnctr, "nLatitude", 0.0);
            model.setAddress(lnctr, "nLongitud", 0.0);
            
            
            model.setAddress(lnctr, "sBrgyName", "CAWAYAN BOGTONG");
            model.setAddress(lnctr, "sTownName", "MALASIQUI");
            model.setAddress(lnctr, "sProvName", "PANGASINAN");
            
            
            String lsFullAddress = (String) model.getAddress(lnctr, "sHouseNox") + (String) model.getAddress(lnctr, "sAddressx")
                                 + (String) model.getAddress(lnctr, "sBrgyName") + (String) model.getAddress(lnctr, "sTownName")
                                 + (String) model.getAddress(lnctr, "sProvName");
            model.checkClientAddress(lsFullAddress, lnctr, true);
        }
        
        System.out.println("email size = " + model.getEmailList().size());
        for(int lnctr = 0; lnctr < model.getEmailList().size(); lnctr++){
            model.setEmail(lnctr, "sEmailAdd", "555@gmail.com");
            model.setEmail(lnctr, "cOwnerxxx", "0");
            model.setEmail(lnctr, "cPrimaryx", "0");
            model.setEmail(lnctr, "cRecdStat", "0"); 
        }
        
        System.out.println("social media size = " + model.getSocialMediaList().size());
        for(int lnctr = 0; lnctr < model.getSocialMediaList().size(); lnctr++){
            model.setSocialMed(lnctr, "sAccountx", "555@facebook.com");
            model.setSocialMed(lnctr, "cSocialTp", "0");         
        }
        
        assertTrue(result);
        //assertFalse(result);
    }
    
    @Test
    public void test03UpdateRecordSave(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------UPDATE RECORD SAVING--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.saveRecord();
        System.err.println((String) json.get("message"));
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            result = false;
        } else {
            System.out.println((String) json.get("message"));
            result = true;
        }
        assertTrue(result);
        //assertFalse(result);
    }
*/

/*******************************************************************************************************************************/    
//    public static void main(String [] args){
//        String path;
//        if(System.getProperty("os.name").toLowerCase().contains("win")){
//            path = "D:/GGC_Maven_Systems";
//        }
//        else{
//            path = "/srv/GGC_Maven_Systems";
//        }
//        System.setProperty("sys.default.path.config", path);
//
//        GRider instance = new GRider("gRider");
//
//        if (!instance.logUser("gRider", "M001000001")){
//            System.err.println(instance.getErrMsg());
//            System.exit(1);
//        }
//
//        System.out.println("Connected");
//        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
//        
//        
//        JSONObject json;
//        
//        System.out.println("sBranch code = " + instance.getBranchCode());
//        Client model = new Client(instance, false, instance.getBranchCode());
//        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.newRecord();
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        System.err.println("result = " + (String) json.get("result"));
//        
//        json = model.setMaster("cClientTp","0");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sLastName","Lavarias");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sFrstName","Arsiela");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sMiddName","Reloza");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sSuffixNm","");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sMaidenNm","TEST");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
        
        
       // String sCompnyNm = model.getMaster("sLastName") + ", " + model.getMaster("sFrstName") + " " + model.getMaster("sSuffixNm") +  " " + model.getMaster("sMiddName");
//        String sCompnyNm = "CARS PANGASINAN";
//        json = model.setMaster("sCompnyNm", sCompnyNm);
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
        
//        json = model.setMaster("cGenderCd", "0");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("cCvilStat", "0");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sCitizenx", "01");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("dBirthDte", "1990-06-03");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sBirthPlc", "0335");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }       
//
//        json = model.setMaster("sAddlInfo", "TEST COMPANY");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        } 
//        
//        json = model.setMaster("sSpouseID", "");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        System.out.println("mobile size = " + model.getMobileList().size());
//        for(int lnctr = 0; lnctr < model.getMobileList().size(); lnctr++){
//            model.setMobile(lnctr, "sMobileNo", ("09123456785"));
//        }
//        
//        System.out.println("address size = " + model.getAddressList().size());
//        for(int lnctr = 0; lnctr < model.getAddressList().size(); lnctr++){
//            model.setAddress(lnctr, "sHouseNox", "123");
//            model.setAddress(lnctr, "sAddressx", "sample");
//            model.setAddress(lnctr, "sBrgyIDxx", "1200145");
//            model.setAddress(lnctr, "sTownIDxx", "0335");
//            model.setAddress(lnctr, "nLatitude", 0.0);
//            model.setAddress(lnctr, "nLongitud", 0.0);
//            
//            
//            model.setAddress(lnctr, "sBrgyName", "CAWAYAN BOGTONG");
//            model.setAddress(lnctr, "sTownName", "MALASIQUI");
//            model.setAddress(lnctr, "sProvName", "PANGASINAN");
//            
//            
//            String lsFullAddress = (String) model.getAddress(lnctr, "sHouseNox") + (String) model.getAddress(lnctr, "sAddressx")
//                                 + (String) model.getAddress(lnctr, "sBrgyName") + (String) model.getAddress(lnctr, "sTownName")
//                                 + (String) model.getAddress(lnctr, "sProvName");
//            model.checkClientAddress(lsFullAddress, lnctr, true);
//        }
//        
//        System.out.println("email size = " + model.getEmailList().size());
//        for(int lnctr = 0; lnctr < model.getEmailList().size(); lnctr++){
//            model.setEmail(lnctr, "sEmailAdd", "samplemail@gmail.com");
//            model.setEmail(lnctr, "cOwnerxxx", "0");
//            model.setEmail(lnctr, "cPrimaryx", "0");
//            model.setEmail(lnctr, "cRecdStat", "0"); 
//        }
//        
//        System.out.println("social media size = " + model.getSocialMediaList().size());
//        for(int lnctr = 0; lnctr < model.getSocialMediaList().size(); lnctr++){
//            model.setSocialMed(lnctr, "sAccountx", "fb1@facebook.com");
//            model.setSocialMed(lnctr, "cSocialTp", "0");         
//        }

//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------COMPANY RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//
//        System.out.println("mobile size = " + model.getMobileList().size());
//        for(int lnctr = 0; lnctr < model.getMobileList().size(); lnctr++){
//            model.setMobile(lnctr, "sMobileNo", ("09305864899"));
//        }
//        
//        System.out.println("address size = " + model.getAddressList().size());
//        for(int lnctr = 0; lnctr < model.getAddressList().size(); lnctr++){
//            model.setAddress(lnctr, "sHouseNox", "111");
//            model.setAddress(lnctr, "sAddressx", "SAGUR");
//            model.setAddress(lnctr, "sBrgyIDxx", "1200145");
//            model.setAddress(lnctr, "sTownIDxx", "0335");
//            model.setAddress(lnctr, "nLatitude", 0.0);
//            model.setAddress(lnctr, "nLongitud", 0.0);
//            
//            
//            model.setAddress(lnctr, "sBrgyName", "CAWAYAN BOGTONG");
//            model.setAddress(lnctr, "sTownName", "MALASIQUI");
//            model.setAddress(lnctr, "sProvName", "PANGASINAN");
//            
//            
//            String lsFullAddress = (String) model.getAddress(lnctr, "sHouseNox") + (String) model.getAddress(lnctr, "sAddressx")
//                                 + (String) model.getAddress(lnctr, "sBrgyName") + (String) model.getAddress(lnctr, "sTownName")
//                                 + (String) model.getAddress(lnctr, "sProvName");
//            model.checkClientAddress(lsFullAddress, lnctr, true);
//        }
//        
//        System.out.println("email size = " + model.getEmailList().size());
//        for(int lnctr = 0; lnctr < model.getEmailList().size(); lnctr++){
//            model.setEmail(lnctr, "sEmailAdd", "company@gmail.com");
//            model.setEmail(lnctr, "cOwnerxxx", "0");
//            model.setEmail(lnctr, "cPrimaryx", "0");
//            model.setEmail(lnctr, "cRecdStat", "0"); 
//        }
//        
//        System.out.println("social media size = " + model.getSocialMediaList().size());
//        for(int lnctr = 0; lnctr < model.getSocialMediaList().size(); lnctr++){
//            model.setSocialMed(lnctr, "sAccountx", "company@facebook.com");
//            model.setSocialMed(lnctr, "cSocialTp", "0");         
//        }
        
////        System.out.println("--------------------------------------------------------------------");
////        System.out.println("------------------------------NEW RECORD SAVING--------------------------------------");
////        System.out.println("--------------------------------------------------------------------");
        
        //model.searchRecord("", false);
        
//        json = model.saveRecord();
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        } else {
//            System.out.println((String) json.get("message"));
//            System.exit(0);
//        }
        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------RETRIEVAL--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        //retrieval
//        json = model.openRecord("M00124000001");
//        System.err.println((String) json.get("message"));
//        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("CLIENT MASTER");
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("sClientID  :  " + model.getMaster("sClientID"));
//        System.out.println("sLastName  :  " + model.getMaster("sLastName"));
//        System.out.println("sFrstName  :  " + model.getMaster("sFrstName"));
//        System.out.println("sMiddName  :  " + model.getMaster("sMiddName"));
//        System.out.println("sMaidenNm  :  " + model.getMaster("sMaidenNm"));
//        System.out.println("sSuffixNm  :  " + model.getMaster("sSuffixNm"));
//        System.out.println("sTitlexxx  :  " + model.getMaster("sTitlexxx"));
//        System.out.println("cGenderCd  :  " + model.getMaster("cGenderCd"));
//        System.out.println("cCvilStat  :  " + model.getMaster("cCvilStat"));
//        System.out.println("sCitizenx  :  " + model.getMaster("sCitizenx"));
//        System.out.println("dBirthDte  :  " + model.getMaster("dBirthDte"));
//        System.out.println("sBirthPlc  :  " + model.getMaster("sBirthPlc"));
//        System.out.println("sTaxIDNox  :  " + model.getMaster("sTaxIDNox"));
//        System.out.println("sLTOIDxxx  :  " + model.getMaster("sLTOIDxxx"));
//        System.out.println("sAddlInfo  :  " + model.getMaster("sAddlInfo"));
//        System.out.println("sCompnyNm  :  " + model.getMaster("sCompnyNm"));
//        System.out.println("sClientNo  :  " + model.getMaster("sClientNo"));
//        System.out.println("cClientTp  :  " + model.getMaster("cClientTp"));
//        System.out.println("cRecdStat  :  " + model.getMaster("cRecdStat"));
//        System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
//        System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
//        System.out.println("sModified  :  " + model.getMaster("sModified"));
//        System.out.println("dModified  :  " + model.getMaster("dModified"));
//        System.out.println("sCntryNme  :  " + model.getMaster("sCntryNme"));
//        System.out.println("sTownName  :  " + model.getMaster("sTownName"));
//        System.out.println("sCustName  :  " + model.getMaster("sCustName"));
//        System.out.println("sSpouseID  :  " + model.getMaster("sSpouseID"));
//        System.out.println("sSpouseNm  :  " + model.getMaster("sSpouseNm"));
//        System.out.println("sAddressx  :  " + model.getMaster("sAddressx"));
//
//        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("ADDRESS");
//        System.out.println("--------------------------------------------------------------------");
//        for(int lnCtr = 0;lnCtr <= model.getAddressList().size()-1; lnCtr++){
//            System.out.println("sAddrssID  :  " + model.getAddress(lnCtr, "sAddrssID"));
//            System.out.println("sClientID  :  " + model.getAddress(lnCtr, "sClientID"));
//            System.out.println("sHouseNox  :  " + model.getAddress(lnCtr, "sHouseNox"));
//            System.out.println("sAddressx  :  " + model.getAddress(lnCtr, "sAddressx"));
//            System.out.println("sTownIDxx  :  " + model.getAddress(lnCtr, "sTownIDxx"));
//            System.out.println("sBrgyIDxx  :  " + model.getAddress(lnCtr, "sBrgyIDxx"));
//            System.out.println("sZippCode  :  " + model.getAddress(lnCtr, "sZippCode"));
//            System.out.println("nLatitude  :  " + model.getAddress(lnCtr, "nLatitude"));
//            System.out.println("nLongitud  :  " + model.getAddress(lnCtr, "nLongitud"));
//            System.out.println("sRemarksx  :  " + model.getAddress(lnCtr, "sRemarksx"));
//            System.out.println("cOfficexx  :  " + model.getAddress(lnCtr, "cOfficexx"));
//            System.out.println("cProvince  :  " + model.getAddress(lnCtr, "cProvince"));
//            System.out.println("cPrimaryx  :  " + model.getAddress(lnCtr, "cPrimaryx"));
//            System.out.println("cBillingx  :  " + model.getAddress(lnCtr, "cBillingx"));
//            System.out.println("cShipping  :  " + model.getAddress(lnCtr, "cShipping"));
//            System.out.println("cCurrentx  :  " + model.getAddress(lnCtr, "cCurrentx"));
//            System.out.println("cRecdStat  :  " + model.getAddress(lnCtr, "cRecdStat"));
//            System.out.println("sEntryByx  :  " + model.getAddress(lnCtr, "sEntryByx"));
//            System.out.println("dEntryDte  :  " + model.getAddress(lnCtr, "dEntryDte"));
//            System.out.println("sModified  :  " + model.getAddress(lnCtr, "sModified"));
//            System.out.println("dModified  :  " + model.getAddress(lnCtr, "dModified"));
//            System.out.println("sProvName  :  " + model.getAddress(lnCtr, "sProvName"));
//            System.out.println("sBrgyName  :  " + model.getAddress(lnCtr, "sBrgyName"));
//            System.out.println("sTownName  :  " + model.getAddress(lnCtr, "sTownName"));
//            System.out.println("sProvIDxx  :  " + model.getAddress(lnCtr, "sProvIDxx"));
//        }
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("MOBILE");
//        System.out.println("--------------------------------------------------------------------");
//        for(int lnCtr = 0;lnCtr <= model.getMobileList().size()-1; lnCtr++){
//            System.out.println("sMobileID  :  " + model.getMobile(lnCtr, "sMobileID"));
//            System.out.println("sClientID  :  " + model.getMobile(lnCtr, "sClientID"));
//            System.out.println("sMobileNo  :  " + model.getMobile(lnCtr, "sMobileNo"));
//            System.out.println("cMobileTp  :  " + model.getMobile(lnCtr, "cMobileTp"));
//            System.out.println("cOwnerxxx  :  " + model.getMobile(lnCtr, "cOwnerxxx"));
//            System.out.println("cIncdMktg  :  " + model.getMobile(lnCtr, "cIncdMktg"));
//            System.out.println("cVerified  :  " + model.getMobile(lnCtr, "cVerified"));
//            System.out.println("dLastVeri  :  " + model.getMobile(lnCtr, "dLastVeri"));
//            System.out.println("cInvalidx  :  " + model.getMobile(lnCtr, "cInvalidx"));
//            System.out.println("dInvalidx  :  " + model.getMobile(lnCtr, "dInvalidx"));
//            System.out.println("cPrimaryx  :  " + model.getMobile(lnCtr, "cPrimaryx"));
//            System.out.println("cSubscrbr  :  " + model.getMobile(lnCtr, "cSubscrbr"));
//            System.out.println("sRemarksx  :  " + model.getMobile(lnCtr, "sRemarksx"));
//            System.out.println("cRecdStat  :  " + model.getMobile(lnCtr, "cRecdStat"));
//            System.out.println("sEntryByx  :  " + model.getMobile(lnCtr, "sEntryByx"));
//            System.out.println("dEntryDte  :  " + model.getMobile(lnCtr, "dEntryDte"));
//            System.out.println("sModified  :  " + model.getMobile(lnCtr, "sModified"));
//            System.out.println("dModified  :  " + model.getMobile(lnCtr, "dModified"));
//        }
//        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("EMAIL");
//        System.out.println("--------------------------------------------------------------------");
//        for(int lnCtr = 0;lnCtr <= model.getEmailList().size()-1; lnCtr++){
//            System.out.println("sEmailIDx  :  " + model.getEmail(lnCtr, "sEmailIDx"));
//            System.out.println("sClientID  :  " + model.getEmail(lnCtr, "sClientID"));
//            System.out.println("sEmailAdd  :  " + model.getEmail(lnCtr, "sEmailAdd"));
//            System.out.println("cOwnerxxx  :  " + model.getEmail(lnCtr, "cOwnerxxx"));
//            System.out.println("cPrimaryx  :  " + model.getEmail(lnCtr, "cPrimaryx"));
//            System.out.println("cRecdStat  :  " + model.getEmail(lnCtr, "cRecdStat"));
//            System.out.println("sEntryByx  :  " + model.getEmail(lnCtr, "sEntryByx"));
//            System.out.println("dEntryDte  :  " + model.getEmail(lnCtr, "dEntryDte"));
//            System.out.println("sModified  :  " + model.getEmail(lnCtr, "sModified"));
//            System.out.println("dModified  :  " + model.getEmail(lnCtr, "dModified"));
// 
//        }
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("SOCIAL MEDIA");
//        System.out.println("--------------------------------------------------------------------");
//        for(int lnCtr = 0;lnCtr <= model.getSocialMediaList().size()-1; lnCtr++){
//            System.out.println("sSocialID : " + model.getSocialMed(lnCtr, "sSocialID"));
//            System.out.println("sClientID : " + model.getSocialMed(lnCtr, "sClientID"));
//            System.out.println("sAccountx : " + model.getSocialMed(lnCtr, "sAccountx"));
//            System.out.println("cSocialTp : " + model.getSocialMed(lnCtr, "cSocialTp"));
//            System.out.println("cRecdStat : " + model.getSocialMed(lnCtr, "cRecdStat"));
//            System.out.println("sEntryByx : " + model.getSocialMed(lnCtr, "sEntryByx"));
//            System.out.println("dEntryDte : " + model.getSocialMed(lnCtr, "dEntryDte"));
//            System.out.println("sModified : " + model.getSocialMed(lnCtr, "sModified"));
//            System.out.println("dModified : " + model.getSocialMed(lnCtr, "dModified"));
//        }
//        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------UPDATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.updateRecord();
//        System.err.println((String) json.get("message"));
//        
//        json = model.setMaster("sLastName","TEST");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sFrstName","JOSH");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sMiddName","EDIT");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        System.out.println("mobile size = " + model.getMobileList().size());
//        for(int lnctr = 0; lnctr < model.getMobileList().size(); lnctr++){
//            model.setMobile(lnctr, "sMobileNo", ("0930111111" + String.valueOf(lnctr)));
//        }
//        
//        System.out.println("address size = " + model.getAddressList().size());
//        for(int lnctr = 0; lnctr < model.getAddressList().size(); lnctr++){
//            model.setAddress(lnctr, "sHouseNox", "333");
//            model.setAddress(lnctr, "sAddressx", "53355");
//            model.setAddress(lnctr, "sBrgyIDxx", "1200145");
//            model.setAddress(lnctr, "sTownIDxx", "0335");
//            model.setAddress(lnctr, "nLatitude", 0.0);
//            model.setAddress(lnctr, "nLongitud", 0.0);
//            
//            
//            model.setAddress(lnctr, "sBrgyName", "CAWAYAN BOGTONG");
//            model.setAddress(lnctr, "sTownName", "MALASIQUI");
//            model.setAddress(lnctr, "sProvName", "PANGASINAN");
//            
//            
//            String lsFullAddress = (String) model.getAddress(lnctr, "sHouseNox") + (String) model.getAddress(lnctr, "sAddressx")
//                                 + (String) model.getAddress(lnctr, "sBrgyName") + (String) model.getAddress(lnctr, "sTownName")
//                                 + (String) model.getAddress(lnctr, "sProvName");
//            model.checkClientAddress(lsFullAddress, lnctr, true);
//        }
//        
//        System.out.println("email size = " + model.getEmailList().size());
//        for(int lnctr = 0; lnctr < model.getEmailList().size(); lnctr++){
//            model.setEmail(lnctr, "sEmailAdd", "555@gmail.com");
//            model.setEmail(lnctr, "cOwnerxxx", "0");
//            model.setEmail(lnctr, "cPrimaryx", "0");
//            model.setEmail(lnctr, "cRecdStat", "0"); 
//        }
//        
//        System.out.println("social media size = " + model.getSocialMediaList().size());
//        for(int lnctr = 0; lnctr < model.getSocialMediaList().size(); lnctr++){
//            model.setSocialMed(lnctr, "sAccountx", "555@facebook.com");
//            model.setSocialMed(lnctr, "cSocialTp", "0");         
//        }
//        
//        json = model.saveRecord();
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        } else {
//            System.out.println((String) json.get("message"));
//            System.exit(0);
//        }
//        
//        
//        
//    }
    
}
