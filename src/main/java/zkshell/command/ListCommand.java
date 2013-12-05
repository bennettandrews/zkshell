package zkshell.command;

import zkshell.ShellContext;
import zkshell.ZooUtils;

public class ListCommand implements Command {

    ShellContext context;

    public ListCommand(ShellContext context) {
        this.context = context;
    }

	@Override
	public String help() {
		return "list children of a znode";
	}

	@Override
	public void run(String[] tokens) {
	    if (tokens.length == 1) {
            ZooUtils.printChildren(context, context.getCurrentZNode());
	    } else {
            if (ZooUtils.isAbsolutePath(tokens[1])) {
                ZooUtils.printChildren(context,tokens[1]);
            } else {
                String cur = context.getCurrentZNode();
                StringBuilder path = 
                    new StringBuilder(cur);
                if (cur != "/")
                    path.append("/");
                path.append(tokens[1]);
                ZooUtils.printChildren(context, path.toString());
            }
        }
	}

	@Override
	public String name() {
		return "ls";
	}

}
