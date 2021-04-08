package de.oshgnacknak.create_ponder_wonder;

import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderScene;

import java.util.List;
import java.util.stream.Stream;

public class PonderIndexer {

    private PonderIndexer() {}

    public static Stream<PonderScene> getPonders() {
        return PonderRegistry.all
            .values()
            .stream()
            .map(PonderRegistry::compile)
            .flatMap(List::stream);
    }

}
