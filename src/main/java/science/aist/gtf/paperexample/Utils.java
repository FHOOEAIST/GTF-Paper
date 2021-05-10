package science.aist.gtf.paperexample;

import java.io.File;

/**
 * <p>Class that contains various util function.</p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public final class Utils {
    private Utils() {}
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    public static void mkdirs(String dirs) {
        mkdirs(dirs, false);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void mkdirs(String dirs, boolean forParentDir) {
        var f = new File(dirs);
        if (forParentDir) {
            f = f.getParentFile();
        }
        f.mkdirs();
    }
}
