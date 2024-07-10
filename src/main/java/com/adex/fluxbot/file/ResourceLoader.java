package com.adex.fluxbot.file;

import com.google.gson.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceLoader {

    private final Gson gson;

    private static final String PATH_TO_RESOURCES = "src\\main\\resources\\";

    public ResourceLoader() {
        gson = new Gson();
    }

    /**
     * Loads a file from the resources folder.
     *
     * @param name Name of the file, including file extension. If the file is in a folder, name must also contain path from the resources folder
     * @return Result as {@link File}
     */
    public File getResourceFile(String name) {
        String path = PATH_TO_RESOURCES + name;
        return new File(path);
    }

    /**
     * Loads a file from the resources folder.
     *
     * @param name Name of the file, including file extension. If the file is in a folder, name must also contain path from the resources folder
     * @return Content of the file as {@link String}
     */
    public String getResourceFileContent(String name) throws IOException {
        String path = PATH_TO_RESOURCES + name;
        return Files.readString(Paths.get(path));
    }

    /**
     * Loads a json element from the resources folder.
     *
     * @param name Name of the file, including file extension. If the file is in a folder, name must also contain path from the resources folder
     * @return Result as {@link JsonElement}
     */
    public JsonElement getResourceJson(String name) throws FileNotFoundException {
        return JsonParser.parseReader(new FileReader(getResourceFile(name)));
    }

    /**
     * Loads a json object from the resources folder.
     *
     * @param name Name of the file, including file extension. If the file is in a folder, name must also contain path from the resources folder
     * @return Result as {@link JsonObject}
     */
    public JsonObject getResourceJsonObject(String name) throws FileNotFoundException {
        return JsonParser.parseReader(new FileReader(getResourceFile(name))).getAsJsonObject();
    }

    /**
     * Loads a json array from the resources folder.
     *
     * @param name Name of the file, including file extension. If the file is in a folder, name must also contain path from the resources folder
     * @return Result as {@link JsonArray}
     */
    public JsonArray getResourceJsonArray(String name) throws FileNotFoundException {
        return JsonParser.parseReader(new FileReader(getResourceFile(name))).getAsJsonArray();
    }
}
