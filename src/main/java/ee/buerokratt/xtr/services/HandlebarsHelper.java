package ee.buerokratt.xtr.services;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class HandlebarsHelper {
    public String apply(String template, Map<String, String> values) throws IOException {
        Handlebars hbs = new Handlebars();
        Template result = hbs.compileInline(template);
        return result.apply(values);
    }
}
