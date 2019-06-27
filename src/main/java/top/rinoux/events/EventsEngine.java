package top.rinoux.events;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rinoux on 2019-06-24.
 */
public class EventsEngine {


    private static Map<Event, LinkedList<Listener>> eventListenerMap = new ConcurrentHashMap<>();


    public static <T> void listen(Event<T> event, Listener<T> listener) {
        for (Map.Entry<Event, LinkedList<Listener>> entry : eventListenerMap.entrySet()) {
            if (entry.getKey().equals(event)) {
                LinkedList<Listener> listeners = entry.getValue();

                if (listeners == null) {
                    listeners = new LinkedList<>();
                }
                listeners.add(listener);
            }
        }
    }


    public static <T> void fire(Event<T> event, T param) {

        for (Map.Entry<Event, LinkedList<Listener>> entry : eventListenerMap.entrySet()) {
            if (entry.getKey().equals(event)) {
                LinkedList<Listener> listeners = entry.getValue();

                if (listeners != null) {
                    for (Listener listener : listeners) {
                        listener.on(event, param);
                    }
                }
            }
        }
    }
}
