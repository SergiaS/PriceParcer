package com.catchshop.PriceParser.apibot.telegram.repository;

import com.catchshop.PriceParser.apibot.telegram.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileMongoRepository extends MongoRepository<UserProfile, String> {
    UserProfile findByChatId(long chatId);
}
