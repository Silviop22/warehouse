package al.silvio.warehouse.api.service;

import al.silvio.warehouse.auth.model.ui.UserDto;
import al.silvio.warehouse.auth.model.user.User;
import al.silvio.warehouse.auth.model.user.UserRole;
import al.silvio.warehouse.auth.service.TokenService;
import al.silvio.warehouse.manager.OrderManager;
import al.silvio.warehouse.model.Item;
import al.silvio.warehouse.model.Order;
import al.silvio.warehouse.model.OrderFilterParam;
import al.silvio.warehouse.model.OrderItem;
import al.silvio.warehouse.model.OrderSpecification;
import al.silvio.warehouse.model.OrderStatus;
import al.silvio.warehouse.model.OrderTruck;
import al.silvio.warehouse.model.Truck;
import al.silvio.warehouse.model.ui.ItemDto;
import al.silvio.warehouse.model.ui.OrderAuthority;
import al.silvio.warehouse.model.ui.OrderDto;
import al.silvio.warehouse.model.ui.OrderExtendedDto;
import al.silvio.warehouse.model.ui.OrderItemDto;
import al.silvio.warehouse.model.ui.TruckDto;
import com.remondis.remap.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderManager orderManager;
    private final Mapper<Truck, TruckDto> truckEntityDtoMapper;
    private final Mapper<TruckDto, Truck> truckDtoEntityMapper;
    private final Mapper<Item, ItemDto> itemEntityDtoMapper;
    private final Mapper<ItemDto, Item> itemDtoEntityMapper;
    private final TokenService tokenService;
    
    public OrderExtendedDto getByOrderNumber(Long orderNumber) {
        Order order = orderManager.getById(orderNumber);
        
        List<OrderItemDto> items = order.getOrderedItems().stream()
                .map(o -> new OrderItemDto(o.getId(), o.getOrder().getOrderNumber(),
                        itemEntityDtoMapper.map(o.getItem()), o.getQuantity())).toList();
        
        List<TruckDto> trucks = order.getOrderTrucks().stream().map(OrderTruck::getTruck).map(truckEntityDtoMapper::map)
                .toList();
        User customer = order.getCustomer();
        return OrderExtendedDto.builder().orderNumber(orderNumber).submittedDate(order.getSubmittedDate())
                .status(order.getStatus())
                .customer(new OrderAuthority(customer.getId(), customer.getEmail(), customer.getRole())).items(items)
                .trucks(trucks).build();
    }
    
    public List<OrderDto> getList(String token, OrderStatus status) {
        OrderFilterParam params = new OrderFilterParam();
        UserDto user = tokenService.getPrincipal(token);
        if (user.getRole().equals(UserRole.CLIENT)) {
            params.setUserName(user.getEmail());
        }
        params.setStatus(status);
        
        Specification<Order> specification = OrderSpecification.getFilteredOrders(params);
        return orderManager.getList(specification).stream()
                .map(o -> OrderDto.builder().orderNumber(o.getOrderNumber()).submittedDate(o.getSubmittedDate())
                        .status(o.getStatus()).build()).toList();
    }
    
    public void createOrder(OrderExtendedDto orderRequest, String token) {
        UserDto user = tokenService.getPrincipal(token);
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        order.setSubmittedDate(LocalDate.now());
        
        User customer = User.builder().id(user.getId()).build();
        
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(o -> new OrderItem(o.getId(), itemDtoEntityMapper.map(o.getItem()), o.getQuantity(), order))
                .toList();
        
        order.setCustomer(customer);
        order.setOrderedItems(orderItems);
        orderManager.createOrder(order);
    }
    
    public void updateOrder(OrderExtendedDto orderRequest) {
        Order updateCandidate = new Order();
        updateCandidate.setOrderNumber(orderRequest.getOrderNumber());
        updateCandidate.setSubmittedDate(orderRequest.getSubmittedDate());
        updateCandidate.setStatus(orderRequest.getStatus());
        updateCandidate.setDeadlineDate(orderRequest.getDeadlineDate());
        
        if (orderRequest.getItems() != null) {
            List<OrderItem> orderedItems = orderRequest.getItems().stream()
                    .map(o -> new OrderItem(o.getId(), itemDtoEntityMapper.map(o.getItem()), o.getQuantity(),
                            updateCandidate)).collect(Collectors.toList());
            updateCandidate.setOrderedItems(orderedItems);
        }
        
        if (orderRequest.getTrucks() != null) {
            List<OrderTruck> trucks = orderRequest.getTrucks().stream().map(truckDtoEntityMapper::map)
                    .map(truck -> OrderTruck.builder().order(updateCandidate).truck(truck).build())
                    .collect(Collectors.toList());
            updateCandidate.setOrderTrucks(trucks);
        }
        
        orderManager.updateOrder(updateCandidate);
    }
    
    public void deleteOrder(Long orderNumber, String token) {
        tokenService.authorizeOperation(token, List.of(UserRole.WAREHOUSE_MANAGER));
        orderManager.deleteOrder(orderNumber);
    }
}
