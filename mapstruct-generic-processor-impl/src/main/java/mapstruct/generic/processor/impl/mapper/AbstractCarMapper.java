package mapstruct.generic.processor.impl.mapper;

import com.generic.mapper.annotation.Pojo;
import com.generic.mapper.interfaces.BaseMapper;
import mapstruct.generic.processor.impl.dto.Car;
import mapstruct.generic.processor.impl.entity.CarEntity;
import org.mapstruct.Mapper;

@Pojo
@Mapper(componentModel = "spring")
public abstract class AbstractCarMapper implements BaseMapper<CarEntity, Car> {

    Car customMapping(CarEntity carEntity) {
        Car car = new Car();
        car.setId(carEntity.getId());
        car.setName(carEntity.getName());
        return car;
    }
}
