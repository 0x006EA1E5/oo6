//package org.otherobjects.cms.model;
//
///**
// * A dynamic object represents a data node in the content repository.
// * 
// * <p>Dynamic nodes can change their properties and validators at runtime
// * providing a very flexible data model.
// * 
// * <p>TODO Add support for description, icon and image generators
// * <br>TODO Equals, hashCode, serialableId builders
// */
//public class DynaNode extends BaseNode
//{
//    //private final Logger logger = LoggerFactory.getLogger(getClass());
//
//    /** The defining type for this node */
//    private String ooType;
//
//    /**
//     * Creates a node of the specified type. The type definition 
//     * in required to ensure that this node conforms.
//     * 
//     * @param type the type name
//     */
//    public DynaNode(String type)
//    {
//        setOoType(type);
//    }
//
//    public void setOoType(String ooType)
//    {
//        this.ooType = ooType;
//    }
//
//    @Override
//    public String getOoType()
//    {
//        return this.ooType;
//    }
//}
