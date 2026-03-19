const fs = require("fs");
const path = require("path");

const ROOT = process.cwd();

const env = JSON.parse(
  fs.readFileSync(
    path.join(path.join(__dirname, "build-env.json")),
    "utf8"
  )
);

const schemasRoot = path.join(ROOT, env.ddl.schemasRoot);
const outputRoot = path.join(ROOT, env.ddl.outputRoot);

// console.log(`schemasRoot: ${schemasRoot}`);
// console.log(`outputRoot: ${outputRoot}`);

const dict = JSON.parse(
  fs.readFileSync(
    path.join(ROOT, env.columnDictionary.output),
    "utf8"
  )
);

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

    const cols = lines[i]
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

function columnSql(col) {
  // console.log('columnSql', JSON.stringify(col));
  const logical = col["LogicalName"];
  const d = dict[logical];

  if (!d) {
    // console.log(`項目辞書未定義: ${logical}`);
    throw new Error(`項目辞書未定義: ${logical}`);
  }

  const name = d.PhysicalName;
  let type = d.Type;
  if (d.Length) {
    type += `(${d.Length})`;
  }
  const required = col["Required"] === "Y" ? " NOT NULL" : "";
  return `  ${name} ${type}${required}`;
}

function createDDL(schemaName, tableName, rows) {

  const columns = [];
  const pk = [];

  rows.forEach(r => {
    columns.push(columnSql(r));
    if (r["PK"] === "Y") {
      const d = dict[r["LogicalName"]];
      pk.push(d.PhysicalName);
    }
  });

  if (pk.length > 0) {
    columns.push(`  PRIMARY KEY (${pk.join(", ")})`);
  }

  return `CREATE TABLE ${schemaName}.${tableName} (
${columns.join(",\n")}
);
`;
}

function processTables(schemaDir, schemaMeta) {

  const tableDir = path.join(schemaDir, "tables");
  if (!fs.existsSync(tableDir)) {
    return;
  }

  const files = fs.readdirSync(tableDir).filter(v => v.endsWith(".md"));
  const outDir = path.join(outputRoot, schemaMeta.schemaName);
  console.log(`Processing files.count : ${files.length}, output to: ${outDir}`);

  fs.mkdirSync(outDir, { recursive: true });

  files.forEach(file => {
    const tableName = path.basename(file, ".md");
    const md = fs.readFileSync(path.join(tableDir, file), "utf8");

    console.log(`Processing tables in: ${tableDir}`);
    const rows = parseMdTable(md);

    const ddl = createDDL(
      schemaMeta.schemaName,
      tableName,
      rows
    );

    const outFile =
      path.join(outDir, `${tableName}.sql`);

    fs.writeFileSync(outFile, ddl);

    console.log(`generated ${outFile}`);
  });
}
function processViews(schemaDir, schemaMeta) {

  const viewDir = path.join(schemaDir, "views");
  if (!fs.existsSync(viewDir)) {
    return;
  }

  const files = fs.readdirSync(viewDir).filter(v => v.endsWith(".md"));
  const outDir = path.join(outputRoot, schemaMeta.schemaName);
  fs.mkdirSync(outDir, { recursive: true });
  files.forEach(file => {
    const viewName = path.basename(file, ".md");
    const md = fs.readFileSync(path.join(viewDir, file), "utf8");
    const rows =      parseMdTable(md);
    const columns = rows.map(r => {
      const d =
        dict[r["LogicalName"]];

      if (!d) {
        throw new Error(
          `項目辞書未定義: ${r["LogicalName"]}`
        );
      }
      return `  ${d.PhysicalName}`;
    });

    const ddl =
      `CREATE VIEW ${schemaMeta.schemaName}.${viewName} AS
SELECT
${columns.join(",\n")}
FROM ???;
`;

    const outFile =      path.join(outDir, `${viewName}.sql`);
    fs.writeFileSync(outFile, ddl);
    console.log(`generated ${outFile}`);
  });
}

function main() {
  console.log("generate_ddl_sql,schemas:", schemasRoot);
  const schemas = fs.readdirSync(schemasRoot);

  schemas.forEach(schema => {
    // console.log(`Processing schema: ${schema}`);
    const schemaDir = path.join(schemasRoot, schema);
    const schemaJson = path.join(schemaDir, "schema.json");
    if (!fs.existsSync(schemaJson)) {
      return;
    }
    const schemaMeta = JSON.parse(fs.readFileSync(schemaJson, "utf8"));

    processTables(schemaDir, schemaMeta);
    processViews(schemaDir, schemaMeta);
  });
}

main();