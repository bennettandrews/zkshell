package zkshell.command;

import zkshell.ShellContext;
import zkshell.ZooUtils;

public class CdCommand implements Command {
    ShellContext context;
    
    public CdCommand(ShellContext context) {
        this.context = context;
    }
    
    @Override
    public void run() {
        
    }


    @Override
    public String help() {
        return "change to a new znode";
    }

    @Override
    public String name() {
        return "cd";
    }
}