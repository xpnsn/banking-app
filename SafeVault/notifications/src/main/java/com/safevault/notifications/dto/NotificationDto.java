package com.safevault.notifications.dto;

import lombok.Getter;

public class NotificationDto implements Comparable<NotificationDto> {
    private String sender;
    private String type;
    private String message;
    private int priority;

    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(NotificationDto o) {
        return Integer.compare(this.priority, o.priority);
    }
}
