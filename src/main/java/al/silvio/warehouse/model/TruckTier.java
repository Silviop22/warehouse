package al.silvio.warehouse.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TruckTier {
    TIER_1(16000000.000, "Tier 1"),
    TIER_2(33000000.000, "Tier 2"),
    TIER_3(50000000.000, "Tier 3"),
    TIER_4(67000000.000, "Tier 4");
    
    private final Double maximumCapacity;
    private final String value;
}
