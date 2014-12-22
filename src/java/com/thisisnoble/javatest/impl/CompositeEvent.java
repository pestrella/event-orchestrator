package com.thisisnoble.javatest.impl;

import com.thisisnoble.javatest.Event;

import java.util.HashMap;
import java.util.Map;

public class CompositeEvent implements Event {

    private final Event event;
    private final Event parent;
    private final Map<String, Event> children = new HashMap<>();

    public CompositeEvent(Event event, Event parent) {
        this.event = event;
        this.parent = parent;
    }

    @Override
    public String getId() {
        return event.getId();
    }

    @Override
    public String getParentId() {
        return parent.getId();
    }

    public Event getEvent() {
      return event;
    }

    public Event getParent() {
        return parent;
    }

    public CompositeEvent addChild(Event child) {
        children.put(child.getId(), child);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> E getChildById(String id) {
        return (E) children.get(id);
    }

    public int size() {
        return children.size();
    }
}
