package ch.qos.logback.perf.caller;
import java.lang.StackWalker.StackFrame;
import java.util.List; 

public class CallerCompute9 {

    static private StackWalker WALKER = StackWalker.getInstance();
    static String THIS_CLASS_NAME = CallerCompute9.class.getName();
      
    static public String getCallerClass9(int bump) {
        
        //List<StackFrame> frames = WALKER.walk(s -> s.limit(4).collect(Collectors.toList()));
        List<StackFrame> frames = WALKER.walk(s -> s.limit(4).toList());
                      
        int frameCount = frames.size();
        
        // Advance until CallerCompute9 is found
        int i;
        for (i = 0; i < frameCount; i++) {
            if (THIS_CLASS_NAME.equals(frames.get(i).getClassName()))
                break;
        }

        // trace[i] = Util; trace[i+1] = caller; trace[i+2] = caller's caller
        if (i >= frameCount || i + bump >= frameCount) {
            throw new IllegalStateException("Failed to find org.slf4j.helpers.CallerCompute or its caller in the stack; " + "this should not happen");
        }
        return frames.get(i+bump).getClassName();
    }
}
