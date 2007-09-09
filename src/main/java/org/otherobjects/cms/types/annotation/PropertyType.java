package org.otherobjects.cms.types.annotation;

public enum PropertyType {
    STRING("string"), TEXT("text"), DATE("date"), TIME("time"), TIMESTAMP("timestamp"), BOOLEAN("boolean"), NUMBER("number"), DECIMAL("decimal"), COMPONENT("component"), REFERENCE("reference"), LIST(
            "list");

    private final String value;

    PropertyType(String value)
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }

}
