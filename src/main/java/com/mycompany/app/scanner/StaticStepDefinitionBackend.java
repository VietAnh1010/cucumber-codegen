package com.mycompany.app.scanner;

import java.nio.file.Path;

import com.mycompany.app.backend.StepDefinitionBackend;
import com.mycompany.app.logging.Logger;
import com.mycompany.app.logging.LoggerFactory;

import io.cucumber.core.backend.Glue;

public class StaticStepDefinitionBackend {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepDefinitionBackend.class);

    StaticStepDefinitionBackend() {
    }

    public void loadGlue(Glue glue, Path path) {
        
    }
    
    
}
