package de.tilokowalski.util;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class provides a utility to generate a human-readable string representation of any object.
 * It's a customizable alternative to the traditional toString() method.
 */
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ToString {

    /**
     * Constant representing the opening parenthesis of the objects content.
     */
    private static final char TS_PARANTHESIS_OPEN = '[';

    /**
     * Constant representing the closing parenthesis of the objects content.
     */
    private static final char TS_PARANTHESIS_CLOSE = ']';

    /**
     * Constant representing the quotation mark, used to encase string values.
     */
    private static final char TS_PARANTHESIS_STRING = '\"';

    /**
     * Constant representing the equal sign between field name and value.
     */
    private static final char TS_EQUALS = '=';

    /**
     * Constant representing the single line delimiter which separates fields when creating a single line string representation.
     */
    public static final char TS_DELIMITER_SL = ',';

    /**
     * Constant representing the multi-line delimiter which separates fields when creating a multi-line string representation.
     */
    public static final char TS_DELIMITER_ML = '\n';

    /**
     * Constant representing only the object itself as the level of class hierarchy to be explored when creating a string representation.
     */
    public static final int TS_LEVEL_ONLY = 0;

    /**
     * Constant representing the entire class hierarchy to be explored when creating a string representation.
     */
    public static final int TS_LEVEL_DEEP = -1;

    /**
     * Constant representing a null value for a field.
     */
    public static final String TS_EXPR_NULL = "NULL";

    /**
     * Constant representing a circular reference to a parent object, used to avoid infinite recursion.
     */
    public static final String TS_EXPR_CIRC_REF = "PARENT";

    /**
     * The object for which the string representation will be generated.
     */
    @Getter
    private Object object;

    /**
     * The character to be used as delimiter between fields.
     */
    @Getter
    private char delimiter;

    /**
     * Depth of the nested object, used for indentation.
     */
    @Getter
    private int nesting;

    /**
     * Level of class hierarchy to be explored.
     */
    @Getter
    private int level;

    /**
     * Whether to resolve nested objects or not.
     */
    @Getter
    private boolean resolve;

    /**
     * List of objects that have already been resolved during the string conversion.
     */
    @Getter
    private final static List<Object> alreadyResolved = new ArrayList<>();

    /**
     * Overrides the toString method to return a string representation of the object.
     *
     * @return String representation of the object.
     */
    @Override
    public String toString() {
        try {
            return this.buildFromAttributes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Private method to build string representation from object's attributes.
     * Uses indirect recursion to implement nested objects.
     *
     * @return String representation of the object's attributes.
     * @throws Exception When any error occurs while generating the string.
     */
    private String buildFromAttributes() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getObject().getClass().getSimpleName());
        stringBuilder.append(TS_PARANTHESIS_OPEN);

        if (getNesting() == 0) {
            getAlreadyResolved().clear();
        }

        if (!getAlreadyResolved().contains(object)) {
            if (getObject() instanceof Collection<?> collection) {
                appendCollection(stringBuilder, collection);
            } else if (getObject() instanceof Map<?, ?>) {
                Collection<?> collection = ((Map<?, ?>) getObject()).values();
                appendCollection(stringBuilder, collection);
            } else if (getObject().getClass().isArray()) {
                Collection<?> collection = List.of(getObject());
                appendCollection(stringBuilder, collection);
            } else {
                if (getDelimiter() == ToString.TS_DELIMITER_ML) {
                    stringBuilder.append(getDelimiter());
                }

                boolean firstField = true;
                for (Field field : getDeclaredFields(getObject(), getLevel())) {
                    validateFieldAnnotations(field);

                    if (getNesting() > 0 && !isResolve()) {
                        continue;
                    }

                    if (!isFieldIncluded(field)) {
                        continue;
                    }

                    if (!firstField) {
                        stringBuilder.append(getDelimiter());
                    }

                    if (getDelimiter() == ToString.TS_DELIMITER_ML) {
                        stringBuilder.append(getIndentation(getNesting()));
                    }

                    appendValue(stringBuilder, field, getObject());

                    if (firstField) {
                        firstField = false;
                    }
                }

                if (getDelimiter() == ToString.TS_DELIMITER_ML) {
                    stringBuilder.append(getDelimiter());
                    stringBuilder.append(getIndentation(getNesting() - 1));
                }
            }
        } else {
            stringBuilder.append(TS_EXPR_CIRC_REF);
        }

        stringBuilder.append(TS_PARANTHESIS_CLOSE);
        return stringBuilder.toString();
    }

    /**
     * Get the declared fields for a given object up to a certain level.
     *
     * @param object The object for which to get declared fields.
     * @param level The maximum level up to which to get fields.
     * @return A list of Fields for the object.
     */
    private List<Field> getDeclaredFields(Object object, int level) {
        List<Field> result = new ArrayList<>();

        Class<?> currentClass = object.getClass();

        int iteration = 0;

        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {

                try {
                    field.setAccessible(true);

                    if (currentClass.isArray() || Collection.class.isAssignableFrom(currentClass) || Map.class.isAssignableFrom(currentClass)) {
                        if (!field.getName().equals("elementData")) {
                            continue;
                        }
                    }

                    result.add(field);
                } catch (InaccessibleObjectException e) {
                    /* IGNORE */
                }

            }

            currentClass = currentClass.getSuperclass();

            if (level != TS_LEVEL_DEEP && iteration == level) {
                break;
            }
            iteration++;
        }

        return result;
    }

    /**
     * Check if a given field should be included in the string representation.
     * Depends on ignore annotation and final and static modifiers but not on visibility.
     *
     * @param field The field to check.
     * @return Whether the field should be included in the string representation.
     */
    private boolean isFieldIncluded(Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            return false;
        }

        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }

        return field.getAnnotation(ToStringIgnore.class) == null;
    }

    /**
     * Validate the annotations on a given field to detect incompabilities.
     *
     * @param field The field to validate.
     * @throws Exception When the field has incompatible annotations.
     */
    private void validateFieldAnnotations(Field field) throws Exception {
        if (field.getAnnotation(ToStringDontResolve.class) != null) {
            if (field.getAnnotation(ToStringIgnore.class) != null) {
                throw new Exception("annotations " + ToStringIgnore.class.getSimpleName() + " and " + ToStringDontResolve.class.getSimpleName() + " are not compatible");
            }

            if (!Collection.class.isAssignableFrom(field.getType()) && !Map.class.isAssignableFrom(field.getType()) && !field.getClass().isArray()) {
                throw new Exception("annotation " + ToStringDontResolve.class + " is not supported for field " + field.getName() + " of type " + field.getType().getSimpleName());
            }
        }
    }

    /**
     * Append the value of a given field to the StringBuilder that is passed through.
     *
     * @param result The StringBuilder to append to.
     * @param field The field whose value to append.
     * @param object The object from which to get the field's value.
     */
    private void appendValue(StringBuilder result, Field field, Object object) throws IllegalAccessException {
        result.append(field.getName());
        result.append(TS_EQUALS);

        Object value;

        value = field.get(object);

        if (value == null) {
            result.append(TS_EXPR_NULL);
        } else if (value instanceof Enum) {
            result.append(((Enum<?>) value).name());
        } else if (value instanceof LocalDate date) {
            result.append(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else if (value instanceof LocalDateTime dateTime) {
            result.append(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } else if (value instanceof Date) { // Legacy java.util.Date support
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            result.append(format.format(value));
        } else if (field.getType().isPrimitive() || value instanceof Number) {
            result.append(value);
        } else if (value instanceof String) {
            result.append(TS_PARANTHESIS_STRING).append(value).append(TS_PARANTHESIS_STRING);
        } else {
            boolean resolve = isResolve();

            if (field.isAnnotationPresent(ToStringDontResolve.class)) {
                resolve = false;
            }

            result.append(ToString.createCustom(value, getDelimiter(), getNesting() + 1, getLevel(), resolve));
        }

        ToString.addAlreadyResolved(object);
    }

    /**
     * Append the elements of a collection to the StringBuilder that is passed through.
     *
     * @param result The StringBuilder to append to.
     * @param collection The collection whose elements to append.
     */
    private void appendCollection(StringBuilder result, Collection<?> collection) {
        if (collection.isEmpty() || !isResolve()) {
            result.append(collection.size());
        } else {
            for (Object entry : collection) {
                result.append(getDelimiter());
                result.append(getIndentation(getNesting()));
                result.append(ToString.createCustom(entry, getDelimiter(), getNesting() + 1, getLevel(), isResolve()));
            }

            result.append(getDelimiter());
            result.append(getIndentation(getNesting() - 1));
        }
    }

    /**
     * Generate a string of a certain number of indentation characters.
     *
     * @param n The number of indentation characters to generate.
     * @return A string of n indentation characters.
     */
    private static String getIndentation(int n) {
        return "\t".repeat(n + 1);
    }

    /**
     * Adds an object to the list of objects that have already been resolved in the string conversion.
     *
     * @param alreadyResolved An object that has been already resolved.
     */
    public static void addAlreadyResolved(Object alreadyResolved) {
        ToString.alreadyResolved.add(alreadyResolved);
    }

    /**
     * Generates a string representation of an object using single line delimiter and does not resolve nested objects.
     *
     * @param object The object to be represented as a string.
     * @return String representation of the object.
     */
    public static String create(Object object) {
        return (new ToString(object, TS_DELIMITER_SL, 0, TS_LEVEL_DEEP, false)).toString();
    }

    /**
     * Generates a string representation of an object using multi-line identations and resolves nested objects.
     *
     * @param object The object to be represented as a string.
     * @return String representation of the object.
     */
    public static String createDump(Object object) {
        return (new ToString(object, TS_DELIMITER_ML, 0, TS_LEVEL_DEEP, true).toString());
    }

    /**
     * Generates a custom string representation of an object using specified custom parameters.
     *
     * @param object The object to be represented as a string.
     * @param delimiter Tthe character to be used as delimiter.
     * @param nesting The depth of the nested object, used for indentation.
     * @param level The level of class hierarchy to be explored.
     * @param resolve Whether to resolve nested objects or not.
     * @return String representation of the object.
     */
    public static String createCustom(Object object, char delimiter, int nesting, int level, boolean resolve) {
        return (new ToString(object, delimiter, nesting, level, resolve)).toString();
    }

}
