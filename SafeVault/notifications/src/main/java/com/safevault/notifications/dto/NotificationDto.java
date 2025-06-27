package com.safevault.notifications.dto;

import lombok.Getter;

import java.util.Map;

public class NotificationDto implements Comparable<NotificationDto> {
    private String sender;
    private String type;
    private Map<String, String> data;
    private int priority;

    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getData() {
        return data;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(NotificationDto o) {
        return Integer.compare(this.priority, o.priority);
    }
}
