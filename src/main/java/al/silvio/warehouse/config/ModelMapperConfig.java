package al.silvio.warehouse.config;

import al.silvio.warehouse.model.Item;
import al.silvio.warehouse.model.Order;
import al.silvio.warehouse.model.OrderItem;
import al.silvio.warehouse.model.Truck;
import al.silvio.warehouse.model.ui.ItemDto;
import al.silvio.warehouse.model.ui.OrderItemDto;
import al.silvio.warehouse.model.ui.TruckDto;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Configuration
public class ModelMapperConfig {
    
    @Bean
    public Mapper<TruckDto, Truck> truckDtoEntityMapper() {
        return Mapping.from(TruckDto.class)
                .to(Truck.class)
                .omitInDestination(Truck::getOrderTruck)
                .mapper();
    }
    
    @Bean
    public Mapper<Truck, TruckDto> truckEntityDtoMapper() {
        return Mapping.from(Truck.class).to(TruckDto.class).omitInSource(Truck::getOrderTruck).mapper();
    }
    
    @Bean
    public Mapper<Item, Item> itemEntityMapper() {
        return Mapping.from(Item.class).to(Item.class).mapper();
    }
    
    @Bean
    public Mapper<Item, ItemDto> itemEntityDtoMapper() {
        return Mapping.from(Item.class).to(ItemDto.class).mapper();
    }
    
    @Bean
    public Mapper<ItemDto, Item> itemDtoEntityMapper() {
        return Mapping.from(ItemDto.class).to(Item.class).mapper();
    }
    
    @Bean
    public Mapper<OrderItem, OrderItemDto> orderItemDtoMapper() {
        return Mapping.from(OrderItem.class)
                .to(OrderItemDto.class)
                .replace(OrderItem::getOrder, OrderItemDto::getOrderNumber)
                .withSkipWhenNull(Order::getOrderNumber)
                .useMapper(itemEntityDtoMapper())
                .mapper();
    }
    
    @Bean
    public Mapper<OrderItemDto, OrderItem> orderItemEntityMapper() {
        return Mapping.from(OrderItemDto.class)
                .to(OrderItem.class)
                .omitInSource(OrderItemDto::getOrderNumber)
                .omitInDestination(OrderItem::getOrder)
                .useMapper(itemDtoEntityMapper())
                .mapper();
    }
}
