package al.silvio.warehouse.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    CREATED("Created"), AWAITING_APPROVAL("Awaiting Approval"), APPROVED("Approved"), DECLINED(
            "Declined"), UNDER_DELIVERY("Under Delivery"), FULFILLED("Fulfilled"), CANCELED("Canceled");
    private final String value;
}
