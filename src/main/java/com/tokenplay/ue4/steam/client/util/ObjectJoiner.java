package com.tokenplay.ue4.steam.client.util;

import java.util.Collection;
import java.util.StringJoiner;


public class ObjectJoiner {

    private ObjectJoiner() {
    }

    public static String join(CharSequence separator, Object... arguments) {
        StringJoiner st = new StringJoiner(separator);
        if (arguments != null) {
            for (Object object : arguments) {
                if (object != null) {
                    st.add(object.toString());
                } else {
                    st.add("");
                }
            }
        }
        return st.toString();
    }

    public static String join(CharSequence separator, Collection<? extends Object> arguments) {
        StringJoiner st = new StringJoiner(separator);
        if (arguments != null) {
            for (Object object : arguments) {
                if (object != null) {
                    st.add(object.toString());
                } else {
                    st.add("");
                }
            }
        }
        return st.toString();
    }

    public static String simplyJoin(Object... arguments) {
        return join("", arguments);
    }
}
