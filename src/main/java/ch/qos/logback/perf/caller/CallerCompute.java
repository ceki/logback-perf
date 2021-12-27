package ch.qos.logback.perf.caller;
import java.lang.invoke.MethodHandles;

public class CallerCompute {

    private static ClassContextSecurityManager SECURITY_MANAGER;
    private static boolean SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = false;

    /**
     * In order to call {@link SecurityManager#getClassContext()}, which is a
     * protected method, we add this wrapper which allows the method to be visible
     * inside this package.
     */
    private static final class ClassContextSecurityManager extends SecurityManager {
        protected Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }
    
    public static String getCallingClassViaReflection() {
        //return jdk.internal.reflect.Reflection.getCallerClass().getName();
        return "";
    }
    
    private static ClassContextSecurityManager safeCreateSecurityManager() {
        try {
            return new ClassContextSecurityManager();
        } catch (java.lang.SecurityException sm) {
            return null;
        }
    }
    private static ClassContextSecurityManager getSecurityManager() {
        if (SECURITY_MANAGER != null)
            return SECURITY_MANAGER;
        else if (SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED)
            return null;
        else {
            SECURITY_MANAGER = safeCreateSecurityManager();
            SECURITY_MANAGER_CREATION_ALREADY_ATTEMPTED = true;
            return SECURITY_MANAGER;
        }
    }
    
    static public String getCallerClassViaThreadAPI(int bump) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String thisClassName = CallerCompute.class.getName();

        // Advance until Util is found
        int i;
        for (i = 0; i < trace.length; i++) {
            if (thisClassName.equals(trace[i].getClassName()))
                break;
        }

        // trace[i] = Util; trace[i+1] = caller; trace[i+2] = caller's caller
        if (i >= trace.length || i + bump >= trace.length) {
            throw new IllegalStateException("Failed to find org.slf4j.helpers.CallerCompute or its caller in the stack; " + "this should not happen");
        }
        return trace[i + bump].getClassName();
    }
    
    /**
     * Returns the name of the class which called the invoking method.
     *
     * @return the name of the class which called the invoking method.
     */
    public static String getCallingClassViaSecurityManager(int bump) {
        ClassContextSecurityManager securityManager = getSecurityManager();
        if (securityManager == null)
            return null;
        Class<?>[] trace = securityManager.getClassContext();
        String thisClassName = CallerCompute.class.getName();

        // Advance until Util is found
        int i;
        for (i = 0; i < trace.length; i++) {
            if (thisClassName.equals(trace[i].getName()))
                break;
        }

        // trace[i] = Util; trace[i+1] = caller; trace[i+2] = caller's caller
        if (i >= trace.length || i + bump >= trace.length) {
            throw new IllegalStateException("Failed to find org.slf4j.helpers.Util or its caller in the stack; " + "this should not happen");
        }

        return trace[i + bump].getName();
    }
    
    public static String getCallerViaMethodHandles() {
       Class clazz = MethodHandles.lookup().lookupClass();
       return clazz.getCanonicalName();
    }
}
