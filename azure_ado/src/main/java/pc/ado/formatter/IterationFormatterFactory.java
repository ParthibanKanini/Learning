package pc.ado.formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating appropriate iteration formatters based on format type.
 */
public class IterationFormatterFactory {

    private static final Logger logger = LoggerFactory.getLogger(IterationFormatterFactory.class);

    public enum FormatterType {
        JSON("json"),
        TSV("tsv");

        private final String value;

        FormatterType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static FormatterType fromString(String value) {
            if (value == null || value.isEmpty()) {
                return JSON; // Default to JSON
            }
            for (FormatterType type : FormatterType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            logger.warn("Unknown formatter type '{}', defaulting to JSON", value);
            return JSON;
        }
    }

    /**
     * Creates an appropriate formatter based on the specified type.
     *
     * @param formatterType the type of formatter to create
     * @return the appropriate IterationFormatter implementation
     */
    public static IterationFormatter createFormatter(FormatterType formatterType) {
        switch (formatterType) {
            case TSV:
                logger.debug("Creating TSV formatter");
                return new TsvIterationFormatter();
            case JSON:
            default:
                logger.debug("Creating JSON formatter");
                return new JsonIterationFormatter();
        }
    }

    /**
     * Creates an appropriate formatter based on the specified type string.
     *
     * @param formatterType string representation of formatter type (json, tsv)
     * @return the appropriate IterationFormatter implementation
     */
    public static IterationFormatter createFormatter(String formatterType) {
        return createFormatter(FormatterType.fromString(formatterType));
    }
}
