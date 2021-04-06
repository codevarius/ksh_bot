package kshv.org.bot.core.services.loader.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotActionRepository extends JpaRepository<BotActionEntity,String>{
}
