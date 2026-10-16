package mapstruct.custom.processor.impl.mapper;


import com.generic.mapper.interfaces.BaseMapper;
import com.generic.mapper.annotation.Pojo;
import mapstruct.custom.processor.impl.dto.Car;
import mapstruct.custom.processor.impl.entity.CarEntity;
import org.mapstruct.Mapper;

@Pojo
@Mapper(componentModel = "spring")
public interface CarMapper extends BaseMapper<CarEntity, Car> {

}
