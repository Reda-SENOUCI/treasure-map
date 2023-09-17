package org.example.port.adapter.cli;

import org.example.domain.model.Adventurer;
import org.example.domain.model.Boundaries;
import org.example.domain.model.Command;
import org.example.domain.model.Coordinates;
import org.example.domain.model.Mountain;
import org.example.domain.model.Orientation;
import org.example.domain.model.RunRequest;
import org.example.domain.model.Treasure;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class dataConverter {
    public static final String WHITE_SPACE = "\\s";

    public dataConverter() {
    }

    protected RunRequest convertFileToTreasureMap(File inputFile) throws InvalidDataException, IOException, UnkownOrientation {
        List<Adventurer> adventurers = new ArrayList<Adventurer>();
        List<Mountain> mountains = new ArrayList<Mountain>();
        List<Treasure> treasures = new ArrayList<Treasure>();
        DataValidator dataValidator = new DataValidator();
        Boundaries boundaries;
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
            // At this point we skip all comment. We should be on the first instruction line
            // it should be the initialization map boundaries
            boundaries = convertToBoundaries(data);
            // From this point we will contruct Mountains, adventurers and treasures list
            while (iterator.hasNext()) {
                data = iterator.next();
                String[] dataComponents = data.replaceAll(WHITE_SPACE, "").split("-");
                switch (dataComponents[0]) {
                    case "M" -> mountains.add(Mountain.create(Coordinates.create(Integer.parseInt(dataComponents[1]),
                            Integer.parseInt(dataComponents[2]))));
                    case "T" -> treasures.add(Treasure.create(Coordinates.create(Integer.parseInt(dataComponents[1]),
                            Integer.parseInt(dataComponents[2])), Integer.parseInt(dataComponents[3])));
                    case "A" -> adventurers.add(
                            Adventurer.create(
                                    dataComponents[1],
                                    new Coordinates(
                                            Integer.parseInt(dataComponents[2]),
                                            Integer.parseInt(dataComponents[3])
                                    ),
                                    convertToOrientation(dataComponents[4]),
                                    convertToCommands(dataComponents[5])
                            ));
                    default -> throw new InvalidDataException("Invalid data " + data);
                }
            }

        }
        return new RunRequest(mountains, adventurers, treasures, boundaries);
    }

    List<Command> convertToCommands(String data) {
        return new ArrayList<Command>();
    }

    Orientation convertToOrientation(String data) throws UnkownOrientation {
        return switch (data) {
            case "N" -> Orientation.NORTH;
            case "E" -> Orientation.EAST;
            case "O" -> Orientation.WEST;
            case "S" -> Orientation.SOUTH;
            default -> throw new UnkownOrientation(data);
        };
    }

    Boundaries convertToBoundaries(String data) throws InvalidDataException {
        // TODO refactore white space

        String[] dataComponents = data.replaceAll(WHITE_SPACE, "").split("-");
        if (!"C".equals(dataComponents[0])) {
            throw new InvalidDataException(data + " should start with map boundaries");
        }
        return Boundaries.create(Integer.parseInt(dataComponents[1]), Integer.parseInt(dataComponents[2]));
    }
}
