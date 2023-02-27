package al.silvio.warehouse.api.service;

import al.silvio.warehouse.manager.TruckManager;
import al.silvio.warehouse.model.Truck;
import al.silvio.warehouse.model.TruckTier;
import al.silvio.warehouse.model.ui.TruckDto;
import com.remondis.remap.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    
    public List<TruckDto> getTruckList(Double requestedQuantity, LocalDate shippingDate) {
        TruckTier preferredTier = null;
        if(requestedQuantity != null) {
            preferredTier = getTierByCapacity(requestedQuantity, 1.0);
        }
        return truckManager.getTruckList(shippingDate, preferredTier).stream()
                .map(truckEntityDtoMapper::map)
                .collect(Collectors.toList());
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
    
    private TruckTier getTierByCapacity(Double neededCapacity, Double divisor) {
        if(neededCapacity <= TruckTier.TIER_1.getMaximumCapacity()) return TruckTier.TIER_1;
        else if(neededCapacity <= TruckTier.TIER_2.getMaximumCapacity()) return TruckTier.TIER_2;
        else if(neededCapacity <= TruckTier.TIER_3.getMaximumCapacity()) return TruckTier.TIER_3;
        else if(neededCapacity <= TruckTier.TIER_4.getMaximumCapacity()) return TruckTier.TIER_4;
        else return getTierByCapacity(neededCapacity / divisor, divisor+1 );
    }
}
