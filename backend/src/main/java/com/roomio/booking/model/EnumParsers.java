package com.roomio.booking.model;

import java.util.Arrays;

final class EnumParsers {
  private EnumParsers() {
  }

  static <T extends Enum<T> & EnumValue> T parse(Class<T> type, String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    String normalized = value.trim().replace('-', '_');
    return Arrays.stream(type.getEnumConstants())
      .filter(item -> item.name().equalsIgnoreCase(normalized) || item.getValue().equalsIgnoreCase(value.trim()))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Unsupported " + type.getSimpleName() + ": " + value));
  }
}
