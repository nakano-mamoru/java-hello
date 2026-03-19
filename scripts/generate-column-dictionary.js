const fs = require("fs");
const path = require("path");

const config = JSON.parse(
  fs.readFileSync(path.join(__dirname, "build-env.json"), "utf8")
);

const sourcePath = path.join(__dirname, "..", config.columnDictionary.source);
const outputPath = path.join(__dirname, "..", config.columnDictionary.output);

function unescapeCell(text) {
  return text.replace(/\\\|/g, "|").trim();
}

function splitRow(row) {
  let result = [];
  let current = "";
  let escape = false;

  for (let i = 0; i < row.length; i++) {
    const c = row[i];

    if (escape) {
      current += c;
      escape = false;
      continue;
    }

    if (c === "\\") {
      escape = true;
      current += c;
      continue;
    }

    if (c === "|") {
      result.push(current);
      current = "";
      continue;
    }

    current += c;
  }

  result.push(current);

  if (result[0] === "") result.shift();
  if (result[result.length - 1] === "") result.pop();

  return result.map(unescapeCell);
}

function validateType(type) {
  const allowed = [
    "VARCHAR",
    "CHAR",
    "INT",
    "DECIMAL",
    "DATETIME"
  ];
  return allowed.includes(type);
}

function parseMarkdown(md) {
  const lines = md.split(/\r?\n/);

  const tableLines = lines.filter(l => l.trim().startsWith("|"));

  if (tableLines.length < 2) {
    throw new Error("テーブルが見つかりません");
  }

  const header = splitRow(tableLines[0]);

  const expectedHeader = [
    "Index",
    "LogicalName",
    "PhysicalName",
    "Type",
    "Length",
    "CodeType",
    "RegexPattern",
    "Range"
  ];

  if (JSON.stringify(header) !== JSON.stringify(expectedHeader)) {
    throw new Error("ヘッダが仕様と一致しません");
  }

//   const rows = tableLines.slice(2);
  const rows = tableLines.slice(1).filter(line => {
    return !/^\|\s*-+/.test(line);
    });

  const result = {};
  const nameSet = new Set();

  rows.forEach((row, i) => {
    const cols = splitRow(row);

    if (cols.length !== header.length) {

      throw new Error(`列数不一致 row=${i}/cols:${cols.length}/header:${header.length}/row:${row}`);
    }

    const obj = {};

    header.forEach((h, idx) => {
      const value = cols[idx];

      if (value !== "") {
        obj[h] = value;
      }
    });

    if (!obj.PhysicalName) {
      throw new Error(`PhysicalName必須 row=${i + 3}`);
    }

    if (!obj.Type) {
      throw new Error(`Type必須 row=${i + 3} Name=${obj.LogicalName}`);
    }

    if (!validateType(obj.Type)) {
      throw new Error(`Type不正 row=${i + 3} Name=${obj.LogicalName}`);
    }

    if (nameSet.has(obj.PhysicalName)) {
      throw new Error(`Name重複 row=${i + 3} Name=${obj.LogicalName}`);
    }

    nameSet.add(obj.PhysicalName);

    if (obj.Index) {
      const num = Number(obj.Index);
      if (Number.isNaN(num)) {
        throw new Error(`Index型エラー row=${i + 3} Name=${obj.LogicalName}`);
      }
      obj.Index = num;
    }

    result[obj.PhysicalName] = obj;
  });

  return result;
}

function main() {
  const md = fs.readFileSync(sourcePath, "utf8");

  const json = parseMarkdown(md);

  fs.writeFileSync(
    outputPath,
    JSON.stringify(json, null, 2),
    "utf8"
  );

  console.log("column-dictionary.json generated");
}

main();