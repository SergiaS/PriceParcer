package com.catchshop.PriceParser.apibot.telegram.model;

import com.catchshop.PriceParser.apibot.telegram.api.BotStatus;
import com.catchshop.PriceParser.apibot.telegram.util.ResultManager;
import com.catchshop.PriceParser.bike.shops.MainParser;
import com.catchshop.PriceParser.bike.util.ShopHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UserProfile {

    private final Long chatId;
    private ParseItem tmpParsedParseItem;
    private ArrayDeque<FavoriteItem> favorites;
    private BotStatus botStatus;
    private String languageTag;
    private int updateTime;
    private ScheduledExecutorService executorService;
    private ResultManager resultManager;
    // and more shop settings ...

    @Autowired
    public UserProfile(Long chatId, ResultManager resultManager) {
        this.chatId = chatId;
        this.resultManager = resultManager;
        this.botStatus = BotStatus.SHOW_MENU;
        this.languageTag = "en-EN";
        this.favorites = new ArrayDeque<>();
        favorites.addAll(FavoriteItem.fillDefaultFavorites());
        this.updateTime = 15;
        startTrackingUserFavorites();
    }

    private void startTrackingUserFavorites() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        startScheduler().apply(executorService);
    }

    private Function<ScheduledExecutorService, Void> startScheduler() {

        return (executorService -> {
           executorService.scheduleWithFixedDelay(() -> {
               if (favorites.size() == 0) {
                   System.out.println("list of user favorites is empty - shutdown!");
                   executorService.shutdown();
               }

               // do check parse
//               FavoriteItem.checkupWithSite();
               for (int i = 0; i < favorites.size(); i++) {
                   FavoriteItem oldData = favorites.pollFirst();

                   MainParser shopParser = ShopHelper.storeIdentifier(oldData.getUrl());
                   ParseItem newItem = shopParser.parseItemInfo(oldData.getUrl());
                   FavoriteItem newData = FavoriteItem.convertToFavoriteItem(newItem, oldData);

                   resultManager.notifyIfItemUpdated(chatId.toString(), oldData, newData);
                   favorites.addLast(newData);
               }


//               if (favorites.getFirst().getDateTimeUpdate().isBefore(LocalDateTime.now().withNano(0))) {
//                   log.info("checking - {}", currentFavoriteItem.getTitle());
//                   currentFavoriteItem.setDateTimeUpdate(LocalDateTime.now().withNano(0).plusSeconds(updateTimeInMinutes));
//                   favorites.addLast(currentFavoriteItem);
//               } else {
//                   try {
//                       long sec = getDelay();
//                       System.out.println("SLEEP: " + sec + " sec");
//                       Thread.sleep(sec * 1000);
//                   } catch (InterruptedException e) {
//                       e.printStackTrace();
//                   }
//               }
           }, 0, updateTime, TimeUnit.MINUTES);
            return null;
        });
    }

    private long getDelay() {
        LocalDateTime dt1 = favorites.getFirst().getDateTimeUpdate();
        LocalDateTime dt2 = LocalDateTime.now().withNano(0);
        long between = ChronoUnit.SECONDS.between(dt2, dt1);
        return between;
    }

    public ParseItem getTmpParsedItem() {
        return tmpParsedParseItem;
    }

    public void setTmpParsedItem(ParseItem tmpParsedParseItem) {
        this.tmpParsedParseItem = tmpParsedParseItem;
    }

    public ArrayDeque<FavoriteItem> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayDeque<FavoriteItem> favorites) {
        this.favorites = favorites;
    }

    public BotStatus getBotStatus() {
        return botStatus;
    }

    public void setBotStatus(BotStatus botStatus) {
        this.botStatus = botStatus;
    }

    public String getLanguageTag() {
        return this.languageTag;
    }

    public void setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
    }

}
