package kshv.org.bot.core.services.quotegen.data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotes")
public class BotQuoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String quoteText;
    private String quoteAuthor;
    private String senderName;
    private String senderLink;
    private String quoteLink;
    private LocalDateTime quoteGenDate;

    public String getQuoteText() {
        return quoteText.equals("") ? "эх, забыл...😅" : quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getQuoteAuthor() {
        return quoteAuthor.equals("") ? "один мудрец" : quoteAuthor;
    }

    public void setQuoteAuthor(String quoteAuthor) {
        this.quoteAuthor = quoteAuthor;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderLink() {
        return senderLink;
    }

    public void setSenderLink(String senderLink) {
        this.senderLink = senderLink;
    }

    public String getQuoteLink() {
        return quoteLink;
    }

    public void setQuoteLink(String quoteLink) {
        this.quoteLink = quoteLink;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getQuoteGenDate() {
        return quoteGenDate;
    }

    public void setQuoteGenDate(LocalDateTime quoteGenDate) {
        this.quoteGenDate = quoteGenDate;
    }
}
