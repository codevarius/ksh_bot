package kshv.org.bot.core.services.quotegen.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotQuoteRepository extends JpaRepository<BotQuoteEntity, Long> {

    @Query(value = "SELECT * FROM quotes where id=(SELECT MAX(id) from quotes)", nativeQuery = true)
    Optional<BotQuoteEntity> findLatestAddedBotQuoteEntity();
}
