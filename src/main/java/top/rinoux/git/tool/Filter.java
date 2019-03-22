package top.rinoux.git.tool;

/**
 * Created by rinoux on 2019-03-21.
 */
public interface Filter<T> {


    boolean accept(T t);
}
