package org.otherobjects.cms.types.annotation;

// FIXME Is this the best place for this?
public enum PropertyType {
    UNDEFINED("undefined"), STRING("string"), TEXT("text"), DATE("date"), TIME("time"), TIMESTAMP("timestamp"), BOOLEAN("boolean"), NUMBER("number"), DECIMAL("decimal"), COMPONENT("component"), REFERENCE(
            "reference"), LIST("list"), OORESOURCE("ooresource"), TRANSIENT("transient");
    //MAP("map");

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
