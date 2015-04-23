package org.mycompany.bluetoothtictactoe.logger;

/**
 * Created by Saylee on 4/19/2015.
 */
public interface LogNode  {
    public void println(int priority, String tag, String msg, Throwable tr);
}
