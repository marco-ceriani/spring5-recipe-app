package guru.springframework.services;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnitOfMeasureServiceImplTest  {

    private UnitOfMeasureToUnitOfMeasureCommand outConverter = new UnitOfMeasureToUnitOfMeasureCommand();
    private UnitOfMeasureService service;

    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new UnitOfMeasureServiceImpl(unitOfMeasureRepository, outConverter);
    }

    @Test
    public void testListAllUoms() {
        // given
        UnitOfMeasure uom1 = new UnitOfMeasure();
        uom1.setId("1");

        UnitOfMeasure uom2 = new UnitOfMeasure();
        uom2.setId("2");

        when(unitOfMeasureRepository.findAll()).thenReturn(Flux.just(uom1, uom2));

        // when
        List<UnitOfMeasureCommand> commands = service.listAllUoms().collectList().block();

        // then
        assertEquals(2, commands.size());
        verify(unitOfMeasureRepository).findAll();
    }
}
