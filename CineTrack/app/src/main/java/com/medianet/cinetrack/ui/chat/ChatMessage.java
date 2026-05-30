package com.medianet.cinetrack.ui.chat;

public class ChatMessage {
    public enum Type { USER, AI, LOADING }

    public final String text;
    public final Type type;

    public ChatMessage(String text, Type type) {
        this.text = text;
        this.type = type;
    }
}
