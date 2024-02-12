package application.githubfetch.errors;

public class WebClientResponseException extends RuntimeException {
    int code;

    public int getCode() {
        return code;
    }

    public WebClientResponseException(String message, int code) {
        super(message);
        this.code = code;
    }
}
