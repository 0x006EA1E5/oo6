//package org.otherobjects.cms.beans;////import org.apache.commons.lang.StringUtils;//import org.otherobjects.asm.ClassWriter;//import org.otherobjects.asm.FieldVisitor;//import org.otherobjects.asm.Label;//import org.otherobjects.asm.MethodVisitor;//import org.otherobjects.asm.Opcodes;//import org.otherobjects.cms.OtherObjectsException;//import org.otherobjects.cms.types.PropertyDef;//import org.otherobjects.cms.types.TypeDef;//import org.springframework.util.Assert;////@SuppressWarnings("unchecked")//public class BeanCreator//{//    /**//     * Returns class represented by definition.//     * //     * @param definition a BeanDefinition representing class//     * @return the class represented by specified definition//     *///    public Object createBean(TypeDef definition) throws Exception//    {//        byte[] ba = createBeanByteArray(definition);//        Class thisClass = defineClass(definition.getClassName(), ba);//        try//        {//            return thisClass.newInstance();//        }//        catch (Exception e)//        {//            throw e;//        }//    }////    public byte[] createBeanByteArray(TypeDef typeDef) throws Exception//    {//        Assert.notNull(typeDef, "Can not create bean for null TypeDef.");//        Assert.notNull(typeDef.getName(), "TypeDef className must not be null. Type name: " + typeDef.getClassName());////        String className = typeDef.getClassName();////        ClassWriter cw = new ClassWriter(0);//        className = className.replace('.', '/');////        String superClass = typeDef.getSuperClassName().replace('.', '/');//        String[] interfaces = null;////        cw.visit(Opcodes.V1_2, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, className, null, superClass, interfaces);//        createConstructor(superClass, cw, className);////        int lineNumber = 100;//        for (PropertyDef pd : typeDef.getProperties())//        {//            String propertyName = pd.getName();//            String propertyClassName = pd.getClassName();//            // FIXME Don't add fields that aleady exist in supercalass//			if (!propertyName.equals("code"))//            {//                addField(cw, propertyName, propertyClassName); // FIXME Class//                addGetter(cw, className, propertyName, propertyClassName, lineNumber += 10);//                addSetter(cw, className, propertyName, propertyClassName, lineNumber += 10);//            }//        }////        cw.visitEnd();//        return cw.toByteArray();//    }////    private Class defineClass(String className, byte[] b)//    {//        //override classDefine (as it is protected) and define the class.//        Class clazz = null;//        try//        {//            ClassLoader loader = this.getClass().getClassLoader();//            Class cls = Class.forName("java.lang.ClassLoader");//            java.lang.reflect.Method method = cls.getDeclaredMethod("defineClass", new Class[]{String.class, byte[].class, int.class, int.class});////            // protected method invocaton//            method.setAccessible(true);//            try//            {//                Object[] args = new Object[]{className, b, new Integer(0), new Integer(b.length)};//                clazz = (Class) method.invoke(loader, args);//            }//            finally//            {//                method.setAccessible(false);//            }//        }//        catch (Exception e)//        {//            throw new OtherObjectsException("Error creating class: " + className, e);//        }//        return clazz;////    }////    /**//     * Generates a standard constructor method and adds it to the provided {@link ClassWriter}.//     * //     * @param superClass the field for which to create a setter//     * @param cw the ClassWriter for class to add constructor//     * @param className the classname for class to add constructor//     *///    private void createConstructor(String superClass, ClassWriter cw, String className)//    {//        MethodVisitor mv;//        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);//        mv.visitCode();//        Label l0 = new Label();//        mv.visitLabel(l0);//        mv.visitLineNumber(5, l0);//        mv.visitVarInsn(Opcodes.ALOAD, 0);//        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClass, "<init>", "()V");//        mv.visitInsn(Opcodes.RETURN);//        Label l1 = new Label();//        mv.visitLabel(l1);//        mv.visitLocalVariable("this", convertToJavaClassString(className), null, l0, l1, 0);//        mv.visitMaxs(1, 1);//        mv.visitEnd();//    }////    /**//     * Generates a standard field declaration statement and adds it to the provided ClassWriter.//     * //     * @param cw the ClassWriter to add the new method to//     * @param fieldName the field to create setter ford//     * @param clazz the class to add setter to//     *///    private void addField(ClassWriter cw, String fieldName, String propertyClassName)//    {//        FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE, fieldName, convertToJavaClassString(propertyClassName), null, null);//        fv.visitEnd();//    }////    /**//     * Generates a standard getter method and adds it to the provided <code>ClassWriter</code>.//     * //     * @param cw the ClassWriter to add the new method to//     * @param className the class name to add method to//     * @param fieldName the field to create setter for//     * @param clazz the class to add setter to//     * @param lineNumber the line number to add getter in class//     *///    private void addGetter(ClassWriter cw, String className, String fieldName, String propertyClassName, int lineNumber)//    {//        MethodVisitor mv;//        Label l0;//        Label l1;////        String newName = StringUtils.capitalize(fieldName);//        String getterName = "get" + newName;////        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, getterName, "()" + convertToJavaClassString(propertyClassName), null, null);//        mv.visitCode();//        l0 = new Label();//        mv.visitLabel(l0);//        mv.visitLineNumber(lineNumber, l0);//        mv.visitVarInsn(Opcodes.ALOAD, 0);//        mv.visitFieldInsn(Opcodes.GETFIELD, className, fieldName, convertToJavaClassString(propertyClassName));//        mv.visitInsn(Opcodes.ARETURN);//        l1 = new Label();//        mv.visitLabel(l1);//        mv.visitLocalVariable("this", convertToJavaClassString(className), null, l0, l1, 0);//        mv.visitMaxs(1, 1);//        mv.visitEnd();//    }////    /**//     * Generates a standard setter method and adds it to the provided ClassWriter.//     * //     * @param cw the ClassWriter to add the new method to//     * @param className the class name to add method to//     * @param fieldName the field to create setter for//     * @param clazz the class to add setter to//     * @param lineNumber the line number to add setter in class//     *///    private void addSetter(ClassWriter cw, String className, String fieldName, String propertyClassName, int lineNumber)//    {//        MethodVisitor mv;////        String newName = StringUtils.capitalize(fieldName);//        String setterName = "set" + newName;////        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, setterName, "(" + convertToJavaClassString(propertyClassName) + ")V", null, null);//        mv.visitCode();//        Label l0 = new Label();//        mv.visitLabel(l0);//        mv.visitLineNumber(lineNumber, l0);//        mv.visitVarInsn(Opcodes.ALOAD, 0);//        mv.visitVarInsn(Opcodes.ALOAD, 1);//        mv.visitFieldInsn(Opcodes.PUTFIELD, className, fieldName, convertToJavaClassString(propertyClassName));//        Label l1 = new Label();//        mv.visitLabel(l1);//        mv.visitLineNumber(lineNumber + 1, l1);//        mv.visitInsn(Opcodes.RETURN);//        Label l2 = new Label();//        mv.visitLabel(l2);//        mv.visitLocalVariable("this", convertToJavaClassString(className), null, l0, l2, 0);//        mv.visitLocalVariable(fieldName, convertToJavaClassString(propertyClassName), null, l0, l2, 1);//        mv.visitMaxs(2, 2);//        mv.visitEnd();//    }////    /**//     * Formats a java class name into the format required by ASM methods.//     * //     * Example: //     * convertToJavaClassString("java.String.lang") returns java/String/lang//     * //     * @param className a class name//     * @return this string in format required by ASM methods//     *///    private String convertToJavaClassString(String className)//    {//        String classString = "L" + className + ";";//        classString = classString.replace('.', '/');//        return classString;//    }//}