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

function splitMdRow(line) {
  const result = [];
  let current = "";
  let escape = false;

  for (let c of line) {
    if (escape) {
      current += c;
      escape = false;
      continue;
    }

    if (c === "\\") {
      escape = true;
      continue;
    }

    if (c === "|") {
      result.push(current.trim());
      current = "";
      continue;
    }

    current += c;
  }

  result.push(current.trim());
  return result;
}

function parseMd(md) {
  const lines = md.split(/\r?\n/);

  const result = {};
  const physicalNames = new Set();

  let currentLogical = null;
  let currentPhysical = null;
  let tableStarted = false;

  for (let i = 0; i < lines.length; i++) {
    let line = lines[i].trim();

    if (!line) continue;

    const header = line.match(/^##\s*(.+?)（(.+?)）/);
    if (header) {
      currentPhysical = header[1].trim();
      currentLogical = header[2].trim();

      if (physicalNames.has(currentPhysical)) {
        throw new Error(`PhysicalName duplicated: ${currentPhysical}`);
      }
      physicalNames.add(currentPhysical);

      result[currentPhysical] = {
        PhysicalName: currentPhysical,
        LogicalName: currentLogical,
        Items: []
      };

      tableStarted = false;
      continue;
    }

    if (line.startsWith("|")) {
      const cols = splitMdRow(line)
        .map(v => v.trim())
        .filter(v => v !== "");

      if (!tableStarted) {
        if (cols.length < 4 || cols[0] !== "定数名") {
          throw new Error(`MD table header invalid at line ${i + 1}`);
        }
        tableStarted = true;
        continue;
      }

      if (line.includes("---")) continue;

      if (cols.length < 3) {
        throw new Error(`MD row format error at line ${i + 1}`);
      }

      const physicalName = cols[0] || null;
      const logicalName = cols[1] || null;
      const value = cols[2] || null;

      if (!currentLogical) {
        throw new Error(`Item defined before section header at line ${i + 1}`);
      }

      const items = result[currentPhysical].Items;

      if (value !== null) {
        const duplicated = items.find(v => v.Value === value);
        if (duplicated) {
          console.warn(
            `WARN value duplicated [${value}] in ${currentPhysical}`
          );
        }
      }

      items.push({
        LogicalName: logicalName,
        PhysicalName: physicalName,
        Value: value
      });
    }
  }

  return result;
}

function main() {
  const config = loadConfig();

  const src = path.resolve(ROOT, config.codeDefinition.source);
  const out = path.resolve(ROOT, config.codeDefinition.output);

  if (!fs.existsSync(src)) {
    throw new Error(`source not found: ${src}`);
  }

  const md = fs.readFileSync(src, "utf8");
  const json = parseMd(md);

  fs.mkdirSync(path.dirname(out), { recursive: true });

  fs.writeFileSync(out, JSON.stringify(json, null, 2), "utf8");

  console.log("generated:", out);
}

main();