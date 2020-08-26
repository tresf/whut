package io.tresf.whut;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class CliParser {
    private static final OsType osType = OsType.getOsType();
    private static final String[] ENVP = getEnvp();
    /**
     * Returns <code>true</code> if a command executes properly
     */
    public static boolean exec(String ... commandArray) {
        try {
            Process p = Runtime.getRuntime().exec(commandArray, ENVP);
            // Consume output to prevent deadlock
            while (p.getInputStream().read() != -1) {}
            p.waitFor();
            return p.exitValue() == 0;
        } catch(IOException | InterruptedException ignore) {}
        return false;
    }

    /**
     * Returns the raw <code>String</code> output of a shell command
     */
    public static String executeRaw(String ... commandArray) {
        InputStreamReader in = null;
        try {
            Process p = Runtime.getRuntime().exec(commandArray, ENVP);
            if(commandArray.length > 0 && commandArray[0].startsWith("wmic")) {
                // Fix deadlock on old Windows versions https://stackoverflow.com/a/13367685/3196753
                p.getOutputStream().close();
            }
            in = new InputStreamReader(p.getInputStream(), Charset.forName("UTF-8"));
            StringBuilder out = new StringBuilder();
            int c;
            while((c = in.read()) != -1)
                out.append((char)c);

            return out.toString();
        }
        catch(IOException ignore) {}
        finally {
            if (in != null) {
                try { in.close(); } catch(Exception ignore) {}
            }
        }

        return "";
    }

    private static String[] getEnvp() {
        if(osType == OsType.WINDOWS) {
            return null;
        }
        Map<String, String> env = new HashMap<>(System.getenv());
        // Cache existing; permit named overrides w/o full clobber
        if (osType == OsType.MAC) {
            // Enable LANG overrides
            env.put("SOFTWARE", "");
        }
        // Functional equivalent of "export LANG=en_US.UTF-8"
        env.put("LANG", "C");
        String[] envp = new String[env.size()];
        int i = 0;
        for (Map.Entry<String, String> o : env.entrySet())
            envp[i++] = o.getKey() + "=" + o.getValue();
        return envp;
    }

    public static HashMap<String, String> getLinuxReleaseMap() {
        // Prefers sh to walkFileTree for code simplicity reasons
        String raw[] = executeRaw("sh", "-c", "cat /etc/*-release").split("[\\r\\n]+");
        // Build a hashmap of values
        HashMap<String, String> releaseInfo = new HashMap<>();
        for(String line : raw) {
            if(line.contains("=")) {
                String[] split = line.split("=", 2);
                if(split.length == 2) {
                    releaseInfo.put(split[0].toUpperCase(), split[1].trim());
                }
            }
        }
        return releaseInfo;
    }

}
