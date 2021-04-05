package kshv.org.bot.core.services.loader;

import kshv.org.bot.core.BotCore;
import kshv.org.bot.core.ReadFileToString;
import kshv.org.bot.core.interfaces.BotService;
import org.joor.Reflect;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Optional;

@Service
public class BotActionLoaderService {

    @Value("${bot.actions.folder.path}")
    private String path;

    private final Logger logger;
    private List<BotService> botServicesList;

    @Autowired
    public BotActionLoaderService(Logger logger) {
        this.logger = logger;
    }

    public void initAllStoredActions(List<BotService> botServicesList) {
        this.botServicesList = botServicesList;
        File dir = new File(path);
        FilenameFilter filter = (f, name) -> {
            logger.info(String.format("reading %s", name));
            return name.endsWith(".java");
        };
        Optional<File[]> files = Optional.ofNullable(dir.listFiles(filter));

        if (files.isPresent() && files.get().length > 0) {
            File[] actionCodeFilesArray = files.get();
            logger.info(String.format("%s actions detected", actionCodeFilesArray.length));
            for (File actionCodeFile : actionCodeFilesArray) {
                loadAndCompileAction(actionCodeFile);
            }
        } else {
            logger.warn("problems loading actions: no actions found or incorrect path");
        }
    }

    private void loadAndCompileAction(File actionCodeFile) {
        logger.info(String.format("parsing to string code of %s", actionCodeFile.getName()));
        String actionCodeString = ReadFileToString.readLineByLine(actionCodeFile.getAbsolutePath());
        String actionClassName = actionCodeFile.getName().replace(".java", "");
        Reflect reflect = Reflect.compile("kshv.org.bot.core.services." + actionClassName, actionCodeString);
        BotService botActionService = reflect.create().get();
        if (validateBotService(botActionService)) {
            botServicesList.add(botActionService);
            logger.info(String.format("bean %s autowired", botActionService.toString()));
        }
    }

    private boolean validateBotService(BotService botActionService) {
        return true; //TODO set logic;
    }


}
