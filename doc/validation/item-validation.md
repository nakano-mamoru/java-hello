# `@Item` バリデーション（開発者向け説明資料）

## 1. 目的・概要

`@Item` は、DTO のフィールドに付与する独自の Jakarta Bean Validation アノテーションです。辞書キーを指定して、`src/main/resources/metadata/column-dictionary.json` の項目定義に基づき入力を検証します。

- 目的: 入力項目ルールを一元管理し、バリデーションをコードから切り離して簡潔にする
- 対象: `com.example.demo.validation.Item` アノテーション
- 実装: `com.example.demo.validation.ItemValidator`

---

## 2. 利用方法

### 2.1 `@Item` をDTOに付与

`@Item("PhysicalName")` で項目辞書キーを指定します。

```java
public class CustomerRequest {
    @Item("CustomerId")
    private String customerId;
    @Item("CustomerName")
    private String customerName;
    @Item("Email")
    private String email;
    @Item("Age")
    private String age;
}
```

### 2.2 コントローラで `@Valid` 付き受け取り

```java
@PostMapping("/customer")
public ResponseEntity<?> create(@Valid @RequestBody CustomerRequest req) { ... }
```

Spring Boot は自動でバリデーションを実行し、エラーを 400 で返します。

---

## 3. 保守方法

### 3.1 `column-dictionary.json` の生成

`column-dictionary.json` は `doc/resources/define/項目辞書.md` のテーブルから生成します。

```bash
node scripts/generate-column-dictionary.js
```

詳細は `scripts/build-env.json` の `columnDictionary.source` / `output` を参照してください。

### 3.2 項目追加・変更手順

1. `doc/resources/define/項目辞書.md` のテーブルに新しい行を追加または更新
   - `PhysicalName` は `@Item("...")` のキー
   - `Type` は `VARCHAR`, `CHAR`, `INT`, `DECIMAL`, `DATETIME`
   - `Length` は桁数（`DECIMAL` は `10,2` 形式可）
   - `RegexPattern` は正規表現（必要時）
   - `Range` は `min,max` 形式（必要時）
2. `node scripts/generate-column-dictionary.js` でJSONを再生成
3. `src/main/resources/metadata/column-dictionary.json` を差分確認
4. DTO に `@Item("PhysicalName")` を設定
5. テスト/APIで検証結果を確認

> 注意: 辞書キーがないと `ItemValidator` は例外を投げます。PhysicalName を変更した場合は関連 DTO の `@Item` も更新してください。

---

## 4. 内部実装

### 4.1 `Item` アノテーション

`@Item` は Bean Validation アノテーションで、`ItemValidator` を指定します。

```java
@Documented
@Constraint(validatedBy = ItemValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Item {
    String value();
    String message() default "Invalid value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### 4.2 `ItemValidator`

`ConstraintValidator<Item, Object>` を実装し、辞書定義に応じたチェックを実行します。

主要チェック:
1. nullはスキップ（必須は `@NotNull` 併用）
2. 長さチェック（`length` が整数文字列の場合）
3. 正規表現チェック（`regexPattern`）
4. 数値範囲チェック（`range` が `min,max`）

```java
ItemDefinition def = ItemDictionary.get(key);
if (def.getLength() != null && def.getLength().matches("\\d+")) {
  int max = Integer.parseInt(def.getLength());
  if (str.length() > max) return false;
}
if (def.getRegexPattern() != null && !Pattern.matches(def.getRegexPattern(), str)) return false;
if (def.getRange() != null) {
  BigDecimal min = new BigDecimal(range[0].trim());
  BigDecimal max = new BigDecimal(range[1].trim());
  BigDecimal val = new BigDecimal(str);
  if (val.compareTo(min) < 0 || val.compareTo(max) > 0) return false;
}
```

### 4.3 `ItemDictionary`

`column-dictionary.json` を起動時に読み込み、`PhysicalName` をキーに `ItemDefinition` を取得します。

```java
private static final String DICTIONARY_FILE = "/metadata/column-dictionary.json";
static { load(); }
public static ItemDefinition get(String key) { return dictionary.get(key); }
```

---

## 5. 参考

### JSON で使う主な定義
- `physicalName`
- `logicalName`
- `length`
- `regexPattern`
- `range`

### 既知の注意点
- `null` は valid とみなすため、必須は `@NotNull` を併用する
- 辞書にキーがない場合、`RuntimeException` が発生する
- `ItemValidator` は今後 `codeType` や型ごとの厳密チェックで拡張可能
