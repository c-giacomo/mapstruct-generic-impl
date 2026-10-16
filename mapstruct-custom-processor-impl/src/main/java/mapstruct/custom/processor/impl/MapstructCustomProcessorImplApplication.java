package mapstruct.custom.processor.impl;

import lombok.extern.slf4j.Slf4j;
import mapstruct.custom.processor.impl.mapper.CarMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MapstructCustomProcessorImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(MapstructCustomProcessorImplApplication.class, args);
    }

}
