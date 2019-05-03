package templater;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class PageGenerator {

    private static final String TEMPLATES_DIR = "templates";

    private static PageGenerator pageGenerator;
    private final Configuration configuration;

    public static PageGenerator instance() {
        if(pageGenerator == null)
            pageGenerator = new PageGenerator();
        return pageGenerator;
    }

    public String getPage(String filename, Map<String, Object> data) {
        Writer stream = new StringWriter();
        try{
            Template template = configuration.getTemplate(filename);
            template.process(data, stream);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }

        return stream.toString();
    }

    private PageGenerator() {
        configuration = new Configuration(Configuration.VERSION_2_3_28);
        //configuration.setClassForTemplateLoading(this.getClass(), TEMPLATES_DIR);
        try {
            configuration.setDirectoryForTemplateLoading(new File(TEMPLATES_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);

    }

}
