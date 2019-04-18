package tisse.service.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import tisse.dto.DepInfo;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class DepInfoManager {

    @Inject
    private Logger logger;

    public void save(List<DepInfo> depInfos) {
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(depInfos);
        String format = getFileName();
        try {
            Files.write(Paths.get(format), s.getBytes());
        } catch (IOException e) {
            logger.error("{}", e);
        }
    }

    public List<DepInfo> load(){
        if (exists()) {
            Gson gson = new Gson();
            try {
                Type itemsListType = new TypeToken<List<DepInfo>>() {
                }.getType();
                return gson.fromJson(new FileReader(getFileName()), itemsListType);
            } catch (FileNotFoundException e) {
                logger.error("", e);
            }
        }
        return new ArrayList<>();

    }

    private String getFileName() {
        String biosDepDir = System.getProperty("bios.dep.path");
        String format = "depInfo";
        return biosDepDir.concat(format).concat(".json");
    }

    private boolean exists(){
        Path path = Paths.get(getFileName());
        return path.toFile().exists();
    }

}
