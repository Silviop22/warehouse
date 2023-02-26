package al.silvio.warehouse.manager;

import al.silvio.warehouse.model.Item;
import al.silvio.warehouse.repository.ItemRepository;
import al.silvio.warehouse.utils.CustomException;
import com.remondis.remap.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemManager {
    private final ItemRepository repository;
    private final Mapper<Item, Item> itemEntityMapper;
    
    public List<Item> getList() {
        return repository.findAll();
    }
    
    public Item createItem(Item item) {
        return repository.save(item);
    }
    
    public void updateItem(Item updateCandidate) {
        Item existing = getById(updateCandidate.getId());
        existing = itemEntityMapper.map(updateCandidate, existing);
        repository.save(existing);
    }
    
    public Item getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException("There is no existing item with id: " + id, 404));
    }
    
    public void deleteItem(Long id) {
        Item existing = getById(id);
        repository.delete(existing);
    }
}
