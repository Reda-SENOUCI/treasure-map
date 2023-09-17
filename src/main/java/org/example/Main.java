package org.example;

import org.example.domain.service.IDGeneratorService;
import org.example.domain.service.RunnerService;
import org.example.port.adapter.cli.CLIController;
import org.example.port.adapter.cli.InvalidDataException;
import org.example.port.adapter.cli.UnkownOrientation;
import org.example.port.adapter.service.UUIDGeneratorService;

import java.io.IOException;

public class Main {

    static final private IDGeneratorService idGeneratorService;
    static final private RunnerService RUNNER_SERVICE;

    static {
        idGeneratorService = new UUIDGeneratorService();
        RUNNER_SERVICE = new RunnerService();
    }
    public static void main(String[] args) throws InvalidDataException, IOException, UnkownOrientation {
        // run the application with CLI listener
        if(args.length > 0){
            CLIController cliController = new CLIController(RUNNER_SERVICE);
            cliController.startSimulation(args);
        }
        // if tomorrow we need a pub/sub listener, we can do that here is as well

    }
}
