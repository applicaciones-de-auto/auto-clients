
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.clients.Vehicle_Serial;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
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
public class VehicleSerialTest {
    static Vehicle_Serial model;
    JSONObject json;
    boolean result;
    
    public VehicleSerialTest(){}
    
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
        model = new Vehicle_Serial(instance,false, instance.getBranchCode());
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
            json = model.setMaster("sVhclIDxx","M001VM000008");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
            
            json = model.setMaster("sMakeIDxx","M001MK000001");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
            
            json = model.setMaster("sModelIDx","M001MD000004");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
        
            json = model.setMaster("sTypeIDxx","M001TP000001");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
        
            json = model.setMaster("sColorIDx","M001CL000007");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
            
            json = model.setMaster("sTransMsn","M");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
            
            json = model.setMaster("nYearModl",1999);
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
            
            json = model.setMaster("sFrameNox","SHILEC1");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
            
            json = model.setMaster("sEngineNo","LEN1234");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
            
            json = model.setMaster("sCSNoxxxx","C4A12W");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }

            json = model.setMaster("sPlateNox","CAA12WW");
            if ("error".equals((String) json.get("result"))){
                System.err.println((String) json.get("message"));
                System.exit(1);
            }
                 
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
        
        json = model.openRecord("M001VS240001");
        
        if (!"success".equals((String) json.get("result"))){
            result = false;
        } else {
        
            System.out.println("--------------------------------------------------------------------");
            System.out.println("VEHICLE SERIAL");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("sSerialID  :  " + model.getMaster("sSerialID")); 
            System.out.println("sBranchCD  :  " + model.getMaster("sBranchCD")); 
            System.out.println("sFrameNox  :  " + model.getMaster("sFrameNox")); 
            System.out.println("sEngineNo  :  " + model.getMaster("sEngineNo")); 
            System.out.println("sVhclIDxx  :  " + model.getMaster("sVhclIDxx")); 
            System.out.println("sClientID  :  " + model.getMaster("sClientID")); 
            System.out.println("sCoCltIDx  :  " + model.getMaster("sCoCltIDx")); 
            System.out.println("sCSNoxxxx  :  " + model.getMaster("sCSNoxxxx")); 
            System.out.println("sDealerNm  :  " + model.getMaster("sDealerNm")); 
            System.out.println("sCompnyID  :  " + model.getMaster("sCompnyID")); 
            System.out.println("sKeyNoxxx  :  " + model.getMaster("sKeyNoxxx")); 
            System.out.println("cIsDemoxx  :  " + model.getMaster("cIsDemoxx")); 
            System.out.println("cLocation  :  " + model.getMaster("cLocation")); 
            System.out.println("cSoldStat  :  " + model.getMaster("cSoldStat")); 
            System.out.println("cVhclNewx  :  " + model.getMaster("cVhclNewx")); 
            System.out.println("sRemarksx  :  " + model.getMaster("sRemarksx")); 
            System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx")); 
            System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte")); 
            System.out.println("sModified  :  " + model.getMaster("sModified")); 
            System.out.println("dModified  :  " + model.getMaster("dModified")); 
            System.out.println("sPlateNox  :  " + model.getMaster("sPlateNox")); 
            System.out.println("dRegister  :  " + model.getMaster("dRegister")); 
            System.out.println("sPlaceReg  :  " + model.getMaster("sPlaceReg")); 
            System.out.println("sMakeIDxx  :  " + model.getMaster("sMakeIDxx")); 
            System.out.println("sMakeDesc  :  " + model.getMaster("sMakeDesc")); 
            System.out.println("sModelIDx  :  " + model.getMaster("sModelIDx")); 
            System.out.println("sModelDsc  :  " + model.getMaster("sModelDsc")); 
            System.out.println("sTypeIDxx  :  " + model.getMaster("sTypeIDxx")); 
            System.out.println("sTypeDesc  :  " + model.getMaster("sTypeDesc")); 
            System.out.println("sColorIDx  :  " + model.getMaster("sColorIDx")); 
            System.out.println("sColorDsc  :  " + model.getMaster("sColorDsc")); 
            System.out.println("sTransMsn  :  " + model.getMaster("sTransMsn")); 
            System.out.println("nYearModl  :  " + model.getMaster("nYearModl")); 
            System.out.println("sDescript  :  " + model.getMaster("sDescript")); 
            System.out.println("sOwnerAdd  :  " + model.getMaster("sOwnerAdd")); 
            System.out.println("sCOwnerAd  :  " + model.getMaster("sCOwnerAd")); 
            System.out.println("sVhclStat  :  " + model.getMaster("sVhclStat")); 
            System.out.println("sUdrNoxxx  :  " + model.getMaster("sUdrNoxxx")); 
            System.out.println("sUdrDatex  :  " + model.getMaster("sUdrDatex")); 
            System.out.println("sBuyerNmx  :  " + model.getMaster("sBuyerNmx"));
            
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
        
        json = model.setMaster("sPlateNox","CAA12WW");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
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

/*************************************************************************************************************/    
    
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
//        Vehicle_Serial model = new Vehicle_Serial(instance, false, instance.getBranchCode());
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
//        json = model.setMaster("sVhclIDxx","M001VM000008");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sMakeIDxx","M001MK000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sModelIDx","M001MD000004");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sTypeIDxx","M001TP000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sColorIDx","M001CL000007");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sTransMsn","M");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("nYearModl",1999);
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sFrameNox","SHILEC1");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sEngineNo","LEN1234");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sCSNoxxxx","C4A12W");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
        
