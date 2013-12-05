package zkshell;

import zkshell.command.*;

import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import jline.ArgumentCompletor;
import jline.CandidateListCompletionHandler;
import jline.Completor;
import jline.ConsoleReader;
import jline.SimpleCompletor;

import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;

import org.apache.zookeeper.KeeperException;


public class Main {

	private ConsoleReader reader;
	private StrTokenizer tokenizer;
	
	private HashMap<String,Command> commandTable = new HashMap<String,Command>();
		
    private ShellContext context;
    
	private void registerCommands() {
	    Command[] cmds = new Command[] {
            new ListCommand(context),
	    };
	    for (Command cmd : cmds) {
            this.commandTable.put(cmd.name(), cmd);
	    }
	}
	
	private String[] commandList() {
		List<String> cmds = new ArrayList<String>();
		for (String c : commandTable.keySet()) {
			cmds.add(c);
		}
		cmds.add("quit");
		cmds.add("exit");
		cmds.add("help");
		return cmds.toArray(new String[0]);
	}

	public Main(String servers) throws IOException {
        this.context = new ShellContext(servers);
        registerCommands();
        initConsole();
	}

    private StrTokenizer getTokenizer() {
        StrMatcher delimiterMatcher = StrMatcher.charSetMatcher(" \t"); 
        StrMatcher quoteMatcher = StrMatcher.charMatcher('\"');
        return new StrTokenizer("", delimiterMatcher, quoteMatcher);
    }

    private void initConsole() throws IOException {
        reader = new ConsoleReader();
        tokenizer = getTokenizer();
        reader.setCompletionHandler(new CandidateListCompletionHandler());
	
        List<Completor> completors = new ArrayList<Completor>();
	
        completors.add(new SimpleCompletor(commandList()));
        completors.add(new ZNodeContextCompletor());
        reader.addCompletor(new ArgumentCompletor(completors));
    }
    
    /*
     * Completor that will suggest znodes.
     */
    public class ZNodeContextCompletor implements Completor {
        private StrTokenizer tokenizer;
	
        public ZNodeContextCompletor() {
            this.tokenizer = getTokenizer();
        }
	
        @Override
        public int complete(String buffer, int cursor, List candidates) {
            if (buffer == null) {
                System.out.println("BUFFER IS NULL");
                buffer = "";
            }

            tokenizer.reset(buffer);
            String lastToken = "";
            while (tokenizer.hasNext()) {
                lastToken = tokenizer.next().toString();
            }
	    
            try {
                if (lastToken.endsWith("/")) {
                    String currentZNode = context.getCurrentZNode();
                    StringBuilder path = new StringBuilder(currentZNode);
                    //if (!currentZNode.equals("/")) 
                    //    sb.append("/");
                    int lastSlash = buffer.lastIndexOf("/");
                    path.append(buffer.substring(0,lastSlash));
                    
                    System.out.println("Node: " + path.toString());
                    List<String> children = ZooUtils.getChildren(context, path.toString());
                    for (String child : children) {
                        candidates.add(child);
                    }
                } else {
					// sugguest children under current znode
                    String currentZNode = context.getCurrentZNode();
                    for (String child : ZooUtils.getChildren(context, currentZNode)) {
                        if (child.startsWith(buffer))
                            candidates.add(child);
                    }
                }
            } catch (Exception e) { }
	    
            Collections.sort(candidates);
            return 0;
        }
    }
    
	private String getPrompt() {
        StringBuilder p = new StringBuilder();
        p.append("zkshell[");
        p.append(context.getCurrentZNode());
        p.append("]> ");
	    return  p.toString();
	}
    
	/** Tokenize and run a user's command.
	 * @param line the line entered by the user
	 */
    private void interpretLine(String line) throws IOException {
        tokenizer.reset(line.toCharArray());
		String[] tokens = tokenizer.getTokenArray();
		if (0 == tokens.length) 
			return;
		interpretCommand(line, tokens);
	}
    
    
	private void help(String[] tokens) throws IOException {
		System.out.println("ZKShell, version 0.0.1");
		System.out.println("Type \'help name\' to find out more about \'name\'");
		System.out.println("quit\t\tQuits the shell"); 
	}
	
	/** 
	 * Actually run a single commands.
	 * @param line the entire unparsed line for the command
	 * @param token the individual tokens making up the user's command line.
	 */
	private void interpretCommand(String line, String[] tokens) throws IOException {
	    if ("exit".equals(tokens[0]) || "quit".equals(tokens[0])) {
            if (1 == tokens.length) {
                // throw new ExitCodeException(0, "");
                System.exit(0);
            }
	    } else if ("help".equals(tokens[0])) {
			help(tokens);
	    } else {
            Command cmd = commandTable.get(tokens[0]);
            if (cmd == null) {
                System.out.println(tokens[0] + ": command not found");
                return;
            }
            cmd.run(tokens);
	    }
	    
	}
    
    public void run() {
        while (true) {
            try {
                String line = reader.readLine(getPrompt());
                if (null == line) {
                    return;
                }
                interpretLine(line);
            } catch (Exception e) { 
                System.out.println("UHOH: " + e);
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("zkshell <servers>");
            System.exit(-1);
        }
	
        //int ret;
        Main shell = new Main(args[0]);
        shell.run();
    }
}
