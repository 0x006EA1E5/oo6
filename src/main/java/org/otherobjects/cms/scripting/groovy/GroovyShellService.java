/*
 * Copyright 2007 Bruce Fancher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.otherobjects.cms.scripting.groovy;

import groovy.lang.Binding;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * @author Bruce Fancher
 */
public class GroovyShellService implements ApplicationContextAware
{
    protected Logger logger = Logger.getLogger(getClass());

    private ServerSocket serverSocket;
    private int socket;
    private List<GroovyShellThread> threads = new ArrayList<GroovyShellThread>();
    private ApplicationContext applicationContext;
    private boolean launchAtStart;
    private Thread serverThread;

    public GroovyShellService()
    {
        super();
    }

    public GroovyShellService(int socket)
    {
        super();
        this.socket = socket;
    }

    public void initialize()
    {
        if (launchAtStart)
        {
            launchInBackground();
        }
    }

    public void launchInBackground()
    {
        serverThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    launch();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void launch()
    {
        logger.info("GroovyShellService launch()");

        try
        {
            serverSocket = new ServerSocket(socket);
            logger.info("GroovyShellService launch() serverSocket: " + serverSocket);

            while (true)
            {
                Socket clientSocket = null;
                try
                {
                    clientSocket = serverSocket.accept();
                    logger.info("GroovyShellService launch() clientSocket: " + clientSocket);
                }
                catch (IOException e)
                {
                    logger.debug("e: " + e);
                    return;
                }

                Binding bindings = new Binding();
                bindings.setProperty("app", this.applicationContext);
                GroovyShellThread clientThread = new GroovyShellThread(clientSocket, bindings);
                threads.add(clientThread);
                clientThread.start();
            }
        }
        catch (IOException e)
        {
            logger.error("Groovy shell error: " + e);
            return;
        }
        finally
        {
            try
            {
                serverSocket.close();
            }
            catch (IOException e)
            {
                logger.error("Groovy shell error: " + e);
                return;
            }
            logger.info("GroovyShellService launch() closed connection");
        }
    }

    public void destroy()
    {
        logger.info("closing serverSocket: " + serverSocket);
        try
        {
            serverSocket.close();
            for (GroovyShellThread nextThread : threads)
            {
                logger.info("closing nextThread: " + nextThread);
                nextThread.getSocket().close();
            }
        }
        catch (IOException e)
        {
            logger.warn("e: " + e);
        }
    }

    public void setSocket(final int socket)
    {
        this.socket = socket;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public boolean isLaunchAtStart()
    {
        return launchAtStart;
    }

    public void setLaunchAtStart(boolean launchAtStart)
    {
        this.launchAtStart = launchAtStart;
    }
}
