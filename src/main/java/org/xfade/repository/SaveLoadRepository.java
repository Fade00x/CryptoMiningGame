package org.xfade.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.xfade.domain.Player;

import java.io.File;
import java.io.IOException;

public class SaveLoadRepository {
    private static final String SAVE_FILE = "savegame.json";
    private final ObjectMapper mapper;

    public SaveLoadRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void save(Player player) throws IOException {
        mapper.writeValue(new File(SAVE_FILE), player);
    }

    public Player load() throws IOException {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return null;
        }
        return mapper.readValue(file, Player.class);
    }

    public boolean hasSaveFile() {
        return new File(SAVE_FILE).exists();
    }
}
