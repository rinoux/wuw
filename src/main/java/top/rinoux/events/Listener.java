package top.rinoux.events;

/**
 * Created by rinoux on 2019-06-24.
 */
public interface Listener<T> {

    void on(Event<T> event, T param);
}
