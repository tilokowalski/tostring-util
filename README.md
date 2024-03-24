<h1 align="center">
    ToString Utility for Java
</h1>
<p align="center">
    <img alt="Maven CI/CD Pipeline" src="https://github.com/tilokowalski/tostring-util/actions/workflows/maven-deploy.yml/badge.svg">
    <img alt="Checkstyle Compliance" src="https://github.com/tilokowalski/tostring-util/actions/workflows/checkstyle-compliance.yml/badge.svg">
</p>

<p align="center">
    The `ToString` utility class in Java provides a customizable alternative to the traditional `toString()` method. It offers a human-readable string representation of any object, with options for single-line or multi-line formatting, resolving nested objects, and exploring class hierarchies.
</p>

## Features

- **Custom String Representation**: Generate a detailed string representation of any Java object.
- **Flexible Formatting**: Choose between single-line or multi-line output.
- **Class Hierarchy Exploration**: Option to explore any level of class hierarchy of the object.
- **Handling of Nested Objects**: Ability to resolve nested objects (automatically preventing circular references).
- **Custom Annotations**: Utilize `ToStringDontResolve` and `ToStringIgnore` annotations for finer control over the string representation.

## Usage

### Include Maven Dependency

You can get the ToString Utility from [Maven Central Repository](https://central.sonatype.com/artifact/de.tilokowalski.util/tostring-util). Add the following dependency to your `pom.xml` file:

```xml
<dependency>
  <groupId>de.tilokowalski.util</groupId>
  <artifactId>tostring-util</artifactId>
  <version>1.1.0</version>
</dependency>
```

### Create Basic String Representation

To create a simple string representation of an object:

```java
YourClass yourObject = new YourClass();

String simpleToString = ToString.create(yourObject);
System.out.println(simpleToString);
```

### Create Detailed Dump

For a detailed, multi-line representation, including nested objects:

```java
YourClass yourObject = new YourClass();

String detailedDump = ToString.createDump(yourObject);
System.out.println(detailedDump);
```

### Create Custom Representation

Create a custom string representation with specific settings:

```java
ToString.createCustom(anyObject, delimiter, nesting, level, resolve);
```

```java
YourClass yourObject = new YourClass();

String customToString = ToString.createCustom(yourObject, ',', 0, ToString.TS_LEVEL_DEEP, true);
System.out.println(customToString);
```

| Parameter | Description |
|---|---|
| `delimiter` | Character used to separate fields in the output. For example, `,` for single-line or `\n` for multi-line representation. |
| `nesting` | Integer specifying the depth of the currently nested object. This will usually be `0`, except for custom indentation from the beginning.  |
| `level` | Determines the level of class hierarchy to be explored. Use `ToString.TS_LEVEL_ONLY` for the object's direct fields, `ToString.TS_LEVEL_DEEP` for full hierarchy exploration, or any other integer. |
| `resolve` | Boolean flag indicating whether to resolve nested objects. `true` will resolve them, while `false` will avoid resolving nested objects. |

**Note:** Adjust these parameters according to the specific needs of your object's string representation.

## Custom Annotations

### `@ToStringDontResolve`

Use this annotation on a field to prevent resolving it during the string conversion.

```java
public class YourClass {

  @ToStringDontResolve
  private List unresolvedList;

  @ToStringDontResolve
  private OtherClass unresolvedObject;

  private String resolveMe;

  // ...
}
```

### `@ToStringIgnore`

Apply this annotation to a field you wish to exclude from the string representation.

```java
public class YourClass {

  @ToStringIgnore
  private String ignoredField;

  private String includedField;

  // ...
}
```

---

**Note:** The `ToString` utility class requires Java Reflection for its operations.