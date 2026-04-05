package xyz.rive.jttplayer.util;

import java.util.Collection;

public final class CollectionUtils {

    public static boolean sizeLt(Collection<?> c, int limit) {
        if(limit < 0) {
            return false;
        }
        return c == null || c.size() < limit;
    }

}
