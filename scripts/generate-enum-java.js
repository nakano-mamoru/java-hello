#!/usr/bin/env node

const fs = require("fs");
const path = require("path");

const ROOT = path.resolve(__dirname, "..");
const CONFIG_PATH = path.resolve(__dirname, "build-env.json");

function loadConfig() {
  if (!fs.existsSync(CONFIG_PATH)) {
    throw new Error("build-env.json not found");
  }
  return JSON.parse(fs.readFileSync(CONFIG_PATH, "utf8"));
}

function loadCodeDefinition(config) {
  const jsonPath = path.resolve(ROOT, config.codeDefinition.output);
  if (!fs.existsSync(jsonPath)) {
    throw new Error(`code-definition.json not found: ${jsonPath}`);
  }
  return JSON.parse(fs.readFileSync(jsonPath, "utf8"));
}

function toPascalCase(name) {
  return name
    .toLowerCase()
    .split("_")
    .map(v => v.charAt(0).toUpperCase() + v.slice(1))
    .join("");
}

function generateEnumSource(pkg, physicalName, logicalName, items) {

  const enumName = toPascalCase(physicalName);

  const constants = items.map(i => {
    const comment = i.LogicalName ? `    /** ${i.LogicalName} */\n` : "";
    const value = i.Value === null ? "" : i.Value;
    return `${comment}    ${i.PhysicalName}("${value}")`;
  }).join(",\n");

  return `package ${pkg};

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ${logicalName}
 * <p>
 * 自動生成されたEnumクラス
 * 物理名: ${physicalName}
 */
public enum ${enumName} {

${constants};

    /** コード値 */
    private final String code;

    ${enumName}(String code) {
        this.code = code;
    }

    /**
     * コード値を取得します。
     *
     * @return コード
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * コード値からEnumを取得します。
     *
     * @param code コード値
     * @return Enum
     * @throws IllegalArgumentException 該当コードが存在しない場合
     */
    @JsonCreator
    public static ${enumName} fromCode(String code) {

        if (code == null) {
            return null;
        }

        for (${enumName} v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }

        throw new IllegalArgumentException("Invalid code: " + code);
    }

    /**
     * 指定されたコードがこのEnumに存在するか判定します。
     *
     * @param code コード値
     * @return 存在する場合 true
     */
    public static boolean hasCode(String code) {

        if (code == null) {
            return false;
        }

        for (${enumName} v : values()) {
            if (v.code.equals(code)) {
                return true;
            }
        }

        return false;
    }
}
`;
}

function main() {

  const config = loadConfig();
  const defs = loadCodeDefinition(config);

  const pkg = config.enum.package;
  const outputDir = path.resolve(ROOT, config.enum.outputDir);

  fs.mkdirSync(outputDir, { recursive: true });

  Object.entries(defs).forEach(([logicalName, def]) => {

    const source = generateEnumSource(
      pkg,
      def.PhysicalName,
      logicalName,
      def.Items
    );

    const enumName = toPascalCase(def.PhysicalName);
    const filePath = path.join(outputDir, `${enumName}.java`);

    fs.writeFileSync(filePath, source, "utf8");

    console.log("generated:", filePath);
  });
}

main();