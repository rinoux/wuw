package top.rinoux;


import top.rinoux.git.tool.Filter;

import java.io.File;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

/**
 * some tools
 * <p>
 * Created by rinoux on 2018/4/13.
 */
public class GeneralUtils {
    public static boolean isEmpty(String str) {

        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {

        return !isEmpty(str);
    }


    public static String pathJoin(String... nodes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = nodes == null ? 0 : nodes.length; i < len; i++) {
            String node = nodes[i];

            if (node == null) {
                node = "";
            }

            if (i > 0) {
                if (node.startsWith("/") || node.startsWith("\\")) {
                    node = node.substring(1);
                }
            }
            sb.append(node);

            if (i + 1 < len) {
                if (!(node.endsWith("/") || node.endsWith("\\"))) {
                    sb.append("/");
                }
            }
        }

        return sb.toString();
    }


    public static <T> T[] addAll(T[] array1, T[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }

        T[] joinedArray = (T[]) Array.newInstance(array1.getClass().getComponentType(),
                array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    private static <T> T[] clone(T[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }


    public static boolean isEmpty(Object[] array) {
        if (array == null || array.length == 0) {
            return true;
        }
        return false;
    }


    private static String[] getNoneEmptyArray(String[] array) {
        return getNoneEmptyArray(array, 0, array.length);
    }


    private static String[] getNoneEmptyArray(String[] array, int end) {
        return getNoneEmptyArray(array, 0, end);
    }


    private static String[] getNoneEmptyArray(String[] array, int start, int end) {
        Set<String> tmp = new HashSet<>();
        if (end > array.length) {
            end = array.length;
        }
        for (int i = start; i < end; i++) {
            String s = array[i];
            if (isNotEmpty(s)) {
                tmp.add(s);
            }
        }


        return tmp.toArray(new String[tmp.size()]);
    }


    private static int getIntValue(String in) {
        if (in.contains(".")) {
            return (int) Double.parseDouble(in);
        }

        return Integer.parseInt(in);
    }


    /**
     * list names under dir with filter
     * @param dir dir
     * @param filter result
     * @return
     */
    public static String[] list(String dir, Filter<String> filter) {
        Set<String> children = new HashSet<String>();
        File file = new File(dir);
        if (!file.isDirectory()) {
            return new String[0];
        }
        if (filter == null) {
            filter = new Filter<String>() {

                public boolean accept(String s) {
                    return true;
                }
            };
        }
        String[] fileAndDirs = file.list();
        if (fileAndDirs != null && fileAndDirs.length > 0) {
            for (String path : fileAndDirs) {
                if (filter.accept(path)) {
                    children.add(path);
                }
            }
        }
        return children.toArray(new String[0]);
    }


    public static boolean delete(String path) {


        File file = new File(path);
        boolean success = false;
        if (file.exists()) {
            if (file.isDirectory()) {
                success = deleteDirectoryRecursively(path);
            } else {
                success = file.delete();
            }

        }
        return success;
    }

    /**
     * 递归删除文件夹
     *
     * @param dir
     * @return
     */
    private static boolean deleteDirectoryRecursively(String dir) {
        File root = new File(dir);
        if (root.exists()) {
            File[] children = root.listFiles();
            if (children != null && children.length > 0) {
                for (File child : children) {
                    if (child.isDirectory()) {
                        deleteDirectoryRecursively(child.getPath());
                    } else {
                        child.delete();
                    }
                }
            }
            root.delete();
        }

        return !exist(dir);
    }

    public static boolean exist(String path) {
        return new File(path).exists();
    }
}
