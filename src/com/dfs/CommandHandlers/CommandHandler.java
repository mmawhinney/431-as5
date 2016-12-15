package com.dfs.CommandHandlers;

import com.dfs.DfsServerException;

public interface CommandHandler {

    String handleCommand() throws DfsServerException;

}
