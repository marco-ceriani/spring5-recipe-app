package guru.springframework.services;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;
    private final UnitOfMeasureToUnitOfMeasureCommand outConverter;

    public UnitOfMeasureServiceImpl(UnitOfMeasureReactiveRepository unitOfMeasureRepository, UnitOfMeasureToUnitOfMeasureCommand outConverter) {
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.outConverter = outConverter;
    }

    @Override
    public Flux<UnitOfMeasureCommand> listAllUoms() {
        return unitOfMeasureRepository.findAll()
                .map(outConverter::convert);
    }
}