//        json = model.setMaster("sPlateNox","CAA12WW");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD SAVING--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
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
        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------RETRIEVAL--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        //retrieval
//        json = model.openRecord("M001VS240001");
//        System.err.println((String) json.get("message"));
//        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("VEHICLE SERIAL");
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("sSerialID  :  " + model.getMaster("sSerialID")); 
//        System.out.println("sBranchCD  :  " + model.getMaster("sBranchCD")); 
//        System.out.println("sFrameNox  :  " + model.getMaster("sFrameNox")); 
//        System.out.println("sEngineNo  :  " + model.getMaster("sEngineNo")); 
//        System.out.println("sVhclIDxx  :  " + model.getMaster("sVhclIDxx")); 
//        System.out.println("sClientID  :  " + model.getMaster("sClientID")); 
//        System.out.println("sCoCltIDx  :  " + model.getMaster("sCoCltIDx")); 
//        System.out.println("sCSNoxxxx  :  " + model.getMaster("sCSNoxxxx")); 
//        System.out.println("sDealerNm  :  " + model.getMaster("sDealerNm")); 
//        System.out.println("sCompnyID  :  " + model.getMaster("sCompnyID")); 
//        System.out.println("sKeyNoxxx  :  " + model.getMaster("sKeyNoxxx")); 
//        System.out.println("cIsDemoxx  :  " + model.getMaster("cIsDemoxx")); 
//        System.out.println("cLocation  :  " + model.getMaster("cLocation")); 
//        System.out.println("cSoldStat  :  " + model.getMaster("cSoldStat")); 
//        System.out.println("cVhclNewx  :  " + model.getMaster("cVhclNewx")); 
//        System.out.println("sRemarksx  :  " + model.getMaster("sRemarksx")); 
//        System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx")); 
//        System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte")); 
//        System.out.println("sModified  :  " + model.getMaster("sModified")); 
//        System.out.println("dModified  :  " + model.getMaster("dModified")); 
//        System.out.println("sPlateNox  :  " + model.getMaster("sPlateNox")); 
//        System.out.println("dRegister  :  " + model.getMaster("dRegister")); 
//        System.out.println("sPlaceReg  :  " + model.getMaster("sPlaceReg")); 
//        System.out.println("sMakeIDxx  :  " + model.getMaster("sMakeIDxx")); 
//        System.out.println("sMakeDesc  :  " + model.getMaster("sMakeDesc")); 
//        System.out.println("sModelIDx  :  " + model.getMaster("sModelIDx")); 
//        System.out.println("sModelDsc  :  " + model.getMaster("sModelDsc")); 
//        System.out.println("sTypeIDxx  :  " + model.getMaster("sTypeIDxx")); 
//        System.out.println("sTypeDesc  :  " + model.getMaster("sTypeDesc")); 
//        System.out.println("sColorIDx  :  " + model.getMaster("sColorIDx")); 
//        System.out.println("sColorDsc  :  " + model.getMaster("sColorDsc")); 
//        System.out.println("sTransMsn  :  " + model.getMaster("sTransMsn")); 
//        System.out.println("nYearModl  :  " + model.getMaster("nYearModl")); 
//        System.out.println("sDescript  :  " + model.getMaster("sDescript")); 
//        System.out.println("sOwnerAdd  :  " + model.getMaster("sOwnerAdd")); 
//        System.out.println("sCOwnerAd  :  " + model.getMaster("sCOwnerAd")); 
//        System.out.println("sVhclStat  :  " + model.getMaster("sVhclStat")); 
//        System.out.println("sUdrNoxxx  :  " + model.getMaster("sUdrNoxxx")); 
//        System.out.println("sUdrDatex  :  " + model.getMaster("sUdrDatex")); 
//        System.out.println("sBuyerNmx  :  " + model.getMaster("sBuyerNmx")); 
//
//
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------UPDATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.updateRecord();
//        System.err.println((String) json.get("message"));
//        
//        json = model.setMaster("sPlateNox","CAA12WW");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
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
//    }
    
}
