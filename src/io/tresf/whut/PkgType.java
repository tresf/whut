package io.tresf.whut;

public enum PkgType {
    // macOS
    BREW(OsType.MAC),
    PORT(OsType.MAC),

    // Windows
    CHOCO(OsType.WINDOWS),
    SCOOP(OsType.WINDOWS),
    WINGET(OsType.WINDOWS),

    // Linux
    APT(OsType.LINUX,"debian", "ubuntu", "mint", "linspire"),
    APTGET(OsType.LINUX),
    DNF(OsType.LINUX, "redhat", "fedora", "centos"),
    YUM(OsType.LINUX),
    YAST(OsType.LINUX,"suse"),
    PACMAN(OsType.LINUX, "arch", "manjaro"),
    EMERGE(OsType.LINUX, "gentoo"),

    // Solaris
    PKGUTIL(OsType.SOLARIS, "solaris"),
    PKGADD(OsType.SOLARIS, "solaris"),

    // Unix
    PKG(OsType.OTHER_UNIX, "freebsd"),
    PKG_ADD(OsType.OTHER_UNIX, "openbsd"),

    // Other
    PKGMAN(OsType.OTHER, "beos", "haiku"),

    UNKNOWN(OsType.OTHER);

    OsType osType;
    String[] variants;
    PkgType(OsType osType, String ... variants) {
        this.osType = osType;
        this.variants = variants;
    }

    public static PkgType findByVariant(String title) {
        if(title != null) {
            for (PkgType pkgType : values()) {
                for (String variant : pkgType.variants) {
                    if (variant.toLowerCase().contains(variant)) {
                        return pkgType;
                    }
                }
            }
        }
        return UNKNOWN;
    }

    public static String getCommandByCli(OsType osType) {
        return getCommandByCli(osType, values());
    }

    public static String getCommandByCli(OsType osType, PkgType[] pkgTypes)  {
        for(PkgType pkgType : pkgTypes) {
            if(pkgType.osType == osType) {
                switch (pkgType.osType) {
                    case WINDOWS:
                        if (CliParser.exec("where", pkgType.getCommand())) {
                            return pkgType.getCommand();
                        }
                        break;
                    default:
                        if(CliParser.exec("which", pkgType.getCommand())) {
                            return pkgType.getCommand();
                        }
                }
            }
        }
        return UNKNOWN.getCommand();
    }

    public String getCommand() {
        switch(this) {
            case APTGET:
                return "apt-get";
            case PKGUTIL:
                return "/opt/csw/bin/pkgutil";
            case UNKNOWN:
                return null;
        }
        return name().toLowerCase();
    }
}
