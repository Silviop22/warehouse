package al.silvio.warehouse.model;

import java.util.List;

public class Key<T> {
    
    public static final Key<List> PUBLIC_HOLIDAYS = new Key<>("warehouse.trucks.public_holidays", List.class);
    private final String key;
    private final Class<T> valueClass;
    
    private Key(String key, Class<T> valueClass) {
        this.key = key;
        this.valueClass = valueClass;
    }
    
    public String getKey() {
        return key;
    }
    
    public Class<T> getValueClass() {
        return valueClass;
    }
}
