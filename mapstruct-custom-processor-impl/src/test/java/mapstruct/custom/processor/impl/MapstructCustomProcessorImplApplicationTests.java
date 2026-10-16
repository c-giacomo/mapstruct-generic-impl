package mapstruct.custom.processor.impl;

import lombok.extern.slf4j.Slf4j;
import mapstruct.custom.processor.impl.dto.Car;
import mapstruct.custom.processor.impl.entity.CarEntity;
import mapstruct.custom.processor.impl.mapper.GenericBeansMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
class MapstructCustomProcessorImplApplicationTests {

    @Autowired
    private GenericBeansMapper mapper;

    @Test
    void fromDtoToEntityNew() {
        Car car = new Car();
        car.setId(1);
        car.setName("Ferrari");

        CarEntity carEntity = mapper.deserializza(CarEntity.class, car);
        assertEquals(1, (int) carEntity.getId());
        assertEquals("Ferrari", carEntity.getName());
    }

    @Test
    void fromDtoToEntityExistent() {
        Car car = new Car();
        car.setId(1);
        car.setName("Ferrari");
        assertEquals("Ferrari", car.getName());

        CarEntity carEntity = new CarEntity();
        carEntity.setId(1);
        carEntity.setName("Fiat");
        assertEquals("Fiat", carEntity.getName());

        carEntity = mapper.aggiorna(car, carEntity);
        assertEquals("Ferrari", carEntity.getName());
    }

}
