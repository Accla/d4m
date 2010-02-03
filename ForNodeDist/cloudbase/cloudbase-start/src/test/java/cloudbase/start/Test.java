package cloudbase.start;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class Test extends TestCase {
    
    public void testReplaceEnvVars() {
        Map<String, String> env = new HashMap<String, String>();
        env.put("CLOUDBASE_HOME", "/foo");
        env.put("CLOUDBASE_VERSION", "1.0");
        assertEquals(Main.replaceEnvVars("$CLOUDBASE_HOME/lib/cloudbase-$CLOUDBASE_VERSION.jar", env),
                     "/foo/lib/cloudbase-1.0.jar");
        assertEquals(Main.replaceEnvVars("-$XYZZY-", env), "--");
        assertEquals(Main.replaceEnvVars("-$X1_2-", env), "--");
    }

}
