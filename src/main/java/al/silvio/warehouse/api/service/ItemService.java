package al.silvio.warehouse.api.service;

import al.silvio.warehouse.manager.ItemManager;
import al.silvio.warehouse.model.Item;
import al.silvio.warehouse.model.ui.ItemDto;
import com.remondis.remap.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    public final Mapper<Item, ItemDto> itemEntityDtoMapper;
    public final Mapper<ItemDto, Item> itemDtoEntityMapper;
    private final ItemManager itemManager;
    
    public ItemDto getById(Long id) {
        return itemEntityDtoMapper.map(itemManager.getById(id));
    }
    
    public List<ItemDto> getList() {
        return itemManager.getList().stream().map(itemEntityDtoMapper::map).toList();
    }
    
    public Long createItem(ItemDto itemDto) {
        return itemManager.createItem(itemDtoEntityMapper.map(itemDto)).getId();
    }
    
    public void updateItem(ItemDto itemDto, Long id) {
        Item updateCandidate = itemDtoEntityMapper.map(itemDto);
        updateCandidate.setId(id);
        itemManager.updateItem(updateCandidate);
    }
    
    public void deleteItem(Long id) {
        itemManager.deleteItem(id);
    }
}
