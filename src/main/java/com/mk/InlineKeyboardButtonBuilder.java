package com.mk;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class InlineKeyboardButtonBuilder {
    private final InlineKeyboardButton button;

    public InlineKeyboardButtonBuilder() {
        this.button = new InlineKeyboardButton();
    }

    public InlineKeyboardButtonBuilder setText(String text) {
        this.button.setText(text);
        return this;
    }

    public InlineKeyboardButtonBuilder setUrl(String url) {
        this.button.setUrl(url);
        return this;
    }

    public InlineKeyboardButtonBuilder setCallbackData(String callbackData) {
        this.button.setCallbackData(callbackData);
        return this;
    }

    public InlineKeyboardButton build() {
        return this.button;
    }
}
