package com.dfs.CommandHandlers;

import com.dfs.DfsServerException;

public interface CommandHandler {

    String getResponse();
    void parseCommand() throws DfsServerException;

}
