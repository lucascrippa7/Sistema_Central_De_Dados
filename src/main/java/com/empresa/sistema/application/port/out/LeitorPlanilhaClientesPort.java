package com.empresa.sistema.application.port.out;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface LeitorPlanilhaClientesPort {
    List<LinhaClientePlanilha> ler(InputStream arquivo) throws IOException;
}
