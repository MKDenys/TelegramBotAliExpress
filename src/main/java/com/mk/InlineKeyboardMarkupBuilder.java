package com.mk;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardMarkupBuilder {
    private final InlineKeyboardMarkup markup;
    private List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    private List<InlineKeyboardButton> buttonList = new ArrayList<>();

    public InlineKeyboardMarkupBuilder() {
        this.markup = new InlineKeyboardMarkup();
    }

    private InlineKeyboardMarkupBuilder addButton(InlineKeyboardButton button) {
        this.buttonList.add(button);
        this.keyboard.add(buttonList);
        this.buttonList = new ArrayList<>();
        return this;
    }

    public InlineKeyboardMarkupBuilder addCallbackButton(String buttonText, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButtonBuilder()
                .setText(buttonText)
                .setCallbackData(callbackData)
                .build();
        addButton(button);
        return this;
    }

    public InlineKeyboardMarkupBuilder addUrlButton(String buttonText, String url) {
        if (Utils.isUrl(url)) {
            InlineKeyboardButton button = new InlineKeyboardButtonBuilder()
                    .setText(buttonText)
                    .setUrl(url)
                    .build();
            addButton(button);
        }
        return this;
    }

    public InlineKeyboardMarkup build() {
        this.markup.setKeyboard(keyboard);
        return this.markup;
    }
}

