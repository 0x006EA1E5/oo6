package org.otherobjects.cms.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.otherobjects.cms.model.CompositeDatabaseId;

public class IdentifierUtils
{
    static Pattern uuidPattern = Pattern.compile("\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}");

    static Pattern dbCompositeIdPattern = Pattern.compile("^([^-]+)-(\\d+)$");

    /**
     *
     * @param potentialUuid
     * @return true if the potentialUuid matches the uuid pattern as per rfc4122 @see <a href="http://tools.ietf.org/html/rfc4122"> A Universally Unique IDentifier (UUID) URN Namespace</a>
     */
    public static boolean isUUID(String potentialUuid)
    {
        return uuidPattern.matcher(potentialUuid).matches();
    }

    /**
     * 
     * @param compositeId compositeId of form com.some.package.Class-123
     * @return CompositeDatabaseId object or null if compositeId was of wrong format or some error occurred
     */
    public static CompositeDatabaseId getCompositeIdPart(String compositeId)
    {
        CompositeDatabaseId compositeDatabaseId = null;
        try
        {
            Matcher matcher = dbCompositeIdPattern.matcher(compositeId);
            if (matcher.matches())
                return new CompositeDatabaseId(matcher.group(1), Long.parseLong(matcher.group(2)));
        }
        catch (Exception e)
        {
        }

        return compositeDatabaseId;
    }

}
