package de.iani.cubesideutils.sql;

public class SQLUtil {

    public static final String escapeLike(String arg) {
        arg = arg.replaceAll("\\\\", "\\\\");
        arg = arg.replaceAll("\\_", "\\_");
        arg = arg.replaceAll("\\%", "\\%");
        return arg;
    }

}
