package com.mk;

import java.util.Timer;
import java.util.TimerTask;

public class PostingScheduler extends TimerTask {
    private static final int oneHourInMs = 3600000;
    private Bot bot;
    private long chatId;
    private int hourCount;
    private Timer time;
    private DBConnector dbConnector;

    public PostingScheduler(Bot bot, int hourCount) {
        this.bot = bot;
        this.chatId = PreferenceSettingsManager.getSelectedChannelId();
        this.hourCount = hourCount;
        this.time = new Timer();
        this.dbConnector = new DBConnector();
    }

    @Override
    public void run() {
        if (PreferenceSettingsManager.getScheduledPosting()) {
            Item item = this.dbConnector.getFirstItem();
            if (item != null) {
                this.bot.postingItemToChat(this.chatId, item);
                this.dbConnector.deleteItemByUrl(item.getUrl());
            }
            if (this.dbConnector.getItemsCount() == 0) {
                PreferenceSettingsManager.setScheduledPosting(false);
            }
        } else {
            stop();
        }
    }

    public void start() {
        this.time.schedule(this, 0, oneHourInMs * this.hourCount);
    }

    public void stop() {
        this.time.cancel();
    }

}
