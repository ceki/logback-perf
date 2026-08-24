package ch.qos.logback.perf.caller;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CallerComputeTest {

    @Test
    public void testViaSecurityManager() {
        String result = CallerCompute.getCallingClassViaSecurityManager(1);
        assertEquals(CallerComputeTest.class.getName(), result);
    }

    @Test
    @Disabled
    public void testViaReflection() {
        String result = CallerCompute.getCallingClassViaReflection();
        assertEquals(CallerComputeTest.class.getName(), result);
    }
 
    
    @Test
    public void testgetCallerClassViaThreadAPI() {
        String result = CallerCompute.getCallerClassViaThreadAPI(1);
        assertEquals(CallerComputeTest.class.getName(), result);
    }
    
    @Test
    public void testWalkerAPI() {
        String result = CallerCompute9.getCallerClass9(1);
        assertEquals(CallerComputeTest.class.getName(), result);
    }
    
//    @Test
//    public void testCallerViaMethodName() {
//        String result = CallerCompute.getCallerViaMethodHandles();
//        Lookup lk = MethodHandles.publicLookup();// Lookup(this.getClass());
//        lk.
//        System.out.println(result);
//        assertEquals(CallerComputeTest.class.getName(), result);
//    }
    
}