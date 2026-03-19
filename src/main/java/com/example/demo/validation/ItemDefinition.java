package com.example.demo.validation;

public class ItemDefinition {

    private int index;
    private String logicalName;
    private String physicalName;
    private String type;
    private String length;
    private String regexPattern;
    private String range;
    private String codeType;

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public String getLogicalName() { return logicalName; }
    public void setLogicalName(String logicalName) { this.logicalName = logicalName; }

    public String getPhysicalName() { return physicalName; }
    public void setPhysicalName(String physicalName) { this.physicalName = physicalName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLength() { return length; }
    public void setLength(String length) { this.length = length; }

    public String getRegexPattern() { return regexPattern; }
    public void setRegexPattern(String regexPattern) { this.regexPattern = regexPattern; }

    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }

    public String getCodeType() { return codeType; }
    public void setCodeType(String codeType) { this.codeType = codeType; }
}