package org.otherobjects.cms.test;

import org.otherobjects.cms.OtherObjectsException;

public interface SecurityCallback
{
    Object doWithSecurityContext() throws OtherObjectsException;
}
