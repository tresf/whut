package io.tresf.whut;
import java.util.HashMap;
import java.util.Map;

public class Whut {
    public static void main(String[] args) {
        System.out.println("Found OS: \"" + OsType.getOsType() + "\"");
        System.out.println("Found package manager: \"" + Whut.getPackageManager()+ "\"");
    }

    public static String getPackageManager() {
        OsType thisOS = OsType.getOsType();
        switch (thisOS) {
            case LINUX:
                return getLinuxPackageManager();
            default:
                return PkgType.getCommandByCli(thisOS);
        }
    }


    // /etc/*-release keys to search for in order of importance
    public static final String[] LINUX_RELEASE_KEYS = { "ID_LIKE", "REDHAT_SUPPORT_PRODUCT", "REDHAT_BUGZILLA_PRODUCT", "ID", "NAME", "DISTRIB_ID", "PRETTY_NAME" };

    private static String getLinuxPackageManager() {
        PkgType pkgType = PkgType.UNKNOWN;
        HashMap<String,String> releaseMap = CliParser.getLinuxReleaseMap();
        releaseLoop:
        for (Map.Entry<String, String> entry : releaseMap.entrySet()) {
            for(String releaseKey : LINUX_RELEASE_KEYS) {
                if(entry.getKey().equals(releaseKey)) {
                    if(releaseKey.startsWith("REDHAT")) {
                        pkgType = PkgType.DNF;
                    } else {
                        pkgType = PkgType.findByVariant(entry.getValue());
                    }
                }
                if(pkgType != PkgType.UNKNOWN) {
                    break releaseLoop;
                }
            }
        }

        switch(pkgType) {
            case APT:
                return PkgType.getCommandByCli(OsType.LINUX, new PkgType[] { PkgType.APT, PkgType.APTGET});
            case DNF:
                return PkgType.getCommandByCli(OsType.LINUX, new PkgType[] { PkgType.DNF, PkgType.YUM});
            default:
                return pkgType.getCommand();
        }
    }

}
