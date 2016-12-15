package com.dfs;

import com.dfs.DfsServerException;

public interface CommandHandler {

    String handleCommand() throws DfsServerException;

}
