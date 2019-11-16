package com.mk;

public interface Constants {
    String ADD_ITEM_CALLBACK = "add_item";
    String POST_ITEM_CALLBACK = "post";
    String ADD_TO_POSTING_LIST_CALLBACK = "add_to_posting_list";
    String SELECT_CHANNEL_CALLBACK = "select_channel";
    String EDIT_SELECTED_CHANNEL_CALLBACK = "edit_selected_channel";
    String AUTO_POSTING_ON_CALLBACK = "auto_posting_on";
    String AUTO_POSTING_OFF_CALLBACK = "auto_posting_off";
    String POSTING_EVERY_1_HOUR_CALLBACK = "posting_every_hour";
    String POSTING_EVERY_2_HOUR_CALLBACK = "posting_every_2_hour";
    String POSTING_EVERY_3_HOUR_CALLBACK = "posting_every_3_hour";
    String POSTING_EVERY_6_HOUR_CALLBACK = "posting_every_6_hour";
    String POSTING_EVERY_12_HOUR_CALLBACK = "posting_every_12_hour";
    String SCHEDULED_POSTING_OFF_CALLBACK = "scheduled_posting_off_callback";
    String SCHEDULED_POSTING_ON_CALLBACK = "scheduled_posting_on_callback";
    String CANCEL_CALLBACK = "cancel";

    String INVALID_LINK_MESSAGE = "Что-то пошло не так";
    String ENTER_ITEM_INFO_MESSAGE = "Введи название товара и ссылу на него с AliExpress" +
            " в формате \"Название | http://example.com\"";
    String WHAT_TO_DO_MESSAGE = "Что будем делать?";
    String START_MESSAGE = "Перешлите сюда сообщение из канала в котором хотите " +
                    "публиковать сообщения и сделайте бота администратором этого канала";
    String CHANNEL_IS_SAVED_MESSAGE = "Канал сохранен";
    String ITEM_ADDED_TO_PUBLISH_LIST_MESSAGE = "Товар добавлен в список для публикации, количество товаров в списке: ";
    String ITEM_POSTED_MESSAGE = "Сообщение опубликовано";
    String ITEM_NOT_POSTED_MESSAGE = "Сообщение не опубликовано, попробуйте еще раз";
    String WHEN_POST_ITEMS_MESSAGE = "Когда мне публиковать товары?";
    String SCHEDULED_POSTING_OFF_MESSAGE = "Отложенная публикация отключена";
    String SCHEDULED_POSTING_ON_MESSAGE = "Сообщения будут публиковаться с " +
                            "выбраной периодичностью до тех пор, пока вы не отключите отложенную публикацию или в списке не закончатся товары";
    String HI_MESSAGE = "Привет!";
    String POSTING_LIST_WITHOUT_ITEMS_MESSAGE = "В списке для публикации нет товаров";

    String START_COMMAND = "/start";
}
