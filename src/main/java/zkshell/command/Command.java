package zkshell.command;

public interface Command {
    public void run(String[] tokens);
    public String name();
    public String help();
}