package al.silvio.warehouse.api.service;

import al.silvio.warehouse.auth.model.user.User;
import al.silvio.warehouse.auth.service.AuthenticationService;
import al.silvio.warehouse.auth.service.UserService;
import al.silvio.warehouse.manager.OrderManager;
import al.silvio.warehouse.model.Order;
import al.silvio.warehouse.model.OrderItem;
import al.silvio.warehouse.model.OrderSpecification;
import al.silvio.warehouse.model.OrderStatus;
import al.silvio.warehouse.model.OrderTruck;
import al.silvio.warehouse.model.Truck;
import al.silvio.warehouse.model.ui.OrderAuthority;
import al.silvio.warehouse.model.ui.OrderDto;
import al.silvio.warehouse.model.ui.OrderExtendedDto;
import al.silvio.warehouse.model.ui.OrderItemDto;
import al.silvio.warehouse.model.ui.PagedResult;
import al.silvio.warehouse.model.ui.ShippingDay;
import al.silvio.warehouse.model.ui.TruckDto;
import al.silvio.warehouse.utils.CustomException;
import al.silvio.warehouse.utils.OrderUtils;
import com.remondis.remap.Mapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderManager orderManager;
    private final UserService userService;
    private final Mapper<Truck, TruckDto> truckEntityDtoMapper;
    private final Mapper<TruckDto, Truck> truckDtoEntityMapper;
    private final Mapper<OrderItem, OrderItemDto> orderItemDtoMapper;
    private final Mapper<OrderItemDto, OrderItem> orderItemEntityMapper;
    private final AuthenticationService authenticationService;
    private final OrderUtils orderUtils;
    
    @PostAuthorize("hasAnyAuthority('WAREHOUSE_MANAGER', 'CLIENT') or returnObject.customer.email == principal.username")
    public OrderExtendedDto getByOrderNumber(Long orderNumber) {
        Order order = orderManager.getById(orderNumber);
        
        List<OrderItemDto> items = order.getOrderedItems().stream()
                .map(orderItemDtoMapper::map).toList();
        
        List<TruckDto> trucks = order.getOrderTrucks().stream().map(OrderTruck::getTruck).map(truckEntityDtoMapper::map)
                .toList();
        User customer = order.getCustomer();
        return OrderExtendedDto.builder()
                .orderNumber(orderNumber)
                .submittedDate(order.getSubmittedDate())
                .status(order.getStatus())
                .customer(new OrderAuthority(customer.getId(), customer.getEmail())).items(items)
                .trucks(trucks).build();
    }
    public PagedResult<OrderDto> getList(OrderStatus status, String userName, int page, int size) {
        if(authenticationService.getRole().equals("CLIENT")) {
            userName = authenticationService.getPrincipal().getUsername();
        }
        OrderSpecification orderSearch = new OrderSpecification(status, userName);
        Pageable pageRequest = PageRequest.of(page, size);
        Page<OrderDto> orderPage = orderManager.getList(orderSearch, pageRequest)
                .map(o -> OrderDto.builder()
                        .orderNumber(o.getOrderNumber())
                        .submittedDate(o.getSubmittedDate())
                        .status(o.getStatus())
                        .customer(o.getCustomer().getEmail())
                        .build());
        return PagedResult.fromPage(orderPage);
    }
    public List<ShippingDay> getAvailableDates(Long period) {
        return orderUtils.getBusinessDates(period).stream()
                .map(d -> new ShippingDay(d, d.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())))
                .collect(Collectors.toList());
    }
    
    public Long createOrder(OrderExtendedDto orderRequest) {
        String username = authenticationService.getPrincipal().getUsername();
        User customer = userService.getExistingUser(username);
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        order.setSubmittedDate(LocalDate.now());
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(o -> {
                    OrderItem orderItem = orderItemEntityMapper.map(o);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());
        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        order.setCustomer(customer);
        order.setOrderedItems(orderItems);
       return orderManager.createOrder(order).getOrderNumber();
    }
    
    public void updateOrder(OrderExtendedDto orderRequest) {
        Order existing = orderManager.getById(orderRequest.getOrderNumber());
        OrderStatus statusUpdate = orderRequest.getStatus();
        OrderStatus currentStatus = existing.getStatus();
        String role = authenticationService.getRole();
        
        orderUtils.validateStatusUpdate(currentStatus, statusUpdate, role);
        existing.setStatus(statusUpdate);
        
        if (orderUtils.isSimpleStatusUpdate(statusUpdate)) {
            orderManager.updateOrder(existing);
            return;
        }
    
        if (orderUtils.isCustomerAction(currentStatus, statusUpdate)) {
            addItemsToOrder(existing, orderRequest.getItems());
        }
        
        if (statusUpdate.equals(OrderStatus.UNDER_DELIVERY)) {
            addTrucksToOrder(existing, orderRequest.getTrucks());
            existing.setDeadlineDate(existing.getDeadlineDate());
        }
        
        orderManager.updateOrder(existing);
    }
    
    public void deleteOrder(Long orderNumber) {
        orderManager.deleteOrder(orderNumber);
    }
    
    private void addItemsToOrder(Order order, @NonNull List<OrderItemDto> orderItemDtoList) {
        List<OrderItem> orderedItems = mapOrderItemDtoList(order, orderItemDtoList);
        order.setOrderedItems(orderedItems);
    }
    
    private void addTrucksToOrder(Order order, @NonNull List<TruckDto> orderTruckList) {
        List<OrderTruck> orderTrucks = mapTruckDtoList(order, orderTruckList);
        double capacity = orderTrucks.stream()
                .map(OrderTruck::getTruck)
                .map(Truck::getContainerVolume)
                .mapToDouble(Double::doubleValue)
                .sum();
    
        double volumeToBeShipped = order.getOrderedItems().stream()
                .map(o -> o.getItem().getPackageVolume() * o.getQuantity())
                .mapToDouble(Double::doubleValue)
                .sum();
        
        if(volumeToBeShipped > capacity) {
            throw new CustomException("The trucks you have selected cannot fulfill this shipment.", 400);
        }
        
        order.setOrderTrucks(orderTrucks);
    }
    
    private List<OrderTruck> mapTruckDtoList(Order order, @NonNull List<TruckDto> trucks) {
        return trucks.stream().map(truckDtoEntityMapper::map)
                .map(truck -> OrderTruck.builder()
                        .order(order)
                        .truck(truck)
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<OrderItem> mapOrderItemDtoList(Order order, List<OrderItemDto> orderItemDtoList) {
        return orderItemDtoList.stream()
                .map(o -> {
                    OrderItem orderItem = orderItemEntityMapper.map(o);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());
    }
}
