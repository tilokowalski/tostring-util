# ToString Utility for Java

## Introduction

The `ToString` utility class in Java provides a customizable alternative to the traditional `toString()` method. It offers a human-readable string representation of any object, with options for single-line or multi-line formatting, resolving nested objects, and exploring class hierarchies.

## Features

- **Custom String Representation**: Generate a detailed string representation of any Java object.
- **Flexible Formatting**: Choose between single-line or multi-line output.
- **Class Hierarchy Exploration**: Option to explore any level of class hierarchy of the object.
- **Handling of Nested Objects**: Ability to resolve nested objects (automatically preventing circular references).
- **Custom Annotations**: Utilize `ToStringDontResolve` and `ToStringIgnore` annotations for finer control over the string representation.

## Usage

### Basic Usage

To create a simple string representation of an object:

```java
ToString.create(anyObject);
```

### Detailed Dump

For a detailed, multi-line representation, including nested objects:

```java
ToString.createDump(anyObject);
```

### Custom Representation

Create a custom string representation with specific settings:

```java
ToString.createCustom(anyObject, delimiter, nesting, level, resolve);
```

| Parameter | Description |
|---|---|
| `anyObject` | The object for which the string representation is to be generated. |
| `delimiter` | Character used to separate fields in the output. For example, `,` for single-line or `\\n` for multi-line representation. |
| `nesting` | Integer specifying the depth of the currently nested object. This will usually be `0`, except for custom indentation from the beginning.  |
| `level` | Determines the level of class hierarchy to be explored. Use `ToString.TS_LEVEL_ONLY` for the object's direct fields, `ToString.TS_LEVEL_DEEP` for full hierarchy exploration, or any other integer. |
| `resolve` | Boolean flag indicating whether to resolve nested objects. `true` will resolve them, while `false` will avoid resolving nested objects. |

**Note:** Adjust these parameters according to the specific needs of your object's string representation.

## Custom Annotations

### `@ToStringDontResolve`

Use this annotation on a field to prevent resolving it during the string conversion.

### `@ToStringIgnore`

Apply this annotation to a field you wish to exclude from the string representation.

---

**Note:** The `ToString` utility class requires Java Reflection for its operations.