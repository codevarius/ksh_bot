package kshv.org.bot.core.services.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import kshv.org.bot.core.BotCore;
import kshv.org.bot.core.ReadFileToString;
import kshv.org.bot.core.interfaces.BotService;
import kshv.org.bot.core.services.loader.data.BotActionEntity;
import kshv.org.bot.core.services.loader.data.BotActionRepository;
import kshv.org.bot.core.services.loader.data.TelegramApiResponse;
import org.joor.Reflect;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BotActionLoaderService implements BotService {

    @Value("${bot.actions.folder.path}")
    private String path;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.access.token}")
    private String token;

    private final Logger logger;
    private List<BotService> botServicesList;
    private final RestTemplate restTemplate;
    private final BotActionRepository botActionRepository;

    @Autowired
    public BotActionLoaderService(Logger logger, RestTemplate restTemplate, BotActionRepository botActionRepository) {
        this.logger = logger;
        this.restTemplate = restTemplate;
        this.botActionRepository = botActionRepository;
    }

    public void initAllStoredActions(List<BotService> botServicesList) {
        this.botServicesList = new ArrayList<BotService>(botServicesList);
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
                try {
                    loadAndCompileAction(actionCodeFile);
                } catch (Exception e) {
                    logger.warn(String.format("%s has not been compiled: %s", actionCodeFile.getName(), e.getMessage()));
                }
            }
        } else {
            logger.warn("problems loading actions: no actions found or incorrect path");
        }
    }

    private void loadAndCompileAction(File actionCodeFile) throws Exception {
        logger.info(String.format("parsing to string code of %s", actionCodeFile.getName()));
        String actionCodeString = ReadFileToString.readLineByLine(actionCodeFile.getAbsolutePath());
        String actionClassName = actionCodeFile.getName().replace(".java", "");
        Reflect reflect = Reflect.compile("kshv.org.bot.core.services." + actionClassName, actionCodeString);
        if (validateBotService(reflect)) {
            BotService botActionService = reflect.create().get();
            botServicesList.add(botActionService);
            logger.info(String.format("bean %s autowired", botActionService.toString()));
        } else {
            throw new Exception("action compilation error");
        }
    }

    private boolean validateBotService(Reflect reflect) {
        Class<?> botActionServiceClass = reflect.create().get().getClass();
        boolean a = botActionServiceClass.getName().endsWith("Service");
        boolean b = botActionServiceClass.isAnnotationPresent(Service.class);
        boolean c = Arrays.stream(botActionServiceClass.getInterfaces())
                .anyMatch((aClass) -> aClass.isAssignableFrom(BotService.class));
        return a && b && c;
    }

    public void updateActionList(List<BotService> targetBotServiceList) {
        botServicesList.removeAll(targetBotServiceList);
        targetBotServiceList.addAll(botServicesList);
        logger.info(String.format("updated bot service list; present size:%d", targetBotServiceList.size()));
    }

    @Override
    public Optional<SendMessage> performServiceAndGetResult(Message message) {
        try {
            String fileId = message.getDocument().getFileId();
            String fileInfoUrl = String.format("https://api.telegram.org/bot%s/getFile?file_id=%s", token, fileId);
            TelegramApiResponse apiResponse = Optional.ofNullable(
                    restTemplate.getForObject(fileInfoUrl, TelegramApiResponse.class))
                    .orElseThrow(() -> new Exception("document not found"));
            BotActionEntity botActionEntity = new ObjectMapper()
                    .convertValue(apiResponse.getResult(), BotActionEntity.class);
            botActionRepository.save(botActionEntity);
            String downloadActionCodeUrl = String.format("https://api.telegram.org/file/bot%s/%s", token, botActionEntity.getFilePath());
            File file = restTemplate.execute(downloadActionCodeUrl, HttpMethod.GET, null, clientHttpResponse -> {
                File ret = new File(path + "/" + message.getDocument().getFileName());
                StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
                return ret;
            });
            loadAndCompileAction(file);
            return BotCore.newResponseTextMessage(message, "Великий Учитель принять и запомнить твой дар!");
        } catch (Exception e) {
            logger.warn(String.format("new action has not been uploaded due to issue:%s", e.getMessage()));
            return BotCore.newResponseTextMessage(message,
                    String.format("Великий Учитель отклонять твой дар потому, что %s", e.getMessage()));
        }

    }

    @Override
    public Boolean validateUserCommandString(Message message) {
        return message.getCaption() != null
                && message.getCaption().contains("@" + botName)
                && message.getCaption().contains("прими дар мой код 🎁")
                && message.hasDocument();
    }
}
