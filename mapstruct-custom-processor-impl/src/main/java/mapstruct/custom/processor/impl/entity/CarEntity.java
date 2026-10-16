package mapstruct.custom.processor.impl.entity;

import lombok.Getter;
import lombok.Setter;

//@Entity
//@Table(name = "car")
@Getter
@Setter
public class CarEntity {

    private Integer id;
    private String name;
}
