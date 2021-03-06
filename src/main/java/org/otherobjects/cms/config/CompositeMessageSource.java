/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.otherobjects.cms.config;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

@Component
public class CompositeMessageSource implements MessageSource {
    
    @Resource
    private List<MessageSource> messageSources;

    public String getMessage(MessageSourceResolvable arg0, Locale arg1) throws NoSuchMessageException
    {
        for (MessageSource m : messageSources)
        {
            String message = m.getMessage(arg0, arg1);
            if (message != null)
                return message;
        }
        return null;
    }

    public String getMessage(String arg0, Object[] arg1, Locale arg2) throws NoSuchMessageException
    {
        for (MessageSource m : messageSources)
        {
            try
            {
                String message = m.getMessage(arg0, arg1, arg2);
                return message;
            }
            catch (Exception e)
            {
                // No message found
            }
        }
        return null;
    }

    public String getMessage(String arg0, Object[] arg1, String arg2, Locale arg3)
    {
        for (MessageSource m : messageSources)
        {
            String message = m.getMessage(arg0, arg1, arg2, arg3);
            if (message != null)
                return message;
        }
        return null;
    }

    public void setMessageSources(List<MessageSource> messageSources)
    {
        this.messageSources = messageSources;
    }

}
