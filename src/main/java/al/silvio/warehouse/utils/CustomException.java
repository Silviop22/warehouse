package al.silvio.warehouse.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {
    
    private int statusCode;
    
    public CustomException(String message, Throwable error) {
        super(message, error);
    }
    
    public CustomException(String message) {
        super(message);
        this.statusCode = 400;
    }
    
    public CustomException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
