package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ItemValidator implements ConstraintValidator<Item, Object> {

    private String key;

    @Override
    public void initialize(Item annotation) {
        this.key = annotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // System.out.println("Validating key: " + key + ", value: " + value);

        // nullはスキップ（必要なら @NotNull 併用）
        if (value == null) {
            return true;
        }

        ItemDefinition def = ItemDictionary.get(key);

        if (def == null) {
            throw new RuntimeException("Item definition not found: " + key);
        }

        String str = value.toString();

        // Lengthチェック（整数のみ対応）
        if (def.getLength() != null && def.getLength().matches("\\d+")) {
            int max = Integer.parseInt(def.getLength());
            if (str.length() > max) {
                return false;
            }
        }

        // 正規表現チェック
        if (def.getRegexPattern() != null) {
            if (!Pattern.matches(def.getRegexPattern(), str)) {
                return false;
            }
        }

        // 数値範囲チェック
        if (def.getRange() != null) {
            String[] range = def.getRange().split(",");
            BigDecimal min = new BigDecimal(range[0].trim());
            BigDecimal max = new BigDecimal(range[1].trim());
            BigDecimal val = new BigDecimal(str);
            if (val.compareTo(min) < 0 || val.compareTo(max) > 0) {
                return false;
            }
        }

        // CodeTypeチェック（拡張可）

        return true;
    }
}