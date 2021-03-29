package kshv.org.bot.core.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BotQuoteRepository extends JpaRepository<BotQuoteEntity, Long> {

}
