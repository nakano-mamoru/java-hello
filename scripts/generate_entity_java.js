const fs = require("fs");
const path = require("path");

const ROOT = process.cwd();

const env = JSON.parse(
  fs.readFileSync(
    path.join(__dirname, "build-env.json"),
    "utf8"
  )
);

const schemasRoot =
  path.join(ROOT, env.ddl.schemasRoot);

const entityOutput =
  path.join(ROOT, env.entity.outputDir);

const packageName =
  env.entity.package;

const dict = JSON.parse(
  fs.readFileSync(
    path.join(ROOT, env.columnDictionary.output),
    "utf8"
  )
);

function parseLogicalTableName(md) {

  const m =
    md.match(/^#\s*テーブル定義【(.+?)】/m);

  if (!m) {
    throw new Error("論理テーブル名が見つかりません");
  }

  return m[1];
}

function parseMdTable(md) {

  const lines = md
    .split("\n")
    .map(v => v.trim())
    .filter(v => v.startsWith("|"));

  const header = lines[0]
    .split("|")
    .slice(1, -1)
    .map(v => v.trim());

  const rows = [];

  for (let i = 2; i < lines.length; i++) {

    const cols =
      lines[i]
        .split("|")
        .slice(1, -1)
        .map(v => v.trim());

    if (cols.length !== header.length) {
      throw new Error(`列数不一致 row=${i + 1}`);
    }

    const obj = {};

    header.forEach((h, idx) => {
      obj[h] = cols[idx];
    });

    rows.push(obj);
  }

  return rows;
}

function toCamel(name) {

  return name.replace(/_([a-z])/g,
    (_, c) => c.toUpperCase());
}

function toPascal(name) {

  const camel = toCamel(name);
  return camel.charAt(0).toUpperCase() + camel.slice(1);
}

function mapJavaType(type) {

  const t = type.toUpperCase();

  if (t.includes("CHAR") || t.includes("TEXT"))
    return "String";

  if (t.includes("BIGINT"))
    return "Long";

  if (t.includes("INT"))
    return "Integer";

  if (t.includes("DECIMAL"))
    return "java.math.BigDecimal";

  if (t.includes("DATE"))
    return "java.time.LocalDate";

  if (t.includes("TIME"))
    return "java.time.LocalDateTime";

  return "String";
}

function buildValidation(r, d) {

  const list = [];

  if (r["必須"] === "Y") {
    list.push("    @NotNull");
  }

  if (d.Length && d.Type.toUpperCase().includes("CHAR")) {

    const len =
      d.Length.split(",")[0];

    list.push(
      `    @Size(max=${len})`
    );
  }

  return list.join("\n");
}

function buildGetterSetter(type, field) {

  const method =
    field.charAt(0).toUpperCase() +
    field.slice(1);

  return `

    public ${type} get${method}() {
        return ${field};
    }

    public void set${method}(${type} ${field}) {
        this.${field} = ${field};
    }`;
}

function buildEntity(tableName, logicalTableName, rows) {

  const className =
    toPascal(tableName);

  const imports = new Set();
  const fields = [];
  const methods = [];

  rows.forEach(r => {

    const d =
      dict[r["LogicalName"]];

    if (!d) {
      throw new Error(
        `辞書未定義: ${r["LogicalName"]}`
      );
    }

    const javaType =
      mapJavaType(d.Type);

    if (javaType.includes(".")) {
      imports.add(javaType);
    }

    if (r["必須"] === "Y") {
      imports.add("jakarta.validation.constraints.NotNull");
    }
    if (d.Length &&
        d.Type.toUpperCase().includes("CHAR")) {
      imports.add("jakarta.validation.constraints.Size");
    }

    const type =
      javaType.includes(".")
        ? javaType.split(".").pop()
        : javaType;

    const field =
      toCamel(d.PhysicalName);

    const validation =
      buildValidation(r, d);

    fields.push(`

    /**
     * ${r["LogicalName"]}です。
     */
${validation}
    private ${type} ${field};`);

    methods.push(
      buildGetterSetter(type, field)
    );
  });

  const importLines =
    [...imports]
      .map(v => `import ${v};`)
      .join("\n");

  return `package ${packageName};

${importLines}

/**
 * ${logicalTableName}のエンティティクラスです。
 */
public class ${className} {

${fields.join("\n")}

${methods.join("\n")}
}
`;
}

function main() {

  fs.mkdirSync(entityOutput, { recursive: true });

  const schemas =
    fs.readdirSync(schemasRoot);

  schemas.forEach(schema => {

    const tableDir =
      path.join(
        schemasRoot,
        schema,
        "tables"
      );

    if (!fs.existsSync(tableDir))
      return;

    const files =
      fs.readdirSync(tableDir)
        .filter(v => v.endsWith(".md"));

    files.forEach(file => {

      const tableName =
        path.basename(file, ".md");

      const md =
        fs.readFileSync(
          path.join(tableDir, file),
          "utf8"
        );

      const logicalTableName =
        parseLogicalTableName(md);

      const rows =
        parseMdTable(md);

      const src =
        buildEntity(
          tableName,
          logicalTableName,
          rows
        );

      const outFile =
        path.join(
          entityOutput,
          `${toPascal(tableName)}.java`
        );

      fs.writeFileSync(outFile, src);

      console.log(`generated ${outFile}`);
    });
  });
}

main();