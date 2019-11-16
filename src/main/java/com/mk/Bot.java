package com.mk;
import com.mk.parsers.AliExpressParser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    private static final String BOT_NAME = "_____";
    private static final String TOKEN = "___";
    private static final String SPLITTER = "|";
    private static final String PRICE = "Цена - ";
    private static final String BUY = "Купить - ";
    private static final String BUTTON_ADD_ITEM = "Добавить товар";
    private static final String BUTTON_POSTING = "Опубликовать";
    private static final String BUTTON_CANCEL = "Отмена";
    private static final String BUTTON_ADD_TO_POSTING_LIST = "Добавить в список для публикации";
    private static final String BUTTON_SELECT_CHANNEL = "Выбрать канал для публикации";
    private static final String BUTTON_EDIT_SELECTED_CHANNEL = "Изменить канал для публикации";
    private static final String BUTTON_AUTO_POSTING_ON = "Публикация без подтверждения: вкл";
    private static final String BUTTON_AUTO_POSTING_OFF = "Публикация без подтверждения: откл";
    private static final String BUTTON_POSTING_EVERY_1_HOUR = "Каждый час";
    private static final String BUTTON_POSTING_EVERY_2_HOUR = "Каждые 2 часа";
    private static final String BUTTON_POSTING_EVERY_3_HOUR = "Каждые 3 часа";
    private static final String BUTTON_POSTING_EVERY_6_HOUR = "Каждые 6 часов";
    private static final String BUTTON_POSTING_EVERY_12_HOUR = "Каждые 12 часов";
    private static final String BUTTON_SCHEDULED_POSTING_ON = "Отложенная публикация: вкл";
    private static final String BUTTON_SCHEDULED_POSTING_OFF = "Отложенная публикация: откл";
    private Item lastItem;

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            actionTextMessage(update);
        }
        else if (update.hasCallbackQuery()) {
            actionCallbackQuery(update);
        }
    }

    private void actionTextMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (messageText.equals(Constants.START_COMMAND)) {
            sendMessage(chatId, Constants.HI_MESSAGE);
            if (PreferenceSettingsManager.getSelectedChannelId() == 0) {
                sendMessage(chatId, Constants.START_MESSAGE);
            } else {
                sendMessage(chatId, Constants.WHAT_TO_DO_MESSAGE, mainMenuKeyboard());
            }
        } else if (isMessageForwardFromChannelChat(update)) {
                PreferenceSettingsManager.setSelectedChannelId(update.getMessage().getForwardFromChat().getId());
                sendMessage(chatId, Constants.CHANNEL_IS_SAVED_MESSAGE, mainMenuKeyboard());
        } else if (messageText.contains(SPLITTER)) {
            String itemName = messageText.substring(0, messageText.indexOf(SPLITTER)).trim();
            String itemUrl = messageText.substring(messageText.indexOf(SPLITTER) + SPLITTER.length()).trim();
            if (!itemName.isEmpty() && Utils.isUrl(itemUrl)) {
                postItem(chatId, itemName, itemUrl);
            }
        }
    }

    private void postItem(long chatId, String itemName, String itemUrl) {
        try {
            AliExpressParser aliExpressParser = new AliExpressParser(itemUrl);
            Item item = aliExpressParser.parseItem();
            item.setName(itemName);
            if (PreferenceSettingsManager.getAutoPosting()) {
                if (postingItemToChat(PreferenceSettingsManager.getSelectedChannelId(), item)) {
                    sendMessage(chatId, Constants.ITEM_POSTED_MESSAGE, mainMenuKeyboard());
                } else {
                    sendMessage(chatId, Constants.ITEM_NOT_POSTED_MESSAGE, mainMenuKeyboard());
                }
            } else {
                postingItemToChat(chatId, item, publishItemKeyboard());
            }
            this.lastItem = item;
        }
        catch (Exception e) {
            sendMessage(chatId, Constants.INVALID_LINK_MESSAGE);
        }
    }

    private void actionCallbackQuery(Update update) {
        String callData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        deleteKeyboardFromMessage(chatId, messageId);
        switch (callData) {
            case Constants.ADD_ITEM_CALLBACK:
                sendMessage(chatId, Constants.ENTER_ITEM_INFO_MESSAGE, cancelButton());
                break;
            case Constants.POST_ITEM_CALLBACK:
                if (postingItemToChat(PreferenceSettingsManager.getSelectedChannelId(), lastItem)) {
                    sendMessage(chatId, Constants.ITEM_POSTED_MESSAGE, mainMenuKeyboard());
                } else {
                    sendMessage(chatId, Constants.ITEM_NOT_POSTED_MESSAGE, mainMenuKeyboard());
                }
                break;
            case Constants.ADD_TO_POSTING_LIST_CALLBACK: {
                DBConnector dbConnector = new DBConnector();
                dbConnector.insertItem(this.lastItem.getName(), this.lastItem.getUrl());
                sendMessage(chatId, Constants.ITEM_ADDED_TO_PUBLISH_LIST_MESSAGE + dbConnector.getItemsCount(),
                        mainMenuKeyboard());
                break;
            }
            case Constants.SCHEDULED_POSTING_ON_CALLBACK: {
                DBConnector dbConnector = new DBConnector();
                if (dbConnector.getItemsCount() > 0) {
                    sendMessage(chatId, Constants.WHEN_POST_ITEMS_MESSAGE, scheduledPostingMenu());
                } else {
                    sendMessage(chatId, Constants.POSTING_LIST_WITHOUT_ITEMS_MESSAGE, mainMenuKeyboard());
                }
                break;
            }
            case Constants.SCHEDULED_POSTING_OFF_CALLBACK:
                PreferenceSettingsManager.setScheduledPosting(false);
                sendMessage(chatId, Constants.SCHEDULED_POSTING_OFF_MESSAGE, mainMenuKeyboard());
                break;
            case Constants.SELECT_CHANNEL_CALLBACK:
                sendMessage(chatId, Constants.START_MESSAGE);
                break;
            case Constants.EDIT_SELECTED_CHANNEL_CALLBACK:
                PreferenceSettingsManager.removeSelectedChannelId();
                sendMessage(chatId, Constants.START_MESSAGE);
                break;
            case Constants.POSTING_EVERY_1_HOUR_CALLBACK:
                postingMessageEveryNHour(1, chatId);
                break;
            case Constants.POSTING_EVERY_2_HOUR_CALLBACK:
                postingMessageEveryNHour(2, chatId);
                break;
            case Constants.POSTING_EVERY_3_HOUR_CALLBACK:
                postingMessageEveryNHour(3, chatId);
                break;
            case Constants.POSTING_EVERY_6_HOUR_CALLBACK:
                postingMessageEveryNHour(6, chatId);
                break;
            case Constants.POSTING_EVERY_12_HOUR_CALLBACK:
                postingMessageEveryNHour(12, chatId);
                break;
            case Constants.AUTO_POSTING_ON_CALLBACK:
                PreferenceSettingsManager.setAutoPosting(true);
                sendMessage(chatId, Constants.WHAT_TO_DO_MESSAGE, mainMenuKeyboard());
                break;
            case Constants.AUTO_POSTING_OFF_CALLBACK:
                PreferenceSettingsManager.setAutoPosting(false);
                sendMessage(chatId, Constants.WHAT_TO_DO_MESSAGE, mainMenuKeyboard());
                break;
            case Constants.CANCEL_CALLBACK:
                sendMessage(chatId, Constants.WHAT_TO_DO_MESSAGE, mainMenuKeyboard());
                break;
        }
    }

    private void postingMessageEveryNHour(int hourCount, long chatId) {
        PreferenceSettingsManager.setScheduledPosting(true);
        PostingScheduler postingScheduler = new PostingScheduler(this, hourCount);
        postingScheduler.start();
        sendMessage(chatId, Constants.SCHEDULED_POSTING_ON_MESSAGE, mainMenuKeyboard());
    }

    private boolean isMessageForwardFromChannelChat(Update update){
        if (update.getMessage().getForwardFromChat() == null) {
            return false;
        } else {
            return update.getMessage().getForwardFromChat().isChannelChat();
        }
    }

    private void sendMessage(long chatId, String messageText, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(messageText);
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String messageText) {
        sendMessage(chatId, messageText, null);
    }

    private String buildItemCaption(Item item) {
        return item.getName() + System.lineSeparator() +
                PRICE +
                item.getPrice().getPriseUSD() + " " + SPLITTER + " " +
                item.getPrice().getPriseRUB() + " " + SPLITTER + " " +
                item.getPrice().getPriseUAH() + System.lineSeparator() +
                BUY + item.getUrl();
    }

    public boolean postingItemToChat(long chatId, Item item, InlineKeyboardMarkup markup) {
        SendPhoto message = new SendPhoto();
        message.setChatId(chatId);
        message.setPhoto(item.getImageUrl());
        message.setCaption(buildItemCaption(item));
        if (markup != null) {
            message.setReplyMarkup(publishItemKeyboard());
        }
        try {
            execute(message);
            return true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean postingItemToChat(long chatId, Item item) {
        return postingItemToChat(chatId, item, null);
    }

    private InlineKeyboardMarkup mainMenuKeyboard() {
        String channelButton;
        String channelButtonCallback;
        String autoPostingButton;
        String autoPostingCallback;
        String scheduledPostingButton;
        String scheduledCallback;
        if (PreferenceSettingsManager.getSelectedChannelId() == 0) {
            channelButton = BUTTON_SELECT_CHANNEL;
            channelButtonCallback = Constants.SELECT_CHANNEL_CALLBACK;
        } else {
            channelButton = BUTTON_EDIT_SELECTED_CHANNEL;
            channelButtonCallback = Constants.EDIT_SELECTED_CHANNEL_CALLBACK;
        }
        if (PreferenceSettingsManager.getAutoPosting()) {
            autoPostingButton = BUTTON_AUTO_POSTING_OFF;
            autoPostingCallback = Constants.AUTO_POSTING_OFF_CALLBACK;
        } else {
            autoPostingButton = BUTTON_AUTO_POSTING_ON;
            autoPostingCallback = Constants.AUTO_POSTING_ON_CALLBACK;
        }
        if (PreferenceSettingsManager.getScheduledPosting()) {
            scheduledPostingButton = BUTTON_SCHEDULED_POSTING_OFF;
            scheduledCallback = Constants.SCHEDULED_POSTING_OFF_CALLBACK;
        } else {
            scheduledPostingButton = BUTTON_SCHEDULED_POSTING_ON;
            scheduledCallback = Constants.SCHEDULED_POSTING_ON_CALLBACK;
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkupBuilder()
                .addCallbackButton(BUTTON_ADD_ITEM, Constants.ADD_ITEM_CALLBACK)
                .addCallbackButton(autoPostingButton, autoPostingCallback)
                .addCallbackButton(scheduledPostingButton, scheduledCallback)
                .addCallbackButton(channelButton, channelButtonCallback)
                .build();
        return markup;
    }

    private InlineKeyboardMarkup publishItemKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkupBuilder()
                .addCallbackButton(BUTTON_POSTING, Constants.POST_ITEM_CALLBACK)
                .addCallbackButton(BUTTON_ADD_TO_POSTING_LIST, Constants.ADD_TO_POSTING_LIST_CALLBACK)
                .addCallbackButton(BUTTON_CANCEL, Constants.CANCEL_CALLBACK)
                .build();
        return markup;
    }

    private InlineKeyboardMarkup scheduledPostingMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkupBuilder()
                .addCallbackButton(BUTTON_POSTING_EVERY_1_HOUR, Constants.POSTING_EVERY_1_HOUR_CALLBACK)
                .addCallbackButton(BUTTON_POSTING_EVERY_2_HOUR, Constants.POSTING_EVERY_2_HOUR_CALLBACK)
                .addCallbackButton(BUTTON_POSTING_EVERY_3_HOUR, Constants.POSTING_EVERY_3_HOUR_CALLBACK)
                .addCallbackButton(BUTTON_POSTING_EVERY_6_HOUR, Constants.POSTING_EVERY_6_HOUR_CALLBACK)
                .addCallbackButton(BUTTON_POSTING_EVERY_12_HOUR, Constants.POSTING_EVERY_12_HOUR_CALLBACK)
                .addCallbackButton(BUTTON_CANCEL, Constants.CANCEL_CALLBACK)
                .build();
        return markup;
    }

    private InlineKeyboardMarkup cancelButton() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkupBuilder()
                .addCallbackButton(BUTTON_CANCEL, Constants.CANCEL_CALLBACK)
                .build();
        return markup;
    }

    private void deleteKeyboardFromMessage(long chatId, int messageId) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setReplyMarkup(null);
        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}