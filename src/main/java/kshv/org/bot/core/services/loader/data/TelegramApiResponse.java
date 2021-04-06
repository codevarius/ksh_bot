package kshv.org.bot.core.services.loader.data;

public class TelegramApiResponse<T> {
    public Boolean ok;
    public T result;

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
