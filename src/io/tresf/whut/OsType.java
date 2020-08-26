package io.tresf.whut;

import java.util.Locale;

public enum OsType {
    WINDOWS,
    MAC,
    LINUX,
    SOLARIS,
    AIX,
    OTHER_UNIX,
    OTHER;

    private static final OsType osType = OsType.parseOsType(System.getProperty("os.name"));

    private static OsType parseOsType(String osName) {
        if(osName != null) {
            osName = osName.toLowerCase(Locale.ENGLISH);
            if(osName.contains("win")) return WINDOWS;
            if(osName.contains("mac")) return MAC;
            if(osName.contains("nux")) return LINUX;
            if(osName.contains("sunos")) return SOLARIS;
            if(osName.contains("aix")) return AIX;
            if(osName.contains("nix")) return OTHER_UNIX;
        }
        return OTHER;
    }

    public static OsType getOsType() {
        return osType;
    }
}
