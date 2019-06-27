package top.rinoux.events;

import top.rinoux.util.GeneralUtils;

import java.io.Serializable;

/**
 * Created by rinoux on 2019-06-24.
 */
public class Event<T> implements Serializable {

    private String eventName;

    public Event(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public int hashCode() {
        return GeneralUtils.isEmpty(eventName) ? this.getClass().getName().hashCode() : eventName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Event
                && this.getClass().equals(obj.getClass())
                && ((Event) obj).eventName.equals(this.eventName);
    }
}
