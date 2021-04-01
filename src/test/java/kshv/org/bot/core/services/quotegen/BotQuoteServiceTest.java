package kshv.org.bot.core.services.quotegen;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class BotQuoteServiceTest {

    private final BotQuoteService botQuoteService;

    @Autowired
    public BotQuoteServiceTest(BotQuoteService botQuoteService) {
        this.botQuoteService = botQuoteService;
    }

    @Test
    public void testGetRandomSixDigitNumber() {
        var iteration = 0;
        while (iteration++ <= 100) {
            var testSeed = botQuoteService.getRandomSixDigitNumber();
            Assertions.assertThat(testSeed).isNotBlank().hasSize(6);
            for (char c : testSeed.toCharArray()) {
                assertTrue(Character.isDigit(c));
            }
        }
    }

}