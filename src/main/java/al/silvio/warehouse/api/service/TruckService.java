package al.silvio.warehouse.api.service;

import al.silvio.warehouse.manager.TruckManager;
import al.silvio.warehouse.model.Truck;
import al.silvio.warehouse.model.ui.TruckDto;
import com.remondis.remap.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TruckService {
    private final TruckManager truckManager;
    private final Mapper<TruckDto, Truck> truckDtoEntityMapper;
    private final Mapper<Truck, TruckDto> truckEntityDtoMapper;
    
    public TruckDto getTruckByChassisNumber(String chassisNumber) {
        Truck truckEntity = truckManager.getByChassisNumber(chassisNumber);
        return truckEntityDtoMapper.map(truckEntity);
    }
    
    public List<TruckDto> getTruckList() {
        return truckManager.getTruckList().stream().map(truckEntityDtoMapper::map).toList();
    }
    
    public void createTruck(TruckDto truckRequest) {
        Truck truck = truckDtoEntityMapper.map(truckRequest);
        truckManager.createTruck(truck);
    }
    
    public void updateTruck(TruckDto truckDto, String chassisNumber) {
        Truck updatedCandidate = truckDtoEntityMapper.map(truckDto);
        updatedCandidate.setChassisNumber(chassisNumber);
        truckManager.updateTruck(updatedCandidate);
    }
    
    public void deleteTruck(String chassisNumber) {
        truckManager.deleteTruck(chassisNumber);
    }
}
