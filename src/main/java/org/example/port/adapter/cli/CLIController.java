package com.example.treasuremap.port.adapter.cli;

import com.example.treasuremap.domain.exception.CoordinatesAlreadyUsedException;
import com.example.treasuremap.domain.exception.DomainError;
import com.example.treasuremap.domain.exception.OutOfMapBoundariesException;
import com.example.treasuremap.domain.model.Category;
import com.example.treasuremap.domain.model.Coordinates;
import com.example.treasuremap.domain.model.GridItem;
import com.example.treasuremap.domain.model.IDGeneratorService;
import com.example.treasuremap.domain.model.SimulationRequest;
import com.example.treasuremap.domain.model.TreasureMap;
import com.example.treasuremap.domain.service.RunnerService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CLIController {

    final private RunnerService runnerService;
    final private IDGeneratorService idGeneratorService;

    public CLIController(RunnerService runnerService, IDGeneratorService idGeneratorService) {
        this.runnerService = runnerService;
        this.idGeneratorService = idGeneratorService;
    }

    public void startSimulation(String[] args) throws IOException, DomainError, InvalidDataException {
        this.checkSystemArguments(args);
        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        SimulationRequest request = convertFileToTreasureMap(inputFile);

        this.runnerService.init(request);

        this.runnerService.run();

        this.printResult(this.runnerService.getTreasureMap(), outputFile);
    }

    protected SimulationRequest convertFileToTreasureMap(File inputFile) throws InvalidDataException, IOException, OutOfMapBoundariesException, CoordinatesAlreadyUsedException {
        TreasureMap treasureMap;
        DataMapper mapper = new DataMapper();
        DataValidator dataValidator = new DataValidator();
        LinkedHashMap adventurers = new LinkedHashMap();
        // To avoid app crash when large file we load the file by a stream (lazy loading)
        try (Stream<String> lines = Files.lines(inputFile.toPath(), StandardCharsets.UTF_8)) {
            Iterator<String> iterator = lines.iterator();
            if (!iterator.hasNext()) {
                throw new InvalidDataException("empty file");
            }
            String data = "";
            while (iterator.hasNext()) {
                data = iterator.next();
                if (!dataValidator.isValid(data)) {
                    throw new InvalidDataException(data);
                }
                // Skip comments
                if (!data.startsWith("#")) {
                    break;
                }
            }
            // At this point we skip all comment. We should be on the first line
            // We will use this line to initialize the map boundaries and construct the treasure map on it
            treasureMap = mapper.createTreasureMap(data);
            // We will add mountains, adventurer, treasure to treasure map
            while (iterator.hasNext()) {
                data = iterator.next();
                if (!dataValidator.isValid(data)) {
                    throw new InvalidDataException(data);
                }
                // Skip comments
                if (data.startsWith("#")) {
                    continue;
                }

                final String id = idGeneratorService.generateID();

                Map<Coordinates, GridItem> mapElement = mapper.createMapElementFromInput(id, data);
                treasureMap.addItemToGrid(mapElement);

                if (mapElement.getCategory().equals(Category.ADVENTURER)) {
                    adventurers.put(mapElement.getID(), mapElement.getCoordinates());
                }
            }
        }

        return new SimulationRequest(treasureMap, adventurers);
    }

    protected void printResult(TreasureMap treasureMap, File outputFile) throws IOException {

        StringBuilder str = new StringBuilder();

        str.append(treasureMap + "\n");
        treasureMap.getGrid().forEach((coordinates, items) ->
                items.forEach((id, item) -> str.append(item + "\n")));

        Files.write(outputFile.toPath(), str.toString().getBytes());
    }

    protected void checkSystemArguments(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("we should have 2 arguments, input file and an empty output file");
        }

        if (!new File(args[0]).exists()) {
            throw new IllegalArgumentException("invalid input file path ");
        }

        if (!new File(args[1]).exists()) {
            throw new IllegalArgumentException("invalid output file path");
        }
    }
}
